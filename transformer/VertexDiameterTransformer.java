package com.eng.cber.na.transformer;

import org.apache.commons.collections15.Transformer;

import com.eng.cber.na.graph.FDAGraph;
import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.graph.GeneralNode;
import com.eng.cber.na.vaers.VAERS_Node;

/**
 * Vertex diameter transformers map vertices to 
 * their diameters. A variety of static vertex diameter
 * transformers extend the abstract class, each
 * of which uses a different property of the vertex
 * to define the vertex's diameter.
 *
 */
public class VertexDiameterTransformer {	

	public static final Integer MAX_SIZE = 30;
	public static final Double PROP_ALLOWED_TO_VARY = 0.8;
	
	public static int getLinearScaledDiameter(Double observed, Double min, Double max) {
		Double observedInExcessOfMin = observed - min;
		Double totalRange = max - min;
		Double increaseOverMin = (observedInExcessOfMin / totalRange);
		
		Double proportionHeldConstant = 1 - PROP_ALLOWED_TO_VARY;
		
		Double proportionOfMax = proportionHeldConstant + PROP_ALLOWED_TO_VARY*increaseOverMin;
		Double scaledVal = proportionOfMax * MAX_SIZE;
		return (int)Math.round(scaledVal);
	}
	
	/**
	 * The abstract diameter transformer provides the basic
	 * constructor and requires that implementing classes define 
	 * a transform function that maps the node to an integer 
	 * value representing the diameter.
	 *
	 */
	private static abstract class AbstractTransformer implements Transformer<GeneralNode, Integer> {
		
		protected GeneralGraph g;
		
		protected AbstractTransformer(GeneralGraph g) {
			this.g = g;
		}
		
		@Override
		public abstract Integer transform(GeneralNode n);
	}

	/**
	 * The BetweennessTransformer linearly interpolates to size 
	 * nodes such that the smallest observed betweenness node
	 * is very small and the largest observed betweenness node is
	 * the maximum allowable vertex size.
	 *
	 */
	public static class BetweennessTransformer extends AbstractTransformer {

		private Double maxBetweenness,minBetweenness;
		
		public BetweennessTransformer(GeneralGraph g) {
			super(g);
			maxBetweenness = g.getMaxBetweenness();
			minBetweenness = g.getMinBetweenness();
		}

		@Override
		public Integer transform(GeneralNode n) {
			if (maxBetweenness - minBetweenness < 0.000001) {
				return MAX_SIZE;
			}
			if(n.getID().equals("ReferenceDocument"))
				return getLinearScaledDiameter(maxBetweenness, minBetweenness, maxBetweenness);
			else
				return getLinearScaledDiameter(g.getBetweenness(n), minBetweenness, maxBetweenness);
		}
	}
	
	/**
	 * The ClosenessTransformer linearly interpolates to size 
	 * nodes such that the smallest observed closeness node
	 * is very small and the largest observed closeness node is
	 * the maximum allowable vertex size.
	 *
	 */
	public static class ClosenessTransformer extends AbstractTransformer {

		private Double maxCloseness,minCloseness;
		
		public ClosenessTransformer(GeneralGraph g) {
			super(g);
			maxCloseness = g.getMaxCloseness();
			minCloseness = g.getMinCloseness();
		}
		
		@Override
		public Integer transform(GeneralNode n) {
			if (maxCloseness - minCloseness < 0.000001) {
				return MAX_SIZE;
			}
			
			if(n.getID().equals("ReferenceDocument"))
				return getLinearScaledDiameter(maxCloseness, minCloseness, maxCloseness);
			else
				return getLinearScaledDiameter(g.getCloseness(n), minCloseness, maxCloseness);
		}
	}
	
	/**
	 * The DegreeTransformer linearly interpolates to size 
	 * nodes such that the smallest observed degree node
	 * is very small and the largest observed degree node is
	 * the maximum allowable vertex size.
	 *
	 */
	public static class DegreeTransformer extends AbstractTransformer {
		
		private Integer maxDegree,minDegree;
		
		public DegreeTransformer(GeneralGraph g) {
			super(g);
			maxDegree = g.getMaxDegree();
			minDegree = g.getMinDegree();
		}

		@Override
		public Integer transform(GeneralNode n) {
			if (maxDegree - minDegree < 0.000001) {
				return MAX_SIZE;
			}
			
			
			if(n.getID().equals("ReferenceDocument"))
				return getLinearScaledDiameter(new Double(maxDegree), new Double(minDegree), new Double(maxDegree));
			else
				return getLinearScaledDiameter(new Double(g.degree(n)), new Double(minDegree), new Double(maxDegree));
		}
	}
	
	/**
	 * The ReportCountTransformer linearly interpolates to size 
	 * nodes such that the smallest observed report count node
	 * is very small and the largest observed report count node is
	 * the maximum allowable vertex size.
	 *
	 */
	public static class ReportCountTransformer extends AbstractTransformer {

		private Integer maxReportCount,minReportCount;
		
		public ReportCountTransformer(GeneralGraph g) {
			super(g);
			maxReportCount = ((FDAGraph)g).getMaxReportCount();
			minReportCount = ((FDAGraph)g).getMinReportCount();
		}

		@Override
		public Integer transform(GeneralNode n) {
			if (maxReportCount- minReportCount< 0.000001) {
				return MAX_SIZE;
			}
			VAERS_Node n1 = (VAERS_Node) n;
			return getLinearScaledDiameter(new Double(n1.getReports().size()), new Double(minReportCount), new Double(maxReportCount));
		}
	}
	
	/**
	 * The StrengthTransformer linearly interpolates to size 
	 * nodes such that the smallest observed strength node
	 * is very small and the largest observed strength node is
	 * the maximum allowable vertex size.
	 *
	 */
	public static class StrengthTransformer extends AbstractTransformer {

		private Double maxStrength,minStrength;
		
		public StrengthTransformer(GeneralGraph g) {
			super(g);
			maxStrength = g.getMaxStrength();
			minStrength = g.getMinStrength();
		}

		@Override
		public Integer transform(GeneralNode n) {
			if (maxStrength - minStrength < 0.000001) {
				return MAX_SIZE;
			}
			if(n.getID().equals("ReferenceDocument"))
				return getLinearScaledDiameter(maxStrength, minStrength, maxStrength);
			else
				return getLinearScaledDiameter(g.getStrength(n), minStrength, maxStrength);
		}
	}
}
