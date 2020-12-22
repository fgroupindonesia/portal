/*
 *  This is a Portal Access for Client & Admin Usage
 *  (c) FGroupIndonesia, 2020.
 */
package beans;

/**
 *
 * @author ASUS
 */
public class Tool {

    private int id;
    private String app_name;
    private String app_ver;

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
     * @return the app_name
     */
    public String getApp_name() {
        return app_name;
    }

    /**
     * @param app_name the app_name to set
     */
    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }

    /**
     * @return the app_ver
     */
    public String getApp_ver() {
        return app_ver;
    }

    /**
     * @param app_ver the app_ver to set
     */
    public void setApp_ver(String app_ver) {
        this.app_ver = app_ver;
    }

}
