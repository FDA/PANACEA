package com.eng.cber.na.communitydetection;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.eng.cber.na.graph.GeneralEdge;
import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.graph.GeneralNode;

/**
 * @author G. Zhang gzhang@drc.com
 *
 */
public class LouvainClusterer extends CommunityDetection implements Cloneable, Serializable{

	public LouvainClusterer (GeneralGraph gg){
		super(gg);
	}
	public ArrayList<GeneralNode> nodeArrayList; 
	double modularity, maxModularity, resolution, resolution2;
	int algorithm, i, j, modularityFunction, nClusters, nIterations, nRandomStarts;
	int[] cluster;
	long beginTime, endTime, randomSeed;
	Random random;
	String inputFileName, outputFileName;

	private int nNodes;
	public HashMap<Integer, Integer> nodeMapping; //Added a mapping between real node id and the internal node id.
	public HashMap<GeneralNode, Integer> nodeMappingObject; 
	
	private int[][] nodePerCluster;
	private boolean setRandom = false;
	LouvainNetwork network; 
	public void Init(){
		continueToCluster  = false;
		JPanel LouvainPanel = new JPanel();
		LouvainPanel.setLayout(new GridLayout(9, 1)); 
		LouvainPanel.setPreferredSize(new Dimension(800, 250));
		
		//Configure community detection algorithm: Louvain, Louvain with Multi-Level Refinement and Smart Local Moving
		JLabel lblAlgorithm= new JLabel("Algorithm");
		LouvainPanel.add(lblAlgorithm);

		JPanel JAlgorithmPanel = new JPanel();
		JRadioButton algorithm_Louvain= new JRadioButton("Louvain");
		algorithm_Louvain.setSelected(true);
		JRadioButton algorithm_LouvainMulti= new JRadioButton("Louvain with Multilevel Refinement");
		JRadioButton algorithm_SmartLocalMoving= new JRadioButton("Smart Local Moving");
		ButtonGroup bg_Algorithm =  new ButtonGroup();

		bg_Algorithm.add(algorithm_Louvain);
		bg_Algorithm.add(algorithm_SmartLocalMoving);
		bg_Algorithm.add(algorithm_LouvainMulti);
		JAlgorithmPanel.add(algorithm_Louvain);
		JAlgorithmPanel.add(algorithm_LouvainMulti);
		JAlgorithmPanel.add(algorithm_SmartLocalMoving);
		//
		LouvainPanel.add(JAlgorithmPanel);

		//Select modularity function
		JPanel JModularityPanel = new JPanel();
		JLabel lblModularity = new JLabel("Modularity Function", JLabel.LEFT);
		LouvainPanel.add(lblModularity);

		JRadioButton modularity_Standard = new JRadioButton("Standard");
		modularity_Standard.setSelected(true);
		JRadioButton modularity_Alternative = new JRadioButton("Alternative");
		JModularityPanel.add(modularity_Standard);
		JModularityPanel.add(modularity_Alternative);
		ButtonGroup bg_Modularity = new ButtonGroup();
		bg_Modularity.add(modularity_Standard);
		bg_Modularity.add(modularity_Alternative);

		LouvainPanel.add(JModularityPanel);
		//Configure resolution
		JLabel lblResolution= new JLabel("Resolution ");
		JTextField txtResolution = new JTextField("0.5");
		LouvainPanel.add(lblResolution);
		LouvainPanel.add(txtResolution);

		//Configure number of random starts
		JLabel lblRandomStart= new JLabel("Random Starts");
		JTextField txtRandomStart = new JTextField("100");
		LouvainPanel.add(lblRandomStart);
		LouvainPanel.add(txtRandomStart);

		//Configure number of iterations
		JLabel lblMaxIterations= new JLabel("Max Iterations");
		JTextField txtMaxIterations = new JTextField("100");
		LouvainPanel.add(lblMaxIterations);
		LouvainPanel.add(txtMaxIterations);

		//Configure random seeds
		JLabel lblRandomSeed= new JLabel("Random Seed");
		JTextField txtRandomSeed = new JTextField("");
		LouvainPanel.add(lblRandomSeed);
		LouvainPanel.add(txtRandomSeed);

		int result = JOptionPane.showConfirmDialog(null, LouvainPanel, "Louvain Community Detection Parameters", JOptionPane.OK_CANCEL_OPTION);

		if (result == JOptionPane.OK_OPTION){
			if (algorithm_Louvain.isSelected())
				algorithm = 1;
			if (algorithm_LouvainMulti.isSelected())
				algorithm = 2;
			if (algorithm_SmartLocalMoving.isSelected())
				algorithm = 3;

			if (modularity_Standard.isSelected())
				modularityFunction = 1;
			if (modularity_Alternative.isSelected())
				modularityFunction = 2;

			resolution = Double.parseDouble(txtResolution.getText());
			nRandomStarts = Integer.parseInt(txtRandomStart.getText());
			nIterations = Integer.parseInt(txtMaxIterations.getText());
			if (!txtRandomSeed.getText().equalsIgnoreCase ("")){
				randomSeed = Integer.parseInt(txtRandomSeed.getText());
				setRandom = true;
			}
			else{
				setRandom = false;
			}
			network = BuildLouvainNetwork();
			continueToCluster  = true;
		}
	}
	
