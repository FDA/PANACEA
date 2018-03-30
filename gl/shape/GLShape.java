package com.eng.cber.na.gl.shape;

import java.awt.Color;
import java.nio.FloatBuffer;

import javax.media.opengl.GL2;

/**
 * The GLShape interface is used by shapes that can
 * be rendered by OpenGL.  These shapes must have a radius,
 * they must know whether they contain any particular point,
 * they must know how to render themselves, and they must
 * know how many triangles are used to create them.
 *
 */
public interface GLShape {
	public double getRadius();
	public boolean contains(double x, double y);
	public void render(GL2 gl2, double x, double y, Color fillColor, Color boundColor, FloatBuffer vertexBuffer);
	public int getNumTriangles();
}
