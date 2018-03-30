package com.eng.cber.na.concurrent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Future;

import com.eng.cber.na.concurrent.WorkerBundle.BundleStateValue;
import com.eng.cber.na.dialog.ProcessingDialog;
import com.eng.cber.na.event.util.DefaultPropertyChangeSupport;
import com.eng.cber.na.event.util.PropertyChangeSupport;

/**
 * Abstraction of a class that implements the concurrent job interface.  
 * Bundles start by being submitted to the NetworkExecutorService.
 * 
 * This class is implemented by any classes that are looking to 
 * parallelize their processing; these classes are stored in 
 * a lower-level namespace.
 *
 */
public abstract class AbstractConcurrentJob implements ConcurrentJob {

	private PropertyChangeSupport changeSupport = new DefaultPropertyChangeSupport(this);
	
	private Queue<WorkerBundle> bundleQueue = new LinkedList<WorkerBundle>();
	private List<Future<?>> bundleFutures = new LinkedList<Future<?>>();
	
	private ProcessingDialog abortDialog = new ProcessingDialog(this);
	
	private int totalProgress = 0;
	private boolean started = false;
	
	public AbstractConcurrentJob() {
		addPropertyChangeListener(abortDialog);		
	}
		
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
	
	protected Queue<WorkerBundle> getBundleQueue() {
		return bundleQueue;
	}
	
	protected void registerBundle(WorkerBundle bundle) {
		bundle.addPropertyChangeListener(this);
		bundleQueue.add(bundle);
	}
	
	@Override
	public void start() {
		WorkerBundle bundle = bundleQueue.poll();
		List<Future<?>> fs = NetworkExecutorService.submitBundle(bundle);
	}
	
	@Override
	public void cancel() {
		for(Future<?> future : bundleFutures) {
			future.cancel(true);
		}
		bundleFutures.clear();
		abortDialog.destroyDialog();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		WorkerBundle workerBundle = (WorkerBundle)evt.getSource();
		String property = evt.getPropertyName();
		Object value = evt.getNewValue();
		
		if(property.equals("state") && value.equals(BundleStateValue.STARTED)) {
			if(!started) {
				firePropertyChange("state",null,BundleStateValue.STARTED);
				started = true;
			}
		}
		else if(property.equals("progress")) {
			Integer p = (Integer)value;
			firePropertyChange("progress",null,(int)(workerBundle.getLoad() * ((double)p/100) + totalProgress));
		}
		else if(property.equals("state") && value.equals(BundleStateValue.DONE)) {
			workerBundle.removePropertyChangeListener(this);
			bundleFutures.clear();
			
			Queue<WorkerBundle> queue = getBundleQueue();
			if(!queue.isEmpty()) {
				totalProgress += workerBundle.getLoad();
				
				WorkerBundle bundle = bundleQueue.poll();
				List<Future<?>> fs = NetworkExecutorService.submitBundle(bundle);
				bundleFutures.addAll(fs);
			}
			else {
				abortDialog.destroyDialog();
				firePropertyChange("state",null,JobStateValue.DONE);
			}
		}
	}
}
