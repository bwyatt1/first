package bwyatt.game.common;

public class TileMove
{
    int row;
    int col;
    int dir;
    int dist;
    int val;

    public static int LEFT = 0;
    public static int DOWN = 1;
    public static int RIGHT = 2;
    public static int UP = 3;

    public TileMove(int row, int col, int dir, int dist, int val)
    {
        this.row = row;
        this.col = col;
        this.dir = dir;
        this.dist = dist;
        this.val = val;
    }

    public int getRow()
    {
        return this.row;
    }

    public int getCol()
    {
        return this.col;
    }

    public int getDir()
    {
        return this.dir;
    }

    public int getDist()
    {
        return this.dist;
    }

    public int getVal()
    {
        return this.val;
    }
}
