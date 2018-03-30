package com.eng.cber.na.command;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.command.util.BaseCommand;
import com.eng.cber.na.gl.NetworkGLVisualizationServer;
import com.eng.cber.na.graph.GeneralEdge;
import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.graph.GeneralNode;
import com.eng.cber.na.vaers.VAERS_Edge;
import com.eng.cber.na.vaers.VAERS_Node;

/****
 * The command pattern design to save current network information to a file.
 * 
 * There are many options for this command:
 * - JPEG image of the current network
 * - SVG image of the current network (NOT IMPLEMENTED)
 * - Node properties for the current network: names, centrality metrics, reports containing them
 *      -- Selected and unselected nodes will be written to the file in two separate sections
 * - Edge properties for the current network: names, attributes, reports containing them
 *      -- ONLY selected edges will be written to the file
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */

public class SaveNetworkSnapshotCommand extends BaseCommand{
	public String commandType = "CSV";
	public Integer saveInfo = 1;
	
	protected String path;
	protected static enum ExportType {
		CSV,JPEG,SVG, TXT
	}
	// NOTE: These Strings must exactly match the ones used in
	// NetworkAnalysisVisualization to create the command menu items.
	String namesOfVertices = "Names of Nodes...";
	String attributesOfVertices = "Attributes of Nodes...";
	String reportsOfVertices = "Report IDs of Nodes...";
	
	String namesOfEdges= "Names of Edges...";
	String attributesOfEdges = "Attributes of Edges...";
	String reportsOfEdges = "Report IDs of Edges...";
	
	public SaveNetworkSnapshotCommand(){
		title = "Save Network Snapshot";
		shortDescription = "<html> Save network snapshot as one of the followings: <br>"
				+ "JPEG <br> CSV <br> <br>"
				+ "SaveInfo: <br>"
				+ "&nbsp; 1: Names of Vertices <br>"
				+ "&nbsp; 2: Attributes of Vertices <br>"
				+ "&nbsp; 3: Report IDs of Vertices <br>"
				+ "&nbsp; 4: Names of Selected Edges <br>"
				+ "&nbsp; 5: Attributes of Selected Edges <br>"
				+ "&nbsp; 6: Report IDs of Selected Edges <br>"
				+ "&nbsp; 7: Save Tolerance Band Snapshot"
				+ "</html> ";
	}
	public SaveNetworkSnapshotCommand(String actionCommand){
		commandType = actionCommand;
	}

	@Override
	public void redo(String name) {
		switch (saveInfo ){
		case 0:
			break;
		case 1:
			name = namesOfVertices;
			break;
		case 2:
			name = attributesOfVertices;
			break;
		case 3:
			name = reportsOfVertices;
			break;
		case 4:
			name = namesOfEdges;
			break;
		case 5: 
			name = attributesOfEdges;
			break;
		case 6:
			name = reportsOfEdges;
			break;
		default:
			break;
		}
		execute(name);
	}
	
	@Override
	public void execute(String name) {
		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
		if (name.equals(attributesOfVertices) && !nv.getGraph().confirmBetweenClose())
			return;
		
		JFileChooser fc = new JFileChooser(nv.getOutputDir());
		FileNameExtensionFilter filter;
		String approveButtonText;
		FileNameExtensionFilter filter2;
		
		if(commandType.contains("JPEG") || commandType.contains("SVG")) {
			filter = new FileNameExtensionFilter("JPEG File (*.jpg)","jpg");
			approveButtonText = "Set Filename";
		}
		else{
			filter = new FileNameExtensionFilter("Comma-Separated Values File (*.csv)","csv");
			approveButtonText = "Save";
			filter2 = new FileNameExtensionFilter("Text File (*.txt)","txt");
			approveButtonText = "Save";
			fc.setFileFilter(filter2);
		}
	
		fc.setFileFilter(filter);
		
		if(JFileChooser.APPROVE_OPTION == fc.showDialog(nv, approveButtonText)) {
			path = fc.getSelectedFile().getAbsolutePath();
			String[] exts = ((FileNameExtensionFilter)fc.getFileFilter()).getExtensions();
			if(!path.endsWith(exts[0])) {
				path += "." + exts[0];
			}
			
			if(commandType.contains("JPG") || commandType.contains("SVG") || commandType.contains("JPEG")) 
				writeOutputImageFile(path, name);
			else
				writeOutputFile(path, name);
		}
	}
	
