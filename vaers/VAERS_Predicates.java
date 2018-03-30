package com.eng.cber.na.vaers;

import java.util.Collection;

import org.apache.commons.collections15.Predicate;

import com.eng.cber.na.graph.GeneralEdge;
import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.graph.GeneralNode;

/**
 * VAERS_Predicates contains a variety of static
 * classes that allow filtering of graphs.
 *
 */
public class VAERS_Predicates {
	
	/** 
	 * The AbstractVAERSPredicate specifies a
	 * predicate that evaluates VAERS_Objects --
	 * it is the general form of this class.
	 */
	public static abstract class AbstractVAERSPredicate<VAERS_Object> implements Predicate<VAERS_Object> {	
		@Override
		public abstract boolean evaluate(VAERS_Object itemToEvaluate);
	}

	/** 
	 * The SelectionPredicate filters to only include nodes that
	 * are included in a list provided to the constructor of the
	 * SelectionPredicate object.
	 */
	public static class SelectionPredicate extends AbstractVAERSPredicate<GeneralNode> {
		Collection<GeneralNode> nodeList;
		
		public SelectionPredicate(Collection<GeneralNode> nodeList) {
			super();
			this.nodeList = nodeList;
		}

		@Override
		public boolean evaluate(GeneralNode n) {  
			try{
				return (nodeList.contains(n));
			}
			catch(Exception e) {
				return false;
			}
	    }
	}
	
	/** 
	 * The ClosenessPredicate filters out nodes that have
	 * closeness values outside the range provided to the
	 * constructor of the predicate.
	 */
	public static class ClosenessPredicate extends AbstractVAERSPredicate<GeneralNode> {
		GeneralGraph parent;
		Double from, to;
		
		public ClosenessPredicate(GeneralGraph parent, Double from, Double to) {
			super();
			assert from >= 0;
			assert to <= 1;
			assert from <= to;
			
			this.parent = parent;
			this.from = from;
			this.to = to;
		}

		@Override
		public boolean evaluate(GeneralNode n) {  
	    	try{
	    		Double normedCloseness = parent.getCloseness((GeneralNode) n);
	    		return (normedCloseness >= from && normedCloseness <= to);
	    	}
	    	catch(Exception e) {
	    		return false;
	    	}
	    }  
	}
	
	/** 
	 * The ClosenessExcludedRangePredicate filters out nodes that have
	 * closeness values INside the range provided to the
	 * constructor of the predicate.
	 */
	public static class ClosenessExcludedRangePredicate extends AbstractVAERSPredicate<GeneralNode> {
		GeneralGraph parent;
		Double rangeStart, rangeEnd;
		
		public ClosenessExcludedRangePredicate(GeneralGraph parent, Double rangeStart, Double rangeEnd) {
			super();
			assert rangeStart >= 0;
			assert rangeEnd <= 1;
			assert rangeStart <= rangeEnd;
			
			this.parent = parent;
			this.rangeStart = rangeStart;
			this.rangeEnd = rangeEnd;
		}

		@Override
		public boolean evaluate(GeneralNode n) {  
	    	try{
	    		Double normedCloseness = parent.getCloseness((GeneralNode)n);
	    		return (normedCloseness <= rangeStart || normedCloseness >= rangeEnd);
	    	}
	    	catch(Exception e)
	    	{
	    		return false;
	    	}
	    }  
	}
	
	/** 
	 * The BetweennessPredicate filters out nodes that have
	 * betweenness values outside the range provided to the
	 * constructor of the predicate.
	 */
	public static class BetweennessPredicate extends AbstractVAERSPredicate<GeneralNode> {
		GeneralGraph parent;
		Double from, to;
		
		public BetweennessPredicate(GeneralGraph parent, Double from, Double to) {
			super();
			assert from >= 0;
			assert to <= 1;
			assert from <= to;
			
			this.parent = parent;
			this.from = from;
			this.to = to;
		}

