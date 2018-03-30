package com.eng.cber.na.concurrent.cmm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.swing.SwingWorker;

import com.eng.cber.na.concurrent.IncrementableHashMap;
import com.eng.cber.na.graph.GeneralEdge;
import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.graph.GeneralNode;

import edu.uci.ics.jung.algorithms.util.MapBinaryHeap;

/**
 * Does a threaded graph traversal in the background.
 *
 */
public class CMMMain extends SwingWorker<Object,Object> {

	private IncrementableHashMap<GeneralNode> betweennessTransformer;
	private	IncrementableHashMap<GeneralNode> closenessTransformer;
	private IncrementableHashMap<GeneralEdge> betweennessEdgeTransformer;
	private IncrementableHashMap<GeneralEdge> linSimilarityEdgeTransformer;
	
	private Set<GeneralNode> componentNodes = new HashSet<GeneralNode>();
	
	private GeneralGraph graph;
	private GeneralNode source_vertex;
	
	public CMMMain(GeneralGraph graph, 
				   GeneralNode source_vertex, 
				   IncrementableHashMap<GeneralNode> betweennessTransformer, 
				   IncrementableHashMap<GeneralNode> closenessTransformer, 
				   IncrementableHashMap<GeneralEdge> betweennessEdgeTransformer,
				   IncrementableHashMap<GeneralEdge> linSimilarityEdgeTransformer) {
		
		this.graph = graph;
		this.source_vertex = source_vertex;
		this.betweennessTransformer = betweennessTransformer;
		this.closenessTransformer = closenessTransformer;
		this.betweennessEdgeTransformer = betweennessEdgeTransformer;
		this.linSimilarityEdgeTransformer = linSimilarityEdgeTransformer;
	}	
	
	public Set<GeneralNode> getComponentNodes() {
		return componentNodes;
	}
	
	// Based in Brandes algorithm.
	@Override
	public Object doInBackground() {
		
		setProgress(0);
		
		final Map<GeneralNode, GraphTraversalData> vertex_data = new HashMap<GeneralNode, GraphTraversalData>();
		for (GeneralNode s : graph.getVertices()) { 
			if(s.getID().equals("ReferenceDocument"))
				continue;
			
			vertex_data.put(s, new GraphTraversalData());
		}
		
		MapBinaryHeap<GeneralNode> unvisitedNodes = new MapBinaryHeap<GeneralNode>(
			new Comparator<GeneralNode>() {
				@Override
				public int compare(GeneralNode v1, GeneralNode v2) {
					return vertex_data.get(v1).distance > vertex_data.get(v2).distance ? 1 : -1;
				}
			});
		
		GraphTraversalData v_data = vertex_data.get(source_vertex);
		v_data.numSPs = 1;
		v_data.distance = 0;
		
		Stack<GeneralNode> reachableAlters = new Stack<GeneralNode>();
		
		unvisitedNodes.offer(source_vertex);
		while (!unvisitedNodes.isEmpty()) {
			GeneralNode w = unvisitedNodes.poll();
			
			componentNodes.add(w);
			
			reachableAlters.push(w);
			GraphTraversalData w_data = vertex_data.get(w);
			for (GeneralEdge e : graph.getOutEdges(w)) {
				GeneralNode x = graph.getOpposite(w, e);
				if(x.getID().equals("ReferenceDocument"))
					continue;
				
				if (x.equals(w))
					continue;
				double wx_weight = 1;

				GraphTraversalData x_data = vertex_data.get(x);
				double x_potential_dist = w_data.distance + wx_weight;
				if (x_data.distance < 0) {
					x_data.distance = x_potential_dist;
					unvisitedNodes.offer(x);
				}

				if (x_data.distance == w_data.distance + 1) {
					x_data.numSPs += w_data.numSPs;
					x_data.incomingEdges.add(e);
				}
			}               
					
			if (w_data.distance > 0) {
				closenessTransformer.increment(source_vertex, 1/w_data.distance);
			}
			
			if(Thread.interrupted()) {
				firePropertyChange("interrupted",null,null);
				return null;
			}
		}
		setProgress(50);
		
		while (!reachableAlters.isEmpty()) {
			GeneralNode x = reachableAlters.pop();
			GraphTraversalData x_data = vertex_data.get(x);

			for (GeneralEdge e : vertex_data.get(x).incomingEdges) {
				GeneralNode w = graph.getOpposite(x, e);
				double partialDependency = 
					vertex_data.get(w).numSPs / x_data.numSPs *
					(1.0 + x_data.dependency);
				vertex_data.get(w).dependency +=  partialDependency;
				
				betweennessEdgeTransformer.increment(e, partialDependency);
			}
			
			if (!x.equals(source_vertex)) {
				betweennessTransformer.increment(x, vertex_data.get(x).dependency);
			}
			
			if(Thread.interrupted()) {
				firePropertyChange("interrupted",null,null);
				return null;
			}
		}	
		vertex_data.clear();
		
		setProgress(99);
		
		return null;
	}
	
	private class GraphTraversalData {
        public double distance;
        // SPs = shortest paths
        public double numSPs;
        public List<GeneralEdge> incomingEdges;
        public double dependency;

        public GraphTraversalData() {
            distance = -1;
            numSPs = 0;
            incomingEdges = new ArrayList<GeneralEdge>();
            dependency = 0;
        }
        
        @Override
        public String toString() {
        	return "[d:" + distance + ", sp:" + numSPs + 
        		", p:" + incomingEdges + ", d:" + dependency + "]\n";
        }
    }

}