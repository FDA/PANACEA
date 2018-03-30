package com.eng.cber.na.transformer;

import org.apache.commons.collections15.Transformer;

import com.eng.cber.na.graph.GeneralEdge;
import com.eng.cber.na.graph.GeneralGraph;

/**
 * The edge display transformer finds out whether a
 * given edge should be displayed or not.
 * 
 * Note that this only checks the edge's isDisplay()
 * method.  Some edges may be hidden if there are
 * more edges than the max edge size to display.
 * See NetworkGLEdgeRenderer.
 */
public class EdgeDisplayTransformer implements Transformer<GeneralEdge, Boolean> {
	private GeneralGraph graph;
	public EdgeDisplayTransformer(GeneralGraph graph) {
		this.graph = graph;
	}
	
	@Override
	public Boolean transform(GeneralEdge n) {
		
		if (graph.getEdgeDisplay(n)) {
			return true;
		}
		else {
			return false;
		}
	}
}
