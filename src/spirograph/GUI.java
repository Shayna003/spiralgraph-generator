package spirograph;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultFormatter;
import spirograph.Spirograph.InnerCircle;
import spirograph.Spirograph.PenPosition;

/**
 * SpirographPlotter contains a table of InnerCircle objects,
 * which contains an Arraylist of InnerCircle objects,
 * which cantain an Arraylist of PenPosition objects,
 * which contain pen offset and pen color.
 */
public class GUI extends JFrame {

  /**
   * Helper class for number inputs with value restrictions
   */
  public class NumberSpinner extends JSpinner {

    int defaultValue;

    public NumberSpinner(int minValue, int maxValue, int stepSize, int defaultValue,
        ChangeListener listener, int columns) {
      this.defaultValue = defaultValue;
      SpinnerNumberModel spinnerModel = new SpinnerNumberModel(defaultValue, minValue, maxValue,
          stepSize);
      setModel(spinnerModel);
      JComponent editorComponent = getEditor();
      JFormattedTextField field = (JFormattedTextField) editorComponent.getComponent(0);
      field.setColumns(columns);
      DefaultFormatter formatter = (DefaultFormatter) field.getFormatter();
      formatter.setCommitsOnValidEdit(true);
      formatter.setAllowsInvalid(false);
      addChangeListener(listener);
      setToolTipText("Please enter a value between " + minValue + " and " + maxValue + ".");
    }
  }

  // TODO: color support, multiple graphs support, time slider support;
  class SettingsPanel extends JPanel {

    NumberSpinner R_fixed; // fixed circle radius
    NumberSpinner r_spinning; // spinning circle radius
    //NumberSpinner offset;

    NumberSpinner time;
    JButton clear; // clear canvas
    JButton reset; // reset all fields and delete all spirographs
    //JButton stop; // pause/resume
    //JButton reset;
    // steps
    // duration
    // scale
    // line width
    // pen positions & colors
    // (remove spirograph)/multiple spirographs

    public SettingsPanel() {
      R_fixed = new NumberSpinner(0, 100, 1, 50, null, 4);
    }
  }

  SpiralgraphPlotter plotter;
  SettingsPanel settingsPanel;
  SpirographEditor editor;
  ArrayList<Spirograph> spirographs;

  public GUI()
  {
    spirographs = new ArrayList<>();
    settingsPanel = new SettingsPanel();
    plotter = new SpiralgraphPlotter();
    editor = new SpirographEditor(this);

    add(editor, BorderLayout.EAST);
    add(settingsPanel, BorderLayout.NORTH);
    add(plotter, BorderLayout.CENTER);

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setTitle("Spirograph Generator");
    pack();
    setLocationRelativeTo(null);
  }

  class SpiralgraphPlotter extends JComponent {

    volatile int step;
    @Override
    public void paintComponent(Graphics g) {
      Graphics2D g2 = (Graphics2D) g;
      for (Spirograph graph : spirographs) {
        for (InnerCircle circle : graph.inner_circles) {
          for (PenPosition pp : circle.pen_positions) {
            int upper_bound = pp.points_needed_to_draw(step);
            if (upper_bound < 0)
              continue;
            g2.setColor(pp.pen_color);
            for (int i = 0; i <= upper_bound; i++) {
              Point2D.Double p1 = i == 0 ? pp.points.get(0) : pp.points.get(i - 1);
              Point2D.Double p2 = pp.points.get(i);
              g2.drawLine((int) p1.x, (int) p1.y, (int) p2.x, (int) p2.y);
            }
          }
        }
      }
    }
  }
}