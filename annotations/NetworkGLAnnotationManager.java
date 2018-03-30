package com.eng.cber.na.annotations;

import java.io.Serializable;
import java.util.ArrayList;

import com.eng.cber.na.gl.shape.GLRectangle;

/**
 * Stores all of the annotations for a particular layout
 * object, and supports removing them.
 */

public class NetworkGLAnnotationManager<V,E,T> implements Serializable {
	
	private ArrayList<NetworkGLAnnotation> annotations = new ArrayList<NetworkGLAnnotation>();
	
	public void addAnnotation(NetworkGLAnnotation<T> annotation) {
		annotations.add(annotation);
	}
	
	public void removeAnnotation(NetworkGLAnnotation<T> annotation) {
		annotations.remove(annotation);
	}
	
	public void removeLastAnnotation() {
		if (!(annotations.isEmpty())) {
			annotations.remove(annotations.size()-1);
		}
	}
	
	public void clear() {
		annotations.clear();
	}
	
    public ArrayList<NetworkGLAnnotation> getAnnotations() {
		return annotations;
	}
    
    public ArrayList<NetworkGLAnnotation> getTextAnnotations() {
    	ArrayList<NetworkGLAnnotation> textAnnotations = new ArrayList<NetworkGLAnnotation>();
    	for (NetworkGLAnnotation ann : annotations) {
    		if (ann.getAnnotation() instanceof String) {
    			textAnnotations.add(ann);
    		}
    	}
    	return textAnnotations;
    }
    
    public ArrayList<NetworkGLAnnotation> getShapeAnnotations() {
    	ArrayList<NetworkGLAnnotation> shapeAnnotations = new ArrayList<NetworkGLAnnotation>();
    	for (NetworkGLAnnotation ann : annotations) {
    		if (ann.getAnnotation() instanceof GLRectangle) {
    			shapeAnnotations.add(ann);
    		}
    	}
    	return shapeAnnotations;
    }
    
    
	
}