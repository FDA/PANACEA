package com.eng.cber.na.gl.renderer;

import java.util.ArrayList;
import java.util.Collection;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.fixedfunc.GLMatrixFunc;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.annotations.NetworkGLAnnotation;
import com.eng.cber.na.gl.GLRenderContext;
import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.graph.GeneralNode;
import com.eng.cber.na.layout.Layout;

import edu.uci.ics.jung.graph.Graph;


/**
 * A class that calls on other classes to render edges, vertices,
 * labels, and annotations on the rendering context.
 * 
 *
 */
public class NetworkGLRenderer<V,E> implements GLRenderer<V, E> {
	
	public NetworkGLRenderer(GLAutoDrawable glAutoDrawable) {
		
	}
	
	@Override
	public void render(GLRenderContext<V,E> glRenderContext, Layout<V, E> layout) {
		Graph<V,E> g = layout.getGraph();
		
		GLAutoDrawable glAutoDrawable = glRenderContext.getGLAutoDrawable();
		GL2 gl2 = glAutoDrawable.getGL().getGL2();
		gl2.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl2.glLoadIdentity();
		
        if(NetworkAnalysisVisualization.startLogging)
        	NetworkAnalysisVisualization.logger.logp(java.util.logging.Level.INFO,"","","Start rendering..." );
		EdgeRenderer<V,E> edgeRenderer = glRenderContext.getEdgeRenderer();
		edgeRenderer.renderEdges(glRenderContext, layout);
		
		if(NetworkAnalysisVisualization.getInstance().shouldShowLabels()){
			VertexLabelRenderer<V,E> vertexLabelRenderer = glRenderContext.getVertexLabelRenderer();
			Collection<V> vertices = g.getVertices();
			
			for(V v : vertices) {
				if (((GeneralGraph)g).getNodeDisplay((GeneralNode)v))
					vertexLabelRenderer.renderVertexLabel(glRenderContext, layout, v);
			}
		}
		
		VertexRenderer<V,E> vertexRenderer = glRenderContext.getVertexRenderer();
		vertexRenderer.renderVertices(glRenderContext, layout);
	
		AnnotationTextRenderer<V,E> annotationTextRenderer = glRenderContext.getAnnotationTextRenderer();
		ArrayList<NetworkGLAnnotation> textAnnotations = layout.getNetworkGLAnnotationManager().getTextAnnotations();
		for (NetworkGLAnnotation currentTextAnnotation : textAnnotations) {
			annotationTextRenderer.renderTextAnnotations(glRenderContext, layout, currentTextAnnotation);
		}
		
		AnnotationShapeRenderer<V,E> annotationShapeRenderer = glRenderContext.getAnnotationShapeRenderer();
		annotationShapeRenderer.renderShapeAnnotations(glRenderContext, layout);
        if(NetworkAnalysisVisualization.startLogging)
        	NetworkAnalysisVisualization.logger.logp(java.util.logging.Level.INFO,"","","Rendering done." );
	}

}
