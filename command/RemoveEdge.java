package com.eng.cber.na.command;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.command.util.BaseCommand;
import com.eng.cber.na.removal.EdgeRemovalDialog;

/****
 * The command pattern design to open the Remove Edges dialog
 * window to select edges to remove by weight.
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */

public class RemoveEdge extends BaseCommand{
	public RemoveEdge(){
	}
	@Override
	public void execute(String name) {
		EdgeRemovalDialog.showDialog(null, NetworkAnalysisVisualization.getInstance().getGraph());
	}
	@Override
	public Boolean recordable() {
		return true;
	}
	}
