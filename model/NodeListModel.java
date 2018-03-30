package com.eng.cber.na.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import com.eng.cber.na.vaers.VAERS_Node;

/**
 * A ListModel that holds a set of nodes.
 * It knows to compare the nodes on the basis
 * first of their type and then of their ID (name),
 * and it keeps track of which nodes are
 * in the list.
 *
 */
public class NodeListModel implements ListModel {
	
	private List<ListDataListener> listeners = new LinkedList<ListDataListener>();
	private List<VAERS_Node> nodeList;
	
	private static Comparator<VAERS_Node> comparator = new Comparator<VAERS_Node>() {
		@Override
		public int compare(VAERS_Node o1, VAERS_Node o2) {
			int c1 = o1.getNodeType().compareTo(o2.getNodeType());
			int c2 = o1.getID().compareTo(o2.getID());
			return c1 != 0 ? c1 : c2;
		}			
	};
	
	public NodeListModel(int size) {
		nodeList = new ArrayList<VAERS_Node>(size);
	}
	
	public NodeListModel(Collection<VAERS_Node> nodes) {
		nodeList = new ArrayList<VAERS_Node>(nodes);
		sort();
	}
	
	public VAERS_Node[] getNodes() {
		VAERS_Node[] ret = new VAERS_Node[nodeList.size()];
		return nodeList.toArray(ret);
	}
	
	public void add(VAERS_Node node) {
		nodeList.add(node);
		sort();
		fireListDataChange(ListDataEvent.INTERVAL_ADDED,nodeList.indexOf(node));		
	}
	
	private void sort() {
		Collections.sort(nodeList, comparator);
	}
	
	public void remove(VAERS_Node node) {
		int index = nodeList.indexOf(node);
		nodeList.remove(node);
		fireListDataChange(ListDataEvent.INTERVAL_REMOVED,index);	
	}	
	
	public int getElementIndex(VAERS_Node node) {
		return nodeList.indexOf(node);
	}
	
	public void fireListDataChange(int type, int index) {
		ListDataEvent evt = new ListDataEvent(this,type,index,nodeList.size() - 1);
		for(ListDataListener listener : listeners) {
			listener.contentsChanged(evt);
		}
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		if(!listeners.contains(l)) {
			listeners.add(l);
		}
	}

	@Override
	public Object getElementAt(int index) {
		if(index >=0 && index < nodeList.size()) {
			return nodeList.get(index);
		}
		return null;
	}

	@Override
	public int getSize() {
		return nodeList.size();
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		listeners.remove(l);
	}	
}