	public void writeOutputFile(final String path, String actionCommand) {

		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
		NetworkGLVisualizationServer<GeneralNode, GeneralEdge> vv = nv.getNetworkGLVisualizationServer();
		GeneralGraph g = (GeneralGraph) vv.getGraphLayout().getGraph();
		try {
			PrintWriter write = new PrintWriter(path);
			if (actionCommand.equals(namesOfVertices)) {
				if(g.isClustered())
				{
					write.println("Selected Nodes:");
					write.println("Name,Cluster");
					for (GeneralNode node : vv.getPickedVertexState().getPicked()) {
						write.print(node.getID() + ",");
						write.println(node.getCluster());
					}
					write.println("");
					write.println("Other Nodes:");
					write.println("Name,Cluster");
					Set<GeneralNode> selectedNodes = vv.getPickedVertexState().getPicked();
					for (GeneralNode node : g.getVertices()){
						if (!selectedNodes.contains(node)){
							write.print(node.getID() + ",");
							write.println(node.getCluster());
						}
					}
					
				}
				else
				{
					write.println("Selected Nodes:");
					write.println("Name");
					for (GeneralNode node : vv.getPickedVertexState().getPicked()) {
						write.println(node.getID());
					}
					write.println("");
					write.println("Other Nodes:");
					write.println("Name");
					Set<GeneralNode> selectedNodes = vv.getPickedVertexState().getPicked();
					for (GeneralNode node : g.getVertices()){
						if (!selectedNodes.contains(node)){
							write.println(node.getID());
						}
					}
				}
			} else if (actionCommand.equals(attributesOfVertices)) {
				write.println("Selected Nodes: ");
				write.println("Name,Type,Report Count,Betweenness,Closeness,Degree,Strength");
				for (GeneralNode node : vv.getPickedVertexState()
						.getPicked()) {
					write.print(node.getID() + ",");
					if (node instanceof VAERS_Node){
						write.print(((VAERS_Node)node).getNodeType() + ",");
						write.print(((VAERS_Node)node).getReports().size() + ",");
					}
					write.print(String.format("%1.10f",
							g.getBetweenness(node))
							+ ",");
					write.print(String.format("%1.10f",
							g.getCloseness(node))
							+ ",");
					write.print(g.getDegree(node) + ",");
					write.print(g.getStrength(node));
					write.println();
				}
				write.println("");
				write.println("Other Nodes:");
				write.println("Name,Type,Report Count,Betweenness,Closeness,Degree,Strength");
				Set<GeneralNode> selectedNodes = vv.getPickedVertexState().getPicked();
				for (GeneralNode node : g.getVertices()){
					if (!selectedNodes.contains(node)){
						write.print(node.getID() + ",");
						if (node instanceof VAERS_Node){
							write.print(((VAERS_Node)node).getNodeType() + ",");
							write.print(((VAERS_Node)node).getReports().size() + ",");
						}
						write.print(String.format("%1.10f",
								g.getBetweenness(node))
								+ ",");
						write.print(String.format("%1.10f",
								g.getCloseness(node))
								+ ",");
						write.print(g.getDegree(node) + ",");
						write.print(g.getStrength(node));
						write.println();
					}
				}
			} else if (actionCommand.equals(reportsOfVertices)) {
				write.println("Selected Nodes:");
				write.println("Name,Report ID");
				
				for (GeneralNode node : vv.getPickedVertexState()
						.getPicked()) {
					if (node instanceof VAERS_Node){
						for (Object report : ((VAERS_Node)node).getReports()) {
							write.print(node.getID() + ",");
							write.print(report);
							write.println();
						}
					}
				}
				write.println("");
				write.println("Other Nodes:");
				
				write.println("Name,Report ID");
				Set<GeneralNode> selectedNodes = vv.getPickedVertexState().getPicked();
				for (GeneralNode node : g.getVertices()){
					if (!selectedNodes.contains(node))
						if (node instanceof VAERS_Node){
							for (Object report : ((VAERS_Node)node).getReports()) {
								write.print(node.getID() + ",");
								write.print(report);
								write.println();
							}
						}
				}
			}
			else if (actionCommand.equals(namesOfEdges)) {
				write.println("From,To");
				for (GeneralEdge edge : vv.getPickedEdgeState().getPicked()) {
					write.print(g.getFrom(edge).getID() + ",");
					write.print(g.getTo(edge).getID());
					write.println();
				}
			} else if (actionCommand.equals(attributesOfEdges)) {
				write.println("From,To,Type,Report Count,Weight");
				for (GeneralEdge edge : vv.getPickedEdgeState()
						.getPicked()) {
					write.print(g.getFrom(edge).getID() + ",");
					write.print(g.getTo(edge).getID() + ",");
					if(edge instanceof VAERS_Edge){
						write.print(((VAERS_Edge)edge).getEdgeType() + ",");
						write.print(((VAERS_Edge)edge).getReports().size() + ",");
					}
					write.print(edge.getWeight());
					write.println();
				}
			} else if (actionCommand.equals(reportsOfEdges)) {
				write.println("From,To,Report ID");
				for (GeneralEdge edge : vv.getPickedEdgeState()
						.getPicked()) {
					if (edge instanceof VAERS_Edge)
						for (Object report : ((VAERS_Edge)edge).getReports()) {
							write.print(g.getFrom(edge).getID() + ",");
							write.print(g.getTo(edge).getID() + ",");
							write.print(report);
							write.println();
						}
				}
			}
			write.flush();
			write.close();
		} catch (FileNotFoundException e1) {
			JOptionPane
					.showMessageDialog(
							nv,
							"Please ensure that the file is not open in another application.",
							"File Error", JOptionPane.ERROR_MESSAGE);
		}

	}
	public void writeOutputImageFile(final String path, String actionCommand) {

		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
		NetworkGLVisualizationServer<GeneralNode, GeneralEdge> vv = nv.getNetworkGLVisualizationServer();
		boolean flagSVG = false;
		if(path.toLowerCase().endsWith("svg"))
			flagSVG = true;

		vv.setSnapshotFile(path);
		if (!flagSVG){
			vv.setFlagSave(1);
			vv.display();

			try {
				ImageIO.write(vv.getBufferedImage(), "JPG", new File(path));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else{

			try {
				vv.setFlagSave(2);
				vv.	display();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			String pathNew = path.replaceAll("\\\\",  "//");

			Desktop.getDesktop().browse(new URI(pathNew));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

	}
	@Override
	public Boolean recordable() {
		return true;
	}

}