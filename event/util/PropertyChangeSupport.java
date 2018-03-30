package com.eng.cber.na.event.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * An general interface for PropertyChangeSupport that 
 * allows multiple PropertyChangeListeners.
 *
 */
public interface PropertyChangeSupport {	
	public void addPropertyChangeListener(PropertyChangeListener l);	
	public void removePropertyChangeListener(PropertyChangeListener l);
	public PropertyChangeListener[] getPropertyChangeListeners();    
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue);
    public void firePropertyChange(PropertyChangeEvent e);
}
