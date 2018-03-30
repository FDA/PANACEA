package com.eng.cber.na.graph;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import org.apache.commons.collections15.Transformer;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.concurrent.ConcurrentJobs;
import com.eng.cber.na.concurrent.NetworkExecutorService;
import com.eng.cber.na.dialog.ProgressDialog;
import com.eng.cber.na.layout.Layout;
import com.eng.cber.na.vaers.VAERS_Node;
import com.eng.cber.na.weighting.Weighting;

import edu.uci.ics.jung.algorithms.util.MapSettableTransformer;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

/**
 * A generic version of a graph, as used in the field of network
 * analysis.  The graph is made up of nodes and edges and can be
 * represented visually very simply.  Edges connect two nodes if
 * they have something in common.  The weight, or visual thickness,
 * of the line indicates the strength of the connection.<br/><br/>
 * 
 * Many calculations exist for determining the centrality of a
 * node in a graph, or how important it is in the graph as a 
 * whole.  This class tracks closeness, betweenness, degree, and
 * strength for the nodes in the graph.<br/><br/>
 * 
 * This class also supports so-called "dual" graphs, where the graph
 * is a document-based network instead of an element-based network.
 * In document-based networks, the nodes are individual reports (from
 * VAERS, FAERS, etc.) and the edges represent the terms that are in
 * common between two reports.  In element-based networks, the nodes
 * represent each term that can be found in any of the documents, and
 * the edges represent the number of documents that include both of
 * the terms.  A graph is "dual" if it is a document-based network, and
 * the dualID will be either 1 or 2.  A non-dual or element-based
 * network has a dualID of 0.<br/><br/>
 * 
 * By extending UndirectedSparseGraph from the JUNG package, this
 * class has access to a large number of JUNG methods for 
 * controlling and accessing the nodes and edges in this graph.
 * 
 * @author Guangfan (Geoffrey) Zhang
 */
@SuppressWarnings("serial")
public class  GeneralGraph extends UndirectedSparseGraph<GeneralNode, GeneralEdge> implements Serializable{
	private String name;
	private String lineage;
	private boolean FDAType = false;
	/**Tells what type of network is shown in this graph.<br/>
	 * 0 = Element Network<br/>
	 * 1 = Report Network (VAX)<br/>
	 * 2 = Report Network (SYM)*/
	protected int dualID = 0;
	protected boolean multiDualsAllowed = true;
	private boolean isDualGenerated = false;
	private Layout.LayoutType layoutType;
	private int totalReportSize;
	private Collection<Map<GeneralNode, Integer>>  clusters;
	private Weighting similarity;
	private double similarityThreshold = 0.0;
	private boolean islandsAreCalculated = false;
	private boolean calculatingIslands = false;
	private boolean betweenCloseAreCalculated = false;
	private boolean calculatingBetweenClose = false;
	private double[] conductance = null;
	private double[] expansion = null;
	public GeneralGraph(GeneralGraph another){

	    for (GeneralNode v : another.getVertices())
	        this.addVertex(v);

	    for (GeneralEdge e : another.getEdges())
	        this.addEdge(e, another.getIncidentVertices(e));
	    
		this.copyGraphStatistics(another);
	}
	public double[] getExpansion() {
		return expansion;
	}

	public void setExpansion(double[] expansion) {
		this.expansion = expansion;
	}

	private double[] internalDensity = null;
	public double[] getInternalDensity() {
		return internalDensity;
	}

	public void setInternalDensity(double[] internalDensity) {
		this.internalDensity = internalDensity;
	}

	public double[] getNormalizedCut() {
		return normalizedCut;
	}

	public void setNormalizedCut(double[] normalizedCut) {
		this.normalizedCut = normalizedCut;
	}

	public double[] getCutRatio() {
		return cutRatio;
	}

	public void setCutRatio(double[] cutRatio) {
		this.cutRatio = cutRatio;
	}

	private double[] normalizedCut = null;
	private double[] cutRatio = null;
	
	
	public double[] getConductance() {
		return conductance;
	}

	public void setConductance(double[] conductance) {
		this.conductance = conductance;
	}

	public double getSimilarityThreshold() {
		return similarityThreshold;
	}

	public void setSimilarityThreshold(double similarityThreshold) {
		this.similarityThreshold = similarityThreshold;
	}

	public Weighting getSimilarity() {
		return similarity;
	}

	public void setSimilarity(Weighting similarity) {
		this.similarity = similarity;
	}

	public Collection<Map<GeneralNode, Integer>> getClusters() {
		return clusters;
	}

	public void setClusters(Collection<Map<GeneralNode, Integer>> clusters) {
		this.clusters = clusters;
	}
	
