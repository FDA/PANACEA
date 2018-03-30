package com.eng.cber.na.subgraph;

import java.text.DecimalFormat;

import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.vaers.VAERS_Predicates;

/**
 * This class produces a network (and knows how to
 * add that network to the UI, through the run()
 * method of the parent AbstractCreateGraph) that 
 * contains only nodes whose normalized betweenness
 * falls between the given "from" and "to" values.
 *
 */
public class CreateBetweennessSubgraph extends AbstractCreateNodeSubgraph {	
	Double from, to;
	
	public CreateBetweennessSubgraph(Double from, Double to) {
		super(-2);
		this.from = from;
		this.to = to;
	}
	
	@Override
	protected GeneralGraph getNetwork() throws IllegalArgumentException, InstantiationException, IllegalAccessException {		
		super.ensureFromAndTo(from, to, true);
		VAERS_Predicates.BetweennessPredicate pred = new VAERS_Predicates.BetweennessPredicate(super.parent, from, to);

		GeneralGraph subgraph = super.makeSubgraph(pred);
		return subgraph;
	}
	
	@Override
	protected String getName() {
		DecimalFormat df = new DecimalFormat("#.##");
		return ("betweenness range ["+df.format(from)+","+df.format(to)+"] subnetwork");
	}
}