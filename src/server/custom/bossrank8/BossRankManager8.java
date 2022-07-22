package server.custom.bossrank8;

import java.util.LinkedList;
import java.util.List;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import database.DatabaseConnection;
import java.util.HashMap;
import java.util.Map;
import client.MapleCharacter;

public class BossRankManager8
{
    public void setLog(final MapleCharacter player, final String 摇摇乐1, final byte b, final byte b0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public static BossRankManager8 getInstance() {
        return InstanceHolder.instance;
    }
    
    private BossRankManager8() {
    }
    
    public Map<String, BossRankInfo8> getInfoMap(final int cid) {
        final Map<String, BossRankInfo8> info_map = new HashMap<String, BossRankInfo8>();
        final Connection con1 = DatabaseConnection.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con1.prepareStatement("select * from bossrank8 where cid = ?");
            ps.setInt(1, cid);
            rs = ps.executeQuery();
            while (rs.next()) {
                final BossRankInfo8 info = new BossRankInfo8();
                info.setCid(rs.getInt("cid"));
                info.setCname(rs.getString("cname"));
                info.setBossname(rs.getString("bossname"));
                info.setPoints(rs.getInt("points"));
                info.setCount(rs.getInt("count"));
                info_map.put(info.getBossname(), info);
            }
        }
        catch (Exception Ex) {
            Ex.printStackTrace();
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException ex) {
                Logger.getLogger(BossRankManager8.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
            }
        }
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException ex2) {
                Logger.getLogger(BossRankManager8.class.getName()).log(Level.SEVERE, null, (Throwable)ex2);
            }
        }
        return info_map;
    }
    
    public BossRankInfo8 getInfo(final int cid, final String bossname) {
        BossRankInfo8 info = null;
        final Connection con1 = DatabaseConnection.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con1.prepareStatement("select * from bossrank8 where cid = ? and bossname = ?");
            ps.setInt(1, cid);
            ps.setString(2, bossname);
            rs = ps.executeQuery();
            if (rs.next()) {
                info = new BossRankInfo8();
                info.setCid(rs.getInt("cid"));
                info.setCname(rs.getString("cname"));
                info.setBossname(rs.getString("bossname"));
                info.setPoints(rs.getInt("points"));
                info.setCount(rs.getInt("count"));
            }
        }
        catch (Exception Ex) {
            Ex.printStackTrace();
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException ex) {
                Logger.getLogger(BossRankManager8.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
            }
        }
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException ex2) {
                Logger.getLogger(BossRankManager8.class.getName()).log(Level.SEVERE, null, (Throwable)ex2);
            }
        }
        return info;
    }
    
    public int setLog(final int cid, final String cname, final String bossname, final byte type, final int update) {
        int ret = -1;
        BossRankInfo8 info = this.getInfo(cid, bossname);
        boolean add = false;
        boolean doUpdate = true;
        if (info == null) {
            doUpdate = false;
            add = true;
            info = new BossRankInfo8();
            info.setCid(cid);
            info.setCname(cname);
            info.setBossname(bossname);
        }
        switch (type) {
            case 1: {
                ret = info.getPoints() + update;
                info.setPoints(ret);
                break;
            }
            case 2: {
                ret = info.getCount() + update;
                info.setCount(ret);
                break;
            }
            default: {
                doUpdate = false;
                break;
            }
        }
        if (!doUpdate) {
            if (add) {
                this.add(info);
            }
            return ret;
        }
        this.update(info);
        return ret;
    }
    
    public void update(final BossRankInfo8 info) {
        if (info == null) {
            return;
        }
        final Connection con1 = DatabaseConnection.getConnection();
        PreparedStatement ps = null;
        try {
            ps = con1.prepareStatement("update bossrank8 set points = ?,count = ?  where cid = ? and bossname = ?");
            ps.setInt(1, info.getPoints());
            ps.setInt(2, info.getCount());
            ps.setInt(3, info.getCid());
            ps.setString(4, info.getBossname());
            ps.executeUpdate();
        }
        catch (Exception Ex) {
            Ex.printStackTrace();
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (SQLException ex) {
                    Logger.getLogger(BossRankManager8.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
                }
            }
        }
        finally {
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (SQLException ex2) {
                    Logger.getLogger(BossRankManager8.class.getName()).log(Level.SEVERE, null, (Throwable)ex2);
                }
            }
        }
    }
    
    public void add(final BossRankInfo8 info) {
        if (info == null) {
            return;
        }
        final Connection con1 = DatabaseConnection.getConnection();
        PreparedStatement ps = null;
        try {
            ps = con1.prepareStatement("insert into bossrank8 (cid,cname,bossname,points,count) values (?,?,?,?,?)");
            ps.setInt(1, info.getCid());
            ps.setString(2, info.getCname());
            ps.setString(3, info.getBossname());
            ps.setInt(4, info.getPoints());
            ps.setInt(5, info.getCount());
            ps.executeUpdate();
        }
        catch (Exception Ex) {
            Ex.printStackTrace();
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (SQLException ex) {
                    Logger.getLogger(BossRankManager8.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
                }
            }
        }
        finally {
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (SQLException ex2) {
                    Logger.getLogger(BossRankManager8.class.getName()).log(Level.SEVERE, null, (Throwable)ex2);
                }
            }
        }
    }
    
    public List<BossRankInfo8> getRank(final String bossname, final int type) {
        final List<BossRankInfo8> list = new LinkedList<BossRankInfo8>();
        final Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            switch (type) {
                case 1: {
                    ps = con.prepareStatement("SELECT * FROM bossrank8 WHERE bossname = ?  ORDER BY points DESC LIMIT 100");
                    break;
                }
                case 2: {
                    ps = con.prepareStatement("SELECT * FROM bossrank8 WHERE bossname = ?  ORDER BY count DESC LIMIT 100");
                    break;
                }
                default: {
                    ps = con.prepareStatement("SELECT * FROM bossrank8 WHERE bossname = ?  ORDER BY points DESC LIMIT 100");
                    break;
                }
            }
            ps.setString(1, bossname);
            rs = ps.executeQuery();
            while (rs.next()) {
                final BossRankInfo8 info = new BossRankInfo8();
                info.setCid(rs.getInt("cid"));
                info.setCname(rs.getString("cname"));
                info.setBossname(rs.getString("bossname"));
                info.setPoints(rs.getInt("points"));
                info.setCount(rs.getInt("count"));
                list.add(info);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException ex) {
                Logger.getLogger(BossRankManager8.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
            }
        }
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException ex2) {
                Logger.getLogger(BossRankManager8.class.getName()).log(Level.SEVERE, null, (Throwable)ex2);
            }
        }
        return list;
    }
    
    private static class InstanceHolder
    {
        public static final BossRankManager8 instance;
        
        static {
            instance = new BossRankManager8();
        }
    }
}
