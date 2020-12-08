/*
 *  This is a Supplemental File from the Main Project used
 *  in Java Programming Core Fundamental I
 *  with FGroupIndonesia team.
 */
package frames;

import beans.Attendance;
import beans.Document;
import beans.History;
import beans.Payment;
import beans.Schedule;
import beans.Tool;
import beans.User;
import com.google.gson.Gson;
import com.teamdev.jxbrowser.chromium.Browser;
import helper.AudioPlayer;
import helper.CMDExecutor;
import helper.ChartGenerator;
import helper.FileCopier;
import helper.HttpCall;
import helper.JSONChecker;
import helper.PathReference;
import helper.RegistryObtainer;
import helper.RupiahGenerator;
import helper.SWTKey;
import helper.SWThreadWorker;
import helper.ScheduleObserver;
import helper.TableRenderer;
import helper.TrayMaker;
import helper.preferences.SettingPreference;
import helper.UIDragger;
import helper.UIEffect;
import helper.WebReference;
import helper.language.Comp;
import helper.language.LanguageSwitcher;
import helper.preferences.Keys;
import java.awt.CardLayout;
import java.awt.Color;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;

/**
 *
 * @author ASUS
 */
public class ClientFrame extends javax.swing.JFrame implements HttpCall.HttpProcess, UIEffect.PopupAction {

    /**
     * Creates new form MainFrame
     */
    LoginFrame loginFrame;
    CardLayout cardLayouterMain, cardLayouterAttendance;
    SettingPreference configuration = new SettingPreference();
    Browser browser = null;
    TableRenderer tabRender = new TableRenderer();
    RupiahGenerator rpGen = new RupiahGenerator();
    //   ExecutorService executorService = Executors.newFixedThreadPool(28);
    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(28);

    LanguageSwitcher languageHelper  = new LanguageSwitcher(configuration);

    File propicFile;
    File screenshotFile;

    TrayMaker tm = new TrayMaker();

    ImageIcon loadingImage = new ImageIcon(getClass().getResource("/images/loadingprel.gif"));
    ImageIcon okImage = new ImageIcon(getClass().getResource("/images/ok16.png"));
    ImageIcon refreshImage = new ImageIcon(getClass().getResource("/images/refresh24.png"));

    User personLogged;

    public ClientFrame(LoginFrame logRef, User dataIn) {
        personLogged = dataIn;
        loginFrame = logRef;
        processNicely();
    }

    public ClientFrame() {
        processNicely();
    }

    private void processNicely() {
        initComponents();

        // rendering UI based upon its language
        applyLanguageUI();

        // checkin autoupdate tools
        autoUpdateTools();

        //prepare the tray usage
        tm.setFrameRef(this);

        //hide the ui effect
        progressBarDownload.setVisible(false);
        labelPercentage.setVisible(false);

        // activate the effect stuff
        UIDragger.setFrame(this);

        UIEffect.iconChanger(this);
        UIEffect.playTimeEffect(labelTime);

        labelWelcomeUser.setText("Hello " + personLogged.getUsername() + "!");

        labelPanelViewName.setText("Home");
        cardLayouterMain = (CardLayout) panelContentCenter.getLayout();
        cardLayouterAttendance = (CardLayout) panelAttandanceContent.getLayout();

        showPaymentForm(false);

        // for setting ui
        loadConfiguration();

        // hide the label on ui
        hideScheduleLabels();
        hideHistoryLabels();

        // try to access api once again
        refreshProfile();
        refreshDocument();
        refreshAttendance();
        refreshPayment();
        refreshSchedule();
        refreshHistory();

        prepareBrowserNow();
    }

    SWThreadWorker prepbrowser = new SWThreadWorker(this);

    private void prepareBrowserNow() {

        prepbrowser.setBrowser(browser);
        prepbrowser.setPanelInnerBrowser(panelInnerBrowser);
        prepbrowser.setWork(SWTKey.WORK_BROWSER_PREPARE);
        executorService.submit(prepbrowser);

    }

    private String getTokenLocally() {
        return configuration.getStringValue(Keys.TOKEN_API);
    }

    private void prepareToken(SWThreadWorker obSW) {
        System.out.println("adding token for " + obSW.whatWorkAsString() + " " + getTokenLocally());
        obSW.addData("token", this.getTokenLocally());
    }

    private void downloadFile(String target, String fname) {

        SWThreadWorker workDownload = new SWThreadWorker(this);
        workDownload.setWork(SWTKey.WORK_DOCUMENT_DOWNLOAD);
        workDownload.writeMode(true);
        workDownload.addData("url", target);
        workDownload.addData("filename", fname);
        workDownload.addData("username", personLogged.getUsername());
        prepareToken(workDownload);

        // optional for showing the effect
        FileCopier.setProgressBar(progressBarDownload);
        FileCopier.setProgressLabel(labelPercentage, labelLoadingStatus);

        showLoadingStatus("Download");

        // executorService.submit(workSched);
        executorService.schedule(workDownload, 2, TimeUnit.SECONDS);

    }

    private void refreshHistory() {

        SWThreadWorker workHist = new SWThreadWorker(this);
        workHist.setWork(SWTKey.WORK_REFRESH_HISTORY);
        workHist.addData("username", personLogged.getUsername());
        workHist.addData("limit", "5");
        prepareToken(workHist);

        // executorService.submit(workSched);
        executorService.schedule(workHist, 5, TimeUnit.SECONDS);

    }

    private void refreshUserPicture(String filename) {

        // set the path temporarily 
        // for later usage in locally
        PathReference.setPropicFileName(filename);
        File dest = new File(PathReference.UserPropicPath);

        configuration.setValue(Keys.USER_PROPIC, dest.getAbsolutePath());

        SWThreadWorker workPicture = new SWThreadWorker(this);
        // execute the download picture process
        workPicture.setWork(SWTKey.WORK_REFRESH_PICTURE);
        workPicture.writeMode(true);
        workPicture.addData("propic", filename);

        // executorService.submit(workSched);
        executorService.schedule(workPicture, 2, TimeUnit.SECONDS);

    }

    private void refreshProfile() {

        SWThreadWorker workProfile = new SWThreadWorker(this);
        workProfile.setWork(SWTKey.WORK_REFRESH_PROFILE);
        // executorService.submit(workSched);
        workProfile.addData("username", personLogged.getUsername());
        prepareToken(workProfile);

        executorService.schedule(workProfile, 3, TimeUnit.SECONDS);

    }

    private void refreshSchedule() {

        SWThreadWorker workSched = new SWThreadWorker(this);
        workSched.setWork(SWTKey.WORK_REFRESH_SCHEDULE);
        workSched.addData("username", personLogged.getUsername());
        prepareToken(workSched);

        //executorService.submit(workSched);
        executorService.schedule(workSched, 3, TimeUnit.SECONDS);

    }

    private void refreshAttendance() {

        SWThreadWorker workAtt = new SWThreadWorker(this);
        workAtt.setWork(SWTKey.WORK_REFRESH_ATTENDANCE);
        workAtt.addData("username", personLogged.getUsername());
        prepareToken(workAtt);

        //executorService.submit(workAtt);
        executorService.schedule(workAtt, 2, TimeUnit.SECONDS);

    }

    private void refreshPayment() {

        SWThreadWorker workPay = new SWThreadWorker(this);
        workPay.setWork(SWTKey.WORK_REFRESH_PAYMENT);
        workPay.addData("username", personLogged.getUsername());
        prepareToken(workPay);
        //executorService.submit(workPay);
        executorService.schedule(workPay, 2, TimeUnit.SECONDS);

    }

    private void refreshDocument() {

        SWThreadWorker workDoc = new SWThreadWorker(this);
        workDoc.setWork(SWTKey.WORK_REFRESH_DOCUMENT);
        workDoc.addData("username", personLogged.getUsername());
        prepareToken(workDoc);
        //executorService.submit(workDoc);
        executorService.schedule(workDoc, 2, TimeUnit.SECONDS);

    }

    private void showAttendanceStat(boolean stat) {

        if (stat) {
            labelShowAttendanceStatistic.setEnabled(false);
            labelShowAttendanceData.setEnabled(true);
            cardLayouterAttendance.show(panelAttandanceContent, "panelAttendanceStatistic");
        } else {
            labelShowAttendanceStatistic.setEnabled(true);
            labelShowAttendanceData.setEnabled(false);
            cardLayouterAttendance.show(panelAttandanceContent, "panelAttendanceAll");
        }

    }

