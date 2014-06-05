package bwyatt.game.client;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.*;
import javax.swing.*;

public class ImageCache
{
    protected static ImageCache instance = null;
    protected ImageIcon[] largeLetters;
    protected ImageIcon[] smallLetters;
    protected ImageIcon boggleBoard;
    protected ImageIcon boggleHi1;
    protected ImageIcon boggleHi2;
    protected ImageIcon boggleHiEdgeV;
    protected ImageIcon boggleHiEdgeH;
    protected ImageIcon boggleHiEdgeD1;
    protected ImageIcon boggleHiEdgeD2;
    protected ImageIcon[] twenty48Tiles;
    protected ImageIcon[] twenty48Thumbs;
    protected ImageIcon twenty48Blank;
    protected ImageIcon twenty48BlankThumb;
    protected ImageIcon twenty48Board;
    protected ImageIcon twenty48BoardThumb;
    protected ImageIcon[] playerIcons;
    protected ImageIcon[] statusIcons;
    protected ImageIcon backIcon;
    protected ImageIcon homeIcon;
    protected ImageIcon settingsIcon;

    /*
     * Some images fit nicely into combo PNGs, others do not
     */
    protected ImageCache() throws IOException
    {
        BufferedImage bufferedImage;
        Graphics2D graphics;

        largeLetters = new ImageIcon[26];
        smallLetters = new ImageIcon[26];
        
        BufferedImage boggleLargeLetters = ImageIO.read(this.getClass().getResource("resource/BL_96.png"));
        BufferedImage boggleSmallLetters = ImageIO.read(this.getClass().getResource("resource/BL_48.png"));
        for (int i = 0; i < 26; ++i)
        {
            bufferedImage = new BufferedImage(96, 96, BufferedImage.TYPE_INT_ARGB);
            bufferedImage.createGraphics().drawImage(boggleLargeLetters, null, -i*96, 0);
            largeLetters[i] = new ImageIcon(bufferedImage);
            bufferedImage = new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB);
            bufferedImage.createGraphics().drawImage(boggleSmallLetters, null, -i*48, 0);
            smallLetters[i] = new ImageIcon(bufferedImage);
        }
        boggleBoard = new ImageIcon(this.getClass().getResource("resource/B_Board.png"));
        boggleHi1 = new ImageIcon(this.getClass().getResource("resource/B_Hi1.png"));
        boggleHi2 = new ImageIcon(this.getClass().getResource("resource/B_Hi2.png"));
        boggleHiEdgeV = new ImageIcon(this.getClass().getResource("resource/B_Hi_V.png"));
        boggleHiEdgeH = new ImageIcon(this.getClass().getResource("resource/B_Hi_H.png"));
        boggleHiEdgeD1 = new ImageIcon(this.getClass().getResource("resource/B_Hi_D1.png"));
        boggleHiEdgeD2 = new ImageIcon(this.getClass().getResource("resource/B_Hi_D2.png"));

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

        BufferedImage navIcons = ImageIO.read(this.getClass().getResource("resource/nav.png"));
        bufferedImage = new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB);
        graphics = bufferedImage.createGraphics();
        graphics.drawImage(navIcons, null, 0, 0);
        backIcon = new ImageIcon(bufferedImage);
        bufferedImage = new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB);
        graphics = bufferedImage.createGraphics();
        graphics.drawImage(navIcons, null, -48, 0);
        homeIcon = new ImageIcon(bufferedImage);
        bufferedImage = new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB);
        graphics = bufferedImage.createGraphics();
        graphics.drawImage(navIcons, null, -96, 0);
        settingsIcon = new ImageIcon(bufferedImage);
    }

    public static void init()
    {
        try
        {
            instance = new ImageCache();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
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

    public static ImageIcon getBackIcon()
    {
        return instance.backIcon;
    }

    public static ImageIcon getHomeIcon()
    {
        return instance.homeIcon;
    }

    public static ImageIcon getSettingsIcon()
    {
        return instance.settingsIcon;
    }

    public static ImageIcon getBoggleHi1()
    {
        return instance.boggleHi1;
    }

    public static ImageIcon getBoggleHi2()
    {
        return instance.boggleHi2;
    }

    public static ImageIcon getBoggleHiV()
    {
        return instance.boggleHiEdgeV;
    }

    public static ImageIcon getBoggleHiH()
    {
        return instance.boggleHiEdgeH;
    }

    public static ImageIcon getBoggleHiD1()
    {
        return instance.boggleHiEdgeD1;
    }

    public static ImageIcon getBoggleHiD2()
    {
        return instance.boggleHiEdgeD2;
    }
}
