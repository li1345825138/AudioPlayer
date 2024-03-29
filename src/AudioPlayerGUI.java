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

    /**
     * Set up Control Panel Object
     */
    private void setUpControlPanel() {
        this.musicControlPanel = new MusicControlPanel("songs/");
        this.add(this.musicControlPanel);
    }
    /**
     * Constructor
     * Set up Music Player GUI
     */
    public AudioPlayerGUI(Dimension screenSize) {
        setTitle("Java Audio Player");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(581, 330);
        int positionX = (screenSize.width - getWidth()) >> 1;
        int positionY = (screenSize.height - getHeight()) >> 1;
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
