/*
 *  This is a Supplemental File from the Main Project used
 *  in Java Programming Core Fundamental I
 *  with FGroupIndonesia team.
 */
package helper;

import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 *
 * @author ASUS
 */
public class Downloader {
    
    public void start(String url){
        
        ByteArrayOutputStream responseBodyBaos = null;
        Scanner httpResponseBodyScanner = null;
        try {
            // Define server endpoint
            URL robotsUrl = new URL("http://www.techcoil.com/robots.txt");
            HttpURLConnection urlConnection = (HttpURLConnection) robotsUrl.openConnection(); 
  
            httpResponseBodyScanner = new Scanner(urlConnection.getInputStream());
  
            // Use a ByteArrayOutputStream to store the contents of the HTTP response body
            responseBodyBaos = new ByteArrayOutputStream();
            while(httpResponseBodyScanner.hasNextLine()) {
                responseBodyBaos.write(httpResponseBodyScanner.nextLine().getBytes());
            }
            responseBodyBaos.close();
            httpResponseBodyScanner.close();
  
            // Verify contents of robots.txt
            String robotsContent = responseBodyBaos.toString();
            if (robotsContent.trim().equals("Sitemap: http://www.techcoil.com/sitemap-index.xml")) {
                System.out.println("Able to retrieve robots.txt from server. Server is running fine.");
            }
            else {
                System.out.println("Not able to retrive robots.txt from server.");
            }
  
        } catch(Exception ioException) {
            System.out.println("IOException occurred while contacting server.");
        } finally {
            if (responseBodyBaos != null) {
                try {
                responseBodyBaos.close();
                } catch (Exception ioe) {
                    System.out.println("Error while closing response body stream");
                }
            }
            if (httpResponseBodyScanner != null) {
                httpResponseBodyScanner.close();
            }
        }
        
    }
    
}
