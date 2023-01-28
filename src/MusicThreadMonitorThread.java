/**
 * Music play status monitor thread
 * use for monite whether music timeline has reach the end or pausing
 *
 * @author li1345825138
 * @date 02/27/2023
 */
public class MusicThreadMonitorThread extends Thread {

    // music thread
    private AudioStreamThread musicThread;

    // music control panel
    private MusicControlPanelV2 musicControlPanelV2;

    /**
     * Constructor
     * @param musicThread - music thread
     * @param musicControlPanelV2 - music control panel
     */
    public MusicThreadMonitorThread(AudioStreamThread musicThread, MusicControlPanelV2 musicControlPanelV2) {
        this.musicThread = musicThread;
        this.musicControlPanelV2 = musicControlPanelV2;
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
            this.musicControlPanelV2.isPause(false);
            this.musicControlPanelV2.getRefreshMusicListBtn().setEnabled(true);
            this.musicControlPanelV2.getMusicListComp().setEnabled(true);
            this.musicControlPanelV2.getStopMusicBtn().setEnabled(false);
            this.musicControlPanelV2.getPlayPauseMusicBtn().setText("Play");
            this.musicControlPanelV2.getLoopCheckBox().setSelected(false);
            this.musicControlPanelV2.getLoopCheckBox().setEnabled(false);
        }
    }
}
