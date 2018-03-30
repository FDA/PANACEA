package com.eng.cber.na.graphevent;

import java.awt.event.MouseWheelEvent;

import com.eng.cber.na.NetworkAnalysisVisualization;

/**
 * Concrete class for responding to changes in the
 * mouse wheel -- zooms the canvas in/out depending
 * on the extent to which the mouse wheel is rolled.
 *
 */
public class DefaultZoomGraphMouseEvent extends AbstractGraphMouseEvent {
	
	public DefaultZoomGraphMouseEvent() {
		super(0);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if(checkModifiers(e)) {
			NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
			if(e.getWheelRotation() > 0) {
				nv.getNetworkGLVisualizationServer().getNetworkScalingControl().zoomOut(e.getPoint());
			}
			else {
				nv.getNetworkGLVisualizationServer().getNetworkScalingControl().zoomIn(e.getPoint());
			}
		}
	}
}
