import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.border.EmptyBorder;

public class IndicatorPanel extends JPanel
{
    private static final long serialVersionUID      = 1L;

    final int                 INDICATRO_BOOK_X_POS  = 5;
    final int                 INDICATRO_WIDTH       = 12;
    final int                 INDICATRO_ERROR_X_POS = 23;
    final int                 INDICATRO_Y_POS       = 22;
    final int                 INDICATRO_Y_GAP       = 5;

    Rectangle                 m_rcBookmark;
    Rectangle                 m_rcError;
    JCheckBox                 m_chBookmark;
    JCheckBox                 m_chError;
    ArrayList<LogInfo>        m_arLogInfo;
    HashMap<Integer, Integer> m_hmBookmark;
    HashMap<Integer, Integer> m_hmError;
    Graphics                  m_g;
    LogFilterMain             m_LogFilterMain;
    public boolean            m_bDrawFull;
    

    public IndicatorPanel(LogFilterMain logFilterMain)
    {
        super();
        m_LogFilterMain = logFilterMain;
        m_chBookmark = new JCheckBox();
//        m_chBookmark.setBackground( new Color( 555555 ) );
        m_chBookmark.addItemListener(m_itemListener);
        m_chBookmark.setBorder( new EmptyBorder( 0, 0, 0, 0 ) );

        m_chError = new JCheckBox();
        m_chError.addItemListener(m_itemListener);
        m_chError.setBorder( new EmptyBorder( 0, 0, 0, 0 ) );

        m_rcBookmark = new Rectangle();
        m_rcError = new Rectangle();
        m_bDrawFull = true;
        add(m_chBookmark);
        add(m_chError);

        addMouseListener(new MouseListener()
        {
            public void mouseReleased(MouseEvent e){}            
            public void mousePressed(MouseEvent e)
            {
                if(m_arLogInfo != null)
                {
                    float fRate = (float)(e.getY() - m_rcBookmark.y)/(float)(m_rcBookmark.height);
                    int nIndex = (int)(m_arLogInfo.size() * fRate);
                    m_LogFilterMain.m_tbLogTable.showRow(nIndex, false);
                }
            }
            
            public void mouseExited(MouseEvent e){}            
            public void mouseEntered(MouseEvent e){}            
            public void mouseClicked(MouseEvent e){}
        });
        addMouseMotionListener(new MouseMotionListener()
        {
            public void mouseMoved(MouseEvent e){}
            public void mouseDragged(MouseEvent e)
            {
                if(m_arLogInfo != null)
                {
                    float fRate = (float)(e.getY() - m_rcBookmark.y)/(float)(m_rcBookmark.height);
                    int nIndex = (int)(m_arLogInfo.size() * fRate);
                    m_LogFilterMain.m_tbLogTable.showRow(nIndex, false);
                }
            }
        });
        addMouseWheelListener(new MouseWheelListener()
        {
            public void mouseWheelMoved(MouseWheelEvent e)
            {
                m_LogFilterMain.m_scrollVBar.dispatchEvent(e);
            }
        });
    }
    
    public void testMsg(String strMsg)
    {
        JOptionPane.showMessageDialog(this, strMsg);
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        m_g = g;
        m_rcBookmark.setBounds(INDICATRO_BOOK_X_POS, INDICATRO_Y_POS, INDICATRO_WIDTH, getHeight() - INDICATRO_Y_POS - INDICATRO_Y_GAP);
        m_rcError.setBounds(INDICATRO_ERROR_X_POS, INDICATRO_Y_POS, INDICATRO_WIDTH, getHeight() - INDICATRO_Y_POS - INDICATRO_Y_GAP);
        if(m_bDrawFull)
        {
            drawIndicator(m_g);
        }
        drawBookmark(m_g);
        drawError(m_g);
        drawPageIndicator(m_g);
        
        m_bDrawFull = true;
    }
    
