package com.eng.cber.na.command;

import java.awt.Cursor;

import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.command.util.BaseCommand;
import com.eng.cber.na.graph.BuildGraphFromReportSet;
import com.eng.cber.na.graph.FDAGraph;
import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.layout.NetworkVisualizationModelContainer;
import com.eng.cber.na.model.GraphTreeModel;

/**
 * @author Guangfan (Geoffrey) Zhang
 * 
 * The command pattern design to switch between element-based and
 * report-based networks, which is mostly designed for VAERS type of graph.
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */
public class SwitchNetwork extends BaseCommand{
	
	/* 
	 * Perform switching. 
	 * Supports switching among element-based networks and 
	 * two types of report-base networks
	*/ 

	@Override
	public void execute(String name) {
		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();

		if (!(nv.getGraph() instanceof FDAGraph)){
			JOptionPane.showMessageDialog(NetworkAnalysisVisualization.getInstance(), "Dual of general graphs not implemented yet", "Warning", JOptionPane.WARNING_MESSAGE);
			return;
		}		
		GeneralGraph gg =nv.getGraph();
		int networkType = 0;
		if (gg.isFDAType()){
			JPanel edgeTypePanel = new JPanel();
			JRadioButton optnOrig = new JRadioButton("Element Network");
			JRadioButton optnVAX = new JRadioButton("Report Network (VAX)");
			JRadioButton optnPT = new JRadioButton("Report Network (SYM)");
			switch (nv.getGraph().getDual()){
			case 0: 
				optnOrig.setSelected(true);
				break;
			case 1: 
				optnVAX.setSelected(true);
				break;
			case 2:
				optnPT.setSelected(true);
				break;
			default:
				optnOrig.setSelected(true);
			}
			ButtonGroup bg_EdgeType = new ButtonGroup();
			bg_EdgeType.add(optnOrig);
			bg_EdgeType.add(optnVAX);
			bg_EdgeType.add(optnPT);
			edgeTypePanel.add(optnOrig);
			edgeTypePanel.add(optnVAX);
			edgeTypePanel.add(optnPT);
			
			optnVAX.setRequestFocusEnabled(true);
			optnVAX.requestFocus();
			
			int result = JOptionPane.showConfirmDialog(null, edgeTypePanel, "Type", JOptionPane.OK_CANCEL_OPTION);
			if (result == JOptionPane.OK_OPTION){
				if (optnOrig.isSelected())
					networkType = 0; 
				if (optnVAX.isSelected())
					networkType= 1; 
				if (optnPT.isSelected())
					networkType = 2; 
			}
			
			else
				return;
		}
		if (nv.getGraph().getDual()==networkType)
			return;
		JTree tree = nv.getNetworkTree();
		TreePath path = tree.getSelectionPath();
		Object[] treePath = path.getPath();
		
		NetworkVisualizationModelContainer vm = 
				(NetworkVisualizationModelContainer)((DefaultMutableTreeNode)treePath[treePath.length-1]).getUserObject();
		
		if (networkType> 0)
			nv.setDualNetwork(true);
		else
			nv.setDualNetwork(false);
		
		nv.setDualID(networkType);
		
		
		if (vm.getModelExist(vm.getGraphLayout(), networkType)){ //If the graph (layout) has been generated 
			vm.setNetworkModel(vm.getGraphLayout(), networkType);
			nv.setVisualizationModelAndReload(vm);
			((GraphTreeModel)tree.getModel()).reload();
			tree.setSelectionPath(path);
			return;
		}
		else{//Graph has not been generated
			try{
				nv.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

				if (nv.getUnderlyingData().getNodeHash().size() == 0){ //Create dual network (report-based) data if necessary
					nv.getUnderlyingData().generateDual();
				}
				
				int parentPath = tree.getRowForPath(path);
				int rowForParent = 0;
				if (parentPath == 0)
					rowForParent = -1;
				else
					rowForParent = parentPath;
					
				SwingUtilities.invokeLater(new BuildGraphFromReportSet(rowForParent, false));
				nv.getHead().updateUI();
			}
			finally{
			}
		}		
	}

	@Override
	public Boolean recordable() {
		return true;
	}
}
