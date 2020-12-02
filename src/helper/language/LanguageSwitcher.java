/*
 *  This is a Supplemental File from the Main Project used
 *  in Java Programming Core Fundamental II
 *  with FGroupIndonesia team.
 */
package helper.language;

import beans.LanguageComponent;
import helper.PathReference;
import helper.preferences.Keys;
import helper.preferences.SettingPreference;
import java.util.ArrayList;
import javax.swing.JLabel;
import java.io.*;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 *
 * @author ASUS
 */
public class LanguageSwitcher {

    ArrayList<LanguageComponent> dataDictionary = new ArrayList<LanguageComponent>();

    public LanguageSwitcher() {

    }

    public LanguageSwitcher(SettingPreference config) {

        String lang = config.getStringValue(Keys.SYSTEM_LANGUAGE);

        // UIEffect.popup(lang, this);
        // this will load the dictionary based upon the language
        if (lang.toLowerCase().contains("english")) {
            englishSource();
        } else if (lang.toLowerCase().contains("arab")) {
            arabicSource();
        } else {
            // indo
            bahasaSource();
        }

    }

    public void englishSource() {
        readLanguage("lang_en.inf");
    }

    public void bahasaSource() {
        readLanguage("lang_id.inf");
    }

    public void arabicSource() {
        readLanguage("lang_ar.inf");
    }

    private void readLanguage(String fPath) {

        dataDictionary.clear();

        String dest = PathReference.getLanguagePath(fPath);

        try {
            File file = new File(dest);
            BufferedReader br = new BufferedReader(new FileReader(file));

            String line;
            String data[];
            while ((line = br.readLine()) != null) {
                data = line.split("=");
                LanguageComponent lg = new LanguageComponent();

                lg.setKey(data[0]);
                lg.setText(data[1].replace("\"", ""));

                dataDictionary.add(lg);
            }

            br.close();

        } catch (Exception e) {

        }
    }

    public void apply(JLabel el, String[] keys, String[] letter) {
        String earlierText = el.getText();
        StringBuffer stb = new StringBuffer();

        for (LanguageComponent lg : dataDictionary) {
            for (int n = 0; n < keys.length; n++) {
                if (lg.getKey().equalsIgnoreCase(keys[n])) {
                    stb.append(lg.getText()).append(";");
                    break;
                }
            }
        }

        //System.out.println("Dapat kata " + stb.toString());
        String newWord [] = stb.toString().split(";");
        //System.out.println("jumlah kata " + newWord.length);
        //System.out.println("jumlah huruf " + letter.length);
      
        for(int n=0; n<letter.length; n++){
            earlierText = earlierText.replace(letter[n], newWord[n]);
        }
        
        el.setText(earlierText);
        
    }

    public void apply(JComponent el, String key, int comp) {
        for (LanguageComponent lg : dataDictionary) {
            if (lg.getKey().equalsIgnoreCase(key)) {

                switch (comp) {
                    case Comp.TEXTFIELD:
                        ((JTextField) el).setText(lg.getText());
                        break;
                    case Comp.LABEL:
                        ((JLabel) el).setText(lg.getText());
                        break;
                    case Comp.BUTTON:
                        ((JButton) el).setText(lg.getText());
                        break;
                    case Comp.RADIO_BUTTON:
                        ((JRadioButton) el).setText(lg.getText());
                        break;
                    case Comp.CHECKBOX:
                        ((JCheckBox) el).setText(lg.getText());
                        break;
                    default:
                        break;
                }

                break;
            }
        }
    }

}
