package bwyatt.game.client.boggle;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import bwyatt.game.client.*;

public class BoggleFrame extends JFrame
{
    private BogglePanel bogglePanel;

    public BoggleFrame()
    {
        super("Bill's Boggle");

        bogglePanel = new BogglePanel();
        this.getContentPane().add(bogglePanel);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        JMenuItem fileNewItem = new JMenuItem("New");
        fileNewItem.setMnemonic(KeyEvent.VK_N);
        fileNewItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        fileNewItem.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    bogglePanel.newGame();
                }
            }
        );
        JMenuItem fileExitItem = new JMenuItem("Exit");
        fileExitItem.setMnemonic(KeyEvent.VK_X);
        fileExitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        fileExitItem.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    close();
                }
            }
        );

        fileMenu.add(fileNewItem);
        fileMenu.add(fileExitItem);
        menuBar.add(fileMenu);
        
        this.setJMenuBar(menuBar);
    }


    public void close()
    {
        System.exit(0);
    }

    public void newGame()
    {
        bogglePanel.newGame();
    }

    public static void main(String[] args)
    {
        try
        {
            final BoggleFrame app = new BoggleFrame();
            app.addWindowListener(new WindowAdapter()
                {
                    public void windowClosing(WindowEvent e)
                    {
                        app.close();
                    }
                }
            );
            app.setLocation(300, 300);
            app.setSize(800, 600);
            app.setVisible(true);
            app.newGame();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
