package com.eng.cber.na.gl.renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.nio.FloatBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.gl.GLRenderContext;
import com.eng.cber.na.gl.renderer.GLRenderer.EdgeRenderer;
import com.eng.cber.na.graph.GeneralEdge;
import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.layout.Layout;
import com.eng.cber.na.transformer.EdgeDrawPaintTransformer;
import com.jogamp.common.nio.Buffers;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;


/**
 * A class that renders edges using OpenGL methods.
 * 
 * Rendering is done by filling a buffer with lines to draw.  This
 * does not allow you to specify individual widths for each edge,
 * so edge width is achieved by drawing multiple lines offset by
 * one pixel.  
 * 
 * Edges may be hidden (not drawn) if there are more onscreen than
 * the maximum edges set to display.  Selected edges receive the
 * highest priority for rendering, and the renderer also attempts
 * to show at least one edge for each node, to avoid creating the
 * illusion of isolates.
 */
public class NetworkGLEdgeRenderer<V,E> implements EdgeRenderer<V, E> {
	
	private FloatBuffer vertexBuffer = null;
    private int linesToDrawAtOnce = 1000000; // Must be greater than 10
	
	@Override
	public void renderEdges(GLRenderContext<V, E> glRenderContext, Layout<V, E> layout) {
		
		GLAutoDrawable glAutoDrawable = glRenderContext.getGLAutoDrawable();
		GL2 gl2 = glAutoDrawable.getGL().getGL2();
		
		Rectangle viewSize = new Rectangle(glAutoDrawable.getWidth(), glAutoDrawable.getHeight());
		
		Graph<V,E> graph = (Graph<V, E>) layout.getGraph();
		Collection<E> allEdges = graph.getEdges();
		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
		
		linesToDrawAtOnce = nv.getLineToDrawAtOnceSize(); // Must be greater than 10
		
		HashSet<V> nodesWithARenderedEdge = new HashSet<V>();
		ArrayDeque<E> edgesForRender = new ArrayDeque<E>();
		ArrayList<E> highPriorityEdges = new ArrayList<E>();
		NetworkAnalysisVisualization.NALog("Rendering Edges... " );
		for(E e:allEdges){
			// First check if edge should even be displayed or not.
			if (!((GeneralGraph)graph).getEdgeDisplay((GeneralEdge)e))
				continue;
			
	        boolean onScreen = false;
	        Pair<V> endpoints = graph.getEndpoints(e);
	        V v1 = endpoints.getFirst();
	        V v2 = endpoints.getSecond();
	        Point2D p1 = layout.transform(v1);        
	        p1 = glRenderContext.getMultiLayerTransformer().transform(p1);
	        Point2D p2 = layout.transform(v2);
	        p2 = glRenderContext.getMultiLayerTransformer().transform(p2);
	        
	        // Check if either endpoint is within the view of the main screen
	        if (p1.getX() >= 0 && p1.getX() <= viewSize.getWidth() && p1.getY() >= 0 && p1.getY() <= viewSize.getHeight()) {
	        	onScreen = true;
	        }
	        else if (p2.getX() >= 0 && p2.getX() <= viewSize.getWidth() && p2.getY() >= 0 && p2.getY() <= viewSize.getHeight()) {
	        	onScreen = true;
	        }
	        // If necessary, check if the line passes across some part of the view
	        else if (viewSize.intersectsLine(p1.getX(), p1.getY(), p2.getX(), p2.getY())) {
	        	onScreen = true;
	        }
	        
	        if (!onScreen)
	        	continue;
	        
	        // If it passes all tests, then the line should be rendered
	        // (although the number of lines to render may still be
	        // reduced to fit the maxEdgeToDisplay parameter).
	        
	        // Edges that are selected will be the highest priority edges.
			Color color = (Color)glRenderContext.getEdgeDrawPaintTransformer().transform(e);
			if (color.equals(EdgeDrawPaintTransformer.edgePickedColor)) {
				highPriorityEdges.add(e);
			}
			else {
				// Edges that connect to a node that doesn't yet have a rendered edge will be
				// placed at the higher priority end (front) of the ArrayDeque.
				if (!nodesWithARenderedEdge.contains(v1) || !nodesWithARenderedEdge.contains(v2)) {
					edgesForRender.addFirst(e);
					nodesWithARenderedEdge.add(v1);
					nodesWithARenderedEdge.add(v2);
				}
				else {
					edgesForRender.addLast(e);
				}
			}
		}

		
		// Put the selected edges in the very front of the ArrayDeque
		for (E e : highPriorityEdges) {
			edgesForRender.addFirst(e);
		}

		// Cut down to a final edge list if necessary
		int maxEdgeToDisplay = Math.min(edgesForRender.size(), nv.getMaxEdgeSizeToDisplay());
		int initialSize = edgesForRender.size();
		for (int i = 0; i < initialSize - maxEdgeToDisplay; i++) {
			edgesForRender.removeLast();
		}
		List<E> finalEdgeList = new ArrayList<E>(edgesForRender);
		
		nv.setNumHiddenEdges(initialSize - maxEdgeToDisplay);

		// Edges that are rendered last appear "on top" of other edges, so make sure
		// selected edges are moved to the end of the list.
		Collections.reverse(finalEdgeList);

		
		// Add line smoothing and transparency
		gl2.glEnable(GL.GL_LINE_SMOOTH);
		gl2.glEnable(GL.GL_BLEND);
		gl2.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		
		// Get the weight (line width) that should be applied for each edge.
		// This must be done in a separate loop because we have to know the
		// total number of lines that will be drawn (which is the sum of all
		// of the edge weights) before starting the main loop.  The OpenGL
		// rendering uses Buffer objects that must have a predetermined size.
		int totalWeight = 0;
		ArrayList<Integer> lineWeights = new ArrayList<Integer>(finalEdgeList.size());

		for (E e : finalEdgeList) {
				
			BasicStroke stroke = (BasicStroke)glRenderContext.getEdgeStrokeTransformer().transform(e);
			int width = Math.min(10, (int) stroke.getLineWidth());
			totalWeight += width;
			lineWeights.add(width);
		}
		
		// Note the difference between number of edges and number of lines.
		// An edge with line width > 1 will be drawn as multiple lines.
        int finishedEdges = 0;
        if(linesToDrawAtOnce <=10)
        	linesToDrawAtOnce  = 11;
        
		// The max edge weight is 10, so we have to ensure there is always room to add one more edge in each render before adding it in.
        int numRenderingsRequired = (totalWeight / (linesToDrawAtOnce - 10)) + 1;
        
		NetworkAnalysisVisualization.NALog("Number of renderings: " + numRenderingsRequired);
        
		for(int renderIndex = 0; renderIndex < numRenderingsRequired; renderIndex++) {
			
			if (finishedEdges >= finalEdgeList.size())
				break;
			
			int i = 0;
			int weightInThisRender = 0;
			while (weightInThisRender < linesToDrawAtOnce - 10) {
				if (finishedEdges + i >= finalEdgeList.size())
					break;
				
				int width = lineWeights.get(finishedEdges + i); 
				weightInThisRender += width;
				i++;
			}
			int numEdgesInThisRender = i;
				
			
	        if(vertexBuffer == null || vertexBuffer.limit() != 12 * weightInThisRender) {
				if(vertexBuffer != null) {
					vertexBuffer.clear();
				}
				vertexBuffer = Buffers.newDirectFloatBuffer(12 * weightInThisRender);
	        }
	        
	        vertexBuffer.rewind();
	        
	        
	        for (int j = 0; j < numEdgesInThisRender; j++) {
	        	if (finishedEdges + j >= finalEdgeList.size())
	        		break;
	        	
	        	E e = finalEdgeList.get(finishedEdges + j);

				Color color = (Color)glRenderContext.getEdgeDrawPaintTransformer().transform(e);
				float width = lineWeights.get(finishedEdges + j);

				
		        Pair<V> endpoints = graph.getEndpoints(e);
		        V v1 = endpoints.getFirst();
		        V v2 = endpoints.getSecond();
		        
		        Point2D p1 = layout.transform(v1);        
		        p1 = glRenderContext.getMultiLayerTransformer().transform(p1);
		        
		        Point2D p2 = layout.transform(v2);
		        p2 = glRenderContext.getMultiLayerTransformer().transform(p2);
				
				int height = glAutoDrawable.getHeight();
				
		        float x1 = (float) p1.getX();
				float y1 = (float) p1.getY();
				float x2 = (float) p2.getX();
				float y2 = (float) p2.getY();
				
				float r = (float)color.getRed()/255;
				float g = (float)color.getGreen()/255;
				float b = (float)color.getBlue()/255;
				float a = (float)color.getAlpha()/255;
					
				// Draw multiple lines offset by one pixel if line width > 1
				// Offset in y-dimension if line is longest in x-dimension and vice versa
				boolean longerInX = Math.abs(x2 - x1) > Math.abs(y2 -y1);
				for (int k = 0; k < width; k++) {
					if (longerInX) {
						if (k % 2 == 0) {
					        vertexBuffer.put(r);
					        vertexBuffer.put(g);
					        vertexBuffer.put(b);
					        vertexBuffer.put(a);
					        vertexBuffer.put(x1);
					        vertexBuffer.put(height - (y1 + k/2));
					        
					        vertexBuffer.put(r);
					        vertexBuffer.put(g);
					        vertexBuffer.put(b);
					        vertexBuffer.put(a);
					        vertexBuffer.put(x2);
					        vertexBuffer.put(height - (y2 + k/2));
						}
						else {
							vertexBuffer.put(r);
					        vertexBuffer.put(g);
					        vertexBuffer.put(b);
					        vertexBuffer.put(a);
					        vertexBuffer.put(x1);
					        vertexBuffer.put(height - (y1 - k/2));
					        
					        vertexBuffer.put(r);
					        vertexBuffer.put(g);
					        vertexBuffer.put(b);
					        vertexBuffer.put(a);
					        vertexBuffer.put(x2);
					        vertexBuffer.put(height - (y2 - k/2));
						}
					}
					else {
						if (j % 2 == 0) {
					        vertexBuffer.put(r);
					        vertexBuffer.put(g);
					        vertexBuffer.put(b);
					        vertexBuffer.put(a);
					        vertexBuffer.put(x1 + k/2);
					        vertexBuffer.put(height - y1);
					        
					        vertexBuffer.put(r);
					        vertexBuffer.put(g);
					        vertexBuffer.put(b);
					        vertexBuffer.put(a);
					        vertexBuffer.put(x2 + k/2);
					        vertexBuffer.put(height - y2);
						}
						else {
							vertexBuffer.put(r);
					        vertexBuffer.put(g);
					        vertexBuffer.put(b);
					        vertexBuffer.put(a);
					        vertexBuffer.put(x1 - k/2);
					        vertexBuffer.put(height - y1);
					        
					        vertexBuffer.put(r);
					        vertexBuffer.put(g);
					        vertexBuffer.put(b);
					        vertexBuffer.put(a);
					        vertexBuffer.put(x2 - k/2);
					        vertexBuffer.put(height - y2);
						}
					}
				}
	
			}
	        
	        
			vertexBuffer.rewind();
			
			// The actual rendering code, using the assembled buffer
			gl2.glColorPointer(4,GL2.GL_FLOAT, 6*Buffers.SIZEOF_FLOAT, vertexBuffer.position(0));
			gl2.glVertexPointer(2,GL2.GL_FLOAT, 6*Buffers.SIZEOF_FLOAT, vertexBuffer.position(4));
	
			gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY);
			gl2.glEnableClientState(GL2.GL_COLOR_ARRAY);
			gl2.glDrawArrays(GL2.GL_LINES, 0, 2*weightInThisRender);		
			gl2.glDisableClientState(GL2.GL_VERTEX_ARRAY);
			gl2.glDisableClientState(GL2.GL_COLOR_ARRAY);
		
	        finishedEdges += numEdgesInThisRender;

		}
		NetworkAnalysisVisualization.NALog("Edge rendering finished." );
        gl2.glDisable(GL.GL_BLEND);
        gl2.glDisable(GL.GL_LINE_SMOOTH);
	}

	private class EdgeWeightComparator<E> implements Comparator<E> {
		@Override
		public int compare(E e1, E e2) {
			double w1 = ((GeneralEdge) e1).getWeight();
			double w2 = ((GeneralEdge) e2).getWeight();
			if (w1 < w2) 
				return 1;
			else if (w1 > w2)
				return -1;
			else
				return 0;
		}
	}
	
}
