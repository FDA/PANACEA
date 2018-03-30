package com.eng.cber.na.graph;

import java.awt.Cursor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.gl.NetworkGLVisualizationServer;
import com.eng.cber.na.layout.Layout;
import com.eng.cber.na.layout.NetworkVisualizationModelContainer;
import com.eng.cber.na.model.GraphTreeModel;

import edu.uci.ics.jung.graph.Graph;

/**
 * This class is an abstract version of creating a graph.
 * Other classes inherit from it, implementing their approaches
 * to creating networks.  All subclasses have a run()
 * command, which creates the network and adds it to the
 * user interface.
 * Parameters:
 * rowForParentPath: -1: indicate a base graph;
 * rowForParentPath: -2: indicate a non-base graph;
 * rowForParentPath: >=0: indicate a non-base graph and its parent tree path;
 *
 */
public abstract class AbstractCreateGraph implements Runnable {
	protected GeneralGraph parent;
	protected boolean isDual = false;
	protected static int dataType;
	protected boolean isNew = true;
	protected int rowForParentPath = -1;
	
	/** Graphs with more than this many edges will not do island and betweenness/closeness calculations by default */
	protected static int EDGES_THRESHOLD = 10000; 
	
	public AbstractCreateGraph(int rowForParentPath) {
		this(rowForParentPath, true);
	}

	public AbstractCreateGraph(int rowForParentPath, boolean isNew ) {
		parent = NetworkAnalysisVisualization.getInstance().getGraph();
		this.isNew = isNew;
		this.rowForParentPath = rowForParentPath;
	}
	
