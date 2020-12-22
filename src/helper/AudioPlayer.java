/*
 *  This is a Portal Access for Client & Admin Usage
 *  (c) FGroupIndonesia, 2020.
 */

package helper;

import java.io.File;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

/**
 *
 * @author ASUS
 */
public class AudioPlayer {

    int secondDuration = 9 * 1000;

    public void play() {

        new Thread() {
            public void run() {
                File audioFile = new File(PathReference.AlarmPath);
                try {
                    AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
                    AudioFormat format = audioStream.getFormat();
                    DataLine.Info info = new DataLine.Info(Clip.class, format);
                    Clip audioClip = (Clip) AudioSystem.getLine(info);
                    audioClip.open(audioStream);
                    audioClip.start();

                    Thread.sleep(secondDuration);
                    audioClip.close();
                } catch (Exception ex) {

                }
            }
        }.start();

    }
}
