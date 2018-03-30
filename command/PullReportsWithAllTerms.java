package com.eng.cber.na.command;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.command.util.BaseCommand;
import com.eng.cber.na.graph.BuildGraphFromReportedTermsAll;
import com.eng.cber.na.graph.FDAGraph;
import com.eng.cber.na.graph.GeneralNode;

import edu.uci.ics.jung.visualization.picking.PickedState;

/****
 * The command pattern design to create a subnetwork including only
 * reports that contain all of the selected terms.  The selection must
 * be SYMs and/or VAXs from an element network.
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */

public class PullReportsWithAllTerms extends BaseCommand{
	@Override
	public void execute(String name) {
		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
		if (nv.getDualState()) {
			JOptionPane.showMessageDialog(nv, "Cannot do this with a report network.  Switch to element network and select term(s).", "ERROR", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (!(nv.getGraph() instanceof FDAGraph)) {
			JOptionPane.showMessageDialog(nv, "Cannot do this with a general network.");
			return;
		}
		PickedState<GeneralNode> pickedState = nv.getNetworkGLVisualizationServer().getPickedVertexState();
		SwingUtilities.invokeLater(new BuildGraphFromReportedTermsAll(pickedState.getPicked()));
	}

	@Override
	public Boolean recordable() {
		return true;
	}
}
