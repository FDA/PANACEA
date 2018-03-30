package com.eng.cber.na.graphevent;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * Abstract class for behavior given changes to the mouse wheel.
 *
 */
public abstract class AbstractZoomGraphMouseEvent extends
		AbstractGraphMouseEvent implements MouseWheelListener {
	
	protected AbstractZoomGraphMouseEvent() {
		super(0);
	}

	@Override
	public abstract void mouseWheelMoved(MouseWheelEvent e);

}
