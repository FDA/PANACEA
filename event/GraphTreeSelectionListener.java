package com.eng.cber.na.event;

import java.util.HashSet;
import java.util.Set;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.layout.NetworkVisualizationModelContainer;

/**
 * This class handles PANACEA's behavior when users select an existing graph
 * to display from the set of graphs that have previously been created
 * (user is interacting with the Network View panel of the screen).
 *
 */
public class GraphTreeSelectionListener implements TreeSelectionListener {
	private Set<NetworkVisualizationModelContainer> alreadyChecked = new HashSet<NetworkVisualizationModelContainer>();
	
	public GraphTreeSelectionListener(NetworkVisualizationModelContainer initial_vm) {
		alreadyChecked.add(initial_vm);
	}
	
	@Override
	public void valueChanged(TreeSelectionEvent e) {
		JTree tree = (JTree)e.getSource();
		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
		
		TreePath path = tree.getSelectionPath();
		if(path != null) {
			Object[] treePath = path.getPath();
			
			NetworkAnalysisVisualization.NALog("Select tree node.");
			NetworkVisualizationModelContainer vm = (NetworkVisualizationModelContainer)((DefaultMutableTreeNode)treePath[treePath.length-1]).getUserObject();
			GeneralGraph gg = ((GeneralGraph) vm.getGraphLayout().getGraph());
			int networkType = gg.getDual();
			nv.setDualID(networkType);
			nv.SetNetworkViewTypeLabel(gg.GetNetworkTypeString(networkType));
			nv.setVisualizationModelAndReload(vm);	
		}
	}
}
