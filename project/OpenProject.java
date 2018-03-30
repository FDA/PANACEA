package com.eng.cber.na.project;

import java.awt.Cursor;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.eng.cber.na.Macro;
import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.event.GraphTreeSelectionListener;
import com.eng.cber.na.event.mouse.NetworkPopupMouseEvent;
import com.eng.cber.na.gl.NetworkGLVisualizationServer;
import com.eng.cber.na.graph.GeneralEdge;
import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.graph.GeneralNode;
import com.eng.cber.na.graph.GraphLoader;
import com.eng.cber.na.layout.NetworkVisualizationModel;
import com.eng.cber.na.layout.NetworkVisualizationModelContainer;
import com.eng.cber.na.model.GraphTreeModel;
import com.eng.cber.na.renderer.GraphTreeCellRenderer;

public class OpenProject extends SwingWorker {
	File file;
	private String strVertexSize = "";
	private GraphLoader graphLoader = null;
	private GraphTreeModel graphTreeModel = null;
	private Macro history;
	private int countDown = 0;
	public OpenProject(File file){
		this.file= file;
	}

	@Override
	protected Object doInBackground() throws Exception {
		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
		ObjectInputStream input = null;
		try{
			nv.getStatusLabel().setText("Project opening...");
			NetworkGLVisualizationServer<GeneralNode,GeneralEdge> vv = nv.getNetworkGLVisualizationServer();
			input = new ObjectInputStream(new FileInputStream(file));
			Map<Integer, Integer> parentMap = (Map<Integer, Integer> ) input.readObject();
			Map<Integer, Map<String,NetworkVisualizationModel<GeneralNode,GeneralEdge>>> vmMap = (Map<Integer, Map<String,NetworkVisualizationModel<GeneralNode,GeneralEdge>>> )input.readObject();
						
			JTree graphTree = new JTree();
			Map<Integer, NetworkVisualizationModelContainer> mapContainer = new HashMap<Integer, NetworkVisualizationModelContainer>();
			for(int j = 0; j < vmMap.size(); j ++){ 
				Map<String,NetworkVisualizationModel<GeneralNode,GeneralEdge>> models = vmMap.get(j);
				
				NetworkVisualizationModelContainer currentContainer = new NetworkVisualizationModelContainer(models);
				mapContainer.put(j,  currentContainer);
				if (j == 0){
					graphTreeModel = new GraphTreeModel(currentContainer);
					graphTree = new JTree(graphTreeModel);
				}
				else{
					if(j > parentMap.get(j)) //Parent node is needed to add to the network
						graphTreeModel.addNetwork(mapContainer.get(parentMap.get(j)), currentContainer);
					else
					{
						NetworkAnalysisVisualization.logger.logp(java.util.logging.Level.WARNING,"","","Parent network node has not been created: " + models  );
					}						
				}

			}
			setProgress(20);
			graphLoader = (GraphLoader) input.readObject();
			setProgress(30);
			strVertexSize = (String) input.readObject();
			input.close();
			setProgress(50);
			nv.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			/** Generate layouts for all tree nodes.*/
			DefaultMutableTreeNode root = (DefaultMutableTreeNode) graphTree.getModel().getRoot();
			Enumeration en = root.breadthFirstEnumeration();
			GeneralGraph graph;
			countDown = graphTree.getRowCount();
			
			NetworkAnalysisVisualization.logger.logp(java.util.logging.Level.INFO,"","","Objects in the project " + file + " loaded.");
			
			while (en.hasMoreElements()) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) en.nextElement();
				NetworkVisualizationModelContainer vm = (NetworkVisualizationModelContainer) node.getUserObject();

				Map<String,NetworkVisualizationModel<GeneralNode, GeneralEdge>> models = vm.getModels();
				countDown = countDown + models.size() - 1;
			}
			/** update new graph tree structure.*/			
			JTree tree = nv.getNetworkTree();
			if(tree != null){
				tree.setModel(null);
				nv.setGraphTree(null);
			}

			nv.setGraphTree(graphTree);
			GraphTreeModel graphTreeModel = (GraphTreeModel) graphTree.getModel();
			nv.setGraphTreeModel(graphTreeModel);
			nv.getGtsp().getViewport().removeAll();
			nv.getGtsp().getViewport().add(graphTree);
			nv.setGraphLoader(graphLoader);
			nv.getGtsp().revalidate();

			graphTree.setSelectionRow(0);
			TreePath path = graphTree.getPathForRow(0);
			if(path != null) {
				Object[] treePath = path.getPath();
				NetworkVisualizationModelContainer vm = (NetworkVisualizationModelContainer)((DefaultMutableTreeNode)treePath[treePath.length-1]).getUserObject();
				graph =  vm.getCurrentModel().getModel();
				graphTree.setCellRenderer(new GraphTreeCellRenderer());
				graphTree.addMouseListener(new NetworkPopupMouseEvent(nv.getCommandActionListener(), nv.getAppDir()));
				graphTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
				graphTree.setVisible(true);
				graphTree.setEditable(true);
				ToolTipManager.sharedInstance().registerComponent(graphTree);
				vv.setModel(vm);
				nv.setVisualizationModelAndReload(vm);
				graphTree.addTreeSelectionListener(new GraphTreeSelectionListener((NetworkVisualizationModelContainer)vv.getModel()));
				nv.showBottomPanel(true);
				nv.setLayoutType(vm.getCurrentModel().getLayoutType());
				int networkType = graph.getDual();
				nv.SetNetworkViewTypeLabel(graph.GetNetworkTypeString(networkType));
				nv.setDualID(networkType);
				nv.updateGraphLayout();
				nv.setVertexSize(strVertexSize);
				nv.getStatusLabel().setText("Project opened!");
				nv.enableControls(true);
				nv.setCursor(Cursor.getDefaultCursor());

			}
		}
		catch (IOException x)
		{
			x.printStackTrace();
			nv.getStatusLabel().setText("Project opening failed!");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			nv.getStatusLabel().setText("Project opening failed!");
		}
		return null;
	}

}

