package com.eng.cber.na.command;


import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.command.util.BaseCommand;
import com.eng.cber.na.gl.NetworkGLVisualizationServer;
import com.eng.cber.na.graph.GeneralEdge;
import com.eng.cber.na.graph.GeneralNode;

/****
 * The command pattern design to flip the view of the current
 * network horizontally.
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */

public class FlipHorizontalCommand extends BaseCommand{
	
	@Override
	public void execute(String name) {
		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
		NetworkGLVisualizationServer<GeneralNode,GeneralEdge> vv = nv.getNetworkGLVisualizationServer();
		if (vv.getModel()!= null )
			vv.getModel().getGraphLayout().flipHorizontally();
	}

	@Override
	public Boolean recordable() {
		return true;
	}

}
