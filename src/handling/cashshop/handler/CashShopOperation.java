package handling.cashshop.handler;

import java.util.Iterator;
import java.util.Map;
import java.util.List;
import client.inventory.MapleRing;
import server.AutobanManager;
import client.inventory.MapleInventoryType;
import client.inventory.MapleInventoryIdentifier;
import tools.Pair;
import server.CashItemInfo;
import client.inventory.IItem;
import java.util.HashMap;
import constants.ServerConfig;
import constants.GameConstants;
import server.CashItemFactory;
import server.MapleItemInformationProvider;
import client.inventory.MaplePet;
import server.MapleInventoryManipulator;
import java.sql.SQLException;
import client.MapleCharacterUtil;
import tools.packet.MTSCSPacket;
import handling.world.World.Broadcast;
import handling.login.LoginServer;
import tools.MaplePacketCreator;
import handling.world.World;
import handling.world.CharacterTransfer;
import handling.cashshop.CashShopServer;
import tools.FileoutputUtil;
import handling.channel.ChannelServer;
import client.MapleCharacter;
import client.MapleClient;
import client.inventory.ItemFlag;
import constants.OtherSettings;
import tools.data.LittleEndianAccessor;

public class CashShopOperation
{
    public static void LeaveCashShop(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        if (chr == null) {
            return;
        }
        final int channel = c.getChannel();
        final ChannelServer toch = ChannelServer.getInstance(channel);
        if (toch == null) {
            FileoutputUtil.logToFile("logs/Data/离开商城.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 账号 " + c.getAccountName() + " 账号ID " + c.getAccID() + " 角色名 " + chr.getName() + " 角色ID " + chr.getId());
            c.getSession().close();
            return;
        }
        CashShopServer.getPlayerStorage().deregisterPlayer(chr);
        c.updateLoginState(1, c.getSessionIPAddress());
        try {
            World.channelChangeData(new CharacterTransfer(chr), chr.getId(), c.getChannel());
            c.sendPacket(MaplePacketCreator.getChannelChange(c, Integer.parseInt(ChannelServer.getInstance(c.getChannel()).getSocket().split(":")[1])));
        }
        finally {
            chr.saveToDB(false, true);
            c.setPlayer(null);
            c.setReceiving(false);
        }
    }
    
