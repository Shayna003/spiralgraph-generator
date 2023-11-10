package spirograph;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.IconUIResource;
import javax.swing.tree.*;
import java.awt.*;
import java.util.ArrayList;

import com.sun.source.tree.Tree;
import spirograph.Spirograph.InnerCircle;
import spirograph.Spirograph.InnerCircle.PenPosition;

public class SpirographEditor extends JPanel
{
  GUI gui;
  JButton addSpiralgraph;
  JButton addInnerCircle;
  JButton addPenPosition;
  JButton edit;
  JButton delete;

  JPanel buttonsPanel;
  JTree tree;
  SpirographTreeModel model;
  //DefaultMutableTreeNode selectedNode;
  SpiralGraphDialog spiralGraphDialog;
  InnerCircleDialog innerCircleDialog;
  PenPositionDialog penPositionDialog;

  class SpiralGraphDialog extends JDialog
  {
    JLabel labelx;
    JLabel labely;
    JLabel outer_radiusLabel;
    GUI.NumberSpinner offset_x;
    GUI.NumberSpinner offset_y;
    GUI.NumberSpinner outer_radius;
    JButton ok;
    JButton cancel;

    public SpiralGraphDialog()
    {
      super(gui, "Spiral Graph Editor", true);
      JPanel panel = new JPanel(new GridBagLayout());
      labelx = new JLabel("offset x: ");
      labely = new JLabel("offset y: ");
      outer_radiusLabel = new JLabel("outer circle radius: ");

      offset_x = new GUI.NumberSpinner(0, 1000, 50, 0, null, 4);
      offset_y = new GUI.NumberSpinner(0, 1000, 50, 0, null, 4);
      outer_radius = new GUI.NumberSpinner(10, 1000, 10, 300, null, 4);

      panel.add(labelx, new GBC(0, 0).setAnchor(GBC.EAST));
      panel.add(offset_x, new GBC(1, 0).setAnchor(GBC.WEST));
      panel.add(labely, new GBC(0, 1).setAnchor(GBC.EAST));
      panel.add(offset_y, new GBC(1, 1).setAnchor(GBC.WEST));
      panel.add(outer_radiusLabel, new GBC(0, 2).setAnchor(GBC.EAST));
      panel.add(outer_radius, new GBC(1, 2).setAnchor(GBC.WEST));

      ok = new JButton("ok");
      ok.addActionListener(event ->
      {
        ok_pressed = true;
        setVisible(false);
      });
      cancel = new JButton("cancel");
      cancel.addActionListener(event -> setVisible(false));

      panel.add(ok, new GBC(0, 3));
      panel.add(cancel, new GBC(1, 3));
      getContentPane().add(panel);
      setSize(300, 200);
      setLocationRelativeTo(null);
    }

    public void setValues(int x, int y, int radius)
    {
      offset_x.setValue(x);
      offset_y.setValue(y);
      outer_radius.setValue(radius);
    }

    boolean ok_pressed;
    public boolean showDialog()
    {
      ok_pressed = false;
      setVisible(true);
      return ok_pressed;
    }
  }

  class InnerCircleDialog extends JDialog
  {

    JLabel inner_radiusLabel;
    GUI.NumberSpinner inner_radius;
    JButton ok;
    JButton cancel;

    public InnerCircleDialog()
    {
      super(gui, "Inner Circle Editor", true);
      JPanel panel = new JPanel(new GridBagLayout());
      inner_radiusLabel = new JLabel("inner circle radius: ");

      inner_radius = new GUI.NumberSpinner(10, 1000, 10, 300, null, 4);

      panel.add(inner_radiusLabel, new GBC(0, 0).setAnchor(GBC.EAST));
      panel.add(inner_radius, new GBC(1, 0).setAnchor(GBC.WEST));

      ok = new JButton("ok");
      ok.addActionListener(event ->
      {
        ok_pressed = true;
        setVisible(false);
      });
      cancel = new JButton("cancel");
      cancel.addActionListener(event -> setVisible(false));

      panel.add(ok, new GBC(0, 1));
      panel.add(cancel, new GBC(1, 1));
      getContentPane().add(panel);
      setSize(300, 100);
      setLocationRelativeTo(null);
    }

