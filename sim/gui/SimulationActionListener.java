package com.eng.cber.na.sim.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;

import javax.swing.JOptionPane;

import org.apache.commons.math3.random.Well19937c;

import com.eng.cber.na.sim.NetworkSimulator;
import com.eng.cber.na.sim.NetworkSimulatorGUI;
import com.eng.cber.na.sim.ParameterBuilderPanacea;
import com.eng.cber.na.sim.ParameterListing;
import com.eng.cber.na.sim.SimulationExecutorService;
import com.eng.cber.na.sim.gui.signal.SymptomsBySymptom;
import com.eng.cber.na.sim.rstruct.Function;
import com.eng.cber.na.sim.rstruct.Signal;
import com.eng.cber.na.sim.rstruct.SimulatedSignal;

/**
 * Callback for simulation start/stop button.
 * When starting, verifies all the input parameters,
 * then creates the NetworkSimulator objects and
 * submits them as tasks.
 */

public class SimulationActionListener implements ActionListener {

	private List<Future<?>> futures = new LinkedList<Future<?>>();
		
	@Override
	public void actionPerformed(ActionEvent e) {		
		if(e.getActionCommand().equals("Start Simulation")) {
			start();
		}
		else {
			stop();
		}
	}
	
	public void stop() {
		for(Future<?> future : futures) {
			future.cancel(true);
		}		
	}
	
	public void clearFutures() {
		futures.clear();
	}
	
