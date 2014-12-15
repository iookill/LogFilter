import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class LogFilterMain extends JFrame implements INotiEvent
{
    private static final long serialVersionUID           = 1L;
    
    static final String       LOGFILTER                  = "LogFilter";
    static final String       VERSION                    = "Version 1.8";
    final String              COMBO_ANDROID              = "Android          ";
    final String              COMBO_IOS                  = "ios";
    final String              COMBO_CUSTOM_COMMAND       = "custom command";
    final String              IOS_DEFAULT_CMD            = "adb logcat -v time ";
    final String              IOS_SELECTED_CMD_FIRST     = "adb -s ";
    final String              IOS_SELECTED_CMD_LAST      = " logcat -v time ";
//    final String              ANDROID_DEFAULT_CMD        = "logcat -v time ";
//    final String              ANDROID_THREAD_CMD         = "logcat -v threadtime ";
//    final String              ANDROID_EVENT_CMD          = "logcat -b events -v time ";
//    final String              ANDROID_RADIO_CMD          = "logcat -b radio -v time          ";
//    final String              ANDROID_CUSTOM_CMD         = "logcat ";
    final String              ANDROID_DEFAULT_CMD_FIRST  = "adb ";
    final String              ANDROID_SELECTED_CMD_FIRST = "adb -s ";
//    final String              ANDROID_SELECTED_CMD_LAST  = " logcat -v time ";
    final String[]            DEVICES_CMD                = {"adb devices", "", ""};
    
    static final int          DEFAULT_WIDTH              = 1200;
    static final int          DEFAULT_HEIGHT             = 720;
    static final int          MIN_WIDTH                  = 1100;
    static final int          MIN_HEIGHT                 = 500;
    
    static final int          DEVICES_ANDROID            = 0;
    static final int          DEVICES_IOS                = 1;
    static final int          DEVICES_CUSTOM             = 2;
    
    static final int          STATUS_CHANGE              = 1;
    static final int          STATUS_PARSING             = 2;
    static final int          STATUS_READY               = 4;
    
    final int                 L                          = SwingConstants.LEFT;
    final int                 C                          = SwingConstants.CENTER;
    final int                 R                          = SwingConstants.RIGHT;
    
    JTabbedPane               m_tpTab;
    JTextField                m_tfStatus;
    IndicatorPanel            m_ipIndicator;
    ArrayList<TagInfo>        m_arTagInfo;
    ArrayList<LogInfo>        m_arLogInfoAll;
    ArrayList<LogInfo>        m_arLogInfoFiltered;
    HashMap<Integer, Integer> m_hmBookmarkAll;
    HashMap<Integer, Integer> m_hmBookmarkFiltered;
    HashMap<Integer, Integer> m_hmErrorAll;
    HashMap<Integer, Integer> m_hmErrorFiltered;
    ILogParser                m_iLogParser;
    LogTable                  m_tbLogTable;
//    TagTable                    m_tbTagTable;
    JScrollPane               m_scrollVBar;
//    JScrollPane                 m_scrollVTagBar;
    LogFilterTableModel       m_tmLogTableModel;
//    TagFilterTableModel         m_tmTagTableModel;
    boolean                   m_bUserFilter;
    
    //Word Filter, tag filter
    JTextField                m_tfHighlight;
    JTextField                m_tfFindWord;
    JTextField                m_tfRemoveWord;
    JTextField                m_tfShowTag;
    JTextField                m_tfRemoveTag;
    JTextField                m_tfShowPid;
    JTextField                m_tfShowTid;
    
    //Device
    JButton                   m_btnDevice;
    JList                     m_lDeviceList;
    JComboBox                 m_comboDeviceCmd;
    JComboBox                 m_comboCmd;
    JButton                   m_btnSetFont;

    //Log filter enable/disable
    JCheckBox                 m_chkEnableFind;
    JCheckBox                 m_chkEnableRemove;
    JCheckBox                 m_chkEnableShowTag;
    JCheckBox                 m_chkEnableRemoveTag;
    JCheckBox                 m_chkEnableShowPid;
    JCheckBox                 m_chkEnableShowTid;
    JCheckBox                 m_chkEnableHighlight;

    //Log filter
    JCheckBox                 m_chkVerbose;
    JCheckBox                 m_chkDebug;
    JCheckBox                 m_chkInfo;
    JCheckBox                 m_chkWarn;
    JCheckBox                 m_chkError;
    JCheckBox                 m_chkFatal;
    
    //Show column
    JCheckBox                 m_chkClmBookmark;
    JCheckBox                 m_chkClmLine;
    JCheckBox                 m_chkClmDate;
    JCheckBox                 m_chkClmTime;
    JCheckBox                 m_chkClmLogLV;
    JCheckBox                 m_chkClmPid;
    JCheckBox                 m_chkClmThread;
    JCheckBox                 m_chkClmTag;
    JCheckBox                 m_chkClmMessage;
    
    JTextField                m_tfFontSize;
//    JTextField                  m_tfProcessCmd;
    JComboBox                 m_comboEncode;
    JComboBox                 m_jcFontType;
    JButton                   m_btnRun;
    JButton                   m_btnClear;
    JToggleButton             m_tbtnPause;
    JButton                   m_btnStop;
    
    String                    m_strLogFileName;
    String                    m_strSelectedDevice;
//    String                      m_strProcessCmd;
    Process                   m_Process;
    Thread                    m_thProcess;
    Thread                    m_thWatchFile;
    Thread                    m_thFilterParse;
    boolean                   m_bPauseADB;
    
    Object                    FILE_LOCK;
    Object                    FILTER_LOCK;
    volatile int              m_nChangedFilter;
    int                       m_nFilterLogLV;
    int                       m_nWinWidth  = DEFAULT_WIDTH;
    int                       m_nWinHeight = DEFAULT_HEIGHT;
    int                       m_nLastWidth;
    int                       m_nLastHeight;
    int                       m_nWindState;
    static RecentFileMenu     m_recentMenu;
//    String                    m_strLastDir;

    public static void main(final String args[])
    {
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final LogFilterMain mainFrame = new LogFilterMain();
        mainFrame.setTitle(LOGFILTER + " " + VERSION);
//        mainFrame.addWindowListener(new WindowEventHandler());

        JMenuBar menubar = new JMenuBar();
        JMenu file = new JMenu("File");
        file.setMnemonic(KeyEvent.VK_F);

        JMenuItem fileOpen = new JMenuItem("Open");
        fileOpen.setMnemonic(KeyEvent.VK_O);
        fileOpen.setAccelerator( KeyStroke.getKeyStroke(KeyEvent.VK_O,
                ActionEvent.ALT_MASK) );
        fileOpen.setToolTipText("Open log file");
        fileOpen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                mainFrame.openFileBrowser();
            }
        });
        
        m_recentMenu = new RecentFileMenu("RecentFile",10){
            public void onSelectFile(String filePath){
                mainFrame.parseFile(new File(filePath));
            }
        };
        
        file.add(fileOpen);
        file.add(m_recentMenu);

        menubar.add(file);
        mainFrame.setJMenuBar(menubar);
        
        if(args != null && args.length > 0)
        {
            EventQueue.invokeLater(new Runnable()
            {
                public void run()
                {
                    mainFrame.parseFile(new File(args[0]));
                }
            });
        }
    }

    String makeFilename()
    {
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return "LogFilter_" + format.format(now) + ".txt";
    }
    
    void exit()
    {
        if(m_Process != null) m_Process.destroy();
        if(m_thProcess != null) m_thProcess.interrupt();
        if(m_thWatchFile != null) m_thWatchFile.interrupt();
        if(m_thFilterParse != null) m_thFilterParse.interrupt();

        saveFilter();
        saveColor();
        System.exit(0);
    }

    /**
     * @throws HeadlessException
     */
    public LogFilterMain()
    {
        super();
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                exit();
            }
        });
        initValue();
        createComponent();

        Container pane = getContentPane();
        pane.setLayout(new BorderLayout());

        pane.add(getOptionPanel(), BorderLayout.NORTH);
        pane.add(getBookmarkPanel(), BorderLayout.WEST);
        pane.add(getStatusPanel(), BorderLayout.SOUTH);
        pane.add(getTabPanel(), BorderLayout.CENTER);

        setDnDListener();
        addChangeListener();
        startFilterParse();

        setVisible(true);
        addDesc();
        loadFilter();
        loadColor();
        loadCmd();
        m_tbLogTable.setColumnWidth();

