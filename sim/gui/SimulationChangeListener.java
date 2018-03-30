package com.eng.cber.na.sim.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.SwingWorker.StateValue;

import com.eng.cber.na.sim.NetworkSimulator;
import com.eng.cber.na.sim.NetworkSimulatorGUI;

/** Listener for the NetworkSimulator objects while they are
 * running.  Passes along progress updates to the GUI progress
 * bar and handles interrupts.  Also determines when all
 * of the assigned simulators are finished.
 *
 */
public class SimulationChangeListener implements PropertyChangeListener {

	private Map<NetworkSimulator,Integer> state = new ConcurrentHashMap<NetworkSimulator,Integer>();
	private boolean isInterrupted = false;
	
	private int numFinished = 0;
		
	public void addNetworkSimulator(NetworkSimulator networkSimulator) {
		networkSimulator.addPropertyChangeListener(this);
		state.put(networkSimulator, 0);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		NetworkSimulator networkSimulator = (NetworkSimulator)e.getSource();
		NetworkSimulatorGUI main = NetworkSimulatorGUI.getInstance();
		
		synchronized(this) {
			if(e.getPropertyName().equals("interrupted")) {
				main.getProgressBar().setValue(0);
				main.simulatedStopEnabled(true);
				main.updateSimsRemainingLabel(-1);
				
				for(NetworkSimulator sim : state.keySet()) {
					sim.removePropertyChangeListener(this);
				}
				isInterrupted = true;
			}
			else if(e.getPropertyName().equals("progress")) {
				state.put(networkSimulator, (Integer)e.getNewValue());
				int sum = 0;
				for(Integer i : state.values()) {
					sum += i;
				}
				main.getProgressBar().setValue((int)((double)sum / state.size()));
			}
			else if(e.getPropertyName().equals("state") && e.getNewValue().equals(StateValue.DONE)) {			
				if(isInterrupted == false) {
					networkSimulator.removePropertyChangeListener(this);
					
					state.put(networkSimulator, 100);
					numFinished++;
					main.updateSimsRemainingLabel(numFinished);
					
					/** All Done when smallest state value is 100 **/
					if(Collections.min(state.values()) == 100) {
						main.getProgressBar().setValue(0);
						numFinished = 0;
						main.simulatedStopEnabled(true);
						main.createNewSeed();
						state = new ConcurrentHashMap<NetworkSimulator,Integer>();
					}
				}
			}
		}
	}
}
