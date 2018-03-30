package com.eng.cber.na.model;

import javax.swing.table.AbstractTableModel;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.graph.FDAGraph;

/**
 * The HeadAdjacentVertexTableModel is an AbstractTableModel designed
 * specifically for displaying the header that gives vertex
 * information.  This class gives the labels for vertex information
 * that is provided in AdjacentVertexTableModel.
 *
 */
@SuppressWarnings("serial")
public class HeadAdjacentVertexTableModel extends AbstractTableModel {
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
		
		if (NetworkAnalysisVisualization.getInstance().getGraph() instanceof FDAGraph )
		{
			switch(columnIndex) {
			case 0:
				return " Type";
			case 1:
				return " Name";
			case 2:
				return " Betweenness";
			case 3:
				return " Closeness";
			case 4:
				return " Degree";
			case 5:
				return " Strength";
			case 6:
				if (NetworkAnalysisVisualization.getInstance().getGraph().getDual()==0)
					return " Document Count";
				else
					return " Element Count";				
			default:
				return " Strength";
			}
		}
		else
		{
			switch(columnIndex) {
			case 0:
				return " Type";
			case 1:
				return " Name";
			case 2:
				return " Betweenness";
			case 3:
				return " Closeness";
			case 4:
				return " Degree";
			case 5:
				return " Strength";
			default:
				return " Strength";
			}
		}
	}
}
