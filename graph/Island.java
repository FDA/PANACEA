package com.eng.cber.na.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * An Island is a Java implementation of the hierarchical
 * data structure described by Batagelj and Zaversnick (2004) -- 
 * each island contains two subislands, which may consist of
 * one or more other islands.  Islands also have "ports"; a
 * port is the edge that connects the two subislands together 
 * (equivalent to the highest-weight edge that connects the
 * two subislands).  Islands are identified through creation
 * of a maximum spanning tree using Kruskal's algorithm;
 * they are the groupings of nodes that come together over
 * the course of building up the spanning tree.
 *
 * See:
 * Batagelj, V. and Zaversnik, M. (2004). Islands. COSIN Meeting at the 
 * University of Karlsruhe. Retrieved at 
 * <http://vlado.fmf.uni-lj.si/pub/networks/doc/mix/islands.pdf>.
 * 
 */
public class Island implements Serializable{
	private static Integer numIslands = 0;
	
	private GeneralGraph parent; 
	
	private Integer id;
	private List<GeneralNode> nodes;
	private GeneralEdge port;
	private Boolean regular;
	private Island subisland1;
	private Island subisland2;

	public Island(GeneralNode n, GeneralGraph p) {
		id = numIslands;
		numIslands++;
		
		parent = p;
		
		nodes = new ArrayList<GeneralNode>();
		nodes.add(n);
		
		port = null;
		regular = null;
		subisland1 = null;
		subisland2 = null;
	}
	
	public Island(Island sub1, Island sub2, GeneralEdge currentEdge, GeneralGraph p) throws IllegalArgumentException {
		// Ensure data input integrity
		GeneralNode u = p.getFrom(currentEdge);
		GeneralNode v = p.getTo(currentEdge);
		Boolean uInSub1 = sub1.getNodes().contains(u);
		Boolean uInSub2 = sub2.getNodes().contains(u);
		Boolean vInSub1 = sub1.getNodes().contains(v);
		Boolean vInSub2 = sub2.getNodes().contains(v);
		
		if (sub1.getParent() != sub2.getParent()) {
			throw new IllegalArgumentException("New islands must combine existing islands from the same graph");
		}
		if (! (uInSub1 && vInSub2 || uInSub2 && vInSub1)) {
			throw new IllegalArgumentException("currentEdge must connect sub1 and sub2");
		}
		
		// Set ID
		id = numIslands;
		numIslands++;
		
		// Set parent
		parent = p;
		
		// Set nodes
		nodes = new ArrayList<GeneralNode>(sub1.getNodes());
		nodes.addAll(sub2.getNodes());
		
		// Set port
		port = currentEdge;
		
		// Set subislands
		subisland1 = sub1;
		subisland2 = sub2;
		
		// Set regularity
		regular = null;
		
		// Update regularity for subislands
		GeneralEdge subPort1 = sub1.getPort();
		if (subPort1 == null || (Double) subPort1.getWeight() > (Double)currentEdge.getWeight()) {
			sub1.setRegular(true);
		}
		else {
			sub1.setRegular(false);
		}
		GeneralEdge subPort2 = sub2.getPort();
		if (subPort2 == null || (Double) subPort2.getWeight() > (Double) currentEdge.getWeight()) {
			sub2.setRegular(true);
		}
		else {
			sub2.setRegular(false);
		}

		
	}
	
	public List<GeneralNode> getNodes() {
		return nodes;
	}
	
	public GeneralEdge getPort() {
		return port;
	}
	
	protected GeneralGraph getParent() {
		return parent;
	}
	
	public void setRegular(Boolean b) {
		regular = b;
	}
	
	public boolean containsNode(GeneralNode node) {
		return nodes.contains(node);
	}
	
	public Integer getSize() {
		try{
			return nodes.size();
		}
		catch(NullPointerException e){
			throw new NullPointerException("Cannot calculate size of empty nodes set");
		}
	}
	
	public Boolean isRegular() {
		if (regular == null){
			return false;
		}
		return regular;
	}
	
	public Island getSub1() {
		return subisland1;
	}
	
	public Island getSub2() {
		return subisland2;
	}
	
	@Override
	public String toString() {
		return new String("island " + id + " containing nodes " + nodes.toString());
	}
	
	public Integer getID() {
		return id;
	}
}