	public void removeclusterInfo(){
		Iterator<GeneralNode> it = this.getVertices().iterator();
		while(it.hasNext()){
			it.next().setClusterColor(null);
			it.next().setCluster(-1);
		}
	}		

	public int getTotalReportSize() {
		return totalReportSize;
	}

	public void setTotalReportSize(int totalReportSize) {
		this.totalReportSize = totalReportSize;
	}

	public Layout.LayoutType getLayoutType() {
		return layoutType;
	}

	public void setLayoutType(Layout.LayoutType layoutType) {
		this.layoutType = layoutType;
	}

	List<Island> islands = new ArrayList<Island>();
	public boolean isFDAType() {
		return FDAType;
	}

	public void setFDAType(boolean fDAtype) {
		FDAType = fDAtype;
	}

	private double maxWeight = 0;
	private double minWeight = Integer.MAX_VALUE;	
	
	private Integer maxDegree = 0;
	private Integer minDegree = Integer.MAX_VALUE;
	
	
	private Double maxBetweenness = 0.0;
	private Double minBetweenness = Double.MAX_VALUE;

	private Double maxCloseness = Double.MAX_VALUE;
	private Double minCloseness = 0.0;
	
	private Double maxStrength = 0.;
	private Double minStrength = Double.MAX_VALUE;
	
	
	private Map<Integer, Set<GeneralNode>> componentToNodes = new HashMap<Integer, Set<GeneralNode>>();
	public Map<Integer, Set<GeneralNode>> getComponentToNodes() {
		return componentToNodes;
	}

	private Map<GeneralNode, Integer> nodeToComponent = new HashMap<GeneralNode, Integer>();
	
	public Map<GeneralNode, Integer> getNodeToComponent() {
		return nodeToComponent;
	}
	
	public Set<GeneralNode> getNodesInComponent(int i ){
		Set<GeneralNode> nodes = componentToNodes.get(i);
		return nodes;
	}

	private Integer maxReportCount = 0;
	private Integer minReportCount = Integer.MAX_VALUE;
	
	
	private VertexStrengthScorer strengthScorer = new VertexStrengthScorer(this);
	private Map<GeneralNode, Integer> islandHeightMap;
	private Map<GeneralNode, Double> betweennessMap = null;
	private Map<GeneralNode, Double> closenessMap = null;
	private Map<GeneralNode, Double> strengthMap = null;
	private Map<GeneralEdge, Double> betweennessEdgeMap = null;
	private Map<GeneralEdge, Boolean> islandsMstMap = null;
	private Map<GeneralNode, Boolean> nodeDisplayMap = null;
	private Map<GeneralEdge, Boolean> edgeDisplayMap = null;
	
	private transient MapSettableTransformer<GeneralNode, Integer> islandHeightTransformer;
	private transient MapSettableTransformer<GeneralNode, Double> betweennessTransformer;
	private transient MapSettableTransformer<GeneralNode, Double> closenessTransformer;
	private transient MapSettableTransformer<GeneralNode, Double> strengthTransformer;
	
	private transient MapSettableTransformer<GeneralEdge, Double> betweennessEdgeTransformer;
	private transient MapSettableTransformer<GeneralEdge, Boolean> islandsMstTransformer;
	private transient MapSettableTransformer<GeneralEdge, Double> linSimilarityEdgeTransformer;
	
	private transient MapSettableTransformer<GeneralNode, Boolean> nodeDisplayTransformer;
	private transient MapSettableTransformer<GeneralEdge, Boolean> edgeDisplayTransformer;
	
	
	
	public GeneralGraph(int dualID) {
		this.name = "(no name)";
		this.lineage = "(no description)";
		this.dualID = dualID;
	}
	
	public GeneralGraph() {
		this.name = "(no name)";
		this.lineage = "(no description)";
		this.dualID = 0;
	}
	
	public MapSettableTransformer<GeneralNode,Double> getBetweennessTransformer() {
        return betweennessTransformer;
	}
	
	public void setBetweennessTransformer(Map<GeneralNode,Double> map) {
		betweennessMap = map;
		betweennessTransformer = new MapSettableTransformer<GeneralNode,Double>(map);
	}
	
	public MapSettableTransformer<GeneralNode,Double> getClosenessTransformer() {
	        return closenessTransformer;
	}
	
	public void setClosenessTransformer(Map<GeneralNode,Double> map) {
		closenessMap = map;
		closenessTransformer = new MapSettableTransformer<GeneralNode,Double>(map);
	}

	public MapSettableTransformer<GeneralEdge,Double> getBetweennessEdgeTransformer() {
        return betweennessEdgeTransformer;
	}

	public void setBetweennessEdgeTransformer(Map<GeneralEdge,Double> map) {
		betweennessEdgeMap = map;
		betweennessEdgeTransformer = new MapSettableTransformer<GeneralEdge,Double>(map);
	}

