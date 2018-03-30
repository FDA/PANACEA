package com.eng.cber.na.command;

import java.awt.Cursor;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.NetworkAnalysisVisualization.WeightingScheme;
import com.eng.cber.na.command.util.BaseCommand;
import com.eng.cber.na.gl.NetworkGLVisualizationServer;
import com.eng.cber.na.graph.BuildSimilarReportGraph;
import com.eng.cber.na.graph.GeneralEdge;
import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.graph.GeneralNode;
import com.eng.cber.na.vaers.VAERS_Node;
import com.eng.cber.na.vaers.VAERS_Node.NodeType;
import com.eng.cber.na.weighting.CalculateLinSimilarity;
import com.eng.cber.na.weighting.Weighting;

import edu.uci.ics.jung.visualization.picking.PickedState;

/****
 * The command pattern design to create a similarity subnetwork
 * with a reference report created from the union of all the
 * terms in the currently selected report(s) in the network.
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */
public class RetrieveSimilarReportsCommand extends BaseCommand{

	@Override
	public void execute(String name) {

		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
		if (!nv.getGraph().isDual()) {
			JOptionPane.showMessageDialog(nv, "This option not supported for element networks.  You must switch\n to a report network and select one or more reports.");
			return;
		}
		if (nv.getGraph().findNodeByID("ReferenceDocument") != null) {
			JOptionPane.showMessageDialog(nv, "Cannot do a second similarity.  Current version does not support using multiple ReferenceDocument.");
			return;
		}
		// Ask for the threshold value for retrieving similar reports
		nv.setWeightingScheme(WeightingScheme.LinSimilarity);
		Map<Object, Set<VAERS_Node>> report_hash;
		report_hash = NetworkAnalysisVisualization.getInstance().getUnderlyingData().getOrigReportHash();
		Weighting weighting = new Weighting(nv.getGraph(), WeightingScheme.LinSimilarity, "SYM");
		weighting.getLinSimWeight(report_hash);

		NetworkGLVisualizationServer<GeneralNode,GeneralEdge> vv = nv.getNetworkGLVisualizationServer();

		ArrayList<String> ptList = new ArrayList<String>();

		PickedState<GeneralNode> pickedNodes = vv.getPickedVertexState();
		GeneralGraph gg = nv.getGraph();
		for (GeneralNode n : pickedNodes.getPicked()){
			if (gg.containsVertex(n)){
				if(gg.isDual()){
					Set<VAERS_Node> nodes = report_hash.get(n.getObject());
					for(VAERS_Node node: nodes){
						if(node.getNodeType() == NodeType.SYM)
							ptList.add(((String)node.getID()).toLowerCase());
					}
				}
			}
		}
		CalculateLinSimilarity similarNet = new CalculateLinSimilarity(ptList, weighting.getInfoForNode(), weighting.getInfoForReport(), 0.0);
		similarNet.run();
		if (similarNet.getSelWeightMapping().size() == 1 && similarNet.getSelWeightMapping().keySet().contains("ReferenceDocument")) {
			JOptionPane.showMessageDialog(nv, "No reports share any common terms with this reference list.");
			nv.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			return;
		}

		BuildSimilarReportGraph newGraph = new BuildSimilarReportGraph(nv.getGraph(), WeightingScheme.LinSimilarity, 
				similarNet.getSelWeightMapping(), ptList);
		SwingUtilities.invokeLater( newGraph);
		String graphName = ""; 
		for(GeneralNode node:pickedNodes.getPicked()){
			graphName = graphName + node.getID() + " "; 
		}
		newGraph.setGraphName(nv.getGraph().getName() + "_SimilarityTo" + graphName);
		
	}
	@Override
	public Boolean recordable() {
		return true;
	}
	
}