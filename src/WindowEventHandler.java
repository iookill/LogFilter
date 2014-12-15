import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class WindowEventHandler implements WindowListener
{
    public void windowClosing(WindowEvent e)
    {
        e.getWindow().setVisible(false);
        e.getWindow().dispose();
        System.exit(0);
    }

    public void windowActivated(WindowEvent e)  {}
    public void windowOpened(WindowEvent e)     {}
    public void windowClosed(WindowEvent e)     {}
    public void windowIconified(WindowEvent e)  {}    
    public void windowDeiconified(WindowEvent e){}    
    public void windowDeactivated(WindowEvent e){}
}
