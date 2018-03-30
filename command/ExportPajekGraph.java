package com.eng.cber.na.command;


import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.commons.collections15.Transformer;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.command.util.BaseCommand;
import com.eng.cber.na.dialog.FileChooserSpecific;
import com.eng.cber.na.graph.GeneralEdge;
import com.eng.cber.na.graph.GeneralNode;

import edu.uci.ics.jung.io.PajekNetWriter;

/****
 * The command pattern design to export the current network
 * in Pajek format to a .net file.  
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */

public class ExportPajekGraph extends BaseCommand{
	
	@Override
	public void execute(String name) {
		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
		JOptionPane.showMessageDialog(nv, "Pajek exports maintain the node and edge structure only (report information is excluded).", "Export in Pajek Format", JOptionPane.INFORMATION_MESSAGE);

		FileChooserSpecific fc = new FileChooserSpecific(".net");
		if(JFileChooser.APPROVE_OPTION == fc.showSaveDialog(nv)) {
			String path = fc.getFullCorrectPath();
			
			try{
				PajekNetWriter<GeneralNode, GeneralEdge> pnw = new PajekNetWriter<GeneralNode, GeneralEdge>();
				Transformer<GeneralNode, String> vertexLabeller = new Transformer<GeneralNode, String>() {  			
					@Override
					public String transform(GeneralNode n) {
						return n.getID();
					}
				};
				Transformer<GeneralEdge, Number> edgeWeighter = new Transformer<GeneralEdge, Number>() {  			
					@Override
					public Number transform(GeneralEdge e) {
						return (Number) e.getWeight();
					}
				};
				pnw.save(nv.getGraph(), path, vertexLabeller, edgeWeighter);
			}
			catch(IOException ex) {
				JOptionPane.showMessageDialog(nv, "Problem encountered writing Pajek file. " + ex.getMessage(), "Problem writing Pajek file", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	@Override
	public Boolean recordable() {
		return true;
	}
}
