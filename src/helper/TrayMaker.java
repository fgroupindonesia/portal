/*
 *  This is a Supplemental File from the Main Project used
 *  in Java Programming Core Fundamental II
 *  with FGroupIndonesia team.
 */
package helper;

import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;

/**
 *
 * @author ASUS
 */
public class TrayMaker {

    JFrame frameRef;
    SystemTray tray = SystemTray.getSystemTray();
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    TrayIcon icon;

    public void setFrameRef(JFrame f) {
        frameRef = f;
    }

    public boolean isSupported() {
        return SystemTray.isSupported();
    }

    public void destroy() {
        tray.remove(icon);
    }

    public void createTray() throws Exception {

        Image image = toolkit.getImage(PathReference.LogoPath);

        PopupMenu menu = new PopupMenu();

        MenuItem messageItem = new MenuItem("Show Portal Access");
        messageItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frameRef.setVisible(true);
                destroy();
            }
        });
        menu.add(messageItem);

        MenuItem closeItem = new MenuItem("Exit");
        closeItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        menu.add(closeItem);
        icon = new TrayIcon(image, "Portal Access - FGroupIndonesia", menu);
        icon.setImageAutoSize(true);

        tray.add(icon);

    }

}
