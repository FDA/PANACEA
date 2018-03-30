package com.eng.cber.na.command;


import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.command.util.BaseCommand;
import com.eng.cber.na.gl.NetworkGLVisualizationServer;
import com.eng.cber.na.graph.GeneralEdge;
import com.eng.cber.na.graph.GeneralNode;

/****
 * The command pattern design to zoom out in the main viewer.
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */

public class ZoomOutCommand extends BaseCommand{
	
	@Override
	public void execute(String name) {
		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
		NetworkGLVisualizationServer<GeneralNode,GeneralEdge> vv = nv.getNetworkGLVisualizationServer();
		vv.getNetworkScalingControl().zoomOut(vv.getCenter());
	}

	@Override
	public Boolean recordable() {
		return false;
	}
}
