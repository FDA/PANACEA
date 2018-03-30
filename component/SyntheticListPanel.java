package com.eng.cber.na.component;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListCellRenderer;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.graph.GeneralNode;
import com.eng.cber.na.model.SearchableNodeListModel;
import com.eng.cber.na.vaers.VAERS_Node;
import com.eng.cber.na.vaers.VAERS_Node.NodeType;

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
public class SyntheticListPanel extends JPanel implements KeyListener {
	private JList syntheticPTList, caseList;
	private JTextField searchTerm, caseName;
	private JLabel caseTitle;
	private Map<GeneralNode, Boolean> commandHash;
	private SearchableNodeListModel caseListModel;
	private SearchableNodeListModel ptListModel;
	private ArrayList<String> ptList;

	public String getCaseName(){
		return caseName.getText();
	}
	
	public ArrayList<String> getPtList() {
		GeneralNode[] nodes= caseListModel.getNodes();
		
		if(ptList == null){
			ptList = new ArrayList<String>();
		}
		ptList.clear();
		for(GeneralNode node:nodes){
			ptList.add(((String)node.getID()).toLowerCase());
		}
		return ptList;
	}

	public SyntheticListPanel() {
		commandHash= new HashMap<GeneralNode, Boolean>();
		Collection<VAERS_Node> nodes = NetworkAnalysisVisualization.getInstance().getUnderlyingData().getOrigNodeHash().values();

		for(VAERS_Node node: nodes){
			if(node.getNodeType().equals(NodeType.VAX))
				continue;
			commandHash.put((GeneralNode)node,  false);
		}

		ptListModel = new SearchableNodeListModel(commandHash.keySet(), comparator);
		syntheticPTList = new JList(ptListModel);
		syntheticPTList.setToolTipText("<html><p><i>Click to select a PT to add.</i></p</html>");
		//Custom cell renderer to display getID() instead of toString()
		syntheticPTList.setCellRenderer(new SearchableNodeListCellRenderer());

		caseListModel = new SearchableNodeListModel(null, comparator);

		caseList = new JList(caseListModel);
		caseList.setCellRenderer(new SearchableNodeListCellRenderer());
		init();
	}

