package bwyatt.game.common;

import java.util.*;

public class Twenty48Board
{
    private int[][] board;
    private int score;
    private int highTile;

    private final static int BLANK = -1;
    private final static int SCORE_LUT[] = {0, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096};

    public Twenty48Board()
    {
        board = new int[4][];
        for (int row = 0; row < 4; ++row)
        {
            board[row] = new int[4];
            for (int col = 0; col < 4; ++col)
            {
                board[row][col] = BLANK;
            }
        }
        score = 0;
        highTile = -1;
    }

    public Twenty48Board(Twenty48Board source)
    {
        board = new int[4][];
        for (int row = 0; row < 4; ++row)
            board[row] = new int[4];
        this.setBoard(source);
    }

    public void newGame()
    {
        for (int row = 0; row < 4; ++row)
        {
            for (int col = 0; col < 4; ++col)
            {
                this.board[row][col] = BLANK;
            }
        }
        
        int placed = 0;
        while (placed < 2)
        {
            int row = (int)(Math.random() * 4);
            int col = (int)(Math.random() * 4);
            if (this.board[row][col] == -1)
            {
                placed++;
                this.board[row][col] = 0;
            }
        }
        this.score = 0;
        this.highTile = 0;
    }
    
    public void setBoard(int[][] board)
    {
        for (int row = 0; row < 4; ++row)
        {
            for (int col = 0; col < 4; ++col)
            {
                this.board[row][col] = board[row][col];
            }
        }
    }

    public void setBoard(Twenty48Board source)
    {
        for (int row = 0; row < 4; ++row)
        {
             for (int col = 0; col < 4; ++col)
             {
                 this.board[row][col] = source.board[row][col];
                 if (this.board[row][col] > this.highTile)
                 {
                     this.highTile = this.board[row][col];
                 }
             }
        }
        this.score = source.score;
    }

    public int getTile(int row, int col)
    {
        return this.board[row][col];
    }

    public boolean isGridLocked()
    {
        for (int row = 0; row < 4; ++row)
        {
            for (int col = 0; col < 4; ++col)
            {
                if (this.board[row][col] == BLANK)
                    return false;
                if (row < 3 && this.board[row][col] == this.board[row+1][col])
                    return false;
                if (col < 3 && this.board[row][col] == this.board[row][col+1])
                    return false;
            }
        }
        return true;
    }

    public void setScore(int score)
    {
        this.score = score;
    }

    public int getScore()
    {
        return this.score;
    }

    public int getHighTile()
    {
        return this.highTile;
    }