		@Override
		public boolean evaluate(GeneralNode n) {  
	    	try{
	    		Double normedBetweenness = parent.getBetweenness((GeneralNode)n);
	    		return (normedBetweenness >= from && normedBetweenness <= to);
	    	}
	    	catch(Exception e)
	    	{
	    		return false;
	    	}
	    }  
	}
	
	/** 
	 * The BetweennessExcludedRangePredicate filters out nodes that have
	 * betweenness values INside the range provided to the
	 * constructor of the predicate.
	 */
	public static class BetweennessExcludedRangePredicate extends AbstractVAERSPredicate<GeneralNode> {
		GeneralGraph parent;
		Double rangeStart, rangeEnd;
		
		public BetweennessExcludedRangePredicate(GeneralGraph parent, Double rangeStart, Double rangeEnd) {
			super();
			assert rangeStart >= 0;
			assert rangeEnd <= 1;
			assert rangeStart <= rangeEnd;
			
			this.parent = parent;
			this.rangeStart = rangeStart;
			this.rangeEnd = rangeEnd;
		}

		@Override
		public boolean evaluate(GeneralNode n) {  
	    	try{
	    		Double normedBetweenness = parent.getBetweenness((GeneralNode)n);
	    		return (normedBetweenness <= rangeStart || normedBetweenness >= rangeEnd);
	    	}
	    	catch(Exception e)
	    	{
	    		return false;
	    	}
	    }  
	}
	
	/** 
	 * The StrengthPredicate filters out nodes that have
	 * strength values outside the range provided to the
	 * constructor of the predicate.
	 */
	public static class StrengthPredicate extends AbstractVAERSPredicate<GeneralNode> {
		GeneralGraph parent;
		Double from, to;
		
		public StrengthPredicate(GeneralGraph parent, Double from, Double to) {
			super();
			assert from <= to;
			
			this.parent = parent;
			this.from = from;
			this.to = to;
		}

		@Override
		public boolean evaluate(GeneralNode n) {  
	    	try{
	    		Double str = parent.getStrength((GeneralNode)n);
	    		return (str >= from && str <= to);
	    	}
	    	catch(Exception e)
	    	{
	    		return false;
	    	}
	    }  
	}
	
	/** 
	 * The StrengthExcludedRangePredicate filters out nodes that have
	 * betweenness values INside the range provided to the
	 * constructor of the predicate.
	 */
	public static class StrengthExcludedRangePredicate extends AbstractVAERSPredicate<GeneralNode> {
		GeneralGraph parent;
		Double rangeStart, rangeEnd;
		
		public StrengthExcludedRangePredicate(GeneralGraph parent, Double rangeStart, Double rangeEnd) {
			super();
			assert rangeStart <= rangeEnd;
			
			this.parent = parent;
			this.rangeStart = rangeStart;
			this.rangeEnd = rangeEnd;
		}

		@Override
		public boolean evaluate(GeneralNode n) {  
	    	try{
	    		Double str = parent.getStrength((GeneralNode)n);
	    		return (str <= rangeStart || str >= rangeEnd);
	    	}
	    	catch(Exception e)
	    	{
	    		return false;
	    	}
	    }  
	}
	
	/** 
	 * The RawDegreePredicate filters out nodes that have
	 * non-normalized degree values (between 0 and infinity)
	 * outside the range provided to the constructor of the predicate.
	 */
	public static class RawDegreePredicate extends AbstractVAERSPredicate<GeneralNode> {
		GeneralGraph parent;
		Double from, to;
		
		public RawDegreePredicate(GeneralGraph parent, Double from, Double to) {
			super();
			assert from >= 0;
			assert from <= to;
			
			this.parent = parent;
			this.from = from;
			this.to = to;
		}

		@Override
		public boolean evaluate(GeneralNode n) {  
			try {
		    	Integer degree = parent.getDegree((GeneralNode) n );
		    	return (degree >= from && degree <= to);
			} catch (IllegalArgumentException e) {
				return false;
			}
			
	    }  
	}
	
