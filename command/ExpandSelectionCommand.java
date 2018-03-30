package com.eng.cber.na.command;


import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.command.util.BaseCommand;
import com.eng.cber.na.gl.NetworkGLVisualizationServer;
import com.eng.cber.na.graph.GeneralEdge;
import com.eng.cber.na.graph.GeneralNode;

/****
 * The command pattern design to expand the current selection
 * by adding all neighboring nodes.
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */

public class ExpandSelectionCommand extends BaseCommand{
	
	public ExpandSelectionCommand(){
		title = "Expand Selection";
		shortDescription = "Expand selected nodes with its direct neighbors.";
	}
	
	@Override
	public void execute(String name) {
		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
		NetworkGLVisualizationServer<GeneralNode,GeneralEdge> vv = nv.getNetworkGLVisualizationServer();
		vv.expandSelection();	
	}

	@Override
	public Boolean recordable() {
		return true;
	}

	@Override
	public void redo(String name) {
		execute(name);
	}
}
