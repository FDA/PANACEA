package com.eng.cber.na.command;

import java.util.Collection;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.command.util.BaseCommand;
import com.eng.cber.na.dialog.NodeSelectionDialog;
import com.eng.cber.na.graph.GeneralNode;

import edu.uci.ics.jung.visualization.picking.PickedState;


/****
 * The command pattern design to open the node selection dialog window
 * where the user can choose a selection of nodes by name.  The list can
 * be sorted alphabetically or by a centrality metric and the user can
 * search for a node by typing in part of the name.  
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */
public class SelectNodesFromListCommand extends BaseCommand{
	
	private static final long serialVersionUID = 1L;
	
	private Collection<GeneralNode> newSelection;
	
	public SelectNodesFromListCommand(){
		title = "Select node(s) from List.";
		shortDescription = "Select node(s) from list.";
	}
	
	public Collection<GeneralNode> getNewSelection() {
		return newSelection;
	}

	public void setNewSelection(Collection<GeneralNode> newSelection) {
		this.newSelection = newSelection;
	}

	@Override
	public void execute(String name) {
		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();

		PickedState<GeneralNode> pickedState = nv.getNetworkGLVisualizationServer().getPickedVertexState();
		NodeSelectionDialog nsd = new NodeSelectionDialog(nv, nv.getGraph().getVertices());
		newSelection = nsd.getSelection();
		if(nsd.exitedWithSelectButton()) {
			nv.getNetworkGLVisualizationServer().getPickedEdgeState().clear();
			for (GeneralNode n : nv.getGraph().getVertices()) {
				pickedState.pick(n, newSelection.contains(n));
			}
			nv.updateAfterNodeSelection();
			nv.getNetworkGLVisualizationServer().repaint();
		}
	}

	@Override
	public Boolean recordable() {
		return true;
	}

	@Override
	public Collection<GeneralNode> getSelectedNodes() {
		return newSelection;
	}
}
