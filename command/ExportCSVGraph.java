package com.eng.cber.na.command;


import java.io.FileNotFoundException;
import java.io.PrintWriter;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.command.util.BaseCommand;
import com.eng.cber.na.dialog.FileChooserSpecific;
import com.eng.cber.na.graph.GeneralEdge;
import com.eng.cber.na.graph.GeneralNode;

import edu.uci.ics.jung.graph.util.Pair;

/****
 * The command pattern design to export the current network
 * in edgelist format to a .csv file.
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */

public class ExportCSVGraph extends BaseCommand{
	
	@Override
	public void execute(String name) {
		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
		JOptionPane.showMessageDialog(nv, "Edgelist exports maintain the node and edge structure only (report information is excluded).", "Export in Edgelist Format", JOptionPane.INFORMATION_MESSAGE);

		FileChooserSpecific fc = new FileChooserSpecific(".csv");
		if(JFileChooser.APPROVE_OPTION == fc.showSaveDialog(nv)) {
			String path = fc.getFullCorrectPath();
			
			try{
				// Write an edgelist file
				PrintWriter writeViz = new PrintWriter(path);
				for(GeneralEdge edge : nv.getGraph().getEdges()) {
					Pair<GeneralNode> p = nv.getGraph().getEndpoints(edge);
					writeViz.println("\"" + p.getFirst().getID() + "\",\"" + p.getSecond().getID() + "\"," + edge.getWeight());
				}
				writeViz.flush();
				writeViz.close();
			}
			catch (FileNotFoundException ex) {
				JOptionPane.showMessageDialog(nv, "File does not exist.", "File not found", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	@Override
	public Boolean recordable() {
		return true;
	}
}
