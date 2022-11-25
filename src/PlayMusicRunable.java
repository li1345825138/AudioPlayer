import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

/**
 * Play music thread
 * @author li1345825138
 * @date 09/12/2022
 */
@Deprecated(since = "This is old Music Thread, use AudioStreamThread instead")
public class PlayMusicRunable implements Runnable {

    /**
     * Music Playing Status enumerator
     */
    public enum MusicStatus {Playing, Pause, Stop};

    private MusicStatus status;
    // Music file full path
    private String musicFullPath;

    // Audio Input Stream from music file
    private AudioInputStream audioInputStream;

    // Music clip to playing music
    private Clip clip;

    /**
     * Get current playing music Frame Position
     * @return current playing music Frame Position
     */
    public synchronized int getMusicTimePosition() {
        return (this.clip != null) ? clip.getFramePosition() : 0;
    }

    /**
     * Play music Runnable Contractor
     * @param musicPath music files locate root path
     * @param musicName music file name
     */
    public PlayMusicRunable(String musicPath, String musicName) {
        this.musicFullPath = musicPath + musicName + ".wav";
    }

    /**
     * Close Music Audio Stream
     */
    public synchronized void closeAudioStream() {
        if (this.audioInputStream != null) {
            try {
                this.clip.close();
                this.audioInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Initial Audio Stream
     * @throws UnsupportedAudioFileException
     * @throws IOException
     * @throws LineUnavailableException
     * @throws InterruptedException
     */
    private void initialAudioStream() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        this.audioInputStream = AudioSystem.getAudioInputStream(new File(this.musicFullPath));
        this.clip = AudioSystem.getClip();
        this.clip.open(this.audioInputStream);
        this.clip.setFramePosition(0);
        this.clip.start();
        while (!this.clip.isRunning()); // wait for start running
    }

    /**
     * Pause Music
     */
    public synchronized void pauseMusic() {
        if (this.clip == null) return;
        this.status = MusicStatus.Pause;
        setLoop(false);
        this.clip.stop();
        while(this.clip.isRunning());
    }

    /**
     * Resume playing music
     * @param isLoop determine if is continued looping after music resume
     */
    public synchronized void resumeMusic(final boolean isLoop) {
        try {
            if (this.clip != null) {
                setLoop(isLoop);
                this.clip.start();
                while(!this.clip.isRunning());
                this.status = MusicStatus.Playing;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * stop the current playing music
     */
    public synchronized void stopMusic() {
        if (this.clip != null) {
            this.status = MusicStatus.Stop;
            this.clip.stop();
            while(this.clip.isRunning());
            closeAudioStream();
        }
    }

    /**
     * Set isLoop Property
     * If isLoop is true, music will be playing repeatedly
     * If isLoop is false, music will stop until finished
     * @param loop
     */
    public void setLoop(boolean loop) {
        if (this.clip == null) return;
        if (loop) this.clip.loop(Clip.LOOP_CONTINUOUSLY);
        else this.clip.loop(0);
    }

    /**
     * Get current Thread run flag status
     * @return The Status of Music Audio
     */
    public synchronized MusicStatus getStatus() {
        return this.status;
    }

    /**
     * Set Thread Flag status
     * Mostly use for stop music playing by manually
     * @param flag
     */
    public synchronized void setStatus(MusicStatus flag) {
        this.status = flag;
    }

    @Override
    public void run() {
        this.status = MusicStatus.Playing;
        while (true) {
            try {
                if (this.audioInputStream == null)
                    initialAudioStream();
                while (this.clip != null && this.clip.isRunning()) {
                    if (this.status == MusicStatus.Stop) break;
                }
                if (this.status == MusicStatus.Pause) continue;
                this.status = MusicStatus.Stop;
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        stopMusic();
    }
}
