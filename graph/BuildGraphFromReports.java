package com.eng.cber.na.graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.vaers.VAERS_Edge;
import com.eng.cber.na.vaers.VAERS_Node;

/**
 * This class builds a graph from all reports specified
 * by the constructing class specifies.  This class is used
 * by the BuildGraphFromAllReports/BuildGraphfromReportedTerms*.
 *
 */
public class BuildGraphFromReports {	
	Set<?> reportIDs;
	Set<?> nodeIDs;
	int dataType = 0;

	public BuildGraphFromReports(Set<?> reportIDs, Set<?> nodeIDs, int dataType)
	{
		this.reportIDs = reportIDs;
		this.nodeIDs = nodeIDs;
		this.dataType = dataType;
	}
	public BuildGraphFromReports(Set<?> reportIDs, Set<?> nodeIDs)
	{
		this.reportIDs = reportIDs;
		this.nodeIDs = nodeIDs;
	}
	
	public BuildGraphFromReports(Set<?> reportIDs) {
		this(reportIDs, null);
	}
	
	protected FDAGraph buildNetwork() throws IllegalArgumentException, InstantiationException, IllegalAccessException {
		FDAGraph newNetwork; 
		if (dataType > 0)
			newNetwork = new FDAGraph(dataType);
		else
			newNetwork = new FDAGraph(0);
		
		Map<?, Set<VAERS_Node>> report_hash = NetworkAnalysisVisualization.getInstance().getUnderlyingData().getReportHash();
		Map<?, VAERS_Node> node_hash = NetworkAnalysisVisualization.getInstance().getUnderlyingData().getOrigNodeHash();
		
		// Add edges between vertices in the same report
		VAERS_Edge edge;
		Iterator<?> reportsToUse = reportIDs.iterator();
		VAERS_Node origNode = null;
		
		boolean dualState = NetworkAnalysisVisualization.getInstance().getDualState() ;
		
		while (reportsToUse.hasNext()) {			
			Object report = reportsToUse.next();
			if (dualState){
				origNode = node_hash.get(report);
				if(!origNode.getNodeType().equals(VAERS_Node.NodeType.values()[dataType-1]))
				{
					continue;
				}
			}
			
			if (!report_hash.containsKey(report)) {
				if(!(report.toString().compareTo("ReferenceDocument") == 0))
						throw new IllegalArgumentException("The report ID " + report + " does not appear in the data provided.");
				else
					continue;
			}
			
			ArrayList<VAERS_Node> report_nodes = new ArrayList<VAERS_Node>(report_hash.get(report));
			
			for (int i = 0; i < report_nodes.size(); i++) {
				VAERS_Node node = report_nodes.get(i);
				if (nodeIDs==null || nodeIDs.contains(node.getObject()))
				{
					// Make sure the node is in the graph
					if (!newNetwork.containsVertex(node)) {
						newNetwork.addVertex(node);
					}
					for (int j = i+1; j < report_nodes.size(); j++) {
						VAERS_Node alter = report_nodes.get(j);
						if (nodeIDs==null || nodeIDs.contains(alter.getObject()))
						{
							if (!newNetwork.containsVertex(alter)) {
								newNetwork.addVertex(alter);
							}
							
							// If they are already connected,
							// update that edge
							if (newNetwork.isNeighbor(node, alter)) {
								edge = (VAERS_Edge) newNetwork.findEdge(node, alter);
								if (edge == null) {
									edge = (VAERS_Edge) newNetwork.findEdge(alter, node);
								}
								edge.appendReport(report);
								edge.incrementWeight();
							}
							// If they are not connected,
							// add an edge
							else {
								if (node.getNodeType().equals(VAERS_Node.NodeType.REPORT)){
									int edgeType = -1;
									edgeType = origNode.getNodeType().ordinal() * 2;
									
									edge = new VAERS_Edge(node, alter, VAERS_Edge.EdgeType.toEnum(edgeType) , report);
								}
								else{
									edge = new VAERS_Edge(node, alter, VAERS_Edge.EdgeType.toEnum(node.getNodeType().ordinal() + alter.getNodeType().ordinal()) , report);
								}
								newNetwork.addEdge(edge, node, alter);
							}
						}
					}
				}
			}
		}
		
		NetworkAnalysisVisualization.logger.logp(java.util.logging.Level.INFO,"","", "Node Size: " + newNetwork.getVertexCount() + "; Edge Size: " + newNetwork.getEdgeCount());
		
		return newNetwork;
	}
}