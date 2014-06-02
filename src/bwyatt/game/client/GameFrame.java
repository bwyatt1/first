package bwyatt.game.client;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import bwyatt.game.client.boggle.*;
import bwyatt.game.client.twenty48.*;
import bwyatt.game.common.*;

public class GameFrame extends JFrame
{
    private BogglePanel bogglePanel;
    private Twenty48Panel twenty48Panel;
    private GameSocketThread socketThread;
    private JPanel activePanel;
    private PlayerPanel playerPanel;
    private ArrayList<PlayerInfo> players;
    private PlayerInfo myInfo;
    private ChatBoxPanel chatBoxPanel;
    private JTextField chatInput;
    private Config config;
    private JMenu editMenu;
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
        chatInput.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    sendChat();
                }
            }
        );

        JPanel chatPanel = new JPanel();
        SpringLayout chatLayout = new SpringLayout();
        chatPanel.setLayout(chatLayout);
        chatPanel.add(playerPanel);
        chatLayout.putConstraint(SpringLayout.NORTH, playerPanel, 5, SpringLayout.NORTH, chatPanel);
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
        JLabel boggleLabel = new JLabel();
        boggleLabel.setIcon(ImageCache.getLargeLetter('B'));
        boggleLabel.setText("Boggle");
        boggleLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        boggleLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
        boggleLabel.addMouseListener(new MouseAdapter()
            {
                public void mouseClicked(MouseEvent e)
                {
                    startBoggle();
                }
            }
        );
        
        JLabel twenty48Label = new JLabel();
        twenty48Label.setIcon(ImageCache.getTwenty48Tile(10));
        twenty48Label.setText("2048");
        twenty48Label.setHorizontalTextPosition(SwingConstants.CENTER);
        twenty48Label.setVerticalTextPosition(SwingConstants.BOTTOM);
        twenty48Label.addMouseListener(new MouseAdapter()
            {
                public void mouseClicked(MouseEvent e)
                {
                    start2048();
                }
            }
        );

        activePanel.setLayout(new FlowLayout());
        boggleLabel.setSize(boggleLabel.getPreferredSize());
        twenty48Label.setSize(twenty48Label.getPreferredSize());
        activePanel.add(boggleLabel);
        activePanel.add(twenty48Label);

        JMenu gameMenu = new JMenu("Game");
        gameMenu.setMnemonic('G');
        JMenuItem gameBoggleItem = new JMenuItem("Boggle");
        gameBoggleItem.setMnemonic('B');
        gameBoggleItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK));
        gameBoggleItem.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    startBoggle();
                }
            }
        );

        JMenuItem game2048Item = new JMenuItem("2048");
        game2048Item.setMnemonic('2');
        game2048Item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, InputEvent.CTRL_DOWN_MASK));
        game2048Item.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    start2048();
                }
            }
        );

        JMenuItem gameExitItem = new JMenuItem("Exit");
        gameExitItem.setMnemonic('X');
        gameExitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        gameExitItem.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    close();
                }
            }
        );

        this.editMenu = new JMenu("Edit");
        editMenu.setMnemonic('E');
        JMenuItem editPrefItem = new JMenuItem("Preferences");
        editPrefItem.setMnemonic('P');
        editPrefItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, InputEvent.CTRL_DOWN_MASK));
        editPrefItem.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    showPreferences();
                }
            }
        );

        JMenuBar jmenubar = new JMenuBar();
        gameMenu.add(gameBoggleItem);
        gameMenu.add(game2048Item);
        gameMenu.add(gameExitItem);
        jmenubar.add(gameMenu);
        editMenu.add(editPrefItem);
        jmenubar.add(editMenu);
        this.setJMenuBar(jmenubar);

        this.revalidate();
        this.repaint();
    }

    public void setupKeys()
    {
        activePanel.setFocusable(true);
        InputMap inputMap = activePanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap actionMap = activePanel.getActionMap();
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, 0), "slash");
        actionMap.put("slash", new AbstractAction()
            {
                public void actionPerformed(ActionEvent e)
                {
                    chatInput.requestFocusInWindow();
                }
            }
        );
        activePanel.requestFocusInWindow();
    }

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

            Message message = new Message();
            message.setType(Message.MT_PLAYER_INFO_CHANGE);
            message.setPlayerInfo(myInfo);
            socketThread.sendMessage(message);
        }
    }

    public void preferencesHidden()
    {
        saveConfig();
    }

    public void showPreferences()
    {
        preferencesPane.setVisible(true);
    }

    public void startBoggle()
    {
        this.bogglePanel = new BogglePanel();
        JMenuBar jmenubar = new JMenuBar();
        jmenubar.add(bogglePanel.createMenu(this));
        jmenubar.add(editMenu);
        this.setJMenuBar(jmenubar);
        bogglePanel.newGame();
        activePanel.removeAll();
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

    public void closeBoggle()
    {
        this.activePanel.removeAll();
        this.bogglePanel = null;
        this.showGames();
        
        Message message = new Message();
        message.setType(Message.MT_PLAYER_SHOWING_GAME);
        message.setFromID(myInfo.getID());
        message.setVal(PlayerInfo.GAME_NONE);
        socketThread.sendMessage(message);
    }

    public void start2048()
    {
        twenty48Panel = new Twenty48Panel();
        JMenuBar jmenubar = new JMenuBar();
        jmenubar.add(twenty48Panel.createMenu(this));
        jmenubar.add(editMenu);
        this.setJMenuBar(jmenubar);
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

    public void close2048()
    {
        this.activePanel.removeAll();
        this.twenty48Panel = null;
        this.showGames();

        Message message = new Message();
        message.setType(Message.MT_PLAYER_SHOWING_GAME);
        message.setFromID(myInfo.getID());
        message.setVal(PlayerInfo.GAME_NONE);
        socketThread.sendMessage(message);
    }

    public void setServerStatus(boolean status)
    {
        playerPanel.setServerStatus(status);
        if (status)
        {
            Message message = new Message();
            message.setType(Message.MT_JOIN_CHAT);
            message.setPlayerInfo(myInfo);
            socketThread.sendMessage(message);
        }
    }

    public void sendChat()
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
                }
                break;
            case Message.MT_PLAYER_CLOSED:
                System.out.println("MT_PLAYER_CLOSED: " + fromPlayer.toString());
                players.remove(fromPlayer);
                playerPanel.removePlayer(fromPlayer);
                break;
            case Message.MT_PLAYER_INFO_CHANGE:
                System.out.println("MT_PLAYER_INFO_CHANGE: " + message.getPlayerInfo().toString());
                fromPlayer.setName(message.getPlayerInfo().getName());
                fromPlayer.setIconID(message.getPlayerInfo().getIconID());
                playerPanel.updatePlayer(fromPlayer);
                chatBoxPanel.updatePlayerStyle(fromPlayer);
                break;
            case Message.MT_CHAT:
                System.out.println("MT_CHAT (" + fromPlayer.toString() + "): " + message.getText());
                chatBoxPanel.addChat(fromPlayer, message.getText());
                break;
            case Message.MT_PLAYER_SHOWING_GAME:
                System.out.println("MT_PLAYER_SHOWING_GAME (" + fromPlayer.toString() + "): " + message.getVal());
                fromPlayer.setShowing(message.getVal());
                playerPanel.updatePlayer(fromPlayer);
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
