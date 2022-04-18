/*
 *  This is a Portal Access for Client & Admin Usage
 *  (c) FGroupIndonesia, 2020.
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

    
    
    public FormData(String k, String v, int mode) throws Exception {
        
        if(mode == HttpCall.METHOD_GET){
        this.setKey(URLEncoder.encode(k, "UTF-8"));
        this.setValue(URLEncoder.encode(v, "UTF-8"));
        } else {
        this.setKey(k);
        this.setValue(v);
            
        }
        
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
