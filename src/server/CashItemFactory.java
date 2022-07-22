package server;

import java.util.Map.Entry;
import java.util.Collection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import database.DBConPool;
import java.util.Iterator;
import provider.MapleDataTool;
import provider.MapleData;
import java.util.ArrayList;
import java.util.Set;
import tools.FileoutputUtil;
import provider.MapleDataProviderFactory;
import java.util.HashMap;
import provider.MapleDataProvider;
import java.util.List;
import java.util.Map;
import server.CashItemInfo.CashModInfo;

public class CashItemFactory{
    private static final CashItemFactory instance = new CashItemFactory();
    private static final int[] bestItems = new int[] { 50100010, 50100010, 50100010, 50100010, 50100010 };
    private boolean initialized;
    private final Map<Integer, List<Integer>> openBox;
    private final Map<Integer, CashItemInfo> itemStats;
    private final Map<Integer, List<CashItemInfo>> itemPackage;
    private final Map<Integer, server.CashItemInfo.CashModInfo> itemMods;
    private final Map<Integer, Integer> itemIdToSN;
    private final MapleDataProvider data;
    private Map<Integer, Integer> idLookup;
    
    public static final CashItemFactory getInstance() {
        return CashItemFactory.instance;
    }
    
    protected CashItemFactory() {
        this.initialized = false;
        this.openBox = (Map<Integer, List<Integer>>)new HashMap();
        this.itemStats = (Map<Integer, CashItemInfo>)new HashMap();
        this.itemPackage = (Map<Integer, List<CashItemInfo>>)new HashMap();
        this.itemMods = (Map<Integer, server.CashItemInfo.CashModInfo>)new HashMap();
        this.itemIdToSN = (Map<Integer, Integer>)new HashMap();
        this.data = MapleDataProviderFactory.getDataProvider("Etc.wz");
        this.idLookup = (Map<Integer, Integer>)new HashMap();
    }
    
    public void initialize() {
        System.out.println("[正在加载] -> 商城系统加载中请耐心等待");
        this.itemPackage.clear();
        this.itemMods.clear();
        this.refreshAllModInfo();
    }
    
    public final int getSnByItemItd(final int itemid) {
        final int sn = ((Integer)this.itemIdToSN.get(Integer.valueOf(itemid))).intValue();
        return sn;
    }
    
    public final CashItemInfo getItem(final int sn) {
        final CashItemInfo stats = (CashItemInfo)this.itemStats.get(Integer.valueOf(sn));
        final server.CashItemInfo.CashModInfo z = this.getModInfo(sn);
        if (z != null && z.showUp) {
            return z.toCItem(stats);
        }
        if (stats == null || !stats.onSale()) {
            return null;
        }
        return stats;
    }
    
    public final Set<Integer> getAllItemSNs() {
        return this.itemStats.keySet();
    }
    
    public final List<CashItemInfo> getAllItems() {
        return new ArrayList(this.itemStats.values());
    }
    
    public final List<CashItemInfo> getPackageItems(final int itemId) {
        if (this.itemPackage.get(Integer.valueOf(itemId)) != null) {
            return this.itemPackage.get(Integer.valueOf(itemId));
        }
        final List<CashItemInfo> packageItems = (List<CashItemInfo>)new ArrayList();
        final MapleData b = this.data.getData("CashPackage.img");
        if (b == null || b.getChildByPath(itemId + "/SN") == null) {
            return null;
        }
        for (final MapleData d : b.getChildByPath(itemId + "/SN").getChildren()) {
            packageItems.add(this.itemStats.get(Integer.valueOf(MapleDataTool.getIntConvert(d))));
        }
        this.itemPackage.put(Integer.valueOf(itemId), packageItems);
        return packageItems;
    }
    
    public final Map<Integer, List<Integer>> getRandomItemInfo() {
        return this.openBox;
    }
    
    public final boolean getModInfoSN(final int sn) {
        return this.itemMods.get(Integer.valueOf(sn)) != null;
    }
    
    public final server.CashItemInfo.CashModInfo getModInfo(final int sn) {
        server.CashItemInfo.CashModInfo ret = (server.CashItemInfo.CashModInfo)this.itemMods.get(Integer.valueOf(sn));
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        if (ret == null) {
            if (this.initialized) {
                return null;
            }
            try {
                con = (Connection)DBConPool.getInstance().getDataSource().getConnection();
                ps = con.prepareStatement("SELECT * FROM cashshop_modified_items WHERE serial = ?");
                ps.setInt(1, sn);
                rs = ps.executeQuery();
                if (rs.next()) {
                    ret = new CashModInfo(sn, rs.getInt("discount_price"), rs.getInt("mark"), rs.getInt("showup") > 0, rs.getInt("itemid"), rs.getInt("priority"), rs.getInt("package") > 0, rs.getInt("period"), rs.getInt("gender"), rs.getInt("count"), rs.getInt("meso"), rs.getInt("unk_1"), rs.getInt("unk_2"), rs.getInt("unk_3"), rs.getInt("extra_flags"), rs.getInt("mod"));
                    this.itemMods.put(Integer.valueOf(sn), ret);
                }
                rs.close();
                ps.close();
            }catch (Exception e) {
                FileoutputUtil.outError("logs/资料库异常.txt", (Throwable)e);
                e.printStackTrace();
            }
        }
        return ret;
    }
    
    private void refreshAllModInfo() {
        this.itemMods.clear();
        this.itemIdToSN.clear();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = (Connection)DBConPool.getInstance().getDataSource().getConnection();
            ps = con.prepareStatement("SELECT * FROM cashshop_modified_items");
            rs = ps.executeQuery();
            while (rs.next()) {
                final Integer sn = Integer.valueOf(rs.getInt("serial"));
                final CashModInfo ret = new CashModInfo((int)sn, rs.getInt("discount_price"), rs.getInt("mark"), rs.getInt("showup") > 0, rs.getInt("itemid"), rs.getInt("priority"), rs.getInt("package") > 0, rs.getInt("period"), rs.getInt("gender"), rs.getInt("count"), rs.getInt("meso"), rs.getInt("unk_1"), rs.getInt("unk_2"), rs.getInt("unk_3"), rs.getInt("extra_flags"), rs.getInt("mod"));
                this.itemMods.put(sn, ret);
                this.itemIdToSN.put(Integer.valueOf(ret.itemid), sn);
            }
            rs.close();
            ps.close();
        }catch (Exception e) {
            FileoutputUtil.outError("logs/资料库异常.txt", (Throwable)e);
            e.printStackTrace();
        }
        System.out.println("[加载成功] -> 商城系统加载全部完成");
    }
    
    public final Collection<server.CashItemInfo.CashModInfo> getAllModInfo() {
        if (this.itemMods.isEmpty()) {
            this.refreshAllModInfo();
        }
        return this.itemMods.values();
    }
    
    public final int[] getBestItems() {
        return CashItemFactory.bestItems;
    }
    
    public void clearItems() {
        this.refreshAllModInfo();
    }
    
    public final int getItemSN(final int itemid) {
        for (final Entry<Integer, CashItemInfo> ci : this.itemStats.entrySet()) {
            if (((CashItemInfo)ci.getValue()).getId() == itemid) {
                return ((CashItemInfo)ci.getValue()).getSN();
            }
        }
        return 0;
    }
    
    public final void clearCashShop() {
        this.itemStats.clear();
        this.itemPackage.clear();
        this.itemMods.clear();
        this.idLookup.clear();
        this.initialized = false;
        this.initialize();
    }
}
