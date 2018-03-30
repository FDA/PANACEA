package com.eng.cber.na.command;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.command.util.BaseCommand;

/****
 * The command pattern design to set the metric that is used
 * to control the size of nodes in the viewer.
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */

public class SetNodeSizeCommand extends BaseCommand{
	public String nodeSize = "";
	
	public SetNodeSizeCommand(){
		title = "Set Node Size";
		shortDescription = "<html> Set Node Size from one of the following types: <br>"
				+ "&nbsp; Degree <br>"
				+ "&nbsp; Closeness <br>"
				+ "&nbsp; Betweenness <br>"
				+ "&nbsp; Report Count <br>"
				+ "&nbsp; Strength <br>"
				+ "</html>";
	}
	
	@Override
	public void execute(String name) {
		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
		nv.updateGraphNodeSize();
		nodeSize = nv.getVertexSizeString();
	}
	
	@Override
	public Boolean recordable() {
		return true;
	}
}
