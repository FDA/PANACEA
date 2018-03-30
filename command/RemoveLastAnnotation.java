package com.eng.cber.na.command;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.command.util.BaseCommand;

/****
 * The command pattern design to remove the last annotation that
 * was added to this layout object.
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */

public class RemoveLastAnnotation extends BaseCommand{
	@Override
	public void execute(String name) {
		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
		nv.getNetworkGLVisualizationServer().getModel().getGraphLayout().getNetworkGLAnnotationManager().removeLastAnnotation();
		nv.getNetworkGLVisualizationServer().repaint();
	}

	@Override
	public Boolean recordable() {
		return false;
	}
}
