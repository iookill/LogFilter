
public class TagInfo
{
    static final int COMUMN_TAG    = 0;
    static final int COMUMN_SHOW   = 1;
    static final int COMUMN_REMOVE = 2;

    String           m_strTag;
    boolean          m_bShow;
    boolean          m_bRemove;

    public Object getData(int nColumn)
    {
        switch(nColumn)
        {
            case COMUMN_TAG:
                return m_strTag;
            case COMUMN_SHOW:
                return m_bShow;
            case COMUMN_REMOVE:
                return m_bRemove;
        }
        return null;
    }
}
