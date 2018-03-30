package com.eng.cber.na.communitydetection;

import java.awt.Color;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.gl.NetworkGLVisualizationServer;
import com.eng.cber.na.graph.FDAGraph;
import com.eng.cber.na.graph.GeneralEdge;
import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.graph.GeneralNode;
import com.eng.cber.na.layout.Layout;
import com.eng.cber.na.layout.NetworkVisualizationModelContainer;
import com.eng.cber.na.model.GraphTreeModel;
import com.eng.cber.na.subgraph.CreateClusteringSubgraph;

import edu.uci.ics.jung.graph.Graph;


/**
 * @author Guangfan (Geoffrey) Zhang
 *
 */
public abstract class CommunityDetection extends SwingWorker {

	GeneralGraph gg;
	public int numClusters = 2;
	protected boolean continueToCluster = false;
	
	private String ClusterColor[] = {"red", "green", "yellow", "darkGray", "magenta"};
	
	public Collection<Map<GeneralNode, Integer>>  clusters;
	
	public CommunityDetection(){
		return;
	}
	public CommunityDetection(GeneralGraph gg){
		this.gg = gg;
	};
	
	public Map<GeneralNode, double[]> adjVectors;

	@Override
	public Object doInBackground(){
		Init();
		setProgress(10);
		if (continueToCluster )
		{	clusters = cluster();
			setProgress(70);
			gg.setClusters(clusters);
			PaintCluster(clusters);
		}
		setProgress(95);
		return null;
	}
	
	public void Init(){
	}
	public Collection<Map<GeneralNode, Integer>> cluster(){
		return null;
	}

	public void PaintCluster( Collection<Map<GeneralNode, Integer>> clusters ){
		List<Map<GeneralNode, Integer>> cluster_list = new ArrayList<Map<GeneralNode, Integer>>(clusters);
		int numClusters = cluster_list.size();

		Color[] cluster_colors = new Color[numClusters];
		int j = 0;
		for (String curColor : ClusterColor){
			if (j>=numClusters)
				break;
			Field field;
			try {
				field = Class.forName("java.awt.Color").getField(curColor);
				cluster_colors[j] = (Color) field.get(null);
			} catch (SecurityException e1) {
				e1.printStackTrace();
				cluster_colors[j] = null;
			} catch (NoSuchFieldException e1) {
				e1.printStackTrace();
				cluster_colors[j] = null;
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
				cluster_colors[j] = null;
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				cluster_colors[j] = null;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				cluster_colors[j] = null;
			} 
			j = j+1;
		}
		
		for (int i=ClusterColor.length; i<numClusters; ++i)
		{
			cluster_colors[i] = genRandColor(); 
		}

		Map<GeneralNode, Integer> current_cluster;

		for (int i= 0; i < cluster_list.size(); i++){
			current_cluster = cluster_list.get(i);
			Iterator<GeneralNode> it_cluster = current_cluster.keySet().iterator();

			GeneralNode node;
			while (it_cluster.hasNext()){
				node = it_cluster.next();
				node.setClusterColor(cluster_colors[i]);
				node.setCluster(i);
			}
		}
		
		CalcMetrics calcMetrics = new CalcMetrics(gg, cluster_list);
		calcMetrics.calculate();
		calcMetrics.SetMetricsToGraph();
		
		NetworkAnalysisVisualization nv =NetworkAnalysisVisualization.getInstance(); 
		
		JTree tree = nv.getNetworkTree();
		TreePath path = tree.getSelectionPath();
		
		Object[] treePath = path.getPath();
		
		NetworkGLVisualizationServer vv = nv.getNetworkGLVisualizationServer();
		GeneralGraph newGraph ;
		if(nv.getGraph() instanceof FDAGraph)
			newGraph = new FDAGraph(nv.getGraph());
		else
			newGraph = new GeneralGraph(nv.getGraph());
			
		newGraph.setName(nv.getGraph().getName() + "_" + this.getName() );
		
		NetworkVisualizationModelContainer vm = 
				(NetworkVisualizationModelContainer)((DefaultMutableTreeNode)treePath[treePath.length-1]).getUserObject();
				
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
				
		newGraph.setDual(nv.getGraph().getDual());
		NetworkAnalysisVisualization.getInstance().SetNetworkViewTypeLabel(newGraph.GetNetworkTypeString(nv.getGraph().getDual()));

		NetworkVisualizationModelContainer nvm = new NetworkVisualizationModelContainer(layout,vv.getSize(), nv.getGraph().getDual());	
		TreeNode[] newPath;
		
		GraphTreeModel model = (GraphTreeModel)tree.getModel();

		newPath = model.addNetwork(vm, nvm);
		tree.setSelectionPath(new TreePath(newPath));
		
	
		//Create subgraphs based on clustering results
		int curPathRow = tree.getRowForPath(tree.getSelectionPath());
		int rowCount = tree.getRowCount();
		for (int i= 0; i < cluster_list.size(); i++){
			SwingUtilities.invokeLater(new CreateClusteringSubgraph(this.getName(), i, curPathRow));
		}
		while(true){
			if(tree.getRowCount() >= cluster_list.size() + rowCount){
				break;
			}
		}
		tree.setSelectionRow(rowCount-1);
	}
	
	public String getName(){
		return "Community Detection";
	}
	
	public static Color genRandColor() {
		float[] c = new float[3];
		int i = 0;
		while(i < 3) {
			c[i] = (float) Math.random();
			if(c[i] <= 0.8) i++;
		}
		return new Color(c[0],c[1],c[2]);
	}
}