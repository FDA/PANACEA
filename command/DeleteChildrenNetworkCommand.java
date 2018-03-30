package com.eng.cber.na.command;

import javax.swing.SwingUtilities;

import com.eng.cber.na.command.util.BaseCommand;
import com.eng.cber.na.graph.DeleteSubgraphChildren;

/****
 * The command pattern design to delete all the children network nodes
 * for a particular network in the network view panel.
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */
public class DeleteChildrenNetworkCommand extends BaseCommand{
	@Override
	public void execute(String name) {
		SwingUtilities.invokeLater(new DeleteSubgraphChildren());
	}

	@Override
	public Boolean recordable() {
		return true;
	}
}
