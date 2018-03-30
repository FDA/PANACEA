package com.eng.cber.na.gl.renderer;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Collection;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import com.eng.cber.na.gl.GLRenderContext;
import com.eng.cber.na.gl.shape.GLShape;
import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.graph.GeneralNode;
import com.eng.cber.na.layout.Layout;
import com.jogamp.common.nio.Buffers;

import edu.uci.ics.jung.graph.Graph;

/**
 * A class that renders vertices using OpenGL methods.
 * 
 * Vertices are rendered through drawing a set of triangles
 * that are offset by angles from each other.  Circles
 * are comprised of a large number of triangles -- a high-
 * dimensional polygon looks round.
 * 
 */
public class NetworkGLVertexRenderer<V,E> implements GLRenderer.VertexRenderer<V,E>{

	private FloatBuffer vertexBuffer = null;
	private IntBuffer indexBuffer = null;
	
	@Override
	public void renderVertices(GLRenderContext<V, E> glRenderContext, Layout<V, E> layout) {
		GLAutoDrawable glAutoDrawable = glRenderContext.getGLAutoDrawable();
		GL2 gl2 = glAutoDrawable.getGL().getGL2();
		
		Graph<V,E> g = layout.getGraph();
		Collection<V> vertices = g.getVertices();
		
		// Determine the total number of triangles we need to render
		int vertTriangleCount = 0;
		if(vertices.isEmpty())
			return;
		
		for(V v : vertices) {
			boolean display = ((GeneralGraph)g).getNodeDisplay((GeneralNode)v);
			if (!display)
				continue;
			GLShape shape = glRenderContext.getVertexShapeTransformer().transform(v);
			vertTriangleCount += shape.getNumTriangles();
		}
		
		// Make a buffer to hold the triangles if one does not already exist
		if(vertexBuffer == null || indexBuffer == null || vertexBuffer.limit() != 18 * vertTriangleCount) {
			if(vertexBuffer != null) {
				vertexBuffer.clear();
			}
			vertexBuffer = Buffers.newDirectFloatBuffer(18 * vertTriangleCount);
			if(indexBuffer != null) {
				indexBuffer.clear();
			}
			indexBuffer = Buffers.newDirectIntBuffer(2*vertTriangleCount);
			for(int i = 0; i < vertTriangleCount; i++) {
				indexBuffer.put(2+9*i);
				indexBuffer.put(5+9*i);			
			}		
		}

		vertexBuffer.rewind();
		
		// Load vertices and colors into the vertex buffer
		for(V v : vertices) {
			boolean display = ((GeneralGraph)g).getNodeDisplay((GeneralNode)v);
			if (!display)
				continue;
			
			Color fillColor = (Color)glRenderContext.getVertexFillPaintTransformer().transform(v);
			Color boundColor = (Color)glRenderContext.getVertexDrawPaintTransformer().transform(v);
			GLShape shape = glRenderContext.getVertexShapeTransformer().transform(v);
			
			Point2D p = layout.transform(v);		
			p = glRenderContext.getMultiLayerTransformer().transform(p);
			
			shape.render(gl2, p.getX(), glAutoDrawable.getHeight() - p.getY(), fillColor, boundColor, vertexBuffer);
		}
		
		vertexBuffer.rewind();
				
		gl2.glEnable(GL2.GL_BLEND);
		gl2.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		
		//This bit renders a black outline surrounding each vertex.
		//First we render the lines with a width of 2 (part of this width will get covered over 
		//when coloring in the vertex).
		vertexBuffer.rewind();
		indexBuffer.rewind();
		
		gl2.glColor4f(0.0f, 0.0f, 0.0f, 0.5f); // Add some transparency
		gl2.glLineWidth(2.0f);
		gl2.glVertexPointer(2, GL2.GL_FLOAT, 0, vertexBuffer);

		gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl2.glDrawElements(GL2.GL_LINES, indexBuffer.capacity(), GL2.GL_UNSIGNED_INT, indexBuffer);
		gl2.glDisableClientState(GL2.GL_VERTEX_ARRAY);	

		//Drawing the lines will not color the points at each corner. We draw those
		//separately on this next iteration (also drawn with a point size of 2 to account for
		//being colored over later).
		vertexBuffer.rewind();
		indexBuffer.rewind();
		
		gl2.glColor4f(0.0f, 0.0f, 0.0f, 0.01f); // Add some transparency
		gl2.glPointSize(2.0f);
		gl2.glVertexPointer(2, GL2.GL_FLOAT, 0, vertexBuffer);

		gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl2.glDrawElements(GL2.GL_POINTS, indexBuffer.capacity(), GL2.GL_UNSIGNED_INT, indexBuffer);
		gl2.glDisableClientState(GL2.GL_VERTEX_ARRAY);	
		

		
		//Color in vertices.
		gl2.glColorPointer(4,GL2.GL_FLOAT, 6*Buffers.SIZEOF_FLOAT, vertexBuffer.position(0));
		gl2.glVertexPointer(2,GL2.GL_FLOAT, 6*Buffers.SIZEOF_FLOAT, vertexBuffer.position(4));

		gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl2.glEnableClientState(GL2.GL_COLOR_ARRAY);		
		gl2.glDrawArrays(GL2.GL_TRIANGLES, 0, 3*vertTriangleCount);		
		gl2.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		gl2.glDisableClientState(GL2.GL_COLOR_ARRAY);

		gl2.glDisable(GL2.GL_BLEND);

	}
}
