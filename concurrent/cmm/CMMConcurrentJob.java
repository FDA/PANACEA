package com.eng.cber.na.concurrent.cmm;

import com.eng.cber.na.concurrent.AbstractConcurrentJob;
import com.eng.cber.na.graph.GeneralGraph;

/**
 * A class that implements concurrent jobs for computing
 * multiple measures.  
 * 
 * Each concurrent job executes a main and a secondary bundle.
 */
public class CMMConcurrentJob extends AbstractConcurrentJob {

	private GeneralGraph graph;
	
	public CMMConcurrentJob(GeneralGraph graph) {	
		super();
		this.graph = graph;
		CMMMainWorkerBundle main = new CMMMainWorkerBundle(graph);
		registerBundle(main);
		
		CMMPostWorkerBundle post = new CMMPostWorkerBundle(graph,main);
		registerBundle(post);
	}
	
	public GeneralGraph getGraph() {
		return graph;
	}
}
