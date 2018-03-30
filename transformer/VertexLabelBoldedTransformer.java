package com.eng.cber.na.transformer;

import org.apache.commons.collections15.Transformer;

import com.eng.cber.na.graph.GeneralNode;

import edu.uci.ics.jung.visualization.picking.PickedState;

/**
 * The vertex label bolded transformer tells a vertex if
 * it should print its label in bold or not.
 */
public class VertexLabelBoldedTransformer implements Transformer<GeneralNode, Boolean> {

	
	private PickedState<GeneralNode> pickedVertices;
	
	public VertexLabelBoldedTransformer(PickedState<GeneralNode> pickedVertices) {
		this.pickedVertices = pickedVertices;
	}
	
	@Override
	public Boolean transform(GeneralNode n) {
		
		if (pickedVertices.isPicked(n)) {
			return true;
		}
		else {
			return false;
		}
		
	}
}
