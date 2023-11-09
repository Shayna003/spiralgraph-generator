package spirograph;

import java.awt.EventQueue;
import javax.swing.JFrame;

public class Main
{

  public static void main(String[] args)
  {
    EventQueue.invokeLater(() ->
    {
      JFrame frame = new GUI();
      frame.setVisible(true);
    });
  }
}