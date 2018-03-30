package com.eng.cber.na.vaers;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/****
 * A set of nodes that is sorted alphabetically and that has
 * a corresponding set of reports.
 *
 */
@SuppressWarnings("serial")
public class VAERS_NodeSet extends TreeSet<VAERS_Node> implements Comparable<VAERS_NodeSet> {

	private TreeSet<Object> reportSet = new TreeSet<Object>();
	
	public VAERS_NodeSet() {
		super(new VAERS_NodeDisplayComparator());
	}
	
	public VAERS_NodeSet(Set<VAERS_Node> keySet) {
		this();
		addAll(keySet);
	}
	
	@Override
	public boolean add(VAERS_Node n) {
		reportSet.addAll(n.getReports());
		return super.add(n);
	}
	
	public TreeSet<?> getReportSet() {
		return reportSet;
	}
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for(VAERS_Node n : this) {
			buf.append(n.getID() + (!this.last().equals(n) ? "," : ""));
		}
		return buf.toString();
	}

	@Override
	public int compareTo(VAERS_NodeSet vnl) {
		Iterator<VAERS_Node> thisSet = this.iterator();
		Iterator<VAERS_Node> compSet = vnl.iterator();
		
		if(this.isEmpty() && vnl.isEmpty()) {
			return -1;
		}
		else if(this.isEmpty()) {
			return -1;
		}
		else if(vnl.isEmpty()) {
			return 1;
		}
		
		int val = 0;
		while(true) {
			val = thisSet.next().getID().compareTo(compSet.next().getID());		
			if(val != 0 || (!thisSet.hasNext() && !compSet.hasNext())) {
				return val;
			}
			else if(!thisSet.hasNext()) {
				return -1;
			}
			else if(!compSet.hasNext()) {
				return 1;
			}
		} 
	}
	
	/****
	 * An alphabetical comparator for VAERS_Nodes that ensures vaccines precede
	 * symptoms and then each set is alphabetized.
	 */
	static class VAERS_NodeDisplayComparator implements Comparator<VAERS_Node> {
		@Override
		public int compare(VAERS_Node n1, VAERS_Node n2) {
			int c1 = n1.getNodeType().compareTo(n2.getNodeType());
			int c2 = n1.getID().compareTo(n2.getID());
			return c1 != 0 ? c1 : c2;
		}		
	}
}
