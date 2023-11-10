package spirograph;

import java.awt.EventQueue;
import javax.swing.JFrame;

public class Main
{
//TODO: add/edit/tree buttons, animate/change calculations in spiralgrphs/massive testing/generate random
  public static void main(String[] args)
  {
    EventQueue.invokeLater(() ->
    {
      JFrame frame = new GUI();
      frame.setVisible(true);
    });
  }
}