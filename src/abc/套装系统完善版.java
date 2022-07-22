/*
增加伤害的装备
 */
package abc;

import constants.*;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class 套装系统完善版 {

    private static 套装系统完善版 instance = null;
    private static boolean CANLOG;
    private Properties itempb_cfg;
    private String 套装1;
    private String 套装2;
    private String 套装3;
    private String 套装4;
    private String 套装5;
    private String 套装6;

    private static Logger log = LoggerFactory.getLogger(套装系统完善版.class);

    public 套装系统完善版() {
        itempb_cfg = new Properties();
        try {
            InputStreamReader is = new FileReader("套装系统.ini");
            itempb_cfg.load(is);
            is.close();
            套装1 = itempb_cfg.getProperty("TZ1");
            套装2 = itempb_cfg.getProperty("TZ2");
            套装3 = itempb_cfg.getProperty("TZ3");
            套装4 = itempb_cfg.getProperty("TZ4");
            套装5 = itempb_cfg.getProperty("TZ5");
            套装6 = itempb_cfg.getProperty("TZ6");
        } catch (Exception e) {
            log.error("Could not configuration", e);
        }
    }

    public String get套装1() {
        return 套装1;
    }
    public String get套装2() {
        return 套装2;
    }
    public String get套装3() {
        return 套装3;
    }
    public String get套装4() {
        return 套装4;
    }
    public String get套装5() {
        return 套装5;
    }
    public String get套装6() {
        return 套装6;
    }
    public boolean isCANLOG() {
        return CANLOG;
    }

    public void setCANLOG(boolean CANLOG) {
        套装系统完善版.CANLOG = CANLOG;
    }

    public static 套装系统完善版 getInstance() {
        if (instance == null) {
            instance = new 套装系统完善版();
        }
        return instance;
    }
}
