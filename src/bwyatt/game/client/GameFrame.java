package bwyatt.game.client;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import bwyatt.game.client.boggle.*;
import bwyatt.game.client.twenty48.*;
import bwyatt.game.common.*;

/*
 * Primary GUI class for the client
 */
public class GameFrame extends JFrame implements ActionListener, MouseListener
{
    private JPanel gamesPanel;
    private BogglePanel bogglePanel;
    private Twenty48Panel twenty48Panel;
    private GameSocketThread socketThread;
    private JPanel activePanel;
    private PlayerPanel playerPanel;
    private ArrayList<PlayerInfo> players;
    private PlayerInfo myInfo;
    private ChatBoxPanel chatBoxPanel;
    private JTextField chatInput;
    private JLabel boggleLabel;
    private JLabel twenty48Label;
    private JLabel backLabel;
    private JLabel homeLabel;
    private JLabel settingsLabel;
    private Config config;
    private PreferencesPane preferencesPane;
    private JSplitPane splitPane;

    private final static String CONFIG_FILENAME = "boggle.ini";

    public GameFrame()
    {
        players = new ArrayList<PlayerInfo>();
        PlayerInfo serverInfo = new PlayerInfo("Server", -1);
        players.add(serverInfo);

        ImageCache.init();

        preferencesPane = new PreferencesPane(this);

        activePanel = new JPanel();
        playerPanel = new PlayerPanel();
        chatBoxPanel = new ChatBoxPanel();
        chatInput = new JTextField();
        chatInput.addActionListener(this);

        backLabel = new JLabel(ImageCache.getSmallLetter('B'));
        backLabel.addMouseListener(this);
        homeLabel = new JLabel(ImageCache.getSmallLetter('H'));
        homeLabel.setHorizontalAlignment(SwingConstants.LEFT);
        homeLabel.addMouseListener(this);
        settingsLabel = new JLabel(ImageCache.getSmallLetter('S'));
        settingsLabel.addMouseListener(this);

        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BorderLayout());
        navPanel.add(backLabel, BorderLayout.WEST);
        navPanel.add(homeLabel, BorderLayout.CENTER);
        navPanel.add(settingsLabel, BorderLayout.EAST);

        JPanel chatPanel = new JPanel();
        SpringLayout chatLayout = new SpringLayout();
        chatPanel.setLayout(chatLayout);
        chatPanel.add(navPanel);
        chatLayout.putConstraint(SpringLayout.NORTH, navPanel, 5, SpringLayout.NORTH, chatPanel);
        chatLayout.putConstraint(SpringLayout.WEST, navPanel, 5, SpringLayout.WEST, chatPanel);
        chatLayout.putConstraint(SpringLayout.EAST, navPanel, -5, SpringLayout.EAST, chatPanel);
        chatPanel.add(playerPanel);
        chatLayout.putConstraint(SpringLayout.NORTH, playerPanel, 5, SpringLayout.SOUTH, navPanel);
        chatLayout.putConstraint(SpringLayout.WEST, playerPanel, 5, SpringLayout.WEST, chatPanel);
        chatLayout.putConstraint(SpringLayout.EAST, playerPanel, -5, SpringLayout.EAST, chatPanel);
        chatPanel.add(chatBoxPanel);
        chatLayout.putConstraint(SpringLayout.NORTH, chatBoxPanel, 5, SpringLayout.SOUTH, playerPanel);
        chatLayout.putConstraint(SpringLayout.WEST, chatBoxPanel, 5, SpringLayout.WEST, chatPanel);
        chatLayout.putConstraint(SpringLayout.EAST, chatBoxPanel, -5, SpringLayout.EAST, chatPanel);
        chatPanel.add(chatInput);
        chatLayout.putConstraint(SpringLayout.SOUTH, chatBoxPanel, -5, SpringLayout.NORTH, chatInput);
        chatLayout.putConstraint(SpringLayout.WEST, chatInput, 5, SpringLayout.WEST, chatPanel);
        chatLayout.putConstraint(SpringLayout.EAST, chatInput, -5, SpringLayout.EAST, chatPanel);
        chatLayout.putConstraint(SpringLayout.SOUTH, chatInput, -5, SpringLayout.SOUTH, chatPanel);
        chatLayout.getConstraints(chatInput).setHeight(Spring.constant(chatInput.getPreferredSize().height));

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(activePanel);
        splitPane.setRightComponent(chatPanel);
        //splitPane.setResizeWeight(0.5d);
        this.getContentPane().add(splitPane);

