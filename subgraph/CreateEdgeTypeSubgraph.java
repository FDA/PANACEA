package com.eng.cber.na.subgraph;

import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.vaers.VAERS_Edge.EdgeType;
import com.eng.cber.na.vaers.VAERS_Predicates;

/**
 * This class produces a network (and knows how to
 * add that network to the UI, through the run()
 * method of the parent AbstractCreateGraph) that 
 * contains only edges of a single type.  This is
 * the method by which a user can produce a one-mode 
 * PT-PT graph, a one-mode VAX-VAX graph, or a
 * two-mode (bipartite) VAX-PT graph.
 *
 */
public class CreateEdgeTypeSubgraph extends AbstractCreateEdgeSubgraph {
	EdgeType type;
	
	public CreateEdgeTypeSubgraph(EdgeType type) {
		super(-2);
		this.type = type;
	}

	@Override
	protected GeneralGraph getNetwork() throws IllegalArgumentException, InstantiationException,IllegalAccessException {
		VAERS_Predicates.EdgeTypePredicate pred = new VAERS_Predicates.EdgeTypePredicate(type);
		GeneralGraph subgraph = super.makeSubgraph(pred);
		
		return subgraph;
	}
	
	@Override
	protected String getName() {
		return type.toString() + " only";
	}
}
