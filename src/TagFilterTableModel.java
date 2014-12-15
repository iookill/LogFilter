import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

public class TagFilterTableModel extends AbstractTableModel
{
    private static final long serialVersionUID = 1L;

    public static String  ColName[] = { "Tag", "Show", "Remove" };
    public static int     ColWidth[]= { 120,     30,     30};

    ArrayList<TagInfo> m_arData;


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

    public TagInfo getRow(int row) {
        return m_arData.get(row);
    }

    public void setData(ArrayList<TagInfo> arData)
    {
        m_arData = arData;
    }
}
