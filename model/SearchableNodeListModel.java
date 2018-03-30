package com.eng.cber.na.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import com.eng.cber.na.graph.GeneralNode;
import com.eng.cber.na.vaers.VAERS_Node;

/**
 * A re-implementation of model.NodeListModel that
 * uses GeneralNode objects instead of VAERS_Node.
 * Developed for use in the node selection dialog window.
 * 
 * 
 *  * A ListModel that holds a set of nodes.
 *  * It knows to compare the nodes on the basis
 *  * first of their type and then of their ID (name),
 *  * and it keeps track of which nodes are
 *  * in the list.
 *
 */
public class SearchableNodeListModel implements ListModel {
	
	private List<ListDataListener> listeners = new LinkedList<ListDataListener>();
	private List<GeneralNode> nodeList;
	
	private static Comparator<GeneralNode> comparator = new Comparator<GeneralNode>() {
		@Override
		public int compare(GeneralNode o1, GeneralNode o2) {
			int c1 = ((VAERS_Node) o1).getNodeType().compareTo(((VAERS_Node) o2).getNodeType());
			int c2 = o1.getID().compareTo(o2.getID());
			return c1 != 0 ? c1 : c2;
		}			
	};
	
	
	public SearchableNodeListModel(int size) {
		nodeList = new ArrayList<GeneralNode>(size);
	}
	
	public SearchableNodeListModel(Collection<GeneralNode> nodes, Comparator comparator) {
		if(nodes == null)
			nodeList = new ArrayList<GeneralNode>();
		else
			nodeList = new ArrayList<GeneralNode>(nodes);
		this.comparator = comparator;
		sort();
	}
	
	private void sort() {
		Collections.sort(nodeList, comparator);
	}
	
	public GeneralNode[] getNodes() {
		GeneralNode[] ret = new GeneralNode[nodeList.size()];
		return nodeList.toArray(ret);
	}
	
	public void add(GeneralNode node) {
		nodeList.add(node);
		sort();
	}
	
	public void remove(GeneralNode node) {
		nodeList.remove(node);
	}	
	
	public int getElementIndex(GeneralNode node) {
		return nodeList.indexOf(node);
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
	
	public boolean contains(GeneralNode node) {
		return nodeList.contains(node);
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		if(!listeners.contains(l)) {
			listeners.add(l);
		}
	}
	
	@Override
	public void removeListDataListener(ListDataListener l) {
		listeners.remove(l);
	}	
}
