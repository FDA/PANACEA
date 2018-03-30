package com.eng.cber.na.component;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListCellRenderer;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.command.SortNodesCommand;
import com.eng.cber.na.command.util.CommandComboBox;
import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.graph.GeneralNode;
import com.eng.cber.na.model.SearchableNodeListModel;
import com.eng.cber.na.vaers.VAERS_Comparator;
import com.eng.cber.na.vaers.VAERS_Comparator.BetweennessComparator;
import com.eng.cber.na.vaers.VAERS_Comparator.ClosenessComparator;
import com.eng.cber.na.vaers.VAERS_Comparator.DegreeComparator;
import com.eng.cber.na.vaers.VAERS_Comparator.StrengthComparator;
import com.eng.cber.na.vaers.VAERS_Node;

/**
 * A re-implementation of component.SearchableList that
 * uses GeneralNode objects instead of VAERS_Node.
 * Developed for use in the node selection dialog window.
 * 
 * A panel with a list that filters the items in the list
 * depending on the characters that the user types.
 * 
 * Source Nodes are stored (as Keys) in a HashMap with a
 * boolean Value for if they are selected or not.  This
 * allows us to keep track of the selection behind the
 * scenes.  Can't use the JList model for this because
 * the JList shortens (loses elements) when the user
 * types in a search string, and will not remember the
 * selection state of lost elements.
 */
@SuppressWarnings("serial")
public class SearchableNodeListPanel extends JPanel implements KeyListener {

	private Map<GeneralNode, Boolean> sourceNodes = new HashMap<GeneralNode,Boolean>();
	
	private JList displayList;
	private JTextField searchTerm;
	private JLabel selectionCountLabel;
	private CommandComboBox comboSort;
	private String selMetric = "";
	public String getSelMetric() {
		selMetric = (String) comboSort.getSelectedItem();
		return selMetric;
	}

	public void setSelMetric(String selMetric) {
		this.selMetric = selMetric;
	}

	public SearchableNodeListPanel(Collection<GeneralNode> sourceNodes) {
		for(GeneralNode n : sourceNodes) {
			this.sourceNodes.put(n, false);
		}
		displayList = new JList(new SearchableNodeListModel(sourceNodes, comparator)) {
			// Clicking white space at the bottom of the JList will
			// no longer count as clicking on the last entry.
            @Override
            public int locationToIndex(Point location) {
                int index = super.locationToIndex(location);
                if (index != -1 && !getCellBounds(index, index).contains(location)) {
                    return -1;
                }
                else {
                    return index;
                }
            }
        };
        
		init();
	}
	
	// Custom comparator so sorting will put Vaccines at top of list.
	private static Comparator<GeneralNode> comparator = new Comparator<GeneralNode>() {
		@Override
		public int compare(GeneralNode o1, GeneralNode o2) {
			int c1, c2;
			if (o1 instanceof VAERS_Node && o2 instanceof VAERS_Node)
				c1 = ((VAERS_Node) o1).getNodeType().compareTo(((VAERS_Node) o2).getNodeType());
			else
				c1 = 0;
			c2 = o1.getID().compareTo(o2.getID());
			return c1 != 0 ? c1 : c2;
		}			
	};

	
	public Comparator<GeneralNode> getComparator() {
		GeneralGraph graph = NetworkAnalysisVisualization.getInstance().getGraph();
		
		switch (comboSort.getSelectedIndex()){
		case 0:
			return comparator;
		case 1:
			return new DegreeComparator(graph, VAERS_Comparator.Direction.DESCENDING);
		case 2:
			if (!graph.confirmBetweenClose()) {
				comboSort.setSelectedIndex(0);
				return comparator;
			}
			else {
				return new BetweennessComparator(graph, VAERS_Comparator.Direction.DESCENDING);
			}
		case 3:
			if (!graph.confirmBetweenClose()) {
				comboSort.setSelectedIndex(0);
				return comparator;
			}
			else {
				return new ClosenessComparator(graph, VAERS_Comparator.Direction.DESCENDING);
			}
		case 4:
			return new StrengthComparator(graph, VAERS_Comparator.Direction.DESCENDING);
		default:
			return comparator;
		}
	}

