package com.eng.cber.na.transformer;

import java.awt.Shape;
import java.awt.geom.Point2D;

import com.eng.cber.na.event.util.PropertyChangeSupport;

import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.transform.BidirectionalTransformer;
import edu.uci.ics.jung.visualization.transform.shape.ShapeTransformer;

/**
 * Interface that requires its methods to map points from 
 * one or more coordinate systems to other coordinate 
 * systems (e.g., graph to screen and screen to graph). 
 * 
 * This is a PANACEA (OpenGL-reimplementation-friendly) version
 * of the JUNG class MultiLayerTransformer.
 *
 */
public interface MultiLayerTransformer extends BidirectionalTransformer, ShapeTransformer, PropertyChangeSupport {
	void setTransformer(Layer layer, MutableTransformer transformer);
	MutableTransformer getTransformer(Layer layer);
	Point2D inverseTransform(Layer layer, Point2D p);
	Point2D transform(Layer layer, Point2D p);
	Shape transform(Layer layer, Shape shape);
	Shape inverseTransform(Layer layer, Shape shape);
	void setToIdentity();
}
