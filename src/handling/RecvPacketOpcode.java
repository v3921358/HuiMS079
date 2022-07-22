package handling;

import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import tools.StringUtil;
import java.io.FileInputStream;
import java.util.Properties;

public enum RecvPacketOpcode implements WritableIntValueHolder{
    LOGIN_PASSWORD(1), 
    SERVERLIST_REQUEST(2), 
    CHARLIST_REQUEST(9), 
    CHAR_SELECT(10), 
    PLAYER_LOGGEDIN(11), 
    CHECK_CHAR_NAME(12), 
    CREATE_CHAR(17), 
    CLIENT_FEEDBACK, 
    DELETE_CHAR(18), 
    PONG(19), 
    STRANGE_DATA(21), 
    CLIENT_ERROR(20), 
    HELLO_LOGIN, 
    SERVERSTATUS_REQUEST(5), 
    HELLO_CHANNEL, 
    SET_GENDER(4), 
    CLIENT_LOGOUT, 
    CHANGE_MAP(33), 
    CHANGE_CHANNEL(34), 
    QUEST_MAPLEPOINTS(106), 
    ENTER_CASH_SHOP(35), 
    MOVE_PLAYER(36), 
    CANCEL_CHAIR(37), 
    USE_CHAIR(38), 
    SHOW_EXP_CHAIR(39), 
    CLOSE_RANGE_ATTACK(40), 
    RANGED_ATTACK(41), 
    MAGIC_ATTACK(42), 
    PASSIVE_ENERGY(43), 
    TAKE_DAMAGE(44), 
    GENERAL_CHAT(45), 
    CLOSE_CHALKBOARD(46), 
    FACE_EXPRESSION(47), 
    USE_ITEMEFFECT(48), 
    WHEEL_OF_FORTUNE(49), 
    MONSTER_BOOK_COVER(53), 
    NPC_TALK(54), 
    NPC_TALK_MORE(56), 
    NPC_SHOP(58), 
    STORAGE(59), 
    USE_HIRED_MERCHANT(60), 
    MERCH_ITEM_STORE(61), 
    DUEY_ACTION(62), 
    ITEM_SORT(66), 
    ITEM_GATHER(67), 
    ITEM_MOVE(68), 
    USE_ITEM(69), 
    CANCEL_ITEM_EFFECT(70), 
    USE_SUMMON_BAG(72), 
    PET_FOOD(73), 
    USE_MOUNT_FOOD(74), 
    USE_SCRIPTED_NPC_ITEM(75), 
    USE_CASH_ITEM(76), 
    ITEM_UNLOCK, 
    SOLOMON, 
    GACH_EXP, 
    USE_CATCH_ITEM(78), 
    USE_SKILL_BOOK(79), 
    USE_RETURN_SCROLL(82), 
    USE_UPGRADE_SCROLL(83), 
    DISTRIBUTE_AP(84), 
    AUTO_ASSIGN_AP(85), 
    HEAL_OVER_TIME(86), 
    DISTRIBUTE_SP(87), 
    SPECIAL_MOVE(88), 
    CANCEL_BUFF(89), 
    SKILL_EFFECT(90), 
    MESO_DROP(91), 
    GIVE_FAME(92), 
    CHAR_INFO_REQUEST(94), 
    SPAWN_PET(95), 
    CANCEL_DEBUFF(96), 
    CHANGE_MAP_SPECIAL(97), 
    USE_INNER_PORTAL(98), 
    TROCK_ADD_MAP(99), 
    LIE_DETECTOR(100), 
    ChatRoom_SYSTEM(260), 
    LIE_DETECTOR_SKILL(101), 
    LIE_DETECTOR_RESPONSE(102), 
    LIE_DETECTOR_REFRESH(103), 
    QUEST_ACTION(104), 
    SKILL_MACRO(109), 
    ITEM_BAOWU(112), 
    ITEM_SUNZI(142), 
    USE_GACHA_EXP(143),
    REWARD_ITEM(114), 
    ITEM_MAKER(113), 
    USE_TREASUER_CHEST(115), 
    PARTYCHAT(116), 
    WHISPER(117), 
    MESSENGER(118), 
    PLAYER_INTERACTION(119), 
    PARTY_OPERATION(120), 
    DENY_PARTY_REQUEST(121), 
    GUILD_OPERATION(122), 
    DENY_GUILD_REQUEST(123), 
    BUDDYLIST_MODIFY(126), 
    NOTE_ACTION(127), 
    USE_DOOR(129), 
    CHANGE_KEYMAP(131), 
    UPDATE_CHAR_INFO, 
    ENTER_MTS(141), 
    ALLIANCE_OPERATION(138), 
    DENY_ALLIANCE_REQUEST(139), 
    REQUEST_FAMILY(149), 
    OPEN_FAMILY(150), 
    FAMILY_OPERATION(151), 
    DELETE_JUNIOR(152), 
    DELETE_SENIOR(153), 
    ACCEPT_FAMILY(154), 
    USE_FAMILY(155), 
    FAMILY_PRECEPT(156), 
    FAMILY_SUMMON(157), 
    CYGNUS_SUMMON(158), 
    ARAN_COMBO(159), 
    BBS_OPERATION(140), 
    TRANSFORM_PLAYER(162), 
    MOVE_PET(165), 
    PET_CHAT(166), 
    PET_COMMAND(167), 
    PET_LOOT(168), 
    PET_AUTO_POT(169), 
    MOVE_SUMMON(173), 
    SUMMON_ATTACK(174), 
    DAMAGE_SUMMON(176), 
    MOVE_LIFE(183), 
    AUTO_AGGRO(184), 
    FRIENDLY_DAMAGE(189), 
    MONSTER_BOMB(188), 
    HYPNOTIZE_DMG(189), 
    NPC_ACTION(192), 
    ITEM_PICKUP(198), 
    DAMAGE_REACTOR(201), 
    SNOWBALL(207), 
    LEFT_KNOCK_BACK(208), 
    COCONUT(209), 
    MONSTER_CARNIVAL(215), 
    PARTY_SS(223), 
    SHIP_OBJECT(217), 
    CS_UPDATE(232), 
    CASHSHOP_OPERATION(233), 
    BUY_CS_ITEM(233),
    COUPON_CODE(234), 
    MAPLETV, 
    REPAIR, 
    REPAIR_ALL, 
    TOUCHING_MTS(251), 
    USE_MAGNIFY_GLASS, 
    USE_POTENTIAL_SCROLL, 
    USE_EQUIP_SCROLL, 
    GAME_POLL, 
    OWL(63), 
    OWL_WARP(64), 
    USE_OWL_MINERVA(255), 
    RPS_GAME(132), 
    UPDATE_QUEST(247), 
    USE_ITEM_QUEST(250), 
    FOLLOW_REQUEST, 
    FOLLOW_REPLY, 
    MOB_NODE(190), 
    DISPLAY_NODE(191), 
    TOUCH_REACTOR(202), 
    RING_ACTION(133), 
    MTS_TAB(252), 
    SPECIAL_ATTACK(108), 
    PET_IGNORE(170), 
    BEANS_OPERATION(226), 
    LICENSE_REQUEST(3), 
    VICIOUS_HAMMER(269),
    BEANS_UPDATE(227);
    
