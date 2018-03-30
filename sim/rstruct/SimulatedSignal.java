package com.eng.cber.na.sim.rstruct;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.eng.cber.na.sim.gui.signal.AssociatedSymptoms;
import com.eng.cber.na.sim.gui.signal.SymptomsBySymptom;

/**
 * Implementation of a signal used in the simulation. <br/><br/>
 * 
 * A signal consists of:<br/>
 * 1) a numbered report for the vaccine to appear<br/>
 * 2) a vaccine weight (or fitness)<br/>
 * 3) background occurrence rates for PTs associated with vaccine<br/>
 * 4) co-occurrence rates for PTs associated with vaccine<br/><br/>
 * 
 * When the signal vaccine enters the simulation, specific
 * PTs that already exist in the simulation will be chosen 
 * to make up the "syndrome" of PTs that are associated with
 * the vaccine by looking for PTs that match the requested
 * background occurrence rates.  
 */

public class SimulatedSignal implements Signal {
	
	private int vx_entry, vx_weight, vx_index;
	private String signalName, outputVaxName;
	private List<Double> pt_ranks, pt_probs;
	private List<Integer> pt_syndrome;
	private List<String> pt_syndrome_names;
	private AssociatedSymptoms associatedSymptoms;
	
	public SimulatedSignal(String signalName,
						   int vx_entry, 
						   int vx_weight,
						   AssociatedSymptoms associatedSymptoms
						   ) {
		this.signalName = signalName;
		this.vx_entry = vx_entry;
		this.vx_weight = vx_weight;
		this.associatedSymptoms = associatedSymptoms;
		
		this.pt_ranks = this.associatedSymptoms.getPTRanks();
		this.pt_probs = this.associatedSymptoms.getPTProbs();
		this.vx_index = -1;
	}
	
	/** Copy Constructor **/
	public SimulatedSignal(Signal copy) {
		this.signalName = copy.getSignalName();
		this.vx_entry = copy.getVXEntry();
		this.vx_weight = copy.getVXWeight();
		
		this.associatedSymptoms = copy.getAssociatedSymptoms();
		// Not a true copy, but pt_ranks and pt_probs are not
		// taken from here, and nothing will be changed inside, so no copy needed.
		
		this.pt_ranks = new ArrayList<Double>(copy.getPTRanks());
		this.pt_probs = new ArrayList<Double>(copy.getPTProbs());
		this.vx_index = -1;
	}

	@Override
	public String toString() {
		return signalName;
	}
	
	@Override
	public String getSignalName() {
		return signalName;
	}
	
	@Override
	public int getVXEntry() {
		return vx_entry;
	}

	@Override
	public int getVXWeight() {
		return vx_weight;
	}

	@Override
	public int getVXIndex() {
		return vx_index;
	}

	@Override
	public void setVXIndex(int index) {
		this.vx_index = index;
	}
	
	@Override
	public String getOutputVaxName() {
		return outputVaxName;
	}
	
	@Override
	public void setOutputVaxName(String outputVaxName) {
		this.outputVaxName = outputVaxName;
	}

	@Override
	public List<Double> getPTRanks() {
		return pt_ranks;
	}

	@Override
	public List<Double> getPTProbs() {
		return pt_probs;
	}

	@Override
	public AssociatedSymptoms getAssociatedSymptoms() {
		return associatedSymptoms;
	}
	
	@Override
	public List<Integer> getPTSyndrome() {
		return pt_syndrome;
	}

	@Override
	public void resetPTSyndrome() {
		if(pt_syndrome == null) {
			pt_syndrome = new ArrayList<Integer>(pt_ranks.size());
			for(int i = 0; i < pt_ranks.size(); i++) {
				pt_syndrome.add(-1);
			}
		}
		else {
			Collections.fill(pt_syndrome, -1);
		}
	}
	
	@Override
	public List<String> getPTSyndromeNames() {
		return pt_syndrome_names;
	}
	
	@Override
	public void setPTSyndromeNames(List<String> pt_syndrome_names) {
		this.pt_syndrome_names = pt_syndrome_names;
	}
	
	@Override
	public String getDescription() {
		String extraBySymptomInfo="";
		
		if (associatedSymptoms instanceof SymptomsBySymptom) {
			extraBySymptomInfo = "Co-Occurence Vaccine:  " + ((SymptomsBySymptom) associatedSymptoms).getVaxName() + "\n" +
						         "Selected Symptoms:     " + ((SymptomsBySymptom) associatedSymptoms).getSymptoms() + "\n" +
						         "Data Source:           " + ((SymptomsBySymptom) associatedSymptoms).getDataSource() + "\n";
		}
		
		List<String> ptRanksAsStrings = new ArrayList<String>(associatedSymptoms.getPTRanks().size());
		List<String> ptProbsAsStrings = new ArrayList<String>(associatedSymptoms.getPTProbs().size());
		for (int i = 0; i < associatedSymptoms.getPTRanks().size(); i++) {
			ptRanksAsStrings.add(String.format("%1.4f", associatedSymptoms.getPTRanks().get(i)));
			ptProbsAsStrings.add(String.format("%1.4f", associatedSymptoms.getPTProbs().get(i)));
		}
		
		
		return "Signal Name:           " + signalName + "\n" +
			   "Vaccine Entry Report:  " + vx_entry + "\n" +
			   "Vaccine Weight:        " + vx_weight + "\n" +
			   "Associated PT Type:    " + (associatedSymptoms instanceof SymptomsBySymptom ? "By Symptom" : "By Relative Rank") + "\n" +
			   extraBySymptomInfo + 
			   "Symptom Ranks:         " + ptRanksAsStrings + "\n" +
			   "Symptom Probs:         " + ptProbsAsStrings + "\n";
	}
	
	@Override
	public void write(Writer writer) throws IOException {
		
		Object[] objs = {signalName, outputVaxName, vx_entry, vx_weight, vx_index, pt_syndrome_names, pt_ranks, pt_probs, pt_syndrome};
		
		for(int j = 0; j < objs.length; j++) {
			writer.write(labels[j] + ": ");
			
			if(j < 5) {
				writer.write(objs[j] + "\n");
			}
			else if(j < 6){
				List<String> c = (List<String>)objs[j];
				for(int i = 0; i < c.size(); i++) {
					writer.write(c.get(i) + (i < c.size() - 1 ? "," : "\n"));
				}
			}
			else if(j < 8){
				List<Double> c = (List<Double>)objs[j];
				for(int i = 0; i < c.size(); i++) {
					writer.write(String.format("%1.4f", c.get(i)) + (i < c.size() - 1 ? "," : "\n"));
				}
			}
			else {
				List<Integer> c = (List<Integer>)objs[j];
				for(int i = 0; i < c.size(); i++) {
					writer.write(c.get(i) + (i < c.size() - 1 ? "," : "\n"));
				}
			}
		}
		
		if (associatedSymptoms instanceof SymptomsBySymptom) {
			SymptomsBySymptom symptomsBySymptom = (SymptomsBySymptom) associatedSymptoms;
			writer.write("Input Vaccine Name: " + symptomsBySymptom.getVaxName() + "\n");
			writer.write("Input PT Names: " + symptomsBySymptom.getSymptoms() + "\n");
			writer.write("Input Data Source: " + symptomsBySymptom.getDataSource() + "\n");
		}
		
		writer.write("\n");
	}
}
