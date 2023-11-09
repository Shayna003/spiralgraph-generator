package spirograph;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;

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

  public SpirographEditor(GUI gui)
  {
    this.gui = gui;
    setLayout(new BorderLayout());
    addSpiralgraph = new JButton("Add Spiralgraph");
    addInnerCircle = new JButton("Add Inner Circle");
    addPenPosition = new JButton("Add Pen Position");
    edit = new JButton("Edit Selected");
    delete = new JButton("Delete Selected");
    buttonsPanel = new JPanel();
    buttonsPanel.add(addInnerCircle);
    buttonsPanel.add(addPenPosition);
    buttonsPanel.add(edit);
    buttonsPanel.add(delete);

    model = new SpirographTreeModel();
    tree = new JTree(model);
    tree.setCellRenderer(new SpirographRenderer());
    add(buttonsPanel, BorderLayout.SOUTH);
    add(new JScrollPane(tree), BorderLayout.CENTER);
  }

  class SpirographRenderer extends JPanel implements TreeCellRenderer
  {
    JLabel label;
    public SpirographRenderer()
    {
      label = new JLabel();
      setLayout(new BorderLayout());
      add(label, BorderLayout.CENTER);
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
    {
      //setBackground(Color.white);
      if (value.getClass() == Spirograph.class)
      {
        Spirograph sg = (Spirograph) value;
        label.setText("Spirograph | offsetx= " + sg.offset_x + ", offsety=" + sg.offset_y);
      }
      else if (value.getClass() == Spirograph.PenPosition.class)
      {
        Spirograph.PenPosition pp = (Spirograph.PenPosition) value;
        setBackground(pp.pen_color);
        label.setText("Pen Position | offset= " + pp.offset);
      }
      else if (value.getClass() == Spirograph.InnerCircle.class)
      {
        Spirograph.InnerCircle circle = (Spirograph.InnerCircle) value;
        label.setText("Inner Circle | radius= " + circle.inner_radius);
      }
      if (hasFocus)
        setBorder(UIManager.getBorder("List.focusCellHighlightBorder"));
      else
        setBorder(null);

      return this;
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
      if (parent == root) return gui.spirographs.size();
      else if (parent.getClass() == Spirograph.class) return ((Spirograph)parent).inner_circles.size();
      if (parent.getClass() == Spirograph.InnerCircle.class) return ((Spirograph.InnerCircle) parent).pen_positions.size();
      else return 0;
    }

    public Object getChild(Object parent, int index)
    {
      if (parent == root) return gui.spirographs.get(index);
      else if (parent.getClass() == Spirograph.class) return ((Spirograph)parent).inner_circles.get(index);
      else return ((Spirograph.InnerCircle) parent).pen_positions.get(index);
    }

    public int getIndexOfChild(Object parent, Object child)
    {
      if (parent == root) return gui.spirographs.indexOf(child);
      else if (parent.getClass() == Spirograph.class) return ((Spirograph)parent).inner_circles.indexOf(child);
      else return ((Spirograph.InnerCircle) parent).pen_positions.indexOf(child);
    }

    public boolean isLeaf(Object node)
    {
      return getChildCount(node) == 0;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue)
    {
    }

    public void addTreeModelListener(TreeModelListener l)
    {
      listenerList.add(TreeModelListener.class, l);
    }

    public void removeTreeModelListener(TreeModelListener l)
    {
      listenerList.remove(TreeModelListener.class, l);
    }

    protected void fireTreeStructureChanged(Object oldRoot)
    {
      var event = new TreeModelEvent(this, new Object[] { oldRoot });
      for (TreeModelListener l : listenerList.getListeners(TreeModelListener.class))
        l.treeStructureChanged(event);
    }
  }
}