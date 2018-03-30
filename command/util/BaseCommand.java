package com.eng.cber.na.command.util;

import java.io.Serializable;
import java.util.Collection;

import com.eng.cber.na.Command;
import com.eng.cber.na.graph.GeneralNode;

/****
 * The command interface -- for the command pattern design.
 *
 * All commands that extend this class must define a title
 * and short description.
 */

public class BaseCommand implements Command, Serializable {
	protected String title = "", shortDescription = "";
	protected Collection<GeneralNode> selectedNodes;
	public BaseCommand(){};
	public Collection<GeneralNode> getSelectedNodes() {
		return selectedNodes;
	}

	public void setSelectedNodes(Collection<GeneralNode> selectedNodes) {
		this.selectedNodes = selectedNodes;
	}

	@Override
	public void execute(String name) {
		
	}

	@Override
	public Boolean recordable() {
		return false;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String getShortDescription() {
		return shortDescription;
	}

	@Override
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	@Override
	public void redo(String name) {
		execute(name);
	}
}
