/*
 *  This is a Supplemental File from the Main Project used
 *  in Java Programming Core Fundamental I
 *  with FGroupIndonesia team.
 */
package helper;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author ASUS
 */
public class HttpCall {

    public HttpCall(HttpProcess hpro) {
        listener = hpro;
    }

    /**
     * @return the mainData
     */
    public String getMainData() {

        StringBuilder postData = new StringBuilder();

        for (FormData object : mainData) {

            if (postData.length() != 0) {
                postData.append("&");
            }

            postData.append(object.getKey());
            postData.append("=");
            postData.append(object.getValue());

        }

        return postData.toString();

    }

    public boolean isFileAttached() {
        return (fileData.size() > 0);
    }

    /**
     * @param mainData the mainData to set
     */
    public void setMainData(ArrayList<FormData> mainData) {
        this.mainData = mainData;
    }

    private ArrayList<FormData> mainData = new ArrayList<FormData>();
    private ArrayList<FormFile> fileData = new ArrayList<FormFile>();
    public static final int METHOD_POST = 1;
    public static final int METHOD_GET = 2;
    public static final int METHOD_POST_FILE = 3;
    private static final String BOUNDARY = "*****";
    private static final String CRLF = "\r\n";
    private static final String TWO_HYPENS = "--";

    private final String USER_AGENT = "Mozilla/5.0";

    private String endResult, errorMessage;
    private boolean writeDisk = false;

    public void writeToDisk(boolean b) {
        writeDisk = b;
    }

    public boolean isWriteToDisk() {
        return writeDisk;
    }

    public void addFile(String key, File objFile) {
        FormFile fileEntry = new FormFile(key, objFile);
        fileData.add(fileEntry);
    }

    public void addData(String aKey, String aVal) {
        try {
            FormData entry = new FormData(aKey, aVal);
            mainData.add(entry);
        } catch (Exception error) {

        }
    }

    private HttpProcess listener;

    public void setProcess(HttpProcess el) {
        listener = el;
    }

    public String getData(String key) {
        String val = null;
        for (FormData f : mainData) {
            if (f.getKey().equalsIgnoreCase(key)) {
                val = f.getValue();
                break;
            }
        }

        return val;

    }

    public interface HttpProcess {

        void checkResponse(String resp, String urlTarget);
    }

    public boolean isInternetAlive() {
        boolean stat = false;

        try {
            URL url = new URL(WebReference.REMOTE);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            conn.getInputStream().close();

            // set the url target as NULL
            // manually defining success json
            endResult = "{\"status\":\"valid\"}";

            stat = true;
        } catch (Exception e) {
            endResult = "{\"status\":\"invalid\"}";
            stat = false;
        }

        setEndResult(endResult);
        listener.checkResponse(endResult, null);
        return stat;
    }

