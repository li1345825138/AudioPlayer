import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * Music Gui
 * @author li1345825138
 * @date 09/12/2022
 */
public class AudioPlayerGUI extends JFrame {

    // Music Control Panel
    private MusicControlPanel musicControlPanel;

    private MusicControlPanelV2 musicControlPanelV2;

    /**
     * Set up Control Panel Object
     */
    private void setUpControlPanel() {
        this.musicControlPanelV2 = new MusicControlPanelV2("songs/");
        this.add(this.musicControlPanelV2);
    }
    /**
     * Constructor
     * Set up Music Player GUI
     */
    public AudioPlayerGUI(Dimension screenSize) {
        setTitle("Java Audio Player");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(581, 330);
        int positionX = (screenSize.width - getWidth()) / 2;
        int positionY = (screenSize.height - getHeight()) / 2;
        setLocation(positionX, positionY);
        setResizable(false);
        setUpControlPanel();
        setVisible(true);
    }

    /**
     * Program Main Entry
     * @param args
     */
    public static void main(String[] args) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        new AudioPlayerGUI(screenSize);
    }
}
