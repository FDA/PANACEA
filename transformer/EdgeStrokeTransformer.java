package com.eng.cber.na.transformer;

import java.awt.BasicStroke;
import java.awt.Stroke;

import org.apache.commons.collections15.Transformer;

import com.eng.cber.na.graph.GeneralEdge;
import com.eng.cber.na.graph.GeneralGraph;

/**
 * A class that transforms each edge into a corresponding
 * Stroke object with the correct weight.
 *
 */
public class EdgeStrokeTransformer implements Transformer<GeneralEdge, Stroke> {

	private static final Float maxThickness = 10.0f;
	private double minWeight;
	private double maxWeight;
	
	public EdgeStrokeTransformer(GeneralGraph g) {
		this.minWeight = g.getMinWeight();
		this.maxWeight = g.getMaxWeight();
	}
	
	@Override
	public Stroke transform(GeneralEdge e) {
		Double val = (e.getWeight() - (double)minWeight + 1) / (maxWeight - (double)minWeight + 1) * maxThickness + 1;
		return new BasicStroke(new Float(val)); 
	}
}
