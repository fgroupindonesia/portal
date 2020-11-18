/*
 *  This is a Supplemental File from the Main Project used
 *  in Java Programming Core Fundamental II
 *  with FGroupIndonesia team.
 */
package helper;

import java.io.File;

/**
 *
 * @author ASUS
 */
public class FormFile {

    public FormFile(String k, File obj) {
        key = k;
        fileObject = obj;
    }

    private String key;
    private File fileObject;

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
     * @return the fileObject
     */
    public File getFileObject() {
        return fileObject;
    }

    /**
     * @param fileObject the fileObject to set
     */
    public void setFileObject(File fileObject) {
        this.fileObject = fileObject;
    }

}
