/*
 *  This is a Portal Access for Client & Admin Usage
 *  (c) FGroupIndonesia, 2020.
 */
package helper;

/**
 *
 * @author ASUS
 */
public class PathReference {

    public static String SystemName = "Portal";
    public static String DirName = System.getenv("LOCALAPPDATA") + "\\" + SystemName;
    public static String ToolsDirName = DirName + "\\tools";
    public static String LanguagesDirName = DirName + "\\languages";
    public static String DocumentDirName = DirName + "\\documents";
    public static String BugsReportedDirName = DirName + "\\bugs";
    public static String ScreenshotPaymentDirName = DirName + "\\screenshots";
    public static String JXBrowserDirName = DirName + "\\jxbrowser";
    public static String TeamviewerPath = ToolsDirName + "\\TeamViewer_Setup.exe";
    public static String LogoPath = DirName + "\\fgroup.jpg";
    public static String AlarmPath = DirName + "\\alarm.wav";
    public static String UserPropicPath = DirName + "\\propic.jpg";
    public static String SignaturePath = DirName + "\\signature.jpg";
    public static String ScreenshotPaymentPath = DirName + "\\screenshot_payment.jpg";
    public static String DocumentFilePath = DocumentDirName + "\\some.pdf";

    public static void setPropicFileName(String name) {
        UserPropicPath = DirName + "\\" + name;
    }

    public static void setSignatureFileName(String name) {
        SignaturePath = DirName + "\\" + name;
    }

    public static void setScreenshotPaymentFileName(String name) {
        ScreenshotPaymentPath = ScreenshotPaymentDirName + "\\" + name;
    }

    public static void setDocumentFileName(String name) {
        DocumentFilePath = DocumentDirName + "\\" + name;
    }

    public static String getDocumentPath(String fname) {
        return DocumentDirName + "\\" + fname;
    }

    public static String getScreenshotPath(String fname) {
        return ScreenshotPaymentDirName + "\\" + fname;
    }
    
    public static String getScreenshotBugsReportedPath(String fname) {
        return BugsReportedDirName + "\\" + fname;
    }

    public static String getToolsPath(String fname) {
        return ToolsDirName + "\\" + fname;
    }
    public static String getLanguagePath(String fname) {
        return LanguagesDirName + "\\" + fname;
    }
}
