package spirograph;

import static java.lang.Math.*;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;

/**
 * Encapsulates values for a spirograph.
 * Enables drawing multiple spirographs at once.
 */
public class Spirograph
{
  GUI gui;
  int outer_radius;
  //int r;
  int offset_x;
  int offset_y;
  double rotations; // rotations needed to complete the spirograph

  // fixed once assigned, time needed to complete one rotation,
  //total time to finish drawing the spirograph is this * rotations * number of pen positions.
  double time_for_rotation;

  // fixed once assigned, steps needed to complete one rotation,
  // total steps for spirograph = this * rotations * number of pen positions
  int steps_for_rotation;

  ArrayList<InnerCircle> inner_circles;

  public InnerCircle addInnerCircle(ArrayList<PenPosition> pen_positions, int inner_radius)
  {
    InnerCircle circle = new InnerCircle(pen_positions, inner_radius);
    inner_circles.add(circle);
    return circle;
  }

  public class InnerCircle
  {
    int inner_radius;
    ArrayList<PenPosition> pen_positions;

    public InnerCircle(ArrayList<PenPosition> pen_positions, int inner_radius)
    {
      this.pen_positions = pen_positions;
      this.inner_radius = inner_radius;
    }

    public PenPosition addPenPosition(int offset, Color color)
    {
      PenPosition pp = new PenPosition(pen_positions.size(), offset, color);
      pen_positions.add(pp);
      return pp;
    }
  }
  /**
   * Records pen position and color
   */
  public class PenPosition
  {
    int index;
    int offset;
    Color pen_color;
    //double rotations;
    ArrayList<Point2D.Double> points; // used for painting

    public PenPosition(int index, int offset, Color pen_color)
    {
      this.index = index;
      this.offset = offset;
      this.pen_color = pen_color;
      compute_points();
    }

    /**
     * All drawing points are precomputed and fetched according to step number
     */
    void compute_points()
    {
      points = new ArrayList<>();
      for (int step = 1; step < steps_for_rotation + 1; step++)
      {
        points.add(computePoint(step));
      }
    }

    /*
     * @return whether this pen position needs to be drawn at the given step
     */
    public boolean need_to_draw(int step)
    {
      int current_pp_index = (int) (Math.ceil(step / (steps_for_rotation * rotations)) - 1);
      return  current_pp_index >= index;
    }

    /*
     * @return index of points needed to be drawn at given step, upper bound inclusive
     */
    public int points_needed_to_draw(int step)
    {
      double steps = step - index * (steps_for_rotation * rotations);
      if (steps <= 0) return -1;
      if (steps >= points.size()) return points.size() - 1;
      else return (int) Math.ceil(steps);
    }

    Point2D.Double computePoint(int step)
    {
      double t = time_for_rotation * step / steps_for_rotation;
      //double x = (R-r)*cos(t) + offset*cos(((R-r)/r)*t);
      //double y = (R-r)*sin(t) - offset*sin(((R-r)/r)*t);
      return new Point2D.Double(0, 0);
    }
  }

  /**
   * @return least common multiple of 2 numbers
   */
  public static int lcm(int n1, int n2)
  {
    if (n1 == 0 || n2 == 0) { return 0; }
    n1 = Math.abs(n1);
    n2 = Math.abs(n1);
    int higher = Math.max(n1, n2);
    int lower = Math.min(n1, n2);
    int lcm = higher;
    while (lcm % lower != 0) { lcm += higher; }
    return lcm;
  }

  public Spirograph(GUI gui, int outer_radius, int offset_x, int offset_y, ArrayList<InnerCircle> inner_circles)
  {
    this.outer_radius = outer_radius;
    this.offset_x = offset_x;
    this.offset_y = offset_y;
    //this.time_for_rotation = time_for_rotation;
    //this.steps_for_rotation = steps_for_rotation;
    this.inner_circles = inner_circles;
    //this.rotations = lcm(r, R) / (double) r;
  }

  /*Point2D.Double computePoint(int t)
  {
    int pen_position = (int) (Math.ceil(t / time_for_rotation)) - 1;
    int O = pen_positions.get(pen_position).offset;
    double x = (R-r)*cos(t) + O*cos(((R-r)/r)*t);
    double y = (R-r)*sin(t) - O*sin(((R-r)/r)*t);
    return new Point2D.Double(x, y);
  }*/
}

