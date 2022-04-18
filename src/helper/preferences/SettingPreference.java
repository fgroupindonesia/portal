/*
 *  This is a Portal Access for Client & Admin Usage
 *  (c) FGroupIndonesia, 2020.
 */

package helper.preferences;

import java.util.prefs.Preferences;

/**
 *
 * @author ASUS
 */
public class SettingPreference {

    Preferences prefs ;
    boolean nodeExist;

    public void commit(){
        try{
        prefs.flush();
    
        } catch (Exception ex){
            System.err.println("Error while saving SettingPreference");
        }
    }
    
    public SettingPreference() {
        // checking existance
        try {
             prefs = Preferences.userNodeForPackage(this.getClass());
          
              //nodeExist = Preferences.userRoot().nodeExists(this.getClass().getName());
           
              // check either one of the settings is already defined
              if(this.getStringValue(Keys.USER_PROPIC).length()==0){
                  nodeExist = false;
              }else {
                  nodeExist = true;
              }
              
        } catch (Exception e) {

        }

        System.out.println("Node SettingPreference existance status is " + nodeExist);

        if (!nodeExist) {

            // this is the default
            this.setValue(Keys.TEAMVIEWER_VERSION, "15.9.4.0");
            this.setValue(Keys.AUTO_UPDATE_TOOLS, false);
            this.setValue(Keys.SYSTEM_LANGUAGE, "English (default)".toLowerCase());
            this.setValue(Keys.NOTIF_CLASS_START, "1 Hour before".toLowerCase());
            this.setValue(Keys.NOTIF_SESSION_LIMIT, "At least 1".toLowerCase());
            this.setValue(Keys.USER_PROPIC, "default");
            this.setValue(Keys.EXAM_QUESTION_PREVIEW, "exam-prev-default");
            this.setValue(Keys.TOTAL_EXAM_COMPLETED, "0");
            this.setValue(Keys.LAST_EXAM_COMPLETED_DATE, "none");

        }
        
        this.commit();

    }

    public void setValue(String key, String newValue) {
        prefs.put(key, newValue);
        
        this.commit();
    }

    public void setValue(String key, boolean newValue) {
        prefs.putBoolean(key, newValue);
        
        this.commit();
    }

    public String getStringValue(String key) {
        return prefs.get(key, "");
    }

    public boolean getBooleanValue(String key) {
        return prefs.getBoolean(key, false);
    }
}
