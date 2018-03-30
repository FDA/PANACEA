package com.eng.cber.na.command;


import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.command.util.BaseCommand;
import com.eng.cber.na.layout.Layout.LayoutType;

/****
 * The command pattern design to set the layout type for
 * the current network.
 * 
 * For a command to reset the layout by creating a new
 * layout of the same type, see ResetLayout.
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */

public class ResetLayoutCommand extends BaseCommand{
	public Integer selLayoutType = 0;
	

	public ResetLayoutCommand(){
		title = "Set Network Layout";
		shortDescription = "Set network layout";
		String description = "<html> <p>";
		
		description = description +"This algorithm selects one of graph layouts: <br/>";
		LayoutType[] lt = LayoutType.values();
		for (int i = 0; i < LayoutType.values().length; i++ ){
			description = description + "<br/>" + (i) + ":" +lt[i].toString();
		}
					
		shortDescription = description + " </p> </html>";
	}

	@Override
	public void execute(String name) {
		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
		nv.updateGraphLayout();
		selLayoutType = nv.getSelLayoutTypeIndex();
	}

	@Override
	public Boolean recordable() {
		return true;
	}

}
