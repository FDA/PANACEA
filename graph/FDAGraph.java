package com.eng.cber.na.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.eng.cber.na.vaers.VAERS_Edge;
import com.eng.cber.na.vaers.VAERS_Node;

/**
 * FDAGraph describes a graph for the FDA -- that is, a set
 * of edges and nodes, each of which have associated
 * reports.  This class extends UndirectedSparseGraph and has the 
 * specific classes VAERS_Node as V and VAERS_Edge as E.
 */
@SuppressWarnings("serial")
public class FDAGraph extends GeneralGraph {
	
	private VAERS_Node.MedDRA meddraLevel = VAERS_Node.MedDRA.PT;
	private Map<VAERS_Node.MedDRA, Integer> meddraCounts = new EnumMap<VAERS_Node.MedDRA, Integer>(VAERS_Node.MedDRA.class);
	
	private boolean isClustered = false;
	
	public FDAGraph(GeneralGraph another){
		super(another);
	}
	
	public FDAGraph(int dualID) {
		super(dualID);
		multiDualsAllowed = false;
		super.setFDAType(true);
	}
	@Override
	public Set<Object> getReports() {
		Set<Object> reportsInGraph = new HashSet<Object>();
		
		Iterator<GeneralEdge> edgeIterator = getEdges().iterator();
		VAERS_Edge edge; 
		while (edgeIterator.hasNext()) {
			edge = (VAERS_Edge) edgeIterator.next();
			if(edge.getReports() != null )
				if(getEdgeDisplay(edge))
					reportsInGraph.addAll(edge.getReports());
		}
		
		return reportsInGraph;
	}
	
	@Override
	public Set<Object> getTrueReports() {
		Set<Object> reportsInGraph = new HashSet<Object>();
		
		if (this.isDual()){
			for(GeneralNode report: this.getVertices()){
				reportsInGraph.add(report.getObject());
			}
		}
		else{
			reportsInGraph = getReports();
		}
		return reportsInGraph;
	}	
	
	@Override
	public Set<Object> getAllReportsFromNodes() {
		Set<Object> allReportsFromNodes = new HashSet<Object>();
		
		Iterator<GeneralNode> nodeIterator = getVertices().iterator();
		VAERS_Node node; 
		while (nodeIterator.hasNext()) {
			node = (VAERS_Node) nodeIterator.next();
			if(node.getReports() != null )
				if(getNodeDisplay(node))
					allReportsFromNodes.addAll(node.getReports());
		}
		
		return allReportsFromNodes;
	}
	
	@Override
	public ArrayList<GeneralEdge> getEdges(){
		Collection<GeneralEdge> edges = super.getEdges();
		ArrayList<GeneralEdge> edgesArrayList = new ArrayList<GeneralEdge>(edges);
		
		return edgesArrayList;
	}
	public Integer getReportCount() {
		return this.getReports().size();
	}
	
	@Override
	public void setMedDRACounts(Map<Object, Integer> mc) {
		this.meddraCounts = (Map<VAERS_Node.MedDRA, Integer>) ((Map<?, Integer>) mc);
	}
	@Override
	public Map<Object, Integer> getMedDRACounts() {
		return (Map<Object, Integer>) (Map<?, Integer>)this.meddraCounts;
	}
	@Override
	public void setMedDRALevel(Object level) {
		this.meddraLevel = (VAERS_Node.MedDRA) level;
	}
	
	@Override
	public VAERS_Node.MedDRA getMedDRALevel() {
		return meddraLevel;
	}
	
	public boolean isClustered()
	{
		return isClustered;
	}
	
}