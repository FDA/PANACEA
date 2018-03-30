package com.eng.cber.na.vaers;

import java.util.Set;

/**
 * General interface for VAERS_Objects that 
 * allows them to be treated universally in
 * working with graphs. 
 *
 */
public interface VAERS_Object {
	public void appendReport(Object VAERS_ID);
	public Set<?> getReports();
	@Override
	public String toString();
}