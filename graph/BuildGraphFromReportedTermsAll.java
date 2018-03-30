package com.eng.cber.na.graph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.eng.cber.na.vaers.VAERS_Node;

/**
 * This class builds a graph from all reports known that
 * also have ALL of the terms that the constructing 
 * class specifies.
 *
 */
public class BuildGraphFromReportedTermsAll extends AbstractCreateGraph {	
	Collection<GeneralNode> terms;
	
	public BuildGraphFromReportedTermsAll(Collection<GeneralNode> terms) {
		super(-1);
		this.terms = terms;
	}
	
	@Override
	protected GeneralGraph getNetwork() throws IllegalArgumentException, InstantiationException, IllegalAccessException {		
		if (terms == null) {
			throw new IllegalArgumentException("No nodes given for union with AND");
		}
		
		Set<Object> reportIDs = new HashSet<Object>();
		Iterator<GeneralNode> it = terms.iterator();
		if (terms.size() > 0) {
			reportIDs.addAll(((VAERS_Node)it.next()).getReports());
			while (it.hasNext()) {
				Set<?> addlReports = ((VAERS_Node)it.next()).getReports();
				reportIDs.retainAll(addlReports);
			}
		}
		
		BuildGraphFromReports bgfr = new BuildGraphFromReports(reportIDs);
		GeneralGraph network = bgfr.buildNetwork();

		return network;
	}
	
	@Override
	protected String getName() {
		return ("ALL of " + getSorted(terms));
	}
}