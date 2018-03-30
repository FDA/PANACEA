package com.eng.cber.na.sim;

import java.awt.Cursor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.SwingWorker;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.graph.BusinessObjectsGraphLoader;
import com.eng.cber.na.sim.rstruct.Function;
import com.eng.cber.na.sim.rstruct.PiecewiseFunction;
import com.eng.cber.na.vaers.VAERS_Node;


/**
 * Reads the current network in the network visualization and
 * extracts parameters that can be used as input into a 
 * simulation.  Specifically, gets the distributions for the 
 * number of vaccines/PTs in a single report and gets the
 * probability of a previously unseen vaccine/PT appearing
 * in a report at each step.  Step size (groupSize) can be
 * set.
 */
public class ParameterBuilderPanacea extends SwingWorker<Object,Object> implements ParameterListing {

	private int numInputReports;
	private String dataSource;
	
	// The set of names of all symptoms and vaccines.
	private Set<String> symNameSet = new TreeSet<String>();
	private Set<String> vaxNameSet = new TreeSet<String>(); 
	
	// The percentage of reports having n symptoms/vaccines.
	private Map<Integer,Double> percentHavingNumSymptoms = new TreeMap<Integer,Double>();
	private Map<Integer,Double> percentHavingNumVaccines = new TreeMap<Integer,Double>();
	
	// The number of reports to put into a block (step function).
	int groupSize;
	
	// Percentage of symptoms/vaccines that are new at each step of the step function.  Uses groupSize variable.
	private Map<Integer,Double> percentNewSymPerStep = new TreeMap<Integer,Double>();
	private Map<Integer,Double> percentNewVaxPerStep = new TreeMap<Integer,Double>();
	
	// The signal vaccine
	private String signalVaxName;
	// The background percentile.  (The ranks of symptoms from non-signal reports)
	private Map<String,Double> rankOfSymInNonSignalReports = new TreeMap<String,Double>();
	// The co-occurence probabilities.  (The percent of signal-containing reports that include each symptom.)
	private Map<String,Double> percentOfSignalReportsWithSym = new TreeMap<String,Double>();

	/**
	 * Optional parameter to set the minimum percentage of reports that should contain the
	 * signal vaccine, so that we make a separation between background (i.e. non-signal) reports
	 * and signal reports.
	 * If the percent of reports containing the signal vaccine is above this threshold,
	 * then there are no "background" reports, and all occurrences of a symptom (in both
	 * signal and non-signal reports) will be totaled to determine how common the symptom is.
	 * At 0.0, always treat all reports as background reports, even if they contain the signal Vax.
	 */ 
	private double thresholdPercent = 0.0;
	
	public ParameterBuilderPanacea(String coOccurVax) {
		this(coOccurVax, 200);
	}
	
	public ParameterBuilderPanacea(String signalVaxName, int groupSize) {
		this.signalVaxName = signalVaxName;
		this.groupSize = groupSize;
	}
	
	public String getSignalVaxName() {
		return signalVaxName;
	}
	
	public int getGroupSize() {
		return groupSize;
	}
	
	public int getNumInputReports() {
		return numInputReports;
	}
	
	public String getDataSource() {
		return dataSource;
	}
	
	public Set<String> getSymSet() {
		return symNameSet;
	}
	
	public Set<String> getVaxSet() {
		return vaxNameSet;
	}
	
	/** Returns only symptoms that have co-occurred with the signal vaccine **/
	public Set<String> getFilteredSymSet() {
		return percentOfSignalReportsWithSym.keySet();
	}

	public List<Double> getSymPerReportDist() {
		Map<Integer,Double> tmp = new TreeMap<Integer,Double>(percentHavingNumSymptoms);
		if(tmp.containsKey(0)) {
			tmp.remove(0);
		}
		List<Double> ret = new ArrayList<Double>(tmp.values());
		return ret;
	}
	
	public List<Double> getVaxPerReportDist() {
		Map<Integer,Double> tmp = new TreeMap<Integer,Double>(percentHavingNumVaccines);
		if(tmp.containsKey(0)) {
			tmp.remove(0);
		}
		List<Double> ret = new ArrayList<Double>(tmp.values());
		return ret;
	}
	
	public List<Double> getNewSymDist() {
		List<Double> ret = new ArrayList<Double>(percentNewSymPerStep.values());
		return ret;
	}
	
