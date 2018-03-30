package com.eng.cber.na.communitydetection;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.apache.commons.lang3.ArrayUtils;

import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.graph.GeneralNode;
import com.eng.cber.na.util.Normalization;

/**
 * @author G. Zhang gzhang@drc.com
 *
 */
public class VOSClusterer extends CommunityDetection {
	public VOSClusterer(GeneralGraph g){
		super(g);
	}
	int normalization = 0; // '0' for standard, '1' for alternative, '2' for no normalization
	int n_nodes = 0;
	double resolution = 1; 
	int nRandomStarts = 100;
	int randomSeed;
	boolean setRandom = false;
	ArrayList<Double> A_prAL = new ArrayList<Double>();
	ArrayList<Integer> A_irAL = new ArrayList<Integer>();
	ArrayList<Integer> A_jcAL = new ArrayList<Integer>();
	
	public void Init(){
		continueToCluster  = false;
		JPanel VOSPanel = new JPanel();
		VOSPanel.setLayout(new GridLayout(9, 1)); 
		VOSPanel.setPreferredSize(new Dimension(800, 250));
		
		//Configure normalization method: '0' for standard, '1' for alternative, '2' for no normalization):
		JLabel lblNormalization= new JLabel("Normalization");
		VOSPanel.add(lblNormalization);
		
		JPanel JNormalizationPanel = new JPanel();
		JRadioButton normalizationStandard = new JRadioButton("Standard");
		JRadioButton normalizationAlternative = new JRadioButton("Alternative");
		JRadioButton normalizationNone= new JRadioButton("None");
		normalizationNone.setSelected(true);
		ButtonGroup bg_Algorithm =  new ButtonGroup();

		bg_Algorithm.add(normalizationStandard);
		bg_Algorithm.add(normalizationAlternative);
		bg_Algorithm.add(normalizationNone);
		JNormalizationPanel.add(normalizationStandard);
		JNormalizationPanel.add(normalizationAlternative);
		JNormalizationPanel.add(normalizationNone);
		
		VOSPanel.add(JNormalizationPanel);

		//Configure resolution
		JLabel lblResolution= new JLabel("Resolution ");
		JTextField txtResolution = new JTextField("0.15");
		VOSPanel.add(lblResolution);
		VOSPanel.add(txtResolution);

		//Configure number of random starts
		JLabel lblRandomStart= new JLabel("Random Starts");
		JTextField txtRandomStart = new JTextField("100");
		VOSPanel.add(lblRandomStart);
		VOSPanel.add(txtRandomStart);

		//Configure random seeds
		JLabel lblRandomSeed= new JLabel("Random Seed");
		JTextField txtRandomSeed = new JTextField("");
		VOSPanel.add(lblRandomSeed);
		VOSPanel.add(txtRandomSeed);

		int result = JOptionPane.showConfirmDialog(null, VOSPanel, "VOS Detection Parameters", JOptionPane.OK_CANCEL_OPTION);
		
		if (result == JOptionPane.OK_OPTION){
			if (normalizationStandard.isSelected())
				normalization = 1;
			if (normalizationAlternative.isSelected())
				normalization = 2;
			if (normalizationNone.isSelected())
				normalization = 3;

			resolution = Double.parseDouble(txtResolution.getText());
			nRandomStarts = Integer.parseInt(txtRandomStart.getText());
			if (!txtRandomSeed.getText().equalsIgnoreCase ("")){
				randomSeed = Integer.parseInt(txtRandomSeed.getText());
				setRandom = true;
			}
			else{
				setRandom = false;
			}
			
			// Based on adjacent matrix
			adjVectors = gg.getAdjacencyVectors();

			//Convert to data format required by VOS code: flatten matrix to vector 
			ArrayList<Double> A = ExtractAdjMatrix(adjVectors);
			n_nodes = adjVectors.size();

			read_adjMatrix_to_sparse(A, A_irAL,
					A_jcAL, A_prAL, n_nodes );
			//A_irAL: vector for locating the first node
			//A_jcAL: vector indicating second node
			//A_prAL: weight

			A_irAL.trimToSize();
			A_jcAL.trimToSize();
			A_prAL.trimToSize();
			continueToCluster  = true;
		}
		return ;
	}
	
