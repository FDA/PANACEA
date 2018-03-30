package com.eng.cber.na.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

import com.eng.cber.na.graph.FDAGraph;
import com.eng.cber.na.layout.NetworkVisualizationModelContainer;

/**
 * A class that knows how to render cells in a tree 
 * containing graphs on the basis of whether
 * an item is selected, whether the tree is expanded,
 * and so on.
 *
 */
public class GraphTreeCellRenderer implements TreeCellRenderer {
	@Override
	public Component getTreeCellRendererComponent(JTree tree,Object value, boolean selected, boolean expanded,boolean leaf, int row, boolean hasFocus) {
		NetworkVisualizationModelContainer vm = (NetworkVisualizationModelContainer)((DefaultMutableTreeNode)value).getUserObject();
		JPanel ret = new JPanel();
		if(vm != null) {
			ret.setLayout(new BoxLayout(ret,BoxLayout.LINE_AXIS));
			ret.setBackground(selected ? Color.LIGHT_GRAY : Color.WHITE);
			if(vm.getGraphLayout() != null) {
				if (vm.getGraphLayout().getGraph() instanceof FDAGraph) {
					FDAGraph fdaGraph = (FDAGraph)(vm.getGraphLayout().getGraph());
					ret.add(new JLabel(" " + fdaGraph.getName() ));
					ret.setToolTipText(fdaGraph.getLineage());
				} 
				else{
					ret.add(new JLabel(" " + vm.getGraphLayout().getGraph().toString()));
				}
			}
		}
		return ret;
	}
}