    private short code;
    private boolean CheckState;
    
    @Override
    public void setValue(final short code) {
        this.code = code;
    }
    
    @Override
    public final short getValue() {
        return this.code;
    }
    
    private RecvPacketOpcode() {
        this.code = -2;
        this.CheckState = true;
    }
    
    private RecvPacketOpcode(final int code) {
        this.code = -2;
        this.code = (short)code;
        this.CheckState = false;
    }
    
    private RecvPacketOpcode(final short code, final boolean CheckState) {
        this.code = -2;
        this.code = code;
        this.CheckState = CheckState;
    }
    
    private RecvPacketOpcode(final boolean CheckState) {
        this.code = -2;
        this.CheckState = CheckState;
    }
    
    public final boolean NeedsChecking() {
        return this.CheckState;
    }
    
    public static String nameOf(final short value) {
        for (final RecvPacketOpcode header : values()) {
            if (header.getValue() == value) {
                return header.name();
            }
        }
        return "UNKNOWN";
    }
    
    public static boolean isSpamHeader(final RecvPacketOpcode header) {
        final String name = header.name();
        switch (name) {
            case "PONG":
            case "NPC_ACTION": {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static final void reloadValues() {
        final String fileName = "recv.ini";
        final Properties props = new Properties();
        try (final FileInputStream fileInputStream = new FileInputStream(fileName);
             final BufferedReader br = new BufferedReader((Reader)new InputStreamReader((InputStream)fileInputStream, StringUtil.codeString(fileName)))) {
            props.load((Reader)br);
        }
        catch (IOException ex) {
            final InputStream in = RecvPacketOpcode.class.getClassLoader().getResourceAsStream("properties/" + fileName);
            if (in == null) {
                System.err.println("错误: 未加载 " + fileName + " 档案");
                return;
            }
            try {
                props.load(in);
                in.close();
            }
            catch (IOException e) {
                throw new RuntimeException("加载 " + fileName + " 档案出错", (Throwable)e);
            }
        }
        ExternalCodeTableGetter.populateValues(props, values());
    }
    
    static {
        reloadValues();
    }
}
