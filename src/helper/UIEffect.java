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
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author ASUS
 */
public class UIEffect {

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
            //System.out.println("what ");
            PathReference.setPropicFileName("default.png");
            String newPath = PathReference.UserPropicPath;

            try {
                img = ImageIO.read(new File(newPath));
            } catch (Exception ex) {

            }
        }

        // set the picture fitting 
        Image dimg = img.getScaledInstance(el.getWidth(), el.getHeight(),
                Image.SCALE_SMOOTH);

        el.setIcon(new ImageIcon(img));
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
