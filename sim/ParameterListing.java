package com.eng.cber.na.sim;

import java.util.List;

import com.eng.cber.na.sim.rstruct.Function;

/**
 * An interface for obtaining input parameters.  These will
 * all be used when creating a new simulation.
 */

public interface ParameterListing {

	public int getGroupSize();
	public int getNumInputReports();
	public String getDataSource();
	public List<Double> getSymPerReportDist();
	public List<Double> getVaxPerReportDist();
	public List<Double> getNewSymDist();
	public List<Double> getNewVaxDist();
	public Function getNewSymProbDensity(int reportsPerInterval);
	public Function getNewVaxProbDensity(int reportsPerInterval);

}
