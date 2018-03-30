package com.eng.cber.na.chart;

import java.util.HashMap;
import java.util.Map;

import org.jfree.data.xy.XYSeries;

import com.eng.cber.na.graph.GeneralNode;

/****
 * Extension of JFreeChart's XYSeries that links
 * each value in the series to a node.
 *
 */
@SuppressWarnings("serial")
public class NodeXYSeries extends XYSeries {

	private Map<Double,GeneralNode> xToNode = new HashMap<Double,GeneralNode>();
	
	public NodeXYSeries() {
		super("metric", true, false);
	}
	
	public void add(double x, double y, GeneralNode n) {
		super.add(x,y);
		xToNode.put(x, n);
	}
	
	public GeneralNode getNode(double x) {
		return xToNode.get(x);
	}
}
