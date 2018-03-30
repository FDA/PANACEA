package com.eng.cber.na.sim.gui.signal;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

import com.eng.cber.na.sim.NetworkSimulatorGUI;
import com.eng.cber.na.sim.gui.signal.RankProbTableModel.RankProbSortOrder;

/**
 * Callback of sorting the Rank/Probability table.
 * 
 */

public class RankProbHeadMouseListener extends MouseAdapter {
	
	private int curDirection = 1;
	private int curColIndex = 0;
	private JTable table;
	
	public RankProbHeadMouseListener(JTable rankProbTable) {
		this.table = rankProbTable;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		TableColumnModel tcm = table.getColumnModel();
		int colIndex = tcm.getColumnIndexAtX(e.getX());
		
		RankProbTableModel tableModel = (RankProbTableModel)table.getModel();
		
		if(colIndex == curColIndex) {
			curDirection = (curDirection + 1) % 2;
		}
		else {
			curDirection = 1;
			curColIndex = (curColIndex + 1) % 2;
		}
		if(curColIndex == 0) {
			if(curDirection == 1) {
				tableModel.changeOrder(RankProbSortOrder.RANK_FORWARD);
			}
			else {
				tableModel.changeOrder(RankProbSortOrder.RANK_BACKWARD);
			}
		}
		else {
			if(curDirection == 1) {
				tableModel.changeOrder(RankProbSortOrder.PROB_FORWARD);
			}
			else {
				tableModel.changeOrder(RankProbSortOrder.PROB_BACKWARD);
			}
		}
	}
}
