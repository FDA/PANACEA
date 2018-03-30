package com.eng.cber.na.sim.gui.signal;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JOptionPane;
import javax.swing.JTable;

/** 
 * Callback for Add and Remove Rank Probability events.
 * 
 */

public class RankProbActionListener implements ActionListener {

	private com.eng.cber.na.sim.gui.signal.SignalDialog.SignalPanel signalPanel;
	
	public RankProbActionListener(SignalDialog.SignalPanel signalPanel) {
		this.signalPanel = signalPanel;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		JTable table = signalPanel.getRankProbTable();
		RankProbTableModel tableModel = (RankProbTableModel)table.getModel();
		if(e.getActionCommand().equals("Add")) {
			double newRank = -1, newProb = -1;
			try {
				newRank = signalPanel.getNewRank();
				newProb = signalPanel.getNewProb();
			}
			catch(NumberFormatException nfe) {
				JOptionPane.showMessageDialog(signalPanel, "Please make sure there are valid numbers entered.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			tableModel.addPair(newRank, newProb);
			signalPanel.clearTableRankProbFields();
		}
		else if(e.getActionCommand().equals("Remove")) {
			int[] selected = table.getSelectedRows();
			Arrays.sort(selected);
			for(int i = selected.length - 1; i >= 0; i--) {
				tableModel.removePair(selected[i]);
			}
		}
	}
}
