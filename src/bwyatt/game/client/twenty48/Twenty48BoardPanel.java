package bwyatt.game.client.twenty48;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import bwyatt.game.client.*;
import bwyatt.game.common.*;

public class Twenty48BoardPanel extends JPanel
{
    private Twenty48Board twenty48Board;
    private JLabel[][] tileLabel;
    private JPanel movePanel;
    private Twenty48AnimationTask prevAnimationTask;

    public Twenty48BoardPanel()
    {
        prevAnimationTask = null;

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

        SpringLayout mainLayout = new SpringLayout();
        this.setLayout(mainLayout);
        this.add(layeredPane);
        mainLayout.putConstraint(SpringLayout.WEST, layeredPane, 5, SpringLayout.WEST, this);
        mainLayout.putConstraint(SpringLayout. NORTH, layeredPane, 5, SpringLayout.NORTH, this);
    }

    public void newGame()
    {
        twenty48Board.newGame();
        updateTiles();
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
    }

    public boolean isGridLocked()
    {
        return twenty48Board.isGridLocked();
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
