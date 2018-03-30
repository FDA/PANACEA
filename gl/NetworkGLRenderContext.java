package com.eng.cber.na.gl;

import java.awt.BasicStroke;
import java.awt.Color;
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
import com.eng.cber.na.gl.renderer.NetworkGLAnnotationShapeRenderer;
import com.eng.cber.na.gl.renderer.NetworkGLEdgeLabelRenderer;
import com.eng.cber.na.gl.renderer.NetworkGLEdgeRenderer;
import com.eng.cber.na.gl.renderer.NetworkGLVertexRenderer;
import com.eng.cber.na.gl.shape.GLCircle;
import com.eng.cber.na.gl.shape.GLShape;
import com.eng.cber.na.graph.GeneralEdge;
import com.eng.cber.na.graph.GeneralNode;
import com.eng.cber.na.transformer.BasicTransformer;
import com.eng.cber.na.transformer.ConstantTransformer;
import com.eng.cber.na.transformer.MultiLayerTransformer;

import edu.uci.ics.jung.visualization.picking.PickedState;

/**
 * An implemented version of the render context
 * that keeps track of all of the transformers
 * and renderers that convert items that need to be drawn on
 * screen into actual pixels turned on on the screen.
 * 
 */
public class NetworkGLRenderContext<V, E> implements GLRenderContext<V, E> {
    
    protected Transformer<V,Stroke> vertexStrokeTransformer = ConstantTransformer.<V,Stroke>getTransformer(new BasicStroke(1.0f));    
    protected Transformer<V,GLShape> vertexShapeTransformer = ConstantTransformer.<V,GLShape>getTransformer(new GLCircle(10));
    protected Transformer<V,String> vertexLabelTransformer = ConstantTransformer.<V,String>getTransformer(null);
    protected Transformer<V,Paint> vertexLabelPaintTransformer = ConstantTransformer.<V,Paint>getTransformer(Color.BLACK);
    protected Transformer<V,Boolean> vertexLabelBoldedTransformer = ConstantTransformer.<V,Boolean>getTransformer(false);
    protected Transformer<V,Font> vertexFontTransformer = ConstantTransformer.<V,Font>getTransformer(new Font("Helvetica", Font.PLAIN, 12));    
    protected Transformer<V,Paint> vertexDrawPaintTransformer = ConstantTransformer.<V,Paint>getTransformer(Color.BLACK);
    protected Transformer<V,Paint> vertexFillPaintTransformer = ConstantTransformer.<V,Paint>getTransformer(Color.RED);    
    protected Transformer<E,String> edgeLabelTransformer = ConstantTransformer.<E,String>getTransformer(null);
    protected Transformer<E,Stroke> edgeStrokeTransformer = ConstantTransformer.<E,Stroke>getTransformer(new BasicStroke(1.0f));
    protected Transformer<E,Font> edgeFontTransformer = ConstantTransformer.<E,Font>getTransformer(new Font("Helvetica", Font.PLAIN, 12));
    protected Transformer<E,Paint> edgeDrawPaintTransformer = ConstantTransformer.<E,Paint>getTransformer(Color.black);
    protected Transformer<GeneralNode,Boolean> vertexDisplayTransformer = ConstantTransformer.<GeneralNode,Boolean>getTransformer(false);
    protected Transformer<GeneralEdge,Boolean> edgeDisplayTransformer = ConstantTransformer.<GeneralEdge,Boolean>getTransformer(false);
    
    protected MultiLayerTransformer multiLayerTransformer = new BasicTransformer();

    protected GLGraphElementAccessor<V, E> pickSupport;

    protected int labelOffset = 10;
    
    protected PickedState<V> pickedVertexState;
    protected PickedState<E> pickedEdgeState;
    	
    protected VertexLabelRenderer<V,E> vertexLabelRenderer;
    protected EdgeLabelRenderer<V,E> edgeLabelRenderer = new NetworkGLEdgeLabelRenderer<V,E>();
    
    protected VertexRenderer<V,E> vertexRenderer = new NetworkGLVertexRenderer<V,E>();
    protected EdgeRenderer<V,E> edgeRenderer = new NetworkGLEdgeRenderer<V,E>();
	
