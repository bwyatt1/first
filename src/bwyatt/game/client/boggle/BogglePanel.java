package bwyatt.game.client.boggle;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

import bwyatt.game.client.*;
import bwyatt.game.common.*;

public class BogglePanel extends JPanel implements ActionListener
{
    private GameFrame parent;
    private BoggleBoardPanel boggleBoardPanel;
    private JLabel[][] boggleLetterPanel;
    private JPanel hiPanel;
    private SpellingPanel spellingPanel;
    private FoundWordsPanel foundWordsPanel;
    private int gameOn;
    private int currentRoomID;
    private Room[] multiRoom;
    private JTextArea multiPlayersPane;
    private JCheckBox readyBox;
    private JRadioButton[] roomRadio;
    private JRadioButton soloRadio;
    private JRadioButton multiRadio;
    private JButton startButton;
    private PlayerInfo myInfo;
    private ArrayList<PlayerInfo> players;

    private static final int GAME_NONE = 0;
    private static final int GAME_SOLO = 1;
    private static final int GAME_MULTI = 2;
    private static final int GAME_OVER = 3;

    public BogglePanel(GameFrame parent, PlayerInfo myInfo, ArrayList<PlayerInfo> players)
    {
        this.parent = parent;
        this.myInfo = myInfo;
        this.players = players;
        this.gameOn = GAME_NONE;
        this.currentRoomID = Room.THREE_MINUTES.getID();

        this.showStartupPanel();
        this.setOpaque(false);
        this.setupKeys();
    }

    public void showStartupPanel()
    {
        this.gameOn = GAME_NONE;
        this.boggleBoardPanel = null;
        this.removeAll();

        JPanel autoPanel = new JPanel();
        autoPanel.setOpaque(false);

        JPanel modePanel = new JPanel();
        modePanel.setOpaque(false);
        soloRadio = new JRadioButton("Solo");
        multiRadio = new JRadioButton("Multiplayer");
        soloRadio.addActionListener(this);
        multiRadio.addActionListener(this);
        soloRadio.setOpaque(false);
        multiRadio.setOpaque(false);
        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(soloRadio);
        modeGroup.add(multiRadio);
        modePanel.add(soloRadio);
        modePanel.add(multiRadio);
        soloRadio.setSelected(true);

        multiRoom = new Room[3];
        multiRoom[0] = Room.ONE_MINUTE;
        multiRoom[1] = Room.THREE_MINUTES;
        multiRoom[2] = Room.INFINITY;

        JPanel roomPanel = new JPanel();
        roomPanel.setOpaque(false);
        roomRadio = new JRadioButton[3];
        roomRadio[0] = new JRadioButton("1:00");
        roomRadio[1] = new JRadioButton("3:00");
        roomRadio[2] = new JRadioButton("No Time");
        ButtonGroup roomGroup = new ButtonGroup();
        for (int i = 0; i < 3; ++i)
        {
            if (currentRoomID == multiRoom[i].getID())
                roomRadio[i].setSelected(true);
            roomRadio[i].addActionListener(this);
            roomRadio[i].setOpaque(false);
            roomGroup.add(roomRadio[i]);
            roomPanel.add(roomRadio[i]);
        }

        multiPlayersPane = new JTextArea();
        multiPlayersPane.setPreferredSize(new Dimension(300, 200));
        multiPlayersPane.setMaximumSize(multiPlayersPane.getPreferredSize());
        updateMultiTextArea();
        multiPlayersPane.setEditable(false);

        JPanel goPanel = new JPanel();
        goPanel.setOpaque(false);
        readyBox = new JCheckBox("Ready");
        readyBox.setOpaque(false);
        readyBox.addActionListener(this);
        startButton = new JButton("Start");
        startButton.addActionListener(this);
        goPanel.add(readyBox);
        goPanel.add(startButton);


        JPanel optionsPanel = new JPanel();
        optionsPanel.setOpaque(false);
        optionsPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        optionsPanel.add(modePanel, gbc);
        gbc.gridy++;
        optionsPanel.add(roomPanel, gbc);
        gbc.gridy++;
        optionsPanel.add(multiPlayersPane, gbc);
        gbc.gridy++;
        optionsPanel.add(goPanel, gbc);

        this.setLayout(new GridLayout(1, 2));
        this.add(autoPanel);
        this.add(optionsPanel);

        this.setOpaque(false);
        this.revalidate();
        this.repaint();

        parent.myRoomUpdate(currentRoomID, readyBox.isSelected());
    }

    public void updateMultiTextArea()
    {
        String text = "";
        text += myInfo.getName();
        if (myInfo.getStatus() == PlayerInfo.STATUS_ACTIVE)
            text += " Active";
        else if (myInfo.getStatus() == PlayerInfo.STATUS_READY)
            text += " Ready";
        else if (myInfo.getStatus() == PlayerInfo.STATUS_NONE)
            text += " Not Ready";
        for (PlayerInfo player : players)
        {
            if (player.getShowing() == PlayerInfo.GAME_2048 &&
                player.getRoomID() == currentRoomID)
            {
                text += "\n";
                text += player.getName();
                if (player.getStatus() == PlayerInfo.STATUS_ACTIVE)
                    text += " Active";
                else if (player.getStatus() == PlayerInfo.STATUS_READY)
                    text += " Ready";
                else if (player.getStatus() == PlayerInfo.STATUS_NONE)
                    text += " Not Ready";
            }
        }
        multiPlayersPane.setText(text);
    }