	public Collection<Map<GeneralNode, Integer>>  cluster()
	{
		double max_V;
		int[] node_weights;

		double[] A_pr;
		int[] A_ir, A_jc;
		int i; 

		A_pr = new double[A_prAL.size()];
		A_ir = new int[A_irAL.size()];
		A_jc = new int[A_jcAL.size()];
		
		// Convert arraylist to array
		for (i = 0; i < A_prAL.size(); i++)
			A_pr[i] = A_prAL.get(i);
		for (i = 0; i < A_irAL.size(); i++)
			A_ir[i] = A_irAL.get(i);
		for (i = 0; i < A_jcAL.size(); i++)
			A_jc[i] = A_jcAL.get(i);

		Normalization normalize = new Normalization();
		
		if (normalization == 1) {
			normalize.normalize_sparse1(A_ir, A_jc, A_pr, n_nodes);

		} else if (normalization == 2) {
			normalize.normalize_sparse2(A_ir, A_jc, A_pr, n_nodes);
		}

		node_weights = new int[n_nodes];
		for (i = 0; i < n_nodes; i++)
			node_weights[i] = 1;

		int[] X = new int[n_nodes];
		int[] best_X = new int[n_nodes];
		int tmp = 0;

		max_V = -Double.MAX_VALUE;
		ClusteringFunctions clusteringFunctions = new ClusteringFunctions();
		double V;
		int best_n_clusters = 0, n_clusters;
		
		for (int p = 0; p < nRandomStarts; p++) {

			V = clusteringFunctions.run_clustering_optimization(A_ir, A_jc, A_pr,
					node_weights, n_nodes, resolution, X, true);

			n_clusters = 0;
			for (i = 0; i < n_nodes; i++)
				if (X[i] + 1 > n_clusters)
					n_clusters = X[i] + 1;

			System.out.println("Solution found");
			System.out.println("Quality function: " + V);
			System.out.println("Number of clusters: " + n_clusters);

			if (V > max_V) {
				for (i = 0; i < n_nodes; i++) {
					tmp = best_X[i];
					best_X[i] = X[i];
					X[i] = tmp;
				}

				best_n_clusters = n_clusters;
				max_V = V;
			}

		}

		clusteringFunctions.sort_clusters(best_X, n_nodes);

		System.out.println("Best solution found in %i random starts: "
				+ nRandomStarts);
		System.out.println("Quality function: " + max_V);
		System.out.println("Number of clusters: " + best_n_clusters);
		
		clusters = new ArrayList<Map<GeneralNode, Integer>>(); 

		Collection<GeneralNode> vertices = gg.getVertices();
		List<GeneralNode> vertList = new ArrayList<GeneralNode>(vertices);
		Collections.sort(vertList);
		for (int j = 0; j < best_n_clusters; j++){
            Map<GeneralNode, Integer> clusterMap = new HashMap<GeneralNode, Integer>();
            for( int k = 0; k < best_X.length; k++ ){
            	if (best_X[k] == j){
            		clusterMap.put(vertList.get(k), j);
            	}
            }
    		clusters.add(clusterMap);
        }
		
		return clusters;
	}

	public void read_adjMatrix_to_sparse(ArrayList<Double> A,
			ArrayList<Integer> A_ir, ArrayList<Integer> A_jc,
			ArrayList<Double> A_pr, int n_nodes) {

		int n_elements;

		n_elements = 0;
		for (int j = 0; j < n_nodes; j++) {
			A_jc.add(j, n_elements);

			for (int k = 0; k < n_nodes; k++) {
				if (A.get(j + n_nodes * k) > 0.0) {
					A_ir.add(n_elements, k);
					A_pr.add(n_elements, A.get(j + n_nodes * k));
					n_elements++;
				}
			}
		}
		A_jc.add(n_nodes, n_elements);

		A_ir.trimToSize();
		A_pr.trimToSize();
		A_jc.trimToSize();

	}	
	public ArrayList<Double> ExtractAdjMatrix(Map<GeneralNode, double[]> adjVectors){
		ArrayList<Double> A = new ArrayList<Double>();
		double[] value;
		
		for (Map.Entry<GeneralNode, double[]> entry: adjVectors.entrySet()){
			value = entry.getValue();

			A.addAll(Arrays.asList(ArrayUtils.toObject(value)));
		}
		return A;
	}
	@Override
	public String getName(){
		return "VOS";
	}
}
