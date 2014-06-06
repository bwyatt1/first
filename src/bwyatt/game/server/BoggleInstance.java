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
        board = new BoggleBoard();
    }

    public void newGame()
    {
        board.newGame();
        for (ClientInfo client : this.clients)
        {
            foundWords.put(client, new LinkedList<String>());
        }
    }

    public BoggleBoard getBoard()
    {
        return this.board;
    }

    public boolean validateWord(ClientInfo client, String word)
    {
        if (board.contains(word) && !foundWords.get(client).contains(word))
            return true;
    }

    public void addWord(ClientInfo client, String word)
    {
        foundWords.get(client).add(word);
    }
}
