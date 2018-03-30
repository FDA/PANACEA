package com.eng.cber.na.gl.renderer;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.annotations.NetworkGLAnnotation;
import com.eng.cber.na.gl.GLRenderContext;
import com.eng.cber.na.gl.shape.GLRectangle;
import com.eng.cber.na.layout.Layout;
import com.jogamp.common.nio.Buffers;

import edu.uci.ics.jung.visualization.Layer;

/**
 * Rendering class for shape annotations.
 * 
 */

public class NetworkGLAnnotationShapeRenderer<V,E> implements GLRenderer.AnnotationShapeRenderer<V,E> {

	private FloatBuffer vertexBuffer = null;
	
	@Override
	public void renderShapeAnnotations(GLRenderContext<V, E> glRenderContext, Layout<V, E> layout) {
		//Go get the annotations first
		ArrayList<NetworkGLAnnotation> shapeAnnotations = layout.getNetworkGLAnnotationManager().getShapeAnnotations();
		if (shapeAnnotations.isEmpty()) {
			return;
		}
		
		
		GLAutoDrawable glAutoDrawable = glRenderContext.getGLAutoDrawable();
		GL2 gl2 = glAutoDrawable.getGL().getGL2();
		
		// Get total count of lines and points (4 for each rectangle)
		int numberOfLines = 4 * shapeAnnotations.size();
		int numberOfPoints = numberOfLines;
		
		// Make a buffer to hold the colors and vertices if one does not already exist
		if(vertexBuffer == null || vertexBuffer.limit() != 10 * numberOfLines) {
			if(vertexBuffer != null) {
				vertexBuffer.clear();
			}
			vertexBuffer = Buffers.newDirectFloatBuffer(10 * numberOfLines);
		}

		vertexBuffer.rewind();
		
		// Load vertices and colors into the vertex buffer
		for(NetworkGLAnnotation ann : shapeAnnotations) {
			Color boundColor = ann.getColor();
			GLRectangle rectangle = (GLRectangle) ann.getAnnotation();	
			double rectangleOrientation = rectangle.getCurrentOrientation();

			// Scale size of rectangle.  The LAYOUT and VIEW scales
			// represent the zoom level.  Only one is used at a time
			// though; the other scale will be 1.0
			double layoutScale = NetworkAnalysisVisualization.getInstance().getNetworkGLVisualizationServer().getGLRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).getScale();
			double viewScale = NetworkAnalysisVisualization.getInstance().getNetworkGLVisualizationServer().getGLRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).getScale();
			GLRectangle scaledRectangle;
			if (layoutScale > 1.0001){
				scaledRectangle = new GLRectangle(layoutScale*rectangle.getXRadius(), layoutScale*rectangle.getYRadius());
				scaledRectangle.setCurrentOrientation(rectangleOrientation);
			}
			else if (viewScale < 0.9999) {
				scaledRectangle = new GLRectangle(viewScale*rectangle.getXRadius(), viewScale*rectangle.getYRadius());
				scaledRectangle.setCurrentOrientation(rectangleOrientation);
			}
			else {
				scaledRectangle = rectangle;
			}
			
			Point2D p = ann.getLocation();
			p = glRenderContext.getMultiLayerTransformer().transform(p);
			
			scaledRectangle.render(gl2, p.getX(), glAutoDrawable.getHeight() - p.getY(), boundColor, vertexBuffer);
		}
					
		//Set rendering options
		float oldLineWidth[] = new float[1];
		gl2.glGetFloatv(GL2.GL_LINE_WIDTH, oldLineWidth, 0);
		gl2.glLineWidth(1.0f);
		gl2.glEnable(GL2.GL_LINE_SMOOTH);
		
		
		//This bit renders an outline for the rectangle.
		vertexBuffer.rewind();
		
		gl2.glColorPointer(3,GL2.GL_FLOAT, 5*Buffers.SIZEOF_FLOAT, vertexBuffer.position(0));
		gl2.glVertexPointer(2,GL2.GL_FLOAT, 5*Buffers.SIZEOF_FLOAT, vertexBuffer.position(3));

		gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl2.glEnableClientState(GL2.GL_COLOR_ARRAY);		
		gl2.glDrawArrays(GL2.GL_LINES, 0, 2*numberOfLines);	
		gl2.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		gl2.glDisableClientState(GL2.GL_COLOR_ARRAY);
		

		//Render a point at each corner to make sure it's all filled in.
		vertexBuffer.rewind();
		
		gl2.glColorPointer(3,GL2.GL_FLOAT, 10*Buffers.SIZEOF_FLOAT, vertexBuffer.position(0));
		gl2.glVertexPointer(2,GL2.GL_FLOAT, 10*Buffers.SIZEOF_FLOAT, vertexBuffer.position(3));

		gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl2.glEnableClientState(GL2.GL_COLOR_ARRAY);		
		gl2.glDrawArrays(GL2.GL_POINTS, 0, 1*numberOfPoints);	
		gl2.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		gl2.glDisableClientState(GL2.GL_COLOR_ARRAY);
		
		
		//Unset rendering options
		gl2.glLineWidth(oldLineWidth[0]);
		gl2.glDisable(GL2.GL_LINE_SMOOTH);
	}
}