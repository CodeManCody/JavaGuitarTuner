
package GUI;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import Tuner.*;

public class TunerFrame 
{
    Font font = new Font("sansserif", Font.PLAIN, 48);
    Font bigFont = new Font("sansserif", Font.PLAIN, 48);
    JLabel freqLabel = new JLabel("--");
    JFrame frame = new JFrame("AllTuner");
    JLabel matchLabel = new JLabel("--");
    JLabel prevLabel = new JLabel("\u266D"); // "flat" symbol
    JLabel nextLabel = new JLabel("\u266F"); // "sharp" symbol
    java.util.Hashtable labels = new java.util.Hashtable();
    int FREQ_RANGE = 128;
    JSlider freqSlider = new JSlider(JSlider.HORIZONTAL, -FREQ_RANGE, FREQ_RANGE, 0);

    public TunerFrame() 
    {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        matchLabel.setFont(bigFont);
        prevLabel.setFont(font);
        nextLabel.setFont(font);
        labels.put(0, matchLabel);
        labels.put(-FREQ_RANGE, prevLabel);
        labels.put(FREQ_RANGE, nextLabel);
        freqSlider.setLabelTable(labels);
        freqSlider.setPaintLabels(true);
        freqSlider.setPaintTicks(true);
        freqSlider.setSnapToTicks(true);
        freqSlider.setMajorTickSpacing(FREQ_RANGE / 2);
        freqSlider.setMinorTickSpacing(FREQ_RANGE / 8);
        frame.add(freqSlider, BorderLayout.NORTH);
        freqLabel.setFont(new Font("sansserif", Font.PLAIN, 14));
        frame.add(freqLabel, BorderLayout.SOUTH);
        frame.pack();
        frame.setVisible(true);
    }

    public void updateFrequency(double frequency) 
    {
        if (!(Math.abs(frequency) <= 0.0001)) 
        {
            TunerCalculator calc = new TunerCalculator();
            TunerData data = new TunerData();
            String[] Notes = data.getNotes();
            double[] Frequencies = data.getFrequencies();
            
            freqLabel.setText(String.format("%.2fhz", frequency));
            int note = calc.getClosestNote(frequency);
            matchLabel.setText(Notes[note]);
            int value = 0;
            double matchFreq = Frequencies[note];

            if (frequency < matchFreq) 
            {
                double prevFreq = Frequencies[note - 1];
                value = (int) (-FREQ_RANGE * (frequency - matchFreq) / (prevFreq - matchFreq));
            } 
            else 
            {
                double nextFreq = Frequencies[note + 1];
                value = (int) (FREQ_RANGE * (frequency - matchFreq) / (nextFreq - matchFreq));
            }
            freqSlider.setValue(value);
        } 
        else 
        {
            matchLabel.setText("--");
            prevLabel.setText("\u266D"); // "flat" symbol
            nextLabel.setText("\u266F"); // "sharp" symbol
            freqSlider.setValue(0);
            freqLabel.setText("--");
        }

        prevLabel.setSize(prevLabel.getPreferredSize());
        nextLabel.setSize(nextLabel.getPreferredSize());
        matchLabel.setSize(matchLabel.getPreferredSize());

        freqSlider.repaint();
        freqLabel.repaint();
    }
}