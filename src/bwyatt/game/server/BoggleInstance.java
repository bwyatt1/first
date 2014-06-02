package bwyatt.game.server;

import java.util.*;

import bwyatt.game.common.*;

public class BoggleInstance extends GameInstance
{
    private BoggleBoard board;
    private HashMap<ClientInfo, LinkedList<String>> foundWords;

    public BoggleInstance(int id)
    {
        super(id);
    }

    public void newGame()
    {
    }
}
