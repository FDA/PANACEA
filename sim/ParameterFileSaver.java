package com.eng.cber.na.sim;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.eng.cber.na.sim.gui.signal.SymptomsBySymptom;
import com.eng.cber.na.sim.rstruct.SimulatedSignal;
import com.eng.cber.na.util.json.JSONObject;

/**
 * Saves all the parameters from the current state of the 
 * GUI into a .json file, so that the user can set up the
 * same simulation later.  This will not contain the full
 * version of the original input data, but it does contain
 * everything needed to run simulations that was extracted
 * from the original data. 
 */
public class ParameterFileSaver {

	public static String[] labels = {"numInputReports", "groupSize", "probabilityNewVaxPerGroup", "probabilityNewSymPerGroup", "vaxPerReportDistribution", "symPerReportDistribution",
									 "numReportsToSimulate", "reportsPerInterval", "numStartSym", "numStartVax", "randomSeed", "simulateWithSignal", "numSignals",
									 "numSimulations",
									 "outputMatrix", "outputPanacea", "outputEdgelist", "outputFilePath", "filenamePrefix",
									 "dataSource"};

	
	NetworkSimulatorGUI main;
	PrintWriter writer;
	String writeFileName;
	JSONObject jsonObject;
	
	
	public ParameterFileSaver(String writeFileName) {
		this.writeFileName = writeFileName;
		main = NetworkSimulatorGUI.getInstance();
		jsonObject = new JSONObject();
	}
	
	public void saveParamsToFile() {
	
		try {
			writer = new PrintWriter(writeFileName);
		} catch (FileNotFoundException fnfe) {
			JOptionPane.showMessageDialog(main,"Cannot find file \"" + writeFileName + "\"", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		List<Object> objects = new ArrayList<Object>(labels.length);
		
		objects.add(main.getNumInputReports());
		objects.add(main.getGroupSize());
		objects.add(main.getParameterListing().getNewVaxDist());
		objects.add(main.getParameterListing().getNewSymDist());
		objects.add(main.getParameterListing().getVaxPerReportDist());
		objects.add(main.getParameterListing().getSymPerReportDist());
		
		objects.add(main.getNumReportsToSimulate());
		objects.add(main.getReportsPerInterval());
		objects.add(main.getNumStartVax());
		objects.add(main.getNumStartSym());
		objects.add(main.getRandomSeed());
		objects.add(main.isSignalChecked());
		objects.add(main.getNumStoredSignals());
		
		objects.add(main.getNumberSimulations());
		
		objects.add(main.isOutputMatrixChecked());
		objects.add(main.isOutputPanaceaChecked());
		objects.add(main.isOutputEdgelistChecked());
		objects.add(main.getOutputPath());
		objects.add(main.getFilenamePrefix());
		
		objects.add(main.getParameterListing().getDataSource());
		
		// Put information into a JSONObject as key/value pairs.
		for (int i = 0; i < labels.length; i++) {
			jsonObject.put(labels[i], objects.get(i));
		}
		
		// Now add the signals, since there are a variable number of those.
		List<SimulatedSignal> signalList = main.getStoredSignals();
		for (SimulatedSignal signal : signalList) {
			JSONObject jsonSubObject = new JSONObject();
			jsonSubObject.put("signalName", signal.getSignalName());
			jsonSubObject.put("vaccineEntryReport", signal.getVXEntry());
			jsonSubObject.put("vaccineWeight", signal.getVXWeight());
			jsonSubObject.put("symBackgroundPercentiles", signal.getPTRanks());
			jsonSubObject.put("symCoOccurrenceProbabilities", signal.getPTProbs());
			if (signal.getAssociatedSymptoms() instanceof SymptomsBySymptom) {
				jsonSubObject.put("inputVaccineName", ((SymptomsBySymptom) signal.getAssociatedSymptoms()).getVaxName());
				jsonSubObject.put("inputSymNames", ((SymptomsBySymptom) signal.getAssociatedSymptoms()).getSymptoms());
				jsonSubObject.put("dataSource", ((SymptomsBySymptom) signal.getAssociatedSymptoms()).getDataSource());
			}
			
			jsonObject.append("signals", jsonSubObject);
		}
		
		writer.write(jsonObject.toString(3));
		
		writer.flush();
		writer.close();
		
	}
	
}
