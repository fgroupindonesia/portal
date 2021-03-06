/*
 *  This is a Portal Access for Client & Admin Usage
 *  (c) FGroupIndonesia, 2020.
 */
package helper;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserContext;
import com.teamdev.jxbrowser.chromium.BrowserContextParams;
import com.teamdev.jxbrowser.chromium.BrowserPreferences;
import com.teamdev.jxbrowser.chromium.BrowserType;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import frames.LoginFrame;
import frames.AdminFrame;
import frames.ClientFrame;
import helper.jxbrowser.ConfigureSysOut;
import helper.jxbrowser.JxBrowserHackUtil;
import helper.jxbrowser.JxVersion;
import java.awt.BorderLayout;
import java.io.File;
import java.util.UUID;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

/**
 *
 * @author ASUS
 */
public class SWThreadWorker extends SwingWorker<Object, Object> {

    private int work;

    public void setWork(int mode) {
        work = mode;
    }

    public int whatWork() {
        return work;
    }

    public String whatWorkAsString() {

        String name = null;

        switch (whatWork()) {
            case SWTKey.WORK_REMOTE_LOGIN_CHECK:
                name = "remote_login_check";
                break;
            case SWTKey.WORK_REMOTE_LOGIN_VERIFY:
                name = "remote_login_verify";
                break;
            case SWTKey.WORK_REMOTE_LOGIN_ACTIVATE:
                name = "remote_login_activate";
                break;
            case SWTKey.WORK_DOWNLOAD_TOOLS:
                name = "download_tools";
                break;
            case SWTKey.WORK_CHECK_TOOLS:
                name = "check_tools";
                break;
            case SWTKey.WORK_DELETE_SCREENSHOT_PAYMENT:
                name = "delete_screenshot_payment";
                break;
            case SWTKey.WORK_REFRESH_SCREENSHOT_PAYMENT:
                name = "refresh_screenshot_payment";
                break;
            case SWTKey.WORK_HISTORY_SAVE:
                name = "history_save";
                break;
            case SWTKey.WORK_REPORT_BUGS_SAVE:
                name = "report_bugs_save";
                break;
            case SWTKey.WORK_REPORT_BUGS_EDIT:
                name = "report_bugs_edit";
                break;
            case SWTKey.WORK_REPORT_BUGS_UPDATE:
                name = "report_bugs_update";
                break;
            case SWTKey.WORK_REPORT_BUGS_DELETE:
                name = "report_bugs_delete";
                break;
            case SWTKey.WORK_PAYMENT_SAVE:
                name = "payment_save";
                break;
            case SWTKey.WORK_PAYMENT_EDIT:
                name = "payment_edit";
                break;
            case SWTKey.WORK_PAYMENT_UPDATE:
                name = "payment_update";
                break;
            case SWTKey.WORK_PAYMENT_DELETE:
                name = "payment_delete";
                break;
            case SWTKey.WORK_DELETE_SIGNATURE:
                name = "delete_signature";
                break;
            case SWTKey.WORK_ATTENDANCE_SAVE:
                name = "attendance_save";
                break;
            case SWTKey.WORK_ATTENDANCE_EDIT:
                name = "attendance_edit";
                break;
            case SWTKey.WORK_ATTENDANCE_UPDATE:
                name = "attendance_update";
                break;
            case SWTKey.WORK_ATTENDANCE_DELETE:
                name = "attendance_delete";
                break;
            case SWTKey.WORK_SCHEDULE_SAVE:
                name = "schedule_save";
                break;
            case SWTKey.WORK_SCHEDULE_EDIT:
                name = "schedule_edit";
                break;
            case SWTKey.WORK_SCHEDULE_UPDATE:
                name = "schedule_update";
                break;
            case SWTKey.WORK_SCHEDULE_DELETE:
                name = "schedule_delete";
                break;
            case SWTKey.WORK_DOCUMENT_SAVE:
                name = "document_save";
                break;
            case SWTKey.WORK_DOCUMENT_EDIT:
                name = "document_edit";
                break;
            case SWTKey.WORK_DOCUMENT_UPDATE:
                name = "document_update";
                break;
            case SWTKey.WORK_DOCUMENT_DELETE:
                name = "document_delete";
                break;
            case SWTKey.WORK_DOCUMENT_DOWNLOAD:
                name = "document_download";
                break;
            case SWTKey.WORK_REFRESH_SCHEDULE_BY_DAY:
                name = "refresh_schedule_by_day";
                break;
            case SWTKey.WORK_DELETE_PICTURE:
                name = "delete_picture";
                break;
            case SWTKey.WORK_REFRESH_USER:
                name = "refresh_user";
                break;
            case SWTKey.WORK_USER_DELETE:
                name = "user_delete";
                break;
            case SWTKey.WORK_USER_UPDATE:
                name = "user_update";
                break;
            case SWTKey.WORK_USER_SAVE:
                name = "user_save";
                break;
            case SWTKey.WORK_TEST_INTERNET:
                name = "test_internet";
                break;
            case SWTKey.WORK_REFRESH_CLASSROOM:
                name = "refresh_classroom";
                break;
            case SWTKey.WORK_BROWSER_PREPARE:
                name = "refresh_browser";
                break;
            case SWTKey.WORK_REFRESH_SCREENSHOT_REPORT_BUGS:
                name = "refresh_screenshot_report_bugs";
                break;
            case SWTKey.WORK_REFRESH_REPORT_BUGS:
                name = "refresh_report_bugs";
                break;
            case SWTKey.WORK_REFRESH_DOCUMENT:
                name = "refresh_document";
                break;
            case SWTKey.WORK_REFRESH_ATTENDANCE:
                name = "refresh_attendance";
                break;
            case SWTKey.WORK_REFRESH_PAYMENT:
                name = "refresh_payment";
                break;
            case SWTKey.WORK_REFRESH_SCHEDULE:
                name = "refresh_schedule";
                break;
            case SWTKey.WORK_REFRESH_HISTORY:
                name = "refresh_history";
                break;
            case SWTKey.WORK_REFRESH_PROFILE:
                name = "refresh_profile";
                break;
            case SWTKey.WORK_REFRESH_PICTURE:
                name = "refresh_picture";
                break;
            case SWTKey.WORK_REFRESH_SIGNATURE:
                name = "refresh_signature";
                break;
            case SWTKey.WORK_REFRESH_EXAM_CATEGORY:
                name = "refresh_exam_category";
                break;
            case SWTKey.WORK_EXAM_CATEGORY_DELETE:
                name = "exam_category_delete";
                break;
            case SWTKey.WORK_EXAM_CATEGORY_UPDATE:
                name = "exam_category_update";
                break;
            case SWTKey.WORK_EXAM_CATEGORY_SAVE:
                name = "exam_category_save";
                break;
            case SWTKey.WORK_LOGIN:
                name = "login";
                break;

        }

        return name;

    }

