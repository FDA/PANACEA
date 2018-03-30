package com.eng.cber.na.concurrent;

import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.SwingWorker;

import com.eng.cber.na.event.util.PropertyChangeSupport;

/**
 * The worker bundle interface.
 * A worker bundle has workers and a load that can be obtained.
 *
 */
public interface WorkerBundle extends PropertyChangeSupport, PropertyChangeListener {	
	
	public static enum BundleStateValue {
		STARTED, DONE
	};
	
	public List<? extends SwingWorker<Object,Object>> getWorkers();	
	public int getLoad();
}
