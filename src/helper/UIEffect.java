/*
 *  This is a Supplemental File from the Main Project used
 *  in Java Programming Core Fundamental I
 *  with FGroupIndonesia team.
 */
package helper;

import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 *
 * @author ASUS
 */
public class UIEffect {
    
    public static void focusGained(JTextField el){
        el.setBackground(Color.YELLOW);
    }
    
    public static void focusLost(JTextField el){
        el.setBackground(null);
    }
    
}
