package com.eng.cber.na.transformer;

import java.awt.Color;
import java.awt.Paint;

import org.apache.commons.collections15.Transformer;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.graph.GeneralEdge;
import com.eng.cber.na.graph.GeneralNode;

import edu.uci.ics.jung.visualization.picking.PickedState;

/**
 * A class that transforms each edge into its corresponding
 * color, depending on whether the edge is selected or
 * unselected and whether it is a direct neighbor to a
 * selected node.
 *
 */
public class EdgeDrawPaintTransformer implements Transformer<GeneralEdge, Paint> {

	private Paint edgeColor = new Color(145,145,145,255);
	public static final Paint edgePickedColor = Color.CYAN;
	private Paint edgeDeEmphasisColor = new Color(200,200,200,255);

	private PickedState<GeneralNode> pickedVertices;
	private PickedState<GeneralEdge> pickedEdges;
	private int edgeDarkness;
	
	public EdgeDrawPaintTransformer(PickedState<GeneralNode> pickedVertices, PickedState<GeneralEdge> pickedEdges) {
		this.pickedVertices = pickedVertices;
		this.pickedEdges = pickedEdges;
		
		setEdgeDarkness(90);
	}
	
	public void setEdgeDarkness(int edgeDarkness) {
		this.edgeDarkness = edgeDarkness;
		this.edgeColor = new Color(255-edgeDarkness,255-edgeDarkness,255-edgeDarkness,255);
		int edgeDeEmph = (int) ((0.5 * edgeDarkness) + (255 - edgeDarkness));
		this.edgeDeEmphasisColor = new Color(edgeDeEmph,edgeDeEmph,edgeDeEmph,255);
	}
	
	@Override
	public Paint transform(GeneralEdge e) {
		boolean aSelectionExists = false;
		boolean isSelected = false;
		boolean neighborsSelection = false;
		
		GeneralNode nodeFrom;
		GeneralNode nodeTo;
		
		if (NetworkAnalysisVisualization.getInstance().getEdgeDarkness() != edgeDarkness) {
			setEdgeDarkness(NetworkAnalysisVisualization.getInstance().getEdgeDarkness());
		}
		
		try {
			nodeFrom = NetworkAnalysisVisualization.getInstance().getGraph().getFrom(e);
			nodeTo = NetworkAnalysisVisualization.getInstance().getGraph().getTo(e);
		}
		catch (NullPointerException npe) {
			return edgeColor;
		}
		
		// Check the conditions for this particular edge.
		if (pickedEdges.getPicked().size() > 0 || pickedVertices.getPicked().size() > 0) {
			aSelectionExists = true;
			if (pickedEdges.isPicked(e)) {
				isSelected = true;
			}
			if (pickedVertices.isPicked(nodeFrom) || pickedVertices.isPicked(nodeTo)) {
				neighborsSelection = true;
			}
		}
		
		if (isSelected) {
			return edgePickedColor;
		}
		else {
			if (aSelectionExists) {
				return neighborsSelection ? edgeColor : edgeDeEmphasisColor;
		}
			else {
				return edgeColor;
			}
		}
		
	}
}
