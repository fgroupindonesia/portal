/*
 *  This is a Portal Access for Client & Admin Usage
 *  (c) FGroupIndonesia, 2020.
 */
package helper;

import frames.ClientFrame;
import helper.language.LanguageSwitcher;
import java.awt.BorderLayout;

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
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;

/**
 *
 * @author ASUS
 */
public class UIEffect {

    public static int ACTION = -1;
    public static final int ACTION_AUTO_UPDATE = 1;
    public static final int ACTION_CHANGE_SCHEDULE = 2;

    protected static final DateFormat CLOCK_FORMAT = new SimpleDateFormat("dd/MMM/yyyy hh:mm:ss a");
    private static JLabel labelTime, labelInterval;
    private static Timer timer, scheduleTimer;
    private static Date scheduleDate, nowDate;
    protected static ClientFrame frameRef;
    private static LanguageSwitcher langHelper;
    private static StringBuffer stringMainHolder = new StringBuffer();
    private static StringBuffer stringSecondHolder = new StringBuffer();
    public static String selectedItem = null;

    public static void setLanguageHelper(LanguageSwitcher lgs) {
        langHelper = lgs;
    }

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
                    stringMainHolder.append("labelIntervalNextClass").append(" ");
                    stringMainHolder.append("labelIntervalDay").append(" ");
                    stringMainHolder.append("labelIntervalHour").append(" ");
                    stringMainHolder.append("labelIntervalMinute").append(" ");
                    stringMainHolder.append("labelIntervalSecond");

                    stringSecondHolder.append("1A").append(" ");
                    stringSecondHolder.append("1B").append(" ");
                    stringSecondHolder.append("1C").append(" ");
                    stringSecondHolder.append("1D").append(" ");
                    stringSecondHolder.append("1E");

                    //labelInterval.setText("Next Class : " + diffDays + " hari, " + diffHours + " jam, " + diffMinutes + " menit, " + diffSeconds + " detik.");
                    labelInterval.setText("1A : " + diffDays + " 1B, "
                            + diffHours + " 1C, "
                            + diffMinutes + " 1D, "
                            + diffSeconds + " 1E.");

                } else if (diffHours > 0) {
                    stringMainHolder.append("labelIntervalNextClass").append(" ");
                    stringMainHolder.append("labelIntervalToday").append(" ");
                    stringMainHolder.append("labelIntervalHour").append(" ");
                    stringMainHolder.append("labelIntervalMinute").append(" ");
                    stringMainHolder.append("labelIntervalSecond");

                    stringSecondHolder.append("1A").append(" ");
                    stringSecondHolder.append("1B").append(" ");
                    stringSecondHolder.append("1C").append(" ");
                    stringSecondHolder.append("1D").append(" ");
                    stringSecondHolder.append("1E");

                    labelInterval.setText("1A : 1B, "
                            + diffHours + " 1C, "
                            + diffMinutes + " 1D, "
                            + diffSeconds + " 1E.");
                } else if (diffHours == 0) {
                    stringMainHolder.append("labelIntervalNextClass").append(" ");
                    stringMainHolder.append("labelIntervalToday").append(" ");
                    stringMainHolder.append("labelIntervalMinute").append(" ");
                    stringMainHolder.append("labelIntervalSecond");

                    stringSecondHolder.append("1A").append(" ");
                    stringSecondHolder.append("1B").append(" ");
                    stringSecondHolder.append("1C").append(" ");
                    stringSecondHolder.append("1D");

                    labelInterval.setText("1A : 1B, "
                            + diffMinutes + " 1C, "
                            + diffSeconds + " 1D.");

                } else if (diffDays == 0 && diffHours < 0) {
                    // here when the class is already passed
                    stringMainHolder.append("labelIntervalPassed");

                    stringSecondHolder.append("1A");

                    labelInterval.setText("1A");
                }

                String dataOrdered[] = stringMainHolder.toString().split(" ");
                String dataLetter[] = stringSecondHolder.toString().split(" ");

                //clearing string buffer
                stringMainHolder.delete(0, stringMainHolder.length());
                stringSecondHolder.delete(0, stringSecondHolder.length());

                //System.out.println("data ui interval is " + labelInterval.getText());
                if (langHelper != null) {
                    langHelper.apply(labelInterval, dataOrdered, dataLetter);
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
        } else if (diffDays < 0) {

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

    public interface PopupAction {

        void actionYes();

        void actionNo();
    }

    private static PopupAction listener;

    public static void setPopupListener(PopupAction p) {
        listener = p;
    }

    public static void popupConfirm(String[] dataCombobox, String message, String title, JFrame ref) {

        BorderLayout layout = new BorderLayout();
        JPanel topPanel = new JPanel(layout);
        JLabel label = new JLabel(message);
        topPanel.add(label, BorderLayout.NORTH);
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        JComboBox cb = new JComboBox();
        cb.setModel(new DefaultComboBoxModel(dataCombobox));
        cb.setSelectedIndex(-1);
        centerPanel.add(cb, BorderLayout.CENTER);
        topPanel.add(centerPanel);

        int reply = JOptionPane.showConfirmDialog(ref, topPanel, title, JOptionPane.OK_CANCEL_OPTION);
        if (reply == JOptionPane.OK_OPTION) {
            selectedItem = cb.getSelectedItem().toString().toLowerCase();
            listener.actionYes();
        } else {
            selectedItem = null;
            listener.actionNo();
        }
    }

    public static void popupConfirm(String message, JFrame ref) {

        int reply = JOptionPane.showConfirmDialog(ref, message, "confirm", JOptionPane.YES_NO_OPTION);
        if (reply == JOptionPane.YES_OPTION) {
            listener.actionYes();
        } else {
            listener.actionNo();
        }

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
