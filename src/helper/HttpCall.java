/*
 *  This is a Portal Access for Client & Admin Usage
 *  (c) FGroupIndonesia, 2020.
 */
package helper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 *
 * @author ASUS
 */
public class HttpCall {

    FileCopier fcopier;

    public void setFileCopier(FileCopier fcp) {
        fcopier = fcp;
    }

    public HttpCall(HttpProcess hpro, int md) {
        listener = hpro;
        this.modeCall = md;
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

    public void setModeCall(int modeNa){
        modeCall = modeNa;
    }
    
    private int modeCall; // either GET or POST
    private ArrayList<FormData> mainData = new ArrayList<FormData>();
    private ArrayList<FormFile> fileData = new ArrayList<FormFile>();
    public static final int METHOD_POST = 1;
    public static final int METHOD_GET = 2;
    public static final int METHOD_POST_FILE = 3;
    // not GET nor POST request
    public static final int METHOD_EMPTY = -1;
    
    private static final String BOUNDARY = "******";
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
            FormData entry = new FormData(aKey, aVal, modeCall);
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

    public String getMimeType(String filename) {

        String result = null;
        filename = filename.toLowerCase();

        if (filename.contains("docx")) {
            result = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

        } else if (filename.contains("doc")) {
            result = "application/msword";

        } else if (filename.contains("xlsx")) {
            result = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

        } else if (filename.contains("xls")) {
            result = "application/vnd.ms-excel";

        } else if (filename.contains("pptx")) {
            result = "application/vnd.openxmlformats-officedocument.presentationml.presentation";

        } else if (filename.contains("ppt")) {
            result = "application/vnd.ms-powerpoint";

        } else if (filename.contains("jpg") || filename.contains("jpeg")) {
            result = "image/jpeg";

        } else if (filename.contains("png")) {
            result = "image/png";

        } else if (filename.contains("txt")) {
            result = "text/plain";

        } else if (filename.contains("pdf")) {
            result = "application/pdf";

        }

        return result;

    }

    public void start(String urlTarget) {

        try {

            byte[] postDataBytes = this.getMainData().getBytes("UTF-8");

            URL url = new URL(urlTarget);
            BufferedReader in = null;

            HttpURLConnection conn = null;

            if (!isWriteToDisk()) {

                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);

                if ( this.modeCall == METHOD_POST) {
                    
                    
                    System.out.println("\nCalling POST METHOD.... \n"); 

                    
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                    conn.setRequestProperty("Accept-Charset", "en-us");
                    conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                    conn.setRequestProperty("charset", "EN-US");
                    conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    conn.connect();

                    DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                    wr.write(postDataBytes);
                    wr.flush();
                    wr.close();

                    int responseCode = conn.getResponseCode();
                    System.out.println( urlTarget + "\n---------------- HTTP Call response code is " + responseCode);
                    //conn.getOutputStream().write(postDataBytes);

                } else if (this.modeCall == METHOD_POST_FILE) {
                    
                    System.out.println("\nCalling POST_FILE METHOD.... \n"); 

                    int bytesRead, bytesAvailable, bufferSize;
                    byte[] buffer;
                    int maxBufferSize = 1 * 1024 * 1024;

                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setUseCaches(false);
                    conn.setChunkedStreamingMode(1024);

                    OutputStream outputStream = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(outputStream, "UTF-8"));

                    // does this has a form field ?
                    if (mainData.size() > 0) {

                        for (FormData fd : mainData) {

                            // write each key-values
                            writer.append("--" + BOUNDARY).append(CRLF);
                            writer.append("Content-Disposition: form-data; name=\"" + fd.getKey() + "\"")
                                    .append(CRLF);
                            writer.append("Content-Type: text/plain; charset=en-us").append(
                                    CRLF);
                            writer.append(CRLF);
                            writer.append(UIEffect.encode(fd.getValue())).append(CRLF);
                            writer.flush();

                        }

                    }

                    // does this has file attachment?
                    if (fileData.size() > 0) {

                        for (FormFile ff : fileData) {

                            String fileName = ff.getFileObject().getName();
                            writer.append("--" + BOUNDARY).append(CRLF);
                            writer.append(
                                    "Content-Disposition: post-data; name=\"" + ff.getKey()
                                    + "\"; filename=\"" + fileName + "\"")
                                    .append(CRLF);
                            writer.append(
                                    "Content-Type: "
                                    + this.getMimeType(fileName))
                                    .append(CRLF);
                            writer.append("Content-Transfer-Encoding: binary").append(CRLF);
                            writer.append(CRLF).flush();

                            FileInputStream inputStream = new FileInputStream(ff.getFileObject());

                            bytesAvailable = inputStream.available();
                            maxBufferSize = 1000;
                            // int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                            buffer = new byte[bytesAvailable];

                            /*while ((bytesRead = inputStream.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, bytesRead);
                            }*/
                            bytesRead = inputStream.read(buffer, 0, bytesAvailable);
                            while (bytesRead > 0) {
                                outputStream.write(buffer, 0, bytesAvailable);
                                bytesAvailable = inputStream.available();
                                bytesAvailable = Math.min(bytesAvailable, maxBufferSize);
                                bytesRead = inputStream.read(buffer, 0, bytesAvailable);
                            }

                            outputStream.flush();
                            inputStream.close();

                            // this CRLF dont be added coz it will make corruption writer.append(CRLF).flush();
                            writer.flush();
                        }

                    }

                    // closing this POST + FORM + FILE request
                    writer.append(CRLF).flush();
                    writer.append("--" + BOUNDARY + "--").append(CRLF);
                    writer.flush();
                    writer.close();
                    outputStream.close();

                    int responseCode = conn.getResponseCode();
                    System.out.println(urlTarget +"\n---------------- HTTP Call response code is " + responseCode);

                } else if (this.modeCall == METHOD_GET) {
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("User-Agent", USER_AGENT);
                    int responseCode = conn.getResponseCode();
                    System.out.println("\nCalling GET METHOD.... \n");    
                }

            }

            if (isWriteToDisk()) {
               
                System.out.println("----- We got file to be downloaded! -----");

                // set the complete path locally
                String fileName = null;
                if (urlTarget.contains(WebReference.SIGNATURE_ATTENDANCE)) {
                    PathReference.setSignatureFileName(getData("signature"));
                    fileName = PathReference.SignaturePath;
                } else if (urlTarget.contains(WebReference.PREVIEW_EXAM)) {
                    PathReference.setExamQuestionPreviewFileName(getData("preview"));
                    fileName = PathReference.ExamQuestionPreviewPath;
                } else if (urlTarget.contains(WebReference.PICTURE_USER)) {
                    PathReference.setPropicFileName(getData("propic"));
                    fileName = PathReference.UserPropicPath;
                } else if (urlTarget.contains(WebReference.SCREENSHOT_PAYMENT)) {
                    PathReference.setScreenshotPaymentFileName(getData("screenshot"));
                    fileName = PathReference.ScreenshotPaymentPath;
                } else if (urlTarget.contains(WebReference.DOWNLOAD_TOOLS)) {
                    fileName = PathReference.TeamviewerPath;
                } else if (urlTarget.contains(WebReference.SCREENSHOT_REPORT_BUGS)) {
                    fileName = PathReference.getScreenshotBugsReportedPath(getData("screenshot"));
                } else if (urlTarget.contains(WebReference.PICTURE_CERTIFICATE_STUDENT)) {
                    fileName = PathReference.getCertificatePath(getData("filename"));
                } else {
                    // this is for downloading manual
                    PathReference.setDocumentFileName(getData("filename"));
                    fileName = PathReference.DocumentFilePath;
                }

                // download here
                System.out.println("Start downloading... " + fileName + "\nfrom " + urlTarget);
                fcopier.downloadFromURL(url, fileName);

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

   /* public void start(String url) {

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

    } */

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
