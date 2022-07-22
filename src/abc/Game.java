package abc;

import tools.FileoutputUtil;
import server.ServerProperties;
import handling.world.MapleParty;
import java.net.URLConnection;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;
import java.net.URL;

public class Game{
    public static String 服务端名称;
    public static String 安全系统;
    public static String 官方网站;
    public static int 版本;
    public static String 窗口标题;
    public static final short 检测客户端版本1 = 79;
    public static final String 检测客户端版本2 = "1";
    public static int 商城端口;
    public static int 人数限制;
    public static String 官方群链接;
    public static String 获取网络时间;
    public static String 当前时间;
    public static int 清理每日在线时间;
    public static int 准备清理每日在线时间;
    public static String 服务端配置需要开关;
    public static int 服务端配置需要;
    public static String 开服名字;
    public static String 调试;
    public static String 调试2;
    public static String 调试3;
    public static String OX猜题;
    public static String 宠物调试;
    public static String 调试输出;
    public static String 外挂调试;
    public static String 地图脚本报错;
    public static String 原始技能;
    public static String 技改1;
    public static String 技改2;
    public static String 技改3;
    public static String 技改4;
    public static String 等级;
    public static String 人气设置;
    public static String 无敌;
    public static String 刷技能点;
    public static String 刷能力点;
    public static String 刷;
    public static String 吸怪;
    public static String 刷新;
    public static String 清物;
    public static String 清怪;
    public static String 清怪2;
    public static String 我的位置;
    public static String 召唤怪物;
    public static String 传送;
    public static int 向高地活动时间;
    public static int 上楼活动时间;
    public static String 禁止未认证脚本使用;
    public static String NPC错误文本提示;
    public static String NPC前缀;
    public static String NPC前缀2;
    private static final int[] 黑名单;
    public static String[] 事件;
    public static String 丢金币开关;
    
