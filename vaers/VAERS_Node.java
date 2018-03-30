package com.eng.cber.na.vaers;

import java.util.EnumMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.eng.cber.na.graph.GeneralNode;
import com.eng.cber.na.graph.GraphLoader;
/**
 * A VAERS_Node is a vertex object. Vertices have
 * types, names, and a hash that links them to 
 * their higher-level MedDRA terms (e.g., a PT
 * will be linked to a set of HLTs, HLGTs, and SOCs),
 * as well as a set of reports.
 * 
 * Nodes are linked by edges to define graphs.
 *
 */
public class VAERS_Node extends GeneralNode implements VAERS_Object {

	public enum NodeType {
		VAX, SYM, REPORT, REFERENCE
	}
	public static enum MedDRA {
		PT, HLT, HLGT, SOC;
		
		public static MedDRA toEnum(int i) {
			return values()[i];
		}
	}

	private NodeType type;
	
	private EnumMap<MedDRA, Set<String> > meddra_hash; 
	
	public VAERS_Node(Object id, NodeType type, Object VAERS_ID) {
		super(id);
		this.type = type;
		this.id = id;
		reportDegree = new TreeMap<Object,Integer>();
		reportDegree.put(VAERS_ID,0);
	}
	
	public VAERS_Node(Object id, NodeType type, Object VAERS_ID, String[] meddra) {
		super(id);
		this.type = type;
		this.id = id;
		reportDegree = new TreeMap<Object,Integer>();
		reportDegree.put(VAERS_ID,0);
		
		meddra_hash = new EnumMap<MedDRA, Set<String>>(MedDRA.class);
		for (int i = 0; i < meddra.length; i++) {
			String trimmed = GraphLoader.stripString(meddra[i]);
			meddra_hash.put(MedDRA.toEnum(i + 4 - meddra.length), new TreeSet<String>());
			meddra_hash.get(MedDRA.toEnum(i + 4 - meddra.length)).add(trimmed);
		}
	}

	public NodeType getNodeType() {
		return type;
	}

	public EnumMap<MedDRA, Set<String>> getMedDRA() {
		if (meddra_hash != null)
			return meddra_hash.clone();
		else
			return null;
	}
	

	
	public void setDegree(Object VAERS_ID, int degree) {
		reportDegree.put(VAERS_ID, degree);
	}
	
	@Override
	public String toString() {
		return"Type: " + type + " Value: " + id + " Number of reports " + reportDegree.size() + " First report " + getReports().iterator().next();	
	}

	public TreeMap<?, Integer> getReportDegreeMap() {
		return reportDegree;
	}


	@Override
	public void appendReport(Object VAERS_ID) {
		reportDegree.put(VAERS_ID,0);
	}

	@Override
	public Set<?> getReports() {
		return reportDegree.keySet();
	}
}