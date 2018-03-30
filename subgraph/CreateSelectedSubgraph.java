package com.eng.cber.na.subgraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.graph.GeneralNode;
import com.eng.cber.na.vaers.VAERS_Predicates;

import edu.uci.ics.jung.visualization.picking.PickedState;

/**
 * This class produces a network (and knows how to
 * add that network to the UI, through the run()
 * method of the parent AbstractCreateGraph) that 
 * contains only the nodes that the user has selected
 * and passed as a Collection object.
 *
 */
public class CreateSelectedSubgraph extends AbstractCreateNodeSubgraph {
	private Collection<GeneralNode> pickedNodes;
	private String graphName;
	GeneralGraph graph;
	
	public CreateSelectedSubgraph(Boolean userSelectedDelete) {
		super(-2);
		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
		GeneralGraph g = nv.getGraph();
		PickedState<GeneralNode> pickedState = nv.getNetworkGLVisualizationServer().getPickedVertexState();
		Collection<GeneralNode> allNodes = pickedState.getPicked();
		pickedNodes = new ArrayList<GeneralNode>();
		
		for(GeneralNode node:allNodes){
			if(node==null)
				continue;
			
			if(g.getNodeDisplay(node)){
				pickedNodes.add(node);
			}
		}
		if (userSelectedDelete) {
//			Get list of all nodes and trim to just the nodes that were deleted
			HashSet<GeneralNode> allNodesTrim = new HashSet<GeneralNode>();
			allNodesTrim.addAll(NetworkAnalysisVisualization.getInstance().getGraph().getVertices());
			for (GeneralNode n:pickedNodes){
				allNodesTrim.remove(n);
			}
			
			graphName = "Removed Nodes: " + getSorted(allNodesTrim).toString();
		}
		else {
			graphName = "User-Selected: " + getSorted(pickedNodes).toString();
		}
	}

	@Override
	public GeneralGraph getNetwork() throws IllegalArgumentException, InstantiationException,IllegalAccessException {
		graph = makeSubgraph(new VAERS_Predicates.SelectionPredicate(pickedNodes));
		return graph;
	}
	
	@Override
	protected String getName()  {
		return (graphName);
	}
}
