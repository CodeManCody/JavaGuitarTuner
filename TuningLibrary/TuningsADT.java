package TuningLibrary;

import java.util.HashMap;

public interface TuningsADT {
    public String getInstrumentName();
    
    // returns a HashMap containing tuning names and the respective array of notes
    public HashMap<String,String[]> getTunings();
}