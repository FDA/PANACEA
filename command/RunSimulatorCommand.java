package com.eng.cber.na.command;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.command.util.BaseCommand;
import com.eng.cber.na.graph.FDAGraph;
import com.eng.cber.na.sim.NetworkSimulatorGUI;

/****
 * The command pattern design to start the network formation simulator.
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */

public class RunSimulatorCommand extends BaseCommand{
	@Override
	public void execute(String name) {
		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
		if (!(nv.getGraph() instanceof FDAGraph)) {
			JOptionPane.showMessageDialog(nv, "Cannot do this with a general network.");
			return;
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				}
				catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				catch (InstantiationException e) {
					e.printStackTrace();
				}
				catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				catch (UnsupportedLookAndFeelException e) {
					e.printStackTrace();
				}
				NetworkSimulatorGUI.getInstance();
				NetworkSimulatorGUI.getInstance().fireRefresh(true);
			}
		});
	}

	@Override
	public Boolean recordable() {
		return true;
	}

}
