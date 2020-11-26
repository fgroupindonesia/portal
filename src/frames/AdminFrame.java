/*
 *  This is a Supplemental File from the Main Project used
 *  in Java Programming Core Fundamental II
 *  with FGroupIndonesia team.
 */
package frames;

import beans.Attendance;
import beans.ClassRoom;
import beans.Document;
import beans.Schedule;
import beans.User;
import com.google.gson.Gson;
import helper.HttpCall;
import helper.JSONChecker;
import helper.PathReference;
import helper.RupiahGenerator;
import helper.SWTKey;
import helper.SWThreadWorker;
import helper.TableRenderer;
import helper.UIDragger;
import helper.UIEffect;
import helper.WebReference;
import helper.preferences.Keys;
import helper.preferences.SettingPreference;
import java.awt.CardLayout;
import java.awt.Frame;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JList;

/**
 *
 * @author ASUS
 */
public class AdminFrame extends javax.swing.JFrame implements HttpCall.HttpProcess {

    File propicFile, docFile, signatureFile, payFile;
    short idForm;
    TableRenderer tabRender = new TableRenderer();
    LoginFrame loginFrame;
    CardLayout cardLayoutInnerCenter;
    CardLayout cardLayoutEntity;
    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(3);
    SettingPreference configuration = new SettingPreference();
    ImageIcon loadingImage = new ImageIcon(getClass().getResource("/images/loadingprel.gif"));
    ImageIcon errorImage = new ImageIcon(getClass().getResource("/images/terminate.png"));
    ImageIcon refreshImage = new ImageIcon(getClass().getResource("/images/refresh16.png"));

// for every entity form has this edit mode
    boolean editMode;

    /**
     * Creates new form MainAdminFrame
     */
    public AdminFrame() {
        initComponents();
    }

    public AdminFrame(LoginFrame lg) {
        initComponents();

        loginFrame = lg;
        cardLayoutInnerCenter = (CardLayout) panelInnerCenter.getLayout();

        // request to API for rendering the data to table
        refreshUser();
        refreshDocument();
        refreshSchedule();
        refreshClassRoom();
        refreshAttendance();

        // hide the home link
        labelBackToHome.setVisible(false);
        labelLoadingStatus.setVisible(false);

        // hide visibility of padding jlabel
        labelBottomPadding.setVisible(false);
        labelRightPadding.setVisible(false);

        // activate the effect stuff
        // show the timer effect
        UIEffect.iconChanger(this);
        UIEffect.playTimeEffect(labelTime);

        UIDragger.setFrame(this);
    }

    private String getTokenLocally() {
        return configuration.getStringValue(Keys.TOKEN_API);
    }

    private void prepareToken(SWThreadWorker obSW) {
        System.out.println("adding token for " + obSW.whatWorkAsString() + " " + getTokenLocally());
        obSW.addData("token", getTokenLocally());
    }

    private void renderUserForm(User dataCome) {

        idForm = (short) dataCome.getId();

        textareaAddress.setText(UIEffect.decodeSafe(dataCome.getAddress()));
        textfieldEmail.setText(UIEffect.decodeSafe(dataCome.getEmail()));
        textfieldMobile.setText(dataCome.getMobile());
        textfieldPass.setText(UIEffect.decodeSafe(dataCome.getPass()));
        textfieldUsername.setText(dataCome.getUsername());

        if (!dataCome.getPropic().equalsIgnoreCase("default.png")) {
            // we are required to download the image from server
            refreshUserPicture(dataCome.getPropic());
        }

    }

    private void renderAttendanceForm(Attendance dataCome) {

        idForm = (short) dataCome.getId();

        comboboxClassRegAttendance.setSelectedItem(UIEffect.decodeSafe(dataCome.getClass_registered()));
        comboboxUsernameAttendance.setSelectedItem(dataCome.getUsername());
        comboboxStatusAttendance.setSelectedItem(dataCome.getStatus());

        if (!dataCome.getSignature().equalsIgnoreCase("not available")) {
            // we are required to download the image from server
            refreshSignaturePicture(dataCome.getSignature());
        }

    }

    private void renderDocumentForm(Document dataCome) {

        idForm = (short) dataCome.getId();

        textareaDescriptionDoc.setText(UIEffect.decodeSafe(dataCome.getDescription()));
        textfieldFilenameDoc.setText(UIEffect.decodeSafe(dataCome.getFilename()));
        textfieldUrlDoc.setText(dataCome.getUrl());
        textfieldTitleDoc.setText(UIEffect.decodeSafe(dataCome.getTitle()));
        comboboxUsernameDoc.setSelectedItem(dataCome.getUsername());

        lockDocumentForm(false);
        labelLoadingStatus.setVisible(false);

    }

