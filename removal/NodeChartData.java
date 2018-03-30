package com.eng.cber.na.removal;

import java.util.Collections;
import java.util.List;

import com.eng.cber.na.graph.GeneralNode;
import com.eng.cber.na.removal.NodeChartDataSet.Pair;
/**
 * This class stores a list of nodes-double pairs, where each
 * node is mapped to a value (a nodal metric value).  When
 * a NodeChartData object is constructed, it is sorted
 * such that the data in it proceeds from largest metric
 * value to smallest metric value, for displaying on a line
 * chart.
 *
 */
public class NodeChartData {

	List<Pair<GeneralNode,Double>> rawMetrics;	
	
	/** 
	 * Creates a new NodeChartData object containing a
	 * sorted set of metric values, where the largest
	 * value appears first.
	 * 
	 * @param metrics
	 */
	public NodeChartData(List<Pair<GeneralNode,Double>> metrics) {	
		Collections.sort(metrics);
		Collections.reverse(metrics);
		this.rawMetrics = metrics;	
	}

	public List<Pair<GeneralNode,Double>> getSortedMetrics() {
		return rawMetrics;
	}
	
	public double getMinValue() {
		return rawMetrics.get(rawMetrics.size() - 1).value;
	}
	
	public double getMaxValue() {
		return rawMetrics.get(0).value;
	}
	
	public int getNumItems() {
		return rawMetrics.size();
	}

	public Double getMetricGivenRank(Integer i) throws IllegalArgumentException {
		if (i < 1 || i > rawMetrics.size()) { // Rank starts at 1
			throw new IllegalArgumentException("User-provided rank of " + i + " is not in the range [1, number of nodes].");
		}
		return rawMetrics.get(i - 1).value;
	}
}
