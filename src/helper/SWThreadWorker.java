/*
 *  This is a Supplemental File from the Main Project used
 *  in Java Programming Core Fundamental I
 *  with FGrnoupIndonesia team.
 */
package helper;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserContext;
import com.teamdev.jxbrowser.chromium.BrowserContextParams;
import com.teamdev.jxbrowser.chromium.BrowserPreferences;
import com.teamdev.jxbrowser.chromium.BrowserType;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import frames.LoginFrame;
import frames.MainClientFrame;
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
            case SWTKey.WORK_TEST_INTERNET:
                name = "test_internet";
                break;
            case SWTKey.WORK_BROWSER_PREPARE:
                name = "refresh_browser";
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
             case SWTKey.WORK_TEST_INTERNET:
                testInternet();
                break;
            case SWTKey.WORK_BROWSER_PREPARE:
                prepareBrowser();
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
                mainFrame.setBrowserBack(browser);
                mainFrame.setPanelInnerBrowserBack(panelInnerBrowser);
                break;

        }

        System.out.println("SWThreadWorker is done! " + whatWorkAsString());
    }

    Browser browser;
    JPanel panelInnerBrowser;
    private MainClientFrame mainFrame;
    private LoginFrame  loginFrame;
    HttpCall urlExecutor;

    public SWThreadWorker(MainClientFrame mfx) {
        setMainFrame(mfx);
        urlExecutor = new HttpCall(mainFrame);
    }
    
    public SWThreadWorker(LoginFrame mfx){
        setLoginFrame(mfx);
        urlExecutor = new HttpCall(loginFrame);
    }

    public void setMainFrame(MainClientFrame mf) {
        mainFrame = mf;
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
        urlExecutor.addData(k, v);
    }

    public void writeMode(boolean b) {
        urlExecutor.writeToDisk(b);
    }
    
    private void testInternet(){
         urlExecutor.isInternetAlive();
    }
    
    private void userLogin(){
        urlExecutor.start(WebReference.LOGIN_USER, HttpCall.METHOD_POST);
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

    private void refreshDocumentData() {

        urlExecutor.start(WebReference.ALL_DOCUMENT, HttpCall.METHOD_POST);
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
