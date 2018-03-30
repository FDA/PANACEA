package com.eng.cber.na.graph;

import java.awt.Color;
import java.io.Serializable;
import java.util.Set;
import java.util.TreeMap;

/**
 * A generic implementation of a node (or vertex) object for a
 * graph.  The node has a name (or id) and, often, a set of
 * associated reports.  For nodes in a report network, the
 * "reports" that are found here are actually the elements (PTs
 * and vaccines).  The node can also carry cluster information:
 * the index of the cluster it belongs to, and a color that it
 * should be displayed as.
 * 
 * Nodes are linked by edges to define graphs.
 * 
 * @author Guangfan (Geoffrey) Zhang
 *
 */
public  class GeneralNode implements Serializable, Graph_Object, Comparable<GeneralNode> {
	public Object id;
	public Color cluster_color = null;
	public int cluster = -1;
	public String objType = "Node";
	
	static int nodeCount = 0;
	public int idInt;

	public  TreeMap<Object,Integer> reportDegree ;
	
	public GeneralNode(Object id){
		this.id = id;
		this.idInt = nodeCount++;
	}
	
	public String toString(){
		return "V" + id;
	}
	public String getID() {
		return id.toString();
	}

	public void setClusterColor(Color color)
	{
		cluster_color = color;
	}

	public Color getClusterColor()
	{
		return cluster_color;
	}
    public int compareTo(GeneralNode node)
    {
    	return id.toString().compareTo(node.id.toString());
    }
	public void setCluster(int cluster)
	{
		this.cluster = cluster;
	}
	
	public int getCluster()
	{
		return cluster;
	}
	public Object getObject()
	{
		return id;
	}
	
	public int getInterpolatedDegree(Object reportId) {
		return -1;
	}
	
	public void appendReport(Object VAERS_ID) {
		return;
	}
	
	public void setDegree(Object VAERS_ID, int degree) {
		return;
	}

	public Set<?> getReports() {
		if (reportDegree != null )
			return reportDegree.keySet();
		else
			return null;
	}
	
	public TreeMap<?, Integer> getReportDegreeMap() {
		return reportDegree;
	}
	
	@Override
	public ObjectType getObjectType(){
		return ObjectType.NODE;
	}
}