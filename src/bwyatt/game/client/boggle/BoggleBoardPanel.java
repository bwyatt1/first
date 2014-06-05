package bwyatt.game.client.boggle;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import bwyatt.game.client.*;
import bwyatt.game.common.*;

public class BoggleBoardPanel extends JLayeredPane implements MouseMotionListener, MouseListener
{
    BoggleBoard boggleBoard;
    JLabel[][] boggleLetterLabel;
    private JLabel[][] hiLabel;
    private JLabel[][] hiVLabel;
    private JLabel[][] hiHLabel;
    private JLabel[][] hiD1Label;
    private JLabel[][] hiD2Label;
    private JLabel[][] mouseOverLabel;
    private int mouseOverRow;
    private int mouseOverCol;
    private LinkedList<Point> hiPath;
    private boolean[][] hiMark;

    public BoggleBoardPanel()
    {
        super();
        boggleBoard = new BoggleBoard();
        hiPath = new LinkedList<Point>();
        mouseOverRow = -1;
        mouseOverCol = -1;

        JLabel background = new JLabel(ImageCache.getBoggleBoard());
        Dimension max = background.getPreferredSize();
        background.setSize(max);
        background.setLocation(0, 0);

        JPanel boggleLetterPanel = new JPanel();
        boggleLetterPanel.setLayout(new GridLayout(4, 4, 12, 12));
        boggleLetterPanel.setSize(max.width - 7*2, max.height - 7*2);
        boggleLetterPanel.setLocation(7, 7);
        boggleLetterPanel.setOpaque(false);

        JPanel hiPanel = new JPanel();
        hiPanel.setLayout(new GridLayout(4, 4));
        hiPanel.setSize(max);
        hiPanel.setLocation(0, 0);
        hiPanel.setOpaque(false);

        boggleLetterLabel = new JLabel[4][];
        hiLabel = new JLabel[4][];
        for (int row = 0; row < 4; ++row)
        {
            boggleLetterLabel[row] = new JLabel[4];
            hiLabel[row] = new JLabel[4];
            for (int col = 0; col < 4; ++col)
            {
                boggleLetterLabel[row][col] = new JLabel(ImageCache.getLargeLetter('A'));
                boggleLetterPanel.add(boggleLetterLabel[row][col]);
                hiLabel[row][col] = new JLabel();
                hiPanel.add(hiLabel[row][col]);
            }
        }

        // setup edge highlight
        JPanel hiVPanel = new JPanel();
        hiVPanel.setLayout(new GridLayout(3, 4, 92, 86));
        hiVPanel.setSize(max.width - 50*2, max.height - 98*2);
        hiVPanel.setLocation(50, 98);
        hiVPanel.setOpaque(false);
        hiVLabel = new JLabel[3][];
        for (int row = 0; row < 3; ++row)
        {
            hiVLabel[row] = new JLabel[4];
            for (int col = 0; col < 4; ++col)
            {
                hiVLabel[row][col] = new JLabel(ImageCache.getBoggleHiV());
                hiVLabel[row][col].setVisible(false);
                hiVPanel.add(hiVLabel[row][col]);
            }
        }

        JPanel hiHPanel = new JPanel();
        hiHPanel.setLayout(new GridLayout(4, 3, 86, 92));
        hiHPanel.setSize(max.width - 98*2, max.height - 50*2);
        hiHPanel.setLocation(98, 50);
        hiHPanel.setOpaque(false);
        hiHLabel = new JLabel[4][];
        for (int row = 0; row < 4; ++row)
        {
            hiHLabel[row] = new JLabel[3];
            for (int col = 0; col < 3; ++col)
            {
                hiHLabel[row][col] = new JLabel(ImageCache.getBoggleHiH());
                hiHLabel[row][col].setVisible(false);
                hiHPanel.add(hiHLabel[row][col]);
            }
        }

        JPanel hiD1Panel = new JPanel();
        hiD1Panel.setLayout(new GridLayout(3, 3, 65, 65));
        hiD1Panel.setSize(max.width - 85*2, max.height - 85*2);
        hiD1Panel.setLocation(85, 85);
        hiD1Panel.setOpaque(false);

        JPanel hiD2Panel = new JPanel();
        hiD2Panel.setLayout(new GridLayout(3, 3, 65, 65));
        hiD2Panel.setSize(max.width - 85*2, max.height - 85*2);
        hiD2Panel.setLocation(85, 85);
        hiD2Panel.setOpaque(false);

        hiD1Label = new JLabel[3][];
        hiD2Label = new JLabel[3][];
        for (int row = 0; row < 3; ++row)
        {
            hiD1Label[row] = new JLabel[3];
            hiD2Label[row] = new JLabel[3];
            for (int col = 0; col < 3; ++col)
            {
                hiD1Label[row][col] = new JLabel(ImageCache.getBoggleHiD1());
                hiD2Label[row][col] = new JLabel(ImageCache.getBoggleHiD2());
                hiD1Label[row][col].setVisible(false);
                hiD2Label[row][col].setVisible(false);
                hiD1Panel.add(hiD1Label[row][col]);
                hiD2Panel.add(hiD2Label[row][col]);
            }
        }

        JPanel mouseOverPanel = new JPanel();
        mouseOverPanel.setLayout(new GridLayout(4, 4));
        mouseOverPanel.setSize(max);
        mouseOverPanel.setLocation(0, 0);
        mouseOverPanel.setOpaque(false);
        mouseOverLabel = new JLabel[4][];
        for (int row = 0; row < 4; ++row)
        {
            mouseOverLabel[row] = new JLabel[4];
            for (int col = 0; col < 4; ++col)
            {
                mouseOverLabel[row][col] = new JLabel(ImageCache.getBoggleHi2());
                mouseOverLabel[row][col].setVisible(false);
                mouseOverPanel.add(mouseOverLabel[row][col]);
            }
        }

        this.setPreferredSize(background.getPreferredSize());
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.add(background, new Integer(0));
        this.add(boggleLetterPanel, new Integer(1), 0);
        this.add(hiPanel, new Integer(2), 0);
        this.add(hiVPanel, new Integer(3), 0);
        this.add(hiHPanel, new Integer(4), 0);
        this.add(hiD1Panel, new Integer(5), 0);
        this.add(hiD2Panel, new Integer(6), 0);
        this.add(mouseOverPanel, new Integer(7), 0);
    }

