
import javax.swing.*;
import java.awt.*;
import TuningLibrary.*;
public class AllTuner
{
    
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
        while(true){

            Tuner tuner = new Tuner();
        	double frequency;
            if((frequency = tuner.getFrequency())!=0){
            graph1.repaint();
                freqLabel.setText(String.format("%.2fhz",frequency));
                int note = tuner.getClosestNote(frequency);
                matchLabel.setText(TuningLibrary.FrequencyDataSimpleton.NOTES[note]);
                int value = 0;
                double matchFreq = TuningLibrary.FrequencyDataSimpleton.FREQUENCIES[note];
                
                if (frequency < matchFreq) 
                {
                    double prevFreq = TuningLibrary.FrequencyDataSimpleton.FREQUENCIES[note-1];
                    value = (int)(-FREQ_RANGE*(frequency-matchFreq)/(prevFreq-matchFreq));
                }
                else {
                    double nextFreq = TuningLibrary.FrequencyDataSimpleton.FREQUENCIES[note+1];
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

