package com.eng.cber.na.command;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.command.util.BaseCommand;
import com.eng.cber.na.concurrent.NetworkExecutorService;
import com.eng.cber.na.dialog.ProgressDialog;
import com.eng.cber.na.project.OpenProject;

/****
 * The command pattern design to open a project file (binary file) that
 * was previously saved by the software.
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */

public class OpenProjectCommand extends BaseCommand{
	
	@Override
	public void execute(String name) {
		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
		JFileChooser fc = new JFileChooser(nv.getPrjDir());
		fc.setDialogType((int)JFileChooser.OPEN_DIALOG);
		FileNameExtensionFilter filter;
		
		filter = new FileNameExtensionFilter("Project File (*.prj)","prj");
		fc.setFileFilter(filter);
		int rc = fc.showDialog(null, "Select a project to Load");
		if (rc == JFileChooser.APPROVE_OPTION)
		{
			File file = fc.getSelectedFile();
			OpenProject op = new OpenProject(file);
			op.addPropertyChangeListener(new ProgressDialog(nv, "Open Project","Open project: " + file, false, null));
			NetworkExecutorService.submit(op);
		}
	}

	@Override
	public Boolean recordable() {
		return false;
	}
}
