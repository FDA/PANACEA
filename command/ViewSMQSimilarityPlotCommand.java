package com.eng.cber.na.command;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.command.util.BaseCommand;
import com.eng.cber.na.dialog.SMQSimilarityPlotDialog;

/****
 * The command pattern design to show the dialog window
 * with the plot of reports in the current network and
 * their similarity to the ReferenceDocument.
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */
public class ViewSMQSimilarityPlotCommand extends BaseCommand {

	@Override
	public void execute(String name) {
		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();

		SMQSimilarityPlotDialog smqPlotDialog = new SMQSimilarityPlotDialog(nv);
	}

	@Override
	public Boolean recordable() {
		return false;
	}

}
