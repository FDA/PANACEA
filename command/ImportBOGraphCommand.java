package com.eng.cber.na.command;

import java.io.FileNotFoundException;

import javax.swing.JOptionPane;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.command.util.BaseCommand;
import com.eng.cber.na.graph.BusinessObjectsGraphLoader;
import com.eng.cber.na.graph.GraphLoader;

/****
 * The command pattern design to import a network from a
 * Business Objects file in .csv format.
 * 
 * This command is used when importing during startup, using
 * the command line arguments.
 * The ImportBOGraph class is attached to the menu option for importing a new project.
 *
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */

public class ImportBOGraphCommand extends BaseCommand{
	private String boPath = "";
	private String graphName = "";
	public ImportBOGraphCommand(String boPath){
		this.boPath = boPath;
		graphName= boPath.substring(boPath.lastIndexOf("\\")+1);
		if(!graphName.equals("") && !graphName.isEmpty())
			graphName = graphName.substring(0, graphName.indexOf("."));
	}
	
	@Override
	public void execute(String name) {
		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
		try {
			nv.cleanNetwork();
			GraphLoader underlyingData = BusinessObjectsGraphLoader.populateBOData(boPath);
			NetworkAnalysisVisualization.NALog("info","BO data loaded");
			
			int networkType = nv.getDualID();
			if (networkType > 0 ){
				if(underlyingData.getNodeHash().isEmpty())
					underlyingData.generateDual();
			}
			
			nv.setGraphLoaderAndCalculate(underlyingData, graphName, false, false);		
			
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
	@Override
	public Boolean recordable() {
		return true;
	}
}