    private void renderScheduleForm(Schedule dataCome) {

        idForm = (short) dataCome.getId();

        comboboxUsernameSched.setSelectedItem(dataCome.getUsername());
        comboboxClassRegSched.setSelectedItem(dataCome.getClass_registered());
        comboboxDaySched.setSelectedItem(dataCome.getDay_schedule());

        String time = dataCome.getTime_schedule();
        String timeData[] = time.split(":");

        int jam = Integer.parseInt(timeData[0]);
        int menit = Integer.parseInt(timeData[1]);

        spinnerHourSched.setValue(jam);
        spinnerMinutesSched.setValue(menit);

        lockScheduleForm(false);
        labelLoadingStatus.setVisible(false);

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

    private void refreshSignaturePicture(String filename) {

        // set the path temporarily 
        // for later usage in locally
        PathReference.setSignatureFileName(filename);
        File dest = new File(PathReference.SignaturePath);

        configuration.setValue(Keys.SIGNATURE_ATTENDANCE, dest.getAbsolutePath());

        SWThreadWorker workSignature = new SWThreadWorker(this);

        // execute the download picture process
        workSignature.setWork(SWTKey.WORK_REFRESH_SIGNATURE);
        workSignature.writeMode(true);
        workSignature.addData("signature", filename);

        // executorService.submit(workSched);
        executorService.schedule(workSignature, 2, TimeUnit.SECONDS);

    }

    private void refreshUser() {

        SWThreadWorker workUser = new SWThreadWorker(this);
        workUser.setWork(SWTKey.WORK_REFRESH_USER);
        prepareToken(workUser);
        executorService.schedule(workUser, 2, TimeUnit.SECONDS);

    }

    private void refreshAttendance() {

        SWThreadWorker workAttendance = new SWThreadWorker(this);
        workAttendance.setWork(SWTKey.WORK_REFRESH_ATTENDANCE);
        workAttendance.addData("username", "admin");

        prepareToken(workAttendance);
        executorService.schedule(workAttendance, 2, TimeUnit.SECONDS);

    }

    private void refreshScheduleByDay(String dayName) {

        SWThreadWorker workSchedule = new SWThreadWorker(this);
        workSchedule.setWork(SWTKey.WORK_REFRESH_SCHEDULE_BY_DAY);
        workSchedule.addData("day_schedule", dayName);
        prepareToken(workSchedule);
        executorService.schedule(workSchedule, 2, TimeUnit.SECONDS);

    }

    private void refreshDocument() {

        SWThreadWorker workDoc = new SWThreadWorker(this);
        workDoc.setWork(SWTKey.WORK_REFRESH_DOCUMENT);
        workDoc.addData("username", "admin");
        prepareToken(workDoc);
        executorService.schedule(workDoc, 2, TimeUnit.SECONDS);

    }

    private void refreshPayment() {

        SWThreadWorker workPay = new SWThreadWorker(this);
        workPay.setWork(SWTKey.WORK_REFRESH_PAYMENT);
        workPay.addData("username", "admin");
        prepareToken(workPay);
        executorService.schedule(workPay, 2, TimeUnit.SECONDS);

    }

    private void refreshSchedule() {

        SWThreadWorker workSched = new SWThreadWorker(this);
        workSched.setWork(SWTKey.WORK_REFRESH_SCHEDULE);
        workSched.addData("username", "admin");
        prepareToken(workSched);
        executorService.schedule(workSched, 2, TimeUnit.SECONDS);

    }

    private void refreshClassRoom() {

        SWThreadWorker workClassRoom = new SWThreadWorker(this);
        workClassRoom.setWork(SWTKey.WORK_REFRESH_CLASSROOM);
        prepareToken(workClassRoom);
        executorService.schedule(workClassRoom, 2, TimeUnit.SECONDS);

    }

    private void getUserProfile(String usernameIn) {

        SWThreadWorker workUser = new SWThreadWorker(this);
        workUser.setWork(SWTKey.WORK_REFRESH_PROFILE);
        workUser.addData("username", usernameIn);
        prepareToken(workUser);
        executorService.schedule(workUser, 2, TimeUnit.SECONDS);

    }

    private void getAttendance(int idIn) {

        SWThreadWorker workAttendance = new SWThreadWorker(this);
        workAttendance.setWork(SWTKey.WORK_ATTENDANCE_EDIT);
        workAttendance.addData("id", idIn + "");
        prepareToken(workAttendance);
        executorService.schedule(workAttendance, 2, TimeUnit.SECONDS);

    }

    private void getDocument(int anID) {

        SWThreadWorker workDoc = new SWThreadWorker(this);
        workDoc.setWork(SWTKey.WORK_DOCUMENT_EDIT);
        workDoc.addData("id", anID + "");
        prepareToken(workDoc);
        executorService.schedule(workDoc, 2, TimeUnit.SECONDS);

    }

    private void getPayment(int anID) {

        SWThreadWorker workPay = new SWThreadWorker(this);
        workPay.setWork(SWTKey.WORK_PAYMENT_EDIT);
        workPay.addData("id", anID + "");
        prepareToken(workPay);
        executorService.schedule(workPay, 2, TimeUnit.SECONDS);

    }

    private void getSchedule(int anID) {

        SWThreadWorker workSched = new SWThreadWorker(this);
        workSched.setWork(SWTKey.WORK_SCHEDULE_EDIT);
        workSched.addData("id", anID + "");
        prepareToken(workSched);
        executorService.schedule(workSched, 1, TimeUnit.SECONDS);

    }

    private void saveDocument() {
        showLoadingStatus();
        SWThreadWorker workDocumentEntity = new SWThreadWorker(this);

        // check whether this is edit or new form?
        if (editMode) {
            // updating data
            workDocumentEntity.setWork(SWTKey.WORK_DOCUMENT_UPDATE);
            workDocumentEntity.addData("id", idForm + "");
        } else {
            // saving new data
            workDocumentEntity.setWork(SWTKey.WORK_DOCUMENT_SAVE);
        }

        workDocumentEntity.addData("title", textfieldTitleDoc.getText());
        workDocumentEntity.addData("description", textareaDescriptionDoc.getText());
        workDocumentEntity.addData("username", comboboxUsernameDoc.getSelectedItem().toString());
        workDocumentEntity.addData("url", textfieldUrlDoc.getText());

        if (docFile != null) {
            workDocumentEntity.addFile("document", docFile);
        }

        prepareToken(workDocumentEntity);
        executorService.schedule(workDocumentEntity, 2, TimeUnit.SECONDS);

    }

    private void savePayment() {

        showLoadingStatus();
        SWThreadWorker workPaymentEntity = new SWThreadWorker(this);

        // check whether this is edit or new form?
        if (editMode) {
            // updating data
            workPaymentEntity.setWork(SWTKey.WORK_PAYMENT_UPDATE);
            workPaymentEntity.addData("id", idForm + "");
        } else {
            // saving new data
            workPaymentEntity.setWork(SWTKey.WORK_PAYMENT_SAVE);
        }

        workPaymentEntity.addData("amount", textfieldAmountPayment.getText());
        workPaymentEntity.addData("method", comboboxMethodPayment.getSelectedItem().toString());
        workPaymentEntity.addData("username", comboboxUsernameDoc.getSelectedItem().toString());

        if (payFile != null) {
            workPaymentEntity.addFile("document", payFile);
        }

        prepareToken(workPaymentEntity);
        executorService.schedule(workPaymentEntity, 2, TimeUnit.SECONDS);

    }

    private void saveAttendance() {

        showLoadingStatus();

        SWThreadWorker workAttendanceEntity = new SWThreadWorker(this);

        // check whether this is edit or new form?
        if (editMode) {
            // updating data
            workAttendanceEntity.setWork(SWTKey.WORK_ATTENDANCE_UPDATE);
            workAttendanceEntity.addData("id", idForm + "");
        } else {
            // saving new data
            workAttendanceEntity.setWork(SWTKey.WORK_ATTENDANCE_SAVE);
        }

        System.out.println("Adding a data before executing API Request...");

        workAttendanceEntity.addData("username", comboboxUsernameAttendance.getSelectedItem().toString());
        workAttendanceEntity.addData("class_registered", comboboxClassRegAttendance.getSelectedItem().toString());
        workAttendanceEntity.addData("status", comboboxStatusAttendance.getSelectedItem().toString());

        if (signatureFile != null) {
            workAttendanceEntity.addFile("signature", signatureFile);
        }

        prepareToken(workAttendanceEntity);
        executorService.schedule(workAttendanceEntity, 2, TimeUnit.SECONDS);

    }

    private void saveUser() {
        labelLoadingStatus.setVisible(true);
        SWThreadWorker workUserEntity = new SWThreadWorker(this);

        // check whether this is edit or new form?
        if (editMode) {
            // updating data
            workUserEntity.setWork(SWTKey.WORK_USER_UPDATE);
            workUserEntity.addData("id", idForm + "");
        } else {
            // saving new data
            workUserEntity.setWork(SWTKey.WORK_USER_SAVE);
        }

        workUserEntity.addData("username", textfieldUsername.getText());
        workUserEntity.addData("password", textfieldPass.getText());
        workUserEntity.addData("email", textfieldEmail.getText());
        workUserEntity.addData("address", textareaAddress.getText());
        workUserEntity.addData("mobile", textfieldMobile.getText());

        // for propic we will post the data here
        if (propicFile != null) {
            workUserEntity.addFile("propic", propicFile);
        }

        prepareToken(workUserEntity);
        executorService.schedule(workUserEntity, 2, TimeUnit.SECONDS);

    }

    private String getDigit(Object val, int manyNum) {

        Integer nilai = Integer.parseInt(val.toString());
        String hasil = null;
        if (nilai < 10 && manyNum == 2) {
            hasil = "0" + nilai;
        } else if (nilai < 100 && manyNum == 2) {
            hasil = "" + nilai;
        } else if (nilai < 10 && manyNum == 3) {
            hasil = "00" + nilai;
        } else if (nilai < 100 && manyNum == 3) {
            hasil = "0" + nilai;
        }

        return hasil;

    }

    private void saveSchedule() {

        labelLoadingStatus.setVisible(true);

        SWThreadWorker workScheduleEntity = new SWThreadWorker(this);

        // check whether this is edit or new form?
        if (editMode) {
            // updating data
            workScheduleEntity.setWork(SWTKey.WORK_SCHEDULE_UPDATE);
            workScheduleEntity.addData("id", idForm + "");
        } else {
            // saving new data
            workScheduleEntity.setWork(SWTKey.WORK_SCHEDULE_SAVE);
        }

        String timeSched = getDigit(spinnerHourSched.getValue(), 2) + ":" + getDigit(spinnerMinutesSched.getValue(), 2);

        workScheduleEntity.addData("username", comboboxUsernameSched.getSelectedItem().toString());
        workScheduleEntity.addData("day_schedule", comboboxDaySched.getSelectedItem().toString());
        workScheduleEntity.addData("time_schedule", timeSched);
        workScheduleEntity.addData("class_registered", comboboxClassRegSched.getSelectedItem().toString());

        prepareToken(workScheduleEntity);
        executorService.schedule(workScheduleEntity, 2, TimeUnit.SECONDS);

    }

    private void deleteUser(ArrayList<String> dataIn) {

        for (String d : dataIn) {
            SWThreadWorker workUser = new SWThreadWorker(this);
            workUser.addData("username", d);
            workUser.setWork(SWTKey.WORK_USER_DELETE);
            prepareToken(workUser);
            executorService.schedule(workUser, 1, TimeUnit.SECONDS);
        }

    }

    private void deleteAttendance(ArrayList<String> dataIn) {

        // for document usage the d is actually a number (Integer)
        for (String d : dataIn) {
            SWThreadWorker workAttendance = new SWThreadWorker(this);
            workAttendance.addData("id", d);
            workAttendance.setWork(SWTKey.WORK_ATTENDANCE_DELETE);
            prepareToken(workAttendance);
            executorService.schedule(workAttendance, 1, TimeUnit.SECONDS);
        }

    }

    private void deletePayment(ArrayList<String> dataIn) {

        // for payment usage the d is actually a number (Integer)
        for (String d : dataIn) {
            SWThreadWorker workPay = new SWThreadWorker(this);
            workPay.addData("id", d);
            workPay.setWork(SWTKey.WORK_PAYMENT_DELETE);
            prepareToken(workPay);
            executorService.schedule(workPay, 1, TimeUnit.SECONDS);
        }

    }

    private void deleteDocument(ArrayList<String> dataIn) {

        // for document usage the d is actually a number (Integer)
        for (String d : dataIn) {
            SWThreadWorker workDoc = new SWThreadWorker(this);
            workDoc.addData("id", d);
            workDoc.setWork(SWTKey.WORK_DOCUMENT_DELETE);
            prepareToken(workDoc);
            executorService.schedule(workDoc, 1, TimeUnit.SECONDS);
        }

    }

    private void deleteSchedule(ArrayList<String> dataIn) {

        // for schedule usage the d is actually a number (Integer)
        for (String d : dataIn) {
            SWThreadWorker workSched = new SWThreadWorker(this);
            workSched.addData("id", d);
            workSched.setWork(SWTKey.WORK_SCHEDULE_DELETE);
            prepareToken(workSched);
            executorService.schedule(workSched, 1, TimeUnit.SECONDS);
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileChooser = new javax.swing.JFileChooser();
        panelHeader = new javax.swing.JPanel();
        labelClose = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        labelTime = new javax.swing.JLabel();
        labelMinimize = new javax.swing.JLabel();
        panelCenter = new javax.swing.JPanel();
        panelInnerCenter = new javax.swing.JPanel();
        panelHome = new javax.swing.JPanel();
        buttonUserManagement = new javax.swing.JButton();
        buttonDocumentManagement = new javax.swing.JButton();
        buttonAttendance = new javax.swing.JButton();
        buttonPayment = new javax.swing.JButton();
        buttonSchedule = new javax.swing.JButton();
        buttonSettings = new javax.swing.JButton();
        buttonFuture2 = new javax.swing.JButton();
        buttonFuture3 = new javax.swing.JButton();
        panelUser = new javax.swing.JPanel();
        panelUserManagement = new javax.swing.JPanel();
        panelUserControl = new javax.swing.JPanel();
        buttonAddUser = new javax.swing.JButton();
        buttonEditUser = new javax.swing.JButton();
        buttonDeleteUser = new javax.swing.JButton();
        labelUserManagement = new javax.swing.JLabel();
        labelRefreshUser = new javax.swing.JLabel();
        panelUserTable = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableUserData = new javax.swing.JTable();
        panelUserForm = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        buttonCancelUserForm = new javax.swing.JButton();
        textfieldUsername = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        textfieldPass = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        textfieldEmail = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        textareaAddress = new javax.swing.JTextArea();
        textfieldMobile = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        labelPreviewPicture = new javax.swing.JLabel();
        labelLinkChangePicture = new javax.swing.JLabel();
        buttonSaveUserForm = new javax.swing.JButton();
        panelDocument = new javax.swing.JPanel();
        panelDocumentManagement = new javax.swing.JPanel();
        panelDocumentControl = new javax.swing.JPanel();
        buttonAddDocument = new javax.swing.JButton();
        buttonEditDocument = new javax.swing.JButton();
        buttonDeleteDocument = new javax.swing.JButton();
        labelRefreshDocument = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        panelDocumentTable = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tableDocumentData = new javax.swing.JTable();
        panelDocumentForm = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        buttonCancelDocumentForm = new javax.swing.JButton();
        textfieldTitleDoc = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        textfieldFilenameDoc = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        textareaDescriptionDoc = new javax.swing.JTextArea();
        textfieldUrlDoc = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        labelLinkChangeFileDoc = new javax.swing.JLabel();
        buttonSaveDocumentForm = new javax.swing.JButton();
        comboboxUsernameDoc = new javax.swing.JComboBox<>();
        panelSchedule = new javax.swing.JPanel();
        panelScheduleManagement = new javax.swing.JPanel();
        panelScheduleControl = new javax.swing.JPanel();
        buttonAddSchedule = new javax.swing.JButton();
        buttonEditSchedule = new javax.swing.JButton();
        buttonDeleteSchedule = new javax.swing.JButton();
        labelScheduleManagement = new javax.swing.JLabel();
        labelRefreshSchedule = new javax.swing.JLabel();
        panelScheduleTable = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tableScheduleData = new javax.swing.JTable();
        panelScheduleForm = new javax.swing.JPanel();
        buttonCancelScheduleForm = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        buttonSaveScheduleForm = new javax.swing.JButton();
        spinnerMinutesSched = new javax.swing.JSpinner();
        spinnerHourSched = new javax.swing.JSpinner();
        comboboxClassRegSched = new javax.swing.JComboBox<>();
        comboboxUsernameSched = new javax.swing.JComboBox<>();
        comboboxDaySched = new javax.swing.JComboBox<>();
        jLabel18 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        listAnotherClassSched = new javax.swing.JList<>();
        panelAttendance = new javax.swing.JPanel();
        panelAttendanceManagement = new javax.swing.JPanel();
        panelAttendanceControl = new javax.swing.JPanel();
        buttonAddAttendance = new javax.swing.JButton();
        buttonEditAttendance = new javax.swing.JButton();
        buttonDeleteAttendance = new javax.swing.JButton();
        labelAttendanceManagement = new javax.swing.JLabel();
        labelRefreshAttendance = new javax.swing.JLabel();
        panelAttendanceTable = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        tableAttendanceData = new javax.swing.JTable();
        panelAttendanceForm = new javax.swing.JPanel();
        buttonCancelAttendanceForm = new javax.swing.JButton();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        buttonSaveAttendanceForm = new javax.swing.JButton();
        comboboxClassRegAttendance = new javax.swing.JComboBox<>();
        comboboxUsernameAttendance = new javax.swing.JComboBox<>();
        comboboxStatusAttendance = new javax.swing.JComboBox<>();
        jLabel24 = new javax.swing.JLabel();
        labelBrowseSignatureAttendance = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        labelSignatureAttendance = new javax.swing.JLabel();
        panelPayment = new javax.swing.JPanel();
        panelPaymentManagement = new javax.swing.JPanel();
        panelPaymentControl = new javax.swing.JPanel();
        buttonAddPayment = new javax.swing.JButton();
        buttonEditPayment = new javax.swing.JButton();
        buttonDeletePayment = new javax.swing.JButton();
        labelAttendanceManagement1 = new javax.swing.JLabel();
        labelRefreshPayment = new javax.swing.JLabel();
        panelPaymentTable = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        tablePaymentData = new javax.swing.JTable();
        panelPaymentForm = new javax.swing.JPanel();
        buttonCancelPaymentForm = new javax.swing.JButton();
        jLabel23 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        buttonSavePaymentForm = new javax.swing.JButton();
        comboboxMethodPayment = new javax.swing.JComboBox<>();
        comboboxUsernamePayment = new javax.swing.JComboBox<>();
        jLabel26 = new javax.swing.JLabel();
        labelBrowseScreenshotPayment = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        labelScreenshotPayment = new javax.swing.JLabel();
        textfieldAmountPayment = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        labelBottomPadding = new javax.swing.JLabel();
        labelBackToHome = new javax.swing.JLabel();
        labelLoadingStatus = new javax.swing.JLabel();
        labelRightPadding = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
        });

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

        getContentPane().add(panelHeader, java.awt.BorderLayout.PAGE_START);

        panelCenter.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelInnerCenter.setLayout(new java.awt.CardLayout());

        panelHome.setLayout(new java.awt.GridLayout(2, 0, 20, 25));

        buttonUserManagement.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/user.png"))); // NOI18N
        buttonUserManagement.setText("Users");
        buttonUserManagement.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonUserManagement.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonUserManagement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonUserManagementActionPerformed(evt);
            }
        });
        panelHome.add(buttonUserManagement);

        buttonDocumentManagement.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/file.png"))); // NOI18N
        buttonDocumentManagement.setText("Documents");
        buttonDocumentManagement.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonDocumentManagement.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonDocumentManagement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDocumentManagementActionPerformed(evt);
            }
        });
        panelHome.add(buttonDocumentManagement);

        buttonAttendance.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/calendar64.png"))); // NOI18N
        buttonAttendance.setText("Attendance");
        buttonAttendance.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonAttendance.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonAttendance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAttendanceActionPerformed(evt);
            }
        });
        panelHome.add(buttonAttendance);

        buttonPayment.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/cash64.png"))); // NOI18N
        buttonPayment.setText("Payment");
        buttonPayment.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonPayment.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonPayment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonPaymentActionPerformed(evt);
            }
        });
        panelHome.add(buttonPayment);

        buttonSchedule.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/time64.png"))); // NOI18N
        buttonSchedule.setText("Schedule");
        buttonSchedule.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonSchedule.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonSchedule.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonScheduleActionPerformed(evt);
            }
        });
        panelHome.add(buttonSchedule);

        buttonSettings.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/option64.png"))); // NOI18N
        buttonSettings.setText("Settings");
        buttonSettings.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonSettings.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        panelHome.add(buttonSettings);

        buttonFuture2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/warning64.png"))); // NOI18N
        buttonFuture2.setText("next release");
        buttonFuture2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonFuture2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        panelHome.add(buttonFuture2);

        buttonFuture3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/warning64.png"))); // NOI18N
        buttonFuture3.setText("next release");
        buttonFuture3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonFuture3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        panelHome.add(buttonFuture3);

        panelInnerCenter.add(panelHome, "panelHome");

        panelUser.setLayout(new java.awt.CardLayout());

        panelUserManagement.setLayout(new java.awt.BorderLayout());

        panelUserControl.setPreferredSize(new java.awt.Dimension(658, 40));
        panelUserControl.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        buttonAddUser.setText("Add");
        buttonAddUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddUserActionPerformed(evt);
            }
        });
        panelUserControl.add(buttonAddUser, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 5, 60, -1));

        buttonEditUser.setText("Edit");
        buttonEditUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonEditUserActionPerformed(evt);
            }
        });
        panelUserControl.add(buttonEditUser, new org.netbeans.lib.awtextra.AbsoluteConstraints(514, 5, 60, -1));

        buttonDeleteUser.setText("Delete");
        buttonDeleteUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeleteUserActionPerformed(evt);
            }
        });
        panelUserControl.add(buttonDeleteUser, new org.netbeans.lib.awtextra.AbsoluteConstraints(589, 5, -1, -1));

        labelUserManagement.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        labelUserManagement.setText("User Management");
        panelUserControl.add(labelUserManagement, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 280, 40));

        labelRefreshUser.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/refresh16.png"))); // NOI18N
        labelRefreshUser.setText("Refresh");
        labelRefreshUser.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelRefreshUser.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelRefreshUserMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                labelRefreshUserMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                labelRefreshUserMouseExited(evt);
            }
        });
        panelUserControl.add(labelRefreshUser, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 10, 70, 20));

        panelUserManagement.add(panelUserControl, java.awt.BorderLayout.PAGE_START);

        panelUserTable.setLayout(new java.awt.BorderLayout());

        tableUserData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "[ x ]", "Id", "Username", "Pass", "Email", "Address", "Propic", "Mobile"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tableUserData);
        if (tableUserData.getColumnModel().getColumnCount() > 0) {
            tableUserData.getColumnModel().getColumn(0).setMinWidth(30);
            tableUserData.getColumnModel().getColumn(0).setPreferredWidth(30);
            tableUserData.getColumnModel().getColumn(0).setMaxWidth(30);
            tableUserData.getColumnModel().getColumn(1).setMinWidth(0);
            tableUserData.getColumnModel().getColumn(1).setPreferredWidth(0);
            tableUserData.getColumnModel().getColumn(1).setMaxWidth(0);
            tableUserData.getColumnModel().getColumn(2).setMinWidth(100);
            tableUserData.getColumnModel().getColumn(2).setPreferredWidth(100);
            tableUserData.getColumnModel().getColumn(2).setMaxWidth(100);
            tableUserData.getColumnModel().getColumn(3).setMinWidth(80);
            tableUserData.getColumnModel().getColumn(3).setPreferredWidth(80);
            tableUserData.getColumnModel().getColumn(3).setMaxWidth(80);
            tableUserData.getColumnModel().getColumn(5).setHeaderValue("Address");
            tableUserData.getColumnModel().getColumn(6).setHeaderValue("Propic");
            tableUserData.getColumnModel().getColumn(7).setHeaderValue("Mobile");
        }

        panelUserTable.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        panelUserManagement.add(panelUserTable, java.awt.BorderLayout.CENTER);

        panelUser.add(panelUserManagement, "panelUserManagement");

        panelUserForm.setBorder(javax.swing.BorderFactory.createTitledBorder("User Form"));
        panelUserForm.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setText("Username : ");
        panelUserForm.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, 150, -1));

        buttonCancelUserForm.setText("Cancel");
        buttonCancelUserForm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelUserFormActionPerformed(evt);
            }
        });
        panelUserForm.add(buttonCancelUserForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 250, -1, -1));
        panelUserForm.add(textfieldUsername, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 200, -1));

        jLabel5.setText("Password :");
        panelUserForm.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 100, 150, -1));
        panelUserForm.add(textfieldPass, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 120, 200, -1));

        jLabel6.setText("Email :");
        panelUserForm.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 160, 150, -1));
        panelUserForm.add(textfieldEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 180, 200, -1));

        jLabel7.setText("Address :");
        panelUserForm.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 40, 150, -1));

        textareaAddress.setColumns(20);
        textareaAddress.setLineWrap(true);
        textareaAddress.setRows(5);
        jScrollPane2.setViewportView(textareaAddress);

        panelUserForm.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 60, -1, 100));
        panelUserForm.add(textfieldMobile, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 240, 200, -1));

        jLabel8.setText("Mobile :");
        panelUserForm.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 220, 150, -1));

        labelPreviewPicture.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPreviewPicture.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/user.png"))); // NOI18N
        labelPreviewPicture.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        panelUserForm.add(labelPreviewPicture, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 170, 120, 110));

        labelLinkChangePicture.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        labelLinkChangePicture.setForeground(new java.awt.Color(0, 0, 204));
        labelLinkChangePicture.setText("<html><u>Change Picture</u></html>");
        labelLinkChangePicture.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelLinkChangePictureMouseClicked(evt);
            }
        });
        panelUserForm.add(labelLinkChangePicture, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 180, -1, -1));

        buttonSaveUserForm.setText("Save");
        buttonSaveUserForm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveUserFormActionPerformed(evt);
            }
        });
        panelUserForm.add(buttonSaveUserForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(555, 250, 60, -1));

        panelUser.add(panelUserForm, "panelUserForm");

        panelInnerCenter.add(panelUser, "panelUser");

        panelDocument.setLayout(new java.awt.CardLayout());

        panelDocumentManagement.setLayout(new java.awt.BorderLayout());

        panelDocumentControl.setPreferredSize(new java.awt.Dimension(658, 40));
        panelDocumentControl.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        buttonAddDocument.setText("Add");
        buttonAddDocument.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddDocumentActionPerformed(evt);
            }
        });
        panelDocumentControl.add(buttonAddDocument, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 5, 60, -1));

        buttonEditDocument.setText("Edit");
        buttonEditDocument.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonEditDocumentActionPerformed(evt);
            }
        });
        panelDocumentControl.add(buttonEditDocument, new org.netbeans.lib.awtextra.AbsoluteConstraints(514, 5, 60, -1));

        buttonDeleteDocument.setText("Delete");
        buttonDeleteDocument.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeleteDocumentActionPerformed(evt);
            }
        });
        panelDocumentControl.add(buttonDeleteDocument, new org.netbeans.lib.awtextra.AbsoluteConstraints(589, 5, -1, -1));

        labelRefreshDocument.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/refresh16.png"))); // NOI18N
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
        panelDocumentControl.add(labelRefreshDocument, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 10, 70, 20));

        jLabel14.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        jLabel14.setText("Document Management");
        panelDocumentControl.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 280, 40));

        panelDocumentManagement.add(panelDocumentControl, java.awt.BorderLayout.PAGE_START);

        panelDocumentTable.setLayout(new java.awt.BorderLayout());

        tableDocumentData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "[ x ]", "Id", "Title", "Description", "Filename", "Username", "Url", "Date Created"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tableDocumentData.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(tableDocumentData);
        if (tableDocumentData.getColumnModel().getColumnCount() > 0) {
            tableDocumentData.getColumnModel().getColumn(0).setMinWidth(30);
            tableDocumentData.getColumnModel().getColumn(0).setPreferredWidth(30);
            tableDocumentData.getColumnModel().getColumn(0).setMaxWidth(30);
            tableDocumentData.getColumnModel().getColumn(1).setMinWidth(0);
            tableDocumentData.getColumnModel().getColumn(1).setPreferredWidth(0);
            tableDocumentData.getColumnModel().getColumn(1).setMaxWidth(0);
        }

        panelDocumentTable.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        panelDocumentManagement.add(panelDocumentTable, java.awt.BorderLayout.CENTER);

        panelDocument.add(panelDocumentManagement, "panelDocumentManagement");

        panelDocumentForm.setBorder(javax.swing.BorderFactory.createTitledBorder("Document Form"));
        panelDocumentForm.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel9.setText("Title :");
        panelDocumentForm.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, 150, -1));

        buttonCancelDocumentForm.setText("Cancel");
        buttonCancelDocumentForm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelDocumentFormActionPerformed(evt);
            }
        });
        panelDocumentForm.add(buttonCancelDocumentForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 250, -1, -1));
        panelDocumentForm.add(textfieldTitleDoc, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 200, -1));

        jLabel10.setText("Filename :");
        panelDocumentForm.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 100, 150, -1));
        panelDocumentForm.add(textfieldFilenameDoc, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 120, 200, -1));

        jLabel11.setText("Username :");
        panelDocumentForm.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 180, 150, -1));

        jLabel12.setText("Description :");
        panelDocumentForm.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 40, 150, -1));

        textareaDescriptionDoc.setColumns(20);
        textareaDescriptionDoc.setLineWrap(true);
        textareaDescriptionDoc.setRows(5);
        jScrollPane4.setViewportView(textareaDescriptionDoc);

        panelDocumentForm.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 60, -1, 100));
        panelDocumentForm.add(textfieldUrlDoc, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 200, 200, -1));

        jLabel13.setText("Url :");
        panelDocumentForm.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 180, 150, -1));

        labelLinkChangeFileDoc.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        labelLinkChangeFileDoc.setForeground(new java.awt.Color(0, 0, 204));
        labelLinkChangeFileDoc.setText("<html><u>Change File...</u></html>");
        labelLinkChangeFileDoc.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelLinkChangeFileDoc.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelLinkChangeFileDocMouseClicked(evt);
            }
        });
        panelDocumentForm.add(labelLinkChangeFileDoc, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 160, -1, -1));

        buttonSaveDocumentForm.setText("Save");
        buttonSaveDocumentForm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveDocumentFormActionPerformed(evt);
            }
        });
        panelDocumentForm.add(buttonSaveDocumentForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(555, 250, 60, -1));

        comboboxUsernameDoc.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        panelDocumentForm.add(comboboxUsernameDoc, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 200, 200, -1));

        panelDocument.add(panelDocumentForm, "panelDocumentForm");

        panelInnerCenter.add(panelDocument, "panelDocument");

        panelSchedule.setLayout(new java.awt.CardLayout());

        panelScheduleManagement.setLayout(new java.awt.BorderLayout());

        panelScheduleControl.setPreferredSize(new java.awt.Dimension(658, 40));
        panelScheduleControl.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        buttonAddSchedule.setText("Add");
        buttonAddSchedule.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddScheduleActionPerformed(evt);
            }
        });
        panelScheduleControl.add(buttonAddSchedule, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 5, 60, -1));

        buttonEditSchedule.setText("Edit");
        buttonEditSchedule.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonEditScheduleActionPerformed(evt);
            }
        });
        panelScheduleControl.add(buttonEditSchedule, new org.netbeans.lib.awtextra.AbsoluteConstraints(514, 5, 60, -1));

        buttonDeleteSchedule.setText("Delete");
        buttonDeleteSchedule.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeleteScheduleActionPerformed(evt);
            }
        });
        panelScheduleControl.add(buttonDeleteSchedule, new org.netbeans.lib.awtextra.AbsoluteConstraints(589, 5, -1, -1));

        labelScheduleManagement.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        labelScheduleManagement.setText("Schedule Management");
        panelScheduleControl.add(labelScheduleManagement, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 280, 40));

        labelRefreshSchedule.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/refresh16.png"))); // NOI18N
        labelRefreshSchedule.setText("Refresh");
        labelRefreshSchedule.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelRefreshSchedule.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelRefreshScheduleMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                labelRefreshScheduleMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                labelRefreshScheduleMouseExited(evt);
            }
        });
        panelScheduleControl.add(labelRefreshSchedule, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 10, 70, 20));

        panelScheduleManagement.add(panelScheduleControl, java.awt.BorderLayout.PAGE_START);

        panelScheduleTable.setLayout(new java.awt.BorderLayout());

        tableScheduleData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "[ x ]", "Id", "Username", "Day", "Time", "Class"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane5.setViewportView(tableScheduleData);
        if (tableScheduleData.getColumnModel().getColumnCount() > 0) {
            tableScheduleData.getColumnModel().getColumn(0).setMinWidth(30);
            tableScheduleData.getColumnModel().getColumn(0).setPreferredWidth(30);
            tableScheduleData.getColumnModel().getColumn(0).setMaxWidth(30);
            tableScheduleData.getColumnModel().getColumn(1).setMinWidth(0);
            tableScheduleData.getColumnModel().getColumn(1).setPreferredWidth(0);
            tableScheduleData.getColumnModel().getColumn(1).setMaxWidth(0);
            tableScheduleData.getColumnModel().getColumn(2).setMinWidth(100);
            tableScheduleData.getColumnModel().getColumn(2).setPreferredWidth(100);
            tableScheduleData.getColumnModel().getColumn(2).setMaxWidth(100);
            tableScheduleData.getColumnModel().getColumn(3).setMinWidth(100);
            tableScheduleData.getColumnModel().getColumn(3).setPreferredWidth(100);
            tableScheduleData.getColumnModel().getColumn(3).setMaxWidth(100);
            tableScheduleData.getColumnModel().getColumn(4).setMinWidth(80);
            tableScheduleData.getColumnModel().getColumn(4).setPreferredWidth(80);
            tableScheduleData.getColumnModel().getColumn(4).setMaxWidth(80);
        }

        panelScheduleTable.add(jScrollPane5, java.awt.BorderLayout.CENTER);

        panelScheduleManagement.add(panelScheduleTable, java.awt.BorderLayout.CENTER);

        panelSchedule.add(panelScheduleManagement, "panelScheduleManagement");

        panelScheduleForm.setBorder(javax.swing.BorderFactory.createTitledBorder("Schedule Form"));
        panelScheduleForm.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        buttonCancelScheduleForm.setText("Cancel");
        buttonCancelScheduleForm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelScheduleFormActionPerformed(evt);
            }
        });
        panelScheduleForm.add(buttonCancelScheduleForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 250, -1, -1));

        jLabel16.setText("Day :");
        panelScheduleForm.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 100, 150, -1));

        jLabel17.setText("Class Registered :");
        panelScheduleForm.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 160, 150, -1));

        jLabel19.setText("Time : (hour : minutes)");
        panelScheduleForm.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 220, 150, -1));

        buttonSaveScheduleForm.setText("Save");
        buttonSaveScheduleForm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveScheduleFormActionPerformed(evt);
            }
        });
        panelScheduleForm.add(buttonSaveScheduleForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(555, 250, 60, -1));
        panelScheduleForm.add(spinnerMinutesSched, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 240, 50, -1));
        panelScheduleForm.add(spinnerHourSched, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 240, 50, -1));

        comboboxClassRegSched.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        panelScheduleForm.add(comboboxClassRegSched, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 180, 200, -1));

        comboboxUsernameSched.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        panelScheduleForm.add(comboboxUsernameSched, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 200, -1));

        comboboxDaySched.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday" }));
        comboboxDaySched.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboboxDaySchedItemStateChanged(evt);
            }
        });
        panelScheduleForm.add(comboboxDaySched, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 120, 160, -1));

        jLabel18.setText("Username : ");
        panelScheduleForm.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, 150, -1));

        jLabel20.setText("Another Class Same Day :");
        panelScheduleForm.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 60, 220, -1));

        jScrollPane7.setViewportView(listAnotherClassSched);

        panelScheduleForm.add(jScrollPane7, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 80, 210, 130));

        panelSchedule.add(panelScheduleForm, "panelScheduleForm");

        panelInnerCenter.add(panelSchedule, "panelSchedule");

        panelAttendance.setLayout(new java.awt.CardLayout());

        panelAttendanceManagement.setLayout(new java.awt.BorderLayout());

        panelAttendanceControl.setPreferredSize(new java.awt.Dimension(658, 40));
        panelAttendanceControl.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        buttonAddAttendance.setText("Add");
        buttonAddAttendance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddAttendanceActionPerformed(evt);
            }
        });
        panelAttendanceControl.add(buttonAddAttendance, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 5, 60, -1));

        buttonEditAttendance.setText("Edit");
        buttonEditAttendance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonEditAttendanceActionPerformed(evt);
            }
        });
        panelAttendanceControl.add(buttonEditAttendance, new org.netbeans.lib.awtextra.AbsoluteConstraints(514, 5, 60, -1));

        buttonDeleteAttendance.setText("Delete");
        buttonDeleteAttendance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeleteAttendanceActionPerformed(evt);
            }
        });
        panelAttendanceControl.add(buttonDeleteAttendance, new org.netbeans.lib.awtextra.AbsoluteConstraints(589, 5, -1, -1));

        labelAttendanceManagement.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        labelAttendanceManagement.setText("Attendance Management");
        panelAttendanceControl.add(labelAttendanceManagement, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 280, 40));

        labelRefreshAttendance.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/refresh16.png"))); // NOI18N
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
        panelAttendanceControl.add(labelRefreshAttendance, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 10, 70, 20));

        panelAttendanceManagement.add(panelAttendanceControl, java.awt.BorderLayout.PAGE_START);

        panelAttendanceTable.setLayout(new java.awt.BorderLayout());

        tableAttendanceData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "[ x ]", "Id", "Username", "Class Reg", "Status", "Signature", "Date Created", "Date Modified"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane6.setViewportView(tableAttendanceData);
        if (tableAttendanceData.getColumnModel().getColumnCount() > 0) {
            tableAttendanceData.getColumnModel().getColumn(0).setMinWidth(30);
            tableAttendanceData.getColumnModel().getColumn(0).setPreferredWidth(30);
            tableAttendanceData.getColumnModel().getColumn(0).setMaxWidth(30);
            tableAttendanceData.getColumnModel().getColumn(1).setMinWidth(0);
            tableAttendanceData.getColumnModel().getColumn(1).setPreferredWidth(0);
            tableAttendanceData.getColumnModel().getColumn(1).setMaxWidth(0);
            tableAttendanceData.getColumnModel().getColumn(2).setMinWidth(100);
            tableAttendanceData.getColumnModel().getColumn(2).setPreferredWidth(100);
            tableAttendanceData.getColumnModel().getColumn(2).setMaxWidth(100);
            tableAttendanceData.getColumnModel().getColumn(3).setMinWidth(100);
            tableAttendanceData.getColumnModel().getColumn(3).setPreferredWidth(100);
            tableAttendanceData.getColumnModel().getColumn(3).setMaxWidth(100);
            tableAttendanceData.getColumnModel().getColumn(4).setMinWidth(80);
            tableAttendanceData.getColumnModel().getColumn(4).setPreferredWidth(80);
            tableAttendanceData.getColumnModel().getColumn(4).setMaxWidth(80);
            tableAttendanceData.getColumnModel().getColumn(7).setHeaderValue("Date Modified");
        }

        panelAttendanceTable.add(jScrollPane6, java.awt.BorderLayout.CENTER);

        panelAttendanceManagement.add(panelAttendanceTable, java.awt.BorderLayout.CENTER);

        panelAttendance.add(panelAttendanceManagement, "panelAttendanceManagement");

        panelAttendanceForm.setBorder(javax.swing.BorderFactory.createTitledBorder("Attendance Form"));
        panelAttendanceForm.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        buttonCancelAttendanceForm.setText("Cancel");
        buttonCancelAttendanceForm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelAttendanceFormActionPerformed(evt);
            }
        });
        panelAttendanceForm.add(buttonCancelAttendanceForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 250, -1, -1));

        jLabel21.setText("Status :");
        panelAttendanceForm.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 100, 150, -1));

        jLabel22.setText("Class Registered :");
        panelAttendanceForm.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 160, 150, -1));

        buttonSaveAttendanceForm.setText("Save");
        buttonSaveAttendanceForm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveAttendanceFormActionPerformed(evt);
            }
        });
        panelAttendanceForm.add(buttonSaveAttendanceForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(555, 250, 60, -1));

        comboboxClassRegAttendance.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        panelAttendanceForm.add(comboboxClassRegAttendance, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 180, 200, -1));

        comboboxUsernameAttendance.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        panelAttendanceForm.add(comboboxUsernameAttendance, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 200, -1));

        comboboxStatusAttendance.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "hadir", "idzin" }));
        comboboxStatusAttendance.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboboxStatusAttendanceItemStateChanged(evt);
            }
        });
        panelAttendanceForm.add(comboboxStatusAttendance, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 120, 160, -1));

        jLabel24.setText("Username : ");
        panelAttendanceForm.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, 150, -1));

        labelBrowseSignatureAttendance.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        labelBrowseSignatureAttendance.setForeground(new java.awt.Color(0, 51, 255));
        labelBrowseSignatureAttendance.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelBrowseSignatureAttendance.setText("<html><u>Browse Picture</u></html>");
        labelBrowseSignatureAttendance.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelBrowseSignatureAttendance.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelBrowseSignatureAttendanceMouseClicked(evt);
            }
        });
        panelAttendanceForm.add(labelBrowseSignatureAttendance, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 210, 120, -1));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Signature Picture"));
        jPanel1.setLayout(new java.awt.BorderLayout());

        labelSignatureAttendance.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelSignatureAttendance.setText("preview");
        jPanel1.add(labelSignatureAttendance, java.awt.BorderLayout.CENTER);

        panelAttendanceForm.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 40, 210, 170));

        panelAttendance.add(panelAttendanceForm, "panelAttendanceForm");

        panelInnerCenter.add(panelAttendance, "panelAttendance");

        panelPayment.setLayout(new java.awt.CardLayout());

        panelPaymentManagement.setLayout(new java.awt.BorderLayout());

        panelPaymentControl.setPreferredSize(new java.awt.Dimension(658, 40));
        panelPaymentControl.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        buttonAddPayment.setText("Add");
        buttonAddPayment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddPaymentActionPerformed(evt);
            }
        });
        panelPaymentControl.add(buttonAddPayment, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 5, 60, -1));

        buttonEditPayment.setText("Edit");
        buttonEditPayment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonEditPaymentActionPerformed(evt);
            }
        });
        panelPaymentControl.add(buttonEditPayment, new org.netbeans.lib.awtextra.AbsoluteConstraints(514, 5, 60, -1));

        buttonDeletePayment.setText("Delete");
        buttonDeletePayment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeletePaymentActionPerformed(evt);
            }
        });
        panelPaymentControl.add(buttonDeletePayment, new org.netbeans.lib.awtextra.AbsoluteConstraints(589, 5, -1, -1));

        labelAttendanceManagement1.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        labelAttendanceManagement1.setText("Payment Management");
        panelPaymentControl.add(labelAttendanceManagement1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 280, 40));

        labelRefreshPayment.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/refresh16.png"))); // NOI18N
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
        panelPaymentControl.add(labelRefreshPayment, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 10, 70, 20));

        panelPaymentManagement.add(panelPaymentControl, java.awt.BorderLayout.PAGE_START);

        panelPaymentTable.setLayout(new java.awt.BorderLayout());

        tablePaymentData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "[ x ]", "Id", "Username", "Amount", "Method", "Screenshot", "Date Created"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane8.setViewportView(tablePaymentData);
        if (tablePaymentData.getColumnModel().getColumnCount() > 0) {
            tablePaymentData.getColumnModel().getColumn(0).setMinWidth(30);
            tablePaymentData.getColumnModel().getColumn(0).setPreferredWidth(30);
            tablePaymentData.getColumnModel().getColumn(0).setMaxWidth(30);
            tablePaymentData.getColumnModel().getColumn(1).setMinWidth(0);
            tablePaymentData.getColumnModel().getColumn(1).setPreferredWidth(0);
            tablePaymentData.getColumnModel().getColumn(1).setMaxWidth(0);
            tablePaymentData.getColumnModel().getColumn(2).setMinWidth(100);
            tablePaymentData.getColumnModel().getColumn(2).setPreferredWidth(100);
            tablePaymentData.getColumnModel().getColumn(2).setMaxWidth(100);
            tablePaymentData.getColumnModel().getColumn(3).setMinWidth(100);
            tablePaymentData.getColumnModel().getColumn(3).setPreferredWidth(100);
            tablePaymentData.getColumnModel().getColumn(3).setMaxWidth(100);
            tablePaymentData.getColumnModel().getColumn(4).setMinWidth(80);
            tablePaymentData.getColumnModel().getColumn(4).setPreferredWidth(80);
            tablePaymentData.getColumnModel().getColumn(4).setMaxWidth(80);
        }

        panelPaymentTable.add(jScrollPane8, java.awt.BorderLayout.CENTER);

        panelPaymentManagement.add(panelPaymentTable, java.awt.BorderLayout.CENTER);

        panelPayment.add(panelPaymentManagement, "panelPaymentManagement");

        panelPaymentForm.setBorder(javax.swing.BorderFactory.createTitledBorder("Payment Form"));
        panelPaymentForm.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        buttonCancelPaymentForm.setText("Cancel");
        buttonCancelPaymentForm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelPaymentFormActionPerformed(evt);
            }
        });
        panelPaymentForm.add(buttonCancelPaymentForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 250, -1, -1));

        jLabel23.setText("Rp.");
        panelPaymentForm.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 120, 40, 30));

        jLabel25.setText("Method :");
        panelPaymentForm.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 160, 150, -1));

        buttonSavePaymentForm.setText("Save");
        buttonSavePaymentForm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSavePaymentFormActionPerformed(evt);
            }
        });
        panelPaymentForm.add(buttonSavePaymentForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(555, 250, 60, -1));

        comboboxMethodPayment.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Transfer Bank", "Cash", " " }));
        panelPaymentForm.add(comboboxMethodPayment, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 180, 200, -1));

        comboboxUsernamePayment.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        panelPaymentForm.add(comboboxUsernamePayment, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 200, -1));

        jLabel26.setText("Username : ");
        panelPaymentForm.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, 150, -1));

        labelBrowseScreenshotPayment.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        labelBrowseScreenshotPayment.setForeground(new java.awt.Color(0, 51, 255));
        labelBrowseScreenshotPayment.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelBrowseScreenshotPayment.setText("<html><u>Browse Picture</u></html>");
        labelBrowseScreenshotPayment.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelBrowseScreenshotPayment.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelBrowseScreenshotPaymentMouseClicked(evt);
            }
        });
        panelPaymentForm.add(labelBrowseScreenshotPayment, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 210, 120, -1));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Screenshot"));
        jPanel2.setLayout(new java.awt.BorderLayout());

        labelScreenshotPayment.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelScreenshotPayment.setText("preview");
        jPanel2.add(labelScreenshotPayment, java.awt.BorderLayout.CENTER);

        panelPaymentForm.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 40, 210, 170));

        textfieldAmountPayment.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textfieldAmountPaymentFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                textfieldAmountPaymentFocusLost(evt);
            }
        });
        panelPaymentForm.add(textfieldAmountPayment, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 120, 150, -1));

        jLabel27.setText("Amount : ");
        panelPaymentForm.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 100, 150, -1));

        panelPayment.add(panelPaymentForm, "panelPaymentForm");

        panelInnerCenter.add(panelPayment, "panelPayment");

        panelCenter.add(panelInnerCenter, new org.netbeans.lib.awtextra.AbsoluteConstraints(43, 48, 658, 297));

        jLabel1.setFont(new java.awt.Font("sansserif", 1, 24)); // NOI18N
        jLabel1.setText("Admin Area");
        panelCenter.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 10, -1, -1));

        labelBottomPadding.setText("bottom-padding");
        panelCenter.add(labelBottomPadding, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 360, 170, 70));

        labelBackToHome.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/home24.png"))); // NOI18N
        labelBackToHome.setText("Back to Home");
        labelBackToHome.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelBackToHome.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelBackToHomeMouseClicked(evt);
            }
        });
        panelCenter.add(labelBackToHome, new org.netbeans.lib.awtextra.AbsoluteConstraints(51, 360, 170, 40));

        labelLoadingStatus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/loadingprel.gif"))); // NOI18N
        labelLoadingStatus.setText("Loading...");
        panelCenter.add(labelLoadingStatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 360, 200, 30));

        labelRightPadding.setText("r-padding");
        panelCenter.add(labelRightPadding, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 120, 60, 40));

        getContentPane().add(panelCenter, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void labelCloseMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelCloseMouseClicked
        UIEffect.stopTimeEffect();
        logout();
    }//GEN-LAST:event_labelCloseMouseClicked

    private void logout() {

        loginFrame.show();
        this.dispose();
    }

    private void labelMinimizeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelMinimizeMouseClicked
        this.setState(Frame.ICONIFIED);
    }//GEN-LAST:event_labelMinimizeMouseClicked

    private void buttonUserManagementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonUserManagementActionPerformed
        cardLayoutInnerCenter.show(panelInnerCenter, "panelUser");
        cardLayoutEntity = (CardLayout) panelUser.getLayout();
        cardLayoutEntity.show(panelUser, "panelUserManagement");

        labelBackToHome.setVisible(true);
        // set the picture as empty one
        propicFile = null;
    }//GEN-LAST:event_buttonUserManagementActionPerformed

    private void buttonSaveUserFormActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveUserFormActionPerformed
        cardLayoutEntity.show(panelUser, "panelUserManagement");
        saveUser();
    }//GEN-LAST:event_buttonSaveUserFormActionPerformed


    private void buttonCancelUserFormActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelUserFormActionPerformed
        cardLayoutEntity.show(panelUser, "panelUserManagement");
        labelBackToHome.setVisible(true);
    }//GEN-LAST:event_buttonCancelUserFormActionPerformed

    private void labelLinkChangePictureMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelLinkChangePictureMouseClicked

        // browse file
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            propicFile = fileChooser.getSelectedFile();
            try {
                labelPreviewPicture.setIcon(new ImageIcon(ImageIO.read(propicFile)));
            } catch (Exception e) {
                e.printStackTrace();
                UIEffect.popup("Error while browse picutre applied!", this);
            }
        } else {
            // if no file was chosen
            propicFile = null;
        }

    }//GEN-LAST:event_labelLinkChangePictureMouseClicked

    private void buttonAddUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddUserActionPerformed
        cardLayoutEntity.show(panelUser, "panelUserForm");
        // clean but not for editing mode
        cleanUpUserForm(false);
    }//GEN-LAST:event_buttonAddUserActionPerformed

    private void labelBackToHomeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelBackToHomeMouseClicked
        cardLayoutInnerCenter.show(panelInnerCenter, "panelHome");
        labelBackToHome.setVisible(false);
    }//GEN-LAST:event_labelBackToHomeMouseClicked

    private void buttonDeleteUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDeleteUserActionPerformed

        ArrayList dataUser = tabRender.getCheckedRows(tableUserData, 2);

        if (dataUser.size() == 0) {
            UIEffect.popup("Please select the row first!", this);
        } else {
            // passing username only
            deleteUser(dataUser);
            labelLoadingStatus.setVisible(true);
        }

    }//GEN-LAST:event_buttonDeleteUserActionPerformed

    private void buttonEditUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonEditUserActionPerformed
        ArrayList dataUser = tabRender.getCheckedRows(tableUserData, 2);

        if (dataUser.size() == 1) {
            // go to userForm
            cardLayoutEntity.show(panelUser, "panelUserForm");

            // clean the form but for editing mode
            cleanUpUserForm(true);

            // call the API with username passed
            getUserProfile(dataUser.get(0).toString());

            // show the loading bar
            labelLoadingStatus.setVisible(true);
        } else {
            UIEffect.popup("please select 1 single data only!", this);
        }


    }//GEN-LAST:event_buttonEditUserActionPerformed

    private void buttonAddDocumentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddDocumentActionPerformed
        cardLayoutEntity.show(panelDocument, "panelDocumentForm");
        // clean but not for editing mode
        cleanUpDocumentForm(false);

    }//GEN-LAST:event_buttonAddDocumentActionPerformed

    private void buttonEditDocumentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonEditDocumentActionPerformed
        // get the ID
        ArrayList dataDocument = tabRender.getCheckedRows(tableDocumentData, 1);

        if (dataDocument.size() == 1) {
            // go to documentForm
            cardLayoutEntity.show(panelDocument, "panelDocumentForm");

            // clean the form but for editing mode
            cleanUpDocumentForm(true);

            // call the API with id passed
            getDocument(Integer.parseInt(dataDocument.get(0).toString()));

            // show the loading bar
            labelLoadingStatus.setVisible(true);
        } else {
            UIEffect.popup("please select 1 single data only!", this);
        }
    }//GEN-LAST:event_buttonEditDocumentActionPerformed

    private void buttonDeleteDocumentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDeleteDocumentActionPerformed
        ArrayList dataDocument = tabRender.getCheckedRows(tableDocumentData, 1);

        if (dataDocument.size() == 0) {
            UIEffect.popup("Please select the row first!", this);
        } else {
            // passing id only
            deleteDocument(dataDocument);
            labelLoadingStatus.setVisible(true);
        }
    }//GEN-LAST:event_buttonDeleteDocumentActionPerformed

    private void buttonCancelDocumentFormActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelDocumentFormActionPerformed
        cardLayoutEntity.show(panelDocument, "panelDocumentManagement");
        labelBackToHome.setVisible(true);

    }//GEN-LAST:event_buttonCancelDocumentFormActionPerformed

    private void labelLinkChangeFileDocMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelLinkChangeFileDocMouseClicked

        // browse file
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            docFile = fileChooser.getSelectedFile();
            textfieldFilenameDoc.setText(docFile.getName());

        } else {
            // if no file was chosen
            docFile = null;
        }
    }//GEN-LAST:event_labelLinkChangeFileDocMouseClicked

    private void buttonSaveDocumentFormActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveDocumentFormActionPerformed
        cardLayoutEntity.show(panelDocument, "panelDocumentManagement");
        saveDocument();
        labelLoadingStatus.setVisible(true);
    }//GEN-LAST:event_buttonSaveDocumentFormActionPerformed

    private void buttonDocumentManagementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDocumentManagementActionPerformed
        cardLayoutInnerCenter.show(panelInnerCenter, "panelDocument");
        cardLayoutEntity = (CardLayout) panelDocument.getLayout();
        cardLayoutEntity.show(panelDocument, "panelDocumentManagement");

        labelBackToHome.setVisible(true);
        // document has no picture
    }//GEN-LAST:event_buttonDocumentManagementActionPerformed

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        UIDragger.mousePressed(evt);
    }//GEN-LAST:event_formMousePressed

    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged
        UIDragger.mouseDragged(evt);
    }//GEN-LAST:event_formMouseDragged

    private void labelRefreshDocumentMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRefreshDocumentMouseClicked

        // change to loading icon
        labelRefreshDocument.setIcon(loadingImage);

        // refresh the table
        refreshDocument();
    }//GEN-LAST:event_labelRefreshDocumentMouseClicked

    private void labelRefreshDocumentMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRefreshDocumentMouseEntered
        UIEffect.mouseHover(labelRefreshDocument);
    }//GEN-LAST:event_labelRefreshDocumentMouseEntered

    private void labelRefreshDocumentMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRefreshDocumentMouseExited
        UIEffect.mouseExit(labelRefreshDocument);
    }//GEN-LAST:event_labelRefreshDocumentMouseExited

    private void labelRefreshUserMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRefreshUserMouseClicked
        // change to loading icon

        labelRefreshUser.setIcon(loadingImage);

        // refresh the table
        refreshUser();
    }//GEN-LAST:event_labelRefreshUserMouseClicked

    private void labelRefreshUserMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRefreshUserMouseEntered
        UIEffect.mouseHover(labelRefreshUser);
    }//GEN-LAST:event_labelRefreshUserMouseEntered

    private void labelRefreshUserMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRefreshUserMouseExited
        UIEffect.mouseExit(labelRefreshUser);
    }//GEN-LAST:event_labelRefreshUserMouseExited

    private void buttonAddScheduleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddScheduleActionPerformed
        cardLayoutEntity.show(panelSchedule, "panelScheduleForm");
        // clean but not for editing mode
        cleanUpScheduleForm(false);
    }//GEN-LAST:event_buttonAddScheduleActionPerformed

    private void buttonEditScheduleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonEditScheduleActionPerformed
        ArrayList dataSched = tabRender.getCheckedRows(tableScheduleData, 1);

        if (dataSched.size() == 1) {
            // go to schedForm
            cardLayoutEntity.show(panelSchedule, "panelScheduleForm");

            // clean the form but for editing mode
            cleanUpScheduleForm(true);

            // call the API with id passed
            getSchedule(Integer.parseInt(dataSched.get(0).toString()));

            // show the loading bar
            labelLoadingStatus.setVisible(true);
        } else {
            UIEffect.popup("please select 1 single data only!", this);
        }
    }//GEN-LAST:event_buttonEditScheduleActionPerformed

    private void buttonDeleteScheduleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDeleteScheduleActionPerformed

        ArrayList dataSchedule = tabRender.getCheckedRows(tableScheduleData, 1);

        if (dataSchedule.size() == 0) {
            UIEffect.popup("Please select the row first!", this);
        } else {
            // passing id only
            deleteSchedule(dataSchedule);
            labelLoadingStatus.setVisible(true);
        }

    }//GEN-LAST:event_buttonDeleteScheduleActionPerformed

    private void labelRefreshScheduleMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRefreshScheduleMouseClicked
        // change to loading icon
        labelRefreshSchedule.setIcon(loadingImage);

        // refresh the table
        refreshSchedule();
    }//GEN-LAST:event_labelRefreshScheduleMouseClicked

    private void labelRefreshScheduleMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRefreshScheduleMouseEntered
        UIEffect.mouseHover(labelRefreshSchedule);
    }//GEN-LAST:event_labelRefreshScheduleMouseEntered

    private void labelRefreshScheduleMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRefreshScheduleMouseExited
        UIEffect.mouseExit(labelRefreshSchedule);
    }//GEN-LAST:event_labelRefreshScheduleMouseExited

    private void buttonCancelScheduleFormActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelScheduleFormActionPerformed
        cardLayoutEntity.show(panelSchedule, "panelScheduleManagement");
        labelBackToHome.setVisible(true);
    }//GEN-LAST:event_buttonCancelScheduleFormActionPerformed

    private void buttonSaveScheduleFormActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveScheduleFormActionPerformed
        cardLayoutEntity.show(panelSchedule, "panelScheduleManagement");
        saveSchedule();
    }//GEN-LAST:event_buttonSaveScheduleFormActionPerformed

    private void buttonScheduleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonScheduleActionPerformed
        cardLayoutInnerCenter.show(panelInnerCenter, "panelSchedule");
        cardLayoutEntity = (CardLayout) panelSchedule.getLayout();
        cardLayoutEntity.show(panelSchedule, "panelScheduleManagement");

        labelBackToHome.setVisible(true);

    }//GEN-LAST:event_buttonScheduleActionPerformed


    private void comboboxDaySchedItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboboxDaySchedItemStateChanged
        labelLoadingStatus.setVisible(true);

        // when the item selected changed
        // we check the registered class of that day
        if (comboboxDaySched.getSelectedItem() != null) {
            String classSelected = comboboxDaySched.getSelectedItem().toString();
            refreshScheduleByDay(classSelected);
        } else {
            // clearup the list
            listAnotherClassSched.setModel(new DefaultListModel());
            labelLoadingStatus.setVisible(false);
        }
    }//GEN-LAST:event_comboboxDaySchedItemStateChanged

    private void buttonAddAttendanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddAttendanceActionPerformed
        cardLayoutEntity.show(panelAttendance, "panelAttendanceForm");
        // clean but not for editing mode
        cleanUpAttendanceForm(false);
    }//GEN-LAST:event_buttonAddAttendanceActionPerformed

    private void buttonEditAttendanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonEditAttendanceActionPerformed

        ArrayList dataAttendance = tabRender.getCheckedRows(tableAttendanceData, 1);

        if (dataAttendance.size() == 1) {
            // go to userForm
            cardLayoutEntity.show(panelAttendance, "panelAttendanceForm");

            // clean the form but for editing mode
            cleanUpAttendanceForm(true);

            // call the API with id passed
            getAttendance(Integer.parseInt(dataAttendance.get(0).toString()));

            // show the loading bar
            labelLoadingStatus.setVisible(true);
        } else {
            UIEffect.popup("please select 1 single data only!", this);
        }

    }//GEN-LAST:event_buttonEditAttendanceActionPerformed

    private void buttonDeleteAttendanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDeleteAttendanceActionPerformed
        ArrayList dataAttendance = tabRender.getCheckedRows(tableAttendanceData, 1);

        if (dataAttendance.size() == 0) {
            UIEffect.popup("Please select the row first!", this);
        } else {
            // passing id only
            deleteAttendance(dataAttendance);
            labelLoadingStatus.setVisible(true);
        }
    }//GEN-LAST:event_buttonDeleteAttendanceActionPerformed

    private void labelRefreshAttendanceMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRefreshAttendanceMouseClicked

        // change to loading icon
        labelRefreshAttendance.setIcon(loadingImage);

        // refresh the table
        refreshAttendance();
    }//GEN-LAST:event_labelRefreshAttendanceMouseClicked

    private void labelRefreshAttendanceMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRefreshAttendanceMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_labelRefreshAttendanceMouseEntered

    private void labelRefreshAttendanceMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRefreshAttendanceMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_labelRefreshAttendanceMouseExited

    private void buttonCancelAttendanceFormActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelAttendanceFormActionPerformed
        cardLayoutEntity.show(panelAttendance, "panelAttendanceManagement");
        labelBackToHome.setVisible(true);
    }//GEN-LAST:event_buttonCancelAttendanceFormActionPerformed

    private void buttonSaveAttendanceFormActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveAttendanceFormActionPerformed
        cardLayoutEntity.show(panelAttendance, "panelAttendanceManagement");
        saveAttendance();
    }//GEN-LAST:event_buttonSaveAttendanceFormActionPerformed

    private void comboboxStatusAttendanceItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboboxStatusAttendanceItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_comboboxStatusAttendanceItemStateChanged

    private void buttonAttendanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAttendanceActionPerformed
        cardLayoutInnerCenter.show(panelInnerCenter, "panelAttendance");
        cardLayoutEntity = (CardLayout) panelAttendance.getLayout();
        cardLayoutEntity.show(panelAttendance, "panelAttendanceManagement");

        labelBackToHome.setVisible(true);
        // document has no picture

    }//GEN-LAST:event_buttonAttendanceActionPerformed

    private void labelBrowseSignatureAttendanceMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelBrowseSignatureAttendanceMouseClicked

        // browse file
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            signatureFile = fileChooser.getSelectedFile();
            try {
                labelSignatureAttendance.setIcon(new ImageIcon(ImageIO.read(signatureFile)));
            } catch (Exception e) {
                e.printStackTrace();
                UIEffect.popup("Error while browse signature picture applied!", this);
            }
        } else {
            // if no file was chosen
            signatureFile = null;
        }


    }//GEN-LAST:event_labelBrowseSignatureAttendanceMouseClicked

    private void buttonAddPaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddPaymentActionPerformed

        cardLayoutEntity.show(panelPayment, "panelPaymentForm");
        // clean but not for editing mode
        cleanUpPaymentForm(false);

    }//GEN-LAST:event_buttonAddPaymentActionPerformed

    private void buttonEditPaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonEditPaymentActionPerformed

        ArrayList dataPay = tabRender.getCheckedRows(tablePaymentData, 1);

        if (dataPay.size() == 1) {
            // go to userForm
            cardLayoutEntity.show(panelPayment, "panelPaymentForm");

            // clean the form but for editing mode
            cleanUpPaymentForm(true);

            // call the API with id passed
            getPayment(Integer.parseInt(dataPay.get(0).toString()));

            // show the loading bar
            showLoadingStatus();
        } else {
            UIEffect.popup("please select 1 single data only!", this);
        }

    }//GEN-LAST:event_buttonEditPaymentActionPerformed

    private void buttonDeletePaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDeletePaymentActionPerformed
        ArrayList dataPay = tabRender.getCheckedRows(tablePaymentData, 1);

        if (dataPay.size() == 0) {
            UIEffect.popup("Please select the row first!", this);
        } else {
            // passing id only
            deletePayment(dataPay);
            labelLoadingStatus.setVisible(true);
        }
    }//GEN-LAST:event_buttonDeletePaymentActionPerformed

    private void labelRefreshPaymentMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRefreshPaymentMouseClicked
        // change to loading icon
        labelRefreshDocument.setIcon(loadingImage);

        // refresh the table
        refreshPayment();
    }//GEN-LAST:event_labelRefreshPaymentMouseClicked

    private void labelRefreshPaymentMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRefreshPaymentMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_labelRefreshPaymentMouseEntered

    private void labelRefreshPaymentMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRefreshPaymentMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_labelRefreshPaymentMouseExited

    private void buttonCancelPaymentFormActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelPaymentFormActionPerformed
        cardLayoutEntity.show(panelPayment, "panelPaymentManagement");
        labelBackToHome.setVisible(true);
    }//GEN-LAST:event_buttonCancelPaymentFormActionPerformed

    private void buttonSavePaymentFormActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSavePaymentFormActionPerformed
        cardLayoutEntity.show(panelPayment, "panelPaymentManagement");
        savePayment();
    }//GEN-LAST:event_buttonSavePaymentFormActionPerformed

    private void labelBrowseScreenshotPaymentMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelBrowseScreenshotPaymentMouseClicked
         // browse file
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            payFile = fileChooser.getSelectedFile();
            try {
                labelScreenshotPayment.setIcon(new ImageIcon(ImageIO.read(payFile)));
            } catch (Exception e) {
                e.printStackTrace();
                UIEffect.popup("Error while browse picutre applied!", this);
            }
        } else {
            // if no file was chosen
            payFile = null;
        }
    }//GEN-LAST:event_labelBrowseScreenshotPaymentMouseClicked

    private void buttonPaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonPaymentActionPerformed
        cardLayoutInnerCenter.show(panelInnerCenter, "panelPayment");
        cardLayoutEntity = (CardLayout) panelPayment.getLayout();
        cardLayoutEntity.show(panelPayment, "panelPaymentManagement");

        labelBackToHome.setVisible(true);
    }//GEN-LAST:event_buttonPaymentActionPerformed

    private void textfieldAmountPaymentFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textfieldAmountPaymentFocusLost
        UIEffect.focusLostCurrency(textfieldAmountPayment);
    }//GEN-LAST:event_textfieldAmountPaymentFocusLost

    private void textfieldAmountPaymentFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textfieldAmountPaymentFocusGained
        UIEffect.focusGainCurrency(textfieldAmountPayment);
    }//GEN-LAST:event_textfieldAmountPaymentFocusGained

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
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AdminFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AdminFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AdminFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AdminFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AdminFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAddAttendance;
    private javax.swing.JButton buttonAddDocument;
    private javax.swing.JButton buttonAddPayment;
    private javax.swing.JButton buttonAddSchedule;
    private javax.swing.JButton buttonAddUser;
    private javax.swing.JButton buttonAttendance;
    private javax.swing.JButton buttonCancelAttendanceForm;
    private javax.swing.JButton buttonCancelDocumentForm;
    private javax.swing.JButton buttonCancelPaymentForm;
    private javax.swing.JButton buttonCancelScheduleForm;
    private javax.swing.JButton buttonCancelUserForm;
    private javax.swing.JButton buttonDeleteAttendance;
    private javax.swing.JButton buttonDeleteDocument;
    private javax.swing.JButton buttonDeletePayment;
    private javax.swing.JButton buttonDeleteSchedule;
    private javax.swing.JButton buttonDeleteUser;
    private javax.swing.JButton buttonDocumentManagement;
    private javax.swing.JButton buttonEditAttendance;
    private javax.swing.JButton buttonEditDocument;
    private javax.swing.JButton buttonEditPayment;
    private javax.swing.JButton buttonEditSchedule;
    private javax.swing.JButton buttonEditUser;
    private javax.swing.JButton buttonFuture2;
    private javax.swing.JButton buttonFuture3;
    private javax.swing.JButton buttonPayment;
    private javax.swing.JButton buttonSaveAttendanceForm;
    private javax.swing.JButton buttonSaveDocumentForm;
    private javax.swing.JButton buttonSavePaymentForm;
    private javax.swing.JButton buttonSaveScheduleForm;
    private javax.swing.JButton buttonSaveUserForm;
    private javax.swing.JButton buttonSchedule;
    private javax.swing.JButton buttonSettings;
    private javax.swing.JButton buttonUserManagement;
    private javax.swing.JComboBox<String> comboboxClassRegAttendance;
    private javax.swing.JComboBox<String> comboboxClassRegSched;
    private javax.swing.JComboBox<String> comboboxDaySched;
    private javax.swing.JComboBox<String> comboboxMethodPayment;
    private javax.swing.JComboBox<String> comboboxStatusAttendance;
    private javax.swing.JComboBox<String> comboboxUsernameAttendance;
    private javax.swing.JComboBox<String> comboboxUsernameDoc;
    private javax.swing.JComboBox<String> comboboxUsernamePayment;
    private javax.swing.JComboBox<String> comboboxUsernameSched;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JLabel labelAttendanceManagement;
    private javax.swing.JLabel labelAttendanceManagement1;
    private javax.swing.JLabel labelBackToHome;
    private javax.swing.JLabel labelBottomPadding;
    private javax.swing.JLabel labelBrowseScreenshotPayment;
    private javax.swing.JLabel labelBrowseSignatureAttendance;
    private javax.swing.JLabel labelClose;
    private javax.swing.JLabel labelLinkChangeFileDoc;
    private javax.swing.JLabel labelLinkChangePicture;
    private javax.swing.JLabel labelLoadingStatus;
    private javax.swing.JLabel labelMinimize;
    private javax.swing.JLabel labelPreviewPicture;
    private javax.swing.JLabel labelRefreshAttendance;
    private javax.swing.JLabel labelRefreshDocument;
    private javax.swing.JLabel labelRefreshPayment;
    private javax.swing.JLabel labelRefreshSchedule;
    private javax.swing.JLabel labelRefreshUser;
    private javax.swing.JLabel labelRightPadding;
    private javax.swing.JLabel labelScheduleManagement;
    private javax.swing.JLabel labelScreenshotPayment;
    private javax.swing.JLabel labelSignatureAttendance;
    private javax.swing.JLabel labelTime;
    private javax.swing.JLabel labelUserManagement;
    private javax.swing.JList<String> listAnotherClassSched;
    private javax.swing.JPanel panelAttendance;
    private javax.swing.JPanel panelAttendanceControl;
    private javax.swing.JPanel panelAttendanceForm;
    private javax.swing.JPanel panelAttendanceManagement;
    private javax.swing.JPanel panelAttendanceTable;
    private javax.swing.JPanel panelCenter;
    private javax.swing.JPanel panelDocument;
    private javax.swing.JPanel panelDocumentControl;
    private javax.swing.JPanel panelDocumentForm;
    private javax.swing.JPanel panelDocumentManagement;
    private javax.swing.JPanel panelDocumentTable;
    private javax.swing.JPanel panelHeader;
    private javax.swing.JPanel panelHome;
    private javax.swing.JPanel panelInnerCenter;
    private javax.swing.JPanel panelPayment;
    private javax.swing.JPanel panelPaymentControl;
    private javax.swing.JPanel panelPaymentForm;
    private javax.swing.JPanel panelPaymentManagement;
    private javax.swing.JPanel panelPaymentTable;
    private javax.swing.JPanel panelSchedule;
    private javax.swing.JPanel panelScheduleControl;
    private javax.swing.JPanel panelScheduleForm;
    private javax.swing.JPanel panelScheduleManagement;
    private javax.swing.JPanel panelScheduleTable;
    private javax.swing.JPanel panelUser;
    private javax.swing.JPanel panelUserControl;
    private javax.swing.JPanel panelUserForm;
    private javax.swing.JPanel panelUserManagement;
    private javax.swing.JPanel panelUserTable;
    private javax.swing.JSpinner spinnerHourSched;
    private javax.swing.JSpinner spinnerMinutesSched;
    private javax.swing.JTable tableAttendanceData;
    private javax.swing.JTable tableDocumentData;
    private javax.swing.JTable tablePaymentData;
    private javax.swing.JTable tableScheduleData;
    private javax.swing.JTable tableUserData;
    private javax.swing.JTextArea textareaAddress;
    private javax.swing.JTextArea textareaDescriptionDoc;
    private javax.swing.JTextField textfieldAmountPayment;
    private javax.swing.JTextField textfieldEmail;
    private javax.swing.JTextField textfieldFilenameDoc;
    private javax.swing.JTextField textfieldMobile;
    private javax.swing.JTextField textfieldPass;
    private javax.swing.JTextField textfieldTitleDoc;
    private javax.swing.JTextField textfieldUrlDoc;
    private javax.swing.JTextField textfieldUsername;
    // End of variables declaration//GEN-END:variables

    private void loadUserPictureLocally() {

        String propic = configuration.getStringValue(Keys.USER_PROPIC);

        System.out.println("Trying to load " + propic);

        lockUserForm(false);
        labelLoadingStatus.setVisible(false);

        if (!propic.contains("default")) {
            // set the propic
            UIEffect.iconChanger(labelPreviewPicture, (propic));
        }

    }

    private void loadSignaturePictureLocally() {

        String signaturePic = configuration.getStringValue(Keys.SIGNATURE_ATTENDANCE);

        System.out.println("Trying to load " + signaturePic);

        lockAttendanceForm(false);
        labelLoadingStatus.setVisible(false);

        if (!signaturePic.contains("not available")) {
            // set the propic
            UIEffect.iconChanger(labelSignatureAttendance, (signaturePic));

        }

    }

    private void lockUserForm(boolean b) {

        textareaAddress.setEnabled(!b);
        textareaAddress.setEditable(!b);

        textfieldEmail.setEnabled(!b);
        textfieldMobile.setEnabled(!b);
        textfieldPass.setEnabled(!b);
        textfieldUsername.setEnabled(!b);

    }

    private void lockAttendanceForm(boolean b) {

        comboboxClassRegAttendance.setEnabled(!b);
        comboboxStatusAttendance.setEnabled(!b);
        comboboxUsernameAttendance.setEnabled(!b);

        labelBrowseSignatureAttendance.setVisible(!b);

    }

    private void lockScheduleForm(boolean b) {

        listAnotherClassSched.setEnabled(!b);

        spinnerHourSched.setEnabled(!b);
        spinnerMinutesSched.setEnabled(!b);

        comboboxClassRegSched.setEnabled(!b);
        comboboxDaySched.setEnabled(!b);
        comboboxUsernameSched.setEnabled(!b);

    }

    private void lockDocumentForm(boolean b) {

        textareaDescriptionDoc.setEnabled(!b);
        textareaDescriptionDoc.setEditable(!b);

        textfieldUrlDoc.setEnabled(!b);
        textfieldFilenameDoc.setEnabled(!b);
        textfieldTitleDoc.setEnabled(!b);

        comboboxUsernameDoc.setEnabled(!b);

    }

    private void lockPaymentForm(boolean b) {

        textfieldAmountPayment.setEnabled(!b);
        comboboxUsernamePayment.setEnabled(!b);
        comboboxMethodPayment.setEnabled(!b);

        labelScreenshotPayment.setEnabled(!b);

    }

    private void cleanUpUserForm(boolean editWork) {

        editMode = editWork;

        ImageIcon defaultUser = new ImageIcon(getClass().getResource("/images/user.png"));

        textfieldUsername.setText("");
        textareaAddress.setText("");
        textfieldEmail.setText("");
        textfieldMobile.setText("");
        textfieldPass.setText("");

        // this is default entry
        labelPreviewPicture.setIcon(defaultUser);
        propicFile = null;

        if (editMode) {
            // we lock first
            // so later it will be unlocked by async success call

            lockUserForm(editMode);
            labelLoadingStatus.setVisible(true);
        }

    }

    private void cleanUpAttendanceForm(boolean editWork) {

        editMode = editWork;

        comboboxUsernameAttendance.setSelectedIndex(-1);
        comboboxClassRegAttendance.setSelectedIndex(-1);
        comboboxStatusAttendance.setSelectedIndex(-1);

        // this is default entry
        labelSignatureAttendance.setIcon(null);
        labelSignatureAttendance.setText("preview");

        signatureFile = null;

        if (editMode) {
            // we lock first
            // so later it will be unlocked by async success call

            lockAttendanceForm(editMode);
            labelLoadingStatus.setVisible(true);
        }

    }

    private void cleanUpScheduleForm(boolean editWork) {

        editMode = editWork;

        // clearing list
        listAnotherClassSched.setModel(new DefaultListModel());

        spinnerHourSched.setValue(0);
        spinnerMinutesSched.setValue(0);

        comboboxClassRegSched.setSelectedIndex(-1);
        comboboxDaySched.setSelectedIndex(-1);
        comboboxUsernameSched.setSelectedIndex(-1);

        if (editMode) {
            // we lock first
            // so later it will be unlocked by async success call

            lockScheduleForm(editMode);
            labelLoadingStatus.setVisible(true);
        }

    }

    private void cleanUpDocumentForm(boolean editWork) {

        editMode = editWork;

        textfieldTitleDoc.setText("");
        textfieldFilenameDoc.setText("");
        textfieldUrlDoc.setText("");
        textareaDescriptionDoc.setText("");

        comboboxUsernameDoc.setSelectedIndex(-1);
        docFile = null;

        if (editMode) {
            // we lock first
            // so later it will be unlocked by async success call

            lockDocumentForm(editMode);
            labelLoadingStatus.setVisible(true);
        }

    }

    private void cleanUpPaymentForm(boolean editWork) {

        editMode = editWork;

        textfieldAmountPayment.setText("");
        comboboxUsernamePayment.setSelectedIndex(-1);
        comboboxMethodPayment.setSelectedIndex(-1);

        labelScreenshotPayment.setText("preview");
        labelScreenshotPayment.setIcon(null);

        payFile = null;

        if (editMode) {
            // we lock first
            // so later it will be unlocked by async success call

            lockPaymentForm(editMode);
            showLoadingStatus();
        }

    }

    private void renderUsernameForCombobox(User[] dataIn, JComboBox jc) {
        jc.removeAllItems();

        for (User single : dataIn) {
            jc.addItem(single.getUsername());
        }

    }

    private void renderClassRoomForCombobox(ClassRoom[] dataIn, JComboBox jc) {
        jc.removeAllItems();

        for (ClassRoom d : dataIn) {
            jc.addItem(d.getName());
        }
    }

    private void renderScheduleForList(Schedule[] sched, JList elContainer) {

        DefaultListModel dfm = new DefaultListModel();

        for (Schedule s : sched) {
            dfm.addElement(s.getClass_registered() + " - " + s.getTime_schedule());
        }

        elContainer.setModel(dfm);
    }

    @Override
    public void checkResponse(String resp, String urlTarget) {
        Gson objectG = new Gson();

        System.out.println(urlTarget + " have " + resp);
        JSONChecker jchecker = new JSONChecker();

        if (jchecker.isValid(resp)) {
            String innerData = jchecker.getValueAsString("multi_data");

            if (urlTarget.equalsIgnoreCase(WebReference.ALL_USER)) {
                User[] dataIn = objectG.fromJson(innerData, User[].class);
                tabRender.render(tableUserData, dataIn);

                // rendering the username for document ui form
                renderUsernameForCombobox(dataIn, comboboxUsernameDoc);
                renderUsernameForCombobox(dataIn, comboboxUsernameSched);
                renderUsernameForCombobox(dataIn, comboboxUsernameAttendance);
                renderUsernameForCombobox(dataIn, comboboxUsernamePayment);

                hideLoadingStatus();

            } else if (urlTarget.equalsIgnoreCase(WebReference.ALL_CLASSROOM)) {
                ClassRoom[] dataIn = objectG.fromJson(innerData, ClassRoom[].class);

                renderClassRoomForCombobox(dataIn, comboboxClassRegSched);
                renderClassRoomForCombobox(dataIn, comboboxClassRegAttendance);

            } else if (urlTarget.equalsIgnoreCase(WebReference.ALL_SCHEDULE)) {
                Schedule[] dataIn = objectG.fromJson(innerData, Schedule[].class);
                tabRender.render(tableScheduleData, dataIn);

                hideLoadingStatus();

            } else if (urlTarget.equalsIgnoreCase(WebReference.ALL_SCHEDULE_BY_DAY)) {
                Schedule[] dataIn = objectG.fromJson(innerData, Schedule[].class);
                renderScheduleForList(dataIn, listAnotherClassSched);

                hideLoadingStatus();

            } else if (urlTarget.equalsIgnoreCase(WebReference.REGISTER_USER)
                    || urlTarget.equalsIgnoreCase(WebReference.DELETE_USER)
                    || urlTarget.equalsIgnoreCase(WebReference.UPDATE_USER)) {
                // once new user submitted
                // thus we refresh the table
                refreshUser();
            } else if (urlTarget.equalsIgnoreCase(WebReference.ADD_DOCUMENT)
                    || urlTarget.equalsIgnoreCase(WebReference.DELETE_DOCUMENT)
                    || urlTarget.equalsIgnoreCase(WebReference.UPDATE_DOCUMENT)) {
                // thus we refresh the document table
                refreshDocument();
            } else if (urlTarget.equalsIgnoreCase(WebReference.ADD_SCHEDULE)
                    || urlTarget.equalsIgnoreCase(WebReference.DELETE_SCHEDULE)
                    || urlTarget.equalsIgnoreCase(WebReference.UPDATE_SCHEDULE)) {
                // thus we refresh the schedule table
                refreshSchedule();
            } else if (urlTarget.equalsIgnoreCase(WebReference.ADD_ATTENDANCE)
                    || urlTarget.equalsIgnoreCase(WebReference.DELETE_ATTENDANCE)
                    || urlTarget.equalsIgnoreCase(WebReference.UPDATE_ATTENDANCE)) {
                // thus we refresh the attendance table
                refreshAttendance();
            } else if (urlTarget.equalsIgnoreCase(WebReference.PROFILE_USER)) {

                // we got the single user data here
                User dataIn = objectG.fromJson(innerData, User.class);
                renderUserForm(dataIn);

            } else if (urlTarget.contains(WebReference.PICTURE_USER)) {

                System.out.println("Obtaining Picture from web is success...\nNow applying it locally.");
                loadUserPictureLocally();

            } else if (urlTarget.contains(WebReference.SIGNATURE_ATTENDANCE)) {

                System.out.println("Obtaining Signature from web is success...\nNow applying it locally.");
                loadSignaturePictureLocally();

            } else if (urlTarget.equalsIgnoreCase(WebReference.ALL_ATTENDANCE)) {
                Attendance[] dataIn = objectG.fromJson(innerData, Attendance[].class);
                tabRender.render(tableAttendanceData, dataIn);

                hideLoadingStatus();

            } else if (urlTarget.equalsIgnoreCase(WebReference.ALL_DOCUMENT)) {
                Document[] dataIn = objectG.fromJson(innerData, Document[].class);
                tabRender.render(tableDocumentData, dataIn);

                hideLoadingStatus();

            } else if (urlTarget.equalsIgnoreCase(WebReference.DETAIL_ATTENDANCE)) {

                // we got the single attendance data here
                Attendance dataIn = objectG.fromJson(innerData, Attendance.class);
                renderAttendanceForm(dataIn);

            } else if (urlTarget.equalsIgnoreCase(WebReference.DETAIL_DOCUMENT)) {

                // we got the single document data here
                Document dataIn = objectG.fromJson(innerData, Document.class);
                renderDocumentForm(dataIn);

            } else if (urlTarget.equalsIgnoreCase(WebReference.DETAIL_SCHEDULE)) {

                // we got the single schedule data here
                Schedule dataIn = objectG.fromJson(innerData, Schedule.class);
                renderScheduleForm(dataIn);

            }
        } else {

            // when it is invalid but coming from all sched by day
            if (urlTarget.equalsIgnoreCase(WebReference.ALL_SCHEDULE_BY_DAY)) {
                // we clear up the list
                listAnotherClassSched.setModel(new DefaultListModel());
            } else if (urlTarget.equalsIgnoreCase(WebReference.UPDATE_ATTENDANCE)
                    || urlTarget.equalsIgnoreCase(WebReference.UPDATE_DOCUMENT)
                    || urlTarget.equalsIgnoreCase(WebReference.UPDATE_SCHEDULE)
                    || urlTarget.equalsIgnoreCase(WebReference.UPDATE_USER)) {
                showErrorStatus("updating");
            } else if (urlTarget.equalsIgnoreCase(WebReference.ADD_ATTENDANCE)
                    || urlTarget.equalsIgnoreCase(WebReference.ADD_DOCUMENT)
                    || urlTarget.equalsIgnoreCase(WebReference.REGISTER_USER)
                    || urlTarget.equalsIgnoreCase(WebReference.ADD_SCHEDULE)) {
                showErrorStatus("saving new entry");
            }

        }
    }

    private void showErrorStatus(String mes) {
        labelLoadingStatus.setText("Error on " + mes + "...!");
        labelLoadingStatus.setIcon(errorImage);
        labelLoadingStatus.setVisible(true);
    }

    private void hideLoadingStatus() {
        // automatically changed the refresh back to default
        labelRefreshUser.setIcon(refreshImage);
        labelRefreshSchedule.setIcon(refreshImage);
        labelRefreshAttendance.setIcon(refreshImage);
        labelRefreshDocument.setIcon(refreshImage);

        labelLoadingStatus.setVisible(false);
    }

    private void showLoadingStatus() {
        labelLoadingStatus.setText("Loading...");
        labelLoadingStatus.setIcon(loadingImage);
        labelLoadingStatus.setVisible(true);
    }
}
