/*
 *  This is a Supplemental File from the Main Project used
 *  in Java Programming Core Fundamental I
 *  with FGroupIndonesia team.
 */
package helper.jxbrowser;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 *
 * @author ASUS
 */

// this class used to temporarily disable & enable the sout TextOut
public class ConfigureSysOut {

    static PrintStream outOriginal = System.out;

    public static void enableSysout() {
        System.setOut(outOriginal);
    }

    public static void disableSysout() {

        System.setOut(new PrintStream(new OutputStream() {

            @Override
            public void write(int b) throws IOException {
            }

        }));
    }

}