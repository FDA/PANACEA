package com.eng.cber.na.graph;

import java.io.Serializable;

import edu.uci.ics.jung.algorithms.scoring.VertexScorer;

/**
 * This is an extension of a JUNG VertexScorer that
 * scores vertices according to their strength -- that is,
 * it associates each vertex with a particular strength value.
 *
 */
public class VertexStrengthScorer implements Serializable, VertexScorer<GeneralNode, Double> {

	private GeneralGraph g;
	
	public VertexStrengthScorer(GeneralGraph g) {
		this.g = g;
	}
	
	@Override
	public Double getVertexScore(GeneralNode n) {
		Double total = 0.0;
		for(GeneralEdge e : g.getIncidentEdges(n)) {
			total += e.getWeight();
		}
		return total;
	}

}
