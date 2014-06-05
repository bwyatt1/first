package bwyatt.game.client;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import org.apache.log4j.Logger;

import bwyatt.game.common.*;

public class GameSocketThread extends Thread
{
    SocketChannel channel;
    boolean die;
    GameFrame listener;
    String serverHostName;

    private static Logger logger = Logger.getLogger(GameSocketThread.class.getName());

    public GameSocketThread(GameFrame listener, String serverHostName)
    {
        die = false;
        this.serverHostName = serverHostName;
        this.listener = listener;
    }

    public void die()
    {
        try
        {
            die = true;
            synchronized (this)
            {
                if (this.channel != null)
                    this.channel.close();
                this.notifyAll();
            }
            this.join();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public synchronized void retryConnect()
    {
        this.notify();
    }

    public synchronized void setServer(String serverHostName)
    {
        if (this.serverHostName != serverHostName)
        {
            this.serverHostName = serverHostName;
            if (this.channel != null)
            {
                try
                {
                    this.channel.close();
                    this.channel = null;
                }
                catch (Exception e)
                {
                }
            }
            this.notify();
        }
    }
    
    public String getServer()
    {
        return serverHostName;
    }

    public void run()
    {
        ByteBuffer buf = ByteBuffer.allocate(1024);
        Message message;
        this.channel = null;
        while (!this.die)
        {
            if (this.channel == null || !this.channel.isConnected())
            {
                synchronized (this)
                {
                    try
                    {
                        this.channel = SocketChannel.open();
                        listener.setServerStatus(false);
                        this.channel.connect(new InetSocketAddress(serverHostName, 5000));
                        listener.setServerStatus(true);
                    }
                    catch (IOException e)
                    {
                        this.channel = null;
                        try
                        {
                            this.wait(30000);
                        }
                        catch (Exception e2)
                        {
                        }
                    }
                }
            }
            else
            {
                try
                {
                    buf.clear();
                    int bytesRead = this.channel.read(buf);
                    if (bytesRead == -1)
                    {
                        synchronized (this)
                        {
                            listener.setServerStatus(false);
                            this.channel = null;
                        }
                    }
                    else
                    {
                        buf.limit(buf.position());
                        buf.rewind();
                        logger.trace("Read (" + bytesRead + "):" + Message.getBytesAsString(buf.array(), 0, buf.limit()));
                        message = new Message();
                        int bytesParsed = message.parse(buf);
                        while (bytesParsed > 0)
                        {
                            listener.handleMessage(message);
                            bytesRead -= bytesParsed;
                            if (bytesRead > 0)
                                bytesParsed = message.parse(buf);
                            else
                                bytesParsed = 0;
                        }
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    System.out.flush();
                    this.channel = null;
                }
            }
        } 
    }

    public synchronized void sendMessage(Message message)
    {
        if (this.channel != null)
        {
            try
            {
                ByteBuffer buf = message.assemble();
                logger.trace("Write (" + buf.limit() + "):" + Message.getBytesAsString(buf.array(), 0, buf.limit()));
                System.out.flush();
                this.channel.write(buf);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
