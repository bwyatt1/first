package bwyatt.game.client;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

public class PreferencesPane extends JFrame implements ActionListener, ChangeListener, MouseListener
{
    private GameFrame parent;
    private JTextField nameField;
    private JTextField serverField;
    private JSlider musicSlider;
    private JCheckBox musicMuted;
    private JSlider soundsSlider;
    private JCheckBox soundsMuted;
    private JLabel playerIconButton;
    private JPanel playerIconChooser;
    private JLabel[] playerIconLabels;
    int playerIconID;

    public PreferencesPane(GameFrame parent)
    {
        super("Preferences");

        this.parent = parent;
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter()
            {
                public void windowClosing(WindowEvent e)
                {
                    closeAction();
                }
            }
        );
        
        
        JLabel serverLabel = new JLabel("Server: ", SwingConstants.RIGHT);
        serverField = new JTextField();
        serverField.setMaximumSize(new Dimension(Integer.MAX_VALUE, serverField.getPreferredSize().height));
        serverField.addActionListener(this);

        JLabel nameLabel = new JLabel("Name: ", SwingConstants.RIGHT);
        nameField = new JTextField();
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, nameField.getPreferredSize().height));
        nameField.addActionListener(this);

        JLabel playerIconLabel = new JLabel("Player Icon: ", SwingConstants.RIGHT);
        playerIconButton = new JLabel(ImageCache.getPlayerIcon(playerIconID));
        playerIconButton.addMouseListener(this);
        playerIconChooser = new JPanel();
        playerIconChooser.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        playerIconChooser.setBackground(Color.WHITE);
        int rows = 2;
        int cols = 3;
        playerIconChooser.setLayout(new GridLayout(rows, cols, 3, 3));
        playerIconLabels = new JLabel[ImageCache.getPlayerIconCount()];
        for (int i = 0; i < playerIconLabels.length; ++i)
        {
            playerIconLabels[i] = new JLabel(ImageCache.getPlayerIcon(i));
            playerIconLabels[i].addMouseListener(this);
            playerIconChooser.add(playerIconLabels[i]);
        }
                    
        playerIconChooser.setSize((rows+3)*16+3, (cols+3)*16+3);
        playerIconChooser.setVisible(false);

        JPanel volumePanel = new JPanel();
        volumePanel.setBorder(new TitledBorder(new EtchedBorder(), "Volume"));
        JLabel musicLabel = new JLabel("Music: ", SwingConstants.RIGHT);
        musicSlider = new JSlider(0, 100, 100);
        musicSlider.addChangeListener(this);
        musicMuted = new JCheckBox("Mute", false);
        musicMuted.addActionListener(this);
        JLabel soundsLabel = new JLabel("Sounds: ", SwingConstants.RIGHT);
        soundsLabel.setVerticalTextPosition(SwingConstants.CENTER);
        soundsSlider = new JSlider(0, 100, 100);
        soundsSlider.addChangeListener(this);
        soundsMuted = new JCheckBox("Mute", false);
        soundsMuted.addActionListener(this);

        SpringLayout volumeLayout = new SpringLayout();
        volumePanel.setLayout(volumeLayout);
        volumePanel.add(musicLabel);
        volumeLayout.putConstraint(SpringLayout.NORTH, musicLabel, 5, SpringLayout.NORTH, volumePanel);
        volumeLayout.putConstraint(SpringLayout.WEST, musicLabel, 5, SpringLayout.WEST, volumePanel);
        volumePanel.add(musicSlider);
        volumeLayout.putConstraint(SpringLayout.NORTH, musicSlider, 5, SpringLayout.NORTH, volumePanel);
        volumeLayout.putConstraint(SpringLayout.WEST, musicSlider, 5, SpringLayout.EAST, musicLabel);
        volumePanel.add(musicMuted);
        volumeLayout.putConstraint(SpringLayout.NORTH, musicMuted, 5, SpringLayout.NORTH, volumePanel);
        volumeLayout.putConstraint(SpringLayout.WEST, musicMuted, 5, SpringLayout.EAST, musicSlider);
        volumeLayout.putConstraint(SpringLayout.EAST, musicMuted, -5, SpringLayout.EAST, volumePanel);
        volumePanel.add(soundsLabel);
        volumeLayout.putConstraint(SpringLayout.NORTH, soundsLabel, 5, SpringLayout.SOUTH, musicMuted);
        volumeLayout.putConstraint(SpringLayout.WEST, soundsLabel, 5, SpringLayout.WEST, volumePanel);
        volumeLayout.putConstraint(SpringLayout.SOUTH, soundsLabel, -5, SpringLayout.SOUTH, volumePanel);
        volumeLayout.putConstraint(SpringLayout.EAST, musicLabel, 0, SpringLayout.EAST, soundsLabel);
        volumePanel.add(soundsSlider);
        volumeLayout.putConstraint(SpringLayout.NORTH, soundsSlider, 5, SpringLayout.SOUTH, musicMuted);
        volumeLayout.putConstraint(SpringLayout.WEST, soundsSlider, 5, SpringLayout.EAST, soundsLabel);
        volumeLayout.putConstraint(SpringLayout.SOUTH, soundsSlider, -5, SpringLayout.SOUTH, volumePanel);
        volumePanel.add(soundsMuted);
        volumeLayout.putConstraint(SpringLayout.NORTH, soundsMuted, 5, SpringLayout.SOUTH, musicMuted);
        volumeLayout.putConstraint(SpringLayout.WEST, soundsMuted, 5, SpringLayout.EAST, soundsSlider);
        volumeLayout.putConstraint(SpringLayout.SOUTH, soundsMuted, -5, SpringLayout.SOUTH, volumePanel);
        volumeLayout.putConstraint(SpringLayout.EAST, soundsMuted, -5, SpringLayout.EAST, volumePanel);

        SpringLayout generalLayout = new SpringLayout();
        JPanel generalPanel = new JPanel();
        generalPanel.setLayout(generalLayout);
        generalPanel.add(serverLabel);
        generalLayout.putConstraint(SpringLayout.NORTH, serverLabel, 5, SpringLayout.NORTH, generalPanel);
        generalLayout.putConstraint(SpringLayout.WEST, serverLabel, 5, SpringLayout.WEST, generalPanel);
        generalPanel.add(serverField);
        generalLayout.putConstraint(SpringLayout.NORTH, serverField, 5, SpringLayout.NORTH, generalPanel);
        generalLayout.putConstraint(SpringLayout.WEST, serverField, 5, SpringLayout.EAST, serverLabel);
        generalLayout.putConstraint(SpringLayout.EAST, serverField, -5, SpringLayout.EAST, generalPanel);
        generalPanel.add(nameLabel);
        generalLayout.putConstraint(SpringLayout.NORTH, nameLabel, 5, SpringLayout.SOUTH, serverField);
        generalLayout.putConstraint(SpringLayout.WEST, nameLabel, 5, SpringLayout.WEST, generalPanel);
        generalPanel.add(nameField);
        generalLayout.putConstraint(SpringLayout.NORTH, nameField, 5, SpringLayout.SOUTH, serverField);
        generalLayout.putConstraint(SpringLayout.WEST, nameField, 0, SpringLayout.WEST, serverField);
        generalLayout.putConstraint(SpringLayout.EAST, nameField, -5, SpringLayout.EAST, generalPanel);
        generalPanel.add(playerIconLabel);
        generalLayout.putConstraint(SpringLayout.NORTH, playerIconLabel, 5, SpringLayout.SOUTH, nameField);
        generalLayout.putConstraint(SpringLayout.WEST, playerIconLabel, 5, SpringLayout.WEST, generalPanel);
        generalLayout.putConstraint(SpringLayout.EAST, nameLabel, 0, SpringLayout.EAST, playerIconLabel);
        generalLayout.putConstraint(SpringLayout.EAST, serverLabel, 0, SpringLayout.EAST, playerIconLabel);
        generalPanel.add(playerIconButton);
        generalLayout.putConstraint(SpringLayout.NORTH, playerIconButton, 5, SpringLayout.SOUTH, nameField);
        generalLayout.putConstraint(SpringLayout.WEST, playerIconButton, 5, SpringLayout.EAST, playerIconLabel);
        generalLayout.putConstraint(SpringLayout.SOUTH, playerIconButton, 0, SpringLayout.SOUTH, playerIconLabel);
        generalPanel.add(playerIconChooser);
        generalLayout.putConstraint(SpringLayout.NORTH, playerIconChooser, 5, SpringLayout.SOUTH, nameField);
        generalLayout.putConstraint(SpringLayout.WEST, playerIconChooser, 5, SpringLayout.EAST, playerIconButton);
        generalPanel.add(volumePanel);
        generalLayout.putConstraint(SpringLayout.NORTH, volumePanel, 5, SpringLayout.SOUTH, playerIconButton);
        generalLayout.putConstraint(SpringLayout.WEST, volumePanel, 5, SpringLayout.WEST, generalPanel);
        generalLayout.putConstraint(SpringLayout.EAST, volumePanel, -5, SpringLayout.EAST, generalPanel);
        generalLayout.putConstraint(SpringLayout.SOUTH, volumePanel, -5, SpringLayout.SOUTH, generalPanel);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("General", generalPanel);

        this.add(tabbedPane);
    }

    public void init(Config config)
    {
        nameField.setText(config.getName());
        serverField.setText(config.getServer());
        musicSlider.setValue(config.getMusicVolume());
        musicMuted.setSelected(config.getMusicMuted());
        soundsSlider.setValue(config.getSoundsVolume());
        soundsMuted.setSelected(config.getSoundsMuted());
        playerIconID = config.getPlayerIconID();
        playerIconButton.setIcon(ImageCache.getPlayerIcon(playerIconID));
    }

    public void closeAction()
    {
        parent.preferencesChanged();
        setVisible(false);
    }

    // For text fields
    public void actionPerformed(ActionEvent e)
    {
        parent.preferencesChanged();
    }

    // For volume controls
    public void stateChanged(ChangeEvent e)
    {
        if (this.isVisible())
        {
            parent.preferencesChanged();
        }
    }

    // For player icon widgets
    public void mouseClicked(MouseEvent e)
    {
        if (e.getSource() == playerIconButton)
        {
            if (playerIconChooser.isVisible())
            {
                playerIconChooser.setVisible(false);
            }
            else
            {
                playerIconChooser.setVisible(true);
            }
        }
        else
        {
            for (int i = 0; i < playerIconLabels.length; ++i)
            {
                if (e.getSource() == playerIconLabels[i])
                {
                    playerIconID = i;
                    playerIconButton.setIcon(ImageCache.getPlayerIcon(playerIconID));
                    parent.preferencesChanged();
                    playerIconChooser.setVisible(false);
                    return;
                }
            }
        }
    }

    // mouse listener null methods
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}

    public String getName()
    {
        return nameField.getText();
    }

    public String getServer()
    {
        return serverField.getText();
    }

    public int getMusicVolume()
    {
        return musicSlider.getValue();
    }

    public boolean getMusicMuted()
    {
        return musicMuted.isSelected();
    }

    public int getSoundsVolume()
    {
        return soundsSlider.getValue();
    }

    public boolean getSoundsMuted()
    {
        return soundsMuted.isSelected();
    }

    public int getPlayerIconID()
    {
        return this.playerIconID;
    }
}
