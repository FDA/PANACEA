package com.eng.cber.na.transformer;

import org.apache.commons.collections15.Transformer;

import com.eng.cber.na.gl.shape.GLCircle;
import com.eng.cber.na.gl.shape.GLShape;
import com.eng.cber.na.gl.shape.GLSquare;
import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.graph.GeneralNode;
import com.eng.cber.na.vaers.VAERS_Node;

/**
 * Vertex shape transformers map vertices to 
 * their shapes, which are dependent on their
 * diameters. A variety of static vertex shape
 * transformers extend the abstract class, each
 * of which uses a different property of the vertex
 * to define the vertex's shape.
 *
 */
public class VertexShapeTransformer {
	
	/**
	 * The abstract transformer maps each node to a shape
	 * of the appropriate polygon shape and appropriate diameter.
	 *
	 */
	private static abstract class AbstractTransformer implements Transformer<GeneralNode,GLShape> {
		protected Transformer<GeneralNode, Integer> diameterTransformer;
		protected AbstractTransformer(Transformer<GeneralNode, Integer> diameterTransformer) {
			this.diameterTransformer = diameterTransformer;
		}
		@Override
		public GLShape transform(GeneralNode n) {
			Integer diameter = diameterTransformer.transform(n);
			if(n instanceof VAERS_Node )
				return ((VAERS_Node)n).getNodeType() == (VAERS_Node.NodeType.VAX) ? new GLSquare(diameter/2) : 
					((VAERS_Node)n).getNodeType() == (VAERS_Node.NodeType.REFERENCE) ? new GLCircle(diameter) : new GLCircle(diameter/2) ;
			else{
				return new GLCircle(diameter/2);
			}
		}
	}
	
	/**
	 * The BetweennessTransformer draws either a square or 
	 * circle of the diameter as specified by betweenness. 
	 *
	 */
	public static class BetweennessTransformer extends AbstractTransformer {
		public BetweennessTransformer(GeneralGraph g) {
			super(new VertexDiameterTransformer.BetweennessTransformer(g));
		}
	}
	
	/**
	 * The ClosenessTransformer draws either a square or 
	 * circle of the diameter as specified by closeness. 
	 *
	 */
	public static class ClosenessTransformer extends AbstractTransformer {
		public ClosenessTransformer(GeneralGraph g) {
			super(new VertexDiameterTransformer.ClosenessTransformer(g));
		}
	}
	
	/**
	 * The DegreeTransformer draws either a square or 
	 * circle of the diameter as specified by degree. 
	 *
	 */
	public static class DegreeTransformer extends AbstractTransformer {
		public DegreeTransformer(GeneralGraph g) {
			super(new VertexDiameterTransformer.DegreeTransformer(g));
		}
	}
	
	/**
	 * The ReportCountTransformer draws either a square or 
	 * circle of the diameter as specified by report count. 
	 *
	 */
	public static class ReportCountTransformer extends AbstractTransformer {
		public ReportCountTransformer(GeneralGraph g) {
			super(new VertexDiameterTransformer.ReportCountTransformer(g));
		}
	}
	
	/**
	 * The StrengthTransformer draws either a square or 
	 * circle of the diameter as specified by strength. 
	 *
	 */
	public static class StrengthTransformer extends AbstractTransformer {
		public StrengthTransformer(GeneralGraph g) {
			super(new VertexDiameterTransformer.StrengthTransformer(g));
		}
	}
}
