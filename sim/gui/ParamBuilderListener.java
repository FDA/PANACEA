package com.eng.cber.na.sim.gui;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker.StateValue;

import com.eng.cber.na.sim.NetworkSimulatorGUI;
import com.eng.cber.na.sim.ParameterBuilderPanacea;
import com.eng.cber.na.sim.SimulationExecutorService;
import com.eng.cber.na.sim.gui.signal.SignalDialog;
import com.eng.cber.na.sim.gui.signal.SortedListModel;

/**
 * Contains callbacks for the launching the ParameterBuilder
 * and for receiving change events while it is running.
 * 
 * Will only trigger parameter building when the simulator
 * is in LIVE_MODE.
 * 
 */
public class ParamBuilderListener implements ActionListener, PropertyChangeListener {
	
	@Override
	public void actionPerformed(ActionEvent e) {
		NetworkSimulatorGUI main = NetworkSimulatorGUI.getInstance();
		
		if (main.getMode() == NetworkSimulatorGUI.Mode.LIVE_MODE) {
			
			if (e.getActionCommand().equals("inputDataRefreshed")) {
				if (main.getSignalDialog() != null) {
					main.getSignalDialog().getSignalPanel().setVaccines(main.getVaccines());
				}
			}
			
			int groupSize = -1;
			try {
				groupSize = main.getGroupSize();
			}
			catch(NumberFormatException nfe) {
				JOptionPane.showMessageDialog(main, "Please enter a valid number for \"Group Size\".", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if(groupSize < 1) {
				JOptionPane.showMessageDialog(main, "Please make sure that \"Group Size\" has a value greater than 0.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
	
			SignalDialog.SignalPanel signalPanel = null;
			String vaccineName = "";
			
			if (e.getSource() instanceof JComboBox && e.getActionCommand().equals("comboBoxChanged")) {
				JComboBox box = (JComboBox) e.getSource();
	
				Container parent = box.getParent();
				while (!(parent instanceof SignalDialog.SignalPanel)) {
					parent = parent.getParent(); 
				}
				signalPanel = (SignalDialog.SignalPanel) parent;
				
				if(box.getModel().getSize() <= 0) {
					JOptionPane.showMessageDialog(main, "Please load data file.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if(box.getSelectedItem() == null || box.getSelectedItem() == "") {
					JOptionPane.showMessageDialog(main, "Please select a co-occurence vaccine.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				vaccineName = (String) box.getSelectedItem();
			}
	//		}
			else {
				// Since the combo box doesn't exist yet, just pick one
				// of the names from the set as a temporary value.
				vaccineName = main.getVaccines().iterator().next();
			}
			
			
			ParameterBuilderPanacea paramBuilder = new ParameterBuilderPanacea(vaccineName, groupSize);
			paramBuilder.addPropertyChangeListener(this);
			main.setParameterListing(paramBuilder);
			if (signalPanel != null) {
				signalPanel.setParameterListing(paramBuilder);
			}
			SimulationExecutorService.submitSingle(paramBuilder);
		}
		else {
			JOptionPane.showMessageDialog(main, "Not in \"Live Mode\"", "ERROR", JOptionPane.ERROR_MESSAGE);
			return;
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		NetworkSimulatorGUI main = NetworkSimulatorGUI.getInstance();
		if(e.getPropertyName().equals("state") && e.getNewValue().equals(StateValue.STARTED)) {
			main.getProgressBar().setValue(0);
			main.simulatedEnabled(false);
		}
		else if(e.getPropertyName().equals("progress")) {
			main.getProgressBar().setValue((Integer)e.getNewValue());
		}
		// When the parameter builder has finished running.
		else if(e.getPropertyName().equals("state") && e.getNewValue().equals(StateValue.DONE)) {	
			ParameterBuilderPanacea paramBuilder = (ParameterBuilderPanacea)e.getSource();
			
			if (main.getSignalDialog() != null) {
				// Initialize the By Symptom tab if it was never initialized before.
				if (main.getSignalDialog().getSignalPanel().getCoOccurVaccines() == null || main.getSignalDialog().getSignalPanel().getCoOccurVaccines().getItemCount() == 0) {
					main.getSignalDialog().getSignalPanel().initializeSymptomTab();
				}
				JList selected = main.getSignalDialog().getSignalPanel().getSelectedSyms();
				SortedListModel<String> slm = (SortedListModel<String>)selected.getModel();
				slm.clear();
				
				JList unselected = main.getSignalDialog().getSignalPanel().getUnselectedSyms();
				SortedListModel<String> ulm = (SortedListModel<String>)unselected.getModel();
				ulm.clear();
				
				Set<String> syms = paramBuilder.getFilteredSymSet();
				for(String sym : syms) {
					ulm.add(sym);
				}
			}
			
			paramBuilder.removePropertyChangeListener(this);
			main.getProgressBar().setValue(0);
			main.simulatedEnabled(true);
			
			// Update the general info displayed on the GUI
			main.updateNumReportsLabel(paramBuilder.getNumInputReports());
			if (main.isLoadingNetworkForFirstTime()) {
				main.setDefaultDataParameters();
			}
		}
	}

}
