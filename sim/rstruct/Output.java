package com.eng.cber.na.sim.rstruct;

import java.util.List;

/** 
 * The Output interface defines a simulation output container.
 * 
 **/
public interface Output extends WriteableObject {
	public void addOutputTuple(OutputTuple t);
	public List<OutputTuple> getOutputTuples();
	public void clear();
}