	public MapSettableTransformer<GeneralEdge,Double> getLinSimilarityEdgeTransformer() {
		
	        return linSimilarityEdgeTransformer;
	}
	
	public void setLinSimilarityEdgeTransformer(Map<GeneralEdge,Double> map) {
	        linSimilarityEdgeTransformer = new MapSettableTransformer<GeneralEdge,Double>(map);
	}
		
	public MapSettableTransformer<GeneralNode,Double> getStrengthTransformer() {
        return strengthTransformer;
	}
	
	public void setStrengthTransformer(Map<GeneralNode, Double> map) {
		strengthMap = map;
	}
	
	public MapSettableTransformer<GeneralEdge,Boolean> getIslandsMSTTransformer() {
        return islandsMstTransformer;
	}
	
	public void setIslandsMSTTransformer(List<GeneralEdge> includedEdges) {
		if(islandsMstMap == null){
			islandsMstMap = new HashMap<GeneralEdge, Boolean>();
			islandsMstTransformer = new MapSettableTransformer<GeneralEdge, Boolean>(islandsMstMap);
		}
		for (GeneralEdge e : getEdges()) {
			if (includedEdges.contains(e)) {
				islandsMstTransformer.set(e, Boolean.TRUE);
			}
			else {
				islandsMstTransformer.set(e, Boolean.FALSE);
			}
		}
	}
	
	public MapSettableTransformer<GeneralNode, Boolean> getNodeDisplayTransformer() {
		return nodeDisplayTransformer;
	}
	
	public void setNodeDisplayTransformer(Map<GeneralNode, Boolean> map) {
		nodeDisplayMap = map;
		nodeDisplayTransformer = new MapSettableTransformer<GeneralNode, Boolean>(map);
	}
	
	public MapSettableTransformer<GeneralEdge, Boolean> getEdgeDisplayTransformer() {
		return edgeDisplayTransformer;
	}
	
	public void setEdgeDisplayTransformer(Map<GeneralEdge, Boolean> map) {
		edgeDisplayMap = map;
		edgeDisplayTransformer = new MapSettableTransformer<GeneralEdge, Boolean>(map);
	}
	
	public GeneralEdge findEdgeByID(Object edgeID){
		GeneralEdge edgeFound = null;
		for (GeneralEdge e : getEdges()) {
			if(e.getID().equals(edgeID)){
				edgeFound = e;
				break;
			}
		}
		return edgeFound;
	}
	public GeneralNode findNodeByID(Object nodeID){
		GeneralNode nodeFound = null;
		for (GeneralNode node: getVertices()) {
			String curNodeID =node.getID(); 
			if(curNodeID.equals(nodeID)){
				nodeFound = node;
				break;
			}
		}
		return nodeFound;
	}
	
	public void setComponentToNodes(Map<Integer,Set<GeneralNode>> componentToNodes) {
		this.componentToNodes = componentToNodes;
	}
	
	public void setNodeToComponent(Map<GeneralNode,Integer> nodeToComponent) {
		this.nodeToComponent = nodeToComponent;
	}
	
	public int getMainComponent() {
		Integer biggestComponentID = -1;
		int biggestComponentSize = Integer.MIN_VALUE;
		for (Integer componentID : componentToNodes.keySet())
		{
			int numNodesWithinComponent = componentToNodes.get(componentID).size();
			if (numNodesWithinComponent > biggestComponentSize) {
				biggestComponentSize = numNodesWithinComponent;
				biggestComponentID = componentID;
			}
		}
		return biggestComponentID;
	}
	
	public boolean inMainComponent(GeneralNode n) { 
		return (nodeToComponent.get(n) == getMainComponent());
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String s) {
		name = s;
	}

	public void setDualGenerated() {
		isDualGenerated = true;
	}
	public int getDual() {
		return dualID;
	}
	
	public boolean getMultiDualsAllowed() {
		return multiDualsAllowed;
	}
	
	public void setDual(int dualID) {
		this.dualID= dualID;
	}
	public boolean getDualGenerated() {
		return isDualGenerated;
	}
	
	public void setLineage(String lineage) {
		this.lineage = lineage;
	}
	
	public String getLineage() {
		return lineage;
	}

	public String getLongDescription() {
		return new String(name + ": " + getVertexCount() + " vertices; " + getEdgeCount() + " edges; " + lineage);
	}
	
	public String toString() {
		return name;
	}

