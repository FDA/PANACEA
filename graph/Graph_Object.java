package com.eng.cber.na.graph;


/**
 * General interface for VAERS_Objects that 
 * allows them to be treated universally in
 * working with graphs. 
 *
 */
public interface Graph_Object {
	public enum ObjectType {
		NODE, EDGE
	}
	public ObjectType getObjectType();
}