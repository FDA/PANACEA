package com.eng.cber.na.dialog;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.concurrent.ConcurrentJob;
import com.eng.cber.na.concurrent.ConcurrentJob.JobStateValue;

/**
 * A dialog that shows the current state of a process
 * in a progress bar and provides an Abort button.
 *
 */
@SuppressWarnings("serial")
public class ProcessingDialog extends JDialog implements ActionListener, PropertyChangeListener {
	
	private JProgressBar progressBar;
	private ConcurrentJob job;
	
	public ProcessingDialog(ConcurrentJob job) {
		super(NetworkAnalysisVisualization.getInstance(),"Processing...");
		
		this.job = job;
		progressBar = new JProgressBar();
		
		JButton abort = new JButton("Abort");
		abort.addActionListener(this);
		
		JPanel p = new JPanel();
		p.setLayout(new FlowLayout());
		p.add(progressBar);
		p.add(abort);
		add(p);
		
		pack();
		setLocationRelativeTo(NetworkAnalysisVisualization.getInstance());
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setModal(true);
		setResizable(false);
		setProgress(0);
	}
	
	public void setProgress(int progress) {
		progressBar.setValue(progress);
	}
		
	public void showDialog() {
		setVisible(true);
	}
	
	public void destroyDialog() {
		setVisible(false);
		dispose();		
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		job.cancel();
		destroyDialog();
		
		NetworkAnalysisVisualization instance = NetworkAnalysisVisualization.getInstance();
		if (!instance.isVisible()) {
			instance.dispatchEvent(new WindowEvent(instance,WindowEvent.WINDOW_CLOSING));
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String property = evt.getPropertyName();
		Object value = evt.getNewValue();
		
		if((property.equals("state") && value.equals(JobStateValue.DONE)) || property.equals("interrupted")) {
			destroyDialog();
		}
		else if(property.equals("progress")) {
			setProgress((Integer)value);
		}
	}

}
