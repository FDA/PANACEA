package com.eng.cber.na.sim.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.eng.cber.na.sim.NetworkSimulatorGUI;
import com.eng.cber.na.sim.SimulationExecutorService;

/** Listener for window closing events to perform shut down code. **/
public class SimulatorWindowListener extends WindowAdapter {

	@Override
	public void windowClosing(WindowEvent e) {
		NetworkSimulatorGUI main = (NetworkSimulatorGUI) e.getSource();
		SimulationExecutorService.shutdown();
		main.setVisible(false);
		main.dispose();
		NetworkSimulatorGUI.removeInstance();
	}
	
}
