package com.eng.cber.na.sim.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Set;

import com.eng.cber.na.sim.NetworkSimulatorGUI;
import com.eng.cber.na.sim.SimulationExecutorService;
import com.eng.cber.na.sim.VaccineReaderPanacea;

/**
 * Listener for the refresh button and the
 * VaccineReader (the first object run by the
 * refresh button).  
 */

public class VaccineReaderListener implements ActionListener, PropertyChangeListener {

	private ActionListener paramBuilderListener;
	
	public VaccineReaderListener(ActionListener paramBuilderListener) {
		this.paramBuilderListener = paramBuilderListener;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		NetworkSimulatorGUI main = NetworkSimulatorGUI.getInstance();
		
		main.simulatedEnabled(false); 
		VaccineReaderPanacea vaccineReader = new VaccineReaderPanacea();
		vaccineReader.addPropertyChangeListener(this);
		SimulationExecutorService.submitSingle(vaccineReader);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		NetworkSimulatorGUI main = NetworkSimulatorGUI.getInstance();
		if(e.getPropertyName().equals("progress")) {
			main.getProgressBar().setValue((Integer)e.getNewValue());
		}
		if(e.getPropertyName().equals("done")) {
			VaccineReaderPanacea vaccineReader = (VaccineReaderPanacea)e.getSource();
			Set<String> vaccines = vaccineReader.getVaccines();
			main.setVaccines(vaccines);
			
			vaccineReader.removePropertyChangeListener(this);
			main.getProgressBar().setValue(0);
			main.simulatedEnabled(true); 
			ActionEvent ae = new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"inputDataRefreshed");
			paramBuilderListener.actionPerformed(ae);
		}
	}

}