	/**
	 * Validate parameters and start the simulation.
	 */
	public void start() {

		final NetworkSimulatorGUI main = NetworkSimulatorGUI.getInstance();
		ParameterListing paramListing = main.getParameterListing();
		
		if(paramListing == null) {
			JOptionPane.showMessageDialog(main, "Error with input parameters.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		if (paramListing instanceof ParameterBuilderPanacea) {
			if (!((ParameterBuilderPanacea) paramListing).isDone()) {
				JOptionPane.showMessageDialog(main, "Error with input parameters.  Try refreshing.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		
		if(main.getGroupSize() != paramListing.getGroupSize()) {
			JOptionPane.showMessageDialog(main, "Group Size has changed. Please build parameters again.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		//read and validate params
		int n_vx, n_pt, n_reports, reportsPerInterval, numSimulations;
		long randomSeed;
		try {
			n_vx = main.getNumStartVax();
			n_pt = main.getNumStartSym();
			n_reports = main.getNumReportsToSimulate();
			reportsPerInterval = main.getReportsPerInterval();
			numSimulations = main.getNumberSimulations();
			randomSeed = main.getRandomSeed();
		}
		catch(NumberFormatException nfe) {
			JOptionPane.showMessageDialog(main, "Please make sure textfields contain valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		// Verify face validity of inputs
		if(n_reports < 1) {
			JOptionPane.showMessageDialog(main, "Please make sure that \"Number of Reports\" has a value greater than 0.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(reportsPerInterval < 1) {
			JOptionPane.showMessageDialog(main, "Please make sure that \"Reports Per Interval\" has a value greater than 0.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(n_vx < 0) {
			JOptionPane.showMessageDialog(main, "Please make sure that \"Number of Starting Vaccines\" has a non-negative value.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(n_pt < 0) {
			JOptionPane.showMessageDialog(main, "Please make sure that \"Number of Starting Symptoms\" has a non-negative value.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(numSimulations < 1) {
			JOptionPane.showMessageDialog(main, "Please make sure that \"Number of Simulations\" has a value greater than 0.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(randomSeed < 0) {
			JOptionPane.showMessageDialog(main, "Please make sure that \"Random Seed\" is a positive integer.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		// Verify size of inputs
		int maxReports = reportsPerInterval * paramListing.getNewSymDist().size();
		if(n_reports > maxReports) {
			JOptionPane.showMessageDialog(main, "Please make sure that \"Number of Reports\" is less than or equal to " + maxReports + ".", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		// Get the signals if there are any.
		List<Signal> signalsToSimulate = new ArrayList<Signal>();
		if(main.isSignalChecked()) {
			if (main.getStoredSignals().size() < 1) {
				int returnValue = JOptionPane.showConfirmDialog(main, "\"Simulate with Signal\" is checked, but you have not specified any signals.\nProceed anyway?", "Confirm No Signals", JOptionPane.YES_NO_OPTION);
				if (returnValue == JOptionPane.NO_OPTION)
					return;
			}
			signalsToSimulate.addAll(main.getStoredSignals());
		}
		
		// Check the data source for each signal.
		boolean wasWarnedInput = false, wasWarnedMatch = false;
		for (int i = 0; i < signalsToSimulate.size(); i++) {
			if (signalsToSimulate.get(i).getAssociatedSymptoms() instanceof SymptomsBySymptom) {
				SymptomsBySymptom symptoms = (SymptomsBySymptom) signalsToSimulate.get(i).getAssociatedSymptoms();
				// Check if data source matches the current data source in paramListing
				if (!wasWarnedInput && !symptoms.getDataSource().equals(paramListing.getDataSource())) {
					int returnVal = JOptionPane.showConfirmDialog(main, "<html><p style=\"width:400px;\">At least one signal's data source does not match the currently loaded data source.  Proceed anyway?</p></html>","Data Source Mismatch",JOptionPane.YES_NO_OPTION);
					if (returnVal == JOptionPane.NO_OPTION) {
						return;
					}
					wasWarnedInput = true;
				}
				// Check if data source of all signals is the same
				for (int j = i + 1; j < signalsToSimulate.size(); j++) {
					if (signalsToSimulate.get(j).getAssociatedSymptoms() instanceof SymptomsBySymptom) {
						if (!wasWarnedMatch && !symptoms.getDataSource().equals(((SymptomsBySymptom) signalsToSimulate.get(j).getAssociatedSymptoms()).getDataSource())) {
							int returnVal = JOptionPane.showConfirmDialog(main, "<html><p style=\"width:400px;\">At least two of your current signals do not have the same data source.  Proceed anyway?</p></html>","Data Source Mismatch",JOptionPane.YES_NO_OPTION);
							if (returnVal == JOptionPane.NO_OPTION) {
								return;
							}
							wasWarnedMatch = true;
						}
					}
				}
			}
		}
		
		File path = new File(main.getOutputPath());
		if(!path.exists() || !path.isDirectory()) {
			JOptionPane.showMessageDialog(main, "Please make sure that the Ouput Path is a valid directory.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		File[] existingTxtFiles = path.listFiles(new FilenameFilter() {
			public boolean accept(File f, String filename) {
				return filename.endsWith(".txt") && filename.startsWith(main.getFilenamePrefix());
			}
		});
		if(existingTxtFiles.length > 0) {
			int result = JOptionPane.showConfirmDialog(main, 
					"The directory already contains " + existingTxtFiles.length + " .txt files with " + main.getFilenamePrefix() + " prefixes.  Export may overwrite those files.\n" +
							"Are you sure you want to export to this directory?", 
					"Warning", 
					JOptionPane.YES_NO_OPTION);
			if(result != JOptionPane.YES_OPTION) {
				return;
			}
		}
				
		Function vx_probs = paramListing.getNewVaxProbDensity(reportsPerInterval);
		Function pt_probs = paramListing.getNewSymProbDensity(reportsPerInterval);
		List<Double> n_vx_probs = paramListing.getVaxPerReportDist();
		List<Double> n_pt_probs = paramListing.getSymPerReportDist();
		
		main.updateSimsRemainingLabel(0); // 0 Finished
		futures.clear();
				
		// validation complete, swap simulation button
		main.simulatedStopEnabled(false);
				
		// Write the input parameters to the INPUT file
		try {
			main.writeOutInputParameters();
		}
		catch (FileNotFoundException fnfe) {
			JOptionPane.showMessageDialog(main, "Cannot find file when writing INPUT text file.", "ERROR", JOptionPane.ERROR_MESSAGE);
			return;
		}
		catch (IOException ioe) {
			JOptionPane.showMessageDialog(main, "Error when writing INPUT text file.", "ERROR", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		// create ChangeListener to handle change events coming from the NetworkSimulators
		SimulationChangeListener changeListener = new SimulationChangeListener();
		
		// RNG that will generate a random seed for each simulation using the original random seed.
		Well19937c seedGenerator = new Well19937c(randomSeed);
		
		// Signal objects are modified during simulation run, so we copy and pass each simulator instance its own copy
		for(int i = 0; i < numSimulations; i++) {
			ArrayList<Signal> signalCopies = new ArrayList<Signal>(signalsToSimulate.size());
			for (Signal origSig : signalsToSimulate) {
				signalCopies.add(new SimulatedSignal(origSig));
			}
			long currentSeed = seedGenerator.nextLong();
			NetworkSimulator networkSimulator = new NetworkSimulator(n_vx, n_pt, n_vx_probs, n_pt_probs, vx_probs, pt_probs, n_reports, (main.isSignalChecked() ? signalCopies : null), currentSeed, i+1);
			// callback for progress updates and completion, and so it can properly set the overall progress
			changeListener.addNetworkSimulator(networkSimulator);
			// submit NetworkSimulator for execution
			Future<?> future = SimulationExecutorService.submitMulti(networkSimulator);
			futures.add(future);
		}
	}
}
