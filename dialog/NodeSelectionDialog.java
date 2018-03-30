package com.eng.cber.na.dialog;

import java.util.Collection;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;

import com.eng.cber.na.component.SearchableNodeListPanel;
import com.eng.cber.na.graph.GeneralNode;

/**
 * A dialog that lists all of the nodes in sorted order
 * for selection by the user through the node name.
 * 
 * Also has a text box to allow user to filter the list
 * for a specific string.
 *
 */
@SuppressWarnings("serial")
public class NodeSelectionDialog extends JDialog {

	private static NodeSelectionDialog dialog;
	private NodeSelectionVizPanel ncvp;
	private SearchableNodeListPanel searchableNodeListPanel;
	public SearchableNodeListPanel getSearchableNodeListPanel() {
		return searchableNodeListPanel;
	}

	public void setSearchableNodeListPanel(
			SearchableNodeListPanel searchableNodeListPanel) {
		this.searchableNodeListPanel = searchableNodeListPanel;
	}


	private boolean exitedWithSelectButton = false;
	
	public NodeSelectionDialog(JFrame main, Collection<GeneralNode> nodes) {
		super(main,"Node Selection",true);
		
		searchableNodeListPanel = new SearchableNodeListPanel(nodes);
		
		ncvp = new NodeSelectionVizPanel();
		add(ncvp);
		pack();
		setLocationRelativeTo(main);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		dialog = this;
		setVisible(true);
	}
	public static NodeSelectionDialog getInstance(){
		return dialog;
	}

	public static NodeSelectionDialog showDialog(JFrame main, List<GeneralNode> nodes) {
		dialog = new NodeSelectionDialog(main, nodes);
		dialog.setExitedWithSelectButton(false);
		dialog.setVisible(true);
		return dialog;
	}

	private void setExitedWithSelectButton(boolean exitedWithSelectButton) {
		this.exitedWithSelectButton = exitedWithSelectButton;
	}
	
	public boolean exitedWithSelectButton() {
		return exitedWithSelectButton;
	}
	
	public Collection<GeneralNode> getSelection() {
		return searchableNodeListPanel.getSelectedSourceNodes();
	}

	
	/******************************************/
	
	
	public class NodeSelectionVizPanel extends JPanel {
	    
	    private javax.swing.JLabel introLabel;
	    private javax.swing.JLabel instructionsLabel;
	    private javax.swing.JButton cancelButton;
	    private javax.swing.JButton clearSelectionButton;
	    private javax.swing.JButton selectButton;
	    
		
	    public NodeSelectionVizPanel() {
	        initComponents();
	    }

	    private void initComponents() {
	    	
	    	// Prepare Basic Elements - Text and Buttons
	    	// The list and its text box have their own panel
	    	
	        introLabel = new javax.swing.JLabel();
	        instructionsLabel = new javax.swing.JLabel();
	        cancelButton = new javax.swing.JButton();
	        clearSelectionButton = new javax.swing.JButton();
	        selectButton = new javax.swing.JButton();
	        
	        introLabel.setText("<html>To make a selection in the visualization, highlight one or more nodes from the list and press Select.</html>");
	        instructionsLabel.setText("<html><p><i>Use CTRL+CLICK to highlight multiple<br>individual items.  Use SHIFT+CLICK to<br>highlight a continuous group of items.</i></p</html>");
	        
	        cancelButton.setText("Cancel");
	        cancelButton.addActionListener(new java.awt.event.ActionListener() {
	        	public void actionPerformed(java.awt.event.ActionEvent evt) {
	        		cancelButtonActionPerformed(evt);
	        	}
	        });
	        clearSelectionButton.setText("Clear Selection");
	        clearSelectionButton.addActionListener(new java.awt.event.ActionListener() {
	        	public void actionPerformed(java.awt.event.ActionEvent evt) {
	        		clearSelectionButtonActionPerformed(evt);
	        	}
	        });
	        selectButton.setText("Select");
	        selectButton.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	                selectButtonActionPerformed(evt);
	            }
	        });
	        
	        // Layout
	        GroupLayout layout = new GroupLayout(this);
	        this.setLayout(layout);
	        layout.setAutoCreateGaps(true);
	        layout.setAutoCreateContainerGaps(true);
	        
	        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
	        		.addComponent(introLabel)
	        		.addGroup(layout.createSequentialGroup()
	        				.addComponent(searchableNodeListPanel)
	        				.addComponent(instructionsLabel))
	        		.addGroup(layout.createSequentialGroup()
	        				.addComponent(cancelButton)
	        				.addComponent(clearSelectionButton)
	        				.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED,
	        								 GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	        				.addComponent(selectButton)
	        				.addContainerGap()));
	        
	        layout.setVerticalGroup(layout.createSequentialGroup()
	        		.addComponent(introLabel)
	        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
	        				.addComponent(searchableNodeListPanel)
	        				.addComponent(instructionsLabel))
	        		.addGroup(layout.createParallelGroup()
	        				.addComponent(cancelButton)
	        				.addComponent(clearSelectionButton)
	        				.addComponent(selectButton)));
	        
	
	    }


	    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
	    	setExitedWithSelectButton(false);
	    	setVisible(false);
	    	dispose();
	    }
	    private void clearSelectionButtonActionPerformed(java.awt.event.ActionEvent evt) {
	    	searchableNodeListPanel.clearSelection();
	    }
	    
	    private void selectButtonActionPerformed(java.awt.event.ActionEvent evt) {
			try{
				if(searchableNodeListPanel.getSelectedSourceNodes().isEmpty()){
					JOptionPane.showMessageDialog(null, "Select at least one term.", "Input Error", JOptionPane.ERROR_MESSAGE);
				}
				else{
					setExitedWithSelectButton(true);
					setVisible(false);
					dispose();
				}
			}
			catch(IllegalArgumentException x) {
				JOptionPane.showMessageDialog(null, x.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
				setExitedWithSelectButton(false);
			}
	    }
	    
	}

}