	public Set<Object> getNodeObjects()
	{
		Set<Object> nodes = new HashSet<Object>();
		for(GeneralNode node : this.getVertices())
		{
			nodes.add(node.getObject());
		}

		return nodes;
	}
	public Set<Object> getDisplayedNodeObjects()
	{
		Set<Object> nodes = new HashSet<Object>();
		for(GeneralNode node : this.getVertices())
		{
			if(getNodeDisplay(node))
				nodes.add(node.getObject());
		}
		return nodes;
	}

	public Set<Object> getDisplayedEdgeObjects()
	{
		Set<Object> edges= new HashSet<Object>();
		for(GeneralEdge edge: this.getEdges())
		{
			if(getEdgeDisplay(edge))
				edges.add(edge.getID());
		}
		return edges;
	}
	
	private Object[] getNodesAlongEdge(GeneralEdge edge){
		if (edge == null) {
			throw new NullPointerException("Cannot get incident vertices for a null edge");
		}
		Collection<GeneralNode> nodes = this.getIncidentVertices(edge);
		if (nodes == null) {
			throw new NullPointerException("There are no vertices incident to the edge " + edge.toString());
		}
		if (nodes.size() != 2) {
			throw new IllegalArgumentException("Edges must connect two and exactly two nodes.");
		}
		return nodes.toArray();
	}
	
	public GeneralNode getFrom(GeneralEdge edge) {
		Object[] nodesAsArray = getNodesAlongEdge(edge);
		GeneralNode from = (GeneralNode)nodesAsArray[0];
		return from;
	}
	
	public GeneralNode getTo(GeneralEdge edge){
		Object[] nodesAsArray = getNodesAlongEdge(edge);
		GeneralNode to = (GeneralNode)nodesAsArray[1];
		return to;
	}

	
	private VertexStrengthScorer getVertexStrengthScorer() {
		return strengthScorer;
	}	
	
	public Integer getComponentCount() {
		return componentToNodes.size();
	}
	public Integer getComponentID(GeneralNode node) {
		return nodeToComponent.get(node);
	}
	
	public Double getDensity() {
		Double numNodes = new Double(getVertexCount());
		Double numEdges = new Double(getEdgeCount());
		Double totalPossibleEdges = (numNodes * (numNodes-1))/2;
		
		if (totalPossibleEdges == 0) {
			return 0.0;
		}
		return (numEdges/totalPossibleEdges);
	}
	
	public Double getStrength(GeneralNode n) {
		if (strengthTransformer == null )
		{
			strengthMap = new HashMap<GeneralNode, Double>();
			strengthTransformer = new MapSettableTransformer<GeneralNode, Double>(strengthMap);
		}
		Double val = strengthTransformer.transform(n);
		if (val == null){
			val = getVertexStrengthScorer().getVertexScore(n);
			strengthTransformer.set(n, val);
		}
		return val;
	}
	
	/**
	 * Returns normalized closeness (range is [0,1]).
	 */
	public Double getCloseness(GeneralNode n) {
		return normalizeCloseness(getUnnormalizedCloseness(n));
	}
	
	/**
	 * Returns non-normalized closeness (range is [0, positive infinity]).
	 */
	public Double getUnnormalizedCloseness(GeneralNode n) {
		Double val = closenessTransformer.transform(n);
		if (val == null) {
			return 0.0;
		}
		return val;
	}
	
	
	/**
	 * Returns normalized betweenness (range is [0,1]).
	 */
	public Double getBetweenness(GeneralNode n) {		
		Double val = betweennessTransformer.transform(n);
		if (val == null) {
			return 0.0;
		}
		return normalizeBetweenness(val);
	}
	
	/**
	 * Returns non-normalized betweenness (range is [0, positive infinity]).
	 */
	public Double getUnnormalizedBetweenness(GeneralNode n) {
		Double val = betweennessTransformer.transform(n);
		if (val == null) {
			return 0.0;
		}
		return val;
	}
	
	private Double normalizeBetweenness(Double val) {		
		if (getVertexCount() <= 2) {
			return 0.;
		}
		return val/((getVertexCount()-1)*(getVertexCount()-2)/2);
	}
	
	
	private Double normalizeCloseness(Double val) {	
		if (getVertexCount()-1 == 0) {
			return 0.0;
		}
		return val/(getVertexCount()-1);
	}
	
	public Double getLinSimilarity(GeneralEdge n) {		
		Double val = linSimilarityEdgeTransformer.transform(n);
		if (val == null) {
			return 0.0;
		}
		return val;
	}
	
	public Double getMaxStrength() {
		return maxStrength;
	}
	
	public void setMaxStrength(Double maxStrength) {
		this.maxStrength = maxStrength;
	}
	
	public Double getMinStrength() {
		return minStrength;
	}
	
	public void setMinStrength(Double minStrength) {
		this.minStrength = minStrength;
	}
	
	public Integer getDegree(GeneralNode n) {
		return degree(n);
	}
	
