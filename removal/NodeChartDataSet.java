package com.eng.cber.na.removal;

import java.util.ArrayList;
import java.util.List;

import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.graph.GeneralNode;

/** 
 * Constructs and maintains multiple NodeChartData objects,
 * one per each metric that can be displayed in a NodeChartPanel.
 * 
 */
public class NodeChartDataSet {
	
	GeneralGraph graph;
	NodeChartData degree;
	NodeChartData betweenness;
	NodeChartData closeness;
	NodeChartData strength;
	
	public NodeChartDataSet(GeneralGraph graph) {	
		this.graph = graph;
		populateData(graph);
		if (graph.areBetweenCloseCalculated()) {
			populateBetweenClose(graph);
		}
	}
	
	public NodeChartData getDegree() {
		return degree;
	}
	
	public NodeChartData getBetweenness() {
		if (betweenness == null) {
			if (graph.areBetweenCloseCalculated())
				populateBetweenClose(graph);
		}
		return betweenness;
	}
	
	public NodeChartData getCloseness() {
		if (closeness == null) {
			if (graph.areBetweenCloseCalculated())
				populateBetweenClose(graph);
		}
		return closeness;
	}
	
	public NodeChartData getStrength() {
		return strength;
	}
	
	private void populateData(GeneralGraph graph) throws IllegalArgumentException {
		List<Pair<GeneralNode,Double>> degreeMetrics = new ArrayList<Pair<GeneralNode,Double>>();
		List<Pair<GeneralNode,Double>> strengthMetrics = new ArrayList<Pair<GeneralNode,Double>>();
		
		for (GeneralNode n : graph.getVertices()) {
			degreeMetrics.add(new Pair<GeneralNode,Double>(n,new Double(graph.getDegree(n))));
			strengthMetrics.add(new Pair<GeneralNode,Double>(n,graph.getStrength(n)));
		}
		
		degree = new NodeChartData(degreeMetrics);
		strength = new NodeChartData(strengthMetrics);
		
	}
	
	private void populateBetweenClose(GeneralGraph graph) {
		List<Pair<GeneralNode,Double>> betweennessMetrics = new ArrayList<Pair<GeneralNode,Double>>();
		List<Pair<GeneralNode,Double>> closenessMetrics = new ArrayList<Pair<GeneralNode,Double>>();
		
		for (GeneralNode n : graph.getVertices()) {
			betweennessMetrics.add(new Pair<GeneralNode,Double>(n,graph.getBetweenness(n)));
			closenessMetrics.add(new Pair<GeneralNode,Double>(n,graph.getCloseness(n)));
		}
		betweenness = new NodeChartData(betweennessMetrics);
		closeness = new NodeChartData(closenessMetrics);
	}
	
	public static class Pair<K,V extends Comparable<V>> implements Comparable<Pair<K,V>> {
		public V value;
		public K key;
		public Pair(K key,V value) {
			this.value = value;
			this.key = key;
		}
		@Override
		public int compareTo(Pair<K,V> o) {
			return value.compareTo(o.value);
		}
	}
}