package com.eng.cber.na.removal;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;

import javax.swing.SpinnerListModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

/**
 * This class contains and tracks edge weight data for 
 * a network.
 * 
 * Consider the following example, illustrating a network
 * in which there are 4 edges of weight 1, 2 edges of weight
 * 2, and 1 edge of weight 5:
 * 
 *  _4_
 * |   |
 * |   |_2_
 * |   |   |		_1_
 * |___|___|_______|___|
 *   1   2   3   4   5
 * 
 * Then weightToPercentile is of length 7:
 *  [0.0, .57, .86, .86, .86, 1.0, 1.0]
 *    1    2    3    4    5    6    7
 * 
 * And percentileToWeight is a map of the following:
 *  0.00 -> 0
 *  0.57 -> 1
 *  0.86 -> 2
 *  1.00 -> 5
 *  
 *  These two representations both map to the "maximum"
 *  version of the mapping.  It is possible to evaluate
 *  the minimum version through some simple manipulations
 *  on inputs (in the case of weightToPercentile) and
 *  outputs (in the case of percentileToWeight).  Those
 *  manipulations are described by the functions below.
 * 
 */
public class EdgeWeightHistogramData {
	
	static DecimalFormat pf = new DecimalFormat("##%");
	
	double[] rawWeights;
	List<Double> weightToPercentile;
	SortedMap<Double, Integer> percentileToWeight;
	
	public EdgeWeightHistogramData(double[] rw, List<Double> ew, SortedMap<Double, Integer> p) {	
		this.rawWeights = rw;
		this.weightToPercentile = ew;
		this.percentileToWeight = p;		
	}

	public double[] getRawWeights() {
		return rawWeights;
	}
	
	public int getMinWeight() {
		int firstNumIndex = 0;
		while (weightToPercentile.get(firstNumIndex) == 0) {
			firstNumIndex++;
		}
		return firstNumIndex;
	}
	
	public double getPercentileEdgesBetween(int from, int to) {
    	return getPercentileGivenEdgeWeightMax(to) - getPercentileGivenEdgeWeightMin(from);
    }
	
	public int getMaxWeight() {
		return weightToPercentile.size() - 2;
	}
	
	/** 
	 * Returns a SpinnerModel object that is the sequence of possible
	 * edge weights, from 0 until the maximum edge weight. The SpinnerModel
	 * is agnostic as to whether the percentile values are representing
	 * minimum or maximum values.
	 */
	public SpinnerModel getSpinnerModelEdgeWeight() {
		SpinnerModel model =
		        new SpinnerNumberModel(getMinWeight(),
		        					   getMinWeight(),
		                               getMaxWeight(),
		                               1); //step
		return model;
	}
	
	/** 
	 * Returns a SpinnerModel object that only allows the possible
	 * percentiles available in the data.  The SpinnerModel is
	 * agnostic as to whether the percentile values are representing
	 * minimum or maximum values.
	 */
	public SpinnerModel getSpinnerModelPercentile() {
		Collection<Double> possiblePercentilesCol = percentileToWeight.keySet();
		List<String> possiblePercentiles = new ArrayList<String>();
		for (Double perc : possiblePercentilesCol) {
			possiblePercentiles.add(pf.format(perc));
		}
		
		return new SpinnerListModel(possiblePercentiles.toArray());
	}
	
	/** 
	 * Using the "maximum" criteria, get the percentile
	 * for a given edge weight provided as input.
	 */
	public Double getPercentileGivenEdgeWeightMax(Integer i) throws IllegalArgumentException {
		if (i < 0 || i > weightToPercentile.size() - 1) {
			return 0.;
		}
		
		return weightToPercentile.get(i);
	}
	
	/** 
	 * Using the "minimum" criteria, get the percentile
	 * for a given edge weight provided as input.
	 */
	public Double getPercentileGivenEdgeWeightMin(Integer i) throws IllegalArgumentException {
		if (i < 0 || i > weightToPercentile.size()) {
			return 0.;
		}
		
		if (i == 0) {
			return new Double(0);
		}
		else {
			return weightToPercentile.get(i-1);
		}		
	}
	
	/** 
	 * Using the "maximum" criteria, get the edge weight
	 * for a given percentile provided as input.
	 */
	public Integer getEdgeWeightGivenPercentileMax(Double p) throws IllegalArgumentException {
		Integer ew = percentileToWeight.get(p);
		if (ew == null) {
			throw new IllegalArgumentException("Percentile " + p + " is not a valid percentile.");
		}
		return ew;
	}
	
	/** 
	 * Using the "minimum" criteria, get the edge weight
	 * for a given percentile provided as input.
	 */
	public Integer getEdgeWeightGivenPercentileMin(Double p) throws IllegalArgumentException {
		Integer ew = percentileToWeight.get(p);
		if (ew == null) {
			throw new IllegalArgumentException("Percentile " + p + " is not a valid percentile.");
		}
		return (ew + 1);
	}
}
