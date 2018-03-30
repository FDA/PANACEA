package com.eng.cber.na.transformer;

import java.awt.Color;
import java.awt.Paint;
import java.util.Collection;

import org.apache.commons.collections15.Transformer;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.graph.GeneralEdge;
import com.eng.cber.na.graph.GeneralNode;
import com.eng.cber.na.vaers.VAERS_Node;
import com.eng.cber.na.vaers.VAERS_Node.NodeType;

import edu.uci.ics.jung.visualization.picking.PickedState;

/**
 * The vertex fill paint transformer maps vertices to 
 * their colors; the transform function is used on 
 * each node to find the color with which to draw 
 * it when rendering.
 *
 */
public class VertexFillPaintTransformer implements Transformer<GeneralNode, Paint> {

	private static int deEmphasisAlpha = 45;
	
	private static final Paint symColor = Color.BLUE;
	private static final Paint symPickedColor = Color.GREEN;
	private static final Paint refColor = Color.orange;
	private static final Paint refPickedColor = Color.magenta;
	private static final Paint vaxColor = Color.RED;
	private static final Paint vaxPickedColor = Color.YELLOW;
	private static final Paint symDeEmphasisColor = new Color(((Color)symColor).getRed(), ((Color)symColor).getGreen(), ((Color)symColor).getBlue(), deEmphasisAlpha);
	private static final Paint vaxDeEmphasisColor = new Color(((Color)vaxColor).getRed(), ((Color)vaxColor).getGreen(), ((Color)vaxColor).getBlue(), deEmphasisAlpha);
	private static final Paint refDeEmphasisColor = new Color(((Color)refColor).getRed(), ((Color)refColor).getGreen(), ((Color)refColor).getBlue(), deEmphasisAlpha);
	
	
	private PickedState<GeneralNode> pickedVertices;
	private PickedState<GeneralEdge> pickedEdges;
	
	public VertexFillPaintTransformer(PickedState<GeneralNode> pickedVertices, PickedState<GeneralEdge> pickedEdges) {
		this.pickedVertices = pickedVertices;
		this.pickedEdges = pickedEdges;
	}

	@Override
	public Paint transform(GeneralNode n) {
		boolean aSelectionExists = false;
		boolean isSelected = false;
		boolean neighborsSelection = false;
		boolean isVAERSNode = false;
		boolean isInCluster = false;

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
		
		if (n instanceof VAERS_Node) {
			isVAERSNode = true;
		}
		
		if (n.getClusterColor() != null && NetworkAnalysisVisualization.getInstance().shouldShowClusterColoring()) {
			isInCluster = true;
		}
		
		// Choose the return color based on all the selection criteria
		if (isSelected) { // Is selected (1)
			if (isVAERSNode) { // Is a VAERS_Node (2)
				return ((VAERS_Node) n).getNodeType() == NodeType.VAX ? vaxPickedColor : 
					(((VAERS_Node) n).getNodeType() == NodeType.SYM ? symPickedColor : 
						((VAERS_Node) n).getNodeType() == NodeType.REFERENCE? refPickedColor : symPickedColor);
			}
			else { // Not a VAERS_Node (2)
				return vaxPickedColor;
			}
		}
		else { // Not selected (1)
			if (isInCluster) { // Is in cluster (2)
				if (aSelectionExists) { // A selection exists (3)
					return neighborsSelection ? n.getClusterColor() : new Color(n.getClusterColor().getRed(),n.getClusterColor().getGreen(),n.getClusterColor().getBlue(), deEmphasisAlpha);
				}
				else { // No selection exists (3)
					return n.getClusterColor();
				}
			}
			else { // Not in cluster (2)
				if (isVAERSNode) { // Is a VAERS_Node (3)
					if (aSelectionExists) { // A selection exists (4)
						if (neighborsSelection) { // Neighbors selection (5)
							return ((VAERS_Node) n).getNodeType() == NodeType.VAX ? vaxColor : 
									(((VAERS_Node) n).getNodeType() == NodeType.REFERENCE ? refColor : symColor);
						}
						else { // Doesn't neighbor selection (5)
							return ((VAERS_Node) n).getNodeType() == NodeType.VAX ? vaxDeEmphasisColor : 
								(((VAERS_Node) n).getNodeType() == NodeType.REFERENCE ? refDeEmphasisColor: symDeEmphasisColor);
						}
					}
					else { // No selection exists (4)
						return ((VAERS_Node) n).getNodeType() == NodeType.VAX ? vaxColor : 
							(((VAERS_Node) n).getNodeType() == NodeType.REFERENCE ? refColor : symColor); 
					}
				}
				else { // Not a VAERS_Node (3)
					if (aSelectionExists) { // A selection exists (4)
						return neighborsSelection ? vaxColor : vaxDeEmphasisColor;
					}
					else { // No selection exists (4)
						return vaxColor;
					}
				}
			}
		}
		

	}
}
