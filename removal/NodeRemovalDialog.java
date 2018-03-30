package com.eng.cber.na.removal;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.subgraph.CreateBetweennessExcludedRangeSubgraph;
import com.eng.cber.na.subgraph.CreateBetweennessSubgraph;
import com.eng.cber.na.subgraph.CreateClosenessExcludedRangeSubgraph;
import com.eng.cber.na.subgraph.CreateClosenessSubgraph;
import com.eng.cber.na.subgraph.CreateRawDegreeExcludedRangeSubgraph;
import com.eng.cber.na.subgraph.CreateRawDegreeSubgraph;
import com.eng.cber.na.subgraph.CreateStrengthExcludedRangeSubgraph;
import com.eng.cber.na.subgraph.CreateStrengthSubgraph;

/**
 * Dialog describing the behavior of the node removal dialog box
 * (but this is not the node removal UI -- for that, see
 * removal.NodeRemovalPanel).
 *
 */
@SuppressWarnings("serial")
public class NodeRemovalDialog extends JDialog implements ActionListener {

	private NodeRemovalPanel nrp;
	private static NodeRemovalDialog dialog;
	private static Map<GeneralGraph,Tres> states = new HashMap<GeneralGraph,Tres>();
	private JButton rem; 

	public JButton getRemoveButton() {
		return rem;
	}

	private NodeRemovalDialog(JFrame main, GeneralGraph graph, int metric, int min, int max) {
		this(main,graph);
		nrp.setMetrix(metric);
		nrp.setMinMetricRank(min);
		nrp.setMaxMetricRank(max);
	}
	
	private NodeRemovalDialog(JFrame main, GeneralGraph graph) {
		super(main,"Remove Nodes",true);
		setLayout(new BorderLayout());
		nrp = new NodeRemovalPanel(graph);
		add(nrp, BorderLayout.CENTER);
		JPanel ctrls = getCtrlPanel();
		add(ctrls, BorderLayout.SOUTH);
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
	}
	
	public static NodeRemovalDialog showDialog(JFrame main, GeneralGraph graph) {
		if(states.containsKey(graph)) {
			Tres d = states.get(graph);
			dialog = new NodeRemovalDialog(main, graph, d.metric, d.min, d.max);
		}
		else {
			dialog = new NodeRemovalDialog(main, graph);			
		}
		dialog.setVisible(true);
		states.put(graph, new Tres(dialog.getMetric(),dialog.getMinRank(),dialog.getMaxRank()));
		return dialog;
	}
	
	public int getMinRank() {
		return nrp.getMinMetricRank();
	}
	
	public int getMaxRank() {
		return nrp.getMaxMetricRank();
	}
	
	public int getMetric() {
		return nrp.getMetric();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("Remove Nodes")) {
			double maxMetricValue = nrp.getMaxMetricValue();
			double minMetricValue = nrp.getMinMetricValue();
			
			switch(nrp.getMetric()) {
				// Order is reversed (max then min) because the max/min monikers refer 
				// to the max and min ranks, rather than to which of the values themselves.
				case 0: // Degree
					if (maxMetricValue < minMetricValue)
						SwingUtilities.invokeLater(new CreateRawDegreeSubgraph(maxMetricValue, minMetricValue));
					else
						SwingUtilities.invokeLater(new CreateRawDegreeExcludedRangeSubgraph(minMetricValue, maxMetricValue));
					break;
				case 1: // Betweenness
					if (maxMetricValue < minMetricValue)
						SwingUtilities.invokeLater(new CreateBetweennessSubgraph(maxMetricValue, minMetricValue));
					else
						SwingUtilities.invokeLater(new CreateBetweennessExcludedRangeSubgraph(minMetricValue, maxMetricValue));
					break;
				case 2: // Closeness
					if (maxMetricValue < minMetricValue)
						SwingUtilities.invokeLater(new CreateClosenessSubgraph(maxMetricValue, minMetricValue));
					else
						SwingUtilities.invokeLater(new CreateClosenessExcludedRangeSubgraph(minMetricValue, maxMetricValue));
					break;
				case 3: // Strength
					if (maxMetricValue < minMetricValue)
						SwingUtilities.invokeLater(new CreateStrengthSubgraph(maxMetricValue, minMetricValue));
					else
						SwingUtilities.invokeLater(new CreateStrengthExcludedRangeSubgraph(minMetricValue, maxMetricValue));
					break;
				default:
					throw new IllegalArgumentException("Metric option #" + nrp.getMetric()+1 + " currently has no associated action.");
			}
		}
		setVisible(false);
		dispose();	
	}
	
	private JPanel getCtrlPanel() {
		JPanel ctrls = new JPanel();
		
		rem = new JButton("Remove Nodes");
		ctrls.add(rem);
		rem.addActionListener(this);
		
		JButton can = new JButton("Cancel");
		ctrls.add(can);
		can.addActionListener(this);
		
		getRootPane().setDefaultButton(rem);
		return ctrls;
	}
	
	static class Tres {
		public int metric;
		public int min;
		public int max;
		public Tres(int metric, int min, int max) {
			this.metric = metric;
			this.min = min;
			this.max = max;
		}
	}


}
