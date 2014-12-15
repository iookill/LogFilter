import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.StringTokenizer;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class LogTable extends JTable implements FocusListener, ActionListener
{
    private static final long             serialVersionUID = 1L;

    LogFilterMain                         m_LogFilterMain;
    ILogParser                            m_iLogParser;
    String                                m_strHighlight;
    String                                m_strPidShow;
    String                                m_strTidShow;
    String                                m_strTagShow;
    String                                m_strTagRemove;
    String                                m_strFilterRemove;
    String                                m_strFilterFind;
    float                                 m_fFontSize;
    boolean                               m_bAltPressed;
    int                                   m_nTagLength;
    boolean[]                             m_arbShow;

    public LogTable(LogFilterTableModel tablemodel, LogFilterMain filterMain)
    {
        super(tablemodel);
        m_LogFilterMain = filterMain;
        m_strHighlight       = "";
        m_strPidShow         = "";
        m_strTidShow         = "";
        m_strTagShow         = "";
        m_strTagRemove       = "";
        m_strFilterRemove    = "";
        m_strFilterFind      = "";
        m_nTagLength         = 0;
        m_arbShow            = new boolean[LogFilterTableModel.COMUMN_MAX];
        init();
        setColumnWidth();
    }

    public void changeSelection( int rowIndex, int columnIndex, boolean toggle, boolean extend )
    {
        if(rowIndex < 0 ) rowIndex = 0;
        if(rowIndex > getRowCount() - 1) rowIndex = getRowCount() - 1;
        super.changeSelection(rowIndex, columnIndex, toggle, extend);
//        if(getAutoscrolls())
        showRow(rowIndex);
    }

    public void changeSelection( int rowIndex, int columnIndex, boolean toggle, boolean extend, boolean bMove )
    {
        if(rowIndex < 0 ) rowIndex = 0;
        if(rowIndex > getRowCount() - 1) rowIndex = getRowCount() - 1;
        super.changeSelection(rowIndex, columnIndex, toggle, extend);
//        if(getAutoscrolls())
        if(bMove)
            showRow(rowIndex);
    }

    private void init() {
        KeyStroke copy = KeyStroke.getKeyStroke(KeyEvent.VK_C,ActionEvent.CTRL_MASK,false);
        registerKeyboardAction(this,"Copy",copy,JComponent.WHEN_FOCUSED);

        addFocusListener( this );
        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//        setTableHeader(createTableHeader());
//        getTableHeader().setReorderingAllowed(false);
        m_fFontSize = 12;
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
            getColumnModel().getColumn(iIndex).setCellRenderer(new LogCellRenderer());
        }

        addMouseListener(new MouseAdapter()
        {
            public void mouseClicked( MouseEvent e )
            {
                Point p = e.getPoint();
                int row = rowAtPoint( p );
                if ( SwingUtilities.isLeftMouseButton( e ) )
                {
                    if (e.getClickCount() == 2){
                        LogInfo logInfo = ((LogFilterTableModel)getModel()).getRow(row);
                        logInfo.m_bMarked = !logInfo.m_bMarked;
                        m_LogFilterMain.bookmarkItem(row, Integer.parseInt(logInfo.m_strLine) - 1, logInfo.m_bMarked);
                     }
                    else if(m_bAltPressed)
                    {
                        int colum = columnAtPoint(p);
                        if(colum == LogFilterTableModel.COMUMN_TAG)
                        {
                            LogInfo logInfo = ((LogFilterTableModel)getModel()).getRow(row);
                            if(m_strTagShow.contains("|" + (String)logInfo.getData(colum)))
                                m_strTagShow = m_strTagShow.replace("|" + (String)logInfo.getData(colum), "");
                            else if(m_strTagShow.contains((String)logInfo.getData(colum)))
                                m_strTagShow = m_strTagShow.replace((String)logInfo.getData(colum), "");
                            else
                                m_strTagShow += "|" + (String)logInfo.getData(colum);
                            m_LogFilterMain.notiEvent(new INotiEvent.EventParam(INotiEvent.EVENT_CHANGE_FILTER_SHOW_TAG));
                        }
                    }
                }
                else if ( SwingUtilities.isRightMouseButton( e ))
                {
                    int colum = columnAtPoint(p);
                    T.d("m_bAltPressed = " + m_bAltPressed);
                    if(m_bAltPressed)
                    {
                        if(colum == LogFilterTableModel.COMUMN_TAG)
                        {
                            T.d();
                            LogInfo logInfo = ((LogFilterTableModel)getModel()).getRow(row);
                            m_strTagRemove += "|" + (String)logInfo.getData(colum);
                            m_LogFilterMain.notiEvent(new INotiEvent.EventParam(INotiEvent.EVENT_CHANGE_FILTER_REMOVE_TAG));
                        }
                    }
                    else
                    {
                        T.d();
                        LogInfo logInfo = ((LogFilterTableModel)getModel()).getRow(row);
                        StringSelection data = new StringSelection((String)logInfo.getData(colum));
                        getToolkit();
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(data, data);
                    }
                }
            }
        });
        getTableHeader().addMouseListener(new ColumnHeaderListener());
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

    String GetFilterFind()
    {
        return m_strFilterFind;
    }

    String GetFilterRemove()
    {
        return m_strFilterRemove;
    }

    String GetFilterShowPid()
    {
        return m_strPidShow;
    }

    String GetFilterShowTid()
    {
        return m_strTidShow;
    }

    String GetFilterShowTag()
    {
        return m_strTagShow;
    }

    String GetHighlight()
    {
        return m_strHighlight;
    }

    String GetFilterRemoveTag()
    {
        return m_strTagRemove;
    }

    void gotoNextBookmark()
    {
        int nSeletectRow = getSelectedRow();
        Rectangle parent = getVisibleRect();

        LogInfo logInfo;
        for(int nIndex = nSeletectRow + 1; nIndex < getRowCount(); nIndex++)
        {
            logInfo = ((LogFilterTableModel)getModel()).getRow(nIndex);
            if(logInfo.m_bMarked)
            {
                changeSelection(nIndex, 0, false, false);
                int nVisible = nIndex;
                if(!isInnerRect(parent, getCellRect(nIndex, 0, true)))
                    nVisible = nIndex + getVisibleRowCount() / 2;
                showRow(nVisible);
                return;
            }
        }

        for(int nIndex = 0; nIndex < nSeletectRow; nIndex++)
        {
            logInfo = ((LogFilterTableModel)getModel()).getRow(nIndex);
            if(logInfo.m_bMarked)
            {
                changeSelection(nIndex, 0, false, false);
                int nVisible = nIndex;
                if(!isInnerRect(parent, getCellRect(nIndex, 0, true)))
                    nVisible = nIndex - getVisibleRowCount() / 2;
                showRow(nVisible);
                return;
            }
        }
    }

    int getVisibleRowCount()
    {
        return getVisibleRect().height/getRowHeight();
    }

    void gotoPreBookmark()
    {
        int nSeletectRow = getSelectedRow();
        Rectangle parent = getVisibleRect();

        LogInfo logInfo;
        for(int nIndex = nSeletectRow - 1; nIndex >= 0; nIndex--)
        {
            logInfo = ((LogFilterTableModel)getModel()).getRow(nIndex);
            if(logInfo.m_bMarked)
            {
                changeSelection(nIndex, 0, false, false);
                int nVisible = nIndex;
                if(!isInnerRect(parent, getCellRect(nIndex, 0, true)))
                    nVisible = nIndex - getVisibleRowCount() / 2;
                showRow(nVisible);
                return;
            }
        }

        for(int nIndex = getRowCount() - 1; nIndex > nSeletectRow; nIndex--)
        {
            logInfo = ((LogFilterTableModel)getModel()).getRow(nIndex);
            if(logInfo.m_bMarked)
            {
                changeSelection(nIndex, 0, false, false);
                int nVisible = nIndex;
                if(!isInnerRect(parent, getCellRect(nIndex, 0, true)))
                    nVisible = nIndex + getVisibleRowCount() / 2;
                showRow(nVisible);
                return;
            }
        }
    }

    public void hideColumn(int nColumn)
    {
        getColumnModel().getColumn(nColumn).setWidth(0);
        getColumnModel().getColumn(nColumn).setMinWidth(0);
        getColumnModel().getColumn(nColumn).setMaxWidth(0);
        getColumnModel().getColumn(nColumn).setPreferredWidth(0);
        getColumnModel().getColumn(nColumn).setResizable(false);
    }

    protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed)
    {
        m_bAltPressed = e.isAltDown();
//        if(e.getID() == KeyEvent.KEY_RELEASED)
        {
            switch(e.getKeyCode())
            {
                case KeyEvent.VK_END:
                    changeSelection(getRowCount() - 1, 0, false, false);
                    return true;
                case KeyEvent.VK_HOME:
                    changeSelection(0, 0, false, false);
                    return true;
                case KeyEvent.VK_F2:
                    if(e.isControlDown() && e.getID() == KeyEvent.KEY_PRESSED)
                    {
                        int[] arSelectedRow = getSelectedRows();
                        for(int nIndex : arSelectedRow)
                        {
                            LogInfo logInfo = ((LogFilterTableModel)getModel()).getRow(nIndex);
                            logInfo.m_bMarked = !logInfo.m_bMarked;
                            m_LogFilterMain.bookmarkItem(nIndex, Integer.parseInt(logInfo.m_strLine) - 1, logInfo.m_bMarked);                        }
                        repaint();
                    }
                    else if(!e.isControlDown() && e.getID() == KeyEvent.KEY_PRESSED)
                        gotoPreBookmark();
                    return true;
                case KeyEvent.VK_F3:
                    if(e.getID() == KeyEvent.KEY_PRESSED)
                        gotoNextBookmark();
                    return true;
                case KeyEvent.VK_F:
                    if(e.getID() == KeyEvent.KEY_PRESSED && ( (e.getModifiers() & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK))
                    {
                        m_LogFilterMain.setFindFocus();
                        return true;
                    }
                    break;
//                case KeyEvent.VK_O:
//                    if(e.getID() == KeyEvent.KEY_RELEASED)
//                    {
//                        m_LogFilterMain.openFileBrowser();
//                        return true;
//                    }
            }
        }
        return super.processKeyBinding(ks, e, condition, pressed);
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
        Component comp;
//        Component comp = renderer.getTableCellRendererComponent(
//            this, col.getHeaderValue(), false, false, 0, 0);
//        width = comp.getPreferredSize().width;

        JViewport viewport = (JViewport)m_LogFilterMain.m_scrollVBar.getViewport();
        Rectangle viewRect = viewport.getViewRect();
        int nFirst = m_LogFilterMain.m_tbLogTable.rowAtPoint(new Point(0, viewRect.y));
        int nLast = m_LogFilterMain.m_tbLogTable.rowAtPoint(new Point(0, viewRect.height - 1));

        if(nLast < 0)
            nLast = m_LogFilterMain.m_tbLogTable.getRowCount();
        // Get maximum width of column data
        for (int r=nFirst; r<nFirst + nLast; r++) {
            renderer = getCellRenderer(r, vColIndex);
            comp = renderer.getTableCellRendererComponent(
                this, getValueAt(r, vColIndex), false, false, r, vColIndex);
            width = Math.max(width, comp.getPreferredSize().width);
        }

        // Add margin
        width += 2*margin;

        // Set the width
        col.setPreferredWidth(width);
    }

    public float getFontSize()
    {
        return m_fFontSize;
    }
    
    public int getColumnWidth(int nColumn)
    {
        return getColumnModel().getColumn(nColumn).getWidth();
    }

    public void showColumn(int nColumn, boolean bShow)
    {
        m_arbShow[nColumn] = bShow;
        if(bShow)
        {
            getColumnModel().getColumn(nColumn).setResizable(true);
            getColumnModel().getColumn(nColumn).setMaxWidth(LogFilterTableModel.ColWidth[nColumn] * 1000);
            getColumnModel().getColumn(nColumn).setMinWidth(1);
            getColumnModel().getColumn(nColumn).setWidth(LogFilterTableModel.ColWidth[nColumn]);
            getColumnModel().getColumn(nColumn).setPreferredWidth(LogFilterTableModel.ColWidth[nColumn]);
        }
        else
            hideColumn(nColumn);
    }

    public void setColumnWidth()
    {
        for(int iIndex = 0; iIndex < getColumnCount(); iIndex++)
        {
            showColumn(iIndex, true);
        }
        showColumn(LogFilterTableModel.COMUMN_BOOKMARK, false);
//        showColumn(LogFilterTableModel.COMUMN_THREAD, false);
    }

    void setFilterFind(String strFind)
    {
        m_strFilterFind = strFind;
    }

    void SetFilterRemove(String strRemove)
    {
        m_strFilterRemove = strRemove;
    }

    void SetFilterShowTag(String strShowTag)
    {
        m_strTagShow = strShowTag;
    }

    void SetFilterShowPid(String strShowPid)
    {
        m_strPidShow = strShowPid;
    }

    void SetFilterShowTid(String strShowTid)
    {
        m_strTidShow = strShowTid;
    }

    void SetHighlight(String strHighlight)
    {
        m_strHighlight = strHighlight;
    }

    void SetFilterRemoveTag(String strRemoveTag)
    {
        m_strTagRemove = strRemoveTag;
    }

    public void setFontSize(int nFontSize)
    {
        m_fFontSize = nFontSize;
        setRowHeight(nFontSize + 4);
    }

    public void setLogParser(ILogParser iLogParser)
    {
        m_iLogParser = iLogParser;
    }

    public void setValueAt(Object aValue, int row, int column)
    {
        LogInfo logInfo = ((LogFilterTableModel)getModel()).getRow(row);
        if(column == LogFilterTableModel.COMUMN_BOOKMARK)
        {
            logInfo.m_strBookmark = (String)aValue;
            m_LogFilterMain.setBookmark(Integer.parseInt(logInfo.m_strLine) - 1, (String)aValue);
        }
    }

    public class LogCellRenderer extends DefaultTableCellRenderer
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
            if(value != null)
                value = remakeData(column, (String)value);
            Component c = super.getTableCellRendererComponent(table,
                                                              value,
                                                              isSelected,
                                                              hasFocus,
                                                              row,
                                                              column);
            LogInfo logInfo = ((LogFilterTableModel)getModel()).getRow(row);
            c.setFont(getFont().deriveFont(m_fFontSize));
            c.setForeground(logInfo.m_TextColor);
            if(isSelected)
            {
                if(logInfo.m_bMarked)
                    c.setBackground(new Color(LogColor.COLOR_BOOKMARK2));
            }
            else if(logInfo.m_bMarked)
                c.setBackground(new Color(LogColor.COLOR_BOOKMARK));
            else
                c.setBackground(Color.WHITE);

            return c;
        }

        String remakeData(int nIndex, String strText)
        {
            if(nIndex != LogFilterTableModel.COMUMN_MESSAGE && nIndex != LogFilterTableModel.COMUMN_TAG) return strText;

            String strFind = nIndex == LogFilterTableModel.COMUMN_MESSAGE ? GetFilterFind() : GetFilterShowTag();
            m_bChanged = false;

            strText = strText.replace( " ", "\u00A0" );
            if(LogColor.COLOR_HIGHLIGHT != null && LogColor.COLOR_HIGHLIGHT.length > 0)
                strText = remakeFind(strText, GetHighlight(), LogColor.COLOR_HIGHLIGHT, true);
            else
                strText = remakeFind(strText, GetHighlight(), "#00FF00", true);
            strText = remakeFind(strText, strFind, "#FF0000", false);
            if(m_bChanged)
                strText = "<html><nobr>" + strText + "</nobr></html>";

            return strText.replace("\t", "    ");
        }

        String remakeFind(String strText, String strFind, String[] arColor, boolean bUseSpan)
        {
            if(strFind == null || strFind.length() <= 0) return strText;

            strFind = strFind.replace( " ", "\u00A0" );
            StringTokenizer stk = new StringTokenizer(strFind, "|");
            String newText;
            String strToken;
            int nIndex = 0;

            while (stk.hasMoreElements())
            {
                if(nIndex >= arColor.length)
                    nIndex = 0;
                strToken = stk.nextToken();

                if(strText.toLowerCase().contains(strToken.toLowerCase()))
                {
                    if(bUseSpan)
                        newText = "<span style=\"background-color:#" + arColor[nIndex] + "\"><b>";
                    else
                        newText = "<font color=#" + arColor[nIndex] + "><b>";
                    newText += strToken;
                    if(bUseSpan)
                        newText += "</b></span>";
                    else
                        newText += "</b></font>";
                    strText = strText.replace(strToken, newText);
                    m_bChanged = true;
                    nIndex++;
                }
            }
            return strText;
        }

        String remakeFind(String strText, String strFind, String strColor, boolean bUseSpan)
        {
            if(strFind == null || strFind.length() <= 0) return strText;

            strFind = strFind.replace( " ", "\u00A0" );
            StringTokenizer stk = new StringTokenizer(strFind, "|");
            String newText;
            String strToken;

            while (stk.hasMoreElements())
            {
                strToken = stk.nextToken();

                if(strText.toLowerCase().contains(strToken.toLowerCase()))
                {
                    if(bUseSpan)
                        newText = "<span style=\"background-color:" + strColor + "\"><b>";
                    else
                        newText = "<font color=" + strColor + "><b>";
                    newText += strToken;
                    if(bUseSpan)
                        newText += "</b></span>";
                    else
                        newText += "</b></font>";
                    strText = strText.replace(strToken, newText);
                    m_bChanged = true;
                }
            }
            return strText;
        }
    }

    public void showRow(int row)
    {
        if(row < 0 ) row = 0;
        if(row > getRowCount() - 1) row = getRowCount() - 1;

        Rectangle rList = getVisibleRect();
        Rectangle rCell = getCellRect(row, 0, true);
        if(rList != null && rCell != null)
        {
            Rectangle scrollToRect = new Rectangle((int)rList.getX(), (int)rCell.getY(), (int)(rList.getWidth()), (int)rCell.getHeight());
            scrollRectToVisible(scrollToRect);
        }
    }

    public void showRow(int row, boolean bCenter)
    {
        int nLastSelectedIndex = getSelectedRow();

        changeSelection(row, 0, false, false);
        int nVisible = row;
        if(nLastSelectedIndex <= row || nLastSelectedIndex == -1)
            nVisible = row + getVisibleRowCount() / 2;
        else
            nVisible = row - getVisibleRowCount() / 2;
        if(nVisible < 0) nVisible = 0;
        else if(nVisible > getRowCount() - 1) nVisible = getRowCount() - 1;
        showRow(nVisible);
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

    @Override
    public void focusGained( FocusEvent arg0 )
    {
    }

    @Override
    public void focusLost( FocusEvent arg0 )
    {
        m_bAltPressed = false;
    }

    @Override
    public void actionPerformed( ActionEvent arg0 )
    {
        Clipboard system = Toolkit.getDefaultToolkit().getSystemClipboard();;
        StringBuffer sbf = new StringBuffer();
        int numrows = getSelectedRowCount();
        int[] rowsselected = getSelectedRows();
//        if ( !( ( numrows - 1 == rowsselected[rowsselected.length - 1] - rowsselected[0] && numrows == rowsselected.length )
//                && ( numcols - 1 == colsselected[colsselected.length - 1] - colsselected[0] && numcols == colsselected.length ) ) )
//        {
//            JOptionPane.showMessageDialog( null, "Invalid Copy Selection", "Invalid Copy Selection", JOptionPane.ERROR_MESSAGE );
//            return;
//        }

        for ( int i = 0; i < numrows; i++ )
        {
            for ( int j = 0; j < m_arbShow.length; j++ )
            {
                if(!(j == LogFilterTableModel.COMUMN_LINE) && m_arbShow[j])
                {
                    StringBuffer strTemp = new StringBuffer((String)getValueAt( rowsselected[i], j ));
                    if(j == LogFilterTableModel.COMUMN_TAG)
                    {
                        String strTag = strTemp.toString();
                        for(int k = 0; k < m_nTagLength - strTag.length(); k++)
                            strTemp.append(" ");
                    }
                    else if(j == LogFilterTableModel.COMUMN_THREAD || j == LogFilterTableModel.COMUMN_PID)
                    {
                        String strTag = strTemp.toString();
                        for(int k = 0; k < 8 - strTag.length(); k++)
                            strTemp.append(" ");
                    }
                    strTemp.append(" ");
                    sbf.append( strTemp );
                }
            }
            sbf.append( "\n" );
        }
        StringSelection stsel = new StringSelection( sbf.toString() );
        system = Toolkit.getDefaultToolkit().getSystemClipboard();
        system.setContents(stsel,stsel);
    }
    
    public void setTagLength(int nLength)
    {
        if(m_nTagLength < nLength)
        {
            m_nTagLength = nLength;
            T.d("m_nTagLength = " + m_nTagLength);
        }
    }
}
