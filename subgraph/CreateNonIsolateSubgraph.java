package com.eng.cber.na.subgraph;

import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.vaers.VAERS_Predicates;

/**
 * This class produces a network (and knows how to
 * add that network to the UI, through the run()
 * method of the parent AbstractCreateGraph) that 
 * removes all isolates.
 *
 */
public class CreateNonIsolateSubgraph extends AbstractCreateNodeSubgraph {	
	
	public CreateNonIsolateSubgraph() {
		super(-2);
	}
	
	@Override
	protected GeneralGraph getNetwork() throws IllegalArgumentException, InstantiationException, IllegalAccessException {		
		VAERS_Predicates.IsNotIsolatePredicate pred = new VAERS_Predicates.IsNotIsolatePredicate(super.parent);

		GeneralGraph subgraph = super.makeSubgraph(pred);
		return subgraph;
	}
	
	@Override
	protected String getName() {
		return ("no isolate subnetwork");
	}
}