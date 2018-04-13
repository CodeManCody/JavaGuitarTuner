package Tuner;

import java.util.LinkedHashMap;

public class TunerLibrary {
    private LinkedHashMap<String,String> tunings;

    public TunerLibrary() {
        tunings = new LinkedHashMap<String, String>();
        tunings.put("Guitar - Standard", "E A D G B E");
        tunings.put("Guitar - D Standard", "D G C F A D");
        tunings.put("Guitar - Drop D", "D A D G B E");
        tunings.put("Guitar - Drop C", "C G C F A D");
        tunings.put("Bass - Standard", "B E A D G C");
    }
    
    public LinkedHashMap<String,String> getTunings() {
        return tunings;
    }
}