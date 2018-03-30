package com.eng.cber.na.model;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.AbstractListModel;

import com.eng.cber.na.vaers.VAERS_NodeSet;

/**
 * This class maps a set of nodes to an average curve.
 * Underlying this class is a list model, that is, it 
 * is a dataset designed for being used as a list, such
 * as in a UI.
 *
 */
@SuppressWarnings("serial")
public class AvgCurveListModel extends AbstractListModel {

	private Map<VAERS_NodeSet, Map<Object,Double>> nodeListToAvgCurve = new TreeMap<VAERS_NodeSet, Map<Object,Double>>();
	
	public void addEntry(VAERS_NodeSet set, Map<Object,Double> avgCurve) {
		nodeListToAvgCurve.put(set, avgCurve);
		fireIntervalAdded(set, 0, 0);
	}
	
	public void removeEntry(VAERS_NodeSet set) {
		nodeListToAvgCurve.remove(set);
		fireIntervalRemoved(set, 0, Math.max(0,getSize() - 1));
	}
	
	public Map<Object,Double> getCurve(VAERS_NodeSet set) {
		return nodeListToAvgCurve.get(set);
	}
	
	public Set<VAERS_NodeSet> getNodeLists() {
		return nodeListToAvgCurve.keySet();
	}
	
	public int getElementIndex(VAERS_NodeSet curve) {
		int ind = new ArrayList(nodeListToAvgCurve.keySet()).indexOf(curve);
		return ind;
	}
	
	@Override
	public Object getElementAt(int index) {
		return nodeListToAvgCurve.keySet().toArray()[index];
	}

	@Override
	public int getSize() {
		return nodeListToAvgCurve.keySet().size();
	}

}
