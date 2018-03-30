package com.eng.cber.na.command;


import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.command.util.BaseCommand;
import com.eng.cber.na.dialog.FileChooserSpecific;

/****
 * The command pattern design to create a log file of activities.
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */

public class SaveLogCommand extends BaseCommand{
	
	@Override
	public void execute(String name) {
		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
		FileChooserSpecific fc = new FileChooserSpecific(".txt", nv.getLogDir());
		if(JFileChooser.APPROVE_OPTION == fc.showSaveDialog(nv)) {
			String path = fc.getFullCorrectPath();
			
			try{
				
				FileUtils.copyFile(new File(nv.getLogFilePath()), new File(path));
				
			}
			catch (IOException ex) {
				JOptionPane.showMessageDialog(nv, "Problem with log file.  It may be open or may not exist.", "File not found", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	@Override
	public Boolean recordable() {
		return true;
	}
}
