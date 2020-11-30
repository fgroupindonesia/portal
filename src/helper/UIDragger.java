/*
 *  This is a Supplemental File from the Main Project used
 *  in Java Programming Core Fundamental I
 *  with FGroupIndonesia team.
 */
package helper;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;

/**
 *
 * @author ASUS
 */
public class UIDragger {

    private static JFrame frameRef;
    private static Point initialClick;

    public static void setFrame(JFrame refIn) {
        frameRef = refIn;
    }

    public static void mouseReleased(MouseEvent evt) {
        frameRef.setCursor(null);
    }
    
    public static void mouseDragged(MouseEvent evt) {
        int thisX = frameRef.getLocation().x;
        int thisY = frameRef.getLocation().y;

        // Determine how much the mouse moved since the initial click
        int xMoved = evt.getX() - initialClick.x;
        int yMoved = evt.getY() - initialClick.y;

        // Move window to this position
        int X = thisX + xMoved;
        int Y = thisY + yMoved;
        frameRef.setLocation(X, Y);
        
        frameRef.setCursor(Cursor.MOVE_CURSOR);
    }

    public static void mousePressed(MouseEvent evt) {
        initialClick = evt.getPoint();
        frameRef.getComponentAt(initialClick);
    }
}
