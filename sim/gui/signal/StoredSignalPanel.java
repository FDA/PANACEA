package com.eng.cber.na.sim.gui.signal;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle;
import javax.swing.ListSelectionModel;

import com.eng.cber.na.sim.rstruct.SimulatedSignal;

/**
 *  This is a panel containing a JList of the stored signals
 *  and the buttons to add, remove, and view them.
 *
 */

@SuppressWarnings("serial")
public class StoredSignalPanel extends JPanel implements ActionListener {

	private SignalDialog.SignalPanel parent;
	
	public int nextSignalNum = 1;
	
	private JButton addButton, viewButton, removeButton, clearButton;
	private JList<SimulatedSignal> visibleList;
	
	public StoredSignalPanel(SignalDialog.SignalPanel parent) {
		this.parent = parent;
		initComponents();
	}
	
	public int getNumSignals() {
		return visibleList.getModel().getSize();
	}
	
	public SimulatedSignal getSelectedSignal() {
		return visibleList.getSelectedValue();
	}
	
	public int getNextSignalNum() {
		return nextSignalNum++;
	}
	
	public List<SimulatedSignal> getStoredSignals() {
		int size = visibleList.getModel().getSize();
		List<SimulatedSignal> ret = new ArrayList<SimulatedSignal>(size);
		for (int i = 0; i < size; i++) {
			ret.add(visibleList.getModel().getElementAt(i));
		}
		return ret;
	}
	
	/** Puts a new list of signals into the panel.  Used when loading parameters from a .json file. **/
	public void setStoredSignals(List<SimulatedSignal> newSignals) {
		DefaultListModel<SimulatedSignal> model = (DefaultListModel<SimulatedSignal>) visibleList.getModel();
		model.clear();
		for (SimulatedSignal signal : newSignals) {
			model.addElement(signal);
		}
		
		// Set nextSignalNum to one larger than the largest signal name given
		nextSignalNum = Integer.parseInt(newSignals.get(newSignals.size()-1).getSignalName().split("\\s+")[1]) + 1;
	}
	
	public void initComponents() {
		
		addButton = new JButton("Add");
		addButton.addActionListener(this);
		viewButton = new JButton("View");
		viewButton.addActionListener(this);
		removeButton = new JButton("Remove");
		removeButton.addActionListener(this);
		clearButton = new JButton("Clear Fields");
		clearButton.addActionListener(this);
		
		visibleList = new JList<SimulatedSignal>(new DefaultListModel<SimulatedSignal>());
		visibleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		JLabel listTitle = new JLabel("Current Signals: ");
		listTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(visibleList);
		scrollPane.setPreferredSize(new Dimension(100,50));
		scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		JPanel listWithTitle = new JPanel();
		listWithTitle.setLayout(new BoxLayout(listWithTitle, BoxLayout.PAGE_AXIS));
		listWithTitle.add(listTitle);
		listWithTitle.add(Box.createRigidArea(new Dimension(0,7)));
		listWithTitle.add(scrollPane);
		
		GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(layout.createSequentialGroup()
	        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
	        			.addComponent(addButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	        			.addComponent(viewButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	        			.addComponent(removeButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	        			.addComponent(clearButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	        		.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
	        		.addComponent(listWithTitle));
        		
        layout.setVerticalGroup(layout.createParallelGroup()
        			.addGroup(layout.createSequentialGroup()
        				.addContainerGap(25,25)
        				.addComponent(addButton)
        				.addComponent(removeButton)
        				.addComponent(viewButton)
        				.addComponent(clearButton))
        			.addComponent(listWithTitle));
		
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand().equals("Add")) {
			int vaxEntry, vaxWeight;
			try {
				vaxEntry = parent.getVaxEntry();
				vaxWeight = parent.getVaxWeight();
			}
			catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(parent, "Please make sure all fields contain numbers.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			if (!(parent.getAssociatedSymptoms().hasSymptoms())) {
				JOptionPane.showMessageDialog(parent, "You must specify at least one associated symptom.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
							
			SimulatedSignal newSignal = new SimulatedSignal("Signal " + getNextSignalNum(),
													  vaxEntry, vaxWeight,
													  parent.getAssociatedSymptoms());
				
			((DefaultListModel<SimulatedSignal>) visibleList.getModel()).addElement(newSignal);
			
			parent.clearTabbedPaneInfo();
		}
		else if (e.getActionCommand().equals("View")) {
			if (visibleList.getSelectedIndex() != -1  && visibleList.getSelectedIndex() < visibleList.getModel().getSize()) {
				JOptionPane.showMessageDialog(parent, getSelectedSignal().getDescription(), getSelectedSignal().toString(), JOptionPane.INFORMATION_MESSAGE);
			}
		}
		else if (e.getActionCommand().equals("Remove")) {
			SimulatedSignal selectedSignal = visibleList.getSelectedValue();
			((DefaultListModel<SimulatedSignal>) visibleList.getModel()).removeElement(selectedSignal);
		}
		else if (e.getActionCommand().equals("Clear Fields")) {
			parent.clearSignalFields();
		}
	}
	
}
