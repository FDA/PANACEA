package com.eng.cber.na.graphevent;

import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseMotionListener;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import com.eng.cber.na.gl.GLRenderable;

/**
 * Abstract class for mouse listeners on the graph canvas.
 * Includes general implementations for multi-select feedback 
 * on the screen through drawing a dotted rectangle around 
 * the multi-select area.
 *
 */
public abstract class AbstractSelectDragGraphMouseEvent extends AbstractGraphMouseEvent implements MouseMotionListener {

	protected Point start, p;
	protected RectangleRenderable rectRenderable = new RectangleRenderable();
	protected boolean multiSelecting = false;
	
	protected AbstractSelectDragGraphMouseEvent() {
		super(InputEvent.BUTTON1_MASK);
	}
	
	class RectangleRenderable implements GLRenderable {
		@Override
		public void render(GLAutoDrawable glAutoDrawable) {
			GL2 gl2 = glAutoDrawable.getGL().getGL2();
			gl2.glColor3f(0.0f, 0.0f, 0.0f);
			float oldLineWidth[] = new float[1];
			gl2.glGetFloatv(GL2.GL_LINE_WIDTH, oldLineWidth, 0);
			gl2.glLineWidth(1.0f);
					
	        gl2.glBegin(GL.GL_LINES);	        
	    	dashedSegment(glAutoDrawable,start.getX(),start.getY(),p.getX(),start.getY());	        
	    	dashedSegment(glAutoDrawable,start.getX(),p.getY(),p.getX(),p.getY());	    			
	    	dashedSegment(glAutoDrawable,start.getX(),start.getY(),start.getX(),p.getY());	    	
	    	dashedSegment(glAutoDrawable,p.getX(),start.getY(),p.getX(),p.getY());
	        gl2.glEnd();  
	        
	        gl2.glLineWidth(oldLineWidth[0]);
		}		
		
		public void dashedSegment(GLAutoDrawable glAutoDrawable, double x1, double y1, double x2, double y2) {
			GL2 gl2 = glAutoDrawable.getGL().getGL2();
			
			int dash_size = 5;
			double dash_num = Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1)) / (2 * dash_size);
			
			double dx = (x2 - x1) / dash_num;
			double dy = (y2 - y1) / dash_num;
			
			for(double i = 0; i < dash_num; i++) {
				gl2.glVertex2d(x1 + dx * i, glAutoDrawable.getHeight() - (y1 + dy * i));
				gl2.glVertex2d(x2 - x1 > 0 ? Math.min(x1 + dx * (i + 0.5), x2) : Math.max(x1 + dx * (i + 0.5), x2), 
						glAutoDrawable.getHeight() - (y2 - y1 > 0 ? Math.min(y1 + dy * (i + 0.5), y2) : Math.max(y1 + dy * (i + 0.5), y2)));
			}
		}
		
		public void solidSegment(GLAutoDrawable glAutoDrawable, double x1, double y1, double x2, double y2) {
			GL2 gl2 = glAutoDrawable.getGL().getGL2();
			gl2.glVertex2d(x1, glAutoDrawable.getHeight() - y1);
	    	gl2.glVertex2d(x2, glAutoDrawable.getHeight() - y2); 
		}
	}
}
