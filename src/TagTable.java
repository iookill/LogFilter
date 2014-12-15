import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class TagTable extends JTable
{
    private static final long             serialVersionUID = 1L;

    LogFilterMain                         m_LogFilterMain;

    public TagTable(TagFilterTableModel tablemodel, LogFilterMain filterMain)
    {
        super(tablemodel);
        m_LogFilterMain = filterMain;
        init();
        setColumnWidth();
    }

    private void init() {
//        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//        setTableHeader(createTableHeader());
//        getTableHeader().setReorderingAllowed(false);
        setOpaque(false);
        setAutoscrolls(false);
//        setRequestFocusEnabled(false);

//        setGridColor(TABLE_GRID_COLOR);
        setIntercellSpacing(new Dimension(0, 0));
        // turn off grid painting as we'll handle this manually in order to paint
        // grid lines over the entire viewport.
        setShowGrid(false);

        for(int iIndex = 0; iIndex < getColumnCount(); iIndex++)
        {
            getColumnModel().getColumn(iIndex).setCellRenderer(new TagCellRenderer());
        }

        getTableHeader().addMouseListener(new ColumnHeaderListener());
    }

    public void showColumn(int nColumn, boolean bShow)
    {
        if(bShow)
        {
            getColumnModel().getColumn(nColumn).setResizable(true);
            getColumnModel().getColumn(nColumn).setMaxWidth(TagFilterTableModel.ColWidth[nColumn] * 1000);
            getColumnModel().getColumn(nColumn).setMinWidth(1);
            getColumnModel().getColumn(nColumn).setWidth(TagFilterTableModel.ColWidth[nColumn]);
            getColumnModel().getColumn(nColumn).setPreferredWidth(TagFilterTableModel.ColWidth[nColumn]);
        }
        else
            hideColumn(nColumn);
    }

    public void hideColumn(int nColumn)
    {
        getColumnModel().getColumn(nColumn).setWidth(0);
        getColumnModel().getColumn(nColumn).setMinWidth(0);
        getColumnModel().getColumn(nColumn).setMaxWidth(0);
        getColumnModel().getColumn(nColumn).setPreferredWidth(0);
        getColumnModel().getColumn(nColumn).setResizable(false);
    }

    private void setColumnWidth()
    {
        for(int iIndex = 0; iIndex < getColumnCount(); iIndex++)
        {
            showColumn(iIndex, true);
        }
    }

    public boolean isCellEditable(int row, int column)
    {
        if(column == LogFilterTableModel.COMUMN_BOOKMARK)
            return true;
        return false;
    }

    boolean isInnerRect(Rectangle parent, Rectangle child)
    {
        if(parent.y <= child.y && (parent.y + parent.height) >= (child.y + child.height))
            return true;
        else
            return false;
    }

    int getVisibleRowCount()
    {
        return getVisibleRect().height/getRowHeight();
    }

    public void packColumn(int vColIndex, int margin) {
        DefaultTableColumnModel colModel = (DefaultTableColumnModel)getColumnModel();
        TableColumn col = colModel.getColumn(vColIndex);
        int width = 0;

        // Get width of column header
        TableCellRenderer renderer = col.getHeaderRenderer();
        if (renderer == null) {
            renderer = getTableHeader().getDefaultRenderer();
        }
//        Component comp;
//        Component comp = renderer.getTableCellRendererComponent(
//            this, col.getHeaderValue(), false, false, 0, 0);
//        width = comp.getPreferredSize().width;

//        JViewport viewport = (JViewport)m_LogFilterMain.m_scrollVBar.getViewport();
//        Rectangle viewRect = viewport.getViewRect();
//        int nFirst = m_LogFilterMain.m_tbTagTable.rowAtPoint(new Point(0, viewRect.y));
//        int nLast = m_LogFilterMain.m_tbTagTable.rowAtPoint(new Point(0, viewRect.height - 1));
//
//        if(nLast < 0)
//            nLast = m_LogFilterMain.m_tbTagTable.getRowCount();
        // Get maximum width of column data
//        for (int r=nFirst; r<nFirst + nLast; r++) {
//            renderer = getCellRenderer(r, vColIndex);
//            comp = renderer.getTableCellRendererComponent(
//                this, getValueAt(r, vColIndex), false, false, r, vColIndex);
//            width = Math.max(width, comp.getPreferredSize().width);
//        }

        // Add margin
        width += 2*margin;

        // Set the width
        col.setPreferredWidth(width);
    }

    public class TagCellRenderer extends DefaultTableCellRenderer
    {
        private static final long serialVersionUID = 1L;
        boolean m_bChanged;

        public Component getTableCellRendererComponent(JTable table,
                                                       Object value,
                                                       boolean isSelected,
                                                       boolean hasFocus,
                                                       int row,
                                                       int column)
        {
            Component c = super.getTableCellRendererComponent(table,
                                                              value,
                                                              isSelected,
                                                              hasFocus,
                                                              row,
                                                              column);

            if(column == TagInfo.COMUMN_TAG)
                return c;
            else
                return new JCheckBox();
        }
    }

    public class ColumnHeaderListener extends MouseAdapter {
        public void mouseClicked(MouseEvent evt) {

            if ( SwingUtilities.isLeftMouseButton( evt ) && evt.getClickCount() == 2 )
            {
                JTable table = ((JTableHeader)evt.getSource()).getTable();
                TableColumnModel colModel = table.getColumnModel();

                // The index of the column whose header was clicked
                int vColIndex = colModel.getColumnIndexAtX(evt.getX());

                if (vColIndex == -1) {
                    T.d("vColIndex == -1");
                    return;
                }
                packColumn(vColIndex, 1);
            }
        }
    }
}
