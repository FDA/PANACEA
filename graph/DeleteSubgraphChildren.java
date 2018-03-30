package com.eng.cber.na.graph;

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
public class DeleteSubgraphChildren implements Runnable {

	public DeleteSubgraphChildren() {
	}

	@Override
	public void run() {
		JTree tree = NetworkAnalysisVisualization.getInstance().getNetworkTree();
		GraphTreeModel model = (GraphTreeModel)tree.getModel();
		TreePath path = tree.getSelectionPath();
		if(path != null) {
			Object[] treePath = path.getPath();
			DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)treePath[treePath.length-1];
			int childCounts = model.getChildCount(tree.getLastSelectedPathComponent());
			for(int i = 0; i< childCounts; i++ ) //Determine if a dual has been generated
			{
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) (model.getChild(treeNode, 0));
				NetworkVisualizationModelContainer vm = (NetworkVisualizationModelContainer) (node.getUserObject());
				model.removeNetwork(vm);
				NetworkAnalysisVisualization.logger.logp(java.util.logging.Level.INFO,"","","Deleted " + vm.getGraphLayout().getGraph());
			}
			tree.setSelectionPath(path);
		}
	}

}