package bwyatt.game.client;

import java.util.*;
import javax.swing.*;

public class ImageCache
{
    protected static ImageCache instance = null;
    protected ImageIcon[] largeLetters;
    protected ImageIcon[] smallLetters;
    private ImageIcon boggleBoard;
    protected ImageIcon[] twenty48Tiles;
    protected ImageIcon[] twenty48Thumbs;
    protected ImageIcon twenty48Blank;
    protected ImageIcon twenty48BlankThumb;
    protected ImageIcon twenty48Board;
    protected ImageIcon twenty48BoardThumb;
    protected ImageIcon[] playerIcons;
    protected ImageIcon[] statusIcons;

    protected ImageCache()
    {
        largeLetters = new ImageIcon[26];
        smallLetters = new ImageIcon[26];
        
        for (char c = 'A'; c <= 'Z'; ++c)
        {
            largeLetters[(int)(c-'A')] = new ImageIcon(this.getClass().getResource("resource/" + c + ".png"));
            smallLetters[(int)(c-'A')] = new ImageIcon(this.getClass().getResource("resource/" + c + "_30.png"));
        }
        boggleBoard = new ImageIcon(this.getClass().getResource("resource/B_Board.png"));

        twenty48Tiles = new ImageIcon[13];
        twenty48Thumbs = new ImageIcon[13];
        for (int i = 0; i < 12; ++i)
        {
            twenty48Tiles[i] = new ImageIcon(this.getClass().getResource("resource/T" + i + ".png"));
            twenty48Thumbs[i] = new ImageIcon(this.getClass().getResource("resource/T" + i + "_40.png"));
        }
        twenty48Blank = new ImageIcon(this.getClass().getResource("resource/T_Blank.png"));
        twenty48Board = new ImageIcon(this.getClass().getResource("resource/T_Board.png"));
        twenty48BoardThumb = new ImageIcon(this.getClass().getResource("resource/T_Board_174.png"));
        twenty48BlankThumb = new ImageIcon(this.getClass().getResource("resource/T_Blank_40.png"));

        String[] playerIconNames = {"Red", "Orange", "Pink", "Green", "Blue"};
        playerIcons = new ImageIcon[playerIconNames.length];
        for (int i = 0; i < playerIcons.length; ++i)
        {
            playerIcons[i] = new ImageIcon(this.getClass().getResource("resource/PI_" + playerIconNames[i] + ".png"));
        }

        String[] statusIconNames = {"Boggle", "2048"};
        statusIcons = new ImageIcon[statusIconNames.length];
        for (int i = 0; i < statusIcons.length; ++i)
        {
            statusIcons[i] = new ImageIcon(this.getClass().getResource("resource/PS_" + statusIconNames[i] + ".png"));
        }
    }

    public static void init()
    {
        instance = new ImageCache();
    }

    public static ImageIcon getLargeLetter(char letter)
    {
        return instance.largeLetters[(int)(letter-'A')];
    }

    public static ImageIcon getSmallLetter(char letter)
    {
        return instance.smallLetters[(int)(letter-'A')];
    }

    public static ImageIcon getBoggleBoard()
    {
        return instance.boggleBoard;
    }

    public static ImageIcon getTwenty48Tile(int i)
    {
        return instance.twenty48Tiles[i];
    }
    
    public static ImageIcon getTwenty48TileThumb(int i)
    {
        return instance.twenty48Thumbs[i];
    }

    public static ImageIcon getTwenty48Board()
    {
        return instance.twenty48Board;
    }

    public static ImageIcon getTwenty48BoardThumb()
    {
        return instance.twenty48BoardThumb;
    }

    public static ImageIcon getTwenty48Blank()
    {
        return instance.twenty48Blank;
    }

    public static ImageIcon getTwenty48BlankThumb()
    {
        return instance.twenty48BlankThumb;
    }

    public static ImageIcon getPlayerIcon(int i)
    {
        return instance.playerIcons[i];
    }

    public static int getPlayerIconCount()
    {
        return instance.playerIcons.length;
    }

    public static ImageIcon getStatusIcon(int i)
    {
        return instance.statusIcons[i];
    }
}
