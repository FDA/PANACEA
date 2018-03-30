package com.eng.cber.na.weighting;

import java.awt.Cursor;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.NetworkAnalysisVisualization.WeightingScheme;
import com.eng.cber.na.graph.BuildGraphWithWeights;
import com.eng.cber.na.graph.GeneralEdge;
import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.graph.GeneralNode;
import com.eng.cber.na.layout.NetworkVisualizationModelContainer;
import com.eng.cber.na.util.CombinationBig;
import com.eng.cber.na.vaers.VAERS_Node;
import com.eng.cber.na.vaers.VAERS_Node.NodeType;

/**
 * @author G. Zhang gzhang@drc.com
 *
 */
public class Weighting implements Runnable, Serializable {
	GeneralGraph graph; 
	String strType = "";
	int simType = 1;

	Map<Object, Set<VAERS_Node>> report_hash;
	Map<Object, Double> weightMapping;
	private Map<Object, Double> infoForNode;
	
	double minWeight = 100000, maxWeight=0;
	double[] weights;
	
	public Weighting(GeneralGraph gg){
		this(gg, WeightingScheme.Singleton, "");
	}
	
	public Weighting(GeneralGraph gg, WeightingScheme ws ){
		this(gg, WeightingScheme.Singleton, "");
	}

	public Weighting(GeneralGraph gg, WeightingScheme ws, String strType){
		this.graph = gg;
		this.simType = ws.ordinal();
		this.strType = strType;
		weightMapping = new HashMap<Object, Double>();
	}
	
	public Map<Object, Double> getInfoForNode() {
		return infoForNode;
	}

	public void setInfoForNode(Map<Object, Double> infoForNode) {
		this.infoForNode = infoForNode;
	}

	private Map<Object, Double> infoForReport;
	
	
	public Map<Object, Double> getInfoForReport() {
		return infoForReport;
	}

	public void setInfoForReport(Map<Object, Double> infoForReport) {
		this.infoForReport = infoForReport;
	}

	public Map<Object, Double> getWeightMapping() {
		return weightMapping;
	}

	public void setWeightMapping(Map<Object, Double> weightMapping) {
		this.weightMapping = weightMapping;
	}
	
	@Override
	public void run() {

		if (NetworkAnalysisVisualization.getInstance().getUnderlyingData() == null){
			JOptionPane.showMessageDialog(null, "Node hash table does not exist!");
			NetworkAnalysisVisualization.getInstance().setCursor(Cursor.getDefaultCursor());
			return;
		}
		report_hash = NetworkAnalysisVisualization.getInstance().getUnderlyingData().getOrigReportHash();
		if (report_hash == null){
			NetworkAnalysisVisualization.logger.logp(java.util.logging.Level.INFO,"","","Node hash table does not exist for similarity computation");
			JOptionPane.showMessageDialog(null, "Node hash table does not exist!");
			NetworkAnalysisVisualization.getInstance().setWeightingScheme(WeightingScheme.Singleton);
			NetworkAnalysisVisualization.getInstance().setCursor(Cursor.getDefaultCursor());
			return;
		}
		
		if (graph.getDual() <= 1)
		{
			NetworkAnalysisVisualization.logger.logp(java.util.logging.Level.INFO,"","","Lin similarity not designed for element-based networks");
			JOptionPane.showMessageDialog(null, "This weighting scheme is only designed for report-based (SYM) networks.  Switching to singleton weighting");
			NetworkAnalysisVisualization.getInstance().setWeightingScheme(WeightingScheme.Singleton);
			NetworkAnalysisVisualization.getInstance().setCursor(Cursor.getDefaultCursor());
			return;
		}

		
		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
		JTree tree = nv.getNetworkTree();
		TreePath path = tree.getSelectionPath();
		DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
		NetworkVisualizationModelContainer vm= (NetworkVisualizationModelContainer) treeNode.getUserObject();
		String strWeighting = WeightingScheme.values()[simType].toString();
		
		if(vm.toString().startsWith("<Weight")){
			treeNode = (DefaultMutableTreeNode )treeNode.getParent();
			tree.setSelectionPath(path.getParentPath());
			graph = NetworkAnalysisVisualization.getInstance().getGraph();
		}
		if (simType == 1){
			NetworkAnalysisVisualization.getInstance().setCursor(Cursor.getDefaultCursor());
			return;
		}
		
		for(int i = 0; i<treeNode.getChildCount(); i++){
			if (((NetworkVisualizationModelContainer) ((DefaultMutableTreeNode)treeNode.getChildAt(i)).getUserObject()).toString().startsWith("<Weighting>" + strWeighting )){
				tree.setSelectionPath(new TreePath(((DefaultMutableTreeNode)treeNode.getChildAt(i)).getPath()));
				NetworkAnalysisVisualization.getInstance().setWeightingScheme(WeightingScheme.valueOf(strWeighting));
				NetworkAnalysisVisualization.getInstance().setCursor(Cursor.getDefaultCursor());
				return;					
			}
		}
		
		if (simType > 0){
			getWeight(simType, report_hash);
		}
		else{
			getLinSimWeight(report_hash);
		}
		
		if (maxWeight <= 0 )
		{
			NetworkAnalysisVisualization.logger.logp(java.util.logging.Level.INFO,"","","No edge is found and no network is generated");
			NetworkAnalysisVisualization.getInstance().setCursor(Cursor.getDefaultCursor());
			JOptionPane.showMessageDialog(null, "No edge is found and no network is generated for the weighting scheme: " + WeightingScheme.values()[simType].toString());
			return;
		}
		else
			SwingUtilities.invokeLater( new BuildGraphWithWeights(graph, weightMapping, WeightingScheme.values()[simType]));
	}
	
