package handling;

import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import tools.StringUtil;
import java.io.FileInputStream;
import java.util.Properties;
import tools.FileoutputUtil;
import tools.HexTool;

public enum SendPacketOpcode implements WritableIntValueHolder
{
    SPAWN_KITE_ERROR(265), 
    SPAWN_KITE(266), 
    DESTROY_KITE(267), 
    LOGIN_STATUS(0), 
    SERVERLIST(9), 
    CHARLIST(10), 
    SERVER_IP(11), 
    CHAR_NAME_RESPONSE(12), 
    LICENSE_RESULT(2), 
    ADD_NEW_CHAR_ENTRY(17), 
    DELETE_CHAR_RESPONSE(255), 
    CHANGE_CHANNEL(19), 
    PING(20), 
    CS_USE(21), 
    CHANNEL_SELECTED(24), 
    RELOG_RESPONSE(22), 
    SECONDPW_ERROR, 
    CHOOSE_GENDER(4), 
    GENDER_SET(5), 
    SERVERSTATUS(6), 
    OPEN_WEB(118), 
    INVENTORY_OPERATION(32),
    MODIFY_INVENTORY_ITEM(32), 
    UPDATE_INVENTORY_SLOT(33), 
    UPDATE_STATS(34), 
    GIVE_BUFF(35), 
    CANCEL_BUFF(36), 
    TEMP_STATS(37), 
    TEMP_STATS_RESET(38), 
    UPDATE_SKILLS(39), 
    SKILL_USE_RESULT(40), 
    FAME_RESPONSE(41), 
    SHOW_STATUS_INFO(42), 
    SHOW_NOTES(43), 
    MAP_TRANSFER_RESULT(44), 
    LIE_DETECTOR(45), 
    CLAIM_RESULT(47), 
    CLAIM_STATUS_CHANGED(48), 
    SET_TAMING_MOB_INFO(49), 
    SHOW_QUEST_COMPLETION(51), 
    ENTRUSTED_SHOP_CHECK_RESULT(52), 
    USE_SKILL_BOOK(53), 
    GATHER_ITEM_RESULT(54), 
    SORT_ITEM_RESULT(55), 
    CHAR_INFO(58), 
    PARTY_OPERATION(59), 
    BUDDYLIST(60), 
    GUILD_OPERATION(62), 
    ALLIANCE_OPERATION(63), 
    SPAWN_PORTAL(64), 
    SERVERMESSAGE(65), 
    PIGMI_REWARD, 
    SHOP_SCANNER_RESULT(67), 
    SHOP_LINK_RESULT(68), 
    MARRIAGE_REQUEST(73), 
    MARRIAGE_RESULT(74), 
    MARRAGE_EFFECT, 
    SET_WEEK_EVENT_MESSAGE(78), 
    IMITATED_NPC_DATA(90), 
    MONSTERBOOK_ADD(91), 
    MONSTERBOOK_CHANGE_COVER(92), 
    SESSION_VALUE(98), 
    FAMILY_CHART_RESULT(100), 
    FAMILY_INFO_RESULT(101), 
    FAMILY_RESULT(102), 
    FAMILY_JOIN_REQUEST(103), 
    FAMILY_JOIN_REQUEST_RESULT(104), 
    FAMILY_JOIN_ACCEPTED(105), 
    FAMILY_PRIVILEGE_LIST(106), 
    FAMILY_FAMOUS_POINT_INC_RESULT(107), 
    FAMILY_NOTIFY_LOGIN_OR_LOGOUT(108), 
    FAMILY_SET_PRIVILEGE(109), 
    FAMILY_SUMMON_REQUEST(110), 
    LEVEL_UPDATE(111), 
    MARRIAGE_UPDATE(112), 
    JOB_UPDATE(113), 
    SET_BUY_EQUIP_EXT(114), 
    SCRIPT_PROGRESS_MESSAGE(115), 
    BBS_OPERATION(119), 
    FISHING_BOARD_UPDATE(117), 
    AVATAR_MEGA(86), 
    SKILL_MACRO(102), 
    SET_FIELD(129), 
    SET_ITC(130), 
    SET_CASH_SHOP(131), 
    SERVER_BLOCKED, 
    SHOW_EQUIP_EFFECT, 
    MULTICHAT(138), 
    WHISPER(139), 
    SPOUSE_CHAT(140), 
    BOSS_ENV(141), 
    MOVE_ENV(145), 
    CASH_SONG(146), 
    GM_EFFECT(147), 
    OX_QUIZ(148), 
    GMEVENT_INSTRUCTIONS(149), 
    CLOCK(150), 
    BOAT_EFFECT(151), 
    BOAT_PACKET(152), 
    STOP_CLOCK(156), 
    PYRAMID_UPDATE(160), 
    PYRAMID_RESULT(161), 
    MOVE_PLATFORM(159), 
    SPAWN_PLAYER(162), 
    REMOVE_PLAYER_FROM_MAP(163), 
    CHATTEXT(164), 
    CHALKBOARD(166), 
    UPDATE_CHAR_BOX(167), 
    SHOW_SCROLL_EFFECT(169), 
    FISHING_CAUGHT(171), 
    SPAWN_PET(173), 
    MOVE_PET(175), 
    PET_CHAT(176), 
    PET_NAMECHANGE(177), 
    PET_EXCEPTION_LIST(178), 
    PET_COMMAND(179), 
    SPAWN_SUMMON(180), 
    REMOVE_SUMMON(181), 
    SUMMON_ATTACK(183), 
    MOVE_SUMMON(182), 
    DAMAGE_SUMMON(185), 
    MOVE_PLAYER(187), 
    CLOSE_RANGE_ATTACK(188), 
    RANGED_ATTACK(189), 
    MAGIC_ATTACK(190), 
    ENERGY_ATTACK(191), 
    SKILL_EFFECT(192), 
    CANCEL_SKILL_EFFECT(193), 
    DAMAGE_PLAYER(194), 
    FACIAL_EXPRESSION(195), 
    SHOW_ITEM_EFFECT(196), 
    SHOW_CHAIR(198), 
    UPDATE_CHAR_LOOK(199), 
    SHOW_FOREIGN_EFFECT(200), 
    GIVE_FOREIGN_BUFF(201), 
    CANCEL_FOREIGN_BUFF(202), 
    UPDATE_PARTYMEMBER_HP(203), 
    CANCEL_CHAIR(207), 
    SHOW_ITEM_GAIN_INCHAT(13), 
    DOJO_WARP_UP(209), 
    CURRENT_MAP_WARP(327), 
    MESOBAG_SUCCESS(212), 
    MESOBAG_FAILURE(213), 
    UPDATE_QUEST_INFO(214), 
    PET_FLAG_CHANGE(216), 
    PLAYER_HINT(217), 
    REPAIR_WINDOW(154), 
    CYGNUS_INTRO_LOCK(228), 
    CYGNUS_INTRO_DISABLE_UI(227), 
    CS_UPDATE(353), 
    CS_OPERATION(354), 
    SPAWN_NPC(260), 
    REMOVE_NPC(261), 
    SPAWN_NPC_REQUEST_CONTROLLER(262), 
    SPAWN_MONSTER(238), 
    SPAWN_MONSTER_CONTROL(240), 
    MOVE_MONSTER_RESPONSE(242), 
    KILL_MONSTER(239), 
    DROP_ITEM_FROM_MAPOBJECT(272), 
    MOVE_MONSTER(241), 
    OPEN_NPC_SHOP(326), 
    CONFIRM_SHOP_TRANSACTION(327), 
    OPEN_STORAGE(330), 
    REMOVE_ITEM_FROM_MAP(273), 
    PLAYER_INTERACTION(335), 
    NPC_TALK(325), 
    KEYMAP(367), 
    SHOW_MONSTER_HP(252), 
    APPLY_MONSTER_STATUS(244), 
    CANCEL_MONSTER_STATUS(245), 
    SPAWN_DOOR(279), 
    REMOVE_DOOR(280), 
    SPAWN_MIST(277), 
    REMOVE_MIST(278), 
    DAMAGE_MONSTER(248), 
    REACTOR_SPAWN(286), 
    REACTOR_HIT(284), 
    REACTOR_DESTROY(287), 
    EARN_TITLE_MSG, 
    SHOW_MAGNET(253), 
    MERCH_ITEM_MSG(331), 
    MERCH_ITEM_STORE(332), 
    MESSENGER(334), 
    NPC_ACTION, 
    COOLDOWN(236), 
    SUMMON_HINT(229), 
    SUMMON_HINT_MSG(230), 
    SUMMON_SKILL(186), 
    ARIANT_PQ_START, 
    CATCH_MONSTER(259), 
    ARIANT_SCOREBOARD(154), 
    ZAKUM_SHRINE(324), 
    DUEY(351), 
    MONSTER_CARNIVAL_START(297), 
    MONSTER_CARNIVAL_OBTAINED_CP(298), 
    MONSTER_CARNIVAL_PARTY_CP(299), 
    MONSTER_CARNIVAL_SUMMON(300), 
    MONSTER_CARNIVAL_DIED(302), 
    SPAWN_HIRED_MERCHANT(269), 
    UPDATE_HIRED_MERCHANT(271), 
    DESTROY_HIRED_MERCHANT(270), 
    FAIRY_PEND_MSG(96), 
    VICIOUS_HAMMER(387), 
    ROLL_SNOWBALL(288), 
    HIT_SNOWBALL(289), 
    SNOWBALL_MESSAGE(290), 
    LEFT_KNOCK_BACK(291), 
    HIT_COCONUT(292), 
    COCONUT_SCORE(293), 
    HORNTAIL_SHRINE, 
    DRAGON_MOVE, 
    DRAGON_REMOVE, 
    DRAGON_SPAWN, 
    ARAN_COMBO(231), 
    GET_MTS_TOKENS, 
    MTS_OPERATION(380), 
    SHOW_POTENTIAL_EFFECT, 
    SHOW_POTENTIAL_RESET, 
    CHAOS_ZAKUM_SHRINE, 
    CHAOS_HORNTAIL_SHRINE, 
    GAME_POLL_QUESTION, 
    GAME_POLL_REPLY, 
    XMAS_SURPRISE, 
    FOLLOW_REQUEST, 
    FOLLOW_EFFECT, 
    FOLLOW_MOVE, 
    FOLLOW_MSG, 
    FOLLOW_MESSAGE, 
    TALK_MONSTER, 
    REMOVE_TALK_MONSTER, 
    MONSTER_PROPERTIES, 
    GHOST_POINT, 
    GHOST_STATUS, 
    ENGAGE_RESULT(70), 
    ENGLISH_QUIZ(304), 
    RPS_GAME, 
    UPDATE_BEANS(350), 
    TIP_BEANS(347), 
    OPEN_BEANS(348), 
    SHOOT_BEANS(349), 
    SHOW_SPECIAL_ATTACK, 
    PET_AUTO_HP(368), 
    PET_AUTO_MP(369), 
    TOP_MSG(115), 
    CHAR_CASH(125), 
    BOOK_STATS(2457),
    START_TV(363),
    REMOVE_TV(364),
    ENABLE_TV(365),
    CS_WEB;
    
