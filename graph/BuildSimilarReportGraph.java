package com.eng.cber.na.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.eng.cber.na.NetworkAnalysisVisualization.WeightingScheme;
import com.eng.cber.na.subgraph.AbstractCreateNodeSubgraph;
import com.eng.cber.na.vaers.VAERS_Edge;
import com.eng.cber.na.vaers.VAERS_Edge.EdgeType;
import com.eng.cber.na.vaers.VAERS_Node;
import com.eng.cber.na.vaers.VAERS_Node.NodeType;
import com.eng.cber.na.vaers.VAERS_Predicates;

public class BuildSimilarReportGraph extends AbstractCreateNodeSubgraph {
	Set<?> reportIDs;
	Set<?> nodeIDs;
	GeneralGraph graph;
	WeightingScheme weightingScheme;
	Map<Object, Double> selWeightMapping;
	private String graphName;
	public String getGraphName() {
		return graphName;
	}

	public void setGraphName(String graphName) {
		this.graphName = graphName;
	}

	private GeneralGraph  newGraph; 
	private ArrayList<String> ptList;
	
	public BuildSimilarReportGraph(GeneralGraph graph, WeightingScheme ws) {
		this(graph, ws, null, null);
	}
	
	public BuildSimilarReportGraph(GeneralGraph graph, WeightingScheme ws, 
			Map<Object, Double> selWeightMapping, ArrayList<String> ptList) {
		super(-2);
		this.isNew = true;
		this.graph = graph;
		this.weightingScheme = ws;
		this.selWeightMapping = selWeightMapping;
		this.ptList = ptList;
	}
		
	@Override
	protected GeneralGraph getNetwork() throws IllegalArgumentException,
			InstantiationException, IllegalAccessException {

		Set<Object> nodeObjects = selWeightMapping.keySet();
		Collection<GeneralNode> pickedNodes = new ArrayList<GeneralNode>();
		for(Object obj:nodeObjects){
			pickedNodes.add(graph.findNodeByID(obj.toString()));
		}
		newGraph = makeSubgraph(new VAERS_Predicates.SelectionPredicate(pickedNodes));
		
		GeneralNode refNode = null;

		// Add the reference node and associated edges
		if(nodeObjects.contains("ReferenceDocument")){
			refNode =new VAERS_Node("ReferenceDocument", NodeType.REFERENCE, ptList.get(0));
			for(int i = 1; i<ptList.size(); i++)
				refNode.appendReport(ptList.get(i).toLowerCase());

			newGraph.addVertex(refNode);
		}

		for(GeneralNode node:pickedNodes){
			if(node == null)
				continue;
			ArrayList<Object> set1 = new ArrayList<Object>((Set<Object>)node.getReports());
			set1.retainAll((Set<Object>)refNode.getReports());
			GeneralEdge newEdge = new VAERS_Edge(node, refNode, EdgeType.SYM2SYM, new HashSet<Object>(set1));
			newEdge.setWeight(selWeightMapping.get(node.getObject()));
			newGraph.addEdge(newEdge, node, refNode);
		}
		return newGraph;
	}

	@Override
	protected String getName() {
		String name = "<Weighting>" + weightingScheme.toString();
		if (graphName != null )
			name = graphName;
		
		return name;
	}
}