    public static void EnterCashShop(final int playerid, final MapleClient client) {
        final CharacterTransfer transfer = CashShopServer.getPlayerStorage().getPendingCharacter(playerid);
        if (transfer == null) {
            client.getSession().close();
            return;
        }
        final MapleCharacter chr = MapleCharacter.ReconstructChr(transfer, client, false);
        client.setPlayer(chr);
        client.setAccID(chr.getAccountID());
        client.setSecondPassword(chr.getAccountSecondPassword());
        final int state = client.getLoginState();
        boolean allowLogin = false;
        if ((state == 1 || state == 6) && !World.isCharacterListConnected(client.loadCharacterNames(client.getWorld()))) {
            allowLogin = true;
        }
        if (!allowLogin) {
            client.setPlayer(null);
            client.getSession().close();
            return;
        }
        if (!LoginServer.CanLoginKey(client.getPlayer().getLoginKey(), client.getPlayer().getAccountID()) || (LoginServer.getLoginKey(client.getPlayer().getAccountID()) == null && !client.getPlayer().getLoginKey().isEmpty())) {
            FileoutputUtil.logToFile("logs/Data/客戶端登录KEY异常.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + client.getSession().remoteAddress().toString().split(":")[0] + " 账号: " + client.getAccountName() + " 客戶端key：" + LoginServer.getLoginKey(client.getPlayer().getAccountID()) + " HuiMs服务端key：" + client.getPlayer().getLoginKey() + " 进入商城1");
            Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM 密语系統] 非法登录 账号 " + client.getAccountName()));
            client.getSession().close();
            return;
        }
        if (!LoginServer.CanServerKey(client.getPlayer().getServerKey(), client.getPlayer().getAccountID()) || (LoginServer.getServerKey(client.getPlayer().getAccountID()) == null && !client.getPlayer().getServerKey().isEmpty())) {
            FileoutputUtil.logToFile("logs/Data/客戶端頻道KEY异常.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + client.getSession().remoteAddress().toString().split(":")[0] + " 账号: " + client.getAccountName() + " 客戶端key：" + LoginServer.getServerKey(client.getPlayer().getAccountID()) + " HuiMs服务端key：" + client.getPlayer().getServerKey() + " 进入商城2");
            Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM 密语系統] 非法登录 账号 " + client.getAccountName()));
            client.getSession().close();
            return;
        }
        if (!LoginServer.CanClientKey(client.getPlayer().getClientKey(), client.getPlayer().getAccountID()) || (LoginServer.getClientKey(client.getPlayer().getAccountID()) == null && !client.getPlayer().getClientKey().isEmpty())) {
            FileoutputUtil.logToFile("logs/Data/客戶端进入KEY异常.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + client.getSession().remoteAddress().toString().split(":")[0] + " 账号: " + client.getAccountName() + " 客戶端key：" + LoginServer.getClientKey(client.getPlayer().getAccountID()) + " HuiMs服务端key：" + client.getPlayer().getClientKey() + " 进入商城3");
            Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM 密语系統] 非法登录 账号 " + client.getAccountName()));
            client.getSession().close();
            return;
        }
        client.updateLoginState(5, client.getSessionIPAddress());
        CashShopServer.getPlayerStorage().registerPlayer(chr);
        client.sendPacket(MTSCSPacket.warpCS(client));
        sendCashShopUpdate(client);
        client.getPlayer();
        if (MapleCharacter.getCharacterNameById2(playerid) == null) {
            FileoutputUtil.logToFile("logs/Data/角色不存在.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + client.getSession().remoteAddress().toString().split(":")[0] + " 账号 " + client.getAccountName() + "進入商城");
            Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM 密语系統] 非法登录不存在角色 账号 " + client.getAccountName()));
            client.getSession().close();
            return;
        }
        if (!LoginServer.CanLoginKey(client.getPlayer().getLoginKey(), client.getPlayer().getAccountID()) || (LoginServer.getLoginKey(client.getPlayer().getAccountID()) == null && !client.getPlayer().getLoginKey().isEmpty())) {
            FileoutputUtil.logToFile("logs/Data/客戶端进入KEY异常.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + client.getSession().remoteAddress().toString().split(":")[0] + " 账号: " + client.getAccountName() + " 客戶端key：" + LoginServer.getLoginKey(client.getPlayer().getAccountID()) + " 服务端key：" + client.getPlayer().getLoginKey() + " 进入商城1");
            Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM 密语系統] 非法登录 账号 " + client.getAccountName()));
            client.getSession().close();
            return;
        }
        if (!LoginServer.CanServerKey(client.getPlayer().getServerKey(), client.getPlayer().getAccountID()) || (LoginServer.getServerKey(client.getPlayer().getAccountID()) == null && !client.getPlayer().getServerKey().isEmpty())) {
            FileoutputUtil.logToFile("logs/Data/客戶端进入KEY异常.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + client.getSession().remoteAddress().toString().split(":")[0] + " 账号: " + client.getAccountName() + " 客戶端key：" + LoginServer.getServerKey(client.getPlayer().getAccountID()) + " 服务端key：" + client.getPlayer().getServerKey() + " 进入商城2");
            Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM 密语系統] 非法登录 账号 " + client.getAccountName()));
            client.getSession().close();
            return;
        }
        if (!LoginServer.CanClientKey(client.getPlayer().getClientKey(), client.getPlayer().getAccountID()) || (LoginServer.getClientKey(client.getPlayer().getAccountID()) == null && !client.getPlayer().getClientKey().isEmpty())) {
            FileoutputUtil.logToFile("logs/Data/客戶端进入KEY异常.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + client.getSession().remoteAddress().toString().split(":")[0] + " 账号: " + client.getAccountName() + " 客戶端key：" + LoginServer.getClientKey(client.getPlayer().getAccountID()) + " 服务端key：" + client.getPlayer().getClientKey() + " 进入商城3");
            Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM 密语系統] 非法登录 账号 " + client.getAccountName()));
            client.getSession().close();
        }
    }
    
    public static void sendCashShopUpdate(final MapleClient c) {
        c.sendPacket(MTSCSPacket.showCashShopAcc(c));
        c.sendPacket(MTSCSPacket.showGifts(c));
        RefreshCashShop(c);
        c.sendPacket(MTSCSPacket.sendShowWishList(c.getPlayer()));
    }
    