    void drawIndicator(Graphics g)
    {
        if(m_arLogInfo == null) return;

        int TOTAL_COUNT = m_arLogInfo.size();

        if(TOTAL_COUNT > 0)
        {
            int HEIGHT      = 1;
            int MIN_HEIGHT  = 1;
            float fRate = (float)m_rcBookmark.height / (float)TOTAL_COUNT;
            if(m_rcBookmark.height > TOTAL_COUNT)
                HEIGHT = m_rcBookmark.height / TOTAL_COUNT + 1;

            //북마크 indicator를 그린다.
            for( Integer nIndex : m_hmBookmark.keySet())
            {
                if(m_LogFilterMain.m_nChangedFilter == LogFilterMain.STATUS_CHANGE || m_LogFilterMain.m_nChangedFilter == LogFilterMain.STATUS_PARSING)
                    break;
                int nY1 = (int)(INDICATRO_Y_POS + m_hmBookmark.get(nIndex) * fRate);
                int nY2 = (int)(nY1 + HEIGHT);
                if(nY2 - nY1 <= 0)
                    nY2 = nY1 + MIN_HEIGHT;
                if(nY2 > m_rcBookmark.y + m_rcBookmark.height)
                    nY2 = m_rcBookmark.y + m_rcBookmark.height;
                g.setColor(Color.BLUE);
                g.fillRect(m_rcBookmark.x, nY1, m_rcBookmark.width, nY2 - nY1);
            }


            //에러 indicator를 그린다.
            for( Integer nIndex : m_hmError.keySet())
            {
                if(m_LogFilterMain.m_nChangedFilter == LogFilterMain.STATUS_CHANGE || m_LogFilterMain.m_nChangedFilter == LogFilterMain.STATUS_PARSING)
                    break;
                int nY1 = (int)(INDICATRO_Y_POS + m_hmError.get(nIndex) * fRate);
                int nY2 = (int)(nY1 + HEIGHT);
                if(nY2 - nY1 <= 0)
                    nY2 = nY1 + MIN_HEIGHT;
                if(nY2 > m_rcError.y + m_rcError.height)
                    nY2 = m_rcError.y + m_rcError.height;
                g.setColor(Color.RED);
                g.fillRect(m_rcError.x, nY1, m_rcError.width, nY2 - nY1);
            }
        }
    }

    void drawBookmark(Graphics g)
    {
        g.setColor(Color.BLUE);
        g.drawRect(m_rcBookmark.x, m_rcBookmark.y, m_rcBookmark.width, m_rcBookmark.height);
    }

    void drawError(Graphics g)
    {
        g.setColor(Color.RED);
        g.drawRect(m_rcError.x, m_rcError.y, m_rcError.width, m_rcError.height);
    }
    
    int PAGE_INDICATOR_WIDTH = 3;
    int PAGE_INDICATOR_GAP = 2;
    void drawPageIndicator(Graphics g)
    {
        if(m_arLogInfo == null) return;

        int TOTAL_COUNT = m_arLogInfo.size();

        if(TOTAL_COUNT > 0)
        {
            JViewport viewport = (JViewport)m_LogFilterMain.m_scrollVBar.getViewport();
            Rectangle viewRect = viewport.getViewRect();
            
            int nItemHeight = m_LogFilterMain.m_tbLogTable.getRowHeight();
            if(nItemHeight > 0)
            {
                float fRate = (float)m_rcBookmark.height / (float)TOTAL_COUNT;

                int nFirst = m_LogFilterMain.m_tbLogTable.rowAtPoint(new Point(0, viewRect.y));
                int nLast = m_LogFilterMain.m_tbLogTable.rowAtPoint(new Point(0, viewRect.height - 1));
                int nY1 = (int)(m_rcBookmark.y + nFirst * fRate);
                int nH = (int)((nLast + 1) * fRate);
                if(nH <= 0)
                    nH = 1;
                if(nY1 + nH > m_rcBookmark.y + m_rcBookmark.height)
                    nH = m_rcBookmark.y + m_rcBookmark.height - nY1;
                if(nLast == - 1)
                    nH = m_rcBookmark.height;

                g.drawRect(m_rcBookmark.x - PAGE_INDICATOR_WIDTH - PAGE_INDICATOR_GAP, nY1, PAGE_INDICATOR_WIDTH, nH);
                g.drawRect(m_rcError.x + m_rcError.width + PAGE_INDICATOR_GAP, nY1, PAGE_INDICATOR_WIDTH, nH);
            }
        }
    }

    ItemListener m_itemListener = new ItemListener() {
        public void itemStateChanged(ItemEvent itemEvent) {
            if(itemEvent.getSource().equals(m_chBookmark))
            {
                m_LogFilterMain.notiEvent(new INotiEvent.EventParam(INotiEvent.EVENT_CLICK_BOOKMARK));
            }
            else if(itemEvent.getSource().equals(m_chError))
            {
                m_LogFilterMain.notiEvent(new INotiEvent.EventParam(INotiEvent.EVENT_CLICK_ERROR));
            }
        }
    };
    
    public void setData(ArrayList<LogInfo> arLogInfo, HashMap<Integer, Integer> hmBookmark, HashMap<Integer, Integer> hmError)
    {
        m_arLogInfo     = arLogInfo;
        m_hmBookmark    = hmBookmark;
        m_hmError       = hmError;
    }
}
