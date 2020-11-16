/*
 *  This is a Supplemental File from the Main Project used
 *  in Java Programming Core Fundamental II
 *  with FGroupIndonesia team.
 */
package helper;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 *
 * @author ASUS
 */
public class RegistryObtainer {

    public static void main(String[] args) throws Exception {
        new RegistryObtainer().getClientID();
    }

    private String hexToString(String hexStr) {
        int angka = Integer.decode(hexStr);
        return angka + "";
    }

    // this is for obtaining TeamViewer ID only
    public String getClientID() {

        String bitType = System.getProperty("os.arch");
        boolean is64Bit = bitType.contains("64");

        String command = null;
        String s = null;
        String clientID = null;

        if (is64Bit) {
            command = "reg query HKLM\\SOFTWARE\\Wow6432Node\\TeamViewer /v ClientID";
            //command="for /f \"tokens=3\" %%a in ('reg query HKLM\\Software\\Wow6432Node\\TeamViewer /v ClientID') do (set /a num = %%a) echo/%num%";
        } else {
            command = "reg query HKLM\\SOFTWARE\\TeamViewer /v ClientID";
        }

        try {
            Process proc = Runtime.getRuntime().exec(command);

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

            BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));


            /* from  x64bit machine,
    this is the final output:    4 lines of output
    0:    
    1:HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\TeamViewer
    2:ClientID    REG_DWORD    0x19ceff5f
    3:    
             */
            int line = 0;
            StringBuffer data = new StringBuffer();
            while ((s = stdInput.readLine()) != null) {

                // in case they found no key about that in registry
                if (s.equalsIgnoreCase("error:")) {
                    break;
                }

                if (line == 2) {
                    String inData[] = s.split("    ");
                    clientID = inData[3];
                }
                line++;

            }

        } catch (Exception ex) {

        }

        if (clientID != null) {
            System.out.println("we got " + this.hexToString(clientID));
            return this.hexToString(clientID);
        }

        return clientID;

    }

}
