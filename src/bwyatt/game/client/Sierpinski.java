import java.awt.*;
import javax.swing.*;

public class Sierpinski extends JFrame
{

    public Sierpinski()
    {
        super("Sierpinski");

        this.setSize(800, 800);
        this.setVisible(true);
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
