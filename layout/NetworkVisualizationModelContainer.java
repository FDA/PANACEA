package com.eng.cber.na.layout;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.eng.cber.na.event.util.DefaultPropertyChangeSupport;
import com.eng.cber.na.event.util.PropertyChangeSupport;
import com.eng.cber.na.graph.GeneralEdge;
import com.eng.cber.na.graph.GeneralNode;
import com.eng.cber.na.layout.Layout.LayoutType;


/**
 * An implementing class that tracks graph layouts.  Instances
 * of NetworkVisualizationModelContainer keep track of a set
 * of laid out networks according to the algorithm with which
 * they were laid out (for the purposes of recalling existing
 * layouts to screen), as well as tracking which model is currently
 * being displayed and a set of property change listeners. They
 * also allow the user to reset the current layout / recalculate
 * node positions based on the current layout algorithm.
 * 
 */
public class NetworkVisualizationModelContainer implements VisualizationModel<GeneralNode, GeneralEdge>, Serializable {

	private Map<String,NetworkVisualizationModel<GeneralNode,GeneralEdge>> models; 
	public Map<String, NetworkVisualizationModel<GeneralNode, GeneralEdge>> getModels() {
		return models;
	}

	private NetworkVisualizationModel<GeneralNode,GeneralEdge> currentModel;
	private Dimension d;
	private PropertyChangeSupport changeSupport = new DefaultPropertyChangeSupport(this);
	
	public NetworkVisualizationModelContainer() {
		this.currentModel = new NetworkVisualizationModel<GeneralNode,GeneralEdge>();
	}
	
	public NetworkVisualizationModelContainer(Map<String,NetworkVisualizationModel<GeneralNode,GeneralEdge>> models) {
		this.models = models;
		this.currentModel = models.get(models.keySet().toArray()[0]);
	}
		
	public NetworkVisualizationModelContainer(Layout<GeneralNode,GeneralEdge> layout, Dimension dim) {
		this.currentModel = new NetworkVisualizationModel<GeneralNode,GeneralEdge>(layout,dim);
		models = new HashMap<String,NetworkVisualizationModel<GeneralNode,GeneralEdge>>();
		models.put(layout.getType().toString(),this.currentModel);
		this.d = dim;
	}

	public NetworkVisualizationModelContainer(Layout<GeneralNode,GeneralEdge> layout, Dimension dim, int dualID) {
		this.currentModel = new NetworkVisualizationModel<GeneralNode,GeneralEdge>(layout,dim);
		models = new HashMap<String,NetworkVisualizationModel<GeneralNode,GeneralEdge>>();

		LayoutType type = layout.getType();
		String typeString; 
		typeString = type.toString();
		if (dualID > 0)
			typeString = typeString + dualID;
			
		models.put(typeString,this.currentModel);
		this.d = dim;
	}
	
	public Dimension getDimension() {
		return d;
	}

	public void setDimension(Dimension d) {
		this.d = d;
	}
	
	public NetworkVisualizationModel<GeneralNode,GeneralEdge> getCurrentModel(){
		return currentModel;
	}
	
	public void setNetworkLayout(Layout<GeneralNode,GeneralEdge> layout, Dimension viewSize, int dualID) {
		LayoutType type = layout.getType();
		String typeString; 
		typeString = type.toString();
		if (dualID > 0)
			typeString = typeString + dualID;
			
		if(!models.containsKey(typeString)) {
			NetworkVisualizationModel<GeneralNode,GeneralEdge> model = new NetworkVisualizationModel<GeneralNode,GeneralEdge>(layout, viewSize);
			models.put(typeString, model);
			for(PropertyChangeListener listener : changeSupport.getPropertyChangeListeners()) {
				models.get(typeString).addPropertyChangeListener(listener);
			}
			currentModel = models.get(typeString);
		}
		else {
			currentModel = models.get(typeString);
			firePropertyChange("set_network_layout",null,null);
		}
	}

	public void setNetworkModel(Layout<GeneralNode,GeneralEdge> layout, int dualID) {
		LayoutType type = layout.getType();
		String typeString; 
		typeString = type.toString();
		if (dualID > 0)
			typeString = typeString + dualID;
			
		currentModel = models.get(typeString);
		return;
	}
	
	public boolean getModelExist(Layout<GeneralNode,GeneralEdge> layout, int dualID) {
		LayoutType type = layout.getType();
		String typeString; 
		typeString = type.toString();
		if (dualID > 0)
			typeString = typeString + dualID;
			
		if (models.containsKey(typeString))
			return true;
		else
			return false;
	}
	public boolean getModelExist(String typeString, int dualID) {
		if (dualID > 0)
			typeString = typeString + dualID;
			
		if (models.containsKey(typeString))
			return true;
		else
			return false;
	}
	
	public void resetCurrentNetworkLayout(Dimension size) {
		Dimension origSize = currentModel.getGraphLayout().getSize();
		
		if (size.height == origSize.height && size.width == origSize.width)
			currentModel.setGraphLayout(currentModel.getGraphLayout(), size);
		else
		{
			Layout<GeneralNode, GeneralEdge> layout = currentModel.getGraphLayout();
			layout.setSize(size);
			layout.layout();
			
			// Annotations cannot move with the nodes in a new layout, so remove them.
			layout.getNetworkGLAnnotationManager().clear();
			currentModel.setGraphLayout(layout, size);
		}
	}
	
	@Override
	public void firePropertyChange(PropertyChangeEvent e) {
		this.currentModel.firePropertyChange(e);
	}

	@Override
	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		this.currentModel.firePropertyChange(propertyName, oldValue, newValue);
	}
	
	@Override
	public void addPropertyChangeListener(PropertyChangeListener cl) {
		changeSupport.addPropertyChangeListener(cl);
		for(NetworkVisualizationModel<GeneralNode,GeneralEdge> model : models.values()) {
			model.addPropertyChangeListener(cl);
		}
	}

	@Override
	public PropertyChangeListener[] getPropertyChangeListeners() {
		return changeSupport.getPropertyChangeListeners();
	}

	@Override
	public Layout<GeneralNode, GeneralEdge> getGraphLayout() {
		return this.currentModel.getGraphLayout();
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener cl) {
		changeSupport.removePropertyChangeListener(cl);
		for(NetworkVisualizationModel<GeneralNode,GeneralEdge> model : models.values()) {
			model.removePropertyChangeListener(cl);
		}
	}
	@Override
	public String toString(){
		return  currentModel.getModel().getName();
	}

	@Override
	public void setGraphLayout(Layout<GeneralNode, GeneralEdge> layout, Dimension viewSize) {
		this.d = viewSize;
		this.currentModel.setGraphLayout(layout, viewSize);		
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		this.currentModel.propertyChange(evt);
	}
}
