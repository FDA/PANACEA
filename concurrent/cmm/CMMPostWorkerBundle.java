package com.eng.cber.na.concurrent.cmm;

import java.util.List;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.concurrent.AbstractWorkerBundle;
import com.eng.cber.na.graph.GeneralGraph;

/**
 * A bundle of workers that do the post traversals for the
 * compute multiple measures function.  This class bundles 
 * a single CMMPost object and registers it.
 *
 */
public class CMMPostWorkerBundle extends AbstractWorkerBundle {

	public CMMPostWorkerBundle(GeneralGraph graph, CMMMainWorkerBundle mainBundle) {
		List<CMMMain> mainWorkers = (List<CMMMain>)mainBundle.getWorkers();
		CMMPost worker = new CMMPost(mainWorkers,graph);
		registerWorker(worker);
	}
	
	@Override
	protected void finished() {	
		NetworkAnalysisVisualization.getInstance().setComboString();
	}

	@Override
	public int getLoad() {
		return 5;
	}

}
