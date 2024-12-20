package server.events;

import java.util.concurrent.ScheduledFuture;
import client.MapleCharacter;
import server.Timer.EventTimer;
import tools.MaplePacketCreator;

public class MapleJewel extends MapleEvent {

    private static final long serialVersionUID = 845748950824L;
    private final long time = 600000;
    private long timeStarted = 0;
    private ScheduledFuture<?> fitnessSchedule, msgSchedule;

    public MapleJewel(final int channel, final int[] mapid) {
        super(channel, mapid);
    }

    @Override
    public void finished(final MapleCharacter chr) {
        givePrize(chr);
    }

    @Override
    public void onMapLoad(MapleCharacter chr) {
        if (isTimerStarted()) {
            chr.getClient().sendPacket(MaplePacketCreator.getClock((int) (getTimeLeft() / 1000)));
        }
    }

    @Override
    public void startEvent() {
        unreset();
        super.reset();
        broadcast(MaplePacketCreator.getClock((int) (time / 1000)));
        this.timeStarted = System.currentTimeMillis();

        fitnessSchedule = EventTimer.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < mapid.length; i++) {
                    for (MapleCharacter chr : getMap(i).getCharactersThreadsafe()) {
                        warpBack(chr);
                    }
                }
                unreset();
            }
        }, this.time);

        broadcast(MaplePacketCreator.serverNotice(0, "活動已經開始，請通過中間的入口開始遊戲。"));
    }

    public boolean isTimerStarted() {
        return timeStarted > 0;
    }

    public long getTime() {
        return time;
    }

    public void resetSchedule() {
        this.timeStarted = 0;
        if (fitnessSchedule != null) {
            fitnessSchedule.cancel(false);
        }
        fitnessSchedule = null;
        if (msgSchedule != null) {
            msgSchedule.cancel(false);
        }
        msgSchedule = null;
    }

    @Override
    public void reset() {
        super.reset();
        resetSchedule();
        getMap(0).getPortal("join00").setPortalState(false);
    }

    @Override
    public void unreset() {
        super.unreset();
        resetSchedule();
        getMap(0).getPortal("join00").setPortalState(true);
    }

    public long getTimeLeft() {
        return time - (System.currentTimeMillis() - timeStarted);
    }
}
