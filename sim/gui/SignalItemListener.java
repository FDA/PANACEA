package com.eng.cber.na.sim.gui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;

import com.eng.cber.na.sim.NetworkSimulatorGUI;

/** Listener for the Simulate With Signal checkbox.  Triggers
 *  enable/disable for the signal components, including the
 *  signal dialog window.
 */
public class SignalItemListener implements ItemListener {

	private NetworkSimulatorGUI main;
	
	public SignalItemListener(NetworkSimulatorGUI main) {
		this.main = main;
	}
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		// Enable/Disable signal checkbox
		if (e.getItemSelectable() instanceof JCheckBox) {
			JCheckBox cb = (JCheckBox)e.getItemSelectable();
			main.signalEnabled(cb.isSelected());
		}
	}

}
