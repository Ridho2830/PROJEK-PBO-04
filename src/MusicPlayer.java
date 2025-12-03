import java.io.File;
import javax.sound.sampled.*;

public class MusicPlayer {
    // UBAH JADI STATIC (Milik Bersama)
    private static Clip clip; 

    public void play(String filepath, boolean loop) {
        // CEK: Kalau musik sedang jalan, jangan di-restart. Biarkan lanjut.
        if (clip != null && clip.isRunning()) {
            return; 
        }

        try {
            // Kalau ada musik lain (misal sisa error), matikan dulu
            stop(); 
            
            AudioInputStream audio = AudioSystem.getAudioInputStream(new File(filepath));
            clip = AudioSystem.getClip();
            clip.open(audio);
            if (loop) clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch (Exception e) {
            System.out.println("Error playing music: " + e.getMessage());
        }
    }

    public void stop() {
        try {
            if (clip != null) {
                clip.stop();
                clip.close(); // Hapus dari memori
                clip = null;  // Reset variabel
            }
        } catch (Exception ignored) {}
    }
}