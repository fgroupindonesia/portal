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
import beans.User;
import com.google.gson.Gson;
import com.teamdev.jxbrowser.chromium.Browser;
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
import helper.TableRenderer;
import helper.preferences.SettingPreference;
import helper.UIDragger;
import helper.UIEffect;
import helper.WebReference;
import helper.preferences.Keys;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Frame;
import java.io.File;
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
public class ClientFrame extends javax.swing.JFrame implements HttpCall.HttpProcess {

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

    public void setUsername(String username) {
        labelWelcomeUser.setText("Hello " + username + "!");
    }

    public ClientFrame(LoginFrame logRef) {
        loginFrame = logRef;
        processNicely();
    }

    public ClientFrame() {
        processNicely();
    }

    private void processNicely() {
        initComponents();

        // activate the effect stuff
        UIDragger.setFrame(this);

        UIEffect.iconChanger(this);
        UIEffect.playTimeEffect(labelTime);

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
    SWThreadWorker workSched = new SWThreadWorker(this);
    SWThreadWorker workHist = new SWThreadWorker(this);
    SWThreadWorker workAtt = new SWThreadWorker(this);
    SWThreadWorker workDoc = new SWThreadWorker(this);
    SWThreadWorker workPay = new SWThreadWorker(this);
    SWThreadWorker workProfile = new SWThreadWorker(this);
    SWThreadWorker workPicture = new SWThreadWorker(this);

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

    private void refreshHistory() {

        workHist.setWork(SWTKey.WORK_REFRESH_HISTORY);
        workHist.addData("username", "asd");
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

        // execute the download picture process
        workPicture.setWork(SWTKey.WORK_REFRESH_PICTURE);
        workPicture.writeMode(true);
        workPicture.addData("propic", filename);

        // executorService.submit(workSched);
        executorService.schedule(workPicture, 2, TimeUnit.SECONDS);

    }

    private void refreshProfile() {

        workProfile.setWork(SWTKey.WORK_REFRESH_PROFILE);
        // executorService.submit(workSched);
        workProfile.addData("username", "asd");
        prepareToken(workProfile);

        executorService.schedule(workProfile, 3, TimeUnit.SECONDS);

    }

    private void refreshSchedule() {

        workSched.setWork(SWTKey.WORK_REFRESH_SCHEDULE);
        workSched.addData("username", "asd");
        prepareToken(workSched);

        //executorService.submit(workSched);
        executorService.schedule(workSched, 3, TimeUnit.SECONDS);

    }

    private void refreshAttendance() {

        workAtt.setWork(SWTKey.WORK_REFRESH_ATTENDANCE);
        workAtt.addData("username", "asd");
        prepareToken(workAtt);

        //executorService.submit(workAtt);
        executorService.schedule(workAtt, 2, TimeUnit.SECONDS);

    }

    private void refreshPayment() {

        workPay.setWork(SWTKey.WORK_REFRESH_PAYMENT);
        workPay.addData("username", "asd");
        prepareToken(workPay);
        //executorService.submit(workPay);
        executorService.schedule(workPay, 2, TimeUnit.SECONDS);

    }

    private void refreshDocument() {

        workDoc.setWork(SWTKey.WORK_REFRESH_DOCUMENT);
        workDoc.addData("username", "asd");
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

        CategoryDataset dataset = cmaker.createDataset();

        JFreeChart chart = cmaker.createChart(dataset);
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
        jLabel2 = new javax.swing.JLabel();
        textfieldPasswordProfile = new javax.swing.JPasswordField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        textfieldEmailProfile = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        textfieldWhatsappProfile = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        textareaAddressProfile = new javax.swing.JTextArea();
        buttonSaveProfile = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        textfieldTeamviewerID = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        textfieldTeamviewerPass = new javax.swing.JTextField();
        panelSettings = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        radioNotifClass1DaySetting = new javax.swing.JRadioButton();
        radioNotifClass1HourSetting = new javax.swing.JRadioButton();
        radioNotifSessionAtLeast1Setting = new javax.swing.JRadioButton();
        radioNotifSessionLessThan3Setting = new javax.swing.JRadioButton();
        jLabel10 = new javax.swing.JLabel();
        checkboxAutoupdateToolsSetting = new javax.swing.JCheckBox();
        buttonSaveSettings = new javax.swing.JButton();
        comboboxSystemLanguage = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
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
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        textfieldAmountPayment = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        combobocMethodPayment = new javax.swing.JComboBox<>();
        labelHidePaymentForm = new javax.swing.JLabel();
        labelScreenshotPayment = new javax.swing.JLabel();
        buttonSavePayment = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        panelPaymentData = new javax.swing.JPanel();
        panelPaymentTable = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablePaymentData = new javax.swing.JTable();
        panelControllerPayment = new javax.swing.JPanel();
        labelAddPayment = new javax.swing.JLabel();
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
        panelDocument = new javax.swing.JPanel();
        panelDocumentData = new javax.swing.JPanel();
        panelDocumentContent = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tableDocumentData = new javax.swing.JTable();
        panelControllerDocument = new javax.swing.JPanel();
        labelDocumentOpen = new javax.swing.JLabel();
        labelDocumentDownload = new javax.swing.JLabel();
        panelInnerBrowser = new javax.swing.JPanel();
        panelHeaderCenter = new javax.swing.JPanel();
        labelPanelViewName = new javax.swing.JLabel();
        labelNavHome = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

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
        panelHeader.add(labelClose, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 10, -1, 28));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Portal Access");
        panelHeader.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(18, 11, 208, -1));

