package bwyatt.game.client.boggle;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;

import bwyatt.game.client.*;
import bwyatt.game.common.*;

public class FoundWordsPanel extends JPanel
{
    private LinkedList<WordEntry> words;
    private JTextPane wordsPane;
    private PlayerInfo myInfo;
    private ArrayList<PlayerInfo> players;

    public FoundWordsPanel(PlayerInfo myInfo, ArrayList<PlayerInfo> players)
    {
        this.myInfo = myInfo;
        this.players = players;
        this.words = new LinkedList<WordEntry>();

        wordsPane = new JTextPane();
        wordsPane.setEditable(false);

        this.setLayout(new BorderLayout());
        this.add(wordsPane, BorderLayout.CENTER);
    }

    public void addWord(String word)
    {
        ListIterator<WordEntry> iter = words.listIterator();
        WordEntry entry;
        boolean added = false;
        while (iter.hasNext() && !added)
        {
            entry = iter.next();
            if ((word.length() < entry.getWord().length()) ||
                (word.length() == entry.getWord().length() && word.compareTo(entry.getWord()) < 0))
            {
                iter.previous();
                iter.add(new WordEntry(word, myInfo));
                added = true;
            }
        }
        if (!added)
            iter.add(new WordEntry(word, myInfo));

        this.updateDocument();
    }

    public void addWord(PlayerInfo player, String word)
    {
        WordEntry entry;
        boolean added = false;
        ListIterator<WordEntry> iter = words.listIterator();
        while (iter.hasNext() && !added)
        {
            entry = iter.next();
            if (entry.getWord().equals(word))
            {
                entry.addPlayer(player);
                added = true;
            }
            else if ((word.length() < entry.getWord().length()) ||
                (word.length() == entry.getWord().length() && word.compareTo(entry.getWord()) < 0))
            {
                iter.previous();
                iter.add(new WordEntry(word, player));
                added = true;
            }
        }
        if (!added)
            iter.add(new WordEntry(word, player));

        this.updateDocument();
    }

    public boolean contains(String word)
    {
        for (WordEntry entry : words)
        {
            if (entry.getWord().equals(word))
                return entry.getPlayers().contains(myInfo);
        }
        return false;
    }

    public void clear()
    {
        words = new LinkedList<WordEntry>();
        this.updateDocument();
    }

    public void updateDocument()
    {
        DefaultStyledDocument doc = new DefaultStyledDocument();
        Style defaultStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        Style regularStyle = doc.addStyle("regular", defaultStyle);
        Style titleStyle = doc.addStyle("bold", regularStyle);
        StyleConstants.setBold(titleStyle, true);
        Style onlyMeStyle = doc.addStyle("onlyme", regularStyle);
        StyleConstants.setBold(onlyMeStyle, true);
        Style multiStyle = doc.addStyle("multi", regularStyle);
        StyleConstants.setItalic(multiStyle, true);
        Style hiddenStyle = doc.addStyle("hidden", regularStyle);

        Style style = doc.addStyle("icon" + myInfo.getID(), regularStyle);
        StyleConstants.setAlignment(style, StyleConstants.ALIGN_CENTER);
        StyleConstants.setIcon(style, ImageCache.getPlayerIcon(myInfo.getIconID()));
        for (PlayerInfo player : players)
        {
            style = doc.addStyle("icon" + player.getID(), regularStyle);
            StyleConstants.setAlignment(style, StyleConstants.ALIGN_CENTER);
            StyleConstants.setIcon(style, ImageCache.getPlayerIcon(player.getIconID()));
        }

        try
        {
            int length = 0;
            boolean iHaveFound;
            for (WordEntry entry : words)
            {
                if (doc.getLength() > 0)
                    doc.insertString(doc.getLength(), "\n", regularStyle);
                if (entry.getWord().length() != length)
                {
                    length = entry.getWord().length();
                    doc.insertString(doc.getLength(), "" + length + " Letter Words\n", titleStyle);
                }
                iHaveFound = false;
                for (PlayerInfo player : entry.getPlayers())
                {
                    doc.insertString(doc.getLength(), " ", doc.getStyle("icon" + player.getID()));
                    if (player == myInfo)
                        iHaveFound = true;
                }
                if (iHaveFound)
                {
                    if (entry.getPlayers().size() == 1)
                        doc.insertString(doc.getLength(), entry.getWord(), onlyMeStyle);
                    else
                        doc.insertString(doc.getLength(), entry.getWord(), multiStyle);
                }
                else
                {
                    String hidden = "***";
                    for (int i = 3; i < entry.getWord().length(); ++i)
                        hidden += "*";
                    doc.insertString(doc.getLength(), hidden, hiddenStyle);
                }
            }
        }
        catch (BadLocationException e)
        {
            e.printStackTrace();
        }
        wordsPane.setStyledDocument(doc);

        // scroll to bottom
        wordsPane.setCaretPosition(wordsPane.getDocument().getLength());
        wordsPane.repaint();
    }

    public class WordEntry
    {
        private String word;
        private LinkedList<PlayerInfo> players;

        public WordEntry(String word, PlayerInfo player)
        {
            this.word = word;
            this.players = new LinkedList<PlayerInfo>();
            this.players.add(player);
        }

        public void addPlayer(PlayerInfo player)
        {
            this.players.add(player);
        }

        public String getWord()
        {
            return this.word;
        }

        public LinkedList<PlayerInfo> getPlayers()
        {
            return this.players;
        }
    }
}
