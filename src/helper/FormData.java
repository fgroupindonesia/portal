/*
 *  This is a Supplemental File from the Main Project used
 *  in Java Programming Core Fundamental I
 *  with FGroupIndonesia team.
 */
package helper;

import java.net.URLEncoder;

/**
 *
 * @author ASUS
 */
public class FormData {

    private String key;
    private String value;

    public FormData(String k, String v) throws Exception {
        this.setKey(URLEncoder.encode(k, "UTF-8"));
        this.setValue(URLEncoder.encode(v, "UTF-8"));

    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

}
