package com.eng.cber.na.subgraph;

import java.text.DecimalFormat;

import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.vaers.VAERS_Predicates;


/**
 * This class produces a network (and knows how to
 * add that network to the UI, through the run()
 * method of the parent AbstractCreateGraph) that 
 * contains only the edges that have weights outside
 * a range specified by the user.
 * 
 * See CreateTypedEdgeWeightExcludedRangeSubgraph for
 * an alternative that allows the user to specify edge type.
 *
 */
public class CreateEdgeWeightExcludedRangeSubgraph extends AbstractCreateEdgeSubgraph {	
	int rangeStart, rangeEnd;
	
	public CreateEdgeWeightExcludedRangeSubgraph(int rangeStart, int rangeEnd) {
		super(-2);
		this.rangeStart = rangeStart;
		this.rangeEnd = rangeEnd;
	}
	
	@Override
	protected GeneralGraph getNetwork() throws IllegalArgumentException, InstantiationException, IllegalAccessException {		
		super.ensureRangeStartAndEnd(rangeStart, rangeEnd, false);
		VAERS_Predicates.EdgeWeightExcludedRangePredicate pred = new VAERS_Predicates.EdgeWeightExcludedRangePredicate(rangeStart, rangeEnd);
		GeneralGraph subgraph = super.makeSubgraph(pred);
		return subgraph;
	}
	
	@Override
	protected String getName() {
		DecimalFormat df = new DecimalFormat("#");
		return ("edge weight excluding range (" + df.format(rangeStart) + "," + df.format(rangeEnd) + ") subnetwork");
	}
}
