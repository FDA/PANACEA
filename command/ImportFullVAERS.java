package com.eng.cber.na.command;

import java.awt.Cursor;
import java.io.FileNotFoundException;

import javax.swing.JOptionPane;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.command.util.BaseCommand;
import com.eng.cber.na.dialog.GraphImportDialog;
import com.eng.cber.na.graph.GraphLoader;

/****
 * The command pattern design to import full VAERS network
 * from VAX and PT files.
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */

public class ImportFullVAERS extends BaseCommand{
	private GraphLoader gl;
	
	public ImportFullVAERS(){
	}
	@Override
	public void execute(String name) {
		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();

		nv.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		nv.getStatusLabel().setText("Import a network...");
		GraphImportDialog importDialog = GraphImportDialog.showDialog(nv);
		nv.setCursor(Cursor.getDefaultCursor());
		if(importDialog.wasSuccessful()) {
			try {
				nv.cleanNetwork();
				NetworkAnalysisVisualization.logger.logp(java.util.logging.Level.INFO,"","","VAX: " + importDialog.getVaxPath());
				NetworkAnalysisVisualization.logger.logp(java.util.logging.Level.INFO,"","","PT: " + importDialog.getSymPath());
				NetworkAnalysisVisualization.logger.logp(java.util.logging.Level.INFO,"","","VAX: " + importDialog.getSymLevel());
				gl = GraphLoader.populateVAERSData(importDialog.getVaxPath(),importDialog.getSymPath(), importDialog.getSymLevel());
				int networkType = importDialog.getNetworkType();
				nv.setDualID(networkType);
				if (networkType > 0 )
					gl.generateDual();

				nv.setGraphLoaderAndCalculate(gl, importDialog.getGraphName(), importDialog.isIslandsCalcChecked(), importDialog.isBetweenCloseCalcChecked());
			} 
			catch (FileNotFoundException e1) {
				e1.printStackTrace();
				nv.getStatusLabel().setText("Network not imported.");
			}
			catch (ArrayIndexOutOfBoundsException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(nv, "Unexpected file format.", "File format unexpected", JOptionPane.ERROR_MESSAGE);
				nv.getStatusLabel().setText("Network not imported.");
			}
			catch (NumberFormatException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(nv, "Error in import.  File format is likely invalid.", "Import Error", JOptionPane.ERROR_MESSAGE);
				nv.getStatusLabel().setText("Network not imported.");
			}
			catch (Exception e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(nv, "Error in import.", "Import error", JOptionPane.ERROR_MESSAGE);
				nv.getStatusLabel().setText("Network not imported.");
			}
		}
	}
	@Override
	public Boolean recordable() {
		return false;
	}
}
