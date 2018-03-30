package com.eng.cber.na.command;

import java.awt.event.WindowEvent;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.command.util.BaseCommand;

/****
 * The command pattern design to close PANACEA.
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */

public class ExitCommand extends BaseCommand{
	@Override
	public void execute(String name) {
		NetworkAnalysisVisualization.getInstance().dispatchEvent(new WindowEvent(NetworkAnalysisVisualization.getInstance(),WindowEvent.WINDOW_CLOSING));
	}

	@Override
	public Boolean recordable() {
		return true;
	}
}
