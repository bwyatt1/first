package bwyatt.game.common;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import org.apache.log4j.Logger;

public class BoggleBoard
{
    private static final char[][] LETTER_CUBE_4X4 = {
		{'A','A','E','E','G','N'},
		{'E','L','R','T','T','Y'},
		{'A','O','O','T','T','W'},
		{'A','B','B','J','O','O'},
		{'E','H','R','T','V','W'},
		{'C','I','M','O','T','U'},
		{'D','I','S','T','T','Y'},
		{'E','I','O','S','S','T'},
		{'D','E','L','R','V','Y'},
		{'A','C','H','O','P','S'},
		{'H','I','M','N','Q','U'},
		{'E','E','I','N','S','U'},
		{'E','E','G','H','N','W'},
		{'A','F','F','K','P','S'},
		{'H','L','N','N','R','Z'},
		{'D','E','I','L','R','X'}};

    private char[][] board;
    private ArrayList<String> wordList;

    private static Logger logger = Logger.getLogger(BoggleBoard.class.getName());

    public BoggleBoard()
    {
    }

    public void calculateWordList()
    {
        ArrayList<String> sowpods = new ArrayList<String>();
        LinkedList<String> unsortedWordList = new LinkedList<String>();
        try
        {
            InputStream in = this.getClass().getResourceAsStream("/bwyatt/game/client/resource/sowpods.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String nextWord = reader.readLine();
            while (nextWord != null)
            {
                sowpods.add(nextWord);
                nextWord = reader.readLine();
            }

            char[] word = new char[16];
            boolean marked[][] = new boolean[4][];
            for (int row = 0; row < 4; ++row)
            {
                marked[row] = new boolean[4];
                for (int col = 0; col < 4; ++col)
                {
                    marked[row][col] = false;
                }
            }

            for (int row = 0; row < 4; ++row)
            {
                for (int col = 0; col < 4; ++col)
                {
                    this.findWord(row, col, word, 0, marked, sowpods, 0, sowpods.size(), unsortedWordList);
                }
            }
        }
        catch (Exception e)
        {
            logger.error(e.toString());
        }

        // Sort word list
        this.wordList = new ArrayList<String>();
        ListIterator<String> iter;
        String curString;
        String lowString;
        while (unsortedWordList.size() > 0)
        {
            iter = unsortedWordList.listIterator();
            lowString = iter.next();
            while (iter.hasNext())
            {
                curString = iter.next();
                if (mycmp(curString, lowString) < 0)
                    lowString = curString;
            }
            unsortedWordList.remove(lowString);
            wordList.add(lowString);
        }
    }

    public void findWord(int row, int col, char[] word, int wordLen, boolean[][] marked, ArrayList<String> dictionary, int subStart, int subEnd, LinkedList<String> words)
    {
        word[wordLen] = this.board[row][col];
        wordLen++;
        
        int l, r, m, cmp;

        /* Calculate new subStart */
        l = subStart;
        r = subEnd;
        m = (l+r)/2;
        cmp = this.mycmp(word, wordLen, dictionary.get(m));
        while (l < r)
        {
            if (cmp < 0)
                r = m;
            else if (cmp > 0)
                l = m+1;
            else if (cmp == 0)
            {
                l = m;
                r = m;
            }
            m = (l+r)/2;
            cmp = this.mycmp(word, wordLen, dictionary.get(m));
        }
        if (cmp != -1 && cmp != 0) /* No substring match */
            return;
        subStart = l;
        if (cmp == 0 && wordLen >= 3)
        {
            words.add(dictionary.get(l));
        }

        /* Calculate new subEnd */
        r = subEnd;
        m = (l+r+1)/2;
        cmp = this.mycmp(word, wordLen, dictionary.get(m));
        while (l < r)
        {
            if (cmp == -2)
                r = m-1;
            else if (cmp >= -1)
                l = m;
            m = (l+r+1)/2;
            cmp = this.mycmp(word, wordLen, dictionary.get(m));
        }
        /* Guaranteed substring match */
        subEnd = l;
        
        marked[row][col] = true;
        if (row > 0)
        {
            if (!marked[row-1][col])
                findWord(row-1, col, word, wordLen, marked, dictionary, subStart, subEnd, words);
            if (col > 0 && !marked[row-1][col-1])
                findWord(row-1, col-1, word, wordLen, marked, dictionary, subStart, subEnd, words);
            if (col < 3 && !marked[row-1][col+1])
                findWord(row-1, col+1, word, wordLen, marked, dictionary, subStart, subEnd, words);
        }
        if (row < 3)
        {
            if (!marked[row+1][col])
                findWord(row+1, col, word, wordLen, marked, dictionary, subStart, subEnd, words);
            if (col > 0 && !marked[row+1][col-1])
                findWord(row+1, col-1, word, wordLen, marked, dictionary, subStart, subEnd, words);
            if (col < 3 && !marked[row+1][col+1])
                findWord(row+1, col+1, word, wordLen, marked, dictionary, subStart, subEnd, words);
        }
        if (col > 0 && !marked[row][col-1])
            findWord(row, col-1, word, wordLen, marked, dictionary, subStart, subEnd, words);
        if (col < 3 && !marked[row][col+1])
            findWord(row, col+1, word, wordLen, marked, dictionary, subStart, subEnd, words);
        marked[row][col] = false;
    }

    /*
     * RETURN CODES
     * -2 if a < b and no substring match
     * -1 if a is substring of b
     *  0 if match
     *  1 if b is substring of a
     *  2 if a > b and no substring match
     */
    public static int mycmp(char[] a, int aLen, String b)
    {
        int bLen = b.length();
        for (int i = 0; i < aLen && i < bLen; ++i)
        {
            if (a[i] < b.charAt(i))
                return -2;
            else if (a[i] > b.charAt(i))
                return 2;
        }
        if (aLen < bLen)
            return -1;
        if (aLen > bLen)
            return 1;
        return 0;
    }

    public static int mycmp(String a, String b)
    {
        int aLen = a.length();
        int bLen = b.length();
        for (int i = 0; i < aLen && i < bLen; ++i)
        {
            if (a.charAt(i) < b.charAt(i))
                return -2;
            else if (a.charAt(i) > b.charAt(i))
                return 2;
        }
        if (aLen < bLen)
            return -1;
        if (aLen > bLen)
            return 1;
        return 0;
    }

    public boolean contains(String word)
    {
        int l = 0;
        int r = wordList.size();
        int m = (l+r)/2;
        int cmp = mycmp(word, this.wordList.get(m));
        while (l < r)
        {
            if (cmp < 0)
                r = m;
            else if (cmp > 0)
                l = m+1;
            else if (cmp == 0)
            {
                l = m;
                r = m;
            }
            m = (l+r)/2;
            cmp = mycmp(word, this.wordList.get(m));
        }
        return word.equals(this.wordList.get(l));
    }

    public void generateNew()
    {
        int unusedIndex;
        int side;
        ArrayList<Integer> unused = new ArrayList<Integer>();
        for (int i = 0; i < 16; ++i)
            unused.add(new Integer(i));
        
        this.board = new char[4][];
        for (int row = 0; row < 4; ++row)
        {
            this.board[row] = new char[4];
            for (int col = 0; col < 4; ++col)
            {
                unusedIndex = (int)(Math.random() * unused.size());
                side = (int)(Math.random() * 6);
                this.board[row][col] = LETTER_CUBE_4X4[unused.get(unusedIndex).intValue()][side];
                unused.remove(unusedIndex);
            }
        }
    }

    public LinkedList<Point> findPath(String word, LinkedList<Point> path)
    {
        if (word == null || word.length() == 0)
            return null;

        LinkedList<Point> solution;
        boolean[][] marked;

        marked = new boolean[4][];
        for (int row = 0; row < 4; ++row)
        {
            marked[row] = new boolean[4];
            for (int col = 0; col < 4; ++col)
            {
                marked[row][col] = false;
            }
        }

        if (path == null)
        {
            path = new LinkedList<Point>();
            for (int row = 0; row < 4; ++row)
            {
                for (int col = 0; col < 4; ++col)
                {
                    if (board[row][col] == word.charAt(0))
                    {
                        path.addLast(new Point(col, row));
                        marked[row][col] = true;
                        solution = findPath(word, path, marked);
                        if (solution != null)
                            return solution;
                        marked[row][col] = false;
                        path.removeLast();
                    }
                }
            }
        }
        else
        {
            solution = findPath(word, path, marked);
            if (solution != null)
                return solution;
        }
        return null;
    }

    protected LinkedList<Point> findPath(String word, LinkedList<Point> path, boolean[][] marked)
    {
        LinkedList<Point> solution;

        if (word.length() == path.size())
            return path;
        int row = path.getLast().y;
        int col = path.getLast().x;
        char next = word.charAt(path.size());
        if (row > 0)
        {
            if (!marked[row-1][col] && board[row-1][col] == next)
            {
                path.addLast(new Point(col, row-1));
                marked[row-1][col] = true;
                solution = findPath(word, path, marked);
                if (solution != null)
                    return solution;
                marked[row-1][col] = false;
                path.removeLast();
            }
            if (col > 0 && !marked[row-1][col-1] && board[row-1][col-1] == next)
            {
                path.addLast(new Point(col-1, row-1));
                marked[row-1][col-1] = true;
                solution = findPath(word, path, marked);
                if (solution != null)
                    return solution;
                marked[row-1][col-1] = false;
                path.removeLast();
            }
            if (col < 3 && !marked[row-1][col+1] && board[row-1][col+1] == next)
            {
                path.addLast(new Point(col+1, row-1));
                marked[row-1][col+1] = true;
                solution = findPath(word, path, marked);
                if (solution != null)
                    return solution;
                marked[row-1][col+1] = false;
                path.removeLast();
            }
        }
        if (row < 3)
        {
            if (!marked[row+1][col] && board[row+1][col] == next)
            {
                path.addLast(new Point(col, row+1));
                marked[row+1][col] = true;
                solution = findPath(word, path, marked);
                if (solution != null)
                    return solution;
                marked[row+1][col] = false;
                path.removeLast();
            }
            if (col > 0 && !marked[row+1][col-1] && board[row+1][col-1] == next)
            {
                path.addLast(new Point(col-1, row+1));
                marked[row+1][col-1] = true;
                solution = findPath(word, path, marked);
                if (solution != null)
                    return solution;
                marked[row+1][col-1] = false;
                path.removeLast();
            }
            if (col < 3 && !marked[row+1][col+1] && board[row+1][col+1] == next)
            {
                path.addLast(new Point(col+1, row+1));
                marked[row+1][col+1] = true;
                solution = findPath(word, path, marked);
                if (solution != null)
                    return solution;
                marked[row+1][col+1] = false;
                path.removeLast();
            }
        }
        if (col > 0 && !marked[row][col-1] && board[row][col-1] == next)
        {
            path.addLast(new Point(col-1, row));
            marked[row][col-1] = true;
            solution = findPath(word, path, marked);
            if (solution != null)
                return solution;
            marked[row][col-1] = false;
            path.removeLast();
        }
        if (col < 3 && !marked[row][col+1] && board[row][col+1] == next)
        {
            path.addLast(new Point(col+1, row));
            marked[row][col+1] = true;
            solution = findPath(word, path, marked);
            if (solution != null)
                return solution;
            marked[row][col+1] = false;
            path.removeLast();
        }
        return null;
    }

    public void setBoard(char[][] board)
    {
        this.board = new char[4][];
        for (int row = 0; row < 4; ++row)
        {
            this.board[row] = new char[4];
            for (int col = 0; col < 4; ++col)
            {
                this.board[row][col] = board[row][col];
            }
        }
    }

    public char[][] getBoard()
    {
        return this.board;
    }

    public char get(int row, int col)
    {
        return this.board[row][col];
    }

    public String toString()
    {
        String ret = "";
        for (int row = 0; row < 4; ++row)
        {
            for (int col = 0; col < 4; ++col)
                ret += this.board[row][col];
            ret += "\n";
        }
        return ret;
    }
}
