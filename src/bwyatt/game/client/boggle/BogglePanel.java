package bwyatt.game.client.boggle;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

import bwyatt.game.client.*;
import bwyatt.game.common.*;

public class BogglePanel extends JPanel
{
    private GameFrame parent;
    private BoggleBoardPanel boggleBoardPanel;
    private JLabel[][] boggleLetterPanel;
    private JPanel hiPanel;
    private SpellingPanel spellingPanel;
    private FoundWordsPanel foundWordsPanel;
    private boolean chatOn;
    private boolean gameOn;
    private JTextField chatInput;

    public BogglePanel(GameFrame parent)
    {
        this.parent = parent;
        gameOn = false;

        boggleBoardPanel = new BoggleBoardPanel();
        spellingPanel = new SpellingPanel();
        foundWordsPanel = new FoundWordsPanel();

        SpringLayout mainLayout = new SpringLayout();
        this.setLayout(mainLayout);
        this.add(boggleBoardPanel);
        mainLayout.putConstraint(SpringLayout.WEST, boggleBoardPanel, 5, SpringLayout.WEST, this);
        mainLayout.putConstraint(SpringLayout.NORTH, boggleBoardPanel, 5, SpringLayout.NORTH, this);
        this.add(spellingPanel);
        mainLayout.putConstraint(SpringLayout.WEST, spellingPanel, 5, SpringLayout.WEST, this);
        mainLayout.putConstraint(SpringLayout.NORTH, spellingPanel, 5, SpringLayout.SOUTH, boggleBoardPanel);
        this.add(foundWordsPanel);
        mainLayout.putConstraint(SpringLayout.WEST, foundWordsPanel, 5, SpringLayout.EAST, boggleBoardPanel);
        mainLayout.putConstraint(SpringLayout.NORTH, foundWordsPanel, 5, SpringLayout.NORTH, this);

        this.setOpaque(false);
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
                        {
                            spellingPanel.addLetter(e.getActionCommand().charAt(0), true);
                            boggleBoardPanel.hiWord(spellingPanel.getWord());
                        }
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
                    {
                        if (spellingPanel.getWord().length() > 0)
                        {
                            spellingPanel.removeLastLetter();
                            boggleBoardPanel.hiWord(spellingPanel.getWord());
                        }
                    }
                }
            }
        );
        actionMap.put("space", new AbstractAction()
            {
                public void actionPerformed(ActionEvent e)
                {
                    if (gameOn && !chatOn)
                    {
                        spellingPanel.clear();
                        boggleBoardPanel.hiClear();
                    }
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
                        if (boggleBoardPanel.contains(word))
                        {
                            foundWordsPanel.addWord(word);
                        }
                        else
                        {
                            // error beep
                        }
                        boggleBoardPanel.hiClear();
                        spellingPanel.clear();
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
        boggleBoardPanel.generateNew();
        foundWordsPanel.clear();
        spellingPanel.clear();
        gameOn = true;
    }
}