    protected AnnotationShapeRenderer<V,E> annotationShapeRenderer = new NetworkGLAnnotationShapeRenderer<V,E>();
    protected AnnotationTextRenderer<V,E> annotationTextRenderer;
        
	protected GLAutoDrawable glAutoDrawable;

	@Override
	public GLAutoDrawable getGLAutoDrawable() {
		return glAutoDrawable;
	}

	@Override
	public void setGLAutoDrawable(GLAutoDrawable glAutoDrawable) {
		this.glAutoDrawable = glAutoDrawable;
	}

	@Override
	public VertexRenderer<V, E> getVertexRenderer() {
		return vertexRenderer;
	}

	@Override
	public void setVertexRenderer(VertexRenderer<V, E> vertexRenderer) {
		this.vertexRenderer = vertexRenderer;
	}

	@Override
	public EdgeRenderer<V, E> getEdgeRenderer() {
		return edgeRenderer;
	}

	@Override
	public void setEdgeRenderer(EdgeRenderer<V, E> edgeRenderer) {
		this.edgeRenderer = edgeRenderer;
	}
	
	@Override
	public AnnotationShapeRenderer<V,E> getAnnotationShapeRenderer() {
		return annotationShapeRenderer;
	}
	@Override
	public void setAnnotationShapeRenderer(AnnotationShapeRenderer<V, E> annotationRenderer) {
		this.annotationShapeRenderer = annotationRenderer;
	}
	
	@Override
	public AnnotationTextRenderer<V, E> getAnnotationTextRenderer() {
		return annotationTextRenderer;
	}

	@Override
	public void setAnnotationTextRenderer(AnnotationTextRenderer<V, E> annotationTextRenderer) {
		this.annotationTextRenderer = annotationTextRenderer;
	}
	
	@Override
	public Transformer<V, GLShape> getVertexShapeTransformer() {
		return vertexShapeTransformer;
	}

	@Override
	public void setVertexShapeTransformer(Transformer<V,GLShape> vertexShapeTransformer) {
		this.vertexShapeTransformer = vertexShapeTransformer;
	}

	@Override
	public Transformer<V, Stroke> getVertexStrokeTransformer() {
		return vertexStrokeTransformer;
	}

	@Override
	public void setVertexStrokeTransformer(Transformer<V, Stroke> vertexStrokeTransformer) {
		this.vertexStrokeTransformer = vertexStrokeTransformer;
	}
	
	@Override
    public Transformer<E,Font> getEdgeFontTransformer() {
        return edgeFontTransformer;
    }

	@Override
    public void setEdgeFontTransformer(Transformer<E,Font> edgeFontTransformer) {
        this.edgeFontTransformer = edgeFontTransformer;
    }

	@Override
    public EdgeLabelRenderer<V, E> getEdgeLabelRenderer() {
        return edgeLabelRenderer;
    }

	@Override
    public void setEdgeLabelRenderer(EdgeLabelRenderer<V, E> edgeLabelRenderer) {
        this.edgeLabelRenderer = edgeLabelRenderer;
    }

	@Override
    public void setEdgeDrawPaintTransformer(Transformer<E,Paint> edgeDrawPaintTransformer) {
        this.edgeDrawPaintTransformer = edgeDrawPaintTransformer;
    }

	@Override
    public Transformer<E,Paint> getEdgeDrawPaintTransformer() {
        return edgeDrawPaintTransformer;
    }

	@Override
    public Transformer<E,String> getEdgeLabelTransformer() {
        return edgeLabelTransformer;
    }

	@Override
    public void setEdgeLabelTransformer(Transformer<E,String> edgeLabelTransformer) {
        this.edgeLabelTransformer = edgeLabelTransformer;
    }

	@Override
    public Transformer<E,Stroke> getEdgeStrokeTransformer() {
        return edgeStrokeTransformer;
    }

	@Override
    public void setEdgeStrokeTransformer(Transformer<E,Stroke> edgeStrokeTransformer) {
        this.edgeStrokeTransformer = edgeStrokeTransformer;
    }
    
	@Override
    public int getLabelOffset() {
        return labelOffset;
    }

	@Override
    public void setLabelOffset(int labelOffset) {
        this.labelOffset = labelOffset;
    }

