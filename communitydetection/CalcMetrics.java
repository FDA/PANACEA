package com.eng.cber.na.communitydetection;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.eng.cber.na.graph.GeneralEdge;
import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.graph.GeneralNode;

/**
 * This class will calculate several metrics for the clusters
 * in a graph.  The graph should have clusters already determined
 * by one of the available clustering algorithms.
 * 
 * For the equations used to calculate these metrics, see:
 *    Leskovec, Lang, and Mahoney 2010
 *    "Empirical Comparison of Algorithms for Network Community Detection"
 *    Section 4.1 
 */
public class CalcMetrics {
	private GeneralGraph gg;
	private double[] conductance = null;
	private double[] expansion = null;
	private double[] internalDensity = null;
	private double[] normalizedCut = null;
	private double[] cutRatio = null;
	
	private List<Map<GeneralNode, Integer>> cluster_list;
	
	public double[] getConductance() {
		return conductance;
	}

	public void setConductance(double[] conductance) {
		this.conductance = conductance;
	}

	public CalcMetrics(GeneralGraph gg, List<Map<GeneralNode, Integer>> cluster_list){
		this.gg = gg;
		this.cluster_list = cluster_list;
	}

	public void calculate(){
		int clusterSize = cluster_list.size();
		int[] sumOfDegree = new int[clusterSize];
		int[] boundarySize = new int[clusterSize];
		int[] ms = new int[clusterSize]; //Number of Edges in a cluster
		int[] ns = new int[clusterSize];
		
		int sumOfDegreeAll = 0;
		GeneralNode node;

		Map<GeneralNode, Integer> current_cluster;
		
		for (int i= 0; i < cluster_list.size(); i++){
			current_cluster = cluster_list.get(i);
			Iterator<GeneralNode> it_cluster = current_cluster.keySet().iterator();
			while (it_cluster.hasNext()){
				node = it_cluster.next();
				sumOfDegree[i] = sumOfDegree[i] + gg.getDegree(node);
			}
			sumOfDegreeAll = sumOfDegreeAll  + sumOfDegree[i];
			ns[i] = current_cluster.size();
		}
		
		
		Iterator<GeneralNode> it = gg.getVertices().iterator();
		
		while(it.hasNext()){
			node = it.next();
			if (node.getID().equals("ReferenceDocument"))
			{
				continue;
			}
				
			Collection<GeneralNode> neighbors = gg.getNeighbors(node);
			for(GeneralNode node2:neighbors){
				if (node2.getID().equals("ReferenceDocument"))
				{
					continue;
				}				
				if (node.getCluster() != node2.getCluster())
					boundarySize[node.getCluster()] = boundarySize[node.getCluster()] + 1;  
			}
		}
		
		Iterator<GeneralEdge> itEdge = gg.getEdges().iterator();
		
		while(itEdge.hasNext()){
			GeneralEdge edge = itEdge.next();
			if (edge.node1.getID().equals("ReferenceDocument"))
			{
				continue;
			}			
			
			if (edge.node2.getID().equals("ReferenceDocument"))
			{
				continue;
			}			
			
			if(edge.node1.getCluster()==edge.node2.getCluster())
			{
				ms[edge.node1.getCluster()] = ms[edge.node1.getCluster()]  + 1; 
			}
		}
		
		conductance = new double[clusterSize];
		expansion = new double[clusterSize];
		internalDensity = new double[clusterSize];
		cutRatio = new double[clusterSize];
		normalizedCut = new double[clusterSize];

		int n = gg.getVertexCount();
		int m = gg.getEdgeCount();
		for (int i= 0; i < cluster_list.size(); i++){
			if(boundarySize[i]+ 2*ms[i] == 0 )
				conductance[i] = 0;
			else
				conductance[i] = boundarySize[i]*1.0/(boundarySize[i]+ 2*ms[i]);
			
			expansion[i] = boundarySize[i]*1.0/ns[i];
			if (ns[i] == 1)
				internalDensity[i]  = 0;
			else
				internalDensity[i] = 1 - ((double)ms[i])/((ns[i]*1.0*(ns[i]-1.0)/2));
			
			cutRatio[i] = boundarySize[i]*1.0/(ns[i]*(n-ns[i]));
			
			if (2*ms[i] + boundarySize[i] == 0 || 2*(m-ms[i]) + boundarySize[i] == 0)
				normalizedCut[i] = 0;
			else
				normalizedCut[i] = boundarySize[i]*1.0/(2*ms[i] + boundarySize[i]) + boundarySize[i] * 1.0/(2*(m-ms[i])+boundarySize[i]);
		}
	}
	public void SetMetricsToGraph()
	{
		gg.setConductance(conductance);
		gg.setExpansion(expansion);
		gg.setNormalizedCut(normalizedCut);
		gg.setInternalDensity(internalDensity);
		gg.setCutRatio(cutRatio);
	}
}