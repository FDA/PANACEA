package com.eng.cber.na.gl;

import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;

import javax.media.opengl.GLAutoDrawable;

import org.apache.commons.collections15.Transformer;

import com.eng.cber.na.gl.renderer.GLRenderer.AnnotationShapeRenderer;
import com.eng.cber.na.gl.renderer.GLRenderer.AnnotationTextRenderer;
import com.eng.cber.na.gl.renderer.GLRenderer.EdgeLabelRenderer;
import com.eng.cber.na.gl.renderer.GLRenderer.EdgeRenderer;
import com.eng.cber.na.gl.renderer.GLRenderer.VertexLabelRenderer;
import com.eng.cber.na.gl.renderer.GLRenderer.VertexRenderer;
import com.eng.cber.na.gl.shape.GLShape;
import com.eng.cber.na.graph.GeneralEdge;
import com.eng.cber.na.graph.GeneralNode;
import com.eng.cber.na.transformer.MultiLayerTransformer;

import edu.uci.ics.jung.visualization.picking.PickedState;

/**
 * An interface for the background space on which 
 * OpenGL objects can be drawn, which is an OpenGL-appropriate
 * version of JUNG's interface RenderContext.
 * 
 * It provides getters and setters for vertices, edges, labels,
 * and so on -- all the objects that are drawn to screen --
 * as well as picked support and transformers for rendering.
 * 
 */
public interface GLRenderContext<V, E> {

    GLAutoDrawable getGLAutoDrawable();    
    void setGLAutoDrawable(GLAutoDrawable glAutoDrawable);
    
    VertexRenderer<V, E> getVertexRenderer();
    void setVertexRenderer(VertexRenderer<V, E> vertexRenderer);

    EdgeRenderer<V, E> getEdgeRenderer();
    void setEdgeRenderer(EdgeRenderer<V, E> edgeRenderer);
    
    int getLabelOffset();    
    void setLabelOffset(int labelOffset);

    Transformer<E,Font> getEdgeFontTransformer();
    void setEdgeFontTransformer(Transformer<E,Font> edgeFontTransformer);

    EdgeLabelRenderer<V, E> getEdgeLabelRenderer();
    void setEdgeLabelRenderer(EdgeLabelRenderer<V, E> edgeLabelRenderer);
    
    Transformer<E,Paint> getEdgeDrawPaintTransformer();
    void setEdgeDrawPaintTransformer(Transformer<E,Paint> edgeDrawPaintTransformer);

    Transformer<E,String> getEdgeLabelTransformer();
    void setEdgeLabelTransformer(Transformer<E,String> edgeStringer);

    Transformer<E,Stroke> getEdgeStrokeTransformer();
    void setEdgeStrokeTransformer(Transformer<E,Stroke> edgeStrokeTransformer);

    PickedState<E> getPickedEdgeState();
    void setPickedEdgeState(PickedState<E> pickedEdgeState);

    PickedState<V> getPickedVertexState();
    void setPickedVertexState(PickedState<V> pickedVertexState);

    Transformer<V,Font> getVertexFontTransformer();
    void setVertexFontTransformer(Transformer<V,Font> vertexFontTransformer);

    VertexLabelRenderer<V, E> getVertexLabelRenderer();
    void setVertexLabelRenderer(VertexLabelRenderer<V, E> vertexLabelRenderer);

    Transformer<V,Paint> getVertexFillPaintTransformer();
    void setVertexFillPaintTransformer(Transformer<V,Paint> vertexFillPaintTransformer);

    Transformer<V,Paint> getVertexDrawPaintTransformer();
    void setVertexDrawPaintTransformer(Transformer<V,Paint> vertexDrawPaintTransformer);

    Transformer<V,GLShape> getVertexShapeTransformer();
    void setVertexShapeTransformer(Transformer<V,GLShape> vertexShapeTransformer);

    Transformer<V,String> getVertexLabelTransformer();
    void setVertexLabelTransformer(Transformer<V,String> vertexStringer);

    Transformer<V,Paint> getVertexLabelPaintTransformer();
    void setVertexLabelPaintTransformer(Transformer<V,Paint> vertexLabelPaintTransformer);
    
    Transformer<V,Boolean> getVertexLabelBoldedTransformer();
    void setVertexLabelBoldedTransformer(Transformer<V,Boolean> vertexLabelBoldedTransformer);
    
    Transformer<V,Stroke> getVertexStrokeTransformer();
    void setVertexStrokeTransformer(Transformer<V,Stroke> vertexStrokeTransformer);

    MultiLayerTransformer getMultiLayerTransformer();
    void setMultiLayerTransformer(MultiLayerTransformer basicTransformer);
    
	GLGraphElementAccessor<V, E> getPickSupport();
	void setPickSupport(GLGraphElementAccessor<V, E> pickSupport);

	AnnotationShapeRenderer<V,E> getAnnotationShapeRenderer();
	void setAnnotationShapeRenderer(AnnotationShapeRenderer<V,E> annotationShapeRenderer);
	    
	AnnotationTextRenderer<V,E> getAnnotationTextRenderer();
	void setAnnotationTextRenderer(AnnotationTextRenderer<V,E> annotationTextRenderer);

	Transformer<GeneralNode,Boolean> getVertexDisplayTransformer();
    void setVertexDisplayTransformer(Transformer<GeneralNode,Boolean> vertexDisplayTransformer);
    
	Transformer<GeneralEdge,Boolean> getEdgeDisplayTransformer();
    void setEdgeDisplayTransformer(Transformer<GeneralEdge,Boolean> edgeDisplayTransformer);
}