    private void generateAttendanceStat() {

        ChartGenerator cmaker = new ChartGenerator();

        int hadir = tabRender.getRowCountValue(tableAttendanceData, 4, "hadir");
        int idzin = tabRender.getRowCountValue(tableAttendanceData, 4, "idzin");
        int sakit = tabRender.getRowCountValue(tableAttendanceData, 4, "sakit");

        cmaker.setJumlahHadir(hadir);
        cmaker.setJumlahIdzin(idzin);
        cmaker.setJumlahSakit(sakit);

        CategoryDataset dataset = cmaker.createDataset();

        JFreeChart chart = cmaker.createChart(dataset);

        // coloring grey to the Background from default of white
        chart.setBackgroundPaint(null);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        //chart.getPlot().setBackgroundPaint(Color.red);
        panelAttandanceStatistic.add(chartPanel, java.awt.BorderLayout.CENTER);

        pack();

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        radioButtonGroupNotifClass = new javax.swing.ButtonGroup();
        radioButtonGroupNotifSessionLimit = new javax.swing.ButtonGroup();
        fileChooser = new javax.swing.JFileChooser();
        panelBase = new javax.swing.JPanel();
        panelHeader = new javax.swing.JPanel();
        labelClose = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        labelTime = new javax.swing.JLabel();
        labelMinimize = new javax.swing.JLabel();
        labelTimeIntervalSchedule = new javax.swing.JLabel();
        panelContent = new javax.swing.JPanel();
        panelMenu = new javax.swing.JPanel();
        labelPropicUser = new javax.swing.JLabel();
        buttonProfile = new javax.swing.JButton();
        buttonTools = new javax.swing.JButton();
        buttonDocument = new javax.swing.JButton();
        buttonAttendance = new javax.swing.JButton();
        buttonPayment = new javax.swing.JButton();
        buttonSettings = new javax.swing.JButton();
        buttonLogout = new javax.swing.JButton();
        labelLoadingStatus = new javax.swing.JLabel();
        progressBarDownload = new javax.swing.JProgressBar();
        labelPercentage = new javax.swing.JLabel();
        panelCenter = new javax.swing.JPanel();
        panelContentCenter = new javax.swing.JPanel();
        panelHome = new javax.swing.JPanel();
        panelSchedule = new javax.swing.JPanel();
        labelScheduleDay1 = new javax.swing.JLabel();
        labelScheduleDay2 = new javax.swing.JLabel();
        labelScheduleDay3 = new javax.swing.JLabel();
        progressBarTotalSession = new javax.swing.JProgressBar();
        labelWelcomeUser = new javax.swing.JLabel();
        labelReportBugs = new javax.swing.JLabel();
        labelTotalSessionCompleted = new javax.swing.JLabel();
        labelClassRegistered = new javax.swing.JLabel();
        labelLastPayment = new javax.swing.JLabel();
        panelHistory = new javax.swing.JPanel();
        labelHistoryLast1 = new javax.swing.JLabel();
        labelHistoryLast2 = new javax.swing.JLabel();
        labelHistoryLast3 = new javax.swing.JLabel();
        labelHistoryLast4 = new javax.swing.JLabel();
        labelHistoryLast5 = new javax.swing.JLabel();
        panelProfile = new javax.swing.JPanel();
        textfieldUsernameProfile = new javax.swing.JTextField();
        labelPasswordProfile = new javax.swing.JLabel();
        textfieldPasswordProfile = new javax.swing.JPasswordField();
        labelUsernameProfile = new javax.swing.JLabel();
        labelEmailProfile = new javax.swing.JLabel();
        textfieldEmailProfile = new javax.swing.JTextField();
        labelWhatsappProfile = new javax.swing.JLabel();
        textfieldWhatsappProfile = new javax.swing.JTextField();
        labelAddressProfile = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        textareaAddressProfile = new javax.swing.JTextArea();
        buttonSaveProfile = new javax.swing.JButton();
        labelTmvIDProfile = new javax.swing.JLabel();
        textfieldTeamviewerID = new javax.swing.JTextField();
        labelTmvPassProfile = new javax.swing.JLabel();
        textfieldTeamviewerPass = new javax.swing.JTextField();
        panelSettings = new javax.swing.JPanel();
        labelNotifClassSettings = new javax.swing.JLabel();
        radioNotifClass1DaySettings = new javax.swing.JRadioButton();
        radioNotifClass1HourSettings = new javax.swing.JRadioButton();
        radioNotifSessionAtLeast1Settings = new javax.swing.JRadioButton();
        radioNotifSessionLessThan3Settings = new javax.swing.JRadioButton();
        labelNotifSessionSettings = new javax.swing.JLabel();
        checkboxAutoupdateToolsSettings = new javax.swing.JCheckBox();
        buttonSaveSettings = new javax.swing.JButton();
        comboboxSystemLanguage = new javax.swing.JComboBox<>();
        labelSystemLanguagesSettings = new javax.swing.JLabel();
        panelTools = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        buttonTerminateTmv = new javax.swing.JButton();
        buttonRunTmv = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        buttonVisitChrome = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        buttonVisitWhatsapp = new javax.swing.JButton();
        panelPayment = new javax.swing.JPanel();
        panelPaymentForm = new javax.swing.JPanel();
        labelPaymentForm = new javax.swing.JLabel();
        labelMethodPaymentForm = new javax.swing.JLabel();
        textfieldAmountPayment = new javax.swing.JTextField();
        labelScreenshotPaymentForm = new javax.swing.JLabel();
        combobocMethodPayment = new javax.swing.JComboBox<>();
        labelHidePaymentForm = new javax.swing.JLabel();
        labelScreenshotPayment = new javax.swing.JLabel();
        buttonSavePayment = new javax.swing.JButton();
        labelAmountPaymentForm = new javax.swing.JLabel();
        labelBrowseScreenshotPaymentForm = new javax.swing.JLabel();
        panelPaymentData = new javax.swing.JPanel();
        panelPaymentTable = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablePaymentData = new javax.swing.JTable();
        panelControllerPayment = new javax.swing.JPanel();
        labelAddPayment = new javax.swing.JLabel();
        labelRefreshPayment = new javax.swing.JLabel();
        panelAttendance = new javax.swing.JPanel();
        panelAttendanceData = new javax.swing.JPanel();
        panelAttandanceContent = new javax.swing.JPanel();
        panelAttandanceAll = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tableAttendanceData = new javax.swing.JTable();
        panelAttandanceStatistic = new javax.swing.JPanel();
        panelControllerAttendance = new javax.swing.JPanel();
        labelShowAttendanceData = new javax.swing.JLabel();
        labelShowAttendanceStatistic = new javax.swing.JLabel();
        labelRefreshAttendance = new javax.swing.JLabel();
        panelDocument = new javax.swing.JPanel();
        panelDocumentData = new javax.swing.JPanel();
        panelDocumentContent = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tableDocumentData = new javax.swing.JTable();
        panelControllerDocument = new javax.swing.JPanel();
        labelOpenDocument = new javax.swing.JLabel();
        labelDownloadDocument = new javax.swing.JLabel();
        labelRefreshDocument = new javax.swing.JLabel();
        panelInnerBrowser = new javax.swing.JPanel();
        panelHeaderCenter = new javax.swing.JPanel();
        labelPanelViewName = new javax.swing.JLabel();
        labelNavHome = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setPreferredSize(new java.awt.Dimension(800, 500));

        panelBase.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        panelBase.setPreferredSize(new java.awt.Dimension(750, 439));
        panelBase.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                panelBaseMouseDragged(evt);
            }
        });
        panelBase.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                panelBaseMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                panelBaseMouseReleased(evt);
            }
        });
        panelBase.setLayout(new java.awt.BorderLayout());

        panelHeader.setBackground(new java.awt.Color(255, 0, 0));
        panelHeader.setPreferredSize(new java.awt.Dimension(653, 50));
        panelHeader.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/close24.png"))); // NOI18N
        labelClose.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelClose.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelCloseMouseClicked(evt);
            }
        });
        panelHeader.add(labelClose, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 10, -1, 28));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Portal Access");
        panelHeader.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(18, 11, 208, -1));

        labelTime.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        labelTime.setForeground(new java.awt.Color(255, 255, 255));
        labelTime.setText("time is here");
        panelHeader.add(labelTime, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 20, 170, -1));

        labelMinimize.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/minimize.png"))); // NOI18N
        labelMinimize.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelMinimize.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelMinimizeMouseClicked(evt);
            }
        });
        panelHeader.add(labelMinimize, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 10, -1, 28));

        labelTimeIntervalSchedule.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        labelTimeIntervalSchedule.setForeground(new java.awt.Color(255, 255, 255));
        labelTimeIntervalSchedule.setText("time is here");
        panelHeader.add(labelTimeIntervalSchedule, new org.netbeans.lib.awtextra.AbsoluteConstraints(166, 20, 340, -1));

        panelBase.add(panelHeader, java.awt.BorderLayout.PAGE_START);

        panelContent.setLayout(new java.awt.BorderLayout());

        panelMenu.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        panelMenu.setPreferredSize(new java.awt.Dimension(130, 384));
        panelMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                panelMenuMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                panelMenuMouseExited(evt);
            }
        });
        java.awt.FlowLayout flowLayout1 = new java.awt.FlowLayout();
        flowLayout1.setAlignOnBaseline(true);
        panelMenu.setLayout(flowLayout1);

        labelPropicUser.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPropicUser.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/user.png"))); // NOI18N
        labelPropicUser.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelPropicUser.setPreferredSize(new java.awt.Dimension(96, 96));
        labelPropicUser.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelPropicUserMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                labelPropicUserMouseEntered(evt);
            }
        });
        panelMenu.add(labelPropicUser);

        buttonProfile.setText("Profile");
        buttonProfile.setPreferredSize(new java.awt.Dimension(120, 23));
        buttonProfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonProfileActionPerformed(evt);
            }
        });
        panelMenu.add(buttonProfile);

        buttonTools.setText("Tools");
        buttonTools.setPreferredSize(new java.awt.Dimension(120, 23));
        buttonTools.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonToolsActionPerformed(evt);
            }
        });
        panelMenu.add(buttonTools);

        buttonDocument.setText("Document");
        buttonDocument.setPreferredSize(new java.awt.Dimension(120, 23));
        buttonDocument.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDocumentActionPerformed(evt);
            }
        });
        panelMenu.add(buttonDocument);

        buttonAttendance.setText("Attendance");
        buttonAttendance.setPreferredSize(new java.awt.Dimension(120, 23));
        buttonAttendance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAttendanceActionPerformed(evt);
            }
        });
        panelMenu.add(buttonAttendance);

        buttonPayment.setText("Payment");
        buttonPayment.setPreferredSize(new java.awt.Dimension(120, 23));
        buttonPayment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonPaymentActionPerformed(evt);
            }
        });
        panelMenu.add(buttonPayment);

        buttonSettings.setText("Settings");
        buttonSettings.setPreferredSize(new java.awt.Dimension(120, 23));
        buttonSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSettingsActionPerformed(evt);
            }
        });
        panelMenu.add(buttonSettings);

        buttonLogout.setText("Logout");
        buttonLogout.setPreferredSize(new java.awt.Dimension(120, 23));
        buttonLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonLogoutActionPerformed(evt);
            }
        });
        panelMenu.add(buttonLogout);

        labelLoadingStatus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/loadingprel.gif"))); // NOI18N
        labelLoadingStatus.setText("Loading...");
        panelMenu.add(labelLoadingStatus);

        progressBarDownload.setPreferredSize(new java.awt.Dimension(110, 19));
        panelMenu.add(progressBarDownload);

        labelPercentage.setText("0/0");
        panelMenu.add(labelPercentage);

        panelContent.add(panelMenu, java.awt.BorderLayout.WEST);

        panelCenter.setLayout(new java.awt.BorderLayout());

        panelContentCenter.setLayout(new java.awt.CardLayout());

        panelHome.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelSchedule.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder("Schedule"), javax.swing.BorderFactory.createEmptyBorder(0, 15, 0, 15)));
        panelSchedule.setLayout(new java.awt.GridLayout(0, 1, 100, 0));

        labelScheduleDay1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/loadingprel.gif"))); // NOI18N
        labelScheduleDay1.setText("loading...");
        panelSchedule.add(labelScheduleDay1);

        labelScheduleDay2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/calendar16.png"))); // NOI18N
        labelScheduleDay2.setText("schedule day 2");
        panelSchedule.add(labelScheduleDay2);

        labelScheduleDay3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/calendar16.png"))); // NOI18N
        labelScheduleDay3.setText("schedule day 3");
        panelSchedule.add(labelScheduleDay3);

        panelHome.add(panelSchedule, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 110, 210, 100));
        panelSchedule.getAccessibleContext().setAccessibleName("History");

        panelHome.add(progressBarTotalSession, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 260, 190, -1));

        labelWelcomeUser.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/info.png"))); // NOI18N
        labelWelcomeUser.setText("Welcome, USERNAME");
        panelHome.add(labelWelcomeUser, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, -1, -1));

        labelReportBugs.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelReportBugs.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bug32.png"))); // NOI18N
        labelReportBugs.setText("Report Bugs");
        labelReportBugs.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelReportBugs.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        labelReportBugs.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        labelReportBugs.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                labelReportBugsMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                labelReportBugsMouseExited(evt);
            }
        });
        panelHome.add(labelReportBugs, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 40, 80, 60));

        labelTotalSessionCompleted.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/note16.png"))); // NOI18N
        labelTotalSessionCompleted.setText("Total Session Completed: loading...");
        panelHome.add(labelTotalSessionCompleted, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 240, 230, -1));

        labelClassRegistered.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/loadingprel.gif"))); // NOI18N
        labelClassRegistered.setText("Class Registered : loading...");
        panelHome.add(labelClassRegistered, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 290, 280, -1));

        labelLastPayment.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/loadingprel.gif"))); // NOI18N
        labelLastPayment.setText("Last Payment : loading...");
        panelHome.add(labelLastPayment, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 240, 250, -1));

        panelHistory.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder("History"), javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 5)));
        panelHistory.setLayout(new java.awt.GridLayout(0, 1, 100, 0));

        labelHistoryLast1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/loadingprel.gif"))); // NOI18N
        labelHistoryLast1.setText("loading...");
        panelHistory.add(labelHistoryLast1);

        labelHistoryLast2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ok16.png"))); // NOI18N
        labelHistoryLast2.setText("history 2");
        panelHistory.add(labelHistoryLast2);

        labelHistoryLast3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ok16.png"))); // NOI18N
        labelHistoryLast3.setText("history 3");
        panelHistory.add(labelHistoryLast3);

        labelHistoryLast4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ok16.png"))); // NOI18N
        labelHistoryLast4.setText("history 4");
        panelHistory.add(labelHistoryLast4);

        labelHistoryLast5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ok16.png"))); // NOI18N
        labelHistoryLast5.setText("history 5");
        panelHistory.add(labelHistoryLast5);

        panelHome.add(panelHistory, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, 330, 170));

        panelContentCenter.add(panelHome, "panelHome");

        panelProfile.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        textfieldUsernameProfile.setEditable(false);
        textfieldUsernameProfile.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textfieldUsernameProfileKeyReleased(evt);
            }
        });
        panelProfile.add(textfieldUsernameProfile, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 40, 220, -1));

        labelPasswordProfile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lock16.png"))); // NOI18N
        labelPasswordProfile.setText("Password :");
        panelProfile.add(labelPasswordProfile, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 70, -1, -1));

        textfieldPasswordProfile.setText("jPasswordField1");
        textfieldPasswordProfile.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textfieldPasswordProfileKeyReleased(evt);
            }
        });
        panelProfile.add(textfieldPasswordProfile, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 90, 220, -1));

        labelUsernameProfile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/edit.png"))); // NOI18N
        labelUsernameProfile.setText("Username :");
        panelProfile.add(labelUsernameProfile, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 20, -1, -1));

        labelEmailProfile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/email.png"))); // NOI18N
        labelEmailProfile.setText("Email : ");
        panelProfile.add(labelEmailProfile, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 120, -1, -1));

        textfieldEmailProfile.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textfieldEmailProfileKeyReleased(evt);
            }
        });
        panelProfile.add(textfieldEmailProfile, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 140, 220, -1));

        labelWhatsappProfile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/whatsapp.png"))); // NOI18N
        labelWhatsappProfile.setText("Whatsapp Contact:");
        panelProfile.add(labelWhatsappProfile, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 180, -1, -1));

        textfieldWhatsappProfile.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textfieldWhatsappProfileKeyReleased(evt);
            }
        });
        panelProfile.add(textfieldWhatsappProfile, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 200, 150, -1));

        labelAddressProfile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/text.png"))); // NOI18N
        labelAddressProfile.setText("Address:");
        panelProfile.add(labelAddressProfile, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 180, -1, -1));

        textareaAddressProfile.setColumns(20);
        textareaAddressProfile.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        textareaAddressProfile.setRows(5);
        textareaAddressProfile.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textareaAddressProfileKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(textareaAddressProfile);

        panelProfile.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 200, 220, 90));

        buttonSaveProfile.setText("Save");
        buttonSaveProfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveProfileActionPerformed(evt);
            }
        });
        panelProfile.add(buttonSaveProfile, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 300, 80, 30));

        labelTmvIDProfile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/teamviewer16.png"))); // NOI18N
        labelTmvIDProfile.setText("TeamViewer ID");
        panelProfile.add(labelTmvIDProfile, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 70, -1, -1));

        textfieldTeamviewerID.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textfieldTeamviewerIDKeyReleased(evt);
            }
        });
        panelProfile.add(textfieldTeamviewerID, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 90, 150, -1));

        labelTmvPassProfile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/key.png"))); // NOI18N
        labelTmvPassProfile.setText("TeamViewer Password");
        panelProfile.add(labelTmvPassProfile, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 120, -1, -1));

        textfieldTeamviewerPass.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textfieldTeamviewerPassKeyReleased(evt);
            }
        });
        panelProfile.add(textfieldTeamviewerPass, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 140, 150, -1));

        panelContentCenter.add(panelProfile, "panelProfile");

        panelSettings.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelNotifClassSettings.setText("Notify when class started?");
        panelSettings.add(labelNotifClassSettings, new org.netbeans.lib.awtextra.AbsoluteConstraints(69, 58, 170, -1));

        radioButtonGroupNotifClass.add(radioNotifClass1DaySettings);
        radioNotifClass1DaySettings.setText("1 Day before");
        radioNotifClass1DaySettings.setActionCommand(radioNotifClass1DaySettings.getText().toLowerCase());
        panelSettings.add(radioNotifClass1DaySettings, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 100, -1, -1));

        radioButtonGroupNotifClass.add(radioNotifClass1HourSettings);
        radioNotifClass1HourSettings.setText("1 Hour before");
        radioNotifClass1HourSettings.setActionCommand(radioNotifClass1HourSettings.getText().toLowerCase());
        panelSettings.add(radioNotifClass1HourSettings, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 80, -1, -1));

        radioButtonGroupNotifSessionLimit.add(radioNotifSessionAtLeast1Settings);
        radioNotifSessionAtLeast1Settings.setText("At least 1");
        radioNotifSessionAtLeast1Settings.setActionCommand(radioNotifSessionAtLeast1Settings.getText().toLowerCase());
        panelSettings.add(radioNotifSessionAtLeast1Settings, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 80, -1, -1));

        radioButtonGroupNotifSessionLimit.add(radioNotifSessionLessThan3Settings);
        radioNotifSessionLessThan3Settings.setText("Less than 3");
        radioNotifSessionLessThan3Settings.setActionCommand(radioNotifSessionLessThan3Settings.getText().toLowerCase());
        panelSettings.add(radioNotifSessionLessThan3Settings, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 100, -1, -1));

        labelNotifSessionSettings.setText("Notify when sessions limit reach?");
        panelSettings.add(labelNotifSessionSettings, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 60, 210, -1));

        checkboxAutoupdateToolsSettings.setSelected(true);
        checkboxAutoupdateToolsSettings.setText("Autoupdate Tools");
        panelSettings.add(checkboxAutoupdateToolsSettings, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 160, -1, -1));

        buttonSaveSettings.setText("Save");
        buttonSaveSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveSettingsActionPerformed(evt);
            }
        });
        panelSettings.add(buttonSaveSettings, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 300, 80, 30));

        comboboxSystemLanguage.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "english (default)", "bahasa indonesia", "arabic" }));
        panelSettings.add(comboboxSystemLanguage, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 180, 150, -1));

        labelSystemLanguagesSettings.setText("System Languages:");
        panelSettings.add(labelSystemLanguagesSettings, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 160, -1, -1));

        panelContentCenter.add(panelSettings, "panelSettings");

        panelTools.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/teamviewer64.png"))); // NOI18N
        jLabel11.setText("TeamViewer");
        jLabel11.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel11.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        panelTools.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 60, 90, 100));

        buttonTerminateTmv.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/terminate.png"))); // NOI18N
        buttonTerminateTmv.setText("Terminate");
        buttonTerminateTmv.setEnabled(false);
        buttonTerminateTmv.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        buttonTerminateTmv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonTerminateTmvActionPerformed(evt);
            }
        });
        panelTools.add(buttonTerminateTmv, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 110, 130, -1));

        buttonRunTmv.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/play.png"))); // NOI18N
        buttonRunTmv.setText("Run Now");
        buttonRunTmv.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        buttonRunTmv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRunTmvActionPerformed(evt);
            }
        });
        panelTools.add(buttonRunTmv, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 70, 130, -1));

        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/chrome.png"))); // NOI18N
        jLabel15.setText("Chrome");
        jLabel15.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel15.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        panelTools.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 200, 90, 100));

        buttonVisitChrome.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/play.png"))); // NOI18N
        buttonVisitChrome.setText("Open Now");
        buttonVisitChrome.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        buttonVisitChrome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonVisitChromeActionPerformed(evt);
            }
        });
        panelTools.add(buttonVisitChrome, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 210, 130, -1));

        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/whatsapp64.png"))); // NOI18N
        jLabel18.setText("Whatsapp");
        jLabel18.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel18.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        panelTools.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 60, 90, 100));

        buttonVisitWhatsapp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/play.png"))); // NOI18N
        buttonVisitWhatsapp.setText("Open Now");
        buttonVisitWhatsapp.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        buttonVisitWhatsapp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonVisitWhatsappActionPerformed(evt);
            }
        });
        panelTools.add(buttonVisitWhatsapp, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 70, 130, -1));

        panelContentCenter.add(panelTools, "panelTools");

        panelPayment.setLayout(new java.awt.BorderLayout());

        panelPaymentForm.setPreferredSize(new java.awt.Dimension(200, 371));
        panelPaymentForm.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelPaymentForm.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        labelPaymentForm.setText("Payment Form");
        panelPaymentForm.add(labelPaymentForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 31, 102, 30));

        labelMethodPaymentForm.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/calc.png"))); // NOI18N
        labelMethodPaymentForm.setText("Method:");
        panelPaymentForm.add(labelMethodPaymentForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, 120, -1));

        textfieldAmountPayment.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textfieldAmountPaymentFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                textfieldAmountPaymentFocusLost(evt);
            }
        });
        panelPaymentForm.add(textfieldAmountPayment, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, 160, -1));

        labelScreenshotPaymentForm.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/monitor.png"))); // NOI18N
        labelScreenshotPaymentForm.setText("Screenshot:");
        panelPaymentForm.add(labelScreenshotPaymentForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 190, 90, -1));

        combobocMethodPayment.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Cash", "Transfer Bank" }));
        panelPaymentForm.add(combobocMethodPayment, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, 160, -1));

        labelHidePaymentForm.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/left.png"))); // NOI18N
        labelHidePaymentForm.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelHidePaymentForm.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelHidePaymentFormMouseClicked(evt);
            }
        });
        panelPaymentForm.add(labelHidePaymentForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 40, 30, -1));

        labelScreenshotPayment.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelScreenshotPayment.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/file.png"))); // NOI18N
        panelPaymentForm.add(labelScreenshotPayment, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 220, 140, 100));

        buttonSavePayment.setText("Save");
        buttonSavePayment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSavePaymentActionPerformed(evt);
            }
        });
        panelPaymentForm.add(buttonSavePayment, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 350, 70, -1));

        labelAmountPaymentForm.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/coin.png"))); // NOI18N
        labelAmountPaymentForm.setText("Amount:");
        panelPaymentForm.add(labelAmountPaymentForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 120, -1));

        labelBrowseScreenshotPaymentForm.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        labelBrowseScreenshotPaymentForm.setForeground(new java.awt.Color(102, 0, 255));
        labelBrowseScreenshotPaymentForm.setText("Browse Picture");
        labelBrowseScreenshotPaymentForm.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelBrowseScreenshotPaymentForm.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelBrowseScreenshotPaymentFormMouseClicked(evt);
            }
        });
        panelPaymentForm.add(labelBrowseScreenshotPaymentForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 330, -1, -1));

        panelPayment.add(panelPaymentForm, java.awt.BorderLayout.WEST);

        panelPaymentData.setLayout(new java.awt.BorderLayout());

        panelPaymentTable.setLayout(new java.awt.BorderLayout());

        tablePaymentData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            /*new String [] {
                "[ x ]", "Id", "Username", "Amount", "Method", "Screenshot", "Date Created"
            }*/
            languageHelper.getColumnTable("colCheckBox",
                "colId",
                "colUsernamePayment",
                "colAmountPayment",
                "colMethodPayment",
                "colScreenshotPayment",
                "colDateCreatedPayment")
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                true, true, true, false, false, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablePaymentData.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane2.setViewportView(tablePaymentData);
        if (tablePaymentData.getColumnModel().getColumnCount() > 0) {
            tablePaymentData.getColumnModel().getColumn(0).setResizable(false);
            tablePaymentData.getColumnModel().getColumn(0).setPreferredWidth(35);
            tablePaymentData.getColumnModel().getColumn(1).setMinWidth(0);
            tablePaymentData.getColumnModel().getColumn(1).setPreferredWidth(0);
            tablePaymentData.getColumnModel().getColumn(1).setMaxWidth(0);
            tablePaymentData.getColumnModel().getColumn(2).setMinWidth(0);
            tablePaymentData.getColumnModel().getColumn(2).setPreferredWidth(0);
            tablePaymentData.getColumnModel().getColumn(2).setMaxWidth(0);
            tablePaymentData.getColumnModel().getColumn(3).setMinWidth(120);
            tablePaymentData.getColumnModel().getColumn(3).setPreferredWidth(120);
            tablePaymentData.getColumnModel().getColumn(4).setMinWidth(120);
            tablePaymentData.getColumnModel().getColumn(4).setPreferredWidth(120);
            tablePaymentData.getColumnModel().getColumn(5).setMinWidth(0);
            tablePaymentData.getColumnModel().getColumn(5).setPreferredWidth(0);
            tablePaymentData.getColumnModel().getColumn(5).setMaxWidth(0);
            tablePaymentData.getColumnModel().getColumn(6).setMinWidth(120);
            tablePaymentData.getColumnModel().getColumn(6).setPreferredWidth(120);
        }

        panelPaymentTable.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        panelPaymentData.add(panelPaymentTable, java.awt.BorderLayout.CENTER);

        panelControllerPayment.setPreferredSize(new java.awt.Dimension(869, 75));
        panelControllerPayment.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelAddPayment.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add24.png"))); // NOI18N
        labelAddPayment.setText("Add");
        labelAddPayment.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelAddPayment.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelAddPaymentMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                labelAddPaymentMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                labelAddPaymentMouseExited(evt);
            }
        });
        panelControllerPayment.add(labelAddPayment, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 30, 70, 30));

        labelRefreshPayment.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/refresh24.png"))); // NOI18N
        labelRefreshPayment.setText("Refresh");
        labelRefreshPayment.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelRefreshPayment.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelRefreshPaymentMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                labelRefreshPaymentMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                labelRefreshPaymentMouseExited(evt);
            }
        });
        panelControllerPayment.add(labelRefreshPayment, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 30, 80, 30));

        panelPaymentData.add(panelControllerPayment, java.awt.BorderLayout.PAGE_START);

        panelPayment.add(panelPaymentData, java.awt.BorderLayout.CENTER);

        panelContentCenter.add(panelPayment, "panelPayment");

        panelAttendance.setLayout(new java.awt.BorderLayout());

        panelAttendanceData.setLayout(new java.awt.BorderLayout());

        panelAttandanceContent.setLayout(new java.awt.CardLayout());

        panelAttandanceAll.setLayout(new java.awt.BorderLayout());

        tableAttendanceData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            /*new String [] {
                "[ x ]", "Id", "Username", "Class", "Status", "Signature", "Date Created", "Date Modified"
            }*/
            languageHelper.getColumnTable("colCheckBox",
                "colId",
                "colUsernameAttendance",
                "colClassAttendance",
                "colStatusAttendance",
                "colSignatureAttendance",
                "colDateCreatedAttendance",
                "colDateModifiedAttendance")
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, true, false, false, true, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tableAttendanceData.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane3.setViewportView(tableAttendanceData);
        if (tableAttendanceData.getColumnModel().getColumnCount() > 0) {
            tableAttendanceData.getColumnModel().getColumn(0).setResizable(false);
            tableAttendanceData.getColumnModel().getColumn(0).setPreferredWidth(35);
            tableAttendanceData.getColumnModel().getColumn(1).setMinWidth(0);
            tableAttendanceData.getColumnModel().getColumn(1).setPreferredWidth(0);
            tableAttendanceData.getColumnModel().getColumn(1).setMaxWidth(0);
            tableAttendanceData.getColumnModel().getColumn(2).setMinWidth(0);
            tableAttendanceData.getColumnModel().getColumn(2).setPreferredWidth(0);
            tableAttendanceData.getColumnModel().getColumn(2).setMaxWidth(0);
            tableAttendanceData.getColumnModel().getColumn(3).setMinWidth(100);
            tableAttendanceData.getColumnModel().getColumn(3).setPreferredWidth(100);
            tableAttendanceData.getColumnModel().getColumn(4).setMinWidth(75);
            tableAttendanceData.getColumnModel().getColumn(4).setPreferredWidth(75);
            tableAttendanceData.getColumnModel().getColumn(5).setMinWidth(0);
            tableAttendanceData.getColumnModel().getColumn(5).setPreferredWidth(0);
            tableAttendanceData.getColumnModel().getColumn(5).setMaxWidth(0);
            tableAttendanceData.getColumnModel().getColumn(6).setMinWidth(125);
            tableAttendanceData.getColumnModel().getColumn(6).setPreferredWidth(125);
            tableAttendanceData.getColumnModel().getColumn(7).setMinWidth(0);
            tableAttendanceData.getColumnModel().getColumn(7).setPreferredWidth(0);
            tableAttendanceData.getColumnModel().getColumn(7).setMaxWidth(0);
        }

        panelAttandanceAll.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        panelAttandanceContent.add(panelAttandanceAll, "panelAttendanceAll");

        panelAttandanceStatistic.setLayout(new java.awt.BorderLayout());
        panelAttandanceContent.add(panelAttandanceStatistic, "panelAttendanceStatistic");

        panelAttendanceData.add(panelAttandanceContent, java.awt.BorderLayout.CENTER);

        panelControllerAttendance.setPreferredSize(new java.awt.Dimension(869, 75));
        panelControllerAttendance.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelShowAttendanceData.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/file24.png"))); // NOI18N
        labelShowAttendanceData.setText("View All");
        labelShowAttendanceData.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelShowAttendanceData.setEnabled(false);
        labelShowAttendanceData.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelShowAttendanceDataMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                labelShowAttendanceDataMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                labelShowAttendanceDataMouseExited(evt);
            }
        });
        panelControllerAttendance.add(labelShowAttendanceData, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 30, 80, 30));

        labelShowAttendanceStatistic.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/stat.png"))); // NOI18N
        labelShowAttendanceStatistic.setText("Statistic");
        labelShowAttendanceStatistic.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelShowAttendanceStatistic.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelShowAttendanceStatisticMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                labelShowAttendanceStatisticMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                labelShowAttendanceStatisticMouseExited(evt);
            }
        });
        panelControllerAttendance.add(labelShowAttendanceStatistic, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 30, 80, 30));

        labelRefreshAttendance.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/refresh24.png"))); // NOI18N
        labelRefreshAttendance.setText("Refresh");
        labelRefreshAttendance.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelRefreshAttendance.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelRefreshAttendanceMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                labelRefreshAttendanceMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                labelRefreshAttendanceMouseExited(evt);
            }
        });
        panelControllerAttendance.add(labelRefreshAttendance, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 30, 80, 30));

        panelAttendanceData.add(panelControllerAttendance, java.awt.BorderLayout.PAGE_START);

        panelAttendance.add(panelAttendanceData, java.awt.BorderLayout.CENTER);

        panelContentCenter.add(panelAttendance, "panelAttendance");

        panelDocument.setLayout(new java.awt.BorderLayout());

        panelDocumentData.setLayout(new java.awt.BorderLayout());

        panelDocumentContent.setLayout(new java.awt.BorderLayout());

        tableDocumentData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            /*new String [] {
                "[ x ]", "Id", "Title", "Description", "Filename", "Username", "URL", "Date Created"
            }*/
            languageHelper.getColumnTable("colCheckBox",
                "colId",
                "colTitleDocument",
                "colDescDocument",
                "colFilenameDocument",
                "colUsernameDocument",
                "colURLDocument",
                "colDateCreatedDocument")
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tableDocumentData.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tableDocumentData.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableDocumentDataMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tableDocumentData);
        if (tableDocumentData.getColumnModel().getColumnCount() > 0) {
            tableDocumentData.getColumnModel().getColumn(0).setResizable(false);
            tableDocumentData.getColumnModel().getColumn(0).setPreferredWidth(35);
            tableDocumentData.getColumnModel().getColumn(1).setMinWidth(0);
            tableDocumentData.getColumnModel().getColumn(1).setPreferredWidth(0);
            tableDocumentData.getColumnModel().getColumn(1).setMaxWidth(0);
            tableDocumentData.getColumnModel().getColumn(2).setPreferredWidth(150);
            tableDocumentData.getColumnModel().getColumn(3).setPreferredWidth(300);
            tableDocumentData.getColumnModel().getColumn(5).setMinWidth(0);
            tableDocumentData.getColumnModel().getColumn(5).setPreferredWidth(0);
            tableDocumentData.getColumnModel().getColumn(5).setMaxWidth(0);
            tableDocumentData.getColumnModel().getColumn(6).setMinWidth(0);
            tableDocumentData.getColumnModel().getColumn(6).setPreferredWidth(0);
            tableDocumentData.getColumnModel().getColumn(6).setMaxWidth(0);
            tableDocumentData.getColumnModel().getColumn(7).setPreferredWidth(150);
        }

        panelDocumentContent.add(jScrollPane4, java.awt.BorderLayout.CENTER);

        panelDocumentData.add(panelDocumentContent, java.awt.BorderLayout.CENTER);

        panelControllerDocument.setPreferredSize(new java.awt.Dimension(869, 75));
        panelControllerDocument.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelOpenDocument.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/open.png"))); // NOI18N
        labelOpenDocument.setText("Open");
        labelOpenDocument.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelOpenDocument.setEnabled(false);
        labelOpenDocument.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelOpenDocumentMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                labelOpenDocumentMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                labelOpenDocumentMouseExited(evt);
            }
        });
        panelControllerDocument.add(labelOpenDocument, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 30, 80, 30));

        labelDownloadDocument.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/download.png"))); // NOI18N
        labelDownloadDocument.setText("Download");
        labelDownloadDocument.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelDownloadDocument.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelDownloadDocumentMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                labelDownloadDocumentMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                labelDownloadDocumentMouseExited(evt);
            }
        });
        panelControllerDocument.add(labelDownloadDocument, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 30, 90, 30));

        labelRefreshDocument.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/refresh24.png"))); // NOI18N
        labelRefreshDocument.setText("Refresh");
        labelRefreshDocument.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelRefreshDocument.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelRefreshDocumentMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                labelRefreshDocumentMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                labelRefreshDocumentMouseExited(evt);
            }
        });
        panelControllerDocument.add(labelRefreshDocument, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 30, 80, 30));

        panelDocumentData.add(panelControllerDocument, java.awt.BorderLayout.PAGE_START);

        panelDocument.add(panelDocumentData, java.awt.BorderLayout.CENTER);

        panelContentCenter.add(panelDocument, "panelDocument");

        panelInnerBrowser.setLayout(new java.awt.BorderLayout());
        panelContentCenter.add(panelInnerBrowser, "panelInnerBrowser");

        panelCenter.add(panelContentCenter, java.awt.BorderLayout.CENTER);

        panelHeaderCenter.setPreferredSize(new java.awt.Dimension(574, 50));

        labelPanelViewName.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        labelPanelViewName.setText("Which Menu?");

        labelNavHome.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/home24.png"))); // NOI18N
        labelNavHome.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelNavHome.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelNavHomeMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout panelHeaderCenterLayout = new javax.swing.GroupLayout(panelHeaderCenter);
        panelHeaderCenter.setLayout(panelHeaderCenterLayout);
        panelHeaderCenterLayout.setHorizontalGroup(
            panelHeaderCenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelHeaderCenterLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelNavHome)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(labelPanelViewName, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(1084, Short.MAX_VALUE))
        );
        panelHeaderCenterLayout.setVerticalGroup(
            panelHeaderCenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelHeaderCenterLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelHeaderCenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelHeaderCenterLayout.createSequentialGroup()
                        .addComponent(labelNavHome, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(5, 5, 5))
                    .addComponent(labelPanelViewName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        panelCenter.add(panelHeaderCenter, java.awt.BorderLayout.PAGE_START);

        panelContent.add(panelCenter, java.awt.BorderLayout.CENTER);

        panelBase.add(panelContent, java.awt.BorderLayout.CENTER);

        getContentPane().add(panelBase, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void labelCloseMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelCloseMouseClicked

        logout();
    }//GEN-LAST:event_labelCloseMouseClicked

    private void panelBaseMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelBaseMousePressed
        UIDragger.mousePressed(evt);
    }//GEN-LAST:event_panelBaseMousePressed

    private void panelBaseMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelBaseMouseDragged
        UIDragger.mouseDragged(evt);
    }//GEN-LAST:event_panelBaseMouseDragged

    private void buttonProfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonProfileActionPerformed
        
        //change the menu based upon UI Language
        //labelPanelViewName.setText("<< Profile");
        languageHelper.apply(labelPanelViewName, "labelPanelViewNameProfile", Comp.LABEL);
        
        
        cardLayouterMain.show(panelContentCenter, "panelProfile");

        // check the existance of Tmviewer 
        // if so, grab the client ID
        RegistryObtainer rgb = new RegistryObtainer();
        String clientIdNa = rgb.getClientID();
        if (clientIdNa != null) {
            textfieldTeamviewerID.setText(clientIdNa);
        }
    }//GEN-LAST:event_buttonProfileActionPerformed

    private void buttonToolsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonToolsActionPerformed
        //labelPanelViewName.setText("<< Tools");
        languageHelper.apply(labelPanelViewName, "labelPanelViewNameTools", Comp.LABEL);
        
        cardLayouterMain.show(panelContentCenter, "panelTools");
    }//GEN-LAST:event_buttonToolsActionPerformed

    private void buttonDocumentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDocumentActionPerformed
        //labelPanelViewName.setText("<< Document");
        languageHelper.apply(labelPanelViewName, "labelPanelViewNameDocument", Comp.LABEL);
        cardLayouterMain.show(panelContentCenter, "panelDocument");
    }//GEN-LAST:event_buttonDocumentActionPerformed

    private void buttonAttendanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAttendanceActionPerformed
        //labelPanelViewName.setText("<< Attendance");
        languageHelper.apply(labelPanelViewName, "labelPanelViewNameAttendance", Comp.LABEL);
        cardLayouterMain.show(panelContentCenter, "panelAttendance");
    }//GEN-LAST:event_buttonAttendanceActionPerformed

    private void buttonPaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonPaymentActionPerformed
        //labelPanelViewName.setText("<< Payment");
        languageHelper.apply(labelPanelViewName, "labelPanelViewNamePayment", Comp.LABEL);
        cardLayouterMain.show(panelContentCenter, "panelPayment");

    }//GEN-LAST:event_buttonPaymentActionPerformed

    private void buttonLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonLogoutActionPerformed
        logout();
    }//GEN-LAST:event_buttonLogoutActionPerformed

    private void buttonSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSettingsActionPerformed
        //labelPanelViewName.setText("<< Settings");
        languageHelper.apply(labelPanelViewName, "labelPanelViewNameSettings", Comp.LABEL);
        cardLayouterMain.show(panelContentCenter, "panelSettings");
    }//GEN-LAST:event_buttonSettingsActionPerformed

    private void panelMenuMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelMenuMouseEntered
        
    }//GEN-LAST:event_panelMenuMouseEntered

    private void labelPropicUserMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelPropicUserMouseEntered

    }//GEN-LAST:event_labelPropicUserMouseEntered

    private void panelMenuMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelMenuMouseExited

    }//GEN-LAST:event_panelMenuMouseExited

    private void labelPropicUserMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelPropicUserMouseClicked
        // browse the picture...
        FileFilter imageFilter = new FileNameExtensionFilter(
                "Image files", ImageIO.getReaderFileSuffixes());

        fileChooser.setFileFilter(imageFilter);

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            // change accordingly
            // copy the image to Local AppData path
            // use it to Jlabel Propic
            propicFile = fileChooser.getSelectedFile();

            //System.out.println(source.getName());
            PathReference.setPropicFileName(propicFile.getName());
            File dest = new File(PathReference.UserPropicPath);

            try {
                FileCopier.copyTo(propicFile, dest);
                UIEffect.iconChanger(labelPropicUser, dest.getAbsolutePath());

                // store the settings for next time usage
                configuration.setValue(Keys.USER_PROPIC, dest.getAbsolutePath());
                System.out.println("now is " + dest.getAbsolutePath());
            } catch (Exception ex) {

            }

        } else {
            propicFile = null;
        }
    }//GEN-LAST:event_labelPropicUserMouseClicked

    private void labelNavHomeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelNavHomeMouseClicked
        labelPanelViewName.setText("Home");
        cardLayouterMain.show(panelContentCenter, "panelHome");
    }//GEN-LAST:event_labelNavHomeMouseClicked

    private void buttonSaveSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveSettingsActionPerformed
        configuration.setValue(Keys.AUTO_UPDATE_TOOLS, checkboxAutoupdateToolsSettings.isSelected());

        String notifClass = radioButtonGroupNotifClass.getSelection().getActionCommand();
        configuration.setValue(Keys.NOTIF_CLASS_START, notifClass);

        String notifSession = radioButtonGroupNotifSessionLimit.getSelection().getActionCommand();
        configuration.setValue(Keys.NOTIF_SESSION_LIMIT, notifSession);

        configuration.setValue(Keys.SYSTEM_LANGUAGE, comboboxSystemLanguage.getSelectedItem().toString().toLowerCase());


    }//GEN-LAST:event_buttonSaveSettingsActionPerformed

    private void autoUpdateTools() {

        boolean checked = configuration.getBooleanValue(Keys.AUTO_UPDATE_TOOLS);
        System.out.println("Autoupdate is " + checked);

        if (checked) {
            System.out.println("Autoupdate is enabled!");
            // check certain hours
            String timeMust[] = {"08:00", "12:00", "18:00", "22:00"};

            Date n = new Date();
            SimpleDateFormat sdp = new SimpleDateFormat("HH:mm");
            String timeNow = sdp.format(n);

            System.out.println("Now is " + timeNow);

            for (String singleTime : timeMust) {
                if (singleTime.equalsIgnoreCase(timeNow)) {
                    System.out.println("Time for checking...!");
                    checkAutoUpdateTools();
                    break;
                }

            }

        }
    }

    private void checkAutoUpdateTools() {

        SWThreadWorker workCheck = new SWThreadWorker(this);
        workCheck.setWork(SWTKey.WORK_CHECK_TOOLS);
        workCheck.addData("app_name", "teamviewer");

        prepareToken(workCheck);
        //executorService.submit(workPay);
        executorService.schedule(workCheck, 2, TimeUnit.SECONDS);

    }

    private void downloadTools() {

        // rename first the old one
        CMDExecutor.backupOldTeamviewer();

        SWThreadWorker workCheck = new SWThreadWorker(this);
        workCheck.setWork(SWTKey.WORK_DOWNLOAD_TOOLS);
        workCheck.writeMode(true);
        workCheck.addData("app_name", "teamviewer");

        prepareToken(workCheck);

        // optional for showing the effect
        FileCopier.setProgressBar(progressBarDownload);
        FileCopier.setProgressLabel(labelPercentage, labelLoadingStatus);

        showLoadingStatus("Download");

        executorService.schedule(workCheck, 2, TimeUnit.SECONDS);

    }

    private void buttonTerminateTmvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonTerminateTmvActionPerformed
        CMDExecutor.killTeamviewer();

        buttonTerminateTmv.setEnabled(false);
        buttonRunTmv.setEnabled(true);
    }//GEN-LAST:event_buttonTerminateTmvActionPerformed

    private void buttonRunTmvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRunTmvActionPerformed

        buttonTerminateTmv.setEnabled(true);
        buttonRunTmv.setEnabled(false);

        CMDExecutor.runTeamviewer();

    }//GEN-LAST:event_buttonRunTmvActionPerformed

    private void labelAddPaymentMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelAddPaymentMouseClicked
        showPaymentForm(true);
    }//GEN-LAST:event_labelAddPaymentMouseClicked

    private void showPaymentForm(boolean stat) {
        if (stat) {

            panelPaymentForm.setPreferredSize(new java.awt.Dimension(200, 371));

        } else {
            panelPaymentForm.setPreferredSize(new java.awt.Dimension(0, 371));
        }

        panelPaymentForm.invalidate();
    }

    private void labelAddPaymentMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelAddPaymentMouseEntered
        labelAddPayment.setForeground(Color.blue);
    }//GEN-LAST:event_labelAddPaymentMouseEntered

    private void labelAddPaymentMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelAddPaymentMouseExited
        labelAddPayment.setForeground(null);
    }//GEN-LAST:event_labelAddPaymentMouseExited

    private void buttonSavePaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSavePaymentActionPerformed
        showPaymentForm(false);

        savePayment();

    }//GEN-LAST:event_buttonSavePaymentActionPerformed

    private void labelHidePaymentFormMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelHidePaymentFormMouseClicked
        showPaymentForm(false);
    }//GEN-LAST:event_labelHidePaymentFormMouseClicked

    private void labelShowAttendanceDataMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelShowAttendanceDataMouseClicked
        showAttendanceStat(false);

    }//GEN-LAST:event_labelShowAttendanceDataMouseClicked

    private void labelShowAttendanceDataMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelShowAttendanceDataMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_labelShowAttendanceDataMouseEntered

    private void labelShowAttendanceDataMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelShowAttendanceDataMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_labelShowAttendanceDataMouseExited

    private void labelShowAttendanceStatisticMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelShowAttendanceStatisticMouseClicked
        showAttendanceStat(true);
        generateAttendanceStat();
    }//GEN-LAST:event_labelShowAttendanceStatisticMouseClicked

    private void labelShowAttendanceStatisticMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelShowAttendanceStatisticMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_labelShowAttendanceStatisticMouseEntered

    private void labelShowAttendanceStatisticMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelShowAttendanceStatisticMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_labelShowAttendanceStatisticMouseExited

    private void labelOpenDocumentMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelOpenDocumentMouseClicked
        ArrayList dataDoc = tabRender.getCheckedRows(tableDocumentData, 4);

        if (dataDoc.isEmpty()) {
            UIEffect.popup("Please select the row first!", this);
        } else {
            // passing file name
            for (Object f : dataDoc) {
                PathReference.setDocumentFileName(f.toString());
                File lokasiFile = new File(PathReference.DocumentFilePath);
                if (lokasiFile.exists()) {
                    CMDExecutor.openDocument(lokasiFile);
                }
            }

        }


    }//GEN-LAST:event_labelOpenDocumentMouseClicked

    private void labelOpenDocumentMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelOpenDocumentMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_labelOpenDocumentMouseEntered

    private void labelOpenDocumentMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelOpenDocumentMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_labelOpenDocumentMouseExited

    private void labelDownloadDocumentMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelDownloadDocumentMouseClicked

        //cardLayouterMain.show(panelContentCenter, "panelInnerBrowser");
        // temporaryly
        ArrayList dataDoc = tabRender.getCheckedRows(tableDocumentData, 6);
        ArrayList dataDoc2 = tabRender.getCheckedRows(tableDocumentData, 4);

        if (dataDoc.isEmpty()) {
            UIEffect.popup("Please select the row first!", this);
        } else {
            // passing url only
            if (!progressBarDownload.isVisible()) {
                showLoadingStatus();
                progressBarDownload.setVisible(true);
                labelPercentage.setVisible(true);
                // download only the 1 one
                downloadFile(dataDoc.get(0).toString(), dataDoc2.get(0).toString());
            } else {
                UIEffect.popup("Please wait until the download progress is completed!", this);
            }
        }


    }//GEN-LAST:event_labelDownloadDocumentMouseClicked

    private void savePayment() {
        showLoadingStatus();
        SWThreadWorker workPayment = new SWThreadWorker(this);

        workPayment.setWork(SWTKey.WORK_PAYMENT_SAVE);

        workPayment.addData("username", personLogged.getUsername());
        workPayment.addData("method", combobocMethodPayment.getSelectedItem().toString());
        workPayment.addData("amount", "" + rpGen.getIntNumber(textfieldAmountPayment.getText()));

        // for screenshot we will post the data here
        if (screenshotFile != null) {
            workPayment.addFile("screenshot", screenshotFile);
        }

        prepareToken(workPayment);
        executorService.schedule(workPayment, 1, TimeUnit.SECONDS);

        buttonSavePayment.setEnabled(false);
    }


    private void labelDownloadDocumentMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelDownloadDocumentMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_labelDownloadDocumentMouseEntered

    private void labelDownloadDocumentMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelDownloadDocumentMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_labelDownloadDocumentMouseExited

    private void labelReportBugsMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelReportBugsMouseEntered
        UIEffect.focusGained(labelReportBugs);
    }//GEN-LAST:event_labelReportBugsMouseEntered

    private void labelReportBugsMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelReportBugsMouseExited
        UIEffect.focusLost(labelReportBugs);
    }//GEN-LAST:event_labelReportBugsMouseExited

    private void labelMinimizeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelMinimizeMouseClicked
        //this.setState(Frame.ICONIFIED);
        toggleTray();


    }//GEN-LAST:event_labelMinimizeMouseClicked

    private void toggleTray() {
        this.setVisible(false);
        try {

            if (tm.isSupported()) {
                System.out.println("Tray created!");
                tm.createTray();
            }

            System.out.println("Tray done!");
        } catch (Exception ex) {
            UIEffect.popup("Warning! Unsupported Tray!", this);
        }
    }

    private void buttonVisitChromeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonVisitChromeActionPerformed
        browser.loadURL("http://bing.com");

        cardLayouterMain.show(panelContentCenter, "panelInnerBrowser");
    }//GEN-LAST:event_buttonVisitChromeActionPerformed

    private void buttonVisitWhatsappActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonVisitWhatsappActionPerformed
        browser.loadURL("http://web.whatsapp.com");

        cardLayouterMain.show(panelContentCenter, "panelInnerBrowser");
    }//GEN-LAST:event_buttonVisitWhatsappActionPerformed

    public void setBrowserBack(Browser b) {
        browser = b;
    }

    public void setPanelInnerBrowserBack(JPanel jp) {
        panelInnerBrowser = jp;
    }

    private void textfieldAmountPaymentFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textfieldAmountPaymentFocusLost

        if (!UIEffect.isEmpty(textfieldAmountPayment)) {
            double d = Double.parseDouble(textfieldAmountPayment.getText());
            textfieldAmountPayment.setText(rpGen.getText(d));
        }

    }//GEN-LAST:event_textfieldAmountPaymentFocusLost

    private void textfieldAmountPaymentFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textfieldAmountPaymentFocusGained
        if (!UIEffect.isEmpty(textfieldAmountPayment)) {
            textfieldAmountPayment.setText("" + rpGen.getNumber(textfieldAmountPayment.getText()));
        }
    }//GEN-LAST:event_textfieldAmountPaymentFocusGained

    private void buttonSaveProfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveProfileActionPerformed
        saveUserProfile();
    }//GEN-LAST:event_buttonSaveProfileActionPerformed

    private void textfieldTeamviewerIDKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textfieldTeamviewerIDKeyReleased
        enableUserFormSave();
    }//GEN-LAST:event_textfieldTeamviewerIDKeyReleased

    private void textfieldTeamviewerPassKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textfieldTeamviewerPassKeyReleased
        enableUserFormSave();
    }//GEN-LAST:event_textfieldTeamviewerPassKeyReleased

    private void textfieldWhatsappProfileKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textfieldWhatsappProfileKeyReleased
        enableUserFormSave();
    }//GEN-LAST:event_textfieldWhatsappProfileKeyReleased

    private void textfieldUsernameProfileKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textfieldUsernameProfileKeyReleased
        enableUserFormSave();
    }//GEN-LAST:event_textfieldUsernameProfileKeyReleased

    private void textfieldPasswordProfileKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textfieldPasswordProfileKeyReleased
        enableUserFormSave();
    }//GEN-LAST:event_textfieldPasswordProfileKeyReleased

    private void textfieldEmailProfileKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textfieldEmailProfileKeyReleased
        enableUserFormSave();
    }//GEN-LAST:event_textfieldEmailProfileKeyReleased

    private void textareaAddressProfileKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textareaAddressProfileKeyReleased
        enableUserFormSave();
    }//GEN-LAST:event_textareaAddressProfileKeyReleased

    private void panelBaseMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelBaseMouseReleased
        UIDragger.mouseReleased(evt);
    }//GEN-LAST:event_panelBaseMouseReleased

    private void labelRefreshPaymentMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRefreshPaymentMouseClicked
        refreshPayment();
        labelRefreshPayment.setIcon(loadingImage);
    }//GEN-LAST:event_labelRefreshPaymentMouseClicked

    private void labelRefreshPaymentMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRefreshPaymentMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_labelRefreshPaymentMouseEntered

    private void labelRefreshPaymentMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRefreshPaymentMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_labelRefreshPaymentMouseExited

    private void labelRefreshAttendanceMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRefreshAttendanceMouseClicked
        refreshAttendance();
        labelRefreshAttendance.setIcon(loadingImage);
    }//GEN-LAST:event_labelRefreshAttendanceMouseClicked

    private void labelRefreshAttendanceMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRefreshAttendanceMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_labelRefreshAttendanceMouseEntered

    private void labelRefreshAttendanceMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRefreshAttendanceMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_labelRefreshAttendanceMouseExited

    private void labelRefreshDocumentMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRefreshDocumentMouseClicked
        refreshDocument();
        labelRefreshDocument.setIcon(loadingImage);
    }//GEN-LAST:event_labelRefreshDocumentMouseClicked

    private void labelRefreshDocumentMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRefreshDocumentMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_labelRefreshDocumentMouseEntered

    private void labelRefreshDocumentMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRefreshDocumentMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_labelRefreshDocumentMouseExited

    private void tableDocumentDataMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableDocumentDataMouseClicked

        // check the existance of the file
        // the file name is stored here
        String fName = tabRender.getCheckedRowValue(tableDocumentData, 4);

        if (fName != null) {

            File n = new File(PathReference.getDocumentPath(fName));
            labelOpenDocument.setEnabled(n.exists());
            labelDownloadDocument.setEnabled(!n.exists());

        } else {
            labelOpenDocument.setEnabled(false);
            labelDownloadDocument.setEnabled(true);
        }

    }//GEN-LAST:event_tableDocumentDataMouseClicked

    private void labelBrowseScreenshotPaymentFormMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelBrowseScreenshotPaymentFormMouseClicked
        // browse the picture...
        FileFilter imageFilter = new FileNameExtensionFilter(
                "Image files", ImageIO.getReaderFileSuffixes());

        fileChooser.setFileFilter(imageFilter);

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            // change accordingly
            // copy the image to Local AppData path
            // use it to Jlabel Propic
            screenshotFile = fileChooser.getSelectedFile();

            File dest = new File(PathReference.getScreenshotPath(screenshotFile.getName()));

            try {
                FileCopier.copyTo(screenshotFile, dest);
                UIEffect.iconChanger(labelScreenshotPayment, dest.getAbsolutePath());

                System.out.println("screenshot now is " + dest.getAbsolutePath());
            } catch (Exception ex) {

            }

        } else {
            screenshotFile = null;
        }
    }//GEN-LAST:event_labelBrowseScreenshotPaymentFormMouseClicked

    private void enableUserFormSave() {

        boolean allow = false;

        if (!UIEffect.isEmpty(textfieldPasswordProfile)) {

            if (!UIEffect.isEmpty(textfieldEmailProfile)) {

                if (!UIEffect.isEmpty(textfieldWhatsappProfile)) {

                    if (!UIEffect.isEmpty(textfieldTeamviewerID)) {

                        if (!UIEffect.isEmpty(textfieldTeamviewerPass)) {

                            if (!UIEffect.isEmpty(textareaAddressProfile)) {
                                allow = true;
                            }

                        }

                    }
                }
            }
        }

        buttonSaveProfile.setEnabled(allow);

    }

    private void showLoadingStatus() {
        labelLoadingStatus.setText("Loading...");
        labelLoadingStatus.setIcon(loadingImage);
        labelLoadingStatus.setVisible(true);
    }

    private void showLoadingStatus(String message) {
        labelLoadingStatus.setText(message + "...");
        labelLoadingStatus.setIcon(loadingImage);
        labelLoadingStatus.setVisible(true);
    }

    private void hideLoadingStatus() {
        labelLoadingStatus.setVisible(false);
    }

    private void saveUserProfile() {
        showLoadingStatus();
        SWThreadWorker workUserEntity = new SWThreadWorker(this);

        workUserEntity.setWork(SWTKey.WORK_USER_UPDATE);
        workUserEntity.addData("id", personLogged.getId() + "");

        workUserEntity.addData("username", textfieldUsernameProfile.getText());
        workUserEntity.addData("password", textfieldPasswordProfile.getText());
        workUserEntity.addData("email", textfieldEmailProfile.getText());
        workUserEntity.addData("address", textareaAddressProfile.getText());
        workUserEntity.addData("mobile", textfieldWhatsappProfile.getText());
        workUserEntity.addData("tmv_id", textfieldTeamviewerID.getText());

        workUserEntity.addData("tmv_pass", textfieldTeamviewerPass.getText());

        // for propic we will post the data here
        if (propicFile != null) {
            workUserEntity.addFile("propic", propicFile);
        }

        prepareToken(workUserEntity);
        executorService.schedule(workUserEntity, 2, TimeUnit.SECONDS);

        buttonSaveProfile.setEnabled(false);
    }

    private void loadUserPictureLocally() {

        String propic = configuration.getStringValue(Keys.USER_PROPIC);

        System.out.println("Client Frame is Trying to load " + propic);

        if (!propic.contains("default") && propic.length() > 0) {
            // set the propic
            UIEffect.iconChanger(labelPropicUser, (propic));

        }

        hideLoadingStatus();
    }

    // for settings ui
    private void loadConfiguration() {
        loadUserPictureLocally();
        //System.out.println(propic);

        checkboxAutoupdateToolsSettings.setSelected(configuration.getBooleanValue(Keys.AUTO_UPDATE_TOOLS));
        comboboxSystemLanguage.setSelectedItem(configuration.getStringValue(Keys.SYSTEM_LANGUAGE));

        if (configuration.getStringValue(Keys.NOTIF_CLASS_START).equalsIgnoreCase(Keys.DEFAULT_NOTIF_CLASS_START)) {
            radioNotifClass1HourSettings.setSelected(true);
        } else {
            radioNotifClass1HourSettings.setSelected(false);
        }

        if (configuration.getStringValue(Keys.NOTIF_SESSION_LIMIT).equalsIgnoreCase(Keys.DEFAULT_NOTIF_SESSION_LIMIT)) {
            radioNotifSessionAtLeast1Settings.setSelected(true);
        } else {
            radioNotifSessionAtLeast1Settings.setSelected(false);
        }

    }

    private void logout() {
        UIEffect.stopTimeEffect();
        loginFrame.show();
        this.dispose();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {

        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ClientFrame();
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAttendance;
    private javax.swing.JButton buttonDocument;
    private javax.swing.JButton buttonLogout;
    private javax.swing.JButton buttonPayment;
    private javax.swing.JButton buttonProfile;
    private javax.swing.JButton buttonRunTmv;
    private javax.swing.JButton buttonSavePayment;
    private javax.swing.JButton buttonSaveProfile;
    private javax.swing.JButton buttonSaveSettings;
    private javax.swing.JButton buttonSettings;
    private javax.swing.JButton buttonTerminateTmv;
    private javax.swing.JButton buttonTools;
    private javax.swing.JButton buttonVisitChrome;
    private javax.swing.JButton buttonVisitWhatsapp;
    private javax.swing.JCheckBox checkboxAutoupdateToolsSettings;
    private javax.swing.JComboBox<String> combobocMethodPayment;
    private javax.swing.JComboBox<String> comboboxSystemLanguage;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel labelAddPayment;
    private javax.swing.JLabel labelAddressProfile;
    private javax.swing.JLabel labelAmountPaymentForm;
    private javax.swing.JLabel labelBrowseScreenshotPaymentForm;
    private javax.swing.JLabel labelClassRegistered;
    private javax.swing.JLabel labelClose;
    private javax.swing.JLabel labelDownloadDocument;
    private javax.swing.JLabel labelEmailProfile;
    private javax.swing.JLabel labelHidePaymentForm;
    private javax.swing.JLabel labelHistoryLast1;
    private javax.swing.JLabel labelHistoryLast2;
    private javax.swing.JLabel labelHistoryLast3;
    private javax.swing.JLabel labelHistoryLast4;
    private javax.swing.JLabel labelHistoryLast5;
    private javax.swing.JLabel labelLastPayment;
    private javax.swing.JLabel labelLoadingStatus;
    private javax.swing.JLabel labelMethodPaymentForm;
    private javax.swing.JLabel labelMinimize;
    private javax.swing.JLabel labelNavHome;
    private javax.swing.JLabel labelNotifClassSettings;
    private javax.swing.JLabel labelNotifSessionSettings;
    private javax.swing.JLabel labelOpenDocument;
    private javax.swing.JLabel labelPanelViewName;
    private javax.swing.JLabel labelPasswordProfile;
    private javax.swing.JLabel labelPaymentForm;
    private javax.swing.JLabel labelPercentage;
    private javax.swing.JLabel labelPropicUser;
    private javax.swing.JLabel labelRefreshAttendance;
    private javax.swing.JLabel labelRefreshDocument;
    private javax.swing.JLabel labelRefreshPayment;
    private javax.swing.JLabel labelReportBugs;
    private javax.swing.JLabel labelScheduleDay1;
    private javax.swing.JLabel labelScheduleDay2;
    private javax.swing.JLabel labelScheduleDay3;
    private javax.swing.JLabel labelScreenshotPayment;
    private javax.swing.JLabel labelScreenshotPaymentForm;
    private javax.swing.JLabel labelShowAttendanceData;
    private javax.swing.JLabel labelShowAttendanceStatistic;
    private javax.swing.JLabel labelSystemLanguagesSettings;
    private javax.swing.JLabel labelTime;
    private javax.swing.JLabel labelTimeIntervalSchedule;
    private javax.swing.JLabel labelTmvIDProfile;
    private javax.swing.JLabel labelTmvPassProfile;
    private javax.swing.JLabel labelTotalSessionCompleted;
    private javax.swing.JLabel labelUsernameProfile;
    private javax.swing.JLabel labelWelcomeUser;
    private javax.swing.JLabel labelWhatsappProfile;
    private javax.swing.JPanel panelAttandanceAll;
    private javax.swing.JPanel panelAttandanceContent;
    private javax.swing.JPanel panelAttandanceStatistic;
    private javax.swing.JPanel panelAttendance;
    private javax.swing.JPanel panelAttendanceData;
    private javax.swing.JPanel panelBase;
    private javax.swing.JPanel panelCenter;
    private javax.swing.JPanel panelContent;
    private javax.swing.JPanel panelContentCenter;
    private javax.swing.JPanel panelControllerAttendance;
    private javax.swing.JPanel panelControllerDocument;
    private javax.swing.JPanel panelControllerPayment;
    private javax.swing.JPanel panelDocument;
    private javax.swing.JPanel panelDocumentContent;
    private javax.swing.JPanel panelDocumentData;
    private javax.swing.JPanel panelHeader;
    private javax.swing.JPanel panelHeaderCenter;
    private javax.swing.JPanel panelHistory;
    private javax.swing.JPanel panelHome;
    private javax.swing.JPanel panelInnerBrowser;
    private javax.swing.JPanel panelMenu;
    private javax.swing.JPanel panelPayment;
    private javax.swing.JPanel panelPaymentData;
    private javax.swing.JPanel panelPaymentForm;
    private javax.swing.JPanel panelPaymentTable;
    private javax.swing.JPanel panelProfile;
    private javax.swing.JPanel panelSchedule;
    private javax.swing.JPanel panelSettings;
    private javax.swing.JPanel panelTools;
    private javax.swing.JProgressBar progressBarDownload;
    private javax.swing.JProgressBar progressBarTotalSession;
    private javax.swing.ButtonGroup radioButtonGroupNotifClass;
    private javax.swing.ButtonGroup radioButtonGroupNotifSessionLimit;
    private javax.swing.JRadioButton radioNotifClass1DaySettings;
    private javax.swing.JRadioButton radioNotifClass1HourSettings;
    private javax.swing.JRadioButton radioNotifSessionAtLeast1Settings;
    private javax.swing.JRadioButton radioNotifSessionLessThan3Settings;
    private javax.swing.JTable tableAttendanceData;
    private javax.swing.JTable tableDocumentData;
    private javax.swing.JTable tablePaymentData;
    private javax.swing.JTextArea textareaAddressProfile;
    private javax.swing.JTextField textfieldAmountPayment;
    private javax.swing.JTextField textfieldEmailProfile;
    private javax.swing.JPasswordField textfieldPasswordProfile;
    private javax.swing.JTextField textfieldTeamviewerID;
    private javax.swing.JTextField textfieldTeamviewerPass;
    private javax.swing.JTextField textfieldUsernameProfile;
    private javax.swing.JTextField textfieldWhatsappProfile;
    // End of variables declaration//GEN-END:variables

    private void hideScheduleLabels() {
        //labelScheduleDay1.setVisible(false);
        labelScheduleDay2.setVisible(false);
        labelScheduleDay3.setVisible(false);
    }

    private void hideHistoryLabels() {
        //labelScheduleDay1.setVisible(false);
        labelHistoryLast2.setVisible(false);
        labelHistoryLast3.setVisible(false);
        labelHistoryLast4.setVisible(false);
        labelHistoryLast5.setVisible(false);
    }

    private boolean checkDiffVersion(String ver1, String ver2) {

        System.out.println("First ver " + ver1 + " and Second ver " + ver2);

        boolean stat = false;

        if (!ver1.equalsIgnoreCase(ver2)) {
            stat = true;
            //set the new version because we will download it here
            configuration.setValue(Keys.TEAMVIEWER_VERSION, ver2);
        }

        return stat;

    }

    @Override
    public void checkResponse(String resp, String callingFromURL) {
        Gson objectG = new Gson();

        System.out.println(callingFromURL + " have " + resp);
        JSONChecker jchecker = new JSONChecker();

        if (jchecker.isValid(resp)) {

            boolean b = callingFromURL.contains(WebReference.PICTURE_USER);

            ImageIcon okIcon = new ImageIcon(getClass().getResource("/images/ok16.png"));
            ImageIcon coinIcon = new ImageIcon(getClass().getResource("/images/coin.png"));
            ImageIcon sessionIcon = new ImageIcon(getClass().getResource("/images/note16.png"));

            String innerData = jchecker.getValueAsString("multi_data");

            //System.out.println(callingFromURL + " is valid " + b);
            if (callingFromURL.equalsIgnoreCase(WebReference.ADD_PAYMENT)) {

                // success refreshing the table
                refreshPayment();
                hideLoadingStatus();

            } else if (callingFromURL.equalsIgnoreCase(WebReference.UPDATE_USER)) {

                // success updating user profile
                hideLoadingStatus();

            } else if (callingFromURL.equalsIgnoreCase(WebReference.ALL_DOCUMENT)) {
                Document[] dataIn = objectG.fromJson(innerData, Document[].class);
                tabRender.render(tableDocumentData, dataIn);

                labelRefreshDocument.setIcon(refreshImage);

            } else if (callingFromURL.equalsIgnoreCase(WebReference.ALL_ATTENDANCE)) {
                Attendance[] dataIn = objectG.fromJson(innerData, Attendance[].class);
                tabRender.render(tableAttendanceData, dataIn);

                labelRefreshAttendance.setIcon(refreshImage);

                labelTotalSessionCompleted.setText("Total Session Completed: " + dataIn.length);
                labelTotalSessionCompleted.setIcon(sessionIcon);
            } else if (callingFromURL.equalsIgnoreCase(WebReference.CHECK_TOOLS)) {

                Tool dataIn = objectG.fromJson(innerData, Tool.class);

                // checking versioning
                // this is forcing the version to be saved on configuration
                //configuration.setValue(Keys.TEAMVIEWER_VERSION, "15.9.4.0");
                String curVer = configuration.getStringValue(Keys.TEAMVIEWER_VERSION);
                boolean diff = checkDiffVersion(curVer, dataIn.getApp_ver());

                if (diff) {
                    System.out.println("We have to download new tools one!");
                    // kill any instance first
                    UIEffect.setPopupListener(this);
                    UIEffect.popupConfirm("New version for Tools (TeamViewer) is now available. Are you ready to download this update?", this);
                    // it will jump to actionYES or actionNO below
                } else {
                    System.out.println("No new updates for the tools...");
                }

            } else if (callingFromURL.contains(WebReference.PICTURE_USER)) {

                System.out.println("Obtaining Picture from web is success...\nNow applying it locally.");
                loadUserPictureLocally();

            } else if (callingFromURL.equalsIgnoreCase(WebReference.ALL_PAYMENT)) {
                Payment[] dataIn = objectG.fromJson(innerData, Payment[].class);
                tabRender.render(tablePaymentData, dataIn);

                labelLastPayment.setText("Last Payment : " + dataIn[dataIn.length - 1].getDate_created());
                labelLastPayment.setIcon(coinIcon);

                labelRefreshPayment.setIcon(refreshImage);
            } else if (callingFromURL.equalsIgnoreCase(WebReference.PROFILE_USER)) {

                System.out.println("Obtaining Profile User data...");

                User dataIn = objectG.fromJson(innerData, User.class);
                textfieldUsernameProfile.setText(dataIn.getUsername());
                textfieldPasswordProfile.setText(dataIn.getPass());
                textfieldEmailProfile.setText(dataIn.getEmail());
                textfieldWhatsappProfile.setText(dataIn.getMobile());
                textareaAddressProfile.setText(dataIn.getAddress());

                // ref back to the object
                personLogged = dataIn;

                // this is additional for obtaining the picture
                if (!dataIn.getPropic().equalsIgnoreCase("default.png")) {
                    System.out.println("calling the inside picture");
                    refreshUserPicture(dataIn.getPropic());
                }

                System.out.println("Complete User Profile data was obtained!");

            } else if (callingFromURL.equalsIgnoreCase(WebReference.LAST_HISTORY)) {
                History[] dataIn = objectG.fromJson(innerData, History[].class);

                switch (dataIn.length) {
                    case 5:
                        labelHistoryLast5.setText(" " + dataIn[4].getDescription() + " on " + dataIn[4].getDate_created());
                        labelHistoryLast5.setVisible(true);
                    case 4:
                        labelHistoryLast4.setText(" " + dataIn[3].getDescription() + " on " + dataIn[3].getDate_created());
                        labelHistoryLast4.setVisible(true);
                    case 3:
                        labelHistoryLast3.setText(" " + dataIn[2].getDescription() + " on " + dataIn[2].getDate_created());
                        labelHistoryLast3.setVisible(true);
                    case 2:
                        labelHistoryLast2.setText(" " + dataIn[1].getDescription() + " on " + dataIn[1].getDate_created());
                        labelHistoryLast2.setVisible(true);
                    case 1:
                        labelHistoryLast1.setText(" " + dataIn[0].getDescription() + " on " + dataIn[0].getDate_created());
                        labelHistoryLast1.setVisible(true);
                        labelHistoryLast1.setIcon(okIcon);
                        break;
                    default:
                        labelHistoryLast2.setVisible(false);
                        labelHistoryLast3.setVisible(false);
                        labelHistoryLast4.setVisible(false);
                        labelHistoryLast5.setVisible(false);
                }

            } else if (callingFromURL.equalsIgnoreCase(WebReference.ALL_SCHEDULE)) {
                Schedule[] dataIn = objectG.fromJson(innerData, Schedule[].class);

                ImageIcon calendarIcon = new ImageIcon(getClass().getResource("/images/calendar16.png"));
                ImageIcon classIcon = new ImageIcon(getClass().getResource("/images/class.png"));

                // change the title accordingly
                String className = dataIn[0].getClass_registered();
                panelSchedule.setBorder(javax.swing.BorderFactory.createTitledBorder("Schedule : " + className));
                labelClassRegistered.setText("Class Registered : " + className);
                labelClassRegistered.setIcon(classIcon);

                // schedule helper to calculate and animate time interval before class started
                String schedText = dataIn[0].getDay_schedule() + " " + dataIn[0].getTime_schedule();
                ScheduleObserver schedObs = new ScheduleObserver();
                schedObs.setDate(schedText);

                UIEffect.setFrameRef(this);
                UIEffect.playIntervalTimeEffect(labelTimeIntervalSchedule, schedObs.getDate());

                switch (dataIn.length) {
                    case 3:
                        labelScheduleDay3.setText(" " + dataIn[2].getDay_schedule() + " " + dataIn[2].getTime_schedule());
                        labelScheduleDay3.setVisible(true);
                    case 2:
                        labelScheduleDay2.setText(" " + dataIn[1].getDay_schedule() + " " + dataIn[1].getTime_schedule());
                        labelScheduleDay2.setVisible(true);
                    case 1:
                        labelScheduleDay1.setText(" " + dataIn[0].getDay_schedule() + " " + dataIn[0].getTime_schedule());
                        labelScheduleDay1.setVisible(true);
                        labelScheduleDay1.setIcon(calendarIcon);
                        break;
                    default:
                        labelScheduleDay2.setVisible(false);
                        labelScheduleDay3.setVisible(false);
                }

            }

        } else {

            if (callingFromURL.equalsIgnoreCase(WebReference.LAST_HISTORY)) {
                hideHistoryData();
            } else if (callingFromURL.equalsIgnoreCase(WebReference.LAST_PAYMENT)) {
                hidePaymentData();
            } else if (callingFromURL.equalsIgnoreCase(WebReference.ALL_SCHEDULE)) {
                hideScheduleData();
            }

            System.out.println(callingFromURL + " catched");
        }

    }

    private void hideHistoryData() {
        labelHistoryLast1.setVisible(false);
    }

    private void hideScheduleData() {
        labelScheduleDay1.setVisible(false);
    }

    private void hidePaymentData() {
        labelLastPayment.setVisible(false);
    }

    public boolean isNotifHourBefore() {
        boolean stat = false;

        stat = configuration.getStringValue(Keys.NOTIF_CLASS_START).contains("hour");

        return stat;
    }

    public void playNotifSound() {
        AudioPlayer wmp = new AudioPlayer();
        wmp.play();
        UIEffect.popup("Class started now!", this);

    }

    public boolean isNotifDayBefore() {
        boolean stat = false;

        stat = configuration.getStringValue(Keys.NOTIF_CLASS_START).contains("day");

        return stat;
    }

    @Override
    public void actionYes() {
        // this is when popup say YES
        UIEffect.popup("yes we do!", this);
        CMDExecutor.killTeamviewer();
        downloadTools();

    }

    private void applyLanguageUI() {

        

        // front page @Home
        languageHelper.apply(buttonProfile, "buttonProfile", Comp.BUTTON);
        languageHelper.apply(buttonTools, "buttonTools", Comp.BUTTON);
        languageHelper.apply(buttonDocument, "buttonDocument", Comp.BUTTON);
        languageHelper.apply(buttonAttendance, "buttonAttendance", Comp.BUTTON);
        languageHelper.apply(buttonPayment, "buttonPayment", Comp.BUTTON);
        languageHelper.apply(buttonSettings, "buttonSettings", Comp.BUTTON);
        languageHelper.apply(buttonLogout, "buttonLogout", Comp.BUTTON);
        languageHelper.apply(labelPanelViewName, "labelPanelViewName", Comp.LABEL);
        languageHelper.apply(labelWelcomeUser, "labelWelcomeUser", Comp.LABEL);
        languageHelper.apply(labelReportBugs, "labelReportBugs", Comp.LABEL);
        languageHelper.apply(labelTotalSessionCompleted, "labelTotalSessionCompleted", Comp.LABEL);
        languageHelper.apply(labelClassRegistered, "labelClassRegistered", Comp.LABEL);
        languageHelper.apply(labelLastPayment, "labelLastPayment", Comp.LABEL);

        // inner page @Settings
        languageHelper.apply(buttonSaveSettings, "buttonSaveSettings", Comp.BUTTON);
        languageHelper.apply(labelSystemLanguagesSettings, "labelSystemLanguagesSettings", Comp.LABEL);
        languageHelper.apply(radioNotifSessionLessThan3Settings, "radioNotifSessionLessThan3Settings", Comp.RADIO_BUTTON);
        languageHelper.apply(radioNotifSessionAtLeast1Settings, "radioNotifSessionAtLeast1Settings", Comp.RADIO_BUTTON);
        languageHelper.apply(labelNotifSessionSettings, "labelNotifSessionSettings", Comp.LABEL);
        languageHelper.apply(radioNotifClass1DaySettings, "radioNotifClass1DaySettings", Comp.RADIO_BUTTON);
        languageHelper.apply(radioNotifClass1HourSettings, "radioNotifClass1HourSettings", Comp.RADIO_BUTTON);
        languageHelper.apply(labelNotifClassSettings, "labelNotifClassSettings", Comp.LABEL);
        languageHelper.apply(checkboxAutoupdateToolsSettings, "checkboxAutoupdateToolsSettings", Comp.CHECKBOX);
        
        // for animation time
        UIEffect.setLanguageHelper(languageHelper);
        
        // for @userprofilePage
        languageHelper.apply(labelTmvIDProfile, "labelTmvIDProfile", Comp.LABEL);
        languageHelper.apply(labelTmvPassProfile, "labelTmvPassProfile", Comp.LABEL);
        languageHelper.apply(labelWhatsappProfile, "labelWhatsappProfile", Comp.LABEL);
        languageHelper.apply(labelUsernameProfile, "labelUsernameProfile", Comp.LABEL);
        languageHelper.apply(labelPasswordProfile, "labelPasswordProfile", Comp.LABEL);
        languageHelper.apply(labelEmailProfile, "labelEmailProfile", Comp.LABEL);
        languageHelper.apply(labelAddressProfile, "labelAddressProfile", Comp.LABEL);
        languageHelper.apply(buttonSaveProfile, "buttonSaveProfile", Comp.BUTTON);
        
        // header page name
        languageHelper.apply(labelPanelViewName, "labelPanelViewName", Comp.LABEL);
        
        // button on Tools
        languageHelper.apply(buttonRunTmv, "buttonRunTmv", Comp.BUTTON);
        languageHelper.apply(buttonVisitChrome, "buttonVisitChrome", Comp.BUTTON);
        languageHelper.apply(buttonVisitWhatsapp, "buttonVisitWhatsapp", Comp.BUTTON);
        
        // page @paymentForm
        languageHelper.apply(labelPaymentForm, "labelPaymentForm", Comp.LABEL);
        languageHelper.apply(labelAmountPaymentForm, "labelAmountPaymentForm", Comp.LABEL);
        languageHelper.apply(labelMethodPaymentForm, "labelMethodPaymentForm", Comp.LABEL);
        languageHelper.apply(labelScreenshotPaymentForm, "labelScreenshotPaymentForm", Comp.LABEL);
        languageHelper.apply(labelBrowseScreenshotPaymentForm, "labelBrowseScreenshotPaymentForm", Comp.LABEL);
        languageHelper.apply(buttonSavePayment, "buttonSavePayment", Comp.BUTTON);
        languageHelper.apply(labelRefreshPayment, "labelRefreshPayment", Comp.LABEL);
        languageHelper.apply(labelAddPayment, "labelAddPayment", Comp.LABEL);
        
         // page @attendance
        languageHelper.apply(labelRefreshAttendance, "labelRefreshAttendance", Comp.LABEL);
        languageHelper.apply(labelShowAttendanceStatistic, "labelShowAttendanceStatistic", Comp.LABEL);
        languageHelper.apply(labelShowAttendanceData, "labelShowAttendanceData", Comp.LABEL);
        
        // page @document
        languageHelper.apply(labelRefreshDocument, "labelRefreshDocument", Comp.LABEL);
        languageHelper.apply(labelDownloadDocument, "labelDownloadDocument", Comp.LABEL);
        languageHelper.apply(labelOpenDocument, "labelOpenDocument", Comp.LABEL);
       
        // table for @payment
        // column order are:
        // id, username, amount, method, screenshot, date created
        
        languageHelper.getColumnTable("colUsernamePayment",
        "colAmountPayment",
        "colMethodPayment",
        "colScreenshotPayment",
        "colDateCreatedPayment");

    }

    @Override
    public void actionNo() {
        // this is when popup say NO
        UIEffect.popup("Download cancelled and Tools will be updated later...", this);
    }

}
