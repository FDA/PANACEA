package com.eng.cber.na.sim.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.eng.cber.na.sim.NetworkSimulatorGUI;

/** ActionListener placed on the new seed button.  Creates
 *  a new 8 digit number.
 */

public class NewSeedButtonListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		// Event should originate from the "New Seed" Button.
		
		String newSeedString = "", digit;
		for (int i = 0; i < 8; i++) {
			do {
				digit = String.valueOf((int) (Math.random() * 9.9));
			} while (i == 0 && digit.equals("0"));
			newSeedString = newSeedString + digit;
		}
		NetworkSimulatorGUI.getInstance().setRandomSeed(Long.parseLong(newSeedString));
	}

}
