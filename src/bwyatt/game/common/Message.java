package bwyatt.game.common;

import java.nio.*;

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
    public static final int MT_GAME_INSTANCE_JOIN = 13;
    public static final int MT_2048_NEW_SOLO = 20;
    public static final int MT_2048_NEW_MULTI = 21;
    public static final int MT_2048_BOARD_UPDATE = 22;
    public static final int MT_BOGGLE_NEW_GAME = 40;
    public static final int MT_BOGGLE_SUBMIT_WORD = 41;

    ByteBuffer partialBuf;
    private int gameVersion;
    private int messageType;
    private int fromID;
    private String text;
    private int val;
    private Twenty48Board twenty48Board;
    private PlayerInfo playerInfo;

    private final int FRAME_TAG = 0xCAFEBABE;

    public Message()
    {
        gameVersion = -1;
        messageType = MT_NONE;
        fromID = -1;
        text = null;
        partialBuf = null;
    }

    public Message(int type, int fromID, String text)
    {
        this.messageType = type;
        this.fromID = fromID;
        this.text = text;
        this.val = -1;
        this.twenty48Board = null;
        this.gameVersion = -1;
        this.partialBuf = null;
    }

    /*
     * 0 Message Length
     * 4 Game Version
     * 8 Message Type
     * 12 From ID
     */
    public int parse(ByteBuffer buf)
    {
        int bytesParsed = 0;

        if (partialBuf != null)
        {
            System.out.println("Parse: Continue partial buf");
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
                System.out.println("Parse: too short " + buf.limit());
            return -1;
        }

        int messageLen = buf.getInt();
        if (messageLen > buf.limit())
        {
            partialBuf = buf;
            System.out.println("Parse: too short for len " + messageLen + " > " + buf.limit());
            return -1;
        }

        gameVersion = buf.getInt();
        messageType = buf.getInt();
        fromID = buf.getInt();

        bytesParsed += 16;

        switch (messageType)
        {
            case MT_CHAT:
            case MT_BOGGLE_SUBMIT_WORD:
                int textLength = buf.getInt();
                byte[] textBytes = new byte[textLength];
                buf.get(textBytes);
                this.text = new String(textBytes);
                bytesParsed += 4 + textLength;
                break;
            case MT_ID_UPDATE:
            case MT_PLAYER_SHOWING_GAME:
            case MT_PLAYER_STATUS_UPDATE:
                this.val = buf.getInt();
                bytesParsed += 4;
                break;
            case MT_2048_NEW_SOLO:
            case MT_2048_NEW_MULTI:
            case MT_PLAYER_CLOSED:
                break;
            case MT_2048_BOARD_UPDATE:
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
                bytesParsed += 4*4*4;
                break;
            case MT_JOIN_CHAT:
            case MT_NEW_PLAYER:
            case MT_PLAYER_LIST:
            case MT_PLAYER_INFO_CHANGE:
                int nameLength = buf.getInt();
                byte[] nameBytes = new byte[nameLength];
                buf.get(nameBytes);
                this.playerInfo = new PlayerInfo();
                this.playerInfo.setName(new String(nameBytes));
                this.playerInfo.setIconID(buf.getInt());
                bytesParsed += 8 + nameLength;
                break;
            default:
                System.out.println("parse: Unrecognized message type: " + messageType);
        }
        return bytesParsed;
    }

    public ByteBuffer create()
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
            case MT_BOGGLE_SUBMIT_WORD:
                buf.putInt(this.text.length());
                buf.put(this.text.getBytes());
                break;
            case MT_ID_UPDATE:
            case MT_PLAYER_SHOWING_GAME:
            case MT_PLAYER_STATUS_UPDATE:
                buf.putInt(this.val);
                break;
            case MT_2048_NEW_SOLO:
            case MT_2048_NEW_MULTI:
            case MT_PLAYER_CLOSED:
                break;
            case MT_2048_BOARD_UPDATE:
                for (int row = 0; row < 4; ++row)
                {
                    for (int col = 0; col < 4; ++col)
                    {
                        buf.put((byte)(this.twenty48Board.getTile(row, col) & 0xff));
                    }
                }
                break;
            case MT_JOIN_CHAT:
            case MT_NEW_PLAYER:
            case MT_PLAYER_LIST:
            case MT_PLAYER_INFO_CHANGE:
                buf.putInt(this.playerInfo.getName().length());
                buf.put(this.playerInfo.getName().getBytes());
                buf.putInt(this.playerInfo.getIconID());
                break;
            default:
                System.out.println("create: Unrecognized message type: " + this.messageType);
        }

        // Put message length
        buf.limit(buf.position());
        buf.rewind();
        buf.putInt(buf.limit());
        buf.rewind();
        return buf;
    }

    public static void printBytes(byte[] bytes, int offset, int length)
    {
        for (int i = offset; i < length; ++i)
        {
            if ((i-offset) % 4 == 0)
                System.out.print(" ");
            System.out.printf("%02x", bytes[i]);
        }
        System.out.println();
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

    public void setPlayerInfo(PlayerInfo playerInfo)
    {
        this.playerInfo = playerInfo;
    }

    public PlayerInfo getPlayerInfo()
    {
        return this.playerInfo;
    }
}