        config = new Config(CONFIG_FILENAME);

        AudioUtil.init();

        if (config.getMusicMuted())
        {
            AudioUtil.setMusicVolume(0);
        }
        else
        {
            AudioUtil.setMusicVolume(config.getMusicVolume());
        }

        if (config.getSoundsMuted())
        {
            AudioUtil.setSoundsVolume(0);
        }
        else
        {
            AudioUtil.setSoundsVolume(config.getSoundsVolume());
        }
        AudioUtil.playBoggleSong();

        Dimension windowSize = config.getWindowSize();
        if (windowSize == null)
        {
            windowSize = new Dimension(800, 600);
            config.setWindowSize(windowSize);
        }
        this.setSize(windowSize);
        Point windowLoc = config.getWindowLoc();
        if (windowLoc == null)
        {
            windowLoc = new Point(300, 300);
            config.setWindowLoc(windowLoc);
        }
        this.setLocation(windowLoc);

        Dimension prefWindowSize = config.getPrefWindowSize();
        if (prefWindowSize == null)
        {
            prefWindowSize = new Dimension(400, 240);
            config.setPrefWindowSize(prefWindowSize);
        }
        preferencesPane.setSize(prefWindowSize);
        Point prefWindowLoc = config.getPrefWindowLoc();
        if (prefWindowLoc == null)
        {
            prefWindowLoc = new Point(windowLoc.x+100, windowLoc.y+100);
            config.setPrefWindowLoc(prefWindowLoc);
        }
        preferencesPane.setLocation(prefWindowLoc);

        int gamePanelWidth = config.getGamePanelWidth();
        activePanel.setPreferredSize(new Dimension(gamePanelWidth, Integer.MAX_VALUE));

        this.setVisible(true);

        String name = config.getName();
        if (name == null)
        {
            name = JOptionPane.showInputDialog(this, "Login Name: ");
            if (name == null)
                name = "Guest";
            config.setName(name);
        }
        myInfo = new PlayerInfo(name, 0);
        myInfo.setIconID(config.getPlayerIconID());
        playerPanel.setMe(myInfo);

        String serverHostName = config.getServer();
        if (serverHostName == null)
        {
            serverHostName = "shoeboxer.no-ip.biz";
            config.setServer(serverHostName);
        }
        socketThread = new GameSocketThread(this, serverHostName);
        socketThread.start();

        preferencesPane.init(config);

        showGames();

