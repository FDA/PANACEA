package com.eng.cber.na.subgraph;

import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.vaers.VAERS_Predicates;

/**
 * This class produces a network (and knows how to
 * add that network to the UI, through the run()
 * method of the parent AbstractCreateGraph) that 
 * removes the outermost layer of pendant nodes or edges.
 * The resulting network may still contain pendants;
 * the graph creation method is not recursive and
 * therefore if there was a long chain of pendants,
 * only the last one will be removed in the output
 * network.
 *
 */
public class CreateNonPendantSubgraph extends AbstractCreateNodeSubgraph {	
	
	public CreateNonPendantSubgraph() {
		super(-2);
	}
	
	@Override
	protected GeneralGraph getNetwork() throws IllegalArgumentException, InstantiationException, IllegalAccessException {		
		VAERS_Predicates.IsNotPendantPredicate pred = new VAERS_Predicates.IsNotPendantPredicate(super.parent);

		GeneralGraph subgraph = super.makeSubgraph(pred);
		return subgraph;
	}
	
	@Override
	protected String getName() {
		return("no pendant subnetwork");
	}
}