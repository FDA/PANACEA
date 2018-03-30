package com.eng.cber.na.command;

import java.awt.Desktop;
import java.net.URI;

import javax.swing.JOptionPane;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.command.util.BaseCommand;

/****
 * The command pattern design to show the User Manual document.
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */

public class ViewDocumentCommand extends BaseCommand{
	@Override
	public void execute(String name) {
		String docPath = NetworkAnalysisVisualization.getAppDir() + "/PANACEA.chm";
		try {
			Desktop.getDesktop().browse(new URI(docPath));
		}
		catch(Exception io) {
			JOptionPane.showMessageDialog(null, "Unable to start help system.  Cannot open file: " + docPath +".", "Help System Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	public Boolean recordable() {
		return false;
	}
}
