/*
 *  This is a Supplemental File from the Main Project used
 *  in Java Programming Core Fundamental I
 *  with FGroupIndonesia team.
 */
package helper;

import frames.ClientFrame;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;

/**
 *
 * @author ASUS
 */
public class UIEffect {

    protected static final DateFormat CLOCK_FORMAT = new SimpleDateFormat("dd/MMM/yyyy hh:mm:ss a");
    private static JLabel labelTime, labelInterval;
    private static Timer timer, scheduleTimer;
    private static Date scheduleDate, nowDate;
    protected static ClientFrame frameRef;

    //this is for client Frame 
    public static void setFrameRef(ClientFrame aF) {
        frameRef = aF;
    }

    //this is for JLabel HTML
    public static String underline(String text) {
        return "<html><u>" + text + "</u></html>";
    }

    // this is for UTF-8 decoder
    public static String decodeSafe(String val) {
        String en = null;
        try {
            en = URLDecoder.decode(val, "UTF-8");
        } catch (Exception ex) {

        }

        return en;
    }

    private static void addingTimerSchedule() {
        scheduleTimer = new Timer(1010, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateInterval();
            }

        });
        scheduleTimer.start();
    }

    private static void addingTimer() {
        timer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateClock();
            }

        });
        timer.start();
    }

    public static void stopTimeEffect() {
        if (timer != null) {
            timer.stop();
        }

        if (scheduleTimer != null) {
            scheduleTimer.stop();
        }

        System.out.println("Stopping timer is done!");
    }

    public static void focusLostCurrency(JTextField el) {
        // when empty set back to Rp.0
        if (el.getText().trim().length() == 0) {
            el.setText(new RupiahGenerator().getText(0));
        } else {
            // when there is a number 
            // we add Rp.x
            double nilai = Double.parseDouble(el.getText());
            el.setText(new RupiahGenerator().getText(nilai));
        }

    }

    public static void focusGainCurrency(JTextField el) {

        // when there is a number we remove all marker Rp. etc
        // until remain only number
        if (el.getText().length() > 0) {
            String nilai = el.getText();
            if (nilai.contains("Rp")) {
                el.setText(new RupiahGenerator().getIntNumber(nilai) + "");
            } else {
                el.setText(nilai.replace(".0", ""));
            }

        }

    }

    protected static void updateInterval() {

        nowDate = new Date();

        //in milliseconds
        long diff = scheduleDate.getTime() - nowDate.getTime();

        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);

        if (diffDays != -1) {

            if (labelInterval != null) {
                if (diffDays > 0) {
                    labelInterval.setText("Next Class : " + diffDays + " hari, " + diffHours + " jam, " + diffMinutes + " menit, " + diffSeconds + " detik.");
                } else if (diffHours > 0) {
                    labelInterval.setText("Next Class : hari ini, " + diffHours + " jam, " + diffMinutes + " menit, " + diffSeconds + " detik.");
                } else if (diffHours == 0) {
                    labelInterval.setText("Next Class : hari ini, " + diffMinutes + " menit, " + diffSeconds + " detik.");
                }
            }

            if (frameRef.isNotifHourBefore()) {
                if (diffDays == 0 && diffHours == 1 && diffMinutes == 0 && (diffSeconds == 0 || diffSeconds == 1)) {
                    frameRef.playNotifSound();
                }
            } else if (frameRef.isNotifDayBefore()) {
                if (diffDays == 1 && diffHours == 0 && diffMinutes == 0 && diffSeconds == 0) {
                    frameRef.playNotifSound();
                }
            }
        } else {
            scheduleTimer.stop();
            labelInterval.setText("");
        }
    }

    protected static void updateClock() {

        if (labelTime != null) {
            labelTime.setText(CLOCK_FORMAT.format(System.currentTimeMillis()));
        }

    }

    public static void playTimeEffect(JLabel el) {
        labelTime = el;
        addingTimer();
    }

    public static void playIntervalTimeEffect(JLabel el, Date scheduleTime) {
        labelInterval = el;
        addingTimerSchedule();

        scheduleDate = scheduleTime;
    }

    public static void popup(String message, JFrame ref) {

        JOptionPane.showMessageDialog(ref, message);

    }

    public static boolean isEmpty(JTextArea el) {

        return (el.getText().trim().length() == 0);

    }

    public static boolean isEmpty(JTextField el) {

        return (el.getText().trim().length() == 0);

    }

    public static void iconChanger(JFrame frame) {
        ImageIcon img = new ImageIcon(PathReference.LogoPath);
        frame.setIconImage(img.getImage());
    }

    public static void iconChanger(JLabel el, String aPath) {
        BufferedImage img = null;
        try {
            System.out.println("The new data stored is " + aPath);
            img = ImageIO.read(new File(aPath));
            //System.out.println("complete ");
        } catch (Exception e) {
            //e.printStackTrace();
            //set the default image
            System.out.println("something goes wrong...");
            PathReference.setPropicFileName("default.png");
            String newPath = PathReference.UserPropicPath;

            try {
                img = ImageIO.read(new File(newPath));
            } catch (Exception ex) {

            }
        }

        System.out.println("Fitting in image...");
        // set the picture fitting 
        Image dimg = img.getScaledInstance(el.getWidth(), el.getHeight(),
                Image.SCALE_SMOOTH);

        el.setIcon(new ImageIcon(dimg));

        // important to remove from the memory usage
        dimg.flush();
        img.flush();

    }

    public static void mouseHover(JLabel el) {
        el.setForeground(Color.BLUE);

    }

    public static void mouseExit(JLabel el) {
        el.setForeground(Color.BLACK);
    }

    public static void focusGained(JLabel el) {
        el.setForeground(Color.blue);
    }

    public static void focusLost(JLabel el) {
        el.setForeground(Color.black);
    }

    public static void focusGained(JTextField el) {
        el.setBackground(Color.YELLOW);
    }

    public static void focusLost(JTextField el) {
        el.setBackground(null);
    }

}