        setupKeys();
    }

    public void showGames()
    {
        this.setTitle("Bill's Games");

        gamesPanel = new JPanel();
        boggleLabel = new JLabel();
        boggleLabel.setIcon(ImageCache.getLargeLetter('B'));
        boggleLabel.setText("Boggle");
        boggleLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        boggleLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
        boggleLabel.addMouseListener(this);
        
        twenty48Label = new JLabel();
        twenty48Label.setIcon(ImageCache.getTwenty48Tile(10));
        twenty48Label.setText("2048");
        twenty48Label.setHorizontalTextPosition(SwingConstants.CENTER);
        twenty48Label.setVerticalTextPosition(SwingConstants.BOTTOM);
        twenty48Label.addMouseListener(this);

        gamesPanel.setLayout(new FlowLayout());
        boggleLabel.setSize(boggleLabel.getPreferredSize());
        twenty48Label.setSize(twenty48Label.getPreferredSize());
        gamesPanel.add(boggleLabel);
        gamesPanel.add(twenty48Label);

        activePanel.removeAll();
        activePanel.setLayout(new GridLayout(1, 1));
        activePanel.add(gamesPanel); 

        this.revalidate();
        this.repaint();
    }

    public void setupKeys()
    {
        activePanel.setFocusable(true);
        InputMap inputMap = activePanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap actionMap = activePanel.getActionMap();
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, 0), "slash");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK), "ctrl-q");
        actionMap.put("slash", new AbstractAction()
            {
                public void actionPerformed(ActionEvent e)
                {
                    chatInput.requestFocusInWindow();
                }
            }
        );
        actionMap.put("ctrl-q", new AbstractAction()
            {
                public void actionPerformed(ActionEvent e)
                {
                    close();
                }
            }
        );
        activePanel.requestFocusInWindow();
    }

    /*
     * Application close - write config file and exit
     */
    public void close()
    {
        saveConfig();
        System.exit(0);
    }

    public void saveConfig()
    {
        config.setServer(socketThread.getServer());
        config.setWindowSize(this.getSize());
        config.setWindowLoc(this.getLocation());
        config.setPrefWindowSize(preferencesPane.getSize());
        config.setPrefWindowLoc(preferencesPane.getLocation());
        config.setGamePanelWidth(activePanel.getWidth());
        config.setName(myInfo.getName());
        config.setPlayerIconID(myInfo.getIconID());
        config.setMusicVolume(preferencesPane.getMusicVolume());
        config.setMusicMuted(preferencesPane.getMusicMuted());
        config.setSoundsVolume(preferencesPane.getSoundsVolume());
        config.setSoundsMuted(preferencesPane.getSoundsMuted());
        config.writeFile(CONFIG_FILENAME);
    }

    /*
     * Called from the Preferences Pane if a GUI setting was changed
     */
    public void preferencesChanged()
    {
        if (preferencesPane.getMusicMuted())
        {
            AudioUtil.setMusicVolume(0);
        }
        else if (preferencesPane.getMusicVolume() != AudioUtil.getMusicVolume())
        {
           AudioUtil.setMusicVolume(preferencesPane.getMusicVolume());
        }

        if (preferencesPane.getSoundsMuted())
        {
            AudioUtil.setSoundsVolume(0);
        }
        else if (preferencesPane.getSoundsVolume() != AudioUtil.getSoundsVolume())
        {
           AudioUtil.setSoundsVolume(preferencesPane.getSoundsVolume());
        }

        if (!preferencesPane.getServer().equals(socketThread.getServer()))
        {
            socketThread.setServer(preferencesPane.getServer());
        }
        if (!preferencesPane.getName().equals(myInfo.getName()) ||
            preferencesPane.getPlayerIconID() != myInfo.getIconID())
        {
            myInfo.setName(preferencesPane.getName());
            myInfo.setIconID(preferencesPane.getPlayerIconID());
            playerPanel.updatePlayer(myInfo);
            chatBoxPanel.updatePlayerStyle(myInfo);
            if (twenty48Panel != null)
                twenty48Panel.updatePlayerStyle(myInfo);

            Message message = new Message();
            message.setType(Message.MT_PLAYER_INFO_CHANGE);
            message.setPlayerInfo(myInfo);
            socketThread.sendMessage(message);
        }
    }

    /*
     * Called from the Preferences Pane when it is closed
     */
    public void preferencesHidden()
    {
        saveConfig();
    }

    public void showPreferences()
    {
        preferencesPane.setVisible(true);
    }

    public void navHome()
    {
        if (gamesPanel != null)
        {
            return;
        }
        else if (twenty48Panel != null)
        {
            twenty48Panel.removeAll();
            twenty48Panel = null;
            showGames();
        }
        else if (bogglePanel != null)
        {
            bogglePanel.removeAll();
            bogglePanel = null;
            showGames();
        }

        Message message = new Message();
        message.setType(Message.MT_PLAYER_SHOWING_GAME);
        message.setFromID(myInfo.getID());
        message.setVal(PlayerInfo.GAME_NONE);
        socketThread.sendMessage(message);
    }

    public void navBack()
    {
        if (twenty48Panel != null)
        {
            twenty48Panel.navBack();
        }
        else if (bogglePanel != null)
        {
            bogglePanel.navBack();
        }
    }

    /*
     * load the boggle panel and inform the server
     */
    public void startBoggle()
    {
        activePanel.removeAll();
        gamesPanel = null;

        this.bogglePanel = new BogglePanel();
        bogglePanel.newGame();
        activePanel.setLayout(new GridLayout(1, 1));
        activePanel.add(bogglePanel); 
        this.revalidate();
        this.repaint();
        bogglePanel.requestFocusInWindow();

        Message message = new Message();
        message.setType(Message.MT_PLAYER_SHOWING_GAME);
        message.setFromID(myInfo.getID());
        message.setVal(PlayerInfo.GAME_BOGGLE);
        socketThread.sendMessage(message);
    }

    /*
     * load the 2048 panel and inform the server
     */
    public void start2048()
    {
        activePanel.removeAll();
        gamesPanel = null;

        twenty48Panel = new Twenty48Panel(myInfo, players, this);
        activePanel.removeAll();
        activePanel.setLayout(new GridLayout(1, 1));
        activePanel.add(twenty48Panel);
        this.revalidate();
        this.repaint();
        twenty48Panel.requestFocusInWindow();

        Message message = new Message();
        message.setType(Message.MT_PLAYER_SHOWING_GAME);
        message.setFromID(myInfo.getID());
        message.setVal(PlayerInfo.GAME_2048);
        socketThread.sendMessage(message);
    }

    /*
     * called from the socket thread if connect/disconnect
     */
    public void setServerStatus(boolean status)
    {
        playerPanel.setServerStatus(status);
        if (status)
        {
            Message message = new Message();
            message.setType(Message.MT_JOIN_CHAT);
            message.setFromID(myInfo.getID());
            message.setPlayerInfo(myInfo);
            socketThread.sendMessage(message);
        }
    }

    /*
     * called from a game multiplayer panel
     */
    public void myRoomUpdate(int roomID, boolean readyStatus)
    {
        // TODO: wait for server response before updating info and GUI
        myInfo.setRoomID(roomID);
        if (readyStatus)
            myInfo.setStatus(PlayerInfo.STATUS_READY);
        else
            myInfo.setStatus(PlayerInfo.STATUS_NONE);

        Message message = new Message();
        message.setType(Message.MT_PLAYER_ROOM_CHANGE);
        message.setFromID(myInfo.getID());
        message.setPlayerInfo(myInfo);
        socketThread.sendMessage(message);
    }

    public void newMultiRequest()
    {
        Message message = new Message();
        message.setType(Message.MT_2048_NEW_MULTI);
        message.setFromID(myInfo.getID());
        message.setVal(-1);
        socketThread.sendMessage(message);
    }

    public void twenty48MoveRequest(int dir)
    {
        Message message = new Message();
        message.setType(Message.MT_2048_MOVE);
        message.setFromID(myInfo.getID());
        message.setVal(dir);
        socketThread.sendMessage(message);
    }

    public void gameOverRequest()
    {
        Message message = new Message();
        message.setType(Message.MT_GAME_INSTANCE_OVER);
        message.setFromID(myInfo.getID());
        socketThread.sendMessage(message);
    }

    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == chatInput)
        {
            String chatText = chatInput.getText();
            if (!chatText.equals(""))
            {
                Message message = new Message(Message.MT_CHAT, myInfo.getID(), chatText);
                socketThread.sendMessage(message);
                chatInput.setText("");
            }
            if (this.twenty48Panel != null)
                twenty48Panel.requestFocusInWindow();
            else if (this.bogglePanel != null)
                bogglePanel.requestFocusInWindow();
            else
                activePanel.requestFocusInWindow();
        }
    }

    public void mouseClicked(MouseEvent e)
    {
        if (e.getSource() == boggleLabel)
        {
            startBoggle();
        }
        else if (e.getSource() == twenty48Label)
        {
            start2048();
        }
        else if (e.getSource() == backLabel)
        {
            navBack();
        }
        else if (e.getSource() == homeLabel)
        {
            navHome();
        }
        else if (e.getSource() == settingsLabel)
        {
            showPreferences();
        }
    }

    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

    /*
     * Primary event handling for messages from the server
     */
    public void handleMessage(Message message)
    {
        PlayerInfo fromPlayer = getPlayerInfo(message.getFromID());
        switch (message.getType())
        {
            case Message.MT_ID_UPDATE:
                System.out.println("MT_ID_UPDATE: " + message.getVal());
                myInfo.setID(message.getVal());
                chatBoxPanel.updatePlayerStyle(myInfo);
                break;
            case Message.MT_NEW_PLAYER:
            case Message.MT_PLAYER_LIST:
                System.out.println("MT_PLAYER: " + message.getPlayerInfo().toString());
                if (message.getFromID() != myInfo.getID())
                {
                    PlayerInfo info = new PlayerInfo(message.getPlayerInfo());
                    info.setID(message.getFromID());
                    players.add(info);
                    playerPanel.addPlayer(info);
                    chatBoxPanel.updatePlayerStyle(info);
                    if (twenty48Panel != null)
                        twenty48Panel.updatePlayerRoom(info);
                }
                break;
            case Message.MT_PLAYER_CLOSED:
                System.out.println("MT_PLAYER_CLOSED: " + fromPlayer.toString());
                players.remove(fromPlayer);
                playerPanel.removePlayer(fromPlayer);
                if (twenty48Panel != null)
                    twenty48Panel.updatePlayerRoom(null);
                break;
            case Message.MT_PLAYER_INFO_CHANGE:
                System.out.println("MT_PLAYER_INFO_CHANGE: " + message.getPlayerInfo().toString());
                fromPlayer.setName(message.getPlayerInfo().getName());
                fromPlayer.setIconID(message.getPlayerInfo().getIconID());
                playerPanel.updatePlayer(fromPlayer);
                chatBoxPanel.updatePlayerStyle(fromPlayer);
                if (twenty48Panel != null)
                {
                    twenty48Panel.updatePlayerStyle(fromPlayer);
                }
                break;
            case Message.MT_PLAYER_ROOM_CHANGE:
                System.out.println("MT_PLAYER_ROOM_CHANGE: " + fromPlayer.toString());
                fromPlayer.setRoomID(message.getPlayerInfo().getRoomID());
                fromPlayer.setStatus(message.getPlayerInfo().getStatus());
                if (twenty48Panel != null)
                    twenty48Panel.updatePlayerRoom(null);
                break;
            case Message.MT_GAME_INSTANCE_JOIN:
                System.out.println("MT_GAME_INSTANCE_JOIN: " + fromPlayer.toString());
                if (twenty48Panel != null)
                {
                    twenty48Panel.addInstancePlayer(fromPlayer);
                }
                break;
            case Message.MT_GAME_INSTANCE_LEAVE:
                System.out.println("MT_GAME_INSTANCE_LEAVE: " + fromPlayer.toString());
                if (twenty48Panel != null)
                {
                    twenty48Panel.removeInstancePlayer(fromPlayer);
                }
                break;
            case Message.MT_GAME_INSTANCE_OVER:
                System.out.println("MT_GAME_INSTANCE_OVER: " + fromPlayer.toString());
                if (twenty48Panel != null)
                {
                    twenty48Panel.gameOver(fromPlayer);
                }
                break;
            case Message.MT_GAME_INSTANCE_START_TIMER:
                System.out.println("MT_GAME_INSTANCE_START_TIMER: " + message.getVal());
                if (twenty48Panel != null)
                {
                    twenty48Panel.startTimer(message.getVal());
                }
                break;
            case Message.MT_2048_NEW_MULTI:
                System.out.println("MT_2048_NEW_MULTI: " + message.getVal());
                if (twenty48Panel != null)
                {
                    twenty48Panel.startMultiGame();
                }
                break;
            case Message.MT_2048_BOARD_UPDATE:
                System.out.println("MT_2048_BOARD_UPDATE");
                if (twenty48Panel != null)
                {
                    twenty48Panel.updateBoard(fromPlayer, message.getTwenty48Board(), message.getTwenty48Moves());
                }
                break;
            case Message.MT_CHAT:
                System.out.println("MT_CHAT (" + fromPlayer.toString() + "): " + message.getText());
                chatBoxPanel.addChat(fromPlayer, message.getText());
                break;
            case Message.MT_PLAYER_SHOWING_GAME:
                System.out.println("MT_PLAYER_SHOWING_GAME (" + fromPlayer.toString() + "): " + message.getVal());
                fromPlayer.setShowing(message.getVal());
                playerPanel.updatePlayer(fromPlayer);
                if (twenty48Panel != null)
                {
                    twenty48Panel.updatePlayerStyle(fromPlayer);
                }
                break;
            default:
                System.out.println("handleMessage: Unknown type: " + message.getType());
        }
    }

    public PlayerInfo getPlayerInfo(int id)
    {
        if (id == myInfo.getID())
            return myInfo;
        for (PlayerInfo info : this.players)
        {
            if (info.getID() == id)
                return info;
        }
        return null;
    }

    public static void main(String[] args)
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        try
        {
            final GameFrame app = new GameFrame();
            app.addWindowListener(new WindowAdapter()
                {
                    public void windowClosing(WindowEvent e)
                    {
                        app.close();
                    }
                }
            );
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
