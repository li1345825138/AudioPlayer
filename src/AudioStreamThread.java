import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

/**
 * Play Audio Stream Thread
 * @author li1345825138
 * @date 11/24/2022
 */
public class AudioStreamThread implements Runnable {

    /**
     * Music Playing Status
     */
    public enum MusicStatus {Playing, Pause, Stop};

    // music current status
    private volatile MusicStatus status;

    // music file full path
    private String musicFullPath;

    // Audio Input Stream from music file
    private AudioInputStream audioInputStream;

    // Music clip to play music
    private Clip clip;

    /**
     * Play music Runnable Contractor
     * This Constructor didn't set up anything
     * Set up the properties before run it
     */
    public AudioStreamThread() {
        this.status = MusicStatus.Stop;
        this.musicFullPath = null;
        this.audioInputStream = null;
        this.clip = null;
    }

    /**
     * Play music Runnable Contractor
     * @param musicPath music files locate root path
     * @param musicName music file name
     */
    public AudioStreamThread(String musicPath, String musicName){
        StringBuilder sb = new StringBuilder(musicPath);
        sb.append(musicName).append(".wav");
        this.musicFullPath = sb.toString();
        try {
            initialAudioStream();
        } catch (Exception e) {
            System.err.println("Exception occur on initial the Audio Stream");
            System.exit(-1);
        }
    }

    /**
     * Set up Music Audio properties
     * @param musicPath music files locate root path
     * @param musicName music file name
     */
    public void setMusicProperties(String musicPath, String musicName) {
        StringBuilder sb = new StringBuilder(musicPath);
        sb.append(musicName).append(".wav");
        this.musicFullPath = sb.toString();
        try {
            initialAudioStream();
        } catch (Exception e) {
            System.err.println("Exception occur on initial the Audio Stream");
            System.exit(-1);
        }
    }

    /**
     * Initial Audio Stream
     */
    private void initialAudioStream() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        this.audioInputStream = AudioSystem.getAudioInputStream(new File(this.musicFullPath));
        this.clip = AudioSystem.getClip();
        this.clip.open(this.audioInputStream);
        this.clip.setFramePosition(0);
        this.status = MusicStatus.Stop;
    }

    /**
     * Set is music keep playing repeatly
     * If isLoop is true, music will be playing repeatedly
     * If isLoop is false, music will stop until finished
     * @param repeat repeat condition
     */
    public void setRepeat(boolean repeat) {
        if (this.clip == null) return;
        this.clip.loop((repeat) ? Clip.LOOP_CONTINUOUSLY : 0);
    }

    /**
     * Pause Music play
     */
    public synchronized void pauseAudioStream() {
        if (this.clip == null) return;
        this.clip.stop();
        while (this.clip.isRunning());  // wait for complete stop
        this.status = MusicStatus.Pause;
    }

    /**
     * Close Music Audio Stream
     * @throws IOException if input or output error
     * @throws SecurityException if the clip line cannot be close
     */
    private synchronized void closeAudioStream() throws IOException, SecurityException {
        if (this.audioInputStream == null || this.clip == null) return;
        this.clip.stop();
        while (this.clip.isRunning());  // wait for complete stop
        this.clip.close();
        this.audioInputStream.close();
    }

    /**
     * Get if music clip is running
     * @return true clip is running, otherwise false
     */
    public synchronized boolean isPlaying() {
       return this.clip.isRunning();
    }

    /**
     * Set Music play Status
     * @param status new status
     */
    public synchronized void setStatus(MusicStatus status) {
        this.status = status;
    }

    /**
     * if current status is paused
     * @return if current is paused
     */
    public boolean isPause(){
        return this.status == MusicStatus.Pause;
    }

    /**
     * stop the current playing music
     */
    public synchronized void stopMusic() {
        if (this.clip == null) return;
        try {
            closeAudioStream();
            this.status = MusicStatus.Stop;
        } catch (Exception e) {
            System.err.println("Exception occur on stop music");
            System.exit(-1);
        }
    }

    @Override
    public void run() {

        // Just for secure check
        if (this.audioInputStream == null || this.clip == null) return;
        this.clip.start();
        this.status = MusicStatus.Playing;
    }
}
