package com.eng.cber.na.graphevent;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.annotations.NetworkGLAnnotation;
import com.eng.cber.na.gl.GLGraphElementAccessor;
import com.eng.cber.na.gl.GLVisualizationServer;
import com.eng.cber.na.gl.shape.GLRectangle;
import com.eng.cber.na.graph.GeneralEdge;
import com.eng.cber.na.graph.GeneralNode;
import com.eng.cber.na.graph.Graph_Object;
import com.eng.cber.na.graph.Graph_Object.ObjectType;
import com.eng.cber.na.layout.Layout;
import com.eng.cber.na.transformer.MutableTransformer;

import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.picking.PickedState;

/**
 * Concrete class for selecting and multi-selecting
 * on the network. 
 *
 */
public class DefaultSelectDragGraphMouseEvent extends AbstractSelectDragGraphMouseEvent {

	public DefaultSelectDragGraphMouseEvent() {
		super();
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if(NetworkAnalysisVisualization.getInstance().getGraph() == null || NetworkAnalysisVisualization.getInstance().getGraph().getVertexCount()==0)
			return;
		@SuppressWarnings("unchecked")
		GLVisualizationServer<GeneralNode,GeneralEdge> vv = (GLVisualizationServer<GeneralNode,GeneralEdge>)e.getSource();
		PickedState<GeneralNode> pickedVertexState = vv.getPickedVertexState();
		Point p = e.getPoint();		
		// need the inverse transform in case view is zoomed in
        Point2D tp = vv.getGLRenderContext().getMultiLayerTransformer().inverseTransform(p);
    	Point2D tstart;
    	if(start != null) {
    		tstart = vv.getGLRenderContext().getMultiLayerTransformer().inverseTransform(start);
    	}
    	else {
    		tstart = vv.getGLRenderContext().getMultiLayerTransformer().inverseTransform(p);
    	}
    	double dx = tp.getX()-tstart.getX();
		double dy = tp.getY()-tstart.getY();
        if(checkModifiers(e) || (e.getModifiers() == multiSelectModifiers && multiSelecting == false)) {
			vv.removePostRenderRenderable(rectRenderable);
			if(!pickedVertexState.getPicked().isEmpty()) {
				Layout<GeneralNode,GeneralEdge> layout = vv.getGraphLayout();
				for(GeneralNode node : pickedVertexState.getPicked()) {
					Point2D pt = layout.transform(node);
					pt.setLocation(pt.getX()+dx, pt.getY()+dy);
					layout.setLocation(node, pt);
				}
			}
			else {
				MutableTransformer modelTransformer = vv.getGLRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT);
	            try {
	                modelTransformer.translate(dx, dy);
	            } 
	            catch(RuntimeException ex) {
	            	System.err.println(ex.getMessage());
	            }       
			}
			start = p;
		}		
		else if(e.getModifiers() == multiSelectModifiers && multiSelecting == true) {
			this.p = p;
		}
		else if(e.getModifiers() == annotationModifiers) {
			this.p = p;
			}
        
        vv.repaint();
	}
	
	@Override
	public void mousePressed(MouseEvent e, Graph_Object obj) { 		
		
		if(NetworkAnalysisVisualization.getInstance().getGraph() == null || NetworkAnalysisVisualization.getInstance().getGraph().getVertexCount()==0)
			return;
		@SuppressWarnings("unchecked")
		GLVisualizationServer<GeneralNode,GeneralEdge> vv = (GLVisualizationServer<GeneralNode,GeneralEdge>)e.getSource();

		PickedState<GeneralNode> pickedVertexState = vv.getPickedVertexState();
		PickedState<GeneralEdge> pickedEdgeState = vv.getPickedEdgeState();
		
		GeneralNode node = null;
		GeneralEdge edge = null;
		if(obj != null) {
			node = obj.getObjectType() == ObjectType.NODE ? (GeneralNode)obj : null;			
			edge = obj.getObjectType() == ObjectType.EDGE ? (GeneralEdge)obj : null;
		}
		
		if(checkModifiers(e)){
			if(node != null && !pickedVertexState.isPicked(node)) {
				pickedVertexState.clear();
				pickedEdgeState.clear();
				pickedVertexState.pick(node, true);
			}
			else if(node == null && edge != null && !pickedEdgeState.isPicked(edge)) {
				pickedVertexState.clear();
				pickedEdgeState.clear();
				pickedEdgeState.pick(edge, true);
			}
			else if(node == null && edge == null) {
				pickedVertexState.clear();
				pickedEdgeState.clear();
			}
			multiSelecting = false;
		}
		else if(e.getModifiers() == multiSelectModifiers) {
			// Toggle the picked state of the thing the user clicks on
			// if the user has multi-select on (i.e., is holding the right
			// keyboard key to initiate multi-select)
			if(node != null) {
				pickedVertexState.pick(node, !pickedVertexState.isPicked(node));
			}
			else if(edge != null) {
				pickedEdgeState.pick(edge, !pickedEdgeState.isPicked(edge));
			}
			vv.addPostRenderRenderable(rectRenderable);
			multiSelecting = true;
			this.p = e.getPoint();
		}
		else if(e.getModifiers() == annotationModifiers) {
			vv.addPostRenderRenderable(rectRenderable);
			multiSelecting = false;
			this.p = e.getPoint();
			}


		start = e.getPoint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
		if(nv.getGraph() == null || nv.getGraph().getVertexCount()==0)
			return;
		
		GLVisualizationServer<GeneralNode,GeneralEdge> vv = (GLVisualizationServer<GeneralNode,GeneralEdge>)e.getSource();
		PickedState<GeneralNode> pickedVertexState = vv.getPickedVertexState();
		if(e.getModifiers() == multiSelectModifiers && multiSelecting) {
			GLGraphElementAccessor<GeneralNode,GeneralEdge> gea = vv.getPickSupport();
			for(GeneralNode v : gea.getVertices(vv.getGraphLayout(), start, p)) {
				pickedVertexState.pick(v, true);
			}
		}
		else if (e.getModifiers() == annotationModifiers && p.distance(start) >= 0.1d) {
			Point2D tp = vv.getGLRenderContext().getMultiLayerTransformer().inverseTransform(p);
			Point2D tstart = vv.getGLRenderContext().getMultiLayerTransformer().inverseTransform(start);
			Double dx = tp.getX() - tstart.getX();
			Double dy = tp.getY() - tstart.getY();
			Point2D center = new Point2D.Double((tp.getX()+tstart.getX())/2.,(tp.getY()+tstart.getY())/2.);
			vv.getModel().getGraphLayout().getNetworkGLAnnotationManager().addAnnotation(new NetworkGLAnnotation(new GLRectangle(dx/2.,dy/2.), center, Color.black)); 
		}

		multiSelecting = false;
		vv.removePostRenderRenderable(rectRenderable);
		vv.repaint();
		
		nv.setSelectedVertexCount(pickedVertexState.getPicked().size());
	}
}
