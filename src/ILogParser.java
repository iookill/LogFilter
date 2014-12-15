import java.awt.Color;

/**
 * 
 */


/**
 * 
 */
public interface ILogParser
{
    public static final int TYPE_ANDROID_DDMS   = 0;
    public static final int TYPE_ANDROID_LOGCAT = 1;
    
    public LogInfo parseLog(String strText);
    public Color   getColor(LogInfo logInfo);
    public int     getLogLV(LogInfo logInfo);
}
