package com.eng.cber.na.sim.rstruct;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

/**
 * PanaceaOutput implements the output container for the simulation.
 * Writes the OutputTuples to a file.  This class is responsible
 * for creating the PT and VAX files.
 *
 */
public class PanaceaOutput implements Output {

	private List<OutputTuple> lst = new LinkedList<OutputTuple>();
	
	@Override
	public void addOutputTuple(OutputTuple t) {
		lst.add(t);
	}

	@Override
	public List<OutputTuple> getOutputTuples() {
		return lst;
	}

	@Override
	public void write(Writer writer) throws IOException{
		for(OutputTuple ot : lst) {
			writer.write("\"" + ot.getReport() + "\", \"" + ot.getPt() + "\", \"" + ot.getHlt() + "\", \"" + ot.getHglt() + "\", \"" + ot.getSoc() + "\"\n");
		}		
	}

	@Override
	public void clear() {
		lst.clear();
	}
}
