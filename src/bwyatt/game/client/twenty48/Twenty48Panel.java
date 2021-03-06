package bwyatt.game.client.twenty48;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import bwyatt.game.client.*;
import bwyatt.game.common.*;

public class Twenty48Panel extends JPanel implements ActionListener
{
    private GameFrame parent;
    private Twenty48BoardPanel myBoardPanel;
    private HashMap<PlayerInfo, Twenty48BoardPanel> otherBoards;
    private JPanel otherBoardsPanel;
    private int gameOn;
    private JRadioButton[] roomRadio;
    private JRadioButton soloRadio;
    private JRadioButton multiRadio;
    private JCheckBox readyBox;
    private JTextArea multiPlayersPane;
    private JButton startButton;
    private JLabel timerLabel;
    private javax.swing.Timer timer;
    private Room[] multiRoom;
    private int currentRoomID;
    private PlayerInfo myInfo;
    private ArrayList<PlayerInfo> players;
    private long endTime;

    private static final int GAME_NONE = 0;
    private static final int GAME_SOLO = 1;
    private static final int GAME_MULTI = 2;
    private static final int GAME_OVER = 3;

    public Twenty48Panel(PlayerInfo myInfo, ArrayList<PlayerInfo> players, GameFrame parent)
    {
        this.myInfo = myInfo;
        this.players = players;
        this.gameOn = GAME_NONE;
        this.parent = parent;
        this.currentRoomID = Room.THREE_MINUTES.getID();
        this.showStartupPanel();
        this.setupKeys();
    }

    public void setupKeys()
    {
        this.setFocusable(true);
        InputMap inputMap = this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap actionMap = this.getActionMap();
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "up");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "down");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "left");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "right");

        actionMap.put("up", new AbstractAction()
            {
                public void actionPerformed(ActionEvent e)
                {
                    move(TileMove.UP);
                }
            }
        );
        actionMap.put("down", new AbstractAction()
            {
                public void actionPerformed(ActionEvent e)
                {
                    move(TileMove.DOWN);
                }
            }
        );
        actionMap.put("left", new AbstractAction()
            {
                public void actionPerformed(ActionEvent e)
                {
                    move(TileMove.LEFT);
                }
            }
        );
        actionMap.put("right", new AbstractAction()
            {
                public void actionPerformed(ActionEvent e)
                {
                    move(TileMove.RIGHT);
                }
            }
        );

    }

    public void navBack()
    {
        if (gameOn == GAME_NONE)
        {
            parent.navHome();
        }
        else
        {
            if (gameOn != GAME_OVER)
                gameOver();
            showStartupPanel();
        }
    }

    public void showStartupPanel()
    {
        this.gameOn = GAME_NONE;
        this.myBoardPanel = null;
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

    public void startSoloGame()
    {
        this.removeAll();
        this.setLayout(new GridLayout(1, 1));
        myBoardPanel = new Twenty48BoardPanel(myInfo);
        this.add(myBoardPanel);

        this.gameOn = GAME_SOLO;
        myBoardPanel.newGame();

        this.revalidate();
        this.repaint();

        this.requestFocusInWindow();
    }

    /*
     * Called by parent
     */
    public void startMultiGame()
    {
        this.removeAll();

        myBoardPanel = new Twenty48BoardPanel(myInfo);

        JPanel myPanel = new JPanel();
        myPanel.setLayout(new BorderLayout());
        myPanel.add(myBoardPanel, BorderLayout.CENTER);

        otherBoards = new HashMap<PlayerInfo, Twenty48BoardPanel>();
        otherBoardsPanel = new JPanel();

        this.setLayout(new GridLayout(1, 2));
        this.add(myBoardPanel);
        this.add(otherBoardsPanel);

        this.gameOn = GAME_MULTI;

        this.revalidate();
        this.repaint();

        this.requestFocusInWindow();
    }

    public void startTimer(int seconds)
    {
        long now = System.nanoTime() / 1000000L;
        endTime = now + seconds*1000L;
        timer = new javax.swing.Timer(1000, new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    updateTimerLabel();
                }
            }
        );
        updateTimerLabel();
        timer.start();
    }

    public void updateTimerLabel()
    {
        long now = System.nanoTime() / 1000000L;
        if (endTime < now)
        {
            gameOver();
        }
        int secondsLeft = (int)((endTime - now + 999L)/1000L);
        myBoardPanel.updateTimerLabel(secondsLeft);
    }

    public void gameOver()
    {
        gameOn = GAME_OVER;
        if (timer != null)
        {
            timer.stop();
            timer = null;
        }
        parent.gameOverRequest();
        parent.myRoomUpdate(currentRoomID, false);
    }

    public void gameOver(PlayerInfo player)
    {
        if (player == myInfo)
        {
            if (myBoardPanel != null)
                myBoardPanel.gameOver();
        }
        else
        {
            otherBoards.get(player).gameOver();
        }
    }

    public void layoutOtherBoards()
    {
        otherBoardsPanel.removeAll();
        if (otherBoards.size() == 1)
        {
            Twenty48BoardPanel boardPanel = otherBoards.values().iterator().next();
            boardPanel.setPanelSize(Twenty48BoardPanel.SIZE_NORMAL);
            otherBoardsPanel.setLayout(new GridLayout(1, 1));
            otherBoardsPanel.add(otherBoards.values().iterator().next());
        }
        else
        {
            otherBoardsPanel.setLayout(new GridLayout((otherBoards.size()+1)/2, 2));
            // sad attempt to keep some sort of order
            for (PlayerInfo info : this.players)
            {
                if (otherBoards.get(info) != null)
                {
                    Twenty48BoardPanel boardPanel = otherBoards.get(info);
                    boardPanel.setPanelSize(Twenty48BoardPanel.SIZE_THUMB);
                    otherBoardsPanel.add(boardPanel);
                }
            }
        }
        otherBoardsPanel.revalidate();
        otherBoardsPanel.repaint();
    }

    public void updatePlayerRoom(PlayerInfo player)
    {
        if (this.gameOn == GAME_NONE)
            updateMultiTextArea();
    }

    public void updatePlayerStyle(PlayerInfo player)
    {
        if (gameOn == GAME_MULTI || gameOn == GAME_SOLO)
        {
            if (player == myInfo)
            {
                myBoardPanel.updatePlayerStyle(player);
            }
            else
            {
                otherBoards.get(player).updatePlayerStyle(player);
            }
        }
        updateMultiTextArea();
    }

    public void addInstancePlayer(PlayerInfo player)
    {
        if (player != myInfo)
        {
            Twenty48BoardPanel newBoard = new Twenty48BoardPanel(player);
            otherBoards.put(player, newBoard);
            layoutOtherBoards();
        }
    }

    public void removeInstancePlayer(PlayerInfo player)
    {
        otherBoards.remove(player);
        layoutOtherBoards();
    }

    public void updateBoard(PlayerInfo player, Twenty48Board board, LinkedList<TileMove> moves)
    {
        if (player == myInfo)
        {
            myBoardPanel.moveUpdate(board, moves);
        }
        else
        {
            otherBoards.get(player).moveUpdate(board, moves);
        }
    }

    public void move(int dir)
    {
        if (gameOn == GAME_SOLO)
        {
            this.myBoardPanel.move(dir);
        }
        else if (gameOn == GAME_MULTI)
        {
            parent.twenty48MoveRequest(dir);
        }
        if (this.myBoardPanel.isGridLocked())
        {
            myBoardPanel.gameOver();
        }
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
