package bwyatt.game.client.boggle;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import javax.swing.*;

import bwyatt.game.client.*;
import bwyatt.game.common.*;

public class BogglePanel extends JPanel
{
    private BoggleBoard boggleBoard;
    private JPanel boggleBoardPanel;
    private JPanel movePanel;
    private JLabel[][] boggleLetterPanel;
    private SpellingPanel spellingPanel;
    private FoundWordsPanel foundWordsPanel;
    private boolean chatOn;
    private boolean gameOn;
    private JTextField chatInput;
    private GameFrame parent;

    public BogglePanel()
    {
        gameOn = false;
        boggleBoard = new BoggleBoard();
        JLabel background = new JLabel(ImageCache.getBoggleBoard());
        boggleBoardPanel = new JPanel();
        boggleBoardPanel.setLayout(new GridLayout(4, 4, 5, 5));
        boggleLetterPanel = new JLabel[4][];
        for (int row = 0; row < 4; ++row)
        {
            boggleLetterPanel[row] = new JLabel[4];
            for (int col = 0; col < 4; ++col)
            {
                boggleLetterPanel[row][col] = new JLabel(ImageCache.getLargeLetter('A'));
                boggleBoardPanel.add(boggleLetterPanel[row][col]);
            }
        }
        movePanel = new JPanel();

        spellingPanel = new SpellingPanel();
        foundWordsPanel = new FoundWordsPanel();

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(background.getPreferredSize());
        background.setSize(background.getPreferredSize());
        background.setLocation(0, 0);
        layeredPane.add(background, new Integer(0));
        boggleBoardPanel.setSize(boggleBoardPanel.getPreferredSize());
        boggleBoardPanel.setLocation(5, 5);
        boggleBoardPanel.setOpaque(false);
        layeredPane.add(boggleBoardPanel, new Integer(1), 0);
        movePanel.setSize(background.getPreferredSize());
        movePanel.setLocation(0, 0);
        movePanel.setOpaque(false);
        layeredPane.add(movePanel, new Integer(2), 0);

        SpringLayout mainLayout = new SpringLayout();
        this.setLayout(mainLayout);
        this.add(layeredPane);
        mainLayout.putConstraint(SpringLayout.WEST, layeredPane, 5, SpringLayout.WEST, this);
        mainLayout.putConstraint(SpringLayout.NORTH, layeredPane, 5, SpringLayout.NORTH, this);
        this.add(spellingPanel);
        mainLayout.putConstraint(SpringLayout.WEST, spellingPanel, 5, SpringLayout.WEST, this);
        mainLayout.putConstraint(SpringLayout.NORTH, spellingPanel, 5, SpringLayout.SOUTH, layeredPane);
        this.add(foundWordsPanel);
        mainLayout.putConstraint(SpringLayout.WEST, foundWordsPanel, 5, SpringLayout.EAST, layeredPane);
        mainLayout.putConstraint(SpringLayout.NORTH, foundWordsPanel, 5, SpringLayout.NORTH, this);

        this.setupKeys();
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
                        if (gameOn && !chatOn)
                            spellingPanel.addLetter(e.getActionCommand().charAt(0), true);
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
                    if (gameOn && !chatOn)
                        spellingPanel.removeLastLetter();
                }
            }
        );
        actionMap.put("space", new AbstractAction()
            {
                public void actionPerformed(ActionEvent e)
                {
                    if (gameOn && !chatOn)
                        spellingPanel.clear();
                }
            }
        );
        actionMap.put("enter", new AbstractAction()
            {
                public void actionPerformed(ActionEvent e)
                {
                    if (gameOn && !chatOn)
                    {
                        String word = spellingPanel.getWord();
                        if (boggleBoard.contains(word))
                        {
                            foundWordsPanel.addWord(word);
                            spellingPanel.clear();
                        }
                        else
                        {
                            // error beep
                            spellingPanel.clear();
                        }
                    }
                }
            }
        );
    }

    public void navBack()
    {
        parent.navHome();
    }

    public void newGame()
    {
        boggleBoard.generateNew();
        boggleBoard.print();
        for (int row = 0; row < 4; ++row)
        {
            for (int col = 0; col < 4; ++col)
            {
                boggleLetterPanel[row][col].setIcon(ImageCache.getLargeLetter(boggleBoard.get(row, col)));
            }
        }
        boggleBoard.calculateWordList();
        foundWordsPanel.clear();
        spellingPanel.clear();
        gameOn = true;
    }

}
