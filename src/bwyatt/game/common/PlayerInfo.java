package bwyatt.game.common;

public class PlayerInfo
{
    private String name;
    private int id;
    private int showingGame;
    private int status;
    private int iconID;

    public static final int GAME_NONE = 0;
    public static final int GAME_BOGGLE = 1;
    public static final int GAME_2048 = 2;

    public static final int STATUS_READY = 1;
    public static final int STATUS_ACTIVE = 2;

    public PlayerInfo()
    {
        this.name = null;
        this.id = -1;
        this.showingGame = GAME_NONE;
        this.status = 0;
        this.iconID = 0;
    }

    public PlayerInfo(String name, int id)
    {
        this.name = name;
        this.id = id;
        this.showingGame = GAME_NONE;
        this.status = 0;
        this.iconID = 0;
    }

    public PlayerInfo(PlayerInfo source)
    {
        this.name = new String(source.name);
        this.id = source.id;
        this.showingGame = source.showingGame;
        this.status = source.status;
        this.iconID = source.iconID;
    }

    public String toString()
    {
        String ret = name + "(" + id + ") icon=" + iconID;
        if (showingGame == GAME_BOGGLE)
            ret += ",show=Boggle";
        else if (showingGame == GAME_2048)
            ret += ",show=2048";
        if (status == STATUS_READY)
            ret += ",status=Ready";
        else if (status == STATUS_ACTIVE)
            ret += ",status=Active";
        return ret;
    }

    public String getName()
    {
        return this.name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }

    public int getID()
    {
        return this.id;
    }

    public void setID(int id)
    {
        this.id = id;
    }

    public int getShowing()
    {
        return this.showingGame;
    }

    public void setShowing(int showingGame)
    {
        this.showingGame = showingGame;
    }

    public int getStatus()
    {
        return this.status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public int getIconID()
    {
        return this.iconID;
    }

    public void setIconID(int iconID)
    {
        this.iconID = iconID;
    }
}
