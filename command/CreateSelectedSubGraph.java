package com.eng.cber.na.command;

import javax.swing.SwingUtilities;

import com.eng.cber.na.command.util.BaseCommand;
import com.eng.cber.na.subgraph.CreateSelectedSubgraph;

/****
 * The command pattern design to create a new subnetwork including
 * only the nodes that are currently selected.
 *
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */

public class CreateSelectedSubGraph extends BaseCommand{
	public CreateSelectedSubGraph(){
		title = "Create subgraph";
		shortDescription = "Create a subgraph from selected nodes.";
	}
	
	
	@Override
	public void execute(String name) {
		SwingUtilities.invokeLater(new CreateSelectedSubgraph(false));
	}

	@Override
	public Boolean recordable() {
		return true;
	}
}
