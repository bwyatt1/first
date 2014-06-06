package bwyatt.game.server;

import java.util.*;

import bwyatt.game.common.*;

public class Twenty48Instance extends GameInstance
{
    private HashMap<ClientInfo, Twenty48Board> boards;

    public Twenty48Instance(int id)
    {
        super(id);
        this.boards = new HashMap<ClientInfo, Twenty48Board>();
        this.endTime = 0;
    }

    public void newGame()
    {
        Twenty48Board startBoard = new Twenty48Board();
        startBoard.newGame();

        for (ClientInfo client : this.clients)
        {
            Twenty48Board board = new Twenty48Board(startBoard);
            boards.put(client, board);
        }
    }

    public Twenty48Board getBoard(ClientInfo client)
    {
        return boards.get(client);
    }
}
