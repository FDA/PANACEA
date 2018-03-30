package com.eng.cber.na.graph;

import java.util.Set;

import com.eng.cber.na.NetworkAnalysisVisualization;

/**
 * This class builds a graph from all reports that are known.
 * 
 * This is the default method of creating a graph: connect
 * all terms within all reports with a relationship
 * weight equal to the number of times two terms were
 * reported.
 *
 */
public class BuildGraphFromAllReports extends AbstractCreateGraph {	

	public BuildGraphFromAllReports() {
		super(-1);
	}
	
	@Override
	protected FDAGraph getNetwork() throws IllegalArgumentException, InstantiationException, IllegalAccessException {
		Set<?> allReports = NetworkAnalysisVisualization.getInstance().getUnderlyingData().getReportHash().keySet();
		BuildGraphFromReports bgfr = new BuildGraphFromReports(allReports);
		FDAGraph fullNetwork = bgfr.buildNetwork();
		return fullNetwork;
	}
	
	@Override
	protected String getName() {
		return 	("All Reports");	
	}
}