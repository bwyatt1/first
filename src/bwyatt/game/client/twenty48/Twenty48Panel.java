package bwyatt.game.client.twenty48;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import bwyatt.game.client.*;
import bwyatt.game.common.*;

public class Twenty48Panel extends JPanel
{
    private GameFrame parent;
    private Twenty48BoardPanel myBoardPanel;
    private boolean gameOn;

    public Twenty48Panel()
    {
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

    public JMenu createMenu(GameFrame gameFrame)
    {
        this.parent = gameFrame;
        JMenu twenty48Menu = new JMenu("2048");
        twenty48Menu.setMnemonic('2');
        JMenuItem newItem = new JMenuItem("New");
        newItem.setMnemonic('N');
        newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        newItem.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    showStartupPanel();
                }
            }
        );
        JMenuItem closeItem = new JMenuItem("Close");
        closeItem.setMnemonic('C');
        closeItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK));
        closeItem.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    parent.close2048();
                }
            }
        );
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setMnemonic('X');
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        exitItem.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    parent.close();
                }
            }
        );
        twenty48Menu.add(newItem);
        twenty48Menu.add(closeItem);
        twenty48Menu.add(exitItem);
        return twenty48Menu;
    }

    public void showStartupPanel()
    {
        this.gameOn = false;
        this.myBoardPanel = null;
        this.removeAll();
        this.setLayout(new GridLayout(1, 2));

        JLabel soloLabel = new JLabel("Solo");
        soloLabel.setIcon(ImageCache.getTwenty48Tile(10));
        soloLabel.addMouseListener(new MouseAdapter()
            {
                public void mouseClicked(MouseEvent e)
                {
                    newSoloGame();
                }
            }
        );

        JPanel multiPanel = new JPanel();
        JLabel multiLabel = new JLabel("Multiplayer");
        multiPanel.add(multiLabel);

        this.add(soloLabel);
        this.add(multiPanel);

        this.revalidate();
        this.repaint();
    }

    public void move(int dir)
    {
        if (gameOn)
            this.myBoardPanel.move(dir);
        if (this.myBoardPanel.isGridLocked())
        {
            System.out.println("GridLocked");
            gameOn = false;
        }
    }


    public void newSoloGame()
    {
        this.removeAll();
        this.setLayout(new GridLayout(1, 1));
        myBoardPanel = new Twenty48BoardPanel();
        this.add(myBoardPanel);

        this.gameOn = true;
        myBoardPanel.newGame();

        this.revalidate();
        this.repaint();
    }
}
