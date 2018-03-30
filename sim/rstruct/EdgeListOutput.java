package com.eng.cber.na.sim.rstruct;

import java.io.IOException;
import java.io.Writer;

/** A WriteableObject that can create the output
 * edgelist file when given a simulation matrix.
 */
public class EdgeListOutput implements WriteableObject {

	private Matrix simMatrix;
	
	public EdgeListOutput(Matrix simMatrix) {
		this.simMatrix = simMatrix;
	}
	
	@Override
	public void write(Writer writer) throws IOException {
		int matSize = simMatrix.getVaxCount() + simMatrix.getPtCount();
		
		for (int i=0; i<matSize; i++) {
			for (int j=0; j<i; j++) {
				String prefix, iName, jName;
				// Get name of row
				if (simMatrix.getVXIndicies().contains(i)) {
					prefix = "vx";
					iName = prefix + (simMatrix.getVXIndicies().indexOf(i) + 1); 
				}
				else {
					prefix = "pt";
					iName = prefix + (simMatrix.getPTIndicies().indexOf(i) + 1);
				}
				
				// Get name of column
				if (simMatrix.getVXIndicies().contains(j)) {
					prefix = "vx";
					jName = prefix + (simMatrix.getVXIndicies().indexOf(j) + 1); 
				}
				else {
					prefix = "pt";
					jName = prefix + (simMatrix.getPTIndicies().indexOf(j) + 1);
				}
				
				int weight = simMatrix.getRow(i).get(j);
				if (weight > 0) {
					writer.write(iName + "," + jName + "," + simMatrix.getRow(i).get(j) + "\n");
				}
			}
		}
	}

}