	public Double getNormalizedDegree(GeneralNode n) {
		if (getVertexCount() <= 1) {
			return 0.;
		}
		return degree(n)/(new Double(getVertexCount()-1));
	}

	public Integer getMaxDegree()  {
		return maxDegree;
	}
	
	public void setMaxDegree(Integer maxDegree) {
		this.maxDegree = maxDegree;
	}
	
	public Integer getMinDegree() {
		return minDegree;
	}
	
	public void setMinDegree(Integer minDegree) {
		this.minDegree = minDegree;
	}
	
	/**
	 * Gets max normalized closeness.
	 */
	public Double getMaxCloseness() {
		return normalizeCloseness(maxCloseness);
	}
	
	public void setMaxCloseness(Double maxC) {
		this.maxCloseness = maxC;
	}
	
	/** 
	 * Gets min normalized closeness.
	 */
	public Double getMinCloseness() {
		return normalizeCloseness(minCloseness);
	}
	
	public void setMinCloseness(Double minC) {
		this.minCloseness = minC;
	}

	/** 
	 * Gets max normalized betweenness.
	 */
	public Double getMaxBetweenness()  {
		Double value = normalizeBetweenness(maxBetweenness);
		return value;
	}
	
	/**
	 * Sets max betweeness.  Parameter must NOT be normalized.
	 */
	public void setMaxBetweenness(Double maxBetweenness) {
		this.maxBetweenness = maxBetweenness;
	}
	
	/** Gets min normalized betweenness.
	 */
	public Double getMinBetweenness() {
		return normalizeBetweenness(minBetweenness);
	}
	
	/**
	 * Sets min betweenness.  Parameter must NOT be normalized.
	 */
	public void setMinBetweenness(Double minBetweenness) {
		this.minBetweenness = minBetweenness;
	}
	
	public double getMaxWeight() {
		return maxWeight;
	}
	
	public void setMaxWeight(double maxWeight) {
		this.maxWeight = maxWeight;
	}
	
	public double getMinWeight() {
		return minWeight;
	}
	
	public void setMinWeight(double minWeight) {
		this.minWeight = minWeight;
	}
	
	public Integer getIslandHeight(GeneralNode n) {
		Integer height = islandHeightTransformer.transform(n);
		if (height == null) {
			return 0;
		}
		return islandHeightTransformer.transform(n);		
	}
	
	public Transformer<GeneralNode, Integer> getIslandHeightTransformer() {
		return islandHeightTransformer;
	}
	
	public void setIslandHeightTransformer(MapSettableTransformer<GeneralNode, Integer> iht, Map<GeneralNode, Integer>map) {
		islandHeightMap = map;
		islandHeightTransformer = iht;
	}

	public static String padRight(String s, int n) {
		return String.format("%1$-" + n + "s", s);
	}
	
	public void setIslands(List<Island> islands) {
		this.islands = islands;
	}
	
	public List<Island> getIslands() {
		return islands;
	}


	public Map<GeneralNode,double[]> getAdjacencyVectors()
	{
		Collection<GeneralNode> vertices = this.getVertices();
		List<GeneralNode> vertList = new ArrayList<GeneralNode>(vertices);
		GeneralNode refNode = this.findNodeByID("ReferenceDocument");
		if(refNode!= null )
		{
			vertList.remove(refNode);
		}
		int numVertices = vertList.size();

		Collections.sort(vertList);
		Map<GeneralNode,double[]> adjacencyVectors = new LinkedHashMap<GeneralNode,double[]>();
		for(int i=0; i<numVertices; ++i)
		{
			double[] adjList = new double[numVertices];
			for(int j=0; j<numVertices; ++j)
			{
				GeneralEdge edge = this.findEdge(vertList.get(i), vertList.get(j));
				adjList[j] = (Double) ((edge == null) ? 0 : edge.getWeight());
			}
			adjacencyVectors.put(vertList.get(i), adjList);
		}
		
		return adjacencyVectors;
	}
	
	public double[] getFlatAdjacencyVectors()
	{
		Collection<GeneralNode> vertices = this.getVertices();
		int numVertices = vertices.size();
		double[] AA = new double[numVertices * numVertices];
		
		List<GeneralNode> vertList = new ArrayList<GeneralNode>(vertices);
		Collections.sort(vertList);
		for(int i=0; i<numVertices; ++i)
		{
			for(int j=0; j<numVertices; ++j)
			{
				GeneralEdge edge = this.findEdge(vertList.get(i), vertList.get(j));
				AA[i + numVertices*j]= (Double) ((edge == null) ? 0 : edge.getWeight());
			}
		}
		
		return AA;
	}

