package com.eng.cber.na.command;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.JTextField;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.command.util.BaseCommand;

/****
 * The command pattern design to show the preferences dialog window.
 * 
 * Adding or removing any of the settings here will also require
 * changes in util.PreferenceFileIO for reading/writing the
 * preferences to a file.
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */

public class configCommand extends BaseCommand{
	public configCommand(){
		title = "Set preferences in PANACEA";
		shortDescription = "Pattern-based and Advanced Network Analyzer for Clinical Evaluation and Assessment (PANACEA) Preferences";
	}
	
	@Override
	public void execute(String name) {
		NetworkAnalysisVisualization nv =NetworkAnalysisVisualization.getInstance();
		int maxEdgeSizeToDisplay = nv.getMaxEdgeSizeToDisplay();	
		JTextField txtMaxEdgeSize = new JTextField(maxEdgeSizeToDisplay + "");
		JTextField txtRepaintInterval= new JTextField(nv.getRepaintInterval() + "");
		
		JSlider edgeDarkSlider = new JSlider(0, 255, nv.getEdgeDarkness());
		
		JCheckBox startLogging = new JCheckBox("Detailed Logging Info");
		startLogging.setSelected(nv.isStartLogging());
			
		Object[] message={"Enter Maximum Number of Edges to Display: ", txtMaxEdgeSize, 
				"Frame Interval to Animate Layout (0=No Animation): ",  txtRepaintInterval,
				"Set Edge Darkness: ", edgeDarkSlider,
				"Debug: ", startLogging}; 
		int result = JOptionPane.showConfirmDialog(null, message, "PANACEA Preference Settings", JOptionPane.OK_CANCEL_OPTION);

		if (result == JOptionPane.OK_OPTION){
			nv.setStartLogging(startLogging.isSelected());
			maxEdgeSizeToDisplay = Integer.parseInt(txtMaxEdgeSize.getText());
			if (maxEdgeSizeToDisplay < 10000) {
				JOptionPane.showMessageDialog(null, "Maximum Number of Edges to Display cannot be less than 10,000.  Setting to 10,000.");
				maxEdgeSizeToDisplay = 10000;
			}
			nv.setMaxEdgeSizeToDisplay(maxEdgeSizeToDisplay);
			nv.setRepaintInterval(Integer.parseInt(txtRepaintInterval.getText()));
			nv.setEdgeDarkness(edgeDarkSlider.getValue());
			nv.getNetworkGLVisualizationServer().repaint();
			
			nv.writePreferences();
		}
	}
	
	@Override
	public Boolean recordable() {
		return true;
	}
	
	@Override
	public void redo(String name) {
		execute(name);		
	}
}
