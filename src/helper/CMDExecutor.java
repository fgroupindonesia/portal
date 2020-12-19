/*
 *  This is a Supplemental File from the Main Project used
 *  in Java Programming Core Fundamental I
 *  with FGroupIndonesia team.
 */
package helper;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author ASUS
 */
public class CMDExecutor {

    public static String getMachineUniqueID() {

        // command for obtaining specific windows unique ID
        String command = "wmic csproduct get UUID";
        StringBuffer output = new StringBuffer();
        String val = null;

        try {

            Process SerNumProcess = Runtime.getRuntime().exec(command);
            BufferedReader sNumReader = new BufferedReader(new InputStreamReader(SerNumProcess.getInputStream()));

            String line = "";
            while ((line = sNumReader.readLine()) != null) {
                output.append(line + "\n");
            }
            val = output.toString().substring(output.indexOf("\n"), output.length()).trim();;

        } catch (Exception ex) {
            UIEffect.popup("Error while obtaining Machine Unique ID " + ex.getMessage(), null);
        }

        return val;

    }

    public static void openPicture(File fileIn) {
        try {
            Desktop.getDesktop().open(fileIn);
        } catch (Exception ex) {
            UIEffect.popup("Error while opening picture", null);
        }
    }

    // same functionallity
    public static void openDocument(File fileIn) {
        openPicture(fileIn);
    }

    public static void runTeamviewer() {

        // check first does he has installed?
        // check under C:\Program Files (x86)\TeamViewer
        String command = null;

        File dirInstalled = new File("C:\\Program Files (x86)\\TeamViewer");

        if (dirInstalled.exists() && dirInstalled.listFiles().length > 0) {
            // installed
            command = dirInstalled.getAbsolutePath() + "\\TeamViewer.exe";
        } else {
            command = PathReference.TeamviewerPath;
        }

        call(command);
    }

    public static void backupOldTeamviewer() {

        SimpleDateFormat smp = new SimpleDateFormat("yyyyMMddHHmmss");
        Date tgl = new Date();
        String tglBak = "_" + smp.format(tgl) + ".bak";
        String newName = PathReference.TeamviewerPath.replace(".exe", tglBak);

        try {
            Path source = Paths.get(PathReference.TeamviewerPath);
            Files.move(source, source.resolveSibling(newName));

        } catch (Exception ex) {
            UIEffect.popup("something error " + ex.getMessage(), null);
        }
    }

    public static void main(String[] args) {
        //runTeamviewer();
        //killTeamviewer();
    }

    public static boolean isRunning(String appName) {

        boolean yesRun = false;
        String line;
        String pidInfo = "";

        try {

            Process p = Runtime.getRuntime().exec(System.getenv("windir") + "\\system32\\" + "tasklist.exe");

            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

            while ((line = input.readLine()) != null) {
                pidInfo += line;
            }

            input.close();

        } catch (Exception ex) {

        }

        if (pidInfo.contains(appName)) {
            yesRun = true;
        }

        return yesRun;

    }

    public static void killTeamviewer() {
        // this will kill the TMviewer as non-admin usage
        String command = "taskkill.exe /IM TeamViewer_.exe /F";

        call(command);

        // this will kill the TMviewer even from Admin usage
        command = "wmic process where name=\"TeamViewer.exe\" call terminate";

        call(command);
    }

    private static void call(String cmd) {

        try {
            Runtime.getRuntime().exec(cmd);
            Thread.sleep(3000);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

}
