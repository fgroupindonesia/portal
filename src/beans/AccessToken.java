/*
 *  This is a Portal Access for Client & Admin Usage
 *  (c) FGroupIndonesia, 2020.
 */
package beans;

/**
 *
 * @author ASUS
 */
public class AccessToken {
    private String username;
    private String token;
    private String expired_date;

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
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * @param token the token to set
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * @return the expired_date
     */
    public String getExpired_date() {
        return expired_date;
    }

    /**
     * @param expired_date the expired_date to set
     */
    public void setExpired_date(String expired_date) {
        this.expired_date = expired_date;
    }
    
}