//        if(m_nWindState == JFrame.MAXIMIZED_BOTH)
//        else
            setSize(m_nWinWidth, m_nWinHeight);
            setExtendedState( m_nWindState );
        setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
    }
    
    final String INI_FILE           = "LogFilter.ini";
    final String INI_FILE_CMD       = "LogFilterCmd.ini";
    final String INI_FILE_COLOR     = "LogFilterColor.ini";
    final String INI_LAST_DIR       = "LAST_DIR";
    final String INI_CMD_COUNT      = "CMD_COUNT";
    final String INI_CMD            = "CMD_";
    final String INI_FONT_TYPE      = "FONT_TYPE";
    final String INI_WORD_FIND      = "WORD_FIND";
    final String INI_WORD_REMOVE    = "WORD_REMOVE";
    final String INI_TAG_SHOW       = "TAG_SHOW";
    final String INI_TAG_REMOVE     = "TAG_REMOVE";
    final String INI_HIGHLIGHT      = "HIGHLIGHT";
    final String INI_PID_SHOW       = "PID_SHOW";
    final String INI_TID_SHOW       = "TID_SHOW";
    final String INI_COLOR_0        = "INI_COLOR_0";
    final String INI_COLOR_1        = "INI_COLOR_1";
    final String INI_COLOR_2        = "INI_COLOR_2";
    final String INI_COLOR_3        = "INI_COLOR_3(E)";
    final String INI_COLOR_4        = "INI_COLOR_4(W)";
    final String INI_COLOR_5        = "INI_COLOR_5";
    final String INI_COLOR_6        = "INI_COLOR_6(I)";
    final String INI_COLOR_7        = "INI_COLOR_7(D)";
    final String INI_COLOR_8        = "INI_COLOR_8(F)";
    final String INI_HIGILIGHT_COUNT= "INI_HIGILIGHT_COUNT";
    final String INI_HIGILIGHT_=    "INI_HIGILIGHT_";
    final String INI_WIDTH          = "INI_WIDTH";
    final String INI_HEIGHT         = "INI_HEIGHT";
    final String INI_WINDOW_STATE   = "INI_WINDOW_STATE";

    final String INI_COMUMN         = "INI_COMUMN_";
    
    void loadCmd()
    {
        try
        {
            Properties p = new Properties();
            
            // ini 파일 읽기
            p.load(new FileInputStream(INI_FILE_CMD));
            
            T.d("p.getProperty(INI_CMD_COUNT) = " + p.getProperty(INI_CMD_COUNT));
            int nCount = Integer.parseInt(p.getProperty(INI_CMD_COUNT));
            T.d("nCount = " + nCount);
            for(int nIndex = 0; nIndex < nCount; nIndex++)
            {
                T.d("CMD = " + INI_CMD + nIndex);
                m_comboCmd.addItem(p.getProperty(INI_CMD + nIndex));
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }
    
    void loadColor()
    {
        try
        {
            Properties p = new Properties();
            
            p.load(new FileInputStream(INI_FILE_COLOR));
            
            LogColor.COLOR_0 = Integer.parseInt(p.getProperty(INI_COLOR_0).replace("0x", ""), 16);
            LogColor.COLOR_1 = Integer.parseInt(p.getProperty(INI_COLOR_1).replace("0x", ""), 16);
            LogColor.COLOR_2 = Integer.parseInt(p.getProperty(INI_COLOR_2).replace("0x", ""), 16);
            LogColor.COLOR_ERROR = LogColor.COLOR_3 = Integer.parseInt(p.getProperty(INI_COLOR_3).replace("0x", ""), 16);
            LogColor.COLOR_WARN  = LogColor.COLOR_4 = Integer.parseInt(p.getProperty(INI_COLOR_4).replace("0x", ""), 16);
            LogColor.COLOR_5 = Integer.parseInt(p.getProperty(INI_COLOR_5).replace("0x", ""), 16);
            LogColor.COLOR_INFO  = LogColor.COLOR_6 = Integer.parseInt(p.getProperty(INI_COLOR_6).replace("0x", ""), 16);
            LogColor.COLOR_DEBUG = LogColor.COLOR_7 = Integer.parseInt(p.getProperty(INI_COLOR_7).replace("0x", ""), 16);
            LogColor.COLOR_FATAL = LogColor.COLOR_8 = Integer.parseInt(p.getProperty(INI_COLOR_8).replace("0x", ""), 16);
            
            int nCount = Integer.parseInt(p.getProperty( INI_HIGILIGHT_COUNT, "0" ));
            if(nCount > 0)
            {
                LogColor.COLOR_HIGHLIGHT = new String[nCount];
                for(int nIndex = 0; nIndex < nCount; nIndex++)
                    LogColor.COLOR_HIGHLIGHT[nIndex] = p.getProperty(INI_HIGILIGHT_ + nIndex).replace("0x", "");
            }
            else
            {
                LogColor.COLOR_HIGHLIGHT = new String[1];
                LogColor.COLOR_HIGHLIGHT[0] = "ffff";
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }
    
    void saveColor()
    {
        try
        {
            Properties p = new Properties();

            p.setProperty(INI_COLOR_0, "0x" + Integer.toHexString(LogColor.COLOR_0).toUpperCase());
            p.setProperty(INI_COLOR_1, "0x" + Integer.toHexString(LogColor.COLOR_1).toUpperCase());
            p.setProperty(INI_COLOR_2, "0x" + Integer.toHexString(LogColor.COLOR_2).toUpperCase());
            p.setProperty(INI_COLOR_3, "0x" + Integer.toHexString(LogColor.COLOR_3).toUpperCase());
            p.setProperty(INI_COLOR_4, "0x" + Integer.toHexString(LogColor.COLOR_4).toUpperCase());
            p.setProperty(INI_COLOR_5, "0x" + Integer.toHexString(LogColor.COLOR_5).toUpperCase());
            p.setProperty(INI_COLOR_6, "0x" + Integer.toHexString(LogColor.COLOR_6).toUpperCase());
            p.setProperty(INI_COLOR_7, "0x" + Integer.toHexString(LogColor.COLOR_7).toUpperCase());
            p.setProperty(INI_COLOR_8, "0x" + Integer.toHexString(LogColor.COLOR_8).toUpperCase());

            if(LogColor.COLOR_HIGHLIGHT != null)
            {
                p.setProperty(INI_HIGILIGHT_COUNT, "" + LogColor.COLOR_HIGHLIGHT.length);
                for(int nIndex = 0; nIndex < LogColor.COLOR_HIGHLIGHT.length; nIndex++)
                    p.setProperty(INI_HIGILIGHT_ + nIndex, "0x" + LogColor.COLOR_HIGHLIGHT[nIndex].toUpperCase());
            }

            p.store( new FileOutputStream(INI_FILE_COLOR), "done.");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    void loadFilter()
    {
        try
        {
            Properties p = new Properties();
            
            // ini 파일 읽기
            p.load(new FileInputStream(INI_FILE));
            
            // Key 값 읽기
            String strFontType = p.getProperty(INI_FONT_TYPE);
            if(strFontType != null && strFontType.length() > 0)
                m_jcFontType.setSelectedItem(p.getProperty(INI_FONT_TYPE));
            m_tfFindWord.setText(p.getProperty(INI_WORD_FIND));
            m_tfRemoveWord.setText(p.getProperty(INI_WORD_REMOVE));
            m_tfShowTag.setText(p.getProperty(INI_TAG_SHOW));
            m_tfRemoveTag.setText(p.getProperty(INI_TAG_REMOVE));
            m_tfShowPid.setText(p.getProperty(INI_PID_SHOW));
            m_tfShowTid.setText(p.getProperty(INI_TID_SHOW));
            m_tfHighlight.setText(p.getProperty(INI_HIGHLIGHT));
            m_nWinWidth  = Integer.parseInt( p.getProperty( INI_WIDTH ));
            m_nWinHeight = Integer.parseInt( p.getProperty( INI_HEIGHT ));
            m_nWindState = Integer.parseInt( p.getProperty( INI_WINDOW_STATE ));
            
            for(int nIndex = 0; nIndex < LogFilterTableModel.COMUMN_MAX; nIndex++)
            {
                LogFilterTableModel.setColumnWidth( nIndex, Integer.parseInt( p.getProperty( INI_COMUMN + nIndex) ) );
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }
    
    void saveFilter()
    {
        try
        {
            m_nWinWidth  = m_nLastWidth;
            m_nWinHeight = m_nLastHeight;
            m_nWindState = getExtendedState();
            T.d("m_nWindState = " + m_nWindState);
            
            Properties p = new Properties();
//            p.setProperty( INI_LAST_DIR, m_strLastDir );
            p.setProperty(INI_FONT_TYPE,   (String)m_jcFontType.getSelectedItem());
            p.setProperty(INI_WORD_FIND,   m_tfFindWord.getText());
            p.setProperty(INI_WORD_REMOVE, m_tfRemoveWord.getText());
            p.setProperty(INI_TAG_SHOW,    m_tfShowTag.getText());
            p.setProperty(INI_TAG_REMOVE,  m_tfRemoveTag.getText());
            p.setProperty(INI_PID_SHOW,    m_tfShowPid.getText());
            p.setProperty(INI_TID_SHOW,    m_tfShowTid.getText());
            p.setProperty(INI_HIGHLIGHT,   m_tfHighlight.getText());
            p.setProperty(INI_WIDTH,       "" + m_nWinWidth);
            p.setProperty(INI_HEIGHT,      "" + m_nWinHeight);
            p.setProperty(INI_WINDOW_STATE,"" + m_nWindState);

            for(int nIndex = 0; nIndex < LogFilterTableModel.COMUMN_MAX; nIndex++)
            {
                p.setProperty(INI_COMUMN + nIndex, "" + m_tbLogTable.getColumnWidth(nIndex));
            }
            p.store( new FileOutputStream(INI_FILE), "done.");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    void addDesc(String strMessage)
    {
        LogInfo logInfo = new LogInfo();
        logInfo.m_strLine = "" + (m_arLogInfoAll.size() + 1);
        logInfo.m_strMessage = strMessage;
        m_arLogInfoAll.add(logInfo);
    }

    void addDesc()
    {
        addDesc(VERSION);
        addDesc("");
        addDesc("Version 1.8 : java -jar LogFilter_xx.jar [filename] 추가");
        addDesc("Version 1.7 : copy시 보이는 column만 clipboard에 복사(Line 제외)");
        addDesc("Version 1.6 : cmd콤보박스 길이 고정");
        addDesc("Version 1.5 : Highlight color list추가()");
        addDesc("   - LogFilterColor.ini 에 카운트와 값 넣어 주시면 됩니다.");
        addDesc("   - ex)INI_HIGILIGHT_COUNT=2");
        addDesc("   -    INI_COLOR_HIGILIGHT_0=0xFFFF");
        addDesc("   -    INI_COLOR_HIGILIGHT_1=0x00FF");
        addDesc("Version 1.4 : 창크기 저장");
        addDesc("Version 1.3 : recent file 및 open메뉴추가");
        addDesc("Version 1.2 : Tid 필터 추가");
        addDesc("Version 1.1 : Level F 추가");
        addDesc("Version 1.0 : Pid filter 추가");
        addDesc("Version 0.9 : Font type 추가");
        addDesc("Version 0.8 : 필터체크 박스 추가");
        addDesc("Version 0.7 : 커널로그 파싱/LogFilter.ini에 컬러정의(0~7)");
        addDesc("Version 0.6 : 필터 대소문 무시");
        addDesc("Version 0.5 : 명령어 ini파일로 저장");
        addDesc("Version 0.4 : add thread option, filter 저장");
        addDesc("Version 0.3 : 단말 선택 안되는 문제 수정");
        addDesc("");
        addDesc("[Tag]");
        addDesc("Alt+L/R Click : Show/Remove tag");
        addDesc("");
        addDesc("[Bookmark]");
        addDesc("Ctrl+F2/double click: bookmark toggle");
        addDesc("F2 : pre bookmark");
        addDesc("F3 : next bookmark");
        addDesc("");
        addDesc("[Copy]");
        addDesc("Ctrl+c : row copy");
        addDesc("right click : cloumn copy");
        addDesc("");
        addDesc("[New version]");
        addDesc("http://blog.naver.com/iookill/140135139931");
    }

    /**
     * @param nIndex    실제 리스트의 인덱스
     * @param nLine     m_strLine
     * @param bBookmark
     */
    void bookmarkItem(int nIndex, int nLine, boolean bBookmark)
    {
        synchronized(FILTER_LOCK)
        {
            LogInfo logInfo = m_arLogInfoAll.get(nLine);
            logInfo.m_bMarked = bBookmark;
            m_arLogInfoAll.set(nLine, logInfo);

            if(logInfo.m_bMarked)
            {
                m_hmBookmarkAll.put(nLine, nLine);
                if(m_bUserFilter)
                    m_hmBookmarkFiltered.put(nLine, nIndex);
            }
            else
            {
                m_hmBookmarkAll.remove(nLine);
                if(m_bUserFilter)
                    m_hmBookmarkFiltered.remove(nLine);
            }
        }
        m_ipIndicator.repaint();
    }

    void clearData()
    {
        m_arTagInfo.clear();
        m_arLogInfoAll.clear();
        m_arLogInfoFiltered.clear();
        m_hmBookmarkAll.clear();
        m_hmBookmarkFiltered.clear();
        m_hmErrorAll.clear();
        m_hmErrorFiltered.clear();
    }

    void createComponent()
    {
    }

    Component getBookmarkPanel()
    {
        JPanel jp = new JPanel();
        jp.setLayout(new BorderLayout());

//        //iookill
//        m_tmTagTableModel = new TagFilterTableModel();
//        m_tmTagTableModel.setData(m_arTagInfo);
//        m_tbTagTable = new TagTable(m_tmTagTableModel, this);
//
//        m_scrollVTagBar = new JScrollPane(m_tbTagTable);
//        m_scrollVTagBar.setPreferredSize(new Dimension(182,50));
//        // show list
//        jp.add(m_scrollVTagBar, BorderLayout.WEST);

        m_ipIndicator = new IndicatorPanel(this);
        m_ipIndicator.setData(m_arLogInfoAll, m_hmBookmarkAll, m_hmErrorAll);
        jp.add(m_ipIndicator, BorderLayout.CENTER);
        return jp;
    }

    Component getCmdPanel()
    {
        JPanel jpOptionDevice = new JPanel();
        jpOptionDevice.setBorder(BorderFactory.createTitledBorder("Device select"));
        jpOptionDevice.setLayout(new BorderLayout());
//        jpOptionDevice.setPreferredSize(new Dimension(200, 100));

        JPanel jpCmd = new JPanel();
        m_comboDeviceCmd = new JComboBox();
        m_comboDeviceCmd.addItem(COMBO_ANDROID);
//        m_comboDeviceCmd.addItem(COMBO_IOS);
//        m_comboDeviceCmd.addItem(CUSTOM_COMMAND);
        m_comboDeviceCmd.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent e)
            {
                if(e.getStateChange() != ItemEvent.SELECTED) return;

                DefaultListModel listModel = (DefaultListModel)m_lDeviceList.getModel();
                listModel.clear();
                if (e.getItem().equals(COMBO_CUSTOM_COMMAND)) {
                    m_comboDeviceCmd.setEditable(true);
                } else {
                    m_comboDeviceCmd.setEditable(false);
                }
                setProcessCmd(m_comboDeviceCmd.getSelectedIndex(), m_strSelectedDevice);
            }
        });

        final DefaultListModel listModel = new DefaultListModel();
        m_btnDevice = new JButton("OK");
        m_btnDevice.setMargin(new Insets(0, 0, 0, 0));
        m_btnDevice.addActionListener(m_alButtonListener);

        jpCmd.add(m_comboDeviceCmd);
        jpCmd.add(m_btnDevice);

        jpOptionDevice.add(jpCmd, BorderLayout.NORTH);

        m_lDeviceList = new JList(listModel);
        JScrollPane vbar = new JScrollPane(m_lDeviceList);
        vbar.setPreferredSize(new Dimension(100,50));
        m_lDeviceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        m_lDeviceList.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                JList deviceList = (JList)e.getSource();
                Object selectedItem = (Object)deviceList.getSelectedValue();
                m_strSelectedDevice = "";
                if(selectedItem != null)
                {
                    m_strSelectedDevice = selectedItem.toString();
                    m_strSelectedDevice = m_strSelectedDevice.replace("\t", " ").replace("device", "").replace("offline", "");
                    setProcessCmd(m_comboDeviceCmd.getSelectedIndex(), m_strSelectedDevice);
                }
            }
        });
        jpOptionDevice.add(vbar);

        return jpOptionDevice;
    }

    void addTagList(String strTag)
    {
//        for(TagInfo tagInfo : m_arTagInfo)
//            if(tagInfo.m_strTag.equals(strTag))
//                return;
//        String strRemoveFilter = m_tbLogTable.GetFilterRemoveTag();
//        String strShowFilter = m_tbLogTable.GetFilterShowTag();
//        TagInfo tagInfo = new TagInfo();
//        tagInfo.m_strTag = strTag;
//        if(strRemoveFilter.contains(strTag))
//            tagInfo.m_bRemove = true;
//        if(strShowFilter.contains(strTag))
//            tagInfo.m_bShow = true;
//        m_arTagInfo.add(tagInfo);
//        m_tmTagTableModel.setData(m_arTagInfo);
//
//        m_tmTagTableModel.fireTableRowsUpdated(0, m_tmTagTableModel.getRowCount() - 1);
//        m_scrollVTagBar.validate();
//        m_tbTagTable.invalidate();
//        m_tbTagTable.repaint();
//            m_tbTagTable.changeSelection(0, 0, false, false);
    }

    void addLogInfo(LogInfo logInfo)
    {
        synchronized(FILTER_LOCK)
        {
            m_tbLogTable.setTagLength( logInfo.m_strTag.length() );
            m_arLogInfoAll.add(logInfo);
//            addTagList(logInfo.m_strTag);
            if(logInfo.m_strLogLV.equals("E") || logInfo.m_strLogLV.equals("ERROR"))
                m_hmErrorAll.put(Integer.parseInt(logInfo.m_strLine) - 1, Integer.parseInt(logInfo.m_strLine) - 1);

            if(m_bUserFilter)
            {
                if(m_ipIndicator.m_chBookmark.isSelected() || m_ipIndicator.m_chError.isSelected())
                {
                    boolean bAddFilteredArray = false;
                    if(logInfo.m_bMarked && m_ipIndicator.m_chBookmark.isSelected())
                    {
                        bAddFilteredArray = true;
                        m_hmBookmarkFiltered.put(Integer.parseInt(logInfo.m_strLine) - 1, m_arLogInfoFiltered.size());
                        if(logInfo.m_strLogLV.equals("E") || logInfo.m_strLogLV.equals("ERROR"))
                            m_hmErrorFiltered.put(Integer.parseInt(logInfo.m_strLine) - 1, m_arLogInfoFiltered.size());
                    }
                    if((logInfo.m_strLogLV.equals("E") || logInfo.m_strLogLV.equals("ERROR")) && m_ipIndicator.m_chError.isSelected())
                    {
                        bAddFilteredArray = true;
                        m_hmErrorFiltered.put(Integer.parseInt(logInfo.m_strLine) - 1, m_arLogInfoFiltered.size());
                        if(logInfo.m_bMarked)
                            m_hmBookmarkFiltered.put(Integer.parseInt(logInfo.m_strLine) - 1, m_arLogInfoFiltered.size());
                    }

                    if(bAddFilteredArray) m_arLogInfoFiltered.add(logInfo);
                }
                else if(checkLogLVFilter(logInfo)
                        && checkPidFilter(logInfo)
                        && checkTidFilter(logInfo)
                        && checkShowTagFilter(logInfo)
                        && checkRemoveTagFilter(logInfo)
                        && checkFindFilter(logInfo)
                        && checkRemoveFilter(logInfo))
                {
                    m_arLogInfoFiltered.add(logInfo);
                    if(logInfo.m_bMarked)
                        m_hmBookmarkFiltered.put(Integer.parseInt(logInfo.m_strLine) - 1, m_arLogInfoFiltered.size());
                    if(logInfo.m_strLogLV == "E" || logInfo.m_strLogLV == "ERROR")
                        if(logInfo.m_strLogLV.equals("E") || logInfo.m_strLogLV.equals("ERROR"))
                            m_hmErrorFiltered.put(Integer.parseInt(logInfo.m_strLine) - 1, m_arLogInfoFiltered.size());
                }
            }
        }
    }

    void addChangeListener()
    {
        m_tfHighlight.getDocument().addDocumentListener(m_dlFilterListener);
        m_tfFindWord.getDocument().addDocumentListener(m_dlFilterListener);
        m_tfRemoveWord.getDocument().addDocumentListener(m_dlFilterListener);
        m_tfShowTag.getDocument().addDocumentListener(m_dlFilterListener);
        m_tfRemoveTag.getDocument().addDocumentListener(m_dlFilterListener);
        m_tfShowPid.getDocument().addDocumentListener(m_dlFilterListener);
        m_tfShowTid.getDocument().addDocumentListener(m_dlFilterListener);

        m_chkEnableFind.addItemListener(m_itemListener);
        m_chkEnableRemove.addItemListener(m_itemListener);
        m_chkEnableShowPid.addItemListener(m_itemListener);
        m_chkEnableShowTid.addItemListener(m_itemListener);
        m_chkEnableShowTag.addItemListener(m_itemListener);
        m_chkEnableRemoveTag.addItemListener(m_itemListener);
        m_chkEnableHighlight.addItemListener(m_itemListener);

        m_chkVerbose.addItemListener(m_itemListener);
        m_chkDebug.addItemListener(m_itemListener);
        m_chkInfo.addItemListener(m_itemListener);
        m_chkWarn.addItemListener(m_itemListener);
        m_chkError.addItemListener(m_itemListener);
        m_chkFatal.addItemListener(m_itemListener);
        m_chkClmBookmark.addItemListener(m_itemListener);
        m_chkClmLine.addItemListener(m_itemListener);
        m_chkClmDate.addItemListener(m_itemListener);
        m_chkClmTime.addItemListener(m_itemListener);
        m_chkClmLogLV.addItemListener(m_itemListener);
        m_chkClmPid.addItemListener(m_itemListener);
        m_chkClmThread.addItemListener(m_itemListener);
        m_chkClmTag.addItemListener(m_itemListener);
        m_chkClmMessage.addItemListener(m_itemListener);


        m_scrollVBar.getViewport().addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent e)
            {
//                m_ipIndicator.m_bDrawFull = false;
                if(getExtendedState() != JFrame.MAXIMIZED_BOTH)
                {
                    m_nLastWidth  = getWidth();
                    m_nLastHeight = getHeight();
                }
                m_ipIndicator.repaint();
            }
        });
    }

    Component getFilterPanel()
    {
        m_chkEnableFind         = new JCheckBox();
        m_chkEnableRemove       = new JCheckBox();
        m_chkEnableShowTag      = new JCheckBox();
        m_chkEnableRemoveTag    = new JCheckBox();
        m_chkEnableShowPid      = new JCheckBox();
        m_chkEnableShowTid      = new JCheckBox();
        m_chkEnableFind.setSelected(true);
        m_chkEnableRemove.setSelected(true);
        m_chkEnableShowTag.setSelected(true);
        m_chkEnableRemoveTag.setSelected(true);
        m_chkEnableShowPid.setSelected(true);
        m_chkEnableShowTid.setSelected(true);

        m_tfFindWord    = new JTextField();
        m_tfRemoveWord  = new JTextField();
        m_tfShowTag     = new JTextField();
        m_tfRemoveTag   = new JTextField();
        m_tfShowPid     = new JTextField();
        m_tfShowTid     = new JTextField();

        JPanel jpMain = new JPanel(new BorderLayout());

        JPanel jpWordFilter = new JPanel(new BorderLayout());
        jpWordFilter.setBorder(BorderFactory.createTitledBorder("Word filter"));

        JPanel jpFind = new JPanel(new BorderLayout());
        JLabel find = new JLabel();
        find.setText("        Find : ");
        jpFind.add(find, BorderLayout.WEST);
        jpFind.add(m_tfFindWord, BorderLayout.CENTER);
        jpFind.add(m_chkEnableFind, BorderLayout.EAST);

        JPanel jpRemove = new JPanel(new BorderLayout());
        JLabel remove = new JLabel();
        remove.setText("Remove : ");
        jpRemove.add(remove, BorderLayout.WEST);
        jpRemove.add(m_tfRemoveWord, BorderLayout.CENTER);
        jpRemove.add(m_chkEnableRemove, BorderLayout.EAST);

        jpWordFilter.add(jpFind, BorderLayout.NORTH);
        jpWordFilter.add(jpRemove);

        jpMain.add(jpWordFilter, BorderLayout.NORTH);

        JPanel jpTagFilter = new JPanel(new GridLayout(4, 1));
        jpTagFilter.setBorder(BorderFactory.createTitledBorder("Tag filter"));

        JPanel jpPid = new JPanel(new BorderLayout());
        JLabel pid = new JLabel();
        pid.setText("         Pid : ");
        jpPid.add(pid, BorderLayout.WEST);
        jpPid.add(m_tfShowPid, BorderLayout.CENTER);
        jpPid.add(m_chkEnableShowPid, BorderLayout.EAST);

        JPanel jpTid = new JPanel(new BorderLayout());
        JLabel tid = new JLabel();
        tid.setText("         Tid : ");
        jpTid.add(tid, BorderLayout.WEST);
        jpTid.add(m_tfShowTid, BorderLayout.CENTER);
        jpTid.add(m_chkEnableShowTid, BorderLayout.EAST);

        JPanel jpShow = new JPanel(new BorderLayout());
        JLabel show = new JLabel();
        show.setText("     Show : ");
        jpShow.add(show, BorderLayout.WEST);
        jpShow.add(m_tfShowTag, BorderLayout.CENTER);
        jpShow.add(m_chkEnableShowTag, BorderLayout.EAST);

        JPanel jpRemoveTag = new JPanel(new BorderLayout());
        JLabel removeTag = new JLabel();
        removeTag.setText("Remove : ");
        jpRemoveTag.add(removeTag, BorderLayout.WEST);
        jpRemoveTag.add(m_tfRemoveTag, BorderLayout.CENTER);
        jpRemoveTag.add(m_chkEnableRemoveTag, BorderLayout.EAST);

        jpTagFilter.add(jpPid);
        jpTagFilter.add(jpTid);
        jpTagFilter.add(jpShow);
        jpTagFilter.add(jpRemoveTag);

        jpMain.add(jpTagFilter, BorderLayout.CENTER);

        return jpMain;
    }

    Component getHighlightPanel()
    {
        m_chkEnableHighlight   = new JCheckBox();
        m_chkEnableHighlight.setSelected(true);

        m_tfHighlight   = new JTextField();

        JPanel jpMain = new JPanel(new BorderLayout());
        jpMain.setBorder(BorderFactory.createTitledBorder("Highlight"));

        JLabel jlHighlight = new JLabel();
        jlHighlight.setText("Highlight : ");
        jpMain.add(jlHighlight, BorderLayout.WEST);
        jpMain.add(m_tfHighlight);
        jpMain.add(m_chkEnableHighlight, BorderLayout.EAST);

        return jpMain;
    }

    Component getCheckPanel()
    {
        m_chkVerbose    = new JCheckBox();
        m_chkDebug      = new JCheckBox();
        m_chkInfo       = new JCheckBox();
        m_chkWarn       = new JCheckBox();
        m_chkError      = new JCheckBox();
        m_chkFatal      = new JCheckBox();

        m_chkClmBookmark= new JCheckBox();
        m_chkClmLine    = new JCheckBox();
        m_chkClmDate    = new JCheckBox();
        m_chkClmTime    = new JCheckBox();
        m_chkClmLogLV   = new JCheckBox();
        m_chkClmPid     = new JCheckBox();
        m_chkClmThread  = new JCheckBox();
        m_chkClmTag     = new JCheckBox();
        m_chkClmMessage = new JCheckBox();

        JPanel jpMain = new JPanel(new BorderLayout());

        JPanel jpLogFilter = new JPanel();
        jpLogFilter.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        jpLogFilter.setBorder(BorderFactory.createTitledBorder("Log filter"));
        m_chkVerbose.setText("Verbose");
        m_chkVerbose.setSelected(true);
        m_chkDebug.setText("Debug");
        m_chkDebug.setSelected(true);
        m_chkInfo.setText("Info");
        m_chkInfo.setSelected(true);
        m_chkWarn.setText("Warn");
        m_chkWarn.setSelected(true);
        m_chkError.setText("Error");
        m_chkError.setSelected(true);
        m_chkFatal.setText("Fatal");
        m_chkFatal.setSelected(true);
        jpLogFilter.add(m_chkVerbose);
        jpLogFilter.add(m_chkDebug);
        jpLogFilter.add(m_chkInfo);
        jpLogFilter.add(m_chkWarn);
        jpLogFilter.add(m_chkError);
        jpLogFilter.add(m_chkFatal);

        jpMain.add(jpLogFilter, BorderLayout.NORTH);

        JPanel jpShowColumn = new JPanel();
        jpShowColumn.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        jpShowColumn.setBorder(BorderFactory.createTitledBorder("Show column"));
        m_chkClmBookmark.setText("Mark");
        m_chkClmBookmark.setToolTipText("Bookmark");
        m_chkClmLine.setText("Line");
        m_chkClmLine.setSelected(true);
        m_chkClmDate.setText("Date");
        m_chkClmDate.setSelected(true);
        m_chkClmTime.setText("Time");
        m_chkClmTime.setSelected(true);
        m_chkClmLogLV.setText("LogLV");
        m_chkClmLogLV.setSelected(true);
        m_chkClmPid.setText("Pid");
        m_chkClmPid.setSelected(true);
        m_chkClmThread.setText("Thread");
        m_chkClmThread.setSelected(true);
        m_chkClmTag.setText("Tag");
        m_chkClmTag.setSelected(true);
        m_chkClmMessage.setText("Msg");
        m_chkClmMessage.setSelected(true);
        jpShowColumn.add(m_chkClmBookmark);
        jpShowColumn.add(m_chkClmLine);
        jpShowColumn.add(m_chkClmDate);
        jpShowColumn.add(m_chkClmTime);
        jpShowColumn.add(m_chkClmLogLV);
        jpShowColumn.add(m_chkClmPid);
        jpShowColumn.add(m_chkClmThread);
        jpShowColumn.add(m_chkClmTag);
        jpShowColumn.add(m_chkClmMessage);

        jpMain.add(jpShowColumn, BorderLayout.CENTER);
        jpMain.add(getHighlightPanel(), BorderLayout.SOUTH);
        return jpMain;
    }

    Component getOptionFilter()
    {
        JPanel optionFilter = new JPanel(new BorderLayout());

        optionFilter.add(getCmdPanel(), BorderLayout.WEST);
        optionFilter.add(getCheckPanel(), BorderLayout.EAST);
        optionFilter.add(getFilterPanel(), BorderLayout.CENTER);

        return optionFilter;
    }

    Component getOptionMenu()
    {
        JPanel optionMenu = new JPanel(new BorderLayout());
        JPanel optionWest = new JPanel();

        JLabel jlFontType = new JLabel("Font Type : ");
        m_jcFontType = new JComboBox();
        String fonts[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        m_jcFontType.addItem("Dialog");
        for ( int i = 0; i < fonts.length; i++ )
        {
            m_jcFontType.addItem(fonts[i]);
        }
        m_jcFontType.addActionListener(m_alButtonListener);


        JLabel jlFont = new JLabel("Font Size : ");
        m_tfFontSize = new JTextField(2);
        m_tfFontSize.setHorizontalAlignment(SwingConstants.RIGHT);
        m_tfFontSize.setText("12");

        m_btnSetFont = new JButton("OK");
        m_btnSetFont.setMargin(new Insets(0, 0, 0, 0));
        m_btnSetFont.addActionListener(m_alButtonListener);

        JLabel jlEncode = new JLabel("Text Encode : ");
        m_comboEncode = new JComboBox();
        m_comboEncode.addItem("UTF-8");
        m_comboEncode.addItem("Local");

        JLabel jlGoto = new JLabel("Goto : ");
        final JTextField tfGoto = new JTextField(6);
        tfGoto.setHorizontalAlignment(SwingConstants.RIGHT);
        tfGoto.addCaretListener(new CaretListener(){
            public void caretUpdate(CaretEvent e)
            {
                try
                {
                    int nIndex = Integer.parseInt(tfGoto.getText()) - 1;
                    m_tbLogTable.showRow(nIndex, false);
                }
                catch(Exception err)
                {
                }
            }
        });

        JLabel jlProcessCmd = new JLabel("Cmd : ");
        m_comboCmd = new JComboBox();
        m_comboCmd.setPreferredSize( new Dimension( 180, 25) );
//        m_comboCmd.setMaximumSize( m_comboCmd.getPreferredSize()  );
//        m_comboCmd.setSize( 20000, m_comboCmd.getHeight() );
//        m_comboCmd.addItem(ANDROID_THREAD_CMD);
//        m_comboCmd.addItem(ANDROID_DEFAULT_CMD);
//        m_comboCmd.addItem(ANDROID_RADIO_CMD);
//        m_comboCmd.addItem(ANDROID_EVENT_CMD);
//        m_comboCmd.addItem(ANDROID_CUSTOM_CMD);
//        m_comboCmd.addItemListener(new ItemListener()
//        {
//            public void itemStateChanged(ItemEvent e)
//            {
//                if(e.getStateChange() != ItemEvent.SELECTED) return;
//
//                if (e.getItem().equals(ANDROID_CUSTOM_CMD)) {
//                    m_comboCmd.setEditable(true);
//                } else {
//                    m_comboCmd.setEditable(false);
//                }
////                setProcessCmd(m_comboDeviceCmd.getSelectedIndex(), m_strSelectedDevice);
//            }
//        });

        m_btnClear = new JButton("Clear");
        m_btnClear.setMargin(new Insets(0, 0, 0, 0));
        m_btnClear.setEnabled(false);
        m_btnRun = new JButton("Run");
        m_btnRun.setMargin(new Insets(0, 0, 0, 0));

        m_tbtnPause = new JToggleButton("Pause");
        m_tbtnPause.setMargin(new Insets(0, 0, 0, 0));
        m_tbtnPause.setEnabled(false);
        m_btnStop = new JButton("Stop");
        m_btnStop.setMargin(new Insets(0, 0, 0, 0));
        m_btnStop.setEnabled(false);
        m_btnRun.addActionListener(m_alButtonListener);
        m_btnStop.addActionListener(m_alButtonListener);
        m_btnClear.addActionListener(m_alButtonListener);
        m_tbtnPause.addActionListener(m_alButtonListener);

        optionWest.add(jlFontType);
        optionWest.add(m_jcFontType);
        optionWest.add(jlFont);
        optionWest.add(m_tfFontSize);
        optionWest.add(m_btnSetFont);
        optionWest.add(jlEncode);
        optionWest.add(m_comboEncode);
        optionWest.add(jlGoto);
        optionWest.add(tfGoto);
        optionWest.add(jlProcessCmd);
        optionWest.add(m_comboCmd);
        optionWest.add(m_btnClear);
        optionWest.add(m_btnRun);
        optionWest.add(m_tbtnPause);
        optionWest.add(m_btnStop);

        optionMenu.add(optionWest, BorderLayout.WEST);
        return optionMenu;
    }

    Component getOptionPanel()
    {
        JPanel optionMain = new JPanel(new BorderLayout());

        optionMain.add(getOptionFilter(), BorderLayout.CENTER);
        optionMain.add(getOptionMenu(), BorderLayout.SOUTH);

        return optionMain;
    }

    Component getStatusPanel()
    {
        m_tfStatus = new JTextField("ready");
        m_tfStatus.setEditable(false);
        return m_tfStatus;
    }

    Component getTabPanel()
    {
        m_tpTab = new JTabbedPane();
        m_tmLogTableModel = new LogFilterTableModel();
        m_tmLogTableModel.setData(m_arLogInfoAll);
        m_tbLogTable = new LogTable(m_tmLogTableModel, this);
        m_iLogParser = new LogCatParser();
        m_tbLogTable.setLogParser(m_iLogParser);

        m_scrollVBar = new JScrollPane(m_tbLogTable);

        m_tpTab.addTab("Log", m_scrollVBar);

        return m_scrollVBar;
    }

    void initValue()
    {
        m_bPauseADB         = false;
        FILE_LOCK           = new Object();
        FILTER_LOCK         = new Object();
        m_nChangedFilter    = STATUS_READY;
        m_nFilterLogLV      = LogInfo.LOG_LV_ALL;

        m_arTagInfo         = new ArrayList<TagInfo>();
        m_arLogInfoAll      = new ArrayList<LogInfo>();
        m_arLogInfoFiltered = new ArrayList<LogInfo>();
        m_hmBookmarkAll     = new HashMap<Integer, Integer>();
        m_hmBookmarkFiltered= new HashMap<Integer, Integer>();
        m_hmErrorAll        = new HashMap<Integer, Integer>();
        m_hmErrorFiltered   = new HashMap<Integer, Integer>();

        m_strLogFileName = makeFilename();
//        m_strProcessCmd     = ANDROID_DEFAULT_CMD + m_strLogFileName;
    }

    void parseFile(final File file)
    {
        if(file == null)
        {
            T.e("file == null");
            return;
        }

        setTitle(file.getPath());
        new Thread(new Runnable()
        {
            public void run()
            {
                FileInputStream fstream = null;
                DataInputStream in = null;
                BufferedReader br = null;
                int nIndex = 1;

                try {
                    fstream = new FileInputStream(file);
                    in = new DataInputStream(fstream);
                    if(m_comboEncode.getSelectedItem().equals("UTF-8"))
                        br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                    else
                        br = new BufferedReader(new InputStreamReader(in));

                    String strLine;

                    setStatus("Parsing");
                    clearData();
                    m_tbLogTable.clearSelection();
                    while ((strLine = br.readLine()) != null)
                    {
                        if(strLine != null && !"".equals(strLine.trim()))
                        {
                            LogInfo logInfo = m_iLogParser.parseLog(strLine);
                            logInfo.m_strLine = "" + nIndex++;
                            addLogInfo(logInfo);
                        }
                    }
                    runFilter();
                    setStatus("Parse complete");
                } catch(Exception ioe) {
                    T.e(ioe);
                }
                try
                {
                    if(br != null)br.close();
                    if(in != null) in.close();
                    if(fstream != null) fstream.close();
                }
                catch(Exception e)
                {
                    T.e(e);
                }
            }
        }).start();
    }

    void pauseProcess()
    {
        if(m_tbtnPause.isSelected())
        {
            m_bPauseADB = true;
            m_tbtnPause.setText("Resume");
        }
        else
        {
            m_bPauseADB = false;
            m_tbtnPause.setText("Pause");
        }
    }

    void setBookmark(int nLine, String strBookmark)
    {
        LogInfo logInfo = m_arLogInfoAll.get(nLine);
        logInfo.m_strBookmark = strBookmark;
        m_arLogInfoAll.set(nLine, logInfo);
    }

    void setDeviceList()
    {
        m_strSelectedDevice = "";

        DefaultListModel listModel = (DefaultListModel)m_lDeviceList.getModel();
        try
        {
            listModel.clear();
            String s;
            String strCommand = DEVICES_CMD[m_comboDeviceCmd.getSelectedIndex()];
            if(m_comboDeviceCmd.getSelectedIndex() == DEVICES_CUSTOM)
                strCommand = (String)m_comboDeviceCmd.getSelectedItem();
            Process oProcess = Runtime.getRuntime().exec(strCommand);

            // 외부 프로그램 출력 읽기
            BufferedReader stdOut   = new BufferedReader(new InputStreamReader(oProcess.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(oProcess.getErrorStream()));

            // "표준 출력"과 "표준 에러 출력"을 출력
            while ((s =   stdOut.readLine()) != null)
            {
                if(!s.equals("List of devices attached "))
                {
                    s = s.replace("\t", " ");
                    s = s.replace("device", "");
                    listModel.addElement(s);
                }
            }
            while ((s = stdError.readLine()) != null)
            {
                listModel.addElement(s);
            }

            // 외부 프로그램 반환값 출력 (이 부분은 필수가 아님)
            System.out.println("Exit Code: " + oProcess.exitValue());
        }
        catch(Exception e)
        {
            T.e("e = " + e);
            listModel.addElement(e);
        }
    }

    public void setFindFocus()
    {
        m_tfFindWord.requestFocus();
    }

    void setDnDListener()
    {

        new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, new DropTargetListener()
        {
            public void dropActionChanged(DropTargetDragEvent dtde) {}
            public void dragOver(DropTargetDragEvent dtde)          {}
            public void dragExit(DropTargetEvent dte)               {}
            public void dragEnter(DropTargetDragEvent event)        {}

            public void drop(DropTargetDropEvent event)
            {
                try
                {
                    event.acceptDrop(DnDConstants.ACTION_COPY);
                    Transferable t = event.getTransferable();
                    List<?> list = (List<?>)(t.getTransferData(DataFlavor.javaFileListFlavor));
                    Iterator<?> i = list.iterator();
                    if(i.hasNext())
                    {
                        File file = (File)i.next();
                        setTitle(file.getPath());

                        stopProcess();
                        parseFile(file);
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    void setLogLV(int nLogLV, boolean bChecked)
    {
        if(bChecked)
            m_nFilterLogLV |= nLogLV;
        else
            m_nFilterLogLV &= ~nLogLV;
        m_nChangedFilter = STATUS_CHANGE;
        runFilter();
    }

    void useFilter(JCheckBox checkBox)
    {
        if(checkBox.equals(m_chkEnableFind))
            m_tbLogTable.setFilterFind(checkBox.isSelected() ? m_tfFindWord.getText() : "");
        if(checkBox.equals(m_chkEnableRemove))
            m_tbLogTable.SetFilterRemove(checkBox.isSelected() ? m_tfRemoveWord.getText() : "");
        if(checkBox.equals(m_chkEnableShowPid))
            m_tbLogTable.SetFilterShowPid(checkBox.isSelected() ? m_tfShowPid.getText() : "");
        if(checkBox.equals(m_chkEnableShowTid))
            m_tbLogTable.SetFilterShowTid(checkBox.isSelected() ? m_tfShowTid.getText() : "");
        if(checkBox.equals(m_chkEnableShowTag))
            m_tbLogTable.SetFilterShowTag(checkBox.isSelected() ? m_tfShowTag.getText() : "");
        if(checkBox.equals(m_chkEnableRemoveTag))
            m_tbLogTable.SetFilterRemoveTag(checkBox.isSelected() ? m_tfRemoveTag.getText() : "");
        if(checkBox.equals(m_chkEnableHighlight))
            m_tbLogTable.SetHighlight(checkBox.isSelected() ? m_tfHighlight.getText() : "");
        m_nChangedFilter = STATUS_CHANGE;
        runFilter();
    }

    void setProcessBtn(boolean bStart)
    {
        if(bStart)
        {
            m_btnRun.setEnabled(false);
            m_btnStop.setEnabled(true);
            m_btnClear.setEnabled(true);
            m_tbtnPause.setEnabled(true);
        }
        else
        {
            m_btnRun.setEnabled(true);
            m_btnStop.setEnabled(false);
            m_btnClear.setEnabled(false);
            m_tbtnPause.setEnabled(false);
            m_tbtnPause.setSelected(false);
            m_tbtnPause.setText("Pause");
        }
    }

    String getProcessCmd()
    {
        if(m_lDeviceList.getSelectedIndex() < 0)
            return ANDROID_DEFAULT_CMD_FIRST + m_comboCmd.getSelectedItem();
//            return ANDROID_DEFAULT_CMD_FIRST + m_comboCmd.getSelectedItem() + makeFilename();
        else
            return ANDROID_SELECTED_CMD_FIRST + m_strSelectedDevice + m_comboCmd.getSelectedItem();
    }

    void setProcessCmd(int nType, String strSelectedDevice)
    {
//        m_comboCmd.removeAllItems();

        m_strLogFileName = makeFilename();
//        if(strSelectedDevice != null)
//        {
//            strSelectedDevice = strSelectedDevice.replace("\t", " ").replace("device", "").replace("offline", "");
//            T.d("strSelectedDevice = " + strSelectedDevice);
//        }

        if(nType == DEVICES_ANDROID)
        {
            if(strSelectedDevice != null && strSelectedDevice.length() > 0)
            {
//                m_comboCmd.addItem(ANDROID_SELECTED_CMD_FIRST + strSelectedDevice + ANDROID_SELECTED_CMD_LAST);
//                m_strProcessCmd = ANDROID_SELECTED_CMD_FIRST + strSelectedDevice + ANDROID_SELECTED_CMD_LAST;
            }
            else
            {
//                m_comboCmd.addItem(ANDROID_DEFAULT_CMD);
//                m_strProcessCmd = ANDROID_DEFAULT_CMD;
            }
        }
        else if(nType == DEVICES_IOS)
        {
            if(strSelectedDevice != null && strSelectedDevice.length() > 0)
            {
//                m_comboCmd.addItem(ANDROID_SELECTED_CMD_FIRST + strSelectedDevice + ANDROID_SELECTED_CMD_LAST);
//                m_strProcessCmd = IOS_SELECTED_CMD_FIRST + strSelectedDevice + IOS_SELECTED_CMD_LAST;
            }
          else
          {
//              m_comboCmd.addItem(IOS_DEFAULT_CMD);
//              m_strProcessCmd = IOS_DEFAULT_CMD;
          }
        }
        else
        {
//            m_comboCmd.addItem(ANDROID_DEFAULT_CMD);
        }
    }

    void setStatus(String strText)
    {
        m_tfStatus.setText(strText);
    }

    public void setTitle(String strTitle)
    {
        super.setTitle(strTitle);
    }

    void stopProcess()
    {
        setProcessBtn(false);
        if(m_Process != null) m_Process.destroy();
        if(m_thProcess != null) m_thProcess.interrupt();
        if(m_thWatchFile != null) m_thWatchFile.interrupt();
        m_Process = null;
        m_thProcess = null;
        m_thWatchFile = null;
        m_bPauseADB = false;
    }

    void startFileParse()
    {
        m_thWatchFile = new Thread(new Runnable()
        {
            public void run()
            {
                FileInputStream fstream = null;
                DataInputStream in = null;
                BufferedReader br = null;

                try {
                    fstream = new FileInputStream(m_strLogFileName);
                    in = new DataInputStream(fstream);
                    if(m_comboEncode.getSelectedItem().equals("UTF-8"))
                        br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                    else
                        br = new BufferedReader(new InputStreamReader(in));

                    String strLine;

                    setTitle(m_strLogFileName);

                    m_arLogInfoAll.clear();
                    m_arTagInfo.clear();

                    boolean bEndLine;
                    int nSelectedIndex;
                    int nAddCount;
                    int nPreRowCount = 0;
                    int nEndLine;

                    while(true)
                    {
                        Thread.sleep(50);

                        if(m_nChangedFilter == STATUS_CHANGE || m_nChangedFilter == STATUS_PARSING)
                            continue;
                        if(m_bPauseADB) continue;

                        bEndLine = false;
                        nSelectedIndex = m_tbLogTable.getSelectedRow();
                        nPreRowCount = m_tbLogTable.getRowCount();
                        nAddCount = 0;

                        if(nSelectedIndex == -1 || nSelectedIndex == m_tbLogTable.getRowCount() - 1)
                            bEndLine = true;

                        synchronized(FILE_LOCK)
                        {
                            int nLine = m_arLogInfoAll.size() + 1;
                            while (!m_bPauseADB && (strLine = br.readLine()) != null)
                            {
                                if(strLine != null && !"".equals(strLine.trim()))
                                {
                                    LogInfo logInfo = m_iLogParser.parseLog(strLine);
                                    logInfo.m_strLine = "" + nLine++;
                                    addLogInfo(logInfo);
                                    nAddCount++;
                                }
                            }
                        }
                        if(nAddCount == 0) continue;

                        synchronized(FILTER_LOCK)
                        {
                            if(m_bUserFilter == false)
                            {
                                m_tmLogTableModel.setData(m_arLogInfoAll);
                                m_ipIndicator.setData(m_arLogInfoAll, m_hmBookmarkAll, m_hmErrorAll);
                            }
                            else
                            {
                                m_tmLogTableModel.setData(m_arLogInfoFiltered);
                                m_ipIndicator.setData(m_arLogInfoFiltered, m_hmBookmarkFiltered, m_hmErrorFiltered);
                            }

                            nEndLine = m_tmLogTableModel.getRowCount();
                            if(nPreRowCount != nEndLine)
                            {
                                if(bEndLine)
                                    updateTable(nEndLine - 1, true);
                                else
                                    updateTable(nSelectedIndex, false);
                            }
                        }
                    }
                } catch(Exception e) {
                    T.e(e);
                    e.printStackTrace();
                }
                try
                {
                    if(br != null)br.close();
                    if(in != null) in.close();
                    if(fstream != null) fstream.close();
                }
                catch(Exception e)
                {
                    T.e(e);
                }
                System.out.println("End m_thWatchFile thread");
//                setTitle(LOGFILTER + " " + VERSION);
            }
        });
        m_thWatchFile.start();
    }

    void runFilter()
    {
        checkUseFilter();
        while(m_nChangedFilter == STATUS_PARSING)
            try
            {
                Thread.sleep(100);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        synchronized(FILTER_LOCK)
        {
            FILTER_LOCK.notify();
        }
    }

    void startFilterParse()
    {
        m_thFilterParse = new Thread(new Runnable()
        {
            public void run()
            {
                try {
                    while(true)
                    {
                        synchronized(FILTER_LOCK)
                        {
                            m_nChangedFilter = STATUS_READY;
                            FILTER_LOCK.wait();

                            m_nChangedFilter = STATUS_PARSING;

                            m_arLogInfoFiltered.clear();
                            m_hmBookmarkFiltered.clear();
                            m_hmErrorFiltered.clear();
                            m_tbLogTable.clearSelection();

                            if(m_bUserFilter == false)
                            {
                                m_tmLogTableModel.setData(m_arLogInfoAll);
                                m_ipIndicator.setData(m_arLogInfoAll, m_hmBookmarkAll, m_hmErrorAll);
                                updateTable(m_arLogInfoAll.size() - 1, true);
                                m_nChangedFilter = STATUS_READY;
                                continue;
                            }
                            m_tmLogTableModel.setData(m_arLogInfoFiltered);
                            m_ipIndicator.setData(m_arLogInfoFiltered, m_hmBookmarkFiltered, m_hmErrorFiltered);
    //                        updateTable(-1);
                            setStatus("Parsing");

                            int nRowCount = m_arLogInfoAll.size();
                            LogInfo logInfo;
                            boolean bAddFilteredArray;

                            for(int nIndex = 0; nIndex < nRowCount; nIndex++)
                            {
                                if(nIndex % 10000 == 0)
                                    Thread.sleep(1);
                                if(m_nChangedFilter == STATUS_CHANGE)
                                {
//                                    T.d("m_nChangedFilter == STATUS_CHANGE");
                                    break;
                                }
                                logInfo = m_arLogInfoAll.get(nIndex);

                                if(m_ipIndicator.m_chBookmark.isSelected() || m_ipIndicator.m_chError.isSelected())
                                {
                                    bAddFilteredArray = false;
                                    if(logInfo.m_bMarked && m_ipIndicator.m_chBookmark.isSelected())
                                    {
                                        bAddFilteredArray = true;
                                        m_hmBookmarkFiltered.put(Integer.parseInt(logInfo.m_strLine) - 1, m_arLogInfoFiltered.size());
                                        if(logInfo.m_strLogLV.equals("E") || logInfo.m_strLogLV.equals("ERROR"))
                                            m_hmErrorFiltered.put(Integer.parseInt(logInfo.m_strLine) - 1, m_arLogInfoFiltered.size());
                                    }
                                    if((logInfo.m_strLogLV.equals("E") || logInfo.m_strLogLV.equals("ERROR")) && m_ipIndicator.m_chError.isSelected())
                                    {
                                        bAddFilteredArray = true;
                                        m_hmErrorFiltered.put(Integer.parseInt(logInfo.m_strLine) - 1, m_arLogInfoFiltered.size());
                                        if(logInfo.m_bMarked)
                                            m_hmBookmarkFiltered.put(Integer.parseInt(logInfo.m_strLine) - 1, m_arLogInfoFiltered.size());
                                    }

                                    if(bAddFilteredArray) m_arLogInfoFiltered.add(logInfo);
                                }
                                else if(checkLogLVFilter(logInfo)
                                    && checkPidFilter(logInfo)
                                    && checkTidFilter(logInfo)
                                    && checkShowTagFilter(logInfo)
                                    && checkRemoveTagFilter(logInfo)
                                    && checkFindFilter(logInfo)
                                    && checkRemoveFilter(logInfo))
                                {
                                    m_arLogInfoFiltered.add(logInfo);
                                    if(logInfo.m_bMarked)
                                        m_hmBookmarkFiltered.put(Integer.parseInt(logInfo.m_strLine) - 1, m_arLogInfoFiltered.size());
                                    if(logInfo.m_strLogLV.equals("E") || logInfo.m_strLogLV.equals("ERROR"))
                                        m_hmErrorFiltered.put(Integer.parseInt(logInfo.m_strLine) - 1, m_arLogInfoFiltered.size());
                                }
                            }
                            if(m_nChangedFilter == STATUS_PARSING)
                            {
                                m_nChangedFilter = STATUS_READY;
                                m_tmLogTableModel.setData(m_arLogInfoFiltered);
                                m_ipIndicator.setData(m_arLogInfoFiltered, m_hmBookmarkFiltered, m_hmErrorFiltered);
                                updateTable(m_arLogInfoFiltered.size() - 1, true);
                                setStatus("Complete");
                            }
                        }
                    }
                } catch(Exception e) {
                    T.e(e);
                    e.printStackTrace();
                }
                System.out.println("End m_thFilterParse thread");
            }
        });
        m_thFilterParse.start();
    }

    void startProcess()
    {
        clearData();
        m_tbLogTable.clearSelection();
        m_thProcess = new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    String s;
                    m_Process = null;
                    setProcessCmd(m_comboDeviceCmd.getSelectedIndex(), m_strSelectedDevice);

                    T.d("getProcessCmd() = " + getProcessCmd());
                    m_Process = Runtime.getRuntime().exec(getProcessCmd());
                    BufferedReader stdOut   = new BufferedReader(new InputStreamReader(m_Process.getInputStream(), "UTF-8"));

//                    BufferedWriter fileOut = new BufferedWriter(new FileWriter(m_strLogFileName));
                    Writer fileOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(m_strLogFileName), "UTF-8"));

                    startFileParse();

                    while ((s =   stdOut.readLine()) != null)
                    {
                        if(s != null && !"".equals(s.trim()))
                        {
                            synchronized(FILE_LOCK)
                            {
                                fileOut.write(s);
                                fileOut.write("\r\n");
//                                fileOut.newLine();
                                fileOut.flush();
                            }
                        }
                    }
                    fileOut.close();
//                    T.d("Exit Code: " + m_Process.exitValue());
                }
                catch(Exception e)
                {
                    T.e("e = " + e);
                }
                stopProcess();
            }
        });
        m_thProcess.start();
        setProcessBtn(true);
    }

    boolean checkLogLVFilter(LogInfo logInfo)
    {
        if(m_nFilterLogLV == LogInfo.LOG_LV_ALL)
            return true;
        if((m_nFilterLogLV & LogInfo.LOG_LV_VERBOSE) != 0 && (logInfo.m_strLogLV.equals("V") || logInfo.m_strLogLV.equals("VERBOSE")))
            return true;
        if((m_nFilterLogLV & LogInfo.LOG_LV_DEBUG) != 0 && (logInfo.m_strLogLV.equals("D") || logInfo.m_strLogLV.equals("DEBUG")))
            return true;
        if((m_nFilterLogLV & LogInfo.LOG_LV_INFO) != 0 && (logInfo.m_strLogLV.equals("I") || logInfo.m_strLogLV.equals("INFO")))
            return true;
        if((m_nFilterLogLV & LogInfo.LOG_LV_WARN) != 0 && (logInfo.m_strLogLV.equals("W") || logInfo.m_strLogLV.equals("WARN")))
            return true;
        if((m_nFilterLogLV & LogInfo.LOG_LV_ERROR) != 0 && (logInfo.m_strLogLV.equals("E") || logInfo.m_strLogLV.equals("ERROR")))
            return true;
        if((m_nFilterLogLV & LogInfo.LOG_LV_FATAL) != 0 && (logInfo.m_strLogLV.equals("F") || logInfo.m_strLogLV.equals("FATAL")))
            return true;

        return false;
    }

    boolean checkPidFilter(LogInfo logInfo)
    {
        if(m_tbLogTable.GetFilterShowPid().length() <= 0) return true;

        StringTokenizer stk = new StringTokenizer(m_tbLogTable.GetFilterShowPid(), "|", false);

        while(stk.hasMoreElements())
        {
            if(logInfo.m_strPid.toLowerCase().contains(stk.nextToken().toLowerCase()))
                return true;
        }

        return false;
    }

    boolean checkTidFilter(LogInfo logInfo)
    {
        if(m_tbLogTable.GetFilterShowTid().length() <= 0) return true;

        StringTokenizer stk = new StringTokenizer(m_tbLogTable.GetFilterShowTid(), "|", false);

        while(stk.hasMoreElements())
        {
            if(logInfo.m_strThread.toLowerCase().contains(stk.nextToken().toLowerCase()))
                return true;
        }

        return false;
    }

    boolean checkFindFilter(LogInfo logInfo)
    {
        if(m_tbLogTable.GetFilterFind().length() <= 0) return true;

        StringTokenizer stk = new StringTokenizer(m_tbLogTable.GetFilterFind(), "|", false);

        while(stk.hasMoreElements())
        {
            if(logInfo.m_strMessage.toLowerCase().contains(stk.nextToken().toLowerCase()))
                return true;
        }

        return false;
    }

    boolean checkRemoveFilter(LogInfo logInfo)
    {
        if(m_tbLogTable.GetFilterRemove().length() <= 0) return true;

        StringTokenizer stk = new StringTokenizer(m_tbLogTable.GetFilterRemove(), "|", false);

        while(stk.hasMoreElements())
        {
            if(logInfo.m_strMessage.toLowerCase().contains(stk.nextToken().toLowerCase()))
                return false;
        }

        return true;
    }

    boolean checkShowTagFilter(LogInfo logInfo)
    {
        if(m_tbLogTable.GetFilterShowTag().length() <= 0) return true;

        StringTokenizer stk = new StringTokenizer(m_tbLogTable.GetFilterShowTag(), "|", false);

        while(stk.hasMoreElements())
        {
            if(logInfo.m_strTag.toLowerCase().contains(stk.nextToken().toLowerCase()))
                return true;
        }

        return false;
    }

    boolean checkRemoveTagFilter(LogInfo logInfo)
    {
        if(m_tbLogTable.GetFilterRemoveTag().length() <= 0) return true;

        StringTokenizer stk = new StringTokenizer(m_tbLogTable.GetFilterRemoveTag(), "|", false);

        while(stk.hasMoreElements())
        {
            if(logInfo.m_strTag.toLowerCase().contains(stk.nextToken().toLowerCase()))
                return false;
        }

        return true;
    }

    boolean checkUseFilter()
    {
        if(!m_ipIndicator.m_chBookmark.isSelected()
            && !m_ipIndicator.m_chError.isSelected()
            && checkLogLVFilter(new LogInfo())
            && (m_tbLogTable.GetFilterShowPid().length() == 0   || !m_chkEnableShowPid.isSelected())
            && (m_tbLogTable.GetFilterShowTid().length() == 0   || !m_chkEnableShowTid.isSelected())
            && (m_tbLogTable.GetFilterShowTag().length() == 0   || !m_chkEnableShowTag.isSelected())
            && (m_tbLogTable.GetFilterRemoveTag().length() == 0 || !m_chkEnableRemoveTag.isSelected())
            && (m_tbLogTable.GetFilterFind().length() == 0      || !m_chkEnableFind.isSelected())
            && (m_tbLogTable.GetFilterRemove().length() == 0    || !m_chkEnableRemove.isSelected()))
        {
            m_bUserFilter = false;
        }
        else m_bUserFilter = true;
        return m_bUserFilter;
    }

    ActionListener m_alButtonListener = new ActionListener()
    {
        public void actionPerformed(ActionEvent e)
        {
            if(e.getSource().equals(m_btnDevice))
                setDeviceList();
            else if(e.getSource().equals(m_btnSetFont))
            {
                m_tbLogTable.setFontSize(Integer.parseInt(m_tfFontSize.getText()));
                updateTable(-1, false);
            }
            else if(e.getSource().equals(m_btnRun))
            {
                startProcess();
            }
            else if(e.getSource().equals(m_btnStop))
            {
                stopProcess();
            }
            else if(e.getSource().equals(m_btnClear))
            {
                boolean bBackup = m_bPauseADB;
                m_bPauseADB = true;
                clearData();
                updateTable(-1, false);
                m_bPauseADB = bBackup;
            }
            else if(e.getSource().equals(m_tbtnPause))
                pauseProcess();
            else if(e.getSource().equals(m_jcFontType))
            {
                T.d("font = " + m_tbLogTable.getFont());
                
                m_tbLogTable.setFont(new Font((String)m_jcFontType.getSelectedItem(), Font.PLAIN, 12));
                m_tbLogTable.setFontSize(Integer.parseInt(m_tfFontSize.getText()));
            }
        }
    };

    public void notiEvent(EventParam param)
    {
        switch(param.nEventId)
        {
            case EVENT_CLICK_BOOKMARK:
            case EVENT_CLICK_ERROR:
                m_nChangedFilter = STATUS_CHANGE;
                runFilter();
                break;
            case EVENT_CHANGE_FILTER_SHOW_TAG:
                m_tfShowTag.setText(m_tbLogTable.GetFilterShowTag());
                break;
            case EVENT_CHANGE_FILTER_REMOVE_TAG:
                m_tfRemoveTag.setText(m_tbLogTable.GetFilterRemoveTag());
                break;
        }
    }

    void updateTable(int nRow, boolean bMove)
    {
        m_tmLogTableModel.fireTableRowsUpdated(0, m_tmLogTableModel.getRowCount() - 1);
        m_scrollVBar.validate();
//        if(nRow >= 0)
//            m_tbLogTable.changeSelection(nRow, 0, false, false);
        m_tbLogTable.invalidate();
        m_tbLogTable.repaint();
        if(nRow >= 0)
            m_tbLogTable.changeSelection(nRow, 0, false, false, bMove);
    }

    DocumentListener m_dlFilterListener = new DocumentListener()
    {
        public void changedUpdate(DocumentEvent arg0)
        {
            try
            {
                if(arg0.getDocument().equals(m_tfFindWord.getDocument()) && m_chkEnableFind.isSelected())
                    m_tbLogTable.setFilterFind(arg0.getDocument().getText(0, arg0.getDocument().getLength()));
                else if(arg0.getDocument().equals(m_tfRemoveWord.getDocument()) && m_chkEnableRemove.isSelected())
                    m_tbLogTable.SetFilterRemove(arg0.getDocument().getText(0, arg0.getDocument().getLength()));
                else if(arg0.getDocument().equals(m_tfShowPid.getDocument()) && m_chkEnableShowPid.isSelected())
                    m_tbLogTable.SetFilterShowPid(arg0.getDocument().getText(0, arg0.getDocument().getLength()));
                else if(arg0.getDocument().equals(m_tfShowTid.getDocument()) && m_chkEnableShowTid.isSelected())
                    m_tbLogTable.SetFilterShowTid(arg0.getDocument().getText(0, arg0.getDocument().getLength()));
                else if(arg0.getDocument().equals(m_tfShowTag.getDocument()) && m_chkEnableShowTag.isSelected())
                    m_tbLogTable.SetFilterShowTag(arg0.getDocument().getText(0, arg0.getDocument().getLength()));
                else if(arg0.getDocument().equals(m_tfRemoveTag.getDocument()) && m_chkEnableRemoveTag.isSelected())
                    m_tbLogTable.SetFilterRemoveTag(arg0.getDocument().getText(0, arg0.getDocument().getLength()));
                else if(arg0.getDocument().equals(m_tfHighlight.getDocument()) && m_chkEnableHighlight.isSelected())
                    m_tbLogTable.SetHighlight(arg0.getDocument().getText(0, arg0.getDocument().getLength()));
                m_nChangedFilter = STATUS_CHANGE;
                runFilter();
            }
            catch(Exception e)
            {
                T.e(e);
            }
        }

        public void insertUpdate(DocumentEvent arg0)
        {
            try
            {
                if(arg0.getDocument().equals(m_tfFindWord.getDocument()) && m_chkEnableFind.isSelected())
                    m_tbLogTable.setFilterFind(arg0.getDocument().getText(0, arg0.getDocument().getLength()));
                else if(arg0.getDocument().equals(m_tfRemoveWord.getDocument()) && m_chkEnableRemove.isSelected())
                    m_tbLogTable.SetFilterRemove(arg0.getDocument().getText(0, arg0.getDocument().getLength()));
                else if(arg0.getDocument().equals(m_tfShowPid.getDocument()) && m_chkEnableShowPid.isSelected())
                    m_tbLogTable.SetFilterShowPid(arg0.getDocument().getText(0, arg0.getDocument().getLength()));
                else if(arg0.getDocument().equals(m_tfShowTid.getDocument()) && m_chkEnableShowTid.isSelected())
                    m_tbLogTable.SetFilterShowTid(arg0.getDocument().getText(0, arg0.getDocument().getLength()));
                else if(arg0.getDocument().equals(m_tfShowTag.getDocument()) && m_chkEnableShowTag.isSelected())
                    m_tbLogTable.SetFilterShowTag(arg0.getDocument().getText(0, arg0.getDocument().getLength()));
                else if(arg0.getDocument().equals(m_tfRemoveTag.getDocument()) && m_chkEnableRemoveTag.isSelected())
                    m_tbLogTable.SetFilterRemoveTag(arg0.getDocument().getText(0, arg0.getDocument().getLength()));
                else if(arg0.getDocument().equals(m_tfHighlight.getDocument()) && m_chkEnableHighlight.isSelected())
                    m_tbLogTable.SetHighlight(arg0.getDocument().getText(0, arg0.getDocument().getLength()));
                m_nChangedFilter = STATUS_CHANGE;
                runFilter();
            }
            catch(Exception e)
            {
                T.e(e);
            }
        }

        public void removeUpdate(DocumentEvent arg0)
        {
            try
            {
                if(arg0.getDocument().equals(m_tfFindWord.getDocument()) && m_chkEnableFind.isSelected())
                    m_tbLogTable.setFilterFind(arg0.getDocument().getText(0, arg0.getDocument().getLength()));
                else if(arg0.getDocument().equals(m_tfRemoveWord.getDocument()) && m_chkEnableRemove.isSelected())
                    m_tbLogTable.SetFilterRemove(arg0.getDocument().getText(0, arg0.getDocument().getLength()));
                else if(arg0.getDocument().equals(m_tfShowPid.getDocument()) && m_chkEnableShowPid.isSelected())
                    m_tbLogTable.SetFilterShowPid(arg0.getDocument().getText(0, arg0.getDocument().getLength()));
                else if(arg0.getDocument().equals(m_tfShowTid.getDocument()) && m_chkEnableShowTid.isSelected())
                    m_tbLogTable.SetFilterShowTid(arg0.getDocument().getText(0, arg0.getDocument().getLength()));
                else if(arg0.getDocument().equals(m_tfShowTag.getDocument()) && m_chkEnableShowTag.isSelected())
                    m_tbLogTable.SetFilterShowTag(arg0.getDocument().getText(0, arg0.getDocument().getLength()));
                else if(arg0.getDocument().equals(m_tfRemoveTag.getDocument()) && m_chkEnableRemoveTag.isSelected())
                    m_tbLogTable.SetFilterRemoveTag(arg0.getDocument().getText(0, arg0.getDocument().getLength()));
                else if(arg0.getDocument().equals(m_tfHighlight.getDocument()) && m_chkEnableHighlight.isSelected())
                    m_tbLogTable.SetHighlight(arg0.getDocument().getText(0, arg0.getDocument().getLength()));
                m_nChangedFilter = STATUS_CHANGE;
                runFilter();
            }
            catch(Exception e)
            {
                T.e(e);
            }
        }
    };

    ItemListener m_itemListener = new ItemListener() {
        public void itemStateChanged(ItemEvent itemEvent) {
            JCheckBox check = (JCheckBox)itemEvent.getSource();

            if(check.equals(m_chkVerbose))
                setLogLV(LogInfo.LOG_LV_VERBOSE, check.isSelected());
            else if(check.equals(m_chkDebug))
                setLogLV(LogInfo.LOG_LV_DEBUG, check.isSelected());
            else if(check.equals(m_chkInfo))
                setLogLV(LogInfo.LOG_LV_INFO, check.isSelected());
            else if(check.equals(m_chkWarn))
                setLogLV(LogInfo.LOG_LV_WARN, check.isSelected());
            else if(check.equals(m_chkError))
                setLogLV(LogInfo.LOG_LV_ERROR, check.isSelected());
            else if(check.equals(m_chkFatal))
                setLogLV(LogInfo.LOG_LV_FATAL, check.isSelected());
            else if(check.equals(m_chkClmBookmark))
                m_tbLogTable.showColumn(LogFilterTableModel.COMUMN_BOOKMARK, check.isSelected());
            else if(check.equals(m_chkClmLine))
                m_tbLogTable.showColumn(LogFilterTableModel.COMUMN_LINE, check.isSelected());
            else if(check.equals(m_chkClmDate))
                m_tbLogTable.showColumn(LogFilterTableModel.COMUMN_DATE, check.isSelected());
            else if(check.equals(m_chkClmTime))
                m_tbLogTable.showColumn(LogFilterTableModel.COMUMN_TIME, check.isSelected());
            else if(check.equals(m_chkClmLogLV))
                m_tbLogTable.showColumn(LogFilterTableModel.COMUMN_LOGLV, check.isSelected());
            else if(check.equals(m_chkClmPid))
                m_tbLogTable.showColumn(LogFilterTableModel.COMUMN_PID, check.isSelected());
            else if(check.equals(m_chkClmThread))
                m_tbLogTable.showColumn(LogFilterTableModel.COMUMN_THREAD, check.isSelected());
            else if(check.equals(m_chkClmTag))
                m_tbLogTable.showColumn(LogFilterTableModel.COMUMN_TAG, check.isSelected());
            else if(check.equals(m_chkClmMessage))
                m_tbLogTable.showColumn(LogFilterTableModel.COMUMN_MESSAGE, check.isSelected());
            else if(check.equals(m_chkEnableFind)
                    || check.equals(m_chkEnableRemove)
                    || check.equals(m_chkEnableShowPid)
                    || check.equals(m_chkEnableShowTid)
                    || check.equals(m_chkEnableShowTag)
                    || check.equals(m_chkEnableRemoveTag)
                    || check.equals(m_chkEnableHighlight))
                useFilter(check);
        }
    };
    
    public void openFileBrowser()
    {
        FileDialog fd = new FileDialog(this, "File open", FileDialog.LOAD); 
//        fd.setDirectory( m_strLastDir );
        fd.setVisible( true );
        if (fd.getFile() != null)
        {
            parseFile(new File(fd.getDirectory() + fd.getFile()));
            m_recentMenu.addEntry( fd.getDirectory() + fd.getFile() );
        }

        //In response to a button click:
//        final JFileChooser fc = new JFileChooser(m_strLastDir);
//        int returnVal = fc.showOpenDialog(this);
//        if (returnVal == JFileChooser.APPROVE_OPTION)
//        {
//            File file = fc.getSelectedFile();
//            m_strLastDir = fc.getCurrentDirectory().getAbsolutePath();
//            T.d("file = " + file.getAbsolutePath());
//            parseFile(file);
//            m_recentMenu.addEntry( file.getAbsolutePath() );
//        }
    }
}

