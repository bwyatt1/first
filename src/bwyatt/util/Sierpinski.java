import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;

public class Sierpinski extends JFrame implements ActionListener
{
    private JTextField iterationsField;
    private JTextField startXField;
    private JTextField startYField;
    private JTextField deltaField;
    private JTextField widthField;
    private JTextField heightField;
    private JButton createButton;

    public Sierpinski()
    {
        super("Sierpinski");

        JLabel iterationsLabel = new JLabel("Iterations:");
        JLabel startXLabel = new JLabel("Start X:");
        JLabel startYLabel = new JLabel("Start Y:");
        JLabel deltaLabel = new JLabel("Delta:");
        JLabel widthLabel = new JLabel("Width:");
        JLabel heightLabel = new JLabel("Height:");

        iterationsField = new JTextField("1");
        startXField = new JTextField("400");
        startYField = new JTextField("400");
        deltaField = new JTextField("50");
        widthField = new JTextField("800");
        heightField = new JTextField("800");
        createButton = new JButton("Create");
        iterationsField.addActionListener(this);
        startXField.addActionListener(this);
        startYField.addActionListener(this);
        deltaField.addActionListener(this);
        widthField.addActionListener(this);
        heightField.addActionListener(this);
        createButton.addActionListener(this);

        this.setLayout(new GridLayout(7, 2));
        this.add(iterationsLabel);
        this.add(iterationsField);
        this.add(startXLabel);
        this.add(startXField);
        this.add(startYLabel);
        this.add(startYField);
        this.add(deltaLabel);
        this.add(deltaField);
        this.add(widthLabel);
        this.add(widthField);
        this.add(heightLabel);
        this.add(heightField);
        this.add(createButton);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(400, 250);
        this.setVisible(true);
    }

    public BufferedImage createDragon(int iterations, int startX, int startY, double delta,
                                      int width, int height)
    {
        LinkedList<byte[]> allEdges = new LinkedList<byte[]>();
        byte[] edges = new byte[1];
        edges[0] = 1;
        allEdges.add(edges);
        byte[] oldEdges;
        for (int i = 0; i < iterations; ++i)
        {
            oldEdges = edges;
            edges = new byte[oldEdges.length*2];
            edges[0] = 1;
            int j = edges.length-1;
            for (byte[] edge : allEdges)
            {
                for (byte b : edge)
                {
                    edges[j] = (byte)(1 - b);
                    --j;
                }
            }
            allEdges.add(edges);
        }

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.BLACK);
        int dir = 0;
        double x = startX+delta;
        double y = startY;
        double ox = startX;
        double oy = startY;
        for (byte[] edge : allEdges)
        {
            for (byte b : edge)
            {
                graphics.draw(new Line2D.Double(ox, oy, x, y));
                ox = x;
                oy = y;
                if ((dir == 0 && b == 0) || (dir == 2 && b == 1))
                {
                    y -= delta;
                    dir = 1;
                }
                else if ((dir == 1 && b == 0) || (dir == 3 && b == 1))
                {
                    x -= delta;
                    dir = 2;
                }
                else if ((dir == 2 && b == 0) || (dir == 0 && b == 1))
                {
                    y += delta;
                    dir = 3;
                }
                else
                {
                    x += delta;
                    dir = 0;
                }
            }
        }
        graphics.draw(new Line2D.Double(ox, oy, x, y));

        return image;
    }

    public void actionPerformed(ActionEvent event)
    {
        if (event.getSource() == createButton)
        {
            try
            {
                int iterations = Integer.parseInt(iterationsField.getText());
                int startX = Integer.parseInt(startXField.getText());
                int startY = Integer.parseInt(startYField.getText());
                double delta = Double.parseDouble(deltaField.getText());
                int width = Integer.parseInt(widthField.getText());
                int height = Integer.parseInt(heightField.getText());
                BufferedImage image = createDragon(iterations, startX, startY, delta, width, height);

                JFrame frame = new JFrame("" + iterations);
                frame.setLayout(new BorderLayout());
                frame.getContentPane().add(new JLabel(new ImageIcon(image)), BorderLayout.CENTER);
                frame.pack();
                frame.setVisible(true);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args)
    {
        try
        {
            Sierpinski app = new Sierpinski();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