    public void setValues(int radius)
    {
      inner_radius.setValue(radius);
    }

    boolean ok_pressed;
    public boolean showDialog()
    {
      ok_pressed = false;
      setVisible(true);
      return ok_pressed;
    }
  }

  class PenPositionDialog extends JDialog
  {
    JLabel offsetLabel;
    GUI.NumberSpinner offset;
    JLabel colorLabel;
    JButton color;

    JButton ok;
    JButton cancel;

    public PenPositionDialog()
    {
      super(gui, "Pen Position Editor", true);
      JPanel panel = new JPanel(new GridBagLayout());
      offsetLabel = new JLabel("offset: ");
      colorLabel = new JLabel("color: ");
      color = new JButton("select...");
      color.setOpaque(true);

      color.addActionListener(event ->
      {
        Color newColor = JColorChooser.showDialog(this, "choose pen color", color.getBackground());
        if (newColor != null)
        {
          color.setBackground(newColor);
        }
      });

      offset = new GUI.NumberSpinner(0, 1000, 50, 0, null, 4);

      panel.add(offsetLabel, new GBC(0, 0).setAnchor(GBC.EAST));
      panel.add(offset, new GBC(1, 0).setAnchor(GBC.WEST));
      panel.add(colorLabel, new GBC(0, 1).setAnchor(GBC.EAST));
      panel.add(color, new GBC(1, 1).setAnchor(GBC.WEST));

      ok = new JButton("ok");
      ok.addActionListener(event ->
      {
        ok_pressed = true;
        setVisible(false);
      });
      cancel = new JButton("cancel");
      cancel.addActionListener(event -> setVisible(false));

      panel.add(ok, new GBC(0, 3));
      panel.add(cancel, new GBC(1, 3));
      getContentPane().add(panel);
      setSize(300, 200);
      setLocationRelativeTo(null);
    }

    public void setValues(int offset_val, Color pen_color)
    {
      offset.setValue(offset_val);
      color.setBackground(pen_color);
    }

    boolean ok_pressed;
    public boolean showDialog()
    {
      ok_pressed = false;
      setVisible(true);
      return ok_pressed;
    }
  }
  Object getSelectedNode()
  {
    TreePath path = tree.getSelectionPath();
    if (path == null) return null;
    return path.getLastPathComponent();
  }
  void setButtonStates()
  {
    Object selected = getSelectedNode();
    boolean isSpirograph = selected == null ? false : selected.getClass() == Spirograph.class;
    boolean isInnerCircle = selected == null ? false : selected.getClass() == InnerCircle.class;
    boolean isPenPosition = selected == null ? false : selected.getClass() == PenPosition.class;
    addSpiralgraph.setEnabled(true);
    addInnerCircle.setEnabled(isSpirograph);
    addPenPosition.setEnabled(isInnerCircle);
    edit.setEnabled(isSpirograph || isInnerCircle || isPenPosition);
    delete.setEnabled(isSpirograph || isInnerCircle || isPenPosition);
  }

