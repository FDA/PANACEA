package com.eng.cber.na.command;

import java.util.Collection;
import java.util.Iterator;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.command.util.BaseCommand;
import com.eng.cber.na.graph.GeneralEdge;
import com.eng.cber.na.graph.GeneralNode;

/****
 * The command pattern design to remove certain graph/node information
 * from the current graph.  This includes: similarity threshold to
 * a ReferenceDocument and clustering information, including coloring.
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */
public class ResetGraphCommand extends BaseCommand{
	@Override
	public void execute(String name) {
		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
		nv.getGraph().setSimilarityThreshold(0);
		
		Collection<GeneralNode> nodes = nv.getGraph().getVertices();
		Iterator<GeneralNode> it = nodes.iterator();
		while(it.hasNext()){
			GeneralNode node = it.next();
			node.setClusterColor(null);
			nv.getGraph().getNodeDisplayTransformer().set(node, true);
			node.setCluster(-1);
		}
		
		Collection<GeneralEdge> edges = nv.getGraph().getEdges();
		Iterator<GeneralEdge> itEdge = edges.iterator();
		while(itEdge.hasNext()){
			nv.getGraph().getEdgeDisplayTransformer().set(itEdge.next(), true);
		}
		
		nv.getNetworkGLVisualizationServer().repaint();
	}

	@Override
	public Boolean recordable() {
		return true;
	}
}