    public void hiWord(String word)
    {
        LinkedList<Point> wordPath = boggleBoard.findPath(word, null);
        Point oldp = null;
        for (Point p : hiPath)
        {
            hiLabel[p.y][p.x].setIcon(null);
            if (oldp != null)
                getEdgeLabel(oldp, p).setVisible(false);
            oldp = p;
        }
        if (wordPath != null)
        {
            hiPath = wordPath;
            oldp = null;
            for (Point p : hiPath)
            {
                hiLabel[p.y][p.x].setIcon(ImageCache.getBoggleHi1());
                if (oldp != null)
                    getEdgeLabel(oldp, p).setVisible(true);
                oldp = p;
            }
        }
        else
        {
            hiPath.clear();
        }
    }

    public JLabel getEdgeLabel(Point p1, Point p2)
    {
        if (p1.y < p2.y)
        {
            if (p1.x < p2.x)
                return hiD1Label[p1.y][p1.x];
            else if (p1.x == p2.x)
                return hiVLabel[p1.y][p1.x];
            return hiD2Label[p1.y][p2.x];
        }
        else if (p1.y == p2.y)
        {
            if (p1.x < p2.x)
                return hiHLabel[p1.y][p1.x];
            return hiHLabel[p1.y][p2.x];
        }
        if (p1.x < p2.x)
            return hiD2Label[p2.y][p1.x];
        else if (p1.x == p2.x)
            return hiVLabel[p2.y][p1.x];
        return hiD1Label[p2.y][p2.x];
    }

    public void hiRemoveLast()
    {
        if (hiPath.size() > 0)
        {
            Point p = hiPath.removeLast();
            hiLabel[p.y][p.x].setIcon(null);
            if (hiPath.size() > 0)
                getEdgeLabel(p, hiPath.getLast()).setVisible(false);
        }
    }

    public void hiClear()
    {
        for (Point p : hiPath)
        {
            hiLabel[p.y][p.x].setIcon(null);
        }
        hiPath.clear();
    }

    public boolean contains(String word)
    {
        return boggleBoard.contains(word);
    }

    public void generateNew()
    {
        boggleBoard.generateNew();
        for (int row = 0; row < 4; ++row)
        {
            for (int col = 0; col < 4; ++col)
            {
                boggleLetterLabel[row][col].setIcon(ImageCache.getLargeLetter(boggleBoard.get(row, col)));
            }
        }
        boggleBoard.calculateWordList();
    }

    public void mouseClicked(MouseEvent e)
    {
        System.out.println("Mouse clicked: " + e.toString());
    }

    public void mouseMoved(MouseEvent e)
    {
        //System.out.println("Mouse Move: " + e.toString());
        int row = (e.getY() - 5) / 106;
        int col = (e.getX() - 5) / 106;
        if ((row != mouseOverRow || col != mouseOverCol) && 
            (row >= 0 && row < 4 && col >= 0 && col < 4))
        {
            if (mouseOverRow >= 0 && mouseOverCol >= 0)
                mouseOverLabel[mouseOverRow][mouseOverCol].setVisible(false);
            mouseOverRow = row;
            mouseOverCol = col;
            mouseOverLabel[row][col].setVisible(true);
        }
    }

    public void mouseExited(MouseEvent e)
    {
        //System.out.println("Mouse exit: " + e.toString());
        if (mouseOverRow >= 0 && mouseOverCol >= 0)
        {
            mouseOverLabel[mouseOverRow][mouseOverCol].setVisible(false);
            mouseOverRow = -1;
            mouseOverCol = -1;
        }
    }

    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {}
}
