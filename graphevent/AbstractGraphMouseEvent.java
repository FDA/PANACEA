package com.eng.cber.na.graphevent;

import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import com.eng.cber.na.gl.GLGraphElementAccessor;
import com.eng.cber.na.gl.NetworkGLVisualizationServer;
import com.eng.cber.na.graph.GeneralEdge;
import com.eng.cber.na.graph.GeneralNode;
import com.eng.cber.na.graph.Graph_Object;

/**
 * This mouse event class is an abstract class focused
 * on mouse events on the graph/network displayed to
 * the user.  Its main function is to send the presses 
 * event to the edge or the vertex that was clicked on,
 * or to a general listener if nothing was clicked on.
 * It also tracks whether the selection was performed
 * under multi-select.  All mouse events are derived
 * from this class.
 *
 */
public abstract class AbstractGraphMouseEvent extends MouseAdapter implements GraphMouseEvent {
	
	private int modifiers;
	protected int multiSelectModifiers = InputEvent.BUTTON1_MASK | InputEvent.SHIFT_MASK;
	protected int annotationModifiers = InputEvent.BUTTON1_MASK | InputEvent.ALT_MASK;
	
	protected AbstractGraphMouseEvent(int modifiers) {
		this.modifiers = modifiers;
	}
	
	@Override
	public int getModifiers() {
		return modifiers;
	}
	
	@Override
	public void setModifiers(int modifiers) {
		this.modifiers = modifiers;
	}
	
	@Override
	public boolean checkModifiers(MouseEvent e) {
		return e.getModifiers() == modifiers;
	}
	
	@Override
	public void mousePressed(MouseEvent e) { 
		@SuppressWarnings("unchecked")
		NetworkGLVisualizationServer<GeneralNode,GeneralEdge> vv = (NetworkGLVisualizationServer<GeneralNode,GeneralEdge>)e.getSource();
		
		Point2D p = e.getPoint();
		GLGraphElementAccessor<GeneralNode,GeneralEdge> gea = vv.getPickSupport();
		
		if (vv.getGraphLayout() != null ){
			
			GeneralNode node = (GeneralNode) gea.getVertex(vv.getGraphLayout(), p.getX(), p.getY());
			if(node != null) {
				mousePressed(e,node);
			}
			else {
				GeneralEdge edge = gea.getEdge(vv.getGraphLayout(), p.getX(), p.getY());
				if(edge != null) {
					mousePressed(e,edge);
				}
				else {
					mousePressed(e, null);
				}
			}
		}

	}

	public void mousePressed(MouseEvent e, Graph_Object obj) { };
}
