package server;

import client.MapleCharacter;
import handling.world.World;
import java.util.ArrayList;
import java.util.List;
import tools.packet.MTSCSPacket;

public class MapleTVEffect {

    private static List<String> message = new ArrayList<>(5);
    private static MapleCharacter user;
    private static boolean active;
    private static int type;
    public static int delay;
    private static MapleCharacter partner;

    public MapleTVEffect(MapleCharacter user_, MapleCharacter partner_, List<String> msg, int type_,int time) {
        this.message = msg;
        this.user = user_;
        this.type = type_;
        this.partner = partner_;
        this.delay = time;
        broadcastTV(true);
    }

    public static void sendTV() {
        World.Broadcast.broadcastMessage(MTSCSPacket.enableTV());
        World.Broadcast.broadcastMessage(MTSCSPacket.sendTV(user, message, type <= 2 ? type : type - 3, partner, delay / 1000));
    }
    public void removeTV() {
        World.Broadcast.broadcastMessage(MTSCSPacket.removeTV());
    }

    public static boolean isActive() {
        return active;
    }

    private void setActive(boolean set) {
        active = set;
    }

    private void broadcastTV(boolean active_) {
        setActive(active_);
        if (active_) {
            World.Broadcast.broadcastMessage(MTSCSPacket.enableTV());
            World.Broadcast.broadcastMessage(MTSCSPacket.sendTV(user, message, type <= 2 ? type : type - 3, partner, delay / 1000));

            Timer.EventTimer.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    broadcastTV(false);
                    delay = 0;
                }
            }, delay);
        } else {
            World.Broadcast.broadcastMessage(MTSCSPacket.removeTV());
        }
    }
}
