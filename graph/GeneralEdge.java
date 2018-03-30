package com.eng.cber.na.graph;

import java.io.Serializable;

/**
 * A generic implementation of an edge (a link between two nodes)
 * for a graph.  
 * 
 * The weight of the edge is stored by the edge itself, instead of
 * by the Graph (which is the way JUNG does it).
 * 
 * @author Guangfan (Geoffrey) Zhang
 *
 */
public class GeneralEdge implements Serializable, Graph_Object, Comparable<GeneralEdge> {
	public Object id;
	public double weight;
	static int edgeCount = 0;
	public GeneralNode node1;
	public GeneralNode node2;
	public String objType = "Edge";
	
	public GeneralEdge(double weight){
		this.weight = weight;
		this.id = "" + (edgeCount++);
	}
	public String toString(){
		return "E" + id;
	}
	
    public GeneralEdge(double weight, GeneralNode node1, GeneralNode node2 )
    {
		this.id = "" + edgeCount++;
        this.node1 = node1;
        this.node2 = node2;
        this.weight = weight;
    }

    public int compareTo(GeneralEdge edge)
    {
      return (node1 == edge.node1) ? (node2.id.toString().compareTo(edge.node2.id.toString())) : (node1.id.toString().compareTo(edge.node1.id.toString()));    	
    }
	
	public double getWeight() {
		return weight;
	}	
	public void setWeight(Integer w) {
		weight = w;
	}
	
	public void setWeight(double w) {
		weight = w;
	}

	public Object getID() {
		return id;
	}
	public void incrementWeight() {
		weight++;
	}
	@Override
	public ObjectType getObjectType(){
		return ObjectType.EDGE;
	}
	
	
}
