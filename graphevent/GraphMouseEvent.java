package com.eng.cber.na.graphevent;

import java.awt.event.MouseEvent;

/**
 * Interface for a mouse event; tracks and checks the mouse event modifiers.
 *
 */
public interface GraphMouseEvent {
	public int getModifiers();
	public void setModifiers(int modifiers);
	public boolean checkModifiers(MouseEvent e);
}
