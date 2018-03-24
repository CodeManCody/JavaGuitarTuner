
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import TuningLibrary.*;

public class AllTuner
{
    private static final double[] FREQUENCIES = 
    {16.35, 17.32, 18.35, 19.45, 20.60, 21.83, 23.12, 24.50, 25.96, 27.50, 29.14,
     30.87, 32.70, 34.65, 36.71, 38.89, 41.20, 43.65, 46.25, 49.00, 51.91, 55.00,
     58.27, 61.74, 65.41, 69.30, 73.42, 77.78, 82.41, 87.31, 92.50, 98.00, 103.83,
     110.00, 116.54, 123.47, 130.81, 138.59, 146.83, 155.56, 164.81, 174.61,
     185.00, 196.00, 207.65, 220.00, 233.08, 246.94, 261.63, 277.18, 293.66,
     311.13, 329.63, 349.23, 369.99, 392.00, 415.30, 440.00, 466.16, 493.88,
     523.25, 554.37, 587.33, 622.25, 659.25, 698.46, 739.99, 783.99, 830.61,
     880.00, 932.33, 987.77, 1046.50,1108.73,1174.66,1244.51,1318.51,1396.91,
     1479.98,1567.98,1661.22,1760.00,1864.66,1975.53,2093.00,2217.46,2349.32,
     2489.02,2637.02,2793.83,2959.96,3135.96,3322.44,3520.00,3729.31,3951.07,
     4186.01,4434.92,4698.63,4978.03,5274.04,5587.65,5919.91,6271.93,6644.88,
     7040.00,7458.62,7902.13};
    
    private static final String[] NOTES = 
    {"C0","C#","D0","D#","E0","F0","F#","G0","G#","A0","A#","B0","C1","C#","D1",
     "D#","E1","F1","F#","G1","G#","A1","A#","B1","C2","C#","D2","D#","E2","F2",
     "F#","G2","G#","A2","A#","B2","C3","C#","D3","D#","E3","F3","F#","G3","G#",
     "A3","A#","B3","C4","C#","D4","D#","E4","F4","F#","G4","G#","A4","A#","B4",
     "C5","C#","D5","D#","E5","F5","F#","G5","G#","A5","A#","B5","C6","C#","D6",
     "D#","E6","F6","F#","G6","G#","A6","A#","B6","C7","C#","D7","D#","E7","F7",
     "F#","G7","G#","A7","A#","B7","C8","C#","D8","D#","E8","F8","F#","G8","G#",
     "A8","A#","B8"};
    
    private static int closestNote(double hz)
    {
        double minDist = Double.MAX_VALUE;
        int minFreq = -1;
        for (int i = 0; i < FREQUENCIES.length; i++) 
        {
            double dist = Math.abs(FREQUENCIES[i] - hz);
            if (dist < minDist) 
            {
                minDist=dist;
                minFreq=i;
            }
        }
        return minFreq;
    }
    
    public static void main(String[] args) throws Exception
    {
        Font font = new Font("sansserif", Font.PLAIN, 48);  
        Font bigFont = new Font("sansserif", Font.PLAIN, 48);
        
        JFrame frame = new JFrame("AllTuner");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        Graph graph1 = new Graph();
        frame.add(graph1, BorderLayout.CENTER);
        
        JLabel matchLabel = new JLabel("--");
        matchLabel.setFont(bigFont);
        JLabel prevLabel = new JLabel("\u266D");    // "flat" symbol
        prevLabel.setFont(font);
        JLabel nextLabel = new JLabel("\u266F");    // "sharp" symbol
        nextLabel.setFont(font);
        
        int FREQ_RANGE = 128;
        
        JSlider freqSlider = new JSlider(JSlider.HORIZONTAL, -FREQ_RANGE, FREQ_RANGE, 0);
        
        java.util.Hashtable labels = new java.util.Hashtable();
        labels.put(0, matchLabel);
        labels.put(-FREQ_RANGE, prevLabel);
        labels.put(FREQ_RANGE, nextLabel);
        freqSlider.setLabelTable(labels);
        freqSlider.setPaintLabels(true);
        freqSlider.setPaintTicks(true);
        freqSlider.setSnapToTicks(true);
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
        
        while ( (n = targetDataLine.read(buffer, 0, buffer.length)) > 0 )
        {
            for (int i = 0; i < n; i+= 2)
            {
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
            
            for (int i = 0; i < len; i++) 
            {
                double diff = 0;
                for (int j = 0; j < len; j++) 
                    diff += Math.abs(a[j]-a[i+j]);
                
                graph1.add(diff);
                double dx = prevDiff-diff;
                
                // change of sign in dx
                if (dx < 0 && prevDx > 0) 
                {
                    // only look for troughs that drop to less than 10% of peak
                    if ( diff < (0.2*maxDiff) )     // ** changed to 20% **
                    { 
                        graph1.mark(i-1);
                        if ( sampleLen == 0 ) 
                            sampleLen=i-1;
                    }
                }
                
                prevDx = dx;
                prevDiff=diff;
                maxDiff=Math.max(diff,maxDiff);
            }
            
            graph1.repaint();
            
            if (sampleLen > 0) 
            {
                double frequency = (format.getSampleRate()/sampleLen);
                freqLabel.setText(String.format("%.2fhz",frequency));
                int note = closestNote(frequency);
                
                matchLabel.setText(NOTES[note]);
                prevLabel.setText("\u266D");    // "flat" symbol
                nextLabel.setText("\u266F");    // "sharp" symbol
                
                int value = 0;
                double matchFreq = FREQUENCIES[note];
                
                if (frequency < matchFreq) 
                {
                    double prevFreq = FREQUENCIES[note-1];
                    value = (int)(-FREQ_RANGE*(frequency-matchFreq)/(prevFreq-matchFreq));
                }
                else {
                    double nextFreq = FREQUENCIES[note+1];
                    value = (int)(FREQ_RANGE*(frequency-matchFreq)/(nextFreq-matchFreq));
                }
                freqSlider.setValue(value);
            }
            else {
                matchLabel.setText("--");
                prevLabel.setText("\u266D");    // "flat" symbol
                nextLabel.setText("\u266F");    // "sharp" symbol
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

