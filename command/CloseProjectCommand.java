package com.eng.cber.na.command;


import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.command.util.BaseCommand;

/****
 * The command pattern design to close the current project.
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */

public class CloseProjectCommand extends BaseCommand{
	
	@Override
	public void execute(String name) {
		NetworkAnalysisVisualization.getInstance().cleanNetwork();
	}

	@Override
	public Boolean recordable() {
		return false;
	}
}
