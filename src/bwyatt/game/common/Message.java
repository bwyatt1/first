package bwyatt.game.common;

import java.nio.*;
import java.util.*;
import org.apache.log4j.Logger;

public class Message
{
    public static final int MT_NONE = 0;
    public static final int MT_JOIN_CHAT = 1;
    public static final int MT_ID_UPDATE = 2;
    public static final int MT_NEW_PLAYER = 3;
    public static final int MT_PLAYER_CLOSED = 4;
    public static final int MT_PLAYER_LIST = 5;
    public static final int MT_CHAT = 6;
    public static final int MT_PLAYER_SHOWING_GAME = 10;
    public static final int MT_PLAYER_STATUS_UPDATE = 11;
    public static final int MT_PLAYER_INFO_CHANGE = 12;
    public static final int MT_PLAYER_ROOM_CHANGE = 13;
    public static final int MT_GAME_INSTANCE_JOIN = 14;
    public static final int MT_GAME_INSTANCE_LEAVE = 15;
    public static final int MT_GAME_INSTANCE_START_TIMER = 16;
    public static final int MT_GAME_INSTANCE_OVER = 17;
    public static final int MT_2048_NEW_SOLO = 20;
    public static final int MT_2048_NEW_MULTI = 21;
    public static final int MT_2048_BOARD_UPDATE = 22;
    public static final int MT_2048_MOVE = 23;
    public static final int MT_BOGGLE_NEW_MULTI = 40;
    public static final int MT_BOGGLE_BOARD_UPDATE = 41;
    public static final int MT_BOGGLE_NEW_WORD = 42;

    private ByteBuffer partialBuf;
    private int gameVersion;
    private int messageType;
    private int fromID;
    private String text;
    private int val;
    private Twenty48Board twenty48Board;
    private LinkedList<TileMove> twenty48Moves;
    private BoggleBoard boggleBoard;
    private PlayerInfo playerInfo;

    private static Logger logger = Logger.getLogger(Message.class.getName());

    private static final int FRAME_TAG = 0xCAFEBABE;

    public Message()
    {
        gameVersion = -1;
        messageType = MT_NONE;
        fromID = -1;
        text = null;
        val = -1;
        twenty48Board = null;
        twenty48Moves = null;
        boggleBoard = null;
        playerInfo = null;
        partialBuf = null;
    }

