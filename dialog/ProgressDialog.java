package com.eng.cber.na.dialog;

import java.awt.Dialog;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker.StateValue;
import javax.swing.WindowConstants;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.concurrent.ConcurrentJob.JobStateValue;
import com.eng.cber.na.concurrent.cmm.CMMConcurrentJob;
import com.eng.cber.na.graph.GraphIslandCalculator;

/** 
 * A small, non-interrupting dialog to show a progress bar
 * for a background task.  To use, create a new job and
 * register this dialog as a PropertyChangeListener.
 */
@SuppressWarnings("serial")
public class ProgressDialog extends JDialog implements PropertyChangeListener {

	private JProgressBar progressBar;
	private JLabel descriptionLabel;
	private PropertyChangeListener listenerToForwardTo;
	
	
	/** Generic constructor with a default title and description. */
	public ProgressDialog() {
		this("Calculation In Progress", "Calculating...");
	}
	
	public ProgressDialog(String title, String description) {
		this(NetworkAnalysisVisualization.getInstance(), title, description, true, null);
	}
	
	public ProgressDialog(Window windowRelativeTo, String title, String description, boolean displayContinueUsingMessage, PropertyChangeListener listenerToForwardTo) {
		super(windowRelativeTo, title, Dialog.ModalityType.MODELESS);

		progressBar = new JProgressBar();
		progressBar.setValue(0);
		descriptionLabel = new JLabel(description);
		this.listenerToForwardTo = listenerToForwardTo;
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.PAGE_AXIS));
		panel.add(descriptionLabel);
		panel.add(progressBar);
		if (displayContinueUsingMessage)
			panel.add(new JLabel("You may continue to use the system during this time."));
		add(panel);
		
		pack();
		setLocationRelativeTo(NetworkAnalysisVisualization.getInstance());
		setResizable(false);
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		
		setFocusableWindowState(false);
		
		setVisible(true);
	}
	
	public void setDescription(String description) {
		descriptionLabel.setText(description);
	}
	
	public void setListenerToForwardTo(PropertyChangeListener listenerToForwardTo) {
		this.listenerToForwardTo = listenerToForwardTo;
	}
	
	public PropertyChangeListener getListenerToForwardTo() {
		return listenerToForwardTo;
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		// Update progress bar
		if (e.getPropertyName().equals("progress")) {
			progressBar.setValue((Integer) e.getNewValue());
		}
		// Check if calculation is finished.  Note that SwingWorker and ConcurrentJob have different DONE state values.
		else if (e.getPropertyName().equals("state") && (e.getNewValue().equals(StateValue.DONE) || e.getNewValue().equals(JobStateValue.DONE))) {
			NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
			if (e.getSource() instanceof GraphIslandCalculator) {
				GraphIslandCalculator finishedJob = (GraphIslandCalculator) e.getSource();
				NetworkAnalysisVisualization.logger.logp(java.util.logging.Level.INFO,"","","Finished Calculating Islands for " + finishedJob.getGraph().getName());				
				finishedJob.getGraph().setCalculatingIslands(false);
				finishedJob.getGraph().setIslandsAreCalculated(true);
			}
			else if (e.getSource() instanceof CMMConcurrentJob) {
				CMMConcurrentJob finishedJob = (CMMConcurrentJob) e.getSource();
				NetworkAnalysisVisualization.logger.logp(java.util.logging.Level.INFO,"","","Finished Betweenness/Closeness Calculation for " + finishedJob.getGraph().getName());				
				finishedJob.getGraph().setCalculatingBetweenClose(false);
				finishedJob.getGraph().setBetweenCloseAreCalculated(true);
				nv.updateAfterNodeSelection();
			}
			// Always destroy the progress dialog when that task is done.
			dispose();
		}
		if (listenerToForwardTo != null)
			listenerToForwardTo.propertyChange(e);
		
	}
}
