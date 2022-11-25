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
import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Music Control Panel Version 2
 * Update from original MusicControlPanel Class
 *
 * @author li1345825138
 * @date 11/24/2022
 */
public class MusicControlPanelV2 extends JPanel implements ActionListener {
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

    // music files locate root path
    private String musicPath;

    // music thread pool
    private ThreadPoolExecutor musicThreadPool;

    // music play thread
    private AudioStreamThread musicThread;

    // is current music thread pause
    private volatile boolean isPause;

    /**
     * Music Control Panel Constructor
     * @param musicsPath the root of music files locate path
     */
    public MusicControlPanelV2(String musicsPath) {
        setLayout(null);
        this.musicThreadPool = new ThreadPoolExecutor(2, 2, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1), Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.DiscardPolicy());
        this.musicPath = musicsPath;
        this.isPause = false;
        setUpComponents();
        this.musicThread = new AudioStreamThread();
    }

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
        for (int i = 0; i < Objects.requireNonNull(musicNames).length; i++)
            musicNames[i] = musicNames[i].replace(".wav", "");
        return musicNames;
    }

    /**
     * Set up all Component on Panel
     */
    private void setUpComponents () {
        // Set up Music List Panel
        String[] musicListNames = getMusicList();
        assert musicListNames != null;
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

        this.loopCheckBox = new JCheckBox("Repeat", false);
        this.loopCheckBox.setFocusable(false);
        this.loopCheckBox.setBounds(440, 90, 81, 21);
        this.loopCheckBox.addActionListener(this);
        this.loopCheckBox.setEnabled(false);
        this.add(this.loopCheckBox);

        // this.addKeyListener(this);
        this.setFocusable(true);
    }

    /**
     * Monite music thread to see if it ended
     */
    private void moniteMusicThread() {
        while (this.musicThread.isPlaying() || this.musicThread.isPause()){
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.isPause = false;
        this.refreshMusicListBtn.setEnabled(true);
        this.musicList.setEnabled(true);
        this.stopMusicBtn.setEnabled(false);
        this.playPauseMusicBtn.setText("Play");
        this.loopCheckBox.setSelected(false);
        this.loopCheckBox.setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Play" -> {
                if (this.selectMusicTitle.getText() == null || this.selectMusicTitle.getText().isEmpty() || this.selectMusicTitle.getText().equals("<== Select Music From Left Side First"))
                    return;
                if (this.isPause) {
                    this.musicThread.setRepeat(this.loopCheckBox.isSelected());
                    this.musicThreadPool.execute(this.musicThread);
                    this.playPauseMusicBtn.setText("Pause");
                    this.loopCheckBox.setEnabled(true);
                    this.isPause = false;
                    return;
                }
                this.playPauseMusicBtn.setText("Pause");
                this.musicList.setEnabled(false);
                this.refreshMusicListBtn.setEnabled(false);
                this.stopMusicBtn.setEnabled(true);
                this.musicThread.setMusicProperties(this.musicPath, this.selectMusicTitle.getText());
                this.musicThreadPool.execute(this.musicThread);
                while (!this.musicThread.isPlaying()) ;
                this.musicThreadPool.execute(this::moniteMusicThread);
                this.loopCheckBox.setEnabled(true);
            }
            case "Pause" -> {
                if (this.musicThread == null) return;
                this.musicThread.pauseAudioStream();
                this.loopCheckBox.setEnabled(false);
                this.playPauseMusicBtn.setText("Play");
                this.isPause = true;
            }
            case "Stop" -> {
                this.musicThread.stopMusic();
                this.isPause = false;
            }
            case "Repeat" -> this.musicThread.setRepeat(this.loopCheckBox.isSelected());
            case "Refresh Music List" -> {
                this.defaultModule.removeAllElements();
                String[] musicListNames = getMusicList();
                assert musicListNames != null;
                if (musicListNames.length == 0) {
                    JOptionPane.showMessageDialog(this, "There is no wav music files on songs/ directory", "Warning", JOptionPane.WARNING_MESSAGE);
                }
                this.defaultModule.addAll(List.of(musicListNames));
                this.selectMusicTitle.setText("<== Select Music From Left Side First");
            }
        }
    }
}
