package com.eng.cber.na.layout;

import java.awt.Dimension;
import java.beans.PropertyChangeListener;

import com.eng.cber.na.event.util.PropertyChangeSupport;

/**
 * An interface for classes that track graph layouts. (The
 * layouts themselves track the graphs.)
 * 
 */
public interface VisualizationModel<V, E> extends PropertyChangeSupport, PropertyChangeListener {
	
	void setGraphLayout(Layout<V,E> layout, Dimension size);
	
    Layout<V,E> getGraphLayout();
    
}
