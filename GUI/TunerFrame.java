package GUI;

import Tuner.*;
import java.awt.Color;
import java.util.Map;

public class TunerFrame extends javax.swing.JFrame {
    private TunerLibrary tunings = new TunerLibrary();
    private int FREQ_RANGE = 128;
    private int GREEN_TEXT_THRESHOLD = FREQ_RANGE / 8;
    
    public TunerFrame() {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels())
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
        }
        catch (Exception e) { }
        
        initComponents();
        java.awt.Font font = new java.awt.Font("Dialog", 1, 18);
        lblFreq.setFont(font);
        lblFlat.setFont(font);
        lblSharp.setFont(font);
        sldVariance.setMinimum(-FREQ_RANGE);
        sldVariance.setMaximum(FREQ_RANGE);
        sldVariance.setMajorTickSpacing(FREQ_RANGE / 2);
        sldVariance.setMinorTickSpacing(FREQ_RANGE / 8);
        populateTunings(cboTunings);
        updateFrequency(0);
        //setExtendedState(java.awt.Frame.MAXIMIZED_BOTH); // start maximized
        this.setVisible(true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sldVariance = new javax.swing.JSlider();
        cboTunings = new javax.swing.JComboBox<>();
        lblNote = new javax.swing.JLabel();
        lblTuningNotes = new javax.swing.JLabel();
        lblFlat = new javax.swing.JLabel();
        lblFreq = new javax.swing.JLabel();
        lblSharp = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Tuner");

        sldVariance.setPaintTicks(true);
        sldVariance.setSnapToTicks(true);
        sldVariance.setValue(0);

        cboTunings.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "(Choose Tuning Profile)" }));
        cboTunings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTuningsActionPerformed(evt);
            }
        });

        lblNote.setFont(new java.awt.Font("Dialog", 1, 48)); // NOI18N
        lblNote.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNote.setText("---");

        lblTuningNotes.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        lblTuningNotes.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        lblFlat.setText("♭");

        lblFreq.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblFreq.setText("--");

        lblSharp.setText("♯");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblFlat)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblFreq)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblSharp))
                    .addComponent(lblTuningNotes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblNote, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cboTunings, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(sldVariance, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 537, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cboTunings, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTuningNotes)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sldVariance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFreq)
                    .addComponent(lblFlat)
                    .addComponent(lblSharp))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblNote, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void updateFrequency(double frequency) 
    {
        if (!(Math.abs(frequency) <= 0.0001)) 
        {
            TunerCalculator calc = new TunerCalculator();
            TunerData data = new TunerData();
            String[] Notes = data.getNotes();
            double[] Frequencies = data.getFrequencies();
            
            lblFreq.setText(String.format("%.2fhz", frequency));
            int note = calc.getClosestNote(frequency);
            lblNote.setText(Notes[note]);
            int variance = 0;
            double matchFreq = Frequencies[note];

            if (frequency < matchFreq) 
            {
                double prevFreq = Frequencies[note - 1];
                variance = (int) (-FREQ_RANGE * (frequency - matchFreq) / (prevFreq - matchFreq));
            } 
            else 
            {
                double nextFreq = Frequencies[note + 1];
                variance = (int) (FREQ_RANGE * (frequency - matchFreq) / (nextFreq - matchFreq));
            }
            
            sldVariance.setValue(variance);
            if (variance >= -GREEN_TEXT_THRESHOLD && variance <= GREEN_TEXT_THRESHOLD)
                lblNote.setForeground(new Color(0, 128, 0)); // green
            else
                lblNote.setForeground(Color.RED);
        }
        else 
        {
            lblNote.setForeground(Color.BLACK);
            lblNote.setText("--");
            sldVariance.setValue(0);
            lblFreq.setText(String.format("%.2fhz", frequency));
        }
    }

    private void populateTunings(javax.swing.JComboBox comboBox) {
        for(Map.Entry<String, String> entry : tunings.getTunings().entrySet())
            comboBox.addItem(entry.getKey());
    }
    
    private void cboTuningsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTuningsActionPerformed
        lblTuningNotes.setText(tunings.getTunings().get(cboTunings.getSelectedItem()));
    }//GEN-LAST:event_cboTuningsActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cboTunings;
    private javax.swing.JLabel lblFlat;
    private javax.swing.JLabel lblFreq;
    private javax.swing.JLabel lblNote;
    private javax.swing.JLabel lblSharp;
    private javax.swing.JLabel lblTuningNotes;
    private javax.swing.JSlider sldVariance;
    // End of variables declaration//GEN-END:variables

}
