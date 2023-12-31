package spirograph;

import static java.lang.Math.*;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Encapsulates values for a spirograph.
 * Enables drawing multiple spirographs at once.
 */
public class Spirograph
{
  GUI gui;
  int outer_radius;
  int offset_x;
  int offset_y;

  ArrayList<InnerCircle> inner_circles;

  public void recomputePoints()
  {
    for (InnerCircle circle : inner_circles)
    {
      circle.recomputePoints();
    }
  }
  public InnerCircle addInnerCircle(ArrayList<InnerCircle.PenPosition> pen_positions, int inner_radius)
  {
    InnerCircle circle = new InnerCircle(pen_positions, inner_radius);
    inner_circles.add(circle);
    return circle;
  }

  public class InnerCircle
  {
    int inner_radius;
    ArrayList<PenPosition> pen_positions;

    public class PenPosition
    {
      int index;
      int offset;
      Color pen_color;
      double rotations;
      ArrayList<Point2D.Double> points; // used for painting

      public PenPosition(int index, int offset, Color pen_color)
      {
        this.index = index;
        this.offset = offset;
        this.pen_color = pen_color;
        compute_points();
      }

      @Override
      public String toString()
      {
        return "pp, number of points: " + points.size() + System.lineSeparator() + points.toString();
      }

      /**
       * All drawing points are precomputed and fetched according to step number
       */
      void compute_points()
      {
        points = new ArrayList<>(gui.steps_for_completion() + 1);

        if (outer_radius % inner_radius == 0)
        {
          this.rotations = 1;
        }
        else this.rotations = lcm(inner_radius, outer_radius) / (double) inner_radius;

        for (int step = 0; step < gui.steps_for_completion() + 1; step++)
        {
          points.add(computePoint(step));
        }
      }

      Point2D.Double computePoint(int step)
      {
        double t = (2 * Math.PI * rotations * step) / ((double) (gui.steps_for_completion()));
        double x = (outer_radius-inner_radius)*cos(t) + offset*cos(((outer_radius-inner_radius)/(double)inner_radius)*t);
        double y = (outer_radius-inner_radius)*sin(t) - offset*sin(((outer_radius-inner_radius)/(double)inner_radius)*t);
        return new Point2D.Double(x, y);
      }
    }

    public InnerCircle(ArrayList<PenPosition> pen_positions, int inner_radius)
    {
      this.pen_positions = pen_positions;
      this.inner_radius = inner_radius;
    }

    public void recomputePoints()
    {
      for (PenPosition pp : pen_positions)
      {
        pp.compute_points();
      }
    }

    public PenPosition addPenPosition(int offset, Color color)
    {
      PenPosition pp = new PenPosition(pen_positions.size(), offset, color);
      pen_positions.add(pp);
      return pp;
    }
  }

  /**
   * @return least common multiple of 2 numbers
   */
  public static int lcm(int n1, int n2)
  {
    if (n1 == 0 || n2 == 0) { return 0; }
    n1 = Math.abs(n1);
    n2 = Math.abs(n2);
    int higher = Math.max(n1, n2);
    int lower = Math.min(n1, n2);
    int lcm = higher;
    while (lcm % lower != 0) { lcm += higher; }
    return lcm;
  }

  public Spirograph(GUI gui, int outer_radius, int offset_x, int offset_y, ArrayList<InnerCircle> inner_circles)
  {
    this.gui = gui;
    this.outer_radius = outer_radius;
    this.offset_x = offset_x;
    this.offset_y = offset_y;
    this.inner_circles = inner_circles;
  }
}

