/*
 *  This is a Portal Access for Client & Admin Usage
 *  (c) FGroupIndonesia, 2020.
 */

package helper;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

/**
 *
 * @author ASUS
 */
public class FileCopier {

    private static JProgressBar innerProgressBar;
    // innerLabelBar is for percentage
    private static JLabel innerLabelBar;
    // labelLoading is for loadingicon
    private static JLabel labelLoading;

    public static void setProgressBar(JProgressBar jp) {
        innerProgressBar = jp;

    }

    public static void setProgressLabel(JLabel jb, JLabel jb2) {
        innerLabelBar = jb;
        labelLoading = jb2;

    }

    public static void downloadFromURL(URL urlCome, String savedPath) throws Exception {

        if (innerLabelBar != null) {
            innerLabelBar.setVisible(true);
            innerProgressBar.setVisible(true);
            labelLoading.setVisible(true);
        }

        System.out.println("Downloading " + urlCome);
        System.out.println("Saving " + savedPath);

        URLConnection con = urlCome.openConnection();
        long fileSize = con.getContentLength();
        long fileSizeInKB = fileSize / 1024;
        long fileSizeInMB = fileSizeInKB / 1024;

        if (innerProgressBar != null) {
            innerProgressBar.setMaximum((int) fileSize);
        }

        ReadableByteChannel rbc = Channels.newChannel(con.getInputStream());
        ReadableConsumerByteChannel rcbc = new ReadableConsumerByteChannel(rbc, (int b) -> {

            //double prc = (b * 100) / fileSize;
            double prc = (b / 1024 / 1024);
            String percentage = prc + " Mb of " + fileSizeInMB + " Mb";

            System.out.println("Read  " + b + "/" + fileSize);
            if (innerProgressBar != null) {
                innerProgressBar.setValue(b);
            }

            if (innerLabelBar != null) {
                innerLabelBar.setText(percentage);
            }

            if (b == fileSize) {
                if (innerLabelBar != null) {
                    innerLabelBar.setVisible(false);
                    innerProgressBar.setVisible(false);
                    labelLoading.setVisible(false);
                }
            }
        });
        FileOutputStream fos = new FileOutputStream(new File(savedPath));
        fos.getChannel().transferFrom(rcbc, 0, Long.MAX_VALUE);

        // manual for releasing memory
        fos.flush();
        fos.close();
        fos = null;
        System.gc();

    }

    public static void downloadImageFromURL(URL urlCome, String pathSave) throws Exception {

        System.out.println("Downloading image " + urlCome);
        BufferedImage img = ImageIO.read(urlCome);
        File file = new File(pathSave);

        // remove the dot part
        String ext = getFileExtension(file).replace(".", "");
        System.out.println("Saving " + file + " with ext " + ext);
        ImageIO.write(img, ext, file);

    }

    public static String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf);
    }

    public static void copyTo(File source, File dest) throws Exception {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            is.close();
            os.close();
        }
    }
}
