package com.eng.cber.na.command;

import java.awt.Cursor;

import javax.swing.SwingUtilities;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.command.util.BaseCommand;
import com.eng.cber.na.subgraph.CreateEdgeWeightSubgraph;

/****
 * The command pattern design to create a new graph only
 * including edges within a specified range.
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */
public class CreateEdgeWeightSubGraphCommand extends BaseCommand{
	public Integer from, to;
	
	public  CreateEdgeWeightSubGraphCommand(){
		this(0, 999);
	}
	public  CreateEdgeWeightSubGraphCommand(int from, int to){
		title = "Create a subgraph based on edge weights";
		shortDescription = "Create a subgraph based on edge weights in the range of (from, to)";
		this.from = from;
		this.to = to;
	}
	@Override
	public void execute(String name) {
		NetworkAnalysisVisualization.getInstance().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		SwingUtilities.invokeLater(new CreateEdgeWeightSubgraph(from, to));
	}

	@Override
	public Boolean recordable() {
		return true;
	}

}
