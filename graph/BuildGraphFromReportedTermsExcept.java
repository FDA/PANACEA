package com.eng.cber.na.graph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.vaers.VAERS_Node;

/**
 * This class builds a graph from all reports known that
 * do NOT have ANY of the terms that the constructing 
 * class specifies.
 *
 */
public class BuildGraphFromReportedTermsExcept extends AbstractCreateGraph {	
	Collection<GeneralNode> terms;
	
	public BuildGraphFromReportedTermsExcept(Collection<GeneralNode> terms) {
		super(-1);
		this.terms = terms;
	}
	
	@Override
	protected FDAGraph getNetwork() throws IllegalArgumentException, InstantiationException, IllegalAccessException {		
		if (terms == null) {
			throw new IllegalArgumentException("No nodes given for intersection with EXCEPT");
		}
		
		Set<Object> reportIDs = new HashSet<Object>();
		Iterator<GeneralNode> it = terms.iterator();
		if (terms.size() > 0) {
			reportIDs.addAll(NetworkAnalysisVisualization.getInstance().getUnderlyingData().getReportHash().keySet());
			while (it.hasNext()) {
				Set<?> removableReports = ((VAERS_Node)it.next()).getReports();
				reportIDs.removeAll(removableReports);
			}
		}
		
		BuildGraphFromReports bgfr = new BuildGraphFromReports(reportIDs);
		FDAGraph network = bgfr.buildNetwork();

		return network;
	}
	
	@Override
	protected String getName() {
		return ("EXCEPT " + getSorted(terms));
	}
}