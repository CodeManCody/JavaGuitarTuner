import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;

public class GT {
    
    private static final double[] FREQUENCIES = 
    {174.61, 164.81, 155.56, 146.83, 138.59, 130.81, 123.47, 116.54, 110.00, 
     103.83, 98.00, 92.50, 87.31, 82.41, 77.78};
    
    private static final String[] NAME = 
    {"F3", "E3", "D#", "D3", "C#", "C3", "B2", "A#", "A2", "G#", "G2", "F#",
     "F2", "E2", "D#"};
    
    public static class Graph extends JPanel {
        private java.util.List<Double> points = new ArrayList<Double>();
        private java.util.List<Integer> markers = new ArrayList<Integer>();
        
        public Graph() {
            setPreferredSize(new Dimension(320,100));
        }
        
        public synchronized void clear() {
            points.clear();
            markers.clear();
        }
        
        public synchronized void add(double value) {
            points.add(value);
        }
        
        public synchronized void mark(int pos) {
            markers.add(pos);
        }
        
        public synchronized void paint(Graphics g) {
            g.setColor(Color.BLACK);
            
            double min = Double.MAX_VALUE, max = -Double.MAX_VALUE;
            for ( double p: points ) {
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
            for ( double p: points ) {
                double y = height - (height*(p-min)/(max-min));
                double x = (width*ix)/points.size();
                
                if ( !first ) {
                    g.drawLine((int)prevX,(int)prevY,(int)x,(int)y);
                }
                
                first = false;
                prevY = y;
                prevX = x;
                ix++;
            }
            
            double zero = height - (height*(0-min)/(max-min));
            g.drawLine(0,(int)zero,(int)width,(int)zero);
            
            g.setColor(Color.RED);
            for ( int pos: markers ) {
                double x = (width*pos)/points.size();
                g.drawLine((int)x, 0, (int)x, (int)height);
            }
            
        }
    }
    
    private static double normaliseFreq(double hz) {
        // get hz into a standard range to make things easier to deal with
        while ( hz < 82.41 ) {
            hz = 2*hz;
        }
        while ( hz > 164.81 ) {
            hz = 0.5*hz;
        }
        return hz;
    }
    
    private static int closestNote(double hz) {
        double minDist = Double.MAX_VALUE;
        int minFreq = -1;
        for ( int i = 0; i < FREQUENCIES.length; i++ ) {
            double dist = Math.abs(FREQUENCIES[i]-hz);
            if ( dist < minDist ) {
                minDist=dist;
                minFreq=i;
            }
        }
        
        return minFreq;
    }
    
    public static void main(String[] args) throws Exception {
        
        Font font = new Font("sansserif", Font.PLAIN, 24);
        Font bigFont = new Font("sansserif", Font.PLAIN, 48);
        
        JFrame frame = new JFrame("5KTuner");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        Graph graph1 = new Graph();
        frame.add(graph1, BorderLayout.CENTER);
        
        JLabel matchLabel = new JLabel("--");
        matchLabel.setFont(bigFont);
        JLabel prevLabel = new JLabel("--");
        prevLabel.setFont(font);
        JLabel nextLabel = new JLabel("--");
        nextLabel.setFont(font);
        
        int FREQ_RANGE = 128;
        
        JSlider freqSlider = new JSlider(JSlider.HORIZONTAL, -FREQ_RANGE, FREQ_RANGE, 0);
        
        java.util.Hashtable labels = new java.util.Hashtable();
        labels.put(0, matchLabel);
        labels.put(-FREQ_RANGE, nextLabel);
        labels.put(FREQ_RANGE, prevLabel);
        freqSlider.setLabelTable(labels);
        freqSlider.setPaintLabels(true);
        freqSlider.setPaintTicks(true);
        freqSlider.setMajorTickSpacing(FREQ_RANGE/2);
        freqSlider.setMinorTickSpacing(FREQ_RANGE/8);
        
        frame.add(freqSlider, BorderLayout.NORTH);
        
        JLabel freqLabel = new JLabel("--");
        freqLabel.setFont(new Font("sansserif", Font.PLAIN, 14));
        frame.add(freqLabel, BorderLayout.SOUTH);
        
        frame.pack();
        frame.setVisible(true);
        
        float sampleRate = 44100;
        int sampleSizeInBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
        DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, format);
        TargetDataLine targetDataLine = (TargetDataLine)AudioSystem.getLine(dataLineInfo);
        
