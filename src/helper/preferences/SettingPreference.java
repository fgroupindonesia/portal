/*
 *  This is a Supplemental File from the Main Project used
 *  in Java Programming Core Fundamental I
 *  with FGroupIndonesia team.
 */
package helper.preferences;

import java.util.prefs.Preferences;

/**
 *
 * @author ASUS
 */
public class SettingPreference {

    Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
    boolean nodeExist;

    public SettingPreference() {
        // checking existance
        try {
            nodeExist = Preferences.userRoot().nodeExists(this.getClass().getName());
        } catch (Exception e) {

        }

        //System.out.println("Node exist " + nodeExist);

        if (!nodeExist) {

            this.setValue(Keys.AUTO_UPDATE_TOOLS, false);
            this.setValue(Keys.SYSTEM_LANGUAGE, "English (default)");
            this.setValue(Keys.NOTIF_CLASS_START, "1 Hour before");
            this.setValue(Keys.NOTIF_SESSION_LIMIT, "At least 1");
            this.setValue(Keys.USER_PROPIC, "default");

        }

    }

    public void setValue(String key, String newValue) {
        prefs.put(key, newValue);
    }

    public void setValue(String key, boolean newValue) {
        prefs.putBoolean(key, newValue);
    }

    public String getStringValue(String key) {
        return prefs.get(key, "");
    }

    public boolean getBooleanValue(String key) {
        return prefs.getBoolean(key, false);
    }
}
