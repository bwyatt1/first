package bwyatt.game.client;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;

import bwyatt.game.common.*;

public class ChatBoxPanel extends JPanel
{
    private JTextPane textPane;
    private JScrollPane scrollPane;
    private Style defaultStyle;
    private Style regularStyle;
    private Style boldStyle;
    private HashMap<PlayerInfo, Style> playerStyles;
    private LinkedList<Chat> chatList;

    public ChatBoxPanel()
    {
        chatList = new LinkedList<Chat>();
        playerStyles = new HashMap<PlayerInfo, Style>();

        textPane = new JTextPane();
        textPane.setEditable(false);

        defaultStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        regularStyle = textPane.getStyledDocument().addStyle("regular", defaultStyle);
        StyleConstants.setSpaceBelow(regularStyle, 2);
        boldStyle = textPane.getStyledDocument().addStyle("bold", regularStyle);
        StyleConstants.setBold(boldStyle, true);

        scrollPane = new JScrollPane(textPane);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setOpaque(false);

        this.setLayout(new BorderLayout());
        this.add(scrollPane, BorderLayout.CENTER);

        this.setOpaque(false);
    }

    public void addChat(PlayerInfo player, String text)
    {
        StyledDocument doc = textPane.getStyledDocument();
        Style playerStyle = doc.getStyle("icon" + player.getID());
        try
        {
            if (doc.getLength() > 0)
                doc.insertString(doc.getLength(), "\n", regularStyle);
            doc.insertString(doc.getLength(), " ", playerStyle);
            doc.insertString(doc.getLength(), player.getName() + ": ", boldStyle);
            doc.insertString(doc.getLength(), text, regularStyle);
        }
        catch (BadLocationException e)
        {
            e.printStackTrace();
        }
        chatList.add(new Chat(player, text));
        
        // scroll to bottom
        textPane.setCaretPosition(textPane.getDocument().getLength());
        textPane.repaint();
    }

    public void updatePlayerStyle(PlayerInfo player)
    {
        StyledDocument doc = textPane.getStyledDocument();
        String styleName = "icon" + player.getID();
        if (doc.getStyle(styleName) != null)
            doc.removeStyle(styleName);
        
        Style newStyle = doc.addStyle(styleName, regularStyle);
        StyleConstants.setAlignment(newStyle, StyleConstants.ALIGN_CENTER);
        StyleConstants.setIcon(newStyle, ImageCache.getPlayerIcon(player.getIconID()));

        if (playerStyles.get(player) != null)
        {
            playerStyles.remove(player);
            playerStyles.put(player, newStyle);
            rebuildDocument();
        }
        else
        {
            playerStyles.put(player, newStyle);
        }
    }

    public void rebuildDocument()
    {
        DefaultStyledDocument doc = new DefaultStyledDocument();
        regularStyle = doc.addStyle("regular", defaultStyle);
        StyleConstants.setSpaceBelow(regularStyle, 2);
        boldStyle = doc.addStyle("bold", regularStyle);
        StyleConstants.setBold(boldStyle, true);

        for (PlayerInfo player : playerStyles.keySet())
        {
            Style style = doc.addStyle("icon" + player.getID(), regularStyle);
            StyleConstants.setAlignment(style, StyleConstants.ALIGN_CENTER);
            StyleConstants.setIcon(style, ImageCache.getPlayerIcon(player.getIconID()));
            playerStyles.put(player, style);
        }

        try
        {
            for (Chat chat : this.chatList)
            {
                if (doc.getLength() > 0)
                    doc.insertString(doc.getLength(), "\n", regularStyle);
                doc.insertString(doc.getLength(), " ", doc.getStyle("icon" + chat.getPlayer().getID()));
                doc.insertString(doc.getLength(), chat.getPlayer().getName() + ": ", boldStyle);
                doc.insertString(doc.getLength(), chat.getText(), regularStyle);
            }
        }
        catch (BadLocationException e)
        {
            e.printStackTrace();
        }
        textPane.setStyledDocument(doc);

        // scroll to bottom
        textPane.setCaretPosition(textPane.getDocument().getLength());
        textPane.repaint();
    }

    private class Chat
    {
        private PlayerInfo player;
        private String text;

        public Chat(PlayerInfo player, String text)
        {
            this.player = player;
            this.text = text;
        }

        public PlayerInfo getPlayer()
        {
            return this.player;
        }

        public String getText()
        {
            return this.text;
        }
    }
}
