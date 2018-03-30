package com.eng.cber.na.subgraph;

import java.text.DecimalFormat;

import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.vaers.VAERS_Edge;
import com.eng.cber.na.vaers.VAERS_Predicates;

/**
 * This class produces a network (and knows how to
 * add that network to the UI, through the run()
 * method of the parent AbstractCreateGraph) that 
 * filters only the edges of the specified type
 * according to their value -- any edge that is of
 * the type specified and falls outside the excluded
 * range is included in the produced network, as 
 * are all edges that are not of that specified 
 * type.
 * 
 * See CreateEdgeWeightSubgraph for an alternative 
 * that does not specify edge type.
 *
 */
public class CreateTypedEdgeWeightExcludedRangeSubgraph extends AbstractCreateEdgeSubgraph {	
	int rangeStart, rangeEnd;
	VAERS_Edge.EdgeType type;
	
	public CreateTypedEdgeWeightExcludedRangeSubgraph(int rangeStart, int rangeEnd, VAERS_Edge.EdgeType type) {
		super(-2);
		this.rangeStart = rangeStart;
		this.rangeEnd = rangeEnd;
		this.type = type;
	}
	
	@Override
	protected GeneralGraph getNetwork() throws IllegalArgumentException, InstantiationException, IllegalAccessException {		
		super.ensureRangeStartAndEnd(rangeStart, rangeEnd, false);
		VAERS_Predicates.TypedEdgeWeightExcludedRangePredicate pred = new VAERS_Predicates.TypedEdgeWeightExcludedRangePredicate(rangeStart, rangeEnd, type);
		GeneralGraph subgraph = super.makeSubgraph(pred);
		return subgraph;
	}
	
	@Override
	protected String getName() {
		DecimalFormat df = new DecimalFormat("#");
		return("edge weight " + type + " excluding range (" + df.format(rangeStart) + ", " + df.format(rangeEnd) + ") subnetwork");
	}
}