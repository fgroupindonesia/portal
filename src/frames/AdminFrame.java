/*
 *  This is a Portal Access for Client & Admin Usage
 *  (c) FGroupIndonesia, 2020.
 */
package frames;

import beans.Attendance;
import beans.CertificateStudent;
import beans.ClassRoom;
import beans.Document;
import beans.ExamCategory;
import beans.ExamMultipleChoice;
import beans.ExamQuestion;
import beans.ExamStudentAnswer;
import beans.Payment;
import beans.RBugs;
import beans.Schedule;
import beans.ExamSubCategory;
import beans.User;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.google.gson.Gson;
import helper.CMDExecutor;
import helper.HttpCall;
import helper.JSONChecker;
import helper.PathReference;
import helper.RupiahGenerator;
import helper.SWTKey;
import helper.SWThreadWorker;
import helper.TableRenderer;
import helper.TrayMaker;
import helper.UIDragger;
import helper.UIEffect;
import helper.WebReference;
import helper.preferences.Keys;
import helper.preferences.SettingPreference;
import java.awt.CardLayout;
import java.awt.Desktop;
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
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author ASUS
 */
public class AdminFrame extends javax.swing.JFrame implements HttpCall.HttpProcess {

    File propicFile, docFile, signatureFile, payFile, bugsFile, examPreviewFile,
            certificateStudentFile;
    short idForm;

    // used for exam question form only
    int examQCatID, examQSubCatID;

    // used for Exam Student Answer form only
    int scoreStudentAnswer;

    // used for ClassRoom Form only
    int instructorID;

    // used for Certificate Student form only
    int certExamCatID;
    
    // used for Schedule form only
    int schedExamCatID;

    TableRenderer tabRender = new TableRenderer();
    LoginFrame loginFrame;
    CardLayout cardLayoutInnerCenter;
    CardLayout cardLayoutEntity;
    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(3);
    SettingPreference configuration = new SettingPreference();

    ImageIcon statusOKImage = new ImageIcon(getClass().getResource("/images/ok24.png"));
    ImageIcon statusWRONGImage = new ImageIcon(getClass().getResource("/images/delete24.png"));
    ImageIcon statusCUSTOMImage = new ImageIcon(getClass().getResource("/images/edit24.png"));

    ImageIcon loadingImage = new ImageIcon(getClass().getResource("/images/loadingprel.gif"));
    ImageIcon errorImage = new ImageIcon(getClass().getResource("/images/terminate.png"));
    ImageIcon refreshImage = new ImageIcon(getClass().getResource("/images/refresh16.png"));
    ImageIcon defaultUser = new ImageIcon(getClass().getResource("/images/user.png"));
    ImageIcon defaultExamQuestionPreview = new ImageIcon(getClass().getResource("/images/examprevdefault72.png"));
    ImageIcon defaultCertImage = new ImageIcon(getClass().getResource("/images/newdoc.png"));
    ImageIcon defaultPDFImage = new ImageIcon(getClass().getResource("/images/pdf72.png"));

    TrayMaker tm = new TrayMaker();

    // for form checking before save mechanism
    User userEdited;
    Attendance attendanceEdited;
    Payment paymentEdited;
    Document documentEdited;
    Schedule scheduleEdited;

// for every entity form has this edit mode
    boolean editMode;

    ArrayList<ExamSubCategory> isiSubCategory = new ArrayList<ExamSubCategory>();

    ArrayList<ExamMultipleChoice> isiExamQuestionOptions = new ArrayList<ExamMultipleChoice>();

    /**
     * Creates new form MainAdminFrame
     */
    public AdminFrame() {
        initComponents();
    }

