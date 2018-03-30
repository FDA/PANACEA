package com.eng.cber.na.layout;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

import com.eng.cber.na.concurrent.NetworkExecutorService;
import com.eng.cber.na.event.util.DefaultPropertyChangeSupport;
import com.eng.cber.na.event.util.PropertyChangeSupport;
import com.eng.cber.na.graph.GeneralGraph;

public class NetworkVisualizationModel<V, E> implements VisualizationModel<V, E>, Serializable {

	private PropertyChangeSupport changeSupport = new DefaultPropertyChangeSupport(this);
	private Layout<V, E> layout;
	private GeneralGraph gg;
	private Layout.LayoutType layoutType;
	
	public Layout.LayoutType getLayoutType() {
		return layoutType;
	}

	public NetworkVisualizationModel() {
	}

	public NetworkVisualizationModel(Layout<V,E> layout, Dimension size) {
		setGraphLayout(layout,size);
		gg = (GeneralGraph) layout.getGraph();
		layoutType = layout.getType();
	}

	public GeneralGraph getModel(){
		return gg;
	}

	@Override
	public void setGraphLayout(Layout<V, E> layout, Dimension size) {
		if(this.layout != null) {
			this.layout.removePropertyChangeListener(this);
		}
		
		this.layout = layout;	
		layout.addPropertyChangeListener(this);
		
		
		if(size != null) {
			layout.setSize(size);
			NetworkExecutorService.submitAlternate(layout);
		}
		gg = (GeneralGraph) layout.getGraph();
		layoutType = layout.getType();
	}

	@Override
	public String toString() {
		return gg.getName();
	}
	
	@Override
	public Layout<V, E> getGraphLayout() {
		return layout;
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

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		firePropertyChange(e);
	}	
}
