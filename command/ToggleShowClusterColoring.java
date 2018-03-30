package com.eng.cber.na.command;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.command.util.BaseCommand;

/****
 * The command pattern design to toggle whether nodes should be
 * shown with their cluster colors or with standard colors.  Does
 * not delete cluster coloring information.  See ResetGraphCommand
 * for that.
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */

public class ToggleShowClusterColoring extends BaseCommand {

	@Override
	public void execute(String name) {
		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
		nv.setShowClusterColoring(!nv.shouldShowClusterColoring());
		nv.getNetworkGLVisualizationServer().repaint();
	}

	@Override
	public Boolean recordable() {
		return false;
	}

}