	public ArrayList<GeneralNode> getSourceNodes() {
		ArrayList<GeneralNode> listOfNodes = new ArrayList<GeneralNode>(sourceNodes.keySet());
		comparator = getComparator();
		Collections.sort(listOfNodes, comparator);
		return listOfNodes;
	}
	
	public ArrayList<GeneralNode> getSelectedSourceNodes() {
		ArrayList<GeneralNode> listOfSelectedNodes = new ArrayList<GeneralNode>();
		for(GeneralNode node : sourceNodes.keySet()) {
			if(sourceNodes.get(node)) {
				listOfSelectedNodes.add(node);
			}
		}
		Collections.sort(listOfSelectedNodes, comparator);
		
		if (listOfSelectedNodes.isEmpty()) {
			return new ArrayList<GeneralNode>();
		}
		else {
			return listOfSelectedNodes;
		}
		
	}
	
	public void addNode(GeneralNode node) {
		if(!sourceNodes.containsKey(node)) {
			sourceNodes.put(node,false);
		}
	}
	public void removeNode(GeneralNode node) {
		if(sourceNodes.containsKey(node)) {
			sourceNodes.remove(node);
		}
	}

	public void selectNode(GeneralNode node) {
		sourceNodes.put(node,true);
		makeSelectionsMatch();
	}
	
	public void deselectNode(GeneralNode node) {
		sourceNodes.put(node,false);
		makeSelectionsMatch();
	}
	
	public JList getList() {
		return displayList;
	}
		
	public void setListCellRenderer(ListCellRenderer listCellRenderer) {
		displayList.setCellRenderer(listCellRenderer);
	}
	
	public void clearSelection() {
		for(Map.Entry<GeneralNode,Boolean> e : sourceNodes.entrySet()) {
			e.setValue(false);
		}
		displayList.clearSelection();
		updateCountLabel();
		
		for (MouseListener ml : displayList.getMouseListeners()) {
			if (ml instanceof SearchableNodeListMouseAdapter) {
				((SearchableNodeListMouseAdapter) ml).clearLastObjectClicked();
			}
		}
	}
	
	public void init() {
		JLabel promptLabel = new JLabel();
		promptLabel.setText("Search: ");
		selectionCountLabel = new JLabel();
		selectionCountLabel.setText("Currently Selected: " + getSelectedSourceNodes().size());
		searchTerm = new JTextField();
		searchTerm.addKeyListener(this);
		JLabel sortLabel = new JLabel();
		sortLabel.setText("Sort by ");
		comboSort = new CommandComboBox(getMetricDropDowns(), "Select Metric", null, NetworkAnalysisVisualization.getInstance().getCommandActionListener());
		comboSort.setCommand(new SortNodesCommand("", sourceNodes));
		
		displayList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		displayList.addMouseListener(new SearchableNodeListMouseAdapter());
		
		// Custom cell renderer to display getID() instead of toString()
		displayList.setCellRenderer(new SearchableNodeListCellRenderer());
		
		JScrollPane scrollDisplayList = new JScrollPane(displayList);
		

		GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(layout.createParallelGroup()
        		.addGroup(layout.createSequentialGroup()
        				.addComponent(promptLabel)
        				.addComponent(searchTerm)
        				.addComponent(sortLabel)
        				.addComponent(comboSort))
        		.addComponent(scrollDisplayList)
        		.addComponent(selectionCountLabel));
        layout.setVerticalGroup(layout.createSequentialGroup()
        		   		.addGroup(layout.createParallelGroup()
        				.addComponent(promptLabel)
        				.addComponent(searchTerm)
        				.addComponent(sortLabel)
        				.addComponent(comboSort))
        		.addComponent(scrollDisplayList)
        		.addComponent(selectionCountLabel));
        
        
        scrollDisplayList.setPreferredSize(scrollDisplayList.getPreferredSize());
	}
	
		
	private List<GeneralNode> filter(String term) {
		List<GeneralNode> ret = new ArrayList<GeneralNode>(sourceNodes.size());
		CharSequence c = term.toLowerCase().subSequence(0, term.length());
		for(GeneralNode v : sourceNodes.keySet()) {
			String id = v.getID().toLowerCase();
			if(id.contains(c)) {
				ret.add(v);
			}
		}
		return ret;
	}
	
