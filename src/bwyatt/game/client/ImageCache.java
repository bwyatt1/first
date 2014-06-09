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
    protected ImageIcon mainBackground;
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
    protected ImageIcon twenty48Board;
    protected ImageIcon twenty48BoardThumb;
    protected ImageIcon[] playerIcons;
    protected ImageIcon[] statusIcons;
    protected ImageIcon navBackground;
    protected ImageIcon backIcon;
    protected ImageIcon homeIcon;
    protected ImageIcon settingsIcon;
    protected ImageIcon[] roomIcons;

    /*
     * Some images fit nicely into combo PNGs, others do not
     */
    protected ImageCache() throws IOException
    {
        BufferedImage bufferedImage;
        Graphics2D graphics;

        mainBackground = new ImageIcon(this.getClass().getResource("resource/Wood_background.png"));

        largeLetters = new ImageIcon[26];
        smallLetters = new ImageIcon[26];
        
        BufferedImage boggleLargeLetters = ImageIO.read(this.getClass().getResource("resource/Wood_Letters_96.png"));
        BufferedImage boggleSmallLetters = ImageIO.read(this.getClass().getResource("resource/Wood_Letters_48.png"));
        for (int i = 0; i < 26; ++i)
        {
            bufferedImage = new BufferedImage(96, 96, BufferedImage.TYPE_INT_ARGB);
            bufferedImage.createGraphics().drawImage(boggleLargeLetters, null, -i*96, 0);
            largeLetters[i] = new ImageIcon(bufferedImage);
            bufferedImage = new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB);
            bufferedImage.createGraphics().drawImage(boggleSmallLetters, null, -i*48, 0);
            smallLetters[i] = new ImageIcon(bufferedImage);
        }
        boggleBoard = new ImageIcon(this.getClass().getResource("resource/Wood_board.png"));
        boggleHi1 = new ImageIcon(this.getClass().getResource("resource/Wood_Hi1.png"));
        boggleHi2 = new ImageIcon(this.getClass().getResource("resource/Wood_Hi2.png"));
        boggleHiEdgeV = new ImageIcon(this.getClass().getResource("resource/B_Hi_V.png"));
        boggleHiEdgeH = new ImageIcon(this.getClass().getResource("resource/B_Hi_H.png"));
        boggleHiEdgeD1 = new ImageIcon(this.getClass().getResource("resource/B_Hi_D1.png"));
        boggleHiEdgeD2 = new ImageIcon(this.getClass().getResource("resource/B_Hi_D2.png"));

        BufferedImage twenty48AllTiles = ImageIO.read(this.getClass().getResource("resource/Wood_T_96.png"));
        BufferedImage twenty48AllThumbs = ImageIO.read(this.getClass().getResource("resource/Wood_T_48.png"));

        twenty48Tiles = new ImageIcon[13];
        twenty48Thumbs = new ImageIcon[13];
        for (int i = 0; i < 12; ++i)
        {
            bufferedImage = new BufferedImage(96, 96, BufferedImage.TYPE_INT_ARGB);
            bufferedImage.createGraphics().drawImage(twenty48AllTiles, null, -i*96, 0);
            twenty48Tiles[i] = new ImageIcon(bufferedImage);
            bufferedImage = new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB);
            bufferedImage.createGraphics().drawImage(twenty48AllThumbs, null, -i*48, 0);
            twenty48Thumbs[i] = new ImageIcon(bufferedImage);
        }
        twenty48Board = new ImageIcon(this.getClass().getResource("resource/Wood_board.png"));
        twenty48BoardThumb = new ImageIcon(this.getClass().getResource("resource/T_Board_174.png"));

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

        navBackground = new ImageIcon(this.getClass().getResource("resource/Wood_nav_back.png"));
        BufferedImage navIcons = ImageIO.read(this.getClass().getResource("resource/Wood_nav_icon.png"));
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

        BufferedImage roomAllIcons = ImageIO.read(this.getClass().getResource("resource/Wood_rooms.png"));
        roomIcons = new ImageIcon[3];
        for (int i = 0; i < 3; ++i)
        {
            bufferedImage = new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB);
            bufferedImage.createGraphics().drawImage(roomAllIcons, null, -i*48, 0);
            roomIcons[i] = new ImageIcon(bufferedImage);
        }
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

    public static ImageIcon getMainBackground()
    {
        return instance.mainBackground;
    }

    public static ImageIcon getNavBackground()
    {
        return instance.navBackground;
    }

    public static ImageIcon getRoomIcon(int i)
    {
        return instance.roomIcons[i];
    }
}
