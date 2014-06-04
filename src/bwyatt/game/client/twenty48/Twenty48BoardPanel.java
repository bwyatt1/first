package bwyatt.game.client.twenty48;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

import bwyatt.game.client.*;
import bwyatt.game.common.*;

public class Twenty48BoardPanel extends JPanel
{
    private Twenty48Board twenty48Board;
    private JLabel[][] tileLabel;
    private JPanel movePanel;
    private JLabel playerLabel;
    private JLabel scoreLabel;
    private JLabel timerLabel;
    private PlayerInfo player;
    private Twenty48AnimationTask prevAnimationTask;
    private int panelSize;
    private int highTile;

    public static final int SIZE_NORMAL = 0;
    public static final int SIZE_THUMB = 1;

    public Twenty48BoardPanel(PlayerInfo player)
    {
        prevAnimationTask = null;
        this.panelSize = SIZE_NORMAL;
        this.player = player;
        this.highTile = -1;

        twenty48Board = new Twenty48Board();
        JLayeredPane layeredPane = new JLayeredPane();
        JLabel background = new JLabel(ImageCache.getTwenty48Board());
        JPanel tilePanel = new JPanel();
        tilePanel.setLayout(new GridLayout(4, 4, 5, 5));
        tileLabel = new JLabel[4][];
        for (int row = 0; row < 4; ++row)
        {
            tileLabel[row] = new JLabel[4];
            for (int col = 0; col < 4; ++col)
            {
                tileLabel[row][col] = new JLabel(ImageCache.getTwenty48Blank());
                tilePanel.add(tileLabel[row][col]);
            }
        }
        movePanel = new JPanel();
        
        layeredPane.setPreferredSize(background.getPreferredSize());
        background.setSize(background.getPreferredSize());
        background.setLocation(0, 0);
        layeredPane.add(background, new Integer(0));
        tilePanel.setLocation(5, 5);
        tilePanel.setSize(tilePanel.getPreferredSize());
        tilePanel.setOpaque(false);
        layeredPane.add(tilePanel, new Integer(1), 0);
        movePanel.setLocation(5, 5);
        movePanel.setSize(tilePanel.getPreferredSize());
        movePanel.setOpaque(false);
        layeredPane.add(movePanel, new Integer(2), 0);

        playerLabel = new JLabel(player.getName());
        playerLabel.setIcon(ImageCache.getPlayerIcon(player.getIconID()));
        playerLabel.setFont(playerLabel.getFont().deriveFont(Font.ITALIC, 18));
        playerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        playerLabel.setVerticalAlignment(SwingConstants.CENTER);

        scoreLabel = new JLabel("0");
        scoreLabel.setFont(scoreLabel.getFont().deriveFont(Font.BOLD, 24));
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);

        timerLabel = new JLabel("Timer");
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timerLabel.setForeground(new Color(196, 0, 0, 0));
        timerLabel.setFont(timerLabel.getFont().deriveFont(Font.BOLD, 48));

        this.setBorder(new EtchedBorder());
        
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        this.setLayout(layout);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        this.add(layeredPane, gbc);
        gbc.gridy++;
        gbc.ipady=50;
        this.add(playerLabel, gbc);
        gbc.ipady=25;
        gbc.gridy++;
        this.add(scoreLabel, gbc);
        gbc.ipady = 10;
        gbc.gridy++;
        this.add(timerLabel, gbc);
    }

    public void setPanelSize(int panelSize)
    {
        this.panelSize = panelSize;
    }

    public void newGame()
    {
        twenty48Board.newGame();
        updateTiles();
        updateScore();
    }

    public void updateTiles()
    {
        for (int row = 0; row < 4; ++row)
        {
            for (int col = 0; col < 4; ++col)
            {
                int value = twenty48Board.getTile(row, col);
                if (value == -1)
                {
                    tileLabel[row][col].setIcon(ImageCache.getTwenty48Blank());
                }
                else
                {
                    tileLabel[row][col].setIcon(ImageCache.getTwenty48Tile(value));
                }
            }
        }
    }

    public void move(int dir)
    {
        animateMoves(twenty48Board.move(dir));
        updateScore();
    }

    public void moveUpdate(Twenty48Board board, LinkedList<TileMove> moves)
    {
        this.twenty48Board.setBoard(board);
        animateMoves(moves);
        updateScore();
    }

    public void updateScore()
    {
        scoreLabel.setText("" + twenty48Board.getScore());
        if (highTile != twenty48Board.getHighTile())
        {
            highTile = twenty48Board.getHighTile();
            scoreLabel.setIcon(ImageCache.getTwenty48TileThumb(highTile));
        }
    }

    public void updateTimerLabel(int secondsLeft)
    {
        String text = "" + (secondsLeft / 60) + ":" + String.format("%02d", (secondsLeft % 60));
        timerLabel.setForeground(null);
        timerLabel.setText(text);
    }

    public boolean isGridLocked()
    {
        return twenty48Board.isGridLocked();
    }

    public void gameOver()
    {
        timerLabel.setText("GAME OVER!");
    }

    public void updatePlayerStyle(PlayerInfo player)
    {
        playerLabel.setIcon(ImageCache.getPlayerIcon(player.getIconID()));
        playerLabel.setText(player.getName());
    }

    public void animateMoves(LinkedList<TileMove> moves)
    {

        // movePanel is intialized, update the blank tiles underneath it and queue the soundFX
        LinkedList<Integer> playPop = new LinkedList<Integer>();
        for (TileMove move : moves)
        {
            tileLabel[move.getRow()][move.getCol()].setIcon(ImageCache.getTwenty48Blank());
            int dir = move.getDir();
            int val = move.getVal();
            int row = move.getRow();
            int col = move.getCol();
            int dist = move.getDist();
            if ((dir == TileMove.LEFT && val != twenty48Board.getTile(row, col-dist)) ||
                (dir == TileMove.RIGHT && val != twenty48Board.getTile(row, col+dist)) || 
                (dir == TileMove.UP && val != twenty48Board.getTile(row-dist, col)) ||
                (dir == TileMove.DOWN && val != twenty48Board.getTile(row+dist, col)))
            {
                playPop.add(new Integer(val));
            }
        }

        Twenty48AnimationTask task = new Twenty48AnimationTask(moves, movePanel, this);
        task.setPlayPop(playPop);

        if (prevAnimationTask == null || prevAnimationTask.isFinished())
        {
            new javax.swing.Timer(Twenty48AnimationTask.FRAME_DELAY, task).start();
        }
        else
        {
            prevAnimationTask.setNextTask(task);
        }
        prevAnimationTask = task;
    }
}
