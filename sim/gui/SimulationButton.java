package com.eng.cber.na.sim.gui;

import java.awt.Dimension;

import javax.swing.JButton;

public class SimulationButton extends JButton {

	private Dimension initialDimension;
	
	public SimulationButton(String name) {
		super(name);
	}
	
	@Override
	public void setSize(int width, int height) {
		if(initialDimension == null) {
			super.setSize(width, height);
			initialDimension = super.getPreferredSize();
		}
	}
	
	@Override 
	public Dimension getPreferredSize() {
		return initialDimension == null ? super.getPreferredSize() : initialDimension;
	}
}
