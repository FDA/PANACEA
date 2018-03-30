package com.eng.cber.na.sim.rstruct;

import java.io.IOException;
import java.io.Writer;

/**
 * Defines a Writeable Object.
 * 
 */

public interface WriteableObject {
	
	public void write(Writer writer) throws IOException;
}
