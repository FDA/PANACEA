package com.eng.cber.na.concurrent.cmm;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import javax.swing.SwingWorker;

import com.eng.cber.na.graph.GeneralEdge;
import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.graph.GeneralNode;

import edu.uci.ics.jung.algorithms.util.MapSettableTransformer;

/***
 * Does post-processing of the results of the graph
 * traversal of CMMMain.
 */
public class CMMPost extends SwingWorker<Object, Object> {

	private List<CMMMain> mainWorkers;
	private GeneralGraph graph;
	
	
	public CMMPost(List<CMMMain> mainWorkers, GeneralGraph graph) {
		this.mainWorkers = mainWorkers;
		this.graph = graph;
	}

	@Override
	protected Object doInBackground() throws Exception {
		
		setProgress(0);
		
		identifyMinAndMaxForNodes();
		setProgress(50);
		
		identifyMinAndMaxForEdges();
		setProgress(99);
		
		return null;
	}
	
	private void identifyMinAndMaxForNodes() {
		Double minBetweenness = Double.MAX_VALUE, maxBetweenness = Double.MIN_VALUE;
		Double minCloseness = Double.MAX_VALUE, maxCloseness = Double.MIN_VALUE; // reversal of sign occurs because unnormalized closeness is really farness
		
		List<GeneralNode> v_lst = new LinkedList<GeneralNode>((graph).getVertices());
		
		MapSettableTransformer<GeneralNode,Double> betweennessTransformer = graph.getBetweennessTransformer();
		MapSettableTransformer<GeneralNode,Double> closenessTransformer = graph.getClosenessTransformer();
		for(ListIterator<GeneralNode> it = v_lst.listIterator(); it.hasNext(); ) {
			GeneralNode v = it.next();
			
			// Divide betweenness by 2 for undirected graphs
			try{
				double betweenness, closeness ;
				if(v.getID().equals("ReferenceDocument")){
					betweenness = 0.0;
					closeness= 0.0;
				}
				else{
					betweenness = betweennessTransformer.transform(v).doubleValue();
					betweennessTransformer.set(v, betweenness / 2);
					closeness = closenessTransformer.transform(v).doubleValue();

					closenessTransformer.set(v, closeness);
				}
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}


            if (maxCloseness < graph.getUnnormalizedCloseness(v)){ 
            	maxCloseness = graph.getUnnormalizedCloseness(v);
            }
            if (minCloseness > graph.getUnnormalizedCloseness(v)){
            	minCloseness = graph.getUnnormalizedCloseness(v);
            }

            if (maxBetweenness < graph.getUnnormalizedBetweenness(v)){
            	maxBetweenness = graph.getUnnormalizedBetweenness(v);
            }
            if (minBetweenness > graph.getUnnormalizedBetweenness(v)){
            	minBetweenness = graph.getUnnormalizedBetweenness(v);
            }
			setProgress((int)(50*((double)it.nextIndex()/graph.getVertexCount())) );
		}
		
		if (graph.getEdgeCount() == 0) {
			graph.setMaxCloseness(0.0);
			graph.setMinCloseness(0.0);
			graph.setMaxBetweenness(0.0);
			graph.setMinBetweenness(0.0);
			return;
		}
		
		graph.setMaxCloseness(maxCloseness);
		graph.setMinCloseness(minCloseness);
		graph.setMaxBetweenness(maxBetweenness);
		graph.setMinBetweenness(minBetweenness);
	}
	
	private void identifyMinAndMaxForEdges() {
		List<GeneralEdge> e_lst = new LinkedList<GeneralEdge>(graph.getEdges());
		
		MapSettableTransformer<GeneralEdge,Double> betweennessEdgeTransformer = graph.getBetweennessEdgeTransformer();
		for(ListIterator<GeneralEdge> it = e_lst.listIterator(); it.hasNext(); ) {
			GeneralEdge e = it.next();
			
			// Divide betweenness by 2 for undirected graphs
			double betweennessEdge = betweennessEdgeTransformer.transform(e).doubleValue();
            betweennessEdgeTransformer.set(e, betweennessEdge / 2);
			
			setProgress((int)(50*((double)it.nextIndex()/e_lst.size())) + 50);
		}

	}
	
}