        // read about a second at a time
        targetDataLine.open(format, (int)sampleRate);
        targetDataLine.start();
        
        byte[] buffer = new byte[2*1200];
        int[] a = new int[buffer.length/2];
        
        int n = -1;
        while ( (n = targetDataLine.read(buffer, 0, buffer.length)) > 0 ) {
            
            for ( int i = 0; i < n; i+=2 ) {
                // convert two bytes into single value
                int value = (short)((buffer[i]&0xFF) | ((buffer[i+1]&0xFF) << 8));
                a[i >> 1] = value;
            }
            
            double prevDiff = 0;
            double prevDx = 0;
            double maxDiff = 0;
            
            int sampleLen = 0;
            
            
            graph1.clear();
            int len = a.length/2;
            for ( int i = 0; i < len; i++ ) {
                double diff = 0;
                for ( int j = 0; j < len; j++ ) {
                    diff += Math.abs(a[j]-a[i+j]);
                }
                
                graph1.add(diff);
                
                double dx = prevDiff-diff;
                
                // change of sign in dx
                if ( dx < 0 && prevDx > 0 ) {
                    // only look for troughs that drop to less than 10% of peak
                    if ( diff < (0.1*maxDiff) ) {
                        graph1.mark(i-1);
                        if ( sampleLen == 0 ) {
                            sampleLen=i-1;
                        }
                    }
                }
                
                prevDx = dx;
                prevDiff=diff;
                maxDiff=Math.max(diff,maxDiff);
            }
            graph1.repaint();
            
            if ( sampleLen > 0 ) {
                double frequency = (format.getSampleRate()/sampleLen);
                double trueFrequency = frequency;
                freqLabel.setText(String.format("%.2fhz",trueFrequency));
                
                frequency = normaliseFreq(frequency);
                int note = closestNote(frequency);
                
                if(trueFrequency >= 285 && trueFrequency <= 300)
                    matchLabel.setText("D4");
                else if(trueFrequency >= 320 && trueFrequency <= 340)
                    matchLabel.setText("E4");
                else if(trueFrequency >= 240 && trueFrequency <= 252)
                    matchLabel.setText("B3");
                else if(trueFrequency >= 190 && trueFrequency <= 203)
                    matchLabel.setText("G3");
                else if(trueFrequency >= 213 && trueFrequency <= 227)
                    matchLabel.setText("A3");
                else if(trueFrequency >= 170 && trueFrequency <= 180)
                    matchLabel.setText("F3");
                else
                    matchLabel.setText(NAME[note]);
                
                prevLabel.setText(NAME[note-1]);
                nextLabel.setText(NAME[note+1]);
                
                int value = 0;
                double matchFreq = FREQUENCIES[note];
                if ( frequency < matchFreq ) {
                    double prevFreq = FREQUENCIES[note+1];
                    value = (int)(-FREQ_RANGE*(frequency-matchFreq)/(prevFreq-matchFreq));
                }
                else {
                    double nextFreq = FREQUENCIES[note-1];
                    value = (int)(FREQ_RANGE*(frequency-matchFreq)/(nextFreq-matchFreq));
                }
                freqSlider.setValue(value);
            }
            else {
                matchLabel.setText("--");
                prevLabel.setText("--");
                nextLabel.setText("--");
                freqSlider.setValue(0);
                freqLabel.setText("--");
            }
            prevLabel.setSize(prevLabel.getPreferredSize());
            nextLabel.setSize(nextLabel.getPreferredSize());
            matchLabel.setSize(matchLabel.getPreferredSize());
            
            freqSlider.repaint();
            freqLabel.repaint();
            
            try { Thread.sleep(250); }catch( Exception e ){}
        }
    }
    
}

