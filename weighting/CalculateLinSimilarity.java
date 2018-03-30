package com.eng.cber.na.weighting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.graph.GeneralEdge;
import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.vaers.VAERS_Node;

/**
 * @author G. Zhang gzhang@drc.com
 *
 */
public class CalculateLinSimilarity {
	
	private Map<Object, Double> selWeightMapping;
	
	public Map<Object, Double> getSelWeightMapping() {
		return selWeightMapping;
	}

	String strType = "";
	int simType = 1;

	Map<Object, Set<VAERS_Node>> report_hash;
	double minWeight = 100000, maxWeight=0;
	double threshold;
	double[] weights;
	private ArrayList<String> ptList;
	private Map<Object, Double> infoForNode;
	private Map<Object, Double> infoForReport;
	public CalculateLinSimilarity(ArrayList<String> ptList, Map<Object, Double> infoForNode, 
			Map<Object, Double> infoForReport, double threshold){
		this.ptList = ptList;
		this.infoForNode = infoForNode;
		this.infoForReport = infoForReport;
		this.threshold = threshold;
	}

	public void run() {
		double linSimilarityValue;

		report_hash = NetworkAnalysisVisualization.getInstance().getUnderlyingData().getOrigReportHash();
		
		Iterator<String> it = ptList.iterator();
		double infoSource = 0;
		while(it.hasNext()){
			String curPT = it.next();
			if(infoForNode.containsKey(curPT.toLowerCase())){
				infoSource = infoSource + infoForNode.get(curPT.toLowerCase());
			}
		}
		selWeightMapping = new HashMap<Object, Double>();
		
		for(Entry<Object, Double> entry: infoForReport.entrySet()){
			Map<String, VAERS_Node> tempMap = new HashMap<String, VAERS_Node>();
			
			Iterator<VAERS_Node> it2 = report_hash.get(entry.getKey()).iterator();
			while(it2.hasNext()){
				VAERS_Node node = it2.next();
				tempMap.put(((String)node.getID()).toLowerCase(), node);
			}

			ArrayList<String> tempSet = new ArrayList<String>(tempMap.keySet());
			tempSet.retainAll(ptList);
			if(!tempSet.isEmpty()){
				Iterator<String> it3 = tempSet.iterator();
				double common= 0; 
				while(it3.hasNext()){
					common = common + 2*infoForNode.get(it3.next()); 
				}
				linSimilarityValue = common/(infoSource + infoForReport.get(entry.getKey()));
				selWeightMapping.put(entry.getKey(), linSimilarityValue);
			}
		}

		selWeightMapping.put("ReferenceDocument",  infoSource);
	}
	
	public void findCommonReportSet(Map<Object, Double> firstMap, Map<Object, Double> secondMap){
		Set<Object> second = secondMap.keySet();
		Set<Object> first = firstMap.keySet();
		System.out.println("Selected Similar Reports: " + firstMap.size());
		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
		GeneralGraph graph = nv.getGraph();
		int counter = 0;
		
		Iterator<Object> it = second.iterator();
		while(it.hasNext()){
			counter = counter + 1;
			Object edgeID = it.next();
			GeneralEdge edge = graph.findEdgeByID(edgeID);
			if (!(first.contains(edge.node1.id) && first.contains(edge.node2.id)))
				secondMap.put(edgeID, 0.0);
			else
				if(firstMap.get(edge.node1.id) < threshold ||firstMap.get(edge.node2.id) < threshold  )
					secondMap.put(edgeID, 0.0);
		}
		
	}
	
}