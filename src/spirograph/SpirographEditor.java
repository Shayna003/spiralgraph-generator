package spirograph;

import java.awt.*;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.util.ArrayList;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import spirograph.Spirograph.InnerCircle;


/**
 * An implementation of a TreeTable
 * Nodes are inner circle, leaves are pen positions
 * Code of basic TreeTable implementation adapted from https://www.comp.nus.edu.sg/~cs3283/ftp/Java/swingConnect/tech_topics/tables-trees/tables-trees.html
 */
public class SpirographEditor extends JTable
{
  class TreeTableModelAdapter extends AbstractTableModel
  {
    JTree tree;
    TreeTableModel treeTableModel;

    public TreeTableModelAdapter(TreeTableModel treeTableModel, JTree tree)
    {
      this.tree = tree;
      this.treeTableModel = treeTableModel;

      tree.addTreeExpansionListener(new TreeExpansionListener()
      {
        public void treeExpanded(TreeExpansionEvent event) { fireTableDataChanged(); }
        public void treeCollapsed(TreeExpansionEvent event) { fireTableDataChanged(); }
      });
    }

    public int getColumnCount() { return treeTableModel.getColumnCount();}

    public String getColumnName(int column) { return treeTableModel.getColumnName(column);}

    public Class getColumnClass(int column) { return treeTableModel.getColumnClass(column);}

    public int getRowCount() { return tree.getRowCount();}

    protected Object nodeForRow(int row)
    {
      TreePath treePath = tree.getPathForRow(row);
      return treePath.getLastPathComponent();
    }

    public Object getValueAt(int row, int column)
    {
      return treeTableModel.getValueAt(nodeForRow(row), column);
    }

    public boolean isCellEditable(int row, int column)
    {
      return treeTableModel.isCellEditable(nodeForRow(row), column);
    }

    public void setValueAt(Object value, int row, int column)
    {
      treeTableModel.setValueAt(value, nodeForRow(row), column);
    }
  }
  class TreeTableModel implements TreeModel
  {
    String[] columnNames = { "Inner Circle Radius", "Pen Offset", "Color"};
    ArrayList<InnerCircle> inner_circles;

    public String getColumnName(int c)
    {
      return columnNames[c];
    }

    public Class<?> getColumnClass(int c)
    {
      if (c == 0) return Double.class;
      else if (c == 1) return Double.class;
      else return Color.class;
    }

    public int getColumnCount()
    {
      return columnNames.length;
    }

    public int getRowCount()
    {
      return inner_circles.size();
    }

/*    public Object getValueAt(int r, int c)
    {
      InnerCircle circle = inner_circles.get(r);
      if (c == 0)
      {
        return circle.radius;
      }
      return -1;
    }*/


    /** By default, make the column with the Tree in it the only editable one.
     *  Making this column editable causes the JTable to forward mouse
     *  and keyboard events in the Tree column to the underlying JTree.
     */
    public boolean isCellEditable(Object node, int column)
    {
      return getColumnClass(column) == TreeTableModel.class;
    }

    //public void setValueAt(Object aValue, Object node, int column) {}

    public Object getChild(Object parent, int index)
    {
      if (parent.getClass() == InnerCircle.class)
      {
        return ((InnerCircle) parent).pen_positions.get(index);
      }
      else
      {
        return null;
      }
    }

    public int getChildCount(Object parent)
    {
      if (parent.getClass() == InnerCircle.class)
      {
        return ((InnerCircle) parent).pen_positions.size();
      }
      else
      {
        return 0;
      }
    }

    public String getColumnName(Object node, int column)
    {
      return getColumnName(column);
    }
    public Object getValueAt(Object node, int column)
    {

    }

    public void setValueAt(Object aValue, Object node, int column)
    {

    }

    protected Object root;
    protected EventListenerList listenerList = new EventListenerList();

    public TreeTableModel(Object root) { this.root = root; }

    public Object getRoot() { return root; }

    public boolean isLeaf(Object node) { return getChildCount(node) == 0; }

    public void valueForPathChanged(TreePath path, Object newValue) {}

    // This is not called in the JTree's default mode: use a naive implementation.
    public int getIndexOfChild(Object parent, Object child)
    {
      for (int i = 0; i < getChildCount(parent); i++)
      {
        if (getChild(parent, i).equals(child))
        {
          return i;
        }
      }
      return -1;
    }

    public void addTreeModelListener(TreeModelListener l) { listenerList.add(TreeModelListener.class, l);}

