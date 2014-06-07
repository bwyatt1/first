package bwyatt.game.client.boggle;

import java.awt.GridLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import bwyatt.game.client.*;

public class SpellingPanel extends JPanel
{
    char[] word;
    int wordLen;
    JLabel[] letterLabel;

    public SpellingPanel()
    {
        word = new char[16];
        wordLen = 0;
        this.setLayout(new GridLayout(1, 16));
        letterLabel = new JLabel[16];
        for (int i = 0; i < 16; ++i)
        {
            letterLabel[i] = new JLabel();
            this.add(letterLabel[i]);
        }
        this.setOpaque(false);
    }

    public void addLetter(char letter, boolean matched)
    {
        if (wordLen < 16)
        {
            letter = Character.toUpperCase(letter);
            word[wordLen] = letter;
            letterLabel[wordLen].setIcon(ImageCache.getSmallLetter(letter));
            ++wordLen;
        }
    }

    public void removeLastLetter()
    {
        if (wordLen > 0)
        {
            --wordLen;
            letterLabel[wordLen].setIcon(null);
        }
    }

    public void clear()
    {
        for (int i = 0; i < wordLen; ++i)
            letterLabel[i].setIcon(null);
        wordLen = 0;
    }

    public String getWord()
    {
        return new String(word, 0, wordLen);
    }
}
