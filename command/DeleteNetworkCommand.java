package com.eng.cber.na.command;

import javax.swing.SwingUtilities;

import com.eng.cber.na.command.util.BaseCommand;
import com.eng.cber.na.graph.DeleteSubgraph;

/****
 * The command pattern design to delete a network node from
 * the JTree in the network view panel.
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */

public class DeleteNetworkCommand extends BaseCommand{
	@Override
	public void execute(String name) {
		SwingUtilities.invokeLater(new DeleteSubgraph());
	}

	@Override
	public Boolean recordable() {
		return true;
	}
}