  void updateGraphs()
  {
    gui.plotter.repaint();
    gui.plotter.revalidate();
    gui.repaint();
  }
  public SpirographEditor(GUI gui)
  {
    this.gui = gui;
    spiralGraphDialog = new SpiralGraphDialog();
    innerCircleDialog = new InnerCircleDialog();
    penPositionDialog = new PenPositionDialog();

    setLayout(new BorderLayout());
    addSpiralgraph = new JButton("+ Spiralgraph");
    addSpiralgraph.addActionListener(event ->
    {
      spiralGraphDialog.setValues(200, 200, 100);
      boolean create = spiralGraphDialog.showDialog();
      if (create)
      {
        //System.out.println("outer radius: " + spiralGraphDialog.outer_radius.getValue());
        Spirograph spiral = new Spirograph(gui, (int) spiralGraphDialog.outer_radius.getValue(), (int) spiralGraphDialog.offset_x.getValue(), (int) spiralGraphDialog.offset_y.getValue(), new ArrayList<InnerCircle>());
        gui.spirographs.add(spiral);
        //System.out.println(gui.spirographs);
        //tree.expandRow(0);

        TreePath path = new TreePath(new Object[] {model.root});
        model.fireTreeStructureChanged(new TreeModelEvent(tree, path));
        //tree.expandPath(new TreePath(new Object[] {model.root, spiral}));
        model.expandAllPaths();
        tree.setSelectionPath(new TreePath(new Object[] {model.root, spiral}));
      }
    });
    addInnerCircle = new JButton("+ Inner Circle");
    addInnerCircle.addActionListener(event ->
    {
      innerCircleDialog.setValues(30);
      boolean create = innerCircleDialog.showDialog();
      if (create)
      {
        //System.out.println("inner radius: " + innerCircleDialog.inner_radius.getValue());
        Spirograph owner = (Spirograph) tree.getSelectionPath().getPath()[1];
        InnerCircle circle = owner.addInnerCircle(new ArrayList<PenPosition>(), (int) innerCircleDialog.inner_radius.getValue());

        TreePath path = new TreePath(new Object[] {model.root, owner});
        model.fireTreeStructureChanged(new TreeModelEvent(tree, path));
        //tree.expandPath(new TreePath(new Object[] {model.root, owner, circle}));
        model.expandAllPaths();
        tree.setSelectionPath(new TreePath(new Object[] {model.root, owner, circle}));
      }
    });

    addPenPosition = new JButton("+ Pen Position");
    addPenPosition.addActionListener(event ->
    {
      penPositionDialog.setValues(10, Color.BLACK);
      boolean create = penPositionDialog.showDialog();
      if (create)
      {
        //System.out.println("offset: " + penPositionDialog.offset.getValue());
        InnerCircle circle = (InnerCircle) tree.getSelectionPath().getPath()[2];
        PenPosition pp = circle.addPenPosition((int) penPositionDialog.offset.getValue(), penPositionDialog.color.getBackground());

        TreePath path = new TreePath(new Object[] {model.root, tree.getSelectionPath().getPath()[1], circle});
        model.fireTreeStructureChanged(new TreeModelEvent(tree, path));
        //tree.expandPath(path);
        model.expandAllPaths();
        tree.setSelectionPath(new TreePath(new Object[] {model.root, tree.getSelectionPath().getPath()[1], circle, pp}));
      }
    });

    edit = new JButton("Edit Selected");
    edit.addActionListener(event ->
    {
      Object selected = getSelectedNode();
      if (selected instanceof Spirograph)
      {
        Spirograph spiral = (Spirograph) selected;
        spiralGraphDialog.setValues(spiral.offset_x, spiral.offset_y, spiral.outer_radius);
        boolean ok = spiralGraphDialog.showDialog();
        if (ok)
        {

          spiral.offset_x = (int) spiralGraphDialog.offset_x.getValue();
          spiral.offset_y = (int) spiralGraphDialog.offset_y.getValue();
          spiral.outer_radius = (int) spiralGraphDialog.outer_radius.getValue();
          spiral.recomputePoints();
          updateGraphs();
          model.fireTreeStructureChanged(model.root);
          model.expandAllPaths();
        }
      }
      else if (selected instanceof InnerCircle)
      {
        InnerCircle circle = (InnerCircle) selected;
        innerCircleDialog.setValues(circle.inner_radius);
        boolean ok = innerCircleDialog.showDialog();
        if (ok)
        {
          circle.inner_radius = (int) innerCircleDialog.inner_radius.getValue();
          circle.recomputePoints();
          updateGraphs();
          model.fireTreeStructureChanged(new TreeModelEvent(tree, new TreePath(new Object[] {model.root, tree.getSelectionPath().getPath()[1]})));
          model.expandAllPaths();
        }
      }
      else if (selected instanceof PenPosition)
      {
        PenPosition pp = (PenPosition) selected;

        penPositionDialog.setValues(pp.offset, pp.pen_color);
        boolean ok = penPositionDialog.showDialog();
        if (ok)
        {
          pp.offset = (int) penPositionDialog.offset.getValue();
          pp.pen_color = penPositionDialog.color.getBackground();
          pp.compute_points();
          updateGraphs();
          model.fireTreeStructureChanged(new TreeModelEvent(tree, new TreePath(new Object[] {model.root, tree.getSelectionPath().getPath()[1], tree.getSelectionPath().getPath()[2]})));
          model.expandAllPaths();
        }
      }
    });

    delete = new JButton("Delete Selected");
    delete.addActionListener(event ->
    {
      Object selected = getSelectedNode();
      if (selected instanceof Spirograph)
      {
        gui.spirographs.remove(selected);
        model.fireTreeStructureChanged(model.root);
        model.expandAllPaths();
      }
      else if (selected instanceof InnerCircle)
      {
        Spirograph owner = (Spirograph) tree.getSelectionPath().getPath()[1];
        owner.inner_circles.remove(selected);
        TreePath path = new TreePath(new Object[] {model.root, owner});
        model.fireTreeStructureChanged(path);
        model.expandAllPaths();
        tree.expandPath(path);
      }
      else if (selected instanceof PenPosition)
      {
        InnerCircle owner = (InnerCircle) tree.getSelectionPath().getPath()[2];
        owner.pen_positions.remove(selected);
        TreePath path = new TreePath(new Object[] {model.root, tree.getSelectionPath().getPath()[1], tree.getSelectionPath().getPath()[2]});
        model.fireTreeStructureChanged(path);
        model.expandAllPaths();
        tree.expandPath(path);
      }
    });
    buttonsPanel = new JPanel(new GridBagLayout());


    buttonsPanel.add(addSpiralgraph, new GBC(0, 0));
    buttonsPanel.add(addInnerCircle, new GBC(1, 0));
    buttonsPanel.add(addPenPosition, new GBC(2, 0));
    buttonsPanel.add(edit, new GBC(0, 1));
    buttonsPanel.add(delete, new GBC(1, 1));

    model = new SpirographTreeModel();
    tree = new JTree(model);

    tree.setExpandsSelectedPaths(true);
    tree.setRootVisible(false);
    //tree.setExpandsSelectedPaths(true);
    //tree.setSelectionPath(new TreePath(model.root));
    //tree.expandPath(new TreePath(model.root));
    tree.expandRow(0);

    //tree.expandRow(0);
    //tree.expandRow(0);
    tree.setShowsRootHandles(true);

    //
    tree.addTreeSelectionListener(new TreeSelectionListener()
    {
      @Override
      public void valueChanged(TreeSelectionEvent e)
      {
        setButtonStates();
      }
    });

    tree.setCellRenderer(new SpirographRenderer());
    add(buttonsPanel, BorderLayout.SOUTH);
    add(new JScrollPane(tree), BorderLayout.CENTER);
    setButtonStates();
  }

