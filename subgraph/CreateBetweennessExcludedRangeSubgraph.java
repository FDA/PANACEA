package com.eng.cber.na.subgraph;

import java.text.DecimalFormat;

import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.vaers.VAERS_Predicates;

/**
 * This class produces a network (and knows how to
 * add that network to the UI, through the run()
 * method of the parent AbstractCreateGraph) that 
 * contains only nodes whose normalized betweenness
 * fall outside the given range values.
 *
 */
public class CreateBetweennessExcludedRangeSubgraph extends AbstractCreateNodeSubgraph {	
	Double rangeStart, rangeEnd;
	
	public CreateBetweennessExcludedRangeSubgraph(Double rangeStart, Double rangeEnd) {
		super(-2);
		this.rangeStart = rangeStart;
		this.rangeEnd = rangeEnd;
	}
	
	@Override
	protected GeneralGraph getNetwork() throws IllegalArgumentException, InstantiationException, IllegalAccessException {		
		super.ensureRangeStartAndEnd(rangeStart, rangeEnd, true);
		VAERS_Predicates.BetweennessExcludedRangePredicate pred = new VAERS_Predicates.BetweennessExcludedRangePredicate(super.parent, rangeStart, rangeEnd);

		GeneralGraph subgraph = super.makeSubgraph(pred);
		return subgraph;
	}
	
	@Override
	protected String getName() {
		DecimalFormat df = new DecimalFormat("#.###");
		return ("betweenness excluding range ["+df.format(rangeStart)+","+df.format(rangeEnd)+"] subnetwork");
	}
}