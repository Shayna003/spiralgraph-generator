package spirograph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.beans.EventHandler;
import java.util.ArrayList;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.text.DefaultFormatter;
import spirograph.Spirograph.InnerCircle;
import spirograph.Spirograph.PenPosition;

/**
 * SpirographPlotter contains a table of InnerCircle objects,
 * which contains an Arraylist of InnerCircle objects,
 * which cantain an Arraylist of PenPosition objects,
 * which contain pen offset and pen color.
 */
public class SpirographPlotter extends JFrame {

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

  //Plotter plotter;
  SettingsPanel settingsPanel;

  public SpirographPlotter() {
    //settingsPanel = new SettingsPanel();
    //plotter = new Plotter();
    //add(settingsPanel, BorderLayout.NORTH);
    //add(plotter, BorderLayout.CENTER);

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setTitle("spirograph.Spirograph Plotter");
    pack();
    setLocationRelativeTo(null);
  }

  class Plotter extends JComponent {

    volatile int step;
    ArrayList<Spirograph> spirographs;

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