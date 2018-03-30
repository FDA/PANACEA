package com.eng.cber.na.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.eng.cber.na.NetworkAnalysisVisualization.WeightingScheme;
import com.eng.cber.na.vaers.VAERS_Edge;
import com.eng.cber.na.vaers.VAERS_Edge.EdgeType;
import com.eng.cber.na.vaers.VAERS_Node;
import com.eng.cber.na.vaers.VAERS_Node.NodeType;

public class BuildGraphWithWeights extends AbstractCreateGraph {
	Set<?> reportIDs;
	Set<?> nodeIDs;
	Map<Object, Double> weightMapping;
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
	
	public BuildGraphWithWeights(GeneralGraph graph, Map<Object, Double> weightMapping, WeightingScheme ws) {
		this(graph, weightMapping, ws, null, null);
	}
	
	public BuildGraphWithWeights(GeneralGraph graph, Map<Object, Double> weightMapping, WeightingScheme ws, 
			Map<Object, Double> selWeightMapping, ArrayList<String> ptList) {
		super(-2);
		this.isNew = true;
		this.graph = graph;
		this.weightMapping =weightMapping ;
		this.weightingScheme = ws;
		this.selWeightMapping = selWeightMapping;
		this.ptList = ptList;
	}
		
	@Override
	protected GeneralGraph getNetwork() throws IllegalArgumentException,
			InstantiationException, IllegalAccessException {

		
		if(graph instanceof FDAGraph)
			newGraph= new FDAGraph(graph.getDual());
		else
			newGraph = new GeneralGraph();
		
		
		Collection<GeneralEdge> edges = graph.getEdges();
		Collection<GeneralNode> nodes = graph.getVertices();

		Iterator<GeneralNode> itNodes = nodes.iterator();
		
		while(itNodes.hasNext()){
			GeneralNode curNode = itNodes.next();
			newGraph.addVertex(curNode);
		}
		
		double weight = 0;
		
		Iterator<GeneralEdge> it = edges.iterator();
		
		double minWeight = 100000, maxWeight=0;
		while(it.hasNext()){
			GeneralEdge edge = it.next();
			weight = weightMapping.get(edge.getID());
			GeneralNode node1 = edge.node1;
			GeneralNode node2 = edge.node2;
			
			
			if (weight > 0){
				if (!newGraph.isNeighbor(node1, node2)) {
					if(edge instanceof VAERS_Edge){
						Set<Object> curReports = (Set<Object>)(((VAERS_Edge)edge).getReports());
						if (curReports.size() > 1)
							edge = new VAERS_Edge(node1, node2, ((VAERS_Edge)edge).getEdgeType(), curReports);
						else
							edge = new VAERS_Edge(node1, node2, ((VAERS_Edge)edge).getEdgeType(), curReports);
					}
					else
						edge = new GeneralEdge(weight, node1, node2 );
						
					newGraph.addEdge(edge, node1, node2);
					edge.setWeight(weight);
				}
			}	
			
			if (weight > maxWeight )
				maxWeight = weight  ;
			if (weight  < minWeight )
				minWeight = weight ;
			
		}

		newGraph.setMinWeight(minWeight);
		newGraph.setMaxWeight(maxWeight);
		itNodes = nodes.iterator();
		
		if (selWeightMapping == null )
			while(itNodes.hasNext()){
				GeneralNode curNode = itNodes.next();
				if (newGraph.getStrength(curNode)== 0)
					newGraph.removeVertex(curNode);
			}
		else{
			Set<Object> nodeObjects = selWeightMapping.keySet();
			GeneralNode refNode = null;
			
			// Add the reference node and associated edges
			if(nodeObjects.contains("ReferenceDocument")){
				refNode =new VAERS_Node("ReferenceDocument", NodeType.REFERENCE, ptList.get(0));
				for(int i = 1; i<ptList.size(); i++)
					refNode.appendReport(ptList.get(i).toLowerCase());
				
				newGraph.addVertex(refNode);
			}
			
			while(itNodes.hasNext()){
				GeneralNode curNode = itNodes.next();
				if (newGraph.getStrength(curNode)== 0 && !nodeObjects.contains(curNode.getObject()))
					newGraph.removeVertex(curNode);
				
				if(nodeObjects.contains(curNode.getObject())&& refNode!= null){
					
					ArrayList<Object> set1 = new ArrayList<Object>((Set<Object>)curNode.getReports());
					set1.retainAll((Set<Object>)refNode.getReports());
					
					GeneralEdge newEdge = new VAERS_Edge(curNode, refNode, EdgeType.SYM2SYM, new HashSet<Object>(set1));
					newEdge.setWeight(selWeightMapping.get(curNode.getObject()));
					newGraph.addEdge(newEdge, curNode, refNode);
				}
			}
			
			
		
		}
		return newGraph;
	}

	@Override
	protected String getName() {
		String name = parent.getName() + "<" + weightingScheme.toString() + ">";
		if (graphName != null )
			name = graphName;
		
		return name;
	}
}
