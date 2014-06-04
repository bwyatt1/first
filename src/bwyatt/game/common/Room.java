package bwyatt.game.common;

import java.util.*;

public class Room
{
    protected int duration;
    protected int id;

    protected static ArrayList<Room> roomList;

    public static final Room ONE_MINUTE = new Room(1, 60);
    public static final Room THREE_MINUTES = new Room(3, 180);
    public static final Room TEN_MINUTES = new Room(10, 600);


    protected Room(int id, int duration)
    {
        this.id = id;
        this.duration = duration;
    }

    public static void init()
    {
        roomList = new ArrayList<Room>();
        roomList.add(ONE_MINUTE);
        roomList.add(THREE_MINUTES);
        roomList.add(TEN_MINUTES);
    }

    public static Room getRoomFromID(int id)
    {
        for (Room room : roomList)
        {
            if (room.getID() == id)
                return room;
        }
        return null;
    }

    public int getID()
    {
        return this.id;
    }

    public int getDuration()
    {
        return this.duration;
    }
}
