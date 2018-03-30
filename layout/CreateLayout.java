package com.eng.cber.na.layout;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.graph.GeneralEdge;
import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.graph.GeneralNode;

public class CreateLayout {
	private String selLayoutType;
	private Layout<GeneralNode, GeneralEdge> layout, prevLayout; 
	public Layout<GeneralNode, GeneralEdge> getLayout() {
		return layout;
	}

	private GeneralGraph graph;
	
	public CreateLayout(String strLayout, GeneralGraph graph) {
		this.selLayoutType = strLayout.toLowerCase();
		this.graph = graph;	
		this.prevLayout = null;
	}

	public CreateLayout(String strLayout, GeneralGraph graph, Layout prevLayout) {
		this.selLayoutType = strLayout.toLowerCase();
		this.graph = graph;	
		this.prevLayout = prevLayout;
	}
	
	public Layout<GeneralNode, GeneralEdge> create(){
		if (graph.getEdgeCount() <= 1) { // some layouts don't render with only one edge, so set to CircleLayout
			layout = new CircleLayout<GeneralNode, GeneralEdge>(graph);
		}
		else{			
			if(selLayoutType.equals("principal components")) {
				layout = new PCALayout(graph);
			}
			else if(selLayoutType.equals("force directed")) {
				// Start off the update using the current location of all the nodes, rather than the default 0,0 location
				if(prevLayout == null )
					layout = new FRLayout<GeneralNode,GeneralEdge>(graph);
				else
					layout = new FRLayout<GeneralNode,GeneralEdge>(prevLayout);
			}
			else if(selLayoutType.equals("island height")) {
				layout = new IslandLayout(graph);
			}
			else if(selLayoutType.equals("self-organizing map")) {
				layout = new ISOMLayout<GeneralNode, GeneralEdge>(graph);
			}
			else if(selLayoutType.equals("circle")) {
				layout = new CircleLayout<GeneralNode, GeneralEdge>(graph);
			}
			else if(selLayoutType.equals("vos mapping")) {
				layout = new VOSLayout<GeneralNode, GeneralEdge>(graph);
			}
			else {
				NetworkAnalysisVisualization.getInstance().logger.logp(java.util.logging.Level.INFO,"","","No layout match is found, use PCA instead");
				layout = new PCALayout(graph);
			}
		}
		return layout;
	}
}
