/**
 * Music play status monitor thread
 * use for monite whether music timeline has reach the end or pausing
 *
 * @author li1345825138
 * @date 02/27/2023
 */
public class MusicThreadMonitorRunnable implements Runnable {

    // music thread
    private AudioStreamRunnable musicThread;

    // music control panel
    private MusicControlPanel musicControlPanel;

    /**
     * Constructor
     * @param musicThread - music thread
     * @param musicControlPanel - music control panel
     */
    public MusicThreadMonitorRunnable(AudioStreamRunnable musicThread, MusicControlPanel musicControlPanel) {
        this.musicThread = musicThread;
        this.musicControlPanel = musicControlPanel;
    }

    @Override
    public void run() {
        synchronized (this) {
            while (this.musicThread.isPlaying() || this.musicThread.isPause()){
                try {
                    this.wait(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            this.musicControlPanel.setPauseFlag(false);
            this.musicControlPanel.getRefreshMusicListBtn().setEnabled(true);
            this.musicControlPanel.getMusicListComp().setEnabled(true);
            this.musicControlPanel.getStopMusicBtn().setEnabled(false);
            this.musicControlPanel.getPlayPauseMusicBtn().setText("Play");
            this.musicControlPanel.getPlayPauseMusicBtn().setEnabled(true);
            this.musicControlPanel.getLoopCheckBox().setSelected(false);
            this.musicControlPanel.getLoopCheckBox().setEnabled(false);
        }
    }
}
