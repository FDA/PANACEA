package com.eng.cber.na.command;

import java.awt.Cursor;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.eng.cber.na.NACommandActionListener;
import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.command.util.BaseCommand;
import com.eng.cber.na.concurrent.NetworkExecutorService;
import com.eng.cber.na.dialog.ProgressDialog;
import com.eng.cber.na.project.SaveProject;

/****
 * The command pattern design to save the current project
 * to a project file (binary file).
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */

public class SaveProjectCommand extends BaseCommand{
	
	@Override
	public void execute(String name) {
		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
		JFileChooser fc = new JFileChooser(nv.getPrjDir());
		fc.setDialogType((int)JFileChooser.SAVE_DIALOG);
		FileNameExtensionFilter filter;
		
		filter = new FileNameExtensionFilter("Project File (*.prj)","prj");
		fc.setFileFilter(filter);
		int rc = fc.showDialog(null, "Select project File");

		if (rc == JFileChooser.APPROVE_OPTION)
		{
			String path = fc.getSelectedFile().getAbsolutePath();
			String[] exts = ((FileNameExtensionFilter)fc.getFileFilter()).getExtensions();
			if(!path.endsWith(exts[0])) {
				path += "." + exts[0];
			}
			File file = new File(path);
			nv.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			SaveProject sp = new SaveProject(
					file, 
					nv.getGraphTreeModel(),
					nv.getGraphLoader(), 
					nv.getVertexSizeString(), 
					((NACommandActionListener)nv.getCommandActionListener()).getMacro());
					
			sp.addPropertyChangeListener(new ProgressDialog(nv, "Save  Project","Save project: " + file, false, null));
			NetworkExecutorService.submit(sp);
		}
	}

	@Override
	public Boolean recordable() {
		return true;
	}

}
