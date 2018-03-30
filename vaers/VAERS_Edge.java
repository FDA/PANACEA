package com.eng.cber.na.vaers;

import java.util.HashSet;
import java.util.Set;

import com.eng.cber.na.graph.GeneralEdge;
import com.eng.cber.na.graph.GeneralNode;

/**
 * A VAERS_Edge is an edge object. In PANACEA, an edge 
 * must connect two (and only two) nodes.  Edges
 * have types, weights, and sets of reports
 * that attest the relationship represented
 * by the edge.
 * 
 * The way this class is currently set up,
 * the weight of an edge belongs to the edge itself,
 * rather than to the Graph in which it takes part.
 * This contrasts with the way JUNG generics track 
 * the edges -- the JUNG generics keep the weight as part of
 * a SettableTransformer associated with the *graph*,
 * so that the node and edge objects can be reused
 * in multiple graphs.
 * 
 */
public class VAERS_Edge extends GeneralEdge implements VAERS_Object {
	private Set<Object> reports;
	
	public static enum EdgeType {
		VAX2VAX, VAX2SYM, SYM2SYM, G2G;
		
		public static EdgeType toEnum(int i) {
			return values()[i];
		}
	}
	
	private EdgeType type;
	
	public VAERS_Edge(GeneralNode node1, GeneralNode node2, EdgeType type, Object VAERS_ID) {
		super(1, node1, node2);
		this.id = node1.getID() + " to " +node2.getID(); // uses word "to" because node IDs may be #s
		this.type = type;
		reports = new HashSet<Object>();
		
		reports.add(VAERS_ID);
		weight = 1;
	}
	
	public VAERS_Edge(GeneralNode node1, GeneralNode node2, EdgeType type, Set<Object> r) {
		super(1, node1, node2);
		this.id = node1.getID() + " to " +node2.getID(); // uses word "to" because node IDs may be #s
		this.type = type;
		reports = new HashSet<Object>(r);
		weight = 1;
	}
	
	public VAERS_Edge(String id1, String id2, EdgeType type, Set<Object> r) {
		super(1);
		this.id = id1 + " to " +id2;
		this.type = type;
		reports = new HashSet<Object>(r);
		
		weight = 1;
	}
	
	
	@Override
	public String toString() {
		return"Type: " + type + " Value: " + id + " Number of reports " + reports.size() + " First report " + reports.iterator().next();	
	}

	/* 
	 * The weight on the edge is not necessarily the size 
	 * of the report object (for instance, consider a 
	 * triangular weight graph, where the associated reports
	 * for an edge have not changed, but the weight has been
	 * altered).
	 */

	public EdgeType getEdgeType() {
		return type;
	}
	
	@Override
	public Set<?> getReports() {
		return reports;
	}
	
	@Override
	public void appendReport(Object VAERS_ID) {
		reports.add(VAERS_ID);
	}
	
}
