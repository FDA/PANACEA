package com.eng.cber.na.command;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.command.util.BaseCommand;

/****
 * The command pattern design to toggle node label
 * names on and off.
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */

public class DisplayLabelCommand extends BaseCommand{
	public Boolean toggleLabel = false; 
	
	public DisplayLabelCommand(){
		title = "Display Labels";
		shortDescription = title;
	}
	@Override
	public void execute(String name) {
		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
		nv.updateGraphLabels();
	}

	@Override
	public void redo(String name) {
		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
		nv.toggleLabel(toggleLabel);
		nv.updateGraphLabels();
	}
	@Override
	public Boolean recordable() {
		return true;
	}

}