    public LinkedList<TileMove> move(int dir)
    {
        LinkedList<TileMove> moves = new LinkedList<TileMove>();
        if (dir == TileMove.LEFT)
        {
            for (int row = 0; row < 4; ++row)
            {
                int last = 0;
                for (int col = 1; col < 4; ++col)
                {
                    if (board[row][col] != -1)
                    {
                        if (board[row][col] == board[row][last])
                        {
                            moves.add(new TileMove(row, col, TileMove.LEFT, col-last, board[row][col]));
                            board[row][last] = board[row][col] + 1;
                            score += SCORE_LUT[board[row][last]];
                            if (board[row][last] > highTile)
                                highTile = board[row][last];
                            board[row][col] = -1;
                            ++last;
                        }
                        else if (col > last && board[row][last] == -1)
                        {
                            moves.add(new TileMove(row, col, TileMove.LEFT, col-last, board[row][col]));
                            board[row][last] = board[row][col];
                            board[row][col] = -1;
                        }
                        else if (col > last+1)
                        {
                            moves.add(new TileMove(row, col, TileMove.LEFT, col-last-1, board[row][col]));
                            board[row][last+1] = board[row][col];
                            board[row][col] = -1;
                            ++last;
                        }
                        else
                        {
                            ++last;
                        }
                    }
                }
            }
        }
        else if (dir == TileMove.RIGHT)
        {
            for (int row = 0; row < 4; ++row)
            {
                int last = 3;
                for (int col = 2; col >= 0; --col)
                {
                    if (board[row][col] != -1)
                    {
                        if (board[row][col] == board[row][last])
                        {
                            moves.add(new TileMove(row, col, TileMove.RIGHT, last-col, board[row][col]));
                            board[row][last] = board[row][col] + 1;
                            score += SCORE_LUT[board[row][last]];
                            if (board[row][last] > highTile)
                                highTile = board[row][last];
                            board[row][col] = -1;
                            --last;
                        }
                        else if (col < last && board[row][last] == -1)
                        {
                            moves.add(new TileMove(row, col, TileMove.RIGHT, last-col, board[row][col]));
                            board[row][last] = board[row][col];
                            board[row][col] = -1;
                        }
                        else if (col < last-1)
                        {
                            moves.add(new TileMove(row, col, TileMove.RIGHT, last-col-1, board[row][col]));
                            board[row][last-1] = board[row][col];
                            board[row][col] = -1;
                            --last;
                        }
                        else
                        {
                            --last;
                        }
                    }
                }
            }
        }
        else if (dir == TileMove.UP)
        {
            for (int col = 0; col < 4; ++col)
            {
                int last = 0;
                for (int row = 1; row < 4; ++row)
                {
                    if (board[row][col] != -1)
                    {
                        if (board[row][col] == board[last][col])
                        {
                            moves.add(new TileMove(row, col, TileMove.UP, row-last, board[row][col]));
                            board[last][col] = board[row][col] + 1;
                            score += SCORE_LUT[board[last][col]];
                            if (board[last][col] > highTile)
                                highTile = board[last][col];
                            board[row][col] = -1;
                            ++last;
                        }
                        else if (row > last && board[last][col] == -1)
                        {
                            moves.add(new TileMove(row, col, TileMove.UP, row-last, board[row][col]));
                            board[last][col] = board[row][col];
                            board[row][col] = -1;
                        }
                        else if (row > last+1)
                        {
                            moves.add(new TileMove(row, col, TileMove.UP, row-last-1, board[row][col]));
                            board[last+1][col] = board[row][col];
                            board[row][col] = -1;
                            ++last;
                        }
                        else
                        {
                            ++last;
                        }
                    }
                }
            }
        }
        else if (dir == TileMove.DOWN)
        {
            for (int col = 0; col < 4; ++ col)
            {
                int last = 3;
                for (int row = 2; row >= 0; --row)
                {
                    if (board[row][col] != -1)
                    {
                        if (board[row][col] == board[last][col])
                        {
                            moves.add(new TileMove(row, col, TileMove.DOWN, last-row, board[row][col]));
                            board[last][col] = board[row][col] + 1;
                            score += SCORE_LUT[board[last][col]];
                            if (board[last][col] > highTile)
                                highTile = board[last][col];
                            board[row][col] = -1;
                            --last;
                        }
                        else if (row < last && board[last][col] == -1)
                        {
                            moves.add(new TileMove(row, col, TileMove.DOWN, last-row, board[row][col]));
                            board[last][col] = board[row][col];
                            board[row][col] = -1;
                        }
                        else if (row < last-1)
                        {
                            moves.add(new TileMove(row, col, TileMove.DOWN, last-row-1, board[row][col]));
                            board[last-1][col] = board[row][col];
                            board[row][col] = -1;
                            --last;
                        }
                        else
                        {
                            --last;
                        }
                    }
                }
            }
        }

        if (moves.size() > 0)
        {
            // create a new tile
            int newVal = (int)(Math.random() * 4 / 3);
            int emptyCnt = 0;
            for (int row = 0; row < 4; ++row)
            {
                for (int col = 0; col < 4; ++col)
                {
                    if (board[row][col] == -1)
                        ++emptyCnt;
                }
            }

            int newLoc = (int)(Math.random() * emptyCnt);
            for (int row = 0; row < 4 && newLoc >= 0; ++row)
            {
                for (int col = 0; col < 4 && newLoc >= 0; ++col)
                {
                    if (board[row][col] == -1)
                    {
                        if (newLoc == 0)
                            board[row][col] = newVal;
                        --newLoc;
                    }
                }
            }
        }

        return moves;
    }
}