    public void setupKeys()
    {
        this.setFocusable(true);
        InputMap inputMap = this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap actionMap = this.getActionMap();
        String keyText;
        for (int i = KeyEvent.VK_A; i <= KeyEvent.VK_Z; ++i)
        {
            keyText = KeyEvent.getKeyText(i);
            inputMap.put(KeyStroke.getKeyStroke(i, 0), keyText);
            actionMap.put(keyText, new AbstractAction()
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        if (gameOn == GAME_SOLO || gameOn == GAME_MULTI)
                        {
                            spellingPanel.addLetter(e.getActionCommand().charAt(0), true);
                            boggleBoardPanel.hiWord(spellingPanel.getWord());
                        }
                    }
                }
            );
        }

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "backspace");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "space");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");
        
        actionMap.put("backspace", new AbstractAction()
            {
                public void actionPerformed(ActionEvent e)
                {
                    if (gameOn == GAME_SOLO || gameOn == GAME_MULTI)
                    {
                        if (spellingPanel.getWord().length() > 0)
                        {
                            spellingPanel.removeLastLetter();
                            boggleBoardPanel.hiWord(spellingPanel.getWord());
                        }
                    }
                }
            }
        );
        actionMap.put("space", new AbstractAction()
            {
                public void actionPerformed(ActionEvent e)
                {
                    if (gameOn == GAME_SOLO || gameOn == GAME_MULTI)
                    {
                        spellingPanel.clear();
                        boggleBoardPanel.hiClear();
                    }
                }
            }
        );
        actionMap.put("enter", new AbstractAction()
            {
                public void actionPerformed(ActionEvent e)
                {
                    if (gameOn == GAME_SOLO)
                    {
                        String word = spellingPanel.getWord();
                        if (boggleBoardPanel.contains(word))
                        {
                            if (!foundWordsPanel.contains(word))
                                foundWordsPanel.addWord(word);
                        }
                        else
                        {
                            // error beep
                        }
                        boggleBoardPanel.hiClear();
                        spellingPanel.clear();
                    }
                    else if (gameOn == GAME_MULTI)
                    {
                        String word = spellingPanel.getWord();
                        if (boggleBoardPanel.contains(word))
                        {
                            if (!foundWordsPanel.contains(word))
                                parent.boggleNewWord(word);
                        }
                        else
                        {
                            // error beep
                        }
                        boggleBoardPanel.hiClear();
                        spellingPanel.clear();
                    }
                }
            }
        );
    }

    public void startSoloGame()
    {
        this.removeAll();
        boggleBoardPanel = new BoggleBoardPanel();
        spellingPanel = new SpellingPanel();
        foundWordsPanel = new FoundWordsPanel(myInfo, players);
        gameOn = GAME_SOLO;

        SpringLayout mainLayout = new SpringLayout();
        this.setLayout(mainLayout);
        this.add(boggleBoardPanel);
        mainLayout.putConstraint(SpringLayout.WEST, boggleBoardPanel, 5, SpringLayout.WEST, this);
        mainLayout.putConstraint(SpringLayout.NORTH, boggleBoardPanel, 5, SpringLayout.NORTH, this);
        this.add(spellingPanel);
        mainLayout.putConstraint(SpringLayout.WEST, spellingPanel, 5, SpringLayout.WEST, this);
        mainLayout.putConstraint(SpringLayout.NORTH, spellingPanel, 5, SpringLayout.SOUTH, boggleBoardPanel);
        this.add(foundWordsPanel);
        mainLayout.putConstraint(SpringLayout.WEST, foundWordsPanel, 5, SpringLayout.EAST, boggleBoardPanel);
        mainLayout.putConstraint(SpringLayout.NORTH, foundWordsPanel, 5, SpringLayout.NORTH, this);
        
        boggleBoardPanel.generateNew();

        this.revalidate();
        this.repaint();
    }

    public void newWord(PlayerInfo player, String word)
    {
        foundWordsPanel.addWord(player, word);
    }

    public void navBack()
    {
        parent.navHome();
    }

    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == readyBox)
        {
            parent.myRoomUpdate(currentRoomID, readyBox.isSelected());
        }
        else if (e.getSource() == startButton)
        {
            if (soloRadio.isSelected())
            {
                startSoloGame();
            }
            else
            {
                parent.newMultiRequest();
            }
        }
        else
        {
            for (int i = 0; i < multiRoom.length; ++i)
            {
                if (e.getSource() == roomRadio[i] && currentRoomID != multiRoom[i].getID())
                {
                    currentRoomID = multiRoom[i].getID();
                    updateMultiTextArea();
                    readyBox.setSelected(false);
                    parent.myRoomUpdate(currentRoomID, readyBox.isSelected());
                    return;
                }
            }
        }
    }
}
