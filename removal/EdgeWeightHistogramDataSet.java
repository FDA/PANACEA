package com.eng.cber.na.removal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import com.eng.cber.na.graph.GeneralEdge;
import com.eng.cber.na.vaers.VAERS_Edge;

/** 
 * Constructs and maintains multiple 
 * EdgeWeightHistogramData objects.
 * 
 * Edge weights MUST be whole numbers.
 *
 */
public class EdgeWeightHistogramDataSet {
	
	EdgeWeightHistogramData all;
	EdgeWeightHistogramData vaxVax;
	EdgeWeightHistogramData symVax;
	EdgeWeightHistogramData symSym;
	
	public EdgeWeightHistogramDataSet(Collection<GeneralEdge> edges) {		
		populateData(edges);
	}
	
	public EdgeWeightHistogramData getAll() {
		return all;
	}

	public EdgeWeightHistogramData getVaxVax() {
		return vaxVax;
	}
	
	public EdgeWeightHistogramData getSymVax() {
		return symVax;
	}
	
	public EdgeWeightHistogramData getSymSym() {
		return symSym;
	}
	
	private void populateData(Collection<GeneralEdge> edges) throws IllegalArgumentException {
		Integer maxAll = 0;
		Integer maxVV = 0;
		Integer maxVS = 0;
		Integer maxSS = 0;
		
		// Find max values
		for (GeneralEdge e : edges){
			if (e.getWeight() > maxAll) {
				maxAll = (int) e.getWeight();
			}
			if (e instanceof VAERS_Edge){
				if (((VAERS_Edge)e).getEdgeType() == VAERS_Edge.EdgeType.VAX2VAX) {
					if (e.getWeight() > maxVV)
						maxVV = (int) e.getWeight();
				}
				else if (((VAERS_Edge)e).getEdgeType() == VAERS_Edge.EdgeType.VAX2SYM) {
					if (e.getWeight() > maxVS)
						maxVS = (int) e.getWeight();
				}
				else if (((VAERS_Edge)e).getEdgeType() == VAERS_Edge.EdgeType.SYM2SYM) {
					if (e.getWeight() > maxSS)
						maxSS = (int) e.getWeight();
				}
			}
		}
		
		List<Integer> allRW = new ArrayList<Integer>();
		List<Double> allEW = new ArrayList<Double>(maxAll);
		SortedMap<Double, Integer> allP = new TreeMap<Double, Integer>();
		Double allNumEdges = 0.;
		
		for (int i = 0; i < 2+maxAll; i++) {
			allEW.add(0.);
		}
				
		List<Integer> vvRW = new ArrayList<Integer>();
		List<Double> vvEW = new ArrayList<Double>(maxVV);
		SortedMap<Double, Integer> vvP = new TreeMap<Double, Integer>();
		Double vvNumEdges = 0.;
		
		for (int i = 0; i < 2+maxVV; i++) {
			vvEW.add(0.);
		}
		
		List<Integer> vsRW = new ArrayList<Integer>();
		List<Double> vsEW = new ArrayList<Double>(maxVS);
		SortedMap<Double, Integer> vsP = new TreeMap<Double, Integer>();
		Double vsNumEdges = 0.;
		
		for (int i = 0; i < 2+maxVS; i++) {
			vsEW.add(0.);
		}
		
		List<Integer> ssRW = new ArrayList<Integer>();
		List<Double> ssEW = new ArrayList<Double>(maxSS);
		SortedMap<Double, Integer> ssP = new TreeMap<Double, Integer>();
		Double ssNumEdges = 0.;
		
		for (int i = 0; i < 2+maxSS; i++) {
			ssEW.add(0.);
		}
		
		
		// First, create the cumulative sum of edge weights in allEW
		// and maintain all the raw weights in rw
		for (GeneralEdge e : edges){
			Integer curWeight = (int) e.getWeight();
			
			// (a) keep track of raw weights
			allRW.add(curWeight);
			// (b) increment corresponding edge weight
			Double prevVal = allEW.get(curWeight);
			allEW.set(curWeight, prevVal+1);
			// (c) increment total weight counter
			allNumEdges += 1;
			
			if (e instanceof VAERS_Edge){
				if (((VAERS_Edge)e).getEdgeType() == VAERS_Edge.EdgeType.VAX2VAX) {
					// (a) keep track of raw weights
					vvRW.add(curWeight);
					// (b) increment corresponding edge weight
					prevVal = vvEW.get(curWeight);
					vvEW.set(curWeight, prevVal+1);
					// (c) increment total weight counter
					vvNumEdges += 1;
				}
				else if (((VAERS_Edge)e).getEdgeType() == VAERS_Edge.EdgeType.VAX2SYM) {
					// (a) keep track of raw weights
					vsRW.add(curWeight);
					// (b) increment corresponding edge weight
					prevVal = vsEW.get(curWeight);
					vsEW.set(curWeight, prevVal+1);
					// (c) increment total weight counter
					vsNumEdges += 1;
				}
				else if (((VAERS_Edge)e).getEdgeType() == VAERS_Edge.EdgeType.SYM2SYM) {
					// (a) keep track of raw weights
					ssRW.add(curWeight);
					// (b) increment corresponding edge weight
					prevVal = ssEW.get(curWeight);
					ssEW.set(curWeight, prevVal+1);
					// (c) increment total weight counter
					ssNumEdges += 1;
				}
			}
		}
		
		// Second, for each place in the corresponding array...
		// (i) divide by (c)
		// (ii) if result is new, add it to hashmap
		all = finalize(allRW, allEW, allP, allNumEdges);
		vaxVax = finalize(vvRW, vvEW, vvP, vvNumEdges);
		symVax = finalize(vsRW, vsEW, vsP, vsNumEdges);
		symSym = finalize(ssRW, ssEW, ssP, ssNumEdges);
	}
	
	/**
	 * Turn the cumulative sum of edge weights into a percentile mapping, create a 
	 * mapping back from the percentiles to the edge weights, and create a
	 * EdgeWeightHistogramData object from the result that can be returned.
	 */
	private EdgeWeightHistogramData finalize(List<Integer> rw, List<Double> ew, SortedMap<Double, Integer> perc, Double numEdges) {
		Double cumSum = 0.0;
		
		// For each place in the edgeweight array...
		for (int i = 0; i < ew.size(); i++) {
			// (i) creating a running total and then divide by (b) to get a percentile rather than a running sum
			Double curVal = ew.get(i);
			cumSum += curVal;
			Double correspondingPercentile = cumSum/numEdges;
			ew.set(i, correspondingPercentile);
			
			// (ii) if result is a new percentile value, add it to hashmap
			if (!perc.containsKey(correspondingPercentile)) {
				perc.put(correspondingPercentile, i);
			}
		}
		
		// Create a double[] from rw
		double[] rawweights = new double[rw.size()];
		for (int i = 0; i < rw.size(); i++) {
			rawweights[i] = rw.get(i);
		}
		
		// Create an EdgeWeightHistogramData object containing this information
		return new EdgeWeightHistogramData(rawweights, ew, perc);
	}
}