	// Updates the list when user types in box
	public void updateList() {
		// Get all the names that match the user input text
		String term = searchTerm.getText();
		SearchableNodeListModel model = new SearchableNodeListModel(filter(term), getComparator());
		displayList.setModel(model);
		
		// In case user deleted text and expanded the JList,
		// want to reselect anything that newly reappeared in 
		// the JList that should be selected.
		makeSelectionsMatch();
	}
	
	// Make sure the JList selection that the user sees reflects the HashMap selection.
	public void makeSelectionsMatch() {
		displayList.clearSelection();
		for(GeneralNode n : ((SearchableNodeListModel) displayList.getModel()).getNodes()) {
			if(sourceNodes.get(n)) {
				int index = ((SearchableNodeListModel) displayList.getModel()).getElementIndex(n);
				displayList.addSelectionInterval(index,index);
			}
		}
	}
	
	public void updateCountLabel() {
		selectionCountLabel.setText("Currently Selected: " + getSelectedSourceNodes().size());
	}

	@Override
	public void keyReleased(KeyEvent e) {
		updateList();
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {}
	
	
	/* Need a custom cell renderer to use GeneralNode#getID() instead of toString() to
	 * get the text to display. */ 
	public class SearchableNodeListCellRenderer extends DefaultListCellRenderer {
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			setText(((GeneralNode) value).getID());
			return(this);
		}
		
	}
	
	/**
	 * A mouse listener class for when the user clicks items in the JList.
	 * Updates the HashMap with every click, and then re-updates the
	 * visible selection in the JList.
	 */
	public class SearchableNodeListMouseAdapter extends MouseAdapter {

		private GeneralNode lastObjectClicked;
		
		public void clearLastObjectClicked() {
			lastObjectClicked = null;
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			// Check if the mouse is on one of the list items.
			int indexOfClickedNode = displayList.locationToIndex(e.getPoint());
			if (indexOfClickedNode >= 0) {
				
				// Unmodified Click
				if (!(e.isControlDown()) && !(e.isShiftDown())) {
					clearSelection();
					selectNode((GeneralNode) displayList.getModel().getElementAt(indexOfClickedNode));
				}
				// CTRL+CLICK
				else if (e.isControlDown() && !(e.isShiftDown())) {
					GeneralNode clickedNode = (GeneralNode) displayList.getModel().getElementAt(indexOfClickedNode);
					if (sourceNodes.get(clickedNode)) {
						deselectNode((GeneralNode) displayList.getModel().getElementAt(indexOfClickedNode));
					}
					else {
						selectNode((GeneralNode) displayList.getModel().getElementAt(indexOfClickedNode));
					}
				}
				// SHIFT+CLICK
				else if (!(e.isControlDown()) && e.isShiftDown()) {
					// If most recently clicked object is still in list
					SearchableNodeListModel nlm = (SearchableNodeListModel) displayList.getModel();
					if (nlm.contains(lastObjectClicked)) {
						int lastClickIndex = nlm.getElementIndex(lastObjectClicked);
						for (int i=Math.min(lastClickIndex,indexOfClickedNode); i<=Math.max(lastClickIndex,indexOfClickedNode); i++) {
							selectNode((GeneralNode) nlm.getElementAt(i));
						}
					}
					else {
						selectNode((GeneralNode) displayList.getModel().getElementAt(indexOfClickedNode));
					}
				}
				
				lastObjectClicked = (GeneralNode) displayList.getModel().getElementAt(indexOfClickedNode);
			}
			makeSelectionsMatch();
			updateCountLabel();
		}
	}
	
    private String[] getMetricDropDowns() {
    	return new String[] { "Alphabet", "Degree", "Betweenness", "Closeness", "Strength"};
    }
}
