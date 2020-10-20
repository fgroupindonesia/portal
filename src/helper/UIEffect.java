/*
 *  This is a Supplemental File from the Main Project used
 *  in Java Programming Core Fundamental I
 *  with FGroupIndonesia team.
 */
package helper;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 *
 * @author ASUS
 */
public class UIEffect {

    public static void iconChanger(JFrame frame) {
        ImageIcon img = new ImageIcon(PathReference.LogoPath);
        frame.setIconImage(img.getImage());
    }

    public static void iconChanger(JLabel el, String aPath) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(aPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // set the picture fitting 
        Image dimg = img.getScaledInstance(el.getWidth(), el.getHeight(),
                Image.SCALE_SMOOTH);
        
        el.setIcon(new ImageIcon(dimg));
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
