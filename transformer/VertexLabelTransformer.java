package com.eng.cber.na.transformer;

import org.apache.commons.collections15.Transformer;

import com.eng.cber.na.graph.FDAGraph;
import com.eng.cber.na.graph.GeneralNode;
import com.eng.cber.na.vaers.VAERS_Node;


/**
 * Vertex label transformers map vertices to 
 * their string labels. A variety of static vertex label
 * transformers extend the abstract class, each
 * of which uses a different property of the vertex
 * to define the vertex's label.
 * 
 * Only the NameTransformer is currently available
 * to the user through the GUI.
 *
 */
public class VertexLabelTransformer {
	
	/**
	 * The abstract diameter transformer takes care of the basic
	 * constructor and requires that implementing classes define 
	 * a transform function that maps the node to an string 
	 * value representing the label.
	 *
	 */
	private static abstract class AbstractGraphTransformer implements Transformer<GeneralNode, String> {

		protected FDAGraph g;
		
		protected AbstractGraphTransformer(FDAGraph g) {
			this.g = g;
		}
		
		@Override
		public abstract String transform(GeneralNode n);
	}
	
	/**
	 * The BetweennessTransformer gives the formatted 
	 * betweenness value for a node as its label.
	 *
	 */
	public static class BetweennessTransformer extends AbstractGraphTransformer {
		
		public BetweennessTransformer(FDAGraph g) {
			super(g);
		}
		
		@Override
		public String transform(GeneralNode n) {
			return String.format("%1.4f",g.getBetweenness(n));
		}
	}

	/**
	 * The ClosenessTransformer gives the formatted 
	 * closeness value for a node as its label.
	 *
	 */
	public static class ClosenessTransformer extends AbstractGraphTransformer {

		public ClosenessTransformer(FDAGraph g) {
			super(g);
		}

		@Override
		public String transform(GeneralNode n) {
			return String.format("%1.4f",g.getCloseness(n));
		}
	}
	
	/**
	 * The DegreeTransformer gives the formatted 
	 * degree value for a node as its label.
	 *
	 */
	public static class DegreeTransformer extends AbstractGraphTransformer {

		public DegreeTransformer(FDAGraph g) {
			super(g);
		}

		@Override
		public String transform(GeneralNode n) {
			return new Integer(g.degree(n)).toString();
		}
	}
	
	/**
	 * The NameTransformer gives the formatted 
	 * name for a node as its label.
	 *
	 */
	public static class NameTransformer implements Transformer<GeneralNode,String> {
		@Override
		public String transform(GeneralNode node) {
			return node.getID();
		}
	}
	
	/**
	 * The ReportCountTransformer gives the formatted 
	 * report count value for a node as its label.
	 *
	 */
	public static class ReportCountTransformer implements Transformer<GeneralNode,String> {
		@Override
		public String transform(GeneralNode n) {
			VAERS_Node n1 = (VAERS_Node)n;
			return new Integer(n1.getReports().size()).toString();
		}
	}

	/**
	 * The StrengthTransformer gives the formatted 
	 * strength value for a node as its label.
	 *
	 */
	public static class StrengthTransformer extends AbstractGraphTransformer {
		
		public StrengthTransformer(FDAGraph g) {
			super(g);
		}
		
		@Override
		public String transform(GeneralNode n) {
			return g.getStrength(n).toString();
		}
	}
	
	/**
	 * The BlankTransformer produces a blank (empty) label.
	 *
	 */
	public static class BlankTransformer implements Transformer<GeneralNode,String> {
		@Override
		public String transform(GeneralNode node) {
			return "";
		}
	}
}