	private Map<Object, Double>  getWeight(int simType, Map<Object, Set<VAERS_Node>> report_hash ){
		Collection<GeneralEdge> edges = graph.getEdges();
		Iterator<GeneralEdge> it = edges.iterator();
		long weight;
		CombinationBig combination = new CombinationBig();
		
		while(it.hasNext()){
			GeneralEdge edge = it.next();
			Set<VAERS_Node> nodes1 = report_hash.get(edge.node1.getObject());
			Set<VAERS_Node> nodes2 = report_hash.get(edge.node2.getObject());
			
			Set<VAERS_Node> nodesCommon = new HashSet<VAERS_Node>(nodes1);
			Set<VAERS_Node> nodesCommonReduced = new HashSet<VAERS_Node>();
			nodesCommon.retainAll(nodes2);
			
			if (strType != ""){
				Iterator<VAERS_Node> it2 = nodesCommon.iterator();
				
				while(it2.hasNext()){
					VAERS_Node currentNode = it2.next();
					if(currentNode.getNodeType() == NodeType.valueOf(strType))
						nodesCommonReduced.add(currentNode);
				}
			}
			
			if(nodesCommonReduced.size()>=simType){
				combination.setValues(nodesCommonReduced.size(), simType );
				weight = combination.exec();
			}
			else
				weight = 0;
			
			weightMapping.put(edge.getID(), new Double(weight));
			if (weight<=0){
				continue;
			}

			if (weight > maxWeight )
				maxWeight = weight ;
			if (weight < minWeight )
				minWeight = weight;
		}
		return weightMapping;
	}
	
	public Map<Object, Double>  getLinSimWeight(Map<Object, Set<VAERS_Node>> report_hash ){
		if (graph.getDual() <= 1)
		{
			NetworkAnalysisVisualization.logger.logp(java.util.logging.Level.INFO,"","","Lin similarity not designed for element-based networks");
			JOptionPane.showMessageDialog(null, "This weighting scheme is only designed for report-based (SYM) networks.  Switching to singleton weighting");
			NetworkAnalysisVisualization.getInstance().setWeightingScheme(WeightingScheme.Singleton);
			NetworkAnalysisVisualization.getInstance().setCursor(Cursor.getDefaultCursor());
			return null;
		}		
		Map<Object, VAERS_Node> node_hash = NetworkAnalysisVisualization.getInstance().getUnderlyingData().getOrigNodeHash();
		infoForNode = new HashMap<Object, Double>();
		infoForReport = new HashMap<Object, Double>();
		int report_size = report_hash.size();
		for( GeneralNode n: node_hash.values()){
			if (n.getReportDegreeMap()==null)
				return null;
							
			if (strType != "")
				if (((VAERS_Node)n).getNodeType() != NodeType.valueOf(strType))
					continue;
			double v1 = n.getReportDegreeMap().keySet().size();

			infoForNode.put(((String)n.getObject()).toLowerCase(), -1*Math.log10(v1/report_size));
		}
		
		for(Object report: report_hash.keySet()){
			double info = 0; 
			Set<VAERS_Node> nodes = report_hash.get(report);
			Iterator<VAERS_Node> it = nodes.iterator();
			while(it.hasNext()){
				VAERS_Node n = it.next();
				if (n.getNodeType() != NodeType.valueOf(strType))
					continue;
				info = info + infoForNode.get(((String)n.getObject()).toLowerCase());
			}
			infoForReport.put(report, info);
		}
		
		Collection<GeneralEdge> edges = graph.getEdges();
		Iterator<GeneralEdge> it = edges.iterator();
		double weight;
		while(it.hasNext()){
			GeneralEdge edge = it.next();
			Set<VAERS_Node> nodes1 = report_hash.get(edge.node1.getObject());
			Set<VAERS_Node> nodes2 = report_hash.get(edge.node2.getObject());
			Set<VAERS_Node> nodesCommon = new HashSet<VAERS_Node>(nodes1);
			nodesCommon.retainAll(nodes2);
			Iterator<VAERS_Node> itCommon = nodesCommon.iterator();
			weight = 0;
			
			while (itCommon.hasNext() ){
				VAERS_Node currentNode = itCommon.next();
				if (currentNode.getNodeType() == NodeType.valueOf(strType)){
					weight = weight + infoForNode.get(((String)currentNode.getObject()).toLowerCase())*2;
				}
			}
			double infoAll = infoForReport.get(edge.node1.getObject())+infoForReport.get(edge.node2.getObject());
			double linSimilarityValue = weight/infoAll;
			
			if (linSimilarityValue > maxWeight )
				maxWeight = linSimilarityValue ;
			if (linSimilarityValue < minWeight )
				minWeight = linSimilarityValue;
			
			weightMapping.put(edge.getID(), new Double(linSimilarityValue));
		}
		return weightMapping;
	}
}