package com.eng.cber.na.graph;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A class with several methods for calculating/setting information
 * for a graph object.  All of the operations here should be fast
 * enough to not be obtrusive.  Very time-consuming calculations,
 * such as betweenness, are run on separate threads.
 * 
 */
public class BasicGraphDataCalculator {

	private GeneralGraph graph;
	
	public BasicGraphDataCalculator(GeneralGraph graph) {
		this.graph = graph;
	}
	
	/** Runs a graph traversal from each node to find what it's connected
	 * to.  Any group of one or more nodes that is not connected to other
	 * groups is an individual component.
	 */
	public void identifyComponents() {
		Map<Integer,Set<GeneralNode>> componentToNodes = new HashMap<Integer,Set<GeneralNode>>();
		Map<GeneralNode,Integer> nodeToComponent = new HashMap<GeneralNode,Integer>();
		int componentIndex = 0;
		
		// Check every node in the graph
		for (GeneralNode node : graph.getVertices()) {
			// If this node is not in any known component, then create a new component
			if (!nodeToComponent.containsKey(node)) {
				componentIndex++;

				// Find all nodes connected to this node, no matter how many
				// steps away.  They will all be in the same component.
				HashSet<GeneralNode> nodesInComponent = new HashSet<GeneralNode>(graph.getVertexCount()/2);
				nodesInComponent.add(node);
				ArrayDeque<GeneralNode> unvisitedNeighbors = new ArrayDeque<GeneralNode>(graph.getVertexCount()/2);
				unvisitedNeighbors.add(node);
				
				while (!unvisitedNeighbors.isEmpty()) {
					GeneralNode currentNode = unvisitedNeighbors.poll();
										
					for (GeneralNode currentNeighbor : graph.getNeighbors(currentNode)) {
						if (!nodesInComponent.contains(currentNeighbor)) {
							nodesInComponent.add(currentNeighbor);
							unvisitedNeighbors.add(currentNeighbor);
						}
					}
				}
				
				for(GeneralNode n : nodesInComponent ) {
					nodeToComponent.put(n, componentIndex);
				}
				componentToNodes.put(componentIndex, nodesInComponent);	
			}
		}
		
		graph.setComponentToNodes(componentToNodes);
		graph.setNodeToComponent(nodeToComponent);	
	}

	/** Calculate min and max for Degree, Strength, and Report Count. **/
	public void identifyMinAndMaxForVertices() {
		Integer minDegree = Integer.MAX_VALUE, maxDegree = Integer.MIN_VALUE;
		Integer minReportCount = Integer.MAX_VALUE, maxReportCount = Integer.MIN_VALUE;
		Double minStrength = Double.MAX_VALUE, maxStrength = Double.MIN_VALUE;
		
		for (GeneralNode v : graph.getVertices()) {
			if (maxDegree < graph.getDegree(v)) {
            	maxDegree = graph.getDegree(v);
            }
            if (minDegree > graph.getDegree(v)) {
            	minDegree = graph.getDegree(v);
            }
            
            if (v.getReports() != null ){
            	if (maxReportCount < v.getReports().size()) {
            		maxReportCount = v.getReports().size();
            	}
            	if (minReportCount > v.getReports().size()) {
            		minReportCount = v.getReports().size();
            	}
            }
            else{
            	maxReportCount = 0;
            	minReportCount = 0;
            }
			
            if (maxStrength < graph.getStrength(v)){
            	maxStrength = graph.getStrength(v);
            }
            if (minStrength > graph.getStrength(v)){
            	minStrength = graph.getStrength(v);
            }
		}
		
		if (graph.getEdgeCount() == 0) {
			graph.setMaxDegree(0);
			graph.setMinDegree(0);
			graph.setMaxReportCount(maxReportCount);
			graph.setMinReportCount(minReportCount);
			graph.setMaxStrength(0.0);
			graph.setMinStrength(0.0);
			return;
		}
		
		graph.setMaxDegree(maxDegree);
		graph.setMinDegree(minDegree);
		graph.setMaxReportCount(maxReportCount);
		graph.setMinReportCount(minReportCount);
		graph.setMaxStrength(maxStrength);
		graph.setMinStrength(minStrength);
	}

	/** Finds the min and max edge weight in the graph and notifies the graph **/
	public void identifyMinAndMaxForEdges() {
		Integer minWeight = Integer.MAX_VALUE, maxWeight = Integer.MIN_VALUE;

		if (graph.getEdgeCount() == 0) {
			graph.setMaxWeight(0.0);
			graph.setMinWeight(0.0);
			return;
		}
		
		for (GeneralEdge e : graph.getEdges()) {
			if (maxWeight < (int)e.getWeight()) {
				maxWeight = (int) e.getWeight();
			}
			if (minWeight > e.getWeight()) {
				minWeight = (int) e.getWeight();
			}
		}
		
		graph.setMaxWeight(maxWeight);
		graph.setMinWeight(minWeight);
	}

	/** Prepares the Node and Edge Display Transformers for the graph by mapping every node and edge to a true value. */
	public void setAllDisplaysToTrue() {
		Map<GeneralNode, Boolean> nodeMap = new HashMap<GeneralNode, Boolean>();
		Map<GeneralEdge, Boolean> edgeMap = new HashMap<GeneralEdge, Boolean>();
		
		for (GeneralNode node : graph.getVertices())
			nodeMap.put(node, true);
		
		for (GeneralEdge edge : graph.getEdges())
			edgeMap.put(edge, true);
		
		graph.setNodeDisplayTransformer(nodeMap);
		graph.setEdgeDisplayTransformer(edgeMap);
	}
	
}