	private LouvainNetwork BuildLouvainNetwork(){
		GeneralNode node1, node2;
		
		ArrayList<GeneralEdge> edgeArrayList_uni =  new ArrayList<GeneralEdge>(gg.getEdges());
		
		GeneralNode refNode = gg.findNodeByID("ReferenceDocument");

		ArrayList<GeneralEdge> edgeArrayList1 = new ArrayList<GeneralEdge>();
		
		GeneralEdge edge;
		
		for (int i = 0; i < edgeArrayList_uni.size(); i++ ){//get duplicated edges for undirected graph
			edge = edgeArrayList_uni.get(i);
			if(refNode!= null )
			{
				if(edge.node1.equals(refNode) || edge.node2.equals(refNode)){
					continue;
				}
			}
			
			edgeArrayList1.add(edge);
            edgeArrayList1.add(new GeneralEdge(edge.getWeight(), edge.node2, edge.node1));
		}
		
		Collections.sort(edgeArrayList1);
		ArrayList<GeneralEdge> edgeArrayList2 = new ArrayList<GeneralEdge>();
        double edgeWeight;
        double[] edgeWeight2, nodeWeight;

        edgeWeight = 0;
        node1 = new GeneralNode(-1);
        node2 = new GeneralNode(-1);
        // Take care of multi-links
        for (i = 0; i < edgeArrayList1.size(); i++)
        {
            edge = edgeArrayList1.get(i);
            if ((edge.node1.getID().equals(node1.getID())) && (edge.node2.getID().equals(node2.getID())))
                edgeWeight += edge.getWeight();
            else
            {
                if (i > 0)
                    edgeArrayList2.add(new GeneralEdge(edgeWeight, node1, node2));
                node1 = edge.node1;
                node2 = edge.node2;
                edgeWeight = edge.weight;
            }
        }
        edgeArrayList2.add(new GeneralEdge(edgeWeight, node1, node2));

        int[][] edge2;
        edge2 = new int[edgeArrayList2.size()][2];
        edgeWeight2 = new double[edgeArrayList2.size()];
        for (i = 0; i < edgeArrayList2.size(); i++)
        {
            edge = edgeArrayList2.get(i);
            edge2[i][0] = (Integer)(edge.node1.idInt);
            edge2[i][1] = (Integer)(edge.node2.idInt);
            edgeWeight2[i] = edge.getWeight();
        }

        i = -1;
        nNodes = 0;
        
        nodeMapping = new HashMap<Integer, Integer>();
        nodeMappingObject = new HashMap<GeneralNode, Integer>();
        
		nodeArrayList =  new ArrayList<GeneralNode>(gg.getVertices());
		
		if(refNode!= null )
		{
			nodeArrayList.remove(refNode);
		}
		
        Collections.sort(nodeArrayList);
        for(int i = 0; i < nodeArrayList.size(); i++){
        	nodeMapping.put((nodeArrayList.get(i).idInt), nNodes);
        	nodeMappingObject.put(nodeArrayList.get(i), nNodes);
    		nNodes=nNodes + 1;
        }
        
        if (modularityFunction == 1)
        {
            nodeWeight = new double[nNodes];
            for (i = 0; i < edge2.length; i++){
                nodeWeight[nodeMapping.get(edge2[i][0])] += edgeWeight2[i];
                //Bug in the original Louvain Java code. The weight for the neighbor node should also be counted.  
                nodeWeight[nodeMapping.get(edge2[i][1])] += edgeWeight2[i];
            }
            
            network = new LouvainNetwork(nodeMapping, edge2, edgeWeight2, nodeWeight);
        }
        else
        	network = new LouvainNetwork(nodeMapping, edge2, edgeWeight2);
		return network;
	}

	public Collection<Map<GeneralNode, Integer>>  cluster(){
		boolean update;
		clusters = new ArrayList<Map<GeneralNode, Integer>>(); 
		
		resolution2 = ((modularityFunction == 1) ? (resolution / network.getTotalEdgeWeight()) : resolution);
        cluster = null;
        nClusters = -1;
        maxModularity = Double.NEGATIVE_INFINITY;
        random = new Random(randomSeed);
        for (i = 0; i < nRandomStarts; i++)
        {
            network.initSingletonClusters();
            j = 0;
            update = true;
            do
            {
                if (algorithm == 1)
                    update = network.runLouvainAlgorithm(resolution2, random);
                else if (algorithm == 2)
                    update = network.runLouvainAlgorithmWithMultilevelRefinement(resolution2, random);
                else if (algorithm == 3)
                    network.runSmartLocalMovingAlgorithm(resolution2, random);
                j++;
            }
            while ((j < nIterations) && update);

            modularity = network.calcQualityFunction(resolution2);
            if (modularity > maxModularity)
            {
                network.orderClustersByNNodes();
                cluster = network.getClusters();
                nClusters = network.getNClusters();
                maxModularity = modularity;
                nodePerCluster = network.getNodesPerCluster();
            }
        }
       
        
        int[] nodesInCluster;
        System.out.println("Cluster size: " + nClusters);        
        for (j = 0; j < nClusters; j++){
            Map<GeneralNode, Integer> clusterMap = new HashMap<GeneralNode, Integer>();
        	nodesInCluster = nodePerCluster[j];
        	for (int k = 0; k < nodesInCluster.length; k++ ){
        		clusterMap.put(getKeyByValue(nodeMappingObject, nodesInCluster[k]), j);
        	}
    		clusters.add(clusterMap);
        }
		return clusters;	
	}
	public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
	    for (Entry<T, E> entry : map.entrySet()) {
	        if (value.equals(entry.getValue())) {
	            return entry.getKey();
	        }
	    }
	    return null;
	}
	
	@Override
	public String getName(){
		return "Louvain";
	}
	
}