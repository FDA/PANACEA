package com.eng.cber.na.command;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.command.util.BaseCommand;

/****
 * The command pattern design to clear all annotations from
 * the currently visible layout.
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */

public class ClearAllAnnotations extends BaseCommand{
	@Override
	public void execute(String name) {
		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
		nv.getNetworkGLVisualizationServer().getModel().getGraphLayout().getNetworkGLAnnotationManager().clear();
		nv.getNetworkGLVisualizationServer().repaint();
	}

	@Override
	public Boolean recordable() {
		return false;
	}
}
