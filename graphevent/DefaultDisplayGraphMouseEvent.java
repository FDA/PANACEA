package com.eng.cber.na.graphevent;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.JOptionPane;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.annotations.NetworkGLAnnotation;
import com.eng.cber.na.gl.NetworkGLVisualizationServer;
import com.eng.cber.na.graph.GeneralEdge;
import com.eng.cber.na.graph.GeneralNode;
import com.eng.cber.na.graph.Graph_Object;

/**
 * Default behavior of mouse on the network. If a
 * node or edge are selected, update the user display 
 * (bottom half of screen) with its information.
 * If nothing is selected, or if there is multi-select,
 * clear the user display.
 *
 */
public class DefaultDisplayGraphMouseEvent extends AbstractGraphMouseEvent {
		
	public DefaultDisplayGraphMouseEvent() {
		super(InputEvent.BUTTON1_MASK);
	}
	
	@Override
	public void mousePressed(MouseEvent e, Graph_Object obj) {
		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
		
		NetworkGLVisualizationServer<GeneralNode,GeneralEdge> vv = nv.getNetworkGLVisualizationServer();	

		int size = vv.getPickedVertexState().getPicked().size();		
		if(checkModifiers(e) && obj != null && (size == 0 || size == 1)) {
			nv.setDisplay(obj);
		}
		else if(obj == null || e.getModifiers() == multiSelectModifiers) {
			nv.clearDisplay();
		}
	}	
	@Override
	public void mouseClicked(MouseEvent e) {
		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
		NetworkGLVisualizationServer<GeneralNode,GeneralEdge> vv = nv.getNetworkGLVisualizationServer(); 
		if (e.getModifiers() == annotationModifiers) {
			Point p = e.getPoint();
			p.translate(1,7); //Move down to be vertically centered on cursor
			Point2D tp = vv.getGLRenderContext().getMultiLayerTransformer().inverseTransform(p);
			String userInput = JOptionPane.showInputDialog("Enter Annotation Text");
			if (userInput != null) { 
				vv.getModel().getGraphLayout().getNetworkGLAnnotationManager().addAnnotation(new NetworkGLAnnotation(userInput, tp, Color.red));
				vv.repaint();
			}
		}
	}
}
