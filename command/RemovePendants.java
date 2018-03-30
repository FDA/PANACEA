package com.eng.cber.na.command;

import javax.swing.SwingUtilities;

import com.eng.cber.na.command.util.BaseCommand;
import com.eng.cber.na.subgraph.CreateNonPendantSubgraph;

/****
 * The command pattern design to create a subnetwork without any
 * pendants (nodes with exactly one edge connection).
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */
public class RemovePendants extends BaseCommand{
	@Override
	public void execute(String name) {
		SwingUtilities.invokeLater(new CreateNonPendantSubgraph());
	}

	@Override
	public Boolean recordable() {
		return true;
	}
}
