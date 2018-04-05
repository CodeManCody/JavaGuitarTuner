package TuningLibrary;

import java.util.HashMap;

public class GuitarTunings implements TuningsADT {
    private HashMap<String,String[]> tunings;

    public GuitarTunings() {
        tunings = new HashMap<String, String[]>();
        tunings.put("Standard", new String[]{"E","A","D","G","B","E"});
        tunings.put("D Standard", new String[]{"D","G","C","F","A","D"});
        tunings.put("Drop D", new String[]{"D","A","D","G","B","E"});
        tunings.put("Drop C", new String[]{"C","G","C","F","A","D"});
    }

    public String getInstrumentName() {
        return "Guitar";
    }
    
    public HashMap<String,String[]> getTunings() {
        return tunings;
    }
}