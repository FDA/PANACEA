package com.eng.cber.na.concurrent;

import java.beans.PropertyChangeListener;

import com.eng.cber.na.concurrent.cmm.CMMConcurrentJob;
import com.eng.cber.na.graph.GeneralGraph;

/**
 * Class with static functions to kick off/start the implemented
 * multi-threaded applications, namely computing multiple 
 * measures concurrently.
 *
 */
public class ConcurrentJobs {
	
	public static void computeMultipleMeasures(GeneralGraph g, PropertyChangeListener listener) {
		ConcurrentJob job = new CMMConcurrentJob(g);
		job.addPropertyChangeListener(listener);
		if (g.getVertexCount() > 0) {
			job.start();
		}
	}
}