  class SpirographRenderer extends DefaultTreeCellRenderer
  {
    /*JPanel panel;
    JLabel label;
    public SpirographRenderer()
    {
      label = new JLabel();
      setLayout(new BorderLayout());
      add(label, BorderLayout.CENTER);
    }*/

    public SpirographRenderer()
    {
      super();
      setOpenIcon(null);
      setClosedIcon(null);
      setLeafIcon(null);
      //UIManager.put("Tree.collapsedIcon", new IconUIResource(new NodeIcon('+')));
      //UIManager.put("Tree.expandedIcon",  new IconUIResource(new NodeIcon('-')));
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
    {
      JLabel l = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
      l.setOpaque(false);
      JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
      //setOpaque(false);
      //setBackground(new Color(0, 0, 0, 0));
      p.setOpaque(true);
      p.setBackground(new Color(0, 0, 0, 0));
      l.setOpaque(true);
      l.setBackground(new Color(0, 0, 0, 0));

      TreePath path = tree.getPathForRow(row);
      if (path == null) // I don't know why this happens when you change the look and feel
      {
        //System.out.println("!path is null for row" + row );
        return p;
      }
      Object obj = path.getLastPathComponent();

      //System.out.println("rendering row=" + row + ", value=" + value + ", class=" + value.getClass());
      //setBackground(Color.white);
      if (obj.getClass() == Object.class)
      {
        l.setText("All Spirographs");
      }
      else if (obj.getClass() == Spirograph.class)
      {
        //System.out.println("rendering spirograph " + value);
        Spirograph sg = (Spirograph) obj;
        l.setText("Spirograph | offsetx= " + sg.offset_x + ", offsety=" + sg.offset_y + ", outer_radius=" + sg.outer_radius);
      }
      else if (obj.getClass() == InnerCircle.class)
      {
        //System.out.println("rendering innercircle " + value);
        InnerCircle circle = (InnerCircle) obj;
        l.setText("Inner Circle | radius= " + circle.inner_radius);
      }
      else if (obj.getClass() == PenPosition.class)
      {
        //System.out.println("rendering penposition " + value);
        PenPosition pp = (PenPosition) obj;
        p.setBackground(pp.pen_color);
        l.setText("Pen Position | offset= " + pp.offset);
        //l.setBackground(pp.pen_color);
      }

      if (hasFocus)
        setBorder(UIManager.getBorder("List.focusCellHighlightBorder"));
      else
        setBorder(null);

      p.add(l);
      return p;
    }
  }
  class SpirographTreeModel implements TreeModel
  {
    //private Variable root;
    private EventListenerList listenerList = new EventListenerList();
    Object root;