    private short code;
    
    private SendPacketOpcode() {
        this.code = -2;
        this.code = -2;
    }
    
    private SendPacketOpcode(final int code) {
        this.code = -2;
        this.code = (short)code;
    }
    
    public static String nameOf(final int value) {
        for (final SendPacketOpcode opcode : values()) {
            if (opcode.getValue() == value) {
                return opcode.name();
            }
        }
        final StringBuilder sb = new StringBuilder("发现未知客户端数据包 - (包头:0x" + HexTool.getOpcodeToString(value) + "");
        FileoutputUtil.log("logs\\客户端数据包_未知.txt", sb.toString());
        return "UNKNOWN";
    }
    
    @Override
    public void setValue(final short code) {
        this.code = code;
    }
    
    @Override
    public short getValue() {
        return this.code;
    }
    
    public static boolean isSpamHeader(final SendPacketOpcode opcode) {
        final String name = opcode.name();
        switch (name) {
            case "WARP_TO_MAP":
            case "PING":
            case "NPC_ACTION": {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static final void reloadValues() {
        final String fileName = "send.ini";
        final Properties props = new Properties();
        try (final FileInputStream fileInputStream = new FileInputStream(fileName);
             final BufferedReader br = new BufferedReader((Reader)new InputStreamReader((InputStream)fileInputStream, StringUtil.codeString(fileName)))) {
            props.load((Reader)br);
        }
        catch (IOException ex) {
            final InputStream in = SendPacketOpcode.class.getClassLoader().getResourceAsStream("properties/" + fileName);
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
