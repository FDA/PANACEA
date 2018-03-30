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
 * This object is built when specifying the ranks and
 * probs explicitly.  For a symptoms list based on
 * real data, see SymptomsBySymptom.
 */
public class SymptomsByRank implements AssociatedSymptoms {

	private List<Double> ranks;
	private List<Double> probs;
	
	public SymptomsByRank(List<Double> ranks, List<Double> probs) {
		this.ranks = ranks;
		this.probs = probs;
	}
	
	@Override
	public boolean hasSymptoms() {
		return !(ranks.isEmpty() || probs.isEmpty());
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
