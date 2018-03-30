package com.eng.cber.na.vaers;

import org.apache.commons.collections15.Transformer;

import com.eng.cber.na.graph.AbstractCreateGraph;
import com.eng.cber.na.graph.FDAGraph;
import com.eng.cber.na.graph.GeneralGraph;

/**
 * The VAERS_AbstractTransformer transforms an FDAGraph
 * into another FDAGraph and updates the characteristics
 * of the new graph to match the characteristics of the
 * source graph.
 *
 */
public abstract class VAERS_AbstractTransformer implements Transformer<GeneralGraph, GeneralGraph> { 
	
	protected GeneralGraph getFiltered(GeneralGraph parent) {
		// Start with a blank graph
		GeneralGraph filtered;
		if (parent.isFDAType())
			filtered = new FDAGraph(parent.getDual());
		else
			filtered = new GeneralGraph(parent.getDual());
			
		AbstractCreateGraph.updateGraphCharacteristics(filtered, parent); // necessary so that the filtered graph knows its properties before it is filtered
		return filtered;
	}
}