	@Override
    public PickedState<E> getPickedEdgeState() {
        return pickedEdgeState;
    }

	@Override
    public void setPickedEdgeState(PickedState<E> pickedEdgeState) {
        this.pickedEdgeState = pickedEdgeState;
    }

	@Override
    public PickedState<V> getPickedVertexState() {
        return pickedVertexState;
    }

	@Override
    public void setPickedVertexState(PickedState<V> pickedVertexState) {
        this.pickedVertexState = pickedVertexState;
    }

	@Override
    public Transformer<V,Font> getVertexFontTransformer() {
        return vertexFontTransformer;
    }

	@Override
    public void setVertexFontTransformer(Transformer<V,Font> vertexFontTransformer) {
        this.vertexFontTransformer = vertexFontTransformer;
    }

	@Override
    public VertexLabelRenderer<V, E> getVertexLabelRenderer() {
        return vertexLabelRenderer;
    }

	@Override
    public void setVertexLabelRenderer(VertexLabelRenderer<V, E> vertexLabelRenderer) {
        this.vertexLabelRenderer = vertexLabelRenderer;
    }

	@Override
    public Transformer<V,Paint> getVertexFillPaintTransformer() {
        return vertexFillPaintTransformer;
    }

	@Override
    public void setVertexFillPaintTransformer(Transformer<V,Paint> vertexFillPaintTransformer) {
        this.vertexFillPaintTransformer = vertexFillPaintTransformer;
    }

	@Override
    public Transformer<V,Paint> getVertexDrawPaintTransformer() {
        return vertexDrawPaintTransformer;
    }

	@Override
    public void setVertexDrawPaintTransformer(Transformer<V,Paint> vertexDrawPaintTransformer) {
        this.vertexDrawPaintTransformer = vertexDrawPaintTransformer;
    }

	@Override
    public Transformer<V,String> getVertexLabelTransformer() {
        return vertexLabelTransformer;
    }

	@Override
    public void setVertexLabelTransformer(Transformer<V,String> vertexLabelTransformer) {
        this.vertexLabelTransformer = vertexLabelTransformer;
    }
	
	@Override
	public Transformer<V,Paint> getVertexLabelPaintTransformer() {
		return vertexLabelPaintTransformer;
	}
	
	@Override
	public void setVertexLabelPaintTransformer(Transformer<V,Paint> vertexLabelPaintTransformer) {
		this.vertexLabelPaintTransformer = vertexLabelPaintTransformer;
	}

	@Override
	public Transformer<V,Boolean> getVertexLabelBoldedTransformer() {
		return vertexLabelBoldedTransformer;
	}
	
	@Override
	public void setVertexLabelBoldedTransformer(Transformer<V,Boolean> vertexLabelBoldedTransformer) {
		this.vertexLabelBoldedTransformer = vertexLabelBoldedTransformer;
	}
	
	@Override
	public GLGraphElementAccessor<V, E> getPickSupport() {
		return pickSupport;
	}

	@Override
	public void setPickSupport(GLGraphElementAccessor<V, E> pickSupport) {
		this.pickSupport = pickSupport;
	}
	
	@Override
	public MultiLayerTransformer getMultiLayerTransformer() {
		return multiLayerTransformer;
	}

	@Override
	public void setMultiLayerTransformer(MultiLayerTransformer multiLayerTransformer) {
		this.multiLayerTransformer = multiLayerTransformer;
	}

	@Override
	public Transformer<GeneralNode, Boolean> getVertexDisplayTransformer() {
		return vertexDisplayTransformer;
	}

	@Override
	public void setVertexDisplayTransformer(
			Transformer<GeneralNode, Boolean> vertexDisplayTransformer) {
		this.vertexDisplayTransformer = vertexDisplayTransformer;
	}

	@Override
	public Transformer<GeneralEdge, Boolean> getEdgeDisplayTransformer() {
		return edgeDisplayTransformer;
	}

	@Override
	public void setEdgeDisplayTransformer(
			Transformer<GeneralEdge, Boolean> edgeDisplayTransformer) {
		this.edgeDisplayTransformer = edgeDisplayTransformer;
	}
}