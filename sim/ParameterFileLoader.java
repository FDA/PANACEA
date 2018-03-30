package com.eng.cber.na.sim;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.eng.cber.na.sim.gui.signal.AssociatedSymptoms;
import com.eng.cber.na.sim.gui.signal.SymptomsByRank;
import com.eng.cber.na.sim.gui.signal.SymptomsBySymptom;
import com.eng.cber.na.sim.rstruct.Function;
import com.eng.cber.na.sim.rstruct.PiecewiseFunction;
import com.eng.cber.na.sim.rstruct.SimulatedSignal;
import com.eng.cber.na.util.json.JSONArray;
import com.eng.cber.na.util.json.JSONException;
import com.eng.cber.na.util.json.JSONObject;

/**
 *  Reads a previously saved parameter file (in .json format)
 *  and extracts parameters that can be used as input into a
 *  simulation.  Also restores any signals that were saved
 *  along with the previous parameters.
 *  
 *   Note: Loading a saved parameter file will put the
 *   NetworkSimulatorGui into STORED_MODE because the full 
 *   set of original data is not saved in the .json file,
 *   which means that certain options must be unavailable.
 */
public class ParameterFileLoader implements ParameterListing {

	private JSONObject jsonObject;

	private List<Double> probabilityNewSymPerGroup, probabilityNewVaxPerGroup;
	private List<SimulatedSignal> signals;
	
	private int groupSize,numInputReports,numReportsToSimulate,reportsPerInterval,numStartSym,numStartVax,numSimulations;
	private long randomSeed;
	private boolean simulateWithSignal,outputMatrix,outputPanacea,outputEdgelist;
	private String dataSource,outputFilePath,filenamePrefix;
	private List<Double> symPerReportDistribution,vaxPerReportDistribution;
	
	public ParameterFileLoader(File f) throws FileNotFoundException, IOException, JSONException {
		InputStream inputStream = new FileInputStream(f);
		String fileAsString = org.apache.commons.io.IOUtils.toString(inputStream);
		jsonObject = new JSONObject(fileAsString);
		
		// Assemble JSONArrays into lists.
		JSONArray jsonArraySymDist = jsonObject.getJSONArray("probabilityNewSymPerGroup");
		probabilityNewSymPerGroup = new ArrayList<Double>(jsonArraySymDist.length());
		for (int i = 0; i < jsonArraySymDist.length(); i++) {
			probabilityNewSymPerGroup.add(jsonArraySymDist.getDouble(i));
		}
		
		JSONArray jsonArrayVaxDist = jsonObject.getJSONArray("probabilityNewVaxPerGroup");
		probabilityNewVaxPerGroup = new ArrayList<Double>(jsonArrayVaxDist.length());
		for (int i = 0; i < jsonArrayVaxDist.length(); i++) {
			probabilityNewVaxPerGroup.add(jsonArrayVaxDist.getDouble(i));
		}
		
		JSONArray jsonArraySymPerReport = jsonObject.getJSONArray("symPerReportDistribution");
		symPerReportDistribution = new ArrayList<Double>(jsonArraySymPerReport.length());
		for (int i = 0; i < jsonArraySymPerReport.length(); i++) {
			symPerReportDistribution.add(jsonArraySymPerReport.getDouble(i));
		}
		
		JSONArray jsonArrayVaxPerReport = jsonObject.getJSONArray("vaxPerReportDistribution");
		vaxPerReportDistribution = new ArrayList<Double>(jsonArrayVaxPerReport.length());
		for (int i = 0; i < jsonArrayVaxPerReport.length(); i++) {
			vaxPerReportDistribution.add(jsonArrayVaxPerReport.getDouble(i));
		}
		
		groupSize = jsonObject.getInt("groupSize");


		// Re-create the signals that were stored.
		dataSource = "Loaded from: " + f.getAbsolutePath() + "\nPrevious Data Source: " + jsonObject.getString("dataSource");
		if (jsonObject.getInt("numSignals") > 0) {
			JSONArray jsonArraySignals = jsonObject.getJSONArray("signals");
			signals = new ArrayList<SimulatedSignal>(jsonArraySignals.length());
			for (int i = 0; i < jsonArraySignals.length(); i++) {
				JSONObject jsonObjectSignal = jsonArraySignals.getJSONObject(i);
				JSONArray jsonArrayPTRanks = jsonObjectSignal.getJSONArray("symBackgroundPercentiles");
				JSONArray jsonArrayPTProbs = jsonObjectSignal.getJSONArray("symCoOccurrenceProbabilities");
				if (jsonArrayPTRanks.length() != jsonArrayPTProbs.length()) {
					throw new JSONException("<html><p style=\"width:300px;\">A signal has mismatched number of background rank and co-occurrence probability entries.</p></html>");
				}
				
				List<Double> ptRanks = new ArrayList<Double>(jsonArrayPTRanks.length());
				List<Double> ptProbs = new ArrayList<Double>(jsonArrayPTProbs.length());
				for (int j = 0; j < jsonArrayPTRanks.length(); j++) {
					ptRanks.add(jsonArrayPTRanks.getDouble(j));
					ptProbs.add(jsonArrayPTProbs.getDouble(j));
				}
				
				AssociatedSymptoms associatedSymptoms;
				
				// Check for optional JSON entries produced by a SymptomsBySymptom object.
				String vaxName = jsonObjectSignal.optString("inputVaccineName", "");
				String dataSource = this.dataSource;
				JSONArray jsonArrayPTNames = jsonObjectSignal.optJSONArray("inputSymNames");
				if (vaxName != null && !vaxName.equals("" ) && dataSource != null && !dataSource.equals("") && jsonArrayPTNames != null) {
					List<String> ptNames = new ArrayList<String>(jsonArrayPTRanks.length());
					for (int k = 0; k < jsonArrayPTNames.length(); k++) {
						ptNames.add(jsonArrayPTNames.getString(k));
					}
					if (ptNames.size() != ptRanks.size() || ptNames.size() != ptProbs.size()) {
						throw new JSONException("<html><p style=\"width:300px;\">A signal has mismatched number of input symptom names to background ranks and co-occurrence probabilities.</p></html>");
					}
					
					associatedSymptoms = new SymptomsBySymptom(ptRanks, ptProbs, vaxName, ptNames, dataSource);
					
				}
				else {
					associatedSymptoms = new SymptomsByRank(ptRanks, ptProbs);
				}
				
				signals.add(new SimulatedSignal(jsonObjectSignal.getString("signalName"),
												jsonObjectSignal.getInt("vaccineEntryReport"),
												jsonObjectSignal.getInt("vaccineWeight"),
												associatedSymptoms));
			}
		}
		
		// Gather all the values from the JSON object
		groupSize = jsonObject.getInt("groupSize");
		numInputReports = jsonObject.getInt("numInputReports");
		numReportsToSimulate = jsonObject.getInt("numReportsToSimulate");
		reportsPerInterval = jsonObject.getInt("reportsPerInterval");
		numStartSym = jsonObject.getInt("numStartSym");
		numStartVax = jsonObject.getInt("numStartVax");
		randomSeed = jsonObject.getLong("randomSeed");
		simulateWithSignal = jsonObject.getBoolean("simulateWithSignal");
		numSimulations = jsonObject.getInt("numSimulations");
		outputMatrix = jsonObject.getBoolean("outputMatrix");
		outputPanacea = jsonObject.getBoolean("outputPanacea");
		outputEdgelist = jsonObject.getBoolean("outputEdgelist");
		outputFilePath = jsonObject.getString("outputFilePath");
		filenamePrefix = jsonObject.getString("filenamePrefix");
	}
	
