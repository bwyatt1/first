package bwyatt.game.client;

import java.awt.*;
import java.io.*;
import java.util.*;

public class Config
{
    private Properties props;

    public Config(String iniFileName)
    {
        try
        {
            props = new Properties();
            File file = new File(iniFileName);
            if (file.exists())
            {
                FileInputStream in = new FileInputStream(file);
                props.load(in);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void writeFile(String iniFileName)
    {
        try
        {
            FileOutputStream out = new FileOutputStream(iniFileName);
            props.store(out, "BoggleINI");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void setServer(String server)
    {
        props.setProperty("Server", server);
    }

    public String getServer()
    {
        return props.getProperty("Server");
    }

    public void setWindowSize(Dimension size)
    {
        props.setProperty("WindowWidth", "" + size.width);
        props.setProperty("WindowHeight", "" + size.height);
    }

    public Dimension getWindowSize()
    {
        try
        {
            int width = Integer.parseInt(props.getProperty("WindowWidth"));
            int height = Integer.parseInt(props.getProperty("WindowHeight"));
            return new Dimension(width, height);
        }
        catch (Exception e)
        {
        }
        return null;
    }

    public void setWindowLoc(Point loc)
    {
        props.setProperty("WindowX", "" + loc.x);
        props.setProperty("WindowY", "" + loc.y);
    }

    public Point getWindowLoc()
    {
        try
        {
            int x = Integer.parseInt(props.getProperty("WindowX"));
            int y = Integer.parseInt(props.getProperty("WindowY"));
            return new Point(x, y);
        }
        catch (Exception e)
        {
        }
        return null;
    }

    public void setPrefWindowSize(Dimension size)
    {
        props.setProperty("PrefWindowWidth", "" + size.width);
        props.setProperty("PrefWindowHeight", "" + size.height);
    }

    public Dimension getPrefWindowSize()
    {
        try
        {
            int width = Integer.parseInt(props.getProperty("PrefWindowWidth"));
            int height = Integer.parseInt(props.getProperty("PrefWindowHeight"));
            return new Dimension(width, height);
        }
        catch (Exception e)
        {
        }
        return null;
    }

    public void setPrefWindowLoc(Point loc)
    {
        props.setProperty("PrefWindowX", "" + loc.x);
        props.setProperty("PrefWindowY", "" + loc.y);
    }

    public Point getPrefWindowLoc()
    {
        try
        {
            int x = Integer.parseInt(props.getProperty("PrefWindowX"));
            int y = Integer.parseInt(props.getProperty("PrefWindowY"));
            return new Point(x, y);
        }
        catch (Exception e)
        {
        }
        return null;
    }

    public void setGamePanelWidth(int width)
    {
        props.setProperty("GamePanelWidth", "" + width);
    }

    public int getGamePanelWidth()
    {
        try
        {
            int width = Integer.parseInt(props.getProperty("GamePanelWidth"));
            return width;
        }
        catch (Exception e)
        {
        }
        return 400;
    }

    public void setName(String name)
    {
        props.setProperty("PlayerName", name);
    }

    public String getName()
    {
        return props.getProperty("PlayerName");
    }

    public void setMusicVolume(int volume)
    {
        props.setProperty("MusicVolume", "" + volume);
    }
    
    public int getMusicVolume()
    {
        try
        {
            return Integer.parseInt(props.getProperty("MusicVolume"));
        }
        catch (Exception e)
        {
        }
        return 100;
    }

    public void setMusicMuted(boolean muted)
    {
        props.setProperty("MusicMuted", "" + muted);
    }
    
    public boolean getMusicMuted()
    {
        try
        {
            return Boolean.parseBoolean(props.getProperty("MusicMuted"));
        }
        catch (Exception e)
        {
        }
        return false;
    }

    public void setSoundsVolume(int volume)
    {
        props.setProperty("SoundsVolume", "" + volume);
    }

    public int getSoundsVolume()
    {
        try
        {
            return Integer.parseInt(props.getProperty("SoundsVolume"));
        }
        catch (Exception e)
        {
        }
        return 100;
    }

    public void setSoundsMuted(Boolean muted)
    {
        props.setProperty("SoundsMuted", "" + muted);
    }

    public boolean getSoundsMuted()
    {
        try
        {
            return Boolean.parseBoolean(props.getProperty("SoundsMuted"));
        }
        catch (Exception e)
        {
        }
        return false;
    }

    public void setPlayerIconID(int id)
    {
        props.setProperty("PlayerIconID", "" + id);
    }

    public int getPlayerIconID()
    {
        try
        {
            return Integer.parseInt(props.getProperty("PlayerIconID"));
        }
        catch (Exception e)
        {
        }
        return 0;
    }
}
