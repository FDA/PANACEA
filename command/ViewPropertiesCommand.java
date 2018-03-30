package com.eng.cber.na.command;

import javax.swing.JOptionPane;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.command.util.BaseCommand;
import com.eng.cber.na.event.NetworkPropertiesPanel;

/****
 * The command pattern design to show the dialog window with
 * the properties for the current network.
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */

public class ViewPropertiesCommand extends BaseCommand{
	
	@Override
	public void execute(String name) {
		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
		JOptionPane.showMessageDialog(nv, new NetworkPropertiesPanel(nv), "Network Description", JOptionPane.INFORMATION_MESSAGE);
	}

	@Override
	public Boolean recordable() {
		return false;
	}
}
