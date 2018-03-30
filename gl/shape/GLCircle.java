package com.eng.cber.na.gl.shape;

import java.awt.Color;
import java.nio.FloatBuffer;

import javax.media.opengl.GL2;


/**
 * This class is a OpenGL-renderable shape that forms a
 * circle. It knows how to render itself as a circle
 * through putting the necessary information into the buffers --
 * many iterations around a circle, incrementing the angle
 * of the triangle being drawn slightly each time.
 *
 */
public class GLCircle implements GLShape {

	private double radius;
	
	public GLCircle(double radius) {
		this.radius = radius;
	}
	
	@Override
	public void render(GL2 gl2, double x, double y, Color fillColor, Color boundColor, FloatBuffer vertexBuffer) {
		
		float r = (float)fillColor.getRed()/255;
		float g = (float)fillColor.getGreen()/255;
		float b = (float)fillColor.getBlue()/255;
		float a = (float)fillColor.getAlpha()/255;
		
		int numTriangles = getNumTriangles();
        double angleIncr = 2*Math.PI / numTriangles;
		
        double angle = 0;
		for(int i = 0; i < numTriangles; i++) {			
			vertexBuffer.put(r);
			vertexBuffer.put(g);
			vertexBuffer.put(b);
			vertexBuffer.put(a);
			
	        vertexBuffer.put((float)(x + radius * Math.sin(angle)));
	        vertexBuffer.put((float)(y + radius * Math.cos(angle)));
	        
	        vertexBuffer.put(r);
			vertexBuffer.put(g);
			vertexBuffer.put(b);
			vertexBuffer.put(a);
			
	        vertexBuffer.put((float)(x + radius * Math.sin(angle + angleIncr)));
	        vertexBuffer.put((float)(y + radius * Math.cos(angle + angleIncr)));
	        
	        vertexBuffer.put(r);
			vertexBuffer.put(g);
			vertexBuffer.put(b);
			vertexBuffer.put(a);
			
	        vertexBuffer.put((float)x);
	        vertexBuffer.put((float)y);
	        angle += angleIncr;
        }
	}

	@Override
	public double getRadius() {
		return radius;
	}

	@Override
	public boolean contains(double x, double y) {
		return x*x + y*y <= radius*radius;
	}

	@Override
	public int getNumTriangles() {
		return Math.max((int)radius*2,6);
	}

}
