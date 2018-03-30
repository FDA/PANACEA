package com.eng.cber.na.graph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.eng.cber.na.vaers.VAERS_Node;

/**
 * This class builds a graph from all reports known that
 * also have ANY of the terms that the constructing 
 * class specifies.
 *
 */
public class BuildGraphFromReportedTermsAny extends AbstractCreateGraph {	
	Collection<GeneralNode> terms;
	
	public BuildGraphFromReportedTermsAny(Collection<GeneralNode> terms) {
		super(-1);
		this.terms = terms;
	}
	
	@Override
	protected FDAGraph getNetwork() throws IllegalArgumentException, InstantiationException, IllegalAccessException {		
		if (terms == null) {
			throw new IllegalArgumentException("No nodes given for union with ANY");
		}
		
		Set<Object> reportIDs = new HashSet<Object>();
		Iterator<GeneralNode> it = terms.iterator();
		while (it.hasNext()) {
			Set<?> addlReports = ((VAERS_Node)it.next()).getReports();
			reportIDs.addAll(addlReports);
		}

		BuildGraphFromReports bgfr = new BuildGraphFromReports(reportIDs);
		FDAGraph network = bgfr.buildNetwork();
		

		return network;
	}
	
	@Override
	protected String getName() {
		return("ANY of " + getSorted(terms));
	}
}