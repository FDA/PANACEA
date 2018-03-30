package com.eng.cber.na.gl.shape;

import java.awt.Color;
import java.nio.FloatBuffer;

import javax.media.opengl.GL2;

/**
 * This class is an OpenGL-renderable shape that forms a
 * rectangle outline.  It places the vertices to make four
 * lines and their colors into a buffer.  The buffer looks like:<br/><br/>
 * <verbatim>
 * [r1,g1,b1,x1,y1,r1,g1,b1,x2,y2, r2,g2,b2,x2,y2,r2,g2,b2,x3,y3, ... r4,g4,b4,x4,y4,r4,g4,b4,x1,y1]
 *  -----------------------------  -----------------------------      -----------------------------
 *         The top line                 The right side line       ...      The left side line
 * </verbatim><br/><br/>
 * where r,g,b is the color and is currently fixed for all
 * four sides of this rectangle.
 *
 * Note that GLRectangle does not extend GLShape.
 */

public class GLRectangle {

		private double xRadius,yRadius;
		private double currentOrientation;
		
		public GLRectangle(double xRadius, double yRadius) {
			this.xRadius = xRadius;
			this.yRadius = yRadius;
			currentOrientation = 0;
		}
		
		public void render(GL2 gl2, double x, double y, Color outlineColor, FloatBuffer vertexBuffer) {
			
			
			float r = (float)outlineColor.getRed()/255;
			float g = (float)outlineColor.getGreen()/255;
			float b = (float)outlineColor.getBlue()/255;
			
			vertexBuffer.put(r);
			vertexBuffer.put(g);
			vertexBuffer.put(b);
			
			vertexBuffer.put((float)(x + (-xRadius)*Math.cos(currentOrientation) - yRadius*Math.sin(currentOrientation))); //x1
			vertexBuffer.put((float)(y + (-xRadius)*Math.sin(currentOrientation) + yRadius*Math.cos(currentOrientation))); //y1
			
			vertexBuffer.put(r);
			vertexBuffer.put(g);
			vertexBuffer.put(b);
			
			vertexBuffer.put((float)(x + xRadius*Math.cos(currentOrientation) - yRadius*Math.sin(currentOrientation))); //x2
			vertexBuffer.put((float)(y + xRadius*Math.sin(currentOrientation) + yRadius*Math.cos(currentOrientation))); //y2

			vertexBuffer.put(r);
			vertexBuffer.put(g);
			vertexBuffer.put(b);
			
			vertexBuffer.put((float)(x + xRadius*Math.cos(currentOrientation) - yRadius*Math.sin(currentOrientation))); //x2
			vertexBuffer.put((float)(y + yRadius*Math.cos(currentOrientation) + xRadius*Math.sin(currentOrientation))); //y2
			
			vertexBuffer.put(r);
			vertexBuffer.put(g);
			vertexBuffer.put(b);
			
			vertexBuffer.put((float)(x + xRadius*Math.cos(currentOrientation) - (-yRadius)*Math.sin(currentOrientation))); //x3
			vertexBuffer.put((float)(y + xRadius*Math.sin(currentOrientation) + (-yRadius)*Math.cos(currentOrientation))); //y3
			
			vertexBuffer.put(r);
			vertexBuffer.put(g);
			vertexBuffer.put(b);
			
			vertexBuffer.put((float)(x + xRadius*Math.cos(currentOrientation) - (-yRadius)*Math.sin(currentOrientation))); //x3
			vertexBuffer.put((float)(y + xRadius*Math.sin(currentOrientation) + (-yRadius)*Math.cos(currentOrientation))); //y3
			
			vertexBuffer.put(r);
			vertexBuffer.put(g);
			vertexBuffer.put(b);
			
			vertexBuffer.put((float)(x + (-xRadius)*Math.cos(currentOrientation) - (-yRadius)*Math.sin(currentOrientation))); //x4
			vertexBuffer.put((float)(y + (-xRadius)*Math.sin(currentOrientation) + (-yRadius)*Math.cos(currentOrientation))); //y4
			
			vertexBuffer.put(r);
			vertexBuffer.put(g);
			vertexBuffer.put(b);
			
			vertexBuffer.put((float)(x + (-xRadius)*Math.cos(currentOrientation) - (-yRadius)*Math.sin(currentOrientation))); //x4
			vertexBuffer.put((float)(y + (-xRadius)*Math.sin(currentOrientation) + (-yRadius)*Math.cos(currentOrientation))); //y4
			
			vertexBuffer.put(r);
			vertexBuffer.put(g);
			vertexBuffer.put(b);
			
			vertexBuffer.put((float)(x + (-xRadius)*Math.cos(currentOrientation) - yRadius*Math.sin(currentOrientation))); //x1
			vertexBuffer.put((float)(y + (-xRadius)*Math.sin(currentOrientation) + yRadius*Math.cos(currentOrientation))); //y1
			
		}

		public double getXRadius() {
			return xRadius;
		}
		
		public double getYRadius() {
			return yRadius;
		}
		
		public void rotateClockwise(double angleInRadians) {
			currentOrientation -= angleInRadians;
		}
		
		public void rotateCounterClockwise(double angleInRadians) {
			currentOrientation -= angleInRadians;
		}
		
		public double getCurrentOrientation() {
			return currentOrientation;
		}
		
		public void setCurrentOrientation(double orientation) {
			this.currentOrientation = orientation;
		}

		public void scale(double scaleFactor) {
			xRadius *= scaleFactor;
			yRadius *= scaleFactor;
		}
}