	public Set<Object> getReports() {
		return null;
	}
	public Set<Object> getTrueReports() {
		return null;
	}
	public Set<Object> getAllReportsFromNodes() {
		return new HashSet<Object>(0);
	}
	
	public Integer getReportCount() {
		if (this.getReports() != null ) 
			return this.getReports().size();
		else
			return 0;
	}
	
	public void setMedDRACounts(Map<Object, Integer> mc) {
		return;
	}
	
	public Map<Object, Integer> getMedDRACounts() {
		return null;
	}
	
	public void setMedDRALevel(Object level) {
		return;
	}
	
	public Object getMedDRALevel() {
		return null;
	}

	
	public Integer getMaxReportCount() {
		return maxReportCount;
	}
	
	public void setMaxReportCount(Integer maxReportCount) {
		this.maxReportCount = maxReportCount;
	}

	public Integer getMinReportCount() {
		return minReportCount;
	}
	
	public void setMinReportCount(Integer minReportCount) {
		this.minReportCount = minReportCount;
	}
	
	public boolean isDual() {
		if (dualID > 0 )
			return true;
		else
			return false;
	}
	
	public boolean isClustered() {return false;};
	
	
	public String GetNetworkTypeString(int networkType){
		String strNetworkType = "Viewing";
		switch (networkType){
		case 0:
			strNetworkType = strNetworkType + " Element Network ";
			break;
		case 1: 
			strNetworkType = strNetworkType + " Report Network: VAX ";
			break;
		case 2: 
			strNetworkType = strNetworkType + " Report Network: SYM ";
			break;
		}
		return strNetworkType;
	}
	
