package handling.channel.handler;

import client.MapleClient;
import tools.MaplePacketCreator;
import tools.data.LittleEndianAccessor;

/**
 *
 * @author 小灰灰
 */
public class ViciousHammerHandler {
    public static void VICIOUS_HAMMER(LittleEndianAccessor slea, MapleClient c) {
        int type = slea.readInt();
        if(type == 0x33){
            c.getPlayer().getClient().sendPacket(MaplePacketCreator.sendHammerMessage(58));
        }
        if(type == 0x34){
            c.getPlayer().getClient().sendPacket(MaplePacketCreator.sendHammerMessage(59));
        }
    }
}
