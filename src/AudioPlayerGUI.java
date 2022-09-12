import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * Music Gui
 * @author SaltFish
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
     * Test code only
     */
    /*public AudioPlayerGUI() {
        try {
            URL musicPath = new URL("jar:file:!/res/");
            File musicFiles = new File(musicPath.getPath());
            String[] musicNames = musicFiles.list((dir, name) -> name.endsWith(".wav"));
            for (int i = 0; i < musicNames.length; i++)
                musicNames[i] = musicNames[i].replace(".wav", "");
            System.out.println(musicNames);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }*/

    /**
     * Program Main Entry
     * @param args
     */
    public static void main(String[] args) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        new AudioPlayerGUI(screenSize);
        /*new AudioPlayerGUI();*/
    }
}
