package com.eng.cber.na.annotations;

import java.awt.Color;
import java.awt.geom.Point2D;

/**
 * Stores an annotation.  The type should be
 * either a String or a GLRectangle.
 *
 * @param <T>
 */
public class NetworkGLAnnotation<T> {
	
	protected T annotation;
	protected Point2D location;
	protected Color color;
	
	public NetworkGLAnnotation(T annotation, Point2D location, Color color) {
		this.annotation = annotation;
		this.location = location;
		this.color = color;
	}

	public T getAnnotation() {
		return annotation;
	}
	public void setAnnotation(T annotation) {
		this.annotation = annotation;
	}
	public Point2D getLocation() {
		return location;
	}
	public void setLocation(Point2D location) {
		this.location = location;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	
}