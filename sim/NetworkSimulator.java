package com.eng.cber.na.sim;

import java.awt.Cursor;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.SwingWorker;

import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.random.Well19937c;

import com.eng.cber.na.sim.rstruct.PanaceaOutput;
import com.eng.cber.na.sim.rstruct.Function;
import com.eng.cber.na.sim.rstruct.Matrix;
import com.eng.cber.na.sim.rstruct.Output;
import com.eng.cber.na.sim.rstruct.OutputTuple;
import com.eng.cber.na.sim.rstruct.Signal;
import com.eng.cber.na.sim.rstruct.SimulationMatrix;

/**
 * This class runs a single simulation using the doInBackground()
 * method.  If requested, one or more signals will be introduced.
 * It also calls the exporter to write the output files. <br/><br/>
 * 
 * See NetworkSimulatorGUI for an explanation of how the simulator
 * operates.
 */
public class NetworkSimulator extends SwingWorker<Object,Object> {

	private int start_vx, start_pt;
	private List<Double> vx_probs, pt_probs; 
	private Function p_vx, p_pt;
	private int report_num;
	private List<Signal> signals; 
	
	private UniformRealDistribution uniformRNG;
	private int simNumber;
	
	private boolean hasSignals;
	
	private Matrix matrix;
	private Output panacea_vx, panacea_pt;

	private List<Integer> updated;
	private List<Double> ptNew, vxNew;
	
	/**
	 * Constructs an instance of the network simulator.
	 * 
	 * @param start_vx Number of starting vaccine nodes
	 * @param start_pt Number of starting PT nodes
	 * @param vx_probs Distribution of number of vaccine nodes per report
	 * @param pt_probs Distribution of number of PT nodes per report
	 * @param p_vx Decay function for proportion of vaccine nodes that are new
	 * @param p_pt Decay function for proportion of pt nodes that are new
	 * @param report_num Number of reports
	 * @param signals A control structure describing a planted signal
	 * @param randomSeed A seed for the random number generator
	 * @param simNumber The number assigned to this simulator (from order they were created)
	 */
	public NetworkSimulator(int start_vx,
							int start_pt,
							List<Double> vx_probs,
							List<Double> pt_probs,
							Function p_vx,
							Function p_pt,
							int report_num,
							List<Signal> signals, 
							long randomSeed, 
							int simNumber
							) { 
		
		this.start_vx = start_vx;
		this.start_pt = start_pt;
		this.vx_probs = vx_probs;
		this.pt_probs = pt_probs;
		this.p_vx = p_vx;
		this.p_pt = p_pt;
		this.report_num = report_num;
		this.signals = signals;
		this.simNumber = simNumber;
		
		// Prepare random number generator
		uniformRNG = new UniformRealDistribution(new Well19937c(randomSeed), 0, 1);
		
		this.ptNew = new ArrayList<Double>(report_num);
		this.vxNew = new ArrayList<Double>(report_num);
	}
	
	public Matrix getMatrix() {
		return matrix;
	}
	
	public Output getPanaceaVX() {
		return panacea_vx;
	}
	
	public Output getPanaceaPT() {
		return panacea_pt;
	}

	public List<Signal> getSignals() {
		return signals;
	}
	
	public int getSimNumber() {
		return simNumber;
	}
	
	public List<Double> getVxPerReportDist() {
		return vx_probs;
	}

	public List<Double> getPtPerReportDist() {
		return pt_probs;
	}
	
	public List<Double> getVxNewProb() {
		return vxNew;
	}
	
	public List<Double> getPtNewProb() {
		return ptNew;
	}

