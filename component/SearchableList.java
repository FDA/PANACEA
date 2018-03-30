package com.eng.cber.na.component;

import java.awt.BorderLayout;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.eng.cber.na.event.util.NodeSelectionEvent;
import com.eng.cber.na.event.util.NodeSelectionListener;
import com.eng.cber.na.model.NodeListModel;
import com.eng.cber.na.vaers.VAERS_Node;
import com.eng.cber.na.vaers.VAERS_NodeSet;

/**
 * A panel with a list that filters the items in the list
 * depending on the characters that the user types.
 */
@SuppressWarnings("serial")
public class SearchableList extends JPanel implements KeyListener, ListSelectionListener {

	private Map<VAERS_Node, Boolean> sourceNodes = new HashMap<VAERS_Node,Boolean>();
	private List<NodeSelectionListener> selectionListeners = new LinkedList<NodeSelectionListener>();
	
	private JList displayList;
	private JTextField searchTerm;
	
	public SearchableList(int size) {
		displayList = new JList(new NodeListModel(size));
		init();
	}
	
	public SearchableList(Collection<VAERS_Node> sourceNodes) {
		for(VAERS_Node n : sourceNodes) {
			this.sourceNodes.put(n, false);
		}
		displayList = new JList(new NodeListModel(sourceNodes));
		init();
	}
	
	public VAERS_NodeSet getSourceNodes() {
		return new VAERS_NodeSet(sourceNodes.keySet());
	}
	
	public VAERS_NodeSet getSelectedSourceNodes() {
		VAERS_NodeSet ret = new VAERS_NodeSet();
		for(VAERS_Node n : sourceNodes.keySet()) {
			if(sourceNodes.get(n)) {
				ret.add(n);
			}
		}
		return ret;
	}
	
	public void addNode(VAERS_Node node) {
		if(!sourceNodes.containsKey(node)) {
			sourceNodes.put(node,false);
		}
	}
	
	public void removeNode(VAERS_Node node) {
		if(sourceNodes.containsKey(node)) {
			sourceNodes.remove(node);
		}
	}
	
	public void selectNode(VAERS_Node node) {
		sourceNodes.put(node,true);
		NodeListModel nlm = (NodeListModel)(displayList.getModel());
		for(VAERS_Node n : ((NodeListModel)(displayList.getModel())).getNodes()) {
			if(n.equals(node)) {
				int ind = nlm.getElementIndex(n);
				displayList.addSelectionInterval(ind,ind);
				return;
			}
		}
	}
	
	public void deselectNode(VAERS_Node node) {
		sourceNodes.put(node,false);
		NodeListModel nlm = (NodeListModel)(displayList.getModel());
		for(VAERS_Node n : nlm.getNodes()) {
			if(n.equals(node)) {
				int ind = nlm.getElementIndex(n);
				displayList.removeSelectionInterval(ind, ind);
				return;
			}
		}
	}
	
	public JList getList() {
		return displayList;
	}
	
	public void addFocusListener(FocusListener l) {
		displayList.addFocusListener(l);
	}
	
	public void setListCellRenderer(ListCellRenderer listCellRenderer) {
		displayList.setCellRenderer(listCellRenderer);
	}
	
	public void clearSelection() {
		for(Map.Entry<VAERS_Node,Boolean> e : sourceNodes.entrySet()) {
			e.setValue(false);
		}
		displayList.clearSelection();
	}
	
	public void init() {
		searchTerm = new JTextField();
		searchTerm.addKeyListener(this);
		
		displayList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		displayList.addListSelectionListener(this);
		
		JScrollPane scrollDisplayList = new JScrollPane(displayList);
		
		setLayout(new BorderLayout());
		add(searchTerm, BorderLayout.NORTH);
		add(scrollDisplayList, BorderLayout.CENTER);
	}
	
	public void addNodeSelectionListener(NodeSelectionListener l) {
		selectionListeners.add(l);
	}
		
	private List<VAERS_Node> filter(String term) {
		List<VAERS_Node> ret = new ArrayList<VAERS_Node>(sourceNodes.size());
		CharSequence c = term.toLowerCase().subSequence(0, term.length());
		for(VAERS_Node v : sourceNodes.keySet()) {
			String id = v.getID().toLowerCase();
			if(id.contains(c)) {
				ret.add(v);
			}
		}
		return ret;
	}
	
	public void updateList() {
		String term = searchTerm.getText();
		NodeListModel model = new NodeListModel(filter(term));
		displayList.setModel(model);
		for(VAERS_Node node : sourceNodes.keySet()) {
			if(sourceNodes.get(node)) {
				displayList.setSelectedValue(node, false);
			}
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		// Update nodes associated with what is visible within the display list
		for(VAERS_Node n : ((NodeListModel)(displayList.getModel())).getNodes()) {
			sourceNodes.put(n, false);
		}
		for(Object obj : displayList.getSelectedValues()) {
			sourceNodes.put((VAERS_Node)obj,true);
		}
		NodeSelectionEvent e2 = new NodeSelectionEvent(this);
		for(NodeSelectionListener l : selectionListeners) {
			l.selectionChanged(e2);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		updateList();
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {}
}
