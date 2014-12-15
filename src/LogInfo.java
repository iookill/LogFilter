import java.awt.Color;

public class LogInfo
{
    public static final int LOG_LV_VERBOSE  = 1;
    public static final int LOG_LV_DEBUG    = LOG_LV_VERBOSE << 1;
    public static final int LOG_LV_INFO     = LOG_LV_DEBUG << 1;
    public static final int LOG_LV_WARN     = LOG_LV_INFO << 1;
    public static final int LOG_LV_ERROR    = LOG_LV_WARN << 1;
    public static final int LOG_LV_FATAL    = LOG_LV_ERROR << 1;
    public static final int LOG_LV_ALL      = LOG_LV_VERBOSE | LOG_LV_DEBUG | LOG_LV_INFO
                                              | LOG_LV_WARN | LOG_LV_ERROR | LOG_LV_FATAL;
    
    boolean                 m_bMarked;
    String                  m_strBookmark   = "";
    String                  m_strDate       = "";
    String                  m_strLine       = "";
    String                  m_strTime       = "";
    String                  m_strLogLV      = "";
    String                  m_strPid        = "";
    String                  m_strThread     = "";
    String                  m_strTag        = "";
    String                  m_strMessage    = "";
    Color                   m_TextColor;
    
    public void display()
    {
        T.d("=============================================");
        T.d("m_bMarked      = " + m_bMarked);
        T.d("m_strBookmark  = " + m_strBookmark);
        T.d("m_strDate      = " + m_strDate);
        T.d("m_strLine      = " + m_strLine);
        T.d("m_strTime      = " + m_strTime);
        T.d("m_strLogLV     = " + m_strLogLV);
        T.d("m_strPid       = " + m_strPid);
        T.d("m_strThread    = " + m_strThread);
        T.d("m_strTag       = " + m_strTag);
        T.d("m_strMessage   = " + m_strMessage);
        T.d("=============================================");
    }
    
    public Object getData(int nColumn)
    {
        switch(nColumn)
        {
            case LogFilterTableModel.COMUMN_LINE:
                return m_strLine;
            case LogFilterTableModel.COMUMN_DATE:
                return m_strDate;
            case LogFilterTableModel.COMUMN_TIME:
                return m_strTime;
            case LogFilterTableModel.COMUMN_LOGLV:
                return m_strLogLV;
            case LogFilterTableModel.COMUMN_PID:
                return m_strPid;
            case LogFilterTableModel.COMUMN_THREAD:
                return m_strThread;
            case LogFilterTableModel.COMUMN_TAG:
                return m_strTag;
            case LogFilterTableModel.COMUMN_BOOKMARK:
                return m_strBookmark;
            case LogFilterTableModel.COMUMN_MESSAGE:
                return m_strMessage;
        }
        return null;
    }
}
