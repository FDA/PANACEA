package com.eng.cber.na.graphevent;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.HashSet;
import java.util.Set;

/**
 * A holding class for graph mouse events that keeps a set
 * of listeners and directs events on the screen to the
 * appropriate listener.  This is implemented by setting
 * the graph mouse of the NetworkGLVisualizationServer to an
 * instance of this class, and adding all of the specific mouse 
 * listeners to it (e.g., for dragging, mouse wheeling, and so 
 * on).
 *
 */
public class GraphMouseEventHandler implements GraphMouse {

	private Set<MouseListener> mouseListeners;
	private Set<MouseMotionListener> mouseMotionListeners;
	private Set<MouseWheelListener> mouseWheelListeners;
	
	public GraphMouseEventHandler() {
		mouseListeners = new HashSet<MouseListener>();
		mouseMotionListeners = new HashSet<MouseMotionListener>();
		mouseWheelListeners = new HashSet<MouseWheelListener>();
	}
	
	public void addGraphMouseEvent(GraphMouseEvent m) {
		if(m instanceof MouseListener) {
			mouseListeners.add((MouseListener)m);
		}
		if(m instanceof MouseMotionListener) {
			mouseMotionListeners.add((MouseMotionListener)m);
		}
		if(m instanceof MouseWheelListener) {
			mouseWheelListeners.add((MouseWheelListener)m);
		}
	}
	
	public void removeGraphMouseEvent(GraphMouseEvent m) {
		if(m instanceof MouseListener) {
			mouseListeners.remove(m);
		}
		if(m instanceof MouseMotionListener) {
			mouseMotionListeners.remove(m);
		}
		if(m instanceof MouseWheelListener) {
			mouseWheelListeners.remove(m);
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		for(MouseListener m : mouseListeners) {
			m.mouseClicked(e);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) { }

	@Override
	public void mouseExited(MouseEvent e) {	}

	@Override
	public void mousePressed(MouseEvent e) {
		for(MouseListener m : mouseListeners) {
			m.mousePressed(e);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		for(MouseListener m : mouseListeners) {
			m.mouseReleased(e);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		for(MouseMotionListener m : mouseMotionListeners) {
			m.mouseDragged(e);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		for(MouseMotionListener m : mouseMotionListeners) {
			m.mouseMoved(e);
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		for(MouseWheelListener m : mouseWheelListeners) {
			m.mouseWheelMoved(e);
		}
	}
}