    @Override
    protected Object doInBackground() throws Exception {

        System.out.println("I am working on " + whatWorkAsString());

        switch (whatWork()) {
            case SWTKey.WORK_REMOTE_LOGIN_ACTIVATE:
                remoteLoginActivate();
                break;
            case SWTKey.WORK_REMOTE_LOGIN_CHECK:
                remoteLoginCheck();
                break;
            case SWTKey.WORK_REMOTE_LOGIN_VERIFY:
                remoteLoginVerify();
                break;
            case SWTKey.WORK_REFRESH_SCREENSHOT_REPORT_BUGS:
                refreshScreenshotReportBugsData();
                break;
            case SWTKey.WORK_REFRESH_REPORT_BUGS:
                refreshReportBugsData();
                break;
            case SWTKey.WORK_DOWNLOAD_TOOLS:
                downloadTools();
                break;
            case SWTKey.WORK_CHECK_TOOLS:
                checkTools();
                break;
            case SWTKey.WORK_DELETE_SCREENSHOT_PAYMENT:
                screenshotDelete();
                break;
            case SWTKey.WORK_REFRESH_SCREENSHOT_PAYMENT:
                refreshScreenshotPaymentData();
                break;
            case SWTKey.WORK_HISTORY_SAVE:
                historySave();
                break;
            case SWTKey.WORK_REPORT_BUGS_UPDATE:
                reportBugsUpdate();
                break;
            case SWTKey.WORK_REPORT_BUGS_EDIT:
                reportBugsEdit();
                break;
            case SWTKey.WORK_REPORT_BUGS_SAVE:
                reportBugsSave();
                break;
            case SWTKey.WORK_REPORT_BUGS_DELETE:
                reportBugsDelete();
                break;
            case SWTKey.WORK_PAYMENT_UPDATE:
                paymentUpdate();
                break;
            case SWTKey.WORK_PAYMENT_EDIT:
                paymentEdit();
                break;
            case SWTKey.WORK_PAYMENT_SAVE:
                paymentSave();
                break;
            case SWTKey.WORK_PAYMENT_DELETE:
                paymentDelete();
                break;
            case SWTKey.WORK_DELETE_SIGNATURE:
                signatureDelete();
                break;
            case SWTKey.WORK_ATTENDANCE_UPDATE:
                attendanceUpdate();
                break;
            case SWTKey.WORK_ATTENDANCE_EDIT:
                attendanceEdit();
                break;
            case SWTKey.WORK_ATTENDANCE_SAVE:
                attendanceSave();
                break;
            case SWTKey.WORK_ATTENDANCE_DELETE:
                attendanceDelete();
                break;
            case SWTKey.WORK_SCHEDULE_UPDATE:
                scheduleUpdate();
                break;
            case SWTKey.WORK_SCHEDULE_EDIT:
                scheduleEdit();
                break;
            case SWTKey.WORK_SCHEDULE_SAVE:
                scheduleSave();
                break;
            case SWTKey.WORK_SCHEDULE_DELETE:
                scheduleDelete();
                break;
            case SWTKey.WORK_DOCUMENT_UPDATE:
                documentUpdate();
                break;
            case SWTKey.WORK_DOCUMENT_EDIT:
                documentEdit();
                break;
            case SWTKey.WORK_DOCUMENT_SAVE:
                documentSave();
                break;
            case SWTKey.WORK_DOCUMENT_DELETE:
                documentDelete();
                break;
            case SWTKey.WORK_DOCUMENT_DOWNLOAD:
                documentDownload();
                break;
            case SWTKey.WORK_DELETE_PICTURE:
                pictureDelete();
                break;
            case SWTKey.WORK_REFRESH_USER:
                refreshUserData();
                break;
            case SWTKey.WORK_USER_DELETE:
                userDelete();
                break;
            case SWTKey.WORK_USER_UPDATE:
                userUpdate();
                break;
            case SWTKey.WORK_USER_SAVE:
                userSave();
                break;
            case SWTKey.WORK_TEST_INTERNET:
                testInternet();
                break;
            case SWTKey.WORK_BROWSER_PREPARE:
                prepareBrowser();
                break;
            case SWTKey.WORK_REFRESH_CLASSROOM:
                refreshClassRoomData();
                break;
            case SWTKey.WORK_REFRESH_DOCUMENT:
                refreshDocumentData();
                break;
            case SWTKey.WORK_REFRESH_ATTENDANCE:
                refreshAttendanceData();
                break;
            case SWTKey.WORK_REFRESH_PAYMENT:
                refreshPaymentData();
                break;
            case SWTKey.WORK_REFRESH_SCHEDULE_BY_DAY:
                refreshScheduleDataByDay();
                break;
            case SWTKey.WORK_REFRESH_SCHEDULE:
                refreshScheduleData();
                break;
            case SWTKey.WORK_REFRESH_HISTORY:
                refreshHistoryData();
                break;
            case SWTKey.WORK_REFRESH_PROFILE:
                refreshProfileData();
                break;
            case SWTKey.WORK_REFRESH_PICTURE:
                refreshPictureData();
                break;
            case SWTKey.WORK_REFRESH_SIGNATURE:
                refreshSignatureData();
                break;
            case SWTKey.WORK_REFRESH_EXAM_CATEGORY:
                refreshExamCategoryData();
                break;
            case SWTKey.WORK_EXAM_CATEGORY_DELETE:
                examCategoryDelete();
                break;
            case SWTKey.WORK_EXAM_CATEGORY_UPDATE:
                examCategoryUpdate();
                break;
            case SWTKey.WORK_EXAM_CATEGORY_SAVE:
                examCategorySave();
                break;
            case SWTKey.WORK_LOGIN:
                userLogin();
                break;

        }

        return null;
    }

