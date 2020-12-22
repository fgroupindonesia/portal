/*
 *  This is a Portal Access for Client & Admin Usage
 *  (c) FGroupIndonesia, 2020.
 */
package beans;

import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 *
 * @author ASUS
 */
public class Attendance {
    private int id;
    private String username;
    private String class_registered;
    private String status;
    private String signature;
    private String date_created;
    private String date_modified;

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the class_registered
     */
    public String getClass_registered() {
        return class_registered;
    }

    /**
     * @param class_registered the class_registered to set
     */
    public void setClass_registered(String class_registered) {
        this.class_registered = class_registered;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the signature
     */
    public String getSignature() {
        return signature;
    }

    /**
     * @param signature the signature to set
     */
    public void setSignature(String signature) {
        this.signature = signature;
    }

    /**
     * @return the date_created
     */
    public String getDate_created() {
        return date_created;
    }

    /**
     * @param date_created the date_created to set
     */
    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    /**
     * @return the date_modified
     */
    public String getDate_modified() {
        return date_modified;
    }

    /**
     * @param date_modified the date_modified to set
     */
    public void setDate_modified(String date_modified) {
        this.date_modified = date_modified;
    }
}
