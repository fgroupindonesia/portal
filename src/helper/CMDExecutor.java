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
public class CMDExecutor {

    public static void runTeamviewer() {
        String command = PathReference.TeamviewerPath;
        call(command);
    }

    public static void main(String[] args) {
        //runTeamviewer();
        //killTeamviewer();
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