        labelTime.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        labelTime.setForeground(new java.awt.Color(255, 255, 255));
        labelTime.setText("time is here");
        panelHeader.add(labelTime, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 20, 166, -1));

        labelMinimize.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/minimize.png"))); // NOI18N
        labelMinimize.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelMinimize.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelMinimizeMouseClicked(evt);
            }
        });
        panelHeader.add(labelMinimize, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 10, -1, 28));

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

        panelContent.add(panelMenu, java.awt.BorderLayout.WEST);

        panelCenter.setLayout(new java.awt.BorderLayout());

        panelContentCenter.setLayout(new java.awt.CardLayout());

        panelHome.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelSchedule.setBorder(javax.swing.BorderFactory.createTitledBorder("Schedule"));
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

        panelHome.add(panelSchedule, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 110, 190, 100));
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
        panelHome.add(labelLastPayment, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 240, 230, -1));

        panelHistory.setBorder(javax.swing.BorderFactory.createTitledBorder("History"));
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
        panelProfile.add(textfieldUsernameProfile, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 40, 220, -1));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lock16.png"))); // NOI18N
        jLabel2.setText("Password :");
        panelProfile.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 70, -1, -1));

        textfieldPasswordProfile.setText("jPasswordField1");
        panelProfile.add(textfieldPasswordProfile, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 90, 220, -1));

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/edit.png"))); // NOI18N
        jLabel4.setText("Username :");
        panelProfile.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 20, -1, -1));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/email.png"))); // NOI18N
        jLabel5.setText("Email : ");
        panelProfile.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 120, -1, -1));
        panelProfile.add(textfieldEmailProfile, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 140, 220, -1));

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/whatsapp.png"))); // NOI18N
        jLabel6.setText("Whatsapp Contact:");
        panelProfile.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 180, -1, -1));
        panelProfile.add(textfieldWhatsappProfile, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 200, 150, -1));

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/text.png"))); // NOI18N
        jLabel7.setText("Address:");
        panelProfile.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 180, -1, -1));

        textareaAddressProfile.setColumns(20);
        textareaAddressProfile.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        textareaAddressProfile.setRows(5);
        jScrollPane1.setViewportView(textareaAddressProfile);

        panelProfile.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 200, 220, 90));

        buttonSaveProfile.setText("Save");
        panelProfile.add(buttonSaveProfile, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 300, 80, 30));

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/teamviewer16.png"))); // NOI18N
        jLabel8.setText("TeamViewer ID");
        panelProfile.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 70, -1, -1));
        panelProfile.add(textfieldTeamviewerID, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 90, 150, -1));

        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/key.png"))); // NOI18N
        jLabel16.setText("TeamViewer Password");
        panelProfile.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 120, -1, -1));
        panelProfile.add(textfieldTeamviewerPass, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 140, 150, -1));

        panelContentCenter.add(panelProfile, "panelProfile");

        panelSettings.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setText("Notify when class started?");
        panelSettings.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(69, 58, 140, -1));

        radioButtonGroupNotifClass.add(radioNotifClass1DaySetting);
        radioNotifClass1DaySetting.setText("1 Day before");
        panelSettings.add(radioNotifClass1DaySetting, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 100, -1, -1));

        radioButtonGroupNotifClass.add(radioNotifClass1HourSetting);
        radioNotifClass1HourSetting.setText("1 Hour before");
        panelSettings.add(radioNotifClass1HourSetting, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 80, -1, -1));

        radioButtonGroupNotifSessionLimit.add(radioNotifSessionAtLeast1Setting);
        radioNotifSessionAtLeast1Setting.setText("At least 1");
        panelSettings.add(radioNotifSessionAtLeast1Setting, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 80, -1, -1));

        radioButtonGroupNotifSessionLimit.add(radioNotifSessionLessThan3Setting);
        radioNotifSessionLessThan3Setting.setText("Less than 3");
        panelSettings.add(radioNotifSessionLessThan3Setting, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 100, -1, -1));

        jLabel10.setText("Notify when sessions limit reach?");
        panelSettings.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 60, 160, -1));

        checkboxAutoupdateToolsSetting.setSelected(true);
        checkboxAutoupdateToolsSetting.setText("Autoupdate Tools");
        panelSettings.add(checkboxAutoupdateToolsSetting, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 160, -1, -1));

        buttonSaveSettings.setText("Save");
        buttonSaveSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveSettingsActionPerformed(evt);
            }
        });
        panelSettings.add(buttonSaveSettings, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 300, 80, 30));

        comboboxSystemLanguage.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "English (default)", "Bahasa Indonesia", "Arabic" }));
        panelSettings.add(comboboxSystemLanguage, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 180, 150, -1));

        jLabel9.setText("System Languages:");
        panelSettings.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 160, -1, -1));

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
        panelTools.add(buttonTerminateTmv, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 110, 110, -1));

        buttonRunTmv.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/play.png"))); // NOI18N
        buttonRunTmv.setText("Run Now");
        buttonRunTmv.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        buttonRunTmv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRunTmvActionPerformed(evt);
            }
        });
        panelTools.add(buttonRunTmv, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 70, 110, -1));

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
        panelTools.add(buttonVisitChrome, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 210, 110, -1));

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
        panelTools.add(buttonVisitWhatsapp, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 70, 110, -1));

        panelContentCenter.add(panelTools, "panelTools");

        panelPayment.setLayout(new java.awt.BorderLayout());

        panelPaymentForm.setPreferredSize(new java.awt.Dimension(200, 371));
        panelPaymentForm.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel12.setText("Payment Form");
        panelPaymentForm.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 31, 102, 30));

        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/calc.png"))); // NOI18N
        jLabel13.setText("Method:");
        panelPaymentForm.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, 120, -1));

        textfieldAmountPayment.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textfieldAmountPaymentFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                textfieldAmountPaymentFocusLost(evt);
            }
        });
        panelPaymentForm.add(textfieldAmountPayment, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, 160, -1));

        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/monitor.png"))); // NOI18N
        jLabel14.setText("Screenshot:");
        panelPaymentForm.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 190, 90, -1));

        combobocMethodPayment.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
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
        panelPaymentForm.add(buttonSavePayment, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 370, 70, -1));

        jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/coin.png"))); // NOI18N
        jLabel17.setText("Amount:");
        panelPaymentForm.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 120, -1));

        panelPayment.add(panelPaymentForm, java.awt.BorderLayout.WEST);

        panelPaymentData.setLayout(new java.awt.BorderLayout());

        panelPaymentTable.setLayout(new java.awt.BorderLayout());

        tablePaymentData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "[ x ]", "Date Created", "Amount", "Method"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, false
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
            tablePaymentData.getColumnModel().getColumn(1).setMinWidth(120);
            tablePaymentData.getColumnModel().getColumn(1).setPreferredWidth(120);
            tablePaymentData.getColumnModel().getColumn(2).setMinWidth(120);
            tablePaymentData.getColumnModel().getColumn(2).setPreferredWidth(120);
            tablePaymentData.getColumnModel().getColumn(3).setMinWidth(120);
            tablePaymentData.getColumnModel().getColumn(3).setPreferredWidth(120);
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
        panelControllerPayment.add(labelAddPayment, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, 70, 30));

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
            new String [] {
                "[ x ]", "Username", "Class", "Status", "Signature", "Date Created", "Date Modified"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                true, true, false, false, true, false, true
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
            tableAttendanceData.getColumnModel().getColumn(2).setMinWidth(100);
            tableAttendanceData.getColumnModel().getColumn(2).setPreferredWidth(100);
            tableAttendanceData.getColumnModel().getColumn(3).setMinWidth(75);
            tableAttendanceData.getColumnModel().getColumn(3).setPreferredWidth(75);
            tableAttendanceData.getColumnModel().getColumn(4).setMinWidth(0);
            tableAttendanceData.getColumnModel().getColumn(4).setPreferredWidth(0);
            tableAttendanceData.getColumnModel().getColumn(4).setMaxWidth(0);
            tableAttendanceData.getColumnModel().getColumn(5).setMinWidth(125);
            tableAttendanceData.getColumnModel().getColumn(5).setPreferredWidth(125);
            tableAttendanceData.getColumnModel().getColumn(6).setMinWidth(0);
            tableAttendanceData.getColumnModel().getColumn(6).setPreferredWidth(0);
            tableAttendanceData.getColumnModel().getColumn(6).setMaxWidth(0);
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
        panelControllerAttendance.add(labelShowAttendanceData, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 30, 80, 30));

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
        panelControllerAttendance.add(labelShowAttendanceStatistic, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, 80, 30));

        panelAttendanceData.add(panelControllerAttendance, java.awt.BorderLayout.PAGE_START);

        panelAttendance.add(panelAttendanceData, java.awt.BorderLayout.CENTER);

        panelContentCenter.add(panelAttendance, "panelAttendance");

        panelDocument.setLayout(new java.awt.BorderLayout());

        panelDocumentData.setLayout(new java.awt.BorderLayout());

        panelDocumentContent.setLayout(new java.awt.BorderLayout());

        tableDocumentData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "[ x ]", "Id", "Title", "Description", "Filename", "Username", "URL", "Date Created"
            }
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
        jScrollPane4.setViewportView(tableDocumentData);
        if (tableDocumentData.getColumnModel().getColumnCount() > 0) {
            tableDocumentData.getColumnModel().getColumn(0).setResizable(false);
            tableDocumentData.getColumnModel().getColumn(0).setPreferredWidth(35);
            tableDocumentData.getColumnModel().getColumn(1).setMinWidth(0);
            tableDocumentData.getColumnModel().getColumn(1).setPreferredWidth(0);
            tableDocumentData.getColumnModel().getColumn(1).setMaxWidth(0);
            tableDocumentData.getColumnModel().getColumn(2).setPreferredWidth(150);
            tableDocumentData.getColumnModel().getColumn(3).setPreferredWidth(500);
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

        labelDocumentOpen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/open.png"))); // NOI18N
        labelDocumentOpen.setText("Open");
        labelDocumentOpen.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelDocumentOpen.setEnabled(false);
        labelDocumentOpen.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelDocumentOpenMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                labelDocumentOpenMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                labelDocumentOpenMouseExited(evt);
            }
        });
        panelControllerDocument.add(labelDocumentOpen, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 30, 80, 30));

        labelDocumentDownload.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/download.png"))); // NOI18N
        labelDocumentDownload.setText("Download");
        labelDocumentDownload.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelDocumentDownload.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelDocumentDownloadMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                labelDocumentDownloadMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                labelDocumentDownloadMouseExited(evt);
            }
        });
        panelControllerDocument.add(labelDocumentDownload, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, 80, 30));

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
                .addContainerGap(932, Short.MAX_VALUE))
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
        UIEffect.stopTimeEffect();
        logout();
    }//GEN-LAST:event_labelCloseMouseClicked

    private void panelBaseMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelBaseMousePressed
        UIDragger.mousePressed(evt);
    }//GEN-LAST:event_panelBaseMousePressed

    private void panelBaseMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelBaseMouseDragged
        UIDragger.mouseDragged(evt);
    }//GEN-LAST:event_panelBaseMouseDragged

    private void buttonProfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonProfileActionPerformed
        labelPanelViewName.setText("<< Profile");
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
        labelPanelViewName.setText("<< Tools");
        cardLayouterMain.show(panelContentCenter, "panelTools");
    }//GEN-LAST:event_buttonToolsActionPerformed

    private void buttonDocumentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDocumentActionPerformed
        labelPanelViewName.setText("<< Document");
        cardLayouterMain.show(panelContentCenter, "panelDocument");
    }//GEN-LAST:event_buttonDocumentActionPerformed

    private void buttonAttendanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAttendanceActionPerformed
        labelPanelViewName.setText("<< Attendance");
        cardLayouterMain.show(panelContentCenter, "panelAttendance");
    }//GEN-LAST:event_buttonAttendanceActionPerformed

    private void buttonPaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonPaymentActionPerformed
        labelPanelViewName.setText("<< Payment");
        cardLayouterMain.show(panelContentCenter, "panelPayment");

    }//GEN-LAST:event_buttonPaymentActionPerformed

    private void buttonLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonLogoutActionPerformed
        logout();
    }//GEN-LAST:event_buttonLogoutActionPerformed

    private void buttonSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSettingsActionPerformed
        labelPanelViewName.setText("<< Settings");
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
            File source = fileChooser.getSelectedFile();

            //System.out.println(source.getName());
            PathReference.setPropicFileName(source.getName());
            File dest = new File(PathReference.UserPropicPath);

            try {
                FileCopier.copyTo(source, dest);
                UIEffect.iconChanger(labelPropicUser, dest.getAbsolutePath());

                // store the settings for next time usage
                configuration.setValue(Keys.USER_PROPIC, dest.getAbsolutePath());
                System.out.println("now is " + dest.getAbsolutePath());
            } catch (Exception ex) {

            }

        }
    }//GEN-LAST:event_labelPropicUserMouseClicked

    private void labelNavHomeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelNavHomeMouseClicked
        labelPanelViewName.setText("Home");
        cardLayouterMain.show(panelContentCenter, "panelHome");
    }//GEN-LAST:event_labelNavHomeMouseClicked

    private void buttonSaveSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveSettingsActionPerformed
        configuration.setValue(Keys.AUTO_UPDATE_TOOLS, checkboxAutoupdateToolsSetting.isSelected());

        String notifClass = radioButtonGroupNotifClass.getSelection().getActionCommand();
        configuration.setValue(Keys.NOTIF_CLASS_START, notifClass);

        String notifSession = radioButtonGroupNotifSessionLimit.getSelection().getActionCommand();
        configuration.setValue(Keys.NOTIF_SESSION_LIMIT, notifSession);

        configuration.setValue(Keys.SYSTEM_LANGUAGE, comboboxSystemLanguage.getSelectedItem().toString());

    }//GEN-LAST:event_buttonSaveSettingsActionPerformed

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

        postDataPayment();

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

    private void labelDocumentOpenMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelDocumentOpenMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_labelDocumentOpenMouseClicked

    private void labelDocumentOpenMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelDocumentOpenMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_labelDocumentOpenMouseEntered

    private void labelDocumentOpenMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelDocumentOpenMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_labelDocumentOpenMouseExited

    private void labelDocumentDownloadMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelDocumentDownloadMouseClicked

        cardLayouterMain.show(panelContentCenter, "panelInnerBrowser");
    }//GEN-LAST:event_labelDocumentDownloadMouseClicked

    private void postDataPayment() {

        RupiahGenerator rp = new RupiahGenerator();

        Payment data = new Payment();
        data.setAmount(ABORT);

    }


    private void labelDocumentDownloadMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelDocumentDownloadMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_labelDocumentDownloadMouseEntered

    private void labelDocumentDownloadMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelDocumentDownloadMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_labelDocumentDownloadMouseExited

    private void labelReportBugsMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelReportBugsMouseEntered
        UIEffect.focusGained(labelReportBugs);
    }//GEN-LAST:event_labelReportBugsMouseEntered

    private void labelReportBugsMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelReportBugsMouseExited
        UIEffect.focusLost(labelReportBugs);
    }//GEN-LAST:event_labelReportBugsMouseExited

    private void labelMinimizeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelMinimizeMouseClicked
        this.setState(Frame.ICONIFIED);
    }//GEN-LAST:event_labelMinimizeMouseClicked

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

    private void loadUserPictureLocally() {

        String propic = configuration.getStringValue(Keys.USER_PROPIC);

        System.out.println("Trying to load " + propic);

        if (!propic.contains("default")) {
            // set the propic
            UIEffect.iconChanger(labelPropicUser, (propic));
        }

    }

    // for settings ui
    private void loadConfiguration() {
        loadUserPictureLocally();
        //System.out.println(propic);

        checkboxAutoupdateToolsSetting.setSelected(configuration.getBooleanValue(Keys.AUTO_UPDATE_TOOLS));
        comboboxSystemLanguage.setSelectedItem(configuration.getStringValue(Keys.SYSTEM_LANGUAGE));

        if (configuration.getStringValue(Keys.NOTIF_CLASS_START).equalsIgnoreCase(Keys.DEFAULT_NOTIF_CLASS_START)) {
            radioNotifClass1HourSetting.setSelected(true);
        } else {
            radioNotifClass1HourSetting.setSelected(false);
        }

        if (configuration.getStringValue(Keys.NOTIF_SESSION_LIMIT).equalsIgnoreCase(Keys.DEFAULT_NOTIF_SESSION_LIMIT)) {
            radioNotifSessionAtLeast1Setting.setSelected(true);
        } else {
            radioNotifSessionAtLeast1Setting.setSelected(false);
        }

    }

    private void logout() {

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
    private javax.swing.JCheckBox checkboxAutoupdateToolsSetting;
    private javax.swing.JComboBox<String> combobocMethodPayment;
    private javax.swing.JComboBox<String> comboboxSystemLanguage;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel labelAddPayment;
    private javax.swing.JLabel labelClassRegistered;
    private javax.swing.JLabel labelClose;
    private javax.swing.JLabel labelDocumentDownload;
    private javax.swing.JLabel labelDocumentOpen;
    private javax.swing.JLabel labelHidePaymentForm;
    private javax.swing.JLabel labelHistoryLast1;
    private javax.swing.JLabel labelHistoryLast2;
    private javax.swing.JLabel labelHistoryLast3;
    private javax.swing.JLabel labelHistoryLast4;
    private javax.swing.JLabel labelHistoryLast5;
    private javax.swing.JLabel labelLastPayment;
    private javax.swing.JLabel labelMinimize;
    private javax.swing.JLabel labelNavHome;
    private javax.swing.JLabel labelPanelViewName;
    private javax.swing.JLabel labelPropicUser;
    private javax.swing.JLabel labelReportBugs;
    private javax.swing.JLabel labelScheduleDay1;
    private javax.swing.JLabel labelScheduleDay2;
    private javax.swing.JLabel labelScheduleDay3;
    private javax.swing.JLabel labelScreenshotPayment;
    private javax.swing.JLabel labelShowAttendanceData;
    private javax.swing.JLabel labelShowAttendanceStatistic;
    private javax.swing.JLabel labelTime;
    private javax.swing.JLabel labelTotalSessionCompleted;
    private javax.swing.JLabel labelWelcomeUser;
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
    private javax.swing.JProgressBar progressBarTotalSession;
    private javax.swing.ButtonGroup radioButtonGroupNotifClass;
    private javax.swing.ButtonGroup radioButtonGroupNotifSessionLimit;
    private javax.swing.JRadioButton radioNotifClass1DaySetting;
    private javax.swing.JRadioButton radioNotifClass1HourSetting;
    private javax.swing.JRadioButton radioNotifSessionAtLeast1Setting;
    private javax.swing.JRadioButton radioNotifSessionLessThan3Setting;
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

    @Override
    public void checkResponse(String resp, String callingFromURL) {
        Gson objectG = new Gson();

        System.out.println(callingFromURL + " have " + resp);
        JSONChecker jchecker = new JSONChecker();

        if (jchecker.isValid(resp)) {

            boolean b = callingFromURL.contains(WebReference.PICTURE_USER);

            ImageIcon okIcon = new ImageIcon(getClass().getResource("/images/ok16.png"));
            ImageIcon coinIcon = new ImageIcon(getClass().getResource("/images/coin.png"));

            String innerData = jchecker.getValueAsString("multi_data");

            //System.out.println(callingFromURL + " is valid " + b);
            if (callingFromURL.equalsIgnoreCase(WebReference.ALL_DOCUMENT)) {
                Document[] dataIn = objectG.fromJson(innerData, Document[].class);
                tabRender.render(tableDocumentData, dataIn);
            } else if (callingFromURL.equalsIgnoreCase(WebReference.ALL_ATTENDANCE)) {
                Attendance[] dataIn = objectG.fromJson(innerData, Attendance[].class);
                tabRender.render(tableAttendanceData, dataIn);
            } else if (callingFromURL.contains(WebReference.PICTURE_USER)) {

                System.out.println("Obtaining Picture from web is success...\nNow applying it locally.");
                loadUserPictureLocally();

            } else if (callingFromURL.equalsIgnoreCase(WebReference.ALL_PAYMENT)) {
                Payment[] dataIn = objectG.fromJson(innerData, Payment[].class);
                tabRender.render(tablePaymentData, dataIn);

                labelLastPayment.setText("Last Payment : " + dataIn[dataIn.length - 1].getDate_created());
                labelLastPayment.setIcon(coinIcon);

            } else if (callingFromURL.equalsIgnoreCase(WebReference.PROFILE_USER)) {
                User dataIn = objectG.fromJson(innerData, User.class);
                textfieldUsernameProfile.setText(dataIn.getUsername());
                textfieldPasswordProfile.setText(dataIn.getPass());
                textfieldEmailProfile.setText(dataIn.getEmail());
                textfieldWhatsappProfile.setText(dataIn.getMobile());
                textareaAddressProfile.setText(dataIn.getAddress());

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
            System.out.println(callingFromURL + " catched");
        }

    }

}
