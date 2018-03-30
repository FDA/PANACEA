package com.eng.cber.na.sim.gui.signal;

import java.util.List;

/** Interface for a group of PTs that are associated
 * with a vaccine.  This is used as part of the
 * definition of a signal.
 */
public interface AssociatedSymptoms {

	public boolean hasSymptoms();
	public List<Double> getPTRanks();
	public List<Double> getPTProbs();
	
}
