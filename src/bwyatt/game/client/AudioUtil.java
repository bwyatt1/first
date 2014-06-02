package bwyatt.game.client;

import java.net.URL;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.*;

public class AudioUtil
{
    protected AudioClip[] pop;
    protected MediaPlayer boggleSong;
    protected int musicVolume;
    protected int soundsVolume;
    protected static AudioUtil instance;
    
    protected AudioUtil()
    {
        try
        {
            URL url;

            JFXPanel fxPanel = new JFXPanel(); // trigger javafx init

            pop = new AudioClip[9];
            for (int i = 0; i < 9; ++i)
            {
                url = this.getClass().getResource("resource/pop" + i + ".mp3");
                pop[i] = new AudioClip(url.toString());
            }
            url = this.getClass().getResource("resource/boggle.mp3");
            boggleSong = new MediaPlayer(new Media(url.toString()));
            boggleSong.setCycleCount(Integer.MAX_VALUE);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void init()
    {
        instance = new AudioUtil();
    }

    public static void setMusicVolume(int volume)
    {
        instance.musicVolume = volume;
        instance.boggleSong.setVolume(volume / 100f);
    }

    public static int getMusicVolume()
    {
        return instance.musicVolume;
    }

    public static void setSoundsVolume(int volume)
    {
        instance.soundsVolume = volume;
    }

    public static int getSoundsVolume()
    {
        return instance.soundsVolume;
    }

    public static void playBoggleSong()
    {
        instance.boggleSong.play();
    }
    
    public static void stopBoggleSong()
    {
        instance.boggleSong.stop();
    }

    public static void playPop(int i)
    {
        if (instance.soundsVolume == 0)
            return;
        if (i > 8)
            i = 8;
        if (!instance.pop[i].isPlaying())
            instance.pop[i].play(instance.soundsVolume / 100f);
    }
}
