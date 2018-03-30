package com.eng.cber.na.concurrent.cmm;

import java.io.Serializable;

import com.eng.cber.na.concurrent.AbstractWorkerBundle;
import com.eng.cber.na.concurrent.IncrementableHashMap;
import com.eng.cber.na.graph.GeneralEdge;
import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.graph.GeneralNode;

/**
 * A bundle of workers that do the main traversals for the
 * compute multiple measures function.  This class bundles 
 * together a set of CMMMain objects and registers them.
 *
 */
public class CMMMainWorkerBundle extends AbstractWorkerBundle implements Serializable {

	private GeneralGraph graph;
	
	private IncrementableHashMap<GeneralNode> betweennessTransformer = new IncrementableHashMap<GeneralNode>();
	private IncrementableHashMap<GeneralNode> closenessTransformer = new IncrementableHashMap<GeneralNode>();
	private IncrementableHashMap<GeneralEdge> betweennessEdgeTransformer = new IncrementableHashMap<GeneralEdge>();
	private IncrementableHashMap<GeneralEdge> linSimilarityEdgeTransformer = new IncrementableHashMap<GeneralEdge>();
	
	public CMMMainWorkerBundle(GeneralGraph graph) {
		this.graph = graph;

		
		for (GeneralNode v : graph.getVertices()) {
			if(v.getID().equals("ReferenceDocument"))
					continue;

			betweennessTransformer.put(v, 0.0);
			closenessTransformer.put(v, 0.0);
		}

		for (GeneralEdge e : graph.getEdges()) {
			if(e.node1.getID().equals("ReferenceDocument") || e.node2.getID().equals("ReferenceDocument") )
				continue;
			betweennessEdgeTransformer.put(e, 0.0);
			linSimilarityEdgeTransformer.put(e, 0.0);
			if (e.getWeight() < 0) {
				throw new IllegalArgumentException(String.format("Weight for edge '%s' is <0: %d", e, e.getWeight()));
			}
		}
		
		for (GeneralNode v : graph.getVertices()) {
			if(v.getID().equals("ReferenceDocument"))
				continue;
			CMMMain worker = new CMMMain(graph,
										 v,
										 betweennessTransformer,
										 closenessTransformer,
										 betweennessEdgeTransformer,
										 linSimilarityEdgeTransformer);
			
			registerWorker(worker);
		}
	}

	@Override
	protected void finished() {
		graph.setBetweennessEdgeTransformer(betweennessEdgeTransformer);
		graph.setLinSimilarityEdgeTransformer(linSimilarityEdgeTransformer);
		graph.setBetweennessTransformer(betweennessTransformer);
		graph.setClosenessTransformer(closenessTransformer);
	}

	@Override
	public int getLoad() {
		return 95;
	}
}
