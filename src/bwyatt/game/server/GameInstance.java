package bwyatt.game.server;

import java.util.*;

import bwyatt.game.common.*;

public class GameInstance
{
    protected int id;
    protected LinkedList<ClientInfo> clients;

    public GameInstance(int id)
    {
        this.id = id;
        this.clients = new LinkedList<ClientInfo>();
    }

    public void addClient(ClientInfo info)
    {
        this.clients.add(info);
        info.setInstance(this);
    }

    public void removeClient(ClientInfo info)
    {
        this.clients.remove(info);
        info.setInstance(null);
    }

    public LinkedList<ClientInfo> getClients()
    {
        return this.clients;
    }
    
    public int getID()
    {
        return this.id;
    }
}