    @Override
    protected void done() {
        switch (whatWork()) {
            case SWTKey.WORK_BROWSER_PREPARE:
                mainClientFrame.setBrowserBack(browser);
                mainClientFrame.setPanelInnerBrowserBack(panelInnerBrowser);
                break;

        }

        System.out.println("SWThreadWorker is done! " + whatWorkAsString());
    }

    Browser browser;
    JPanel panelInnerBrowser;
    private ClientFrame mainClientFrame;
    private AdminFrame mainAdminFrame;
    private LoginFrame loginFrame;
    HttpCall urlExecutor;

    public SWThreadWorker(ClientFrame mfx) {
        setMainFrame(mfx);
        urlExecutor = new HttpCall(mainClientFrame);
    }

    public SWThreadWorker(AdminFrame mdx) {
        setMainFrame(mdx);
        urlExecutor = new HttpCall(mainAdminFrame);
    }

    public SWThreadWorker(LoginFrame mfx) {
        setLoginFrame(mfx);
        urlExecutor = new HttpCall(loginFrame);
    }

    public void setMainFrame(ClientFrame mf) {
        mainClientFrame = mf;
    }

    public void setMainFrame(AdminFrame mdf) {
        mainAdminFrame = mdf;
    }

    public void setLoginFrame(LoginFrame mf) {
        loginFrame = mf;
    }

