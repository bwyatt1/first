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
    protected ImageIcon twenty48Blank;
    protected ImageIcon twenty48Board;
    protected ImageIcon[] playerIcons;

    protected ImageCache()
    {
        largeLetters = new ImageIcon[26];
        smallLetters = new ImageIcon[26];
        
        for (char c = 'A'; c <= 'Z'; ++c)
        {
            largeLetters[(int)(c-'A')] = new ImageIcon(this.getClass().getResource("resource/" + c + ".png"));
            smallLetters[(int)(c-'A')] = new ImageIcon(this.getClass().getResource("resource/" + c + "_30.png"));
            //System.out.println("loading " + c + ": " + largeLetters[(int)(c-'A')].toString());
        }
        boggleBoard = new ImageIcon(this.getClass().getResource("resource/B_Board.png"));

        twenty48Tiles = new ImageIcon[13];
        for (int i = 0; i < 12; ++i)
        {
            twenty48Tiles[i] = new ImageIcon(this.getClass().getResource("resource/T" + i + ".png"));
        }
        twenty48Blank = new ImageIcon(this.getClass().getResource("resource/T_Blank.png"));
        twenty48Board = new ImageIcon(this.getClass().getResource("resource/T_Board.png"));

        String[] playerIconNames = {"Red", "Orange", "Pink", "Green", "Blue"};
        playerIcons = new ImageIcon[playerIconNames.length];
        for (int i = 0; i < playerIcons.length; ++i)
        {
            playerIcons[i] = new ImageIcon(this.getClass().getResource("resource/PI_" + playerIconNames[i] + ".png"));
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
    
    public static ImageIcon getTwenty48Board()
    {
        return instance.twenty48Board;
    }

    public static ImageIcon getTwenty48Blank()
    {
        return instance.twenty48Blank;
    }

    public static ImageIcon getPlayerIcon(int i)
    {
        return instance.playerIcons[i];
    }

    public static int getPlayerIconCount()
    {
        return instance.playerIcons.length;
    }
}
