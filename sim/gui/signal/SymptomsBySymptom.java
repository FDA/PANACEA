package com.eng.cber.na.sim.gui.signal;

import java.util.List;

/** A listing of symptoms associated with a particular
 * vaccine.  Used as part of the definition for a
 * signal.  The ranks are the background occurrence
 * rates for the PTs (used to select which of the
 * currently existing PTs to associate with this
 * signal vaccine) and the probs are the probabilities
 * of the PT co-occurring in a report with the signal
 * vaccine.<br/><br/>
 * 
 * This object is built when specifying a vaccine and 
 * some PTs from an existing data set.  For a symptoms
 * list that is entered directly, see SymptomsByRank.
 */
public class SymptomsBySymptom implements AssociatedSymptoms {

	private List<Double> ranks;
	private List<Double> probs;
	private String vaxName, dataSource;
	private List<String> selectedSymptoms;
	
	public SymptomsBySymptom(List<Double> ranks, List<Double> probs, String vaxName, List<String> selectedSymptoms, String dataSource) {
		this.ranks = ranks;
		this.probs = probs;
		this.vaxName = vaxName;
		this.selectedSymptoms = selectedSymptoms;
		this.dataSource = dataSource;
	}
	
	public String getVaxName() {
		return vaxName;
	}
	
	public List<String> getSymptoms() {
		return selectedSymptoms;
	}
	
	public String getDataSource() {
		return dataSource;
	}
	
	@Override
	public boolean hasSymptoms() {
		return !(ranks.isEmpty() || probs.isEmpty() || selectedSymptoms.isEmpty());
	}
	
	@Override
	public List<Double> getPTRanks() {
		return ranks;
	}
	
	@Override
	public List<Double> getPTProbs() {
		return probs;
	}
}