    public static boolean 屏蔽文字(final String a) {
        int n = -1;
        switch (a.hashCode()) {
            case 25830: {
                if (a.equals((Object)"擦")) {
                    n = 0;
                    break;
                }
                break;
            }
        }
        switch (n) {
            case 0: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean 两小时限时道具(final int a) {
        switch (a) {
            case 5211060: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean 三小时限时道具(int a) {
        switch (a) {
            //双倍经验值卡（三小时）
            case 5211047:
            //双倍爆率值卡（三小时）
            case 5360014:
            //三倍经验值卡（三小时）
            case 5211060:
                return true;
            default:
                return false;
        }
    }
    
    public static boolean 一天时限时道具(int a) {
        switch (a) {
            //黑板1日
            case 5370001:
            //雇佣商店
            case 5030001:
            case 5030009:
            case 5030011:
            case 5030012:
            //双倍经验值卡（一天权）
            case 5210000:
            //双倍经验值卡（白天一天权）
            case 5210002:
            //双倍经验值卡（晚上一天权）
            case 5210004:
            //双倍爆率卡（一天权）
            case 5360000:
            //双倍爆率卡（一天权）
            case 5360015:
                return true;
            default:
                return false;
        }
    }
    
    public static boolean 七天时限时道具(int a) {
        switch (a) {
            //黑板7日
            case 5370000:
            //装备许可证
            case 5590000:
            case 5590001:
            //雇佣商店
            case 5030000:
            case 5030008:
            case 5030010:
            case 5030018:
            //双倍经验值卡（七天权）
            case 5210001:
            //双倍经验值卡（白天七天权）
            case 5210003:
            //双倍经验值卡（晚上七天权）
            case 5210005:
            //双倍爆率卡（一周权）
            case 5360016:
                return true;
            default:
                return false;
        }
    }
    
    public static boolean 三十天时限时道具(int a) {
        switch (a) {
            //雇佣
            case 5030016:
                return true;
            default:
                return false;
        }
    }
    
    public static boolean 主城(final int a) {
        switch (a) {
            case 1000000:
            case 1000001:
            case 1000002:
            case 1000003:
            case 2000000:
            case 100000000:
            case 100000001:
            case 100000100:
            case 100000101:
            case 100000102:
            case 100000103:
            case 100000104:
            case 100000105:
            case 100000200:
            case 100000202:
            case 100000203:
            case 100000204:
            case 101000000:
            case 101000001:
            case 101000002:
            case 101000003:
            case 101000004:
            case 101000200:
            case 101000300:
            case 101000301:
            case 102000000:
            case 102000001:
            case 102000002:
            case 102000003:
            case 102000004:
            case 103000000:
            case 103000001:
            case 103000002:
            case 103000003:
            case 103000004:
            case 103000005:
            case 103000006:
            case 103000008:
            case 103000100:
            case 104000000:
            case 104000001:
            case 104000002:
            case 104000003:
            case 104000004:
            case 105040300:
            case 105040400:
            case 105040401:
            case 105040402:
            case 106020000:
            case 140000000:
            case 140000001:
            case 140000010:
            case 140000011:
            case 140000012:
            case 140010110:
            case 200000000:
            case 200000001:
            case 200000002:
            case 200000100:
            case 200000110:
            case 200000111:
            case 200000112:
            case 200000120:
            case 200000121:
            case 200000122:
            case 200000130:
            case 200000131:
            case 200000132:
            case 200000140:
            case 200000141:
            case 200000150:
            case 200000151:
            case 200000152:
            case 200000160:
            case 200000161:
            case 200000200:
            case 200000201:
            case 200000202:
            case 200000203:
            case 200000300:
            case 200000301:
            case 209000000:
            case 209080000:
            case 209080100:
            case 211000000:
            case 211000001:
            case 211000100:
            case 211000101:
            case 211000102:
            case 220000000:
            case 220000001:
            case 220000002:
            case 220000003:
            case 220000004:
            case 220000005:
            case 220000006:
            case 220000100:
            case 220000110:
            case 220000111:
            case 220000300:
            case 220000301:
            case 220000302:
            case 220000303:
            case 220000304:
            case 220000305:
            case 220000306:
            case 220000307:
            case 220000400:
            case 220000500:
            case 221000000:
            case 221000001:
            case 221000100:
            case 221000200:
            case 221000300:
            case 222000000:
            case 222020000:
            case 230000000:
            case 230000001:
            case 230000002:
            case 230000003:
            case 240000000:
            case 240000001:
            case 240000002:
            case 240000003:
            case 240000004:
            case 240000005:
            case 240000006:
            case 240000100:
            case 240000110:
            case 240000111:
            case 250000000:
            case 250000001:
            case 250000002:
            case 250000003:
            case 250000100:
            case 251000000:
            case 260000000:
            case 260000100:
            case 260000110:
            case 260000200:
            case 260000201:
            case 260000202:
            case 260000203:
            case 260000204:
            case 260000205:
            case 260000206:
            case 260000207:
            case 260000300:
            case 260000301:
            case 260000302:
            case 260000303:
            case 261000000:
            case 261000001:
            case 261000002:
            case 261000010:
            case 261000011:
            case 261000020:
            case 261000021:
            case 270000000:
            case 270010000:
            case 300000000:
            case 300000001:
            case 300000002:
            case 300000010:
            case 300000011:
            case 300000012:
            case 500000000:
            case 540000000:
            case 541000000:
            case 550000000:
            case 551000000:
            case 600000000:
            case 600000001:
            case 700000000:
            case 700000100:
            case 700000101:
            case 700000200:
            case 701000000:
            case 701000100:
            case 701000200:
            case 701000201:
            case 701000202:
            case 701000203:
            case 701000210:
            case 702000000:
            case 702050000:
            case 702090102:
            case 741000200:
            case 741000201:
            case 741000202:
            case 741000203:
            case 741000204:
            case 741000205:
            case 741000206:
            case 741000207:
            case 741000208:
            case 800000000:
            case 801000000:
            case 801000001:
            case 801000002:
            case 801000100:
            case 801000110:
            case 801000200:
            case 801000210:
            case 801000300:
            case 810000000:
            case 910000000:
            case 910110000:
            case 930000700: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean 主城2(final int a) {
        switch (a) {
            case 100000000:
            case 101000000:
            case 102000000:
            case 103000000:
            case 104000000:
            case 105040300:
            case 120000000:
            case 130000200:
            case 200000000:
            case 211000000:
            case 220000000:
            case 221000000:
            case 222000000:
            case 230000000:
            case 240000000:
            case 250000000:
            case 251000000:
            case 260000000:
            case 261000000:
            case 551000000:
            case 801000000: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static void 说明() {
        System.out.println("");
    }
    
    private static String 获取网络时间(final String webUrl) {
        try {
            final URL url = new URL(webUrl);
            final URLConnection uc = url.openConnection();
            uc.connect();
            final long ld = uc.getDate();
            final Date date = new Date(ld);
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            return sdf.format(date);
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e2) {
            e2.printStackTrace();
        }
        return null;
    }
    
    public static boolean 箱子(final int xiangzi) {
        return false;
    }
    
    static {
        Game.服务端名称 = "冒险岛";
        Game.安全系统 = "<安全防护>\r\n\r\nVer.1.0\r\n\r\n";
        Game.官方网站 = "www.baidu.com";
        Game.版本 = 27;
        Game.窗口标题 = "冒险岛" + Game.版本 + " 官网:" + Game.官方网站 + " ";
        Game.商城端口 = 8600;
        Game.人数限制 = 999;
        Game.官方群链接 = "http://baidu.com";
        Game.获取网络时间 = "http://baidu.com";
        Game.当前时间 = "" + 获取网络时间(Game.获取网络时间) + "";
        Game.清理每日在线时间 = 1;
        Game.准备清理每日在线时间 = 0;
        Game.服务端配置需要开关 = "关";
        Game.服务端配置需要 = 16777216;
        Game.开服名字 = MapleParty.开服名字;
        Game.调试 = "关";
        Game.调试2 = "关";
        Game.调试3 = "开";
        Game.OX猜题 = "关";
        Game.宠物调试 = "关";
        Game.调试输出 = "关";
        Game.外挂调试 = "关";
        Game.地图脚本报错 = "关";
        Game.原始技能 = "开";
        Game.技改1 = "关";
        Game.技改2 = "关";
        Game.技改3 = "关";
        Game.技改4 = "关";
        Game.等级 = "开";
        Game.人气设置 = "开";
        Game.无敌 = "开";
        Game.刷技能点 = "开";
        Game.刷能力点 = "开";
        Game.刷 = "开";
        Game.吸怪 = "开";
        Game.刷新 = "开";
        Game.清物 = "开";
        Game.清怪 = "开";
        Game.清怪2 = "开";
        Game.我的位置 = "开";
        Game.召唤怪物 = "开";
        Game.传送 = "开";
        Game.向高地活动时间 = 1200000;
        Game.上楼活动时间 = 1200000;
        Game.禁止未认证脚本使用 = "关";
        Game.NPC错误文本提示 = "我是不是能为你做些什么呢？";
        Game.NPC前缀 = "cm";
        Game.NPC前缀2 = "MMP";
        黑名单 = new int[] { 159502199 };
        Game.事件 = new String[] { "Gailou", "Laba", "MonsterPark", "GoldTempleBoss", "szsl", "SkyPark", "WitchTower_Hard", "WitchTower_Med", "WitchTower_EASY", "CWKPQ", "Relic", "HontalePQ", "HorntailBattle", "cpq2", "elevator", "Christmas", "FireDemon", "Amoria", "cpq", "AutomatedEvent", "Flight", "English", "English0", "English1", "English2", "WuGongPQ", "ElementThanatos", "4jberserk", "4jrush", "Trains", "Geenie", "AirPlane", "Boats", "OrbisPQ", "HenesysPQ", "Romeo", "Juliet", "Pirate", "Ellin", "DollHouse", "BossBalrog_NORMAL", "Nibergen", "PinkBeanBattle", "ZakumBattle", "ZakumPQ", "LudiPQ", "KerningPQ", "ProtectTylus", "CoreBlaze", "GuildQuest", "Aufhaven", "Subway", "KyrinTrainingGroundC", "KyrinTrainingGroundV", "ProtectPig", "ScarTarBattle", "Relic", "QiajiPQ", "BossBalrog", "s4resurrection", "s4resurrection2", "s4nest", "s4aWorld", "DLPracticeField", "BossQuestEASY", "shaoling" };
        Game.丢金币开关 = "关";
    }
}
