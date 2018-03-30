package com.eng.cber.na.transformer;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.eng.cber.na.event.util.DefaultPropertyChangeSupport;
import com.eng.cber.na.event.util.PropertyChangeSupport;

import edu.uci.ics.jung.visualization.transform.AffineTransformer;

/**
 * Discrete class that provides methods to map points from one coordinate 
 * system to another (like graph to screen, one scale to another scale,
 * or the effect of a transformation or translation of a graph). 
 * The class uses Java's AffineTranform objects to facilitate the 
 * transformations.
 * 
 * This is a PANACEA (OpenGL-reimplementation-friendly) version
 * of the JUNG class MutableAffineTransformer.
 *
 */
public class MutableAffineTransformer extends AffineTransformer implements MutableTransformer {

	protected PropertyChangeSupport changeSupport = new DefaultPropertyChangeSupport(this);
    
	public MutableAffineTransformer(AffineTransform transform) {
        super(transform);
    }

	@Override
    public String toString() {
        return transform.toString();
    }

	@Override
    public void scale(double scalex, double scaley, Point2D from) {
        AffineTransform xf = AffineTransform.getTranslateInstance(from.getX(),from.getY());
        xf.scale(scalex, scaley);
        xf.translate(-from.getX(), -from.getY());
        inverse = null;
        transform.preConcatenate(xf);
        firePropertyChange("scale", null, null);
    }

	@Override  
    public void setScale(double scalex, double scaley, Point2D from) {
        transform.setToIdentity();
        scale(scalex, scaley, from);
    }

	@Override  
    public void shear(double shx, double shy, Point2D from) {
        inverse = null;
        AffineTransform at = AffineTransform.getTranslateInstance(from.getX(), from.getY());
        at.shear(shx, shy);
        at.translate(-from.getX(), -from.getY());
        transform.preConcatenate(at);
        firePropertyChange("shear", null, null);
    }

	@Override   
    public void setTranslate(double tx, double ty) {
        float scalex = (float) transform.getScaleX();
        float scaley = (float) transform.getScaleY();
        float shearx = (float) transform.getShearX();
        float sheary = (float) transform.getShearY();
        inverse = null;
        transform.setTransform(scalex, sheary, shearx, scaley, tx, ty);
        firePropertyChange("setTranslate", null, null);
    }

	@Override   
    public void translate(double offsetx, double offsety) {
        inverse = null;
        transform.translate(offsetx, offsety);
        firePropertyChange("translate", null, null);
    }

	@Override   
    public void rotate(double theta, Point2D from) {
        AffineTransform rotate = AffineTransform.getRotateInstance(theta, from.getX(), from.getY());
        inverse = null;
        transform.preConcatenate(rotate);
        firePropertyChange("rotate", null, null);
    }

	@Override    
    public void rotate(double radians, double x, double y) {
        inverse = null;
        transform.rotate(radians, x, y);
        firePropertyChange("rotate", null, null);
    }

	@Override    
    public void concatenate(AffineTransform xform) {
        inverse = null;
        transform.concatenate(xform);
        firePropertyChange("concatenate", null, null);        
    }

	@Override
	public void preConcatenate(AffineTransform xform) {
        inverse = null;
        transform.preConcatenate(xform);
        firePropertyChange("preConcatenate", null, null);
    }   

	@Override
    public void setToIdentity() {
        inverse = null;
        transform.setToIdentity();
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
	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		changeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}

	@Override
	public void firePropertyChange(PropertyChangeEvent e) {
		changeSupport.firePropertyChange(e);
	}
	
	@Override
	public PropertyChangeListener[] getPropertyChangeListeners() {
		return changeSupport.getPropertyChangeListeners();
	}
}
