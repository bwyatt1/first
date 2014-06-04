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
    private JRadioButton[] multiRadio;
    private JCheckBox readyBox;
    private JTextArea multiPlayersPane;
    private JButton multiStartButton;
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

        JLabel soloLabel = new JLabel("Solo");
        soloLabel.setIcon(ImageCache.getTwenty48Tile(10));
        soloLabel.setHorizontalAlignment(SwingConstants.CENTER);
        soloLabel.addMouseListener(new MouseAdapter()
            {
                public void mouseClicked(MouseEvent e)
                {
                    startSoloGame();
                }
            }
        );

        multiRoom = new Room[3];
        multiRoom[0] = Room.ONE_MINUTE;
        multiRoom[1] = Room.THREE_MINUTES;
        multiRoom[2] = Room.TEN_MINUTES;

        JPanel multiPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        multiRadio = new JRadioButton[3];
        multiRadio[0] = new JRadioButton("1:00");
        multiRadio[1] = new JRadioButton("3:00");
        multiRadio[2] = new JRadioButton("10:00");
        ButtonGroup buttonGroup = new ButtonGroup();
        for (int i = 0; i < 3; ++i)
        {
            if (currentRoomID == multiRoom[i].getID())
                multiRadio[i].setSelected(true);
            multiRadio[i].addActionListener(this);
            buttonGroup.add(multiRadio[i]);
            buttonPanel.add(multiRadio[i]);
        }

        multiPlayersPane = new JTextArea();
        updateMultiTextArea();
        multiPlayersPane.setEditable(false);
        readyBox = new JCheckBox("Ready");
        readyBox.addActionListener(this);
        multiStartButton = new JButton("Start Multiplayer");
        multiStartButton.addActionListener(this);

        BoxLayout multiLayout = new BoxLayout(multiPanel, BoxLayout.Y_AXIS);
        multiPanel.setLayout(multiLayout);
        multiPanel.add(buttonPanel);
        multiPanel.add(multiPlayersPane);
        multiPanel.add(readyBox);
        multiPanel.add(multiStartButton);

        this.setLayout(new GridLayout(1, 2));
        this.add(soloLabel);
        this.add(multiPanel);

        this.revalidate();
        this.repaint();

        parent.myRoomUpdate(currentRoomID, readyBox.isSelected());
    }

    public void updateMultiTextArea()
    {
        System.out.println("Multi text");
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

    public void newMultiRequest()
    {
        parent.newMultiRequest();
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
        else if (e.getSource() == multiStartButton)
        {
            newMultiRequest();
        }
        else
        {
            for (int i = 0; i < multiRoom.length; ++i)
            {
                if (e.getSource() == multiRadio[i] && currentRoomID != multiRoom[i].getID())
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
