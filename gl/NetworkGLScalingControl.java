package com.eng.cber.na.gl;

import java.awt.geom.Point2D;

import com.eng.cber.na.transformer.MutableTransformer;

import edu.uci.ics.jung.visualization.Layer;

/**
 * An implemented version of the scaling control
 * that allows for rescaling of the visualized 
 * OpenGL canvas/VisualizationServer.
 * 
 */
public class NetworkGLScalingControl implements GLScalingControl {
	
	private GLVisualizationServer<?,?> vv;
	
	private double crossover = 1.0;
	
	public NetworkGLScalingControl(GLVisualizationServer<?,?> vv) {
		this.vv = vv;
	}
	
	public void rescale() {
		MutableTransformer layoutTransformer = vv.getGLRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT);
	    MutableTransformer viewTransformer = vv.getGLRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW);
	    double modelScale = layoutTransformer.getScale();
	    double viewScale = viewTransformer.getScale();
	    layoutTransformer.scale(1.0/modelScale, 1.0/modelScale, vv.getCenter());
	    viewTransformer.scale(1.0/viewScale, 1.0/viewScale, vv.getCenter());
	}
	
	public void reset() {
		vv.getGLRenderContext().getMultiLayerTransformer().setToIdentity();
	}
	
	public void setScale(double layoutScale, double viewScale, Point2D center) {
		MutableTransformer layoutTransformer = vv.getGLRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT);
	    MutableTransformer viewTransformer = vv.getGLRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW);
	    layoutTransformer.scale(1.0/layoutScale, 1.0/layoutScale, center);
	    viewTransformer.scale(1.0/viewScale, 1.0/viewScale, center);
	}
	
	public void zoomIn(Point2D pt) {
		scale(1.1f, pt);
	}
	
	public void zoomOut(Point2D pt) {
		scale(1/1.1f, pt);
	}	

	@Override
	public void scale(float amount, Point2D pt) {
        
	    MutableTransformer layoutTransformer = vv.getGLRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT);
	    MutableTransformer viewTransformer = vv.getGLRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW);
	    double modelScale = layoutTransformer.getScale();
	    double viewScale = viewTransformer.getScale();
	    double inverseModelScale = Math.sqrt(crossover)/modelScale;
	    double inverseViewScale = Math.sqrt(crossover)/viewScale;
	    double scale = modelScale * viewScale;
	    
	    Point2D transformedAt = vv.getGLRenderContext().getMultiLayerTransformer().inverseTransform(Layer.VIEW, pt);
	    
        if((scale*amount - crossover)*(scale*amount - crossover) < 0.001) {
            // close to the control point, return both transformers to a scale of sqrt crossover value
            layoutTransformer.scale(inverseModelScale, inverseModelScale, transformedAt);
            viewTransformer.scale(inverseViewScale, inverseViewScale, pt);
        } else if(scale*amount < crossover) {
            // scale the viewTransformer, return the layoutTransformer to sqrt crossover value
	        viewTransformer.scale(amount, amount, pt);
	        layoutTransformer.scale(inverseModelScale, inverseModelScale, transformedAt);
	    } else {
            // scale the layoutTransformer, return the viewTransformer to crossover value
	        layoutTransformer.scale(amount, amount, transformedAt);
	        viewTransformer.scale(inverseViewScale, inverseViewScale, pt);
	    }
	}
}
