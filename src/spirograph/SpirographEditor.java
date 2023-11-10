package spirograph;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.IconUIResource;
import javax.swing.tree.*;
import java.awt.*;
import java.util.ArrayList;

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
      labelx = new JLabel("offset x:");
      labely = new JLabel("offset y:");
      outer_radiusLabel = new JLabel("outer circle radius:");

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
    boolean isInnerCircle = selected == null ? false : selected.getClass() == Spirograph.InnerCircle.class;
    boolean isPenPosition = selected == null ? false : selected.getClass() == Spirograph.PenPosition.class;
    addSpiralgraph.setEnabled(true);
    addInnerCircle.setEnabled(isSpirograph);
    addPenPosition.setEnabled(isInnerCircle);
    edit.setEnabled(isSpirograph || isInnerCircle || isPenPosition);
    delete.setEnabled(isSpirograph || isInnerCircle || isPenPosition);
  }
  public SpirographEditor(GUI gui)
  {
    this.gui = gui;
    spiralGraphDialog = new SpiralGraphDialog();

    setLayout(new BorderLayout());
    addSpiralgraph = new JButton("+ Spiralgraph");
    addSpiralgraph.addActionListener(event ->
    {
      spiralGraphDialog.setValues(0, 0, 100);
      boolean create = spiralGraphDialog.showDialog();
      if (create)
      {
        System.out.println("outer radius: " + spiralGraphDialog.outer_radius.getValue());
        Spirograph spiral = new Spirograph(gui, (int) spiralGraphDialog.outer_radius.getValue(), (int) spiralGraphDialog.offset_x.getValue(), (int) spiralGraphDialog.offset_y.getValue(), new ArrayList<Spirograph.InnerCircle>());
        gui.spirographs.add(spiral);
        System.out.println(gui.spirographs);
        //tree.expandRow(0);

        model.fireTreeStructureChanged(new TreeModelEvent(tree, new TreePath(new Object[] {model.root})));
        //model.reload();
        //model.fireTreeStructureChanged();
        tree.revalidate();
        tree.repaint();
      }
    });
    addInnerCircle = new JButton("+ Inner Circle");

    addPenPosition = new JButton("+ Pen Position");
    edit = new JButton("Edit Selected");
    delete = new JButton("Delete Selected");
    buttonsPanel = new JPanel(new GridBagLayout());


    buttonsPanel.add(addSpiralgraph, new GBC(0, 0));
    buttonsPanel.add(addInnerCircle, new GBC(1, 0));
    buttonsPanel.add(addPenPosition, new GBC(2, 0));
    buttonsPanel.add(edit, new GBC(0, 1));
    buttonsPanel.add(delete, new GBC(1, 1));

    model = new SpirographTreeModel();
    tree = new JTree(model);

    //tree.setModel(model);
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
      p.setOpaque(false);
      p.setBackground(new Color(0, 0, 0, 0));

      TreePath path = tree.getPathForRow(row);
      if (path == null) // I don't know why this happens when you change the look and feel
      {
        System.out.println("!path is null for row" + row );
        return p;
      }
      Object obj = path.getLastPathComponent();

      System.out.println("rendering row=" + row + ", value=" + value + ", class=" + value.getClass());
      //setBackground(Color.white);
      if (obj.getClass() == Object.class)
      {
        l.setText("All Spirographs");
      }
      else if (obj.getClass() == Spirograph.class)
      {
        System.out.println("rendering spirograph " + value);
        Spirograph sg = (Spirograph) obj;
        l.setText("Spirograph | offsetx= " + sg.offset_x + ", offsety=" + sg.offset_y + ", outer_radius=" + sg.outer_radius);
      }
      else if (obj.getClass() == Spirograph.InnerCircle.class)
      {
        System.out.println("rendering innercircle " + value);
        Spirograph.InnerCircle circle = (Spirograph.InnerCircle) obj;
        l.setText("Inner Circle | radius= " + circle.inner_radius);
      }
      else if (obj.getClass() == Spirograph.PenPosition.class)
      {
        System.out.println("rendering penposition " + value);
        Spirograph.PenPosition pp = (Spirograph.PenPosition) obj;
        setBackground(pp.pen_color);
        l.setText("Pen Position | offset= " + pp.offset);
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
        System.out.println("root child count: " + gui.spirographs.size());
      }
      if (parent == root) return gui.spirographs.size();
      else if (parent.getClass() == Spirograph.class) return ((Spirograph)parent).inner_circles.size();
      if (parent.getClass() == Spirograph.InnerCircle.class) return ((Spirograph.InnerCircle) parent).pen_positions.size();
      else return 0;
    }

    public Object getChild(Object parent, int index)
    {
      if (parent == root)
      {
        System.out.println("  get child " + index + ": " + gui.spirographs.get(index));
        return gui.spirographs.get(index);
      }
      else if (parent.getClass() == Spirograph.class) return ((Spirograph)parent).inner_circles.get(index);
      else return ((Spirograph.InnerCircle) parent).pen_positions.get(index);
    }

    public int getIndexOfChild(Object parent, Object child)
    {
      if (parent == root)
      {
        System.out.println("index of " + child + ": " + gui.spirographs.indexOf(child));
        return gui.spirographs.indexOf(child);
      }
      else if (parent.getClass() == Spirograph.class) return ((Spirograph)parent).inner_circles.indexOf(child);
      else return ((Spirograph.InnerCircle) parent).pen_positions.indexOf(child);
    }

    public boolean isLeaf(Object node)
    {
      //return node instanceof Spirograph.PenPosition;
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
    }
  }
}