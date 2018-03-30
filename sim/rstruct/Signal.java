package com.eng.cber.na.sim.rstruct;

import java.util.List;

import com.eng.cber.na.sim.gui.signal.AssociatedSymptoms;

/**
 * Defines a collection of methods that constitute a signal.
 *  
 */

public interface Signal extends WriteableObject {

	public static String[] labels = {"Signal Name","Vaccine Name","Vaccine Entry Report","Vaccine Weight","Vaccine Index in Matrix","Associated PT Names","Associated PT Background Percentiles","Associated PT Co-Occurrence Probabilities","Associated PT Matrix Indices"};		

	public String getSignalName();
	public int getVXEntry();
	public int getVXWeight();
	public int getVXIndex();
	public void setVXIndex(int index);
	public String getOutputVaxName();
	public void setOutputVaxName(String name);
	public List<Double> getPTRanks();
	public List<Double> getPTProbs();
	public AssociatedSymptoms getAssociatedSymptoms();
	public List<Integer> getPTSyndrome();
	public void resetPTSyndrome();
	public List<String> getPTSyndromeNames();
	public String getDescription();
	public void setPTSyndromeNames(List<String> pt_syndrome_names);
}