    public void setBrowser(Browser br) {
        browser = br;
    }

    public void setPanelInnerBrowser(JPanel jp) {
        panelInnerBrowser = jp;
    }

    public void addData(String k, String v) {
        System.out.println("Key : " + k + " with value : " + v);
        urlExecutor.addData(k, v);
    }

    public void addFile(String k, File f) {
        urlExecutor.addFile(k, f);
    }

    public void writeMode(boolean b) {
        urlExecutor.writeToDisk(b);
    }

    private void testInternet() {
        urlExecutor.isInternetAlive();
    }

    private void remoteLoginActivate() {
        urlExecutor.start(WebReference.REMOTE_LOGIN_ACTIVATE, HttpCall.METHOD_POST);
    }

    private void remoteLoginCheck() {
        urlExecutor.start(WebReference.REMOTE_LOGIN_CHECK, HttpCall.METHOD_POST);
        // in progress
    }

    private void remoteLoginVerify() {
        // urlExecutor.start(WebReference.REMOTE_LOGIN_ACTIVATE, HttpCall.METHOD_POST);
        // in progress
    }

    private void userLogin() {
        urlExecutor.start(WebReference.LOGIN_USER, HttpCall.METHOD_POST);
    }

    private void userSave() {
        if (hasFile()) {
            urlExecutor.start(WebReference.REGISTER_USER, HttpCall.METHOD_POST_FILE);
        } else {
            urlExecutor.start(WebReference.REGISTER_USER, HttpCall.METHOD_POST);
        }
    }

    private void userUpdate() {
        if (hasFile()) {
            urlExecutor.start(WebReference.UPDATE_USER, HttpCall.METHOD_POST_FILE);
        } else {
            urlExecutor.start(WebReference.UPDATE_USER, HttpCall.METHOD_POST);
        }
    }

    private void userDelete() {
        urlExecutor.start(WebReference.DELETE_USER, HttpCall.METHOD_POST);
    }

    private void documentDelete() {
        urlExecutor.start(WebReference.DELETE_DOCUMENT, HttpCall.METHOD_POST);
    }

    private void documentDownload() {
        String urlManual = UIEffect.decodeSafe(urlExecutor.getData("url"));
        urlExecutor.start(urlManual, HttpCall.METHOD_GET);
    }

    private void documentSave() {
        if (hasFile()) {
            urlExecutor.start(WebReference.ADD_DOCUMENT, HttpCall.METHOD_POST_FILE);
        } else {
            urlExecutor.start(WebReference.ADD_DOCUMENT, HttpCall.METHOD_POST);
        }
    }

    private void documentEdit() {
        urlExecutor.start(WebReference.DETAIL_DOCUMENT, HttpCall.METHOD_POST);
    }

    private void documentUpdate() {
        if (hasFile()) {
            urlExecutor.start(WebReference.UPDATE_DOCUMENT, HttpCall.METHOD_POST_FILE);
        } else {
            urlExecutor.start(WebReference.UPDATE_DOCUMENT, HttpCall.METHOD_POST);
        }
    }

    private boolean hasFile() {
        return urlExecutor.isFileAttached();
    }

    private void reportBugsDelete() {
        urlExecutor.start(WebReference.DELETE_REPORT_BUGS, HttpCall.METHOD_POST);
    }

