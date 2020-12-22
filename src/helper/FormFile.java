/*
 *  This is a Portal Access for Client & Admin Usage
 *  (c) FGroupIndonesia, 2020.
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
