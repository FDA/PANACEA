package com.eng.cber.na.event.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;


/**
 * Default property change support that tracks a set of 
 * listeners, allows clients to add listeners to that list
 * and fire property changes.
 *
 */
public class DefaultPropertyChangeSupport implements PropertyChangeSupport, Serializable{

	private Object source;
	private transient List<PropertyChangeListener> listeners = new LinkedList<PropertyChangeListener>();

	public DefaultPropertyChangeSupport(Object source) {
		this.source = source;
	}	
	
	@Override
	public void addPropertyChangeListener(PropertyChangeListener l) {
		listeners.add(l);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener l) {
		listeners.remove(l);
	}

	@Override
	public PropertyChangeListener[] getPropertyChangeListeners() {
		return listeners.toArray(new PropertyChangeListener[listeners.size()]);
	}

	@Override
	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		PropertyChangeEvent event = new PropertyChangeEvent(source,propertyName,oldValue,newValue);
		for(PropertyChangeListener listener : getPropertyChangeListeners()) {
			listener.propertyChange(event);
		}
	}

	@Override
	public void firePropertyChange(PropertyChangeEvent e) {
 		for(PropertyChangeListener listener : listeners) {
			listener.propertyChange(e);
		}
	}
	
	private void readObject(ObjectInputStream in) throws IOException{
        try {
			in.defaultReadObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
    	listeners = new LinkedList<PropertyChangeListener>();
    }
}