	public void init() {
		JLabel promptLabel = new JLabel();
		promptLabel.setText("Search: ");
		searchTerm = new JTextField();
		searchTerm.addKeyListener(this);

		syntheticPTList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		JButton addButton, removeButton;

		JScrollPane scrollDisplayList = new JScrollPane(syntheticPTList);
		JPanel buttonPanel = new JPanel(new BorderLayout());
		addButton = new JButton("Add >>");
		addButton.addActionListener(new AddListener());
		removeButton = new JButton("<< Remove");
		removeButton.addActionListener(new RemoveListener());
		buttonPanel.add(addButton, BorderLayout.NORTH);
		buttonPanel.add(removeButton, BorderLayout.SOUTH);
		JPanel displayPanel = new JPanel(new BorderLayout());
		JPanel searchPanel = new JPanel(new BorderLayout());
		searchPanel.add(promptLabel, BorderLayout.WEST);
		searchPanel.add(searchTerm, BorderLayout.CENTER);
		displayPanel.add(searchPanel, BorderLayout.NORTH);
		scrollDisplayList.setPreferredSize(scrollDisplayList.getPreferredSize());
		displayPanel.add(scrollDisplayList, BorderLayout.CENTER);

		caseTitle = new JLabel("Case Name: ");
		caseName = new JTextField("Case name");
		caseName.setColumns(37);

		JPanel caseNamePanel = new JPanel(new FlowLayout());

		caseNamePanel.add(caseTitle );
		caseNamePanel.add(caseName);

		JScrollPane algorithmDisplayList = new JScrollPane(caseList);

		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		JPanel algSelPanelL = new JPanel(new BorderLayout());
		algSelPanelL.add(displayPanel, BorderLayout.CENTER);
		algSelPanelL.add(buttonPanel, BorderLayout.EAST);

		JPanel algSelPanel = new JPanel(new BorderLayout());
		algSelPanel.add(algSelPanelL, BorderLayout.WEST);
		algSelPanel.add(algorithmDisplayList, BorderLayout.CENTER);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(algSelPanel)
				.addComponent(caseNamePanel)
				);
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(algSelPanel)
				.addComponent(caseNamePanel));
	}


	private List<GeneralNode> filter(String term) {
		List<GeneralNode> ret = new ArrayList<GeneralNode>(commandHash.size());
		CharSequence c = term.toLowerCase().subSequence(0, term.length());
		for(GeneralNode v : commandHash.keySet()) {
			String id = v.getID().toLowerCase();
			if(id.contains(c)) {
				ret.add(v);
			}
		}

		return ret;
	}

	public void selectNode(GeneralNode node) {
		commandHash.put(node,true);
	}
	public void deselectNode(GeneralNode node) {
		commandHash.put(node,false);
	}
	// Updates the list when user types in box
	public void updateList() {
		// Get all the names that match the user input text
		String term = searchTerm.getText();
		SearchableNodeListModel model = new SearchableNodeListModel(filter(term), getComparator());
		syntheticPTList.setModel(model);


	}

	// Custom comparator so sorting will put Vaccines at top of list.
	private static Comparator<GeneralNode> comparator = new Comparator<GeneralNode>() {
		@Override
		public int compare(GeneralNode o1, GeneralNode o2) {
			int c1 = ((VAERS_Node) o1).getNodeType().compareTo(((VAERS_Node) o2).getNodeType());
			int c2 = o1.getID().compareTo(o2.getID());
			return c1 != 0 ? c1 : c2;
		}			
	};

	public Comparator<GeneralNode> getComparator() {
		return comparator;
	}

	private void fillListModel(SearchableNodeListModel model, Object newValues) {
		model.add((GeneralNode)newValues);
	}

	public void addDestinationElements(SearchableNodeListModel model, Object newValue) {
		fillListModel(model, newValue);
	}

	private class RemoveListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			TransferElement(caseList, syntheticPTList);
		}
	}

	private class AddListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			TransferElement(syntheticPTList, caseList);
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


	public void clearSelection() {
		for(Map.Entry<GeneralNode,Boolean> e : commandHash.entrySet()) {
			e.setValue(false);
		}
		syntheticPTList.clearSelection();

		for (MouseListener ml : syntheticPTList.getMouseListeners()) {
			if (ml instanceof SearchableNodeListMouseAdapter) {
				((SearchableNodeListMouseAdapter) ml).clearLastObjectClicked();
			}
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
			int indexOfClickedNode = syntheticPTList.locationToIndex(e.getPoint());
			if (indexOfClickedNode >= 0) {

				// Unmodified Click
				if (!(e.isControlDown()) && !(e.isShiftDown())) {
					clearSelection();
					selectNode((GeneralNode) syntheticPTList.getModel().getElementAt(indexOfClickedNode));
				}
				// CTRL+CLICK
				else if (e.isControlDown() && !(e.isShiftDown())) {
					GeneralNode clickedNode = (GeneralNode) syntheticPTList.getModel().getElementAt(indexOfClickedNode);
					if (commandHash.get(clickedNode)) {
						deselectNode((GeneralNode) syntheticPTList.getModel().getElementAt(indexOfClickedNode));
					}
					else {
						selectNode((GeneralNode) syntheticPTList.getModel().getElementAt(indexOfClickedNode));
					}
				}
				// SHIFT+CLICK
				else if (!(e.isControlDown()) && e.isShiftDown()) {
					// If most recently clicked object is still in list
					SearchableNodeListModel nlm = (SearchableNodeListModel) syntheticPTList.getModel();
					if (nlm.contains(lastObjectClicked)) {
						int lastClickIndex = nlm.getElementIndex(lastObjectClicked);
						for (int i=Math.min(lastClickIndex,indexOfClickedNode); i<=Math.max(lastClickIndex,indexOfClickedNode); i++) {
							selectNode((GeneralNode) nlm.getElementAt(i));
						}
					}
					else {
						selectNode((GeneralNode) syntheticPTList.getModel().getElementAt(indexOfClickedNode));
					}
				}

				lastObjectClicked = (GeneralNode) syntheticPTList.getModel().getElementAt(indexOfClickedNode);
			}
		}
	}
	
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
	private void TransferElement(JList  source, JList dest){
		int[] selectedIndices = source.getSelectedIndices();
		for (int i = 0; i < selectedIndices.length; i++) {
			Object obj = source.getModel().getElementAt(selectedIndices[i]);
			fillListModel((SearchableNodeListModel)dest.getModel(), obj);
			((SearchableNodeListModel)source.getModel()).remove((GeneralNode)obj);
		}
		
		caseList.revalidate();
		caseList.updateUI();
		syntheticPTList.revalidate();
		syntheticPTList.updateUI();
	}}
