package com.eng.cber.na.subgraph;

import com.eng.cber.na.graph.AbstractCreateGraph;
import com.eng.cber.na.graph.GeneralEdge;
import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.vaers.VAERS_EdgePredicateFilter;
import com.eng.cber.na.vaers.VAERS_Predicates;
import com.eng.cber.na.vaers.VAERS_Predicates.AbstractVAERSPredicate;
import com.eng.cber.na.vaers.VAERS_VertexPredicateFilter;

/**
 * This class is a version of AbstractCreateGraph specifically
 * designed for subgraphs built based on edge properties.
 * The necessary filters are typed specifically for edges.
 *
 */
public abstract class AbstractCreateEdgeSubgraph extends AbstractCreateGraph {
	
	public AbstractCreateEdgeSubgraph(int rowForParentPath) {
		super(rowForParentPath);
	}
	
	protected GeneralGraph makeSubgraph(AbstractVAERSPredicate<GeneralEdge> edgeInSubgraph) {
		// Remove edges
		VAERS_EdgePredicateFilter edgeFilter = new VAERS_EdgePredicateFilter(edgeInSubgraph);
		GeneralGraph subgraphWithIsolates = edgeFilter.transform(super.parent);
		System.out.println(subgraphWithIsolates.getReportCount());
		// Then remove any nodes left over that don't have any edges
		VAERS_VertexPredicateFilter nodeFilterNoIsolates = new VAERS_VertexPredicateFilter(new VAERS_Predicates.IsNotIsolatePredicate(subgraphWithIsolates));
		GeneralGraph subgraph = nodeFilterNoIsolates.transform(subgraphWithIsolates);		
		
		return subgraph;
	}
	
	@Override
	protected abstract GeneralGraph getNetwork() throws IllegalArgumentException, InstantiationException, IllegalAccessException;
	@Override
	protected abstract String getName();
}