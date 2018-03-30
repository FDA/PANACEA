package com.eng.cber.na.dialog;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.eng.cber.na.NetworkAnalysisVisualization;

/**
 * A file chooser for choosing the name of a file to
 * save that checks whether the file already exists
 * and that always opens in the Data folder.
 *
 */
@SuppressWarnings("serial")
public class FileChooserSpecific extends JFileChooser {
	String ext;
	boolean confirmOverwrite = true;
	
	
	public FileChooserSpecific(String extension) {
		this(extension, NetworkAnalysisVisualization.getDataDir());
	}
	
	public FileChooserSpecific(String extension, String defDir) {
		super(defDir);
		this.ext = extension;
		ext = ext.replaceAll("\\.", "");
		FileNameExtensionFilter filter = new FileNameExtensionFilter("." + ext + " files",ext);
		this.setFileFilter(filter);
	}
	
	
    @Override 
    public void approveSelection() { 
        if (isSave() && confirmOverwrite && fileToSaveExists()) { 
            if (confirmOverwrite()) { 
                super.approveSelection(); 
            } 
        } else { 
            super.approveSelection(); 
        } 
    } 

    private boolean isSave() { 
        return getDialogType() == JFileChooser.SAVE_DIALOG; 
    } 
    
    public void setConfirmOverwrite(boolean confirmOverwrite) { 
        this.confirmOverwrite = confirmOverwrite; 
    }
    
    private boolean fileToSaveExists() {
		String path = getFullCorrectPath();
		boolean pathExists = new File(path).exists();
		return pathExists;
    }	
    
    private boolean confirmOverwrite() {
		int result = JOptionPane.showConfirmDialog(this, "Overwrite the existing file?", "Confirm overwrite", JOptionPane.YES_NO_OPTION);
		return (result == JOptionPane.YES_OPTION);
    }
    
    public String getFullCorrectPath() {
		String path = getSelectedFile().getAbsolutePath();
		if (path.indexOf("." + ext) == -1) {
			path = path + "." + ext;
		}
		return path;
    }
}
