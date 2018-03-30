package com.eng.cber.na.concurrent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.SwingWorker;
import javax.swing.SwingWorker.StateValue;

import com.eng.cber.na.event.util.DefaultPropertyChangeSupport;
import com.eng.cber.na.event.util.PropertyChangeSupport;

/**
 * Abstraction of a class that implements the worker bundle
 * interface.
 * 
 * Each worker bundle combines a set of workers that do the 
 * work of the multi-threaded task.
 * 
 * This class is implemented by any classes that are looking to 
 * parallelize their processing.  These classes (e.g., compute
 * multiple measures) are stored in a lower-level namespace.
 *
 */
public abstract class AbstractWorkerBundle implements WorkerBundle {

	protected PropertyChangeSupport changeSupport = new DefaultPropertyChangeSupport(this);
	protected List<SwingWorker<Object,Object>> workers = new LinkedList<SwingWorker<Object,Object>>();	
	
	private int currentProgress = 0;
	private boolean started = false;


	@Override
	public void addPropertyChangeListener(PropertyChangeListener l) {
		changeSupport.addPropertyChangeListener(l);
	}
	
	@Override
	public void removePropertyChangeListener(PropertyChangeListener l) {
		changeSupport.removePropertyChangeListener(l);
	}
	
	@Override
	public PropertyChangeListener[] getPropertyChangeListeners() {
		return changeSupport.getPropertyChangeListeners();
	}

	@Override
	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		changeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}
	
	@Override
	public void firePropertyChange(PropertyChangeEvent e) {
		changeSupport.firePropertyChange(e);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		SwingWorker<Object,Object> worker = (SwingWorker<Object,Object>)evt.getSource();
		String property = evt.getPropertyName();
		Object value = evt.getNewValue();
		
		synchronized(this) {
			if(property.equals("state") && value.equals(StateValue.STARTED)) {
				if(!started) {
					firePropertyChange("state",null,BundleStateValue.STARTED);
					started = true;
				}
			}
			else if(property.equals("progress")) {
				int sum = 0;
				for(SwingWorker<Object,Object> w : workers) {
					sum += w.getProgress();
				}			
				int instantProgress = sum / workers.size();
				if(instantProgress != currentProgress) {
					firePropertyChange("progress",currentProgress,instantProgress);
					currentProgress = instantProgress;
				}
			}
			else if(property.equals("state") && value.equals(StateValue.DONE)) {
				worker.removePropertyChangeListener(this);
				for(SwingWorker<Object,Object> w : workers) {
					if(!w.isDone()) {
						return;
					}
				}
				finished();
				firePropertyChange("state",null,BundleStateValue.DONE);
			}
		}
	}
	
	protected abstract void finished();
	
	protected void registerWorker(SwingWorker<Object,Object> worker) {
		worker.addPropertyChangeListener(this);
		workers.add(worker);
	}
	
	@Override
	public List<? extends SwingWorker<Object,Object>> getWorkers() {
		return workers;
	}
}