	public List<Double> getNewVaxDist() {
		List<Double> ret = new ArrayList<Double>(percentNewVaxPerStep.values());
		return ret;
	}
	
	public List<Double> getSignalRanks(List<String> syms) {
		List<Double> ret = new ArrayList<Double>(syms.size());
		for(String sym : syms) {
			if(rankOfSymInNonSignalReports.containsKey(sym)) {
				ret.add(rankOfSymInNonSignalReports.get(sym));
			}
			else if(percentOfSignalReportsWithSym.containsKey(sym)) {
				// if vaccine prevalence is below the threshold percentage of reports, give it a percentile of rank 1 
				ret.add(.01);
			}
			
		}
		return ret;
	}
	
	public List<Double> getSignalProbs(List<String> syms) {
		List<Double> ret = new ArrayList<Double>(syms.size());
		for(String sym : syms) {
			if(percentOfSignalReportsWithSym.containsKey(sym)) {
				ret.add(percentOfSignalReportsWithSym.get(sym));
			}
			else {
				System.err.println(sym + " not found in signal probs.");
				System.exit(1);
			}
		}
		return ret;
	}
	
	public Function getNewSymProbDensity(int reportsPerInterval) {
		return new PiecewiseFunction(new ArrayList<Double>(percentNewSymPerStep.values()), reportsPerInterval);
	}
	
	public Function getNewVaxProbDensity(int reportsPerInterval) {
		return new PiecewiseFunction(new ArrayList<Double>(percentNewVaxPerStep.values()), reportsPerInterval);
	}
	
	
	