	@Override
	public void run() {
		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
		final NetworkGLVisualizationServer<GeneralNode, GeneralEdge> vv = nv.getNetworkGLVisualizationServer();
		final JTree tree = nv.getNetworkTree();
		final GraphTreeModel model = (GraphTreeModel)tree.getModel();
		
		final TreePath path = tree.getSelectionPath();
		if(path != null || (path == null && rowForParentPath >= 0 && isNew)) {
			final Object[] treePath;
			final NetworkVisualizationModelContainer vm;
			if (rowForParentPath < 0 )
				treePath = path.getPath();
			else
				treePath = tree.getPathForRow(rowForParentPath).getPath();

			vm = 
					(NetworkVisualizationModelContainer)((DefaultMutableTreeNode)treePath[treePath.length-1]).getUserObject();
			
			try {
				dataType = nv.getDualID();
				final GeneralGraph newGraph = getFinalNetwork();
				if (newGraph.getVertexCount() == 0 ){
					JOptionPane.showMessageDialog(null,  "The new graph is impossible.  It has no nodes.");
					NetworkAnalysisVisualization.NALog("WARNING","No Nodes Found ");
					nv.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					return;
				}
				
				newGraph.setFDAType(parent.isFDAType());
				
				if (parent.getDual()>0)
					newGraph.setLineage("derived from " + parent.toString() + ", " + parent.getLineage() + "(" + parent.GetNetworkTypeString(parent.getDual()).substring(7) + ")" );					
				else
					newGraph.setLineage("derived from " + parent.toString() + ", " + parent.getLineage());
				
				NetworkAnalysisVisualization.logger.logp(java.util.logging.Level.INFO,"","","Created "  + newGraph.getName() + " " + newGraph.getLineage());
				
				// Calculate the first round of data for the graph
				// e.g. Degree, Strength, Edge Weight, Islands
				BasicGraphDataCalculator graphCalc = new BasicGraphDataCalculator(newGraph);
				graphCalc.identifyComponents();
				graphCalc.identifyMinAndMaxForVertices();
				graphCalc.identifyMinAndMaxForEdges();
				graphCalc.setAllDisplaysToTrue();
				
				// If graph has vertices but is not too large, start the islands
				// and betweenness/closeness calculations by default.
				// NOTE: Both will run on background threads
				if (newGraph.getVertexCount() > 0) {
					if (newGraph.getEdgeCount() < EDGES_THRESHOLD) {
						newGraph.calculateBetweenClose();
						newGraph.calculateIslands();
					}
				}

				// Calculate/set dual-network information
				int graphType = NetworkAnalysisVisualization.getInstance().getDualID();
				//Determine layout type
				Class<?> layoutClass = vm.getGraphLayout().getClass();
				Constructor<?> constructor;
				Layout<GeneralNode, GeneralEdge> layout = null;
				try {
					constructor = layoutClass.getConstructor(Graph.class);
					layout = (Layout<GeneralNode, GeneralEdge>)constructor.newInstance(newGraph);
				} catch (NoSuchMethodException e1) {
					e1.printStackTrace();
				} catch (SecurityException e1) {
					e1.printStackTrace();
				} catch (InstantiationException e1) {
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					e1.printStackTrace();
				} catch (IllegalArgumentException e1) {
					e1.printStackTrace();
				} catch (InvocationTargetException e1) {
					e1.printStackTrace();
				}							
				
				if (isNew){
					NetworkVisualizationModelContainer nvm = new NetworkVisualizationModelContainer(layout,vv.getSize(), graphType);	
					TreeNode[] newPath;
					if (rowForParentPath == -1 ) {
						newPath = model.addNetworkToRoot(nvm); 
					}
					else {
						newPath = model.addNetwork(vm, nvm);
						//The network type is the same as its parent
						dataType = vm.getCurrentModel().getModel().getDual();
					}

					tree.setSelectionPath(new TreePath(newPath));
				}
				else{

					vm.setNetworkLayout( layout, vv.getSize(), graphType);

					NetworkAnalysisVisualization.getInstance().setVisualizationModelAndReload(vm);
					
					model.reload();
					NetworkAnalysisVisualization.getInstance().getNetworkTree().setSelectionPath(path);
				}
				newGraph.setLayoutType(layout.getType());
				tree.setEditable(true);
				updateGraphCharacteristics((GeneralGraph)newGraph, (GeneralGraph)parent);
				NetworkAnalysisVisualization.getInstance().setCursor(Cursor.getDefaultCursor());
				

			} catch (IllegalArgumentException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			} catch (InstantiationException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		}
	}
	
	
	protected static void ensureFromAndTo(Number from, Number to, boolean rangeLessThanOne) throws IllegalArgumentException {
		if (from.doubleValue() < 0.) {
			throw new IllegalArgumentException("Cannot calculate range with a negative 'from' value");
		}
		if (from.doubleValue() > to.doubleValue()) {
			throw new IllegalArgumentException("Range 'from' value should be less than or equal to the 'to' value");
		}
		if (rangeLessThanOne && to.doubleValue() > 1) {
			throw new IllegalArgumentException("Cannot calculate range with a 'to' value > 1");
		}
	}
	
	protected static void ensureRangeStartAndEnd(Number rangeStart, Number rangeEnd, boolean rangeLessThanOne) throws IllegalArgumentException {
		if (rangeStart.doubleValue() < 0.) {
			throw new IllegalArgumentException("Cannot calculate range with a negative 'start' value");
		}
		if (rangeStart.doubleValue() > rangeEnd.doubleValue()) {
			throw new IllegalArgumentException("Range 'start' value should be less than or equal to the 'end' value");
		}
		if (rangeLessThanOne && rangeEnd.doubleValue() > 1) {
			throw new IllegalArgumentException("Cannot calculate range with an 'end' value > 1");
		}
	}
	
	// Helper method that sorts the input
	protected static List<String> getSorted(Collection<GeneralNode> nodeList) {
		ArrayList<String> nodeNames = new ArrayList<String>();
		for (GeneralNode n : nodeList) {
			nodeNames.add(n.getID());
		}
		Collections.sort(nodeNames);
		
		return nodeNames;
	}	
	
	public static void updateGraphCharacteristics(GeneralGraph newGraph, GeneralGraph oldGraph) {
		newGraph.setMedDRALevel(oldGraph.getMedDRALevel());
		newGraph.setDual(dataType);
		NetworkAnalysisVisualization.getInstance().SetNetworkViewTypeLabel(newGraph.GetNetworkTypeString(dataType));
	}
	
	protected abstract GeneralGraph getNetwork() throws IllegalArgumentException, InstantiationException, IllegalAccessException;
	protected abstract String getName();
	
	public GeneralGraph getFinalNetwork() throws IllegalArgumentException, InstantiationException, IllegalAccessException {
		
		GeneralGraph subnet;
		subnet = getNetwork();
		
		subnet.setName(getName());
		return subnet;
	}
}