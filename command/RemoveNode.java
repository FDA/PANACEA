package com.eng.cber.na.command;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.command.util.BaseCommand;
import com.eng.cber.na.removal.NodeRemovalDialog;

/****
 * The command pattern design to open the Remove Nodes dialog
 * window to select nodes to remove based on a metric.
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */

public class RemoveNode extends BaseCommand{
	private NodeRemovalDialog dialog;
	public NodeRemovalDialog getDialog() {
		return dialog;
	}
	public RemoveNode(){
	}
	@Override
	public void execute(String name) {
		dialog = NodeRemovalDialog.showDialog(null, NetworkAnalysisVisualization.getInstance().getGraph());		
	}
	@Override
	public Boolean recordable() {
		return true;
	}
	}
