package com.eng.cber.na.subgraph;

import java.text.DecimalFormat;

import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.vaers.VAERS_Predicates;

/**
 * This class produces a network (and knows how to
 * add that network to the UI, through the run()
 * method of the parent AbstractCreateGraph) that 
 * contains only nodes whose raw degree
 * falls outside the given range values.
 *
 */
public class CreateRawDegreeExcludedRangeSubgraph extends AbstractCreateNodeSubgraph {	
	Double rangeStart, rangeEnd;
	
	public CreateRawDegreeExcludedRangeSubgraph(Double rangeStart, Double rangeEnd) {
		super(-2);
		this.rangeStart = rangeStart;
		this.rangeEnd = rangeEnd;
	}
	
	@Override
	protected GeneralGraph getNetwork() throws IllegalArgumentException, InstantiationException, IllegalAccessException {		
		super.ensureRangeStartAndEnd(rangeStart, rangeEnd, false);
		VAERS_Predicates.RawDegreeExcludedRangePredicate pred = new VAERS_Predicates.RawDegreeExcludedRangePredicate(super.parent, rangeStart, rangeEnd);

		GeneralGraph subgraph = super.makeSubgraph(pred);
		return subgraph;
	}
	
	@Override
	protected String getName() {
		DecimalFormat df = new DecimalFormat("#.#");
		return ("degree excluding range ["+df.format(rangeStart)+","+df.format(rangeEnd)+"] subnetwork");
	}
}