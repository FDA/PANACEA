package com.eng.cber.na.project;

import java.awt.Cursor;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;

import com.eng.cber.na.Macro;
import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.graph.GeneralEdge;
import com.eng.cber.na.graph.GeneralNode;
import com.eng.cber.na.graph.GraphLoader;
import com.eng.cber.na.layout.NetworkVisualizationModel;
import com.eng.cber.na.layout.NetworkVisualizationModelContainer;
import com.eng.cber.na.model.GraphTreeModel;

public class SaveProject extends SwingWorker {
	private GraphTreeModel graphTreeModel;
	private GraphLoader graphLoader;
	private File file;
	private Macro history;
	private String strVertexSize;
	
	public SaveProject() {
	}
	public SaveProject(File file, GraphTreeModel graphTreeModel, GraphLoader graphLoader, String strVertexSize, Macro macro) {
		this.file = file;
		this.graphTreeModel = graphTreeModel;
		this.graphLoader = graphLoader;
		this.strVertexSize = strVertexSize;
		this.history = macro;
	}
	
	@Override
	protected Object doInBackground() throws Exception {
		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
		try {					
			nv.getStatusLabel().setText("Saving...");
			ObjectOutputStream output = null;
			setProgress(0);
			output = new ObjectOutputStream(new FileOutputStream(file));
			JTree graphTree = NetworkAnalysisVisualization.getInstance().getNetworkTree();
			
			/** Perform serialization for each visualization model container in the JTree structure.*/
			DefaultMutableTreeNode root = (DefaultMutableTreeNode) graphTree.getModel().getRoot();
			Enumeration en = root.breadthFirstEnumeration();
			Map<Integer, Map<String,NetworkVisualizationModel<GeneralNode,GeneralEdge>> > vmMap = new HashMap<Integer, Map<String,NetworkVisualizationModel<GeneralNode,GeneralEdge>> >();
			Map<Integer, Integer> parentMap = new HashMap<Integer, Integer>();
			
			int index = 0;
			int parentIndex = -1;
			while (en.hasMoreElements()) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) en.nextElement();
				Map<String,NetworkVisualizationModel<GeneralNode,GeneralEdge>> models = ((NetworkVisualizationModelContainer) node.getUserObject()).getModels();
				
				vmMap.put(index, models);
				if (node.getParent() == null)
					parentIndex = -1;
				else{
					Map<String,NetworkVisualizationModel<GeneralNode,GeneralEdge>> parentModels= (Map<String,NetworkVisualizationModel<GeneralNode,GeneralEdge>>)
							((NetworkVisualizationModelContainer)((DefaultMutableTreeNode)node.getParent()).getUserObject()).getModels();
					parentIndex = -2;
					for(Entry<Integer, Map<String,NetworkVisualizationModel<GeneralNode,GeneralEdge>>>  e:vmMap.entrySet()){
						if (e.getValue() == parentModels){
							parentIndex = e.getKey();
							break;
						}
					}
					if (parentIndex == -2) 
						System.out.println("Parent Not Found!");
				}
				parentMap.put(index,  parentIndex);
				index = index + 1;
			}
			output.writeObject(parentMap);
			output.writeObject(vmMap);
			
			setProgress(50);
			output.writeObject(graphLoader);
			setProgress(70);
			output.writeObject(strVertexSize);
			output.close();
			setProgress(100);
			NetworkAnalysisVisualization.logger.logp(java.util.logging.Level.INFO,"","", "Project " + file + " saved.");  
		}catch (IOException e){
			e.printStackTrace();
			nv.getStatusLabel().setText("Saving failed!");
		}catch(NullPointerException n){
			n.printStackTrace();
			nv.getStatusLabel().setText("Saving failed!");
		}
		nv.getStatusLabel().setText("Saving completed!");
		nv.setCursor(Cursor.getDefaultCursor());
		return null;
	}
}
