package tools;

import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import tools.packet.PacketHelper;

public class KoreanDateUtil {

    private final static int QUEST_UNIXAGE = 27111908;
    private final static int ITEM_YEAR2000 = -1085019342;
    private final static long REAL_YEAR2000 = 946681229830l;

    public static final long getTempBanTimestamp(final long realTimestamp) {
        return ((realTimestamp * 10000) + PacketHelper.FT_UT_OFFSET);
    }
    
    public static final int getItemTimestamp(final long realTimestamp) {
        final int time = (int) ((realTimestamp - REAL_YEAR2000) / 1000 / 60);
        return (int) (time * 35.762787) + ITEM_YEAR2000;
    }
    
    public static final int getQuestTimestamp(final long realTimestamp) {
        final int time = (int) (realTimestamp / 1000 / 60);
        return (int) (time * 0.1396987) + QUEST_UNIXAGE;
    }

    public static boolean isDST() {
        return TimeZone.getDefault().inDaylightTime(new Date());
    }

    public static long getFileTimestamp(long timeStampinMillis) {
        return getFileTimestamp(timeStampinMillis, false);
    }

    public static long getFileTimestamp(long timeStampinMillis, boolean roundToMinutes) {
        if (isDST()) {
            timeStampinMillis -= 3600000L;
        }
        timeStampinMillis += 12 * 60 * 60 * 1000;
        long time;
        if (roundToMinutes) {
            time = (timeStampinMillis / 1000 / 60) * 600000000;
        } else {
            time = timeStampinMillis * 10000;
        }
        return time + PacketHelper.FT_UT_OFFSET;
    }
}
