package com.eng.cber.na.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.SwingWorker;

import com.eng.cber.na.NetworkAnalysisVisualization;

import edu.uci.ics.jung.algorithms.util.MapSettableTransformer;

/** 
* Line Islands Algorithm
* 
* This script assigns line island values to a graph G.  
* Islands are also known as m-slices.
* Islands are densely connected subgraphs of the input 
* graph.
*
* WHY
* By using the line islands, it is possible to sparsen the network
* and view only the portion(s) of the network with particular 
* densities of weighted ties.  Islands are a general
* and efficient approach to determining these high-density regions
* of the network.
*
* SOURCE
* Batagelj, V. and Zaversnik, M. (2004, November 8-9). Islands. 
* COSIN Meeting at the University of Karlsruhe.
* Available <http://vlado.fmf.uni-lj.si/pub/networks/doc/mix/islands.pdf>.
*
* ALGORITHM
* <code>
* islands := {{v} : v in V}
* for each i in islands do i.port := null
* sort L in decreasing order according to w
* for each e(u ; v) in L (in the obtained order) do begin
* 	i1 := island in islands : u in island
* 	i2 := island in islands : v in island
* 	if i1 != i2 then begin
* 		island := new Island()
* 		island.port := e
* 		island.subisland1 := i1
* 		island.subisland2 := i2
* 		islands := islands UNION {island} REMOVE {i1, i2}
* 		i1.regular := i1.port = null OR w(i1.port) > w(e)
* 		i2.regular := i2.port = null OR w(i2.port) > w(e)
* 	end
* 	determine the type of island
* end
* for each i in islands do i.regular := true
*</code>
*
* The above algorithm runs in O(m lg n) time.
* 
* CHANGES TO ALGORITHM
* Instead of sorting edges by weight, this algorithm
* sorts them both by weight and by island height.
*
* ADDITIONAL NOTES
* The port is the line in the maximum spanning tree with the
* smallest value (not necessarily the line in the island
* with the smallest value).
* 
* IN PAJEK
* Having loaded an appropriate network, use the menu items:
* Net > Partitions > Islands > Line Weights
*
* IMPLEMENTATION AUTHORSHIP
* Pamela Toman
* <pamela.toman@fda.hhs.gov>, <toman_pamela@bah.com>
* January 2012
*/
public class GraphIslandCalculator extends SwingWorker implements Serializable{

	GeneralGraph graph;
	private Map<GeneralNode, Integer> islandHeigthMap = new HashMap<GeneralNode, Integer>();
	public GraphIslandCalculator(GeneralGraph graph) {
		this.graph = graph;
	}
	
	public GeneralGraph getGraph() {
		return graph;
	}
	
	@Override
	public Object doInBackground() {
		///////////////////////////
		// Consider the following point in the building of
		// the hierarchy:
		//
		//	   16				  15
		//     / \				  / \
		//  11	  12	13		 14  \
		//  / \   / \   / \		 / \  \
		//	1  2  3  4  5  6  7  8  9  10
		//
		// Then islands object contains the following:
		//      <16, 13, 7, 15>
		// Each of which knows its subislands and the
		// base level nodes it contains.
		///////////////////////////
		
		
		///////////////////////////
		// Generate the hierarchy of islands.
		///////////////////////////
		

		// Create an island for each of the original vertices.
		List<Island> islands = new ArrayList<Island>();
		for (GeneralNode v : graph.getVertices()) {
			Island rawNode = new Island(v, graph);
			islands.add(rawNode);
		}

		// Create a decreasing sorted set of edges, where within
		// all edges of a certain weight, the edges are sorted
		// such that the one that connects to the node highest in height
		// comes first
		List<GeneralEdge> edges = new ArrayList<GeneralEdge>(graph.getEdges());
		Collections.sort(edges, new Comparator<GeneralEdge>() {
			@Override
			public int compare(GeneralEdge e1, GeneralEdge e2) {
				Integer weightComparison = (int)e1.getWeight() - (int)e2.getWeight();
				return weightComparison > 0 ? -1 : weightComparison < 0 ? 1 : 0;	
			}			
		});
		
		MapSettableTransformer<GeneralNode,Integer> islandHeightTransformer = new MapSettableTransformer<GeneralNode, Integer>(islandHeigthMap);
		List<GeneralEdge> mst = new LinkedList<GeneralEdge>();
		
		// For each edge, find the islands to which its nodes belong.
		// If they are different islands, then this edge is a bridge
		// and the islands should be merged.
		NetworkAnalysisVisualization.logger.logp(java.util.logging.Level.INFO,"","","Start to find islands for all " + edges.size() + " edges...");  
		int jj = 0;
		int counter = 1;
		int edgeSize = edges.size();
		for (GeneralEdge edge : edges ){
			jj = jj + 1;
			if (jj == Math.floor(edgeSize/10.0*counter))
			{
				
				NetworkAnalysisVisualization.NALog("Islands found for " + jj + " edges.");
				counter = counter + 1;
			}
			if (jj % (int)((double)edges.size()*0.02) == 0) {
				setProgress((int) ((double)jj/edgeSize*100));
			}
			
			if (edge == null) {
				throw new NullPointerException("Internal data representation issue: edge is unexpectedly null");
			}
			
			GeneralNode u = graph.getFrom(edge);
			GeneralNode v = graph.getTo(edge);
			
			// Get the island to which each node in the edge belongs.
			Island islandForU = null;
			Island islandForV = null;
			for (Island i : islands) {
				if (i.containsNode(u)){
					islandForU = i;
				}
				if (i.containsNode(v)) {
					islandForV = i;
				}
			}
			if (islandForU == null || islandForV == null ){
				throw new RuntimeException("Internal data error -- the islands set should be complete but is missing at least one node");
			}
			
			// If they are different, combine them.
			// Because we need to know the minimal height island
			// in which they would be included, mark those 
			// nodes with the height of the port.
			if (islandForU != islandForV) {
				Island combinedIsland = new Island(islandForU, islandForV, edge, graph);
				islands.add(combinedIsland);
				islands.remove(islandForU);
				islands.remove(islandForV);
				
				if (islandHeightTransformer.transform(u) == null) {
					islandHeightTransformer.set(u, (int)edge.getWeight());
				}
				if (islandHeightTransformer.transform(v) == null) {
					islandHeightTransformer.set(v, (int)edge.getWeight());
				}
				
				// Add edge to the list of edges in MST
				mst.add(edge);
			}
		}
		
		// Set the regularity of each of the final islands to be true.
		for (Island i : islands) {
			i.setRegular(true);
		}

		NetworkAnalysisVisualization.logger.logp(java.util.logging.Level.INFO,"","","All islands found.");
		graph.setIslandHeightTransformer(islandHeightTransformer, islandHeigthMap);
		graph.setIslands(islands);
		graph.setIslandsMSTTransformer(mst); 
		
		return null;
	}

}