    /*
     * 0 Message Length
     * 4 Game Version
     * 8 Message Type
     * 12 From ID
     */
    public int parse(ByteBuffer buf)
    {
        int bufStart = buf.position();

        if (partialBuf != null)
        {
            logger.error("Parse: Continue partial buf");
            partialBuf.position(partialBuf.limit());
            partialBuf.put(buf);
            partialBuf.rewind();
            buf = partialBuf;
        }

        if (buf.limit() < 12)
        {
            // partial message
            partialBuf = buf;
            if (buf.limit() > 0)
                logger.error("Parse: too short " + buf.limit());
            return -1;
        }

        int messageLen = buf.getInt();
        if (messageLen > buf.limit())
        {
            partialBuf = buf;
            logger.error("Parse: too short for len " + messageLen + " > " + buf.limit());
            return -1;
        }

        gameVersion = buf.getInt();
        messageType = buf.getInt();
        fromID = buf.getInt();

        int nameLength;
        byte[] nameBytes;

        switch (messageType)
        {
            case MT_CHAT:
            case MT_BOGGLE_NEW_WORD:
                {
                    int textLength = buf.getInt();
                    byte[] textBytes = new byte[textLength];
                    buf.get(textBytes);
                    this.text = new String(textBytes);
                }
                break;
            case MT_ID_UPDATE:
            case MT_PLAYER_SHOWING_GAME:
            case MT_PLAYER_STATUS_UPDATE:
            case MT_GAME_INSTANCE_JOIN:
            case MT_GAME_INSTANCE_LEAVE:
            case MT_GAME_INSTANCE_START_TIMER:
            case MT_2048_NEW_MULTI:
            case MT_2048_MOVE:
            case MT_BOGGLE_NEW_MULTI:
                {
                    this.val = buf.getInt();
                }
                break;
            case MT_2048_NEW_SOLO:
            case MT_GAME_INSTANCE_OVER:
            case MT_PLAYER_CLOSED:
                break;
            case MT_2048_BOARD_UPDATE:
                {
                    int[][] board = new int[4][];
                    for (int row = 0; row < 4; ++row)
                    {
                        board[row] = new int[4];
                        for (int col = 0; col < 4; ++col)
                        {
                            board[row][col] = (int)buf.get();
                        }
                    }
                    twenty48Board = new Twenty48Board();
                    twenty48Board.setBoard(board);
                    twenty48Board.setScore(buf.getInt());
                    twenty48Moves = new LinkedList<TileMove>();
                    int numMoves = buf.getInt();
                    for (int i = 0; i < numMoves; ++i)
                    {
                        TileMove tileMove = new TileMove((int)buf.get(), (int)buf.get(), (int)buf.get(),
                                                         (int)buf.get(), (int)buf.get());
                        twenty48Moves.add(tileMove);
                    }
                }
                break;
            case MT_BOGGLE_BOARD_UPDATE:
                {
                    char[][] board = new char[4][];
                    for (int row = 0; row < 4; ++row)
                    {
                        board[row] = new char[4];
                        for (int col = 0; col < 4; ++col)
                        {
                            board[row][col] = (char)buf.get();
                        }
                    }
                    boggleBoard = new BoggleBoard();
                    boggleBoard.setBoard(board);
                }
                break;
            case MT_JOIN_CHAT:
            case MT_NEW_PLAYER:
            case MT_PLAYER_INFO_CHANGE:
                {
                    nameLength = buf.getInt();
                    nameBytes = new byte[nameLength];
                    buf.get(nameBytes);
                    this.playerInfo = new PlayerInfo();
                    this.playerInfo.setName(new String(nameBytes));
                    this.playerInfo.setIconID(buf.getInt());
                }
                break;
            case MT_PLAYER_ROOM_CHANGE:
                {
                    this.playerInfo = new PlayerInfo();
                    this.playerInfo.setRoomID(buf.getInt());
                    this.playerInfo.setStatus(buf.getInt());
                }
                break;
            case MT_PLAYER_LIST:
                {
                    nameLength = buf.getInt();
                    nameBytes = new byte[nameLength];
                    buf.get(nameBytes);
                    this.playerInfo = new PlayerInfo();
                    this.playerInfo.setName(new String(nameBytes));
                    this.playerInfo.setIconID(buf.getInt());
                    this.playerInfo.setShowing(buf.getInt());
                    this.playerInfo.setRoomID(buf.getInt());
                    this.playerInfo.setStatus(buf.getInt());
                }
                break;
            default:
                logger.error("parse: Unrecognized message type: " + messageType);
        }
        return buf.position()-bufStart;
    }

