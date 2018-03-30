package com.eng.cber.na.dialog;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.NetworkAnalysisVisualization.WeightingScheme;
import com.eng.cber.na.component.SyntheticListPanel;
import com.eng.cber.na.graph.BuildSimilarReportGraph;
import com.eng.cber.na.vaers.VAERS_Node;
import com.eng.cber.na.weighting.CalculateLinSimilarity;
import com.eng.cber.na.weighting.Weighting;

/**
 * A dialog that allows a user choose specific terms to include
 * in a reference report and then retrieve all the reports in
 * the current network that are similar to that reference.
 * Reports are similar if they share at least one term in
 * common with the reference.
 */
@SuppressWarnings("serial")
public class SyntheticCaseRetrievalDialog extends JDialog {
	private static SyntheticCaseRetrievalDialog dialog;
	private SyntheticListPanel syntheticListPanel;
	private boolean exitedWithSelectButton;
	
	private SyntheticCaseBuilderPanel caseBuilderPanel;
	
	public SyntheticCaseRetrievalDialog(JFrame main) {
		super(main, "Synthetic Document Builder", false);
		syntheticListPanel = new SyntheticListPanel();
		caseBuilderPanel = new SyntheticCaseBuilderPanel();
		add(caseBuilderPanel);
		pack();
		setLocationRelativeTo(main);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		dialog = this;
		setVisible(true);
	}
	public static SyntheticCaseRetrievalDialog getInstance() {
		return dialog;
	}

	public static SyntheticCaseRetrievalDialog showDialog(JFrame main) {
		dialog = new SyntheticCaseRetrievalDialog(main);
		
		dialog.setExitedWithSelectButton(false);
		dialog.setVisible(true);
		return dialog;
	}

	private void setExitedWithSelectButton(boolean exitedWithSelectButton) {
		this.exitedWithSelectButton = exitedWithSelectButton;
	}

	public boolean exitedWithSelectButton() {
		return exitedWithSelectButton;
	}
	
	
	/******************************************/
	
	
	public class SyntheticCaseBuilderPanel extends JPanel {

		private JLabel introLabel;
		private JButton cancelButton;
		private JButton selectButton;

		public SyntheticCaseBuilderPanel() {
			initComponents();
		}

		private void initComponents() {

			// Prepare Basic Elements - Text and Buttons
			// The list and its text box have their own panel
			introLabel = new JLabel();
			cancelButton = new JButton();
			selectButton = new JButton("Retrieve Similar Cases");
			selectButton.addActionListener(new selectButtonActionPerformed());
			
			introLabel.setText("<html><font size =\"4\" > Pick the terms to be used as the reference.  Select a term in the left list and click the Add button to place it into the reference.</font> </html>");

			cancelButton.setText("Cancel");
			cancelButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					cancelButtonActionPerformed(evt);
				}
			});
			
			// Layout
			GroupLayout layout = new GroupLayout(this);
			this.setLayout(layout);
			layout.setAutoCreateGaps(true);
			layout.setAutoCreateContainerGaps(true);

			layout.setHorizontalGroup(layout
					.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(introLabel)
					.addComponent(syntheticListPanel)
					.addGroup(
							layout.createSequentialGroup()
									.addComponent(cancelButton)
									.addComponent(selectButton)
									.addContainerGap()));
			layout.setVerticalGroup(layout
					.createSequentialGroup()
					.addComponent(introLabel)
					.addComponent(syntheticListPanel)
					.addGroup(
							layout.createParallelGroup()
									.addComponent(cancelButton)
									.addComponent(selectButton)
							)
					);
		}

		private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
			setExitedWithSelectButton(false);
			setVisible(false);
			dispose();
		}

		public class selectButtonActionPerformed implements ActionListener	{
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					ArrayList<String> ptList = syntheticListPanel.getPtList();
					if (ptList.isEmpty()) {
						JOptionPane.showMessageDialog(null,
								"Empty List",
								"Input Error", JOptionPane.ERROR_MESSAGE);
					} else {
						NetworkAnalysisVisualization nv = NetworkAnalysisVisualization
								.getInstance();
						if (!nv.getDualState()){
							JOptionPane.showMessageDialog(null,"Not a report network (SYM or VAX).", "Network Type Error", JOptionPane.ERROR_MESSAGE);
							return;
						}
						if (nv.getGraph().findNodeByID("ReferenceDocument") != null) {
							JOptionPane.showMessageDialog(nv, "Cannot do a second similarity.  Current version does not support using multiple ReferenceDocuments.");
							return;
						}
						
						dialog.setVisible(false);
						nv.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						nv.setWeightingScheme(WeightingScheme.LinSimilarity);
						Weighting weighting;
						if (nv.getGraph().getSimilarity()== null){
							Map<Object, Set<VAERS_Node>> report_hash;
							report_hash = NetworkAnalysisVisualization.getInstance().getUnderlyingData().getOrigReportHash();
							weighting = new Weighting(nv.getGraph(), WeightingScheme.LinSimilarity, "SYM");
							weighting.getLinSimWeight(report_hash);
							nv.getGraph().setSimilarity(weighting);
						}
						else
							weighting = nv.getGraph().getSimilarity();
						
						CalculateLinSimilarity similarNet = new CalculateLinSimilarity(ptList, weighting.getInfoForNode(), weighting.getInfoForReport(), 0.0);
						similarNet.run();
						if (similarNet.getSelWeightMapping().size() == 1 && similarNet.getSelWeightMapping().keySet().contains("ReferenceDocument")) {
							JOptionPane.showMessageDialog(nv, "No reports share any common terms with this reference list.");
							dialog.setVisible(true);
							nv.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
							return;
						}
						BuildSimilarReportGraph newGraph = new BuildSimilarReportGraph(nv.getGraph(), WeightingScheme.LinSimilarity, 
								similarNet.getSelWeightMapping(), ptList);
						SwingUtilities.invokeLater( newGraph );
						newGraph.setGraphName(nv.getGraph().getName() + "_SimilarityTo" + syntheticListPanel.getCaseName());
					}
				} catch (IllegalArgumentException x) {
					JOptionPane.showMessageDialog(null, x.getMessage(),
							"Input Error", JOptionPane.ERROR_MESSAGE);
					setExitedWithSelectButton(false);
				}
			}
		}
	}
}