	/** 
	 * The RawDegreeExcludedRangePredicate filters out nodes that have
	 * non-normalized degree values (between 0 and infinity)
	 * INside the range provided to the constructor of the predicate.
	 */
	public static class RawDegreeExcludedRangePredicate extends AbstractVAERSPredicate<GeneralNode> {
		GeneralGraph parent;
		Double rangeStart, rangeEnd;
		
		public RawDegreeExcludedRangePredicate(GeneralGraph parent, Double rangeStart, Double rangeEnd) {
			super();
			assert rangeStart >= 0;
			assert rangeStart <= rangeEnd;
			
			this.parent = parent;
			this.rangeStart = rangeStart;
			this.rangeEnd = rangeEnd;
		}

		@Override
		public boolean evaluate(GeneralNode n) {  
			try {
		    	Integer degree = parent.getDegree((GeneralNode) n );
		    	return (degree <= rangeStart || degree >= rangeEnd);
			} catch (IllegalArgumentException e) {
				return false;
			}
	    }  
	}
	
	/** 
	 * The IsNotIsolatePredicate filters out nodes that
	 * are not connected to any other nodes.
	 */
	public static class IsNotIsolatePredicate extends AbstractVAERSPredicate<GeneralNode> {
		GeneralGraph parent;

		public IsNotIsolatePredicate(GeneralGraph parent) {
			super();
			this.parent = parent;
		}

		@Override
		public boolean evaluate(GeneralNode n) {  
			try {
				return (parent.degree(n) > 0);  
			} catch (IllegalArgumentException e) {
				return false;
			}
	    }  
	}
	
	/** 
	 * The IsNotPendantPredicate filters out nodes that
	 * are connected only to one other node / have a degree
	 * of 1 in the unfiltered parent graph.
	 */
	public static class IsNotPendantPredicate extends AbstractVAERSPredicate<GeneralNode> {
		GeneralGraph parent;

		public IsNotPendantPredicate(GeneralGraph parent) {
			super();
			this.parent = parent;
		}

		@Override
		public boolean evaluate(GeneralNode n) {  
			try {
				return (parent.degree(n) > 1 || parent.degree(n) == 0);
			} catch (IllegalArgumentException e) {
				return false;
			}
	    }  
	}
	
	
	/** 
	 * The MinEdgeWeightPredicate filters out edges that have
	 * weights less than the value provided to the constructor 
	 * of the predicate.
	 */
	public static class MinEdgeWeightPredicate extends AbstractVAERSPredicate<GeneralEdge> {
		int from;
		
		public MinEdgeWeightPredicate(int from) {
			super();
			assert from >= 0;
			
			this.from = from;
		}

		@Override
		public boolean evaluate(GeneralEdge e) {  
			return (e.getWeight() >= from);
	    }  
	}
	
	/** 
	 * The EdgeWeightPredicate filters out edges that have
	 * weights outside than the range provided to the constructor 
	 * of the predicate.
	 */
	public static class EdgeWeightPredicate extends AbstractVAERSPredicate<GeneralEdge> {
		int from, to;
		
		public EdgeWeightPredicate(int from, int to) {
			super();
			assert from >= 0;
			assert from <= to;
			
			this.from = from;
			this.to = to;
		}

		@Override
		public boolean evaluate(GeneralEdge e) {  
			return (e.getWeight() >= from && e.getWeight() <= to);
	    }  
	}
	
	/** 
	 * The EdgeWeightExcludedRange Predicate filters out edges that have
	 * weights INside the range provided to the constructor 
	 * of the predicate.
	 */
	public static class EdgeWeightExcludedRangePredicate extends AbstractVAERSPredicate<GeneralEdge> {
		int rangeStart, rangeEnd;
		
		public EdgeWeightExcludedRangePredicate(int rangeStart, int rangeEnd) {
			super();
			assert rangeStart >= 0;
			assert rangeStart <= rangeEnd;
			
			this.rangeStart = rangeStart;
			this.rangeEnd = rangeEnd;
		}

