package com.eng.cber.na.vaers;

import java.util.Comparator;

import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.graph.GeneralNode;

/**
 * A set of comparators that allow the user to sort
 * by a particular comparison type (e.g., type,
 * name, report count, etc.) This class is used
 * in the adjacent vertex table.
 *
 */
public class VAERS_Comparator {
	
	public static enum ComparatorType {
		TYPE,NAME,BETWEENNESS,CLOSENESS,DEGREE,STRENGTH,REPORT_COUNT
	}
	
	public static enum Direction {
		ASCENDING,DESCENDING
	}
	
	/**
	 * AbstractComparator is a general class for comparisons
	 * between nodes that tracks a particular direction and 
	 * requires all subclasses to implement a compare method.
	 * It is only applicable for properties of a node, rather
	 * than structural properties of how the node is connected
	 * to its peer nodes (for this second type of property,
	 * there is AbstractGraphComparator).
	 *
	 */
	private static abstract class AbstractComparator implements Comparator<GeneralNode> {
		protected Direction d;
		protected AbstractComparator(Direction d) {
			this.d = d;
		}
		@Override
		public abstract int compare(GeneralNode o1, GeneralNode o2);
	}
	
	/***
	 * The TypeComparator sorts nodes according to their type and
	 * whether the list is ascending or descending. Nodes of
	 * the same type are sorted according to their IDs.
	 *
	 */
	public static class TypeComparator extends AbstractComparator {
		public TypeComparator(Direction d) {
			super(d);
		}
		@Override
		public int compare(GeneralNode o1, GeneralNode o2) {
			int compare = 0;
			if (o1 instanceof VAERS_Node){
				compare = ((VAERS_Node)o1).getNodeType().compareTo(((VAERS_Node)o2).getNodeType());
			}
			int res = d.equals(Direction.ASCENDING) ? compare : -compare;
			return res != 0 ? res : o1.getID().compareTo(o2.getID());
		}		
	}
	
	/***
	 * The NameComparator sorts nodes according to their names and
	 * whether the list is ascending or descending.
	 *
	 */
	public static class NameComparator extends AbstractComparator {
		public NameComparator(Direction d) {
			super(d);
		}
		@Override
		public int compare(GeneralNode o1, GeneralNode o2) {
			int compare = o1.getID().compareTo(o2.getID());
			return d.equals(Direction.ASCENDING) ? compare : -compare;
		}		
	}

	/***
	 * The ReportCountComparator sorts nodes according to the value of
	 * their report count and as to whether the list is ascending
	 * or descending.
	 *
	 */
	public static class ReportCountComparator extends AbstractComparator {
		public ReportCountComparator(Direction d) {
			super(d);
		}
		@Override
		public int compare(GeneralNode o1, GeneralNode o2) {
			if (o1 instanceof VAERS_Node){
				VAERS_Node o1_v = (VAERS_Node)o1, o2_v = (VAERS_Node)o2;
				int compare = new Integer(o1_v.getReports().size()).compareTo(new Integer(o2_v.getReports().size()));
				return d.equals(Direction.ASCENDING) ? compare : -compare;
			}
			else{
				return 0;
			}
		}		
	}
	
	/***
	 * The AbstractGraphComparator is an AbstractComparator
	 * for the properties of a vertex that are graph-dependent
	 * (e.g., the properties that are situated in a particular
	 * topological structure).  For these properties to be
	 * calculated, the comparator also needs access to a graph
	 * object.  The AbstractGraphComparator stores that graph
	 * object.
	 *
	 */
	private static abstract class AbstractGraphComparator extends AbstractComparator {
		protected GeneralGraph g;
		protected AbstractGraphComparator(GeneralGraph g, Direction d) {
			super(d);
			this.g = g;
		}
	}
	
	/***
	 * The ReportCountComparator sorts nodes according to the value of
	 * their betweenness in a particular network and as to whether the 
	 * list is ascending or descending.
	 *
	 */
	public static class BetweennessComparator extends AbstractGraphComparator {
		public BetweennessComparator(GeneralGraph g, Direction d) {
			super(g,d);
		}
		@Override
		public int compare(GeneralNode o1, GeneralNode o2) {
			int compare = g.getBetweenness(o1).compareTo(g.getBetweenness(o2));
			return d.equals(Direction.ASCENDING) ? compare : -compare;
		}		
	}
	
	/***
	 * The ClosenessComparator sorts nodes according to the value of
	 * their closeness in a particular network and as to whether the 
	 * list is ascending or descending.
	 *
	 */
	public static class ClosenessComparator extends AbstractGraphComparator {
		public ClosenessComparator(GeneralGraph g, Direction d) {
			super(g,d);
		}
		@Override
		public int compare(GeneralNode o1, GeneralNode o2) {
			int compare = g.getCloseness(o1).compareTo(g.getCloseness(o2));
			return d.equals(Direction.ASCENDING) ? compare : -compare; 
		}		
	}
	
	/***
	 * The ReportCountComparator sorts nodes according to the value of
	 * their degree in a particular network and as to whether the 
	 * list is ascending or descending.
	 *
	 */
	public static class DegreeComparator extends AbstractGraphComparator {
		public DegreeComparator(GeneralGraph g, Direction d) {
			super(g,d);
		}
		@Override
		public int compare(GeneralNode o1, GeneralNode o2) {
			int compare = g.getDegree(o1).compareTo(g.getDegree(o2));
			return d.equals(Direction.ASCENDING) ? compare : -compare;
		}		
	}
	
	/***
	 * The ReportCountComparator sorts nodes according to the value of
	 * their strength in a particular network and as to whether the 
	 * list is ascending or descending.
	 *
	 */
	public static class StrengthComparator extends AbstractGraphComparator {
		public StrengthComparator(GeneralGraph g, Direction d) {
			super(g,d);
		}
		@Override
		public int compare(GeneralNode o1, GeneralNode o2) {
			int compare = g.getStrength(o1).compareTo(g.getStrength(o2));
			return d.equals(Direction.ASCENDING) ? compare : -compare;
		}		
	}
}
