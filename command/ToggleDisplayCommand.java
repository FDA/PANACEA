package com.eng.cber.na.command;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.command.util.BaseCommand;

/****
 * The command pattern design to toggle whether the bottom
 * information panel should be shown or not.
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */

public class ToggleDisplayCommand extends BaseCommand{
	@Override
	public void execute(String name) {
		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
		nv.showBottomPanel(!nv.getFlagShowBottomPanel());
		nv.getNetworkGLVisualizationServer().getNetworkScalingControl().reset();
	}

	@Override
	public Boolean recordable() {
		return false;
	}


	@Override
	public void redo(String name) {
		execute(name);
	}
}
