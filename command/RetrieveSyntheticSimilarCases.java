package com.eng.cber.na.command;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.command.util.BaseCommand;
import com.eng.cber.na.dialog.SyntheticCaseRetrievalDialog;

/**
 * The command pattern design to create a similarity subnetwork
 * with a reference report created by manually selecting all the
 * terms to include from a list.
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */

public class RetrieveSyntheticSimilarCases extends BaseCommand{
	
	public RetrieveSyntheticSimilarCases(){
		title = "Retrieve Similar Documents (Synthesis)";
		shortDescription = title; 
	}

	@Override
	public void execute(String name) {
		NetworkAnalysisVisualization nv =NetworkAnalysisVisualization.getInstance();
		SyntheticCaseRetrievalDialog.showDialog(nv);
	}

	@Override
	public Boolean recordable() {
		return true;
	}
}
