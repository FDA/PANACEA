package com.eng.cber.na.command;

import java.util.Map;

import com.eng.cber.na.command.util.BaseCommand;
import com.eng.cber.na.component.SearchableNodeListPanel;
import com.eng.cber.na.dialog.NodeSelectionDialog;
import com.eng.cber.na.graph.GeneralNode;

/****
 * The command pattern design to sort the list of nodes by
 * the selected metric or alphabetically.
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */

public class SortNodesCommand extends BaseCommand{
	private NodeSelectionDialog dialog;
	private Map<GeneralNode, Boolean> sourceNodes;
	public SortNodesCommand(){};
	public SortNodesCommand(String selMetric, Map<GeneralNode, Boolean> sourceNodes ){
		this.sourceNodes = sourceNodes;
	}
	@Override
	public void execute(String name) {
		dialog = NodeSelectionDialog.getInstance();
		SearchableNodeListPanel listPanel = dialog.getSearchableNodeListPanel();
		listPanel.updateList();
		listPanel.revalidate();
	}
	@Override
	public Boolean recordable() {
		return true;
	}

}
