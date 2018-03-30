package com.eng.cber.na.subgraph;

import java.text.DecimalFormat;

import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.vaers.VAERS_Predicates;


/**
 * This class produces a network (and knows how to
 * add that network to the UI, through the run()
 * method of the parent AbstractCreateGraph) that 
 * contains only the edges that have weights within
 * a range specified by the user.
 * 
 * See CreateTypedEdgeWeightSubgraph for an alternative 
 * that allows the user to specify edge type.
 *
 */
public class CreateEdgeWeightSubgraph extends AbstractCreateEdgeSubgraph {	
	int from, to;
	
	public CreateEdgeWeightSubgraph(int from, int to) {
		super(-2);
		this.from = from;
		this.to = to;
	}
	
	@Override
	protected GeneralGraph getNetwork() throws IllegalArgumentException, InstantiationException, IllegalAccessException {		
		super.ensureFromAndTo(from, to, false);
		VAERS_Predicates.EdgeWeightPredicate pred = new VAERS_Predicates.EdgeWeightPredicate(from, to);
		GeneralGraph subgraph = super.makeSubgraph(pred);
		return subgraph;
	}
	
	@Override
	protected String getName() {
		DecimalFormat df = new DecimalFormat("#");
		return ("edge weight range [" + df.format(from) + "," + df.format(to) + "] subnetwork");
	}
}