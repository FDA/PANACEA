package com.eng.cber.na.command;

import javax.swing.SwingUtilities;

import com.eng.cber.na.command.util.BaseCommand;
import com.eng.cber.na.subgraph.CreateNonIsolateSubgraph;

/****
 * The command pattern design to create a subnetwork without any
 * isolates (nodes with zero edge connections).
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */
public class RemoveIsolates extends BaseCommand{
	
	private static final long serialVersionUID = 1L;
	
	public RemoveIsolates(){
		title = "Remove isolates";
		shortDescription = "Remove isolated nodes and create a subgraph.";
	}
	@Override
	public void execute(String name) {
		SwingUtilities.invokeLater(new CreateNonIsolateSubgraph());
	}
	@Override
	public Boolean recordable() {
		return true;
	}
}
