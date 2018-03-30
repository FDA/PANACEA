package com.eng.cber.na.gl.shape;

import java.awt.Color;
import java.nio.FloatBuffer;

import javax.media.opengl.GL2;

/**
 * This class is a OpenGL-renderable shape that forms a
 * square. It knows how to render itself as a square
 * through putting the necessary information into the buffers --
 * many four triangles combine, with the angle incremented
 * slightly each time to ensure they form a square.
 *
 */
public class GLSquare implements GLShape {

	private double radius;
	
	public GLSquare(double radius) {
		this.radius = radius;
	}
	
	@Override
	public void render(GL2 gl2, double x, double y, Color fillColor, Color boundColor, FloatBuffer vertexBuffer) {
		
		
		float r = (float)fillColor.getRed()/255;
		float g = (float)fillColor.getGreen()/255;
		float b = (float)fillColor.getBlue()/255;
		float a = (float)fillColor.getAlpha()/255;
		
		vertexBuffer.put(r);
		vertexBuffer.put(g);
		vertexBuffer.put(b);
		vertexBuffer.put(a);
		
		vertexBuffer.put((float)(x - radius));
		vertexBuffer.put((float)(y + radius));
		
		vertexBuffer.put(r);
		vertexBuffer.put(g);
		vertexBuffer.put(b);
		vertexBuffer.put(a);
		
		vertexBuffer.put((float)(x + radius));
		vertexBuffer.put((float)(y + radius));

		vertexBuffer.put(r);
		vertexBuffer.put(g);
		vertexBuffer.put(b);
		vertexBuffer.put(a);
		
		vertexBuffer.put((float)x);
		vertexBuffer.put((float)y);
		
		vertexBuffer.put(r);
		vertexBuffer.put(g);
		vertexBuffer.put(b);
		vertexBuffer.put(a);
		
		vertexBuffer.put((float)(x + radius));
		vertexBuffer.put((float)(y + radius));
		
		vertexBuffer.put(r);
		vertexBuffer.put(g);
		vertexBuffer.put(b);
		vertexBuffer.put(a);
		
		vertexBuffer.put((float)(x + radius));
		vertexBuffer.put((float)(y - radius));
		
		vertexBuffer.put(r);
		vertexBuffer.put(g);
		vertexBuffer.put(b);
		vertexBuffer.put(a);
		
		vertexBuffer.put((float)x);
		vertexBuffer.put((float)y);
		
		vertexBuffer.put(r);
		vertexBuffer.put(g);
		vertexBuffer.put(b);
		vertexBuffer.put(a);
		
		vertexBuffer.put((float)(x + radius));
		vertexBuffer.put((float)(y - radius));
		
		vertexBuffer.put(r);
		vertexBuffer.put(g);
		vertexBuffer.put(b);
		vertexBuffer.put(a);
		
		vertexBuffer.put((float)(x - radius));
		vertexBuffer.put((float)(y - radius));

		vertexBuffer.put(r);
		vertexBuffer.put(g);
		vertexBuffer.put(b);
		vertexBuffer.put(a);
		
		vertexBuffer.put((float)x);
		vertexBuffer.put((float)y);
		
		vertexBuffer.put(r);
		vertexBuffer.put(g);
		vertexBuffer.put(b);
		vertexBuffer.put(a);
		
		vertexBuffer.put((float)(x - radius));
		vertexBuffer.put((float)(y - radius));
		
		vertexBuffer.put(r);
		vertexBuffer.put(g);
		vertexBuffer.put(b);
		vertexBuffer.put(a);
		
		vertexBuffer.put((float)(x - radius));
		vertexBuffer.put((float)(y + radius));

		vertexBuffer.put(r);
		vertexBuffer.put(g);
		vertexBuffer.put(b);
		vertexBuffer.put(a);
		
		vertexBuffer.put((float)x);
		vertexBuffer.put((float)y);
	}

	@Override
	public double getRadius() {
		return radius;
	}

	@Override
	public boolean contains(double x, double y) {
		return x >= -radius && x <= radius && y >= -radius && y <= radius ? true : false;
	}

	@Override
	public int getNumTriangles() {
		return 4;
	}

}
