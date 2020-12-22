/*
 *  This is a Portal Access for Client & Admin Usage
 *  (c) FGroupIndonesia, 2020.
 */

package helper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class BarcodeGenerator {

    JLabel refElement;
    String filename = "\\qrcode.png";
    String path = PathReference.DirName + filename;

    // Function to create the QR code
    public void createQR(String data,
            String charset, Map hashMap,
            int height, int width) {

        try {
            BitMatrix matrix = new MultiFormatWriter().encode(
                    new String(data.getBytes(charset), charset),
                    BarcodeFormat.QR_CODE, width, height);

            MatrixToImageWriter.writeToFile(
                    matrix,
                    path.substring(path.lastIndexOf('.') + 1),
                    new File(path));

            refElement.setIcon(new ImageIcon(path));
        } catch (Exception ex) {

        }
    }

    // Driver code
    public void create(String data, JLabel element) {

        // the JLabel for showing the image
        refElement = element;
        refElement.setText("");

        // The path where the image will get saved
        File file = new File(PathReference.DirName);

        // true if the directory was created, false otherwise
        if (file.isDirectory() && !file.exists()) {

            if (file.mkdirs()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Directory for barcode is ready!");
            }

        }

        // Encoding charset
        String charset = "UTF-8";

        Map<EncodeHintType, ErrorCorrectionLevel> hashMap
                = new HashMap<EncodeHintType, ErrorCorrectionLevel>();

        hashMap.put(EncodeHintType.ERROR_CORRECTION,
                ErrorCorrectionLevel.L);

        // Create the QR code and save
        // in the specified folder
        // as a jpg file
        createQR(data, charset, hashMap, 200, 200);
        System.out.println("QR Code Generated!!! " + path);
    }
}
