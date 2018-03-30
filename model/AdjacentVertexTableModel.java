package com.eng.cber.na.model;

import javax.swing.table.AbstractTableModel;

import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.graph.GeneralNode;
import com.eng.cber.na.vaers.VAERS_Node;

/**
 * The AdjacentVertexTableModel is an AbstractTableModel designed
 * specifically for displaying vertex information.
 *
 */
@SuppressWarnings("serial")
public class AdjacentVertexTableModel extends AbstractTableModel {
	
	private GeneralNode node;
	private GeneralGraph g;
	
	public AdjacentVertexTableModel(GeneralGraph g, GeneralNode node) {
		this.g = g;
		this.node = node;
	}
	
	@Override
	public int getRowCount() {
		return 1;
	}
	
	@Override
	public int getColumnCount() {
		return 7;
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch(columnIndex) {
		case 0:
			if(node instanceof VAERS_Node){
				return ((VAERS_Node)node).getNodeType();
			}
			else{
				return VAERS_Node.NodeType.VAX;
			}
					
		case 1:
			return node.getID();
		case 2:
			return !g.areBetweenCloseCalculated() ? (g.isCalculatingBetweenClose() ? "Calculating..." : "Not Calculated") : g.getBetweenness(node);
		case 3:
			return !g.areBetweenCloseCalculated() ? (g.isCalculatingBetweenClose() ? "Calculating..." : "Not Calculated") : g.getCloseness(node);
		case 4:
			return g.getDegree(node);
		case 5:
			return g.getStrength(node);
		case 6:
			if (node instanceof VAERS_Node)
				return new Integer(((VAERS_Node)node).getReports().size());
			else
				return 0;
		default:
			return g.getStrength(node);
		}			
	}
}
