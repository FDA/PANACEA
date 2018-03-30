package com.eng.cber.na.command;

import java.awt.Container;

import javax.swing.SwingUtilities;

import com.eng.cber.na.command.util.BaseCommand;
import com.eng.cber.na.removal.EdgeRemovalDialog;
import com.eng.cber.na.removal.EdgeRemovalPanel;
import com.eng.cber.na.subgraph.CreateEdgeWeightExcludedRangeSubgraph;
import com.eng.cber.na.subgraph.CreateEdgeWeightSubgraph;
import com.eng.cber.na.subgraph.CreateTypedEdgeWeightExcludedRangeSubgraph;
import com.eng.cber.na.subgraph.CreateTypedEdgeWeightSubgraph;
import com.eng.cber.na.vaers.VAERS_Edge;

/****
 * The command pattern design to remove edges that have weights
 * inside or outside the selected range.  Triggered from within
 * the Remove Edges dialog window.
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */

public class RemoveEdgeSubCommand extends BaseCommand{
	private EdgeRemovalPanel erp;
	public RemoveEdgeSubCommand(){
	}
	public RemoveEdgeSubCommand(EdgeRemovalPanel erp){
		this.erp = erp;
	}
	@Override
	public void execute(String name) {
		boolean excludedRange = false;
		// If Remove Below is set lower than Remove Above, then we want to remove an internal range of values.
		if (erp.getPercentileEdgesBetweenSelection() <= 0.0) {
			excludedRange = true;
		}

		// When calling excluded range subgraphs, the max and min will be reversed.
		switch(erp.getEdgeType()) {
		case 0: //  "All"
			SwingUtilities.invokeLater(excludedRange ?
					new CreateEdgeWeightExcludedRangeSubgraph(erp.getMaxEdgeWeight(), erp.getMinEdgeWeight()) :
					new CreateEdgeWeightSubgraph(erp.getMinEdgeWeight(), erp.getMaxEdgeWeight()));
			break;
		case 1: // "Vaccine-Vaccine"
			SwingUtilities.invokeLater(excludedRange ?
					new CreateTypedEdgeWeightExcludedRangeSubgraph(erp.getMaxEdgeWeight(), erp.getMinEdgeWeight(), VAERS_Edge.EdgeType.VAX2VAX) :
					new CreateTypedEdgeWeightSubgraph(erp.getMinEdgeWeight(), erp.getMaxEdgeWeight(), VAERS_Edge.EdgeType.VAX2VAX));
			break;
		case 2: // "Vaccine-Symptom"
			SwingUtilities.invokeLater(excludedRange ?
					new CreateTypedEdgeWeightExcludedRangeSubgraph(erp.getMaxEdgeWeight(), erp.getMinEdgeWeight(), VAERS_Edge.EdgeType.VAX2SYM) :
					new CreateTypedEdgeWeightSubgraph(erp.getMinEdgeWeight(), erp.getMaxEdgeWeight(), VAERS_Edge.EdgeType.VAX2SYM));
			break;
		case 3: // "Symptom-Symptom"
			SwingUtilities.invokeLater(excludedRange ?
					new CreateTypedEdgeWeightExcludedRangeSubgraph(erp.getMaxEdgeWeight(), erp.getMinEdgeWeight(), VAERS_Edge.EdgeType.SYM2SYM) :
					new CreateTypedEdgeWeightSubgraph(erp.getMinEdgeWeight(), erp.getMaxEdgeWeight(), VAERS_Edge.EdgeType.SYM2SYM));
			break;
		default:
			throw new IllegalArgumentException("Edge type option #" + erp.getEdgeType()+1 + " currently has no associated action.");
		}
		
		Container parent = erp.getParent();
		while (!(parent instanceof EdgeRemovalDialog))
			parent = parent.getParent();
		
		EdgeRemovalDialog dialog = (EdgeRemovalDialog) parent;
		dialog.setVisible(false);
		dialog.dispose();	
	}
	@Override
	public Boolean recordable() {
		return true;
	}
}
