package com.eng.cber.na.command;

import java.io.FileNotFoundException;

import javax.swing.JOptionPane;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.command.util.BaseCommand;
import com.eng.cber.na.dialog.GraphImportBusinessObjectsDialog;
import com.eng.cber.na.graph.BusinessObjectsGraphLoader;
import com.eng.cber.na.graph.GraphLoader;

/****
 * The command pattern design to import a network from a
 * Business Objects file in .csv format.
 * 
 * This command is attached to the menu option for importing a new project.
 * The ImportBOGraphCommand class is used when importing during startup,
 * using the command line arguments.
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */

public class ImportBOGraph extends BaseCommand{
	private String boPath = "";
	private String graphName = "";
	public ImportBOGraph(){
	}
	@Override
	public void execute(String name) {
		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
		
		GraphImportBusinessObjectsDialog importBODialog = GraphImportBusinessObjectsDialog.showDialog(nv);
		if(importBODialog.wasSuccessful()) {				
			boPath =importBODialog.getFilePath(); 
			graphName = importBODialog.getGraphName();
			try {
				nv.cleanNetwork();
				GraphLoader underlyingData = BusinessObjectsGraphLoader.populateBOData(boPath);
				NetworkAnalysisVisualization.NALog("INFO","BO data loaded");
				int networkType = importBODialog.getNetworkType();
				nv.setDualID(networkType);
				if (networkType > 0 )
					underlyingData.generateDual();
				
				nv.setGraphLoaderAndCalculate(underlyingData, graphName, importBODialog.isIslandsCalcChecked(), importBODialog.isBetweenCloseCalcChecked());		
				NetworkAnalysisVisualization.NALog("info","graph loaded");
			} 
			catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} 
			catch (Exception e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(nv, "Error in import.", "Import error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	@Override
	public Boolean recordable() {
		return true;
	}
}