    private void paymentDelete() {
        urlExecutor.start(WebReference.DELETE_PAYMENT, HttpCall.METHOD_POST);
    }

    // this is for user picture
    private void pictureDelete() {
        urlExecutor.start(WebReference.DELETE_PICTURE, HttpCall.METHOD_POST);
    }

    // this is for payment picture
    private void screenshotDelete() {
        urlExecutor.start(WebReference.DELETE_SCREENSHOT, HttpCall.METHOD_POST);
    }

    // this is for attendance picture
    private void signatureDelete() {
        urlExecutor.start(WebReference.DELETE_SIGNATURE, HttpCall.METHOD_POST);
    }

    private void historySave() {
        urlExecutor.start(WebReference.ADD_HISTORY, HttpCall.METHOD_POST);

    }

    private void paymentSave() {
        if (hasFile()) {
            urlExecutor.start(WebReference.ADD_PAYMENT, HttpCall.METHOD_POST_FILE);
        } else {
            urlExecutor.start(WebReference.ADD_PAYMENT, HttpCall.METHOD_POST);
        }
    }

    private void paymentEdit() {
        urlExecutor.start(WebReference.DETAIL_PAYMENT, HttpCall.METHOD_POST);
    }

    private void paymentUpdate() {
        if (hasFile()) {
            urlExecutor.start(WebReference.UPDATE_PAYMENT, HttpCall.METHOD_POST_FILE);
        } else {
            urlExecutor.start(WebReference.UPDATE_PAYMENT, HttpCall.METHOD_POST);
        }
    }

    private void reportBugsSave() {
        if (hasFile()) {
            urlExecutor.start(WebReference.ADD_REPORT_BUGS, HttpCall.METHOD_POST_FILE);
        } else {
            urlExecutor.start(WebReference.ADD_REPORT_BUGS, HttpCall.METHOD_POST);
        }
    }

    private void reportBugsEdit() {
        urlExecutor.start(WebReference.DETAIL_REPORT_BUGS, HttpCall.METHOD_POST);
    }

    private void reportBugsUpdate() {
        if (hasFile()) {
            urlExecutor.start(WebReference.UPDATE_REPORT_BUGS, HttpCall.METHOD_POST_FILE);
        } else {
            urlExecutor.start(WebReference.UPDATE_REPORT_BUGS, HttpCall.METHOD_POST);
        }
    }

    private void attendanceDelete() {
        urlExecutor.start(WebReference.DELETE_ATTENDANCE, HttpCall.METHOD_POST);
    }

    private void attendanceSave() {
        if (hasFile()) {
            urlExecutor.start(WebReference.ADD_ATTENDANCE, HttpCall.METHOD_POST_FILE);
        } else {
            urlExecutor.start(WebReference.ADD_ATTENDANCE, HttpCall.METHOD_POST);
        }
    }

    private void attendanceEdit() {
        urlExecutor.start(WebReference.DETAIL_ATTENDANCE, HttpCall.METHOD_POST);
    }

    private void attendanceUpdate() {
        urlExecutor.start(WebReference.UPDATE_ATTENDANCE, HttpCall.METHOD_POST_FILE);
    }

    private void scheduleDelete() {
        urlExecutor.start(WebReference.DELETE_SCHEDULE, HttpCall.METHOD_POST);
    }

    private void scheduleSave() {
        urlExecutor.start(WebReference.ADD_SCHEDULE, HttpCall.METHOD_POST);
    }

    private void scheduleEdit() {
        urlExecutor.start(WebReference.DETAIL_SCHEDULE, HttpCall.METHOD_POST);
    }

    private void scheduleUpdate() {
        urlExecutor.start(WebReference.UPDATE_SCHEDULE, HttpCall.METHOD_POST);
    }

    private void examCategoryDelete() {
        urlExecutor.start(WebReference.DELETE_EXAM_CATEGORY, HttpCall.METHOD_POST);
    }

    private void examCategorySave() {
        urlExecutor.start(WebReference.ADD_EXAM_CATEGORY, HttpCall.METHOD_POST);
    }

    private void examCategoryEdit() {
        urlExecutor.start(WebReference.DETAIL_EXAM_CATEGORY, HttpCall.METHOD_POST);
    }

    private void examCategoryUpdate() {
        urlExecutor.start(WebReference.UPDATE_EXAM_CATEGORY, HttpCall.METHOD_POST);
    }
    