	@Override
	public int getGroupSize() {
		return groupSize;
	}

	@Override
	public int getNumInputReports() {
		return numInputReports;
	}

	@Override
	public String getDataSource() {
		return dataSource;
	}

	@Override
	public List<Double> getSymPerReportDist() {
		return symPerReportDistribution;
	}

	@Override
	public List<Double> getVaxPerReportDist() {
		return vaxPerReportDistribution;
	}

	@Override
	public List<Double> getNewSymDist() {
		return probabilityNewSymPerGroup;
	}

	@Override
	public List<Double> getNewVaxDist() {
		return probabilityNewVaxPerGroup;
	}

	@Override
	public Function getNewSymProbDensity(int reportsPerInterval) {
		return new PiecewiseFunction(probabilityNewSymPerGroup, reportsPerInterval);
	}

	@Override
	public Function getNewVaxProbDensity(int reportsPerInterval) {
		return new PiecewiseFunction(probabilityNewVaxPerGroup, reportsPerInterval);
	}

	
	
	public int getNumReportsToSimulate() {
		return numReportsToSimulate;
	}
	
	public int getReportsPerInterval() {
		return reportsPerInterval;
	}
	
	public int getNumStartSym() {
		return numStartSym;
	}
	
	public int getNumStartVax() {
		return numStartVax;
	}
	
	public long getRandomSeed() {
		return randomSeed;
	}
	public boolean getSimulateWithSignal() {
		return simulateWithSignal;
	}
	
	public List<SimulatedSignal> getStoredSignals() {
		if (signals != null && signals.size() > 0) {
			return signals;
		}
		else {
			return new ArrayList<SimulatedSignal>();
		}
	}
	
	public int getNumSimulations() {
		return numSimulations;
	}
	
	public boolean getOutputMatrix() {
		return outputMatrix;
	}
	
	public boolean getOutputPanacea() {
		return outputPanacea;
	}
	
	public boolean getOutputEdgelist() {
		return outputEdgelist;
	}
	
	public String getOutputFilePath() {
		return outputFilePath;
	}
	
	public String getFilenamePrefix() {
		return filenamePrefix;
	}
}
