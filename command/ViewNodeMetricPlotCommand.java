package com.eng.cber.na.command;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.command.util.BaseCommand;
import com.eng.cber.na.dialog.NodeMetricPlotDialog;

/****
 * The command pattern design to show the dialog window
 * with the plot of centrality metrics for the current
 * network.
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */

public class ViewNodeMetricPlotCommand extends BaseCommand {

	@Override
	public void execute(String name) {
		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
		if (!nv.getGraph().confirmBetweenClose()) {
			return;
		}
		
		NodeMetricPlotDialog vmpd = new NodeMetricPlotDialog(nv);
	}

	@Override
	public Boolean recordable() {
		return false;
	}

}
