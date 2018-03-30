package com.eng.cber.na.vaers;

import java.util.Collection;

import org.apache.commons.collections15.Predicate;

import com.eng.cber.na.graph.GeneralEdge;
import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.graph.GeneralNode;


/**
 * The VAERS_VertexPredicateFilter is a filter that transforms
 * a source network according to a Predicate object that 
 * allows particular nodes and disallows other nodes. 
 * 
 * This is a PANACEA (OpenGL-reimplementation-friendly) version of 
 * the JUNG class VertexPredicateFilter.  It is necessary because 
 * the original JUNG implementation returns Graph<V,E> rather than
 * FDAGraph; the lack of specificity leads to problems in using
 * the new functions that are designed specifically to work with
 * the FDAGraph structure (such as the triangular weights 
 * transformations, which require each edge to have an associated
 * set of reports).
 *
 */
public class VAERS_VertexPredicateFilter extends VAERS_AbstractTransformer {
	Predicate<GeneralNode> vertex_pred;
	
	public VAERS_VertexPredicateFilter(Predicate<GeneralNode> vertex_pred) {
		this.vertex_pred = vertex_pred;
	}
	
	@Override
	public GeneralGraph transform(GeneralGraph g) {
		GeneralGraph filtered = super.getFiltered(g);
		transformTarget(g, filtered);
		return filtered;
	}
	
	public void transformTarget(GeneralGraph g, GeneralGraph target) {
		for (GeneralNode v : g.getVertices()) {
			if (vertex_pred.evaluate(v)) {
				target.addVertex(v);
			}
		}
		
		Collection<GeneralNode> filtered_vertices = target.getVertices();
		
		for (GeneralEdge e : g.getEdges()) {
			Collection<GeneralNode> incident = g.getIncidentVertices(e);
			if (filtered_vertices.containsAll(incident)) {
				target.addEdge(e, incident);
			}
		}
	}
}
