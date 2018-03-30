package com.eng.cber.na.gl;

import java.awt.geom.Point2D;

/**
 * An interface that controls the scaling 
 * for objects rendered through OpenGL.
 * 
 * This is an OpenGL version of JUNG's interface ScalingControl.
 *
 */
public interface GLScalingControl {
	public void scale(float amount, Point2D pt);
}