    public void start(String urlTarget, int modeCall) {

        try {

            byte[] postDataBytes = this.getMainData().getBytes("UTF-8");

            URL url = new URL(urlTarget);
            BufferedReader in = null;

            HttpURLConnection conn = null;

            if (!isWriteToDisk()) {

                conn = (HttpURLConnection) url.openConnection();

                if (modeCall == METHOD_POST) {
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                    conn.setDoOutput(true);
                    conn.getOutputStream().write(postDataBytes);

                } else if (modeCall == METHOD_POST_FILE) {

                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("Cache-Control", "no-cache");
                    conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
                    conn.addRequestProperty("Accept", "*/*");
                    conn.setDoOutput(true);

                    DataOutputStream request = new DataOutputStream(conn.getOutputStream());

                    // does this has a form field ?
                    if (mainData.size() > 0) {

                        for (FormData fd : mainData) {

                            request.writeBytes(TWO_HYPENS + BOUNDARY + CRLF);
                            request.writeBytes("Content-Disposition: form-data; name=\"" + fd.getKey() + "\"" + CRLF);
                            request.writeBytes("Content-Type: text/plain; charset=UTF-8" + CRLF);
                            request.writeBytes(CRLF);
                            request.writeBytes(fd.getValue() + CRLF);
                            request.flush();

                        }

                    }

                    // does this has file attachment?
                    if (fileData.size() > 0) {

                        for (FormFile ff : fileData) {
                            String fileName = ff.getFileObject().getName();
                            request.writeBytes(TWO_HYPENS + BOUNDARY + CRLF);
                            request.writeBytes("Content-Disposition: form-data; name=\"" + ff.getKey() + "\"; filename=\"" + fileName + "\"" + CRLF);
                            request.writeBytes("Content-Type: " + conn.guessContentTypeFromName(fileName) + CRLF);
                            request.writeBytes("Content-Transfer-Encoding: binary" + CRLF);
                            request.writeBytes(CRLF);
                            request.flush();

                            FileInputStream inputStream = new FileInputStream(ff.getFileObject());
                            byte[] buffer = new byte[4096];
                            int bytesRead = -1;
                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                request.write(buffer, 0, bytesRead);
                            }

                            request.flush();

                        }

                    }

                    // closing this POST + FORM + FILE request
                    request.writeBytes(CRLF);
                    request.writeBytes(TWO_HYPENS + BOUNDARY + TWO_HYPENS);

                    request.flush();
                    //request.close();

                } else if (modeCall == METHOD_GET) {
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("User-Agent", USER_AGENT);
                    int responseCode = conn.getResponseCode();

                }

            }
            // is this want to be writable?
            if (isWriteToDisk()) {
                System.out.println("We got propic " + getData("propic"));

                // set the complete path locally
                String fileName = null;
                if (urlTarget.contains(WebReference.SIGNATURE_ATTENDANCE)) {
                    PathReference.setSignatureFileName(getData("signature"));
                    fileName = PathReference.SignaturePath;
                } else if (urlTarget.contains(WebReference.PICTURE_USER)) {
                    PathReference.setPropicFileName(getData("propic"));
                    fileName = PathReference.UserPropicPath;
                }else if (urlTarget.contains(WebReference.SCREENSHOT_PAYMENT)) {
                    PathReference.setScreenshotPaymentFileName(getData("screenshot"));
                    fileName = PathReference.ScreenshotPaymentPath;
                }else if (urlTarget.contains(WebReference.DOWNLOAD_TOOLS)) {
                    fileName = PathReference.TeamviewerPath;
                }else {
                    // this is for downloading manual
                    PathReference.setDocumentFileName(getData("filename"));
                    fileName = PathReference.DocumentFilePath;
                }

                // download here
                FileCopier.downloadFromURL(url, fileName);

                // manually defining success json
                endResult = "{\"status\":\"valid\"}";

            } else {
                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String line;
                StringBuilder response = new StringBuilder();

                while ((line = in.readLine()) != null) {
                    response.append(line);
                }

                setEndResult(response.toString());
                in.close();
            }

            if (conn != null) {
                conn.disconnect();
            }

        } catch (Exception e) {
            setEndResult(urlTarget + " error " + e.getMessage());

            Writer writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            setErrorMessage(writer.toString());

            e.printStackTrace();
        }

        if (endResult != null && errorMessage == null) {
            listener.checkResponse(endResult, urlTarget);
        }

    }

    public void start(String url) {

        ByteArrayOutputStream responseBodyBaos = null;
        Scanner httpResponseBodyScanner = null;
        try {
            // Define server endpoint
            URL robotsUrl = new URL("http://www.techcoil.com/robots.txt");
            HttpURLConnection urlConnection = (HttpURLConnection) robotsUrl.openConnection();

            httpResponseBodyScanner = new Scanner(urlConnection.getInputStream());

            // Use a ByteArrayOutputStream to store the contents of the HTTP response body
            responseBodyBaos = new ByteArrayOutputStream();
            while (httpResponseBodyScanner.hasNextLine()) {
                responseBodyBaos.write(httpResponseBodyScanner.nextLine().getBytes());
            }
            responseBodyBaos.close();
            httpResponseBodyScanner.close();

            // Verify contents of robots.txt
            String robotsContent = responseBodyBaos.toString();
            if (robotsContent.trim().equals("Sitemap: http://www.techcoil.com/sitemap-index.xml")) {
                System.out.println("Able to retrieve robots.txt from server. Server is running fine.");
            } else {
                System.out.println("Not able to retrive robots.txt from server.");
            }

        } catch (Exception ioException) {
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

    /**
     * @return the endResult
     */
    public String getEndResult() {
        return endResult;
    }

    /**
     * @param endResult the endResult to set
     */
    public void setEndResult(String endResult) {
        this.endResult = endResult;
    }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @param errorMessage the errorMessage to set
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
