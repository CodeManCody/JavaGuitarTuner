
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import javax.swing.JPanel;

public class Graph extends JPanel 
{
        private java.util.List<Double> points = new ArrayList<Double>();
        private java.util.List<Integer> markers = new ArrayList<Integer>();
        
        public Graph() 
        { 
            setPreferredSize(new Dimension(320,100));
        }
        
        public synchronized void clear() 
        {
            points.clear();
            markers.clear();
        }
        
        public synchronized void add(double value) 
        {
            points.add(value);
        }
        
        public synchronized void mark(int pos) 
        {
            markers.add(pos);
        }
        
        public synchronized void paint(Graphics g) 
        {
            g.setColor(Color.BLACK);
            double min = Double.MAX_VALUE, max = -Double.MAX_VALUE;
            
            for (double p: points) 
            {
                min = Math.min(p, min);
                max = Math.max(p, max);
            }
            
            double width  = getWidth();
            double height = getHeight();
            
            g.clearRect(0,0,(int)width,(int)height);
            g.drawRect(0,0,(int)width,(int)height);
            
            double prevY = 0, prevX = 0;
            boolean first = true;
            int ix = 0;
            
            for (double p: points) 
            {
                double y = height - (height*(p-min)/(max-min));
                double x = (width*ix)/points.size();
                
                if (!first) 
                    g.drawLine((int)prevX,(int)prevY,(int)x,(int)y);
                
                first = false;
                prevY = y;
                prevX = x;
                ix++;
            }
            
            double zero = height - (height*(0-min)/(max-min));
            g.drawLine(0,(int)zero,(int)width,(int)zero);
            g.setColor(Color.RED);
            
            for (int pos: markers) 
            {
                double x = (width*pos)/points.size();
                g.drawLine((int)x, 0, (int)x, (int)height);
            }
        }
}