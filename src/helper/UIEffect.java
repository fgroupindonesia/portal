/*
 *  This is a Supplemental File from the Main Project used
 *  in Java Programming Core Fundamental I
 *  with FGroupIndonesia team.
 */
package helper;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.Timer;

/**
 *
 * @author ASUS
 */
public class UIEffect {

    protected static final DateFormat CLOCK_FORMAT = new SimpleDateFormat("dd/MMM/yyyy hh:mm:ss a");
    private static JLabel labelTime;
    private static Timer timer;

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
        
        System.out.println("Stopping timer is done!");
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

    public static void popup(String message, JFrame ref) {

        JOptionPane.showMessageDialog(ref, message);

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
    }

    public static void mouseHover(JLabel el){
        el.setForeground(Color.BLUE);
        
    }
    
    public static void mouseExit(JLabel el){
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
