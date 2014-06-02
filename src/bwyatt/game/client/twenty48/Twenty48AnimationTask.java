package bwyatt.game.client.twenty48;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import bwyatt.game.client.*;
import bwyatt.game.common.*;

public class Twenty48AnimationTask implements ActionListener
{
    private LinkedList<TileMove> moves;
    private int ticks;
    private JPanel movePanel;
    private JLabel[] tileLabel;
    private int moveDelta;
    private Twenty48BoardPanel parent;
    private LinkedList<Integer> playPop;
    private Twenty48AnimationTask nextTask;
    private boolean finished;

    public static final int FRAMES = 5;
    public static final int FRAME_DELAY = 20;

    public Twenty48AnimationTask(LinkedList<TileMove> moves, JPanel movePanel, Twenty48BoardPanel parent)
    {
        this.parent = parent;
        this.moves = moves;
        this.ticks = 0;
        this.finished = false;
        this.movePanel = movePanel;
        tileLabel = new JLabel[moves.size()];
        int i = 0;
        this.moveDelta = (81 + 5) / FRAMES;
        movePanel.setLayout(null);
        for (TileMove move : moves)
        {
            tileLabel[i] = new JLabel();
            tileLabel[i].setIcon(ImageCache.getTwenty48Tile(move.getVal()));
            tileLabel[i].setSize(tileLabel[i].getPreferredSize());
            movePanel.add(tileLabel[i]);
            int xloc = (81+5) * move.getCol();
            int yloc = (81+5) * move.getRow();
            tileLabel[i].setLocation(xloc, yloc);
            ++i;
        }
        movePanel.revalidate();
        movePanel.repaint();
    }

    public void setPlayPop(LinkedList<Integer> playPop)
    {
        this.playPop = playPop;
    }

    public void setNextTask(Twenty48AnimationTask nextTask)
    {
        this.nextTask = nextTask;
    }

    public boolean isFinished()
    {
        return this.finished;
    }

    public void actionPerformed(ActionEvent e)
    {
        if (this.finished)
            return;
        this.ticks++;
        if (FRAMES - ticks + 1 <= playPop.size())
            AudioUtil.playPop(playPop.remove().intValue());
        if (ticks < FRAMES)
        {
            int i = 0;
            for (TileMove move : this.moves)
            {
                Point loc = tileLabel[i].getLocation();
                if (move.getDir() == TileMove.LEFT)
                    tileLabel[i].setLocation(loc.x - this.moveDelta*move.getDist(), loc.y);
                else if (move.getDir() == TileMove.DOWN)
                    tileLabel[i].setLocation(loc.x, loc.y + this.moveDelta*move.getDist());
                else if (move.getDir() == TileMove.RIGHT)
                    tileLabel[i].setLocation(loc.x + this.moveDelta*move.getDist(), loc.y);
                else if (move.getDir() == TileMove.UP)
                    tileLabel[i].setLocation(loc.x, loc.y - this.moveDelta*move.getDist());
                ++i;
            }
            movePanel.repaint();
        }
        else
        {
            this.finished = true;
            ((javax.swing.Timer)(e.getSource())).stop();
            movePanel.removeAll();
            movePanel.revalidate();
            movePanel.repaint();
            parent.updateTiles();
            if (this.nextTask != null)
            {
                new javax.swing.Timer(FRAME_DELAY, this.nextTask).start();
            }
        }
    }
}