	private void readObject(ObjectInputStream in) throws IOException{
        try {
			in.defaultReadObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
    	islandHeightTransformer = new MapSettableTransformer<GeneralNode, Integer>(islandHeightMap);
    	betweennessTransformer = new MapSettableTransformer<GeneralNode, Double>(betweennessMap);
    	closenessTransformer = new MapSettableTransformer<GeneralNode, Double>(closenessMap);;
    	strengthTransformer = new MapSettableTransformer<GeneralNode, Double>(strengthMap);
    	
    	betweennessEdgeTransformer = new MapSettableTransformer<GeneralEdge, Double>(betweennessEdgeMap);
    	islandsMstTransformer = new MapSettableTransformer<GeneralEdge, Boolean>(islandsMstMap);
    	linSimilarityEdgeTransformer = new MapSettableTransformer<GeneralEdge, Double>(new HashMap<GeneralEdge, Double>());
    }
	
	public boolean getNodeDisplay(GeneralNode node){
		if (nodeDisplayTransformer == null & nodeDisplayMap != null) {
			nodeDisplayTransformer = new MapSettableTransformer<GeneralNode, Boolean>(nodeDisplayMap);
		}
		return nodeDisplayTransformer.transform(node);
	}

	public boolean getEdgeDisplay(GeneralEdge edge){
		if (edgeDisplayTransformer == null & edgeDisplayMap != null) {
			edgeDisplayTransformer = new MapSettableTransformer<GeneralEdge, Boolean>(edgeDisplayMap);
		}
		return edgeDisplayTransformer.transform(edge);
	}
	
	public int getDisplayedNodeCount(){
		return this.getDisplayedNodeObjects().size();
	}
	public int getDisplayedEdgeCount(){
		return Math.min(this.getDisplayedEdgeObjects().size(), NetworkAnalysisVisualization.getInstance().getMaxEdgeSizeToDisplay());
	}
	
	/** Checks if islands have been calculated for this graph and asks the user if they
	 * want to start the calculation if they have not been. */
	public boolean confirmIslands() {
		if (!islandsAreCalculated) {
			NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
			if (!calculatingIslands) {
				int choice = JOptionPane.showConfirmDialog(nv,
														   "<html><p style=\"width:400px;\">The Island and Island Height information for this graph has not been calculated yet.  " +
														   "Would you like to begin this process?</p><br>" +
														   "<p style=\"width:400px;\">It is estimated to take <b>" + String.format("%1.1f", getEstimatedIslandsTime()) + " minutes or more</b> (based on a Dell Latitude E6430 laptop with an Intel Core i5-3210M 2.50GHz 2-Core CPU).</p></html>",
														   "Confirm Island Calculation",
														   JOptionPane.YES_NO_OPTION);
				if (choice == JOptionPane.YES_OPTION) {
					calculateIslands();
				}
			}
			else {
				JOptionPane.showMessageDialog(nv,
											  "<html><p style=\"width:400px;\">Island information is currently being calculated for " + getName() + ".  Please wait.",
											  "Calculation In Progress",
											  JOptionPane.INFORMATION_MESSAGE);
			}
			return false;
		}
		else {
			return true;
		}
	}
	
	public void calculateIslands() {
		// If the estimated time is very large, make the user confirm the operation again.
		if (getEstimatedIslandsTime() > 2) {
			if (JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(NetworkAnalysisVisualization.getInstance(),
																		"<html><p style=\"width:400px;\">The calculation of island heights for " + getName() + " is estimated to take <b>" + String.format("%1.1f", getEstimatedIslandsTime()) + " minutes or more</b> " +
																		"(based on a Dell Latitude E6430 laptop with an Intel Core i5-3210M 2.50GHz 2-Core CPU).  " +
																		"Are you sure you wish to proceed?</p></html>",
																		"Confirm Long Operation",
																		JOptionPane.YES_NO_CANCEL_OPTION))
				return;
		}
		
		calculatingIslands = true;
		NetworkAnalysisVisualization.logger.logp(java.util.logging.Level.INFO,"","","Started Islands Calculation for " + getName());  
		GraphIslandCalculator islandCalculator = new GraphIslandCalculator(this);
		islandCalculator.addPropertyChangeListener(new ProgressDialog("Island Calculation","Calculating Islands for " + getName()));
		NetworkExecutorService.submit(islandCalculator);
	}
	
	public double getEstimatedIslandsTime() {
		// Approximate time to calculate islands on
		// Dell Latitude E6430, Intel Core i5-3210M 2.50GHz, 4GB RAM
		//    time (in minutes) = 1.5933e-10 * (numNodes * numEdges)
		return 1.5933e-10 * getVertexCount() * getEdgeCount() * 1.3; // Increase by 30% for other overhead
	}
	
	/** Checks if betweenness/closeness have been calculated for this graph and asks
	 * the user if they want to start the calculation if they have not been. */
	public boolean confirmBetweenClose() {
		if (!betweenCloseAreCalculated) {
			NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
			if (!calculatingBetweenClose) {
				int choice = JOptionPane.showConfirmDialog(nv,
														   "<html><p style=\"width:400px;\">Betweenness and Closeness for nodes in this graph have not been calculated yet.  " +
														   "Would you like to begin this process?</p><br>" +
														   "<p style=\"width:400px;\">It is estimated to take <b>" + String.format("%1.1f", getEstimatedBetweenCloseTime()) + " minutes or more</b> (based on a Dell Latitude E6430 laptop with an Intel Core i5-3210M 2.50GHz 2-Core CPU).</p></html>",
														   "Confirm Betweenness and Closeness Calculation",
														   JOptionPane.YES_NO_OPTION);
				if (choice == JOptionPane.YES_OPTION) {
					calculateBetweenClose();
				}
			}
			else {
				JOptionPane.showMessageDialog(nv,
											  "<html><p style=\"width:400px;\">Betweenness and Closeness information is currently being calculated for " + getName() + ".  Please wait.",
											  "Calculation In Progress",
											  JOptionPane.INFORMATION_MESSAGE);
			}
			return false;
		}
		else {
			return true;
		}
	}
	
	public void calculateBetweenClose() {
		// If the estimated time is very large, make the user confirm the operation again.
		if (getEstimatedBetweenCloseTime() > 2) {
			if (JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(NetworkAnalysisVisualization.getInstance(),
																		"<html><p style=\"width:400px;\">The calculation of betweenness and closeness for " + getName() + " is estimated to take <b>" + String.format("%1.1f", getEstimatedBetweenCloseTime()) + " minutes or more</b> " +
																		"(based on a Dell Latitude E6430 laptop with an Intel Core i5-3210M 2.50GHz 2-Core CPU).  " +
																		"Are you sure you wish to proceed?</p></html>",
																		"Confirm Long Operation",
																		JOptionPane.YES_NO_CANCEL_OPTION))
				return;
		}
		
		calculatingBetweenClose = true;
		NetworkAnalysisVisualization.logger.logp(java.util.logging.Level.INFO,"","","Started Betweenness/Closeness calculation for " + getName());
		ConcurrentJobs.computeMultipleMeasures(this, new ProgressDialog("Betweenness and Closeness Calculation", "Calculating Betweenness/Closeness for " + getName()));
	}
	
	public double getEstimatedBetweenCloseTime() {
		// Approximate time to calculate betweenness/closeness on
		// Dell Latitude E6430, Intel Core i5-3210M 2.50GHz, 4GB RAM
		//    time (in minutes) = 4.438e-9 * (numNodes * numEdges)   -   Very good linear fit.
		return 4.438e-9 * getVertexCount() * getEdgeCount() * 1.3; // Increase by 30% for other overhead
	}
	
	public void setIslandsAreCalculated(boolean islandsAreCalculated) {
		this.islandsAreCalculated = islandsAreCalculated;
	}
	
	public boolean areIslandsCalculated() {
		return islandsAreCalculated;
	}
	
	public void setCalculatingIslands(boolean calculatingIslands) {
		this.calculatingIslands = calculatingIslands;
	}
	
	public boolean isCalculatingIslands() {
		return calculatingIslands;
	}
	
	public void setBetweenCloseAreCalculated(boolean betweenCloseAreCalculated) {
		this.betweenCloseAreCalculated = betweenCloseAreCalculated;
	}
	
	public boolean areBetweenCloseCalculated() {
		return betweenCloseAreCalculated;
	}
	
	public void setCalculatingBetweenClose(boolean calculatingBetweenClose) {
		this.calculatingBetweenClose = calculatingBetweenClose;
	}
	
	public boolean isCalculatingBetweenClose() {
		return calculatingBetweenClose;
	}
	
	private Set<GeneralNode> nodes = null;
	private Set<GeneralEdge> edges = null;

	public void SaveNodesEdges(){
		if(nodes==null)
			nodes.clear();
		
		nodes = new HashSet<GeneralNode>(getVertices());
		
		if(edges == null)
			edges.clear();
		
		edges = new HashSet<GeneralEdge>(getEdges());
	}
	
	public int getNumNodesOutsideComponent(VAERS_Node n) {
		Integer componentID = nodeToComponent.get(n);
		int numNodesWithinComponent = componentToNodes.get(componentID).size();
		return getVertexCount() - numNodesWithinComponent;
	}
	
	
	public void copyGraphStatistics(GeneralGraph graph) {
		this.name = graph.getName();
		this.lineage = graph.getLineage();
		this.FDAType = graph.isFDAType();
		this.multiDualsAllowed = graph.getMultiDualsAllowed();
		this.isDualGenerated = graph.getDualGenerated();
		this.layoutType = graph.getLayoutType();
		this.totalReportSize = graph.getTotalReportSize();
		this.clusters = graph.getClusters();
		this.similarity = graph.getSimilarity();
		this.similarityThreshold = graph.getSimilarityThreshold();
		this.islandsAreCalculated = graph.areIslandsCalculated();
		this.calculatingIslands = graph.isCalculatingIslands();
		this.betweenCloseAreCalculated = graph.areBetweenCloseCalculated();
		this.calculatingBetweenClose = graph.isCalculatingBetweenClose();
		this.conductance = graph.getConductance();
		this.expansion = graph.getExpansion();
		
		this.maxWeight = graph.getMaxWeight();
		this.minWeight = graph.getMinWeight();
		this.maxDegree = graph.getMaxDegree();
		this.minDegree = graph.getMinDegree();
		this.maxBetweenness = graph.maxBetweenness;
		this.minBetweenness = graph.minBetweenness;
		this.maxCloseness = graph.maxCloseness;
		this.minCloseness = graph.minCloseness;
		this.maxStrength = graph.getMaxStrength();
		this.minStrength = graph.getMinStrength();
		this.componentToNodes = graph.componentToNodes;
		this.nodeToComponent = graph.nodeToComponent;
		this.maxReportCount = graph.getMaxReportCount();
		this.minReportCount = graph.getMinReportCount();
		
		this.islandHeightTransformer = (MapSettableTransformer<GeneralNode, Integer>) graph.getIslandHeightTransformer();
		this.betweennessTransformer = graph.getBetweennessTransformer();
		this.closenessTransformer = graph.getClosenessTransformer();
		this.strengthTransformer = graph.getStrengthTransformer();
		this.betweennessEdgeTransformer = graph.getBetweennessEdgeTransformer();
		this.islandsMstTransformer = graph.getIslandsMSTTransformer();
		this.linSimilarityEdgeTransformer = graph.getLinSimilarityEdgeTransformer();
		this.nodeDisplayTransformer = graph.getNodeDisplayTransformer();
		this.edgeDisplayTransformer = graph.getEdgeDisplayTransformer();
		
		this.strengthScorer = graph.getVertexStrengthScorer();
		this.islandHeightMap = graph.islandHeightMap;
		this.betweennessMap = graph.betweennessMap;
		this.closenessMap = graph.closenessMap;
		this.strengthMap = graph.strengthMap;
		this.betweennessEdgeMap = graph.betweennessEdgeMap;
		this.islandsMstMap = graph.islandsMstMap;
		this.nodeDisplayMap = graph.nodeDisplayMap;
		this.edgeDisplayMap = graph.edgeDisplayMap;
		this.conductance = graph.conductance;
		this.internalDensity = graph.internalDensity;
		this.expansion = graph.expansion;
		this.cutRatio = graph.cutRatio;
		this.normalizedCut = graph.normalizedCut;
	}
}