    public AdminFrame(LoginFrame lg) {
        initComponents();

        //prepare the tray usage
        tm.setFrameRef(this);

        loginFrame = lg;
        cardLayoutInnerCenter = (CardLayout) panelInnerCenter.getLayout();

        // request to API for rendering the data to table
        refreshUser();
        refreshDocument();
        refreshSchedule();
        refreshClassRoom();
        refreshAttendance();
        refreshPayment();
        refreshBugsReported();

        refreshExamCategory();
        refreshExamQuestions();
        refreshExamStudentAnswer();
        refreshCertificateStudent();

        // hide the home link
        labelBackToHome.setVisible(false);
        hideLoadingStatus();

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
        } else {
            // we open the form access
            lockUserForm(false);
            hideLoadingStatus();
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
        } else {
            // we open the form access
            lockPaymentForm(false);
            hideLoadingStatus();
        }

    }

    private void renderPaymentForm(Payment dataCome) {

        idForm = (short) dataCome.getId();

        comboboxUsernamePayment.setSelectedItem(dataCome.getUsername());
        textfieldAmountPayment.setText(dataCome.getAmount() + "");
        comboboxMethodPayment.setSelectedItem(UIEffect.decodeSafe(dataCome.getMethod()));

        if (!dataCome.getScreenshot().equalsIgnoreCase("not available")) {
            // we are required to download the image from server
            refreshScreenshotPicture(dataCome.getScreenshot());
        } else {
            // we open the form access
            lockPaymentForm(false);
            hideLoadingStatus();
        }

    }

    private void renderBugsReportedForm(RBugs dataCome) {

        idForm = (short) dataCome.getId();

        comboboxUsernameBugsReported.setSelectedItem(dataCome.getUsername());
        comboboxAppNameBugsReported.setSelectedItem(dataCome.getApp_name());
        textfieldTitleBugsReported.setText(dataCome.getTitle());
        textfieldIPAddressBugsReported.setText(dataCome.getIp_address());
        textAreaDescriptionBugsReported.setText(dataCome.getDescription());

        if (!dataCome.getScreenshot().equalsIgnoreCase("not available")) {
            // we are required to download the image from server
            refreshScreenshotBugsReportedPicture(dataCome.getScreenshot());
        } else {
            // we open the form access
            lockBugsReportedForm(false);
            hideLoadingStatus();
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
        hideLoadingStatus();

    }

    private void renderClassRoomForm(ClassRoom dataCome) {

        idForm = (short) dataCome.getId();

        textfieldNameClassRoom.setText(dataCome.getName());
        textareaDescriptionClassRoom.setText(dataCome.getDescription());
       
        comboboxUsernameClassRoom.setSelectedItem(dataCome.getInstructor_name());

        lockClassRoomForm(false);
        hideLoadingStatus();

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
        hideLoadingStatus();

    }

    private void renderExamCategoryForm(ExamCategory dataCome) {

        idForm = (short) dataCome.getId();

        textfieldTitleExamCategory.setText(dataCome.getTitle());
        textfieldCodeExamCategory.setText(dataCome.getCode());
        textfieldBaseScoreExamCategory.setText("" + dataCome.getScore_base());
        // the locking mechanism will be called by the next call
        // returned by calling examsubcategory
        /*
        lockExamCategoryForm(false);
        hideLoadingStatus();
         */
        // we better do some clearing 
        // mechanism for the table here
        TableRenderer.clearData(tableExamSubCategoryData);

        getAllExamSubCategory("" + dataCome.getId());

    }

    private void renderCertificateStudentForm(CertificateStudent dataCome) {

        idForm = (short) dataCome.getId();

        comboboxCertificateStudentCategory.setSelectedItem(dataCome.getExam_category_title());
        comboboxCertificateStudentUsername.setSelectedItem(dataCome.getStudent_username());

        textfieldDateReleaseCertificateStudent.setText(dataCome.getExam_date_created());

        // status has 2 values: 
        // 1 released
        // 0 waiting
        if (dataCome.getStatus() == 0) {
            radioButtonCertificateStudentWaiting.setSelected(true);
        } else {
            radioButtonCertificateStudentReleased.setSelected(true);
        }

        if (dataCome.getFilename() != null) {

            if (dataCome.getFilename().equalsIgnoreCase("cert-default.png")) {
                labelPreviewCertificateStudent.setIcon(defaultCertImage);
            } else {
                labelPreviewCertificateStudent.setIcon(defaultPDFImage);
            }

            // calling the SERVER through API
            // to download the file itself
            refreshCertificateImage(dataCome.getFilename());

        }

    }

    private void refreshCertificateImage(String filename) {

        // set the path temporarily 
        // for later usage in locally
        PathReference.setCertificateFileName(filename);
        File dest = new File(PathReference.CertificateFilePath);

        configuration.setValue(Keys.CERTIFICATE_PICTURE, dest.getAbsolutePath());

        SWThreadWorker workCertImage = new SWThreadWorker(this);

        // execute the download picture process
        workCertImage.setWork(SWTKey.WORK_REFRESH_CERTIFICATE_PICTURE);
        workCertImage.writeMode(true);
        workCertImage.addData("filename", filename);

        // executorService.submit(workSched);
        executorService.schedule(workCertImage, 2, TimeUnit.SECONDS);

    }

    private void renderExamStudentAnswerForm(ExamStudentAnswer dataCome) {

        idForm = (short) dataCome.getId();

        comboboxUsernameExamStudentAnswer.setSelectedItem(dataCome.getStudent_username());
        textareaAnswerExamStudentAnswer.setText(dataCome.getAnswer());
        labelScoreEarnedStudentAnswer.setText("" + dataCome.getScore_earned());

        numericQuestionIDExamStudentAnswer.setValue(dataCome.getExam_qa_id());

        // this will call the data from server
        getExamQuestion("" + dataCome.getExam_qa_id());

        comboboxStatusExamStudentAnswer.setSelectedItem(dataCome.getStatus());
    }

    private void renderExamQuestionForm(ExamQuestion dataCome) {

        idForm = (short) dataCome.getId();

        // temporarily cleaning up
        isiExamQuestionOptions = new ArrayList<ExamMultipleChoice>();

        textfieldExamQuestion.setText(dataCome.getQuestion());
        textfieldScorePointExamQuestion.setText(dataCome.getScore_point() + "");

        // 1 is pg abcd
        // 2 is essay
        // 3 is pg also ab only
        System.out.println("This is a question with jenis of " + dataCome.getJenis());
        if (dataCome.getJenis() == 1 || dataCome.getJenis() == 3) {
            radiobuttonMultipleChoiceExamQuestion.setSelected(true);

            isiExamQuestionOptions.add(new ExamMultipleChoice(dataCome.getOption_a(), "A", dataCome.getAnswer().equalsIgnoreCase("a")));
            isiExamQuestionOptions.add(new ExamMultipleChoice(dataCome.getOption_b(), "B", dataCome.getAnswer().equalsIgnoreCase("b")));

            // fill the table also
            if (dataCome.getJenis() == 1) {
                isiExamQuestionOptions.add(new ExamMultipleChoice(dataCome.getOption_c(), "C", dataCome.getAnswer().equalsIgnoreCase("c")));
                isiExamQuestionOptions.add(new ExamMultipleChoice(dataCome.getOption_d(), "D", dataCome.getAnswer().equalsIgnoreCase("d")));
            }

            refreshExamQuestionOptionLocally();

        } else if (dataCome.getJenis() == 2) {
            radiobuttonEssayExamQuestion.setSelected(true);
            textAreaAnswerEssayExamQuestion.setText(dataCome.getAnswer());
        }

        comboboxCategoryExamQuestion.setSelectedItem(getExamCategoryNameLocally(dataCome.getExam_category_id()));

        // the locking mechanism will be called by the next call
        // returned by calling examsubcategory
        /*
        lockExamCategoryForm(false);
        hideLoadingStatus();
         */
        // we better do some clearing 
        // mechanism for the table here
        TableRenderer.clearData(tableExamSubCategoryData);

        // calling the SERVER through API
        getAllExamSubCategory("" + dataCome.getId());

        if (!dataCome.getPreview().equalsIgnoreCase("exam-prev-default.png")) {
            // we are required to download the image from server
            refreshExamQuestionsPreview(dataCome.getPreview());
            System.err.println("I found the exam preview file is " + dataCome.getPreview());

        } else {
            // we open the form access
            lockExamQuestionForm(false);
            hideLoadingStatus();
        }

    }

    private void refreshExamQuestionsPreview(String filename) {

        // set the path temporarily 
        // for later usage in locally
        PathReference.setExamQuestionPreviewFileName(filename);
        File dest = new File(PathReference.ExamQuestionPreviewPath);

        configuration.setValue(Keys.EXAM_QUESTION_PREVIEW, dest.getAbsolutePath());

        SWThreadWorker workPicture = new SWThreadWorker(this);

        // execute the download picture process
        workPicture.setWork(SWTKey.WORK_REFRESH_EXAM_QUESTION_PREVIEW);
        workPicture.writeMode(true);
        workPicture.addData("preview", filename);

        // executorService.submit(workSched);
        executorService.schedule(workPicture, 2, TimeUnit.SECONDS);

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

    private void refreshScreenshotBugsReportedPicture(String filename) {

        // set the path temporarily 
        // for later usage in locally
        File dest = new File(PathReference.getScreenshotBugsReportedPath(filename));

        configuration.setValue(Keys.SCREENSHOT_REPORT_BUGS, dest.getAbsolutePath());

        SWThreadWorker workScreenshot = new SWThreadWorker(this);

        // execute the download picture process
        workScreenshot.setWork(SWTKey.WORK_REFRESH_SCREENSHOT_REPORT_BUGS);
        workScreenshot.writeMode(true);
        workScreenshot.addData("screenshot", filename);

        // executorService.submit(workSched);
        executorService.schedule(workScreenshot, 2, TimeUnit.SECONDS);

    }

    private void refreshScreenshotPicture(String filename) {

        // set the path temporarily 
        // for later usage in locally
        PathReference.setScreenshotPaymentFileName(filename);
        File dest = new File(PathReference.ScreenshotPaymentPath);

        configuration.setValue(Keys.SCREENSHOT_LAST_PAYMENT, dest.getAbsolutePath());

        SWThreadWorker workScreenshot = new SWThreadWorker(this);

        // execute the download picture process
        workScreenshot.setWork(SWTKey.WORK_REFRESH_SCREENSHOT_PAYMENT);
        workScreenshot.writeMode(true);
        workScreenshot.addData("screenshot", filename);

        // executorService.submit(workSched);
        executorService.schedule(workScreenshot, 2, TimeUnit.SECONDS);

    }

    private void deleteSignaturePicture() {

        // clear up the path temporarily 
        PathReference.setSignatureFileName("");

        configuration.setValue(Keys.SIGNATURE_ATTENDANCE, "");

        SWThreadWorker workSignature = new SWThreadWorker(this);

        // execute the download picture process
        workSignature.setWork(SWTKey.WORK_DELETE_SIGNATURE);
        workSignature.addData("id", idForm + "");

        prepareToken(workSignature);
        // executorService.submit(workSched);
        executorService.schedule(workSignature, 2, TimeUnit.SECONDS);

    }

    private void deleteExamQuestionPreview() {

        // clear up the path temporarily 
        PathReference.setExamQuestionPreviewFileName("");

        configuration.setValue(Keys.EXAM_QUESTION_PREVIEW, "");

        SWThreadWorker workPropic = new SWThreadWorker(this);

        // execute the deleting picture process
        workPropic.setWork(SWTKey.WORK_DELETE_EXAM_QUESTION_PREVIEW);
        workPropic.addData("id", idForm + "");

        prepareToken(workPropic);
        // executorService.submit(workSched);
        executorService.schedule(workPropic, 2, TimeUnit.SECONDS);

    }

    private void deleteCertificatePicture() {

        // clear up the path temporarily 
        PathReference.setCertificateFileName("");

        configuration.setValue(Keys.CERTIFICATE_PICTURE, "");

        SWThreadWorker workCert = new SWThreadWorker(this);

        // execute the download picture process
        workCert.setWork(SWTKey.WORK_DELETE_CERTIFICATE_PICTURE);
        workCert.addData("id", idForm + "");

        prepareToken(workCert);
        // executorService.submit(workSched);
        executorService.schedule(workCert, 2, TimeUnit.SECONDS);

    }

    private void deleteUserPicture() {

        // clear up the path temporarily 
        PathReference.setPropicFileName("");

        configuration.setValue(Keys.USER_PROPIC, "");

        SWThreadWorker workPropic = new SWThreadWorker(this);

        // execute the download picture process
        workPropic.setWork(SWTKey.WORK_DELETE_USER_PICTURE);
        workPropic.addData("id", idForm + "");

        prepareToken(workPropic);
        // executorService.submit(workSched);
        executorService.schedule(workPropic, 2, TimeUnit.SECONDS);

    }

    private void deleteScreenshotPicture() {

        // clear up the path temporarily 
        PathReference.setScreenshotPaymentFileName("");

        configuration.setValue(Keys.SCREENSHOT_LAST_PAYMENT, "");

        SWThreadWorker workScreenshot = new SWThreadWorker(this);

        // execute the download picture process
        workScreenshot.setWork(SWTKey.WORK_DELETE_SCREENSHOT_PAYMENT);
        workScreenshot.addData("id", idForm + "");

        prepareToken(workScreenshot);
        // executorService.submit(workSched);
        executorService.schedule(workScreenshot, 2, TimeUnit.SECONDS);

    }

    private void refreshUser() {

        SWThreadWorker workUser = new SWThreadWorker(this);
        workUser.setWork(SWTKey.WORK_REFRESH_USER);
        prepareToken(workUser);
        executorService.schedule(workUser, 2, TimeUnit.SECONDS);

        // we clear up the table first manually
        // since not all API call will return value
        TableRenderer.clearData(tableUserData);

    }

    private void refreshAttendance() {

        SWThreadWorker workAttendance = new SWThreadWorker(this);
        workAttendance.setWork(SWTKey.WORK_REFRESH_ATTENDANCE);
        workAttendance.addData("username", "admin");

        prepareToken(workAttendance);
        executorService.schedule(workAttendance, 2, TimeUnit.SECONDS);

        // we clear up the table first manually
        // since not all API call will return value
        TableRenderer.clearData(tableAttendanceData);

    }

    private void refreshExamQuestions() {

        SWThreadWorker workExamQ = new SWThreadWorker(this);
        workExamQ.setWork(SWTKey.WORK_REFRESH_EXAM_QUESTIONS);
        //workExamCat.addData("username", "admin");

        prepareToken(workExamQ);
        executorService.schedule(workExamQ, 2, TimeUnit.SECONDS);

        // we clear up the table first manually
        // since not all API call will return value
        TableRenderer.clearData(tableExamQuestionData);

    }

    private void refreshExamCategory() {

        SWThreadWorker workExamCat = new SWThreadWorker(this);
        workExamCat.setWork(SWTKey.WORK_REFRESH_EXAM_CATEGORY);
        //workExamCat.addData("username", "admin");

        prepareToken(workExamCat);
        executorService.schedule(workExamCat, 2, TimeUnit.SECONDS);

        // we clear up the table first manually
        // since not all API call will return value
        TableRenderer.clearData(tableExamCategoryData);

    }

    private void refreshCertificateStudent() {

        SWThreadWorker workCertStudent = new SWThreadWorker(this);
        workCertStudent.setWork(SWTKey.WORK_REFRESH_CERTIFICATE_STUDENT);
        //workExamCat.addData("username", "admin");

        prepareToken(workCertStudent);
        executorService.schedule(workCertStudent, 2, TimeUnit.SECONDS);

        TableRenderer.clearData(tableCertificateStudentData);

    }

    private void refreshExamStudentAnswer() {

        SWThreadWorker workExamStudentAns = new SWThreadWorker(this);
        workExamStudentAns.setWork(SWTKey.WORK_REFRESH_EXAM_STUDENT_ANS);
        //workExamCat.addData("username", "admin");

        prepareToken(workExamStudentAns);
        executorService.schedule(workExamStudentAns, 2, TimeUnit.SECONDS);

        // we clear up the table first manually
        // since not all API call will return value
        TableRenderer.clearData(tableExamStudentAnswerData);

    }

    private void refreshScheduleByDay(String dayName) {

        SWThreadWorker workSchedule = new SWThreadWorker(this);
        workSchedule.setWork(SWTKey.WORK_REFRESH_SCHEDULE_BY_DAY);
        workSchedule.addData("day_schedule", dayName);
        prepareToken(workSchedule);
        executorService.schedule(workSchedule, 2, TimeUnit.SECONDS);

        // we clear up the table first manually
        // since not all API call will return value
        TableRenderer.clearData(tableScheduleData);

    }

    private void refreshDocument() {

        SWThreadWorker workDoc = new SWThreadWorker(this);
        workDoc.setWork(SWTKey.WORK_REFRESH_DOCUMENT);
        workDoc.addData("username", "admin");
        prepareToken(workDoc);
        executorService.schedule(workDoc, 2, TimeUnit.SECONDS);

        // we clear up the table first manually
        // since not all API call will return value
        TableRenderer.clearData(tableDocumentData);

    }

    private void refreshPayment() {

        SWThreadWorker workPay = new SWThreadWorker(this);
        workPay.setWork(SWTKey.WORK_REFRESH_PAYMENT);
        workPay.addData("username", "admin");
        prepareToken(workPay);
        executorService.schedule(workPay, 2, TimeUnit.SECONDS);

        // we clear up the table first manually
        // since not all API call will return value
        TableRenderer.clearData(tablePaymentData);

    }

    private void refreshBugsReported() {

        SWThreadWorker workBugs = new SWThreadWorker(this);
        workBugs.setWork(SWTKey.WORK_REFRESH_REPORT_BUGS);
        workBugs.addData("username", "admin");
        prepareToken(workBugs);
        executorService.schedule(workBugs, 2, TimeUnit.SECONDS);

        // we clear up the table first manually
        // since not all API call will return value
        TableRenderer.clearData(tableBugsReportedData);

    }

    private void refreshSchedule() {

        SWThreadWorker workSched = new SWThreadWorker(this);
        workSched.setWork(SWTKey.WORK_REFRESH_SCHEDULE);
        workSched.addData("username", "admin");
        prepareToken(workSched);
        executorService.schedule(workSched, 2, TimeUnit.SECONDS);

        // we clear up the table first manually
        // since not all API call will return value
        TableRenderer.clearData(tableScheduleData);

    }

    private void refreshClassRoom() {

        SWThreadWorker workClassRoom = new SWThreadWorker(this);
        workClassRoom.setWork(SWTKey.WORK_REFRESH_CLASSROOM);
        prepareToken(workClassRoom);
        executorService.schedule(workClassRoom, 2, TimeUnit.SECONDS);

        TableRenderer.clearData(tableClassRoomData);
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

    private void getExamQuestion(String idIn) {

        SWThreadWorker workExamQ = new SWThreadWorker(this);
        workExamQ.setWork(SWTKey.WORK_EXAM_QUESTION_EDIT);
        workExamQ.addData("id", idIn);
        prepareToken(workExamQ);
        executorService.schedule(workExamQ, 2, TimeUnit.SECONDS);

    }

    private void getExamCategory(String idIn) {

        SWThreadWorker workExamCat = new SWThreadWorker(this);
        workExamCat.setWork(SWTKey.WORK_EXAM_CATEGORY_EDIT);
        workExamCat.addData("id", idIn);
        prepareToken(workExamCat);
        executorService.schedule(workExamCat, 2, TimeUnit.SECONDS);

    }

    private void getClassRoom(String idIn) {

        SWThreadWorker workClassR = new SWThreadWorker(this);
        workClassR.setWork(SWTKey.WORK_CLASSROOM_EDIT);
        workClassR.addData("id", idIn);
        prepareToken(workClassR);
        executorService.schedule(workClassR, 2, TimeUnit.SECONDS);

    }

    private void getCertificateStudent(String idIn) {

        SWThreadWorker workCertStudent = new SWThreadWorker(this);
        workCertStudent.setWork(SWTKey.WORK_CERTIFICATE_STUDENT_EDIT);
        workCertStudent.addData("id", idIn);
        prepareToken(workCertStudent);
        executorService.schedule(workCertStudent, 2, TimeUnit.SECONDS);

    }

    private void getExamStudentAnswer(String idIn) {

        SWThreadWorker workExamStudentAns = new SWThreadWorker(this);
        workExamStudentAns.setWork(SWTKey.WORK_EXAM_STUDENT_ANS_EDIT);
        workExamStudentAns.addData("id", idIn);
        prepareToken(workExamStudentAns);
        executorService.schedule(workExamStudentAns, 2, TimeUnit.SECONDS);

    }

    // this is sub call
    private void getAllExamSubCategory(String idIn) {

        // the id come here is the main parent
        // exam category id as reference
        SWThreadWorker workExamSubCat = new SWThreadWorker(this);
        workExamSubCat.setWork(SWTKey.WORK_REFRESH_EXAM_SUBCATEGORY);
        workExamSubCat.addData("exam_category_id", idIn);
        prepareToken(workExamSubCat);
        executorService.schedule(workExamSubCat, 2, TimeUnit.SECONDS);

    }

    private void getDocument(int anID) {

        SWThreadWorker workDoc = new SWThreadWorker(this);
        workDoc.setWork(SWTKey.WORK_DOCUMENT_EDIT);
        workDoc.addData("id", anID + "");
        prepareToken(workDoc);
        executorService.schedule(workDoc, 2, TimeUnit.SECONDS);

    }

    private void getBugsReported(int anID) {

        SWThreadWorker workBugs = new SWThreadWorker(this);
        workBugs.setWork(SWTKey.WORK_REPORT_BUGS_EDIT);
        workBugs.addData("id", anID + "");
        prepareToken(workBugs);
        executorService.schedule(workBugs, 2, TimeUnit.SECONDS);

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

        RupiahGenerator rpg = new RupiahGenerator();

        workPaymentEntity.addData("amount", rpg.getIntNumber(textfieldAmountPayment.getText()) + "");
        workPaymentEntity.addData("method", comboboxMethodPayment.getSelectedItem().toString());
        workPaymentEntity.addData("username", comboboxUsernamePayment.getSelectedItem().toString());

        if (payFile != null) {
            workPaymentEntity.addFile("screenshot", payFile);
        }

        prepareToken(workPaymentEntity);
        executorService.schedule(workPaymentEntity, 2, TimeUnit.SECONDS);

    }

    private void saveBugsReported() {

        showLoadingStatus();
        SWThreadWorker workBugsEntity = new SWThreadWorker(this);

        // saving new data
        workBugsEntity.setWork(SWTKey.WORK_REPORT_BUGS_SAVE);

        workBugsEntity.addData("app_name", comboboxAppNameBugsReported.getSelectedItem().toString());
        workBugsEntity.addData("username", comboboxUsernameBugsReported.getSelectedItem().toString());
        workBugsEntity.addData("title", textfieldTitleBugsReported.getText());
        workBugsEntity.addData("description", textAreaDescriptionBugsReported.getText());

        if (bugsFile != null) {
            workBugsEntity.addFile("screenshot", bugsFile);
        }

        prepareToken(workBugsEntity);
        executorService.schedule(workBugsEntity, 2, TimeUnit.SECONDS);

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

    private void saveExamCategory() {

        showLoadingStatus();

        SWThreadWorker workExamCategoryEntity = new SWThreadWorker(this);

        // check whether this is edit or new form?
        if (editMode) {
            // updating data
            workExamCategoryEntity.setWork(SWTKey.WORK_EXAM_CATEGORY_UPDATE);
            workExamCategoryEntity.addData("id", idForm + "");
        } else {
            // saving new data
            workExamCategoryEntity.setWork(SWTKey.WORK_EXAM_CATEGORY_SAVE);

        }

        // we also push the sub_category data here
        if (isiSubCategory != null) {
            if (isiSubCategory.size() > 0) {

                String dataJSON = new Gson().toJson(isiSubCategory);
                System.err.println("we have a sub category data too " + dataJSON);
                workExamCategoryEntity.addData("json", dataJSON);
            }
        }

        System.out.println("Adding a data before executing API Request...");

        workExamCategoryEntity.addData("title", textfieldTitleExamCategory.getText());
        workExamCategoryEntity.addData("code", textfieldCodeExamCategory.getText());
        workExamCategoryEntity.addData("score_base", textfieldBaseScoreExamCategory.getText());

        prepareToken(workExamCategoryEntity);
        executorService.schedule(workExamCategoryEntity, 2, TimeUnit.SECONDS);

    }

    private void saveClassRoom() {

        showLoadingStatus();

        SWThreadWorker workClassR = new SWThreadWorker(this);

        // check whether this is edit or new form?
        if (editMode) {
            // updating data
            workClassR.setWork(SWTKey.WORK_CLASSROOM_UPDATE);
            workClassR.addData("id", idForm + "");
        } else {
            // saving new data
            workClassR.setWork(SWTKey.WORK_CLASSROOM_SAVE);
        }

        workClassR.addData("instructor_id", instructorID + "");
        workClassR.addData("name", textfieldNameClassRoom.getText());
        workClassR.addData("description", textareaDescriptionClassRoom.getText());

        prepareToken(workClassR);
        executorService.schedule(workClassR, 2, TimeUnit.SECONDS);

    }

    private void saveCertificateStudent() {

        showLoadingStatus();

        SWThreadWorker workCertStudent = new SWThreadWorker(this);

        // check whether this is edit or new form?
        if (editMode) {
            // updating data
            workCertStudent.setWork(SWTKey.WORK_CERTIFICATE_STUDENT_UPDATE);
            workCertStudent.addData("id", idForm + "");
        } else {
            // saving new data
            workCertStudent.setWork(SWTKey.WORK_CERTIFICATE_STUDENT_SAVE);

        }

        System.out.println("Adding a data before executing API Request...");

        workCertStudent.addData("username", comboboxCertificateStudentUsername.getSelectedItem().toString());
        workCertStudent.addData("exam_category_id", certExamCatID + "");
        workCertStudent.addData("exam_category_title", comboboxCertificateStudentCategory.getSelectedItem().toString());
        workCertStudent.addData("exam_date_created", textfieldDateReleaseCertificateStudent.getText());

        String statCert = null;

        // stat is 1 for released
        // stat 0 for waiting
        if (radioButtonCertificateStudentReleased.isSelected()) {
            statCert = "1";
        } else {
            statCert = "0";
        }

        workCertStudent.addData("status", statCert);

        if (certificateStudentFile != null) {

            System.out.println("--------- found certificate file -----------");
            workCertStudent.addFile("filename", certificateStudentFile);
        }

        prepareToken(workCertStudent);
        executorService.schedule(workCertStudent, 2, TimeUnit.SECONDS);

    }

    private void saveExamStudentAnswer() {

        showLoadingStatus();

        SWThreadWorker workExamSA = new SWThreadWorker(this);

        // check whether this is edit or new form?
        if (editMode) {
            // updating data
            workExamSA.setWork(SWTKey.WORK_EXAM_STUDENT_ANS_UPDATE);
            workExamSA.addData("id", idForm + "");
        } else {
            // saving new data
            workExamSA.setWork(SWTKey.WORK_EXAM_STUDENT_ANS_SAVE);
        }

        String statusStudentAnswer = comboboxStatusExamStudentAnswer.getSelectedItem().toString();

        workExamSA.addData("username", comboboxUsernameExamStudentAnswer.getSelectedItem().toString());
        workExamSA.addData("answer", textareaAnswerExamStudentAnswer.getText());
        workExamSA.addData("status", statusStudentAnswer);
        workExamSA.addData("exam_qa_id", numericQuestionIDExamStudentAnswer.getValue() + "");

        if (statusStudentAnswer.equalsIgnoreCase("custom")) {
            workExamSA.addData("score_earned", labelScoreEarnedStudentAnswer.getText().substring(1));
        } else if (statusStudentAnswer.equalsIgnoreCase("ok")) {
            workExamSA.addData("score_earned", "" + scoreStudentAnswer);
        } else {
            workExamSA.addData("score_earned", "0");
        }

        prepareToken(workExamSA);
        executorService.schedule(workExamSA, 2, TimeUnit.SECONDS);

    }

    private void saveUser() {
        showLoadingStatus();
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

    private void saveExamQuestion() {
        showLoadingStatus();
        SWThreadWorker workExamQAEntity = new SWThreadWorker(this);

        // check whether this is edit or new form?
        if (editMode) {
            // updating data
            workExamQAEntity.setWork(SWTKey.WORK_EXAM_QUESTION_UPDATE);
            workExamQAEntity.addData("id", idForm + "");
        } else {
            // saving new data
            workExamQAEntity.setWork(SWTKey.WORK_EXAM_QUESTION_SAVE);
        }

        workExamQAEntity.addData("question", textfieldExamQuestion.getText());

        // 1 = pg abcd
        // 2 = essay
        // 3 = pg benar salah
        int jenisExamQ = 0;
        if (radiobuttonEssayExamQuestion.isSelected()) {
            jenisExamQ = 2;
        } else {

            if (tableExamQuestionOptions.getRowCount() == 2) {
                jenisExamQ = 3;
            } else {
                jenisExamQ = 1;
            }
        }

        workExamQAEntity.addData("jenis", jenisExamQ + "");

        workExamQAEntity.addData("score_point", textfieldScorePointExamQuestion.getText());
        workExamQAEntity.addData("exam_category_id", "" + examQCatID);
        workExamQAEntity.addData("exam_sub_category_id", "" + examQSubCatID);

        if (radiobuttonMultipleChoiceExamQuestion.isSelected()) {
            // we take the options a-d
            workExamQAEntity.addData("option_a", tabRender.getValueWithParameter(tableExamQuestionOptions, "A", 1, 3));
            workExamQAEntity.addData("option_b", tabRender.getValueWithParameter(tableExamQuestionOptions, "B", 1, 3));

            if (tableExamQuestionOptions.getRowCount() > 2) {
                // if there are many options c,d
                // thus we took them all
                workExamQAEntity.addData("option_c", tabRender.getValueWithParameter(tableExamQuestionOptions, "C", 1, 3));
                workExamQAEntity.addData("option_d", tabRender.getValueWithParameter(tableExamQuestionOptions, "D", 1, 3));
            }

            workExamQAEntity.addData("answer", tabRender.getValueWithParameter(tableExamQuestionOptions, "true", 2, 1));

        } else {
            // if this is not multiple choice,
            // thus this must be an essay
            workExamQAEntity.addData("answer", textAreaAnswerEssayExamQuestion.getText());
        }

        // for file exam preview
        if (examPreviewFile != null) {
            workExamQAEntity.addFile("preview", examPreviewFile);
        }

        prepareToken(workExamQAEntity);
        executorService.schedule(workExamQAEntity, 2, TimeUnit.SECONDS);

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

        showLoadingStatus();

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

    private void deleteExamQuestion(ArrayList<String> dataIn) {

        // for exam category usage the d is actually a number (Integer)
        for (String d : dataIn) {
            SWThreadWorker workExamQ = new SWThreadWorker(this);
            workExamQ.addData("id", d);
            workExamQ.setWork(SWTKey.WORK_EXAM_QUESTION_DELETE);
            prepareToken(workExamQ);
            executorService.schedule(workExamQ, 1, TimeUnit.SECONDS);
        }

    }

    private void deleteExamCategory(ArrayList<String> dataIn) {

        // for exam category usage the d is actually a number (Integer)
        for (String d : dataIn) {
            SWThreadWorker workExamCat = new SWThreadWorker(this);
            workExamCat.addData("id", d);
            workExamCat.setWork(SWTKey.WORK_EXAM_CATEGORY_DELETE);
            prepareToken(workExamCat);
            executorService.schedule(workExamCat, 1, TimeUnit.SECONDS);
        }

    }

    private void deleteClassRoom(ArrayList<String> dataIn) {

        // for exam category usage the d is actually a number (Integer)
        for (String d : dataIn) {
            SWThreadWorker workClassR = new SWThreadWorker(this);
            workClassR.addData("id", d);
            workClassR.setWork(SWTKey.WORK_CLASSROOM_DELETE);
            prepareToken(workClassR);
            executorService.schedule(workClassR, 1, TimeUnit.SECONDS);
        }

    }

    private void deleteCertificateStudent(ArrayList<String> dataIn) {

        // for exam category usage the d is actually a number (Integer)
        for (String d : dataIn) {
            SWThreadWorker workCertificateStudent = new SWThreadWorker(this);
            workCertificateStudent.addData("id", d);
            workCertificateStudent.setWork(SWTKey.WORK_CERTIFICATE_STUDENT_DELETE);
            prepareToken(workCertificateStudent);
            executorService.schedule(workCertificateStudent, 1, TimeUnit.SECONDS);
        }

    }

    private void deleteExamStudentAnswer(ArrayList<String> dataIn) {

        // for exam category usage the d is actually a number (Integer)
        for (String d : dataIn) {
            SWThreadWorker workExamStudentAns = new SWThreadWorker(this);
            workExamStudentAns.addData("id", d);
            workExamStudentAns.setWork(SWTKey.WORK_EXAM_STUDENT_ANS_DELETE);
            prepareToken(workExamStudentAns);
            executorService.schedule(workExamStudentAns, 1, TimeUnit.SECONDS);
        }

    }

    private void deleteExamSubCategory(int anID) {

        // for exam sub category usage the d is actually a number (Integer)
        SWThreadWorker workExamSubCat = new SWThreadWorker(this);
        workExamSubCat.addData("id", "" + anID);
        workExamSubCat.setWork(SWTKey.WORK_EXAM_SUBCATEGORY_DELETE);
        prepareToken(workExamSubCat);
        executorService.schedule(workExamSubCat, 1, TimeUnit.SECONDS);

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

    private void deleteBugsReported(ArrayList<String> dataIn) {

        // for bugsreported usage the d is actually a number (Integer)
        for (String d : dataIn) {
            SWThreadWorker workBugs = new SWThreadWorker(this);
            workBugs.addData("id", d);
            workBugs.setWork(SWTKey.WORK_REPORT_BUGS_DELETE);
            prepareToken(workBugs);
            executorService.schedule(workBugs, 1, TimeUnit.SECONDS);
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
        radioButtonGroupTypeExamQuestion = new javax.swing.ButtonGroup();
        radioButtonGroupStatusCertificate = new javax.swing.ButtonGroup();
        panelHeader = new javax.swing.JPanel();
        labelClose = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        labelTime = new javax.swing.JLabel();
        labelMinimize = new javax.swing.JLabel();
        panelCenter = new javax.swing.JPanel();
        panelInnerCenter = new javax.swing.JPanel();
        panelHomeMenu = new javax.swing.JPanel();
        buttonUserManagement = new javax.swing.JButton();
        buttonDocumentManagement = new javax.swing.JButton();
        buttonAttendance = new javax.swing.JButton();
        buttonPayment = new javax.swing.JButton();
        buttonSchedule = new javax.swing.JButton();
        buttonBugsReported = new javax.swing.JButton();
        buttonClassRoom = new javax.swing.JButton();
        buttonLogout = new javax.swing.JButton();
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
        labelLinkChangePicture = new javax.swing.JLabel();
        buttonSaveUserForm = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        labelPreviewPicture = new javax.swing.JLabel();
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
        panelExamCategorySched = new javax.swing.JPanel();
        comboboxExamCategorySched = new javax.swing.JComboBox<>();
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
        panelBugsReported = new javax.swing.JPanel();
        panelBugsReportedManagement = new javax.swing.JPanel();
        panelReportedBugsControl = new javax.swing.JPanel();
        buttonViewBugsReported = new javax.swing.JButton();
        buttonDeleteBugsReported = new javax.swing.JButton();
        labelBugsReportedManagement = new javax.swing.JLabel();
        labelRefreshBugsReported = new javax.swing.JLabel();
        panelReportedBugsTable = new javax.swing.JPanel();
        jScrollPane9 = new javax.swing.JScrollPane();
        tableBugsReportedData = new javax.swing.JTable();
        panelBugsReportedForm = new javax.swing.JPanel();
        buttonCancelBugsReportedForm = new javax.swing.JButton();
        jLabel29 = new javax.swing.JLabel();
        buttonSaveBugsReportedForm = new javax.swing.JButton();
        comboboxUsernameBugsReported = new javax.swing.JComboBox<>();
        jLabel30 = new javax.swing.JLabel();
        labelBrowseScreenshotBugsReported = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        labelScreenshotBugsReported = new javax.swing.JLabel();
        textfieldTitleBugsReported = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        jScrollPane10 = new javax.swing.JScrollPane();
        textAreaDescriptionBugsReported = new javax.swing.JTextArea();
        jLabel32 = new javax.swing.JLabel();
        comboboxAppNameBugsReported = new javax.swing.JComboBox<>();
        textfieldIPAddressBugsReported = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        panelExamMenu = new javax.swing.JPanel();
        buttonExamCategory = new javax.swing.JButton();
        buttonExamStudentAnswer = new javax.swing.JButton();
        buttonExamQuestions = new javax.swing.JButton();
        buttonStudentCertificate = new javax.swing.JButton();
        buttonXXX = new javax.swing.JButton();
        buttonXXXX = new javax.swing.JButton();
        buttonBugsReported1 = new javax.swing.JButton();
        buttonLogout1 = new javax.swing.JButton();
        panelExamCategory = new javax.swing.JPanel();
        panelExamCategoryManagement = new javax.swing.JPanel();
        panelExamCategoryControl = new javax.swing.JPanel();
        buttonAddExamCategory = new javax.swing.JButton();
        buttonEditExamCategory = new javax.swing.JButton();
        buttonDeleteExamCategory = new javax.swing.JButton();
        labelExamCategoryManagement = new javax.swing.JLabel();
        labelRefreshExamCategoryManagement = new javax.swing.JLabel();
        panelExamCategoryTable = new javax.swing.JPanel();
        jScrollPane11 = new javax.swing.JScrollPane();
        tableExamCategoryData = new javax.swing.JTable();
        panelExamCategoryForm = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        buttonCancelExamCategoryForm = new javax.swing.JButton();
        textfieldTitleExamCategory = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        textfieldCodeExamCategory = new javax.swing.JTextField();
        buttonSaveExamCategoryForm = new javax.swing.JButton();
        jLabel34 = new javax.swing.JLabel();
        jScrollPane12 = new javax.swing.JScrollPane();
        tableExamSubCategoryData = new javax.swing.JTable();
        editExamSubCategory = new javax.swing.JLabel();
        addExamSubCategory = new javax.swing.JLabel();
        deleteExamSubCategory = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        textfieldBaseScoreExamCategory = new javax.swing.JTextField();
        panelExamStudentAnswer = new javax.swing.JPanel();
        panelExamStudentAnswerManagement = new javax.swing.JPanel();
        panelExamStudentAnswerControl = new javax.swing.JPanel();
        buttonAddExamStudentAnswer = new javax.swing.JButton();
        buttonEditExamStudentAnswer = new javax.swing.JButton();
        buttonDeleteExamStudentAnswer = new javax.swing.JButton();
        labelExamStudentAnswerManagement = new javax.swing.JLabel();
        labelRefreshExamStudentAnswerManagement = new javax.swing.JLabel();
        panelExamStudentAnswerTable = new javax.swing.JPanel();
        jScrollPane13 = new javax.swing.JScrollPane();
        tableExamStudentAnswerData = new javax.swing.JTable();
        panelExamStudentAnswerForm = new javax.swing.JPanel();
        jLabel35 = new javax.swing.JLabel();
        buttonCancelExamStudentAnswerForm = new javax.swing.JButton();
        jLabel36 = new javax.swing.JLabel();
        buttonSaveExamStudentAnswerForm = new javax.swing.JButton();
        jLabel37 = new javax.swing.JLabel();
        labelIconStatusExamStudentAnswer = new javax.swing.JLabel();
        jScrollPane15 = new javax.swing.JScrollPane();
        textareaQuestionExamStudentAnswer = new javax.swing.JTextArea();
        jScrollPane14 = new javax.swing.JScrollPane();
        textareaAnswerExamStudentAnswer = new javax.swing.JTextArea();
        jLabel38 = new javax.swing.JLabel();
        comboboxStatusExamStudentAnswer = new javax.swing.JComboBox<>();
        labelScoreEarnedStudentAnswer = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        numericQuestionIDExamStudentAnswer = new javax.swing.JSpinner();
        comboboxUsernameExamStudentAnswer = new javax.swing.JComboBox<>();
        buttonRefreshExamQuestionDetail = new javax.swing.JButton();
        panelExamQuestions = new javax.swing.JPanel();
        panelExamQuestionsManagement = new javax.swing.JPanel();
        panelExamQuestionControl = new javax.swing.JPanel();
        buttonAddExamQuestion = new javax.swing.JButton();
        buttonEditExamQuestion = new javax.swing.JButton();
        buttonDeleteExamQuestion = new javax.swing.JButton();
        labelExamCategoryManagement1 = new javax.swing.JLabel();
        labelRefreshExamQuestionManagement = new javax.swing.JLabel();
        panelExamQuestionTable = new javax.swing.JPanel();
        jScrollPane16 = new javax.swing.JScrollPane();
        tableExamQuestionData = new javax.swing.JTable();
        panelExamQuestionForm = new javax.swing.JPanel();
        buttonCancelExamQuestionForm = new javax.swing.JButton();
        jLabel41 = new javax.swing.JLabel();
        buttonSaveExamQuestionForm = new javax.swing.JButton();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        textfieldScorePointExamQuestion = new javax.swing.JTextField();
        jLabel44 = new javax.swing.JLabel();
        comboboxSubCategoryExamQuestion = new javax.swing.JComboBox<>();
        comboboxCategoryExamQuestion = new javax.swing.JComboBox<>();
        textfieldExamQuestion = new javax.swing.JTextField();
        jLabel45 = new javax.swing.JLabel();
        radiobuttonMultipleChoiceExamQuestion = new javax.swing.JRadioButton();
        radiobuttonEssayExamQuestion = new javax.swing.JRadioButton();
        jPanel5 = new javax.swing.JPanel();
        labelPreviewExamQuestion = new javax.swing.JLabel();
        labelBrowseExamPreviewImage = new javax.swing.JLabel();
        panelAnswerExamQuestion = new javax.swing.JPanel();
        panelMultipleChoiceExamQuestion = new javax.swing.JPanel();
        addOptionsExamQuestion = new javax.swing.JLabel();
        editOptionsExamQuestion = new javax.swing.JLabel();
        deleteOptionsExamQuestion = new javax.swing.JLabel();
        jScrollPane17 = new javax.swing.JScrollPane();
        tableExamQuestionOptions = new javax.swing.JTable();
        panelEssayExamQuestion = new javax.swing.JPanel();
        jScrollPane19 = new javax.swing.JScrollPane();
        textAreaAnswerEssayExamQuestion = new javax.swing.JTextArea();
        panelCertificateStudent = new javax.swing.JPanel();
        panelCertificateStudentManagement = new javax.swing.JPanel();
        panelCertificateStudentControl = new javax.swing.JPanel();
        buttonAddCertificateStudent = new javax.swing.JButton();
        buttonEditCertificateStudent = new javax.swing.JButton();
        buttonDeleteCertificateStudent = new javax.swing.JButton();
        labelCertificateStudentManagement = new javax.swing.JLabel();
        labelRefreshCertificateStudentManagement = new javax.swing.JLabel();
        panelCertificateStudentTable = new javax.swing.JPanel();
        jScrollPane18 = new javax.swing.JScrollPane();
        tableCertificateStudentData = new javax.swing.JTable();
        panelCertificateStudentForm = new javax.swing.JPanel();
        jLabel46 = new javax.swing.JLabel();
        buttonCancelCertificateStudentForm = new javax.swing.JButton();
        jLabel47 = new javax.swing.JLabel();
        buttonSaveCertificateStudentForm = new javax.swing.JButton();
        jLabel48 = new javax.swing.JLabel();
        addFileCertificateStudent = new javax.swing.JLabel();
        deleteFileCertificateStudent = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        comboboxCertificateStudentUsername = new javax.swing.JComboBox<>();
        comboboxCertificateStudentCategory = new javax.swing.JComboBox<>();
        radioButtonCertificateStudentWaiting = new javax.swing.JRadioButton();
        radioButtonCertificateStudentReleased = new javax.swing.JRadioButton();
        labelPreviewCertificateStudent = new javax.swing.JLabel();
        DatePickerSettings dateSettings = new DatePickerSettings();
        dateSettings.setFormatForDatesCommonEra("yyyy-MM-dd");
        textfieldDateReleaseCertificateStudent = new com.github.lgooddatepicker.components.DatePicker(dateSettings);
        panelClassRoom = new javax.swing.JPanel();
        panelClassRoomManagement = new javax.swing.JPanel();
        panelClassRoomControl = new javax.swing.JPanel();
        buttonAddClassRoom = new javax.swing.JButton();
        buttonEditClassRoom = new javax.swing.JButton();
        buttonDeleteClassRoom = new javax.swing.JButton();
        labelRefreshClassRoom = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        panelClassRoomTable = new javax.swing.JPanel();
        jScrollPane20 = new javax.swing.JScrollPane();
        tableClassRoomData = new javax.swing.JTable();
        panelClassRoomForm = new javax.swing.JPanel();
        jLabel52 = new javax.swing.JLabel();
        buttonCancelClassRoomForm = new javax.swing.JButton();
        textfieldNameClassRoom = new javax.swing.JTextField();
        jLabel53 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        jScrollPane21 = new javax.swing.JScrollPane();
        textareaDescriptionClassRoom = new javax.swing.JTextArea();
        buttonSaveClassRoomForm = new javax.swing.JButton();
        comboboxUsernameClassRoom = new javax.swing.JComboBox<>();
        labelNextMenu = new javax.swing.JLabel();
        labelBottomPadding = new javax.swing.JLabel();
        labelBackToHome = new javax.swing.JLabel();
        labelLoadingStatus = new javax.swing.JLabel();
        labelRightPadding = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Portal Access - FGroupIndonesia");
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
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                formMouseReleased(evt);
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

        panelHomeMenu.setLayout(new java.awt.GridLayout(2, 0, 20, 25));

        buttonUserManagement.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/user.png"))); // NOI18N
        buttonUserManagement.setText("Users");
        buttonUserManagement.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        buttonUserManagement.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonUserManagement.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonUserManagement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonUserManagementActionPerformed(evt);
            }
        });
        panelHomeMenu.add(buttonUserManagement);

        buttonDocumentManagement.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/file.png"))); // NOI18N
        buttonDocumentManagement.setText("Documents");
        buttonDocumentManagement.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        buttonDocumentManagement.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonDocumentManagement.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonDocumentManagement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDocumentManagementActionPerformed(evt);
            }
        });
        panelHomeMenu.add(buttonDocumentManagement);

        buttonAttendance.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/calendar64.png"))); // NOI18N
        buttonAttendance.setText("Attendance");
        buttonAttendance.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        buttonAttendance.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonAttendance.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonAttendance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAttendanceActionPerformed(evt);
            }
        });
        panelHomeMenu.add(buttonAttendance);

        buttonPayment.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/cash64.png"))); // NOI18N
        buttonPayment.setText("Payment");
        buttonPayment.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        buttonPayment.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonPayment.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonPayment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonPaymentActionPerformed(evt);
            }
        });
        panelHomeMenu.add(buttonPayment);

        buttonSchedule.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/time64.png"))); // NOI18N
        buttonSchedule.setText("Schedule");
        buttonSchedule.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        buttonSchedule.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonSchedule.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonSchedule.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonScheduleActionPerformed(evt);
            }
        });
        panelHomeMenu.add(buttonSchedule);

        buttonBugsReported.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bug64.png"))); // NOI18N
        buttonBugsReported.setText("Bugs Reported");
        buttonBugsReported.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        buttonBugsReported.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonBugsReported.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonBugsReported.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonBugsReportedActionPerformed(evt);
            }
        });
        panelHomeMenu.add(buttonBugsReported);

        buttonClassRoom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/classroom.png"))); // NOI18N
        buttonClassRoom.setText("Class Room");
        buttonClassRoom.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        buttonClassRoom.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonClassRoom.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonClassRoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClassRoomActionPerformed(evt);
            }
        });
        panelHomeMenu.add(buttonClassRoom);

        buttonLogout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lock64.png"))); // NOI18N
        buttonLogout.setText("Logout");
        buttonLogout.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        buttonLogout.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonLogout.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonLogoutActionPerformed(evt);
            }
        });
        panelHomeMenu.add(buttonLogout);

        panelInnerCenter.add(panelHomeMenu, "panelHomeMenu");

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
            tableUserData.getColumnModel().getColumn(3).setMinWidth(80);
            tableUserData.getColumnModel().getColumn(3).setPreferredWidth(80);
            tableUserData.getColumnModel().getColumn(3).setMaxWidth(80);
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

        textfieldUsername.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textfieldUsernameKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                textfieldUsernameKeyTyped(evt);
            }
        });
        panelUserForm.add(textfieldUsername, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 200, -1));

        jLabel5.setText("Password :");
        panelUserForm.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 100, 150, -1));

        textfieldPass.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textfieldPassKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                textfieldPassKeyTyped(evt);
            }
        });
        panelUserForm.add(textfieldPass, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 120, 200, -1));

        jLabel6.setText("Email :");
        panelUserForm.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 160, 150, -1));

        textfieldEmail.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textfieldEmailKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                textfieldEmailKeyTyped(evt);
            }
        });
        panelUserForm.add(textfieldEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 180, 200, -1));

        jLabel7.setText("Address :");
        panelUserForm.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 40, 150, -1));

        textareaAddress.setColumns(20);
        textareaAddress.setLineWrap(true);
        textareaAddress.setRows(5);
        textareaAddress.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textareaAddressKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                textareaAddressKeyTyped(evt);
            }
        });
        jScrollPane2.setViewportView(textareaAddress);

        panelUserForm.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 60, -1, 100));

        textfieldMobile.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textfieldMobileKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                textfieldMobileKeyTyped(evt);
            }
        });
        panelUserForm.add(textfieldMobile, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 240, 200, -1));

        jLabel8.setText("Mobile :");
        panelUserForm.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 220, 150, -1));

        labelLinkChangePicture.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        labelLinkChangePicture.setForeground(new java.awt.Color(0, 0, 204));
        labelLinkChangePicture.setText("<html><u>Browse Picture</u></html>");
        labelLinkChangePicture.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelLinkChangePicture.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelLinkChangePictureMouseClicked(evt);
            }
        });
        panelUserForm.add(labelLinkChangePicture, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 190, -1, -1));

        buttonSaveUserForm.setText("Save");
        buttonSaveUserForm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveUserFormActionPerformed(evt);
            }
        });
        panelUserForm.add(buttonSaveUserForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(555, 250, 60, -1));

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Profile Picture"));
        jPanel3.setLayout(new java.awt.BorderLayout());

        labelPreviewPicture.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPreviewPicture.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/user.png"))); // NOI18N
        labelPreviewPicture.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelPreviewPicture.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        labelPreviewPicture.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelPreviewPictureMouseClicked(evt);
            }
        });
        jPanel3.add(labelPreviewPicture, java.awt.BorderLayout.CENTER);

        panelUserForm.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 170, 120, 120));

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
            tableDocumentData.getColumnModel().getColumn(4).setMinWidth(0);
            tableDocumentData.getColumnModel().getColumn(4).setPreferredWidth(0);
            tableDocumentData.getColumnModel().getColumn(4).setMaxWidth(0);
            tableDocumentData.getColumnModel().getColumn(6).setHeaderValue("Url");
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
        comboboxClassRegSched.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboboxClassRegSchedActionPerformed(evt);
            }
        });
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
        panelScheduleForm.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 40, 220, -1));

        jScrollPane7.setViewportView(listAnotherClassSched);

        panelScheduleForm.add(jScrollPane7, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 60, 160, 100));

        panelExamCategorySched.setBorder(javax.swing.BorderFactory.createTitledBorder("Exam Category"));
        panelExamCategorySched.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        comboboxExamCategorySched.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboboxExamCategorySched.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboboxExamCategorySchedActionPerformed(evt);
            }
        });
        panelExamCategorySched.add(comboboxExamCategorySched, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, 160, -1));

        panelScheduleForm.add(panelExamCategorySched, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 170, 200, 70));

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
        labelSignatureAttendance.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelSignatureAttendanceMouseClicked(evt);
            }
        });
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
        labelScreenshotPayment.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelScreenshotPayment.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelScreenshotPaymentMouseClicked(evt);
            }
        });
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

        panelBugsReported.setLayout(new java.awt.CardLayout());

        panelBugsReportedManagement.setLayout(new java.awt.BorderLayout());

        panelReportedBugsControl.setPreferredSize(new java.awt.Dimension(658, 40));
        panelReportedBugsControl.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        buttonViewBugsReported.setText("View");
        buttonViewBugsReported.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonViewBugsReportedActionPerformed(evt);
            }
        });
        panelReportedBugsControl.add(buttonViewBugsReported, new org.netbeans.lib.awtextra.AbsoluteConstraints(514, 5, 60, -1));

        buttonDeleteBugsReported.setText("Delete");
        buttonDeleteBugsReported.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeleteBugsReportedActionPerformed(evt);
            }
        });
        panelReportedBugsControl.add(buttonDeleteBugsReported, new org.netbeans.lib.awtextra.AbsoluteConstraints(589, 5, -1, -1));

        labelBugsReportedManagement.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        labelBugsReportedManagement.setText("Bugs Reported Management");
        panelReportedBugsControl.add(labelBugsReportedManagement, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 280, 40));

        labelRefreshBugsReported.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/refresh16.png"))); // NOI18N
        labelRefreshBugsReported.setText("Refresh");
        labelRefreshBugsReported.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelRefreshBugsReported.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelRefreshBugsReportedMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                labelRefreshBugsReportedMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                labelRefreshBugsReportedMouseExited(evt);
            }
        });
        panelReportedBugsControl.add(labelRefreshBugsReported, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 10, 70, 20));

        panelBugsReportedManagement.add(panelReportedBugsControl, java.awt.BorderLayout.PAGE_START);

        panelReportedBugsTable.setLayout(new java.awt.BorderLayout());

        tableBugsReportedData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "[ x ]", "Id", "App Name", "Username", "IP Address", "Title", "Description", "Screenshot", "Date Created"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane9.setViewportView(tableBugsReportedData);
        if (tableBugsReportedData.getColumnModel().getColumnCount() > 0) {
            tableBugsReportedData.getColumnModel().getColumn(0).setMinWidth(30);
            tableBugsReportedData.getColumnModel().getColumn(0).setPreferredWidth(30);
            tableBugsReportedData.getColumnModel().getColumn(0).setMaxWidth(30);
            tableBugsReportedData.getColumnModel().getColumn(1).setMinWidth(0);
            tableBugsReportedData.getColumnModel().getColumn(1).setPreferredWidth(0);
            tableBugsReportedData.getColumnModel().getColumn(1).setMaxWidth(0);
            tableBugsReportedData.getColumnModel().getColumn(2).setMinWidth(100);
            tableBugsReportedData.getColumnModel().getColumn(2).setPreferredWidth(100);
            tableBugsReportedData.getColumnModel().getColumn(2).setMaxWidth(100);
            tableBugsReportedData.getColumnModel().getColumn(3).setMinWidth(100);
            tableBugsReportedData.getColumnModel().getColumn(3).setPreferredWidth(100);
            tableBugsReportedData.getColumnModel().getColumn(3).setMaxWidth(100);
            tableBugsReportedData.getColumnModel().getColumn(4).setMinWidth(80);
            tableBugsReportedData.getColumnModel().getColumn(4).setPreferredWidth(80);
            tableBugsReportedData.getColumnModel().getColumn(4).setMaxWidth(80);
            tableBugsReportedData.getColumnModel().getColumn(5).setMinWidth(80);
            tableBugsReportedData.getColumnModel().getColumn(5).setPreferredWidth(80);
            tableBugsReportedData.getColumnModel().getColumn(5).setMaxWidth(80);
        }

        panelReportedBugsTable.add(jScrollPane9, java.awt.BorderLayout.CENTER);

        panelBugsReportedManagement.add(panelReportedBugsTable, java.awt.BorderLayout.CENTER);

        panelBugsReported.add(panelBugsReportedManagement, "panelBugsReportedManagement");

        panelBugsReportedForm.setBorder(javax.swing.BorderFactory.createTitledBorder("Bugs Reported Form"));
        panelBugsReportedForm.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        buttonCancelBugsReportedForm.setText("Cancel");
        buttonCancelBugsReportedForm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelBugsReportedFormActionPerformed(evt);
            }
        });
        panelBugsReportedForm.add(buttonCancelBugsReportedForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 250, -1, -1));

        jLabel29.setText("Description :");
        panelBugsReportedForm.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 160, 150, -1));

        buttonSaveBugsReportedForm.setText("Save");
        buttonSaveBugsReportedForm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveBugsReportedFormActionPerformed(evt);
            }
        });
        panelBugsReportedForm.add(buttonSaveBugsReportedForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(555, 250, 60, -1));

        comboboxUsernameBugsReported.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        panelBugsReportedForm.add(comboboxUsernameBugsReported, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 200, -1));

        jLabel30.setText("Username : ");
        panelBugsReportedForm.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, 150, -1));

        labelBrowseScreenshotBugsReported.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        labelBrowseScreenshotBugsReported.setForeground(new java.awt.Color(0, 51, 255));
        labelBrowseScreenshotBugsReported.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelBrowseScreenshotBugsReported.setText("<html><u>Browse Picture</u></html>");
        labelBrowseScreenshotBugsReported.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelBrowseScreenshotBugsReported.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelBrowseScreenshotBugsReportedMouseClicked(evt);
            }
        });
        panelBugsReportedForm.add(labelBrowseScreenshotBugsReported, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 210, 120, -1));

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Screenshot"));
        jPanel4.setLayout(new java.awt.BorderLayout());

        labelScreenshotBugsReported.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelScreenshotBugsReported.setText("preview");
        labelScreenshotBugsReported.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelScreenshotBugsReported.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelScreenshotBugsReportedMouseClicked(evt);
            }
        });
        jPanel4.add(labelScreenshotBugsReported, java.awt.BorderLayout.CENTER);

        panelBugsReportedForm.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 40, 210, 170));

        textfieldTitleBugsReported.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textfieldTitleBugsReportedFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                textfieldTitleBugsReportedFocusLost(evt);
            }
        });
        panelBugsReportedForm.add(textfieldTitleBugsReported, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 120, 190, -1));

        jLabel31.setText("Title :");
        panelBugsReportedForm.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 100, 150, -1));

        jScrollPane10.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        textAreaDescriptionBugsReported.setColumns(20);
        textAreaDescriptionBugsReported.setRows(5);
        jScrollPane10.setViewportView(textAreaDescriptionBugsReported);

        panelBugsReportedForm.add(jScrollPane10, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 190, 230, 90));

        jLabel32.setText("App name :");
        panelBugsReportedForm.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 40, 150, -1));

        comboboxAppNameBugsReported.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "portal access", "fgi mobile" }));
        panelBugsReportedForm.add(comboboxAppNameBugsReported, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 60, 170, -1));

        textfieldIPAddressBugsReported.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textfieldIPAddressBugsReportedFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                textfieldIPAddressBugsReportedFocusLost(evt);
            }
        });
        panelBugsReportedForm.add(textfieldIPAddressBugsReported, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 120, 160, -1));

        jLabel33.setText("IP Address:");
        panelBugsReportedForm.add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 100, 150, -1));

        panelBugsReported.add(panelBugsReportedForm, "panelBugsReportedForm");

        panelInnerCenter.add(panelBugsReported, "panelBugsReported");

        panelExamMenu.setLayout(new java.awt.GridLayout(2, 0, 20, 25));

        buttonExamCategory.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/category.png"))); // NOI18N
        buttonExamCategory.setText("Exam Category");
        buttonExamCategory.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        buttonExamCategory.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonExamCategory.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonExamCategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonExamCategoryActionPerformed(evt);
            }
        });
        panelExamMenu.add(buttonExamCategory);

        buttonExamStudentAnswer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/option64.png"))); // NOI18N
        buttonExamStudentAnswer.setText("Exam Student Answer");
        buttonExamStudentAnswer.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        buttonExamStudentAnswer.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonExamStudentAnswer.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonExamStudentAnswer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonExamStudentAnswerActionPerformed(evt);
            }
        });
        panelExamMenu.add(buttonExamStudentAnswer);

        buttonExamQuestions.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/newdoc.png"))); // NOI18N
        buttonExamQuestions.setText("Exam Questions");
        buttonExamQuestions.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        buttonExamQuestions.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonExamQuestions.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonExamQuestions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonExamQuestionsActionPerformed(evt);
            }
        });
        panelExamMenu.add(buttonExamQuestions);

        buttonStudentCertificate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/cert.png"))); // NOI18N
        buttonStudentCertificate.setText("Student Certificates");
        buttonStudentCertificate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        buttonStudentCertificate.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonStudentCertificate.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonStudentCertificate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStudentCertificateActionPerformed(evt);
            }
        });
        panelExamMenu.add(buttonStudentCertificate);

        buttonXXX.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/warning64.png"))); // NOI18N
        buttonXXX.setText("-");
        buttonXXX.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        buttonXXX.setEnabled(false);
        buttonXXX.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonXXX.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonXXX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonXXXActionPerformed(evt);
            }
        });
        panelExamMenu.add(buttonXXX);

        buttonXXXX.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/warning64.png"))); // NOI18N
        buttonXXXX.setText("-");
        buttonXXXX.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        buttonXXXX.setEnabled(false);
        buttonXXXX.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonXXXX.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonXXXX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonXXXXActionPerformed(evt);
            }
        });
        panelExamMenu.add(buttonXXXX);

        buttonBugsReported1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/warning64.png"))); // NOI18N
        buttonBugsReported1.setText("-");
        buttonBugsReported1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        buttonBugsReported1.setEnabled(false);
        buttonBugsReported1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonBugsReported1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonBugsReported1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonBugsReported1ActionPerformed(evt);
            }
        });
        panelExamMenu.add(buttonBugsReported1);

        buttonLogout1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/warning64.png"))); // NOI18N
        buttonLogout1.setText("-");
        buttonLogout1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        buttonLogout1.setEnabled(false);
        buttonLogout1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonLogout1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonLogout1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonLogout1ActionPerformed(evt);
            }
        });
        panelExamMenu.add(buttonLogout1);

        panelInnerCenter.add(panelExamMenu, "panelExamMenu");

        panelExamCategory.setLayout(new java.awt.CardLayout());

        panelExamCategoryManagement.setLayout(new java.awt.BorderLayout());

        panelExamCategoryControl.setPreferredSize(new java.awt.Dimension(658, 40));
        panelExamCategoryControl.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        buttonAddExamCategory.setText("Add");
        buttonAddExamCategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddExamCategoryActionPerformed(evt);
            }
        });
        panelExamCategoryControl.add(buttonAddExamCategory, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 5, 60, -1));

        buttonEditExamCategory.setText("Edit");
        buttonEditExamCategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonEditExamCategoryActionPerformed(evt);
            }
        });
        panelExamCategoryControl.add(buttonEditExamCategory, new org.netbeans.lib.awtextra.AbsoluteConstraints(514, 5, 60, -1));

        buttonDeleteExamCategory.setText("Delete");
        buttonDeleteExamCategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeleteExamCategoryActionPerformed(evt);
            }
        });
        panelExamCategoryControl.add(buttonDeleteExamCategory, new org.netbeans.lib.awtextra.AbsoluteConstraints(589, 5, -1, -1));

        labelExamCategoryManagement.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        labelExamCategoryManagement.setText("Exam Category Management");
        panelExamCategoryControl.add(labelExamCategoryManagement, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 280, 40));

        labelRefreshExamCategoryManagement.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/refresh16.png"))); // NOI18N
        labelRefreshExamCategoryManagement.setText("Refresh");
        labelRefreshExamCategoryManagement.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelRefreshExamCategoryManagement.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelRefreshExamCategoryManagementMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                labelRefreshExamCategoryManagementMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                labelRefreshExamCategoryManagementMouseExited(evt);
            }
        });
        panelExamCategoryControl.add(labelRefreshExamCategoryManagement, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 10, 70, 20));

        panelExamCategoryManagement.add(panelExamCategoryControl, java.awt.BorderLayout.PAGE_START);

        panelExamCategoryTable.setLayout(new java.awt.BorderLayout());

        tableExamCategoryData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "[ x ]", "Id", "Title", "Code"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane11.setViewportView(tableExamCategoryData);
        if (tableExamCategoryData.getColumnModel().getColumnCount() > 0) {
            tableExamCategoryData.getColumnModel().getColumn(0).setMinWidth(30);
            tableExamCategoryData.getColumnModel().getColumn(0).setPreferredWidth(30);
            tableExamCategoryData.getColumnModel().getColumn(0).setMaxWidth(30);
            tableExamCategoryData.getColumnModel().getColumn(1).setMinWidth(0);
            tableExamCategoryData.getColumnModel().getColumn(1).setPreferredWidth(0);
            tableExamCategoryData.getColumnModel().getColumn(1).setMaxWidth(0);
            tableExamCategoryData.getColumnModel().getColumn(3).setMinWidth(80);
            tableExamCategoryData.getColumnModel().getColumn(3).setPreferredWidth(80);
            tableExamCategoryData.getColumnModel().getColumn(3).setMaxWidth(80);
        }

        panelExamCategoryTable.add(jScrollPane11, java.awt.BorderLayout.CENTER);

        panelExamCategoryManagement.add(panelExamCategoryTable, java.awt.BorderLayout.CENTER);

        panelExamCategory.add(panelExamCategoryManagement, "panelExamCategoryManagement");

        panelExamCategoryForm.setBorder(javax.swing.BorderFactory.createTitledBorder("Exam Category Form"));
        panelExamCategoryForm.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel15.setText("Sub Categories :");
        panelExamCategoryForm.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 40, 150, -1));

        buttonCancelExamCategoryForm.setText("Cancel");
        buttonCancelExamCategoryForm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelExamCategoryFormActionPerformed(evt);
            }
        });
        panelExamCategoryForm.add(buttonCancelExamCategoryForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 250, -1, -1));

        textfieldTitleExamCategory.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textfieldTitleExamCategoryKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                textfieldTitleExamCategoryKeyTyped(evt);
            }
        });
        panelExamCategoryForm.add(textfieldTitleExamCategory, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 200, -1));

        jLabel28.setText("Base Score :");
        panelExamCategoryForm.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 160, 150, -1));

        textfieldCodeExamCategory.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textfieldCodeExamCategoryKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                textfieldCodeExamCategoryKeyTyped(evt);
            }
        });
        panelExamCategoryForm.add(textfieldCodeExamCategory, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 120, 200, -1));

        buttonSaveExamCategoryForm.setText("Save");
        buttonSaveExamCategoryForm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveExamCategoryFormActionPerformed(evt);
            }
        });
        panelExamCategoryForm.add(buttonSaveExamCategoryForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(555, 250, 60, -1));

        jLabel34.setText("Title :");
        panelExamCategoryForm.add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, 150, -1));

        tableExamSubCategoryData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "ID", "Title"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tableExamSubCategoryData.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tableExamSubCategoryDataFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tableExamSubCategoryDataFocusLost(evt);
            }
        });
        jScrollPane12.setViewportView(tableExamSubCategoryData);
        if (tableExamSubCategoryData.getColumnModel().getColumnCount() > 0) {
            tableExamSubCategoryData.getColumnModel().getColumn(0).setMinWidth(30);
            tableExamSubCategoryData.getColumnModel().getColumn(0).setPreferredWidth(30);
            tableExamSubCategoryData.getColumnModel().getColumn(0).setMaxWidth(30);
            tableExamSubCategoryData.getColumnModel().getColumn(1).setMinWidth(0);
            tableExamSubCategoryData.getColumnModel().getColumn(1).setPreferredWidth(0);
            tableExamSubCategoryData.getColumnModel().getColumn(1).setMaxWidth(0);
        }

        panelExamCategoryForm.add(jScrollPane12, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 70, 380, 140));

        editExamSubCategory.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/edit24.png"))); // NOI18N
        editExamSubCategory.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        editExamSubCategory.setEnabled(false);
        editExamSubCategory.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                editExamSubCategoryMouseClicked(evt);
            }
        });
        panelExamCategoryForm.add(editExamSubCategory, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 40, -1, -1));

        addExamSubCategory.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add24.png"))); // NOI18N
        addExamSubCategory.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        addExamSubCategory.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addExamSubCategoryMouseClicked(evt);
            }
        });
        panelExamCategoryForm.add(addExamSubCategory, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 40, -1, -1));

        deleteExamSubCategory.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete24.png"))); // NOI18N
        deleteExamSubCategory.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        deleteExamSubCategory.setEnabled(false);
        deleteExamSubCategory.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                deleteExamSubCategoryMouseClicked(evt);
            }
        });
        panelExamCategoryForm.add(deleteExamSubCategory, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 40, -1, -1));

        jLabel39.setText("Code :");
        panelExamCategoryForm.add(jLabel39, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 100, 150, -1));

        textfieldBaseScoreExamCategory.setText("0");
        panelExamCategoryForm.add(textfieldBaseScoreExamCategory, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 180, 110, -1));

        panelExamCategory.add(panelExamCategoryForm, "panelExamCategoryForm");

        panelInnerCenter.add(panelExamCategory, "panelExamCategory");

        panelExamStudentAnswer.setLayout(new java.awt.CardLayout());

        panelExamStudentAnswerManagement.setLayout(new java.awt.BorderLayout());

        panelExamStudentAnswerControl.setPreferredSize(new java.awt.Dimension(658, 40));
        panelExamStudentAnswerControl.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        buttonAddExamStudentAnswer.setText("Add");
        buttonAddExamStudentAnswer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddExamStudentAnswerActionPerformed(evt);
            }
        });
        panelExamStudentAnswerControl.add(buttonAddExamStudentAnswer, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 5, 60, -1));

        buttonEditExamStudentAnswer.setText("Edit");
        buttonEditExamStudentAnswer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonEditExamStudentAnswerActionPerformed(evt);
            }
        });
        panelExamStudentAnswerControl.add(buttonEditExamStudentAnswer, new org.netbeans.lib.awtextra.AbsoluteConstraints(514, 5, 60, -1));

        buttonDeleteExamStudentAnswer.setText("Delete");
        buttonDeleteExamStudentAnswer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeleteExamStudentAnswerActionPerformed(evt);
            }
        });
        panelExamStudentAnswerControl.add(buttonDeleteExamStudentAnswer, new org.netbeans.lib.awtextra.AbsoluteConstraints(589, 5, -1, -1));

        labelExamStudentAnswerManagement.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        labelExamStudentAnswerManagement.setText("Exam St. Answer Management");
        panelExamStudentAnswerControl.add(labelExamStudentAnswerManagement, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 280, 40));

        labelRefreshExamStudentAnswerManagement.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/refresh16.png"))); // NOI18N
        labelRefreshExamStudentAnswerManagement.setText("Refresh");
        labelRefreshExamStudentAnswerManagement.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelRefreshExamStudentAnswerManagement.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelRefreshExamStudentAnswerManagementMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                labelRefreshExamStudentAnswerManagementMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                labelRefreshExamStudentAnswerManagementMouseExited(evt);
            }
        });
        panelExamStudentAnswerControl.add(labelRefreshExamStudentAnswerManagement, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 10, 70, 20));

        panelExamStudentAnswerManagement.add(panelExamStudentAnswerControl, java.awt.BorderLayout.PAGE_START);

        panelExamStudentAnswerTable.setLayout(new java.awt.BorderLayout());

        tableExamStudentAnswerData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "[ x ]", "Id", "Username", "Answer", "Score Earned", "Status", "Date Created"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane13.setViewportView(tableExamStudentAnswerData);
        if (tableExamStudentAnswerData.getColumnModel().getColumnCount() > 0) {
            tableExamStudentAnswerData.getColumnModel().getColumn(0).setMinWidth(30);
            tableExamStudentAnswerData.getColumnModel().getColumn(0).setPreferredWidth(30);
            tableExamStudentAnswerData.getColumnModel().getColumn(0).setMaxWidth(30);
            tableExamStudentAnswerData.getColumnModel().getColumn(1).setMinWidth(0);
            tableExamStudentAnswerData.getColumnModel().getColumn(1).setPreferredWidth(0);
            tableExamStudentAnswerData.getColumnModel().getColumn(1).setMaxWidth(0);
            tableExamStudentAnswerData.getColumnModel().getColumn(3).setMinWidth(80);
            tableExamStudentAnswerData.getColumnModel().getColumn(3).setPreferredWidth(80);
            tableExamStudentAnswerData.getColumnModel().getColumn(3).setMaxWidth(80);
        }

        panelExamStudentAnswerTable.add(jScrollPane13, java.awt.BorderLayout.CENTER);

        panelExamStudentAnswerManagement.add(panelExamStudentAnswerTable, java.awt.BorderLayout.CENTER);

        panelExamStudentAnswer.add(panelExamStudentAnswerManagement, "panelExamStudentAnswerManagement");

        panelExamStudentAnswerForm.setBorder(javax.swing.BorderFactory.createTitledBorder("Exam Student Answer Form"));
        panelExamStudentAnswerForm.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel35.setText("Score :");
        panelExamStudentAnswerForm.add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 260, 60, -1));

        buttonCancelExamStudentAnswerForm.setText("Cancel");
        buttonCancelExamStudentAnswerForm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelExamStudentAnswerFormActionPerformed(evt);
            }
        });
        panelExamStudentAnswerForm.add(buttonCancelExamStudentAnswerForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 250, -1, -1));

        jLabel36.setText("Answer :");
        panelExamStudentAnswerForm.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 100, 150, -1));

        buttonSaveExamStudentAnswerForm.setText("Save");
        buttonSaveExamStudentAnswerForm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveExamStudentAnswerFormActionPerformed(evt);
            }
        });
        panelExamStudentAnswerForm.add(buttonSaveExamStudentAnswerForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(555, 250, 60, -1));

        jLabel37.setText("Username :");
        panelExamStudentAnswerForm.add(jLabel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, 150, -1));

        labelIconStatusExamStudentAnswer.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelIconStatusExamStudentAnswer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelIconStatusExamStudentAnswerMouseClicked(evt);
            }
        });
        panelExamStudentAnswerForm.add(labelIconStatusExamStudentAnswer, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 220, 30, 30));

        textareaQuestionExamStudentAnswer.setColumns(20);
        textareaQuestionExamStudentAnswer.setRows(5);
        jScrollPane15.setViewportView(textareaQuestionExamStudentAnswer);

        panelExamStudentAnswerForm.add(jScrollPane15, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 66, 380, 120));

        textareaAnswerExamStudentAnswer.setColumns(20);
        textareaAnswerExamStudentAnswer.setRows(5);
        jScrollPane14.setViewportView(textareaAnswerExamStudentAnswer);

        panelExamStudentAnswerForm.add(jScrollPane14, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 120, 200, 150));

        jLabel38.setText("Question ID :");
        panelExamStudentAnswerForm.add(jLabel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 40, 90, -1));

        comboboxStatusExamStudentAnswer.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "OK", "WRONG", "CUSTOM" }));
        comboboxStatusExamStudentAnswer.setSelectedIndex(1);
        comboboxStatusExamStudentAnswer.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboboxStatusExamStudentAnswerItemStateChanged(evt);
            }
        });
        comboboxStatusExamStudentAnswer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboboxStatusExamStudentAnswerActionPerformed(evt);
            }
        });
        panelExamStudentAnswerForm.add(comboboxStatusExamStudentAnswer, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 220, -1, -1));

        labelScoreEarnedStudentAnswer.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        labelScoreEarnedStudentAnswer.setText("0");
        panelExamStudentAnswerForm.add(labelScoreEarnedStudentAnswer, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 250, 50, 30));

        jLabel40.setText("Status :");
        panelExamStudentAnswerForm.add(jLabel40, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 220, 70, -1));

        numericQuestionIDExamStudentAnswer.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                numericQuestionIDExamStudentAnswerStateChanged(evt);
            }
        });
        panelExamStudentAnswerForm.add(numericQuestionIDExamStudentAnswer, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 40, 50, -1));

        panelExamStudentAnswerForm.add(comboboxUsernameExamStudentAnswer, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 180, -1));

        buttonRefreshExamQuestionDetail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/refresh16.png"))); // NOI18N
        buttonRefreshExamQuestionDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRefreshExamQuestionDetailActionPerformed(evt);
            }
        });
        panelExamStudentAnswerForm.add(buttonRefreshExamQuestionDetail, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 37, 30, -1));

        panelExamStudentAnswer.add(panelExamStudentAnswerForm, "panelExamStudentAnswerForm");

        panelInnerCenter.add(panelExamStudentAnswer, "panelExamStudentAnswer");

        panelExamQuestions.setLayout(new java.awt.CardLayout());

        panelExamQuestionsManagement.setLayout(new java.awt.BorderLayout());

        panelExamQuestionControl.setPreferredSize(new java.awt.Dimension(658, 40));
        panelExamQuestionControl.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        buttonAddExamQuestion.setText("Add");
        buttonAddExamQuestion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddExamQuestionActionPerformed(evt);
            }
        });
        panelExamQuestionControl.add(buttonAddExamQuestion, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 5, 60, -1));

        buttonEditExamQuestion.setText("Edit");
        buttonEditExamQuestion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonEditExamQuestionActionPerformed(evt);
            }
        });
        panelExamQuestionControl.add(buttonEditExamQuestion, new org.netbeans.lib.awtextra.AbsoluteConstraints(514, 5, 60, -1));

        buttonDeleteExamQuestion.setText("Delete");
        buttonDeleteExamQuestion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeleteExamQuestionActionPerformed(evt);
            }
        });
        panelExamQuestionControl.add(buttonDeleteExamQuestion, new org.netbeans.lib.awtextra.AbsoluteConstraints(589, 5, -1, -1));

        labelExamCategoryManagement1.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        labelExamCategoryManagement1.setText("Exam Question Management");
        panelExamQuestionControl.add(labelExamCategoryManagement1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 280, 40));

        labelRefreshExamQuestionManagement.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/refresh16.png"))); // NOI18N
        labelRefreshExamQuestionManagement.setText("Refresh");
        labelRefreshExamQuestionManagement.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelRefreshExamQuestionManagement.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelRefreshExamQuestionManagementMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                labelRefreshExamQuestionManagementMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                labelRefreshExamQuestionManagementMouseExited(evt);
            }
        });
        panelExamQuestionControl.add(labelRefreshExamQuestionManagement, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 10, 70, 20));

        panelExamQuestionsManagement.add(panelExamQuestionControl, java.awt.BorderLayout.PAGE_START);

        panelExamQuestionTable.setLayout(new java.awt.BorderLayout());

        tableExamQuestionData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "[ x ]", "Id", "Question", "Jenis", "Answer", "Score Point", "Preview"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane16.setViewportView(tableExamQuestionData);
        if (tableExamQuestionData.getColumnModel().getColumnCount() > 0) {
            tableExamQuestionData.getColumnModel().getColumn(0).setMinWidth(30);
            tableExamQuestionData.getColumnModel().getColumn(0).setPreferredWidth(30);
            tableExamQuestionData.getColumnModel().getColumn(0).setMaxWidth(30);
            tableExamQuestionData.getColumnModel().getColumn(1).setMinWidth(0);
            tableExamQuestionData.getColumnModel().getColumn(1).setPreferredWidth(0);
            tableExamQuestionData.getColumnModel().getColumn(1).setMaxWidth(0);
            tableExamQuestionData.getColumnModel().getColumn(3).setMinWidth(80);
            tableExamQuestionData.getColumnModel().getColumn(3).setPreferredWidth(80);
            tableExamQuestionData.getColumnModel().getColumn(3).setMaxWidth(80);
        }

        panelExamQuestionTable.add(jScrollPane16, java.awt.BorderLayout.CENTER);

        panelExamQuestionsManagement.add(panelExamQuestionTable, java.awt.BorderLayout.CENTER);

        panelExamQuestions.add(panelExamQuestionsManagement, "panelExamQuestionsManagement");

        panelExamQuestionForm.setBorder(javax.swing.BorderFactory.createTitledBorder("Exam Question Form"));
        panelExamQuestionForm.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        buttonCancelExamQuestionForm.setText("Cancel");
        buttonCancelExamQuestionForm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelExamQuestionFormActionPerformed(evt);
            }
        });
        panelExamQuestionForm.add(buttonCancelExamQuestionForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 260, -1, -1));

        jLabel41.setText("Score Point :");
        panelExamQuestionForm.add(jLabel41, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 80, 150, -1));

        buttonSaveExamQuestionForm.setText("Save");
        buttonSaveExamQuestionForm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveExamQuestionFormActionPerformed(evt);
            }
        });
        panelExamQuestionForm.add(buttonSaveExamQuestionForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 260, 60, -1));

        jLabel42.setText("Category :");
        panelExamQuestionForm.add(jLabel42, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, 150, -1));

        jLabel43.setText("Type :");
        panelExamQuestionForm.add(jLabel43, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 80, 150, -1));

        textfieldScorePointExamQuestion.setText("0");
        panelExamQuestionForm.add(textfieldScorePointExamQuestion, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 100, 110, -1));

        jLabel44.setText("Sub Category :");
        panelExamQuestionForm.add(jLabel44, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 100, 150, -1));

        comboboxSubCategoryExamQuestion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboboxSubCategoryExamQuestionActionPerformed(evt);
            }
        });
        panelExamQuestionForm.add(comboboxSubCategoryExamQuestion, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 120, 160, -1));

        comboboxCategoryExamQuestion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboboxCategoryExamQuestionActionPerformed(evt);
            }
        });
        panelExamQuestionForm.add(comboboxCategoryExamQuestion, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 160, -1));
        panelExamQuestionForm.add(textfieldExamQuestion, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 50, 410, -1));

        jLabel45.setText("Question :");
        panelExamQuestionForm.add(jLabel45, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 30, 150, -1));

        radioButtonGroupTypeExamQuestion.add(radiobuttonMultipleChoiceExamQuestion);
        radiobuttonMultipleChoiceExamQuestion.setText("Multiple Choice");
        radiobuttonMultipleChoiceExamQuestion.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                radiobuttonMultipleChoiceExamQuestionItemStateChanged(evt);
            }
        });
        panelExamQuestionForm.add(radiobuttonMultipleChoiceExamQuestion, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 100, -1, -1));

        radioButtonGroupTypeExamQuestion.add(radiobuttonEssayExamQuestion);
        radiobuttonEssayExamQuestion.setText("Essay");
        radiobuttonEssayExamQuestion.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                radiobuttonEssayExamQuestionItemStateChanged(evt);
            }
        });
        panelExamQuestionForm.add(radiobuttonEssayExamQuestion, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 100, -1, -1));

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Preview Image"));
        jPanel5.setLayout(new java.awt.BorderLayout());

        labelPreviewExamQuestion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPreviewExamQuestion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/examprevdefault72.png"))); // NOI18N
        labelPreviewExamQuestion.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelPreviewExamQuestion.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        labelPreviewExamQuestion.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelPreviewExamQuestionMouseClicked(evt);
            }
        });
        jPanel5.add(labelPreviewExamQuestion, java.awt.BorderLayout.CENTER);

        panelExamQuestionForm.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 130, 120, 120));

        labelBrowseExamPreviewImage.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        labelBrowseExamPreviewImage.setForeground(new java.awt.Color(0, 0, 204));
        labelBrowseExamPreviewImage.setText("<html><u>Browse Picture</u></html>");
        labelBrowseExamPreviewImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelBrowseExamPreviewImage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelBrowseExamPreviewImageMouseClicked(evt);
            }
        });
        panelExamQuestionForm.add(labelBrowseExamPreviewImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 150, -1, -1));

        panelAnswerExamQuestion.setBorder(javax.swing.BorderFactory.createTitledBorder("Answers"));
        panelAnswerExamQuestion.setLayout(new java.awt.CardLayout());

        panelMultipleChoiceExamQuestion.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        addOptionsExamQuestion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add24.png"))); // NOI18N
        addOptionsExamQuestion.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        addOptionsExamQuestion.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addOptionsExamQuestionMouseClicked(evt);
            }
        });
        panelMultipleChoiceExamQuestion.add(addOptionsExamQuestion, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 5, -1, -1));

        editOptionsExamQuestion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/edit24.png"))); // NOI18N
        editOptionsExamQuestion.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        editOptionsExamQuestion.setEnabled(false);
        editOptionsExamQuestion.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                editOptionsExamQuestionMouseClicked(evt);
            }
        });
        panelMultipleChoiceExamQuestion.add(editOptionsExamQuestion, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 5, -1, -1));

        deleteOptionsExamQuestion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete24.png"))); // NOI18N
        deleteOptionsExamQuestion.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        deleteOptionsExamQuestion.setEnabled(false);
        deleteOptionsExamQuestion.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                deleteOptionsExamQuestionMouseClicked(evt);
            }
        });
        panelMultipleChoiceExamQuestion.add(deleteOptionsExamQuestion, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 5, -1, -1));

        tableExamQuestionOptions.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Ops", "Answer", "Title"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class, java.lang.Boolean.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tableExamQuestionOptions.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tableExamQuestionOptionsFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tableExamQuestionOptionsFocusLost(evt);
            }
        });
        jScrollPane17.setViewportView(tableExamQuestionOptions);
        if (tableExamQuestionOptions.getColumnModel().getColumnCount() > 0) {
            tableExamQuestionOptions.getColumnModel().getColumn(0).setMinWidth(30);
            tableExamQuestionOptions.getColumnModel().getColumn(0).setPreferredWidth(30);
            tableExamQuestionOptions.getColumnModel().getColumn(0).setMaxWidth(30);
            tableExamQuestionOptions.getColumnModel().getColumn(1).setMinWidth(25);
            tableExamQuestionOptions.getColumnModel().getColumn(1).setPreferredWidth(25);
            tableExamQuestionOptions.getColumnModel().getColumn(1).setMaxWidth(25);
            tableExamQuestionOptions.getColumnModel().getColumn(2).setMinWidth(75);
            tableExamQuestionOptions.getColumnModel().getColumn(2).setPreferredWidth(75);
            tableExamQuestionOptions.getColumnModel().getColumn(2).setMaxWidth(75);
        }

        panelMultipleChoiceExamQuestion.add(jScrollPane17, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 35, 370, 80));

        panelAnswerExamQuestion.add(panelMultipleChoiceExamQuestion, "panelMultipleChoiceExamQuestion");

        panelEssayExamQuestion.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        textAreaAnswerEssayExamQuestion.setColumns(20);
        textAreaAnswerEssayExamQuestion.setRows(5);
        jScrollPane19.setViewportView(textAreaAnswerEssayExamQuestion);

        panelEssayExamQuestion.add(jScrollPane19, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 350, 100));

        panelAnswerExamQuestion.add(panelEssayExamQuestion, "panelEssayExamQuestion");

        panelExamQuestionForm.add(panelAnswerExamQuestion, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 150, 390, 140));

        panelExamQuestions.add(panelExamQuestionForm, "panelExamQuestionForm");
        panelExamQuestionForm.getAccessibleContext().setAccessibleName("");

        panelInnerCenter.add(panelExamQuestions, "panelExamQuestions");

        panelCertificateStudent.setLayout(new java.awt.CardLayout());

        panelCertificateStudentManagement.setLayout(new java.awt.BorderLayout());

        panelCertificateStudentControl.setPreferredSize(new java.awt.Dimension(658, 40));
        panelCertificateStudentControl.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        buttonAddCertificateStudent.setText("Add");
        buttonAddCertificateStudent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddCertificateStudentActionPerformed(evt);
            }
        });
        panelCertificateStudentControl.add(buttonAddCertificateStudent, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 5, 60, -1));

        buttonEditCertificateStudent.setText("Edit");
        buttonEditCertificateStudent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonEditCertificateStudentActionPerformed(evt);
            }
        });
        panelCertificateStudentControl.add(buttonEditCertificateStudent, new org.netbeans.lib.awtextra.AbsoluteConstraints(514, 5, 60, -1));

        buttonDeleteCertificateStudent.setText("Delete");
        buttonDeleteCertificateStudent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeleteCertificateStudentActionPerformed(evt);
            }
        });
        panelCertificateStudentControl.add(buttonDeleteCertificateStudent, new org.netbeans.lib.awtextra.AbsoluteConstraints(589, 5, -1, -1));

        labelCertificateStudentManagement.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        labelCertificateStudentManagement.setText("Certificate Student Management");
        panelCertificateStudentControl.add(labelCertificateStudentManagement, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 310, 40));

        labelRefreshCertificateStudentManagement.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/refresh16.png"))); // NOI18N
        labelRefreshCertificateStudentManagement.setText("Refresh");
        labelRefreshCertificateStudentManagement.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelRefreshCertificateStudentManagement.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelRefreshCertificateStudentManagementMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                labelRefreshCertificateStudentManagementMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                labelRefreshCertificateStudentManagementMouseExited(evt);
            }
        });
        panelCertificateStudentControl.add(labelRefreshCertificateStudentManagement, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 10, 70, 20));

        panelCertificateStudentManagement.add(panelCertificateStudentControl, java.awt.BorderLayout.PAGE_START);

        panelCertificateStudentTable.setLayout(new java.awt.BorderLayout());

        tableCertificateStudentData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "[ x ]", "Id", "Username", "Category", "Status", "Filename", "Date Created"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane18.setViewportView(tableCertificateStudentData);
        if (tableCertificateStudentData.getColumnModel().getColumnCount() > 0) {
            tableCertificateStudentData.getColumnModel().getColumn(0).setMinWidth(30);
            tableCertificateStudentData.getColumnModel().getColumn(0).setPreferredWidth(30);
            tableCertificateStudentData.getColumnModel().getColumn(0).setMaxWidth(30);
            tableCertificateStudentData.getColumnModel().getColumn(1).setMinWidth(0);
            tableCertificateStudentData.getColumnModel().getColumn(1).setPreferredWidth(0);
            tableCertificateStudentData.getColumnModel().getColumn(1).setMaxWidth(0);
            tableCertificateStudentData.getColumnModel().getColumn(3).setMinWidth(80);
            tableCertificateStudentData.getColumnModel().getColumn(3).setPreferredWidth(80);
            tableCertificateStudentData.getColumnModel().getColumn(3).setMaxWidth(80);
        }

        panelCertificateStudentTable.add(jScrollPane18, java.awt.BorderLayout.CENTER);

        panelCertificateStudentManagement.add(panelCertificateStudentTable, java.awt.BorderLayout.CENTER);

        panelCertificateStudent.add(panelCertificateStudentManagement, "panelCertificateStudentManagement");

        panelCertificateStudentForm.setBorder(javax.swing.BorderFactory.createTitledBorder("Certificate Student Form"));
        panelCertificateStudentForm.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel46.setText("Certificate Scan Image :");
        panelCertificateStudentForm.add(jLabel46, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 160, 150, -1));

        buttonCancelCertificateStudentForm.setText("Cancel");
        buttonCancelCertificateStudentForm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelCertificateStudentFormActionPerformed(evt);
            }
        });
        panelCertificateStudentForm.add(buttonCancelCertificateStudentForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 250, -1, -1));

        jLabel47.setText("Date Released : (dd/mm/yyyy)");
        panelCertificateStudentForm.add(jLabel47, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 80, 150, -1));

        buttonSaveCertificateStudentForm.setText("Save");
        buttonSaveCertificateStudentForm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveCertificateStudentFormActionPerformed(evt);
            }
        });
        panelCertificateStudentForm.add(buttonSaveCertificateStudentForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(555, 250, 60, -1));

        jLabel48.setText("Category :");
        panelCertificateStudentForm.add(jLabel48, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, 150, -1));

        addFileCertificateStudent.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add24.png"))); // NOI18N
        addFileCertificateStudent.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        addFileCertificateStudent.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addFileCertificateStudentMouseClicked(evt);
            }
        });
        panelCertificateStudentForm.add(addFileCertificateStudent, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 180, -1, -1));

        deleteFileCertificateStudent.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete24.png"))); // NOI18N
        deleteFileCertificateStudent.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        deleteFileCertificateStudent.setEnabled(false);
        deleteFileCertificateStudent.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                deleteFileCertificateStudentMouseClicked(evt);
            }
        });
        panelCertificateStudentForm.add(deleteFileCertificateStudent, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 180, -1, -1));

        jLabel49.setText("Status :");
        panelCertificateStudentForm.add(jLabel49, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 80, 150, -1));

        jLabel50.setText("Username :");
        panelCertificateStudentForm.add(jLabel50, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 100, 150, -1));

        panelCertificateStudentForm.add(comboboxCertificateStudentUsername, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 120, 160, -1));

        comboboxCertificateStudentCategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboboxCertificateStudentCategoryActionPerformed(evt);
            }
        });
        panelCertificateStudentForm.add(comboboxCertificateStudentCategory, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 160, -1));

        radioButtonGroupStatusCertificate.add(radioButtonCertificateStudentWaiting);
        radioButtonCertificateStudentWaiting.setText("Waiting");
        panelCertificateStudentForm.add(radioButtonCertificateStudentWaiting, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 100, -1, -1));

        radioButtonGroupStatusCertificate.add(radioButtonCertificateStudentReleased);
        radioButtonCertificateStudentReleased.setText("Released");
        panelCertificateStudentForm.add(radioButtonCertificateStudentReleased, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 100, -1, -1));

        labelPreviewCertificateStudent.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPreviewCertificateStudent.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/newdoc.png"))); // NOI18N
        labelPreviewCertificateStudent.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelPreviewCertificateStudent.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        labelPreviewCertificateStudent.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelPreviewCertificateStudentMouseClicked(evt);
            }
        });
        panelCertificateStudentForm.add(labelPreviewCertificateStudent, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 180, 180, 100));
        panelCertificateStudentForm.add(textfieldDateReleaseCertificateStudent, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 100, -1, -1));

        panelCertificateStudent.add(panelCertificateStudentForm, "panelCertificateStudentForm");

        panelInnerCenter.add(panelCertificateStudent, "panelCertificateStudent");

        panelClassRoom.setLayout(new java.awt.CardLayout());

        panelClassRoomManagement.setLayout(new java.awt.BorderLayout());

        panelClassRoomControl.setPreferredSize(new java.awt.Dimension(658, 40));
        panelClassRoomControl.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        buttonAddClassRoom.setText("Add");
        buttonAddClassRoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddClassRoomActionPerformed(evt);
            }
        });
        panelClassRoomControl.add(buttonAddClassRoom, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 5, 60, -1));

        buttonEditClassRoom.setText("Edit");
        buttonEditClassRoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonEditClassRoomActionPerformed(evt);
            }
        });
        panelClassRoomControl.add(buttonEditClassRoom, new org.netbeans.lib.awtextra.AbsoluteConstraints(514, 5, 60, -1));

        buttonDeleteClassRoom.setText("Delete");
        buttonDeleteClassRoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeleteClassRoomActionPerformed(evt);
            }
        });
        panelClassRoomControl.add(buttonDeleteClassRoom, new org.netbeans.lib.awtextra.AbsoluteConstraints(589, 5, -1, -1));

        labelRefreshClassRoom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/refresh16.png"))); // NOI18N
        labelRefreshClassRoom.setText("Refresh");
        labelRefreshClassRoom.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelRefreshClassRoom.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelRefreshClassRoomMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                labelRefreshClassRoomMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                labelRefreshClassRoomMouseExited(evt);
            }
        });
        panelClassRoomControl.add(labelRefreshClassRoom, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 10, 70, 20));

        jLabel51.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        jLabel51.setText("Class Room Management");
        panelClassRoomControl.add(jLabel51, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 280, 40));

        panelClassRoomManagement.add(panelClassRoomControl, java.awt.BorderLayout.PAGE_START);

        panelClassRoomTable.setLayout(new java.awt.BorderLayout());

        tableClassRoomData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "[ x ]", "Id", "Class Name", "Description", "Instructor ID", "Instructor Name", "Date Created"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tableClassRoomData.getTableHeader().setReorderingAllowed(false);
        jScrollPane20.setViewportView(tableClassRoomData);
        if (tableClassRoomData.getColumnModel().getColumnCount() > 0) {
            tableClassRoomData.getColumnModel().getColumn(0).setMinWidth(30);
            tableClassRoomData.getColumnModel().getColumn(0).setPreferredWidth(30);
            tableClassRoomData.getColumnModel().getColumn(0).setMaxWidth(30);
            tableClassRoomData.getColumnModel().getColumn(1).setMinWidth(0);
            tableClassRoomData.getColumnModel().getColumn(1).setPreferredWidth(0);
            tableClassRoomData.getColumnModel().getColumn(1).setMaxWidth(0);
            tableClassRoomData.getColumnModel().getColumn(4).setMinWidth(0);
            tableClassRoomData.getColumnModel().getColumn(4).setPreferredWidth(0);
            tableClassRoomData.getColumnModel().getColumn(4).setMaxWidth(0);
        }

        panelClassRoomTable.add(jScrollPane20, java.awt.BorderLayout.CENTER);

        panelClassRoomManagement.add(panelClassRoomTable, java.awt.BorderLayout.CENTER);

        panelClassRoom.add(panelClassRoomManagement, "panelClassRoomManagement");

        panelClassRoomForm.setBorder(javax.swing.BorderFactory.createTitledBorder("ClassRoom Form"));
        panelClassRoomForm.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel52.setText("Class Name :");
        panelClassRoomForm.add(jLabel52, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, 150, -1));

        buttonCancelClassRoomForm.setText("Cancel");
        buttonCancelClassRoomForm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelClassRoomFormActionPerformed(evt);
            }
        });
        panelClassRoomForm.add(buttonCancelClassRoomForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 250, -1, -1));
        panelClassRoomForm.add(textfieldNameClassRoom, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 200, -1));

        jLabel53.setText("Instructor :");
        panelClassRoomForm.add(jLabel53, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 100, 150, -1));

        jLabel55.setText("Description :");
        panelClassRoomForm.add(jLabel55, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 40, 150, -1));

        textareaDescriptionClassRoom.setColumns(20);
        textareaDescriptionClassRoom.setLineWrap(true);
        textareaDescriptionClassRoom.setRows(5);
        jScrollPane21.setViewportView(textareaDescriptionClassRoom);

        panelClassRoomForm.add(jScrollPane21, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 60, -1, 100));

        buttonSaveClassRoomForm.setText("Save");
        buttonSaveClassRoomForm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveClassRoomFormActionPerformed(evt);
            }
        });
        panelClassRoomForm.add(buttonSaveClassRoomForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(555, 250, 60, -1));

        comboboxUsernameClassRoom.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "unknown", "admin", "dede", "udin" }));
        comboboxUsernameClassRoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboboxUsernameClassRoomActionPerformed(evt);
            }
        });
        panelClassRoomForm.add(comboboxUsernameClassRoom, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 130, 200, -1));

        panelClassRoom.add(panelClassRoomForm, "panelClassRoomForm");

        panelInnerCenter.add(panelClassRoom, "panelClassRoom");

        panelCenter.add(panelInnerCenter, new org.netbeans.lib.awtextra.AbsoluteConstraints(43, 48, 658, 297));

        labelNextMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/play.png"))); // NOI18N
        labelNextMenu.setText("Next Menu");
        labelNextMenu.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelNextMenu.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        labelNextMenu.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        labelNextMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelNextMenuMouseClicked(evt);
            }
        });
        panelCenter.add(labelNextMenu, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 20, 90, -1));

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

        jLabel2.setFont(new java.awt.Font("sansserif", 1, 24)); // NOI18N
        jLabel2.setText("Admin Area");
        panelCenter.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 10, -1, -1));

        getContentPane().add(panelCenter, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void labelCloseMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelCloseMouseClicked

        logout();
    }//GEN-LAST:event_labelCloseMouseClicked

    private void logout() {
        UIEffect.stopTimeEffect();
        loginFrame.show();
        this.dispose();
    }

    private void labelMinimizeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelMinimizeMouseClicked
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
        if (labelLinkChangePicture.getText().contains("Delete")) {
            // call the delete to the API
            deleteUserPicture();

            labelLinkChangePicture.setText(UIEffect.underline("Browse Picture"));
            propicFile = null;
            labelPreviewPicture.setIcon(null);
            labelPreviewPicture.setText("preview");

        } else {
            // browse file
            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                propicFile = fileChooser.getSelectedFile();
                try {
                    labelPreviewPicture.setIcon(new ImageIcon(ImageIO.read(propicFile)));
                    enableUserFormSave();
                } catch (Exception e) {
                    e.printStackTrace();
                    UIEffect.popup("Error while browse picutre applied!", this);
                }
            } else {
                // if no file was chosen
                propicFile = null;
            }
        }
    }//GEN-LAST:event_labelLinkChangePictureMouseClicked

    private void buttonAddUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddUserActionPerformed
        cardLayoutEntity.show(panelUser, "panelUserForm");
        // clean but not for editing mode
        cleanUpUserForm(false);
    }//GEN-LAST:event_buttonAddUserActionPerformed

    private void labelBackToHomeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelBackToHomeMouseClicked
        cardLayoutInnerCenter.show(panelInnerCenter, "panelHomeMenu");
        labelBackToHome.setVisible(false);
        labelNextMenu.setVisible(true);

    }//GEN-LAST:event_labelBackToHomeMouseClicked

    private void buttonDeleteUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDeleteUserActionPerformed

        ArrayList dataUser = tabRender.getCheckedRows(tableUserData, 2);

        if (dataUser.size() == 0) {
            UIEffect.popup("Please select the row first!", this);
        } else {
            // passing username only
            deleteUser(dataUser);
            showLoadingStatus();
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
            showLoadingStatus();
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
            showLoadingStatus();
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
            showLoadingStatus();
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
        showLoadingStatus();
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
            showLoadingStatus();
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
            showLoadingStatus();
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


    private void comboboxDaySchedItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboboxDaySchedItemStateChanged
        showLoadingStatus();

        // when the item selected changed
        // we check the registered class of that day
        if (comboboxDaySched.getSelectedItem() != null) {
            String classSelected = comboboxDaySched.getSelectedItem().toString();
            refreshScheduleByDay(classSelected);
        } else {
            // clearup the list
            listAnotherClassSched.setModel(new DefaultListModel());
            showLoadingStatus();
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
            showLoadingStatus();
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
            showLoadingStatus();
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
        if (labelBrowseSignatureAttendance.getText().contains("Delete")) {
            // call the delete to the API
            deleteSignaturePicture();

            labelBrowseSignatureAttendance.setText(UIEffect.underline("Browse Picture"));
            signatureFile = null;
            labelSignatureAttendance.setIcon(null);
            labelSignatureAttendance.setText("preview");

        } else {

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
            showLoadingStatus();
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

        // check first whether it is for delete
        // or for browse picture?
        if (labelBrowseScreenshotPayment.getText().contains("Delete")) {
            // call the delete to the API
            deleteScreenshotPicture();

            labelBrowseScreenshotPayment.setText(UIEffect.underline("Browse Picture"));
            payFile = null;
            labelScreenshotPayment.setIcon(null);
            labelScreenshotPayment.setText("preview");

        } else {
            // call the browse process

            // browse file
            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                payFile = fileChooser.getSelectedFile();
                try {
                    labelScreenshotPayment.setIcon(new ImageIcon(ImageIO.read(payFile)));
                    labelBrowseScreenshotPayment.setText(UIEffect.underline("Delete"));
                } catch (Exception e) {
                    e.printStackTrace();
                    UIEffect.popup("Error while browse picutre applied!", this);
                }
            } else {
                // if no file was chosen
                payFile = null;
            }

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

    private void labelScreenshotPaymentMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelScreenshotPaymentMouseClicked
        if (labelScreenshotPayment.getIcon() != null) {
            File lokasiScreenshot = new File(PathReference.ScreenshotPaymentPath);
            CMDExecutor.openPicture(lokasiScreenshot);
        }
    }//GEN-LAST:event_labelScreenshotPaymentMouseClicked

    private void textfieldUsernameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textfieldUsernameKeyTyped
        // only for edited form
        enableUserFormSave();
    }//GEN-LAST:event_textfieldUsernameKeyTyped

    private void textfieldPassKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textfieldPassKeyTyped

    }//GEN-LAST:event_textfieldPassKeyTyped

    private void textfieldEmailKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textfieldEmailKeyTyped

    }//GEN-LAST:event_textfieldEmailKeyTyped

    private void textfieldMobileKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textfieldMobileKeyTyped

    }//GEN-LAST:event_textfieldMobileKeyTyped

    private void textareaAddressKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textareaAddressKeyTyped

    }//GEN-LAST:event_textareaAddressKeyTyped

    private void textfieldUsernameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textfieldUsernameKeyReleased
        enableUserFormSave();
    }//GEN-LAST:event_textfieldUsernameKeyReleased

    private void textfieldPassKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textfieldPassKeyReleased
        enableUserFormSave();
    }//GEN-LAST:event_textfieldPassKeyReleased

    private void textfieldEmailKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textfieldEmailKeyReleased
        enableUserFormSave();
    }//GEN-LAST:event_textfieldEmailKeyReleased

    private void textfieldMobileKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textfieldMobileKeyReleased
        enableUserFormSave();
    }//GEN-LAST:event_textfieldMobileKeyReleased

    private void textareaAddressKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textareaAddressKeyReleased
        enableUserFormSave();
    }//GEN-LAST:event_textareaAddressKeyReleased

    private void labelPreviewPictureMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelPreviewPictureMouseClicked
        if (labelPreviewPicture.getIcon() != defaultUser) {
            File lokasiPropic = new File(PathReference.UserPropicPath);
            CMDExecutor.openPicture(lokasiPropic);
        }
    }//GEN-LAST:event_labelPreviewPictureMouseClicked

    private void labelSignatureAttendanceMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelSignatureAttendanceMouseClicked
        if (labelSignatureAttendance.getIcon() != null) {
            File lokasiSignature = new File(PathReference.SignaturePath);
            CMDExecutor.openPicture(lokasiSignature);
        }
    }//GEN-LAST:event_labelSignatureAttendanceMouseClicked

    private void formMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseReleased
        UIDragger.mouseReleased(evt);
    }//GEN-LAST:event_formMouseReleased

    private void buttonViewBugsReportedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonViewBugsReportedActionPerformed

        ArrayList dataBugs = tabRender.getCheckedRows(tableBugsReportedData, 1);

        if (dataBugs.size() == 1) {
            // go to reportedbugs form
            cardLayoutEntity.show(panelBugsReported, "panelBugsReportedForm");

            // clean the form 
            cleanUpBugsReportedForm();

            // call the API with id passed
            getBugsReported(Integer.parseInt(dataBugs.get(0).toString()));

            // show the loading bar
            showLoadingStatus();
        } else {
            UIEffect.popup("please select 1 single data only!", this);
        }
    }//GEN-LAST:event_buttonViewBugsReportedActionPerformed

    private void buttonDeleteBugsReportedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDeleteBugsReportedActionPerformed
        ArrayList dataBugs = tabRender.getCheckedRows(tableBugsReportedData, 1);

        if (dataBugs.size() == 0) {
            UIEffect.popup("Please select the row first!", this);
        } else {
            // passing id only
            deleteBugsReported(dataBugs);
            showLoadingStatus();
        }
    }//GEN-LAST:event_buttonDeleteBugsReportedActionPerformed

    private void labelRefreshBugsReportedMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRefreshBugsReportedMouseClicked
        // change to loading icon
        labelRefreshBugsReported.setIcon(loadingImage);

        // refresh the table
        refreshBugsReported();
    }//GEN-LAST:event_labelRefreshBugsReportedMouseClicked

    private void labelRefreshBugsReportedMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRefreshBugsReportedMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_labelRefreshBugsReportedMouseEntered

    private void labelRefreshBugsReportedMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRefreshBugsReportedMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_labelRefreshBugsReportedMouseExited

    private void buttonCancelBugsReportedFormActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelBugsReportedFormActionPerformed
        cardLayoutEntity.show(panelBugsReported, "panelBugsReportedManagement");
        labelBackToHome.setVisible(true);
    }//GEN-LAST:event_buttonCancelBugsReportedFormActionPerformed

    private void buttonSaveBugsReportedFormActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveBugsReportedFormActionPerformed
        cardLayoutEntity.show(panelBugsReported, "panelBugsReportedManagement");
        saveBugsReported();
    }//GEN-LAST:event_buttonSaveBugsReportedFormActionPerformed

    private void labelBrowseScreenshotBugsReportedMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelBrowseScreenshotBugsReportedMouseClicked

        // check first whether it is for delete
        // or for browse picture?
        if (labelBrowseScreenshotBugsReported.getText().contains("Delete")) {

            labelBrowseScreenshotBugsReported.setText(UIEffect.underline("Browse Picture"));
            bugsFile = null;

            labelScreenshotBugsReported.setIcon(null);
            labelScreenshotBugsReported.setText("preview");

        } else {
            // call the browse process

            // browse file
            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                payFile = fileChooser.getSelectedFile();
                try {
                    labelScreenshotBugsReported.setIcon(new ImageIcon(ImageIO.read(bugsFile)));
                    labelBrowseScreenshotBugsReported.setText(UIEffect.underline("Delete"));
                } catch (Exception e) {
                    e.printStackTrace();
                    UIEffect.popup("Error while browse picutre applied!", this);
                }
            } else {
                // if no file was chosen
                bugsFile = null;
            }

        }
    }//GEN-LAST:event_labelBrowseScreenshotBugsReportedMouseClicked

    private void labelScreenshotBugsReportedMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelScreenshotBugsReportedMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_labelScreenshotBugsReportedMouseClicked

    private void textfieldTitleBugsReportedFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textfieldTitleBugsReportedFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_textfieldTitleBugsReportedFocusGained

    private void textfieldTitleBugsReportedFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textfieldTitleBugsReportedFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_textfieldTitleBugsReportedFocusLost

    private void textfieldIPAddressBugsReportedFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textfieldIPAddressBugsReportedFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_textfieldIPAddressBugsReportedFocusGained

    private void textfieldIPAddressBugsReportedFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textfieldIPAddressBugsReportedFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_textfieldIPAddressBugsReportedFocusLost

    private void buttonExamCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonExamCategoryActionPerformed

        cardLayoutInnerCenter.show(panelInnerCenter, "panelExamCategory");
        cardLayoutEntity = (CardLayout) panelExamCategory.getLayout();
        cardLayoutEntity.show(panelExamCategory, "panelExamCategoryManagement");

        labelBackToHome.setVisible(true);


    }//GEN-LAST:event_buttonExamCategoryActionPerformed

    private void buttonExamStudentAnswerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonExamStudentAnswerActionPerformed
        cardLayoutInnerCenter.show(panelInnerCenter, "panelExamStudentAnswer");
        cardLayoutEntity = (CardLayout) panelExamStudentAnswer.getLayout();
        cardLayoutEntity.show(panelExamStudentAnswer, "panelExamStudentAnswerManagement");

        labelBackToHome.setVisible(true);

    }//GEN-LAST:event_buttonExamStudentAnswerActionPerformed

    private void buttonExamQuestionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonExamQuestionsActionPerformed

        cardLayoutInnerCenter.show(panelInnerCenter, "panelExamQuestions");
        cardLayoutEntity = (CardLayout) panelExamQuestions.getLayout();
        cardLayoutEntity.show(panelExamQuestions, "panelExamQuestionsManagement");

        labelBackToHome.setVisible(true);

    }//GEN-LAST:event_buttonExamQuestionsActionPerformed

    private void buttonStudentCertificateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonStudentCertificateActionPerformed

        cardLayoutInnerCenter.show(panelInnerCenter, "panelCertificateStudent");
        cardLayoutEntity = (CardLayout) panelCertificateStudent.getLayout();
        cardLayoutEntity.show(panelCertificateStudent, "panelCertificateStudentManagement");

        labelBackToHome.setVisible(true);

    }//GEN-LAST:event_buttonStudentCertificateActionPerformed

    private void buttonXXXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonXXXActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_buttonXXXActionPerformed

    private void buttonXXXXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonXXXXActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_buttonXXXXActionPerformed

    private void buttonBugsReported1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonBugsReported1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_buttonBugsReported1ActionPerformed

    private void buttonLogout1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonLogout1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_buttonLogout1ActionPerformed

    private void buttonLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonLogoutActionPerformed
        logout();
    }//GEN-LAST:event_buttonLogoutActionPerformed

    private void buttonBugsReportedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonBugsReportedActionPerformed

        cardLayoutInnerCenter.show(panelInnerCenter, "panelBugsReported");
        cardLayoutEntity = (CardLayout) panelBugsReported.getLayout();
        cardLayoutEntity.show(panelBugsReported, "panelBugsReportedManagement");

        labelBackToHome.setVisible(true);
    }//GEN-LAST:event_buttonBugsReportedActionPerformed

    private void buttonClassRoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonClassRoomActionPerformed

        cardLayoutInnerCenter.show(panelInnerCenter, "panelClassRoom");
        cardLayoutEntity = (CardLayout) panelClassRoom.getLayout();
        cardLayoutEntity.show(panelClassRoom, "panelClassRoomManagement");

        labelBackToHome.setVisible(true);

    }//GEN-LAST:event_buttonClassRoomActionPerformed

    private void buttonScheduleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonScheduleActionPerformed
        cardLayoutInnerCenter.show(panelInnerCenter, "panelSchedule");
        cardLayoutEntity = (CardLayout) panelSchedule.getLayout();
        cardLayoutEntity.show(panelSchedule, "panelScheduleManagement");

        labelBackToHome.setVisible(true);
    }//GEN-LAST:event_buttonScheduleActionPerformed

    private void buttonAddExamCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddExamCategoryActionPerformed

        // ## Add New from Management
        cardLayoutEntity.show(panelExamCategory, "panelExamCategoryForm");
        // clean but not for editing mode
        cleanUpExamCategoryForm(false);

    }//GEN-LAST:event_buttonAddExamCategoryActionPerformed

    private void buttonEditExamCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonEditExamCategoryActionPerformed
        // ## Edit Item from Management
        ArrayList dataExam = tabRender.getCheckedRows(tableExamCategoryData, 1);

        if (dataExam.size() == 1) {
            // go to examCategoryForm
            cardLayoutEntity.show(panelExamCategory, "panelExamCategoryForm");

            // clean the form but for editing mode
            cleanUpExamCategoryForm(true);

            // call the API with id passed
            getExamCategory(dataExam.get(0).toString());

            // show the loading bar
            showLoadingStatus();
        } else {
            UIEffect.popup("please select 1 single data only!", this);
        }
    }//GEN-LAST:event_buttonEditExamCategoryActionPerformed

    private void buttonDeleteExamCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDeleteExamCategoryActionPerformed

        ArrayList dataExam = tabRender.getCheckedRows(tableExamCategoryData, 1);

        if (dataExam.isEmpty()) {
            UIEffect.popup("Please select the row first!", this);
        } else {
            // passing id only

            deleteExamCategory(dataExam);
            showLoadingStatus();
        }

    }//GEN-LAST:event_buttonDeleteExamCategoryActionPerformed

    private void labelRefreshExamCategoryManagementMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRefreshExamCategoryManagementMouseClicked

        // ## Refresh on Management
        // change to loading icon
        labelRefreshExamCategoryManagement.setIcon(loadingImage);

        // refresh the table
        refreshExamCategory();

    }//GEN-LAST:event_labelRefreshExamCategoryManagementMouseClicked

    private void labelRefreshExamCategoryManagementMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRefreshExamCategoryManagementMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_labelRefreshExamCategoryManagementMouseEntered

    private void labelRefreshExamCategoryManagementMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRefreshExamCategoryManagementMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_labelRefreshExamCategoryManagementMouseExited

    private void buttonCancelExamCategoryFormActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelExamCategoryFormActionPerformed
        // clear all from ArrayList
        isiSubCategory.clear();

        cardLayoutEntity.show(panelExamCategory, "panelExamCategoryManagement");
        labelBackToHome.setVisible(true);
    }//GEN-LAST:event_buttonCancelExamCategoryFormActionPerformed

    private void textfieldTitleExamCategoryKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textfieldTitleExamCategoryKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_textfieldTitleExamCategoryKeyReleased

    private void textfieldTitleExamCategoryKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textfieldTitleExamCategoryKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_textfieldTitleExamCategoryKeyTyped

    private void textfieldCodeExamCategoryKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textfieldCodeExamCategoryKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_textfieldCodeExamCategoryKeyReleased

    private void textfieldCodeExamCategoryKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textfieldCodeExamCategoryKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_textfieldCodeExamCategoryKeyTyped

    private void buttonSaveExamCategoryFormActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveExamCategoryFormActionPerformed
        // ## save everything but in the following order
        // first the exam category
        // second is the exam sub category

        // ## Save UI Form
        cardLayoutEntity.show(panelExamCategory, "panelExamCategoryManagement");
        saveExamCategory();

    }//GEN-LAST:event_buttonSaveExamCategoryFormActionPerformed

    private void resortingOps() {

        String ops[] = {"A", "B", "C", "D"};
        int i = 0;

        for (ExamMultipleChoice exm : isiExamQuestionOptions) {

            exm.setOps(ops[i]);
            i++;

        }

    }

    private void refreshExamQuestionOptionLocally() {

        if (isiExamQuestionOptions.size() > 0) {
            // this will resorting the multiple choice ops
            resortingOps();

            tabRender.renderExamMultipleChoices(tableExamQuestionOptions, isiExamQuestionOptions);

            // show the control buttons
            toggleExamQuestionOption(false);

        } else {
            UIEffect.popup("nothing to refresh...!", this);

            // disable the control buttons
            toggleExamQuestionOption(true);
        }

        // disable the add-button if the table is already full
        // full is not more than 4
        addOptionsExamQuestion.setEnabled(!(tableExamQuestionOptions.getRowCount() > 3));
        hideLoadingStatus();
    }

    private void refreshExamSubCategoryLocally() {

        if (isiSubCategory.size() > 0) {
            TableRenderer.renderExamSubCategory(tableExamSubCategoryData, isiSubCategory);

            // show the control buttons
            toggleExamSubCategory(false);

        } else {
            UIEffect.popup("nothing to refresh...!", this);

            // disable the control buttons
            toggleExamSubCategory(true);
        }

    }

    private void addExamSubCategoryMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addExamSubCategoryMouseClicked

        // adding item to sub category table
        if (addExamSubCategory.isEnabled()) {
            String jawaban = UIEffect.popupInput("Sub Category Title : ", this);
            if (jawaban != null) {
                if (!jawaban.isEmpty()) {
                    // add from the input to array
                    // then forward to the jtable
                    isiSubCategory.add(new ExamSubCategory(jawaban));
                    // refresh locally
                    refreshExamSubCategoryLocally();
                }
            }
        }
    }//GEN-LAST:event_addExamSubCategoryMouseClicked

    private void tableExamSubCategoryDataFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tableExamSubCategoryDataFocusGained
        editExamSubCategory.setEnabled(true);
        deleteExamSubCategory.setEnabled(true);
    }//GEN-LAST:event_tableExamSubCategoryDataFocusGained

    private void tableExamSubCategoryDataFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tableExamSubCategoryDataFocusLost

        editExamSubCategory.setEnabled(false);
        deleteExamSubCategory.setEnabled(false);

    }//GEN-LAST:event_tableExamSubCategoryDataFocusLost

    private void editExamSubCategoryMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_editExamSubCategoryMouseClicked

        if (editExamSubCategory.isEnabled()) {
            // ID is located at the 1st index
            String idVal = tabRender.getCheckedRowValue(tableExamSubCategoryData, 1);
            String initialValue = tabRender.getCheckedRowValue(tableExamSubCategoryData, 2);

            if (idVal != null) {

                String jawaban = null;
                int idValNumber = Integer.parseInt(idVal);

                jawaban = UIEffect.popupInput("Sub Category Title : ", initialValue, this);

                if (jawaban != null) {
                    if (!jawaban.isEmpty()) {
                        // update as well from the arraylist
                        updateExamSubCategory(initialValue, jawaban);
                        refreshExamSubCategoryLocally();
                    }
                }

            }

        }

    }//GEN-LAST:event_editExamSubCategoryMouseClicked

    private void updateExamSubCategory(String intialData, String newData) {

        for (ExamSubCategory ex : isiSubCategory) {
            if (intialData.equalsIgnoreCase(ex.getTitle())) {
                ex.setTitle(newData);
            }
        }

    }

    private void updateExamQuestionOption(String intialData, String newData, boolean selected) {

        for (ExamMultipleChoice ex : isiExamQuestionOptions) {
            if (intialData.equalsIgnoreCase(ex.getTitle())) {
                ex.setTitle(newData);
                ex.setAnswer(selected);
            }
        }

    }

    private int getExamSubCategoryIdFromList(String titleChosen) {
        int value = 0;

        for (ExamSubCategory ex : isiSubCategory) {
            if (titleChosen.equalsIgnoreCase(ex.getTitle())) {
                value = ex.getId();
            }
        }

        return value;
    }

    private void removeOptionExamQuestion(String opsNa) {

        ExamMultipleChoice cari = null;

        for (ExamMultipleChoice opSatuan : isiExamQuestionOptions) {
            if (opsNa.equalsIgnoreCase(opSatuan.getOps())) {
                cari = opSatuan;
            }
        }

        if (cari != null) {
            isiExamQuestionOptions.remove(cari);
        }
    }

    private void removeExamSubCategory(int idNa) {
        ExamSubCategory found = null;

        for (ExamSubCategory ex : isiSubCategory) {
            if (ex.getId() == idNa) {
                found = ex;
            }
        }

        if (found != null) {
            isiSubCategory.remove(found);
        }
    }

    private void removeExamSubCategory(String judulNa) {
        ExamSubCategory fnd = null;

        for (ExamSubCategory ex : isiSubCategory) {
            if (ex.getTitle().equalsIgnoreCase(judulNa)) {
                fnd = ex;
            }
        }

        if (fnd != null) {
            isiSubCategory.remove(fnd);
        }
    }


    private void deleteExamSubCategoryMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_deleteExamSubCategoryMouseClicked

        if (deleteExamSubCategory.isEnabled()) {
            // removing from the table UI 
            // ID is located at the 1st index
            // TITLE is located at the 2nd index
            ArrayList dataExam = tabRender.getCheckedRows(tableExamSubCategoryData, 2);

            if (dataExam.isEmpty()) {
                UIEffect.popup("Please select the data first!", this);
            } else {
                // locking the control
                toggleExamSubCategory(true);
                showLoadingStatus();

                // passing title only
                // to the server for deleting purposes
                for (Object obj : dataExam) {

                    Integer val = getExamSubCategoryIdFromList(obj.toString());
                    if (val != 0) {
                        // calling server delete command

                        deleteExamSubCategory(val);
                        // removal based on its INDEX
                        removeExamSubCategory(val);
                    } else {
                        // removal based on its TITLE
                        removeExamSubCategory(obj.toString());
                    }

                }

            }

            // and add-into the array for Server list (tobe deleted)
        }

    }//GEN-LAST:event_deleteExamSubCategoryMouseClicked

    private void buttonAddExamStudentAnswerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddExamStudentAnswerActionPerformed

        // ## Add New from Management
        cardLayoutEntity.show(panelExamStudentAnswer, "panelExamStudentAnswerForm");
        // clean but not for editing mode
        cleanUpExamStudentAnswerForm(false);

    }//GEN-LAST:event_buttonAddExamStudentAnswerActionPerformed

    private void buttonEditExamStudentAnswerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonEditExamStudentAnswerActionPerformed

        // ## Edit Item from Management
        ArrayList dataExam = tabRender.getCheckedRows(tableExamStudentAnswerData, 1);

        if (dataExam.size() == 1) {
            // go to panelExamStudentAnswerForm
            cardLayoutEntity.show(panelExamStudentAnswer, "panelExamStudentAnswerForm");

            // clean the form but for editing mode
            cleanUpExamStudentAnswerForm(true);

            // call the API with id passed
            getExamStudentAnswer(dataExam.get(0).toString());

            // show the loading bar
            showLoadingStatus();
        } else {
            UIEffect.popup("please select 1 single data only!", this);
        }

    }//GEN-LAST:event_buttonEditExamStudentAnswerActionPerformed

    private void buttonDeleteExamStudentAnswerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDeleteExamStudentAnswerActionPerformed

        ArrayList dataExam = tabRender.getCheckedRows(tableExamStudentAnswerData, 1);

        if (dataExam.isEmpty()) {
            UIEffect.popup("Please select the row first!", this);
        } else {
            // passing id only

            deleteExamStudentAnswer(dataExam);
            showLoadingStatus();
        }

    }//GEN-LAST:event_buttonDeleteExamStudentAnswerActionPerformed

    private void labelRefreshExamStudentAnswerManagementMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRefreshExamStudentAnswerManagementMouseClicked

        // ## Refresh on Management
        // change to loading icon
        labelRefreshExamStudentAnswerManagement.setIcon(loadingImage);

        // refresh the table
        refreshExamStudentAnswer();


    }//GEN-LAST:event_labelRefreshExamStudentAnswerManagementMouseClicked

    private void labelRefreshExamStudentAnswerManagementMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRefreshExamStudentAnswerManagementMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_labelRefreshExamStudentAnswerManagementMouseEntered

    private void labelRefreshExamStudentAnswerManagementMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRefreshExamStudentAnswerManagementMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_labelRefreshExamStudentAnswerManagementMouseExited

    private void buttonCancelExamStudentAnswerFormActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelExamStudentAnswerFormActionPerformed

        cardLayoutInnerCenter.show(panelInnerCenter, "panelExamStudentAnswer");
        cardLayoutEntity = (CardLayout) panelExamStudentAnswer.getLayout();
        cardLayoutEntity.show(panelExamStudentAnswer, "panelExamStudentAnswerManagement");

        labelBackToHome.setVisible(true);

    }//GEN-LAST:event_buttonCancelExamStudentAnswerFormActionPerformed

    private void buttonSaveExamStudentAnswerFormActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveExamStudentAnswerFormActionPerformed

        cardLayoutInnerCenter.show(panelInnerCenter, "panelExamStudentAnswer");
        cardLayoutEntity = (CardLayout) panelExamStudentAnswer.getLayout();
        cardLayoutEntity.show(panelExamStudentAnswer, "panelExamStudentAnswerManagement");

        labelBackToHome.setVisible(true);

        saveExamStudentAnswer();

    }//GEN-LAST:event_buttonSaveExamStudentAnswerFormActionPerformed

    private void labelIconStatusExamStudentAnswerMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelIconStatusExamStudentAnswerMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_labelIconStatusExamStudentAnswerMouseClicked

    private void buttonAddExamQuestionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddExamQuestionActionPerformed

        // ## Add New from Management
        cardLayoutEntity.show(panelExamQuestions, "panelExamQuestionForm");
        // clean but not for editing mode
        cleanUpExamQuestionForm(false);

    }//GEN-LAST:event_buttonAddExamQuestionActionPerformed

    private void buttonEditExamQuestionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonEditExamQuestionActionPerformed

        // ## Edit Item from Management
        String noIDExamQuestion = tabRender.getCheckedRowValue(tableExamQuestionData, 1);

        if (noIDExamQuestion != null) {
            // go to examCategoryForm
            cardLayoutEntity.show(panelExamQuestions, "panelExamQuestionForm");

            // clean the form but for editing mode
            cleanUpExamQuestionForm(true);

            // call the API with id passed
            getExamQuestion(noIDExamQuestion);

            // show the loading bar
            showLoadingStatus();
        } else {
            UIEffect.popup("please select 1 single data only!", this);
        }

    }//GEN-LAST:event_buttonEditExamQuestionActionPerformed

    private void buttonDeleteExamQuestionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDeleteExamQuestionActionPerformed

        ArrayList dataExam = tabRender.getCheckedRows(tableExamQuestionData, 1);

        if (dataExam.isEmpty()) {
            UIEffect.popup("Please select the row first!", this);
        } else {
            // passing id only

            deleteExamQuestion(dataExam);
            showLoadingStatus();
        }

    }//GEN-LAST:event_buttonDeleteExamQuestionActionPerformed

    private void labelRefreshExamQuestionManagementMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRefreshExamQuestionManagementMouseClicked

        // ## Refresh on Management
        // change to loading icon
        labelRefreshExamQuestionManagement.setIcon(loadingImage);

        // refresh the table
        refreshExamQuestions();

    }//GEN-LAST:event_labelRefreshExamQuestionManagementMouseClicked

    private void labelRefreshExamQuestionManagementMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRefreshExamQuestionManagementMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_labelRefreshExamQuestionManagementMouseEntered

    private void labelRefreshExamQuestionManagementMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRefreshExamQuestionManagementMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_labelRefreshExamQuestionManagementMouseExited

    private void buttonCancelExamQuestionFormActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelExamQuestionFormActionPerformed

        // clear all from ArrayList
        isiExamQuestionOptions.clear();

        cardLayoutInnerCenter.show(panelInnerCenter, "panelExamQuestions");
        cardLayoutEntity = (CardLayout) panelExamQuestions.getLayout();
        cardLayoutEntity.show(panelExamQuestions, "panelExamQuestionsManagement");

        labelBackToHome.setVisible(true);

    }//GEN-LAST:event_buttonCancelExamQuestionFormActionPerformed

    private void buttonSaveExamQuestionFormActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveExamQuestionFormActionPerformed

        cardLayoutInnerCenter.show(panelInnerCenter, "panelExamQuestions");
        cardLayoutEntity = (CardLayout) panelExamQuestions.getLayout();
        cardLayoutEntity.show(panelExamQuestions, "panelExamQuestionsManagement");

        saveExamQuestion();

    }//GEN-LAST:event_buttonSaveExamQuestionFormActionPerformed

    private void tableExamQuestionOptionsFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tableExamQuestionOptionsFocusGained


    }//GEN-LAST:event_tableExamQuestionOptionsFocusGained

    private void tableExamQuestionOptionsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tableExamQuestionOptionsFocusLost


    }//GEN-LAST:event_tableExamQuestionOptionsFocusLost

    private void editOptionsExamQuestionMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_editOptionsExamQuestionMouseClicked

        if (editOptionsExamQuestion.isEnabled()) {

            // Ops is located at the 1st index
            String opVal = tabRender.getCheckedRowValue(tableExamQuestionOptions, 1);
            String initialValue = tabRender.getCheckedRowValue(tableExamQuestionOptions, 3);

            if (opVal != null) {

                // tabRender.collectExamQuestionOpsUsed(tableExamQuestionOptions, 1);
                ExamMultipleChoice jawaban = UIEffect.popupMultipleChoiceDialog(this, opVal, initialValue);

                if (jawaban != null) {
                    if (!jawaban.getTitle().isEmpty()) {
                        // update as well from the arraylist
                        updateExamQuestionOption(initialValue, jawaban.getTitle(), jawaban.isAnswer());
                        refreshExamQuestionOptionLocally();
                    }
                }

            }

        }

    }//GEN-LAST:event_editOptionsExamQuestionMouseClicked


    private void addOptionsExamQuestionMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addOptionsExamQuestionMouseClicked

        if (addOptionsExamQuestion.isEnabled()) {

            String op = tabRender.getOptionCodeFromTable(tableExamQuestionOptions);
            ExamMultipleChoice data = UIEffect.popupMultipleChoiceDialog(this, op);

            if (data != null) {
                isiExamQuestionOptions.add(data);
            }

            refreshExamQuestionOptionLocally();

        }

    }//GEN-LAST:event_addOptionsExamQuestionMouseClicked

    private void deleteOptionsExamQuestionMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_deleteOptionsExamQuestionMouseClicked

        // deleting the row selected
        if (deleteOptionsExamQuestion.isEnabled()) {
            // removing from the table UI 

            // the data is abcd string single format
            ArrayList dataExam = tabRender.getCheckedRows(tableExamQuestionOptions, 1);

            if (dataExam.isEmpty()) {
                UIEffect.popup("Please select the data first!", this);
            } else {
                // locking the control
                toggleExamQuestionOption(true);
                showLoadingStatus();

                // passing abcd only
                for (Object obj : dataExam) {

                    removeOptionExamQuestion(obj.toString());

                }

            }

            // refresh after deleting complete from list
            refreshExamQuestionOptionLocally();
        }

    }//GEN-LAST:event_deleteOptionsExamQuestionMouseClicked

    private void buttonAddCertificateStudentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddCertificateStudentActionPerformed

        // ## Add New from Management
        cardLayoutEntity.show(panelCertificateStudent, "panelCertificateStudentForm");
        // clean but not for editing mode
        cleanUpCertificateStudentForm(false);

    }//GEN-LAST:event_buttonAddCertificateStudentActionPerformed

    private void buttonEditCertificateStudentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonEditCertificateStudentActionPerformed

        // ## Edit Item from Management
        ArrayList dataStudent = tabRender.getCheckedRows(tableCertificateStudentData, 1);

        if (dataStudent.size() == 1) {
            // go to examCategoryForm
            cardLayoutEntity.show(panelCertificateStudent, "panelCertificateStudentForm");

            // clean the form but for editing mode
            cleanUpCertificateStudentForm(true);

            // call the API with id passed
            getCertificateStudent(dataStudent.get(0).toString());

            // show the loading bar
            showLoadingStatus();
        } else {
            UIEffect.popup("please select 1 single data only!", this);
        }

    }//GEN-LAST:event_buttonEditCertificateStudentActionPerformed

    private void buttonDeleteCertificateStudentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDeleteCertificateStudentActionPerformed

        ArrayList dataStudent = tabRender.getCheckedRows(tableCertificateStudentData, 1);

        if (dataStudent.isEmpty()) {
            UIEffect.popup("Please select the row first!", this);
        } else {
            // passing id only

            deleteCertificateStudent(dataStudent);
            showLoadingStatus();
        }

    }//GEN-LAST:event_buttonDeleteCertificateStudentActionPerformed

    private void labelRefreshCertificateStudentManagementMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRefreshCertificateStudentManagementMouseClicked

        // ## Refresh on Management
        // change to loading icon
        labelRefreshCertificateStudentManagement.setIcon(loadingImage);

        // refresh the table
        refreshCertificateStudent();

    }//GEN-LAST:event_labelRefreshCertificateStudentManagementMouseClicked

    private void labelRefreshCertificateStudentManagementMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRefreshCertificateStudentManagementMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_labelRefreshCertificateStudentManagementMouseEntered

    private void labelRefreshCertificateStudentManagementMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRefreshCertificateStudentManagementMouseExited


    }//GEN-LAST:event_labelRefreshCertificateStudentManagementMouseExited

    private void buttonCancelCertificateStudentFormActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelCertificateStudentFormActionPerformed

        cardLayoutInnerCenter.show(panelInnerCenter, "panelCertificateStudent");
        cardLayoutEntity = (CardLayout) panelCertificateStudent.getLayout();
        cardLayoutEntity.show(panelCertificateStudent, "panelCertificateStudentManagement");

        labelBackToHome.setVisible(true);

    }//GEN-LAST:event_buttonCancelCertificateStudentFormActionPerformed

    private void buttonSaveCertificateStudentFormActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveCertificateStudentFormActionPerformed

        cardLayoutInnerCenter.show(panelInnerCenter, "panelCertificateStudent");
        cardLayoutEntity = (CardLayout) panelCertificateStudent.getLayout();
        cardLayoutEntity.show(panelCertificateStudent, "panelCertificateStudentManagement");

        labelBackToHome.setVisible(true);

        saveCertificateStudent();
    }//GEN-LAST:event_buttonSaveCertificateStudentFormActionPerformed

    private void addFileCertificateStudentMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addFileCertificateStudentMouseClicked

        if (addFileCertificateStudent.isEnabled()) {

            // browse file for PDF only
            FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF File", "*.pdf", "pdf");
            fileChooser.setFileFilter(filter);

            int result = fileChooser.showOpenDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                certificateStudentFile = fileChooser.getSelectedFile();
                try {
                    labelPreviewCertificateStudent.setIcon(defaultPDFImage);
                    deleteFileCertificateStudent.setEnabled(true);
                    addFileCertificateStudent.setEnabled(false);
                } catch (Exception e) {
                    e.printStackTrace();
                    UIEffect.popup("Error while browse PDF Image applied!", this);
                }
            } else {
                // if no file was chosen
                certificateStudentFile = null;
            }
        }

    }//GEN-LAST:event_addFileCertificateStudentMouseClicked

    private void deleteFileCertificateStudentMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_deleteFileCertificateStudentMouseClicked

        // call the delete to the API
        deleteCertificatePicture();

        addFileCertificateStudent.setEnabled(true);
        deleteFileCertificateStudent.setEnabled(false);

        certificateStudentFile = null;
        labelPreviewCertificateStudent.setIcon(defaultCertImage);


    }//GEN-LAST:event_deleteFileCertificateStudentMouseClicked

    private void comboboxCategoryExamQuestionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboboxCategoryExamQuestionActionPerformed

        // get the category name
        if (comboboxCategoryExamQuestion.getItemCount() > 0) {
            String catName = comboboxCategoryExamQuestion.getSelectedItem().toString();

            // and then convert it to an ID
            // based on data stored locally in the jtable
            String anIDChosen = tabRender.getValueWithParameter(tableExamCategoryData, catName, 2, 1);

            // used for later submission
            examQCatID = Integer.parseInt(anIDChosen);

            // pass that ID to the Server to obtain the remaining sub category name listing
            getAllExamSubCategory(anIDChosen);

            // clearup the sub combobox because later will be updated by the async call from server callback
            comboboxSubCategoryExamQuestion.removeAllItems();
            comboboxSubCategoryExamQuestion.setEnabled(false);

        }
    }//GEN-LAST:event_comboboxCategoryExamQuestionActionPerformed

    private void labelPreviewExamQuestionMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelPreviewExamQuestionMouseClicked
        // TODO add your handling code here:
        if (labelPreviewExamQuestion.getIcon() != defaultExamQuestionPreview) {
            File lokasiExamPreview = new File(PathReference.ExamQuestionPreviewPath);
            CMDExecutor.openPicture(lokasiExamPreview);
        }

    }//GEN-LAST:event_labelPreviewExamQuestionMouseClicked

    private void labelBrowseExamPreviewImageMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelBrowseExamPreviewImageMouseClicked

        if (labelBrowseExamPreviewImage.getText().contains("Delete")) {
            // call the delete to the API
            deleteExamQuestionPreview();

            labelBrowseExamPreviewImage.setText(UIEffect.underline("Browse Picture"));
            examPreviewFile = null;

            labelPreviewExamQuestion.setIcon(defaultExamQuestionPreview);

        } else {
            // browse file
            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                examPreviewFile = fileChooser.getSelectedFile();
                try {
                    labelPreviewExamQuestion.setIcon(new ImageIcon(ImageIO.read(examPreviewFile)));

                    labelBrowseExamPreviewImage.setText(UIEffect.underline("Delete"));
                } catch (Exception e) {
                    e.printStackTrace();
                    UIEffect.popup("Error while browse picutre applied for exam question!", this);
                }
            } else {
                // if no file was chosen
                examPreviewFile = null;
            }
        }


    }//GEN-LAST:event_labelBrowseExamPreviewImageMouseClicked

    private void radiobuttonEssayExamQuestionItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_radiobuttonEssayExamQuestionItemStateChanged

        showProperExamQuestionAnswerLayout();

    }//GEN-LAST:event_radiobuttonEssayExamQuestionItemStateChanged

    private void radiobuttonMultipleChoiceExamQuestionItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_radiobuttonMultipleChoiceExamQuestionItemStateChanged

        showProperExamQuestionAnswerLayout();
    }//GEN-LAST:event_radiobuttonMultipleChoiceExamQuestionItemStateChanged

    private void comboboxSubCategoryExamQuestionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboboxSubCategoryExamQuestionActionPerformed

        if (comboboxSubCategoryExamQuestion.getItemCount() > 0) {
            String subSelected = comboboxSubCategoryExamQuestion.getSelectedItem().toString();

            // we grab the data from locally (array)
            examQSubCatID = getExamSubCategoryIdFromList(subSelected);
        }
    }//GEN-LAST:event_comboboxSubCategoryExamQuestionActionPerformed

    private void comboboxStatusExamStudentAnswerItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboboxStatusExamStudentAnswerItemStateChanged


    }//GEN-LAST:event_comboboxStatusExamStudentAnswerItemStateChanged

    private void comboboxStatusExamStudentAnswerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboboxStatusExamStudentAnswerActionPerformed

        if (comboboxStatusExamStudentAnswer.getSelectedItem() != null) {

            if (comboboxStatusExamStudentAnswer.getSelectedItem().equals("OK")) {
                if (scoreStudentAnswer != 0) {
                    labelScoreEarnedStudentAnswer.setText("+" + scoreStudentAnswer);
                }
                labelIconStatusExamStudentAnswer.setIcon(statusOKImage);
            } else if (comboboxStatusExamStudentAnswer.getSelectedItem().equals("WRONG")) {
                labelScoreEarnedStudentAnswer.setText("0");
                labelIconStatusExamStudentAnswer.setIcon(statusWRONGImage);
            } else if (comboboxStatusExamStudentAnswer.getSelectedItem().equals("CUSTOM")) {

                // show popup to enter the score
                String jawaban = UIEffect.popupInput("Input Score : ", "" + scoreStudentAnswer, this);

                // we used that score here
                labelScoreEarnedStudentAnswer.setText("+" + jawaban);
                labelIconStatusExamStudentAnswer.setIcon(statusCUSTOMImage);
            }

        } else {

            labelScoreEarnedStudentAnswer.setText("0");
            labelIconStatusExamStudentAnswer.setIcon(null);

        }
    }//GEN-LAST:event_comboboxStatusExamStudentAnswerActionPerformed

    private void numericQuestionIDExamStudentAnswerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_numericQuestionIDExamStudentAnswerStateChanged

        getExamQuestionDetail();

    }//GEN-LAST:event_numericQuestionIDExamStudentAnswerStateChanged

    private void buttonRefreshExamQuestionDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRefreshExamQuestionDetailActionPerformed

        getExamQuestionDetail();

    }//GEN-LAST:event_buttonRefreshExamQuestionDetailActionPerformed

    private void comboboxCertificateStudentCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboboxCertificateStudentCategoryActionPerformed

        // get the category name
        if (comboboxCertificateStudentCategory.getSelectedItem() != null) {
            String catName = comboboxCertificateStudentCategory.getSelectedItem().toString();

            // and then convert it to an ID
            // based on data stored locally in the jtable
            String anIDChosen = tabRender.getValueWithParameter(tableExamCategoryData, catName, 2, 1);

            // used for later submission
            certExamCatID = Integer.parseInt(anIDChosen);

        }

    }//GEN-LAST:event_comboboxCertificateStudentCategoryActionPerformed

    private void labelPreviewCertificateStudentMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelPreviewCertificateStudentMouseClicked

        if (labelPreviewCertificateStudent.getIcon() == defaultPDFImage) {
            // let's open the path

            try {
                Desktop.getDesktop().open(new File(PathReference.CertificateFilePath));
            } catch (Exception ex) {
                UIEffect.popup("Error while opening file certificate!", this);
                ex.printStackTrace();
            }

        }


    }//GEN-LAST:event_labelPreviewCertificateStudentMouseClicked

    private void labelNextMenuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelNextMenuMouseClicked

        cardLayoutInnerCenter.show(panelInnerCenter, "panelExamMenu");

        labelBackToHome.setVisible(true);
        labelNextMenu.setVisible(false);

    }//GEN-LAST:event_labelNextMenuMouseClicked

    private void buttonAddClassRoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddClassRoomActionPerformed

        // ## Add New from Management
        cardLayoutEntity.show(panelClassRoom, "panelClassRoomForm");
        // clean but not for editing mode
        cleanUpClassRoomForm(false);


    }//GEN-LAST:event_buttonAddClassRoomActionPerformed

    private void buttonEditClassRoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonEditClassRoomActionPerformed

        // ## Edit Item from Management
        ArrayList dataClassRoom = tabRender.getCheckedRows(tableClassRoomData, 1);

        if (dataClassRoom.size() == 1) {
            // go to examCategoryForm
            cardLayoutEntity.show(panelClassRoom, "panelClassRoomForm");

            // clean the form but for editing mode
            cleanUpClassRoomForm(true);

            // call the API with id passed
            getClassRoom(dataClassRoom.get(0).toString());

            // show the loading bar
            showLoadingStatus();
        } else {
            UIEffect.popup("please select 1 single data only!", this);
        }


    }//GEN-LAST:event_buttonEditClassRoomActionPerformed

    private void buttonDeleteClassRoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDeleteClassRoomActionPerformed

        ArrayList dataClassRoom = tabRender.getCheckedRows(tableClassRoomData, 1);

        if (dataClassRoom.isEmpty()) {
            UIEffect.popup("Please select the row first!", this);
        } else {
            // passing id only

            deleteClassRoom(dataClassRoom);
            showLoadingStatus();
        }


    }//GEN-LAST:event_buttonDeleteClassRoomActionPerformed

    private void labelRefreshClassRoomMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRefreshClassRoomMouseClicked

        // ## Refresh on Management
        // change to loading icon
        labelRefreshClassRoom.setIcon(loadingImage);

        // refresh the table
        refreshClassRoom();

    }//GEN-LAST:event_labelRefreshClassRoomMouseClicked

    private void labelRefreshClassRoomMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRefreshClassRoomMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_labelRefreshClassRoomMouseEntered

    private void labelRefreshClassRoomMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRefreshClassRoomMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_labelRefreshClassRoomMouseExited

    private void buttonCancelClassRoomFormActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelClassRoomFormActionPerformed

        cardLayoutEntity.show(panelClassRoom, "panelClassRoomManagement");
        labelBackToHome.setVisible(true);
    }//GEN-LAST:event_buttonCancelClassRoomFormActionPerformed

    private void buttonSaveClassRoomFormActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveClassRoomFormActionPerformed

        // ## Save UI Form
        cardLayoutEntity.show(panelClassRoom, "panelClassRoomManagement");
        saveClassRoom();

    }//GEN-LAST:event_buttonSaveClassRoomFormActionPerformed

    private void comboboxUsernameClassRoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboboxUsernameClassRoomActionPerformed
        // TODO add your handling code here:
        Object name = comboboxUsernameClassRoom.getSelectedItem();
        if (name != null) {
            if (!name.toString().equalsIgnoreCase("unknown")) {
                instructorID = getInstructorIDLocally(name.toString());
            }
        }

        System.out.println("We got " + instructorID);

    }//GEN-LAST:event_comboboxUsernameClassRoomActionPerformed

    private void comboboxClassRegSchedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboboxClassRegSchedActionPerformed
        
        // if the class is actually for exam
        // we enabled the radio button exam category
        if(true){
            panelExamCategorySched.setVisible(true);
        }else{
            panelExamCategorySched.setVisible(false);
        }
        
    }//GEN-LAST:event_comboboxClassRegSchedActionPerformed

    private void comboboxExamCategorySchedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboboxExamCategorySchedActionPerformed
        
        if (comboboxExamCategorySched.getSelectedItem() != null) {
            String catName = comboboxExamCategorySched.getSelectedItem().toString();

            // and then convert it to an ID
            // based on data stored locally in the jtable
            String anIDChosen = tabRender.getValueWithParameter(tableExamCategoryData, catName, 2, 1);

            // used for later submission
            schedExamCatID = Integer.parseInt(anIDChosen);

        }
        
    }//GEN-LAST:event_comboboxExamCategorySchedActionPerformed

    private void getExamQuestionDetail() {

        textareaQuestionExamStudentAnswer.setText("loading...");
        buttonRefreshExamQuestionDetail.setIcon(loadingImage);

        String anID = numericQuestionIDExamStudentAnswer.getValue().toString();
        getExamQuestion(anID);

    }

    private void showProperExamQuestionAnswerLayout() {

        if (radiobuttonEssayExamQuestion.isSelected()) {

            // show the essay
            cardLayoutEntity = (CardLayout) panelAnswerExamQuestion.getLayout();
            cardLayoutEntity.show(panelAnswerExamQuestion, "panelEssayExamQuestion");

        } else {

            // show the multiple choice table
            cardLayoutEntity = (CardLayout) panelAnswerExamQuestion.getLayout();
            cardLayoutEntity.show(panelAnswerExamQuestion, "panelMultipleChoiceExamQuestion");

        }

    }

    private void enableUserFormSave() {

        if (editMode) {
            boolean changes = false;

            if (!userEdited.getUsername().equals(textfieldUsername.getText())) {
                changes = true;
            } else if (!userEdited.getPass().equals(textfieldPass.getText())) {
                changes = true;
            } else if (!userEdited.getMobile().equals(textfieldMobile.getText())) {
                changes = true;
            } else if (!userEdited.getEmail().equals(textfieldEmail.getText())) {
                changes = true;
            } else if (!userEdited.getAddress().equals(textareaAddress.getText())) {
                changes = true;
            } else if (propicFile != null) {
                if (!propicFile.getAbsolutePath().contains(userEdited.getPropic())) {
                    changes = true;
                }
            }

            buttonSaveUserForm.setEnabled(changes);
        } else {

            // when this is new form
            boolean allow = false;

            if (!UIEffect.isEmpty(textfieldUsername)) {
                if (!UIEffect.isEmpty(textfieldPass)) {
                    if (!UIEffect.isEmpty(textfieldEmail)) {
                        if (!UIEffect.isEmpty(textfieldMobile)) {
                            if (!UIEffect.isEmpty(textareaAddress)) {
                                allow = true;
                            }
                        }
                    }
                }
            }

            buttonSaveUserForm.setEnabled(allow);

        }
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
    private javax.swing.JLabel addExamSubCategory;
    private javax.swing.JLabel addFileCertificateStudent;
    private javax.swing.JLabel addOptionsExamQuestion;
    private javax.swing.JButton buttonAddAttendance;
    private javax.swing.JButton buttonAddCertificateStudent;
    private javax.swing.JButton buttonAddClassRoom;
    private javax.swing.JButton buttonAddDocument;
    private javax.swing.JButton buttonAddExamCategory;
    private javax.swing.JButton buttonAddExamQuestion;
    private javax.swing.JButton buttonAddExamStudentAnswer;
    private javax.swing.JButton buttonAddPayment;
    private javax.swing.JButton buttonAddSchedule;
    private javax.swing.JButton buttonAddUser;
    private javax.swing.JButton buttonAttendance;
    private javax.swing.JButton buttonBugsReported;
    private javax.swing.JButton buttonBugsReported1;
    private javax.swing.JButton buttonCancelAttendanceForm;
    private javax.swing.JButton buttonCancelBugsReportedForm;
    private javax.swing.JButton buttonCancelCertificateStudentForm;
    private javax.swing.JButton buttonCancelClassRoomForm;
    private javax.swing.JButton buttonCancelDocumentForm;
    private javax.swing.JButton buttonCancelExamCategoryForm;
    private javax.swing.JButton buttonCancelExamQuestionForm;
    private javax.swing.JButton buttonCancelExamStudentAnswerForm;
    private javax.swing.JButton buttonCancelPaymentForm;
    private javax.swing.JButton buttonCancelScheduleForm;
    private javax.swing.JButton buttonCancelUserForm;
    private javax.swing.JButton buttonClassRoom;
    private javax.swing.JButton buttonDeleteAttendance;
    private javax.swing.JButton buttonDeleteBugsReported;
    private javax.swing.JButton buttonDeleteCertificateStudent;
    private javax.swing.JButton buttonDeleteClassRoom;
    private javax.swing.JButton buttonDeleteDocument;
    private javax.swing.JButton buttonDeleteExamCategory;
    private javax.swing.JButton buttonDeleteExamQuestion;
    private javax.swing.JButton buttonDeleteExamStudentAnswer;
    private javax.swing.JButton buttonDeletePayment;
    private javax.swing.JButton buttonDeleteSchedule;
    private javax.swing.JButton buttonDeleteUser;
    private javax.swing.JButton buttonDocumentManagement;
    private javax.swing.JButton buttonEditAttendance;
    private javax.swing.JButton buttonEditCertificateStudent;
    private javax.swing.JButton buttonEditClassRoom;
    private javax.swing.JButton buttonEditDocument;
    private javax.swing.JButton buttonEditExamCategory;
    private javax.swing.JButton buttonEditExamQuestion;
    private javax.swing.JButton buttonEditExamStudentAnswer;
    private javax.swing.JButton buttonEditPayment;
    private javax.swing.JButton buttonEditSchedule;
    private javax.swing.JButton buttonEditUser;
    private javax.swing.JButton buttonExamCategory;
    private javax.swing.JButton buttonExamQuestions;
    private javax.swing.JButton buttonExamStudentAnswer;
    private javax.swing.JButton buttonLogout;
    private javax.swing.JButton buttonLogout1;
    private javax.swing.JButton buttonPayment;
    private javax.swing.JButton buttonRefreshExamQuestionDetail;
    private javax.swing.JButton buttonSaveAttendanceForm;
    private javax.swing.JButton buttonSaveBugsReportedForm;
    private javax.swing.JButton buttonSaveCertificateStudentForm;
    private javax.swing.JButton buttonSaveClassRoomForm;
    private javax.swing.JButton buttonSaveDocumentForm;
    private javax.swing.JButton buttonSaveExamCategoryForm;
    private javax.swing.JButton buttonSaveExamQuestionForm;
    private javax.swing.JButton buttonSaveExamStudentAnswerForm;
    private javax.swing.JButton buttonSavePaymentForm;
    private javax.swing.JButton buttonSaveScheduleForm;
    private javax.swing.JButton buttonSaveUserForm;
    private javax.swing.JButton buttonSchedule;
    private javax.swing.JButton buttonStudentCertificate;
    private javax.swing.JButton buttonUserManagement;
    private javax.swing.JButton buttonViewBugsReported;
    private javax.swing.JButton buttonXXX;
    private javax.swing.JButton buttonXXXX;
    private javax.swing.JComboBox<String> comboboxAppNameBugsReported;
    private javax.swing.JComboBox<String> comboboxCategoryExamQuestion;
    private javax.swing.JComboBox<String> comboboxCertificateStudentCategory;
    private javax.swing.JComboBox<String> comboboxCertificateStudentUsername;
    private javax.swing.JComboBox<String> comboboxClassRegAttendance;
    private javax.swing.JComboBox<String> comboboxClassRegSched;
    private javax.swing.JComboBox<String> comboboxDaySched;
    private javax.swing.JComboBox<String> comboboxExamCategorySched;
    private javax.swing.JComboBox<String> comboboxMethodPayment;
    private javax.swing.JComboBox<String> comboboxStatusAttendance;
    private javax.swing.JComboBox<String> comboboxStatusExamStudentAnswer;
    private javax.swing.JComboBox<String> comboboxSubCategoryExamQuestion;
    private javax.swing.JComboBox<String> comboboxUsernameAttendance;
    private javax.swing.JComboBox<String> comboboxUsernameBugsReported;
    private javax.swing.JComboBox<String> comboboxUsernameClassRoom;
    private javax.swing.JComboBox<String> comboboxUsernameDoc;
    private javax.swing.JComboBox<String> comboboxUsernameExamStudentAnswer;
    private javax.swing.JComboBox<String> comboboxUsernamePayment;
    private javax.swing.JComboBox<String> comboboxUsernameSched;
    private javax.swing.JLabel deleteExamSubCategory;
    private javax.swing.JLabel deleteFileCertificateStudent;
    private javax.swing.JLabel deleteOptionsExamQuestion;
    private javax.swing.JLabel editExamSubCategory;
    private javax.swing.JLabel editOptionsExamQuestion;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane15;
    private javax.swing.JScrollPane jScrollPane16;
    private javax.swing.JScrollPane jScrollPane17;
    private javax.swing.JScrollPane jScrollPane18;
    private javax.swing.JScrollPane jScrollPane19;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane20;
    private javax.swing.JScrollPane jScrollPane21;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JLabel labelAttendanceManagement;
    private javax.swing.JLabel labelAttendanceManagement1;
    private javax.swing.JLabel labelBackToHome;
    private javax.swing.JLabel labelBottomPadding;
    private javax.swing.JLabel labelBrowseExamPreviewImage;
    private javax.swing.JLabel labelBrowseScreenshotBugsReported;
    private javax.swing.JLabel labelBrowseScreenshotPayment;
    private javax.swing.JLabel labelBrowseSignatureAttendance;
    private javax.swing.JLabel labelBugsReportedManagement;
    private javax.swing.JLabel labelCertificateStudentManagement;
    private javax.swing.JLabel labelClose;
    private javax.swing.JLabel labelExamCategoryManagement;
    private javax.swing.JLabel labelExamCategoryManagement1;
    private javax.swing.JLabel labelExamStudentAnswerManagement;
    private javax.swing.JLabel labelIconStatusExamStudentAnswer;
    private javax.swing.JLabel labelLinkChangeFileDoc;
    private javax.swing.JLabel labelLinkChangePicture;
    private javax.swing.JLabel labelLoadingStatus;
    private javax.swing.JLabel labelMinimize;
    private javax.swing.JLabel labelNextMenu;
    private javax.swing.JLabel labelPreviewCertificateStudent;
    private javax.swing.JLabel labelPreviewExamQuestion;
    private javax.swing.JLabel labelPreviewPicture;
    private javax.swing.JLabel labelRefreshAttendance;
    private javax.swing.JLabel labelRefreshBugsReported;
    private javax.swing.JLabel labelRefreshCertificateStudentManagement;
    private javax.swing.JLabel labelRefreshClassRoom;
    private javax.swing.JLabel labelRefreshDocument;
    private javax.swing.JLabel labelRefreshExamCategoryManagement;
    private javax.swing.JLabel labelRefreshExamQuestionManagement;
    private javax.swing.JLabel labelRefreshExamStudentAnswerManagement;
    private javax.swing.JLabel labelRefreshPayment;
    private javax.swing.JLabel labelRefreshSchedule;
    private javax.swing.JLabel labelRefreshUser;
    private javax.swing.JLabel labelRightPadding;
    private javax.swing.JLabel labelScheduleManagement;
    private javax.swing.JLabel labelScoreEarnedStudentAnswer;
    private javax.swing.JLabel labelScreenshotBugsReported;
    private javax.swing.JLabel labelScreenshotPayment;
    private javax.swing.JLabel labelSignatureAttendance;
    private javax.swing.JLabel labelTime;
    private javax.swing.JLabel labelUserManagement;
    private javax.swing.JList<String> listAnotherClassSched;
    private javax.swing.JSpinner numericQuestionIDExamStudentAnswer;
    private javax.swing.JPanel panelAnswerExamQuestion;
    private javax.swing.JPanel panelAttendance;
    private javax.swing.JPanel panelAttendanceControl;
    private javax.swing.JPanel panelAttendanceForm;
    private javax.swing.JPanel panelAttendanceManagement;
    private javax.swing.JPanel panelAttendanceTable;
    private javax.swing.JPanel panelBugsReported;
    private javax.swing.JPanel panelBugsReportedForm;
    private javax.swing.JPanel panelBugsReportedManagement;
    private javax.swing.JPanel panelCenter;
    private javax.swing.JPanel panelCertificateStudent;
    private javax.swing.JPanel panelCertificateStudentControl;
    private javax.swing.JPanel panelCertificateStudentForm;
    private javax.swing.JPanel panelCertificateStudentManagement;
    private javax.swing.JPanel panelCertificateStudentTable;
    private javax.swing.JPanel panelClassRoom;
    private javax.swing.JPanel panelClassRoomControl;
    private javax.swing.JPanel panelClassRoomForm;
    private javax.swing.JPanel panelClassRoomManagement;
    private javax.swing.JPanel panelClassRoomTable;
    private javax.swing.JPanel panelDocument;
    private javax.swing.JPanel panelDocumentControl;
    private javax.swing.JPanel panelDocumentForm;
    private javax.swing.JPanel panelDocumentManagement;
    private javax.swing.JPanel panelDocumentTable;
    private javax.swing.JPanel panelEssayExamQuestion;
    private javax.swing.JPanel panelExamCategory;
    private javax.swing.JPanel panelExamCategoryControl;
    private javax.swing.JPanel panelExamCategoryForm;
    private javax.swing.JPanel panelExamCategoryManagement;
    private javax.swing.JPanel panelExamCategorySched;
    private javax.swing.JPanel panelExamCategoryTable;
    private javax.swing.JPanel panelExamMenu;
    private javax.swing.JPanel panelExamQuestionControl;
    private javax.swing.JPanel panelExamQuestionForm;
    private javax.swing.JPanel panelExamQuestionTable;
    private javax.swing.JPanel panelExamQuestions;
    private javax.swing.JPanel panelExamQuestionsManagement;
    private javax.swing.JPanel panelExamStudentAnswer;
    private javax.swing.JPanel panelExamStudentAnswerControl;
    private javax.swing.JPanel panelExamStudentAnswerForm;
    private javax.swing.JPanel panelExamStudentAnswerManagement;
    private javax.swing.JPanel panelExamStudentAnswerTable;
    private javax.swing.JPanel panelHeader;
    private javax.swing.JPanel panelHomeMenu;
    private javax.swing.JPanel panelInnerCenter;
    private javax.swing.JPanel panelMultipleChoiceExamQuestion;
    private javax.swing.JPanel panelPayment;
    private javax.swing.JPanel panelPaymentControl;
    private javax.swing.JPanel panelPaymentForm;
    private javax.swing.JPanel panelPaymentManagement;
    private javax.swing.JPanel panelPaymentTable;
    private javax.swing.JPanel panelReportedBugsControl;
    private javax.swing.JPanel panelReportedBugsTable;
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
    private javax.swing.JRadioButton radioButtonCertificateStudentReleased;
    private javax.swing.JRadioButton radioButtonCertificateStudentWaiting;
    private javax.swing.ButtonGroup radioButtonGroupStatusCertificate;
    private javax.swing.ButtonGroup radioButtonGroupTypeExamQuestion;
    private javax.swing.JRadioButton radiobuttonEssayExamQuestion;
    private javax.swing.JRadioButton radiobuttonMultipleChoiceExamQuestion;
    private javax.swing.JSpinner spinnerHourSched;
    private javax.swing.JSpinner spinnerMinutesSched;
    private javax.swing.JTable tableAttendanceData;
    private javax.swing.JTable tableBugsReportedData;
    private javax.swing.JTable tableCertificateStudentData;
    private javax.swing.JTable tableClassRoomData;
    private javax.swing.JTable tableDocumentData;
    private javax.swing.JTable tableExamCategoryData;
    private javax.swing.JTable tableExamQuestionData;
    private javax.swing.JTable tableExamQuestionOptions;
    private javax.swing.JTable tableExamStudentAnswerData;
    private javax.swing.JTable tableExamSubCategoryData;
    private javax.swing.JTable tablePaymentData;
    private javax.swing.JTable tableScheduleData;
    private javax.swing.JTable tableUserData;
    private javax.swing.JTextArea textAreaAnswerEssayExamQuestion;
    private javax.swing.JTextArea textAreaDescriptionBugsReported;
    private javax.swing.JTextArea textareaAddress;
    private javax.swing.JTextArea textareaAnswerExamStudentAnswer;
    private javax.swing.JTextArea textareaDescriptionClassRoom;
    private javax.swing.JTextArea textareaDescriptionDoc;
    private javax.swing.JTextArea textareaQuestionExamStudentAnswer;
    private javax.swing.JTextField textfieldAmountPayment;
    private javax.swing.JTextField textfieldBaseScoreExamCategory;
    private javax.swing.JTextField textfieldCodeExamCategory;
    private com.github.lgooddatepicker.components.DatePicker textfieldDateReleaseCertificateStudent;
    private javax.swing.JTextField textfieldEmail;
    private javax.swing.JTextField textfieldExamQuestion;
    private javax.swing.JTextField textfieldFilenameDoc;
    private javax.swing.JTextField textfieldIPAddressBugsReported;
    private javax.swing.JTextField textfieldMobile;
    private javax.swing.JTextField textfieldNameClassRoom;
    private javax.swing.JTextField textfieldPass;
    private javax.swing.JTextField textfieldScorePointExamQuestion;
    private javax.swing.JTextField textfieldTitleBugsReported;
    private javax.swing.JTextField textfieldTitleDoc;
    private javax.swing.JTextField textfieldTitleExamCategory;
    private javax.swing.JTextField textfieldUrlDoc;
    private javax.swing.JTextField textfieldUsername;
    // End of variables declaration//GEN-END:variables

    private String getExamCategoryNameLocally(int idIn) {

        // this will enquery to the jtable locally, 
        // thus it will have a memory-safer time
        // using 1th index as ID
        // and 2nd index as Value to be used
        return tabRender.getValueWithID(tableExamCategoryData, idIn, 1, 2);

    }

    private int getInstructorIDLocally(String usernameIn) {

        // this will enquery to the jtable locally, 
        // thus it will have a memory-safer time
        // using 1th index as ID
        // and 2nd index as Value to be used
        String data = tabRender.getValueWithText(tableUserData, usernameIn, 2, 1);

        if (data != null) {
            return Integer.parseInt(data);
        }

        return -1;

    }

    private void loadUserPictureLocally() {

        String propic = configuration.getStringValue(Keys.USER_PROPIC);

        System.out.println("Trying to load " + propic);

        lockUserForm(false);
        hideLoadingStatus();

        if (!propic.contains("default")) {
            // set the propic
            UIEffect.iconChanger(labelPreviewPicture, (propic));

            // change the text of the browse button
            labelLinkChangePicture.setText("Delete");
        }

    }

    private void loadCertificatePictureLocally() {

        String propic = configuration.getStringValue(Keys.CERTIFICATE_PICTURE);

        System.out.println("Trying to load " + propic);

        lockCertificateStudentForm(false);
        hideLoadingStatus();

        if (!propic.contains("default")) {
            // set the propic
            labelPreviewCertificateStudent.setIcon(defaultPDFImage);

            addFileCertificateStudent.setEnabled(false);
            deleteFileCertificateStudent.setEnabled(true);

        }

    }

    private void loadScreenshotPaymentLocally() {

        String propic = configuration.getStringValue(Keys.SCREENSHOT_LAST_PAYMENT);

        System.out.println("Trying to load " + propic);

        lockPaymentForm(false);
        hideLoadingStatus();

        if (!propic.contains("not available")) {
            // set the propic
            UIEffect.iconChanger(labelScreenshotPayment, (propic));
            // change the browse link
            labelBrowseScreenshotPayment.setText(UIEffect.underline("Delete"));
        }

    }

    private void loadExamPreviewLocally() {

        String propic = configuration.getStringValue(Keys.EXAM_QUESTION_PREVIEW);

        System.out.println("Trying to load " + propic);

        hideLoadingStatus();

        if (!propic.contains("not available")) {
            // set the propic
            UIEffect.iconChanger(labelPreviewExamQuestion, (propic));
            // change the browse link
            labelBrowseExamPreviewImage.setText(UIEffect.underline("Delete"));
        }

    }

    private void loadScreenshotBugsReportedLocally() {

        String propic = configuration.getStringValue(Keys.SCREENSHOT_REPORT_BUGS);

        System.out.println("Trying to load " + propic);

        lockBugsReportedForm(false);
        hideLoadingStatus();

        if (!propic.contains("not available")) {
            // set the propic
            UIEffect.iconChanger(labelScreenshotBugsReported, (propic));
            // change the browse link
            labelBrowseScreenshotBugsReported.setText(UIEffect.underline("Delete"));
        }

    }

    private void loadSignaturePictureLocally() {

        String signaturePic = configuration.getStringValue(Keys.SIGNATURE_ATTENDANCE);

        System.out.println("Trying to load " + signaturePic);

        lockAttendanceForm(false);
        hideLoadingStatus();

        if (!signaturePic.contains("not available")) {
            // set the propic
            UIEffect.iconChanger(labelSignatureAttendance, (signaturePic));

            // change the text of the browse button
            labelBrowseSignatureAttendance.setText("Delete");
        }

    }

    private void lockUserForm(boolean b) {

        textareaAddress.setEnabled(!b);
        textareaAddress.setEditable(!b);

        textfieldEmail.setEnabled(!b);
        textfieldMobile.setEnabled(!b);
        textfieldPass.setEnabled(!b);
        textfieldUsername.setEnabled(!b);

        if (editMode) {
            buttonSaveUserForm.setEnabled(b);
        } else {
            buttonSaveUserForm.setEnabled(!b);
        }
    }

    private void lockAttendanceForm(boolean b) {

        comboboxClassRegAttendance.setEnabled(!b);
        comboboxStatusAttendance.setEnabled(!b);
        comboboxUsernameAttendance.setEnabled(!b);

        //labelBrowseSignatureAttendance.setVisible(!b);
    }

    private void lockExamQuestionForm(boolean b) {
        comboboxCategoryExamQuestion.setEnabled(!b);
        comboboxSubCategoryExamQuestion.setEnabled(!b);

        textfieldExamQuestion.setEnabled(!b);
        textfieldScorePointExamQuestion.setEnabled(!b);

        textAreaAnswerEssayExamQuestion.setEnabled(!b);

        radiobuttonEssayExamQuestion.setEnabled(!b);
        radiobuttonMultipleChoiceExamQuestion.setEnabled(!b);

        addOptionsExamQuestion.setEnabled(!b);

    }

    private void lockExamCategoryForm(boolean b) {
        textfieldTitleExamCategory.setEnabled(!b);
        textfieldCodeExamCategory.setEnabled(!b);
        textfieldBaseScoreExamCategory.setEnabled(!b);

        addExamSubCategory.setEnabled(!b);

    }

    private void lockClassRoomForm(boolean b) {
        textfieldNameClassRoom.setEnabled(!b);
        textareaDescriptionClassRoom.setEnabled(!b);
        comboboxUsernameClassRoom.setEnabled(!b);

    }

    private void lockCertificateStudentForm(boolean b) {
        comboboxCertificateStudentCategory.setEnabled(!b);
        comboboxCertificateStudentUsername.setEnabled(!b);
        textfieldDateReleaseCertificateStudent.setEnabled(!b);

        radioButtonCertificateStudentReleased.setEnabled(!b);
        radioButtonCertificateStudentWaiting.setEnabled(!b);

        addFileCertificateStudent.setEnabled(!b);

    }

    private void lockExamStudentAnswerForm(boolean b) {

        comboboxUsernameExamStudentAnswer.setEnabled(!b);
        textareaAnswerExamStudentAnswer.setEnabled(!b);
        textareaQuestionExamStudentAnswer.setEnabled(!b);

        comboboxStatusExamStudentAnswer.setEnabled(!b);

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

    private void lockBugsReportedForm(boolean b) {

        textfieldTitleBugsReported.setEnabled(!b);
        textfieldIPAddressBugsReported.setEnabled(!b);
        textAreaDescriptionBugsReported.setEnabled(!b);

        comboboxUsernameBugsReported.setEnabled(!b);
        comboboxAppNameBugsReported.setEnabled(!b);

        labelScreenshotBugsReported.setEnabled(!b);

    }

    private void lockPaymentForm(boolean b) {

        textfieldAmountPayment.setEnabled(!b);
        comboboxUsernamePayment.setEnabled(!b);
        comboboxMethodPayment.setEnabled(!b);

        labelScreenshotPayment.setEnabled(!b);

    }

    private void cleanUpUserForm(boolean editWork) {

        editMode = editWork;

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
            showLoadingStatus();
        }

        // protect the form from hijack saving
        buttonSaveUserForm.setEnabled(false);

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
            showLoadingStatus();
        }

    }

    private void cleanUpExamCategoryForm(boolean editWork) {

        editMode = editWork;

        isiSubCategory = new ArrayList<ExamSubCategory>();

        textfieldTitleExamCategory.setText("");
        textfieldCodeExamCategory.setText("");

        // this is default entry
        TableRenderer.clearData(tableExamSubCategoryData);

        if (editMode) {
            // we lock first
            // so later it will be unlocked by async success call

            lockExamCategoryForm(editMode);
            showLoadingStatus();
        }

    }

    private void cleanUpClassRoomForm(boolean editWork) {

        editMode = editWork;

        textfieldNameClassRoom.setText("");
        textareaDescriptionClassRoom.setText("");

        comboboxUsernameClassRoom.setSelectedIndex(-1);

        if (editMode) {
            // we lock first
            // so later it will be unlocked by async success call

            lockClassRoomForm(editMode);
            showLoadingStatus();
        }

    }

    private void cleanUpCertificateStudentForm(boolean editWork) {

        editMode = editWork;

        comboboxCertificateStudentCategory.setSelectedIndex(-1);
        comboboxCertificateStudentUsername.setSelectedIndex(-1);
        radioButtonCertificateStudentReleased.setSelected(false);
        textfieldDateReleaseCertificateStudent.clear();

        addFileCertificateStudent.setEnabled(true);
        deleteFileCertificateStudent.setEnabled(false);

        labelPreviewCertificateStudent.setIcon(defaultCertImage);

        if (editMode) {
            // we lock first
            // so later it will be unlocked by async success call

            lockCertificateStudentForm(editMode);
            showLoadingStatus();
        }

    }

    private void cleanUpExamStudentAnswerForm(boolean editWork) {

        editMode = editWork;

        comboboxUsernameExamStudentAnswer.setSelectedIndex(-1);
        comboboxStatusExamStudentAnswer.setSelectedIndex(-1);

        labelScoreEarnedStudentAnswer.setText("0");

        textareaAnswerExamStudentAnswer.setText("");
        textareaQuestionExamStudentAnswer.setText("");

        if (editMode) {
            // we lock first
            // so later it will be unlocked by async success call
            lockExamStudentAnswerForm(editMode);
            showLoadingStatus();
        }

    }

    private void fillExamStudentAnswer(ExamQuestion entry) {

        textareaQuestionExamStudentAnswer.setEnabled(true);

        // we build also the options here if it's possible
        if (entry.getJenis() == 1 || entry.getJenis() == 3) {
            // 1 is for abcd
            // 2 is for essay
            // 3 is for ab
            String multipleOps = "A. " + entry.getAnswer() + "\n"
                    + "B. " + entry.getOption_b();

            if (entry.getJenis() != 3) {
                multipleOps = multipleOps + "\nC. " + entry.getOption_c() + "\n"
                        + "D. " + entry.getOption_d();
            }

            textareaQuestionExamStudentAnswer.setText(entry.getQuestion() + "\n" + multipleOps);
        } else {
            // for essay
            textareaQuestionExamStudentAnswer.setText(entry.getQuestion());
        }

        scoreStudentAnswer = entry.getScore_point();
    }

    private void fillComboboxExamCategoryName(ExamCategory[] entries) {

        // into dropdown combobox under every form related
        comboboxCategoryExamQuestion.removeAllItems();
        comboboxCertificateStudentCategory.removeAllItems();
        comboboxExamCategorySched.removeAllItems();

        for (ExamCategory es : entries) {
            comboboxCategoryExamQuestion.addItem(es.getTitle());
            comboboxCertificateStudentCategory.addItem(es.getTitle());
            comboboxExamCategorySched.addItem(es.getTitle());
        }

        comboboxCertificateStudentCategory.setEnabled(true);
        comboboxCategoryExamQuestion.setEnabled(true);
        comboboxExamCategorySched.setEnabled(true);
    }

    private void fillComboboxExamSubCategoryName(ExamSubCategory[] entries) {

        // into dropdown combobox under every form related
        comboboxSubCategoryExamQuestion.removeAllItems();

        for (ExamSubCategory es : entries) {
            comboboxSubCategoryExamQuestion.addItem(es.getTitle());
        }

        comboboxSubCategoryExamQuestion.setEnabled(true);

    }

    private void cleanUpExamQuestionForm(boolean editWork) {

        editMode = editWork;

        isiExamQuestionOptions = new ArrayList<ExamMultipleChoice>();

        textfieldExamQuestion.setText("");
        textfieldScorePointExamQuestion.setText("0");

        labelPreviewExamQuestion.setIcon(defaultExamQuestionPreview);
        labelBrowseExamPreviewImage.setText(UIEffect.underline("Browse Picture"));

        textAreaAnswerEssayExamQuestion.setText("");

        radiobuttonEssayExamQuestion.setSelected(true);
        comboboxCategoryExamQuestion.setSelectedIndex(0);

        // this is default entry
        TableRenderer.clearData(tableExamQuestionOptions);

        lockExamQuestionForm(editMode);
        if (editMode) {
            // we lock first
            // so later it will be unlocked by async success call
            // so we animate it here
            showLoadingStatus();
        }

        examPreviewFile = null;

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
            showLoadingStatus();
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
            showLoadingStatus();
        }

    }

    private void cleanUpBugsReportedForm() {

        comboboxUsernameBugsReported.setSelectedIndex(-1);
        comboboxAppNameBugsReported.setSelectedIndex(-1);
        textfieldTitleBugsReported.setText("");
        textfieldIPAddressBugsReported.setText("");
        textAreaDescriptionBugsReported.setText("");

        labelScreenshotBugsReported.setText("preview");
        labelScreenshotBugsReported.setIcon(null);
        labelBrowseScreenshotBugsReported.setText("Browse Picture");

        bugsFile = null;

    }

    private void cleanUpPaymentForm(boolean editWork) {

        editMode = editWork;

        textfieldAmountPayment.setText("");
        comboboxUsernamePayment.setSelectedIndex(-1);
        comboboxMethodPayment.setSelectedIndex(-1);

        labelScreenshotPayment.setText("preview");
        labelScreenshotPayment.setIcon(null);
        labelBrowseScreenshotPayment.setText("Browse Picture");

        payFile = null;

        if (editMode) {
            // we lock first
            // so later it will be unlocked by async success call

            lockPaymentForm(editMode);
            showLoadingStatus();
        }

    }

    private void toggleExamQuestionOption(boolean emptyness) {
        editOptionsExamQuestion.setEnabled(!emptyness);
        deleteOptionsExamQuestion.setEnabled(!emptyness);
    }

    private void toggleExamSubCategory(boolean emptyness) {
        editExamSubCategory.setEnabled(!emptyness);
        deleteExamSubCategory.setEnabled(!emptyness);
    }

    private void renderUsernameForCombobox(User[] dataIn, JComboBox jc) {
        jc.removeAllItems();

        for (User single : dataIn) {
            jc.addItem(single.getUsername());
        }

    }
    
    private void renderUsernameForComboboxClassRoom(User[] dataIn, JComboBox jc) {
        jc.removeAllItems();

        // this is from SERVER API
        // access_level = 1 = ADMIN
	// access_level = 2 = STUDENT
	// access_level = 3 = INSTRUCTOR
        
        for (User single : dataIn) {
            if(single.getAccess_level()==3){
            jc.addItem(single.getUsername());
            }
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

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    // all Thread Safe

                    if (urlTarget.equalsIgnoreCase(WebReference.ALL_USER)) {
                        User[] dataIn = objectG.fromJson(innerData, User[].class);
                        tabRender.render(tableUserData, dataIn);

                        // rendering the username for document ui form
                        renderUsernameForCombobox(dataIn, comboboxUsernameDoc);
                        renderUsernameForCombobox(dataIn, comboboxUsernameSched);
                        renderUsernameForCombobox(dataIn, comboboxUsernameAttendance);
                        renderUsernameForCombobox(dataIn, comboboxUsernamePayment);
                        renderUsernameForCombobox(dataIn, comboboxUsernameBugsReported);
                        renderUsernameForCombobox(dataIn, comboboxUsernameExamStudentAnswer);
                        renderUsernameForCombobox(dataIn, comboboxCertificateStudentUsername);
                        renderUsernameForComboboxClassRoom(dataIn, comboboxUsernameClassRoom);

                        hideLoadingStatus();

                    } else if (urlTarget.equalsIgnoreCase(WebReference.ALL_CLASSROOM)) {
                        ClassRoom[] dataIn = objectG.fromJson(innerData, ClassRoom[].class);

                        renderClassRoomForCombobox(dataIn, comboboxClassRegSched);
                        renderClassRoomForCombobox(dataIn, comboboxClassRegAttendance);

                        tabRender.render(tableClassRoomData, dataIn);

                        hideLoadingStatus();

                    } else if (urlTarget.equalsIgnoreCase(WebReference.ALL_REPORT_BUGS)) {
                        RBugs[] dataIn = objectG.fromJson(innerData, RBugs[].class);

                        tabRender.render(tableBugsReportedData, dataIn);
                        hideLoadingStatus();
                    } else if (urlTarget.equalsIgnoreCase(WebReference.ALL_EXAM_CATEGORY)) {
                        ExamCategory[] dataIn = objectG.fromJson(innerData, ExamCategory[].class);
                        tabRender.render(tableExamCategoryData, dataIn);

                        hideLoadingStatus();

                        // filling in another form that needs the data as well
                        // for obtaining its name
                        fillComboboxExamCategoryName(dataIn);

                    } else if (urlTarget.equalsIgnoreCase(WebReference.ALL_SCHEDULE)) {
                        Schedule[] dataIn = objectG.fromJson(innerData, Schedule[].class);
                        tabRender.render(tableScheduleData, dataIn);

                        hideLoadingStatus();

                    } else if (urlTarget.equalsIgnoreCase(WebReference.ALL_SCHEDULE_BY_DAY)) {
                        Schedule[] dataIn = objectG.fromJson(innerData, Schedule[].class);
                        renderScheduleForList(dataIn, listAnotherClassSched);

                        hideLoadingStatus();

                    } else if (urlTarget.equalsIgnoreCase(WebReference.ALL_ATTENDANCE)) {
                        Attendance[] dataIn = objectG.fromJson(innerData, Attendance[].class);
                        tabRender.render(tableAttendanceData, dataIn);

                        hideLoadingStatus();

                    } else if (urlTarget.equalsIgnoreCase(WebReference.ALL_CERTIFICATE_STUDENT)) {
                        CertificateStudent[] dataIn = objectG.fromJson(innerData, CertificateStudent[].class);
                        tabRender.render(tableCertificateStudentData, dataIn);

                        hideLoadingStatus();

                    } else if (urlTarget.equalsIgnoreCase(WebReference.ALL_EXAM_QUESTION)) {
                        ExamQuestion[] dataIn = objectG.fromJson(innerData, ExamQuestion[].class);
                        tabRender.render(tableExamQuestionData, dataIn);

                        hideLoadingStatus();

                    } else if (urlTarget.equalsIgnoreCase(WebReference.ALL_EXAM_STUDENT_ANSWER)) {
                        ExamStudentAnswer[] dataIn = objectG.fromJson(innerData, ExamStudentAnswer[].class);
                        tabRender.render(tableExamStudentAnswerData, dataIn);

                        hideLoadingStatus();

                    } else if (urlTarget.equalsIgnoreCase(WebReference.ALL_DOCUMENT)) {
                        Document[] dataIn = objectG.fromJson(innerData, Document[].class);
                        tabRender.render(tableDocumentData, dataIn);

                        hideLoadingStatus();

                    } else if (urlTarget.equalsIgnoreCase(WebReference.ALL_PAYMENT)) {
                        Payment[] dataIn = objectG.fromJson(innerData, Payment[].class);
                        tabRender.render(tablePaymentData, dataIn);

                        hideLoadingStatus();

                    } else if (urlTarget.equalsIgnoreCase(WebReference.DELETE_EXAM_SUBCATEGORY)) {
                        // once exam sub is deleted
                        // thus we refresh the table
                        refreshExamSubCategoryLocally();
                        hideLoadingStatus();

                    } else if (urlTarget.equalsIgnoreCase(WebReference.ADD_CLASSROOM)
                            || urlTarget.equalsIgnoreCase(WebReference.DELETE_CLASSROOM)
                            || urlTarget.equalsIgnoreCase(WebReference.UPDATE_CLASSROOM)) {
                        // once data is obtained
                        // thus we refresh the table
                        refreshClassRoom();

                    } else if (urlTarget.equalsIgnoreCase(WebReference.ADD_CERTIFICATE_STUDENT)
                            || urlTarget.equalsIgnoreCase(WebReference.DELETE_CERTIFICATE_STUDENT)
                            || urlTarget.equalsIgnoreCase(WebReference.UPDATE_CERTIFICATE_STUDENT)) {
                        // once data is obtained
                        // thus we refresh the table
                        refreshCertificateStudent();
                    } else if (urlTarget.equalsIgnoreCase(WebReference.ADD_EXAM_STUDENT_ANSWER)
                            || urlTarget.equalsIgnoreCase(WebReference.DELETE_EXAM_STUDENT_ANSWER)
                            || urlTarget.equalsIgnoreCase(WebReference.UPDATE_EXAM_STUDENT_ANSWER)) {
                        // once new exam student answer given
                        // thus we refresh the table
                        refreshExamStudentAnswer();
                    } else if (urlTarget.equalsIgnoreCase(WebReference.ADD_EXAM_QUESTION)
                            || urlTarget.equalsIgnoreCase(WebReference.DELETE_EXAM_QUESTION)
                            || urlTarget.equalsIgnoreCase(WebReference.UPDATE_EXAM_QUESTION)) {
                        // once new exam question given
                        // thus we refresh the table
                        refreshExamQuestions();
                    } else if (urlTarget.equalsIgnoreCase(WebReference.ADD_EXAM_CATEGORY)
                            || urlTarget.equalsIgnoreCase(WebReference.DELETE_EXAM_CATEGORY)
                            || urlTarget.equalsIgnoreCase(WebReference.UPDATE_EXAM_CATEGORY)) {
                        // once new exam category
                        // thus we refresh the table
                        refreshExamCategory();
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
                    } else if (urlTarget.equalsIgnoreCase(WebReference.ADD_PAYMENT)
                            || urlTarget.equalsIgnoreCase(WebReference.DELETE_PAYMENT)
                            || urlTarget.equalsIgnoreCase(WebReference.UPDATE_PAYMENT)) {
                        // thus we refresh the payment table
                        refreshPayment();
                    } else if (urlTarget.equalsIgnoreCase(WebReference.ADD_ATTENDANCE)
                            || urlTarget.equalsIgnoreCase(WebReference.DELETE_ATTENDANCE)
                            || urlTarget.equalsIgnoreCase(WebReference.UPDATE_ATTENDANCE)) {
                        // thus we refresh the attendance table
                        refreshAttendance();
                    } else if (urlTarget.equalsIgnoreCase(WebReference.DELETE_REPORT_BUGS)) {
                        // thus we refresh the reportedbugs table
                        refreshBugsReported();
                    } else if (urlTarget.equalsIgnoreCase(WebReference.PROFILE_USER)) {

                        // we got the single user data here
                        User dataIn = objectG.fromJson(innerData, User.class);
                        // data temporarily saved here for button save checking
                        userEdited = dataIn;
                        renderUserForm(dataIn);

                    } else if (urlTarget.contains(WebReference.PICTURE_CERTIFICATE_STUDENT) && !urlTarget.contains("delete")) {

                        System.out.println("Obtaining Certificate Student Image from web is success...\nNow applying it locally.");
                        loadCertificatePictureLocally();

                    } else if (urlTarget.contains(WebReference.SCREENSHOT_REPORT_BUGS) && !urlTarget.contains("delete")) {

                        System.out.println("Obtaining Screenshot Picture from web is success...\nNow applying it locally.");
                        loadScreenshotBugsReportedLocally();

                    } else if (urlTarget.contains(WebReference.SCREENSHOT_PAYMENT) && !urlTarget.contains("delete")) {

                        System.out.println("Obtaining Screenshot Picture from web is success...\nNow applying it locally.");
                        loadScreenshotPaymentLocally();

                    } else if (urlTarget.contains(WebReference.PREVIEW_EXAM) && !urlTarget.contains("delete")) {

                        System.out.println("Obtaining Exam Preview Picture from web is success...\nNow applying it locally.");
                        loadExamPreviewLocally();

                        // open the form lock
                        lockExamQuestionForm(false);

                    } else if (urlTarget.contains(WebReference.PICTURE_USER) && !urlTarget.contains("delete")) {

                        System.out.println("Obtaining User Picture from web is success...\nNow applying it locally.");
                        loadUserPictureLocally();

                    } else if (urlTarget.contains(WebReference.SIGNATURE_ATTENDANCE) && !urlTarget.contains("delete")) {

                        System.out.println("Obtaining Signature Picture from web is success...\nNow applying it locally.");
                        loadSignaturePictureLocally();

                    } else if (urlTarget.equalsIgnoreCase(WebReference.DETAIL_REPORT_BUGS)) {

                        // we got the single reportbugs data here
                        RBugs dataIn = objectG.fromJson(innerData, RBugs.class);
                        renderBugsReportedForm(dataIn);

                    } else if (urlTarget.equalsIgnoreCase(WebReference.DETAIL_PAYMENT)) {

                        // we got the single payment data here
                        Payment dataIn = objectG.fromJson(innerData, Payment.class);
                        renderPaymentForm(dataIn);

                    } else if (urlTarget.equalsIgnoreCase(WebReference.DETAIL_ATTENDANCE)) {

                        // we got the single attendance data here
                        Attendance dataIn = objectG.fromJson(innerData, Attendance.class);
                        renderAttendanceForm(dataIn);

                    } else if (urlTarget.equalsIgnoreCase(WebReference.DETAIL_DOCUMENT)) {

                        // we got the single document data here
                        Document dataIn = objectG.fromJson(innerData, Document.class);
                        renderDocumentForm(dataIn);

                    } else if (urlTarget.equalsIgnoreCase(WebReference.DETAIL_CLASSROOM)) {

                        // we got the single data here
                        ClassRoom dataIn = objectG.fromJson(innerData, ClassRoom.class);
                        renderClassRoomForm(dataIn);


                    } else if (urlTarget.equalsIgnoreCase(WebReference.DETAIL_CERTIFICATE_STUDENT)) {

                        // we got the single data here
                        CertificateStudent dataIn = objectG.fromJson(innerData, CertificateStudent.class);
                        renderCertificateStudentForm(dataIn);

                        // open the form lock
                        lockCertificateStudentForm(false);

                    } else if (urlTarget.equalsIgnoreCase(WebReference.DETAIL_SCHEDULE)) {

                        // we got the single schedule data here
                        Schedule dataIn = objectG.fromJson(innerData, Schedule.class);
                        renderScheduleForm(dataIn);

                    } else if (urlTarget.equalsIgnoreCase(WebReference.DETAIL_EXAM_QUESTION)) {

                        // we got the single exam question data here
                        ExamQuestion dataIn = objectG.fromJson(innerData, ExamQuestion.class);
                        renderExamQuestionForm(dataIn);

                        // we also show this part of data under another form
                        fillExamStudentAnswer(dataIn);

                        // open the form lock
                        lockExamQuestionForm(false);

                    } else if (urlTarget.equalsIgnoreCase(WebReference.DETAIL_EXAM_CATEGORY)) {

                        // we got the single exam category data here
                        ExamCategory dataIn = objectG.fromJson(innerData, ExamCategory.class);
                        renderExamCategoryForm(dataIn);

                    } else if (urlTarget.equalsIgnoreCase(WebReference.DETAIL_EXAM_STUDENT_ANSWER)) {

                        // we got the single exam studentanswer data here
                        ExamStudentAnswer dataIn = objectG.fromJson(innerData, ExamStudentAnswer.class);
                        renderExamStudentAnswerForm(dataIn);

                        // open the form lock
                        lockExamStudentAnswerForm(false);

                    } else if (urlTarget.equalsIgnoreCase(WebReference.ALL_EXAM_SUBCATEGORY)) {

                        // we may got many exam sub category or even less
                        ExamSubCategory[] dataIn = objectG.fromJson(innerData, ExamSubCategory[].class);
                        tabRender.render(tableExamSubCategoryData, dataIn);

                        // we also put 'em inside an array
                        // for editing purposes later
                        if (isiSubCategory == null) {
                            isiSubCategory = new ArrayList<ExamSubCategory>();
                        }

                        isiSubCategory.clear();

                        for (ExamSubCategory dataSatuan : dataIn) {
                            isiSubCategory.add(dataSatuan);

                        }
                        // when the data is filled up we unlock the button
                        toggleExamSubCategory(tabRender.isTableEmpty(tableExamSubCategoryData));

                        // we also fill the name into another elements
                        fillComboboxExamSubCategoryName(dataIn);

                        lockExamCategoryForm(false);
                        hideLoadingStatus();

                    }

                }
            });

        } else {

            // when it is invalid but coming from all sched by day
            if (urlTarget.equalsIgnoreCase(WebReference.ALL_SCHEDULE_BY_DAY)) {
                // we clear up the list
                listAnotherClassSched.setModel(new DefaultListModel());
            } else if (urlTarget.equalsIgnoreCase(WebReference.UPDATE_ATTENDANCE)
                    || urlTarget.equalsIgnoreCase(WebReference.UPDATE_DOCUMENT)
                    || urlTarget.equalsIgnoreCase(WebReference.UPDATE_SCHEDULE)
                    || urlTarget.equalsIgnoreCase(WebReference.UPDATE_CERTIFICATE_STUDENT)
                    || urlTarget.equalsIgnoreCase(WebReference.UPDATE_EXAM_CATEGORY)
                    || urlTarget.equalsIgnoreCase(WebReference.UPDATE_EXAM_STUDENT_ANSWER)
                    || urlTarget.equalsIgnoreCase(WebReference.UPDATE_USER)) {
                showErrorStatus("error after updating");
            } else if (urlTarget.equalsIgnoreCase(WebReference.ADD_ATTENDANCE)
                    || urlTarget.equalsIgnoreCase(WebReference.ADD_DOCUMENT)
                    || urlTarget.equalsIgnoreCase(WebReference.ADD_EXAM_QUESTION)
                    || urlTarget.equalsIgnoreCase(WebReference.ADD_EXAM_STUDENT_ANSWER)
                    || urlTarget.equalsIgnoreCase(WebReference.ADD_EXAM_CATEGORY)
                    || urlTarget.equalsIgnoreCase(WebReference.ADD_CERTIFICATE_STUDENT)
                    || urlTarget.equalsIgnoreCase(WebReference.REGISTER_USER)
                    || urlTarget.equalsIgnoreCase(WebReference.ADD_SCHEDULE)) {
                showErrorStatus("error on saving new entry");
            } else if (urlTarget.equalsIgnoreCase(WebReference.ALL_EXAM_CATEGORY)
                    || urlTarget.equalsIgnoreCase(WebReference.ALL_EXAM_QUESTION)
                    || urlTarget.equalsIgnoreCase(WebReference.ALL_EXAM_STUDENT_ANSWER)
                    || urlTarget.equalsIgnoreCase(WebReference.ALL_ATTENDANCE)
                    || urlTarget.equalsIgnoreCase(WebReference.ALL_CLASSROOM)
                    || urlTarget.equalsIgnoreCase(WebReference.ALL_CERTIFICATE_STUDENT)
                    || urlTarget.equalsIgnoreCase(WebReference.ALL_DOCUMENT)
                    || urlTarget.equalsIgnoreCase(WebReference.ALL_PAYMENT)
                    || urlTarget.equalsIgnoreCase(WebReference.ALL_SCHEDULE)
                    || urlTarget.equalsIgnoreCase(WebReference.ALL_USER)
                    || urlTarget.equalsIgnoreCase(WebReference.ALL_REPORT_BUGS)) {

                // hide the loading at the moment
                // from the previous call
                hideLoadingStatus();

            } else if (urlTarget.equalsIgnoreCase(WebReference.ALL_EXAM_SUBCATEGORY)) {

                // since the sub category of the exam is not exist
                // thus, we just turn on the form from here
                lockExamCategoryForm(false);
                hideLoadingStatus();

                // clear also the subcategory table
                TableRenderer.clearData(tableExamSubCategoryData);

                refreshExamSubCategoryLocally();

            } else if (urlTarget.equalsIgnoreCase(WebReference.DETAIL_EXAM_QUESTION)) {
                // showing no question for the student question ans
                textareaQuestionExamStudentAnswer.setText("no data!");
                buttonRefreshExamQuestionDetail.setIcon(refreshImage);
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
        labelRefreshBugsReported.setIcon(refreshImage);
        labelRefreshExamCategoryManagement.setIcon(refreshImage);
        labelRefreshExamQuestionManagement.setIcon(refreshImage);
        labelRefreshExamStudentAnswerManagement.setIcon(refreshImage);
        labelRefreshCertificateStudentManagement.setIcon(refreshImage);
        labelRefreshClassRoom.setIcon(refreshImage);
        buttonRefreshExamQuestionDetail.setIcon(refreshImage);

        labelLoadingStatus.setVisible(false);
    }

    private void showLoadingStatus() {
        labelLoadingStatus.setText("Loading...");
        labelLoadingStatus.setIcon(loadingImage);
        labelLoadingStatus.setVisible(true);
    }
}
