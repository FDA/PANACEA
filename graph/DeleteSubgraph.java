package com.eng.cber.na.graph;

import java.util.Arrays;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.layout.NetworkVisualizationModelContainer;
import com.eng.cber.na.model.GraphTreeModel;

/**
 * This class deletes a network (and knows how to
 * remove the network from the UI).
 *
 */
public class DeleteSubgraph implements Runnable {

	public DeleteSubgraph() {
	}

	@Override
	public void run() {
		JTree tree = NetworkAnalysisVisualization.getInstance().getNetworkTree();
		GraphTreeModel model = (GraphTreeModel)tree.getModel();
		TreePath path = tree.getSelectionPath();
		if(path != null) {
			Object[] treePath = path.getPath();
			DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)treePath[treePath.length-1];
			if(!treeNode.equals(model.getNetworkRoot())) {
				NetworkVisualizationModelContainer vm = (NetworkVisualizationModelContainer)treeNode.getUserObject();
				model.removeNetwork(vm);
				for(int i = 0; i < tree.getRowCount(); i++) {
					tree.expandRow(i);
				}
				treePath = Arrays.copyOf(treePath, treePath.length - 1);
				tree.setSelectionPath(new TreePath(treePath));

				NetworkAnalysisVisualization.logger.logp(java.util.logging.Level.INFO,"","","Deleted " + vm.getGraphLayout().getGraph());
			}
		}
	}

}