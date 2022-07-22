package constants;

import gui.HuiMS;
import handling.world.MapleParty;
import server.maps.MapleMapObject;
import java.util.Iterator;
import server.ServerProperties;
import server.life.MapleMonster;
import server.maps.MapleMap;

public class MapConstants
{
    public static boolean isBlockFM(final int mapid) {
        final int header = mapid / 100000;
        if (isEventMap(mapid)) {
            return true;
        }
        if (header == 9800 && (mapid % 10 == 1 || mapid % 1000 == 100)) {
            return true;
        }
        if (mapid / 10000 == 92502) {
            return true;
        }
        if (header == 7090) {
            return true;
        }
        if (header == 1090) {
            return true;
        }
        switch (mapid) {
            case 702060000: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean isForceRespawn(final int mapid) {
        switch (mapid) {
            case 925100100: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean isCar(final int mapid) {
        switch (mapid) {
            case 680000000:
            case 980000000:
            case 980030000: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean isStartingEventMap(final int mapid) {
        switch (mapid) {
            case 109010000:
            case 109020001:
            case 109030001:
            case 109030101:
            case 109030201:
            case 109030301:
            case 109030401:
            case 109040000:
            case 109060001:
            case 109060002:
            case 109060003:
            case 109060004:
            case 109060005:
            case 109060006:
            case 109080000:
            case 109080001:
            case 109080002:
            case 109080003: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean isEventMap(final int mapid) {
        return (mapid >= 109010000 && mapid < 109050000) || (mapid > 109050001 && mapid < 109090000) || (mapid >= 809040000 && mapid <= 809040100);
    }
    
    public static boolean inBossMap(final int mapid) {
        if (mapid / 10000 == 92502) {
            return true;
        }
        if (mapid == 220040000) {
            return true;
        }
        switch (mapid) {
            case 105100300:
            case 220080001:
            case 230040420:
            case 240060000:
            case 240060100:
            case 240060200:
            case 270050100:
            case 280030000:
            case 551030200:
            case 702060000:
            case 740000000:
            case 741020102:
            case 749050301:
            case 802000211:
            case 922010900:
            case 925020200:
            case 930000600: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static int isMonsterSpawn(final MapleMap map) {
        if (isBossMap(map.getId()) || isEventMap(map.getId())) {
            return 1;
        }
        for (final MapleMapObject obj : map.getAllMonstersThreadsafe()) {
            final MapleMonster mob = (MapleMonster)obj;
            if (mob.getStats().isBoss()) {
                return 1;
            }
        }
        boolean 判定 = false;
        String 所有倍怪地图[] = HuiMS.倍怪地图.getText().split(",");
        if (MapleParty.怪物倍怪 = true) {
            for (int i = 0; i < 所有倍怪地图.length; ++i) {
                if (map.getId() == Integer.parseInt(所有倍怪地图[i])) {
                    判定 = true;
                }
            }
            if (判定) {
                return MapleParty.怪物倍率;
            }
        }
        return 1;
    }
    
    public static boolean isBossMap(final int mapid) {
        if (mapid / 10000 == 92502) {
            return true;
        }
        switch (mapid) {
            case 105100300:
            case 220080001:
            case 230040420:
            case 240060000:
            case 240060100:
            case 240060200:
            case 270010500:
            case 270020500:
            case 270030500:
            case 270050100:
            case 280030000:
            case 551030200:
            case 740000000:
            case 741020101:
            case 741020102:
            case 749040001:
            case 749050301:
            case 802000211:
            case 802000611:
            case 922010900:
            case 925020200:
            case 930000600: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
}
