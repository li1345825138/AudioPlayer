import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.List;

/**
 * Music Control Panel
 * @author li1345825138
 * @date 09/12/2022
 */
public class MusicControlPanel extends JPanel implements ActionListener, KeyListener {

    // Default Module set up for JList
    private DefaultListModel<String> defaultModule;

    // Store all wav music file in list
    private JList<String> musicList;

    // Scroll Panel to display value on musicList
    private JScrollPane musicListScrollPanel;

    // display select music title
    private JLabel selectMusicTitle;

    // Play and Pause Button (Pause function not implement yet)
    private JButton playPauseMusicBtn;

    // Stop current playing music
    private JButton stopMusicBtn;

    // Refresh Music List Button
    private JButton refreshMusicListBtn;

    // loop current playing music check box
    private JCheckBox loopCheckBox;

    // music start playing thread
    private Thread musicThread;

    // music playing progress monitor thread
    private Thread musicPlayingMonitor;

    // music playing runnable class, use for musicThread
    private PlayMusicRunable musicPlay;

    // music files locate root path
    private String musicPath;

    // music pause time position record for music resume playing
    private int musicPauseTimePosition = 0;

    /**
     * Return all wav format music file
     * @return a string array of all the files that are WAV format
     */
    private String[] getMusicList() {
        File musicFiles = new File(musicPath);
        if (!musicFiles.exists()) {
            musicFiles.mkdir();
            JOptionPane.showMessageDialog(this, "songs directory is not exists, auto create", "Warning", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        String[] musicNames = musicFiles.list((dir, name) -> name.endsWith(".wav"));
        for (int i = 0; i < musicNames.length; i++)
            musicNames[i] = musicNames[i].replace(".wav", "");
        return musicNames;
    }

    /**
     * Set up all Component on Panel
     */
    private void setUpComponents () {
        // Set up Music List Panel
        String[] musicListNames = getMusicList();
        if (musicListNames.length == 0) {
            JOptionPane.showMessageDialog(this, "There is no wav music files on songs/ directory", "Warning", JOptionPane.WARNING_MESSAGE);
        }
        this.defaultModule = new DefaultListModel<>();
        this.defaultModule.addAll(List.of(musicListNames));
        this.musicList = new JList<>(this.defaultModule);
        this.musicList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.musicList.setFocusable(false);
        this.musicList.addListSelectionListener((e) -> this.selectMusicTitle.setText(this.musicList.getSelectedValue()));
        this.musicListScrollPanel = new JScrollPane();
        this.musicListScrollPanel.setViewportView(this.musicList);
        this.musicListScrollPanel.setBounds(10, 10, 191, 261);
        this.musicListScrollPanel.setFocusable(false);
        this.add(this.musicListScrollPanel);

        // Set up Music Label, if one music been select from music panel, display music name
        this.selectMusicTitle = new JLabel("<== Select Music From Left Side First");
        this.selectMusicTitle.setBounds(230, 20, 331, 21);
        this.selectMusicTitle.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(this.selectMusicTitle);

        this.refreshMusicListBtn = new JButton("Refresh Music List");
        this.refreshMusicListBtn.setFocusable(false);
        this.refreshMusicListBtn.setBounds(250, 130, 171, 31);
        this.refreshMusicListBtn.addActionListener(this);
        this.add(this.refreshMusicListBtn);

        this.playPauseMusicBtn = new JButton("Play");
        this.playPauseMusicBtn.setFocusable(false);
        this.playPauseMusicBtn.addActionListener(this);
        this.playPauseMusicBtn.setBounds(250, 90, 81, 31);
        this.add(this.playPauseMusicBtn);

        this.stopMusicBtn = new JButton("Stop");
        this.stopMusicBtn.setFocusable(false);
        this.stopMusicBtn.setEnabled(false);
        this.stopMusicBtn.addActionListener(this);
        this.stopMusicBtn.setBounds(340, 90, 81, 31);
        this.add(this.stopMusicBtn);

        this.loopCheckBox = new JCheckBox("Loop", false);
        this.loopCheckBox.setFocusable(false);
        this.loopCheckBox.setBounds(440, 90, 81, 21);
        this.loopCheckBox.addActionListener((e) -> {
            if (this.loopCheckBox.isSelected()){
                setMusicPlayLoop(true);
                return;
            }
            setMusicPlayLoop(false);
        });
        this.loopCheckBox.setEnabled(false);
        this.add(this.loopCheckBox);

        this.addKeyListener(this);
        this.setFocusable(true);
    }

    /**
     * Stop music when music play thread complete
     * use by music monitor thread
     */
    private synchronized void stopPlayWhenFinished() {
        while (this.musicPlay != null) {
            if (this.musicPlay.getStatus() == PlayMusicRunable.MusicStatus.Stop) {
                this.refreshMusicListBtn.setEnabled(true);
                this.musicList.setEnabled(true);
                this.musicPlay.setLoop(false);
                this.musicPauseTimePosition = 0;
                this.stopMusicBtn.setEnabled(false);
                this.playPauseMusicBtn.setText("Play");
                this.playPauseMusicBtn.setEnabled(true);
                this.loopCheckBox.setEnabled(false);
                this.loopCheckBox.setSelected(false);
                this.musicPlay = null;
                this.musicThread = null;
                this.musicPlayingMonitor = null;
                break;
            }
        }
    }

    /**
     * Set music play in loop
     * @param flag
     */
    private void setMusicPlayLoop(boolean flag) {
        if (this.musicPlay == null) return;
        this.musicPlay.setLoop(flag);
    }

    private void pauseMusic() {
        if (this.musicPlay == null) return;
        this.musicPlay.pauseMusic();
    }

    /**
     * Music Control Panel Constructor
     * @param musicsPath the root of music files locate path
     */
    public MusicControlPanel(String musicsPath) {
        setLayout(null);
        this.musicPath = musicsPath;
        setUpComponents();
    }

    /**
     * play or pause music
     * @param command
     */
    private void playOrPauseMusic(String command) {
        if (command.equals("Play")) {
            if (this.selectMusicTitle.getText() == null || this.selectMusicTitle.getText().equals("") || this.selectMusicTitle.getText().equals("<== Select Music From Left Side First")) return;
            if (this.musicPauseTimePosition > 0) {
                this.musicPlay.resumeMusic(this.loopCheckBox.isSelected());
                this.playPauseMusicBtn.setText("Pause");
                this.loopCheckBox.setEnabled(true);
                return;
            }
            this.playPauseMusicBtn.setText("Pause");
            this.musicList.setEnabled(false);
            this.refreshMusicListBtn.setEnabled(false);
            this.stopMusicBtn.setEnabled(true);
            this.musicPlay = new PlayMusicRunable(this.musicPath, this.selectMusicTitle.getText());
            this.musicThread = new Thread(this.musicPlay);
            this.musicThread.start();
            this.musicPlayingMonitor = new Thread(() -> this.stopPlayWhenFinished());
            this.musicPlayingMonitor.start();
            this.loopCheckBox.setEnabled(true);
        } else if (command.equals("Pause")) {
            this.musicPauseTimePosition = this.musicPlay.getMusicTimePosition();
            pauseMusic();
            this.loopCheckBox.setEnabled(false);
            this.playPauseMusicBtn.setText("Play");
        }
    }

    /**
     * Process Button press command
     * When Play Button been press, play music
     * When Stop Button been press, stop playing music
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Play")) {
            playOrPauseMusic("Play");
        } else if (e.getActionCommand().equals("Pause")) {
            playOrPauseMusic("Pause");
        } else if (e.getSource() == this.stopMusicBtn) {
            this.refreshMusicListBtn.setEnabled(true);
            this.stopMusicBtn.setEnabled(false);
            this.musicPlay.setStatus(PlayMusicRunable.MusicStatus.Stop);
            this.musicList.setEnabled(true);
        } else if (e.getSource() == this.refreshMusicListBtn) {
            this.defaultModule.removeAllElements();
            String[] musicListNames = getMusicList();
            if (musicListNames.length == 0) {
                JOptionPane.showMessageDialog(this, "There is no wav music files on songs/ directory", "Warning", JOptionPane.WARNING_MESSAGE);
            }
            this.defaultModule.addAll(List.of(musicListNames));
        }
    }

    /**
     * Not using method
     * @param e the event to be processed
     */
    @Override
    public void keyTyped(KeyEvent e) {

    }

    /**
     * Processing if pause music key is press
     * @param e the event to be processed
     */
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_0:
            case KeyEvent.VK_NUMPAD0:
                if (this.playPauseMusicBtn.getText().equals("Play"))
                    playOrPauseMusic("Play");
                else if (this.playPauseMusicBtn.getText().equals("Pause"))
                    playOrPauseMusic("Pause");
                break;
            default:
                break;
        }
    }

    /**
     * Not using Method
     * @param e the event to be processed
     */
    @Override
    public void keyReleased(KeyEvent e) {

    }
}