	@Override
	public Object doInBackground() {
		try{
			NetworkSimulatorGUI.getInstance().setCursor(new Cursor(Cursor.WAIT_CURSOR));
	
			// instantiate and init matrix
			matrix = new SimulationMatrix(start_pt, start_vx);
			
			updated = new LinkedList<Integer>();
			
			panacea_vx = new PanaceaOutput();
			panacea_pt = new PanaceaOutput();
			
			for (int i = 1; i <= start_vx; i++) {
				panacea_vx.addOutputTuple(new OutputTuple(0,"vx" + i, "", "", ""));
			}
			for (int j = 1; j <= start_pt; j++) {
				panacea_pt.addOutputTuple(new OutputTuple(0,"pt" + j, "", "", ""));
			}
			
			setProgress(0);
			
			hasSignals = (signals != null);
			
			for(int i = 1; i < report_num; i++) {
				if(i % 10 == 0) {
					setProgress(Math.min(99, (int)((double)(i+1)/report_num*100)));
				}
				nextReport(i);
				
				if(Thread.interrupted()) {
					firePropertyChange("interrupted",null,null);
					return null;
				}
			}
			
			matrix.setDiag(0);
			
			// Output the files
			SimulationExporter simExporter = new SimulationExporter(this);
			simExporter.exportSimulation();
			
			setProgress(99);
		}
		finally {
			NetworkSimulatorGUI.getInstance().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
		return null;
	}
	
	/** Creates and adds the next report **/
	private void nextReport(int iter) {
		int num_vx = Util.sample(uniformRNG,Util.makeIntList(1,vx_probs.size()),vx_probs, 1).get(0);
		int num_pt = Util.sample(uniformRNG,Util.makeIntList(1,pt_probs.size()),pt_probs, 1).get(0);
		
		double vxNewProb = p_vx.eval(iter);
		vxNew.add(vxNewProb);
		
		double ptNewProb = p_pt.eval(iter);
		ptNew.add(ptNewProb);
		
		int new_vx = Util.sampleBinomial((long) (uniformRNG.sample()*Long.MAX_VALUE), num_vx, vxNewProb, 1).get(0);
		int new_pt = Util.sampleBinomial((long) (uniformRNG.sample()*Long.MAX_VALUE), num_pt, ptNewProb, 1).get(0);
		
		if(num_vx - new_vx > matrix.getVaxCount()) {
			num_vx = matrix.getVaxCount() + new_vx;
		}
		
		if(num_pt - new_pt > matrix.getPtCount()) {
			num_pt = matrix.getPtCount() + new_pt;
		}
		
		
		/** If there is a signal **/
		/* 
		 * If there is a signal, then add the signal vaccine
		 * into the matrix, and give it the desired weight
		 * (fitness).  Also selects the PT's that will make
		 * up the syndrome.
		 */
		if (hasSignals) {
			for (Signal signal : signals) {
				if(signal != null && signal.getVXEntry() == iter) {			
					
					matrix.addNewVX(0);
					int matSize = matrix.getPtCount() + matrix.getVaxCount();
					matrix.increment(matSize - 1, matSize - 1, signal.getVXWeight()); //Adding the fitness
					
					signal.setVXIndex(matSize - 1);
					signal.setOutputVaxName("vx"+matrix.getVaxCount());
					signal.resetPTSyndrome();
					
					// Rank the list of current PT nodes and get their percentiles.
					List<Integer> nodes_ranked = Util.order(matrix.getDiagList(matrix.getPTIndicies()));
					List<Double> nodes_percentile = Util.genPercentiles(nodes_ranked.size());
					
					List<Double> pt_ranks = signal.getPTRanks();
					List<Integer> pt_syndrome = signal.getPTSyndrome();
					ArrayList<String> pt_syndrome_names = new ArrayList<String>(pt_syndrome.size());
					while (pt_syndrome_names.size() < pt_syndrome.size())
						pt_syndrome_names.add("");
					
					List<Integer> nodes_candidates = null;
					
					for(int i = 0; i < pt_ranks.size(); i++) {
						boolean nodes_enough = false;
						double nodes_window = 0.01;
						
						while(!nodes_enough) {
							List<Integer> ind = new ArrayList<Integer>();
							for(int j = 0; j < nodes_percentile.size(); j++) {
								if(Math.abs(nodes_percentile.get(j) - pt_ranks.get(i)) < nodes_window) {
									ind.add(j);
								}
							}
							nodes_candidates = Util.subList(nodes_ranked, ind);
							nodes_candidates.removeAll(pt_syndrome);
							
							if(nodes_candidates.size() >= 1) {
								nodes_enough = true;
							}
							else {
								nodes_window += 0.01;
							}
						}
						
						// Sets the matrix indicies of the PTs that were chosen to be in pt_syndrome
						pt_syndrome.set(i, Util.sample(uniformRNG,nodes_candidates, 1).get(0));
						
						// Set the names of the PTs that were chosen to be in pt_syndrome
						pt_syndrome_names.set(i, "pt" + (matrix.getPTIndicies().indexOf(pt_syndrome.get(i)) + 1));	
					}		
					signal.setPTSyndromeNames(pt_syndrome_names);
				}
			}
		}
		
		/*
		 * Pick all the old VAXs that will be used in this report, 
		 * and add their indices to updated.  The probability for
		 * picking each old VAX is proportional to its diagonal
		 * element in the matrix.
		 */
		if(new_vx < num_vx) {
			List<Integer> indicies = matrix.getVXIndicies();
			if(indicies.contains(-1))
				System.err.println(this + " new_vx < num_vx");
			
			List<Integer> old_vx = Util.sample(uniformRNG, indicies, Util.normalizeIntList(matrix.getDiagList(indicies)), num_vx-new_vx);
			updated.addAll(old_vx);
		}
		
		if(new_vx > 0) {
			for(int i = 0; i < new_vx; i++) {
				int ind = matrix.addNewVX(0);
				updated.add(ind);
			}
		}
		
		/* If there is a signal, checks if it should add the
		 * syndrome PTs to the report if the signal Vax is in it.
		 */
		if (hasSignals) {
			for (Signal signal : signals) {
				if(signal != null && signal.getVXEntry() <= iter && signal.getVXIndex() != -1) {	
					// update updated list
					if(updated.contains(signal.getVXIndex())) {
						for(int i = 0; i < signal.getPTSyndrome().size(); i++) {
							Double n = signal.getPTProbs().get(i);
							if(Util.sampleBinomial((long) (uniformRNG.sample()*Long.MAX_VALUE), 1, n, 1).get(0) == 1) {
								Integer ind = signal.getPTSyndrome().get(i);
								// "ind" is the index in the full matrix, not in the PTIndicies 
								updated.add(ind);
							}
						}
					}
				}
			}
		}
		
		/*
		 * Pick all the old PTs that will be used in this report, 
		 * and add their indices to updated.  The probability for
		 * picking each old PT is proportional to its diagonal
		 * element in the matrix.
		 */
		if(new_pt < num_pt) {
			List<Integer> indicies = matrix.getPTIndicies();
			
			List<Integer> old_vx = Util.sample(uniformRNG, indicies, Util.normalizeIntList(matrix.getDiagList(indicies)), num_pt-new_pt);
			updated.addAll(old_vx);
		}
		
		if(new_pt > 0) {
			for(int i = 0; i < new_pt; i++) {
				int ind = matrix.addNewPT(0);
				updated.add(ind);
			}
		}
		
		/*
		 * Add one to the weight of the connections between all
		 * terms in this report.  Also will increase the total weight
		 * of each Vax/PT (stored in the diagonal elements) by the
		 * number of new connections it made by being in this report.
		 */
		for(Integer i : updated) {
			for(Integer j : updated) {
				if (i == j) {
					matrix.increment(i, j, updated.size() - 1);
				}
				else {
					matrix.increment(i, j);
				}
			}
		}
		
		List<Integer> vxs = matrix.getVXIndicies();
		List<Integer> pts = matrix.getPTIndicies();
		
		for(Integer i : updated) {
			if(vxs.contains(i)) {
				panacea_vx.addOutputTuple(new OutputTuple(iter, "vx" + (vxs.indexOf(i) + 1), "", "", ""));
			}
			else {
				panacea_pt.addOutputTuple(new OutputTuple(iter, "pt" + (pts.indexOf(i) + 1), "", "", ""));
			}
		}
		
				
		updated.clear();
	}
	
}