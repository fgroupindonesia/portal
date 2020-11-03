/*
 *  This is a Supplemental File from the Main Project used
 *  in Java Programming Core Fundamental I
 *  with FGroupIndonesia team.
 */
package helper;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import javax.imageio.ImageIO;

/**
 *
 * @author ASUS
 */
public class FileCopier {

    public static void downloadFromURL(URL urlCome, String savedPath) throws Exception {

        System.out.println("Downloading " + urlCome);
        System.out.println("Saving " + savedPath);
        
        ReadableByteChannel rbc = Channels.newChannel(urlCome.openStream());
        FileOutputStream fos = new FileOutputStream(savedPath);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

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