    public static void CouponCode(final String code, final MapleClient c) {
        boolean validcode = false;
        int type = -1;
        int item = -1;
        int size = -1;
        int time = -1;
        validcode = MapleCharacterUtil.getNXCodeValid(code.toUpperCase(), validcode);
        if (validcode) {
            type = MapleCharacterUtil.getNXCodeType(code);
            item = MapleCharacterUtil.getNXCodeItem(code);
            size = MapleCharacterUtil.getNXCodeSize(code);
            time = MapleCharacterUtil.getNXCodeTime(code);
            if (type <= 4) {
                try {
                    MapleCharacterUtil.setNXCodeUsed(c.getPlayer().getName(), code);
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            int maplePoints = 0;
            int mesos = 0;
            int as = 0;
            String cc = "";
            String tt = "";
            switch (type) {
                case 1: {
                    c.getPlayer().modifyCSPoints(1, item, false);
                    maplePoints = item;
                    cc = "点卷";
                    break;
                }
                case 2: {
                    c.getPlayer().modifyCSPoints(2, item, false);
                    maplePoints = item;
                    cc = "抵用卷";
                    break;
                }
                case 3: {
                    MapleInventoryManipulator.addById(c, item, (short)size, "优待卷礼品.", null, (long)time);
                    as = 1;
                    break;
                }
                case 4: {
                    c.getPlayer().gainMeso(item, false);
                    mesos = item;
                    cc = "金币";
                    break;
                }
            }
            if (time == -1) {
                tt = "永久";
                as = 2;
            }
            switch (as) {
                case 1: {
                    c.getPlayer().dropMessage(1, "已成功使用优待卷获得" + MapleItemInformationProvider.getInstance().getName(item) + time + "天 x" + size + "。");
                    break;
                }
                case 2: {
                    c.getPlayer().dropMessage(1, "已成功使用优待卷获得" + MapleItemInformationProvider.getInstance().getName(item) + "永久 x" + size + "。");
                    break;
                }
                default: {
                    c.getPlayer().dropMessage(1, "已成功使用优待卷获得" + item + cc);
                    break;
                }
            }
        }
        else {
            c.sendPacket(MTSCSPacket.sendCSFail(validcode ? 165 : 167));
        }
        RefreshCashShop(c);
    }
    
    public static final void BuyCashItem(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        final OtherSettings item_id = new OtherSettings();
        final String[] itembp_id = item_id.getItempb_id();
        final String[] itemjy_id = item_id.getItemjy_id();
        final int action = slea.readByte();
        switch (action) {
            case 3:
            case 30: {
                final int useNX = slea.readByte() + 1;
                final int snCS = slea.readInt();
                final CashItemInfo cItem = CashItemFactory.getInstance().getItem(snCS);
                List<CashItemInfo> ccc = null;
                if (action == 30 && cItem != null) {
                    ccc = CashItemFactory.getInstance().getPackageItems(cItem.getId());
                }
                boolean canBuy = true;
                int errorCode = 0;
                if (cItem == null || (action == 30 && (ccc == null || (ccc != null && ccc.isEmpty()))) || useNX < 1 || useNX > 2) {
                    canBuy = false;
                }else if (!cItem.onSale()) {
                    canBuy = false;
                    errorCode = 225;
                }else if (chr.getCSPoints(useNX) < cItem.getPrice()) {
                    if (useNX == 1) {
                        errorCode = 168;
                    }
                    else {
                        errorCode = 225;
                    }
                    canBuy = false;
                }else if (!cItem.genderEquals((int)c.getPlayer().getGender())) {
                    canBuy = false;
                    errorCode = 186;
                }else if (c.getPlayer().getCashInventory().getItemsSize() >= 100) {
                    canBuy = false;
                    errorCode = 175;
                }
                if (canBuy && cItem != null) {
                    for (final int i : GameConstants.cashBlock) {
                        if (cItem.getId() == i) {
                            c.getPlayer().dropMessage(1, GameConstants.getCashBlockedMsg(cItem.getId()));
                            RefreshCashShop(c);
                            return;
                        }
                    }
                    for (int j = 0; j < itembp_id.length; ++j) {
                        if (cItem.getId() == Integer.parseInt(itembp_id[j])) {
                            c.getPlayer().dropMessage(1, "这个物品是禁止购买的.");
                            RefreshCashShop(c);
                            return;
                        }
                    }
                    if (cItem.getPrice() < 100) {
                        c.getPlayer().dropMessage(1, "价格低于100点卷的物品是禁止购买的.");
                        RefreshCashShop(c);
                        return;
                    }
                    if (action == 3) {
                        final IItem itemz = chr.getCashInventory().toItem(cItem, chr);
                        if (itemz != null && itemz.getUniqueId() > 0 && itemz.getItemId() == cItem.getId() && itemz.getQuantity() == cItem.getCount()) {
                            if (useNX == 1) {
                                byte flag = itemz.getFlag();
                                final boolean 交易 = true;
                                if (交易) {
                                    if (itemz.getType() == MapleInventoryType.EQUIP.getType()) {
                                        flag |= (byte)ItemFlag.KARMA_EQ.getValue();
                                    }
                                    else {
                                        flag |= (byte)ItemFlag.KARMA_USE.getValue();
                                    }
                                    itemz.setFlag(flag);
                                }
                            }
                            chr.getCashInventory().addToInventory(itemz);
                            c.sendPacket(MTSCSPacket.showBoughtCashItem(itemz, cItem.getSN(), c.getAccID()));
                            if (ServerConfig.LOG_CSBUY) {
                                FileoutputUtil.logToFile("logs/Data/商城购买.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 账号: " + c.getAccountName() + " 玩家: " + c.getPlayer().getName() + " 使用了" + ((useNX == 1) ? "点券" : "抵用") + cItem.getPrice() + "点 来购买" + cItem.getId() + "x" + cItem.getCount());
                            }
                        }else {
                            c.sendPacket(MTSCSPacket.sendCSFail(errorCode));
                        }
                        chr.modifyCSPoints(useNX, -cItem.getPrice(), false);
                    }
                    else {
                        final Map<Integer, IItem> ccz = (Map<Integer, IItem>)new HashMap();
                        for (final CashItemInfo k : ccc) {
                            for (final int iz : GameConstants.cashBlock) {
                                if (k.getId() == iz) {}
                            }
                            final IItem itemz2 = chr.getCashInventory().toItem(k, chr, MapleInventoryManipulator.getUniqueId(k.getId(), null), "");
                            if (itemz2 != null && itemz2.getUniqueId() > 0) {
                                if (itemz2.getItemId() != k.getId()) {
                                    continue;
                                }
                                ccz.put(Integer.valueOf(k.getSN()), itemz2);
                                c.getPlayer().getCashInventory().addToInventory(itemz2);
                            }
                        }
                        if (ServerConfig.LOG_CSBUY) {
                            FileoutputUtil.logToFile("logs/Data/商城购买.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 账号: " + c.getAccountName() + " 玩家: " + c.getPlayer().getName() + " 使用了" + ((useNX == 1) ? "点券" : "抵用") + cItem.getPrice() + "点 来购买套装" + cItem.getId() + "x" + cItem.getCount());
                        }
                        chr.modifyCSPoints(useNX, -cItem.getPrice(), false);
                        c.sendPacket(MTSCSPacket.showBoughtCashPackage(ccz, c.getAccID()));
                    }
                }
                else {
                    c.sendPacket(MTSCSPacket.sendCSFail(errorCode));
                }
                RefreshCashShop(c);
                break;
            }
            case 4: {
                final int sn = slea.readInt();
                final int toCharge = slea.readByte() + 1;
                final String characterName = slea.readMapleAsciiString();
                final String message = slea.readMapleAsciiString();
                boolean canBuy = true;
                int errorCode = 0;
                final CashItemInfo cItem2 = CashItemFactory.getInstance().getItem(sn);
                final Pair<Integer, Pair<Integer, Integer>> info = MapleCharacterUtil.getInfoByName(characterName, (int)c.getPlayer().getWorld());
                if (cItem2 == null) {
                    canBuy = false;
                }
                else if (!cItem2.onSale()) {
                    canBuy = false;
                    errorCode = 225;
                }
                else if (chr.getCSPoints(toCharge) < cItem2.getPrice()) {
                    errorCode = 168;
                    canBuy = false;
                }
                else if (message.getBytes().length < 1 || message.getBytes().length > 32) {
                    canBuy = false;
                    errorCode = 225;
                }
                else if (info == null) {
                    canBuy = false;
                    errorCode = 172;
                }
                else if (((Integer)(info.getRight()).getLeft()).intValue() == c.getAccID() || ((Integer)info.getLeft()).intValue() == c.getPlayer().getId()) {
                    canBuy = false;
                    errorCode = 171;
                }
                else if (!cItem2.genderEquals(((Integer)(info.getRight()).getRight()).intValue())) {
                    canBuy = false;
                    errorCode = 176;
                }
                if (canBuy && info != null && cItem2 != null) {
                    for (final int l : GameConstants.cashBlock) {
                        if (cItem2.getId() == l) {
                            c.getPlayer().dropMessage(1, GameConstants.getCashBlockedMsg(cItem2.getId()));
                            return;
                        }
                    }
                    c.getPlayer().getCashInventory().gift(((Integer)info.getLeft()).intValue(), c.getPlayer().getName(), message, cItem2.getSN(), MapleInventoryIdentifier.getInstance());
                    c.getPlayer().modifyCSPoints(1, -cItem2.getPrice(), false);
                    if (ServerConfig.LOG_CSBUY) {
                        FileoutputUtil.logToFile("logs/Data/商城送礼.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 账号: " + c.getAccountName() + " 玩家: " + c.getPlayer().getName() + " 使用了点券" + cItem2.getPrice() + "点 贈送了" + cItem2.getId() + "x" + cItem2.getCount() + " 给" + characterName);
                    }
                    c.sendPacket(MTSCSPacket.sendGift(characterName, cItem2, cItem2.getPrice() / 2, false));
                    chr.sendNote(characterName, chr.getName() + " 送了你礼物! 趕快去商城确认看看.", 0);
                    final MapleCharacter receiver = c.getChannelServer().getPlayerStorage().getCharacterByName(characterName);
                    if (receiver != null) {
                        receiver.showNote();
                    }
                }
                else {
                    c.sendPacket(MTSCSPacket.sendCSFail(errorCode));
                }
                RefreshCashShop(c);
                break;
            }
            case 5: {
                final boolean wishlistboolean = true;
                if (!wishlistboolean) {
                    RefreshCashShop(c);
                    return;
                }
                chr.clearWishlist();
                if (slea.available() < 40L) {
                    c.sendPacket(MTSCSPacket.sendCSFail(0));
                    RefreshCashShop(c);
                    return;
                }
                final int[] wishlist = new int[10];
                for (int m = 0; m < 10; ++m) {
                    wishlist[m] = slea.readInt();
                }
                chr.setWishlist(wishlist);
                c.sendPacket(MTSCSPacket.setWishList(chr));
                RefreshCashShop(c);
                break;
            }
            case 6: {
                final int useNX = slea.readByte() + 1;
                final boolean coupon = slea.readByte() > 0;
                if (coupon) {
                    final int abc = slea.readInt();
                    final MapleInventoryType type = getInventoryType(abc);
                    if (chr.getCSPoints(useNX) >= 1100 && chr.getInventory(type).getSlotLimit() < 89) {
                        chr.modifyCSPoints(useNX, -1100, false);
                        chr.getInventory(type).addSlot((byte)8);
                        chr.dropMessage(1, "栏位已经扩充到 " + (int)chr.getInventory(type).getSlotLimit());
                        if (ServerConfig.LOG_CSBUY) {
                            FileoutputUtil.logToFile("logs/Data/商城扩充.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 账号: " + c.getAccountName() + " 玩家: " + c.getPlayer().getName() + " 使用了" + ((useNX == 1) ? "点券" : "抵用") + "1100点 来购买扩充栏位" + type.name() + "8格 目前共有" + (int)chr.getInventory(type).getSlotLimit() + "格");
                        }
                    }
                    else {
                        c.getSession().write(MTSCSPacket.sendCSFail(164));
                    }
                }
                else {
                    final MapleInventoryType type2 = MapleInventoryType.getByType(slea.readByte());
                    if (chr.getCSPoints(useNX) >= 600 && chr.getInventory(type2).getSlotLimit() < 93) {
                        chr.modifyCSPoints(useNX, -600, false);
                        chr.getInventory(type2).addSlot((byte)4);
                        chr.dropMessage(1, "栏位已经扩充到 " + (int)chr.getInventory(type2).getSlotLimit());
                        if (ServerConfig.LOG_CSBUY) {
                            FileoutputUtil.logToFile("logs/Data/商城扩充.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 账号: " + c.getAccountName() + " 玩家: " + c.getPlayer().getName() + " 使用了" + ((useNX == 1) ? "点券" : "抵用") + "600点 来购买扩充栏位" + type2.name() + "8格 目前共有" + (int)chr.getInventory(type2).getSlotLimit() + "格");
                        }
                    }
                    else {
                        c.getSession().write(MTSCSPacket.sendCSFail(164));
                    }
                }
                RefreshCashShop(c);
                break;
            }
            case 7: {
                final int useNX = slea.readByte() + 1;
                if (chr.getCSPoints(useNX) >= (ServerConfig.DISCOUNTED ? 540 : 600) && chr.getStorage().getSlots() < 45) {
                    chr.modifyCSPoints(useNX, ServerConfig.DISCOUNTED ? -540 : -600, false);
                    chr.getStorage().increaseSlots((byte)4);
                    chr.getStorage().saveToDB();
                    if (ServerConfig.LOG_CSBUY) {
                        FileoutputUtil.logToFile("logs/Data/商城扩充.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 账号: " + c.getAccountName() + " 玩家: " + c.getPlayer().getName() + " 使用了" + ((useNX == 1) ? "点券" : "抵用") + "100点 来购买扩充栏位仓库4格 目前共有" + chr.getStorage().getSlots() + "格");
                    }
                }
                else {
                    c.sendPacket(MTSCSPacket.sendCSFail(164));
                }
                RefreshCashShop(c);
                break;
            }
            case 8: {
                final int useNX = slea.readByte() + 1;
                final CashItemInfo item = CashItemFactory.getInstance().getItem(slea.readInt());
                final int slots = c.getCharacterSlots();
                if (item == null || c.getPlayer().getCSPoints(1) < item.getPrice() || slots > 15) {
                    c.sendPacket(MTSCSPacket.sendCSFail(0));
                    RefreshCashShop(c);
                    return;
                }
                c.getPlayer().modifyCSPoints(useNX, -item.getPrice(), false);
                if (c.gainCharacterSlot()) {
                    c.sendPacket(MTSCSPacket.increasedStorageSlots(slots + 1));
                    chr.dropMessage(1, "角色栏位扩充成功");
                    if (ServerConfig.LOG_CSBUY) {
                        FileoutputUtil.logToFile("logs/Data/商城扩充.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 账号: " + c.getAccountName() + " 玩家: " + c.getPlayer().getName() + " 使用了" + ((useNX == 1) ? "点券" : "抵用") + item.getPrice() + "点 来购买扩充角色栏位 目前共有" + c.getCharacterSlots() + "格");
                    }
                }
                else {
                    c.sendPacket(MTSCSPacket.sendCSFail(0));
                }
                RefreshCashShop(c);
                break;
            }
            case 13: {
                final IItem item2 = c.getPlayer().getCashInventory().findByCashId((int)slea.readLong());
                for (int i2 = 0; i2 < itembp_id.length; ++i2) {
                    if (item2.getItemId() == Integer.parseInt(itembp_id[i2])) {
                        c.getPlayer().dropMessage(1, "这个物品是禁止拿出的.");
                        RefreshCashShop(c);
                        return;
                    }
                }
                if (item2 != null && item2.getQuantity() > 0 && MapleInventoryManipulator.checkSpace(c, item2.getItemId(), (int)item2.getQuantity(), item2.getOwner())) {
                    final IItem item_ = item2.copy();
                    final short pos = MapleInventoryManipulator.addbyItem(c, item_, true);
                    if (pos >= 0) {
                        if (item_.getPet() != null) {
                            item_.getPet().setInventoryPosition(pos);
                            c.getPlayer().addPet(item_.getPet());
                        }
                        c.getPlayer().getCashInventory().removeFromInventory(item2);
                        c.sendPacket(MTSCSPacket.confirmFromCSInventory(item_, pos));
                        FileoutputUtil.logToFile("logs/Data/商城拿出.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 账号: " + c.getAccountName() + " 玩家: " + c.getPlayer().getName() + " 从商城拿出 " + item_.getItemId() + "x" + (int)item_.getQuantity());
                    }
                    else {
                        c.sendPacket(MTSCSPacket.sendCSFail(177));
                    }
                }
                else {
                    c.sendPacket(MTSCSPacket.sendCSFail(177));
                }
                RefreshCashShop(c);
                break;
            }
            case 14: {
                final int uniqueid = (int)slea.readLong();
                final MapleInventoryType type3 = MapleInventoryType.getByType(slea.readByte());
                final IItem item3 = c.getPlayer().getInventory(type3).findByUniqueId(uniqueid);
                if (item3.getItemId() == 5150043 || item3.getItemId() == 5150037) {
                    RefreshCashShop(c);
                    return;
                }
                for (int i3 = 0; i3 < itembp_id.length; ++i3) {
                    if (item3.getItemId() == Integer.parseInt(itembp_id[i3])) {
                        c.getPlayer().dropMessage(1, "这个物品是禁止存入的.");
                        RefreshCashShop(c);
                        return;
                    }
                }
                if (item3 != null && item3.getQuantity() > 0 && item3.getUniqueId() > 0 && c.getPlayer().getCashInventory().getItemsSize() < 100) {
                    final IItem item_2 = item3.copy();
                    c.getPlayer().getInventory(type3).removeItem(item3.getPosition(), item3.getQuantity(), false);
                    final int sn2 = CashItemFactory.getInstance().getItemSN(item_2.getItemId());
                    if (item_2.getPet() != null) {
                        c.getPlayer().removePet(item_2.getPet());
                    }
                    item_2.setPosition((short)0);
                    c.getPlayer().getCashInventory().addToInventory(item_2);
                    c.sendPacket(MTSCSPacket.confirmToCSInventory(item3, c.getAccID(), sn2));
                    FileoutputUtil.logToFile("logs/Data/商城存入.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 账号: " + c.getAccountName() + " 玩家: " + c.getPlayer().getName() + " 从商城存入 " + item_2.getItemId() + "x" + (int)item_2.getQuantity());
                }
                else {
                    c.sendPacket(MTSCSPacket.sendCSFail(177));
                }
                RefreshCashShop(c);
                break;
            }
            case 26: {
                final int toCharge2 = 2;
                final long uniqueId = (long)(int)slea.readLong();
                final IItem item4 = c.getPlayer().getCashInventory().findByCashId((int)uniqueId);
                if (item4 == null) {
                    RefreshCashShop(c);
                    return;
                }
                final int sn2 = CashItemFactory.getInstance().getSnByItemItd(item4.getItemId());
                final CashItemInfo cItem3 = CashItemFactory.getInstance().getItem(sn2);
                if (!MapleItemInformationProvider.getInstance().isCash(item4.getItemId())) {
                    AutobanManager.getInstance().autoban(chr.getClient(), "商城非法换购道具.");
                    return;
                }
                final int Money = cItem3.getPrice() / 10 * 3;
                c.getPlayer().getCashInventory().removeFromInventory(item4);
                chr.modifyCSPoints(toCharge2, Money, false);
                chr.dropMessage(1, "成功换购抵用券" + Money + "点。");
                if (ServerConfig.LOG_CSBUY) {
                    FileoutputUtil.logToFile("logs/Data/商城换购.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 账号: " + c.getAccountName() + " 玩家: " + c.getPlayer().getName() + " 使用了 " + item4.getItemId() + " 换购获得抵用卷 " + Money);
                }
                RefreshCashShop(c);
                break;
            }
            case 31: {
                final int a = 0;
                if (a == 0) {
                    c.getPlayer().dropMessage(1, "礼包不支持购买");
                    RefreshCashShop(c);
                    return;
                }
                final String secondPassword = slea.readMapleAsciiString();
                final int sn3 = slea.readInt();
                final String characterName2 = slea.readMapleAsciiString();
                final String message2 = slea.readMapleAsciiString();
                final CashItemInfo cItem3 = CashItemFactory.getInstance().getItem(sn3);
                final IItem item5 = chr.getCashInventory().toItem(cItem3);
                final Pair<Integer, Pair<Integer, Integer>> info = MapleCharacterUtil.getInfoByName(characterName2, (int)c.getPlayer().getWorld());
                if (c.getSecondPassword() != null) {
                    if (secondPassword == null) {
                        c.getPlayer().dropMessage(1, "请输入密码。");
                        RefreshCashShop(c);
                        return;
                    }
                    if (!c.getCheckSecondPassword(secondPassword)) {
                        c.getPlayer().dropMessage(1, "密码错误。");
                        RefreshCashShop(c);
                        return;
                    }
                    if (info == null || ((Integer)info.getLeft()).intValue() <= 0 || ((Integer)info.getLeft()).intValue() == c.getPlayer().getId() || ((Integer)(info.getRight()).getLeft()).intValue() == c.getAccID()) {
                        c.sendPacket(MTSCSPacket.sendCSFail(162));
                        RefreshCashShop(c);
                        return;
                    }
                    if (!cItem3.genderEquals(((Integer)(info.getRight()).getRight()).intValue())) {
                        c.sendPacket(MTSCSPacket.sendCSFail(163));
                        RefreshCashShop(c);
                        return;
                    }
                    for (final int l : GameConstants.cashBlock) {
                        if (cItem3.getId() == l) {
                            c.getPlayer().dropMessage(1, GameConstants.getCashBlockedMsg(cItem3.getId()));
                            RefreshCashShop(c);
                            return;
                        }
                    }
                    c.getPlayer().getCashInventory().gift(((Integer)info.getLeft()).intValue(), c.getPlayer().getName(), message2, cItem3.getSN(), MapleInventoryIdentifier.getInstance());
                    c.getPlayer().modifyCSPoints(1, -cItem3.getPrice(), false);
                    c.sendPacket(MTSCSPacket.sendGift(characterName2, cItem3, cItem3.getPrice() / 2, false));
                    chr.sendNote(characterName2, chr.getName() + " 送了你礼物! 趕快去商城确认看看.", 0);
                    final MapleCharacter receiver = c.getChannelServer().getPlayerStorage().getCharacterByName(characterName2);
                    if (receiver != null) {
                        receiver.showNote();
                    }
                }
                RefreshCashShop(c);
                break;
            }
            case 32: {
                final CashItemInfo item6 = CashItemFactory.getInstance().getItem(slea.readInt());
                if (item6 == null || !MapleItemInformationProvider.getInstance().isQuestItem(item6.getId())) {
                    c.sendPacket(MTSCSPacket.sendCSFail(0));
                    RefreshCashShop(c);
                    return;
                }
                if (c.getPlayer().getMeso() < item6.getPrice()) {
                    c.sendPacket(MTSCSPacket.sendCSFail(184));
                    RefreshCashShop(c);
                    return;
                }
                if (c.getPlayer().getInventory(GameConstants.getInventoryType(item6.getId())).getNextFreeSlot() < 0) {
                    c.sendPacket(MTSCSPacket.sendCSFail(177));
                    RefreshCashShop(c);
                    return;
                }
                for (final int iz2 : GameConstants.cashBlock) {
                    if (item6.getId() == iz2) {
                        c.getPlayer().dropMessage(1, GameConstants.getCashBlockedMsg(item6.getId()));
                        RefreshCashShop(c);
                        return;
                    }
                }
                final byte pos2 = MapleInventoryManipulator.addId(c, item6.getId(), (short)item6.getCount(), null);
                if (pos2 < 0) {
                    c.sendPacket(MTSCSPacket.sendCSFail(177));
                    RefreshCashShop(c);
                    return;
                }
                chr.gainMeso(-item6.getPrice(), false);
                c.sendPacket(MTSCSPacket.showBoughtCSQuestItem(item6.getPrice(), (short)item6.getCount(), pos2, item6.getId()));
                RefreshCashShop(c);
                break;
            }
            case 29:
            case 36: {
                final int sn = slea.readInt();
                final String partnerName = slea.readMapleAsciiString();
                final String message3 = slea.readMapleAsciiString();
                final CashItemInfo cItem4 = CashItemFactory.getInstance().getItem(sn);
                final Pair<Integer, Pair<Integer, Integer>> info2 = MapleCharacterUtil.getInfoByName(partnerName, (int)c.getPlayer().getWorld());
                boolean canBuy2 = true;
                int errorCode2 = 0;
                if (cItem4 == null) {
                    canBuy2 = false;
                }
                else if (!cItem4.onSale()) {
                    canBuy2 = false;
                    errorCode2 = 225;
                }
                else if (chr.getCSPoints(1) < cItem4.getPrice()) {
                    errorCode2 = 168;
                    canBuy2 = false;
                }
                else if (message3.getBytes().length < 1 || message3.getBytes().length > 74) {
                    canBuy2 = false;
                    errorCode2 = 225;
                }
                else if (info2 == null) {
                    canBuy2 = false;
                    errorCode2 = 172;
                }
                else if (((Integer)(info2.getRight()).getLeft()).intValue() == c.getAccID() || ((Integer)info2.getLeft()).intValue() == c.getPlayer().getId()) {
                    canBuy2 = false;
                    errorCode2 = 171;
                }
                else if (!cItem4.genderEquals(((Integer)(info2.getRight()).getRight()).intValue())) {
                    canBuy2 = false;
                    errorCode2 = 176;
                }
                else if (!GameConstants.isEffectRing(cItem4.getId())) {
                    canBuy2 = false;
                    errorCode2 = 0;
                }
                for (int i4 = 0; i4 < itembp_id.length; ++i4) {
                    if (cItem4.getId() == Integer.parseInt(itembp_id[i4])) {
                        c.getPlayer().dropMessage(1, "这个物品是禁止购买的.");
                        RefreshCashShop(c);
                        return;
                    }
                }
                if (cItem4.getPrice() < 100) {
                    c.getPlayer().dropMessage(1, "价格低于100点卷的物品是禁止购买的.");
                    RefreshCashShop(c);
                    return;
                }
                if (canBuy2 && info2 != null && cItem4 != null) {
                    for (final int i5 : GameConstants.cashBlock) {
                        if (cItem4.getId() == i5) {
                            c.getPlayer().dropMessage(1, GameConstants.getCashBlockedMsg(cItem4.getId()));
                            RefreshCashShop(c);
                            return;
                        }
                    }
                    final int err = MapleRing.createRing(cItem4.getId(), c.getPlayer(), partnerName, message3, ((Integer)info2.getLeft()).intValue(), cItem4.getSN());
                    if (err != 1) {
                        c.sendPacket(MTSCSPacket.sendCSFail(0));
                        RefreshCashShop(c);
                        return;
                    }
                    c.getPlayer().modifyCSPoints(1, -cItem4.getPrice(), false);
                    chr.sendNote(partnerName, chr.getName() + " 送了你礼物! 趕快去商城确认看看.", 0);
                    final MapleCharacter receiver = c.getChannelServer().getPlayerStorage().getCharacterByName(partnerName);
                    if (receiver != null) {
                        receiver.showNote();
                    }
                    if (ServerConfig.LOG_CSBUY) {
                        FileoutputUtil.logToFile("logs/Data/商城送礼.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 账号: " + c.getAccountName() + " 玩家: " + c.getPlayer().getName() + " 使用了点券" + cItem4.getPrice() + "点 贈送了" + cItem4.getId() + "x" + cItem4.getCount() + " 给" + partnerName);
                    }
                }
                else {
                    System.out.println(errorCode2 + ":" + canBuy2);
                    c.sendPacket(MTSCSPacket.sendCSFail(errorCode2));
                }
                RefreshCashShop(c);
                break;
            }
            case 49: {
                RefreshCashShop(c);
                break;
            }
            case 51: {
                final CashItemInfo item6 = CashItemFactory.getInstance().getItem(slea.readInt());
                if (item6 == null || c.getPlayer().getCSPoints(1) < item6.getPrice()) {
                    c.sendPacket(MTSCSPacket.sendCSFail(0));
                    RefreshCashShop(c);
                    return;
                }
                switch (item6.getPrice()) {
                    case 50: {
                        c.getPlayer().modifyCSPoints(2, item6.getPrice(), false);
                        break;
                    }
                    case 150: {
                        c.getPlayer().modifyCSPoints(2, item6.getPrice(), false);
                        break;
                    }
                    case 500: {
                        c.getPlayer().modifyCSPoints(2, item6.getPrice(), false);
                        break;
                    }
                }
                chr.dropMessage(1, "成功购买抵用:" + item6.getPrice());
                c.getPlayer().modifyCSPoints(1, -item6.getPrice(), false);
                RefreshCashShop(c);
                break;
            }
            default: {
                c.sendPacket(MTSCSPacket.sendCSFail(0));
                RefreshCashShop(c);
                break;
            }
        }
    }
    
    private static final MapleInventoryType getInventoryType(final int id) {
        switch (id) {
            case 50200075: {
                return MapleInventoryType.EQUIP;
            }
            case 50200074: {
                return MapleInventoryType.USE;
            }
            case 50200073: {
                return MapleInventoryType.ETC;
            }
            default: {
                return MapleInventoryType.UNDEFINED;
            }
        }
    }
    
    private static void RefreshCashShop(final MapleClient c) {
        c.sendPacket(MTSCSPacket.showCashInventory(c));
        c.sendPacket(MTSCSPacket.showNXMapleTokens(c.getPlayer()));
        c.sendPacket(MTSCSPacket.enableCSUse());
        c.getPlayer().getCashInventory().checkExpire(c);
    }
    
    public static void sendWebSite(final MapleClient c) {
        c.sendPacket(MTSCSPacket.sendWEB(c));
        RefreshCashShop(c);
    }
}
