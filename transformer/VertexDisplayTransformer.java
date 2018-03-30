package com.eng.cber.na.transformer;

import org.apache.commons.collections15.Transformer;

import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.graph.GeneralNode;

/**
 * The vertex display transformer finds out whether a
 * given vertex should be displayed or not.
 */
public class VertexDisplayTransformer implements Transformer<GeneralNode, Boolean> {

	
	private GeneralGraph graph;
	
	public VertexDisplayTransformer(GeneralGraph graph) {
		this.graph = graph;
	}
	
	@Override
	public Boolean transform(GeneralNode n) {
		
		if (graph.getNodeDisplay(n)) {
			return true;
		}
		else {
			return false;
		}
		
	}
}
