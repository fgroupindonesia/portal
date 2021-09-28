/*
 *  This is a Portal Access for Client & Admin Usage
 *  (c) FGroupIndonesia, 2020.
 */
package beans;

import javax.swing.JTextField;

/**
 *
 * @author ASUS
 */
public class User {
    
    private int id;
    private int access_level;
    private String username;
    private String pass;
    private String email;
    private String address;
    private String propic;
    private String mobile;
    private String date_created;
    private String tmv_id;
    private String tmv_pass;

    public User(){
    
    }
    
    public User(JTextField usernameEl, JTextField passEl){
        username = usernameEl.getText();
        pass = passEl.getText();
    }
    
    public User(String usernameEl, String passEl){
        username = usernameEl;
        pass = passEl;
    }
    
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
     * @return the pass
     */
    public String getPass() {
        return pass;
    }

    /**
     * @param pass the pass to set
     */
    public void setPass(String pass) {
        this.pass = pass;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return the propic
     */
    public String getPropic() {
        return propic;
    }

    /**
     * @param propic the propic to set
     */
    public void setPropic(String propic) {
        this.propic = propic;
    }

    /**
     * @return the mobile
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * @param mobile the mobile to set
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
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
     * @return the tmv_pass
     */
    public String getTmv_pass() {
        return tmv_pass;
    }

    /**
     * @param tmv_pass the tmv_pass to set
     */
    public void setTmv_pass(String tmv_pass) {
        this.tmv_pass = tmv_pass;
    }

    /**
     * @return the tmv_id
     */
    public String getTmv_id() {
        return tmv_id;
    }

    /**
     * @param tmv_id the tmv_id to set
     */
    public void setTmv_id(String tmv_id) {
        this.tmv_id = tmv_id;
    }

    /**
     * @return the access_level
     */
    public int getAccess_level() {
        return access_level;
    }

    /**
     * @param access_level the access_level to set
     */
    public void setAccess_level(int access_level) {
        this.access_level = access_level;
    }
    
}
