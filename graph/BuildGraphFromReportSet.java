 package com.eng.cber.na.graph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.dual.IdentifyNodesFromReports;

public class BuildGraphFromReportSet extends AbstractCreateGraph {
	Set<?> reportIDs;
	Set<?> nodeIDs;
	
	public BuildGraphFromReportSet(boolean isNew) {
		this(-2, isNew);
	}
	public BuildGraphFromReportSet(int rowForParentPath, boolean isNew) {
		super(rowForParentPath, isNew);
	}
	@Override
	protected FDAGraph getNetwork() throws IllegalArgumentException,
			InstantiationException, IllegalAccessException {
		Set<Object> nodes;
		Set<Object> reports = null;
		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
		GraphLoader gl = nv.getUnderlyingData();
		GeneralGraph graph = nv.getGraph();
		int networkType = nv.getDualID();
		boolean loadFromHash = false;
		if(graph == null )
			loadFromHash = true;
		else if (graph.getVertexCount() == 0 )
			loadFromHash = true;

		if (loadFromHash  == false){
			if (parent.getDual() > 0){
				Collection<GeneralNode> nodesInGraph = graph.getVertices();
				reports = new HashSet<Object>();
				for(GeneralNode node: nodesInGraph){
					if(graph.getNodeDisplay(node))
						reports.add(node.getObject());
				}
			}
			else{
					reports = graph.getReports();
			}
		}
		else{
			reports = gl.getOrigReportHash().keySet();			
		}
			
		if (reports.size() == 0){
			JOptionPane.showMessageDialog(null, "No edge exists in the graph.");
			return null;
		}
		

		IdentifyNodesFromReports idNodes = new IdentifyNodesFromReports(gl);
		nodes = idNodes.GetNodeList(reports);

		if (networkType > 0){
			Set<Object> tempSet = reports;
			reports = nodes;
			nodes = tempSet;
		}
		BuildGraphFromReports bgfr = new BuildGraphFromReports(reports, nodes, networkType);
		
		FDAGraph network = bgfr.buildNetwork();
		network.setDual(networkType);
		nv.SetNetworkViewTypeLabel(network.GetNetworkTypeString(networkType));

		return network;
	}

	@Override
	protected String getName() {
		String name ;
		if (parent == null)
			name = "";
		else
			name = parent.getName();
		
		return name;
	}
}
