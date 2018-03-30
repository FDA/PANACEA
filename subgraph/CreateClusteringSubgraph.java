package com.eng.cber.na.subgraph;

import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.vaers.VAERS_Predicates;


public class CreateClusteringSubgraph extends AbstractCreateNodeSubgraph {	
	int clusterID; 
	String method;
	public CreateClusteringSubgraph(int clusterID) {
		this(null, clusterID, -1);
	}
	
	public CreateClusteringSubgraph(String methodName, int clusterID) {
		this(methodName, clusterID, -1);
	}
	public CreateClusteringSubgraph(String methodName, int clusterID, int rowForParentPath) {
		super(-2);
		this.clusterID = clusterID;
		this.method = methodName;
		this.rowForParentPath = rowForParentPath;
	}
	
	@Override
	protected GeneralGraph getNetwork() throws IllegalArgumentException, InstantiationException, IllegalAccessException {		
		VAERS_Predicates.ClusteringPredicate pred = new VAERS_Predicates.ClusteringPredicate(clusterID);
		GeneralGraph subgraph = super.makeSubgraph(pred);
		
		double[] conductance = new double[1];
		conductance[0] = super.parent.getConductance()[clusterID];
		subgraph.setConductance(conductance);
		
		double[] internalDensity= new double[1];
		internalDensity[0] = super.parent.getInternalDensity()[clusterID];
		subgraph.setInternalDensity(internalDensity);

		double[] expansion = new double[1];
		expansion[0] = super.parent.getExpansion()[clusterID];
		subgraph.setExpansion(expansion);

		double[] cutRatio= new double[1];
		cutRatio[0] = super.parent.getCutRatio()[clusterID];
		subgraph.setCutRatio(cutRatio);

		double[] normalizedCutRatio= new double[1];
		normalizedCutRatio[0] = super.parent.getNormalizedCut()[clusterID];
		subgraph.setNormalizedCut(normalizedCutRatio);
		
		return subgraph;
	}
	
	@Override
	protected String getName() {
		return ("<" + method + ">Cluster ID = " + clusterID );
	}
}