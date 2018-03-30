package com.eng.cber.na.sim.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;

import com.eng.cber.na.sim.NetworkSimulatorGUI;
import com.eng.cber.na.sim.gui.signal.SignalDialog;

/** Listener for the Set Up Signals button.  Opens the signal
 *  dialog window.  Not used to listen to the Simulate With
 *  Signal checkbox.  See SignalItemListener for that.
 */
public class SignalActionListener implements ActionListener {

	private NetworkSimulatorGUI main;
	
	public SignalActionListener(NetworkSimulatorGUI main) {
		this.main = main;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// Enable/Disable signal checkbox
		if (e.getSource() instanceof JCheckBox) {
			JCheckBox cb = (JCheckBox)e.getSource();
			main.signalEnabled(cb.isSelected());
		}
		// Run signal set up dialog when button is pressed 
		else if (e.getSource() instanceof JButton) {
			if (e.getActionCommand().equals("Set Up Signals")) {
				SignalDialog dialog = new SignalDialog(main);
				main.setSignalDialog(dialog);
			}
		}
	}

}
