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

import com.eng.cber.na.NACommandActionListener;
import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.command.RemoveEdgeSubCommand;
import com.eng.cber.na.command.util.CommandButton;
import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.removal.NodeRemovalDialog.Tres;

/**
 * Dialog describing the behavior of the edge removal dialog box
 * (not the edge removal UI -- for that, see removal.EdgeRemovalPanel).
 *
 */
@SuppressWarnings("serial")
public class EdgeRemovalDialog extends JDialog {

	private EdgeRemovalPanel erp;
	private CommandButton rem;
	private static EdgeRemovalDialog dialog;
	private static Map<GeneralGraph,Tres> states = new HashMap<GeneralGraph,Tres>();

	private EdgeRemovalDialog(JFrame main, GeneralGraph graph, int type, int min, int max) {
		this(main,graph);
		erp.setEdgeType(type);
		erp.setMinEdgeWeight(min);
		erp.setMaxEdgeWeight(max);
	}
	
	private EdgeRemovalDialog(JFrame main, GeneralGraph graph) {
		super(main,"Remove Edges by Weight",true);
		setLayout(new BorderLayout());
		erp = new EdgeRemovalPanel(graph);
		add(erp, BorderLayout.CENTER);
		JPanel ctrls = getCtrlPanel();
		add(ctrls, BorderLayout.SOUTH);
		

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
	}

	public static EdgeRemovalDialog showDialog(JFrame main, GeneralGraph graph) {
		if(states.containsKey(graph)) {
			Tres d = states.get(graph);
			dialog = new EdgeRemovalDialog(main, graph, d.metric, d.min, d.max);
		}
		else {
			dialog = new EdgeRemovalDialog(main, graph);			
		}
		dialog.setVisible(true);
		states.put(graph, new Tres(dialog.getEdgeType(), dialog.getMinRank(),dialog.getMaxRank()));
		return dialog;
	}
	
	public int getMinRank() {
		return erp.getMinEdgeWeight();
	}
	
	public int getMaxRank() {
		return erp.getMaxEdgeWeight();
	}
	
	public int getEdgeType() {
		return erp.getEdgeType();
	}
	
	private JPanel getCtrlPanel() {
		JPanel ctrls = new JPanel();
		NACommandActionListener CA = NetworkAnalysisVisualization.getInstance().getCommandActionListener();
		
		rem = new CommandButton("Remove Edges", null, CA);
		rem.setCommand(new RemoveEdgeSubCommand(erp));
		ctrls.add(rem);
		
		JButton can = new JButton("Cancel");
		ctrls.add(can);
		can.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);				
				dispose();
			}
			
		});
		
		getRootPane().setDefaultButton(rem);
		return ctrls;
	}

}
