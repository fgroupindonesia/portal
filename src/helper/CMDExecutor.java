/*
 *  This is a Supplemental File from the Main Project used
 *  in Java Programming Core Fundamental I
 *  with FGroupIndonesia team.
 */
package helper;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 *
 * @author ASUS
 */
public class CMDExecutor {

    public static void runTeamviewer() {
        String command = PathReference.TeamviewerPath;
        call(command);
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
