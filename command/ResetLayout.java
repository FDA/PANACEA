package com.eng.cber.na.command;


import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.command.util.BaseCommand;
import com.eng.cber.na.layout.Layout.LayoutType;

/****
 * The command pattern design to reset the layout for the
 * current network.
 * 
 * This command is strictly for creating a new layout
 * of the same type as the current view.  It is
 * attached to the "Reset Layout" button.
 * 
 * For the possibility of changing to a different layout,
 * see ResetlayoutCommand.
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */

public class ResetLayout extends BaseCommand{
	public Integer selLayoutType = 0;
	
	public ResetLayout(){
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
		nv.resetGraphLayout();
		selLayoutType = nv.getSelLayoutTypeIndex();
	}

	@Override
	public Boolean recordable() {
		return true;
	}
}
