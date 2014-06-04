package bwyatt.game.server;

import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

import bwyatt.game.common.*;

public class GameServer extends Thread
{
    private Selector selector;
    private LinkedList<ClientInfo> connectedClients;
    private LinkedList<Twenty48Instance> twenty48Games;
    private LinkedList<BoggleInstance> boggleGames;
    private boolean die;
    private int nextClientID;
    private int nextInstanceID;

    public static final int SERVER_ID = -1;

    public GameServer()
    {
        this.selector = null;
        this.die = false;
        this.connectedClients = new LinkedList<ClientInfo>();
        this.twenty48Games = new LinkedList<Twenty48Instance>();
        this.boggleGames = new LinkedList<BoggleInstance>();
        this.nextClientID = 1;
        this.nextInstanceID = 1;

        Room.init();
    }

    public void die()
    {
        try
        {
            this.die = true;
            this.selector.wakeup();
            this.join();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void run()
    {
        Set<SelectionKey> selected;
        Iterator<SelectionKey> iter;
        SelectionKey key;

        try
        {
            this.selector = Selector.open();
            ServerSocketChannel serverSocket = ServerSocketChannel.open();
            serverSocket.bind(new InetSocketAddress(5000));
            serverSocket.configureBlocking(false);
            serverSocket.register(this.selector, SelectionKey.OP_ACCEPT);
            System.out.println("Listening on port 5000");
            this.selector.select();
            while (!this.die)
            {
                selected = selector.selectedKeys();
                iter = selected.iterator();
                while (iter.hasNext())
                {
                    key = iter.next();
                    if (key.isAcceptable())
                    {
                        SocketChannel channel = ((ServerSocketChannel)key.channel()).accept();
                        if (channel != null)
                        {
                            channel.configureBlocking(false);
                            channel.register(this.selector, SelectionKey.OP_READ);
                            ClientInfo newClient = new ClientInfo();
                            newClient.setChannel(channel);
                            connectedClients.add(newClient);
                            System.out.println("New connection from: " + channel.getRemoteAddress().toString());

                            PlayerInfo playerInfo = new PlayerInfo();
                            playerInfo.setID(nextClientID);
                            newClient.setPlayer(playerInfo);

                            Message message = new Message();
                            message.setType(Message.MT_ID_UPDATE);
                            message.setFromID(SERVER_ID);
                            message.setVal(nextClientID);
                            sendMessage(newClient, message);
                            nextClientID++;

                        }
                        else
                        {
                            System.out.println("Accepted null channel");
                        }
                    }
                    else if (key.isReadable())
                    {
                        SocketChannel channel = (SocketChannel)key.channel();
                        ClientInfo fromInfo = getClient(channel);
                        ByteBuffer buf = ByteBuffer.allocate(1024);
                        int bytesRead = -1;
                        try
                        {
                            bytesRead = channel.read(buf);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        if (bytesRead == -1)
                        {
                            System.out.println("READ -1 bytes, removing client " + fromInfo.getPlayer().getName() +
                                    "(" + channel.getRemoteAddress().toString() + ")");
                            key.cancel();
                            connectedClients.remove(fromInfo);
                            Message message = new Message();
                            message.setType(Message.MT_PLAYER_CLOSED);
                            message.setFromID(fromInfo.getPlayer().getID());
                            broadcast(message);
                        }
                        else
                        {
                            buf.limit(buf.position());
                            buf.rewind();
                            System.out.print("READ " + fromInfo.getPlayer().getName() + " (" + bytesRead + "):");
                            Message.printBytes(buf.array(), 0, buf.limit());

                            Message inMessage = new Message();
                            int bytesParsed = inMessage.parse(buf);
                            if (bytesParsed > 0)
                            {
                                handleMessage(fromInfo, inMessage);
                            }
                            else
                            {
                                //System.out.println("Partial message from ");
                            }
                        }

                    }
                        
                }
                this.selector.select();
            }
            this.selector.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void handleMessage(ClientInfo client, Message inMessage)
    {
        switch (inMessage.getType())
        {
            case Message.MT_JOIN_CHAT:
                {
                    System.out.println("MT_JOIN_CHAT (" + inMessage.getFromID() + "): " + inMessage.getPlayerInfo().getName());
                    client.getPlayer().setName(inMessage.getPlayerInfo().getName());
                    client.getPlayer().setIconID(inMessage.getPlayerInfo().getIconID());

                    // announce new player join
                    Message outMessage = new Message();
                    outMessage.setType(Message.MT_NEW_PLAYER);
                    outMessage.setFromID(client.getPlayer().getID());
                    outMessage.setPlayerInfo(client.getPlayer());
                    broadcast(outMessage);

                    // send player list to the new player
                    for (ClientInfo info : connectedClients)
                    {
                        if (info != client)
                        {
                            outMessage = new Message();
                            outMessage.setType(Message.MT_PLAYER_LIST);
                            outMessage.setFromID(info.getPlayer().getID());
                            outMessage.setPlayerInfo(info.getPlayer());
                            sendMessage(client, outMessage);
                        }
                    }
                }
                break;
            case Message.MT_CHAT:
                {
                    System.out.println("MT_CHAT (" + inMessage.getFromID() + "): " + inMessage.getText());

                    Message outMessage = new Message();
                    outMessage.setType(Message.MT_CHAT);
                    outMessage.setFromID(client.getPlayer().getID());
                    outMessage.setText(inMessage.getText());
                    broadcast(outMessage);
                }
                break;
            case Message.MT_PLAYER_INFO_CHANGE:
                {
                    System.out.println("MT_PLAYER_INFO_CHANGE (" + inMessage.getFromID() + ")");

                    client.getPlayer().setName(inMessage.getPlayerInfo().getName());

                    Message outMessage = new Message();
                    outMessage.setType(Message.MT_PLAYER_INFO_CHANGE);
                    outMessage.setFromID(client.getPlayer().getID());
                    outMessage.setPlayerInfo(inMessage.getPlayerInfo());
                    broadcast(outMessage);
                }
                break;
            case Message.MT_PLAYER_SHOWING_GAME:
                {
                    System.out.println("MT_PLAYER_SHOWING_GAME (" + inMessage.getFromID() + "): " + inMessage.getVal());
                    client.getPlayer().setShowing(inMessage.getVal());

                    Message outMessage = new Message();
                    outMessage.setType(Message.MT_PLAYER_SHOWING_GAME);
                    outMessage.setFromID(client.getPlayer().getID());
                    outMessage.setVal(inMessage.getVal());
                    broadcast(outMessage);
                }
                break;
            case Message.MT_PLAYER_ROOM_CHANGE:
                {
                    System.out.println("MT_PLAYER_ROOM_CHANGE (" + inMessage.getFromID() + "): " + inMessage.getPlayerInfo().toString());
                    client.getPlayer().setRoomID(inMessage.getPlayerInfo().getRoomID());
                    client.getPlayer().setStatus(inMessage.getPlayerInfo().getStatus());

                    Message outMessage = new Message();
                    outMessage.setType(Message.MT_PLAYER_ROOM_CHANGE);
                    outMessage.setFromID(client.getPlayer().getID());
                    outMessage.setPlayerInfo(client.getPlayer());
                    broadcast(outMessage);
                }
                break;
            case Message.MT_2048_NEW_MULTI:
                {
                    System.out.println("MT_2048_NEW_MULTI (" + inMessage.getFromID() + "): ");

                    Twenty48Instance instance = new Twenty48Instance(nextInstanceID);
                    ++nextInstanceID;

                    Message newMultiMessage = new Message();
                    newMultiMessage.setType(Message.MT_2048_NEW_MULTI);
                    newMultiMessage.setFromID(client.getPlayer().getID());
                    newMultiMessage.setVal(instance.getID());

                    // Find ready 2048 clients and change to active
                    for (ClientInfo info : connectedClients)
                    {
                        if (info.getPlayer().getShowing() == PlayerInfo.GAME_2048 &&
                            info.getPlayer().getRoomID() == client.getPlayer().getRoomID() &&
                            info.getPlayer().getStatus() == PlayerInfo.STATUS_READY)
                        {
                            instance.addClient(info);
                            info.setInstance(instance);
                            sendMessage(info, newMultiMessage);
                            info.getPlayer().setStatus(PlayerInfo.STATUS_ACTIVE);

                            Message statusMessage = new Message();
                            statusMessage.setType(Message.MT_PLAYER_ROOM_CHANGE);
                            statusMessage.setFromID(info.getPlayer().getID());
                            statusMessage.setPlayerInfo(info.getPlayer());
                            broadcast(statusMessage);
                        }
                    }

                    // Tell each player who else is in this instance
                    for (ClientInfo info : instance.getClients())
                    {
                        Message joinMessage = new Message();
                        joinMessage.setType(Message.MT_GAME_INSTANCE_JOIN);
                        joinMessage.setFromID(info.getPlayer().getID());
                        joinMessage.setVal(instance.getID());
                        instanceBroadcast(instance, joinMessage);
                    }

                    instance.newGame();

                    Message boardMessage = new Message();
                    boardMessage.setType(Message.MT_2048_BOARD_UPDATE);
                    for (ClientInfo info : instance.getClients())
                    {
                        boardMessage.setFromID(info.getPlayer().getID());
                        boardMessage.setTwenty48Board(instance.getBoard(info));
                        instanceBroadcast(instance, boardMessage);
                    }

                    Room room = Room.getRoomFromID(client.getPlayer().getRoomID());
                    instance.startTimer(room.getDuration());

                    Message timerMessage = new Message();
                    timerMessage.setType(Message.MT_GAME_INSTANCE_START_TIMER);
                    timerMessage.setFromID(SERVER_ID);
                    timerMessage.setVal(room.getDuration());
                    instanceBroadcast(instance, timerMessage);

                }
                break;
            case Message.MT_2048_MOVE:
                {
                    System.out.println("MT_2048_MOVE (" + inMessage.getFromID() + ")");
                    Twenty48Instance instance = (Twenty48Instance)client.getInstance();
                    if (instance != null)
                    {
                        LinkedList<TileMove> moves = instance.getBoard(client).move(inMessage.getVal());
                        if (moves != null)
                        {
                            Message outMessage = new Message();
                            outMessage.setType(Message.MT_2048_BOARD_UPDATE);
                            outMessage.setFromID(client.getPlayer().getID());
                            outMessage.setTwenty48Board(instance.getBoard(client));
                            outMessage.setTwenty48Moves(moves);
                            instanceBroadcast(instance, outMessage);
                        }
                    }
                    else
                    {
                        System.out.println("No instance found");
                    }
                }
                break;
            case Message.MT_GAME_INSTANCE_LEAVE:
                {
                    System.out.println("MT_GAME_INSTANCE_LEAVE (" + inMessage.getFromID() + ")");
                    GameInstance instance = client.getInstance();
                    if (instance != null)
                    {
                        client.setInstance(null);
                        instance.removeClient(client);
                        Message outMessage = new Message();
                        outMessage.setType(Message.MT_GAME_INSTANCE_LEAVE);
                        outMessage.setFromID(client.getPlayer().getID());
                        outMessage.setVal(client.getInstance().getID());
                        instanceBroadcast(instance, outMessage);
                    }
                }
                break;
            case Message.MT_GAME_INSTANCE_OVER:
                {
                    System.out.println("MT_GAME_INSTANCE_OVER (" + inMessage.getFromID() + ")");
                    GameInstance instance = client.getInstance();
                    if (instance != null)
                    {
                        Message outMessage = new Message();
                        outMessage.setType(Message.MT_GAME_INSTANCE_OVER);
                        outMessage.setFromID(client.getPlayer().getID());
                        instanceBroadcast(instance, outMessage);
                    }
                }
                break;
            case Message.MT_BOGGLE_SUBMIT_WORD:
                {
                    System.out.println("MT_BOGGLE_SUBMIT_WORD (" + inMessage.getFromID() + "): " + inMessage.getText());
                }
                break;
            default:
                System.out.println("handleMessage: Unrecognized type: " + inMessage.getType());
        }
    }

    public void sendMessage(ClientInfo client, Message message)
    {
        try
        {
            ByteBuffer buf = message.assemble();
            System.out.print("WRITE " + client.getPlayer().getName() + " (" + buf.limit() + "):");
            Message.printBytes(buf.array(), buf.position(), buf.limit());
            client.getChannel().write(buf);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void broadcast(Message message)
    {
        ByteBuffer buf = message.assemble();
        for (ClientInfo info : this.connectedClients)
        {
            try
            {
                System.out.print("WRITE " + info.getPlayer().getName() + " (" + buf.limit() + "):");
                Message.printBytes(buf.array(), buf.position(), buf.limit());
                info.getChannel().write(buf);
                buf.rewind();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void instanceBroadcast(GameInstance instance, Message message)
    {
        for (ClientInfo client : instance.getClients())
        {
            sendMessage(client, message);
        }
    }

    public ClientInfo getClient(SocketChannel channel)
    {
        for (ClientInfo info : this.connectedClients)
        {
            if (info.getChannel() == channel)
                return info;
        }
        return null;
    }

    public static void main(String[] args)
    {
        GameServer server = new GameServer();
        server.start();
        byte[] buf = new byte[256];
        int bytesRead = 0;
        boolean quit = false;
        String line;

        try
        {
            while (!quit)
            {
                bytesRead = System.in.read(buf);
                line = new String(buf, 0, bytesRead);
                if (line.startsWith("/"))
                {
                    if (line.startsWith("/help"))
                    {
                        System.out.print("\t/quit - Quit\n" +
                                         "\t/list - List connected clients" +
                                         "\t/help - This info\n");
                    }
                    else if (line.startsWith("/list"))
                    {
                        for (ClientInfo client : server.connectedClients)
                        {
                            System.out.println(client.getPlayer().toString());
                        }
                        System.out.println();
                    }
                    else if (line.startsWith("/quit"))
                    {
                        System.out.print("Shutting down...");
                        server.die();
                        quit = true;
                    }
                }
                else
                {
                    // Chat
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
