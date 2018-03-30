package com.eng.cber.na.transformer;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.eng.cber.na.event.util.DefaultPropertyChangeSupport;
import com.eng.cber.na.event.util.PropertyChangeSupport;

import edu.uci.ics.jung.visualization.Layer;

/**
 * Discrete implementation of MultiLayerTranformer that 
 * maps points between coordinate systems. This is a basic 
 * implementation that provides two Layers: View and 
 * Layout. It also provides ChangeEventSupport  (e.g., graph to 
 * screen and screen to graph). 
 * 
 * This is a PANACEA (OpenGL-reimplementation-friendly) version
 * of the JUNG class BasicTransformer.
 *
 */
public class BasicTransformer implements MultiLayerTransformer, PropertyChangeListener {

	protected PropertyChangeSupport changeSupport = new DefaultPropertyChangeSupport(this);
	protected MutableTransformer viewTransformer = new MutableAffineTransformer(new AffineTransform());	    
	protected MutableTransformer layoutTransformer = new MutableAffineTransformer(new AffineTransform());
	    
	public BasicTransformer() {
		viewTransformer.addPropertyChangeListener(this);
		layoutTransformer.addPropertyChangeListener(this);
	}
	
	protected void setViewTransformer(MutableTransformer transformer) {
	    this.viewTransformer.removePropertyChangeListener(this);
	    this.viewTransformer = transformer;
	    this.viewTransformer.addPropertyChangeListener(this);
	}
	
	protected void setLayoutTransformer(MutableTransformer transformer) {
	    this.layoutTransformer.removePropertyChangeListener(this);
	    this.layoutTransformer = transformer;
	    this.layoutTransformer.addPropertyChangeListener(this);
	}
	
	protected MutableTransformer getLayoutTransformer() {
		return layoutTransformer;
	}
	
	protected MutableTransformer getViewTransformer() {
		return viewTransformer;
	}
	
	@Override
	public Point2D inverseTransform(Point2D p) {
	    return inverseLayoutTransform(inverseViewTransform(p));
	}
	
	protected Point2D inverseViewTransform(Point2D p) {
	    return viewTransformer.inverseTransform(p);
	}
	
	protected Point2D inverseLayoutTransform(Point2D p) {
	    return layoutTransformer.inverseTransform(p);
	}
	
	@Override
	public Point2D transform(Point2D p) {
	    return viewTransform(layoutTransform(p));
	}
	
	protected Point2D viewTransform(Point2D p) {
	    return viewTransformer.transform(p);
	}
	
	protected Point2D layoutTransform(Point2D p) {
	    return layoutTransformer.transform(p);
	}
	
	@Override
	public Shape inverseTransform(Shape shape) {
	    return inverseLayoutTransform(inverseViewTransform(shape));
	}
	
	protected Shape inverseViewTransform(Shape shape) {
	    return viewTransformer.inverseTransform(shape);
	}
	
	protected Shape inverseLayoutTransform(Shape shape) {
	    return layoutTransformer.inverseTransform(shape);
	}
	
	@Override
	public Shape transform(Shape shape) {
	    return viewTransform(layoutTransform(shape));
	}
	
	protected Shape viewTransform(Shape shape) {
	    return viewTransformer.transform(shape);
	}
	
	protected Shape layoutTransform(Shape shape) {
	    return layoutTransformer.transform(shape);
	}
	
	@Override
	public void setToIdentity() {
		layoutTransformer.setToIdentity();
		viewTransformer.setToIdentity();
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
	
	@Override
	public MutableTransformer getTransformer(Layer layer) {
		if(layer == Layer.LAYOUT) return layoutTransformer;
		if(layer == Layer.VIEW) return viewTransformer;
		return null;
	}
	
	@Override
	public Point2D inverseTransform(Layer layer, Point2D p) {
		if(layer == Layer.LAYOUT) return inverseLayoutTransform(p);
		if(layer == Layer.VIEW) return inverseViewTransform(p);
		return null;
	}
	
	@Override
	public void setTransformer(Layer layer, MutableTransformer transformer) {
		if(layer == Layer.LAYOUT) setLayoutTransformer(transformer);
		if(layer == Layer.VIEW) setViewTransformer(transformer);
		
	}
	
	@Override
	public Point2D transform(Layer layer, Point2D p) {
		if(layer == Layer.LAYOUT) return layoutTransform(p);
		if(layer == Layer.VIEW) return viewTransform(p);
		return null;
	}
	
	@Override
	public Shape transform(Layer layer, Shape shape) {
		if(layer == Layer.LAYOUT) return layoutTransform(shape);
		if(layer == Layer.VIEW) return viewTransform(shape);
		return null;
	}
	
	@Override
	public Shape inverseTransform(Layer layer, Shape shape) {
		if(layer == Layer.LAYOUT) return inverseLayoutTransform(shape);
		if(layer == Layer.VIEW) return inverseViewTransform(shape);
		return null;
	}

}
