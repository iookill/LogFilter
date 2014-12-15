/**
 * 
 */

/**
 * 
 */
public interface INotiEvent
{
    public static int EVENT_CLICK_BOOKMARK              = 0;
    public static int EVENT_CLICK_ERROR                 = 1;
    public static int EVENT_CHANGE_FILTER_SHOW_TAG      = 2;
    public static int EVENT_CHANGE_FILTER_REMOVE_TAG    = 3;
    public static int EVENT_CHANGE_FILTER_FIND_WORD     = 4;
    public static int EVENT_CHANGE_FILTER_REMOVE_WORD   = 5;

    void notiEvent(EventParam param);
    
    class EventParam
    {
        int nEventId;
        Object param1;
        Object param2;
        Object param3;
        
        public EventParam(int nEventId)
        {
            this.nEventId = nEventId;
        }
    }
}
