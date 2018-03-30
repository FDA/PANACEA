package com.eng.cber.na.graphevent;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Set;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.command.ClearAllAnnotations;
import com.eng.cber.na.command.CreateSelectedSubGraph;
import com.eng.cber.na.command.DeleteSelectionCommand;
import com.eng.cber.na.command.ExpandSelectionCommand;
import com.eng.cber.na.command.InvertSelectionCommand;
import com.eng.cber.na.command.RemoveLastAnnotation;
import com.eng.cber.na.command.SaveNetworkSnapshotCommand;
import com.eng.cber.na.command.SelectAllNodesCommand;
import com.eng.cber.na.command.SelectNodesFromListCommand;
import com.eng.cber.na.command.SetSimilarityThresholdCommand;
import com.eng.cber.na.command.ViewPropertiesCommand;
import com.eng.cber.na.event.PopupMenu;
import com.eng.cber.na.event.PopupMenuItem;
import com.eng.cber.na.event.mouse.AbstractPopupMouseEvent;
import com.eng.cber.na.gl.NetworkGLVisualizationServer;
import com.eng.cber.na.graph.GeneralEdge;
import com.eng.cber.na.graph.GeneralNode;
import com.eng.cber.na.vaers.VAERS_Node;
import com.eng.cber.na.vaers.VAERS_Node.NodeType;

import edu.uci.ics.jung.visualization.picking.PickedState;

/**
 * Class that controls right clicks on the network.
 * Displays a popup menu with a set of choices,
 * with actions defined below.
 *
 */
public class DefaultPopupGraphMouseEvent extends AbstractPopupMouseEvent implements GraphMouseEvent {

	public DefaultPopupGraphMouseEvent(ActionListener al) {
		super(InputEvent.BUTTON3_MASK);		
		addItem("Select All nodes", SelectionType.NONE, null, al, new SelectAllNodesCommand(), KeyEvent.VK_A);
		addItem("Expand Selection", SelectionType.VERTEX, null, al, new ExpandSelectionCommand()); 
		addItem("Invert Selection", SelectionType.VERTEX, null, al, new InvertSelectionCommand());
		addItem("Select Nodes From List...", SelectionType.NONE, null, al, new SelectNodesFromListCommand());
		addSeparator();
		addItem("Create Subnetwork from Selection", SelectionType.VERTEX,null, al, new CreateSelectedSubGraph());
		addItem("Delete Selection", SelectionType.VERTEX, null, al, new DeleteSelectionCommand());
		addSeparator();
		addItem("Save Network Snapshot...", SelectionType.NONE, null, al, new SaveNetworkSnapshotCommand("JPEG"));
		addSeparator();
		addItem("Remove Last Annotation", SelectionType.NONE, null, al, new RemoveLastAnnotation());
		addItem("Clear All Annotations", SelectionType.NONE, null, al, new ClearAllAnnotations());
		addSeparator();
		addItem("Set Minimum Similarity", SelectionType.REFERENCE, new String[] {"By Value...", "By Common Terms..."}, al, new SetSimilarityThresholdCommand());
		addSeparator();
		addItem("View Properties", SelectionType.NONE, null, al, new ViewPropertiesCommand());
	}	
	
	@Override
	public void alterPopupMenu(Component comp) {
		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
		if(nv.getGraph() == null || nv.getGraph().getVertexCount()==0){
			for (java.awt.Component c : popup.getComponents()) {
				c.setEnabled(false);
			}			
			return;
		}
		NetworkGLVisualizationServer<GeneralNode,GeneralEdge> vv = nv.getNetworkGLVisualizationServer();
		PickedState<GeneralNode> pickedVertexState = vv.getPickedVertexState();
		PickedState<GeneralEdge> pickedEdgeState = vv.getPickedEdgeState();
		
		for (java.awt.Component c : popup.getComponents()) {
			c.setEnabled(true);
			if ((c instanceof PopupMenuItem) && ((PopupMenuItem)c).requiresVertexSelection() == SelectionType.VERTEX) {
				c.setEnabled(pickedVertexState.getPicked().size() > 0);
			}
			if ((c instanceof PopupMenu) && ((PopupMenu)c).requiresVertexSelection() == SelectionType.VERTEX) {
				c.setEnabled(pickedVertexState.getPicked().size() > 0);
			}
			if ((c instanceof PopupMenuItem) && ((PopupMenuItem)c).requiresVertexSelection() == SelectionType.REFERENCE) {
				Set<GeneralNode> nodes = pickedVertexState.getPicked();
				c.setEnabled(false);

				if (!nodes.isEmpty()){
					GeneralNode node = ((GeneralNode)pickedVertexState.getPicked().toArray()[0]);
					if( node instanceof VAERS_Node )
						if (((VAERS_Node)node).getNodeType() == NodeType.REFERENCE){
							c.setEnabled(true);
						}
				}
			}
			if ((c instanceof PopupMenu) && ((PopupMenu)c).requiresVertexSelection() == SelectionType.REFERENCE) {
				Set<GeneralNode> nodes = pickedVertexState.getPicked();
				c.setEnabled(false);

				if (!nodes.isEmpty()){
					GeneralNode node = ((GeneralNode)pickedVertexState.getPicked().toArray()[0]);
					if( node instanceof VAERS_Node )
						if (((VAERS_Node)node).getNodeType() == NodeType.REFERENCE){
							c.setEnabled(true);
						}
				}
			}			
			if ((c instanceof PopupMenuItem) && ((PopupMenuItem)c).requiresVertexSelection() == SelectionType.SINGLE_SYM) {
				c.setEnabled(pickedVertexState.getPicked().size() == 1 && ((VAERS_Node)pickedVertexState.getPicked().toArray()[0]).getNodeType() == NodeType.SYM);
				
			}
			if ((c instanceof PopupMenu) && ((PopupMenu)c).requiresVertexSelection() == SelectionType.SINGLE_SYM) {
				c.setEnabled(pickedVertexState.getPicked().size() == 1 && ((VAERS_Node)pickedVertexState.getPicked().toArray()[0]).getNodeType() == NodeType.SYM);
			}
			if ((c instanceof PopupMenuItem) && ((PopupMenuItem)c).requiresVertexSelection() == SelectionType.EDGE) {
				c.setEnabled(pickedEdgeState.getPicked().size() > 0);
			}
			if ((c instanceof PopupMenu) && ((PopupMenu)c).requiresVertexSelection() == SelectionType.EDGE) {
				c.setEnabled(pickedEdgeState.getPicked().size() > 0);
			}
		}
	}

	@Override
	public int getModifiers() {
		return modifiers;
	}

	@Override
	public void setModifiers(int modifiers) {
		this.modifiers = modifiers;		
	}

	@Override
	public boolean checkModifiers(MouseEvent e) {
		return e.getModifiers() == modifiers;
	}
}