    private void refreshHistoryData() {

        urlExecutor.start(WebReference.LAST_HISTORY, HttpCall.METHOD_POST);
    }

    private void refreshProfileData() {
        urlExecutor.start(WebReference.PROFILE_USER, HttpCall.METHOD_POST);
    }

    private void refreshPictureData() {

        // the url is manually defined here
        String urlManual = WebReference.PICTURE_USER + "?propic=" + urlExecutor.getData("propic");
        urlExecutor.start(urlManual, HttpCall.METHOD_GET);
    }

    private void checkTools() {
        urlExecutor.start(WebReference.CHECK_TOOLS, HttpCall.METHOD_POST);
    }

    private void downloadTools() {

        // the url is manually defined here
        String urlManual = WebReference.DOWNLOAD_TOOLS + "?app_name=" + urlExecutor.getData("app_name");
        urlExecutor.start(urlManual, HttpCall.METHOD_GET);
    }

    private void refreshScreenshotReportBugsData() {

        // the url is manually defined here
        String urlManual = WebReference.SCREENSHOT_REPORT_BUGS + "?screenshot=" + urlExecutor.getData("screenshot");
        urlExecutor.start(urlManual, HttpCall.METHOD_GET);
    }

    private void refreshScreenshotPaymentData() {

        // the url is manually defined here
        String urlManual = WebReference.SCREENSHOT_PAYMENT + "?screenshot=" + urlExecutor.getData("screenshot");
        urlExecutor.start(urlManual, HttpCall.METHOD_GET);
    }

    private void refreshSignatureData() {

        // the url is manually defined here
        String urlManual = WebReference.SIGNATURE_ATTENDANCE + "?signature=" + urlExecutor.getData("signature");
        urlExecutor.start(urlManual, HttpCall.METHOD_GET);
    }

    private void refreshUserData() {

        urlExecutor.start(WebReference.ALL_USER, HttpCall.METHOD_POST);
    }

    private void refreshClassRoomData() {

        urlExecutor.start(WebReference.ALL_CLASSROOM, HttpCall.METHOD_POST);
    }

    private void refreshScheduleDataByDay() {

        urlExecutor.start(WebReference.ALL_SCHEDULE_BY_DAY, HttpCall.METHOD_POST);
    }

    private void refreshReportBugsData() {
        urlExecutor.start(WebReference.ALL_REPORT_BUGS, HttpCall.METHOD_POST);
    }

    private void refreshDocumentData() {

        urlExecutor.start(WebReference.ALL_DOCUMENT, HttpCall.METHOD_POST);
    }

     private void refreshExamCategoryData() {
        urlExecutor.start(WebReference.ALL_EXAM_CATEGORY, HttpCall.METHOD_POST);
    }
    
    private void refreshScheduleData() {

        urlExecutor.start(WebReference.ALL_SCHEDULE, HttpCall.METHOD_POST);
    }

    private void refreshAttendanceData() {

        urlExecutor.start(WebReference.ALL_ATTENDANCE, HttpCall.METHOD_POST);
    }

    private void refreshPaymentData() {

        urlExecutor.start(WebReference.ALL_PAYMENT, HttpCall.METHOD_POST);
    }

    private void prepareBrowser() {
        // disabling textout
        ConfigureSysOut.disableSysout();
        // hacking the JXBrowser applicants
        // with the difference context (cache) everytimes run
        JxBrowserHackUtil.hack(JxVersion.V6_22);

        String identity = UUID.randomUUID().toString();
        File folder = new File(PathReference.JXBrowserDirName + "\\" + identity);

        if (!folder.exists()) {
            folder.mkdir();
        }

        BrowserPreferences.setChromiumSwitches(
                "--disable-gpu",
                "--disable-gpu-compositing",
                "--enable-begin-frame-scheduling",
                "--software-rendering-fps=60"
        );

        BrowserContextParams params = new BrowserContextParams(folder.getAbsolutePath());
        BrowserContext context1 = new BrowserContext(params);
        browser = new Browser(BrowserType.LIGHTWEIGHT, context1);
        BrowserView browserView = new BrowserView(browser);

        panelInnerBrowser.add(browserView, BorderLayout.CENTER);
        browser.loadURL("http://youtube.com/fgroupindonesia");
        // activate back textout
        ConfigureSysOut.enableSysout();
    }

}