		@Override
		public boolean evaluate(GeneralEdge e) {  
			return (e.getWeight() <= rangeStart || e.getWeight() >= rangeEnd);
	    }  
	}
	
	/** 
	 * The TypedEdgeWeightPredicate filters out edges of a given
	 * type that fall outside the range provided in the constructor.
	 * Any edge not of the specified type is passed through with
	 * a True.
	 */
	public static class TypedEdgeWeightPredicate extends AbstractVAERSPredicate<GeneralEdge> {
		int from, to;
		VAERS_Edge.EdgeType type;
		
		public TypedEdgeWeightPredicate(int from, int to, VAERS_Edge.EdgeType type) {
			super();
			assert from >= 0;
			assert from <= to;
			
			this.from = from;
			this.to = to;
			this.type = type;
		}

		@Override
		public boolean evaluate(GeneralEdge e1) {  
			
			if (e1 instanceof VAERS_Edge){
				VAERS_Edge e = (VAERS_Edge)e1;
		    	return ( e.getEdgeType() != type || 
		    			(e.getEdgeType() == type && e.getWeight() >= from && e.getWeight() <= to));
			}
			else
				return (e1.getWeight() >= from && e1.getWeight() <= to);
	    }  
	}
	
	/** 
	 * The TypedEdgeWeightExcludedRangePredicate filters out edges of a given
	 * type that fall outside the range provided in the constructor.
	 * Any edge not of the specified type is passed through with
	 * a True.
	 */
	public static class TypedEdgeWeightExcludedRangePredicate extends AbstractVAERSPredicate<GeneralEdge> {
		int rangeStart, rangeEnd;
		VAERS_Edge.EdgeType type;
		
		public TypedEdgeWeightExcludedRangePredicate(int rangeStart, int rangeEnd, VAERS_Edge.EdgeType type) {
			super();
			assert rangeStart >= 0;
			assert rangeStart <= rangeEnd;
			
			this.rangeStart = rangeStart;
			this.rangeEnd = rangeEnd;
			this.type = type;
		}

		@Override
		public boolean evaluate(GeneralEdge e1) {  
			
			if (e1 instanceof VAERS_Edge){
				VAERS_Edge e = (VAERS_Edge)e1;
		    	return ( e.getEdgeType() != type || 
		    			(e.getEdgeType() == type && e.getWeight() <= rangeStart || e.getWeight() >= rangeEnd));
			}
			else
				return (e1.getWeight() <= rangeStart || e1.getWeight() >= rangeEnd);
	    }  
	}
	
	/** 
	 * The EdgeTypePredicate filters out edges that are not the 
	 * type provided to the constructor of the predicate.
	 */
	public static class EdgeTypePredicate extends AbstractVAERSPredicate<GeneralEdge> {
		
		VAERS_Edge.EdgeType type;
		
		public EdgeTypePredicate(VAERS_Edge.EdgeType type) {
			super();
			this.type = type;
		}

		@Override
		public boolean evaluate(GeneralEdge e) {
			if (e instanceof VAERS_Edge){
				VAERS_Edge e1 = (VAERS_Edge)e;
				return (e1.getEdgeType() == type);
			}
			else
			{
				System.out.println("Type not defined for the edge.");
				return true;
			}
	    }  
	}
	
	/** 
	 * The ClusteringPredicate filters out nodes that belong to a 
	 * specific cluster 
	 */
	public static class ClusteringPredicate extends AbstractVAERSPredicate<GeneralNode> {
		int clusterID;
		
		public ClusteringPredicate(int clusterID) {
			super();
			assert clusterID >= 0;
			this.clusterID = clusterID;
		}

		@Override
		public boolean evaluate(GeneralNode n) {  
	    	try{
	    		int cluster = n.getCluster();
	    		return (cluster == clusterID);
	    	}
	    	catch(Exception e)
	    	{
	    		return false;
	    	}
	    }  
	}

}