    public SpirographTreeModel()
    {
      root = new Object();
    }
    public Object getRoot()
    {
      return root;
    }

    public int getChildCount(Object parent)
    {
      if (parent == root)
      {
        //System.out.println("root child count: " + gui.spirographs.size());
      }
      if (parent == root) return gui.spirographs.size();
      else if (parent.getClass() == Spirograph.class) return ((Spirograph)parent).inner_circles.size();
      if (parent.getClass() == InnerCircle.class) return ((InnerCircle) parent).pen_positions.size();
      else return 0;
    }

    public Object getChild(Object parent, int index)
    {
      if (parent == root)
      {
        //System.out.println("  get child " + index + ": " + gui.spirographs.get(index));
        return gui.spirographs.get(index);
      }
      else if (parent.getClass() == Spirograph.class) return ((Spirograph)parent).inner_circles.get(index);
      else return ((InnerCircle) parent).pen_positions.get(index);
    }

    public int getIndexOfChild(Object parent, Object child)
    {
      if (parent == root)
      {
        //System.out.println("index of " + child + ": " + gui.spirographs.indexOf(child));
        return gui.spirographs.indexOf(child);
      }
      else if (parent.getClass() == Spirograph.class) return ((Spirograph)parent).inner_circles.indexOf(child);
      else return ((InnerCircle) parent).pen_positions.indexOf(child);
    }

    public boolean isLeaf(Object node)
    {
      //return node instanceof PenPosition;
      //if (node == root) return false;
      return getChildCount(node) == 0;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue)
    {
    }

    /**
     * Called after takeStep
     */
    protected void fireTreeNodeChanged(TreeModelEvent event)
    {
      for (TreeModelListener l : listenerList.getListeners(TreeModelListener.class))
      {
        l.treeNodesChanged(event);
      }
    }

    public void addTreeModelListener(TreeModelListener l)
    {
      listenerList.add(TreeModelListener.class, l);
    }

    public void removeTreeModelListener(TreeModelListener l)
    {
      listenerList.remove(TreeModelListener.class, l);
    }

    protected void fireTreeStructureChanged(TreeModelEvent event)
    {
      for (TreeModelListener l : listenerList.getListeners(TreeModelListener.class))
      {
        l.treeStructureChanged(event);
      }
    }

    protected void fireTreeStructureChanged(Object oldRoot)
    {
      var event = new TreeModelEvent(this, new Object[] { oldRoot });
      for (TreeModelListener l : listenerList.getListeners(TreeModelListener.class))
        l.treeStructureChanged(event);
      updateGraphs();
    }

    void expandAllPaths()
    {
      int row = tree.getRowCount();
      for (int i = 0; i < row; i++)
      {
        tree.expandRow(i);
      }
      tree.repaint();
    }
  }
}