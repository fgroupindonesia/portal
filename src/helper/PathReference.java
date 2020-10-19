/*
 *  This is a Supplemental File from the Main Project used
 *  in Java Programming Core Fundamental I
 *  with FGroupIndonesia team.
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
    public static String TeamviewerPath = ToolsDirName + "\\TeamViewer_Setup.exe";
    
}
