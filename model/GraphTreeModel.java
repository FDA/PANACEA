package com.eng.cber.na.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.layout.NetworkVisualizationModelContainer;

/**
 * The GraphTreeModel keeps track of the data that goes into 
 * maintaining a tree of all the graph's lineages for the
 * graphs that have been created in this session of PANACEA.
 * It allows classes to change the root of the graph lineage
 * tree, as well as to add new networks at a root level 
 * to that tree, to add networks as leaves, and to delete 
 * networks from the tree representation.
 *
 */
@SuppressWarnings("serial")
public class GraphTreeModel extends DefaultTreeModel 
	implements Serializable {

	private transient DefaultMutableTreeNode root;
	
	public GraphTreeModel(NetworkVisualizationModelContainer vm) {
		super(null);
		setNetworkRoot(vm);
	}
	
	public DefaultMutableTreeNode getNetworkRoot() {
		return root;
	}
	
	public void setNetworkRoot(NetworkVisualizationModelContainer vm) {
		root = new DefaultMutableTreeNode(vm);
		setRoot(root);
	}
	
	public TreeNode[] addNetwork(NetworkVisualizationModelContainer parent, NetworkVisualizationModelContainer newModel) {
		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newModel);
		DefaultMutableTreeNode parentNode = getTreeNode(parent,root);
		if(parentNode != null) {
			parentNode.add(newNode);
		}
		reload();
		NetworkAnalysisVisualization.getInstance().getNetworkTree().makeVisible(new TreePath(newNode.getPath()));
		return newNode.getPath();
	}
	
	public TreeNode[] addNetworkToRoot(NetworkVisualizationModelContainer newModel) {
		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newModel);
		DefaultMutableTreeNode parentNode = root;
		if(parentNode != null) {
			parentNode.add(newNode);
		}
		reload();
		
		return newNode.getPath();
	}

	public void removeNetwork(NetworkVisualizationModelContainer removeModel) {
		DefaultMutableTreeNode removeNode = getTreeNode(removeModel,root);
		if(removeNode != null && removeNode != root) {
			DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)removeNode.getParent();
			parentNode.remove(removeNode);
		}
		reload();
	}
	
	@Override
	public void valueForPathChanged(TreePath path, Object newValue){
		Object obj = ((DefaultMutableTreeNode)path.getLastPathComponent()).getUserObject();
		((GeneralGraph)((NetworkVisualizationModelContainer)obj).getGraphLayout().getGraph()).setName(newValue.toString());
		super.valueForPathChanged(path, obj);
	}
	
	private DefaultMutableTreeNode getTreeNode(NetworkVisualizationModelContainer vm, DefaultMutableTreeNode root) {
		if(!root.getUserObject().equals(vm)) {
			Enumeration<DefaultMutableTreeNode> e = root.children();
			while(e.hasMoreElements()) {
				DefaultMutableTreeNode node = getTreeNode(vm,e.nextElement());	
				if(node != null)
					return node;
			}
			return null;
		}
		return root;
	}
	
	private void writeObject(ObjectOutputStream stream) throws IOException{
		stream.defaultWriteObject();
	}
	
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		stream.defaultReadObject();
	}
}