import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

public class LogFilterTableModel extends AbstractTableModel
{
    static final int        COMUMN_LINE     = 0;
    static final int        COMUMN_DATE     = 1;
    static final int        COMUMN_TIME     = 2;
    static final int        COMUMN_LOGLV    = 3;
    static final int        COMUMN_PID      = 4;
    static final int        COMUMN_THREAD   = 5;
    static final int        COMUMN_TAG      = 6;
    static final int        COMUMN_BOOKMARK = 7;
    static final int        COMUMN_MESSAGE  = 8;
    public static final int COMUMN_MAX      = 9;

    private static final long serialVersionUID = 1L;

    public static String  ColName[]     = { "Line", "Date", "Time", "LogLV", "Pid", "Thread", "Tag", "Bookmark", "Message" };
    public static int     ColWidth[]    = { 50,     50,     100,    20,      50,    50,       100,   100,        600};
    public static int     DEFULT_WIDTH[]= { 50,     50,     100,    20,      50,    50,       100,   100,        600};
    
    ArrayList<LogInfo> m_arData;

    public static void setColumnWidth(int nColumn, int nWidth)
    {
        T.d("nWidth = " + nWidth);
        if(nWidth >= DEFULT_WIDTH[nColumn])
            ColWidth[nColumn] = nWidth;
    }

    public int getColumnCount()
    {
        return ColName.length;
    }

    public int getRowCount()
    {
        if(m_arData != null)
            return m_arData.size();
        else
            return 0;
    }

    public String getColumnName(int col) {
        return ColName[col];
    }
    
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        return m_arData.get(rowIndex).getData(columnIndex);
    }
    
    public LogInfo getRow(int row) {
        return m_arData.get(row);
    }
    
    public void setData(ArrayList<LogInfo> arData)
    {
        m_arData = arData;
    }
}
