package bwyatt.game.server;

import java.nio.channels.*;

import bwyatt.game.common.*;

public class ClientInfo
{
    public SocketChannel channel;
    public PlayerInfo playerInfo;
    public GameInstance gameInstance;

    public ClientInfo()
    {
    }

    public void setChannel(SocketChannel channel)
    {
        this.channel = channel;
    }

    public SocketChannel getChannel()
    {
        return this.channel;
    }

    public void setPlayer(PlayerInfo playerInfo)
    {
        this.playerInfo = playerInfo;
    }

    public PlayerInfo getPlayer()
    {
        return this.playerInfo;
    }

    public void setInstance(GameInstance instance)
    {
        this.gameInstance = instance;
    }

    public GameInstance getInstance()
    {
        return this.gameInstance;
    }
}
