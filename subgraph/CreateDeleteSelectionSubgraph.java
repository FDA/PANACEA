package com.eng.cber.na.subgraph;

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
 * removes only the nodes that the user has selected
 * and passed as a Collection object.
 *
 */
public class CreateDeleteSelectionSubgraph extends AbstractCreateNodeSubgraph {
	private Collection<GeneralNode> pickedNodes;
	private HashSet<GeneralNode> deletedNodes = new HashSet<GeneralNode>();
	GeneralGraph graph;	
	
	public CreateDeleteSelectionSubgraph() {
		super(-2);
		PickedState<GeneralNode> pickedState = NetworkAnalysisVisualization.getInstance().getNetworkGLVisualizationServer().getPickedVertexState();
		pickedNodes = pickedState.getPicked();
		
		for (GeneralNode n:pickedNodes){
			deletedNodes.add(n);
		}
			
		HashSet<GeneralNode> expandedPickedNodes = new HashSet<GeneralNode>();
		expandedPickedNodes.addAll(NetworkAnalysisVisualization.getInstance().getNetworkGLVisualizationServer().getModel().getGraphLayout().getGraph().getVertices());
	
		for (GeneralNode n:pickedNodes){
			expandedPickedNodes.remove(n);
		}
		pickedState.clear();
		for (GeneralNode n : expandedPickedNodes){
			pickedState.pick(n, true);
		}
		NetworkAnalysisVisualization.getInstance().getNetworkGLVisualizationServer().firePropertyChange("expand_selection",null,null);
	}
	
	@Override
	public GeneralGraph getNetwork() throws IllegalArgumentException, InstantiationException,IllegalAccessException {
		graph = makeSubgraph(new VAERS_Predicates.SelectionPredicate(pickedNodes));
		return graph;
	}
	
	@Override
	protected String getName()  {
		return ("Removed Nodes: " + getSorted(deletedNodes).toString());
	}
}
