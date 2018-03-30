package com.eng.cber.na.sim;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.EnumSet;
import java.util.List;

import com.eng.cber.na.sim.NetworkSimulatorGUI.OutputFlag;
import com.eng.cber.na.sim.rstruct.EdgeListOutput;
import com.eng.cber.na.sim.rstruct.Signal;
import com.eng.cber.na.sim.rstruct.WriteableObject;

/**
 *  For a specific NetworkSimulator object, creates all of the
 *  desired output files.  Always writes a SIGNAL file with all
 *  the info about the input signal(s).  Optionally writes the
 *  MATRIX file (the list of weights for each vaccine/PT based
 *  on number of connections with other terms), the PT & VAX
 *  files (the standard format for PANACEA), and the EDGELIST file
 *  (the full list of connection between terms, useful for
 *  network visualization software).
 */
public class SimulationExporter {
	
	private NetworkSimulator sim;
	
	private int bufferSizeInKB = 16;
	
	public SimulationExporter(NetworkSimulator sim) {
		this.sim = sim;
	}
	
	/** Write the SIGNAL file and optionally, the MATRIX, PT, VAX, and EDGELIST files. **/
	public void exportSimulation() {		
		NetworkSimulatorGUI main = NetworkSimulatorGUI.getInstance();
		EnumSet<OutputFlag> flags = main.getOutputFlags();

		String prefix = main.getFilenamePrefix().trim();
		String writeFileName;
		
		File f = new File(main.getOutputPath());			
		BufferedWriter writer;
		
		
		// Writes the SIGNAL file
		List<Signal> sigs = sim.getSignals();
		if(prefix.length() > 0) {
			writeFileName = f.getAbsolutePath() + "/" + String.format("%s_SIGNAL_%d.txt",prefix,sim.getSimNumber());							
		}
		else {
			writeFileName = f.getAbsolutePath() + "/" + String.format("SIGNAL_%d.txt",sim.getSimNumber());
		}
			
		try {
			writer = new BufferedWriter(new FileWriter(writeFileName), bufferSizeInKB*1024);
			
			if (!main.isSignalChecked() || sigs == null || sigs.size() == 0) {
				writer.write("NO SIGNAL");
			}
			else {
				for (Signal sig : sigs) {
					sig.write(writer);
				}
			}
			writer.flush();
			writer.close();
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		// Writes the MATRIX file
		if (flags.contains(OutputFlag.OUTPUT_MATRIX)) {
			if(prefix.length() > 0) {
				writeFileName = f.getAbsolutePath() + "/" + String.format("%s_MATRIX_%d.txt",prefix,sim.getSimNumber());							
			}
			else {
				writeFileName = f.getAbsolutePath() + "/" + String.format("MATRIX_%d.txt",sim.getSimNumber());
			}
			try {
				writer = new BufferedWriter(new FileWriter(writeFileName), bufferSizeInKB*1024);
				sim.getMatrix().write(writer);
				writer.flush();
				writer.close();						
			} 
			catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		
		// Writes the PT & VAX files
		if (flags.contains(OutputFlag.OUTPUT_PANACEA)) {
			String[] names = {"PT_%d.txt", "VAX_%d.txt"};
			Object[] objs = {sim.getPanaceaPT(), sim.getPanaceaVX()};
			for(int i = 0; i < objs.length; i++) {
				if(prefix.length() > 0) {
					writeFileName = f.getAbsolutePath() + "/" + String.format("%s_" + names[i],prefix,sim.getSimNumber());
				}
				else {
					writeFileName = f.getAbsolutePath() + "/" + String.format(names[i],sim.getSimNumber());
				}
				try {
					writer = new BufferedWriter(new FileWriter(writeFileName), bufferSizeInKB*1024);
					if (objs[i] instanceof WriteableObject) {
						((WriteableObject)objs[i]).write(writer);
					}
					writer.flush();
					writer.close();						
				} 
				catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
				catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}
		
		// Writes the EDGELIST file
		if (flags.contains(OutputFlag.OUTPUT_EDGELIST)) {
			if(prefix.length() > 0) {
				writeFileName = f.getAbsolutePath() + "/" + String.format("%s_EDGELIST_%d.txt",prefix,sim.getSimNumber());							
			}
			else {
				writeFileName = f.getAbsolutePath() + "/" + String.format("EDGELIST_%d.txt",sim.getSimNumber());
			}
			try {
				writer = new BufferedWriter(new FileWriter(writeFileName), bufferSizeInKB*1024);
				EdgeListOutput elOutput = new EdgeListOutput(sim.getMatrix());
				elOutput.write(writer);
				writer.flush();
				writer.close();
			} 
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
}
