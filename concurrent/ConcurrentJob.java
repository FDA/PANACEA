package com.eng.cber.na.concurrent;

import java.beans.PropertyChangeListener;

import com.eng.cber.na.event.util.PropertyChangeSupport;

/**
 * The concurrent job interface.
 * A concurrent job can start or be cancelled.
 *
 */
public interface ConcurrentJob extends PropertyChangeSupport, PropertyChangeListener {

	public static enum JobStateValue {
		STARTED,DONE
	};
	
	public void start();
	
	public void cancel();
}