    public void removeTreeModelListener(TreeModelListener l) { listenerList.remove(TreeModelListener.class, l);}

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @see EventListenerList
     */
    protected void fireTreeNodesChanged(Object source, Object[] path, int[] childIndices, Object[] children)
    {
      // Guaranteed to return a non-null array
      Object[] listeners = listenerList.getListenerList();
      TreeModelEvent e = null;
      // Process the listeners last to first, notifying
      // those that are interested in this event
      for (int i = listeners.length-2; i>=0; i-=2)
      {
        if (listeners[i]==TreeModelListener.class)
        {
          // Lazily create the event:
          if (e == null)
            e = new TreeModelEvent(source, path,
                    childIndices, children);
          ((TreeModelListener)listeners[i+1]).treeNodesChanged(e);
        }
      }
    }

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @see EventListenerList
     */
    protected void fireTreeNodesInserted(Object source, Object[] path, int[] childIndices, Object[] children)
    {
      // Guaranteed to return a non-null array
      Object[] listeners = listenerList.getListenerList();
      TreeModelEvent e = null;
      // Process the listeners last to first, notifying
      // those that are interested in this event
      for (int i = listeners.length-2; i>=0; i-=2)
      {
        if (listeners[i]==TreeModelListener.class)
        {
          // Lazily create the event:
          if (e == null)
            e = new TreeModelEvent(source, path,
                    childIndices, children);
          ((TreeModelListener)listeners[i+1]).treeNodesInserted(e);
        }
      }
    }

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @see EventListenerList
     */
    protected void fireTreeNodesRemoved(Object source, Object[] path, int[] childIndices, Object[] children)
    {
      // Guaranteed to return a non-null array
      Object[] listeners = listenerList.getListenerList();
      TreeModelEvent e = null;
      // Process the listeners last to first, notifying
      // those that are interested in this event
      for (int i = listeners.length-2; i>=0; i-=2)
      {
        if (listeners[i]==TreeModelListener.class)
        {
          // Lazily create the event:
          if (e == null)
            e = new TreeModelEvent(source, path,
                    childIndices, children);
          ((TreeModelListener)listeners[i+1]).treeNodesRemoved(e);
        }
      }
    }

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @see EventListenerList
     */
    protected void fireTreeStructureChanged(Object source, Object[] path, int[] childIndices, Object[] children)
    {
      // Guaranteed to return a non-null array
      Object[] listeners = listenerList.getListenerList();
      TreeModelEvent e = null;
      // Process the listeners last to first, notifying
      // those that are interested in this event
      for (int i = listeners.length-2; i>=0; i-=2) {
        if (listeners[i]==TreeModelListener.class) {
          // Lazily create the event:
          if (e == null)
            e = new TreeModelEvent(source, path,
                    childIndices, children);
          ((TreeModelListener)listeners[i+1]).treeStructureChanged(e);
        }
      }
    }
  }

  public class TreeTableCellRenderer extends JTree implements TableCellRenderer
  {
    int visibleRow;

    public TreeTableCellRenderer(TreeModel model) { super(model); }

    public void setBounds(int x, int y, int w, int h)
    {
      super.setBounds(x, 0, w, SpirographEditor.this.getHeight());
    }

    public void paint(Graphics g)
    {
      g.translate(0, -visibleRow * getRowHeight());
      super.paint(g);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
      if(isSelected)
        setBackground(table.getSelectionBackground());
      else
        setBackground(table.getBackground());

      visibleRow = row;
      return this;
    }
  }

  // TODO
  class TreeTableCellEditor extends AbstractCellEditor implements TableCellEditor
  {
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int r, int c)
    {
      return tree;
    }

    @Override
    public Object getCellEditorValue()
    {
      return null;
    }
  }

  TreeTableModel model;
  TreeTableModelAdapter adapter;
  TreeTableCellRenderer treeRenderer;
  TreeTableCellRenderer tree;

  public SpirographEditor(TreeTableModel treeTableModel)
  {
    super();
    tree = new TreeTableCellRenderer(treeTableModel);
    treeRenderer = new TreeTableCellRenderer(treeTableModel);

    super.setModel(new TreeTableModelAdapter(treeTableModel, tree));
    setDefaultRenderer(Color.class, new ColorCellRenderer());
    setDefaultEditor(Color.class, new ColorCellEditor());
  }
}

class ColorCellRenderer extends JPanel implements TableCellRenderer {

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
  {
    setBackground((Color) value);
    if (hasFocus)
      setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
    else
      setBorder(null);
    return this;
  }
}
class ColorCellEditor extends AbstractCellEditor implements TableCellEditor, TreeCellEditor
{

  JColorChooser colorChooser;
  JDialog colorDialog;
  private JPanel panel;

  public ColorCellEditor()
  {
    panel = new JPanel();
    colorChooser = new JColorChooser();
    colorDialog = JColorChooser.createDialog(null, "Pen Color", false, colorChooser,
        EventHandler.create(ActionListener.class, this, "stopCellEditing"),
        EventHandler.create(ActionListener.class, this, "cancelCellEditing"));
  }

  public Component getTableCellEditorComponent(JTable table, Object currentValue, boolean isSelected, int row, int column)
  {
    colorChooser.setColor((Color) currentValue);
    return panel;
  }

  public boolean shouldSelectCell(EventObject anEvent)
  {
    colorDialog.setVisible(true);
    return true;
  }

  public void cancelCellEditing()
  {
    colorDialog.setVisible(false);
    super.cancelCellEditing();
  }

  public boolean stopCellEditing()
  {
    colorDialog.setVisible(false);
    super.stopCellEditing();
    return true;
  }

  public Object getCellEditorValue()
  {
    return colorChooser.getColor();
  }

  @Override
  public Component getTreeCellEditorComponent(JTree tree, Object currentValue, boolean isSelected, boolean expanded, boolean leaf, int row)
  {
    colorChooser.setColor((Color) currentValue);
    return panel;
  }
}