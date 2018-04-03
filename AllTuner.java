
import javax.swing.*;
import java.awt.*;
import TuningLibrary.*;
import GUI.*;

public class AllTuner {

	public static void main(String[] args) throws Exception {
		TunerFrame tunerFrame = new TunerFrame();
		while (true) {

			Tuner tuner = new Tuner();
			tunerFrame.updateFrequency(tuner.getFrequency());
			try {
				Thread.sleep(250);
			} 
			catch (Exception e) {
			}
		}

	}
}
