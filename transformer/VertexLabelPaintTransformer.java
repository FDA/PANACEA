package com.eng.cber.na.transformer;

import java.awt.Color;
import java.awt.Paint;
import java.util.Collection;

import org.apache.commons.collections15.Transformer;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.graph.GeneralEdge;
import com.eng.cber.na.graph.GeneralNode;

import edu.uci.ics.jung.visualization.picking.PickedState;

/**
 * The vertex label paint transformer maps vertices to 
 * their label colors; the transform function is used on 
 * each node to find the color with which to draw 
 * its label when rendering.
 *
 */
public class VertexLabelPaintTransformer implements Transformer<GeneralNode, Paint> {

	private static final Paint labelColor = new Color(50,50,50,255);
	private static final Paint labelPickedColor = Color.BLACK;
	private static final Paint labelDeEmphasisColor = new Color(120,120,120,85);
	
	private PickedState<GeneralNode> pickedVertices;
	private PickedState<GeneralEdge> pickedEdges;
	
	public VertexLabelPaintTransformer(PickedState<GeneralNode> pickedVertices, PickedState<GeneralEdge> pickedEdges) {
		this.pickedVertices = pickedVertices;
		this.pickedEdges = pickedEdges;
	}
	
	@Override
	public Paint transform(GeneralNode n) {
		boolean aSelectionExists = false;
		boolean isSelected = false;
		boolean neighborsSelection = false;

		Collection<GeneralNode> neighboringNodes = NetworkAnalysisVisualization.getInstance().getGraph().getNeighbors(n);
		Collection<GeneralEdge> neighboringEdges = NetworkAnalysisVisualization.getInstance().getGraph().getIncidentEdges(n);
		
		// Check the conditions for this particular vertex.
		if (pickedEdges.getPicked().size() > 0 || pickedVertices.getPicked().size() > 0) {
			aSelectionExists = true;
			if (pickedVertices.isPicked(n)) {
				isSelected = true;
			}
			
			for (GeneralNode neighborVertex : neighboringNodes) {
				if (pickedVertices.isPicked(neighborVertex)) {
					neighborsSelection = true;
					break;
				}
			}
			// If already neighbors a selected node, don't bother checking edges.
			if (!neighborsSelection) {
				for (GeneralEdge neighborEdge : neighboringEdges) {
					if (pickedEdges.isPicked(neighborEdge)) {
						neighborsSelection = true;
						break;
					}
				}
			}
		}
		
		// Choose the return color based on the selection criteria
		if (isSelected) {
			return labelPickedColor;
		}
		else {
			if (aSelectionExists) {
				return neighborsSelection ? labelColor : labelDeEmphasisColor;
			}
			else {
				return labelColor;
			}
		}
		
		
	}
}