	@Override
	protected Object doInBackground() {
		try {
			createParameters();
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void createParameters() {
		
		try {
			NetworkSimulatorGUI.getInstance().setCursor(new Cursor(Cursor.WAIT_CURSOR));

			NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
			symNameSet.clear();
			vaxNameSet.clear();
			percentHavingNumSymptoms.clear();
			percentHavingNumVaccines.clear();
			rankOfSymInNonSignalReports.clear();
			percentOfSignalReportsWithSym.clear();
			numInputReports = 0;
			
			setProgress(0);
	
			// Mappings for report ID to number of symptoms/vaccines in that report.
			Map<Object,Integer> numSymptomsPerReport = new TreeMap<Object,Integer>();
			Map<Object,Integer> numVaccinesPerReport = new TreeMap<Object,Integer>();
			
			// Histogram for the number of times that n syms/vax show up in a report.
			Map<Integer,Integer> frequencyOfNumberOfSymptoms = new TreeMap<Integer,Integer>();
			Map<Integer,Integer> frequencyOfNumberOfVaccines = new TreeMap<Integer,Integer>();
			
			// Number of new symptoms/vaccine per report
			Map<Object,Integer> newSymptomsPerReport = new HashMap<Object,Integer>();
			Map<Object,Integer> newVaccinesPerReport = new HashMap<Object,Integer>();
			
			// Holds the report IDs of reports that contain the signal vaccine
			List<Object> reportIDsWithSignalVax = new LinkedList<Object>();
			
			// The number of occurrences of each symptom in non-signal reports.
			Map<String,Integer> numOccurrencesOfSymInNonSignalReports = new TreeMap<String,Integer>();
			
			String sourceFileString = "";
			if (nv.getUnderlyingData() != null) {
				if(nv.getUnderlyingData().getPtFilePath() != null ){
					sourceFileString = "\n" +
									   "PT Data File: " + nv.getUnderlyingData().getPtFilePath() + "\n" +
									   "VAX Data File: " + nv.getUnderlyingData().getVaxFilePath();
				}
				else if (nv.getUnderlyingData() instanceof BusinessObjectsGraphLoader) {
					sourceFileString = "\nData File: " + ((BusinessObjectsGraphLoader) nv.getUnderlyingData()).getBOFilePath();
				}

			if (nv.getGraphReader() != null )
				sourceFileString = "\nData File: " + nv.getGraphReader().fileName;
			}
			dataSource = "PANACEA - " + nv.getGraph().getName() + sourceFileString;
			
			/*
			 * Gets the reports that are being used in the current graph. 
			 * All vaccines and PTs in each report will be used.
			 */
			
			// Get the map of all reports to set of nodes in that report.
			Map<Object, Set<VAERS_Node>> reportIDToNodesMap = nv.getUnderlyingData().getOrigReportHash();
			// Get the set of reports in the current graph only.
			Set<Object> reportSet;
			if (nv.getGraph().isDual()) {
				reportSet = nv.getGraph().getNodeObjects();
			}
			else {
				reportSet = nv.getGraph().getReports();
			}
			// Get the sorted list of reports.
			List<Object> reportList = new ArrayList<Object>(reportSet);
			Collections.sort(reportList, new Comparator<Object>() {
				@Override
				public int compare(Object o1, Object o2) {
					if (o1 instanceof String && o2 instanceof String)
						return ((String) o1).compareTo((String) o2);
					else
						return ((Integer) o1).compareTo((Integer) o2);
				}
			});
							
			// Loop over all the reports in the current graph
			for (Object report : reportList) {
				
				int numSymInReport = 0;
				int numVaxInReport = 0;
				int numNewSymInReport = 0;
				int numNewVaxInReport = 0;
				boolean reportContainsSignalVax = false;
				
				Set<VAERS_Node> nodeSet = reportIDToNodesMap.get(report);
				// Loop over all the nodes mentioned in the report
				for (VAERS_Node node : nodeSet) {
					
					// Check if the node is a symptom or vaccine.
					if (node.getNodeType() == VAERS_Node.NodeType.SYM) {
						numSymInReport++;
						// Check if the name has appeared before
						if (!(symNameSet.contains(node.getID()))) {
							numNewSymInReport++;
						}
						symNameSet.add(node.getID());
	
					}
					else if (node.getNodeType() == VAERS_Node.NodeType.VAX) {
						numVaxInReport++;
						if (!(vaxNameSet.contains(node.getID()))) {
							numNewVaxInReport++;
						}
						vaxNameSet.add(node.getID());
						
						// Check if the vaccine is the signal vaccine
						if (node.getID().equals(signalVaxName)) {
							reportIDsWithSignalVax.add(report);
							reportContainsSignalVax = true;
						}
					}
					
				}
				
				// Add the number of symptoms in this report into the histogram
				if(!frequencyOfNumberOfSymptoms.containsKey(numSymInReport)) {
					frequencyOfNumberOfSymptoms.put(numSymInReport, 1);
				}
				else {
					frequencyOfNumberOfSymptoms.put(numSymInReport, frequencyOfNumberOfSymptoms.get(numSymInReport) + 1);
				}
				
				// Add the number of vaccines in this report into the histogram
				if(!frequencyOfNumberOfVaccines.containsKey(numVaxInReport)) {
					frequencyOfNumberOfVaccines.put(numVaxInReport, 1);
				}
				else {
					frequencyOfNumberOfVaccines.put(numVaxInReport, frequencyOfNumberOfVaccines.get(numVaxInReport) + 1);
				}
				
				// Add the number of symptoms/vaccines in this report into the mappings
				numSymptomsPerReport.put(report, numSymInReport);
				numVaccinesPerReport.put(report, numVaxInReport);
				
				// Add the number of new symptoms/vaccines in this report into the mappings
				newSymptomsPerReport.put(report, numNewSymInReport);
				newVaccinesPerReport.put(report, numNewVaxInReport);
				
				// Count occurrences of each symptom in signal and non-signal reports
				// First check if this report contained the signal vaccine.
				if (reportContainsSignalVax) {
					// Count each symptom in the signal-containing report by name.
					// Will normalize to percentages later.
					for (VAERS_Node node : nodeSet) {
						if (node.getNodeType() == VAERS_Node.NodeType.SYM) {
							if(!percentOfSignalReportsWithSym.containsKey(node.getID())) {
								percentOfSignalReportsWithSym.put(node.getID(), 1.0);
							}
							else {
								percentOfSignalReportsWithSym.put(node.getID(), percentOfSignalReportsWithSym.get(node.getID()) + 1.0);
							}
						}
					}
				}
				else {
					// Count each symptom in the non-signal report by name.
					for (VAERS_Node node : nodeSet) {
						if (node.getNodeType() == VAERS_Node.NodeType.SYM) {
							if(!numOccurrencesOfSymInNonSignalReports.containsKey(node.getID())) {
								numOccurrencesOfSymInNonSignalReports.put(node.getID(), 1);
							}
							else {
								numOccurrencesOfSymInNonSignalReports.put(node.getID(), numOccurrencesOfSymInNonSignalReports.get(node.getID()) + 1);
							}
						}
					}
				}
			
				setProgress((int) ((double) reportList.indexOf(report) / reportList.size() * 75));
				numInputReports++;
			}
			
			setProgress(75);
			
			// Find the percentage of reports having n symptoms
			for(Integer i : frequencyOfNumberOfSymptoms.keySet()) {
				percentHavingNumSymptoms.put(i,(double)frequencyOfNumberOfSymptoms.get(i)/reportSet.size());
			}
			
			// Find the percentage of reports having n vaccines
			for(Integer i : frequencyOfNumberOfVaccines.keySet()) {
				percentHavingNumVaccines.put(i,(double)frequencyOfNumberOfVaccines.get(i)/reportSet.size());
			}
		
			
			// Calculate the percentage of all total symptoms that were new in
			// each block of groupSize reports.
			int i = 1;
			int numInBucket = 0, bucketIndex = 0;
			double sum = 0;
			for(Object v : numSymptomsPerReport.keySet()) {
				if(i % groupSize == 0 || numSymptomsPerReport.size() == i) {
					percentNewSymPerStep.put(bucketIndex, sum / numInBucket);
					sum = 0;
					numInBucket = 0;
					bucketIndex++;
				}
				if(newSymptomsPerReport.containsKey(v)) {
					if (numSymptomsPerReport.get(v) == 0) {
						sum += 0;
					}
					else {
						sum += (double)newSymptomsPerReport.get(v) / numSymptomsPerReport.get(v);
					}
				}
				i++;
				numInBucket++;
			}
			
			// Calculate the percentage of all total vaccines that were new in
			// each block of groupSize reports.
			i = 1;
			numInBucket = 0;
			bucketIndex = 0;
			sum = 0;
			for(Object v : numVaccinesPerReport.keySet()) {
				if(i % groupSize == 0 || numVaccinesPerReport.size() == i) {
					percentNewVaxPerStep.put(bucketIndex, sum / numInBucket);
					sum = 0;
					numInBucket = 0;
					bucketIndex++;
				}
				if(newVaccinesPerReport.containsKey(v)) {
					if (numVaccinesPerReport.get(v) == 0) {
						sum += 0;
					}
					else {
						sum += (double)newVaccinesPerReport.get(v) / numVaccinesPerReport.get(v);
					}
				}
				i++;
				numInBucket++;
			}
			
			setProgress(85);
			
			// If the signal vaccine dominates the data set (i.e. is present in more
			// than the threshold percentage of reports), then combine the signal and
			// non-signal report count for each symptom.  There is not enough "background"
			// to establish the percentile ranks of symptoms, so we use the entire data set.
			if (reportIDsWithSignalVax.size() > thresholdPercent * numInputReports) {
				// "percentOfSignalReportsWithSym" starts out as the total count of a symptom in signal-containing reports.  It will be converted to a percentage after this.
				for (String symName : percentOfSignalReportsWithSym.keySet()) {
					if (numOccurrencesOfSymInNonSignalReports.containsKey(symName)) {
						numOccurrencesOfSymInNonSignalReports.put(symName, Integer.valueOf((int)Math.round(numOccurrencesOfSymInNonSignalReports.get(symName) + percentOfSignalReportsWithSym.get(symName))));
					}
					else {
						numOccurrencesOfSymInNonSignalReports.put(symName, Integer.valueOf((int)Math.round(percentOfSignalReportsWithSym.get(symName))));
					}
				}
			}
			
			// Rank symptom occurrences using groups=100 (reimplementation of SAS function)
			Map<String,Double> ranked = Util.rank(numOccurrencesOfSymInNonSignalReports,100);
			for(String s : ranked.keySet()) {
				rankOfSymInNonSignalReports.put(s, ranked.get(s) / 100);
			}
			
			// Normalize by total number of signal-containing reports
			for(String s : percentOfSignalReportsWithSym.keySet()) {
				percentOfSignalReportsWithSym.put(s, percentOfSignalReportsWithSym.get(s) / reportIDsWithSignalVax.size());
			}
			setProgress(99);
			
		}
		finally {
			NetworkSimulatorGUI.getInstance().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}
	
}
