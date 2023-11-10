package spirograph;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultFormatter;

import spirograph.Spirograph.InnerCircle;
import spirograph.Spirograph.InnerCircle.PenPosition;

/**
 * SpirographPlotter contains a table of InnerCircle objects,
 * which contains an Arraylist of InnerCircle objects,
 * which cantain an Arraylist of PenPosition objects,
 * which contain pen offset and pen color.
 */
public class GUI extends JFrame {

  JSlider step; // step slider

  /**
   * Helper class for number inputs with value restrictions
   */
  public static class NumberSpinner extends JSpinner {

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

  public int steps_for_completion()
  {
    //System.out.println("steps_for_completion: " + settingsPanel.steps_for_completion.getValue());
    return (int) settingsPanel.steps_for_completion.getValue();
  }
  class SettingsPanel extends JPanel
  {
    JLabel timeLabel;
    JLabel stepsLabel;
    JLabel stepTimeLabel;
    JButton redraw;

    //JButton generateRandom;

    NumberSpinner steps_for_completion; // steps needed for a spirograph's inner circle's pen position to complete its drawing, higher = better resolution
    NumberSpinner step_time; // time of each step, in milliseconds
    JButton animate;

    public SettingsPanel()
    {
      setLayout(new GridBagLayout());
      steps_for_completion = new NumberSpinner(100, 100000, 10000, 5000, null, 4);
      step_time = new NumberSpinner(1, 10, 1, 1, null, 4);
      step = new JSlider(0, 5000, 0);
      animate = new JButton("animate");
      animate.addActionListener(event ->
      {

      });

      step.addChangeListener(event ->
      {
        timeLabel.setText(String.format("Current Step: %7d", step.getValue()));
        plotter.repaint();
      });

      steps_for_completion.addChangeListener(event ->
      {
        double percent = step.getValue() / (double) step.getMaximum();
        step.setMaximum(steps_for_completion());
        step.setValue((int) (percent * step.getMaximum()));
        for (Spirograph spiral : spirographs)
        {
          spiral.recomputePoints();
        }
        plotter.repaint();
      });

      redraw = new JButton("redraw");

      timeLabel = new JLabel(String.format("Current Step: %7d", 0));
      stepsLabel = new JLabel("Steps for completing one drawing: ");
      stepTimeLabel = new JLabel("Time(ms) for each step: ");
      add(timeLabel, new GBC(0, 0).setAnchor(GBC.EAST));
      add(step, new GBC(1, 0, 2, 1));
      add(animate, new GBC(3, 0));
      add(stepsLabel, new GBC(0, 1).setAnchor(GBC.EAST));
      add(steps_for_completion, new GBC(1, 1).setAnchor(GBC.WEST));
      add(stepTimeLabel, new GBC(0, 2).setAnchor(GBC.EAST));
      add(step_time, new GBC(1, 2).setAnchor(GBC.WEST));
      add(redraw, new GBC(1, 3).setAnchor(GBC.WEST));
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
    add(new JScrollPane(plotter), BorderLayout.CENTER);

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setTitle("Spirograph Generator");
    pack();
    setLocationRelativeTo(null);
  }

  class SpiralgraphPlotter extends JComponent
  {

    @Override
    public Dimension getPreferredSize()
    {
      return new Dimension(500, 500);
    }

    @Override
    public void paintComponent(Graphics g)
    {
      Graphics2D g2 = (Graphics2D) g;
      for (Spirograph graph : spirographs)
      {
        g2.translate(graph.offset_x, graph.offset_y);
        for (InnerCircle circle : graph.inner_circles)
        {
          for (PenPosition pp : circle.pen_positions)
          {
            //int upper_bound = pp.points_needed_to_draw(step.getValue());
            //if (upper_bound < 0)
            //  continue;
            g2.setColor(pp.pen_color);
            for (int i = 0; i < Math.min(pp.points.size(), step.getValue() + 1); i++)
            {
              Point2D.Double p1 = i == 0 ? pp.points.get(0) : pp.points.get(i - 1);
              Point2D.Double p2 = pp.points.get(i);
              g2.drawLine((int) p1.x, (int) p1.y, (int) p2.x, (int) p2.y);
            }
          }
        }
        g2.translate(-graph.offset_x, -graph.offset_y);
      }
    }
  }
}