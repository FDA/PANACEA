package com.eng.cber.na.command;

import java.awt.Cursor;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.command.util.BaseCommand;
import com.eng.cber.na.graph.FDAGraph;
import com.eng.cber.na.subgraph.CreateEdgeTypeSubgraph;
import com.eng.cber.na.vaers.VAERS_Edge.EdgeType;

/****
 * The command pattern design to create a subnetwork
 * by extracting only edges of a particular type.
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */
public class CreateCoNetwork extends BaseCommand{
	public EdgeType type = null;
	
	public CreateCoNetwork(){
		this(null);
	}
	
	public CreateCoNetwork(EdgeType type){
		if(type == null )
			type = EdgeType.SYM2SYM;
		
		this.type = type;
		title = "Create Co-Reporting Network";
		shortDescription = "<html> Create one of the different co-reporting networks: VAX2VAX, VAX2SYM or SYM2SYM: <br>"
				+ "VAX2VAX <br>"
				+ "VAX2SYM <br>"
				+ "SYM2SYM <br>"
				+ "G2G </html>";
	}
	
	@Override
	public void execute(String name) {
		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();

		if (nv.getDualState()) {
			JOptionPane.showMessageDialog(nv, "Not applicable to Report Networks.  Please switch to Element Network.", "ERROR", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (!(nv.getGraph() instanceof FDAGraph)) {
			JOptionPane.showMessageDialog(nv, "Cannot do this with a general network.");
			return;
		}
		
		nv.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		SwingUtilities.invokeLater(new CreateEdgeTypeSubgraph(type));
	}
	@Override
	public Boolean recordable() {
		return true;
	}
	
}