    public ByteBuffer assemble()
    {
        ByteBuffer buf = ByteBuffer.allocate(1024);

        // Message length placeholder
        buf.putInt(-1);

        buf.putInt(this.gameVersion);
        buf.putInt(this.messageType);
        buf.putInt(this.fromID);

        switch (this.messageType)
        {
            case MT_CHAT:
            case MT_BOGGLE_NEW_WORD:
                {
                    buf.putInt(this.text.length());
                    buf.put(this.text.getBytes());
                }
                break;
            case MT_ID_UPDATE:
            case MT_PLAYER_SHOWING_GAME:
            case MT_PLAYER_STATUS_UPDATE:
            case MT_GAME_INSTANCE_JOIN:
            case MT_GAME_INSTANCE_LEAVE:
            case MT_GAME_INSTANCE_START_TIMER:
            case MT_2048_NEW_MULTI:
            case MT_2048_MOVE:
            case MT_BOGGLE_NEW_MULTI:
                {
                    buf.putInt(this.val);
                }
                break;
            case MT_2048_NEW_SOLO:
            case MT_PLAYER_CLOSED:
            case MT_GAME_INSTANCE_OVER:
                break;
            case MT_2048_BOARD_UPDATE:
                {
                    for (int row = 0; row < 4; ++row)
                    {
                        for (int col = 0; col < 4; ++col)
                        {
                            buf.put((byte)(this.twenty48Board.getTile(row, col) & 0xff));
                        }
                    }
                    buf.putInt(this.twenty48Board.getScore());
                    if (this.twenty48Moves == null)
                    {
                        buf.putInt(0);
                    }
                    else
                    {
                        buf.putInt(this.twenty48Moves.size());
                        for (TileMove tileMove : this.twenty48Moves)
                        {
                            buf.put((byte)(tileMove.getRow() & 0xff));
                            buf.put((byte)(tileMove.getCol() & 0xff));
                            buf.put((byte)(tileMove.getDir() & 0xff));
                            buf.put((byte)(tileMove.getDist() & 0xff));
                            buf.put((byte)(tileMove.getVal() & 0xff));
                        }
                    }
                }
                break;
            case MT_BOGGLE_BOARD_UPDATE:
                {
                    for (int row = 0; row < 4; ++row)
                    {
                        for (int col = 0; col < 4; ++col)
                        {
                            buf.put((byte)(this.boggleBoard.get(row, col) & 0xff));
                        }
                    }
                }
                break;
            case MT_JOIN_CHAT:
            case MT_NEW_PLAYER:
            case MT_PLAYER_INFO_CHANGE:
                {
                    buf.putInt(this.playerInfo.getName().length());
                    buf.put(this.playerInfo.getName().getBytes());
                    buf.putInt(this.playerInfo.getIconID());
                }
                break;
            case MT_PLAYER_ROOM_CHANGE:
                {
                    buf.putInt(this.playerInfo.getRoomID());
                    buf.putInt(this.playerInfo.getStatus());
                }
                break;
            case MT_PLAYER_LIST:
                {
                    buf.putInt(this.playerInfo.getName().length());
                    buf.put(this.playerInfo.getName().getBytes());
                    buf.putInt(this.playerInfo.getIconID());
                    buf.putInt(this.playerInfo.getShowing());
                    buf.putInt(this.playerInfo.getRoomID());
                    buf.putInt(this.playerInfo.getStatus());
                }
                break;
            default:
                logger.error("create: Unrecognized message type: " + this.messageType);
        }

        // Put message length
        buf.limit(buf.position());
        buf.rewind();
        buf.putInt(buf.limit());
        buf.rewind();
        return buf;
    }

    public static String getBytesAsString(byte[] bytes, int offset, int length)
    {
        String ret = "";
        for (int i = offset; i < length; ++i)
        {
            if ((i-offset) % 4 == 0)
                ret += " ";
            ret += String.format("%02x", bytes[i]);
        }
        return ret;
    }
    
    public int getType()
    {
        return this.messageType;
    }

    public void setType(int type)
    {
        this.messageType = type;
    }

    public int getVersion()
    {
        return this.gameVersion;
    }

    public void setVersion(int version)
    {
        this.gameVersion = version;
    }

    public int getFromID()
    {
        return this.fromID;
    }

    public void setFromID(int fromID)
    {
        this.fromID = fromID;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public int getVal()
    {
        return val;
    }

    public void setVal(int val)
    {
        this.val = val;
    }

    public void setTwenty48Board(Twenty48Board board)
    {
        this.twenty48Board = board;
    }

    public Twenty48Board getTwenty48Board()
    {
        return this.twenty48Board;
    }

    public void setTwenty48Moves(LinkedList<TileMove> twenty48Moves)
    {
        this.twenty48Moves = twenty48Moves;
    }

    public LinkedList<TileMove> getTwenty48Moves()
    {
        return this.twenty48Moves;
    }

    public void setBoggleBoard(BoggleBoard board)
    {
        this.boggleBoard = boggleBoard;
    }

    public BoggleBoard getBoggleBoard(BoggleBoard board)
    {
        return this.boggleBoard;
    }

    public void setPlayerInfo(PlayerInfo playerInfo)
    {
        this.playerInfo = playerInfo;
    }

    public PlayerInfo getPlayerInfo()
    {
        return this.playerInfo;
    }
}
