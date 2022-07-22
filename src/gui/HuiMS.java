package gui;

import client.MapleCharacter;
import client.MapleStat;
import client.inventory.Equip;
import client.inventory.ItemFlag;
import client.inventory.MapleInventoryType;
import client.inventory.ModifyInventory;
import constants.GameConstants;
import constants.ServerConfig;
import constants.ServerConstants;
import constants.WorldConstants;
import database.DatabaseConnection;
import gui.tools.*;
import handling.RecvPacketOpcode;
import handling.SendPacketOpcode;
import handling.channel.ChannelServer;
import handling.login.LoginServer;
import handling.world.MapleParty;
import handling.world.World;
import handling.world.World.Find;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.rmi.NotBoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;
import org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel;
import scripting.PortalScriptManager;
import scripting.ReactorScriptManager;
import server.*;
import server.Timer.EventTimer;
import server.life.MapleMonsterInformationProvider;
import server.quest.MapleQuest;
//import sun.swing.table.DefaultTableCellHeaderRenderer;
import tools.FileoutputUtil;
import tools.MaplePacketCreator;
import tools.wztosql.*;

/**
 *
 * @author Administrator
 */
public class HuiMS extends javax.swing.JFrame {

    private static String authCode;
    private ImageIcon bgImg = new ImageIcon(this.getClass().getClassLoader().getResource("image/qqq.jpg"));// 图片路径不要写错了
    private JLabel imgLabel = new JLabel(bgImg);
    public static Map<String, Integer> ConfigValuesMap = new HashMap<>();
    private final ReentrantReadWriteLock mutex = new ReentrantReadWriteLock();
    private static HuiMS instance = new HuiMS();
    private Map<Windows, javax.swing.JFrame> windows = new HashMap<>();
    private ScheduledFuture<?> shutdownServer, updateplayer;
    private static long startRunTime = 0;
    private static long starttime = 0;
    private ArrayList<Tools> tools = new ArrayList();
    private final Lock writeLock = mutex.writeLock();
    private Vector<Vector<String>> playerTableRom = new Vector<>();
    boolean 调试模式 = false;
    boolean 自动注册 = false;
    String 服务器名字 = "获取中";
    String 经验倍数 = "获取中";
    boolean 开启服务端 = false;
    private boolean searchServer = false;
    String accname = "null", pwd = "null", money = "null", rmb = "null", dj = "null", dy = "null", ljzz = "null";
    String mima = "123456";
    int accid = 0;

    public class HomePanel extends JPanel {

        ImageIcon icon;
        Image img;

        public HomePanel() {
            ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("image/logo.png"));
            this.img = icon.getImage();
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.drawImage(this.img, 0, 0, getWidth(), getHeight(), this);
        }
    }

    //导入gif
    public class HomePanel2 extends JPanel {

        ImageIcon icon;
        Image img;

        public HomePanel2() {
            ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("image/long.gif"));
            this.img = icon.getImage();
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.drawImage(this.img, 0, 0, getWidth(), getHeight(), this);
        }
    }

    /**
     * Creates new form KinMS
     */
    public static final HuiMS getInstance() {

// 重定向System.out和System.err
        return instance;
    }

    public HuiMS() {
        ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("image/Icon.png"));
        setIconImage(icon.getImage());
        setTitle(" 服务端版本为：Ver.079版本");
        //控制台预加载配置
        GetConfigValues();
        initComponents(); // 开始服务端启动计时
        updatePlayerList();// 开始统计人数
        updateThreadNum(); // 开始统计线程
        MemoryTest();//开始内存统计情况
        刷新信息();
        //显示数据剧中代码开始
        刷新蓝蜗牛开关();
        刷新蘑菇仔开关();
        刷新绿水灵开关();
        刷新漂漂猪开关();
        刷新小青蛇开关();
        刷新红螃蟹开关();
        刷新大海龟开关();
        刷新章鱼怪开关();
        刷新顽皮猴开关();
        刷新星精灵开关();
        刷新胖企鹅开关();
        刷新白雪人开关();
        刷新石头人开关();
        刷新紫色猫开关();
        刷新大灰狼开关();
        刷新小白兔开关();
        刷新喷火龙开关();
        刷新火野猪开关();
        刷新青鳄鱼开关();
        刷新花蘑菇开关();
        //显示数据剧中代码结束
        // 设置当前进度值
        内存.setValue(0);
        内存.setStringPainted(true);
        // 绘制百分比文本（进度条中间显示的百分数）
        内存.setMinimum(0);
        内存.setMaximum(100);
        Timer.GuiTimer.getInstance().start();//计时器

        initview();//初始化控件信息
        DefaultTableCellRenderer cr = new DefaultTableCellRenderer();
        cr.setHorizontalAlignment(JLabel.CENTER);

        //jPanel1.setOpaque(false);
        //jPanel6.setOpaque(false);
        输出窗口.setEditable(false);
        输出窗口.setLineWrap(true);        //激活自动换行功能 
        输出窗口.setWrapStyleWord(true);            // 激活断行不断字功能
        主窗口.setOpaque(false);
    }

    private void resetWorldPanel() {
        //给服务器增加一个默认状态
        InputStream is = null;
//        //开始读取ini内的参数信息
//        String exp = null;
//        String drop = null;
//        String cash = null;
        Properties p = new Properties();
        BufferedReader bf = null;
        try {
            is = new FileInputStream("配置.ini");
            //这个要看你dd.properties文件的编码格式，如果编码格式是gbk的要用gbk的InputStreamReader读取，如果utf8的就不用特殊设置了，如果你手工输入的dd的信息应该是gbk的编码
            bf = new BufferedReader(new InputStreamReader(is, "utf-8"));
            p = new Properties();
            p.load(bf);

        } catch (FileNotFoundException e) {
            System.out.println("没有找到文件");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("读取配置文件失败");
            e.printStackTrace();
        }
    }

    private void 刷新信息() {
        刷新泡点金币开关();
        刷新泡点点券开关();
        刷新泡点经验开关();
        刷新泡点抵用开关();
        刷新泡点豆豆开关();
        刷新泡点设置();
        刷新经验加成表();
        刷新冒险家等级上限();
        刷新骑士团等级上限();
        刷新冒险家职业开关();
        刷新战神职业开关();
        刷新骑士团职业开关();
        刷新物品掉落持续时间();
        刷新地图物品上限();
        刷新地图刷新频率();
        刷新商城扩充价格();
        刷新吸怪检测开关();
        刷新多开数量();
        刷新过图存档时间();
        刷新地图名称开关();
        刷新登陆帮助();
        刷新怪物状态开关();
        刷新越级打怪开关();
        刷新回收地图开关();
        刷新玩家聊天开关();
        刷新滚动公告开关();
        刷新指令通知开关();
        刷新管理隐身开关();
        刷新管理加速开关();
        刷新游戏指令开关();
        刷新游戏喇叭开关();
        刷新丢出金币开关();
        刷新丢出物品开关();
        刷新雇佣商人开关();
        刷新上线提醒开关();
        刷新升级快讯();
        刷新玩家交易开关();
        刷新欢迎弹窗开关();
        刷新禁止登陆开关();
        刷新物品叠加数量上限();
        刷新倍怪地图();
        刷新弓标子弹叠加代码();
        刷新区间一倍率();
        刷新区间二倍率();
        刷新区间三倍率();
        //刷新商城扩充价格();
    }

    //初始化表内数据 表结构
    @Override
    public void setVisible(boolean bln) {
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((int) (size.getWidth() - getWidth()) / 2, (int) (size.getHeight() - getHeight()) / 2);
        super.setVisible(bln);
        try {
            //inivalue.初始化账号表(0);
            //inivalue.初始化角色表(0);
            //inivalue.初始化爆率表(0);
        } catch (Exception ex) {
            System.out.println("初始            initDropData();\n"
                    + "            initDropDataGlobal();化角色訊息錯誤:" + ex);
            JOptionPane.showMessageDialog(null, "初始化角色訊息錯誤, 請確認MySQL是否正常啟動");

        }
    }

    public static String[] DEFAULT_FONT = new String[]{
        "Table.font",
        "TableHeader.font",
        "CheckBox.font",
        "Tree.font",
        "Viewport.font",
        "ProgressBar.font",
        "RadioButtonMenuItem.font",
        "ToolBar.font",
        "ColorChooser.font",
        "ToggleButton.font",
        "Panel.font",
        "TextArea.font",
        "Menu.font",
        "TableHeader.font" // ,"TextField.font"
        ,
         "OptionPane.font",
        "MenuBar.font",
        "Button.font",
        "Label.font",
        "PasswordField.font",
        "ScrollPane.font",
        "MenuItem.font",
        "ToolTip.font",
        "List.font",
        "EditorPane.font",
        "Table.font",
        "TabbedPane.font",
        "RadioButton.font",
        "CheckBoxMenuItem.font",
        "TextPane.font",
        "PopupMenu.font",
        "TitledBorder.font",
        "ComboBox.font"
    };

    public void actionPerformed(ActionEvent e) {
        //计时开始
        Dis tt = new Dis();
        tt.start();
    }
    //计时开始
    int year = Calendar.getInstance().get(Calendar.YEAR);//年
    int month = Calendar.getInstance().get(Calendar.MONTH) + 1;//月
    int date = Calendar.getInstance().get(Calendar.DATE);//日
    int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);//小时
    int minute = Calendar.getInstance().get(Calendar.MINUTE);//分钟
    int second = Calendar.getInstance().get(Calendar.SECOND); //毫秒

    private class Dis extends Thread {
        public Dis() {
        }
        public void run() {
            while (true) {
                final int 运行秒数 = (int)((System.currentTimeMillis() - HuiMS.startRunTime) / 1000L);
                时长.setValue(运行秒数 / 60);
                时长.setString(运行秒数 / 86400 + "天" + 运行秒数 / 3600 % 24 + "时" + 运行秒数 / 60 % 60 + "分" + 运行秒数 % 60 + "秒");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //玩家监测
    public static int 在线账号() {
        int data = 0;
        int p = 0;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT loggedin as DATA FROM accounts WHERE loggedin > 0");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    data = rs.getInt("DATA");
                    p += 1;
                }
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("在线账号、出错");
        }
        return p;
    }

    public void updatePlayerList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateplayer = Timer.GuiTimer.getInstance().register(new Runnable() {
                    @Override
                    public void run() {
                        playerTable.removeAll();
                        int cloumn = 0;
                        for (ChannelServer cs : ChannelServer.getAllInstances()) {
                            for (MapleCharacter player : cs.getPlayerStorage().getAllCharacters()) {
                                if (player == null) {
                                    continue;
                                }
                                playerTable.setValueAt(player.getId(), cloumn, 0);//角色ID
                                playerTable.setValueAt(player.getName(), cloumn, 1);//角色名字
                                playerTable.setValueAt(cs.getChannel(), cloumn, 2);//频道
                                playerTable.setValueAt(player.getLevel(), cloumn, 3);//等级
                                playerTable.setValueAt(player.getJob(), cloumn, 4);//职业ID
                                playerTable.setValueAt(player.getMap().getMapName(), cloumn, 5);//所在地图
                                playerTable.setValueAt(player.getMeso(), cloumn, 6);//金币
                                playerTable.setValueAt(player.getCSPoints(1), cloumn, 7);//点卷
                                playerTable.setValueAt(player.getCSPoints(2), cloumn, 8);//抵用卷
                                playerTable.setValueAt(player.getmoneyb(), cloumn, 9);//元宝 余额
                                cloumn++;
                            }
                        }
                        //PlayerCount.setText("【在线人数】：");
                        在线人数.setValue(cloumn);
                        在线人数.setString(cloumn + "/999");
                    }
                }, 1000 * 60);
            }
        }).start();
    }

    public static void GetConfigValues() {
        //动态数据库连接
        Connection con = DatabaseConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement("SELECT name, val FROM ConfigValues")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("name");
                    int val = rs.getInt("val");
                    ConfigValuesMap.put(name, val);
                }
            }
            ps.close();
        } catch (SQLException ex) {
            System.err.println("读取动态数据库出错：" + ex.getMessage());
        }
    }

    public void initview() {
        /*  363: 344 */
        try {
            LoopedStreams ls = new LoopedStreams();
            PrintStream ps = new PrintStream(ls.getOutputStream());
            System.setOut(ps);
            System.setErr(ps);
            startConsoleReaderThread(ls.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(HuiMS.class.getName()).log(Level.SEVERE, null, ex);
        }
        ((JPanel) getContentPane()).setOpaque(true); // 将JFrame上自带的面板设置为透明，否则背景图片
        UIManager.put("TabbedPane.contentOpaque", true);
    }

    void startConsoleReaderThread(InputStream inStream) {
        final BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
        new Thread(new Runnable() {
            public void run() {
                StringBuffer sb = new StringBuffer();
                try {
                    String s;
                    while ((s = br.readLine()) != null) {
                        boolean caretAtEnd = false;
                        sb.setLength(0);
                        HuiMS.this.输出窗口.append(new StringBuilder().append("").append(s).toString() + '\n');
                        if (!caretAtEnd) {
                        }
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "从BufferedReader读取错误：" + e);

                    System.exit(1);
                }
            }
        }).start();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        主窗口 = new javax.swing.JTabbedPane();
        首页功能 = new javax.swing.JPanel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel29 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        输出窗口 = new javax.swing.JTextArea();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        playerTable = new javax.swing.JTable();
        jPanel38 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        角色名称编辑框 = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        角色点券编辑框 = new javax.swing.JTextField();
        角色抵用编辑框 = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        角色所在地图编辑 = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        修改玩家信息 = new javax.swing.JButton();
        个人玩家下线 = new javax.swing.JButton();
        传送玩家到自由 = new javax.swing.JButton();
        全员下线 = new javax.swing.JButton();
        关玩家到小黑屋 = new javax.swing.JButton();
        传送玩家到指定地图 = new javax.swing.JButton();
        一键满技能 = new javax.swing.JButton();
        jLabel27 = new javax.swing.JLabel();
        角色元宝编辑框 = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        重载副本按钮2 = new javax.swing.JButton();
        重载爆率按钮2 = new javax.swing.JButton();
        重载反应堆按钮2 = new javax.swing.JButton();
        重载传送门按钮2 = new javax.swing.JButton();
        重载商城按钮2 = new javax.swing.JButton();
        重载商店按钮2 = new javax.swing.JButton();
        重载任务2 = new javax.swing.JButton();
        重载包头按钮2 = new javax.swing.JButton();
        jLabel28 = new javax.swing.JLabel();
        jPanel34 = new javax.swing.JPanel();
        startserverbutton = new javax.swing.JButton();
        ActiveThread = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        内存 = new javax.swing.JProgressBar();
        jLabel44 = new javax.swing.JLabel();
        jTextField22 = new javax.swing.JTextField();
        jButton16 = new javax.swing.JButton();
        jPanel37 = new javax.swing.JPanel();
        查询在线玩家人数按钮 = new javax.swing.JButton();
        jButton17 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        清空日志 = new javax.swing.JButton();
        PlayerCount = new javax.swing.JLabel();
        时长 = new javax.swing.JProgressBar();
        在线人数 = new javax.swing.JProgressBar();
        常用工具 = new javax.swing.JPanel();
        jPanel67 = new javax.swing.JPanel();
        jButton69 = new javax.swing.JButton();
        jButton70 = new javax.swing.JButton();
        jButton72 = new javax.swing.JButton();
        jButton73 = new javax.swing.JButton();
        jButton74 = new javax.swing.JButton();
        jButton75 = new javax.swing.JButton();
        jButton76 = new javax.swing.JButton();
        jButton77 = new javax.swing.JButton();
        jPanel53 = new javax.swing.JPanel();
        jButton40 = new javax.swing.JButton();
        jButton41 = new javax.swing.JButton();
        jButton29 = new javax.swing.JButton();
        jButton22 = new javax.swing.JButton();
        jButton42 = new javax.swing.JButton();
        jButton43 = new javax.swing.JButton();
        jPanel68 = new javax.swing.JPanel();
        jButton27 = new javax.swing.JButton();
        jButton53 = new javax.swing.JButton();
        jButton55 = new javax.swing.JButton();
        jButton44 = new javax.swing.JButton();
        jButton50 = new javax.swing.JButton();
        jButton47 = new javax.swing.JButton();
        jButton51 = new javax.swing.JButton();
        jButton48 = new javax.swing.JButton();
        jButton38 = new javax.swing.JButton();
        jButton68 = new javax.swing.JButton();
        jButton46 = new javax.swing.JButton();
        jButton49 = new javax.swing.JButton();
        jButton54 = new javax.swing.JButton();
        jButton34 = new javax.swing.JButton();
        jButton35 = new javax.swing.JButton();
        功能设置 = new javax.swing.JTabbedPane();
        jPanel81 = new javax.swing.JPanel();
        jPanel72 = new javax.swing.JPanel();
        jButton13 = new javax.swing.JButton();
        jTextField2 = new javax.swing.JTextField();
        禁止登陆开关 = new javax.swing.JButton();
        玩家交易开关 = new javax.swing.JButton();
        地图名称开关 = new javax.swing.JButton();
        上线提醒开关 = new javax.swing.JButton();
        指令通知开关 = new javax.swing.JButton();
        过图存档开关 = new javax.swing.JButton();
        欢迎弹窗开关 = new javax.swing.JButton();
        玩家聊天开关 = new javax.swing.JButton();
        游戏升级快讯 = new javax.swing.JButton();
        回收地图开关 = new javax.swing.JButton();
        吸怪检测开关 = new javax.swing.JButton();
        雇佣商人开关 = new javax.swing.JButton();
        屠令广播开关 = new javax.swing.JButton();
        游戏喇叭开关 = new javax.swing.JButton();
        登陆帮助开关 = new javax.swing.JButton();
        管理隐身开关 = new javax.swing.JButton();
        游戏指令开关 = new javax.swing.JButton();
        越级打怪开关 = new javax.swing.JButton();
        滚动公告开关 = new javax.swing.JButton();
        丢出金币开关 = new javax.swing.JButton();
        丢出物品开关 = new javax.swing.JButton();
        怪物状态开关 = new javax.swing.JButton();
        管理加速开关 = new javax.swing.JButton();
        大区设置 = new javax.swing.JPanel();
        jPanel74 = new javax.swing.JPanel();
        蓝蜗牛开关 = new javax.swing.JButton();
        蘑菇仔开关 = new javax.swing.JButton();
        绿水灵开关 = new javax.swing.JButton();
        漂漂猪开关 = new javax.swing.JButton();
        小青蛇开关 = new javax.swing.JButton();
        红螃蟹开关 = new javax.swing.JButton();
        大海龟开关 = new javax.swing.JButton();
        章鱼怪开关 = new javax.swing.JButton();
        顽皮猴开关 = new javax.swing.JButton();
        星精灵开关 = new javax.swing.JButton();
        胖企鹅开关 = new javax.swing.JButton();
        白雪人开关 = new javax.swing.JButton();
        石头人开关 = new javax.swing.JButton();
        紫色猫开关 = new javax.swing.JButton();
        大灰狼开关 = new javax.swing.JButton();
        喷火龙开关 = new javax.swing.JButton();
        火野猪开关 = new javax.swing.JButton();
        小白兔开关 = new javax.swing.JButton();
        青鳄鱼开关 = new javax.swing.JButton();
        花蘑菇开关 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jLabel63 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jPanel21 = new javax.swing.JPanel();
        机器码多开数量 = new javax.swing.JTextField();
        修改冒险家等级上限1 = new javax.swing.JButton();
        jLabel262 = new javax.swing.JLabel();
        IP多开数量 = new javax.swing.JTextField();
        jLabel267 = new javax.swing.JLabel();
        修改骑士团等级上限2 = new javax.swing.JButton();
        jPanel17 = new javax.swing.JPanel();
        jLabel329 = new javax.swing.JLabel();
        物品掉落持续时间 = new javax.swing.JTextField();
        修改物品掉落持续时间 = new javax.swing.JButton();
        jLabel319 = new javax.swing.JLabel();
        地图物品上限 = new javax.swing.JTextField();
        修改物品掉落持续时间1 = new javax.swing.JButton();
        jLabel330 = new javax.swing.JLabel();
        地图刷新频率 = new javax.swing.JTextField();
        修改物品掉落持续时间2 = new javax.swing.JButton();
        商城扩充价格修改 = new javax.swing.JTextField();
        修改背包扩充价格 = new javax.swing.JButton();
        jLabel331 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        冒险家职业开关 = new javax.swing.JButton();
        战神职业开关 = new javax.swing.JButton();
        骑士团职业开关 = new javax.swing.JButton();
        jPanel71 = new javax.swing.JPanel();
        幸运职业开关 = new javax.swing.JButton();
        神秘商人开关 = new javax.swing.JButton();
        魔族突袭开关 = new javax.swing.JButton();
        魔族攻城开关 = new javax.swing.JButton();
        jPanel22 = new javax.swing.JPanel();
        jTextMaxLevel = new javax.swing.JTextField();
        修改冒险家等级上限 = new javax.swing.JButton();
        jLabel253 = new javax.swing.JLabel();
        骑士团等级上限 = new javax.swing.JTextField();
        jLabel252 = new javax.swing.JLabel();
        修改骑士团等级上限 = new javax.swing.JButton();
        jTextFieldMaxCharacterNumber = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jButtonMaxCharacter = new javax.swing.JButton();
        jPanel66 = new javax.swing.JPanel();
        jPanel69 = new javax.swing.JPanel();
        开启双倍经验 = new javax.swing.JButton();
        双倍经验持续时间 = new javax.swing.JTextField();
        jLabel359 = new javax.swing.JLabel();
        开启双倍爆率 = new javax.swing.JButton();
        双倍爆率持续时间 = new javax.swing.JTextField();
        jLabel360 = new javax.swing.JLabel();
        开启双倍金币 = new javax.swing.JButton();
        双倍金币持续时间 = new javax.swing.JTextField();
        jLabel361 = new javax.swing.JLabel();
        jPanel70 = new javax.swing.JPanel();
        开启三倍经验 = new javax.swing.JButton();
        三倍经验持续时间 = new javax.swing.JTextField();
        jLabel362 = new javax.swing.JLabel();
        开启三倍爆率 = new javax.swing.JButton();
        三倍爆率持续时间 = new javax.swing.JTextField();
        jLabel348 = new javax.swing.JLabel();
        开启三倍金币 = new javax.swing.JButton();
        三倍金币持续时间 = new javax.swing.JTextField();
        jLabel349 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanel62 = new javax.swing.JPanel();
        经验 = new javax.swing.JTextField();
        jLabel42 = new javax.swing.JLabel();
        经验确认 = new javax.swing.JButton();
        物品 = new javax.swing.JTextField();
        物品确认 = new javax.swing.JButton();
        金币 = new javax.swing.JTextField();
        金币确认 = new javax.swing.JButton();
        jLabel43 = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        jPanel93 = new javax.swing.JPanel();
        jScrollPane136 = new javax.swing.JScrollPane();
        经验加成表 = new javax.swing.JTable();
        经验加成表序号 = new javax.swing.JTextField();
        经验加成表类型 = new javax.swing.JTextField();
        经验加成表数值 = new javax.swing.JTextField();
        经验加成表修改 = new javax.swing.JButton();
        jLabel384 = new javax.swing.JLabel();
        jLabel385 = new javax.swing.JLabel();
        jLabel386 = new javax.swing.JLabel();
        游戏经验加成说明 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jPanel23 = new javax.swing.JPanel();
        怪物倍率 = new javax.swing.JTextField();
        修改怪物倍率 = new javax.swing.JButton();
        jLabel263 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        倍怪地图 = new javax.swing.JTextArea();
        jLabel264 = new javax.swing.JLabel();
        jPanel63 = new javax.swing.JPanel();
        区间一最低等级 = new javax.swing.JTextField();
        经验确认1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        区间一经验倍率 = new javax.swing.JTextField();
        区间一最高等级 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        区间二最低等级 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        区间二最高等级 = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        区间二经验倍率 = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        经验确认2 = new javax.swing.JButton();
        区间三最低等级 = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        区间三最高等级 = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        区间三经验倍率 = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        经验确认3 = new javax.swing.JButton();
        jPanel64 = new javax.swing.JPanel();
        jLabel268 = new javax.swing.JLabel();
        物品叠加数量 = new javax.swing.JTextField();
        修改物品叠加数量 = new javax.swing.JButton();
        jScrollPane11 = new javax.swing.JScrollPane();
        弓标子弹叠加上限突破 = new javax.swing.JTextArea();
        jLabel21 = new javax.swing.JLabel();
        福利中心 = new javax.swing.JPanel();
        jTabbedPane7 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        jPanel59 = new javax.swing.JPanel();
        z2 = new javax.swing.JButton();
        z3 = new javax.swing.JButton();
        z1 = new javax.swing.JButton();
        z4 = new javax.swing.JButton();
        z5 = new javax.swing.JButton();
        z6 = new javax.swing.JButton();
        a1 = new javax.swing.JTextField();
        jLabel235 = new javax.swing.JLabel();
        jPanel57 = new javax.swing.JPanel();
        全服发送物品数量 = new javax.swing.JTextField();
        全服发送物品代码 = new javax.swing.JTextField();
        给予物品1 = new javax.swing.JButton();
        jLabel217 = new javax.swing.JLabel();
        jLabel234 = new javax.swing.JLabel();
        jPanel58 = new javax.swing.JPanel();
        全服发送装备装备加卷 = new javax.swing.JTextField();
        全服发送装备装备制作人 = new javax.swing.JTextField();
        全服发送装备装备力量 = new javax.swing.JTextField();
        全服发送装备装备MP = new javax.swing.JTextField();
        全服发送装备装备智力 = new javax.swing.JTextField();
        全服发送装备装备运气 = new javax.swing.JTextField();
        全服发送装备装备HP = new javax.swing.JTextField();
        全服发送装备装备攻击力 = new javax.swing.JTextField();
        全服发送装备装备给予时间 = new javax.swing.JTextField();
        全服发送装备装备可否交易 = new javax.swing.JTextField();
        全服发送装备装备敏捷 = new javax.swing.JTextField();
        全服发送装备物品ID = new javax.swing.JTextField();
        全服发送装备装备魔法力 = new javax.swing.JTextField();
        全服发送装备装备魔法防御 = new javax.swing.JTextField();
        全服发送装备装备物理防御 = new javax.swing.JTextField();
        给予装备1 = new javax.swing.JButton();
        jLabel219 = new javax.swing.JLabel();
        jLabel220 = new javax.swing.JLabel();
        jLabel221 = new javax.swing.JLabel();
        jLabel222 = new javax.swing.JLabel();
        jLabel223 = new javax.swing.JLabel();
        jLabel224 = new javax.swing.JLabel();
        jLabel225 = new javax.swing.JLabel();
        jLabel226 = new javax.swing.JLabel();
        jLabel227 = new javax.swing.JLabel();
        jLabel228 = new javax.swing.JLabel();
        jLabel229 = new javax.swing.JLabel();
        jLabel230 = new javax.swing.JLabel();
        jLabel231 = new javax.swing.JLabel();
        jLabel232 = new javax.swing.JLabel();
        jLabel233 = new javax.swing.JLabel();
        发送装备玩家姓名 = new javax.swing.JTextField();
        给予装备2 = new javax.swing.JButton();
        jLabel246 = new javax.swing.JLabel();
        jLabel244 = new javax.swing.JLabel();
        jPanel60 = new javax.swing.JPanel();
        个人发送物品数量 = new javax.swing.JTextField();
        个人发送物品玩家名字 = new javax.swing.JTextField();
        个人发送物品代码 = new javax.swing.JTextField();
        给予物品 = new javax.swing.JButton();
        jLabel240 = new javax.swing.JLabel();
        jLabel241 = new javax.swing.JLabel();
        jLabel242 = new javax.swing.JLabel();
        jPanel80 = new javax.swing.JPanel();
        z7 = new javax.swing.JButton();
        z8 = new javax.swing.JButton();
        z9 = new javax.swing.JButton();
        z10 = new javax.swing.JButton();
        z11 = new javax.swing.JButton();
        z12 = new javax.swing.JButton();
        a2 = new javax.swing.JTextField();
        jLabel236 = new javax.swing.JLabel();
        个人发送物品玩家名字1 = new javax.swing.JTextField();
        jLabel64 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane134 = new javax.swing.JScrollPane();
        在线泡点设置 = new javax.swing.JTable();
        泡点序号 = new javax.swing.JTextField();
        泡点类型 = new javax.swing.JTextField();
        泡点值 = new javax.swing.JTextField();
        泡点值修改 = new javax.swing.JButton();
        jLabel322 = new javax.swing.JLabel();
        jLabel326 = new javax.swing.JLabel();
        jLabel327 = new javax.swing.JLabel();
        jPanel75 = new javax.swing.JPanel();
        泡点金币开关 = new javax.swing.JButton();
        泡点经验开关 = new javax.swing.JButton();
        泡点点券开关 = new javax.swing.JButton();
        泡点抵用开关 = new javax.swing.JButton();
        泡点豆豆开关 = new javax.swing.JButton();
        jLabel65 = new javax.swing.JLabel();
        jLabel328 = new javax.swing.JLabel();
        福利提示语言2 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        游戏公告 = new javax.swing.JPanel();
        sendNotice = new javax.swing.JButton();
        sendWinNotice = new javax.swing.JButton();
        sendMsgNotice = new javax.swing.JButton();
        sendNpcTalkNotice = new javax.swing.JButton();
        noticeText = new javax.swing.JTextField();
        jLabel117 = new javax.swing.JLabel();
        jLabel118 = new javax.swing.JLabel();
        jLabel119 = new javax.swing.JLabel();
        jLabel106 = new javax.swing.JLabel();
        公告发布喇叭代码 = new javax.swing.JTextField();
        jButton45 = new javax.swing.JButton();
        jLabel259 = new javax.swing.JLabel();
        物品爆率 = new javax.swing.JPanel();
        jTabbedPane9 = new javax.swing.JTabbedPane();
        jPanel16 = new javax.swing.JPanel();
        jScrollPane26 = new javax.swing.JScrollPane();
        反应堆 = new javax.swing.JTable();
        jPanel61 = new javax.swing.JPanel();
        jButton26 = new javax.swing.JButton();
        反应堆序列号 = new javax.swing.JTextField();
        反应堆代码 = new javax.swing.JTextField();
        反应堆物品 = new javax.swing.JTextField();
        反应堆概率 = new javax.swing.JTextField();
        新增反应堆物品 = new javax.swing.JButton();
        删除反应堆物品1 = new javax.swing.JButton();
        查找反应堆掉落 = new javax.swing.JTextField();
        jButton36 = new javax.swing.JButton();
        查找物品 = new javax.swing.JTextField();
        jButton37 = new javax.swing.JButton();
        jLabel274 = new javax.swing.JLabel();
        jLabel275 = new javax.swing.JLabel();
        jLabel277 = new javax.swing.JLabel();
        jLabel278 = new javax.swing.JLabel();
        jLabel279 = new javax.swing.JLabel();
        jLabel280 = new javax.swing.JLabel();
        修改反应堆物品 = new javax.swing.JButton();
        重载箱子反应堆按钮 = new javax.swing.JButton();
        jLabel282 = new javax.swing.JLabel();
        删除反应堆物品代码 = new javax.swing.JTextField();
        删除反应堆物品 = new javax.swing.JButton();
        jPanel19 = new javax.swing.JPanel();
        jPanel28 = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        世界爆物 = new javax.swing.JTable();
        世界爆物序列号 = new javax.swing.JTextField();
        世界爆物物品代码 = new javax.swing.JTextField();
        世界爆物爆率 = new javax.swing.JTextField();
        添加世界爆物 = new javax.swing.JButton();
        删除世界爆物 = new javax.swing.JButton();
        jLabel210 = new javax.swing.JLabel();
        jLabel202 = new javax.swing.JLabel();
        jLabel209 = new javax.swing.JLabel();
        世界爆物名称 = new javax.swing.JTextField();
        jLabel40 = new javax.swing.JLabel();
        修改世界爆物 = new javax.swing.JButton();
        刷新世界爆物 = new javax.swing.JButton();
        jLabel323 = new javax.swing.JLabel();
        查询物品掉落代码1 = new javax.swing.JTextField();
        查询物品掉落 = new javax.swing.JButton();
        jLabel321 = new javax.swing.JLabel();
        删除指定的掉落1 = new javax.swing.JTextField();
        删除指定的掉落按键 = new javax.swing.JButton();
        jPanel18 = new javax.swing.JPanel();
        jPanel27 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        怪物爆物 = new javax.swing.JTable();
        刷新怪物爆物 = new javax.swing.JButton();
        刷新怪物卡片 = new javax.swing.JButton();
        jLabel213 = new javax.swing.JLabel();
        怪物爆物序列号 = new javax.swing.JTextField();
        jLabel120 = new javax.swing.JLabel();
        怪物爆物怪物代码 = new javax.swing.JTextField();
        jLabel211 = new javax.swing.JLabel();
        怪物爆物物品代码 = new javax.swing.JTextField();
        jLabel212 = new javax.swing.JLabel();
        怪物爆物爆率 = new javax.swing.JTextField();
        jLabel39 = new javax.swing.JLabel();
        怪物爆物物品名称 = new javax.swing.JTextField();
        添加怪物爆物 = new javax.swing.JButton();
        删除怪物爆物 = new javax.swing.JButton();
        修改怪物爆物 = new javax.swing.JButton();
        jLabel324 = new javax.swing.JLabel();
        查询物品掉落代码 = new javax.swing.JTextField();
        查询物品掉落1 = new javax.swing.JButton();
        jLabel316 = new javax.swing.JLabel();
        jLabel325 = new javax.swing.JLabel();
        删除指定的掉落 = new javax.swing.JTextField();
        删除指定的掉落按键1 = new javax.swing.JButton();
        查询怪物掉落代码 = new javax.swing.JTextField();
        查询怪物掉落 = new javax.swing.JButton();
        jPanel20 = new javax.swing.JPanel();
        钓鱼管理 = new javax.swing.JPanel();
        jScrollPane10 = new javax.swing.JScrollPane();
        钓鱼物品 = new javax.swing.JTable();
        jSeparator11 = new javax.swing.JSeparator();
        jSeparator12 = new javax.swing.JSeparator();
        jPanel91 = new javax.swing.JPanel();
        修改钓鱼物品 = new javax.swing.JButton();
        刷新钓鱼物品 = new javax.swing.JButton();
        钓鱼物品代码 = new javax.swing.JTextField();
        新增钓鱼物品 = new javax.swing.JButton();
        钓鱼物品概率 = new javax.swing.JTextField();
        钓鱼物品名称 = new javax.swing.JTextField();
        删除钓鱼物品 = new javax.swing.JButton();
        钓鱼物品序号 = new javax.swing.JTextField();
        jLabel379 = new javax.swing.JLabel();
        jLabel380 = new javax.swing.JLabel();
        jLabel381 = new javax.swing.JLabel();
        jLabel382 = new javax.swing.JLabel();
        ZEVMS2提示框1 = new javax.swing.JLabel();
        关于我们 = new javax.swing.JTabbedPane();
        jScrollPane9 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jPanel52 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jButton7 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));
        setResizable(false);
        setSize(new java.awt.Dimension(890, 690));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        主窗口.setBackground(new java.awt.Color(255, 255, 255));
        主窗口.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        主窗口.setPreferredSize(new java.awt.Dimension(890, 758));

        首页功能.setBackground(new java.awt.Color(255, 255, 255));
        首页功能.setPreferredSize(new java.awt.Dimension(890, 758));
        首页功能.setRequestFocusEnabled(false);
        首页功能.setVerifyInputWhenFocusTarget(false);
        首页功能.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        输出窗口.setColumns(20);
        输出窗口.setForeground(new java.awt.Color(102, 102, 102));
        输出窗口.setRows(5);
        输出窗口.setFocusTraversalPolicyProvider(true);
        输出窗口.setInheritsPopupMenu(true);
        输出窗口.setSelectedTextColor(new java.awt.Color(51, 0, 51));
        jScrollPane2.setViewportView(输出窗口);

        javax.swing.GroupLayout jPanel29Layout = new javax.swing.GroupLayout(jPanel29);
        jPanel29.setLayout(jPanel29Layout);
        jPanel29Layout.setHorizontalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 635, Short.MAX_VALUE)
        );
        jPanel29Layout.setVerticalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 502, Short.MAX_VALUE)
        );

        jTabbedPane2.addTab("服务端输出信息", new javax.swing.ImageIcon(getClass().getResource("/image2/日志.png")), jPanel29); // NOI18N

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "在线玩家信息", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("宋体", 0, 12), new java.awt.Color(255, 153, 153))); // NOI18N
        jPanel5.setPreferredSize(new java.awt.Dimension(500, 500));

        playerTable.setAutoCreateRowSorter(true);
        playerTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "id", "名称", "频道", "等级", "职业", "地图", "金币", "点卷", "抵用", "元宝"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        playerTable.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        playerTable.setGridColor(javax.swing.UIManager.getDefaults().getColor("Button.light"));
        playerTable.getTableHeader().setReorderingAllowed(false);
        playerTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                playerTableMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(playerTable);

        jPanel38.setBackground(new java.awt.Color(255, 255, 255));
        jPanel38.setBorder(javax.swing.BorderFactory.createTitledBorder("便捷功能"));

        jLabel22.setText("角色名称：");

        角色名称编辑框.setEditable(false);
        角色名称编辑框.setForeground(new java.awt.Color(51, 153, 255));
        角色名称编辑框.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                角色名称编辑框ActionPerformed(evt);
            }
        });

        jLabel23.setText("角色点券：");

        角色点券编辑框.setForeground(new java.awt.Color(51, 153, 255));
        角色点券编辑框.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                角色点券编辑框ActionPerformed(evt);
            }
        });

        角色抵用编辑框.setForeground(new java.awt.Color(51, 153, 255));
        角色抵用编辑框.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                角色抵用编辑框ActionPerformed(evt);
            }
        });

        jLabel24.setText("角色抵用：");

        角色所在地图编辑.setForeground(new java.awt.Color(51, 153, 255));
        角色所在地图编辑.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                角色所在地图编辑ActionPerformed(evt);
            }
        });

        jLabel25.setText("所在地图：");

        修改玩家信息.setText("修改信息");
        修改玩家信息.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                修改玩家信息ActionPerformed(evt);
            }
        });

        个人玩家下线.setText("强制下线");
        个人玩家下线.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                个人玩家下线ActionPerformed(evt);
            }
        });

        传送玩家到自由.setText("传送自由");
        传送玩家到自由.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                传送玩家到自由ActionPerformed(evt);
            }
        });

        全员下线.setText("全部下线");
        全员下线.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                全员下线ActionPerformed(evt);
            }
        });

        关玩家到小黑屋.setText("关小黑屋");
        关玩家到小黑屋.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                关玩家到小黑屋ActionPerformed(evt);
            }
        });

        传送玩家到指定地图.setText("传送地图");
        传送玩家到指定地图.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                传送玩家到指定地图ActionPerformed(evt);
            }
        });

        一键满技能.setText("一键满技");
        一键满技能.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                一键满技能ActionPerformed(evt);
            }
        });

        jLabel27.setText("角色元宝：");

        角色元宝编辑框.setForeground(new java.awt.Color(51, 153, 255));
        角色元宝编辑框.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                角色元宝编辑框ActionPerformed(evt);
            }
        });

        jLabel26.setForeground(new java.awt.Color(255, 0, 0));
        jLabel26.setText("给角色传送请填写地图代码！");

        javax.swing.GroupLayout jPanel38Layout = new javax.swing.GroupLayout(jPanel38);
        jPanel38.setLayout(jPanel38Layout);
        jPanel38Layout.setHorizontalGroup(
            jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel38Layout.createSequentialGroup()
                .addGroup(jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel38Layout.createSequentialGroup()
                            .addComponent(jLabel27)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(角色元宝编辑框))
                        .addGroup(jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel38Layout.createSequentialGroup()
                                .addComponent(jLabel24)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(角色抵用编辑框, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel38Layout.createSequentialGroup()
                                .addComponent(jLabel23)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(角色点券编辑框, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel38Layout.createSequentialGroup()
                                .addComponent(jLabel22)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(角色名称编辑框, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel38Layout.createSequentialGroup()
                        .addComponent(jLabel25)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(角色所在地图编辑, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel26)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                .addGroup(jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel38Layout.createSequentialGroup()
                        .addComponent(传送玩家到指定地图)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(传送玩家到自由)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(个人玩家下线))
                    .addGroup(jPanel38Layout.createSequentialGroup()
                        .addComponent(一键满技能)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(关玩家到小黑屋)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(全员下线))
                    .addComponent(修改玩家信息, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel38Layout.setVerticalGroup(
            jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel38Layout.createSequentialGroup()
                .addGroup(jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel38Layout.createSequentialGroup()
                        .addGroup(jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(传送玩家到指定地图)
                            .addComponent(传送玩家到自由)
                            .addComponent(个人玩家下线))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(全员下线)
                            .addComponent(一键满技能)
                            .addComponent(关玩家到小黑屋))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(修改玩家信息, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel38Layout.createSequentialGroup()
                        .addGroup(jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel22)
                            .addComponent(角色名称编辑框, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel23)
                            .addComponent(角色点券编辑框, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel24)
                            .addComponent(角色抵用编辑框, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel27)
                            .addComponent(角色元宝编辑框, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel25)
                            .addComponent(角色所在地图编辑, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel26))))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel38, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel38, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedPane2.addTab("在线玩家监控", new javax.swing.ImageIcon(getClass().getResource("/image2/信息日志.png")), jPanel5); // NOI18N

        首页功能.add(jTabbedPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 640, 540));

        jPanel15.setBackground(new java.awt.Color(250, 250, 250));
        jPanel15.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "重载系列", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP));
        jPanel15.setPreferredSize(new java.awt.Dimension(320, 250));

        重载副本按钮2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/更新.png"))); // NOI18N
        重载副本按钮2.setText("重载副本");
        重载副本按钮2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                重载副本按钮2ActionPerformed(evt);
            }
        });

        重载爆率按钮2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/4031041.png"))); // NOI18N
        重载爆率按钮2.setText("重载爆率");
        重载爆率按钮2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                重载爆率按钮2ActionPerformed(evt);
            }
        });

        重载反应堆按钮2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/更多设置.png"))); // NOI18N
        重载反应堆按钮2.setText("重载反应堆");
        重载反应堆按钮2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                重载反应堆按钮2ActionPerformed(evt);
            }
        });

        重载传送门按钮2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/1802034.png"))); // NOI18N
        重载传送门按钮2.setText("重载传送门");
        重载传送门按钮2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                重载传送门按钮2ActionPerformed(evt);
            }
        });

        重载商城按钮2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/自定义购物中心.png"))); // NOI18N
        重载商城按钮2.setText("重载商城");
        重载商城按钮2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                重载商城按钮2ActionPerformed(evt);
            }
        });

        重载商店按钮2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/商店管理.png"))); // NOI18N
        重载商店按钮2.setText("重载商店");
        重载商店按钮2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                重载商店按钮2ActionPerformed(evt);
            }
        });

        重载任务2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/信息日志.png"))); // NOI18N
        重载任务2.setText("重载任务");
        重载任务2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                重载任务2ActionPerformed(evt);
            }
        });

        重载包头按钮2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/更多设置.png"))); // NOI18N
        重载包头按钮2.setText("重载包头");
        重载包头按钮2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                重载包头按钮2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(重载商城按钮2, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(重载爆率按钮2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(重载任务2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(重载商店按钮2, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(重载包头按钮2, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(重载传送门按钮2, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(重载反应堆按钮2, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(重载副本按钮2, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(重载商店按钮2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(重载反应堆按钮2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(重载商城按钮2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(重载传送门按钮2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(重载包头按钮2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(重载爆率按钮2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(重载任务2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(重载副本按钮2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        首页功能.add(jPanel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 30, 290, 250));

        jLabel28.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jLabel28.setText("【运行时长】：");
        首页功能.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 500, -1, -1));

        jPanel34.setBackground(new java.awt.Color(255, 255, 255));
        jPanel34.setBorder(javax.swing.BorderFactory.createTitledBorder("游戏开关"));

        startserverbutton.setBackground(new java.awt.Color(51, 51, 255));
        startserverbutton.setFont(new java.awt.Font("微软雅黑", 1, 12)); // NOI18N
        startserverbutton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/常用功能.png"))); // NOI18N
        startserverbutton.setText("启动服务端");
        startserverbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startserverbuttonActionPerformed(evt);
            }
        });

        ActiveThread.setText("【游戏线程】:0个进程");

        jLabel2.setText("【当前内存】:");

        内存.setMaximum(1000);
        内存.setAutoscrolls(true);
        内存.setString("0 MB");
        内存.setStringPainted(true);

        jLabel44.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jLabel44.setText("关闭时间/ 分钟");

        jTextField22.setForeground(new java.awt.Color(255, 51, 51));
        jTextField22.setText("5");
        jTextField22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField22ActionPerformed(evt);
            }
        });

        jButton16.setBackground(new java.awt.Color(0, 0, 204));
        jButton16.setFont(new java.awt.Font("微软雅黑", 1, 12)); // NOI18N
        jButton16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/关闭服务器.png"))); // NOI18N
        jButton16.setText("关闭服务端");
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel34Layout = new javax.swing.GroupLayout(jPanel34);
        jPanel34.setLayout(jPanel34Layout);
        jPanel34Layout.setHorizontalGroup(
            jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel34Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(startserverbutton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ActiveThread, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(内存, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel44)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField22, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(jButton16)
                .addContainerGap())
        );
        jPanel34Layout.setVerticalGroup(
            jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel34Layout.createSequentialGroup()
                .addGroup(jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(startserverbutton, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel34Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addGroup(jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(内存, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(ActiveThread)))
                    .addComponent(jButton16, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel34Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField22, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel44))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        首页功能.add(jPanel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 550, 930, 70));
        jPanel34.getAccessibleContext().setAccessibleDescription("");

        jPanel37.setBackground(new java.awt.Color(255, 255, 255));
        jPanel37.setBorder(javax.swing.BorderFactory.createTitledBorder("工具系列"));

        查询在线玩家人数按钮.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/100.png"))); // NOI18N
        查询在线玩家人数按钮.setText("在线人数");
        查询在线玩家人数按钮.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                查询在线玩家人数按钮ActionPerformed(evt);
            }
        });

        jButton17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/100.png"))); // NOI18N
        jButton17.setText("回收内存");
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });

        jButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/100.png"))); // NOI18N
        jButton8.setText("保存数据");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/100.png"))); // NOI18N
        jButton9.setText("保存雇佣");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        清空日志.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/错误日志.png"))); // NOI18N
        清空日志.setText("清空日志");
        清空日志.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                清空日志ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel37Layout = new javax.swing.GroupLayout(jPanel37);
        jPanel37.setLayout(jPanel37Layout);
        jPanel37Layout.setHorizontalGroup(
            jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel37Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(查询在线玩家人数按钮, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addGroup(jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton17, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addGroup(jPanel37Layout.createSequentialGroup()
                .addGap(79, 79, 79)
                .addComponent(清空日志, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel37Layout.setVerticalGroup(
            jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel37Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(查询在线玩家人数按钮, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton17, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(清空日志, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        首页功能.add(jPanel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 290, 290, 200));

        PlayerCount.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        PlayerCount.setText("【在线人数】：");
        首页功能.add(PlayerCount, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 530, 90, -1));

        时长.setMaximum(21000);
        时长.setAutoscrolls(true);
        时长.setString("0天0时0分0秒");
        时长.setStringPainted(true);
        首页功能.add(时长, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 500, -1, -1));

        在线人数.setAutoscrolls(true);
        在线人数.setString("0/999");
        在线人数.setStringPainted(true);
        首页功能.add(在线人数, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 530, -1, -1));

        主窗口.addTab("首页功能", new javax.swing.ImageIcon(getClass().getResource("/image2/枫叶.png")), 首页功能); // NOI18N

        常用工具.setBackground(new java.awt.Color(255, 255, 255));
        常用工具.setVerifyInputWhenFocusTarget(false);
        常用工具.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel67.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "转存数据[非开发者请勿点击下方功能]", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("微软雅黑", 0, 12), new java.awt.Color(255, 0, 0))); // NOI18N

        jButton69.setText("更新物品道具");
        jButton69.setMaximumSize(new java.awt.Dimension(81, 23));
        jButton69.setMinimumSize(new java.awt.Dimension(81, 23));
        jButton69.setPreferredSize(new java.awt.Dimension(81, 23));
        jButton69.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton69ActionPerformed(evt);
            }
        });

        jButton70.setText("导出爆物数据");
        jButton70.setMaximumSize(new java.awt.Dimension(81, 23));
        jButton70.setMinimumSize(new java.awt.Dimension(81, 23));
        jButton70.setPreferredSize(new java.awt.Dimension(81, 23));
        jButton70.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton70ActionPerformed(evt);
            }
        });

        jButton72.setText("更新商城");
        jButton72.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton72ActionPerformed(evt);
            }
        });

        jButton73.setText("更新怪物技能");
        jButton73.setMaximumSize(new java.awt.Dimension(81, 23));
        jButton73.setMinimumSize(new java.awt.Dimension(81, 23));
        jButton73.setPreferredSize(new java.awt.Dimension(81, 23));
        jButton73.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton73ActionPerformed(evt);
            }
        });

        jButton74.setText("更新问答数据");
        jButton74.setMaximumSize(new java.awt.Dimension(81, 23));
        jButton74.setMinimumSize(new java.awt.Dimension(81, 23));
        jButton74.setPreferredSize(new java.awt.Dimension(81, 23));
        jButton74.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton74ActionPerformed(evt);
            }
        });

        jButton75.setText("更新任务数据");
        jButton75.setMaximumSize(new java.awt.Dimension(81, 23));
        jButton75.setMinimumSize(new java.awt.Dimension(81, 23));
        jButton75.setPreferredSize(new java.awt.Dimension(81, 23));
        jButton75.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton75ActionPerformed(evt);
            }
        });

        jButton76.setText("更新发型脸型");
        jButton76.setMaximumSize(new java.awt.Dimension(81, 23));
        jButton76.setMinimumSize(new java.awt.Dimension(81, 23));
        jButton76.setPreferredSize(new java.awt.Dimension(81, 23));
        jButton76.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton76ActionPerformed(evt);
            }
        });

        jButton77.setText("NPC名称");
        jButton77.setMaximumSize(new java.awt.Dimension(81, 23));
        jButton77.setMinimumSize(new java.awt.Dimension(81, 23));
        jButton77.setPreferredSize(new java.awt.Dimension(81, 23));
        jButton77.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton77ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel67Layout = new javax.swing.GroupLayout(jPanel67);
        jPanel67.setLayout(jPanel67Layout);
        jPanel67Layout.setHorizontalGroup(
            jPanel67Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel67Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel67Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton73, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton70, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton69, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(jPanel67Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel67Layout.createSequentialGroup()
                        .addGroup(jPanel67Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton72, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton74, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel67Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton75, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton76, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jButton77, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel67Layout.setVerticalGroup(
            jPanel67Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel67Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel67Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton69, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton72, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton75, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel67Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel67Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton76, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton74, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton73, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23)
                .addGroup(jPanel67Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton70, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton77, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        常用工具.add(jPanel67, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 230));

        jPanel53.setBackground(new java.awt.Color(250, 250, 250));
        jPanel53.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "工具系列", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP));

        jButton40.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/魔方.png"))); // NOI18N
        jButton40.setText("代码查询");
        jButton40.setMaximumSize(new java.awt.Dimension(81, 23));
        jButton40.setMinimumSize(new java.awt.Dimension(81, 23));
        jButton40.setPreferredSize(new java.awt.Dimension(81, 23));
        jButton40.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton40ActionPerformed(evt);
            }
        });

        jButton41.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/问题.png"))); // NOI18N
        jButton41.setText("删除NPC");
        jButton41.setMaximumSize(new java.awt.Dimension(81, 23));
        jButton41.setMinimumSize(new java.awt.Dimension(81, 23));
        jButton41.setPreferredSize(new java.awt.Dimension(81, 23));
        jButton41.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton41ActionPerformed(evt);
            }
        });

        jButton29.setForeground(new java.awt.Color(255, 51, 51));
        jButton29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/4031683.png"))); // NOI18N
        jButton29.setText("一键清空数据库");
        jButton29.setMaximumSize(new java.awt.Dimension(81, 23));
        jButton29.setMinimumSize(new java.awt.Dimension(81, 23));
        jButton29.setPreferredSize(new java.awt.Dimension(81, 23));
        jButton29.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton29ActionPerformed(evt);
            }
        });

        jButton22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/GM工具.png"))); // NOI18N
        jButton22.setText("基址计算工具");
        jButton22.setMaximumSize(new java.awt.Dimension(81, 23));
        jButton22.setMinimumSize(new java.awt.Dimension(81, 23));
        jButton22.setPreferredSize(new java.awt.Dimension(81, 23));
        jButton22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton22ActionPerformed(evt);
            }
        });

        jButton42.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/2000001.png"))); // NOI18N
        jButton42.setText("药水冷却时间控制台");
        jButton42.setMaximumSize(new java.awt.Dimension(81, 23));
        jButton42.setMinimumSize(new java.awt.Dimension(81, 23));
        jButton42.setPreferredSize(new java.awt.Dimension(81, 23));
        jButton42.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton42ActionPerformed(evt);
            }
        });

        jButton43.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/装备修改.png"))); // NOI18N
        jButton43.setText("永恒重生装备控制台");
        jButton43.setMaximumSize(new java.awt.Dimension(81, 23));
        jButton43.setMinimumSize(new java.awt.Dimension(81, 23));
        jButton43.setPreferredSize(new java.awt.Dimension(81, 23));
        jButton43.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton43ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel53Layout = new javax.swing.GroupLayout(jPanel53);
        jPanel53.setLayout(jPanel53Layout);
        jPanel53Layout.setHorizontalGroup(
            jPanel53Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel53Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel53Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton42, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton29, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton41, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel53Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel53Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addGroup(jPanel53Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton40, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton22, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel53Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(jButton43, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel53Layout.setVerticalGroup(
            jPanel53Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel53Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel53Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton41, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton40, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel53Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton29, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton22, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel53Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton42, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton43, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30))
        );

        常用工具.add(jPanel53, new org.netbeans.lib.awtextra.AbsoluteConstraints(472, 0, -1, 230));

        jPanel68.setBorder(javax.swing.BorderFactory.createTitledBorder("其他控制台"));

        jButton27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/4110000.png"))); // NOI18N
        jButton27.setText("CDK卡密");
        jButton27.setMaximumSize(new java.awt.Dimension(81, 23));
        jButton27.setMinimumSize(new java.awt.Dimension(81, 23));
        jButton27.setPreferredSize(new java.awt.Dimension(81, 23));
        jButton27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton27ActionPerformed(evt);
            }
        });

        jButton53.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/自定义购物中心.png"))); // NOI18N
        jButton53.setText("商城管理");
        jButton53.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton53ActionPerformed(evt);
            }
        });

        jButton55.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/3010025.png"))); // NOI18N
        jButton55.setText("椅子管理");
        jButton55.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton55ActionPerformed(evt);
            }
        });

        jButton44.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/2470000.png"))); // NOI18N
        jButton44.setText("金锤子成功率");
        jButton44.setMaximumSize(new java.awt.Dimension(81, 23));
        jButton44.setMinimumSize(new java.awt.Dimension(81, 23));
        jButton44.setPreferredSize(new java.awt.Dimension(81, 23));
        jButton44.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton44ActionPerformed(evt);
            }
        });

        jButton50.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/3800871.png"))); // NOI18N
        jButton50.setText("锻造控制台");
        jButton50.setMaximumSize(new java.awt.Dimension(81, 23));
        jButton50.setMinimumSize(new java.awt.Dimension(81, 23));
        jButton50.setPreferredSize(new java.awt.Dimension(81, 23));
        jButton50.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton50ActionPerformed(evt);
            }
        });

        jButton47.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/3994506.png"))); // NOI18N
        jButton47.setText("抽奖管理");
        jButton47.setMaximumSize(new java.awt.Dimension(81, 23));
        jButton47.setMinimumSize(new java.awt.Dimension(81, 23));
        jButton47.setPreferredSize(new java.awt.Dimension(81, 23));
        jButton47.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton47ActionPerformed(evt);
            }
        });

        jButton51.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/钓鱼.png"))); // NOI18N
        jButton51.setText("鱼来鱼往");
        jButton51.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton51ActionPerformed(evt);
            }
        });

        jButton48.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/111.png"))); // NOI18N
        jButton48.setText("活动控制台");
        jButton48.setMaximumSize(new java.awt.Dimension(81, 23));
        jButton48.setMinimumSize(new java.awt.Dimension(81, 23));
        jButton48.setPreferredSize(new java.awt.Dimension(81, 23));
        jButton48.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton48ActionPerformed(evt);
            }
        });

        jButton38.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/商店管理.png"))); // NOI18N
        jButton38.setText("商店管理");
        jButton38.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton38ActionPerformed(evt);
            }
        });

        jButton68.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/2630205.png"))); // NOI18N
        jButton68.setText("广播系统");
        jButton68.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton68ActionPerformed(evt);
            }
        });

        jButton46.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/101.png"))); // NOI18N
        jButton46.setText("OX答题管理");
        jButton46.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton46ActionPerformed(evt);
            }
        });

        jButton49.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/清除.png"))); // NOI18N
        jButton49.setText("物品删除");
        jButton49.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton49ActionPerformed(evt);
            }
        });

        jButton54.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/后台.png"))); // NOI18N
        jButton54.setText("账号管理");
        jButton54.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton54ActionPerformed(evt);
            }
        });

        jButton34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/任务管理.png"))); // NOI18N
        jButton34.setText("游戏副本控制台");
        jButton34.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton34ActionPerformed(evt);
            }
        });

        jButton35.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/套装.png"))); // NOI18N
        jButton35.setText("套装系统");
        jButton35.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton35ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel68Layout = new javax.swing.GroupLayout(jPanel68);
        jPanel68.setLayout(jPanel68Layout);
        jPanel68Layout.setHorizontalGroup(
            jPanel68Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel68Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel68Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton53, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel68Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jButton46, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton44, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(jPanel68Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton50, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton27, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton38, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(jPanel68Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel68Layout.createSequentialGroup()
                        .addComponent(jButton48, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton49, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton35, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel68Layout.createSequentialGroup()
                        .addComponent(jButton55, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton51, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton34, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel68Layout.createSequentialGroup()
                        .addComponent(jButton68, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton47, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton54, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(42, Short.MAX_VALUE))
        );
        jPanel68Layout.setVerticalGroup(
            jPanel68Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel68Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel68Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton53, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton38, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton68, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton47, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton54, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel68Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                    .addComponent(jButton55, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton27, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton51, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton46, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton34, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel68Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton44, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton50, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton49, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton48, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton35, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(35, Short.MAX_VALUE))
        );

        常用工具.add(jPanel68, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 241, 890, 240));

        主窗口.addTab("常用工具", new javax.swing.ImageIcon(getClass().getResource("/image2/主页.png")), 常用工具); // NOI18N

        功能设置.setMinimumSize(new java.awt.Dimension(890, 758));
        功能设置.setPreferredSize(new java.awt.Dimension(890, 758));

        jPanel81.setBackground(new java.awt.Color(255, 255, 255));

        jPanel72.setBackground(new java.awt.Color(255, 255, 255));
        jPanel72.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "其他功能设置[注意:本版块功能点击立即生效无需重启服务端]", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("宋体", 0, 12), new java.awt.Color(255, 0, 0))); // NOI18N

        jButton13.setBackground(new java.awt.Color(51, 102, 255));
        jButton13.setFont(new java.awt.Font("微软雅黑", 1, 14)); // NOI18N
        jButton13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/2630205.png"))); // NOI18N
        jButton13.setText("发送屏幕中央公告");
        jButton13.setMaximumSize(new java.awt.Dimension(177, 41));
        jButton13.setMinimumSize(new java.awt.Dimension(177, 41));
        jButton13.setPreferredSize(new java.awt.Dimension(177, 41));
        jButton13.setRequestFocusEnabled(false);
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        jTextField2.setBackground(new java.awt.Color(204, 204, 255));
        jTextField2.setFont(new java.awt.Font("微软雅黑", 1, 14)); // NOI18N
        jTextField2.setText("输入公告内容");
        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });

        禁止登陆开关.setBackground(new java.awt.Color(0, 204, 255));
        禁止登陆开关.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        禁止登陆开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF3.png"))); // NOI18N
        禁止登陆开关.setText("游戏登陆");
        禁止登陆开关.setToolTipText("<html>\n<strong><font color=\"#FF0000\">功能说明</font></strong><br> \n<strong>用于限制玩家登陆游戏<br> <br> <br> ");
        禁止登陆开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                禁止登陆开关ActionPerformed(evt);
            }
        });

        玩家交易开关.setBackground(new java.awt.Color(0, 204, 255));
        玩家交易开关.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        玩家交易开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF3.png"))); // NOI18N
        玩家交易开关.setText("玩家交易");
        玩家交易开关.setToolTipText("<html>\n<strong><font color=\"#FF0000\">功能说明</font></strong><br> \n<strong>用于限制游戏内玩家交易功能<br> <br>");
        玩家交易开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                玩家交易开关ActionPerformed(evt);
            }
        });

        地图名称开关.setBackground(new java.awt.Color(0, 204, 255));
        地图名称开关.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        地图名称开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF3.png"))); // NOI18N
        地图名称开关.setText("地图名称");
        地图名称开关.setToolTipText("<html>\n<strong><font color=\"#FF0000\">功能说明</font></strong><br> \n<strong>过地图是否提示地图名称<br> <br>");
        地图名称开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                地图名称开关ActionPerformed(evt);
            }
        });

        上线提醒开关.setBackground(new java.awt.Color(0, 204, 255));
        上线提醒开关.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        上线提醒开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF3.png"))); // NOI18N
        上线提醒开关.setText("登录公告");
        上线提醒开关.setToolTipText("<html>\n<strong><font color=\"#FF0000\">功能说明</font></strong><br> \n<strong>玩家上线是否提示欢迎公告<br> <br>");
        上线提醒开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                上线提醒开关ActionPerformed(evt);
            }
        });

        指令通知开关.setBackground(new java.awt.Color(0, 204, 255));
        指令通知开关.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        指令通知开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF3.png"))); // NOI18N
        指令通知开关.setText("指令通知");
        指令通知开关.setToolTipText("<html>\n<strong><font color=\"#FF0000\">功能说明</font></strong><br> \n<strong>角色上线是否提示命令代码<br> <br> <br> ");
        指令通知开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                指令通知开关ActionPerformed(evt);
            }
        });

        过图存档开关.setBackground(new java.awt.Color(0, 204, 255));
        过图存档开关.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        过图存档开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF3.png"))); // NOI18N
        过图存档开关.setText("过图存档");
        过图存档开关.setToolTipText("<html>\n<strong><font color=\"#FF0000\">功能说明</font></strong><br> \n<strong>是否开启 玩家每过一张图保存当前玩家数据<br> <br>");
        过图存档开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                过图存档开关ActionPerformed(evt);
            }
        });

        欢迎弹窗开关.setBackground(new java.awt.Color(0, 204, 255));
        欢迎弹窗开关.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        欢迎弹窗开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF3.png"))); // NOI18N
        欢迎弹窗开关.setText("欢迎弹窗");
        欢迎弹窗开关.setToolTipText("<html>\n<strong><font color=\"#FF0000\">功能说明</font></strong><br> \n<strong>进入游戏是否弹出欢迎公告<br> <br>");
        欢迎弹窗开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                欢迎弹窗开关ActionPerformed(evt);
            }
        });

        玩家聊天开关.setBackground(new java.awt.Color(0, 204, 255));
        玩家聊天开关.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        玩家聊天开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF3.png"))); // NOI18N
        玩家聊天开关.setText("玩家聊天");
        玩家聊天开关.setToolTipText("<html>\n<strong><font color=\"#FF0000\">功能说明</font></strong><br> \n<strong>用于控制游戏内玩家是否可以聊天说话<br> <br> <br> ");
        玩家聊天开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                玩家聊天开关ActionPerformed(evt);
            }
        });

        游戏升级快讯.setBackground(new java.awt.Color(0, 153, 255));
        游戏升级快讯.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        游戏升级快讯.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF3.png"))); // NOI18N
        游戏升级快讯.setText("升级快讯");
        游戏升级快讯.setToolTipText("<html>\n<strong><font color=\"#FF0000\">功能说明</font></strong><br> \n<strong>用于控制玩家升级了刷公告庆祝<br> <br> <br> ");
        游戏升级快讯.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                游戏升级快讯ActionPerformed(evt);
            }
        });

        回收地图开关.setBackground(new java.awt.Color(0, 204, 255));
        回收地图开关.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        回收地图开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF3.png"))); // NOI18N
        回收地图开关.setText("回收地图");
        回收地图开关.setToolTipText("<html>\n<strong><font color=\"#FF0000\">功能说明</font></strong><br> \n<strong>用于游戏地图回收开关<br> <br> <br> ");
        回收地图开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                回收地图开关ActionPerformed(evt);
            }
        });

        吸怪检测开关.setBackground(new java.awt.Color(0, 204, 255));
        吸怪检测开关.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        吸怪检测开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF3.png"))); // NOI18N
        吸怪检测开关.setText("吸怪检测");
        吸怪检测开关.setToolTipText("");
        吸怪检测开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                吸怪检测开关ActionPerformed(evt);
            }
        });

        雇佣商人开关.setBackground(new java.awt.Color(0, 153, 255));
        雇佣商人开关.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        雇佣商人开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF3.png"))); // NOI18N
        雇佣商人开关.setText("雇佣商人");
        雇佣商人开关.setToolTipText("<html>\n<strong><font color=\"#FF0000\">功能说明</font></strong><br> \n<strong>是否允许玩家在自由摆摊<br> <br>");
        雇佣商人开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                雇佣商人开关ActionPerformed(evt);
            }
        });

        屠令广播开关.setBackground(new java.awt.Color(0, 204, 255));
        屠令广播开关.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        屠令广播开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF3.png"))); // NOI18N
        屠令广播开关.setText("屠令广播");
        屠令广播开关.setToolTipText("");
        屠令广播开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                屠令广播开关ActionPerformed(evt);
            }
        });

        游戏喇叭开关.setBackground(new java.awt.Color(0, 153, 255));
        游戏喇叭开关.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        游戏喇叭开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF3.png"))); // NOI18N
        游戏喇叭开关.setText("游戏喇叭");
        游戏喇叭开关.setToolTipText("<html>\n<strong><font color=\"#FF0000\">功能说明</font></strong><br> \n<strong>用于控制是否让玩家使用游戏喇叭功能<br> <br> <br> ");
        游戏喇叭开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                游戏喇叭开关ActionPerformed(evt);
            }
        });

        登陆帮助开关.setBackground(new java.awt.Color(0, 204, 255));
        登陆帮助开关.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        登陆帮助开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF3.png"))); // NOI18N
        登陆帮助开关.setText("登陆帮助");
        登陆帮助开关.setToolTipText("<html>\n<strong><font color=\"#FF0000\">功能说明</font></strong><br> \n<strong>进游戏是否提示登录帮助<br> <br>");
        登陆帮助开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                登陆帮助开关ActionPerformed(evt);
            }
        });

        管理隐身开关.setBackground(new java.awt.Color(0, 204, 255));
        管理隐身开关.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        管理隐身开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF3.png"))); // NOI18N
        管理隐身开关.setText("管理隐身");
        管理隐身开关.setToolTipText("<html>\n<strong><font color=\"#FF0000\">功能说明</font></strong><br> \n<strong>用于管理员号上线默认是否开启隐身BUFF<br> <br> <br> ");
        管理隐身开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                管理隐身开关ActionPerformed(evt);
            }
        });

        游戏指令开关.setBackground(new java.awt.Color(0, 204, 255));
        游戏指令开关.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        游戏指令开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF3.png"))); // NOI18N
        游戏指令开关.setText("管理指令");
        游戏指令开关.setToolTipText("<html>\n<strong><font color=\"#FF0000\">功能说明</font></strong><br> \n<strong>用于控制GM号是否可以用GM命令<br> <br> <br> ");
        游戏指令开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                游戏指令开关ActionPerformed(evt);
            }
        });

        越级打怪开关.setBackground(new java.awt.Color(0, 204, 255));
        越级打怪开关.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        越级打怪开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF3.png"))); // NOI18N
        越级打怪开关.setText("越级打怪");
        越级打怪开关.setToolTipText("<html>\n<strong><font color=\"#FF0000\">功能说明</font></strong><br> \n<strong>超越本身等级打高级怪物不MISS<br> <br>");
        越级打怪开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                越级打怪开关ActionPerformed(evt);
            }
        });

        滚动公告开关.setBackground(new java.awt.Color(0, 204, 255));
        滚动公告开关.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        滚动公告开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF3.png"))); // NOI18N
        滚动公告开关.setText("滚动公告");
        滚动公告开关.setToolTipText("<html>\n<strong><font color=\"#FF0000\">功能说明</font></strong><br> \n<strong>用于控制游戏顶部滚动公告<br> <br> <br> ");
        滚动公告开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                滚动公告开关ActionPerformed(evt);
            }
        });

        丢出金币开关.setBackground(new java.awt.Color(0, 204, 255));
        丢出金币开关.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        丢出金币开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF3.png"))); // NOI18N
        丢出金币开关.setText("丢出金币");
        丢出金币开关.setToolTipText("<html>\n<strong><font color=\"#FF0000\">功能说明</font></strong><br> \n<strong>用于控制玩家游戏内是否可以丢金币<br> <br> <br> ");
        丢出金币开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                丢出金币开关ActionPerformed(evt);
            }
        });

        丢出物品开关.setBackground(new java.awt.Color(0, 204, 255));
        丢出物品开关.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        丢出物品开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF3.png"))); // NOI18N
        丢出物品开关.setText("丢出物品");
        丢出物品开关.setToolTipText("<html>\n<strong><font color=\"#FF0000\">功能说明</font></strong><br> \n<strong>用于控制游戏内玩家是否可以丢物品<br> <br> <br> ");
        丢出物品开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                丢出物品开关ActionPerformed(evt);
            }
        });

        怪物状态开关.setBackground(new java.awt.Color(0, 204, 255));
        怪物状态开关.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        怪物状态开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF3.png"))); // NOI18N
        怪物状态开关.setText("怪物状态");
        怪物状态开关.setToolTipText("<html>\n<strong><font color=\"#FF0000\">功能说明</font></strong><br> \n<strong>用于游戏内怪物状态释放技能是否提示<br> <br>");
        怪物状态开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                怪物状态开关ActionPerformed(evt);
            }
        });

        管理加速开关.setBackground(new java.awt.Color(0, 204, 255));
        管理加速开关.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        管理加速开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF3.png"))); // NOI18N
        管理加速开关.setText("管理加速");
        管理加速开关.setToolTipText("<html>\n<strong><font color=\"#FF0000\">功能说明</font></strong><br> \n<strong>用于管理员号上线默认是否开启轻功BUFF<br> <br> <br> ");
        管理加速开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                管理加速开关ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel72Layout = new javax.swing.GroupLayout(jPanel72);
        jPanel72.setLayout(jPanel72Layout);
        jPanel72Layout.setHorizontalGroup(
            jPanel72Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel72Layout.createSequentialGroup()
                .addContainerGap(42, Short.MAX_VALUE)
                .addGroup(jPanel72Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jTextField2, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel72Layout.createSequentialGroup()
                        .addGroup(jPanel72Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(地图名称开关, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(玩家交易开关, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(禁止登陆开关, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(上线提醒开关, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(游戏喇叭开关, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(登陆帮助开关, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel72Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel72Layout.createSequentialGroup()
                                .addGroup(jPanel72Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(玩家聊天开关, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(欢迎弹窗开关, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(过图存档开关, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(指令通知开关, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel72Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel72Layout.createSequentialGroup()
                                        .addComponent(吸怪检测开关, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(丢出物品开关, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel72Layout.createSequentialGroup()
                                        .addComponent(雇佣商人开关, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(怪物状态开关, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel72Layout.createSequentialGroup()
                                        .addComponent(回收地图开关, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(丢出金币开关, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel72Layout.createSequentialGroup()
                                        .addComponent(游戏升级快讯, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(屠令广播开关, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(jPanel72Layout.createSequentialGroup()
                                .addComponent(滚动公告开关, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(越级打怪开关, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(管理加速开关, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel72Layout.createSequentialGroup()
                                .addComponent(管理隐身开关, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(游戏指令开关, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addGap(50, 50, 50))
        );
        jPanel72Layout.setVerticalGroup(
            jPanel72Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel72Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel72Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(禁止登陆开关, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(玩家聊天开关, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(游戏升级快讯, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(屠令广播开关, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addGroup(jPanel72Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(玩家交易开关, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(欢迎弹窗开关, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(回收地图开关, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(丢出金币开关, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel72Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(地图名称开关, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(过图存档开关, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(吸怪检测开关, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(丢出物品开关, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel72Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(上线提醒开关, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(指令通知开关, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(雇佣商人开关, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(怪物状态开关, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel72Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(游戏喇叭开关, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(滚动公告开关, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(越级打怪开关, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(管理加速开关, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel72Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(登陆帮助开关, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(管理隐身开关, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(游戏指令开关, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(71, 71, 71))
        );

        javax.swing.GroupLayout jPanel81Layout = new javax.swing.GroupLayout(jPanel81);
        jPanel81.setLayout(jPanel81Layout);
        jPanel81Layout.setHorizontalGroup(
            jPanel81Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel81Layout.createSequentialGroup()
                .addComponent(jPanel72, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(95, Short.MAX_VALUE))
        );
        jPanel81Layout.setVerticalGroup(
            jPanel81Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel81Layout.createSequentialGroup()
                .addComponent(jPanel72, javax.swing.GroupLayout.PREFERRED_SIZE, 392, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 203, Short.MAX_VALUE))
        );

        功能设置.addTab("快捷功能开关", new javax.swing.ImageIcon(getClass().getResource("/image2/3994691.png")), jPanel81); // NOI18N

        大区设置.setBackground(new java.awt.Color(255, 255, 255));
        大区设置.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "大区设置", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 24))); // NOI18N
        大区设置.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel74.setBackground(new java.awt.Color(250, 250, 250));
        jPanel74.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "风之大陆", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("宋体", 1, 12))); // NOI18N
        jPanel74.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        蓝蜗牛开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF2.png"))); // NOI18N
        蓝蜗牛开关.setText("蓝蜗牛");
        蓝蜗牛开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                蓝蜗牛开关ActionPerformed(evt);
            }
        });
        jPanel74.add(蓝蜗牛开关, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 190, 40));

        蘑菇仔开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF2.png"))); // NOI18N
        蘑菇仔开关.setText("蘑菇仔");
        蘑菇仔开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                蘑菇仔开关ActionPerformed(evt);
            }
        });
        jPanel74.add(蘑菇仔开关, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 20, 180, 40));

        绿水灵开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF2.png"))); // NOI18N
        绿水灵开关.setText("绿水灵");
        绿水灵开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                绿水灵开关ActionPerformed(evt);
            }
        });
        jPanel74.add(绿水灵开关, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 20, 180, 40));

        漂漂猪开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF2.png"))); // NOI18N
        漂漂猪开关.setText("漂漂猪");
        漂漂猪开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                漂漂猪开关ActionPerformed(evt);
            }
        });
        jPanel74.add(漂漂猪开关, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 80, 180, 40));

        小青蛇开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF2.png"))); // NOI18N
        小青蛇开关.setText("小青蛇");
        小青蛇开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                小青蛇开关ActionPerformed(evt);
            }
        });
        jPanel74.add(小青蛇开关, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 80, 180, 40));

        红螃蟹开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF2.png"))); // NOI18N
        红螃蟹开关.setText("红螃蟹");
        红螃蟹开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                红螃蟹开关ActionPerformed(evt);
            }
        });
        jPanel74.add(红螃蟹开关, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 260, 180, 40));

        大海龟开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF2.png"))); // NOI18N
        大海龟开关.setText("大海龟");
        大海龟开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                大海龟开关ActionPerformed(evt);
            }
        });
        jPanel74.add(大海龟开关, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, 190, 40));

        章鱼怪开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF2.png"))); // NOI18N
        章鱼怪开关.setText("章鱼怪");
        章鱼怪开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                章鱼怪开关ActionPerformed(evt);
            }
        });
        jPanel74.add(章鱼怪开关, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 200, 180, 40));

        顽皮猴开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF2.png"))); // NOI18N
        顽皮猴开关.setText("顽皮猴");
        顽皮猴开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                顽皮猴开关ActionPerformed(evt);
            }
        });
        jPanel74.add(顽皮猴开关, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 80, 180, 40));

        星精灵开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF2.png"))); // NOI18N
        星精灵开关.setText("星精灵");
        星精灵开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                星精灵开关ActionPerformed(evt);
            }
        });
        jPanel74.add(星精灵开关, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 20, 180, 40));

        胖企鹅开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF2.png"))); // NOI18N
        胖企鹅开关.setText("胖企鹅");
        胖企鹅开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                胖企鹅开关ActionPerformed(evt);
            }
        });
        jPanel74.add(胖企鹅开关, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 260, 180, 40));

        白雪人开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF2.png"))); // NOI18N
        白雪人开关.setText("白雪人");
        白雪人开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                白雪人开关ActionPerformed(evt);
            }
        });
        jPanel74.add(白雪人开关, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 200, 180, 40));

        石头人开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF2.png"))); // NOI18N
        石头人开关.setText("石头人");
        石头人开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                石头人开关ActionPerformed(evt);
            }
        });
        jPanel74.add(石头人开关, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 260, 190, 40));

        紫色猫开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF2.png"))); // NOI18N
        紫色猫开关.setText("紫色猫");
        紫色猫开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                紫色猫开关ActionPerformed(evt);
            }
        });
        jPanel74.add(紫色猫开关, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 140, 190, 40));

        大灰狼开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF2.png"))); // NOI18N
        大灰狼开关.setText("大灰狼");
        大灰狼开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                大灰狼开关ActionPerformed(evt);
            }
        });
        jPanel74.add(大灰狼开关, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 140, 180, 40));

        喷火龙开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF2.png"))); // NOI18N
        喷火龙开关.setText("喷火龙");
        喷火龙开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                喷火龙开关ActionPerformed(evt);
            }
        });
        jPanel74.add(喷火龙开关, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 200, 180, 40));

        火野猪开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF2.png"))); // NOI18N
        火野猪开关.setText("火野猪");
        火野猪开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                火野猪开关ActionPerformed(evt);
            }
        });
        jPanel74.add(火野猪开关, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 140, 180, 40));

        小白兔开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF2.png"))); // NOI18N
        小白兔开关.setText("小白兔");
        小白兔开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                小白兔开关ActionPerformed(evt);
            }
        });
        jPanel74.add(小白兔开关, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 260, 180, 40));

        青鳄鱼开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF2.png"))); // NOI18N
        青鳄鱼开关.setText("青鳄鱼");
        青鳄鱼开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                青鳄鱼开关ActionPerformed(evt);
            }
        });
        jPanel74.add(青鳄鱼开关, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 200, 190, 40));

        花蘑菇开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF2.png"))); // NOI18N
        花蘑菇开关.setText("花蘑菇");
        花蘑菇开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                花蘑菇开关ActionPerformed(evt);
            }
        });
        jPanel74.add(花蘑菇开关, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 140, 180, 40));

        jLabel11.setFont(new java.awt.Font("宋体", 1, 12)); // NOI18N
        jLabel11.setText("游戏大区请勿全部都开启,会炸客户端的，每个区所建立的角色是不一样的,进入游戏后其他没有变化");
        jPanel74.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 340, -1, -1));

        jLabel63.setFont(new java.awt.Font("宋体", 1, 12)); // NOI18N
        jLabel63.setText("本页所有功能都需要重启服务端生效，请务必在开启服务端之前配置好");
        jPanel74.add(jLabel63, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 360, -1, -1));

        大区设置.add(jPanel74, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 860, 390));

        功能设置.addTab("大区设置", new javax.swing.ImageIcon(getClass().getResource("/image2/2614075.png")), 大区设置); // NOI18N

        jPanel12.setBackground(new java.awt.Color(255, 255, 255));

        jPanel21.setBackground(new java.awt.Color(255, 255, 255));
        jPanel21.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "多开限制", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 18))); // NOI18N
        jPanel21.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel21.add(机器码多开数量, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 80, 70, -1));

        修改冒险家等级上限1.setText("修改");
        修改冒险家等级上限1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                修改冒险家等级上限1ActionPerformed(evt);
            }
        });
        jPanel21.add(修改冒险家等级上限1, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 80, 70, -1));

        jLabel262.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel262.setText("机器码多开；");
        jPanel21.add(jLabel262, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, -1, 30));
        jPanel21.add(IP多开数量, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 40, 70, -1));

        jLabel267.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel267.setText("IP地址多开；");
        jPanel21.add(jLabel267, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, -1, 30));

        修改骑士团等级上限2.setText("修改");
        修改骑士团等级上限2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                修改骑士团等级上限2ActionPerformed(evt);
            }
        });
        jPanel21.add(修改骑士团等级上限2, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 40, 70, -1));

        jPanel17.setBackground(new java.awt.Color(255, 255, 255));
        jPanel17.setBorder(javax.swing.BorderFactory.createTitledBorder("其它配置"));

        jLabel329.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        jLabel329.setText("物品掉落持续时间");

        物品掉落持续时间.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        物品掉落持续时间.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                物品掉落持续时间ActionPerformed(evt);
            }
        });

        修改物品掉落持续时间.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        修改物品掉落持续时间.setText("修改确认");
        修改物品掉落持续时间.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                修改物品掉落持续时间ActionPerformed(evt);
            }
        });

        jLabel319.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        jLabel319.setText("地图物品最多数量");

        地图物品上限.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N

        修改物品掉落持续时间1.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        修改物品掉落持续时间1.setText("修改确认");
        修改物品掉落持续时间1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                修改物品掉落持续时间1ActionPerformed(evt);
            }
        });

        jLabel330.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        jLabel330.setText("地图刷新频率时间");

        地图刷新频率.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N

        修改物品掉落持续时间2.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        修改物品掉落持续时间2.setText("修改确认");
        修改物品掉落持续时间2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                修改物品掉落持续时间2ActionPerformed(evt);
            }
        });

        商城扩充价格修改.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        商城扩充价格修改.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                商城扩充价格修改ActionPerformed(evt);
            }
        });

        修改背包扩充价格.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        修改背包扩充价格.setText("修改确认");
        修改背包扩充价格.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                修改背包扩充价格ActionPerformed(evt);
            }
        });

        jLabel331.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        jLabel331.setText("商城扩充背包价格");

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addComponent(jLabel329)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(物品掉落持续时间, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(修改物品掉落持续时间, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                        .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel17Layout.createSequentialGroup()
                                .addComponent(jLabel319)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                                .addComponent(jLabel330, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(商城扩充价格修改, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                                        .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(地图物品上限, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(地图刷新频率, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))))
                        .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(修改物品掉落持续时间1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(修改物品掉落持续时间2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(修改背包扩充价格, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(10, 10, 10))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                .addComponent(jLabel331, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(212, 212, 212))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(物品掉落持续时间, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(修改物品掉落持续时间, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel329))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel319)
                    .addComponent(地图物品上限, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(修改物品掉落持续时间1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(修改物品掉落持续时间2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(地图刷新频率, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel330)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(商城扩充价格修改, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(修改背包扩充价格, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel331))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel7.setBackground(new java.awt.Color(250, 250, 250));
        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "职业开关", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 18))); // NOI18N
        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        冒险家职业开关.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        冒险家职业开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF2.png"))); // NOI18N
        冒险家职业开关.setText("冒险家");
        冒险家职业开关.setToolTipText("<html>\n<strong><font color=\"#FF0000\">开启:</font></strong><br> \n开启后玩家可以创建冒险家职业。<br> \n<strong><font color=\"#FF0000\">关闭:</font></strong><br> \n关闭后玩家不能创建冒险家职业。<br> <br>  \n");
        冒险家职业开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                冒险家职业开关ActionPerformed(evt);
            }
        });
        jPanel7.add(冒险家职业开关, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 40, 170, 40));

        战神职业开关.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        战神职业开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF2.png"))); // NOI18N
        战神职业开关.setText("战神");
        战神职业开关.setToolTipText("<html>\n<strong><font color=\"#FF0000\">开启:</font></strong><br> \n开启后玩家可以创建战神职业。<br> \n<strong><font color=\"#FF0000\">关闭:</font></strong><br> \n关闭后玩家不能创建战神职业。<br> <br>  ");
        战神职业开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                战神职业开关ActionPerformed(evt);
            }
        });
        jPanel7.add(战神职业开关, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 40, 180, 40));

        骑士团职业开关.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        骑士团职业开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF2.png"))); // NOI18N
        骑士团职业开关.setText("骑士团");
        骑士团职业开关.setToolTipText("<html>\n<strong><font color=\"#FF0000\">开启:</font></strong><br> \n开启后玩家可以创建骑士团职业。<br> \n<strong><font color=\"#FF0000\">关闭:</font></strong><br> \n关闭后玩家不能创建骑士团职业。<br> <br>  ");
        骑士团职业开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                骑士团职业开关ActionPerformed(evt);
            }
        });
        jPanel7.add(骑士团职业开关, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 40, 190, 40));

        jPanel71.setBackground(new java.awt.Color(250, 250, 250));
        jPanel71.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "其他开关", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 18))); // NOI18N
        jPanel71.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        幸运职业开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF2.png"))); // NOI18N
        幸运职业开关.setText("幸运职业");
        幸运职业开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                幸运职业开关ActionPerformed(evt);
            }
        });
        jPanel71.add(幸运职业开关, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, -1, 38));

        神秘商人开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF2.png"))); // NOI18N
        神秘商人开关.setText("神秘商人");
        神秘商人开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                神秘商人开关ActionPerformed(evt);
            }
        });
        jPanel71.add(神秘商人开关, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 40, -1, 37));

        魔族突袭开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF2.png"))); // NOI18N
        魔族突袭开关.setText("魔族袭击");
        魔族突袭开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                魔族突袭开关ActionPerformed(evt);
            }
        });
        jPanel71.add(魔族突袭开关, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 40, -1, 37));

        魔族攻城开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/OFF2.png"))); // NOI18N
        魔族攻城开关.setText("魔族攻城");
        魔族攻城开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                魔族攻城开关ActionPerformed(evt);
            }
        });
        jPanel71.add(魔族攻城开关, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 40, -1, 38));

        jPanel22.setBackground(new java.awt.Color(250, 250, 250));
        jPanel22.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "等级上限", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 18))); // NOI18N
        jPanel22.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jPanel22.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTextMaxLevel.setText("250");
        jTextMaxLevel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextMaxLevelActionPerformed(evt);
            }
        });
        jPanel22.add(jTextMaxLevel, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 40, 80, 40));

        修改冒险家等级上限.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        修改冒险家等级上限.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/3801511.png"))); // NOI18N
        修改冒险家等级上限.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                修改冒险家等级上限ActionPerformed(evt);
            }
        });
        jPanel22.add(修改冒险家等级上限, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 40, 70, 40));

        jLabel253.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        jLabel253.setText("冒险家等级上限");
        jPanel22.add(jLabel253, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 30, -1, 60));

        骑士团等级上限.setText("250");
        骑士团等级上限.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                骑士团等级上限ActionPerformed(evt);
            }
        });
        jPanel22.add(骑士团等级上限, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 40, 90, 40));

        jLabel252.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        jLabel252.setText("骑士团等级上限");
        jPanel22.add(jLabel252, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, -1, 60));

        修改骑士团等级上限.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        修改骑士团等级上限.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/3801511.png"))); // NOI18N
        修改骑士团等级上限.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                修改骑士团等级上限ActionPerformed(evt);
            }
        });
        jPanel22.add(修改骑士团等级上限, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 40, 70, 40));

        jTextFieldMaxCharacterNumber.setText("3");
        jTextFieldMaxCharacterNumber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldMaxCharacterNumberActionPerformed(evt);
            }
        });
        jPanel22.add(jTextFieldMaxCharacterNumber, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 40, 80, 40));

        jLabel19.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        jLabel19.setText("最大创建角色个数");
        jPanel22.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 50, 130, 20));

        jButtonMaxCharacter.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jButtonMaxCharacter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/3801511.png"))); // NOI18N
        jButtonMaxCharacter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMaxCharacterActionPerformed(evt);
            }
        });
        jPanel22.add(jButtonMaxCharacter, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 40, 70, 40));

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel71, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel22, javax.swing.GroupLayout.DEFAULT_SIZE, 894, Short.MAX_VALUE))
                .addGap(56, 56, 56))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jPanel71, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(82, Short.MAX_VALUE))
        );

        功能设置.addTab("其它功能配置", new javax.swing.ImageIcon(getClass().getResource("/image2/4030001.png")), jPanel12); // NOI18N

        jPanel66.setBackground(new java.awt.Color(255, 255, 255));
        jPanel66.setBorder(javax.swing.BorderFactory.createTitledBorder("活动经验"));

        jPanel69.setBackground(new java.awt.Color(255, 255, 255));
        jPanel69.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "2倍率活动", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 24))); // NOI18N
        jPanel69.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        开启双倍经验.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        开启双倍经验.setText("开启双倍经验");
        开启双倍经验.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                开启双倍经验ActionPerformed(evt);
            }
        });
        jPanel69.add(开启双倍经验, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 70, 140, 40));
        jPanel69.add(双倍经验持续时间, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 80, 120, -1));

        jLabel359.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel359.setText("持续时间/h；");
        jPanel69.add(jLabel359, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, -1, 20));

        开启双倍爆率.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        开启双倍爆率.setText("开启双倍爆率");
        开启双倍爆率.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                开启双倍爆率ActionPerformed(evt);
            }
        });
        jPanel69.add(开启双倍爆率, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 150, 140, 40));
        jPanel69.add(双倍爆率持续时间, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 160, 120, -1));

        jLabel360.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel360.setText("持续时间/h；");
        jPanel69.add(jLabel360, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 140, -1, 20));

        开启双倍金币.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        开启双倍金币.setText("开启双倍金币");
        开启双倍金币.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                开启双倍金币ActionPerformed(evt);
            }
        });
        jPanel69.add(开启双倍金币, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 230, 140, 40));
        jPanel69.add(双倍金币持续时间, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 240, 120, -1));

        jLabel361.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel361.setText("持续时间/h；");
        jPanel69.add(jLabel361, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 220, -1, 20));

        jPanel70.setBackground(new java.awt.Color(255, 255, 255));
        jPanel70.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "3倍率活动", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 24))); // NOI18N
        jPanel70.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        开启三倍经验.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        开启三倍经验.setText("开启三倍经验");
        开启三倍经验.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                开启三倍经验ActionPerformed(evt);
            }
        });
        jPanel70.add(开启三倍经验, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 70, 140, 40));
        jPanel70.add(三倍经验持续时间, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 80, 120, -1));

        jLabel362.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel362.setText("持续时间/h；");
        jPanel70.add(jLabel362, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, -1, 20));

        开启三倍爆率.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        开启三倍爆率.setText("开启三倍爆率");
        开启三倍爆率.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                开启三倍爆率ActionPerformed(evt);
            }
        });
        jPanel70.add(开启三倍爆率, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 150, 140, 40));
        jPanel70.add(三倍爆率持续时间, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 160, 120, -1));

        jLabel348.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel348.setText("持续时间/h；");
        jPanel70.add(jLabel348, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 140, -1, 20));

        开启三倍金币.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        开启三倍金币.setText("开启三倍金币");
        开启三倍金币.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                开启三倍金币ActionPerformed(evt);
            }
        });
        jPanel70.add(开启三倍金币, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 230, 140, 40));
        jPanel70.add(三倍金币持续时间, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 240, 120, -1));

        jLabel349.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel349.setText("持续时间/h；");
        jPanel70.add(jLabel349, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 220, -1, 20));

        jLabel3.setFont(new java.awt.Font("宋体", 1, 36)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 51, 51));
        jLabel3.setText("功能说明：本功能无需重启服务端立即生效");

        jLabel7.setFont(new java.awt.Font("宋体", 1, 36)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 0, 51));
        jLabel7.setText("单位换算 h=小时 时间到期自动解除倍率");

        jPanel62.setBackground(new java.awt.Color(255, 255, 255));
        jPanel62.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "不限时倍率设置", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("宋体", 0, 21))); // NOI18N

        jLabel42.setText("经验");

        经验确认.setFont(new java.awt.Font("宋体", 0, 24)); // NOI18N
        经验确认.setText("确认");
        经验确认.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                经验确认ActionPerformed(evt);
            }
        });

        物品.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                物品ActionPerformed(evt);
            }
        });

        物品确认.setFont(new java.awt.Font("宋体", 0, 24)); // NOI18N
        物品确认.setText("确认");
        物品确认.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                物品确认ActionPerformed(evt);
            }
        });

        金币确认.setFont(new java.awt.Font("宋体", 0, 24)); // NOI18N
        金币确认.setText("确认");
        金币确认.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                金币确认ActionPerformed(evt);
            }
        });

        jLabel43.setText("爆率");

        jLabel67.setText("金币");

        jLabel68.setBackground(new java.awt.Color(255, 255, 255));
        jLabel68.setFont(new java.awt.Font("宋体", 0, 14)); // NOI18N
        jLabel68.setForeground(new java.awt.Color(255, 0, 153));
        jLabel68.setText("重启服务端恢复默认配置");

        javax.swing.GroupLayout jPanel62Layout = new javax.swing.GroupLayout(jPanel62);
        jPanel62.setLayout(jPanel62Layout);
        jPanel62Layout.setHorizontalGroup(
            jPanel62Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel62Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel62Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel62Layout.createSequentialGroup()
                        .addGroup(jPanel62Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel68, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel62Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel62Layout.createSequentialGroup()
                                    .addComponent(jLabel42)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(经验, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(经验确认))
                                .addGroup(jPanel62Layout.createSequentialGroup()
                                    .addComponent(jLabel67)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(金币, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(金币确认))))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel62Layout.createSequentialGroup()
                        .addComponent(jLabel43)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(物品, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(物品确认)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel62Layout.setVerticalGroup(
            jPanel62Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel62Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(jPanel62Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                    .addComponent(经验确认, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(经验)
                    .addComponent(jLabel42))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel62Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(物品, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(物品确认)
                    .addComponent(jLabel43))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel62Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel67)
                    .addComponent(金币, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(金币确认))
                .addGap(30, 30, 30)
                .addComponent(jLabel68)
                .addGap(68, 68, 68))
        );

        javax.swing.GroupLayout jPanel66Layout = new javax.swing.GroupLayout(jPanel66);
        jPanel66.setLayout(jPanel66Layout);
        jPanel66Layout.setHorizontalGroup(
            jPanel66Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel66Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(jPanel66Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 732, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(156, Short.MAX_VALUE))
            .addGroup(jPanel66Layout.createSequentialGroup()
                .addComponent(jPanel69, javax.swing.GroupLayout.PREFERRED_SIZE, 308, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel70, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel62, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel66Layout.setVerticalGroup(
            jPanel66Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel66Layout.createSequentialGroup()
                .addGroup(jPanel66Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel70, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel69, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel62, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addGap(159, 159, 159))
        );

        功能设置.addTab("活动经验设置", new javax.swing.ImageIcon(getClass().getResource("/image2/经验1.png")), jPanel66); // NOI18N

        jPanel93.setBackground(new java.awt.Color(255, 255, 255));
        jPanel93.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "游戏经验加成", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 24))); // NOI18N
        jPanel93.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPane136.setFont(new java.awt.Font("宋体", 0, 14)); // NOI18N

        经验加成表.setFont(new java.awt.Font("幼圆", 0, 20)); // NOI18N
        经验加成表.setForeground(new java.awt.Color(102, 102, 255));
        经验加成表.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "经验加成序号", "经验加成类型", "经验加成数值百分比设置0为关闭"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        经验加成表.setAutoscrolls(false);
        经验加成表.setGridColor(new java.awt.Color(204, 204, 204));
        经验加成表.setName(""); // NOI18N
        经验加成表.setRequestFocusEnabled(false);
        经验加成表.setRowHeight(18);
        经验加成表.setRowSelectionAllowed(false);
        经验加成表.getTableHeader().setReorderingAllowed(false);
        jScrollPane136.setViewportView(经验加成表);

        jPanel93.add(jScrollPane136, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 70, 630, 390));

        经验加成表序号.setEditable(false);
        经验加成表序号.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                经验加成表序号ActionPerformed(evt);
            }
        });
        jPanel93.add(经验加成表序号, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 480, 70, 30));

        经验加成表类型.setEditable(false);
        jPanel93.add(经验加成表类型, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 480, 230, 30));

        经验加成表数值.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                经验加成表数值ActionPerformed(evt);
            }
        });
        jPanel93.add(经验加成表数值, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 480, 100, 30));

        经验加成表修改.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        经验加成表修改.setText("修改");
        经验加成表修改.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                经验加成表修改ActionPerformed(evt);
            }
        });
        jPanel93.add(经验加成表修改, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 480, 100, 30));

        jLabel384.setFont(new java.awt.Font("幼圆", 0, 18)); // NOI18N
        jLabel384.setText("数值；");
        jPanel93.add(jLabel384, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 460, -1, -1));

        jLabel385.setFont(new java.awt.Font("幼圆", 0, 18)); // NOI18N
        jLabel385.setText("类型；");
        jPanel93.add(jLabel385, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 460, -1, -1));

        jLabel386.setFont(new java.awt.Font("幼圆", 0, 18)); // NOI18N
        jLabel386.setText("序号；");
        jPanel93.add(jLabel386, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 460, -1, -1));

        游戏经验加成说明.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        游戏经验加成说明.setText("说明");
        游戏经验加成说明.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                游戏经验加成说明ActionPerformed(evt);
            }
        });
        jPanel93.add(游戏经验加成说明, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 480, 100, 30));

        功能设置.addTab("经验加成配置", new javax.swing.ImageIcon(getClass().getResource("/image2/2435108.png")), jPanel93); // NOI18N

        jPanel23.setBackground(new java.awt.Color(255, 255, 255));
        jPanel23.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "高级配置 [所有功能重启服务端恢复默认配置]", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 18))); // NOI18N
        jPanel23.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel23.add(怪物倍率, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 150, 70, 30));

        修改怪物倍率.setText("修改");
        修改怪物倍率.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                修改怪物倍率ActionPerformed(evt);
            }
        });
        jPanel23.add(修改怪物倍率, new org.netbeans.lib.awtextra.AbsoluteConstraints(810, 150, 100, 30));

        jLabel263.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel263.setText("已添加倍怪地图；");
        jPanel23.add(jLabel263, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 20, -1, 30));

        倍怪地图.setColumns(20);
        倍怪地图.setLineWrap(true);
        倍怪地图.setRows(5);
        倍怪地图.setToolTipText("");
        jScrollPane6.setViewportView(倍怪地图);

        jPanel23.add(jScrollPane6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 900, 90));

        jLabel264.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel264.setText("怪物倍率；");
        jPanel23.add(jLabel264, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 150, -1, 30));

        jPanel63.setBackground(new java.awt.Color(255, 255, 255));
        jPanel63.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "等级区间倍率设置", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("宋体", 0, 21))); // NOI18N

        经验确认1.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
        经验确认1.setText("确认修改区间一");
        经验确认1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                经验确认1ActionPerformed(evt);
            }
        });

        jLabel1.setText("--");

        jLabel4.setText("级");

        jLabel5.setText("倍经验");

        jLabel6.setText("--");

        jLabel15.setText("级");

        jLabel16.setText("倍经验");

        经验确认2.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
        经验确认2.setText("确认修改区间二");
        经验确认2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                经验确认2ActionPerformed(evt);
            }
        });

        jLabel17.setText("--");

        jLabel18.setText("级");

        jLabel20.setText("倍经验");

        经验确认3.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
        经验确认3.setText("确认修改区间三");
        经验确认3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                经验确认3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel63Layout = new javax.swing.GroupLayout(jPanel63);
        jPanel63.setLayout(jPanel63Layout);
        jPanel63Layout.setHorizontalGroup(
            jPanel63Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel63Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(jPanel63Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(经验确认1, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel63Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addGroup(jPanel63Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel63Layout.createSequentialGroup()
                                .addComponent(区间一最低等级, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(区间一最高等级, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4))
                            .addGroup(jPanel63Layout.createSequentialGroup()
                                .addComponent(区间一经验倍率, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel5)))))
                .addGroup(jPanel63Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel63Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel63Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel63Layout.createSequentialGroup()
                                .addComponent(区间二最低等级, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(区间二最高等级, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel15))
                            .addGroup(jPanel63Layout.createSequentialGroup()
                                .addComponent(区间二经验倍率, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel16)))
                        .addGap(213, 213, 213)
                        .addGroup(jPanel63Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel63Layout.createSequentialGroup()
                                .addComponent(区间三最低等级, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(区间三最高等级, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel18))
                            .addGroup(jPanel63Layout.createSequentialGroup()
                                .addComponent(区间三经验倍率, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel20)))
                        .addGap(77, 77, 77))
                    .addGroup(jPanel63Layout.createSequentialGroup()
                        .addGap(467, 467, 467)
                        .addComponent(经验确认3, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(53, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel63Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(经验确认2, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(366, 366, 366))
        );
        jPanel63Layout.setVerticalGroup(
            jPanel63Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel63Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel63Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel63Layout.createSequentialGroup()
                        .addGroup(jPanel63Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(区间一最低等级)
                            .addComponent(区间一最高等级)
                            .addComponent(jLabel4))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel63Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(区间一经验倍率)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(经验确认1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel63Layout.createSequentialGroup()
                        .addGroup(jPanel63Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(区间二最低等级)
                            .addComponent(区间二最高等级)
                            .addComponent(jLabel15))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel63Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(区间二经验倍率)
                            .addComponent(jLabel16))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(经验确认2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4))
                    .addGroup(jPanel63Layout.createSequentialGroup()
                        .addGroup(jPanel63Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17)
                            .addComponent(区间三最低等级)
                            .addComponent(区间三最高等级)
                            .addComponent(jLabel18))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel63Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(区间三经验倍率)
                            .addComponent(jLabel20))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(经验确认3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(151, 151, 151))
        );

        jPanel23.add(jPanel63, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 180, 910, 180));

        jPanel64.setBackground(new java.awt.Color(255, 255, 255));
        jPanel64.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "弓标子弹叠加上限突破", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("宋体", 0, 21))); // NOI18N

        jLabel268.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jLabel268.setText("所有物品叠加数量");

        修改物品叠加数量.setText("修改确认");
        修改物品叠加数量.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                修改物品叠加数量ActionPerformed(evt);
            }
        });

        弓标子弹叠加上限突破.setColumns(20);
        弓标子弹叠加上限突破.setLineWrap(true);
        弓标子弹叠加上限突破.setRows(5);
        弓标子弹叠加上限突破.setToolTipText("");
        jScrollPane11.setViewportView(弓标子弹叠加上限突破);

        jLabel21.setText("需要突破叠加弓、标、子弹代码：");

        javax.swing.GroupLayout jPanel64Layout = new javax.swing.GroupLayout(jPanel64);
        jPanel64.setLayout(jPanel64Layout);
        jPanel64Layout.setHorizontalGroup(
            jPanel64Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel64Layout.createSequentialGroup()
                .addGroup(jPanel64Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane11)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel64Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel268)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(物品叠加数量, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(修改物品叠加数量, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(jPanel64Layout.createSequentialGroup()
                .addGap(359, 359, 359)
                .addComponent(jLabel21)
                .addContainerGap(359, Short.MAX_VALUE))
        );
        jPanel64Layout.setVerticalGroup(
            jPanel64Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel64Layout.createSequentialGroup()
                .addComponent(jLabel21)
                .addGap(7, 7, 7)
                .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel64Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(修改物品叠加数量, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(物品叠加数量, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel268))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel23.add(jPanel64, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 360, 910, 170));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, 539, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(46, Short.MAX_VALUE))
        );

        功能设置.addTab("高级功能配置", new javax.swing.ImageIcon(getClass().getResource("/image2/怪物血量.png")), jPanel1); // NOI18N

        主窗口.addTab("功能设置", new javax.swing.ImageIcon(getClass().getResource("/image2/3800871.png")), 功能设置); // NOI18N

        福利中心.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTabbedPane7.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        jPanel59.setBackground(new java.awt.Color(255, 255, 255));
        jPanel59.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "全服发送福利", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 12))); // NOI18N
        jPanel59.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        z2.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        z2.setText("发送抵用");
        z2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                z2ActionPerformed(evt);
            }
        });
        jPanel59.add(z2, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 120, 100, 30));

        z3.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        z3.setText("发送金币");
        z3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                z3ActionPerformed(evt);
            }
        });
        jPanel59.add(z3, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 170, 100, 30));

        z1.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        z1.setText("发送点券");
        z1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                z1ActionPerformed(evt);
            }
        });
        jPanel59.add(z1, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 70, 100, 30));

        z4.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        z4.setText("发送经验");
        z4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                z4ActionPerformed(evt);
            }
        });
        jPanel59.add(z4, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 70, 100, 30));

        z5.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        z5.setText("发送人气");
        z5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                z5ActionPerformed(evt);
            }
        });
        jPanel59.add(z5, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 120, 100, 30));

        z6.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        z6.setText("发送豆豆");
        z6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                z6ActionPerformed(evt);
            }
        });
        jPanel59.add(z6, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 170, 100, 30));

        a1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                a1ActionPerformed(evt);
            }
        });
        jPanel59.add(a1, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 20, 100, 30));

        jLabel235.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel235.setText("数量：");
        jPanel59.add(jLabel235, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 30, -1, -1));

        jPanel57.setBackground(new java.awt.Color(255, 255, 255));
        jPanel57.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "全服发送福利", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 12))); // NOI18N
        jPanel57.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        全服发送物品数量.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                全服发送物品数量ActionPerformed(evt);
            }
        });
        jPanel57.add(全服发送物品数量, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 40, 100, 30));

        全服发送物品代码.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                全服发送物品代码ActionPerformed(evt);
            }
        });
        jPanel57.add(全服发送物品代码, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, 110, 30));

        给予物品1.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        给予物品1.setText("给予物品");
        给予物品1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                给予物品1ActionPerformed(evt);
            }
        });
        jPanel57.add(给予物品1, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 40, 100, 30));

        jLabel217.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel217.setText("物品数量；");
        jPanel57.add(jLabel217, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 20, -1, -1));

        jLabel234.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel234.setText("物品代码；");
        jPanel57.add(jLabel234, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));

        jPanel58.setBackground(new java.awt.Color(255, 255, 255));
        jPanel58.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "全服发送福利", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 12))); // NOI18N
        jPanel58.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        全服发送装备装备加卷.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                全服发送装备装备加卷ActionPerformed(evt);
            }
        });
        jPanel58.add(全服发送装备装备加卷, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 100, 100, 30));

        全服发送装备装备制作人.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                全服发送装备装备制作人ActionPerformed(evt);
            }
        });
        jPanel58.add(全服发送装备装备制作人, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 40, 100, 30));

        全服发送装备装备力量.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                全服发送装备装备力量ActionPerformed(evt);
            }
        });
        jPanel58.add(全服发送装备装备力量, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, 100, 30));

        全服发送装备装备MP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                全服发送装备装备MPActionPerformed(evt);
            }
        });
        jPanel58.add(全服发送装备装备MP, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, 100, 30));

        全服发送装备装备智力.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                全服发送装备装备智力ActionPerformed(evt);
            }
        });
        jPanel58.add(全服发送装备装备智力, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 170, 100, 30));

        全服发送装备装备运气.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                全服发送装备装备运气ActionPerformed(evt);
            }
        });
        jPanel58.add(全服发送装备装备运气, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 100, 100, 30));

        全服发送装备装备HP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                全服发送装备装备HPActionPerformed(evt);
            }
        });
        jPanel58.add(全服发送装备装备HP, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 100, 100, 30));

        全服发送装备装备攻击力.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                全服发送装备装备攻击力ActionPerformed(evt);
            }
        });
        jPanel58.add(全服发送装备装备攻击力, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 40, 100, 30));

        全服发送装备装备给予时间.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                全服发送装备装备给予时间ActionPerformed(evt);
            }
        });
        jPanel58.add(全服发送装备装备给予时间, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 230, 100, 30));

        全服发送装备装备可否交易.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                全服发送装备装备可否交易ActionPerformed(evt);
            }
        });
        jPanel58.add(全服发送装备装备可否交易, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 170, 100, 30));

        全服发送装备装备敏捷.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                全服发送装备装备敏捷ActionPerformed(evt);
            }
        });
        jPanel58.add(全服发送装备装备敏捷, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 170, 100, 30));

        全服发送装备物品ID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                全服发送装备物品IDActionPerformed(evt);
            }
        });
        jPanel58.add(全服发送装备物品ID, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 100, 30));

        全服发送装备装备魔法力.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                全服发送装备装备魔法力ActionPerformed(evt);
            }
        });
        jPanel58.add(全服发送装备装备魔法力, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 40, 100, 30));

        全服发送装备装备魔法防御.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                全服发送装备装备魔法防御ActionPerformed(evt);
            }
        });
        jPanel58.add(全服发送装备装备魔法防御, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 230, 100, 30));

        全服发送装备装备物理防御.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                全服发送装备装备物理防御ActionPerformed(evt);
            }
        });
        jPanel58.add(全服发送装备装备物理防御, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 230, 100, 30));

        给予装备1.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        给予装备1.setText("个人发送");
        给予装备1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                给予装备1ActionPerformed(evt);
            }
        });
        jPanel58.add(给予装备1, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 170, 100, 30));

        jLabel219.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel219.setText("能否交易->0交易 1上锁");
        jPanel58.add(jLabel219, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 150, -1, -1));

        jLabel220.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel220.setText("HP加成；");
        jPanel58.add(jLabel220, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 80, -1, -1));

        jLabel221.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel221.setText("魔法攻击力；");
        jPanel58.add(jLabel221, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 20, -1, -1));

        jLabel222.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel222.setText("装备代码；");
        jPanel58.add(jLabel222, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, -1));

        jLabel223.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel223.setText("MP加成；");
        jPanel58.add(jLabel223, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, -1, -1));

        jLabel224.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel224.setText("物理攻击力；");
        jPanel58.add(jLabel224, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 20, -1, -1));

        jLabel225.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel225.setText("可砸卷次数；");
        jPanel58.add(jLabel225, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 80, -1, -1));

        jLabel226.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel226.setText("装备署名；");
        jPanel58.add(jLabel226, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 20, -1, -1));

        jLabel227.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel227.setText("装备力量；");
        jPanel58.add(jLabel227, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, -1, -1));

        jLabel228.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel228.setText("装备敏捷；");
        jPanel58.add(jLabel228, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 150, -1, -1));

        jLabel229.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel229.setText("装备智力；");
        jPanel58.add(jLabel229, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 150, -1, -1));

        jLabel230.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel230.setText("装备运气；");
        jPanel58.add(jLabel230, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 80, -1, -1));

        jLabel231.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel231.setText("魔法防御；");
        jPanel58.add(jLabel231, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 210, -1, -1));

        jLabel232.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel232.setText("物理防御；");
        jPanel58.add(jLabel232, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 210, -1, -1));

        jLabel233.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel233.setText("限时时间；");
        jPanel58.add(jLabel233, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 210, -1, -1));

        发送装备玩家姓名.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                发送装备玩家姓名ActionPerformed(evt);
            }
        });
        jPanel58.add(发送装备玩家姓名, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 40, 100, 30));

        给予装备2.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        给予装备2.setText("全服发送");
        给予装备2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                给予装备2ActionPerformed(evt);
            }
        });
        jPanel58.add(给予装备2, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 100, 100, 30));

        jLabel246.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel246.setText("玩家名字；");
        jPanel58.add(jLabel246, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 20, -1, -1));

        jLabel244.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel244.setText("个人发送需要填写名字");
        jPanel58.add(jLabel244, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 230, -1, -1));

        jPanel60.setBackground(new java.awt.Color(255, 255, 255));
        jPanel60.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "个人发送物品", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 12))); // NOI18N
        jPanel60.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        个人发送物品数量.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                个人发送物品数量ActionPerformed(evt);
            }
        });
        jPanel60.add(个人发送物品数量, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 40, 80, 30));

        个人发送物品玩家名字.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                个人发送物品玩家名字ActionPerformed(evt);
            }
        });
        jPanel60.add(个人发送物品玩家名字, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 130, 30));

        个人发送物品代码.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                个人发送物品代码ActionPerformed(evt);
            }
        });
        jPanel60.add(个人发送物品代码, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 40, 130, 30));

        给予物品.setBackground(new java.awt.Color(255, 255, 255));
        给予物品.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        给予物品.setText("给予物品");
        给予物品.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                给予物品ActionPerformed(evt);
            }
        });
        jPanel60.add(给予物品, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 40, 100, 30));

        jLabel240.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel240.setText("物品数量；");
        jPanel60.add(jLabel240, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 20, -1, -1));

        jLabel241.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel241.setText("玩家名字；");
        jPanel60.add(jLabel241, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, -1));

        jLabel242.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel242.setText("物品代码；");
        jPanel60.add(jLabel242, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 20, -1, -1));

        jPanel80.setBackground(new java.awt.Color(255, 255, 255));
        jPanel80.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "个人发送福利", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 12))); // NOI18N
        jPanel80.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        z7.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        z7.setText("发送抵用");
        z7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                z7ActionPerformed(evt);
            }
        });
        jPanel80.add(z7, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 150, 100, 30));

        z8.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        z8.setText("发送金币");
        z8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                z8ActionPerformed(evt);
            }
        });
        jPanel80.add(z8, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 200, 100, 30));

        z9.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        z9.setText("发送点券");
        z9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                z9ActionPerformed(evt);
            }
        });
        jPanel80.add(z9, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 100, 100, 30));

        z10.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        z10.setText("发送经验");
        z10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                z10ActionPerformed(evt);
            }
        });
        jPanel80.add(z10, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 100, 100, 30));

        z11.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        z11.setText("发送人气");
        z11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                z11ActionPerformed(evt);
            }
        });
        jPanel80.add(z11, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 150, 100, 30));

        z12.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        z12.setText("发送豆豆");
        z12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                z12ActionPerformed(evt);
            }
        });
        jPanel80.add(z12, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 200, 100, 30));

        a2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                a2ActionPerformed(evt);
            }
        });
        jPanel80.add(a2, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 40, 100, 30));

        jLabel236.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel236.setText("数量；");
        jPanel80.add(jLabel236, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, -1, -1));

        个人发送物品玩家名字1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                个人发送物品玩家名字1ActionPerformed(evt);
            }
        });
        jPanel80.add(个人发送物品玩家名字1, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 40, 80, 30));

        jLabel64.setFont(new java.awt.Font("宋体", 0, 14)); // NOI18N
        jLabel64.setText("玩家名字:");
        jPanel80.add(jLabel64, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 50, -1, -1));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jPanel58, javax.swing.GroupLayout.PREFERRED_SIZE, 522, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jPanel60, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 528, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel57, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 530, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel80, javax.swing.GroupLayout.DEFAULT_SIZE, 329, Short.MAX_VALUE)
                    .addComponent(jPanel59, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jPanel57, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel60, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel59, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel80, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
                    .addComponent(jPanel58, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(112, 112, 112))
        );

        jTabbedPane7.addTab("福利道具发送", new javax.swing.ImageIcon(getClass().getResource("/image2/3010025.png")), jPanel4); // NOI18N

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "玩家在线泡点", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 24))); // NOI18N
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        在线泡点设置.setFont(new java.awt.Font("幼圆", 0, 20)); // NOI18N
        在线泡点设置.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "序号", "类型", "数值"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        在线泡点设置.getTableHeader().setReorderingAllowed(false);
        jScrollPane134.setViewportView(在线泡点设置);

        jPanel9.add(jScrollPane134, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 470, 260));

        泡点序号.setEditable(false);
        jPanel9.add(泡点序号, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 380, 70, 30));

        泡点类型.setEditable(false);
        jPanel9.add(泡点类型, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 380, 110, 30));
        jPanel9.add(泡点值, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 380, 120, 30));

        泡点值修改.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        泡点值修改.setText("修改");
        泡点值修改.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                泡点值修改ActionPerformed(evt);
            }
        });
        jPanel9.add(泡点值修改, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 380, 80, 30));

        jLabel322.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel322.setText("类型数值：");
        jPanel9.add(jLabel322, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 360, -1, -1));

        jLabel326.setFont(new java.awt.Font("幼圆", 0, 18)); // NOI18N
        jLabel326.setForeground(new java.awt.Color(255, 0, 153));
        jLabel326.setText("提示：修改泡点时间需30分钟生效,其它设置即时生效。");
        jPanel9.add(jLabel326, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 310, -1, -1));

        jLabel327.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel327.setText("泡点奖励类型：");
        jPanel9.add(jLabel327, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 360, -1, -1));

        jPanel75.setBackground(new java.awt.Color(255, 255, 255));
        jPanel75.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "在线泡点设置", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 24))); // NOI18N

        泡点金币开关.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        泡点金币开关.setText("泡点金币");
        泡点金币开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                泡点金币开关ActionPerformed(evt);
            }
        });

        泡点经验开关.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        泡点经验开关.setText("泡点经验");
        泡点经验开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                泡点经验开关ActionPerformed(evt);
            }
        });

        泡点点券开关.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        泡点点券开关.setText("泡点点券");
        泡点点券开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                泡点点券开关ActionPerformed(evt);
            }
        });

        泡点抵用开关.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        泡点抵用开关.setText("泡点抵用");
        泡点抵用开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                泡点抵用开关ActionPerformed(evt);
            }
        });

        泡点豆豆开关.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        泡点豆豆开关.setText("泡点豆豆");
        泡点豆豆开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                泡点豆豆开关ActionPerformed(evt);
            }
        });

        jLabel65.setForeground(new java.awt.Color(255, 0, 153));
        jLabel65.setText("提示：在线泡点开关，无需重启服务端，即时生效");

        javax.swing.GroupLayout jPanel75Layout = new javax.swing.GroupLayout(jPanel75);
        jPanel75.setLayout(jPanel75Layout);
        jPanel75Layout.setHorizontalGroup(
            jPanel75Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel75Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel75Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(泡点豆豆开关, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel75Layout.createSequentialGroup()
                        .addComponent(泡点金币开关, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(泡点经验开关, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel75Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(泡点点券开关, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(泡点抵用开关, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel65, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0))
        );
        jPanel75Layout.setVerticalGroup(
            jPanel75Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel75Layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(jPanel75Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(泡点金币开关, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(泡点经验开关, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel75Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(泡点点券开关, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(泡点抵用开关, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addComponent(泡点豆豆开关, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel65, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel9.add(jPanel75, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 30, 360, 270));

        jLabel328.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel328.setText("序号：");
        jPanel9.add(jLabel328, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 360, -1, -1));

        福利提示语言2.setFont(new java.awt.Font("幼圆", 0, 18)); // NOI18N
        福利提示语言2.setText("[信息]：");
        jPanel9.add(福利提示语言2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 520, 800, 25));

        jLabel60.setText("金币==数值乘等级 列如：金币数值10，实际泡点所得金币等于10乘当前等级");
        jPanel9.add(jLabel60, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 430, 510, -1));

        jLabel61.setText("经验==数值乘等级 列如：经验数值10，实际泡点所得经验等于10乘当前等级");
        jPanel9.add(jLabel61, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 460, 500, -1));

        jLabel62.setText("其中：点卷/抵用卷/豆豆 这三个数值都是固定数值，设置10泡点所得就是10");
        jPanel9.add(jLabel62, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 490, 520, -1));

        jTabbedPane7.addTab("福利在线泡点", new javax.swing.ImageIcon(getClass().getResource("/image2/2000001.png")), jPanel9); // NOI18N

        福利中心.add(jTabbedPane7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        主窗口.addTab("福利中心", new javax.swing.ImageIcon(getClass().getResource("/image2/福利.png")), 福利中心); // NOI18N

        游戏公告.setBackground(new java.awt.Color(255, 255, 255));
        游戏公告.setBorder(javax.swing.BorderFactory.createTitledBorder("游戏公告"));
        游戏公告.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        sendNotice.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/喇叭1.png"))); // NOI18N
        sendNotice.setText("蓝色提示公告");
        sendNotice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendNoticeActionPerformed(evt);
            }
        });
        游戏公告.add(sendNotice, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 300, 155, 40));

        sendWinNotice.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/喇叭2.png"))); // NOI18N
        sendWinNotice.setText("顶部滚动公告");
        sendWinNotice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendWinNoticeActionPerformed(evt);
            }
        });
        游戏公告.add(sendWinNotice, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 300, 155, 40));

        sendMsgNotice.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/喇叭3.png"))); // NOI18N
        sendMsgNotice.setText("弹窗公告");
        sendMsgNotice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendMsgNoticeActionPerformed(evt);
            }
        });
        游戏公告.add(sendMsgNotice, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 300, 155, 40));

        sendNpcTalkNotice.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/喇叭4.png"))); // NOI18N
        sendNpcTalkNotice.setText("蓝色公告事项");
        sendNpcTalkNotice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendNpcTalkNoticeActionPerformed(evt);
            }
        });
        游戏公告.add(sendNpcTalkNotice, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 300, 155, 40));

        noticeText.setText("游戏即将维护,请安全下线！造成不便请谅解！");
        游戏公告.add(noticeText, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 35, 853, 203));

        jLabel117.setFont(new java.awt.Font("幼圆", 0, 24)); // NOI18N
        jLabel117.setText("1、不得散布谣言，扰乱社会秩序，破坏社会稳定的信息 ");
        游戏公告.add(jLabel117, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 423, 680, 40));

        jLabel118.setFont(new java.awt.Font("幼圆", 0, 24)); // NOI18N
        jLabel118.setText("2、不得散布赌博、暴力、凶杀、恐怖或者教唆犯罪的信息");
        游戏公告.add(jLabel118, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 463, 680, 40));

        jLabel119.setFont(new java.awt.Font("幼圆", 0, 24)); // NOI18N
        jLabel119.setText("3、不得侮辱或者诽谤他人，侵害他人合法权益");
        游戏公告.add(jLabel119, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 503, 680, 40));

        jLabel106.setFont(new java.awt.Font("幼圆", 0, 24)); // NOI18N
        jLabel106.setText("4、不得含有法律、行政法规禁止的其他内容");
        游戏公告.add(jLabel106, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 543, 680, 40));

        公告发布喇叭代码.setForeground(new java.awt.Color(255, 51, 102));
        公告发布喇叭代码.setText("5120027");
        公告发布喇叭代码.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                公告发布喇叭代码ActionPerformed(evt);
            }
        });
        游戏公告.add(公告发布喇叭代码, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 250, 90, 30));

        jButton45.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jButton45.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/喇叭5.png"))); // NOI18N
        jButton45.setText("屏幕正中公告");
        jButton45.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton45ActionPerformed(evt);
            }
        });
        游戏公告.add(jButton45, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 300, 155, 40));

        jLabel259.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel259.setText("喇叭代码：");
        游戏公告.add(jLabel259, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 260, -1, -1));

        主窗口.addTab("游戏公告", new javax.swing.ImageIcon(getClass().getResource("/image2/2630205.png")), 游戏公告); // NOI18N

        物品爆率.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTabbedPane9.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane9.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N

        jPanel16.setBackground(new java.awt.Color(250, 250, 250));
        jPanel16.setBorder(javax.swing.BorderFactory.createTitledBorder("反应堆/箱子爆率管理"));
        jPanel16.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N

        反应堆.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        反应堆.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "序列号", "反应堆", "物品代码", "概率", "物品名字"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        反应堆.setToolTipText("");
        反应堆.setGridColor(new java.awt.Color(255, 255, 255));
        反应堆.getTableHeader().setReorderingAllowed(false);
        jScrollPane26.setViewportView(反应堆);

        jPanel61.setBackground(new java.awt.Color(255, 255, 255));
        jPanel61.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "反应堆编辑", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 18))); // NOI18N
        jPanel61.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton26.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jButton26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/骰子.png"))); // NOI18N
        jButton26.setText("刷新列表信息");
        jButton26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton26ActionPerformed(evt);
            }
        });
        jPanel61.add(jButton26, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 130, 170, 40));

        反应堆序列号.setEditable(false);
        反应堆序列号.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jPanel61.add(反应堆序列号, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 20, 70, 30));

        反应堆代码.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jPanel61.add(反应堆代码, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 60, 90, 30));

        反应堆物品.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jPanel61.add(反应堆物品, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 100, 90, 30));

        反应堆概率.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        反应堆概率.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                反应堆概率ActionPerformed(evt);
            }
        });
        jPanel61.add(反应堆概率, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 140, 90, 30));

        新增反应堆物品.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        新增反应堆物品.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/新增.png"))); // NOI18N
        新增反应堆物品.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                新增反应堆物品ActionPerformed(evt);
            }
        });
        jPanel61.add(新增反应堆物品, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 60, 80, 30));

        删除反应堆物品1.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        删除反应堆物品1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/删除.png"))); // NOI18N
        删除反应堆物品1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                删除反应堆物品1ActionPerformed(evt);
            }
        });
        jPanel61.add(删除反应堆物品1, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 100, 80, 30));

        查找反应堆掉落.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jPanel61.add(查找反应堆掉落, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 100, 90, 30));

        jButton36.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jButton36.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/查找.png"))); // NOI18N
        jButton36.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton36ActionPerformed(evt);
            }
        });
        jPanel61.add(jButton36, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 100, 80, 30));

        查找物品.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jPanel61.add(查找物品, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 60, 90, 30));

        jButton37.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jButton37.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/查找.png"))); // NOI18N
        jButton37.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton37ActionPerformed(evt);
            }
        });
        jPanel61.add(jButton37, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 60, 80, 30));

        jLabel274.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jLabel274.setText("掉落概率:");
        jPanel61.add(jLabel274, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 140, 70, 30));

        jLabel275.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jLabel275.setText("序号:");
        jPanel61.add(jLabel275, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, -1, 30));

        jLabel277.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jLabel277.setText("物品代码:");
        jPanel61.add(jLabel277, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, 70, 30));

        jLabel278.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jLabel278.setText("反应堆:");
        jPanel61.add(jLabel278, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, 50, 30));

        jLabel279.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jLabel279.setText("反应堆:");
        jPanel61.add(jLabel279, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 100, 60, 30));

        jLabel280.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jLabel280.setText("物品代码:");
        jPanel61.add(jLabel280, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 60, 70, 30));

        修改反应堆物品.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        修改反应堆物品.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/更改.png"))); // NOI18N
        修改反应堆物品.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                修改反应堆物品ActionPerformed(evt);
            }
        });
        jPanel61.add(修改反应堆物品, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 140, 80, 30));

        重载箱子反应堆按钮.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        重载箱子反应堆按钮.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/3800871.png"))); // NOI18N
        重载箱子反应堆按钮.setText("重载箱子反应堆");
        重载箱子反应堆按钮.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                重载箱子反应堆按钮ActionPerformed(evt);
            }
        });
        jPanel61.add(重载箱子反应堆按钮, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 60, 170, 40));

        jLabel282.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jLabel282.setText("删除指定反应堆的所有物品");
        jPanel61.add(jLabel282, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 140, 190, 20));
        jPanel61.add(删除反应堆物品代码, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 140, 90, 30));

        删除反应堆物品.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        删除反应堆物品.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/删除.png"))); // NOI18N
        删除反应堆物品.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                删除反应堆物品ActionPerformed(evt);
            }
        });
        jPanel61.add(删除反应堆物品, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 140, 80, 30));

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel61, javax.swing.GroupLayout.DEFAULT_SIZE, 863, Short.MAX_VALUE)
            .addComponent(jScrollPane26)
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                .addComponent(jScrollPane26, javax.swing.GroupLayout.PREFERRED_SIZE, 374, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel61, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane9.addTab("箱子爆率", jPanel16);

        jPanel28.setBackground(new java.awt.Color(250, 250, 250));
        jPanel28.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "全局爆物/(10000=1%)", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 18))); // NOI18N
        jPanel28.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        世界爆物.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        世界爆物.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "序列号", "物品代码", "爆率", "物品名"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        世界爆物.getTableHeader().setReorderingAllowed(false);
        jScrollPane8.setViewportView(世界爆物);

        jPanel28.add(jScrollPane8, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, 800, 370));

        世界爆物序列号.setEditable(false);
        世界爆物序列号.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        世界爆物序列号.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                世界爆物序列号ActionPerformed(evt);
            }
        });
        jPanel28.add(世界爆物序列号, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 420, 100, -1));

        世界爆物物品代码.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        世界爆物物品代码.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                世界爆物物品代码ActionPerformed(evt);
            }
        });
        jPanel28.add(世界爆物物品代码, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 460, 100, 30));

        世界爆物爆率.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        世界爆物爆率.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                世界爆物爆率ActionPerformed(evt);
            }
        });
        jPanel28.add(世界爆物爆率, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 500, 100, 30));

        添加世界爆物.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        添加世界爆物.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/新增.png"))); // NOI18N
        添加世界爆物.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                添加世界爆物ActionPerformed(evt);
            }
        });
        jPanel28.add(添加世界爆物, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 450, 70, 30));

        删除世界爆物.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        删除世界爆物.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/删除.png"))); // NOI18N
        删除世界爆物.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                删除世界爆物ActionPerformed(evt);
            }
        });
        jPanel28.add(删除世界爆物, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 530, 70, 30));

        jLabel210.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        jLabel210.setText("序列号:");
        jPanel28.add(jLabel210, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 420, 50, 30));

        jLabel202.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        jLabel202.setText("物品代码:");
        jPanel28.add(jLabel202, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 460, 60, 30));

        jLabel209.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        jLabel209.setText("爆率:");
        jPanel28.add(jLabel209, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 500, 40, 30));

        世界爆物名称.setEditable(false);
        世界爆物名称.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        世界爆物名称.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                世界爆物名称ActionPerformed(evt);
            }
        });
        jPanel28.add(世界爆物名称, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 540, 100, 30));

        jLabel40.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        jLabel40.setText("物品名:");
        jPanel28.add(jLabel40, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 540, 50, 30));

        修改世界爆物.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        修改世界爆物.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/更改.png"))); // NOI18N
        修改世界爆物.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                修改世界爆物ActionPerformed(evt);
            }
        });
        jPanel28.add(修改世界爆物, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 490, 70, 30));

        刷新世界爆物.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        刷新世界爆物.setText("刷新世界爆物");
        刷新世界爆物.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                刷新世界爆物ActionPerformed(evt);
            }
        });
        jPanel28.add(刷新世界爆物, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 530, 270, 30));

        jLabel323.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jLabel323.setText("指定物品查询掉落");
        jPanel28.add(jLabel323, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 410, -1, -1));

        查询物品掉落代码1.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        查询物品掉落代码1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                查询物品掉落代码1ActionPerformed(evt);
            }
        });
        jPanel28.add(查询物品掉落代码1, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 430, 120, 30));

        查询物品掉落.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        查询物品掉落.setText("查询物品掉落");
        查询物品掉落.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                查询物品掉落ActionPerformed(evt);
            }
        });
        jPanel28.add(查询物品掉落, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 480, 120, 30));

        jLabel321.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jLabel321.setText("删除指定物品掉落");
        jPanel28.add(jLabel321, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 410, -1, -1));

        删除指定的掉落1.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jPanel28.add(删除指定的掉落1, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 430, 120, 30));

        删除指定的掉落按键.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        删除指定的掉落按键.setText("删除指定掉落");
        删除指定的掉落按键.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                删除指定的掉落按键ActionPerformed(evt);
            }
        });
        jPanel28.add(删除指定的掉落按键, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 480, 120, 30));

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel28, javax.swing.GroupLayout.PREFERRED_SIZE, 833, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(32, Short.MAX_VALUE))
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel28, javax.swing.GroupLayout.PREFERRED_SIZE, 579, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        jTabbedPane9.addTab("全局爆率", jPanel19);

        jPanel18.setBackground(new java.awt.Color(250, 250, 250));
        jPanel18.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N

        jPanel27.setBackground(new java.awt.Color(250, 250, 250));
        jPanel27.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "怪物爆物/(10000=1%)", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 18))); // NOI18N
        jPanel27.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        怪物爆物.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        怪物爆物.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "序列号", "怪物代码", "物品代码", "爆率", "物品名字"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        怪物爆物.getTableHeader().setReorderingAllowed(false);
        jScrollPane7.setViewportView(怪物爆物);

        jPanel27.add(jScrollPane7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 690, 400));

        刷新怪物爆物.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        刷新怪物爆物.setText("刷新怪物爆物");
        刷新怪物爆物.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                刷新怪物爆物ActionPerformed(evt);
            }
        });
        jPanel27.add(刷新怪物爆物, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 30, 150, 40));

        刷新怪物卡片.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        刷新怪物卡片.setText("刷新卡片");
        刷新怪物卡片.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                刷新怪物卡片ActionPerformed(evt);
            }
        });
        jPanel27.add(刷新怪物卡片, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 90, 150, 40));

        jLabel213.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jLabel213.setText("序列号:");

        怪物爆物序列号.setEditable(false);
        怪物爆物序列号.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N

        jLabel120.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jLabel120.setText("怪物代码:");

        怪物爆物怪物代码.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N

        jLabel211.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jLabel211.setText("物品代码:");

        怪物爆物物品代码.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N

        jLabel212.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jLabel212.setText("爆率:");

        怪物爆物爆率.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N

        jLabel39.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jLabel39.setText("物品名:");

        怪物爆物物品名称.setEditable(false);
        怪物爆物物品名称.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N

        添加怪物爆物.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        添加怪物爆物.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/新增.png"))); // NOI18N
        添加怪物爆物.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                添加怪物爆物ActionPerformed(evt);
            }
        });

        删除怪物爆物.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        删除怪物爆物.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/删除.png"))); // NOI18N
        删除怪物爆物.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                删除怪物爆物ActionPerformed(evt);
            }
        });

        修改怪物爆物.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        修改怪物爆物.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/更改.png"))); // NOI18N
        修改怪物爆物.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                修改怪物爆物ActionPerformed(evt);
            }
        });

        jLabel324.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jLabel324.setText("指定物品查询掉落");

        查询物品掉落代码.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        查询物品掉落代码.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                查询物品掉落代码ActionPerformed(evt);
            }
        });

        查询物品掉落1.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        查询物品掉落1.setText("查询物品掉落");
        查询物品掉落1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                查询物品掉落1ActionPerformed(evt);
            }
        });

        jLabel316.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jLabel316.setText("指定怪物物品掉落");

        jLabel325.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jLabel325.setText("删除指定物品掉落");

        删除指定的掉落.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N

        删除指定的掉落按键1.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        删除指定的掉落按键1.setText("删除指定掉落");
        删除指定的掉落按键1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                删除指定的掉落按键1ActionPerformed(evt);
            }
        });

        查询怪物掉落代码.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N

        查询怪物掉落.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        查询怪物掉落.setText("查询怪物掉落");
        查询怪物掉落.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                查询怪物掉落ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel18Layout.createSequentialGroup()
                                .addComponent(添加怪物爆物, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(77, 77, 77)
                                .addComponent(修改怪物爆物, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(79, 79, 79))
                            .addGroup(jPanel18Layout.createSequentialGroup()
                                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(jPanel18Layout.createSequentialGroup()
                                        .addGap(14, 14, 14)
                                        .addComponent(jLabel213, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(怪物爆物序列号, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(怪物爆物物品名称, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel18Layout.createSequentialGroup()
                                        .addComponent(jLabel120, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(怪物爆物怪物代码, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel211, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(怪物爆物物品代码, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(删除怪物爆物, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel18Layout.createSequentialGroup()
                                .addComponent(jLabel212, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(怪物爆物爆率, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel316)
                            .addComponent(jLabel325)
                            .addComponent(jLabel324))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(删除指定的掉落, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(查询物品掉落代码, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(查询怪物掉落代码, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(26, 26, 26)
                        .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(删除指定的掉落按键1, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(查询物品掉落1, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(查询怪物掉落, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addComponent(jPanel27, javax.swing.GroupLayout.PREFERRED_SIZE, 439, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(怪物爆物序列号, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel213, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(怪物爆物物品名称, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12, 12, 12)
                        .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel212, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(怪物爆物爆率, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(怪物爆物物品代码, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel211, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(怪物爆物怪物代码)
                            .addComponent(jLabel120, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(26, 26, 26)
                        .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(添加怪物爆物, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(修改怪物爆物, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(删除怪物爆物, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel324, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(查询物品掉落代码, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(查询物品掉落1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel325, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(删除指定的掉落, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(删除指定的掉落按键1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel316, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(查询怪物掉落代码, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(查询怪物掉落, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        jTabbedPane9.addTab("物品爆率", jPanel18);

        jPanel20.setBackground(new java.awt.Color(250, 250, 250));

        钓鱼管理.setBackground(new java.awt.Color(255, 255, 255));
        钓鱼管理.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "钓鱼管理", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 24))); // NOI18N
        钓鱼管理.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        钓鱼管理.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        钓鱼物品.setBackground(new java.awt.Color(102, 102, 102));
        钓鱼物品.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        钓鱼物品.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "序号", "代码", "概率", "物品名称"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        钓鱼物品.getTableHeader().setReorderingAllowed(false);
        jScrollPane10.setViewportView(钓鱼物品);

        钓鱼管理.add(jScrollPane10, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, 810, 380));
        钓鱼管理.add(jSeparator11, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 10, -1, -1));
        钓鱼管理.add(jSeparator12, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 10, -1, -1));

        jPanel91.setBackground(new java.awt.Color(250, 250, 250));
        jPanel91.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "钓鱼编辑", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 18))); // NOI18N
        jPanel91.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jPanel91.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        修改钓鱼物品.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        修改钓鱼物品.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/更改.png"))); // NOI18N
        修改钓鱼物品.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                修改钓鱼物品ActionPerformed(evt);
            }
        });
        jPanel91.add(修改钓鱼物品, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 80, -1, 30));

        刷新钓鱼物品.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        刷新钓鱼物品.setText("刷新钓鱼物品");
        刷新钓鱼物品.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                刷新钓鱼物品ActionPerformed(evt);
            }
        });
        jPanel91.add(刷新钓鱼物品, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 80, -1, 30));

        钓鱼物品代码.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        钓鱼物品代码.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                钓鱼物品代码ActionPerformed(evt);
            }
        });
        jPanel91.add(钓鱼物品代码, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 60, 110, 30));

        新增钓鱼物品.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        新增钓鱼物品.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/新增.png"))); // NOI18N
        新增钓鱼物品.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                新增钓鱼物品ActionPerformed(evt);
            }
        });
        jPanel91.add(新增钓鱼物品, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 130, -1, 30));

        钓鱼物品概率.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jPanel91.add(钓鱼物品概率, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 100, 110, 30));

        钓鱼物品名称.setEditable(false);
        钓鱼物品名称.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jPanel91.add(钓鱼物品名称, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 140, 110, 30));

        删除钓鱼物品.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        删除钓鱼物品.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/删除.png"))); // NOI18N
        删除钓鱼物品.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                删除钓鱼物品ActionPerformed(evt);
            }
        });
        jPanel91.add(删除钓鱼物品, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 30, -1, 30));

        钓鱼物品序号.setEditable(false);
        钓鱼物品序号.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        钓鱼物品序号.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                钓鱼物品序号ActionPerformed(evt);
            }
        });
        jPanel91.add(钓鱼物品序号, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 20, 110, 30));

        jLabel379.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jLabel379.setText("物品名字");
        jPanel91.add(jLabel379, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 140, 70, 30));

        jLabel380.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jLabel380.setText("序列号");
        jPanel91.add(jLabel380, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 20, 50, 30));

        jLabel381.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jLabel381.setText("物品代码");
        jPanel91.add(jLabel381, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 60, 70, 30));

        jLabel382.setFont(new java.awt.Font("微软雅黑", 0, 15)); // NOI18N
        jLabel382.setText("垂钓概率");
        jPanel91.add(jLabel382, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 100, 70, 30));

        钓鱼管理.add(jPanel91, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 420, 810, 180));

        ZEVMS2提示框1.setFont(new java.awt.Font("幼圆", 0, 18)); // NOI18N
        ZEVMS2提示框1.setText("[信息]：");
        钓鱼管理.add(ZEVMS2提示框1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 725, 1260, 30));

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addComponent(钓鱼管理, javax.swing.GroupLayout.PREFERRED_SIZE, 879, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addComponent(钓鱼管理, javax.swing.GroupLayout.PREFERRED_SIZE, 617, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jTabbedPane9.addTab("钓鱼物品", jPanel20);

        物品爆率.add(jTabbedPane9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 880, 640));

        主窗口.addTab("物品爆率", new javax.swing.ImageIcon(getClass().getResource("/image2/2040335.png")), 物品爆率); // NOI18N

        关于我们.setBackground(new java.awt.Color(255, 255, 255));

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jTextArea2.setText("2022-5-27 HuiMS.8更新说明\n1.修复 套装系统需要多段技能才能触发\n2.修复 吸怪被占用的情况\n3.修复 控制台发送道具普通玩家会闪退情况\n4.新增 控制台可以调整玩家点券、抵用、元宝、地图\n5.新增 控制台控制个人玩家下线和全体玩家下线\n6.新增 控制台可以给玩家一键满技能、给玩家传送某个地图\n7.修复 控制台发装备无法给装备上锁的情况\n\n2022-5-26 HuiMS.7更新说明\n1.新增 吸怪特权功能\n2.修复 玩家移动数据封包\n3.更改 经验为double类型\n4.修复 经验获取不正常\n5.新增 双商城\n6.新增 自定义盒子读脚本模式\n7.新增 多倍怪物和多倍怪物地图\n8.修改 等级区间获取经验倍率不同\n9.新增 物品叠加上限和指定弓标子弹上限\n10.新增 持有道具宠物不饥饿\n11.修复 仓库复制问题\n\n2022-5-9 HuiMS.6更新说明\n1.修复 金锤子功能及效果\n2.修复 控制台背景透明情况\n3.修复 控制台某些功能不正常\n4.新增 套装系统\n5.修复 关闭服务端会丢失数据\n\n2022-1-31 HuiMS.5更新说明\n1.修复 刷出来的现金装备无法放进商城\n2.新增 优化内存\n3.新增 皮肤更换功能\n4.优化 服务端\n\n2022-1-7 HuiMS.4更新说明\n1.修复 NPC不说话 以及部分NPC不走动\n2.新增 宠吸功能\n3.新增 怪物掉落点券控制\n4.新增 技能检测 用于防挂\n5.新增 玩家命令 @解卡组队\n6.新增 控制台功能\n7.新增 函数 cm.spawnMobStats(怪物代码,怪物数量,怪物血量,怪物经验,怪物X坐标,怪物Y坐标);\n\n2021-11-12 HuiMS.3更新说明\n1.修复 控制台部分功能\n2.新增 函数 NPCConversationManager 捉鬼任务1() 捉鬼任务2() 捉鬼任务3() 捉鬼任务4()\n3.修正 getByMobSkill(int skill) 读取怪物技能 返回debuff类型\n4.新增 万能记录表单 getFZ1(int someid, String log1) setFZ1(int someid, String log1 ,int slot)\n5.新增 函数 getHp() 读取血量  getMp() 读取蓝量\n\n2021-11-10 HuiMS.2更新说明\n1.新增 控制台\n2.修复 修复商城限时特卖的异常出现 \n3.修复 商城热度包\n4.修复 转职之后技能点给的数值写法\n\n2019-11-5 HuiMS.1 更新说明\n1.修复 进入游戏的所有包头与拆包。 \n2.修复 NPC点击。 \n3.修复 NPC商城。 \n4.修复 怪物召唤，怪移动的问题。 \n5.修复 人物移动的封包错误问题，修复多人同时在同地图封包错误的问题。 \n6.修复 怪物掉落物品坐标问题。 \n7.修复 椅子 \n8.修复 骑宠 \n9.修复 战，法，弓，飞侠等职业技能。 \n10.修复 辅助技能。 \n11.修复 喇叭封包错误问题。 \n12.修复 招待好友封包问题。 \n13.修复 部分被动技能无效问题\n14.修复 技能宏不保存问题\n15.修复 测谎仪系统\n16.修复 学院系统\n17.修复 钓鱼系统\n18.修复 结婚系统 \n19.新增 自定义商城道具\n20.修复 好友组队问题。\n21.优化 大部分源代码写法(使服务端更加稳定)\n22.最新 开服源码框架，服务端长久稳定(不掉线是不可能的，只能说我们的是最稳定的)");
        jScrollPane9.setViewportView(jTextArea2);

        关于我们.addTab("修复内容", new javax.swing.ImageIcon(getClass().getResource("/image2/信息日志.png")), jScrollPane9); // NOI18N

        jPanel52.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "关于我们", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("微软雅黑", 0, 12))); // NOI18N
        jPanel52.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel9.setText("[HuiMS]服务端Ver:079 [正版]");
        jPanel52.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(351, 267, -1, 34));

        jLabel14.setText("请遵守协议,服务端均来自互联网,如有法律侵权请第一时间联系我们删除.");
        jPanel52.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(238, 443, -1, -1));

        jLabel13.setText("请勿商业用途,后果自负");
        jPanel52.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 410, -1, -1));

        jLabel12.setText("游戏中遇到BUG请提交到作者");
        jPanel52.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(344, 377, -1, -1));

        jLabel10.setText("我们是永久包版本更新的哦,不收取其他任何费用");
        jPanel52.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(292, 344, -1, -1));

        jLabel8.setText("不得外传和转发,仅研究使用,请勿商业用途,使用完后请立即删除");
        jPanel52.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(255, 311, -1, -1));

        jLabel41.setBackground(new java.awt.Color(250, 250, 250));
        jLabel41.setFont(new java.awt.Font("宋体", 1, 48)); // NOI18N
        jLabel41.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image2/HuiMs.png"))); // NOI18N
        jPanel52.add(jLabel41, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 110, 400, 150));

        jButton7.setText("问题提交");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        jPanel52.add(jButton7, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 470, 104, 34));

        关于我们.addTab("版权信息", new javax.swing.ImageIcon(getClass().getResource("/image2/警告日志.png")), jPanel52); // NOI18N

        主窗口.addTab("关于我们", new javax.swing.ImageIcon(getClass().getResource("/image2/关于.png")), 关于我们); // NOI18N

        getContentPane().add(主窗口, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 960, 690));
        主窗口.getAccessibleContext().setAccessibleName("首页功能");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField22ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField22ActionPerformed

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
        // TODO add your handling code here:
        重启服务器();
    }//GEN-LAST:event_jButton16ActionPerformed

    private void startserverbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startserverbuttonActionPerformed
        if (开启服务端 == false) {
            开启服务端 = true;
        } else {
            System.out.println("服务端正在运行中！");
            return;
        }
        new Thread(new Runnable() {
            public void run() {
                startRunTime = System.currentTimeMillis();
                Start.是否控制台启动 = true;
                Start.main(null);
                startserverbutton.setText("正在运行中...");
                startserverbutton.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
            }
        }).start();//线程启动服务器初始化
        Dis tt = new Dis();
        tt.start();
    }//GEN-LAST:event_startserverbuttonActionPerformed

    private void 重载包头按钮2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_重载包头按钮2ActionPerformed
        // TODO add your handling code here:
        SendPacketOpcode.reloadValues();
        RecvPacketOpcode.reloadValues();
        String 输出 = "[重载系统] 包头重载成功。";
        JOptionPane.showMessageDialog(null, "包头重载成功。");
        printChatLog(输出);
    }//GEN-LAST:event_重载包头按钮2ActionPerformed

    private void 重载任务2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_重载任务2ActionPerformed
        // TODO add your handling code here:
        MapleQuest.clearQuests();
        String 输出 = "[重载系统] 任务重载成功。";
        JOptionPane.showMessageDialog(null, "任务重载成功。");
        printChatLog(输出);
    }//GEN-LAST:event_重载任务2ActionPerformed

    private void 重载商店按钮2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_重载商店按钮2ActionPerformed
        // TODO add your handling code here:
        MapleShopFactory.getInstance().clear();
        String 输出 = "[重载系统] 商店重载成功。";
        JOptionPane.showMessageDialog(null, "商店重载成功。");
        printChatLog(输出);
    }//GEN-LAST:event_重载商店按钮2ActionPerformed

    private void 重载商城按钮2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_重载商城按钮2ActionPerformed
        // TODO add your handling code here:
        CashItemFactory.getInstance().clearCashShop();
        String 输出 = "[重载系统] 商城重载成功。";
        JOptionPane.showMessageDialog(null, "商城重载成功。");
        printChatLog(输出);
    }//GEN-LAST:event_重载商城按钮2ActionPerformed

    private void 重载传送门按钮2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_重载传送门按钮2ActionPerformed
        // TODO add your handling code here:
        PortalScriptManager.getInstance().clearScripts();
        String 输出 = "[重载系统] 传送门重载成功。";
        JOptionPane.showMessageDialog(null, "传送门重载成功。");
        printChatLog(输出);
    }//GEN-LAST:event_重载传送门按钮2ActionPerformed

    private void 重载反应堆按钮2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_重载反应堆按钮2ActionPerformed
        // TODO add your handling code here:
        ReactorScriptManager.getInstance().clearDrops();
        String 输出 = "[重载系统] 反应堆重载成功。";
        JOptionPane.showMessageDialog(null, "反应堆重载成功。");
        printChatLog(输出);
    }//GEN-LAST:event_重载反应堆按钮2ActionPerformed

    private void 重载爆率按钮2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_重载爆率按钮2ActionPerformed
        // TODO add your handling code here:
        MapleMonsterInformationProvider.getInstance().clearDrops();
        String 输出 = "[重载系统] 爆率重载成功。";
        JOptionPane.showMessageDialog(null, "爆率重载成功。");
        printChatLog(输出);
    }//GEN-LAST:event_重载爆率按钮2ActionPerformed

    private void 重载副本按钮2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_重载副本按钮2ActionPerformed
        // TODO add your handling code here:
        for (ChannelServer instance : ChannelServer.getAllInstances()) {
            if (instance != null) {
                instance.reloadEvents();
            }
        }
        JOptionPane.showMessageDialog(null, "副本重载成功。");
    }//GEN-LAST:event_重载副本按钮2ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        String str = "cmd /c start iexplore ";
        try {
            Runtime.getRuntime().exec(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void 查询怪物掉落ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_查询怪物掉落ActionPerformed
        boolean result = this.查询怪物掉落代码.getText().matches("[0-9]+");
        if (result) {
            if (Integer.parseInt(this.查询怪物掉落代码.getText()) < 0) {
                JOptionPane.showMessageDialog(null, "[信息]:请填写正确的值。");
                return;
            }
            for (int i = ((DefaultTableModel) (this.怪物爆物.getModel())).getRowCount() - 1; i >= 0; i--) {
                ((DefaultTableModel) (this.怪物爆物.getModel())).removeRow(i);
            }
            try {
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = null;

                ResultSet rs = null;
                ps = con.prepareStatement("SELECT * FROM drop_data WHERE dropperid =  " + Integer.parseInt(this.查询怪物掉落代码.getText()) + " && itemid !=0");//&& itemid !=0
                rs = ps.executeQuery();
                while (rs.next()) {

                    ((DefaultTableModel) 怪物爆物.getModel()).insertRow(怪物爆物.getRowCount(), new Object[]{
                        rs.getInt("id"),
                        rs.getInt("dropperid"),
                        rs.getInt("itemid"),
                        rs.getInt("chance"),
                        MapleItemInformationProvider.getInstance().getName(rs.getInt("itemid"))
                    });
                }
                rs.close();
                ps.close();
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
            怪物爆物.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    int i = 怪物爆物.getSelectedRow();
                    String a = 怪物爆物.getValueAt(i, 0).toString();
                    String a1 = 怪物爆物.getValueAt(i, 1).toString();
                    String a2 = 怪物爆物.getValueAt(i, 2).toString();
                    String a3 = 怪物爆物.getValueAt(i, 3).toString();
                    String a4 = 怪物爆物.getValueAt(i, 4).toString();
                    怪物爆物序列号.setText(a);
                    怪物爆物怪物代码.setText(a1);
                    怪物爆物物品代码.setText(a2);
                    怪物爆物爆率.setText(a3);
                    怪物爆物物品名称.setText(a4);
                    //怪物爆物任务.setText(a5);

                }
            });
        } else {
            JOptionPane.showMessageDialog(null, "[信息]:请输入你要查找的怪物代码。");
        }
    }//GEN-LAST:event_查询怪物掉落ActionPerformed

    private void 删除指定的掉落按键1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_删除指定的掉落按键1ActionPerformed
        String 输出 = "";
        PreparedStatement ps1 = null;
        ResultSet rs = null;

        boolean result = this.删除指定的掉落.getText().matches("[0-9]+");
        if (result == true) {
            int 商城SN编码 = Integer.parseInt(this.删除指定的掉落.getText());
            try {
                // for (int i = ((DefaultTableModel) (this.怪物爆物.getModel())).getRowCount() - 1; i >= 0; i--) {
                //   ((DefaultTableModel) (this.怪物爆物.getModel())).removeRow(i);
                //}
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM drop_data WHERE itemid = ?");
                ps1.setInt(1, 商城SN编码);
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlstr = " delete from drop_data where itemid =" + 商城SN编码 + "";
                    ps1.executeUpdate(sqlstr);
                    JOptionPane.showMessageDialog(null, "[信息]:成功删除 " + 商城SN编码 + " 物品。");
                }
                rs.close();
                ps1.close();
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
            刷新怪物爆物();
        } else {
            JOptionPane.showMessageDialog(null, "[信息]:请输入你要查找的物品代码。");
        }
    }//GEN-LAST:event_删除指定的掉落按键1ActionPerformed

    private void 查询物品掉落1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_查询物品掉落1ActionPerformed
        boolean result = this.查询物品掉落代码.getText().matches("[0-9]+");
        if (result) {
            if (Integer.parseInt(this.查询物品掉落代码.getText()) < 0) {
                JOptionPane.showMessageDialog(null, "[信息]:请填写正确的值。");
                return;
            }
            for (int i = ((DefaultTableModel) (this.怪物爆物.getModel())).getRowCount() - 1; i >= 0; i--) {
                ((DefaultTableModel) (this.怪物爆物.getModel())).removeRow(i);
            }
            try {
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = null;

                ResultSet rs = null;
                ps = con.prepareStatement("SELECT * FROM drop_data WHERE itemid =  " + Integer.parseInt(this.查询物品掉落代码.getText()) + "");
                rs = ps.executeQuery();
                while (rs.next()) {

                    ((DefaultTableModel) 怪物爆物.getModel()).insertRow(怪物爆物.getRowCount(), new Object[]{
                        rs.getInt("id"),
                        rs.getInt("dropperid"),
                        rs.getInt("itemid"),
                        rs.getInt("chance"),
                        MapleItemInformationProvider.getInstance().getName(rs.getInt("itemid"))
                    });
                }
                rs.close();
                ps.close();
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
            怪物爆物.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    int i = 怪物爆物.getSelectedRow();
                    String a = 怪物爆物.getValueAt(i, 0).toString();
                    String a1 = 怪物爆物.getValueAt(i, 1).toString();
                    String a2 = 怪物爆物.getValueAt(i, 2).toString();
                    String a3 = 怪物爆物.getValueAt(i, 3).toString();
                    //String a4 = 怪物爆物.getValueAt(i, 4).toString();
                    怪物爆物序列号.setText(a);
                    怪物爆物怪物代码.setText(a1);
                    怪物爆物物品代码.setText(a2);
                    怪物爆物爆率.setText(a3);
                }
            });
        } else {
            JOptionPane.showMessageDialog(null, "[信息]:请输入你要查找的物品代码。");
        }
    }//GEN-LAST:event_查询物品掉落1ActionPerformed

    private void 查询物品掉落代码ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_查询物品掉落代码ActionPerformed

    }//GEN-LAST:event_查询物品掉落代码ActionPerformed

    private void 修改怪物爆物ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_修改怪物爆物ActionPerformed
        boolean result1 = this.怪物爆物怪物代码.getText().matches("[0-9]+");
        boolean result2 = this.怪物爆物物品代码.getText().matches("[0-9]+");
        boolean result3 = this.怪物爆物爆率.getText().matches("[0-9]+");
        //PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        if (result1 && result2 && result3) {
            try {
                //ps = DatabaseConnection.getConnection().prepareStatement("UPDATE drop_data SET dropperid = ?, itemid = ?, chance = ? WHERE id = ?");
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM drop_data WHERE id = ?");
                ps1.setInt(1, Integer.parseInt(this.怪物爆物序列号.getText()));
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlString2 = null;
                    String sqlString3 = null;
                    String sqlString4 = null;
                    sqlString2 = "update drop_data set dropperid='" + this.怪物爆物怪物代码.getText() + "' where id=" + this.怪物爆物序列号.getText() + ";";
                    PreparedStatement dropperid = DatabaseConnection.getConnection().prepareStatement(sqlString2);
                    dropperid.executeUpdate(sqlString2);
                    sqlString3 = "update drop_data set itemid='" + this.怪物爆物物品代码.getText() + "' where id=" + this.怪物爆物序列号.getText() + ";";
                    PreparedStatement itemid = DatabaseConnection.getConnection().prepareStatement(sqlString3);
                    itemid.executeUpdate(sqlString3);
                    sqlString4 = "update drop_data set chance='" + this.怪物爆物爆率.getText() + "' where id=" + this.怪物爆物序列号.getText() + ";";
                    PreparedStatement chance = DatabaseConnection.getConnection().prepareStatement(sqlString4);
                    chance.executeUpdate(sqlString4);
                    JOptionPane.showMessageDialog(null, "[信息]:修改成功。");
                }
                rs.close();
                ps1.close();
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "[信息]:请选择你要修改的数据。");
        }
    }//GEN-LAST:event_修改怪物爆物ActionPerformed

    private void 删除怪物爆物ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_删除怪物爆物ActionPerformed
        PreparedStatement ps1 = null;
        ResultSet rs = null;

        boolean result = this.怪物爆物序列号.getText().matches("[0-9]+");
        if (result == true) {
            int 商城SN编码 = Integer.parseInt(this.怪物爆物序列号.getText());

            try {
                //清楚table数据
                for (int i = ((DefaultTableModel) (this.怪物爆物.getModel())).getRowCount() - 1; i >= 0; i--) {
                    ((DefaultTableModel) (this.怪物爆物.getModel())).removeRow(i);
                }
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM drop_data WHERE id = ?");
                ps1.setInt(1, 商城SN编码);
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlstr = " delete from drop_data where id =" + 商城SN编码 + "";
                    ps1.executeUpdate(sqlstr);
                    JOptionPane.showMessageDialog(null, "[信息]:删除爆物成功。");
                    刷新指定怪物爆物();
                }
                rs.close();
                ps1.close();
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_删除怪物爆物ActionPerformed

    private void 添加怪物爆物ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_添加怪物爆物ActionPerformed
        boolean result1 = this.怪物爆物怪物代码.getText().matches("[0-9]+");
        boolean result2 = this.怪物爆物物品代码.getText().matches("[0-9]+");
        boolean result3 = this.怪物爆物爆率.getText().matches("[0-9]+");
        if (result1 && result2 && result3) {
            if (Integer.parseInt(this.怪物爆物怪物代码.getText()) < 0 && Integer.parseInt(this.怪物爆物物品代码.getText()) < 0 && Integer.parseInt(this.怪物爆物爆率.getText()) < 0) {
                JOptionPane.showMessageDialog(null, "[信息]:请填写正确的值。");
                return;
            }
            try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement("INSERT INTO drop_data ( dropperid,itemid,minimum_quantity,maximum_quantity,chance) VALUES ( ?, ?, ?, ?, ?)")) {
                ps.setInt(1, Integer.parseInt(this.怪物爆物怪物代码.getText()));
                ps.setInt(2, Integer.parseInt(this.怪物爆物物品代码.getText()));
                ps.setInt(3, 1);
                ps.setInt(4, 1);
                ps.setInt(5, Integer.parseInt(this.怪物爆物爆率.getText()));
                ps.executeUpdate();
                ps.close();
                JOptionPane.showMessageDialog(null, "[信息]:添加成功。");
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "[信息]:请输入<怪物代码><物品代码><物品爆率>的格式来添加。");
        }
    }//GEN-LAST:event_添加怪物爆物ActionPerformed

    private void 刷新怪物卡片ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_刷新怪物卡片ActionPerformed
        刷新怪物卡片();
    }//GEN-LAST:event_刷新怪物卡片ActionPerformed

    private void 刷新怪物爆物ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_刷新怪物爆物ActionPerformed
        JOptionPane.showMessageDialog(null, "[信息]:刷新怪物物品掉落数据。");
        刷新怪物爆物();
    }//GEN-LAST:event_刷新怪物爆物ActionPerformed

    private void 钓鱼物品序号ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_钓鱼物品序号ActionPerformed

    }//GEN-LAST:event_钓鱼物品序号ActionPerformed

    private void 删除钓鱼物品ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_删除钓鱼物品ActionPerformed
        String 输出 = "";
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        boolean result1 = this.钓鱼物品序号.getText().matches("[0-9]+");
        if (result1) {
            try {
                //清楚table数据
                for (int i = ((DefaultTableModel) (this.钓鱼物品.getModel())).getRowCount() - 1; i >= 0; i--) {
                    ((DefaultTableModel) (this.钓鱼物品.getModel())).removeRow(i);
                }
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM 钓鱼物品 WHERE id = ?");
                ps1.setInt(1, Integer.parseInt(this.钓鱼物品序号.getText()));
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlstr = " delete from 钓鱼物品 where id =" + Integer.parseInt(this.钓鱼物品序号.getText()) + "";
                    ps1.executeUpdate(sqlstr);
                    JOptionPane.showMessageDialog(null, "[信息]:删除钓鱼奖励物品成功。");
                    刷新钓鱼();
                }
                rs.close();
                ps1.close();
            } catch (SQLException ex) {
                Logger.getLogger(HuiMS.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "[信息]:请选择你要删除的钓鱼物品。");
        }
    }//GEN-LAST:event_删除钓鱼物品ActionPerformed

    private void 新增钓鱼物品ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_新增钓鱼物品ActionPerformed
        boolean result1 = this.钓鱼物品代码.getText().matches("[0-9]+");
        boolean result2 = this.钓鱼物品概率.getText().matches("[0-9]+");

        if (result1 && result2) {
            if (Integer.parseInt(this.钓鱼物品代码.getText()) < 0 && Integer.parseInt(this.钓鱼物品概率.getText()) < 0) {
                JOptionPane.showMessageDialog(null, "[信息]:请填写正确的值。");
                return;
            }
            try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement("INSERT INTO 钓鱼物品 (itemid, chance ,expiration) VALUES (?, ?, ?)")) {
                ps.setInt(1, Integer.parseInt(this.钓鱼物品代码.getText()));
                ps.setInt(2, Integer.parseInt(this.钓鱼物品概率.getText()));
                ps.setInt(3, 1);
                ps.executeUpdate();
                ps.close();
                JOptionPane.showMessageDialog(null, "[信息]:新增钓鱼奖励成功。");
                刷新钓鱼();
            } catch (SQLException ex) {
                Logger.getLogger(HuiMS.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "[信息]:请输入<物品代码><概率>。");
        }
    }//GEN-LAST:event_新增钓鱼物品ActionPerformed

    private void 钓鱼物品代码ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_钓鱼物品代码ActionPerformed

    }//GEN-LAST:event_钓鱼物品代码ActionPerformed

    private void 刷新钓鱼物品ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_刷新钓鱼物品ActionPerformed
        JOptionPane.showMessageDialog(null, "[信息]:刷新钓鱼奖励成功。");
        刷新钓鱼();
    }//GEN-LAST:event_刷新钓鱼物品ActionPerformed

    private void 修改钓鱼物品ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_修改钓鱼物品ActionPerformed
        //PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        boolean result1 = this.钓鱼物品序号.getText().matches("[0-9]+");
        if (result1) {
            try {
                //ps = DatabaseConnection.getConnection().prepareStatement("UPDATE 钓鱼物品 SET itemid = ?,chance = ?WHERE id = ?");
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM 钓鱼物品 WHERE id = ?");
                ps1.setInt(1, Integer.parseInt(this.钓鱼物品序号.getText()));
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlString1 = null;
                    String sqlString2 = null;
                    sqlString1 = "update 钓鱼物品 set itemid='" + this.钓鱼物品代码.getText() + "' where id=" + this.钓鱼物品序号.getText() + ";";
                    PreparedStatement name = DatabaseConnection.getConnection().prepareStatement(sqlString1);
                    name.executeUpdate(sqlString1);
                    sqlString2 = "update 钓鱼物品 set chance='" + this.钓鱼物品概率.getText() + "' where id=" + this.钓鱼物品序号.getText() + ";";
                    PreparedStatement level = DatabaseConnection.getConnection().prepareStatement(sqlString2);
                    level.executeUpdate(sqlString2);
                    //角色提示语言.setText("[信息]:修改钓鱼物品成功。");
                    JOptionPane.showMessageDialog(null, "修改钓鱼物品成功。");
                    刷新钓鱼();
                }
                rs.close();
                ps1.close();
            } catch (SQLException ex) {
                Logger.getLogger(HuiMS.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "[信息]:输入<物品代码><概率>。");
        }
    }//GEN-LAST:event_修改钓鱼物品ActionPerformed

    private void 删除指定的掉落按键ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_删除指定的掉落按键ActionPerformed
        String 输出 = "";
        PreparedStatement ps1 = null;
        ResultSet rs = null;

        boolean result = this.删除指定的掉落.getText().matches("[0-9]+");
        if (result == true) {
            int 商城SN编码 = Integer.parseInt(this.删除指定的掉落.getText());
            try {
                // for (int i = ((DefaultTableModel) (this.怪物爆物.getModel())).getRowCount() - 1; i >= 0; i--) {
                //   ((DefaultTableModel) (this.怪物爆物.getModel())).removeRow(i);
                //}
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM drop_data WHERE itemid = ?");
                ps1.setInt(1, 商城SN编码);
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlstr = " delete from drop_data where itemid =" + 商城SN编码 + "";
                    ps1.executeUpdate(sqlstr);
                    JOptionPane.showMessageDialog(null, "[信息]:成功删除 " + 商城SN编码 + " 物品。");
                }
                rs.close();
                ps1.close();
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
            刷新怪物爆物();
        } else {
            JOptionPane.showMessageDialog(null, "[信息]:请输入你要查找的物品代码。");
        }
    }//GEN-LAST:event_删除指定的掉落按键ActionPerformed

    private void 查询物品掉落ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_查询物品掉落ActionPerformed
        boolean result = this.查询物品掉落代码.getText().matches("[0-9]+");
        if (result) {
            if (Integer.parseInt(this.查询物品掉落代码.getText()) < 0) {
                JOptionPane.showMessageDialog(null, "[信息]:请填写正确的值。");
                return;
            }
            for (int i = ((DefaultTableModel) (this.怪物爆物.getModel())).getRowCount() - 1; i >= 0; i--) {
                ((DefaultTableModel) (this.怪物爆物.getModel())).removeRow(i);
            }
            try {
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = null;

                ResultSet rs = null;
                ps = con.prepareStatement("SELECT * FROM drop_data WHERE itemid =  " + Integer.parseInt(this.查询物品掉落代码.getText()) + "");
                rs = ps.executeQuery();
                while (rs.next()) {

                    ((DefaultTableModel) 怪物爆物.getModel()).insertRow(怪物爆物.getRowCount(), new Object[]{
                        rs.getInt("id"),
                        rs.getInt("dropperid"),
                        rs.getInt("itemid"),
                        rs.getInt("chance"),
                        MapleItemInformationProvider.getInstance().getName(rs.getInt("itemid"))
                    });
                }
                rs.close();
                ps.close();
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
            怪物爆物.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    int i = 怪物爆物.getSelectedRow();
                    String a = 怪物爆物.getValueAt(i, 0).toString();
                    String a1 = 怪物爆物.getValueAt(i, 1).toString();
                    String a2 = 怪物爆物.getValueAt(i, 2).toString();
                    String a3 = 怪物爆物.getValueAt(i, 3).toString();
                    //String a4 = 怪物爆物.getValueAt(i, 4).toString();
                    怪物爆物序列号.setText(a);
                    怪物爆物怪物代码.setText(a1);
                    怪物爆物物品代码.setText(a2);
                    怪物爆物爆率.setText(a3);
                }
            });
        } else {
            JOptionPane.showMessageDialog(null, "[信息]:请输入你要查找的物品代码。");
        }
    }//GEN-LAST:event_查询物品掉落ActionPerformed

    private void 查询物品掉落代码1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_查询物品掉落代码1ActionPerformed

    }//GEN-LAST:event_查询物品掉落代码1ActionPerformed

    private void 刷新世界爆物ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_刷新世界爆物ActionPerformed
        JOptionPane.showMessageDialog(null, "[信息]:刷新世界物品掉落数据。");
        刷新世界爆物();
    }//GEN-LAST:event_刷新世界爆物ActionPerformed

    private void 修改世界爆物ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_修改世界爆物ActionPerformed

        boolean result2 = this.世界爆物物品代码.getText().matches("[0-9]+");
        boolean result3 = this.世界爆物爆率.getText().matches("[0-9]+");
        //PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        if (result2 && result3) {
            try {
                //ps = DatabaseConnection.getConnection().prepareStatement("UPDATE drop_data_global SET dropperid = ?, itemid = ?, chance = ? WHERE id = ?");
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM drop_data_global WHERE id = ?");
                ps1.setInt(1, Integer.parseInt(this.世界爆物序列号.getText()));
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlString2 = null;
                    String sqlString3 = null;
                    sqlString2 = "update drop_data_global set itemid='" + this.世界爆物物品代码.getText() + "' where id=" + this.世界爆物序列号.getText() + ";";
                    PreparedStatement dropperid = DatabaseConnection.getConnection().prepareStatement(sqlString2);
                    dropperid.executeUpdate(sqlString2);
                    sqlString3 = "update drop_data_global set chance='" + this.世界爆物爆率.getText() + "' where id=" + this.世界爆物序列号.getText() + ";";
                    PreparedStatement itemid = DatabaseConnection.getConnection().prepareStatement(sqlString3);
                    itemid.executeUpdate(sqlString3);
                    JOptionPane.showMessageDialog(null, "[信息]:修改成功。");
                    刷新世界爆物();
                }
                rs.close();
                ps1.close();
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "[信息]:请选择你要修改的数据。");
        }
    }//GEN-LAST:event_修改世界爆物ActionPerformed

    private void 世界爆物名称ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_世界爆物名称ActionPerformed

    }//GEN-LAST:event_世界爆物名称ActionPerformed

    private void 删除世界爆物ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_删除世界爆物ActionPerformed
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        boolean result = this.世界爆物序列号.getText().matches("[0-9]+");
        if (result == true) {
            int 商城SN编码 = Integer.parseInt(this.世界爆物序列号.getText());
            try {
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM drop_data_global WHERE id = ?");
                ps1.setInt(1, 商城SN编码);
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlstr = " delete from drop_data_global where id =" + 商城SN编码 + "";
                    ps1.executeUpdate(sqlstr);
                    JOptionPane.showMessageDialog(null, "[信息]:删除成功。");
                    刷新世界爆物();
                }
                rs.close();
                ps1.close();
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "[信息]:请选择你要删除的物品。");
        }
    }//GEN-LAST:event_删除世界爆物ActionPerformed

    private void 添加世界爆物ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_添加世界爆物ActionPerformed
        boolean result1 = this.世界爆物物品代码.getText().matches("[0-9]+");
        boolean result2 = this.世界爆物爆率.getText().matches("[0-9]+");
        if (result1 && result2) {
            if (Integer.parseInt(this.世界爆物物品代码.getText()) < 0 && Integer.parseInt(this.世界爆物爆率.getText()) < 0) {
                JOptionPane.showMessageDialog(null, "[信息]:请填写正确的值。");
                return;
            }
            PreparedStatement ps1 = null;
            ResultSet rs = null;
            try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement("INSERT INTO drop_data_global (continent,dropType,itemid,minimum_quantity,maximum_quantity,questid,chance) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                ps.setInt(1, 1);
                ps.setInt(2, 1);
                ps.setInt(3, Integer.parseInt(this.世界爆物物品代码.getText()));
                ps.setInt(4, 1);
                ps.setInt(5, 1);
                ps.setInt(6, 0);
                ps.setInt(7, Integer.parseInt(this.世界爆物爆率.getText()));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(null, "[信息]:世界爆物添加成功。");
                刷新世界爆物();
                ps.close();
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "[信息]:请输入<物品代码>，<物品爆率> 。");
        }
    }//GEN-LAST:event_添加世界爆物ActionPerformed

    private void 世界爆物爆率ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_世界爆物爆率ActionPerformed

    }//GEN-LAST:event_世界爆物爆率ActionPerformed

    private void 世界爆物物品代码ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_世界爆物物品代码ActionPerformed

    }//GEN-LAST:event_世界爆物物品代码ActionPerformed

    private void 世界爆物序列号ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_世界爆物序列号ActionPerformed

    }//GEN-LAST:event_世界爆物序列号ActionPerformed

    private void 删除反应堆物品ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_删除反应堆物品ActionPerformed
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        boolean result1 = this.删除反应堆物品代码.getText().matches("[0-9]+");

        if (result1) {
            if (Integer.parseInt(this.删除反应堆物品代码.getText()) < 0) {
                JOptionPane.showMessageDialog(null, "请填写正确的值");
            }
            try {
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM reactordrops WHERE itemid = ?");
                ps1.setInt(1, Integer.parseInt(this.删除反应堆物品代码.getText()));
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlstr = " delete from reactordrops where itemid =" + Integer.parseInt(this.删除反应堆物品代码.getText()) + "";
                    ps1.executeUpdate(sqlstr);
                    JOptionPane.showMessageDialog(null, "成功删除 " + Integer.parseInt(this.删除反应堆物品代码.getText()) + " 物品，重载后生效。");
                    刷新反应堆();

                }
                rs.close();
                ps1.close();
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "请输入你要删除的反应堆代码 ");
        }
    }//GEN-LAST:event_删除反应堆物品ActionPerformed

    private void 重载箱子反应堆按钮ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_重载箱子反应堆按钮ActionPerformed
        // TODO add your handling code here:
        ReactorScriptManager.getInstance().clearDrops();
        System.out.println("[重载系统] 箱子反应堆重载成功。");
        JOptionPane.showMessageDialog(null, "箱子反应堆重载成功。");
    }//GEN-LAST:event_重载箱子反应堆按钮ActionPerformed

    private void 修改反应堆物品ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_修改反应堆物品ActionPerformed
        //PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        boolean result = this.反应堆代码.getText().matches("[0-9]+");
        boolean result1 = this.反应堆物品.getText().matches("[0-9]+");
        boolean result2 = this.反应堆概率.getText().matches("[0-9]+");

        if (result && result1 && result2) {
            if (Integer.parseInt(this.反应堆代码.getText()) < 0 && Integer.parseInt(this.反应堆物品.getText()) < 0 && Integer.parseInt(this.反应堆概率.getText()) < 0) {
                JOptionPane.showMessageDialog(null, "请填写正确的值");
            }
            try {
                //ps = DatabaseConnection.getConnection().prepareStatement("UPDATE reactordrops SET reactorid = ?,itemid = ?,chance = ?WHERE reactordropid = ?");
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM reactordrops WHERE reactordropid = ?");
                ps1.setInt(1, Integer.parseInt(this.反应堆序列号.getText()));
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlString1 = null;
                    String sqlString2 = null;
                    String sqlString3 = null;
                    sqlString1 = "update reactordrops set reactorid='" + this.反应堆代码.getText() + "' where reactordropid=" + this.反应堆序列号.getText() + ";";
                    PreparedStatement itemid = DatabaseConnection.getConnection().prepareStatement(sqlString1);
                    itemid.executeUpdate(sqlString1);

                    sqlString2 = "update reactordrops set itemid='" + this.反应堆物品.getText() + "' where reactordropid=" + this.反应堆序列号.getText() + ";";
                    PreparedStatement price = DatabaseConnection.getConnection().prepareStatement(sqlString2);
                    price.executeUpdate(sqlString2);

                    sqlString3 = "update reactordrops set chance='" + this.反应堆概率.getText() + "' where reactordropid=" + this.反应堆序列号.getText() + ";";
                    PreparedStatement shopid = DatabaseConnection.getConnection().prepareStatement(sqlString3);
                    shopid.executeUpdate(sqlString3);
                    shopid.close();

                    刷新反应堆();
                }
                rs.close();
                ps1.close();
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "请选择你要修改的数据");
        }
    }//GEN-LAST:event_修改反应堆物品ActionPerformed

    private void jButton37ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton37ActionPerformed
        boolean result2 = this.查找反应堆掉落.getText().matches("[0-9]+");
        if (result2) {
            for (int i = ((DefaultTableModel) (this.反应堆.getModel())).getRowCount() - 1; i >= 0; i--) {
                ((DefaultTableModel) (this.反应堆.getModel())).removeRow(i);
            }
            try {
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = null;

                ResultSet rs = null;
                ps = con.prepareStatement("SELECT * FROM reactordrops WHERE itemid = " + Integer.parseInt(查找物品.getText()));
                rs = ps.executeQuery();
                while (rs.next()) {
                    ((DefaultTableModel) 反应堆.getModel()).insertRow(反应堆.getRowCount(), new Object[]{
                        rs.getInt("reactordropid"),
                        rs.getInt("reactorid"),
                        rs.getInt("itemid"),
                        rs.getInt("chance"),
                        MapleItemInformationProvider.getInstance().getName(rs.getInt("itemid"))
                    });

                }
                rs.close();
                ps.close();
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "请输入你要查找的物品代码 ");
        }
    }//GEN-LAST:event_jButton37ActionPerformed

    private void jButton36ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton36ActionPerformed
        boolean result2 = this.查找反应堆掉落.getText().matches("[0-9]+");
        if (result2) {
            for (int i = ((DefaultTableModel) (this.反应堆.getModel())).getRowCount() - 1; i >= 0; i--) {
                ((DefaultTableModel) (this.反应堆.getModel())).removeRow(i);
            }
            try {
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = null;
                ResultSet rs = null;
                ps = con.prepareStatement("SELECT * FROM reactordrops WHERE reactorid = " + Integer.parseInt(查找反应堆掉落.getText()));
                rs = ps.executeQuery();
                while (rs.next()) {
                    ((DefaultTableModel) 反应堆.getModel()).insertRow(反应堆.getRowCount(), new Object[]{rs.getInt("reactordropid"), rs.getInt("reactorid"), rs.getInt("itemid"), rs.getInt("chance"), MapleItemInformationProvider.getInstance().getName(rs.getInt("itemid"))});
                }
                rs.close();
                ps.close();
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "请输入你要查找的反应堆 ");
        }
    }//GEN-LAST:event_jButton36ActionPerformed

    private void 删除反应堆物品1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_删除反应堆物品1ActionPerformed
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        boolean result1 = this.反应堆序列号.getText().matches("[0-9]+");

        if (result1) {
            try {
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM reactordrops WHERE reactordropid = ?");
                ps1.setInt(1, Integer.parseInt(this.反应堆序列号.getText()));
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlstr = " delete from reactordrops where reactordropid =" + Integer.parseInt(this.反应堆序列号.getText()) + "";
                    ps1.executeUpdate(sqlstr);
                    刷新反应堆();
                }
                rs.close();
                ps1.close();
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "请选择你要删除的物品 ");
        }
    }//GEN-LAST:event_删除反应堆物品1ActionPerformed

    private void 新增反应堆物品ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_新增反应堆物品ActionPerformed

        boolean result2 = this.反应堆代码.getText().matches("[0-9]+");

        if (result2) {
            try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement("INSERT INTO reactordrops ( reactorid ,itemid ,chance ,questid ) VALUES ( ?, ?, ?, ?)")) {
                ps.setInt(1, Integer.parseInt(this.反应堆代码.getText()));
                ps.setInt(2, Integer.parseInt(this.反应堆物品.getText()));
                ps.setInt(3, Integer.parseInt(this.反应堆概率.getText()));
                ps.setInt(4, -1);
                ps.executeUpdate();
                ps.close();
                刷新反应堆();
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "请输入反应堆代码，物品代码，掉落概率 ");
        }
    }//GEN-LAST:event_新增反应堆物品ActionPerformed

    private void 反应堆概率ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_反应堆概率ActionPerformed

    }//GEN-LAST:event_反应堆概率ActionPerformed

    private void jButton26ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton26ActionPerformed
        刷新反应堆();
    }//GEN-LAST:event_jButton26ActionPerformed

    private void jButton45ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton45ActionPerformed
        sendNotice(4);
        System.out.println("[公告系统] 发送公告成功！");
        JOptionPane.showMessageDialog(null, "发送公告成功！");
    }//GEN-LAST:event_jButton45ActionPerformed

    private void 公告发布喇叭代码ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_公告发布喇叭代码ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_公告发布喇叭代码ActionPerformed

    private void sendNpcTalkNoticeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendNpcTalkNoticeActionPerformed
        sendNotice(3);
        System.out.println("[公告系统] 发送黄色滚动公告成功！");
        JOptionPane.showMessageDialog(null, "发送黄色滚动公告成功！");
    }//GEN-LAST:event_sendNpcTalkNoticeActionPerformed

    private void sendMsgNoticeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendMsgNoticeActionPerformed
        sendNotice(2);
        System.out.println("[公告系统] 发送红色提示公告成功！");
        JOptionPane.showMessageDialog(null, "发送红色提示公告成功！");
    }//GEN-LAST:event_sendMsgNoticeActionPerformed

    private void sendWinNoticeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendWinNoticeActionPerformed
        sendNotice(1);
        System.out.println("[公告系统] 发送弹窗公告成功！");
        JOptionPane.showMessageDialog(null, "发送弹窗公告成功！");
    }//GEN-LAST:event_sendWinNoticeActionPerformed

    private void sendNoticeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendNoticeActionPerformed
        sendNotice(0);
        System.out.println("[公告系统] 发送蓝色公告事项公告成功！");
        JOptionPane.showMessageDialog(null, "发送蓝色公告事项公告成功！");
    }//GEN-LAST:event_sendNoticeActionPerformed

    private void 泡点豆豆开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_泡点豆豆开关ActionPerformed
        int 泡点豆豆开关 = HuiMS.ConfigValuesMap.get("泡点豆豆开关");
        if (泡点豆豆开关 <= 0) {
            按键开关("泡点豆豆开关", 711);
            刷新泡点豆豆开关();
        } else {
            按键开关("泡点豆豆开关", 711);
            刷新泡点豆豆开关();
        }
    }//GEN-LAST:event_泡点豆豆开关ActionPerformed

    private void 泡点抵用开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_泡点抵用开关ActionPerformed
        int 泡点抵用开关 = HuiMS.ConfigValuesMap.get("泡点抵用开关");
        if (泡点抵用开关 <= 0) {
            按键开关("泡点抵用开关", 707);
            刷新泡点抵用开关();
        } else {
            按键开关("泡点抵用开关", 707);
            刷新泡点抵用开关();
        }
    }//GEN-LAST:event_泡点抵用开关ActionPerformed

    private void 泡点点券开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_泡点点券开关ActionPerformed
        int 泡点点券开关 = HuiMS.ConfigValuesMap.get("泡点点券开关");
        if (泡点点券开关 <= 0) {
            按键开关("泡点点券开关", 703);
            刷新泡点点券开关();
        } else {
            按键开关("泡点点券开关", 703);
            刷新泡点点券开关();
        }
    }//GEN-LAST:event_泡点点券开关ActionPerformed

    private void 泡点经验开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_泡点经验开关ActionPerformed

        int 泡点经验开关 = HuiMS.ConfigValuesMap.get("泡点经验开关");
        if (泡点经验开关 <= 0) {
            按键开关("泡点经验开关", 705);
            刷新泡点经验开关();
        } else {
            按键开关("泡点经验开关", 705);
            刷新泡点经验开关();
        }
    }//GEN-LAST:event_泡点经验开关ActionPerformed

    private void 泡点金币开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_泡点金币开关ActionPerformed
        int 泡点金币开关 = HuiMS.ConfigValuesMap.get("泡点金币开关");
        if (泡点金币开关 <= 0) {
            按键开关("泡点金币开关", 701);
            刷新泡点金币开关();
        } else {
            按键开关("泡点金币开关", 701);
            刷新泡点金币开关();
        }
    }//GEN-LAST:event_泡点金币开关ActionPerformed

    private void 泡点值修改ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_泡点值修改ActionPerformed
        PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        boolean result1 = this.泡点值.getText().matches("[0-9]+");
        if (result1) {
            try {
                ps = DatabaseConnection.getConnection().prepareStatement("UPDATE configvalues SET Val = ? WHERE id = ?");

                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM configvalues WHERE id = ?");

                ps1.setInt(1, Integer.parseInt(this.泡点序号.getText()));
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlString1 = null;
                    sqlString1 = "update configvalues set Val = '" + this.泡点值.getText() + "' where id= " + this.泡点序号.getText() + ";";
                    PreparedStatement Val = DatabaseConnection.getConnection().prepareStatement(sqlString1);
                    Val.executeUpdate(sqlString1);
                    刷新泡点设置();
                    HuiMS.GetConfigValues();
                    福利提示语言2.setText("[信息]:修改成功已经生效。");
                }
            } catch (SQLException ex) {
                Logger.getLogger(HuiMS.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            福利提示语言2.setText("[信息]:请选择你要修改的值。");
        }
    }//GEN-LAST:event_泡点值修改ActionPerformed

    private void 个人发送物品玩家名字1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_个人发送物品玩家名字1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_个人发送物品玩家名字1ActionPerformed

    private void a2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_a2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_a2ActionPerformed

    private void z12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_z12ActionPerformed
        个人发送福利(6);
    }//GEN-LAST:event_z12ActionPerformed

    private void z11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_z11ActionPerformed
        个人发送福利(5);
    }//GEN-LAST:event_z11ActionPerformed

    private void z10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_z10ActionPerformed
        个人发送福利(4);
    }//GEN-LAST:event_z10ActionPerformed

    private void z9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_z9ActionPerformed
        个人发送福利(1);
    }//GEN-LAST:event_z9ActionPerformed

    private void z8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_z8ActionPerformed
        个人发送福利(3);
    }//GEN-LAST:event_z8ActionPerformed

    private void z7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_z7ActionPerformed
        个人发送福利(2);
    }//GEN-LAST:event_z7ActionPerformed

    private void 给予物品ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_给予物品ActionPerformed
        刷物品();       // TODO add your handling code here:
    }//GEN-LAST:event_给予物品ActionPerformed

    private void 个人发送物品代码ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_个人发送物品代码ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_个人发送物品代码ActionPerformed

    private void 个人发送物品玩家名字ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_个人发送物品玩家名字ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_个人发送物品玩家名字ActionPerformed

    private void 个人发送物品数量ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_个人发送物品数量ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_个人发送物品数量ActionPerformed

    private void 给予装备2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_给予装备2ActionPerformed
        刷装备2(1);
    }//GEN-LAST:event_给予装备2ActionPerformed

    private void 发送装备玩家姓名ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_发送装备玩家姓名ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_发送装备玩家姓名ActionPerformed

    private void 给予装备1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_给予装备1ActionPerformed
        刷装备2(2);        // TODO add your handling code here:
    }//GEN-LAST:event_给予装备1ActionPerformed

    private void 全服发送装备装备物理防御ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_全服发送装备装备物理防御ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_全服发送装备装备物理防御ActionPerformed

    private void 全服发送装备装备魔法防御ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_全服发送装备装备魔法防御ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_全服发送装备装备魔法防御ActionPerformed

    private void 全服发送装备装备魔法力ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_全服发送装备装备魔法力ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_全服发送装备装备魔法力ActionPerformed

    private void 全服发送装备物品IDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_全服发送装备物品IDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_全服发送装备物品IDActionPerformed

    private void 全服发送装备装备敏捷ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_全服发送装备装备敏捷ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_全服发送装备装备敏捷ActionPerformed

    private void 全服发送装备装备可否交易ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_全服发送装备装备可否交易ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_全服发送装备装备可否交易ActionPerformed

    private void 全服发送装备装备给予时间ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_全服发送装备装备给予时间ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_全服发送装备装备给予时间ActionPerformed

    private void 全服发送装备装备攻击力ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_全服发送装备装备攻击力ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_全服发送装备装备攻击力ActionPerformed

    private void 全服发送装备装备HPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_全服发送装备装备HPActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_全服发送装备装备HPActionPerformed

    private void 全服发送装备装备运气ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_全服发送装备装备运气ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_全服发送装备装备运气ActionPerformed

    private void 全服发送装备装备智力ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_全服发送装备装备智力ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_全服发送装备装备智力ActionPerformed

    private void 全服发送装备装备MPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_全服发送装备装备MPActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_全服发送装备装备MPActionPerformed

    private void 全服发送装备装备力量ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_全服发送装备装备力量ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_全服发送装备装备力量ActionPerformed

    private void 全服发送装备装备制作人ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_全服发送装备装备制作人ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_全服发送装备装备制作人ActionPerformed

    private void 全服发送装备装备加卷ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_全服发送装备装备加卷ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_全服发送装备装备加卷ActionPerformed

    private void 给予物品1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_给予物品1ActionPerformed
        刷物品2();    // TODO add your handling code here:
    }//GEN-LAST:event_给予物品1ActionPerformed

    private void 全服发送物品代码ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_全服发送物品代码ActionPerformed

    }//GEN-LAST:event_全服发送物品代码ActionPerformed

    private void 全服发送物品数量ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_全服发送物品数量ActionPerformed

    }//GEN-LAST:event_全服发送物品数量ActionPerformed

    private void a1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_a1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_a1ActionPerformed

    private void z6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_z6ActionPerformed
        发送福利(6);
    }//GEN-LAST:event_z6ActionPerformed

    private void z5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_z5ActionPerformed
        发送福利(5);
    }//GEN-LAST:event_z5ActionPerformed

    private void z4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_z4ActionPerformed
        发送福利(4);
    }//GEN-LAST:event_z4ActionPerformed

    private void z1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_z1ActionPerformed
        发送福利(1);        // TODO add your handling code here:
    }//GEN-LAST:event_z1ActionPerformed

    private void z3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_z3ActionPerformed
        发送福利(3);
    }//GEN-LAST:event_z3ActionPerformed

    private void z2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_z2ActionPerformed
        发送福利(2);
    }//GEN-LAST:event_z2ActionPerformed

    private void jButton48ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton48ActionPerformed
        // TODO add your handling code here:
        openWindow(Windows.活动控制台);
    }//GEN-LAST:event_jButton48ActionPerformed

    private void jButton51ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton51ActionPerformed
        openWindow(Windows.鱼来鱼往);
    }//GEN-LAST:event_jButton51ActionPerformed

    private void jButton47ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton47ActionPerformed
        openWindow(Windows.游戏抽奖工具);
    }//GEN-LAST:event_jButton47ActionPerformed

    private void jButton50ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton50ActionPerformed
        openWindow(Windows.锻造控制台);
    }//GEN-LAST:event_jButton50ActionPerformed

    private void jButton44ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton44ActionPerformed
        openWindow(Windows.金锤子成功率控制台);
    }//GEN-LAST:event_jButton44ActionPerformed

    private void jButton55ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton55ActionPerformed
        openWindow(Windows.椅子控制台);
    }//GEN-LAST:event_jButton55ActionPerformed

    private void jButton53ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton53ActionPerformed
        openWindow(Windows.商城管理控制台);
    }//GEN-LAST:event_jButton53ActionPerformed

    private void jButton27ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton27ActionPerformed
        openWindow(Windows.卡密制作工具);
    }//GEN-LAST:event_jButton27ActionPerformed

    private void jButton43ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton43ActionPerformed
        openWindow(Windows.永恒重生装备控制台);
    }//GEN-LAST:event_jButton43ActionPerformed

    private void jButton42ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton42ActionPerformed
        openWindow(Windows.药水冷却时间控制台);
    }//GEN-LAST:event_jButton42ActionPerformed

    private void jButton22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton22ActionPerformed
        openWindow(Windows.基址计算工具);
    }//GEN-LAST:event_jButton22ActionPerformed

    private void jButton29ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton29ActionPerformed
        openWindow(Windows.一键还原);
    }//GEN-LAST:event_jButton29ActionPerformed

    private void jButton41ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton41ActionPerformed
        openWindow(Windows.删除自添加NPC工具);
    }//GEN-LAST:event_jButton41ActionPerformed

    private void jButton40ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton40ActionPerformed
        openWindow(Windows.代码查询工具);
        if (!LoginServer.isShutdown() || searchServer) {
            return;
        }
    }//GEN-LAST:event_jButton40ActionPerformed

    private void jButton77ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton77ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton77ActionPerformed

    private void jButton76ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton76ActionPerformed
        runTool(Tools.FixCharSets);
    }//GEN-LAST:event_jButton76ActionPerformed

    private void jButton75ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton75ActionPerformed
        runTool(Tools.DumpQuests);
    }//GEN-LAST:event_jButton75ActionPerformed

    private void jButton74ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton74ActionPerformed
        runTool(Tools.DumpOxQuizData);
    }//GEN-LAST:event_jButton74ActionPerformed

    private void jButton73ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton73ActionPerformed
        runTool(Tools.DumpMobSkills);
    }//GEN-LAST:event_jButton73ActionPerformed

    private void jButton72ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton72ActionPerformed
        runTool(Tools.DumpCashShop);
    }//GEN-LAST:event_jButton72ActionPerformed

    private void jButton70ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton70ActionPerformed
        runTool(Tools.MonsterDropCreator);
    }//GEN-LAST:event_jButton70ActionPerformed

    private void jButton69ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton69ActionPerformed
        runTool(Tools.DumpItems);
    }//GEN-LAST:event_jButton69ActionPerformed

    private void 查询在线玩家人数按钮ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_查询在线玩家人数按钮ActionPerformed
        int p = 0;
        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
            for (MapleCharacter chr : cserv.getPlayerStorage().getAllCharacters()) {
                if (chr != null) {
                    ++p;
                }
            }
        }
        JOptionPane.showMessageDialog(this, "当前在线人数：" + p + "人");
    }//GEN-LAST:event_查询在线玩家人数按钮ActionPerformed

    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
        System.gc();
        JOptionPane.showMessageDialog(null, "回收服务端内存成功！");
    }//GEN-LAST:event_jButton17ActionPerformed

    private void jButton38ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton38ActionPerformed
        openWindow(Windows.商店管理控制台);
    }//GEN-LAST:event_jButton38ActionPerformed

    private void jButton68ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton68ActionPerformed
        openWindow(Windows.广播系统控制台);
    }//GEN-LAST:event_jButton68ActionPerformed

    private void jButton46ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton46ActionPerformed
        openWindow(Windows.OX答题控制台);
    }//GEN-LAST:event_jButton46ActionPerformed

    private void jButton49ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton49ActionPerformed
        openWindow(Windows.物品删除管理工具);
    }//GEN-LAST:event_jButton49ActionPerformed

    private void jButton54ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton54ActionPerformed
        openWindow(Windows.账号管理工具);
    }//GEN-LAST:event_jButton54ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        int p = 0;
        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
            for (MapleCharacter chr : cserv.getPlayerStorage().getAllCharacters()) {
                p++;
                chr.saveToDB(false, false);
            }
        }
        String 输出 = "[保存数据系统] 保存" + p + "个成功。";
        JOptionPane.showMessageDialog(null, 输出);
        printChatLog(输出);
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        // TODO add your handling code here:
        int p = 0;
        for (handling.channel.ChannelServer cserv : handling.channel.ChannelServer.getAllInstances()) {
            p++;
            cserv.closeAllMerchants();
        }
        String 输出 = "[保存雇佣商人系统] 雇佣商人保存" + p + "个频道成功。";
        JOptionPane.showMessageDialog(null, "雇佣商人保存" + p + "个频道成功。");
        printChatLog(输出);
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton34ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton34ActionPerformed
        openWindow(Windows.副本控制台);
    }//GEN-LAST:event_jButton34ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        // TODO add your handling code here:
        sendNoticeGG();
    }//GEN-LAST:event_jButton13ActionPerformed

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void 蓝蜗牛开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_蓝蜗牛开关ActionPerformed
        按键开关("蓝蜗牛开关", 2200);
        刷新蓝蜗牛开关();
        JOptionPane.showMessageDialog(null, "[信息]:修改成功!");
    }//GEN-LAST:event_蓝蜗牛开关ActionPerformed

    private void 蘑菇仔开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_蘑菇仔开关ActionPerformed
        按键开关("蘑菇仔开关", 2201);
        刷新蘑菇仔开关();
        JOptionPane.showMessageDialog(null, "[信息]:修改成功!");
    }//GEN-LAST:event_蘑菇仔开关ActionPerformed

    private void 绿水灵开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_绿水灵开关ActionPerformed
        按键开关("绿水灵开关", 2202);
        刷新绿水灵开关();
        JOptionPane.showMessageDialog(null, "[信息]:修改成功!");
    }//GEN-LAST:event_绿水灵开关ActionPerformed

    private void 漂漂猪开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_漂漂猪开关ActionPerformed
        按键开关("漂漂猪开关", 2203);
        刷新漂漂猪开关();
        JOptionPane.showMessageDialog(null, "[信息]:修改成功!");
    }//GEN-LAST:event_漂漂猪开关ActionPerformed

    private void 小青蛇开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_小青蛇开关ActionPerformed
        按键开关("小青蛇开关", 2204);
        刷新小青蛇开关();
        JOptionPane.showMessageDialog(null, "[信息]:修改成功!");
    }//GEN-LAST:event_小青蛇开关ActionPerformed

    private void 红螃蟹开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_红螃蟹开关ActionPerformed
        按键开关("红螃蟹开关", 2205);
        刷新红螃蟹开关();
        JOptionPane.showMessageDialog(null, "[信息]:修改成功!");
    }//GEN-LAST:event_红螃蟹开关ActionPerformed

    private void 大海龟开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_大海龟开关ActionPerformed
        按键开关("大海龟开关", 2206);
        刷新大海龟开关();
        JOptionPane.showMessageDialog(null, "[信息]:修改成功!");
    }//GEN-LAST:event_大海龟开关ActionPerformed

    private void 章鱼怪开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_章鱼怪开关ActionPerformed
        按键开关("章鱼怪开关", 2207);
        刷新章鱼怪开关();
        JOptionPane.showMessageDialog(null, "[信息]:修改成功!");
    }//GEN-LAST:event_章鱼怪开关ActionPerformed

    private void 顽皮猴开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_顽皮猴开关ActionPerformed
        按键开关("顽皮猴开关", 2208);
        刷新顽皮猴开关();
        JOptionPane.showMessageDialog(null, "[信息]:修改成功!");
    }//GEN-LAST:event_顽皮猴开关ActionPerformed

    private void 星精灵开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_星精灵开关ActionPerformed
        按键开关("星精灵开关", 2209);
        刷新星精灵开关();
        JOptionPane.showMessageDialog(null, "[信息]:修改成功!");
    }//GEN-LAST:event_星精灵开关ActionPerformed

    private void 胖企鹅开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_胖企鹅开关ActionPerformed
        按键开关("胖企鹅开关", 2210);
        刷新胖企鹅开关();
        JOptionPane.showMessageDialog(null, "[信息]:修改成功!");
    }//GEN-LAST:event_胖企鹅开关ActionPerformed

    private void 白雪人开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_白雪人开关ActionPerformed
        按键开关("白雪人开关", 2211);
        刷新白雪人开关();
        JOptionPane.showMessageDialog(null, "[信息]:修改成功!");
    }//GEN-LAST:event_白雪人开关ActionPerformed

    private void 石头人开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_石头人开关ActionPerformed
        按键开关("石头人开关", 2212);
        刷新石头人开关();
        JOptionPane.showMessageDialog(null, "[信息]:修改成功!");
    }//GEN-LAST:event_石头人开关ActionPerformed

    private void 紫色猫开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_紫色猫开关ActionPerformed
        按键开关("紫色猫开关", 2213);
        刷新紫色猫开关();
        JOptionPane.showMessageDialog(null, "[信息]:修改成功!");
    }//GEN-LAST:event_紫色猫开关ActionPerformed

    private void 大灰狼开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_大灰狼开关ActionPerformed
        按键开关("大灰狼开关", 2214);
        刷新大灰狼开关();
        JOptionPane.showMessageDialog(null, "[信息]:修改成功!");
    }//GEN-LAST:event_大灰狼开关ActionPerformed

    private void 喷火龙开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_喷火龙开关ActionPerformed
        按键开关("喷火龙开关", 2216);
        刷新喷火龙开关();
        JOptionPane.showMessageDialog(null, "[信息]:修改成功!");
    }//GEN-LAST:event_喷火龙开关ActionPerformed

    private void 火野猪开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_火野猪开关ActionPerformed
        按键开关("火野猪开关", 2217);
        刷新火野猪开关();
        JOptionPane.showMessageDialog(null, "[信息]:修改成功!");
    }//GEN-LAST:event_火野猪开关ActionPerformed

    private void 小白兔开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_小白兔开关ActionPerformed
        按键开关("小白兔开关", 2215);
        刷新小白兔开关();
        JOptionPane.showMessageDialog(null, "[信息]:修改成功!");
    }//GEN-LAST:event_小白兔开关ActionPerformed

    private void 青鳄鱼开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_青鳄鱼开关ActionPerformed
        按键开关("青鳄鱼开关", 2218);
        刷新青鳄鱼开关();
        JOptionPane.showMessageDialog(null, "[信息]:修改成功!");
    }//GEN-LAST:event_青鳄鱼开关ActionPerformed

    private void 花蘑菇开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_花蘑菇开关ActionPerformed
        按键开关("花蘑菇开关", 2219);
        刷新花蘑菇开关();
        JOptionPane.showMessageDialog(null, "[信息]:修改成功!");
    }//GEN-LAST:event_花蘑菇开关ActionPerformed

    private void 修改冒险家等级上限1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_修改冒险家等级上限1ActionPerformed
        if (机器码多开数量.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "不能为空");
            return;
        }
        boolean result2 = this.机器码多开数量.getText().matches("[0-9]+");
        PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        if (result2) {
            try {
                ps = DatabaseConnection.getConnection().prepareStatement("UPDATE configvalues SET Val = ? WHERE id = ?");
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM configvalues WHERE id = ?");
                ps1.setInt(1, 2301);
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlString2 = null;
                    sqlString2 = "update configvalues set Val='" + this.机器码多开数量.getText() + "' where id = 2064;";
                    PreparedStatement dropperid = DatabaseConnection.getConnection().prepareStatement(sqlString2);
                    dropperid.executeUpdate(sqlString2);
                    gui.HuiMS.GetConfigValues();
                    刷新多开数量();
                    JOptionPane.showMessageDialog(null, "修改成功");
                }
            } catch (SQLException ex) {
                Logger.getLogger(HuiMS.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_修改冒险家等级上限1ActionPerformed

    private void 修改骑士团等级上限2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_修改骑士团等级上限2ActionPerformed
        if (IP多开数量.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "不能为空");
            return;
        }
        boolean result2 = this.IP多开数量.getText().matches("[0-9]+");
        PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        if (result2) {
            try {
                ps = DatabaseConnection.getConnection().prepareStatement("UPDATE configvalues SET Val = ? WHERE id = ?");
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM configvalues WHERE id = ?");
                ps1.setInt(1, 2301);
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlString2 = null;
                    sqlString2 = "update configvalues set Val='" + this.IP多开数量.getText() + "' where id = 2063;";
                    PreparedStatement dropperid = DatabaseConnection.getConnection().prepareStatement(sqlString2);
                    dropperid.executeUpdate(sqlString2);
                    gui.HuiMS.GetConfigValues();
                    刷新多开数量();
                    JOptionPane.showMessageDialog(null, "修改成功");
                }
            } catch (SQLException ex) {
                Logger.getLogger(HuiMS.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_修改骑士团等级上限2ActionPerformed

    private void 修改物品掉落持续时间ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_修改物品掉落持续时间ActionPerformed
        boolean result2 = this.物品掉落持续时间.getText().matches("[0-9]+");
        PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        if (result2) {
            try {
                ps = DatabaseConnection.getConnection().prepareStatement("UPDATE configvalues SET Val = ? WHERE id = ?");
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM configvalues WHERE id = ?");
                ps1.setInt(1, 998);
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlString2 = null;
                    sqlString2 = "update configvalues set Val='" + this.物品掉落持续时间.getText() + "' where id = 998;";
                    PreparedStatement dropperid = DatabaseConnection.getConnection().prepareStatement(sqlString2);
                    dropperid.executeUpdate(sqlString2);
                    HuiMS.GetConfigValues();
                    刷新物品掉落持续时间();
                    JOptionPane.showMessageDialog(null, "[信息]:修改成功。");
                }
            } catch (SQLException ex) {
                Logger.getLogger(HuiMS.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "[信息]:请输入你要修改的数据。");
        }
    }//GEN-LAST:event_修改物品掉落持续时间ActionPerformed

    private void 修改物品掉落持续时间1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_修改物品掉落持续时间1ActionPerformed
        boolean result2 = this.地图物品上限.getText().matches("[0-9]+");
        PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        if (result2) {
            try {
                ps = DatabaseConnection.getConnection().prepareStatement("UPDATE configvalues SET Val = ? WHERE id = ?");
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM configvalues WHERE id = ?");
                ps1.setInt(1, 997);
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlString2 = null;
                    sqlString2 = "update configvalues set Val='" + this.地图物品上限.getText() + "' where id = 997;";
                    PreparedStatement dropperid = DatabaseConnection.getConnection().prepareStatement(sqlString2);
                    dropperid.executeUpdate(sqlString2);
                    HuiMS.GetConfigValues();
                    刷新地图物品上限();
                    JOptionPane.showMessageDialog(null, "[信息]:修改成功。");
                }
            } catch (SQLException ex) {
                Logger.getLogger(HuiMS.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "[信息]:请输入你要修改的数据。");
        }
    }//GEN-LAST:event_修改物品掉落持续时间1ActionPerformed

    private void 修改物品掉落持续时间2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_修改物品掉落持续时间2ActionPerformed
        boolean result2 = this.地图刷新频率.getText().matches("[0-9]+");
        PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        if (result2) {
            try {
                ps = DatabaseConnection.getConnection().prepareStatement("UPDATE configvalues SET Val = ? WHERE id = ?");
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM configvalues WHERE id = ?");
                ps1.setInt(1, 996);
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlString2 = null;
                    sqlString2 = "update configvalues set Val='" + this.地图刷新频率.getText() + "' where id = 996;";
                    PreparedStatement dropperid = DatabaseConnection.getConnection().prepareStatement(sqlString2);
                    dropperid.executeUpdate(sqlString2);
                    HuiMS.GetConfigValues();
                    刷新地图刷新频率();
                    World.registerRespawn();
                    World.registerRespawn1();
                    JOptionPane.showMessageDialog(null, "[信息]:修改成功。");
                }
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "[信息]:请输入你要修改的数据。");
        }
    }//GEN-LAST:event_修改物品掉落持续时间2ActionPerformed

    private void 商城扩充价格修改ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_商城扩充价格修改ActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_商城扩充价格修改ActionPerformed

    private void 修改背包扩充价格ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_修改背包扩充价格ActionPerformed
        boolean result1 = this.商城扩充价格修改.getText().matches("[0-9]+");
        if (result1) {
            if (Integer.parseInt(this.商城扩充价格修改.getText()) < 0) {
                JOptionPane.showMessageDialog(null, "[信息]:请输入正确的修改值。");
                return;
            }
            PreparedStatement ps1 = null;
            ResultSet rs = null;
            try {
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM configvalues WHERE id = ?");
                ps1.setInt(1, 1);
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlstr = " delete from configvalues where id =999";
                    ps1.executeUpdate(sqlstr);

                }
            } catch (SQLException ex) {
                Logger.getLogger(HuiMS.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
            try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement("INSERT INTO configvalues (id, name,Val) VALUES ( ?, ?, ?)")) {
                ps.setInt(1, 999);
                ps.setString(2, "商城扩充价格");
                ps.setInt(3, Integer.parseInt(this.商城扩充价格修改.getText()));
                ps.executeUpdate();
                刷新商城扩充价格();
                GetConfigValues();
                JOptionPane.showMessageDialog(null, "[信息]:商城扩充背包价格修改成功，已经生效。");

            } catch (SQLException ex) {
                Logger.getLogger(HuiMS.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_修改背包扩充价格ActionPerformed

    private void 冒险家职业开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_冒险家职业开关ActionPerformed
        按键开关("冒险家职业开关", 2000);
        刷新冒险家职业开关();
    }//GEN-LAST:event_冒险家职业开关ActionPerformed

    private void 战神职业开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_战神职业开关ActionPerformed
        按键开关("战神职业开关", 2002);
        刷新战神职业开关();
    }//GEN-LAST:event_战神职业开关ActionPerformed

    private void 骑士团职业开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_骑士团职业开关ActionPerformed
        按键开关("骑士团职业开关", 2001);
        刷新骑士团职业开关();
    }//GEN-LAST:event_骑士团职业开关ActionPerformed

    private void 幸运职业开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_幸运职业开关ActionPerformed
        按键开关("幸运职业开关", 749);
        刷新幸运职业开关();
    }//GEN-LAST:event_幸运职业开关ActionPerformed

    private void 神秘商人开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_神秘商人开关ActionPerformed
        按键开关("神秘商人开关", 2406);
        刷新神秘商人开关();
    }//GEN-LAST:event_神秘商人开关ActionPerformed

    private void 魔族突袭开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_魔族突袭开关ActionPerformed
        按键开关("魔族突袭开关", 2400);
        刷新魔族突袭开关();
    }//GEN-LAST:event_魔族突袭开关ActionPerformed

    private void 魔族攻城开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_魔族攻城开关ActionPerformed
        按键开关("魔族攻城开关", 2404);
        刷新魔族攻城开关();
    }//GEN-LAST:event_魔族攻城开关ActionPerformed

    private void jTextMaxLevelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextMaxLevelActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextMaxLevelActionPerformed

    private void 修改冒险家等级上限ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_修改冒险家等级上限ActionPerformed

        if (jTextMaxLevel.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "不能为空");
            return;
        }
        boolean result2 = this.jTextMaxLevel.getText().matches("[0-9]+");
        PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        if (result2) {
            try {
                ps = DatabaseConnection.getConnection().prepareStatement("UPDATE configvalues SET Val = ? WHERE id = ?");
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM configvalues WHERE id = ?");
                ps1.setInt(1, 2300);
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlString2 = null;
                    sqlString2 = "update configvalues set Val='" + this.jTextMaxLevel.getText() + "' where id = 2300;";
                    PreparedStatement dropperid = DatabaseConnection.getConnection().prepareStatement(sqlString2);
                    dropperid.executeUpdate(sqlString2);
                    HuiMS.GetConfigValues();
                    刷新冒险家等级上限();
                    JOptionPane.showMessageDialog(null, "修改成功");
                }
                rs.close();
                ps1.close();
                ps.close();
            } catch (SQLException ex) {
                Logger.getLogger(HuiMS.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_修改冒险家等级上限ActionPerformed

    private void 骑士团等级上限ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_骑士团等级上限ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_骑士团等级上限ActionPerformed

    private void 修改骑士团等级上限ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_修改骑士团等级上限ActionPerformed
        if (骑士团等级上限.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "不能为空");
            return;
        }
        boolean result2 = this.骑士团等级上限.getText().matches("[0-9]+");
        PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        if (result2) {
            try {
                ps = DatabaseConnection.getConnection().prepareStatement("UPDATE configvalues SET Val = ? WHERE id = ?");
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM configvalues WHERE id = ?");
                ps1.setInt(1, 2301);
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlString2 = null;
                    sqlString2 = "update configvalues set Val='" + this.骑士团等级上限.getText() + "' where id = 2301;";
                    PreparedStatement dropperid = DatabaseConnection.getConnection().prepareStatement(sqlString2);
                    dropperid.executeUpdate(sqlString2);
                    HuiMS.GetConfigValues();
                    刷新骑士团等级上限();
                    JOptionPane.showMessageDialog(null, "修改成功");
                }
                rs.close();
                ps1.close();
                ps.close();
            } catch (SQLException ex) {
                Logger.getLogger(HuiMS.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_修改骑士团等级上限ActionPerformed

    private void jTextFieldMaxCharacterNumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldMaxCharacterNumberActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldMaxCharacterNumberActionPerformed

    private void jButtonMaxCharacterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMaxCharacterActionPerformed
        // TODO add your handling code here:
        if (jTextFieldMaxCharacterNumber.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "不能为空");
            return;
        }
        boolean result2 = this.jTextFieldMaxCharacterNumber.getText().matches("[0-9]+");
        PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        if (result2) {
            try {
                ps = DatabaseConnection.getConnection().prepareStatement("UPDATE configvalues SET Val = ? WHERE id = ?");
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM configvalues WHERE id = ?");
                ps1.setInt(1, 400000);
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlString2 = null;
                    sqlString2 = "update configvalues set Val='" + this.jTextFieldMaxCharacterNumber.getText() + "' where id = 400000;";
                    PreparedStatement dropperid = DatabaseConnection.getConnection().prepareStatement(sqlString2);
                    dropperid.executeUpdate(sqlString2);
                    //UpdateMaxCharacterSlots();
                    //HuiMs.GetConfigValues();
                    //刷新冒险家等级上限();
                    dropperid.close();
                    JOptionPane.showMessageDialog(null, "修改成功");
                }
                rs.close();
                ps1.close();
                ps.close();
            } catch (SQLException ex) {
                Logger.getLogger(HuiMS.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "只能填数字0-9");
        }
    }//GEN-LAST:event_jButtonMaxCharacterActionPerformed

    private void 开启双倍经验ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_开启双倍经验ActionPerformed
        boolean result1 = this.双倍经验持续时间.getText().matches("[0-9]+");
        if (result1) {
            if (双倍经验持续时间.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "持续时间不能为空");
                return;
            }
            double 原始经验 = Double.parseDouble(ServerProperties.getProperty("HuiMs.expRate"));
            double 双倍经验活动 = 原始经验 * 2;
            int seconds = 0;
            int mins = 0;
            int hours = Integer.parseInt(this.双倍经验持续时间.getText());
            int time = seconds + (mins * 60) + (hours * 60 * 60);
            final String rate = "经验";
            if(time == 0){
                World.scheduleRateDelay(rate, 1);
                for (ChannelServer cservs : ChannelServer.getAllInstances()) {
                    cservs.setExpRate(原始经验);
                for (MapleCharacter chr : cservs.getPlayerStorage().getAllCharacters()) {
                    chr.getStat().recalcLocalStats(chr);
                }
                }
                World.Broadcast.broadcastSmega(MaplePacketCreator.serverNotice(9, 20, "[系统公告]：" + rate + "倍率活动已经结束，已经恢复正常值。"));
                JOptionPane.showMessageDialog(null, "双倍经验已经结束");
            }else{
                World.scheduleRateDelay(rate, time);
                for (ChannelServer cservs : ChannelServer.getAllInstances()) {
                    cservs.setExpRate(双倍经验活动);
                for (MapleCharacter chr : cservs.getPlayerStorage().getAllCharacters()) {
                    chr.getStat().recalcLocalStats(chr);
                }
            }
                World.Broadcast.broadcastSmega(MaplePacketCreator.serverNotice(9, 20, "[倍率活动] : 游戏开始 " + 双倍经验活动 + " 倍打怪经验活动，将持续 " + hours + " 小时，请各位玩家狂欢吧！"));
                JOptionPane.showMessageDialog(null, "成功开启双倍经验活动，持续 " + hours + " 小时");
            }
        } else {
            JOptionPane.showMessageDialog(null, "持续时间输入不正确");
        }
    }//GEN-LAST:event_开启双倍经验ActionPerformed

    private void 开启双倍爆率ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_开启双倍爆率ActionPerformed
        boolean result1 = this.双倍爆率持续时间.getText().matches("[0-9]+");
        if (result1) {
            if (双倍爆率持续时间.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "持续时间不能为空");
                return;
            }
            int 原始爆率 = Integer.parseInt(ServerProperties.getProperty("HuiMs.dropRate"));
            int 双倍爆率活动 = 原始爆率 * 2;
            int seconds = 0;
            int mins = 0;
            int hours = Integer.parseInt(this.双倍爆率持续时间.getText());
            int time = seconds + (mins * 60) + (hours * 60 * 60);
            final String rate = "爆率";
            if(time == 0){
                World.scheduleRateDelay(rate, 1);
                for (ChannelServer cservs : ChannelServer.getAllInstances()) {
                    cservs.setExpRate(原始爆率);
                for (MapleCharacter chr : cservs.getPlayerStorage().getAllCharacters()) {
                    chr.getStat().recalcLocalStats(chr);
                }
                }
                World.Broadcast.broadcastSmega(MaplePacketCreator.serverNotice(9, 20, "[系统公告]：" + rate + "倍率活动已经结束，已经恢复正常值。"));
                JOptionPane.showMessageDialog(null, "双倍爆率已经结束");
            }else{
                World.scheduleRateDelay(rate, time);
            for (ChannelServer cservs : ChannelServer.getAllInstances()) {
                cservs.setExpRate(双倍爆率活动);
                for (MapleCharacter chr : cservs.getPlayerStorage().getAllCharacters()) {
                    chr.getStat().recalcLocalStats(chr);
                }
            }
            World.Broadcast.broadcastSmega(MaplePacketCreator.serverNotice(9, 20, "[倍率活动] : 游戏开始 2 倍打怪爆率活动，将持续 " + hours + " 小时，请各位玩家狂欢吧！"));
            JOptionPane.showMessageDialog(null, "成功开启双倍爆率活动，持续 " + hours + " 小时");
            }
        } else {
            JOptionPane.showMessageDialog(null, "持续时间输入不正确");
        }
    }//GEN-LAST:event_开启双倍爆率ActionPerformed

    private void 开启双倍金币ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_开启双倍金币ActionPerformed
        boolean result1 = this.双倍金币持续时间.getText().matches("[0-9]+");
        if (result1) {
            if (双倍金币持续时间.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "持续时间不能为空");
                return;
            }
            int 原始金币 = Integer.parseInt(ServerProperties.getProperty("HuiMs.mesoRate"));
            int 双倍金币活动 = 原始金币 * 2;
            int seconds = 0;
            int mins = 0;
            int hours = Integer.parseInt(this.双倍金币持续时间.getText());
            int time = seconds + (mins * 60) + (hours * 60 * 60);
            final String rate = "金币";
            if(time == 0){
                World.scheduleRateDelay(rate, 1);
                for (ChannelServer cservs : ChannelServer.getAllInstances()) {
                    cservs.setExpRate(原始金币);
                for (MapleCharacter chr : cservs.getPlayerStorage().getAllCharacters()) {
                    chr.getStat().recalcLocalStats(chr);
                }
                }
                World.Broadcast.broadcastSmega(MaplePacketCreator.serverNotice(9, 20, "[系统公告]：" + rate + "倍率活动已经结束，已经恢复正常值。"));
                JOptionPane.showMessageDialog(null, "双倍金币已经结束");
            }else{
                World.scheduleRateDelay(rate, time);
            for (ChannelServer cservs : ChannelServer.getAllInstances()) {
                cservs.setExpRate(双倍金币活动);
                for (MapleCharacter chr : cservs.getPlayerStorage().getAllCharacters()) {
                    chr.getStat().recalcLocalStats(chr);
                }
            }
            World.Broadcast.broadcastSmega(MaplePacketCreator.serverNotice(9, 20, "[倍率活动] : 游戏开始 2 倍打怪金币活动，将持续 " + hours + " 小时，请各位玩家狂欢吧！"));
            JOptionPane.showMessageDialog(null, "成功开启双倍金币活动，持续 " + hours + " 小时");
            }
        } else {
            JOptionPane.showMessageDialog(null, "持续时间输入不正确");
        }
    }//GEN-LAST:event_开启双倍金币ActionPerformed

    private void 开启三倍经验ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_开启三倍经验ActionPerformed
        boolean result1 = this.三倍经验持续时间.getText().matches("[0-9]+");
        if (result1) {
            if (三倍经验持续时间.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "持续时间不能为空");
                return;
            }
            double 原始经验 = Double.parseDouble(ServerProperties.getProperty("HuiMs.expRate"));
            double 三倍经验活动 = 原始经验 * 3;
            int seconds = 0;
            int mins = 0;
            int hours = Integer.parseInt(this.三倍经验持续时间.getText());
            int time = seconds + (mins * 60) + (hours * 60 * 60);
            final String rate = "经验";
            if(time == 0){
                World.scheduleRateDelay(rate, 1);
                for (ChannelServer cservs : ChannelServer.getAllInstances()) {
                    cservs.setExpRate(原始经验);
                for (MapleCharacter chr : cservs.getPlayerStorage().getAllCharacters()) {
                    chr.getStat().recalcLocalStats(chr);
                }
                }
                World.Broadcast.broadcastSmega(MaplePacketCreator.serverNotice(9, 20, "[系统公告]：" + rate + "倍率活动已经结束，已经恢复正常值。"));
                JOptionPane.showMessageDialog(null, "三倍经验已经结束");
            }else{
                World.scheduleRateDelay(rate, time);
                for (ChannelServer cservs : ChannelServer.getAllInstances()) {
                    cservs.setExpRate(三倍经验活动);
                for (MapleCharacter chr : cservs.getPlayerStorage().getAllCharacters()) {
                    chr.getStat().recalcLocalStats(chr);
                }
            }
                World.Broadcast.broadcastSmega(MaplePacketCreator.serverNotice(9, 20, "[倍率活动] : 游戏开始 " + 三倍经验活动 + " 倍打怪经验活动，将持续 " + hours + " 小时，请各位玩家狂欢吧！"));
                JOptionPane.showMessageDialog(null, "成功开启三倍经验活动，持续 " + hours + " 小时");
            }
        } else {
            JOptionPane.showMessageDialog(null, "持续时间输入不正确");
        }
    }//GEN-LAST:event_开启三倍经验ActionPerformed

    private void 开启三倍爆率ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_开启三倍爆率ActionPerformed
        boolean result1 = this.三倍爆率持续时间.getText().matches("[0-9]+");
        if (result1) {
            if (三倍爆率持续时间.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "持续时间不能为空");
                return;
            }
            int 原始爆率 = Integer.parseInt(ServerProperties.getProperty("HuiMs.dropRate"));
            int 三倍爆率活动 = 原始爆率 * 3;
            int seconds = 0;
            int mins = 0;
            int hours = Integer.parseInt(this.三倍爆率持续时间.getText());
            int time = seconds + (mins * 60) + (hours * 60 * 60);
            final String rate = "爆率";
            if(time == 0){
                World.scheduleRateDelay(rate, 1);
                for (ChannelServer cservs : ChannelServer.getAllInstances()) {
                    cservs.setExpRate(原始爆率);
                for (MapleCharacter chr : cservs.getPlayerStorage().getAllCharacters()) {
                    chr.getStat().recalcLocalStats(chr);
                }
                }
                World.Broadcast.broadcastSmega(MaplePacketCreator.serverNotice(9, 20, "[系统公告]：" + rate + "倍率活动已经结束，已经恢复正常值。"));
                JOptionPane.showMessageDialog(null, "三倍爆率已经结束");
            }else{
                World.scheduleRateDelay(rate, time);
            for (ChannelServer cservs : ChannelServer.getAllInstances()) {
                cservs.setExpRate(三倍爆率活动);
                for (MapleCharacter chr : cservs.getPlayerStorage().getAllCharacters()) {
                    chr.getStat().recalcLocalStats(chr);
                }
            }
            World.Broadcast.broadcastSmega(MaplePacketCreator.serverNotice(9, 20, "[倍率活动] : 游戏开始 3 倍打怪爆率活动，将持续 " + hours + " 小时，请各位玩家狂欢吧！"));
            JOptionPane.showMessageDialog(null, "成功开启三倍爆率活动，持续 " + hours + " 小时");
            }
        } else {
            JOptionPane.showMessageDialog(null, "持续时间输入不正确");
        }
    }//GEN-LAST:event_开启三倍爆率ActionPerformed

    private void 开启三倍金币ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_开启三倍金币ActionPerformed
        boolean result1 = this.三倍金币持续时间.getText().matches("[0-9]+");
        if (result1) {
            if (三倍金币持续时间.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "持续时间不能为空");
                return;
            }
            int 原始金币 = Integer.parseInt(ServerProperties.getProperty("HuiMs.mesoRate"));
            int 三倍金币活动 = 原始金币 * 3;
            int seconds = 0;
            int mins = 0;
            int hours = Integer.parseInt(this.三倍金币持续时间.getText());
            int time = seconds + (mins * 60) + (hours * 60 * 60);
            final String rate = "金币";
            if(time == 0){
                World.scheduleRateDelay(rate, 1);
                for (ChannelServer cservs : ChannelServer.getAllInstances()) {
                    cservs.setExpRate(原始金币);
                for (MapleCharacter chr : cservs.getPlayerStorage().getAllCharacters()) {
                    chr.getStat().recalcLocalStats(chr);
                }
                }
                World.Broadcast.broadcastSmega(MaplePacketCreator.serverNotice(9, 20, "[系统公告]：" + rate + "倍率活动已经结束，已经恢复正常值。"));
                JOptionPane.showMessageDialog(null, "三倍金币已经结束");
            }else{
                World.scheduleRateDelay(rate, time);
            for (ChannelServer cservs : ChannelServer.getAllInstances()) {
                cservs.setExpRate(三倍金币活动);
                for (MapleCharacter chr : cservs.getPlayerStorage().getAllCharacters()) {
                    chr.getStat().recalcLocalStats(chr);
                }
            }
            World.Broadcast.broadcastSmega(MaplePacketCreator.serverNotice(9, 20, "[倍率活动] : 游戏开始 3 倍打怪金币活动，将持续 " + hours + " 小时，请各位玩家狂欢吧！"));
            JOptionPane.showMessageDialog(null, "成功开启三倍金币活动，持续 " + hours + " 小时");
            }
        } else {
            JOptionPane.showMessageDialog(null, "持续时间输入不正确");
        }
    }//GEN-LAST:event_开启三倍金币ActionPerformed

    private void 经验确认ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_经验确认ActionPerformed
        double exp = Double.parseDouble(经验.getText());
        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
            cserv.setExpRate(exp);
            for (MapleCharacter chr : cserv.getPlayerStorage().getAllCharacters()) {
                chr.getStat().recalcLocalStats(chr);
            }
        }
        System.out.println("经验已修改为" + exp + "倍。");
        JOptionPane.showMessageDialog(null, "成功。");
        // TODO add your handling code here:
    }//GEN-LAST:event_经验确认ActionPerformed

    private void 物品ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_物品ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_物品ActionPerformed

    private void 物品确认ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_物品确认ActionPerformed
        int drop = Integer.valueOf(物品.getText());
        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
            cserv.setDropRate(drop);
            for (MapleCharacter chr : cserv.getPlayerStorage().getAllCharacters()) {
                chr.getStat().recalcLocalStats(chr);
            }
        }
        System.out.println("物品倍率已修改为" + drop + "倍。");
        JOptionPane.showMessageDialog(null, "修改成功。");
        // TODO add your handling code here:
    }//GEN-LAST:event_物品确认ActionPerformed

    private void 金币确认ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_金币确认ActionPerformed
        int meso = Integer.valueOf(金币.getText());
        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
            cserv.setMesoRate(meso);
            for (MapleCharacter chr : cserv.getPlayerStorage().getAllCharacters()) {
                chr.getStat().recalcLocalStats(chr);
            }
        }
        System.out.println("金币倍率已修改为" + meso + "倍。");
        JOptionPane.showMessageDialog(null, "修改成功。");
    }//GEN-LAST:event_金币确认ActionPerformed

    private void 经验加成表序号ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_经验加成表序号ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_经验加成表序号ActionPerformed

    private void 经验加成表数值ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_经验加成表数值ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_经验加成表数值ActionPerformed

    private void 经验加成表修改ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_经验加成表修改ActionPerformed

        PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        boolean result1 = this.经验加成表序号.getText().matches("[0-9]+");
        if (result1) {
            try {
                ps = DatabaseConnection.getConnection().prepareStatement("UPDATE configvalues SET Val = ? WHERE id = ?");
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM configvalues WHERE id = ?");
                ps1.setInt(1, Integer.parseInt(this.经验加成表序号.getText()));
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlString1 = null;
                    sqlString1 = "update configvalues set Val = '" + this.经验加成表数值.getText() + "' where id= " + this.经验加成表序号.getText() + ";";
                    PreparedStatement Val = DatabaseConnection.getConnection().prepareStatement(sqlString1);
                    Val.executeUpdate(sqlString1);
                    刷新经验加成表();
                    HuiMS.GetConfigValues();
                    JOptionPane.showMessageDialog(null, "修改成功已经生效");
                }
            } catch (SQLException ex) {
                Logger.getLogger(HuiMS.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "请选择你要修改的值");
        }
    }//GEN-LAST:event_经验加成表修改ActionPerformed

    private void 游戏经验加成说明ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_游戏经验加成说明ActionPerformed
        JOptionPane.showMessageDialog(null, "<相关说明文>\r\n\r\n"
                + "1:相对应数值为0则为关闭经验加成。\r\n"
                + "2:人气经验 = 人气 * 人气经验加成数值。\r\n"
                + "\r\n");
    }//GEN-LAST:event_游戏经验加成说明ActionPerformed

    private void 禁止登陆开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_禁止登陆开关ActionPerformed
        按键开关("禁止登陆开关", 2013);
        刷新禁止登陆开关();
    }//GEN-LAST:event_禁止登陆开关ActionPerformed

    private void 玩家交易开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_玩家交易开关ActionPerformed
        按键开关("玩家交易开关", 2011);
        刷新玩家交易开关();
    }//GEN-LAST:event_玩家交易开关ActionPerformed

    private void 地图名称开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_地图名称开关ActionPerformed
        按键开关("地图名称开关", 2136);
        刷新地图名称开关();
    }//GEN-LAST:event_地图名称开关ActionPerformed

    private void 上线提醒开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_上线提醒开关ActionPerformed
        按键开关("上线提醒开关", 2021);
        刷新上线提醒开关();
    }//GEN-LAST:event_上线提醒开关ActionPerformed

    private void 指令通知开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_指令通知开关ActionPerformed
        按键开关("指令通知开关", 2028);
        刷新指令通知开关();
    }//GEN-LAST:event_指令通知开关ActionPerformed

    private void 过图存档开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_过图存档开关ActionPerformed
        按键开关("过图存档开关", 2140);
        刷新过图存档时间();
    }//GEN-LAST:event_过图存档开关ActionPerformed

    private void 欢迎弹窗开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_欢迎弹窗开关ActionPerformed
        按键开关("欢迎弹窗开关", 2015);
        刷新欢迎弹窗开关();
    }//GEN-LAST:event_欢迎弹窗开关ActionPerformed

    private void 玩家聊天开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_玩家聊天开关ActionPerformed
        按键开关("玩家聊天开关", 2024);
        刷新玩家聊天开关();
    }//GEN-LAST:event_玩家聊天开关ActionPerformed

    private void 游戏升级快讯ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_游戏升级快讯ActionPerformed
        按键开关("升级快讯开关", 2003);
        刷新升级快讯();
    }//GEN-LAST:event_游戏升级快讯ActionPerformed

    private void 回收地图开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_回收地图开关ActionPerformed
        按键开关("回收地图开关", 2029);
        刷新回收地图开关();
    }//GEN-LAST:event_回收地图开关ActionPerformed

    private void 吸怪检测开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_吸怪检测开关ActionPerformed
        按键开关("吸怪检测开关", 2130);
        刷新吸怪检测开关();
    }//GEN-LAST:event_吸怪检测开关ActionPerformed

    private void 雇佣商人开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_雇佣商人开关ActionPerformed
        按键开关("雇佣商人开关", 2020);
        刷新雇佣商人开关();
    }//GEN-LAST:event_雇佣商人开关ActionPerformed

    private void 屠令广播开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_屠令广播开关ActionPerformed
        按键开关("屠令广播开关", 2016);
        刷新屠令广播开关();
    }//GEN-LAST:event_屠令广播开关ActionPerformed

    private void 游戏喇叭开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_游戏喇叭开关ActionPerformed
        按键开关("游戏喇叭开关", 2009);
        刷新游戏喇叭开关();
    }//GEN-LAST:event_游戏喇叭开关ActionPerformed

    private void 登陆帮助开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_登陆帮助开关ActionPerformed
        按键开关("登陆帮助开关", 2058);
        刷新登陆帮助();
    }//GEN-LAST:event_登陆帮助开关ActionPerformed

    private void 管理隐身开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_管理隐身开关ActionPerformed
        按键开关("管理隐身开关", 2006);
        刷新管理隐身开关();
    }//GEN-LAST:event_管理隐身开关ActionPerformed

    private void 游戏指令开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_游戏指令开关ActionPerformed
        按键开关("游戏指令开关", 2008);
        刷新游戏指令开关();
    }//GEN-LAST:event_游戏指令开关ActionPerformed

    private void 越级打怪开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_越级打怪开关ActionPerformed
        按键开关("越级打怪开关", 2125);
        刷新越级打怪开关();
    }//GEN-LAST:event_越级打怪开关ActionPerformed

    private void 滚动公告开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_滚动公告开关ActionPerformed
        按键开关("滚动公告开关", 2026);
        刷新滚动公告开关();
    }//GEN-LAST:event_滚动公告开关ActionPerformed

    private void 丢出金币开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_丢出金币开关ActionPerformed
        按键开关("丢出金币开关", 2010);
        刷新丢出金币开关();
    }//GEN-LAST:event_丢出金币开关ActionPerformed

    private void 丢出物品开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_丢出物品开关ActionPerformed
        按键开关("丢出物品开关", 2012);
        刷新丢出物品开关();
    }//GEN-LAST:event_丢出物品开关ActionPerformed

    private void 怪物状态开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_怪物状态开关ActionPerformed
        按键开关("怪物状态开关", 2061);
        刷新怪物状态开关();
    }//GEN-LAST:event_怪物状态开关ActionPerformed

    private void 管理加速开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_管理加速开关ActionPerformed
        按键开关("管理加速开关", 2007);
        刷新管理加速开关();
    }//GEN-LAST:event_管理加速开关ActionPerformed

    private void 清空日志ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_清空日志ActionPerformed
        输出窗口.setText("[" + FileoutputUtil.CurrentReadable_Time() + "][=============窗口清空完毕===========]\r\n");
    }//GEN-LAST:event_清空日志ActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        int n = JOptionPane.showConfirmDialog(this, "服务端主控制台一旦退出就会停服！\r\n确定要退出？", "警告", JOptionPane.YES_NO_OPTION);
        if (n == JOptionPane.YES_OPTION) {
            for (handling.channel.ChannelServer cserv : handling.channel.ChannelServer.getAllInstances()) {
                    cserv.closeAllMerchant();//保存雇佣
                }
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (MapleCharacter chr : cserv.getPlayerStorage().getAllCharacters()) {
                    chr.saveToDB(false, false);//保存角色
                }  
            }
            System.exit(0);
        }
        return;
    }//GEN-LAST:event_formWindowClosing

    private void jButton35ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton35ActionPerformed
        // TODO add your handling code here:
        openWindow(Windows.套装系统);
    }//GEN-LAST:event_jButton35ActionPerformed

    private void 修改物品叠加数量ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_修改物品叠加数量ActionPerformed
        // TODO add your handling code here:
        boolean result2 = this.物品叠加数量.getText().matches("[0-9]+");
        PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        if (result2) {
            try {
                ps = DatabaseConnection.getConnection().prepareStatement("UPDATE configvalues SET Val = ? WHERE id = ?");
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM configvalues WHERE id = ?");
                ps1.setInt(1, 2179);
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlString2 = null;
                    sqlString2 = "update configvalues set Val='" + this.物品叠加数量.getText() + "' where id = 2179;";
                    PreparedStatement dropperid = DatabaseConnection.getConnection().prepareStatement(sqlString2);
                    dropperid.executeUpdate(sqlString2);
                    GetConfigValues();
                    刷新物品叠加数量上限();
                    JOptionPane.showMessageDialog(null, "[信息]:修改成功， 本次修改及时生效");
                }
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
        String 弓标子弹叠加代码[] = HuiMS.弓标子弹叠加上限突破.getText().split(",");
            for (int i = 0; i < 弓标子弹叠加代码.length; ++i) {
                MapleItemInformationProvider.slotMaxCache.put(Integer.valueOf(弓标子弹叠加代码[i]), Short.valueOf(物品叠加数量.getText()));
            }
        } else {
            JOptionPane.showMessageDialog(null, "[信息]:请输入你要修改的数据。");
        }
    }//GEN-LAST:event_修改物品叠加数量ActionPerformed

    private void 修改怪物倍率ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_修改怪物倍率ActionPerformed
        // TODO add your handling code here:
        boolean result2 = this.怪物倍率.getText().matches("[0-9]+");
        if (result2) {
        if (怪物倍率.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "怪物倍率不能为空");
            return;
        }
        if (倍怪地图.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "倍怪地图不能为空");
            return;
        }
        int 怪物倍率调整 = Integer.parseInt(this.怪物倍率.getText());
            if(怪物倍率调整 >= 2){
                MapleParty.怪物倍率 = 怪物倍率调整;
                MapleParty.怪物倍怪 = true;
                World.Broadcast.broadcastSmega(MaplePacketCreator.serverNotice(9, 20, "[倍怪活动] : 游戏开始 "+怪物倍率调整 +" 倍打怪活动.请各位玩家狂欢吧！"));
                for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                    for (MapleCharacter chr : cserv.getPlayerStorage().getAllCharacters()) {
                        chr.getMap().respawn(true);
                    }
                }  
            }else{
                MapleParty.怪物倍率 = 1;
                MapleParty.怪物倍怪 = true;
                World.Broadcast.broadcastSmega(MaplePacketCreator.serverNotice(9, 20, "[倍怪活动] : 游戏倍怪活动已经结束！"));
                for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                    for (MapleCharacter chr : cserv.getPlayerStorage().getAllCharacters()) {
                        chr.getMap().respawn(true);
                    }
                }
            } 
        }
    }//GEN-LAST:event_修改怪物倍率ActionPerformed

    private void 经验确认1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_经验确认1ActionPerformed
        // TODO add your handling code here:
        boolean result2 = this.区间一经验倍率.getText().matches("[0-9]+");
        boolean result3 = this.区间一最低等级.getText().matches("[0-9]+");
        boolean result4 = this.区间一最高等级.getText().matches("[0-9]+");
        if (result2) {
        if (区间一经验倍率.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "区间一经验倍率不能为空");
            return;
        }
        }
        if (result3) {
        if (区间一最低等级.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "区间一最低等级不能为空");
            return;
        }
        }
        if (result4) {
        if (区间一最高等级.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "区间一最高等级不能为空");
            return;
        }
        }
        int exp = Integer.valueOf(区间一经验倍率.getText());
        int minlevel = Integer.valueOf(区间一最低等级.getText());
        int maxlevel = Integer.valueOf(区间一最高等级.getText());
        ServerConfig.BeiShu1 = exp;
        ServerConfig.BeiShu1Minlevel = minlevel;
        ServerConfig.BeiShu1Maxlevel = maxlevel;
        for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
            for (MapleCharacter chr : cserv.getPlayerStorage().getAllCharacters()) {
                chr.getStat().recalcLocalStats(chr);
            }
        }
        System.out.println("区间一经验倍率已修改为" + ServerConfig.BeiShu1 + "倍。");
        JOptionPane.showMessageDialog(null, "成功将区间一经验倍率已修改为" + ServerConfig.BeiShu1 + "倍。");
    }//GEN-LAST:event_经验确认1ActionPerformed

    private void 经验确认2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_经验确认2ActionPerformed
        // TODO add your handling code here:
        boolean result2 = this.区间二经验倍率.getText().matches("[0-9]+");
        boolean result3 = this.区间二最低等级.getText().matches("[0-9]+");
        boolean result4 = this.区间二最高等级.getText().matches("[0-9]+");
        if (result2) {
        if (区间二经验倍率.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "区间二经验倍率不能为空");
            return;
        }
        }
        if (result3) {
        if (区间二最低等级.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "区间二最低等级不能为空");
            return;
        }
        }
        if (result4) {
        if (区间二最高等级.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "区间二最高等级不能为空");
            return;
        }
        }
        int exp = Integer.valueOf(区间二经验倍率.getText());
        int minlevel = Integer.valueOf(区间二最低等级.getText());
        int maxlevel = Integer.valueOf(区间二最高等级.getText());
        ServerConfig.BeiShu2 = exp;
        ServerConfig.BeiShu2Minlevel = minlevel;
        ServerConfig.BeiShu2Maxlevel = maxlevel;
        for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
            for (MapleCharacter chr : cserv.getPlayerStorage().getAllCharacters()) {
                chr.getStat().recalcLocalStats(chr);
            }
        }
        System.out.println("区间二经验倍率已修改为" + ServerConfig.BeiShu2 + "倍。");
        JOptionPane.showMessageDialog(null, "成功将区间二经验倍率已修改为" + ServerConfig.BeiShu2 + "倍。");
    }//GEN-LAST:event_经验确认2ActionPerformed

    private void 经验确认3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_经验确认3ActionPerformed
        // TODO add your handling code here:
        boolean result2 = this.区间三经验倍率.getText().matches("[0-9]+");
        boolean result3 = this.区间三最低等级.getText().matches("[0-9]+");
        boolean result4 = this.区间三最高等级.getText().matches("[0-9]+");
        if (result2) {
        if (区间三经验倍率.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "区间三经验倍率不能为空");
            return;
        }
        }
        if (result3) {
        if (区间三最低等级.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "区间三最低等级不能为空");
            return;
        }
        }
        if (result4) {
        if (区间三最高等级.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "区间三最高等级不能为空");
            return;
        }
        }
        int exp = Integer.valueOf(区间三经验倍率.getText());
        int minlevel = Integer.valueOf(区间三最低等级.getText());
        int maxlevel = Integer.valueOf(区间三最高等级.getText());
        ServerConfig.BeiShu3 = exp;
        ServerConfig.BeiShu3Minlevel = minlevel;
        ServerConfig.BeiShu3Maxlevel = maxlevel;
        for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
            for (MapleCharacter chr : cserv.getPlayerStorage().getAllCharacters()) {
                chr.getStat().recalcLocalStats(chr);
            }
        }
        System.out.println("区间三经验倍率已修改为" + ServerConfig.BeiShu3 + "倍。");
        JOptionPane.showMessageDialog(null, "成功将区间三经验倍率已修改为" + ServerConfig.BeiShu3 + "倍。");
    }//GEN-LAST:event_经验确认3ActionPerformed

    private void playerTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_playerTableMouseClicked
        int i = playerTable.getSelectedRow();
        String a = playerTable.getValueAt(i, 1).toString();//名字
        String a0 = playerTable.getValueAt(i, 5).toString();//地图
        String a1 = playerTable.getValueAt(i, 7).toString();//点券
        String a2 = playerTable.getValueAt(i, 8).toString();//抵用
        String a3 = playerTable.getValueAt(i, 9).toString();//元宝
        角色名称编辑框.setText(a);
        角色所在地图编辑.setText(a0);
        角色点券编辑框.setText(a1);
        角色抵用编辑框.setText(a2);
        角色元宝编辑框.setText(a3);
    }//GEN-LAST:event_playerTableMouseClicked

    private void 角色名称编辑框ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_角色名称编辑框ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_角色名称编辑框ActionPerformed

    private void 角色点券编辑框ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_角色点券编辑框ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_角色点券编辑框ActionPerformed

    private void 角色抵用编辑框ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_角色抵用编辑框ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_角色抵用编辑框ActionPerformed

    private void 角色元宝编辑框ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_角色元宝编辑框ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_角色元宝编辑框ActionPerformed

    private void 角色所在地图编辑ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_角色所在地图编辑ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_角色所在地图编辑ActionPerformed

    private void 全员下线ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_全员下线ActionPerformed
        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
            cserv.getPlayerStorage().disconnectAll(true);
        }
        JOptionPane.showMessageDialog(null, "成功");
    }//GEN-LAST:event_全员下线ActionPerformed

    private void 个人玩家下线ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_个人玩家下线ActionPerformed
        final String name = 角色名称编辑框.getText();
            final int ch = Find.findChannel(name);
            if (ch <= 0) {
                JOptionPane.showMessageDialog(null, "该玩家不在线上");
                return;
            }
            final MapleCharacter victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            if (victim != null) {
                victim.getClient().disconnect(true, false);
                victim.getClient().getSession().close();
                JOptionPane.showMessageDialog(null, "成功");
            }else {
                JOptionPane.showMessageDialog(null, "该玩家不在线上");
            }
    }//GEN-LAST:event_个人玩家下线ActionPerformed

    private void 传送玩家到自由ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_传送玩家到自由ActionPerformed
        final String name = 角色名称编辑框.getText();
            final int ch = Find.findChannel(name);
            if (ch <= 0) {
                JOptionPane.showMessageDialog(null, "该玩家不在线上");
                return;
            }
            final MapleCharacter victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            if (victim != null) {
                victim.changeMap(910000000);
                JOptionPane.showMessageDialog(null, "成功");
            }else {
                JOptionPane.showMessageDialog(null, "该玩家不在线上");
            }
    }//GEN-LAST:event_传送玩家到自由ActionPerformed

    private void 关玩家到小黑屋ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_关玩家到小黑屋ActionPerformed
        final String name = 角色名称编辑框.getText();
            final int ch = Find.findChannel(name);
            if (ch <= 0) {
                JOptionPane.showMessageDialog(null, "该玩家不在线上");
                return;
            }
            final MapleCharacter victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            if (victim != null) {
                victim.changeMap(180000001);
                JOptionPane.showMessageDialog(null, "成功");
            }else {
                JOptionPane.showMessageDialog(null, "该玩家不在线上");
            }
        
    }//GEN-LAST:event_关玩家到小黑屋ActionPerformed

    private void 传送玩家到指定地图ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_传送玩家到指定地图ActionPerformed
        final String name = 角色名称编辑框.getText();
            final int ch = Find.findChannel(name);
            if (ch <= 0) {
                JOptionPane.showMessageDialog(null, "该玩家不在线上");
                return;
            }
            final MapleCharacter victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            if (victim != null) {
                if (角色所在地图编辑.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "地图代码不能为空");
                    return;
                }
                victim.changeMap(Integer.parseInt(角色所在地图编辑.getText()));
                JOptionPane.showMessageDialog(null, "成功");
            }else {
                JOptionPane.showMessageDialog(null, "该玩家不在线上");
            }
    }//GEN-LAST:event_传送玩家到指定地图ActionPerformed

    private void 一键满技能ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_一键满技能ActionPerformed
        final String name = 角色名称编辑框.getText();
            final int ch = Find.findChannel(name);
            if (ch <= 0) {
                JOptionPane.showMessageDialog(null, "该玩家不在线上");
                return;
            }
            final MapleCharacter victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            if (victim != null) {
                if (角色名称编辑框.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "角色名字不能为空");
                    return;
                }
                victim.maxSkills();
                JOptionPane.showMessageDialog(null, "成功");
            }else {
                JOptionPane.showMessageDialog(null, "该玩家不在线上");
            }
    }//GEN-LAST:event_一键满技能ActionPerformed

    private void 修改玩家信息ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_修改玩家信息ActionPerformed
        final String name = 角色名称编辑框.getText();
            final int ch = Find.findChannel(name);
            if (ch <= 0) {
                JOptionPane.showMessageDialog(null, "该玩家不在线上");
                return;
            }
            final MapleCharacter victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            if (victim != null) {
                victim.modifyCSPoints(1,Integer.parseInt(角色点券编辑框.getText()));
                victim.modifyCSPoints(2,Integer.parseInt(角色抵用编辑框.getText()));
                victim.setmoneyb(Integer.parseInt(角色元宝编辑框.getText()));
                victim.saveToDB(false, false);//保存角色
                JOptionPane.showMessageDialog(null, "成功");
            }else {
                JOptionPane.showMessageDialog(null, "该玩家不在线上");
            }
    }//GEN-LAST:event_修改玩家信息ActionPerformed

    private void 物品掉落持续时间ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_物品掉落持续时间ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_物品掉落持续时间ActionPerformed

    private void 刷物品() {
        try {
            String 名字;
            if ("玩家名字".equals(个人发送物品玩家名字.getText())) {
                名字 = "";
            } else {
                名字 = 个人发送物品玩家名字.getText();
            }
            int 物品ID;
            if ("物品ID".equals(个人发送物品代码.getText())) {
                物品ID = 0;
            } else {
                物品ID = Integer.parseInt(个人发送物品代码.getText());
            }
            int 数量;
            if ("数量".equals(个人发送物品数量.getText())) {
                数量 = 0;
            } else {
                数量 = Integer.parseInt(个人发送物品数量.getText());
            }
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            MapleInventoryType type = GameConstants.getInventoryType(物品ID);
            for (ChannelServer cserv1 : ChannelServer.getAllInstances()) {
                for (MapleCharacter mch : cserv1.getPlayerStorage().getAllCharacters()) {
                    if (mch.getName().equals(名字)) {
                        if (数量 >= 0) {
                            if (!MapleInventoryManipulator.checkSpace(mch.getClient(), 物品ID, 数量, "")) {
                                return;
                            }
                            if (type.equals(MapleInventoryType.EQUIP)
                                    || type.equals(MapleInventoryType.CASH) && 物品ID >= 5000000 && 物品ID <= 5000100) {
                                final Equip item = (Equip) (ii.getEquipById(物品ID));
                                if (ii.isCash(物品ID)) {
                                    item.setUniqueId(1);
                                }
                                final String name = ii.getName(物品ID);
                                if (物品ID / 10000 == 114 && name != null && name.length() > 0) { //medal
                                    final String msg = "你已获得称号 <" + name + ">";
                                    mch.getClient().getPlayer().dropMessage(5, msg);
                                }
                                MapleInventoryManipulator.addbyItem(mch.getClient(), item.copy());
                            } else {
                                MapleInventoryManipulator.addById(mch.getClient(), 物品ID, (short) 数量, "", null, (byte) 0);
                            }
                        } else {
                            MapleInventoryManipulator.removeById(mch.getClient(), GameConstants.getInventoryType(物品ID), 物品ID, -数量, true, false);
                        }
                        mch.getClient().getSession().write(MaplePacketCreator.getShowItemGain(物品ID, (short) 数量, true));

                    }
                }
            }
            个人发送物品玩家名字.setText("");
            个人发送物品代码.setText("");
            个人发送物品数量.setText("");
            JOptionPane.showMessageDialog(null, "[信息]:发送成功。");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "[信息]:错误!" + e);
        }
    }

    private void 刷物品2() {
        try {
            int 数量;
            int 物品ID;
            物品ID = Integer.parseInt(全服发送物品代码.getText());
            数量 = Integer.parseInt(全服发送物品数量.getText());
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            MapleInventoryType type = GameConstants.getInventoryType(物品ID);
            for (ChannelServer cserv1 : ChannelServer.getAllInstances()) {
                for (MapleCharacter mch : cserv1.getPlayerStorage().getAllCharacters()) {
                    if (数量 >= 0) {
                        if (!MapleInventoryManipulator.checkSpace(mch.getClient(), 物品ID, 数量, "")) {
                            return;
                        }
                        if (type.equals(MapleInventoryType.EQUIP)
                                || type.equals(MapleInventoryType.CASH) && 物品ID >= 5000000 && 物品ID <= 5000100) {
                            final Equip item = (Equip) (ii.getEquipById(物品ID));
                            if (ii.isCash(物品ID)) {
                                item.setUniqueId(1);
                            }
                            final String name = ii.getName(物品ID);
                            MapleInventoryManipulator.addbyItem(mch.getClient(), item.copy());
                        } else {
                            MapleInventoryManipulator.addById(mch.getClient(), 物品ID, (short) 数量, "", null, (byte) 0);
                        }
                    } else {
                        MapleInventoryManipulator.removeById(mch.getClient(), GameConstants.getInventoryType(物品ID), 物品ID, -数量, true, false);
                    }
//                    mch.getClient().getSession.write(MaplePacketCreator.getShowItemGain(物品ID, (short) 数量, true));
                    mch.getClient().getSession().write(MaplePacketCreator.getShowItemGain(物品ID, (short) 数量, true));
                }
            }
            全服发送物品代码.setText("");
            全服发送物品数量.setText("");
            JOptionPane.showMessageDialog(null, "[信息]:发送成功。");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "[信息]:错误!" + e);
        }
    }

    private void 刷装备2(int a) {
        try {
            int 物品ID;
            if ("物品ID".equals(全服发送装备物品ID.getText())) {
                物品ID = 0;
            } else {
                物品ID = Integer.parseInt(全服发送装备物品ID.getText());
            }
            int 力量;
            if ("力量".equals(全服发送装备装备力量.getText())) {
                力量 = 0;
            } else {
                力量 = Integer.parseInt(全服发送装备装备力量.getText());
            }
            int 敏捷;
            if ("敏捷".equals(全服发送装备装备敏捷.getText())) {
                敏捷 = 0;
            } else {
                敏捷 = Integer.parseInt(全服发送装备装备敏捷.getText());
            }
            int 智力;
            if ("智力".equals(全服发送装备装备智力.getText())) {
                智力 = 0;
            } else {
                智力 = Integer.parseInt(全服发送装备装备智力.getText());
            }
            int 运气;
            if ("运气".equals(全服发送装备装备运气.getText())) {
                运气 = 0;
            } else {
                运气 = Integer.parseInt(全服发送装备装备运气.getText());
            }
            int HP;
            if ("HP设置".equals(全服发送装备装备HP.getText())) {
                HP = 0;
            } else {
                HP = Integer.parseInt(全服发送装备装备HP.getText());
            }
            int MP;
            if ("MP设置".equals(全服发送装备装备MP.getText())) {
                MP = 0;
            } else {
                MP = Integer.parseInt(全服发送装备装备MP.getText());
            }
            int 可加卷次数;
            if ("加卷次数".equals(全服发送装备装备加卷.getText())) {
                可加卷次数 = 0;
            } else {
                可加卷次数 = Integer.parseInt(全服发送装备装备加卷.getText());
            }

            String 制作人名字;
            if ("制作人".equals(全服发送装备装备制作人.getText())) {
                制作人名字 = "";
            } else {
                制作人名字 = 全服发送装备装备制作人.getText();
            }
            int 给予时间;
            if ("给予物品时间".equals(全服发送装备装备给予时间.getText())) {
                给予时间 = 0;
            } else {
                给予时间 = Integer.parseInt(全服发送装备装备给予时间.getText());
            }
            String 是否可以交易 = 全服发送装备装备可否交易.getText();
            int 攻击力;
            if ("攻击力".equals(全服发送装备装备攻击力.getText())) {
                攻击力 = 0;
            } else {
                攻击力 = Integer.parseInt(全服发送装备装备攻击力.getText());
            }
            int 魔法力;
            if ("魔法力".equals(全服发送装备装备魔法力.getText())) {
                魔法力 = 0;
            } else {
                魔法力 = Integer.parseInt(全服发送装备装备魔法力.getText());
            }
            int 物理防御;
            if ("物理防御".equals(全服发送装备装备物理防御.getText())) {
                物理防御 = 0;
            } else {
                物理防御 = Integer.parseInt(全服发送装备装备物理防御.getText());
            }
            int 魔法防御;
            if ("魔法防御".equals(全服发送装备装备魔法防御.getText())) {
                魔法防御 = 0;
            } else {
                魔法防御 = Integer.parseInt(全服发送装备装备魔法防御.getText());
            }
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            MapleInventoryType type = GameConstants.getInventoryType(物品ID);
            for (ChannelServer cserv1 : ChannelServer.getAllInstances()) {
                for (MapleCharacter mch : cserv1.getPlayerStorage().getAllCharacters()) {
                    if (a == 1) {
                        if (1 >= 0) {
                            if (!MapleInventoryManipulator.checkSpace(mch.getClient(), 物品ID, 1, "")) {
                                return;
                            }
                            if (type.equals(MapleInventoryType.EQUIP)
                                    || type.equals(MapleInventoryType.CASH) && 物品ID >= 5000000 && 物品ID <= 5000100) {
                                final Equip item = (Equip) (ii.getEquipById(物品ID));
                                if (ii.isCash(物品ID)) {
                                    item.setUniqueId(1);
                                }
                                if (力量 > 0 && 力量 <= 32767) {
                                    item.setStr((short) (力量));
                                }
                                if (敏捷 > 0 && 敏捷 <= 32767) {
                                    item.setDex((short) (敏捷));
                                }
                                if (智力 > 0 && 智力 <= 32767) {
                                    item.setInt((short) (智力));
                                }
                                if (运气 > 0 && 运气 <= 32767) {
                                    item.setLuk((short) (运气));
                                }
                                if (攻击力 > 0 && 攻击力 <= 32767) {
                                    item.setWatk((short) (攻击力));
                                }
                                if (魔法力 > 0 && 魔法力 <= 32767) {
                                    item.setMatk((short) (魔法力));
                                }
                                if (物理防御 > 0 && 物理防御 <= 32767) {
                                    item.setWdef((short) (物理防御));
                                }
                                if (魔法防御 > 0 && 魔法防御 <= 32767) {
                                    item.setMdef((short) (魔法防御));
                                }
                                if (HP > 0 && HP <= 30000) {
                                    item.setHp((short) (HP));
                                }
                                if (MP > 0 && MP <= 30000) {
                                    item.setMp((short) (MP));
                                }
                                if ("1".equals(是否可以交易)) {
                                    item.setFlag((byte)(item.getFlag() | ItemFlag.LOCK.getValue()));
                                }
                                if (给予时间 > 0) {
                                    item.setExpiration(System.currentTimeMillis() + (long)(给予时间 * 24 * 60 * 60 * 1000));
                                }
                                if (可加卷次数 > 0) {
                                    item.setUpgradeSlots((byte) (可加卷次数));
                                }
                                if (制作人名字 != null) {
                                    item.setOwner(制作人名字);
                                }
                                final String name = ii.getName(物品ID);
                                if (物品ID / 10000 == 114 && name != null && name.length() > 0) { //medal
                                    final String msg = "你已获得称号 <" + name + ">";
                                    mch.getClient().getPlayer().dropMessage(5, msg);
                                }
                                MapleInventoryManipulator.addbyItem(mch.getClient(), item.copy());
                            } else {
                           //     MapleInventoryManipulator.addById(mch.getClient(), 物品ID, (short) 1, "", null, 给予时间, "");
                                MapleInventoryManipulator.addById(mch.getClient(), 物品ID, (short) 1, "", null, (byte) 0);

                            }
                        } else {
                            MapleInventoryManipulator.removeById(mch.getClient(), GameConstants.getInventoryType(物品ID), 物品ID, -1, true, false);
                        }
                        mch.getClient().getSession().write(MaplePacketCreator.getShowItemGain(物品ID, (short) 1, true));
                    } else if (mch.getName().equals(发送装备玩家姓名.getText())) {
                        if (1 >= 0) {
                            if (!MapleInventoryManipulator.checkSpace(mch.getClient(), 物品ID, 1, "")) {
                                return;
                            }
                            if (type.equals(MapleInventoryType.EQUIP)
                                    || type.equals(MapleInventoryType.CASH) && 物品ID >= 5000000 && 物品ID <= 5000100) {
                                final Equip item = (Equip) (ii.getEquipById(物品ID));
                                if (ii.isCash(物品ID)) {
                                    item.setUniqueId(1);
                                }
                                if (力量 > 0 && 力量 <= 32767) {
                                    item.setStr((short) (力量));
                                }
                                if (敏捷 > 0 && 敏捷 <= 32767) {
                                    item.setDex((short) (敏捷));
                                }
                                if (智力 > 0 && 智力 <= 32767) {
                                    item.setInt((short) (智力));
                                }
                                if (运气 > 0 && 运气 <= 32767) {
                                    item.setLuk((short) (运气));
                                }
                                if (攻击力 > 0 && 攻击力 <= 32767) {
                                    item.setWatk((short) (攻击力));
                                }
                                if (魔法力 > 0 && 魔法力 <= 32767) {
                                    item.setMatk((short) (魔法力));
                                }
                                if (物理防御 > 0 && 物理防御 <= 32767) {
                                    item.setWdef((short) (物理防御));
                                }
                                if (魔法防御 > 0 && 魔法防御 <= 32767) {
                                    item.setMdef((short) (魔法防御));
                                }
                                if (HP > 0 && HP <= 30000) {
                                    item.setHp((short) (HP));
                                }
                                if (MP > 0 && MP <= 30000) {
                                    item.setMp((short) (MP));
                                }
                                if ("1".equals(是否可以交易)) {
                                    item.setFlag((byte)(item.getFlag() | ItemFlag.LOCK.getValue()));
                                }
                                if (给予时间 > 0) {
                                    item.setExpiration(System.currentTimeMillis() + (long)(给予时间 * 24 * 60 * 60 * 1000));
                                }
                                if (可加卷次数 > 0) {
                                    item.setUpgradeSlots((byte) (可加卷次数));
                                }
                                if (制作人名字 != null) {
                                    item.setOwner(制作人名字);
                                }
                                final String name = ii.getName(物品ID);
                                if (物品ID / 10000 == 114 && name != null && name.length() > 0) { //medal
                                    final String msg = "你已获得称号 <" + name + ">";
                                    mch.getClient().getPlayer().dropMessage(5, msg);
                                }
                                MapleInventoryManipulator.addbyItem(mch.getClient(), item.copy());
                            } else {
                                MapleInventoryManipulator.addById(mch.getClient(), 物品ID, (short) 1, "", null, (byte) 0);
                            }
                        } else {
                            MapleInventoryManipulator.removeById(mch.getClient(), GameConstants.getInventoryType(物品ID), 物品ID, -1, true, false);
                        }
                        mch.getClient().getSession().write(MaplePacketCreator.getShowItemGain(物品ID, (short) 1, true));

                    }
                }
            }
            JOptionPane.showMessageDialog(null, "[信息]:发送成功。");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "[信息]:错误!" + e);
        }
    }

    private void 发送福利(int a) {
        boolean result1 = this.a1.getText().matches("[0-9]+");
        if (result1) {
            int 数量;
            if ("100000000".equals(a1.getText())) {
                数量 = 100;
            } else {
                数量 = Integer.parseInt(a1.getText());
            }
            if (数量 <= 0 || 数量 > 999999999) {
                return;
            }
            String 类型 = "";
            for (ChannelServer cserv1 : ChannelServer.getAllInstances()) {
                for (MapleCharacter mch : cserv1.getPlayerStorage().getAllCharacters()) {

                    switch (a) {
                        case 1:
                            类型 = "点券";
                            mch.modifyCSPoints(1, 数量, true);
                            break;
                        case 2:
                            类型 = "抵用券";
                            mch.modifyCSPoints(2, 数量, true);
                            break;
                        case 3:
                            类型 = "金币";
                            mch.gainMeso(数量, true);
                            break;
                        case 4:
                            类型 = "经验";
                            mch.gainExp(数量, false, false, false);
                            break;
                        case 5:
                            类型 = "人气";
                            mch.addFame(数量);
                            break;
                        case 6:
                            类型 = "豆豆";
                            mch.gainBeans(数量);
                            break;
                        default:
                            break;
                    }
                    mch.startMapEffect("管理员发放 " + 数量 + " " + 类型 + "给在线的所有玩家！", 5121009);
                }
            }
            JOptionPane.showMessageDialog(null, "[信息]:发放 " + 数量 + " " + 类型 + "给在线的所有玩家。");
            a1.setText("");
            JOptionPane.showMessageDialog(null, "发送成功");
        } else {
            JOptionPane.showMessageDialog(null, "[信息]:请输入要发送数量。");
        }
    }

    public void 刷新反应堆() {
        for (int i = ((DefaultTableModel) (this.反应堆.getModel())).getRowCount() - 1; i >= 0; i--) {
            ((DefaultTableModel) (this.反应堆.getModel())).removeRow(i);
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = null;

            ResultSet rs = null;
            ps = con.prepareStatement("SELECT * FROM reactordrops ");
            rs = ps.executeQuery();
            while (rs.next()) {
                ((DefaultTableModel) 反应堆.getModel()).insertRow(反应堆.getRowCount(), new Object[]{rs.getInt("reactordropid"), rs.getInt("reactorid"), rs.getInt("itemid"), rs.getInt("chance"), MapleItemInformationProvider.getInstance().getName(rs.getInt("itemid"))});
            }
        } catch (SQLException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
        反应堆.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int i = 反应堆.getSelectedRow();
                String a = 反应堆.getValueAt(i, 0).toString();
                String a1 = 反应堆.getValueAt(i, 1).toString();
                String a2 = 反应堆.getValueAt(i, 2).toString();
                String a3 = 反应堆.getValueAt(i, 3).toString();
                反应堆序列号.setText(a);
                反应堆代码.setText(a1);
                反应堆物品.setText(a2);
                反应堆概率.setText(a3);
            }
        });
    }

    //新增
    public void runTool(final Tools tool) {
        if (tools.contains(tool)) {
            JOptionPane.showMessageDialog(null, "工具已在运行。");
        } else {
            tools.add(tool);
            Thread t = new Thread() {
                @Override
                public void run() {
                    switch (tool) {

                        case DumpItems:
                            DumpItems.main(new String[0]);
                            break;
                        case DumpCashShop:
                            DumpItems.main(new String[0]);
                            break;
                        case DumpMobSkills:
                            DumpMobSkills.main(new String[0]);
                            break;
                        case DumpOxQuizData: {
                            try {
                                DumpOxQuizData.main(new String[0]);
                            } catch (IOException ex) {
                                Logger.getLogger(HuiMS.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (SQLException ex) {
                                Logger.getLogger(HuiMS.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        break;
                        case DumpQuests:
                            DumpQuests.main(new String[0]);
                            break;
                        case MonsterDropCreator: {
                            try {
                                MonsterDropCreator.main(new String[0]);
                            } catch (IOException ex) {
                                Logger.getLogger(HuiMS.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (NotBoundException ex) {
                                Logger.getLogger(HuiMS.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (InstanceAlreadyExistsException ex) {
                                Logger.getLogger(HuiMS.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (MBeanRegistrationException ex) {
                                Logger.getLogger(HuiMS.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (NotCompliantMBeanException ex) {
                                Logger.getLogger(HuiMS.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (MalformedObjectNameException ex) {
                                Logger.getLogger(HuiMS.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        break;

                    }
                    tools.remove(tool);
                }
            };
            t.start();
        }
    }

    public enum Tools {
        DumpCashShop,
        DumpItems,
        FixCharSets,
        DumpMobSkills,
        DumpNpcNames,
        DumpOxQuizData,
        DumpQuests,
        MonsterDropCreator;
    }
    public void openWindow(final Windows w) {
        if (!windows.containsKey(w)) {
            switch (w) {
                case 商城管理控制台:
                    windows.put(w, new 商城管理控制台());
                    break;
                case 商店管理控制台:
                    windows.put(w, new 商店管理控制台());
                    break;
                case 广播系统控制台:
                    windows.put(w, new 广播系统控制台());
                    break;
                case 游戏抽奖工具:
                    windows.put(w, new 游戏抽奖工具());
                    break;
                case 账号管理工具:
                    windows.put(w, new 账号管理工具());
                    break;
                case OX答题控制台:
                    windows.put(w, new OX答题控制台());
                    break;
                case 卡密制作工具:
                    windows.put(w, new 卡密制作工具());
                    break;
                case 椅子控制台:
                    windows.put(w, new 椅子控制台());
                    break;
                case 鱼来鱼往:
                    windows.put(w, new 鱼来鱼往());
                    break;
                case 副本控制台:
                    windows.put(w, new 副本控制台());
                    break;
                case 金锤子成功率控制台:
                    windows.put(w, new 金锤子成功率控制台());
                    break;
                case 锻造控制台:
                    windows.put(w, new 锻造控制台());
                    break;
                case 活动控制台:
                    windows.put(w, new 活动控制台());
                    break;
                case 物品删除管理工具:
                    windows.put(w, new 物品删除管理工具());
                    break;
                case 套装系统:
                    windows.put(w, new 套装系统());
                    break;   
                case 删除自添加NPC工具:
                    windows.put(w, new 删除自添加NPC工具());
                    break;
                case 代码查询工具:
                    windows.put(w, new 代码查询工具());
                    break;
                case 一键还原:
                    windows.put(w, new 一键还原());
                    break;
                case 基址计算工具:
                    windows.put(w, new 基址计算工具());
                    break;
                case 药水冷却时间控制台:
                    windows.put(w, new 药水冷却时间控制台());
                    break;
                case 永恒重生装备控制台:
                    windows.put(w, new 永恒重生装备控制台());
                    break;
                default:
                    return;
            }
            windows.get(w).setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        }
        windows.get(w).setVisible(true);
    }
    
    public enum Windows {
        基址计算工具,
        广播系统控制台,
        商店管理控制台,
        套装系统,
        一键还原,
        商城管理控制台,
        锻造控制台,
        卡密制作工具,
        代码查询工具,
        活动控制台,
        删除自添加NPC工具,
        游戏抽奖工具,
        药水冷却时间控制台,
        金锤子成功率控制台,
        永恒重生装备控制台,
        椅子控制台,
        鱼来鱼往,
        OX答题控制台,
        物品删除管理工具,
        账号管理工具,
        副本控制台,
        CashShopItemEditor,
        CashShopItemAdder,
        DropDataAdder,
        DropDataEditor,;
    }

    public void 刷新怪物卡片() {
        for (int i = ((DefaultTableModel) (this.怪物爆物.getModel())).getRowCount() - 1; i >= 0; i--) {
            ((DefaultTableModel) (this.怪物爆物.getModel())).removeRow(i);
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = null;
            ResultSet rs = null;
            ps = con.prepareStatement("SELECT * FROM drop_data WHERE itemid >=2380000&& itemid <2390000");
            rs = ps.executeQuery();
            while (rs.next()) {
                ((DefaultTableModel) 怪物爆物.getModel()).insertRow(怪物爆物.getRowCount(), new Object[]{
                    rs.getInt("id"),
                    rs.getInt("dropperid"),
                    //MapleLifeFactory.getMonster(rs.getInt("dropperid")),
                    rs.getInt("itemid"),
                    rs.getInt("chance"),
                    MapleItemInformationProvider.getInstance().getName(rs.getInt("itemid"))
                });
            }
        } catch (SQLException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
        怪物爆物.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int i = 怪物爆物.getSelectedRow();
                String a = 怪物爆物.getValueAt(i, 0).toString();
                String a1 = 怪物爆物.getValueAt(i, 1).toString();
                String a2 = 怪物爆物.getValueAt(i, 2).toString();
                String a3 = 怪物爆物.getValueAt(i, 3).toString();
                //String a4 = 怪物爆物.getValueAt(i, 4).toString();
                怪物爆物序列号.setText(a);
                怪物爆物怪物代码.setText(a1);
                怪物爆物物品代码.setText(a2);
                怪物爆物爆率.setText(a3);
                //怪物爆物物品名称.setText(a4);
            }
        });
    }

    public void 刷新世界爆物() {
        for (int i = ((DefaultTableModel) (this.世界爆物.getModel())).getRowCount() - 1; i >= 0; i--) {
            ((DefaultTableModel) (this.世界爆物.getModel())).removeRow(i);
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = null;
            ResultSet rs = null;
            ps = con.prepareStatement("SELECT * FROM drop_data_global WHERE itemid !=0");
            rs = ps.executeQuery();
            while (rs.next()) {
                ((DefaultTableModel) 世界爆物.getModel()).insertRow(世界爆物.getRowCount(), new Object[]{
                    rs.getInt("id"),
                    rs.getInt("itemid"),
                    rs.getString("chance"),
                    MapleItemInformationProvider.getInstance().getName(rs.getInt("itemid"))
                });
            }
            世界爆物.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    int i = 世界爆物.getSelectedRow();
                    String a = 世界爆物.getValueAt(i, 0).toString();
                    String a1 = 世界爆物.getValueAt(i, 1).toString();
                    String a2 = 世界爆物.getValueAt(i, 2).toString();
                    世界爆物序列号.setText(a);
                    世界爆物物品代码.setText(a1);
                    世界爆物爆率.setText(a2);
                }
            });

        } catch (SQLException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void 刷新指定怪物爆物() {
        boolean result = this.查询怪物掉落代码.getText().matches("[0-9]+");
        if (result) {
            if (Integer.parseInt(this.查询怪物掉落代码.getText()) < 0) {
                JOptionPane.showMessageDialog(null, "请填写正确的值");
            }
            for (int i = ((DefaultTableModel) (this.怪物爆物.getModel())).getRowCount() - 1; i >= 0; i--) {
                ((DefaultTableModel) (this.怪物爆物.getModel())).removeRow(i);
            }
            try {
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = null;

                ResultSet rs = null;
                ps = con.prepareStatement("SELECT * FROM drop_data WHERE dropperid =  " + Integer.parseInt(this.怪物爆物怪物代码.getText()) + "");
                rs = ps.executeQuery();
                while (rs.next()) {
                    ((DefaultTableModel) 怪物爆物.getModel()).insertRow(怪物爆物.getRowCount(), new Object[]{rs.getInt("id"), rs.getInt("dropperid"), rs.getInt("itemid"), rs.getInt("chance"), MapleItemInformationProvider.getInstance().getName(rs.getInt("itemid"))});
                }
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
            怪物爆物.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    int i = 怪物爆物.getSelectedRow();
                    String a = 怪物爆物.getValueAt(i, 0).toString();
                    String a1 = 怪物爆物.getValueAt(i, 1).toString();
                    String a2 = 怪物爆物.getValueAt(i, 2).toString();
                    String a3 = 怪物爆物.getValueAt(i, 3).toString();
                    String a4 = 怪物爆物.getValueAt(i, 4).toString();
                    怪物爆物序列号.setText(a);
                    怪物爆物怪物代码.setText(a1);
                    怪物爆物物品代码.setText(a2);
                    怪物爆物爆率.setText(a3);
                    怪物爆物物品名称.setText(a4);
                }
            });
        } else {
            JOptionPane.showMessageDialog(null, "请输入要查询的怪物代码 ");
        }
    }

    public void 刷新怪物爆物() {
        for (int i = ((DefaultTableModel) (this.怪物爆物.getModel())).getRowCount() - 1; i >= 0; i--) {
            ((DefaultTableModel) (this.怪物爆物.getModel())).removeRow(i);
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = null;
            ResultSet rs = null;
            ps = con.prepareStatement("SELECT * FROM drop_data WHERE itemid !=0");
            rs = ps.executeQuery();
            while (rs.next()) {
                ((DefaultTableModel) 怪物爆物.getModel()).insertRow(怪物爆物.getRowCount(), new Object[]{
                    rs.getInt("id"),
                    rs.getInt("dropperid"),
                    //MapleLifeFactory.getMonster(rs.getInt("dropperid")),
                    rs.getInt("itemid"),
                    rs.getInt("chance"),
                    MapleItemInformationProvider.getInstance().getName(rs.getInt("itemid"))
                });
            }
        } catch (SQLException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
        怪物爆物.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int i = 怪物爆物.getSelectedRow();
                String a = 怪物爆物.getValueAt(i, 0).toString();
                String a1 = 怪物爆物.getValueAt(i, 1).toString();
                String a2 = 怪物爆物.getValueAt(i, 2).toString();
                String a3 = 怪物爆物.getValueAt(i, 3).toString();
                String a4 = 怪物爆物.getValueAt(i, 4).toString();
                怪物爆物序列号.setText(a);
                怪物爆物怪物代码.setText(a1);
                怪物爆物物品代码.setText(a2);
                怪物爆物爆率.setText(a3);
                怪物爆物物品名称.setText(a4);
            }
        });
    }

    public void 刷新泡点设置() {
        for (int i = ((DefaultTableModel) (this.在线泡点设置.getModel())).getRowCount() - 1; i >= 0; i--) {
            ((DefaultTableModel) (this.在线泡点设置.getModel())).removeRow(i);
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = null;
            ResultSet rs = null;
            ps = con.prepareStatement("SELECT * FROM configvalues WHERE id = 700 || id = 702 || id = 704 || id = 706 || id = 708 || id = 712");
            rs = ps.executeQuery();
            while (rs.next()) {
                ((DefaultTableModel) 在线泡点设置.getModel()).insertRow(在线泡点设置.getRowCount(), new Object[]{rs.getString("id"), rs.getString("name"), rs.getString("Val")});
            }
        } catch (SQLException ex) {
            Logger.getLogger(HuiMS.class.getName()).log(Level.SEVERE, null, ex);
        }
        在线泡点设置.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int i = 在线泡点设置.getSelectedRow();
                String a = 在线泡点设置.getValueAt(i, 0).toString();
                String a1 = 在线泡点设置.getValueAt(i, 1).toString();
                String a2 = 在线泡点设置.getValueAt(i, 2).toString();
                泡点序号.setText(a);
                泡点类型.setText(a1);
                泡点值.setText(a2);
            }
        });
    }

    private void 刷新泡点金币开关() {
        String 泡点金币开关显示 = "";
        int 泡点金币开关 = HuiMS.ConfigValuesMap.get("泡点金币开关");
        if (泡点金币开关 <= 0) {
            泡点金币开关显示 = "泡点金币:开启";
        } else {
            泡点金币开关显示 = "泡点金币:关闭";
        }
        泡点金币开关(泡点金币开关显示);
    }

    private void 刷新泡点点券开关() {
        String 泡点点券开关显示 = "";
        int 泡点点券开关 = HuiMS.ConfigValuesMap.get("泡点点券开关");
        if (泡点点券开关 <= 0) {
            泡点点券开关显示 = "泡点点券:开启";
        } else {
            泡点点券开关显示 = "泡点点券:关闭";
        }
        泡点点券开关(泡点点券开关显示);
    }

    private void 刷新泡点经验开关() {
        String 泡点经验开关显示 = "";
        int 泡点经验开关 = HuiMS.ConfigValuesMap.get("泡点经验开关");
        if (泡点经验开关 <= 0) {
            泡点经验开关显示 = "泡点经验:开启";
        } else {
            泡点经验开关显示 = "泡点经验:关闭";
        }
        泡点经验开关(泡点经验开关显示);
    }

    private void 刷新泡点抵用开关() {
        String 泡点抵用开关显示 = "";
        int 泡点抵用开关 = HuiMS.ConfigValuesMap.get("泡点抵用开关");
        if (泡点抵用开关 <= 0) {
            泡点抵用开关显示 = "泡点抵用:开启";
        } else {
            泡点抵用开关显示 = "泡点抵用:关闭";
        }
        泡点抵用开关(泡点抵用开关显示);
    }

    private void 刷新泡点豆豆开关() {
        String 泡点豆豆开关显示 = "";
        int 泡点豆豆开关 = HuiMS.ConfigValuesMap.get("泡点豆豆开关");
        if (泡点豆豆开关 <= 0) {
            泡点豆豆开关显示 = "泡点豆豆:开启";
        } else {
            泡点豆豆开关显示 = "泡点豆豆:关闭";
        }
        泡点豆豆开关(泡点豆豆开关显示);
    }

    private void 泡点点券开关(String str) {
        泡点点券开关.setText(str);
    }

    private void 泡点经验开关(String str) {
        泡点经验开关.setText(str);
    }

    private void 泡点抵用开关(String str) {
        泡点抵用开关.setText(str);
    }

    private void 泡点金币开关(String str) {
        泡点金币开关.setText(str);
    }

    private void 泡点豆豆开关(String str) {
        泡点豆豆开关.setText(str);
    }

    private void 个人发送福利(int a) {
        int 数量 = 0;
        String 类型 = "";
        String name = "";
        数量 = Integer.parseInt(a2.getText());
        name = 个人发送物品玩家名字1.getText();
        for (ChannelServer cserv1 : ChannelServer.getAllInstances()) {
            for (MapleCharacter mch : cserv1.getPlayerStorage().getAllCharacters()) {
                if (mch.getName().equals(name)) {
                    int ch = World.Find.findChannel(name);
                    if (ch <= 0) {
                        JOptionPane.showMessageDialog(null, "该玩家不在线");
                    }
                    switch (a) {
                        case 1:
                            类型 = "点券";
                            mch.modifyCSPoints(1, 数量, true);
                            mch.dropMessage("已经收到点卷" + 数量 + "点");
                            JOptionPane.showMessageDialog(null, "发送成功");
                            break;
                        case 2:
                            类型 = "抵用券";
                            mch.modifyCSPoints(2, 数量, true);
                            mch.dropMessage("已经收到抵用" + 数量 + "点");
                            JOptionPane.showMessageDialog(null, "发送成功");
                            break;
                        case 3:
                            类型 = "金币";
                            mch.gainMeso(数量, true);
                            mch.dropMessage("已经收到金币" + 数量 + "点");
                            JOptionPane.showMessageDialog(null, "发送成功");
                            break;
                        case 4:
                            类型 = "经验";
                            mch.gainExp(数量, true, false, true);
                            mch.dropMessage("已经收到经验" + 数量 + "点");
                            JOptionPane.showMessageDialog(null, "发送成功");
                            break;
                        case 5:
                            类型 = "人气";
                            mch.addFame(数量);
                            mch.dropMessage("已经收到人气" + 数量 + "点");
                            JOptionPane.showMessageDialog(null, "发送成功");
                            break;
                        case 6:
                            类型 = "豆豆";
                            mch.gainBeans(数量);
                            mch.dropMessage("已经收到豆豆" + 数量 + "点");
                            JOptionPane.showMessageDialog(null, "发送成功");
                            break;
                    }
                }
            }
        }
    }
    //新增结束
    private static ScheduledFuture<?> ts = null;
    private int minutesLeft = 0;
    private static Thread t = null;

    private void 重启服务器() {
        try {
            String 输出 = "关闭服务器倒数时间";
            minutesLeft = Integer.parseInt(jTextField22.getText());
            if (ts == null && (t == null || !t.isAlive())) {
                t = new Thread(ShutdownServer.getInstance());
                ts = EventTimer.getInstance().register(new Runnable() {
                    @Override
                    public void run() {
                        if (minutesLeft == 0) {
                            ShutdownServer.getInstance();
                            t.start();
                            ts.cancel(false);
                            return;
                        }
                        World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(0, "本私服器將在 " + minutesLeft + "分钟后关闭. 请尽速关闭雇佣商人 并下线，以免造成损失."));;
                        System.out.println("本私服器將在 " + minutesLeft + "分钟后关闭.");
                        minutesLeft--;
                    }
                }, 60000);
            }
            jTextField22.setText("关闭服务器倒数时间");
            printChatLog(输出);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "错误!\r\n" + e);
        }
    }

    public void 按键开关(String a, int b) {
        int 检测开关 = HuiMS.ConfigValuesMap.get(a);
        PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        if (检测开关 > 0) {
            try {
                ps = DatabaseConnection.getConnection().prepareStatement("UPDATE configvalues SET Val = ? WHERE id = ?");
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM configvalues WHERE id = ?");
                ps1.setInt(1, b);
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlString2 = null;
                    String sqlString3 = null;
                    String sqlString4 = null;
                    sqlString2 = "update configvalues set Val= '0' where id= '" + b + "';";
                    PreparedStatement dropperid = DatabaseConnection.getConnection().prepareStatement(sqlString2);
                    dropperid.executeUpdate(sqlString2);
                }
            } catch (SQLException ex) {
                Logger.getLogger(HuiMS.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                ps = DatabaseConnection.getConnection().prepareStatement("UPDATE configvalues SET Val = ? WHERE id = ?");
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM configvalues WHERE id = ?");
                ps1.setInt(1, b);
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlString2 = null;
                    String sqlString3 = null;
                    String sqlString4 = null;
                    sqlString2 = "update configvalues set Val= '1' where id='" + b + "';";
                    PreparedStatement dropperid = DatabaseConnection.getConnection().prepareStatement(sqlString2);
                    dropperid.executeUpdate(sqlString2);
                }
            } catch (SQLException ex) {
                Logger.getLogger(HuiMS.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        HuiMS.GetConfigValues();
    }

    private void 刷新钓鱼() {
        for (int i = ((DefaultTableModel) (this.钓鱼物品.getModel())).getRowCount() - 1; i >= 0; i--) {
            ((DefaultTableModel) (this.钓鱼物品.getModel())).removeRow(i);
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = null;

            ResultSet rs = null;
            ps = con.prepareStatement("SELECT * FROM 钓鱼物品");
            rs = ps.executeQuery();
            while (rs.next()) {
                ((DefaultTableModel) 钓鱼物品.getModel()).insertRow(钓鱼物品.getRowCount(), new Object[]{
                    rs.getInt("id"),
                    rs.getInt("itemid"),
                    rs.getInt("chance"),
                    MapleItemInformationProvider.getInstance().getName(rs.getInt("itemid"))//itemid
                });
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(HuiMS.class.getName()).log(Level.SEVERE, null, ex);
        }
        钓鱼物品.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int i = 钓鱼物品.getSelectedRow();
                String a = 钓鱼物品.getValueAt(i, 0).toString();
                String a1 = 钓鱼物品.getValueAt(i, 1).toString();
                String a2 = 钓鱼物品.getValueAt(i, 2).toString();
                //String a3 = 钓鱼物品.getValueAt(i, 3).toString();
                钓鱼物品序号.setText(a);
                钓鱼物品代码.setText(a1);
                钓鱼物品概率.setText(a2);
                //钓鱼物品名称.setText(a3);
            }
        });
    }
    //新增结束
    private void printChatLog(String str) {
        输出窗口.setText(输出窗口.getText() + str + "\r\n");
    }

    private void sendNotice(int a) {
        try {
            String str = noticeText.getText();
            String 输出 = "";
            for (ChannelServer cserv1 : ChannelServer.getAllInstances()) {
                for (MapleCharacter mch : cserv1.getPlayerStorage().getAllCharacters()) {
                    switch (a) {
                        case 0:
                            //顶端公告
                            World.Broadcast.broadcastMessage(MaplePacketCreator.getItemNotice(str.toString()));
                            break;
                        case 1:
                            //顶端公告
                            World.Broadcast.broadcastMessage(MaplePacketCreator.serverMessage(str.toString()));
                            break;
                        case 2:
                            //弹窗公告
                            World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(1, str));
                            break;
                        case 3:
                            //聊天蓝色公告
                            World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(0, str));
                            break;
                        case 4:
                            mch.startMapEffect(str, Integer.parseInt(公告发布喇叭代码.getText()));
                            break;
                        default:
                            break;
                    }
                }
                公告发布喇叭代码.setText("5120027");
            }
        } catch (Exception e) {
        }
    }

    public void 按键开关2(String name, int id, int val) {
        int oval = HuiMS.ConfigValuesMap.get(name);
        PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        try {
            //ps = DatabaseConnection.getConnection().prepareStatement("UPDATE configvalues SET Val = ? WHERE id = ?");
            ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM configvalues WHERE id = ?");
            ps1.setInt(1, id);
            rs = ps1.executeQuery();
            if (rs.next()) {
                String sqlString2 = null;
                String sqlString3 = null;
                String sqlString4 = null;
                sqlString2 = "update configvalues set Val= '" + val + "' where id= '" + id + "';";
                PreparedStatement dropperid = DatabaseConnection.getConnection().prepareStatement(sqlString2);
                dropperid.executeUpdate(sqlString2);
                dropperid.close();
            } else {
                ps1 = DatabaseConnection.getConnection().prepareStatement("insert into configvalues values (?,?,?)");
                ps1.setInt(1, id);
                ps1.setString(2, name);
                ps1.setInt(3, val);
                ps1.execute();
            }
            rs.close();
            ps1.close();
            //ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(HuiMS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateThreadNum() {
        writeLock.lock();
        try {
            server.Timer.WorldTimer.GuiTimer.getInstance().register(new Runnable() {
                @Override
                public final void run() {
                    ActiveThread.setText("<html>【线程个数】：<span style='color:red;'>" + Thread.activeCount() + "</span>");
                }
            }, 1 * 1000);
        } finally {
            writeLock.unlock();
        }
    }

    public void MemoryTest() {
        writeLock.lock();
        try {
            server.Timer.WorldTimer.GuiTimer.getInstance().register(new Runnable() {
                @Override
                public final void run() {
                    Runtime rt = Runtime.getRuntime();
                    long totalMemory = rt.totalMemory() / 1024 / 1024;
                    long usedMemory = totalMemory - rt.freeMemory() / 1024 / 1024;
                    int usedRate = Math.toIntExact(usedMemory * 100 / totalMemory);
                    内存.setValue(usedRate);
                    内存.setString("已用:" + usedMemory + "MB/共:" + totalMemory + "MB");
                }
            }, 1 * 1000);
        } finally {
            writeLock.unlock();
        }
    }

    public void 刷新经验加成表() {
        for (int i = ((DefaultTableModel) (this.经验加成表.getModel())).getRowCount() - 1; i >= 0; i--) {
            ((DefaultTableModel) (this.经验加成表.getModel())).removeRow(i);
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = null;
            ResultSet rs = null;
            ps = con.prepareStatement("SELECT * FROM configvalues WHERE id = 150 ||  id = 151  ||  id=152  ||  id=153 || id=154");
            rs = ps.executeQuery();
            while (rs.next()) {
                ((DefaultTableModel) 经验加成表.getModel()).insertRow(经验加成表.getRowCount(), new Object[]{rs.getString("id"), rs.getString("name"), rs.getString("Val")});
            }
        } catch (SQLException ex) {
            Logger.getLogger(HuiMS.class.getName()).log(Level.SEVERE, null, ex);
        }
        经验加成表.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int i = 经验加成表.getSelectedRow();
                String a = 经验加成表.getValueAt(i, 0).toString();
                String a1 = 经验加成表.getValueAt(i, 1).toString();
                String a2 = 经验加成表.getValueAt(i, 2).toString();
                经验加成表序号.setText(a);
                经验加成表类型.setText(a1);
                经验加成表数值.setText(a2);
            }
        });
    }

    public void 刷新商城扩充价格() {
        int 显示 = HuiMS.ConfigValuesMap.get("商城扩充价格");
        商城扩充价格修改.setText("" + 显示 + "");
    }

    private void 骑士团等级上限(String str) {
        骑士团等级上限.setText(str);
    }

    private void 刷新物品掉落持续时间() {
        int 显示 = HuiMS.ConfigValuesMap.get("物品掉落持续时间");
        物品掉落持续时间.setText("" + 显示 + "");
    }
    
    private void 刷新地图物品上限() {
        int 显示 = HuiMS.ConfigValuesMap.get("地图物品上限");
        地图物品上限.setText("" + 显示 + "");
    }

    private void 刷新地图刷新频率() {
        int 显示 = HuiMS.ConfigValuesMap.get("地图刷新频率");
        地图刷新频率.setText("" + 显示 + "");
    }

    private void 滚动公告开关(String str) {
        滚动公告开关.setText(str);
    }

    private void 回收地图开关(String str) {
        回收地图开关.setText(str);
    }

    private void 玩家聊天开关(String str) {
        玩家聊天开关.setText(str);
    }

    private void 上线提醒开关(String str) {
        上线提醒开关.setText(str);
    }

    private void 指令通知开关(String str) {
        指令通知开关.setText(str);
    }

    private void 雇佣商人开关(String str) {
        雇佣商人开关.setText(str);
    }

    private void 欢迎弹窗开关(String str) {
        欢迎弹窗开关.setText(str);
    }

    private void 管理隐身开关(String str) {
        管理隐身开关.setText(str);
    }

    private void 管理加速开关(String str) {
        管理加速开关.setText(str);
    }

    private void 游戏指令开关(String str) {
        游戏指令开关.setText(str);
    }

    private void 游戏喇叭开关(String str) {
        游戏喇叭开关.setText(str);
    }

    private void 丢出金币开关(String str) {
        丢出金币开关.setText(str);
    }

    private void 玩家交易开关(String str) {
        玩家交易开关.setText(str);
    }

    private void 丢出物品开关(String str) {
        丢出物品开关.setText(str);
    }

    private void 禁止登陆开关(String str) {
        禁止登陆开关.setText(str);
    }

    private void 游戏升级快讯(String str) {
        游戏升级快讯.setText(str);
    }

    private void 游戏冒险家职业开关(String str) {
        冒险家职业开关.setText(str);
    }

    private void 游戏骑士团职业开关(String str) {
        骑士团职业开关.setText(str);
    }

    private void 游戏战神职业开关(String str) {
        战神职业开关.setText(str);
    }

    private void 屠令广播开关(String str) {
        屠令广播开关.setText(str);
    }

    //新增开始
    private void 蓝蜗牛开关(String str) {
        蓝蜗牛开关.setText(str);
    }

    private void 蘑菇仔开关(String str) {
        蘑菇仔开关.setText(str);
    }

    private void 绿水灵开关(String str) {
        绿水灵开关.setText(str);
    }

    private void 漂漂猪开关(String str) {
        漂漂猪开关.setText(str);
    }

    private void 小青蛇开关(String str) {
        小青蛇开关.setText(str);
    }

    private void 红螃蟹开关(String str) {
        红螃蟹开关.setText(str);
    }

    private void 大海龟开关(String str) {
        大海龟开关.setText(str);
    }

    private void 章鱼怪开关(String str) {
        章鱼怪开关.setText(str);
    }

    private void 顽皮猴开关(String str) {
        顽皮猴开关.setText(str);
    }

    private void 星精灵开关(String str) {
        星精灵开关.setText(str);
    }

    private void 胖企鹅开关(String str) {
        胖企鹅开关.setText(str);
    }

    private void 白雪人开关(String str) {
        白雪人开关.setText(str);
    }

    private void 紫色猫开关(String str) {
        紫色猫开关.setText(str);
    }

    private void 大灰狼开关(String str) {
        大灰狼开关.setText(str);
    }

    private void 小白兔开关(String str) {
        小白兔开关.setText(str);
    }

    private void 喷火龙开关(String str) {
        喷火龙开关.setText(str);
    }

    private void 火野猪开关(String str) {
        火野猪开关.setText(str);
    }

    private void 青鳄鱼开关(String str) {
        青鳄鱼开关.setText(str);
    }

    private void 花蘑菇开关(String str) {
        花蘑菇开关.setText(str);
    }

    private void 石头人开关(String str) {
        石头人开关.setText(str);
    }

    private void 刷新花蘑菇开关() {
        String 花蘑菇显示 = "";
        int 花蘑菇 = HuiMS.ConfigValuesMap.get("花蘑菇开关");
        if (花蘑菇 <= 0) {
            this.花蘑菇开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON2.png")));
        } else {
            this.花蘑菇开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF2.png")));
        }
        //花蘑菇开关(花蘑菇显示);
    }

    private void 刷新火野猪开关() {
        String 火野猪显示 = "";
        int 火野猪 = HuiMS.ConfigValuesMap.get("火野猪开关");
        if (火野猪 <= 0) {
            this.火野猪开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON2.png")));
        } else {
            this.火野猪开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF2.png")));
        }
        //火野猪开关(火野猪显示);
    }

    private void 刷新青鳄鱼开关() {
        String 青鳄鱼显示 = "";
        int 青鳄鱼 = HuiMS.ConfigValuesMap.get("青鳄鱼开关");
        if (青鳄鱼 <= 0) {
            this.青鳄鱼开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON2.png")));
        } else {
            this.青鳄鱼开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF2.png")));
        }
        //青鳄鱼开关(青鳄鱼显示);
    }

    private void 刷新喷火龙开关() {
        String 喷火龙显示 = "";
        int 喷火龙 = HuiMS.ConfigValuesMap.get("喷火龙开关");
        if (喷火龙 <= 0) {
            this.喷火龙开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON2.png")));
        } else {
            this.喷火龙开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF2.png")));
        }
        //喷火龙开关(喷火龙显示);
    }

    private void 刷新小白兔开关() {
        String 小白兔显示 = "";
        int 小白兔 = HuiMS.ConfigValuesMap.get("小白兔开关");
        if (小白兔 <= 0) {
            this.小白兔开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON2.png")));
        } else {
            this.小白兔开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF2.png")));
        }
        //小白兔开关(小白兔显示);
    }

    private void 刷新大灰狼开关() {
        String 大灰狼显示 = "";
        int 大灰狼 = HuiMS.ConfigValuesMap.get("大灰狼开关");
        if (大灰狼 <= 0) {
            this.大灰狼开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON2.png")));
        } else {
            this.大灰狼开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF2.png")));
        }
        //大灰狼开关(大灰狼显示);
    }

    private void 刷新紫色猫开关() {
        String 紫色猫显示 = "";
        int 紫色猫 = HuiMS.ConfigValuesMap.get("紫色猫开关");
        if (紫色猫 <= 0) {
            this.紫色猫开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON2.png")));
        } else {
            this.紫色猫开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF2.png")));
        }
        //紫色猫开关(紫色猫显示);
    }

    private void 刷新石头人开关() {
        String 石头人显示 = "";
        int 石头人 = HuiMS.ConfigValuesMap.get("石头人开关");
        if (石头人 <= 0) {
            this.石头人开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON2.png")));
        } else {
            this.石头人开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF2.png")));
        }
        //石头人开关(石头人显示);
    }

    private void 刷新白雪人开关() {
        String 白雪人显示 = "";
        int 白雪人 = HuiMS.ConfigValuesMap.get("白雪人开关");
        if (白雪人 <= 0) {
            this.白雪人开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON2.png")));
        } else {
            this.白雪人开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF2.png")));
        }
        //白雪人开关(白雪人显示);
    }

    private void 刷新胖企鹅开关() {
        String 胖企鹅显示 = "";
        int 胖企鹅 = HuiMS.ConfigValuesMap.get("胖企鹅开关");
        if (胖企鹅 <= 0) {
            this.胖企鹅开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON2.png")));
        } else {
            this.胖企鹅开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF2.png")));
        }
        //胖企鹅开关(胖企鹅显示);
    }

    private void 刷新星精灵开关() {
        String 星精灵显示 = "";
        int 星精灵 = HuiMS.ConfigValuesMap.get("星精灵开关");
        if (星精灵 <= 0) {
            this.星精灵开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON2.png")));
        } else {
            this.星精灵开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF2.png")));
        }
        //星精灵开关(星精灵显示);
    }

    private void 刷新顽皮猴开关() {
        String 顽皮猴显示 = "";
        int 顽皮猴 = HuiMS.ConfigValuesMap.get("顽皮猴开关");
        if (顽皮猴 <= 0) {
            this.顽皮猴开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON2.png")));
        } else {
            this.顽皮猴开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF2.png")));
        }
        //顽皮猴开关(顽皮猴显示);
    }

    private void 刷新章鱼怪开关() {
        String 章鱼怪显示 = "";
        int 章鱼怪 = HuiMS.ConfigValuesMap.get("章鱼怪开关");
        if (章鱼怪 <= 0) {
            this.章鱼怪开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON2.png")));
        } else {
            this.章鱼怪开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF2.png")));
        }
        //章鱼怪开关(章鱼怪显示);
    }

    private void 刷新大海龟开关() {
        String 大海龟显示 = "";
        int 大海龟 = HuiMS.ConfigValuesMap.get("大海龟开关");
        if (大海龟 <= 0) {
            this.大海龟开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON2.png")));
        } else {
            this.大海龟开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF2.png")));
        }
        //大海龟开关(大海龟显示);
    }

    private void 刷新红螃蟹开关() {
        String 红螃蟹显示 = "";
        int 红螃蟹 = HuiMS.ConfigValuesMap.get("红螃蟹开关");
        if (红螃蟹 <= 0) {
            this.红螃蟹开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON2.png")));
        } else {
            this.红螃蟹开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF2.png")));
        }
        //红螃蟹开关(红螃蟹显示);
    }

    private void 刷新小青蛇开关() {
        String 小青蛇显示 = "";
        int 小青蛇 = HuiMS.ConfigValuesMap.get("小青蛇开关");
        if (小青蛇 <= 0) {
            this.小青蛇开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON2.png")));
        } else {
            this.小青蛇开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF2.png")));
        }
        //小青蛇开关(小青蛇显示);
    }

    private void 刷新蓝蜗牛开关() {
        String 蓝蜗牛显示 = "";
        int 蓝蜗牛 = HuiMS.ConfigValuesMap.get("蓝蜗牛开关");
        if (蓝蜗牛 <= 0) {
            this.蓝蜗牛开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON2.png")));
        } else {
            this.蓝蜗牛开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF2.png")));
        }
        //蓝蜗牛开关(蓝蜗牛显示);
    }

    private void 刷新漂漂猪开关() {
        String 漂漂猪显示 = "";
        int 漂漂猪 = HuiMS.ConfigValuesMap.get("漂漂猪开关");
        if (漂漂猪 <= 0) {
            this.漂漂猪开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON2.png")));
        } else {
            this.漂漂猪开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF2.png")));
        }
        //漂漂猪开关(漂漂猪显示);
    }

    private void 刷新绿水灵开关() {
        String 绿水灵显示 = "";
        int 绿水灵 = HuiMS.ConfigValuesMap.get("绿水灵开关");
        if (绿水灵 <= 0) {
            this.绿水灵开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON2.png")));
        } else {
            this.绿水灵开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF2.png")));
        }
        //绿水灵开关(绿水灵显示);
    }

    private void 刷新蘑菇仔开关() {
        String 蘑菇仔显示 = "";
        int 蘑菇仔 = HuiMS.ConfigValuesMap.get("蘑菇仔开关");
        if (蘑菇仔 <= 0) {
            this.蘑菇仔开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON2.png")));
        } else {
            this.蘑菇仔开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF2.png")));
        }
        //蘑菇仔开关(蘑菇仔显示);
    }

    private void 刷新登陆帮助() {
        String 显示 = "";
        int S = HuiMS.ConfigValuesMap.get("登陆帮助开关");
        if (S <= 0) {
            this.登陆帮助开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON3.png")));
        } else {
            this.登陆帮助开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF3.png")));
        }
//        登陆帮助开关.setText(显示);
    }

    private void 刷新怪物状态开关() {
        String 显示 = "";
        int S = HuiMS.ConfigValuesMap.get("怪物状态开关");
        if (S <= 0) {
            this.怪物状态开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON3.png")));
        } else {
            this.怪物状态开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF3.png")));
        }
//        怪物状态开关.setText(显示);
    }

    private void 刷新越级打怪开关() {
        String 显示 = "";
        int S = HuiMS.ConfigValuesMap.get("越级打怪开关");
        if (S <= 0) {
            this.越级打怪开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON3.png")));
        } else {
            this.越级打怪开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF3.png")));
        }
//        越级打怪开关.setText(显示);
    }

    private void 刷新地图名称开关() {
        String 显示 = "";
        int S = HuiMS.ConfigValuesMap.get("地图名称开关");
        if (S <= 0) {
            this.地图名称开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON3.png")));
        } else {
            this.地图名称开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF3.png")));
        }
//        地图名称开关.setText(显示);
    }

    private void 刷新过图存档时间() {
        String 显示 = "";
        int S = HuiMS.ConfigValuesMap.get("过图存档开关");
        if (S <= 0) {
            this.过图存档开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON3.png")));
        } else {
            this.过图存档开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF3.png")));
        }
//        过图存档开关.setText(显示);
    }

    private void 刷新吸怪检测开关() {
        String 显示 = "";
        int S = HuiMS.ConfigValuesMap.get("吸怪检测开关");
        if (S <= 0) {
            this.吸怪检测开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON3.png")));
        } else {
            this.吸怪检测开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF3.png")));
        }
//        吸怪检测开关.setText(显示);
    }

    private void 刷新冒险家职业开关() {
        String 冒险家职业开关显示 = "";
        int 冒险家职业开关 = HuiMS.ConfigValuesMap.get("冒险家职业开关");
        if (冒险家职业开关 <= 0) {
            this.冒险家职业开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON2.png")));
        } else {
            this.冒险家职业开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF2.png")));
        }
//        游戏冒险家职业开关(冒险家职业开关显示);
    }

    private void 刷新骑士团职业开关() {
        String 骑士团职业开关显示 = "";
        int 骑士团职业开关 = HuiMS.ConfigValuesMap.get("骑士团职业开关");
        if (骑士团职业开关 <= 0) {
            this.骑士团职业开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON2.png")));
        } else {
            this.骑士团职业开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF2.png")));
        }
//        游戏骑士团职业开关(骑士团职业开关显示);
    }

    private void 刷新战神职业开关() {
        String 战神职业开关显示 = "";
        int 战神职业开关 = HuiMS.ConfigValuesMap.get("战神职业开关");
        if (战神职业开关 <= 0) {
            this.战神职业开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON2.png")));
        } else {
            this.战神职业开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF2.png")));
        }
//        游戏战神职业开关(战神职业开关显示);
    }

    private void 刷新屠令广播开关() {
        String 屠令广播显示 = "";
        int 屠令广播 = HuiMS.ConfigValuesMap.get("屠令广播开关");
        if (屠令广播 <= 0) {
            this.屠令广播开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON3.png")));
        } else {
            this.屠令广播开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF3.png")));
        }
//        屠令广播开关(屠令广播显示);
    }

    private void 刷新指令通知开关() {
        String 刷新指令通知开关显示 = "";
        int 指令通知开关 = HuiMS.ConfigValuesMap.get("指令通知开关");
        if (指令通知开关 <= 0) {
            this.指令通知开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON3.png")));
        } else {
            this.指令通知开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF3.png")));
        }
//        指令通知开关(刷新指令通知开关显示);
    }

    private void 刷新玩家聊天开关() {
        String 刷新玩家聊天开关显示 = "";
        int 玩家聊天开关 = HuiMS.ConfigValuesMap.get("玩家聊天开关");
        if (玩家聊天开关 <= 0) {
            this.玩家聊天开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON3.png")));
        } else {
            this.玩家聊天开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF3.png")));
        }
//        玩家聊天开关(刷新玩家聊天开关显示);
    }

    private void 刷新禁止登陆开关() {
        String 刷新禁止登陆开关显示 = "";
        int 禁止登陆开关 = HuiMS.ConfigValuesMap.get("禁止登陆开关");
        if (禁止登陆开关 <= 0) {
            this.禁止登陆开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF3.png")));
        } else {
            this.禁止登陆开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON3.png")));
        }
        //禁止登陆开关(刷新禁止登陆开关显示);
    }

    private void 刷新升级快讯() {
        String 升级快讯显示 = "";
        int 升级快讯 = HuiMS.ConfigValuesMap.get("升级快讯开关");
        if (升级快讯 <= 0) {
            this.游戏升级快讯.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON3.png")));
        } else {
            this.游戏升级快讯.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF3.png")));
        }
//        游戏升级快讯(升级快讯显示);
    }

    private void 刷新丢出金币开关() {
        String 刷新丢出金币开关显示 = "";
        int 丢出金币开关 = HuiMS.ConfigValuesMap.get("丢出金币开关");
        if (丢出金币开关 <= 0) {
            this.丢出金币开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON3.png")));
        } else {
            this.丢出金币开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF3.png")));
        }
//        丢出金币开关(刷新丢出金币开关显示);
    }

    private void 刷新玩家交易开关() {
        String 刷新玩家交易开关显示 = "";
        int 玩家交易开关 = HuiMS.ConfigValuesMap.get("玩家交易开关");
        if (玩家交易开关 <= 0) {
            this.玩家交易开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON3.png")));
        } else {
            this.玩家交易开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF3.png")));
        }
//        玩家交易开关(刷新玩家交易开关显示);
    }

    private void 刷新丢出物品开关() {
        String 刷新丢出物品开关显示 = "";
        int 丢出物品开关 = HuiMS.ConfigValuesMap.get("丢出物品开关");
        if (丢出物品开关 <= 0) {
            this.丢出物品开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON3.png")));
        } else {
            this.丢出物品开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF3.png")));
        }
//        丢出物品开关(刷新丢出物品开关显示);
    }

    private void 刷新游戏指令开关() {
        String 刷新游戏指令开关显示 = "";
        int 游戏指令开关 = HuiMS.ConfigValuesMap.get("游戏指令开关");
        if (游戏指令开关 <= 0) {
            this.游戏指令开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF3.png")));
        } else {
            this.游戏指令开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON3.png")));
        }
//        游戏指令开关(刷新游戏指令开关显示);
    }
    private void 刷新上线提醒开关() {
        String 刷新上线提醒开关显示 = "";
        int 上线提醒开关 = HuiMS.ConfigValuesMap.get("上线提醒开关");
        if (上线提醒开关 <= 0) {
            this.上线提醒开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON3.png")));
        } else {
            this.上线提醒开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF3.png")));
        }
//        上线提醒开关(刷新上线提醒开关显示);
    }

    private void 刷新回收地图开关() {
        String 刷新回收地图开关显示 = "";
        int 回收地图开关 = HuiMS.ConfigValuesMap.get("回收地图开关");
        if (回收地图开关 <= 0) {
            this.回收地图开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON3.png")));
        } else {
            this.回收地图开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF3.png")));
        }
//        回收地图开关(刷新回收地图开关显示);
    }

    private void 刷新管理隐身开关() {
        String 刷新管理隐身开关显示 = "";
        int 管理隐身开关 = HuiMS.ConfigValuesMap.get("管理隐身开关");
        if (管理隐身开关 <= 0) {
            this.管理隐身开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON3.png")));
        } else {
            this.管理隐身开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF3.png")));
        }
//        管理隐身开关(刷新管理隐身开关显示);
    }

    private void 刷新管理加速开关() {
        String 刷新管理加速开关显示 = "";
        int 管理加速开关 = HuiMS.ConfigValuesMap.get("管理加速开关");
        if (管理加速开关 <= 0) {
            this.管理加速开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON3.png")));
        } else {
            this.管理加速开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF3.png")));
        }
//        管理加速开关(刷新管理加速开关显示);
    }

    private void 刷新雇佣商人开关() {
        String 刷新雇佣商人开关显示 = "";
        int 雇佣商人开关 = HuiMS.ConfigValuesMap.get("雇佣商人开关");
        if (雇佣商人开关 <= 0) {
            this.雇佣商人开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON3.png")));
        } else {
            this.雇佣商人开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF3.png")));
        }
//        雇佣商人开关(刷新雇佣商人开关显示);
    }

    private void 刷新欢迎弹窗开关() {
        String 刷新欢迎弹窗开关显示 = "";
        int 欢迎弹窗开关 = HuiMS.ConfigValuesMap.get("欢迎弹窗开关");
        if (欢迎弹窗开关 <= 0) {
            this.欢迎弹窗开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON3.png")));
        } else {
            this.欢迎弹窗开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF3.png")));
        }
//        欢迎弹窗开关(刷新欢迎弹窗开关显示);
    }

    private void 刷新滚动公告开关() {
        String 刷新滚动公告开关显示 = "";
        int 滚动公告开关 = HuiMS.ConfigValuesMap.get("滚动公告开关");
        if (滚动公告开关 <= 0) {
            this.滚动公告开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON3.png")));
        } else {
            this.滚动公告开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF3.png")));
        }
//        滚动公告开关(刷新滚动公告开关显示);
    }
    private void 刷新物品叠加数量上限() {
        int 显示 = HuiMS.ConfigValuesMap.get("物品额外数量");
        物品叠加数量.setText("" + 显示 + "");
    }
    private void 刷新倍怪地图() {
        String 地图 = ServerProperties.getProperty("HuiMs.倍怪地图");
        倍怪地图.setText("" + 地图 + "");
    }
    private void 刷新弓标子弹叠加代码() {
        String 代码 = ServerProperties.getProperty("HuiMs.弓标子弹叠加代码");
        弓标子弹叠加上限突破.setText("" + 代码 + "");
    }
    private void 刷新区间一倍率() {
        String 倍率 = ServerProperties.getProperty("HuiMs.BeiShu1");
        String 最低 = ServerProperties.getProperty("HuiMs.BeiShu1Minlevel");
        String 最高 = ServerProperties.getProperty("HuiMs.BeiShu1Maxlevel");
        区间一经验倍率.setText("" + 倍率 + "");
        区间一最低等级.setText("" + 最低 + "");
        区间一最高等级.setText("" + 最高 + "");
    }
    private void 刷新区间二倍率() {
        String 倍率 = ServerProperties.getProperty("HuiMs.BeiShu2");
        String 最低 = ServerProperties.getProperty("HuiMs.BeiShu2Minlevel");
        String 最高 = ServerProperties.getProperty("HuiMs.BeiShu2Maxlevel");
        区间二经验倍率.setText("" + 倍率 + "");
        区间二最低等级.setText("" + 最低 + "");
        区间二最高等级.setText("" + 最高 + "");
    }
    private void 刷新区间三倍率() {
        String 倍率 = ServerProperties.getProperty("HuiMs.BeiShu3");
        String 最低 = ServerProperties.getProperty("HuiMs.BeiShu3Minlevel");
        String 最高 = ServerProperties.getProperty("HuiMs.BeiShu3Maxlevel");
        区间三经验倍率.setText("" + 倍率 + "");
        区间三最低等级.setText("" + 最低 + "");
        区间三最高等级.setText("" + 最高 + "");
    }
    private void 刷新游戏喇叭开关() {
        String 刷新游戏喇叭开关显示 = "";
        int 游戏喇叭开关 = HuiMS.ConfigValuesMap.get("游戏喇叭开关");
        if (游戏喇叭开关 <= 0) {
            this.游戏喇叭开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON3.png")));
        } else {
            this.游戏喇叭开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF3.png")));
        }
//        游戏喇叭开关(刷新游戏喇叭开关显示);
    }

    private void 刷新魔族突袭开关() {
        String 显示 = "";
        int S = gui.HuiMS.ConfigValuesMap.get("魔族突袭开关");
        if (S <= 0) {
            this.魔族突袭开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON2.png")));
        } else {
            this.魔族突袭开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF2.png")));
        }
//        魔族突袭开关.setText(显示);
    }

    private void 刷新魔族攻城开关() {
        String 显示 = "";
        int S = gui.HuiMS.ConfigValuesMap.get("魔族攻城开关");
        if (S <= 0) {
            this.魔族攻城开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON2.png")));
        } else {
            this.魔族攻城开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF2.png")));
        }
//        魔族攻城开关.setText(显示);
    }

    private void 刷新幸运职业开关() {
        String 显示 = "";
        int S = gui.HuiMS.ConfigValuesMap.get("幸运职业开关");
        if (S <= 0) {
            this.幸运职业开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON2.png")));
        } else {
            this.幸运职业开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF2.png")));
        }
//        幸运职业开关.setText(显示);
    }

    private void 刷新神秘商人开关() {
        String 显示 = "";
        int S = gui.HuiMS.ConfigValuesMap.get("神秘商人开关");
        if (S <= 0) {
            this.神秘商人开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("ON2.png")));
        } else {
            this.神秘商人开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("OFF2.png")));
        }
//        神秘商人开关.setText(显示);
    }

    private void 刷新骑士团等级上限() {
        String 骑士团等级上限显示 = "";
        int 骑士团等级上限 = HuiMS.ConfigValuesMap.get("骑士团等级上限");

        骑士团等级上限显示 = "" + 骑士团等级上限;

        骑士团等级上限(骑士团等级上限显示);
    }

    private void 刷新冒险家等级上限() {
        String 冒险家等级上限显示 = "";
        int 冒险家等级上限 = HuiMS.ConfigValuesMap.get("冒险家等级上限");

        冒险家等级上限显示 = "" + 冒险家等级上限;

        冒险家等级上限(冒险家等级上限显示);
    }

    private void 冒险家等级上限(String str) {
        jTextMaxLevel.setText(str);
    }

    private void sendNoticeGG() {
        try {
            String str = jTextField2.getText();
            String 输出 = "";
            for (ChannelServer cserv1 : ChannelServer.getAllInstances()) {
                for (MapleCharacter mch : cserv1.getPlayerStorage().getAllCharacters()) {
                    mch.startMapEffect(str, 5121009);
                    输出 = "[公告]:" + str;
                }
            }
            jTextField2.setText("");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "错误!\r\n" + e);
        }
    }

    private void 刷新多开数量() {
        IP多开数量.setText("" + gui.HuiMS.ConfigValuesMap.get("IP多开数") + "");
        机器码多开数量.setText("" + gui.HuiMS.ConfigValuesMap.get("机器码多开数") + "");
    }
    public static void main(final String[] args) throws Exception {
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
        try {
            BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.FrameBorderStyle.translucencySmallShadow;//设置本属性将改变窗口边框样式定义
            UIManager.put("RootPane.setupButtonVisible", false);//关闭设置
            BeautyEyeLNFHelper.launchBeautyEyeLNF();
            //顺便加载一下字体
            for (int i = 0; i < DEFAULT_FONT.length; i++) {
                UIManager.put(DEFAULT_FONT[i], new Font("微软雅黑", Font.PLAIN, 14));
            }
        } catch (Exception e) {
            System.out.println("[" + FileoutputUtil.CurrentReadable_Time() + "]" + e);
        }
        EventQueue.invokeLater((Runnable) new Runnable() {
            @Override
            public void run() {
                new HuiMS().setVisible(true);
                System.out.println("[" + FileoutputUtil.CurrentReadable_Time() + "][========================================]");
                System.out.println("[" + FileoutputUtil.CurrentReadable_Time() + "][每一段时光，都有幸福的理由。 —— 小灰灰]");
                System.out.println("[" + FileoutputUtil.CurrentReadable_Time() + "][快乐不是因为得到的多而是因为计较的少。 —— 大D]");
                System.out.println("[" + FileoutputUtil.CurrentReadable_Time() + "][付出不一定有收获，努力了就值得了。 —— 林羽]");
                System.out.println("[" + FileoutputUtil.CurrentReadable_Time() + "][时间往往不是答案，但答案都在时间里。 —— 林北]");
                System.out.println("[" + FileoutputUtil.CurrentReadable_Time() + "][信息]控制台已启动，点击左下角[启动服务端]运行。");
                System.out.println("[" + FileoutputUtil.CurrentReadable_Time() + "][========================================]");
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel ActiveThread;
    private javax.swing.JTextField IP多开数量;
    private javax.swing.JLabel PlayerCount;
    private javax.swing.JLabel ZEVMS2提示框1;
    private javax.swing.JTextField a1;
    private javax.swing.JTextField a2;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton22;
    private javax.swing.JButton jButton26;
    private javax.swing.JButton jButton27;
    private javax.swing.JButton jButton29;
    private javax.swing.JButton jButton34;
    private javax.swing.JButton jButton35;
    private javax.swing.JButton jButton36;
    private javax.swing.JButton jButton37;
    private javax.swing.JButton jButton38;
    private javax.swing.JButton jButton40;
    private javax.swing.JButton jButton41;
    private javax.swing.JButton jButton42;
    private javax.swing.JButton jButton43;
    private javax.swing.JButton jButton44;
    private javax.swing.JButton jButton45;
    private javax.swing.JButton jButton46;
    private javax.swing.JButton jButton47;
    private javax.swing.JButton jButton48;
    private javax.swing.JButton jButton49;
    private javax.swing.JButton jButton50;
    private javax.swing.JButton jButton51;
    private javax.swing.JButton jButton53;
    private javax.swing.JButton jButton54;
    private javax.swing.JButton jButton55;
    private javax.swing.JButton jButton68;
    private javax.swing.JButton jButton69;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton70;
    private javax.swing.JButton jButton72;
    private javax.swing.JButton jButton73;
    private javax.swing.JButton jButton74;
    private javax.swing.JButton jButton75;
    private javax.swing.JButton jButton76;
    private javax.swing.JButton jButton77;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JButton jButtonMaxCharacter;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel106;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel117;
    private javax.swing.JLabel jLabel118;
    private javax.swing.JLabel jLabel119;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel120;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel202;
    private javax.swing.JLabel jLabel209;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel210;
    private javax.swing.JLabel jLabel211;
    private javax.swing.JLabel jLabel212;
    private javax.swing.JLabel jLabel213;
    private javax.swing.JLabel jLabel217;
    private javax.swing.JLabel jLabel219;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel220;
    private javax.swing.JLabel jLabel221;
    private javax.swing.JLabel jLabel222;
    private javax.swing.JLabel jLabel223;
    private javax.swing.JLabel jLabel224;
    private javax.swing.JLabel jLabel225;
    private javax.swing.JLabel jLabel226;
    private javax.swing.JLabel jLabel227;
    private javax.swing.JLabel jLabel228;
    private javax.swing.JLabel jLabel229;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel230;
    private javax.swing.JLabel jLabel231;
    private javax.swing.JLabel jLabel232;
    private javax.swing.JLabel jLabel233;
    private javax.swing.JLabel jLabel234;
    private javax.swing.JLabel jLabel235;
    private javax.swing.JLabel jLabel236;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel240;
    private javax.swing.JLabel jLabel241;
    private javax.swing.JLabel jLabel242;
    private javax.swing.JLabel jLabel244;
    private javax.swing.JLabel jLabel246;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel252;
    private javax.swing.JLabel jLabel253;
    private javax.swing.JLabel jLabel259;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel262;
    private javax.swing.JLabel jLabel263;
    private javax.swing.JLabel jLabel264;
    private javax.swing.JLabel jLabel267;
    private javax.swing.JLabel jLabel268;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel274;
    private javax.swing.JLabel jLabel275;
    private javax.swing.JLabel jLabel277;
    private javax.swing.JLabel jLabel278;
    private javax.swing.JLabel jLabel279;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel280;
    private javax.swing.JLabel jLabel282;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel316;
    private javax.swing.JLabel jLabel319;
    private javax.swing.JLabel jLabel321;
    private javax.swing.JLabel jLabel322;
    private javax.swing.JLabel jLabel323;
    private javax.swing.JLabel jLabel324;
    private javax.swing.JLabel jLabel325;
    private javax.swing.JLabel jLabel326;
    private javax.swing.JLabel jLabel327;
    private javax.swing.JLabel jLabel328;
    private javax.swing.JLabel jLabel329;
    private javax.swing.JLabel jLabel330;
    private javax.swing.JLabel jLabel331;
    private javax.swing.JLabel jLabel348;
    private javax.swing.JLabel jLabel349;
    private javax.swing.JLabel jLabel359;
    private javax.swing.JLabel jLabel360;
    private javax.swing.JLabel jLabel361;
    private javax.swing.JLabel jLabel362;
    private javax.swing.JLabel jLabel379;
    private javax.swing.JLabel jLabel380;
    private javax.swing.JLabel jLabel381;
    private javax.swing.JLabel jLabel382;
    private javax.swing.JLabel jLabel384;
    private javax.swing.JLabel jLabel385;
    private javax.swing.JLabel jLabel386;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel28;
    private javax.swing.JPanel jPanel29;
    private javax.swing.JPanel jPanel34;
    private javax.swing.JPanel jPanel37;
    private javax.swing.JPanel jPanel38;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel52;
    private javax.swing.JPanel jPanel53;
    private javax.swing.JPanel jPanel57;
    private javax.swing.JPanel jPanel58;
    private javax.swing.JPanel jPanel59;
    private javax.swing.JPanel jPanel60;
    private javax.swing.JPanel jPanel61;
    private javax.swing.JPanel jPanel62;
    private javax.swing.JPanel jPanel63;
    private javax.swing.JPanel jPanel64;
    private javax.swing.JPanel jPanel66;
    private javax.swing.JPanel jPanel67;
    private javax.swing.JPanel jPanel68;
    private javax.swing.JPanel jPanel69;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel70;
    private javax.swing.JPanel jPanel71;
    private javax.swing.JPanel jPanel72;
    private javax.swing.JPanel jPanel74;
    private javax.swing.JPanel jPanel75;
    private javax.swing.JPanel jPanel80;
    private javax.swing.JPanel jPanel81;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanel91;
    private javax.swing.JPanel jPanel93;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane134;
    private javax.swing.JScrollPane jScrollPane136;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane26;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSeparator jSeparator11;
    private javax.swing.JSeparator jSeparator12;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTabbedPane jTabbedPane7;
    private javax.swing.JTabbedPane jTabbedPane9;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField22;
    private javax.swing.JTextField jTextFieldMaxCharacterNumber;
    private javax.swing.JTextField jTextMaxLevel;
    private javax.swing.JTextField noticeText;
    private javax.swing.JTable playerTable;
    private javax.swing.JButton sendMsgNotice;
    private javax.swing.JButton sendNotice;
    private javax.swing.JButton sendNpcTalkNotice;
    private javax.swing.JButton sendWinNotice;
    private static javax.swing.JButton startserverbutton;
    private javax.swing.JButton z1;
    private javax.swing.JButton z10;
    private javax.swing.JButton z11;
    private javax.swing.JButton z12;
    private javax.swing.JButton z2;
    private javax.swing.JButton z3;
    private javax.swing.JButton z4;
    private javax.swing.JButton z5;
    private javax.swing.JButton z6;
    private javax.swing.JButton z7;
    private javax.swing.JButton z8;
    private javax.swing.JButton z9;
    private javax.swing.JButton 一键满技能;
    private javax.swing.JTextField 三倍爆率持续时间;
    private javax.swing.JTextField 三倍经验持续时间;
    private javax.swing.JTextField 三倍金币持续时间;
    private javax.swing.JButton 上线提醒开关;
    private javax.swing.JTable 世界爆物;
    private javax.swing.JTextField 世界爆物名称;
    private javax.swing.JTextField 世界爆物序列号;
    private javax.swing.JTextField 世界爆物爆率;
    private javax.swing.JTextField 世界爆物物品代码;
    private javax.swing.JButton 丢出物品开关;
    private javax.swing.JButton 丢出金币开关;
    private javax.swing.JTextField 个人发送物品代码;
    private javax.swing.JTextField 个人发送物品数量;
    private javax.swing.JTextField 个人发送物品玩家名字;
    private javax.swing.JTextField 个人发送物品玩家名字1;
    private javax.swing.JButton 个人玩家下线;
    private javax.swing.JTabbedPane 主窗口;
    private javax.swing.JButton 传送玩家到指定地图;
    private javax.swing.JButton 传送玩家到自由;
    private javax.swing.JButton 修改世界爆物;
    private javax.swing.JButton 修改冒险家等级上限;
    private javax.swing.JButton 修改冒险家等级上限1;
    private javax.swing.JButton 修改反应堆物品;
    private javax.swing.JButton 修改怪物倍率;
    private javax.swing.JButton 修改怪物爆物;
    private javax.swing.JButton 修改物品叠加数量;
    private javax.swing.JButton 修改物品掉落持续时间;
    private javax.swing.JButton 修改物品掉落持续时间1;
    private javax.swing.JButton 修改物品掉落持续时间2;
    private javax.swing.JButton 修改玩家信息;
    private javax.swing.JButton 修改背包扩充价格;
    private javax.swing.JButton 修改钓鱼物品;
    private javax.swing.JButton 修改骑士团等级上限;
    private javax.swing.JButton 修改骑士团等级上限2;
    public static javax.swing.JTextArea 倍怪地图;
    private javax.swing.JButton 全员下线;
    private javax.swing.JTextField 全服发送物品代码;
    private javax.swing.JTextField 全服发送物品数量;
    private javax.swing.JTextField 全服发送装备物品ID;
    private javax.swing.JTextField 全服发送装备装备HP;
    private javax.swing.JTextField 全服发送装备装备MP;
    private javax.swing.JTextField 全服发送装备装备制作人;
    private javax.swing.JTextField 全服发送装备装备力量;
    private javax.swing.JTextField 全服发送装备装备加卷;
    private javax.swing.JTextField 全服发送装备装备可否交易;
    private javax.swing.JTextField 全服发送装备装备攻击力;
    private javax.swing.JTextField 全服发送装备装备敏捷;
    private javax.swing.JTextField 全服发送装备装备智力;
    private javax.swing.JTextField 全服发送装备装备物理防御;
    private javax.swing.JTextField 全服发送装备装备给予时间;
    private javax.swing.JTextField 全服发送装备装备运气;
    private javax.swing.JTextField 全服发送装备装备魔法力;
    private javax.swing.JTextField 全服发送装备装备魔法防御;
    private javax.swing.JTextField 公告发布喇叭代码;
    private javax.swing.JTabbedPane 关于我们;
    private javax.swing.JButton 关玩家到小黑屋;
    private static javax.swing.JProgressBar 内存;
    private javax.swing.JButton 冒险家职业开关;
    private javax.swing.JButton 删除世界爆物;
    private javax.swing.JButton 删除反应堆物品;
    private javax.swing.JButton 删除反应堆物品1;
    private javax.swing.JTextField 删除反应堆物品代码;
    private javax.swing.JButton 删除怪物爆物;
    private javax.swing.JTextField 删除指定的掉落;
    private javax.swing.JTextField 删除指定的掉落1;
    private javax.swing.JButton 删除指定的掉落按键;
    private javax.swing.JButton 删除指定的掉落按键1;
    private javax.swing.JButton 删除钓鱼物品;
    private javax.swing.JButton 刷新世界爆物;
    private javax.swing.JButton 刷新怪物卡片;
    private javax.swing.JButton 刷新怪物爆物;
    private javax.swing.JButton 刷新钓鱼物品;
    private javax.swing.JTabbedPane 功能设置;
    private javax.swing.JTextField 区间一最低等级;
    private javax.swing.JTextField 区间一最高等级;
    private javax.swing.JTextField 区间一经验倍率;
    private javax.swing.JTextField 区间三最低等级;
    private javax.swing.JTextField 区间三最高等级;
    private javax.swing.JTextField 区间三经验倍率;
    private javax.swing.JTextField 区间二最低等级;
    private javax.swing.JTextField 区间二最高等级;
    private javax.swing.JTextField 区间二经验倍率;
    private javax.swing.JTextField 双倍爆率持续时间;
    private javax.swing.JTextField 双倍经验持续时间;
    private javax.swing.JTextField 双倍金币持续时间;
    private javax.swing.JTable 反应堆;
    private javax.swing.JTextField 反应堆代码;
    private javax.swing.JTextField 反应堆序列号;
    private javax.swing.JTextField 反应堆概率;
    private javax.swing.JTextField 反应堆物品;
    private javax.swing.JTextField 发送装备玩家姓名;
    private javax.swing.JButton 吸怪检测开关;
    private javax.swing.JTextField 商城扩充价格修改;
    private javax.swing.JButton 喷火龙开关;
    private javax.swing.JButton 回收地图开关;
    private static javax.swing.JProgressBar 在线人数;
    private javax.swing.JTable 在线泡点设置;
    private javax.swing.JTextField 地图刷新频率;
    private javax.swing.JButton 地图名称开关;
    private javax.swing.JTextField 地图物品上限;
    private javax.swing.JPanel 大区设置;
    private javax.swing.JButton 大海龟开关;
    private javax.swing.JButton 大灰狼开关;
    private javax.swing.JButton 小白兔开关;
    private javax.swing.JButton 小青蛇开关;
    private javax.swing.JButton 屠令广播开关;
    private javax.swing.JPanel 常用工具;
    private javax.swing.JButton 幸运职业开关;
    private javax.swing.JButton 开启三倍爆率;
    private javax.swing.JButton 开启三倍经验;
    private javax.swing.JButton 开启三倍金币;
    private javax.swing.JButton 开启双倍爆率;
    private javax.swing.JButton 开启双倍经验;
    private javax.swing.JButton 开启双倍金币;
    public static javax.swing.JTextArea 弓标子弹叠加上限突破;
    private javax.swing.JTextField 怪物倍率;
    private javax.swing.JTable 怪物爆物;
    private javax.swing.JTextField 怪物爆物序列号;
    private javax.swing.JTextField 怪物爆物怪物代码;
    private javax.swing.JTextField 怪物爆物爆率;
    private javax.swing.JTextField 怪物爆物物品代码;
    private javax.swing.JTextField 怪物爆物物品名称;
    private javax.swing.JButton 怪物状态开关;
    private javax.swing.JButton 战神职业开关;
    private javax.swing.JButton 指令通知开关;
    private javax.swing.JButton 新增反应堆物品;
    private javax.swing.JButton 新增钓鱼物品;
    private static javax.swing.JProgressBar 时长;
    private javax.swing.JButton 星精灵开关;
    private javax.swing.JTextField 机器码多开数量;
    private javax.swing.JTextField 查找反应堆掉落;
    private javax.swing.JTextField 查找物品;
    private javax.swing.JButton 查询在线玩家人数按钮;
    private javax.swing.JButton 查询怪物掉落;
    private javax.swing.JTextField 查询怪物掉落代码;
    private javax.swing.JButton 查询物品掉落;
    private javax.swing.JButton 查询物品掉落1;
    private javax.swing.JTextField 查询物品掉落代码;
    private javax.swing.JTextField 查询物品掉落代码1;
    private javax.swing.JButton 欢迎弹窗开关;
    private javax.swing.JTextField 泡点值;
    private javax.swing.JButton 泡点值修改;
    private javax.swing.JTextField 泡点序号;
    private javax.swing.JButton 泡点抵用开关;
    private javax.swing.JButton 泡点点券开关;
    private javax.swing.JTextField 泡点类型;
    private javax.swing.JButton 泡点经验开关;
    private javax.swing.JButton 泡点豆豆开关;
    private javax.swing.JButton 泡点金币开关;
    private javax.swing.JButton 添加世界爆物;
    private javax.swing.JButton 添加怪物爆物;
    private javax.swing.JButton 清空日志;
    private javax.swing.JPanel 游戏公告;
    private javax.swing.JButton 游戏升级快讯;
    private javax.swing.JButton 游戏喇叭开关;
    private javax.swing.JButton 游戏指令开关;
    private javax.swing.JButton 游戏经验加成说明;
    private javax.swing.JButton 滚动公告开关;
    private javax.swing.JButton 漂漂猪开关;
    private javax.swing.JButton 火野猪开关;
    private javax.swing.JTextField 物品;
    private javax.swing.JTextField 物品叠加数量;
    private javax.swing.JTextField 物品掉落持续时间;
    private javax.swing.JPanel 物品爆率;
    private javax.swing.JButton 物品确认;
    private javax.swing.JButton 玩家交易开关;
    private javax.swing.JButton 玩家聊天开关;
    private javax.swing.JButton 登陆帮助开关;
    private javax.swing.JButton 白雪人开关;
    private javax.swing.JButton 石头人开关;
    private javax.swing.JButton 神秘商人开关;
    private javax.swing.JButton 禁止登陆开关;
    private javax.swing.JPanel 福利中心;
    private javax.swing.JLabel 福利提示语言2;
    private javax.swing.JButton 章鱼怪开关;
    private javax.swing.JButton 管理加速开关;
    private javax.swing.JButton 管理隐身开关;
    private javax.swing.JButton 紫色猫开关;
    private javax.swing.JButton 红螃蟹开关;
    private javax.swing.JTextField 经验;
    private javax.swing.JTable 经验加成表;
    private javax.swing.JButton 经验加成表修改;
    private javax.swing.JTextField 经验加成表序号;
    private javax.swing.JTextField 经验加成表数值;
    private javax.swing.JTextField 经验加成表类型;
    private javax.swing.JButton 经验确认;
    private javax.swing.JButton 经验确认1;
    private javax.swing.JButton 经验确认2;
    private javax.swing.JButton 经验确认3;
    private javax.swing.JButton 给予物品;
    private javax.swing.JButton 给予物品1;
    private javax.swing.JButton 给予装备1;
    private javax.swing.JButton 给予装备2;
    private javax.swing.JButton 绿水灵开关;
    private javax.swing.JButton 胖企鹅开关;
    private javax.swing.JButton 花蘑菇开关;
    private javax.swing.JButton 蓝蜗牛开关;
    private javax.swing.JButton 蘑菇仔开关;
    private javax.swing.JTextField 角色元宝编辑框;
    private javax.swing.JTextField 角色名称编辑框;
    private javax.swing.JTextField 角色所在地图编辑;
    private javax.swing.JTextField 角色抵用编辑框;
    private javax.swing.JTextField 角色点券编辑框;
    private javax.swing.JButton 越级打怪开关;
    private javax.swing.JTextArea 输出窗口;
    private javax.swing.JButton 过图存档开关;
    private javax.swing.JButton 重载任务2;
    private javax.swing.JButton 重载传送门按钮2;
    private javax.swing.JButton 重载副本按钮2;
    private javax.swing.JButton 重载包头按钮2;
    private javax.swing.JButton 重载反应堆按钮2;
    private javax.swing.JButton 重载商城按钮2;
    private javax.swing.JButton 重载商店按钮2;
    private javax.swing.JButton 重载爆率按钮2;
    private javax.swing.JButton 重载箱子反应堆按钮;
    private javax.swing.JTextField 金币;
    private javax.swing.JButton 金币确认;
    private javax.swing.JTable 钓鱼物品;
    private javax.swing.JTextField 钓鱼物品代码;
    private javax.swing.JTextField 钓鱼物品名称;
    private javax.swing.JTextField 钓鱼物品序号;
    private javax.swing.JTextField 钓鱼物品概率;
    private javax.swing.JPanel 钓鱼管理;
    private javax.swing.JButton 雇佣商人开关;
    private javax.swing.JButton 青鳄鱼开关;
    private javax.swing.JButton 顽皮猴开关;
    private javax.swing.JPanel 首页功能;
    private javax.swing.JTextField 骑士团等级上限;
    private javax.swing.JButton 骑士团职业开关;
    private javax.swing.JButton 魔族攻城开关;
    private javax.swing.JButton 魔族突袭开关;
    // End of variables declaration//GEN-END:variables

}
