package com.eng.cber.na.subgraph;

import com.eng.cber.na.graph.AbstractCreateGraph;
import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.graph.GeneralNode;
import com.eng.cber.na.vaers.VAERS_Predicates.AbstractVAERSPredicate;
import com.eng.cber.na.vaers.VAERS_VertexPredicateFilter;

/**
 * This class is a version of AbstractCreateGraph specifically
 * designed for subgraphs built based on node properties.
 * The necessary filters are typed specifically for nodes.
 *
 */
public abstract class AbstractCreateNodeSubgraph extends AbstractCreateGraph {
	protected String name;
	
	public AbstractCreateNodeSubgraph(int rowForParentPath) {
		super(rowForParentPath);
	}
	
	protected GeneralGraph makeSubgraph(AbstractVAERSPredicate<GeneralNode> nodeInSubgraph) {
		VAERS_VertexPredicateFilter nodeFilter = new VAERS_VertexPredicateFilter(nodeInSubgraph);
		GeneralGraph subgraph = nodeFilter.transform(super.parent);
		return subgraph;
	}
	
	@Override
	protected abstract GeneralGraph getNetwork() throws IllegalArgumentException, InstantiationException, IllegalAccessException;
	@Override
	protected abstract String getName();

	public void setName(String name) {
		this.name = name;
	}
}