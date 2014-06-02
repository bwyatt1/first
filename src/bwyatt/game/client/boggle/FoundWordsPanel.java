package bwyatt.game.client.boggle;

import java.util.LinkedList;
import java.util.ListIterator;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class FoundWordsPanel extends JPanel
{
    private LinkedList<String> words;
    private JLabel wordsLabel;

    public FoundWordsPanel()
    {
        words = new LinkedList<String>();
        wordsLabel = new JLabel();
        this.add(wordsLabel);
    }

    public void addWord(String word)
    {
        ListIterator<String> iter = words.listIterator();
        String curString;
        boolean added = false;
        while (iter.hasNext() && !added)
        {
            curString = iter.next();
            if ((word.length() < curString.length()) ||
                (word.length() == curString.length()
                    && word.compareTo(curString) < 0))
            {
                iter.previous();
                iter.add(word);
                added = true;
            }
        }
        if (!added)
            iter.add(word);

        this.updateLabel();
    }

    public void clear()
    {
        words = new LinkedList<String>();
        this.updateLabel();
    }

    private void updateLabel()
    {
        String text = "<html>";
        int length = 0;
        ListIterator<String> iter = words.listIterator();
        String curString;
        while (iter.hasNext())
        {
            curString = iter.next();
            if (curString.length() != length)
            {
                if (length != 0)
                    text = text + "<br>";
                length = curString.length();
                text = text + length + " Letter Words<br>";
            }
            text = text + curString + "<br>";
        }
        text = text + "</html>";
        wordsLabel.setText(text);
    }
}
