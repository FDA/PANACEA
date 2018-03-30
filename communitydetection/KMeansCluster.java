package com.eng.cber.na.communitydetection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.graph.GeneralNode;

import edu.uci.ics.jung.algorithms.util.KMeansClusterer;

/**
 * @author G. Zhang gzhang@drc.com
 *
 */
public class KMeansCluster extends CommunityDetection {
	private transient KMeansClusterer<GeneralNode> kMeans;
	public KMeansCluster(GeneralGraph g){
		super(g);
	}
	
	public void Init(){
		continueToCluster  = false;
		try
		{
			adjVectors = gg.getAdjacencyVectors();
		}
		catch(Exception ex)
		{
			System.err.println("EX:"+ex);
			ex.printStackTrace();
		}
		
		int maxIterations = 100;
		double convergenceThreshold = 0.001;
		int randomSeed = 0;
		boolean setRandom = false;

		JTextField txtClusterSize = new JTextField("2");
		JTextField txtMaxIterations = new JTextField("100");
		JTextField txtConvergenceThreshold= new JTextField("0.001");
		JTextField txtRandomSeed = new JTextField("");


		Object[] message={"Enter Number of Clusters: ", txtClusterSize, "Enter Maximum Iterations: ", txtMaxIterations, 
				"Enter Convergence Threshold: ", txtConvergenceThreshold, "Random Seed (Optional): ", txtRandomSeed};

		int result = JOptionPane.showConfirmDialog(null, message, "K-Means Community Detection Parameters", JOptionPane.OK_CANCEL_OPTION);

		if (result == JOptionPane.OK_OPTION){
			maxIterations = Integer.parseInt(txtMaxIterations.getText());
			numClusters = Integer.parseInt(txtClusterSize.getText());
			convergenceThreshold = Double.parseDouble(txtConvergenceThreshold.getText());
			
			if (!txtRandomSeed.getText().equalsIgnoreCase ("")){
				randomSeed = Integer.parseInt(txtRandomSeed.getText());
				setRandom = true;
			}
			else{
				setRandom = false;
			}


			kMeans = new KMeansClusterer(maxIterations, convergenceThreshold);
			kMeans.setMaxIterations(maxIterations);
			kMeans.setConvergenceThreshold(convergenceThreshold);

			if (setRandom){
				kMeans.setSeed(randomSeed);
			}
			continueToCluster  = true;
		}
		return ;
	}
	
	public Collection<Map<GeneralNode, Integer>>  cluster()
	{
		try
		{
			clusters = new ArrayList<Map<GeneralNode, Integer>>();
			
			Collection<Map<GeneralNode, double[]>> temp;
			temp =kMeans.cluster(adjVectors, numClusters);
			Iterator<Map<GeneralNode, double[]>> it = temp.iterator();
			int clusterID = 0;
			while(it.hasNext()){
				Map<GeneralNode, double[]> tempMap = it.next();
				Map<GeneralNode, Integer> clusterIDs = new HashMap<GeneralNode, Integer>();
				for(Entry<GeneralNode, double[]> e:tempMap.entrySet()){
					clusterIDs.put(e.getKey(), clusterID);
				}
				clusterID = clusterID + 1;
				clusters.add(clusterIDs);
			}
		}
		catch(Exception ex)
		{
			System.err.println("EX:"+ex);
			ex.printStackTrace();
		}
		return clusters;
	}
	
	@Override
	public String getName(){
		return "KMeans";
	}
}
