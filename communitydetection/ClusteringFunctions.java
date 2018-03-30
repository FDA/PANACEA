package com.eng.cber.na.communitydetection;
import java.util.Arrays;
import java.util.Random;

public class ClusteringFunctions {
	int MAX_N_COARSENING_LEVELS = 100;
	int RAND_MAX = 32767;

	Random rand = new Random(0);
	class Coarsening_level_data {
		int[] A_ir;
		int[] A_jc;
		double[] A_pr;
		int[] node_weights;
		int[] X;
		int n_nodes;
		int[] cluster_weights;
		int[] n_nodes_per_cluster;
		int n_clusters;
	}

	public void run_local_moving_algorithm(
			Coarsening_level_data coarsening_level_data, double resolution) {
		double[] n_links_per_cluster;
		double max_V, V;
		int[] cluster_weights, new_cluster_numbers, node_order, related_clusters, unused_clusters;
		int best_cluster, i, j, k, l, n_related_clusters, n_stable_nodes, n_unused_clusters;

		cluster_weights = new int[coarsening_level_data.n_nodes];
 
		for (i = 0; i < coarsening_level_data.n_nodes; i++)
			cluster_weights[i] = 0;
		for (i = 0; i < coarsening_level_data.n_nodes; i++)
			cluster_weights[coarsening_level_data.X[i]] += coarsening_level_data.node_weights[i];

		unused_clusters = new int[coarsening_level_data.n_nodes];
		n_unused_clusters = 0;

		for (i = 0; i < coarsening_level_data.n_nodes; i++)
			if (cluster_weights[i] == 0) {
				unused_clusters[n_unused_clusters] = i;
				n_unused_clusters++;
			}

		node_order = new int[coarsening_level_data.n_nodes];
		for (i = 0; i < coarsening_level_data.n_nodes; i++)
			node_order[i] = i;
		for (i = 0; i < coarsening_level_data.n_nodes - 1; i++) {
			
			j = coarsening_level_data.n_nodes
					- (int) Math
							.floor((coarsening_level_data.n_nodes - i)
									* ((rand.nextDouble() * RAND_MAX) / (RAND_MAX + 1.0)))
					- 1;
 
			k = node_order[i];
			node_order[i] = node_order[j];
			node_order[j] = k;
		}

		n_links_per_cluster = new double[coarsening_level_data.n_nodes];

		for (i = 0; i < coarsening_level_data.n_nodes; i++)
			n_links_per_cluster[i] = 0;

		if(coarsening_level_data.n_nodes < 1){
			System.out.println(coarsening_level_data.n_nodes);
		}
		related_clusters = new int[coarsening_level_data.n_nodes - 1];

		n_stable_nodes = 0;
		j = 0;
		do {
			i = node_order[j];

			n_related_clusters = 0;
			for (k = coarsening_level_data.A_jc[i]; k < coarsening_level_data.A_jc[i + 1]; k++) {
				l = coarsening_level_data.X[coarsening_level_data.A_ir[k]];
				if (n_links_per_cluster[l] == 0) {
					related_clusters[n_related_clusters] = l;
					n_related_clusters++;
				}
				n_links_per_cluster[l] += coarsening_level_data.A_pr[k];
			}

			cluster_weights[coarsening_level_data.X[i]] -= coarsening_level_data.node_weights[i];
			if (cluster_weights[coarsening_level_data.X[i]] == 0) {
				unused_clusters[n_unused_clusters] = coarsening_level_data.X[i];
				n_unused_clusters++;
			}
			best_cluster = -1;
			max_V = 0;
			for (k = 0; k < n_related_clusters; k++) {
				l = related_clusters[k];
				V = n_links_per_cluster[l]
						- ((double) coarsening_level_data.node_weights[i])
						* ((double) cluster_weights[l]) * resolution;
				if ((V > max_V) || ((V == max_V) && (l < best_cluster))) {
					best_cluster = l;
					max_V = V;
				}
				n_links_per_cluster[l] = 0;
			}
			if (max_V == 0) {
				best_cluster = unused_clusters[n_unused_clusters - 1];
				n_unused_clusters--;
			}
			cluster_weights[best_cluster] += coarsening_level_data.node_weights[i];
			if (best_cluster == coarsening_level_data.X[i])
				n_stable_nodes++;
			else {
				coarsening_level_data.X[i] = best_cluster;
				n_stable_nodes = 1;
			}

			j = (j < coarsening_level_data.n_nodes - 1) ? (j + 1) : 0;
		} while (n_stable_nodes < coarsening_level_data.n_nodes);

		coarsening_level_data.n_clusters = coarsening_level_data.n_nodes
				- n_unused_clusters;
		new_cluster_numbers = new int[coarsening_level_data.n_nodes];
		j = 0;
		for (i = 0; i < coarsening_level_data.n_nodes; i++)
			if (cluster_weights[i] > 0) {
				new_cluster_numbers[i] = j;
				coarsening_level_data.cluster_weights[j] = cluster_weights[i];
				j++;
			}
		for (i = 0; i < coarsening_level_data.n_clusters; i++)
			coarsening_level_data.n_nodes_per_cluster[i] = 0;
		for (i = 0; i < coarsening_level_data.n_nodes; i++) {
			j = new_cluster_numbers[coarsening_level_data.X[i]];
			coarsening_level_data.X[i] = j;
			coarsening_level_data.n_nodes_per_cluster[j]++;
		}

	}

	public void run_multilevel_local_search_algorithm(int[] A_ir, int[] A_jc,
			double[] A_pr, int[] node_weights, int n_nodes, double resolution,
			int[] X, boolean print_output) {
		
		double[] n_links_per_cluster;

		int iteration, n_coarsening_levels, p;

		int[] node_order, node_order_index, related_clusters;

		int i, j, k, l, m, max_n_linked_node_pairs, max_n_linked_node_pairs1, max_n_linked_node_pairs2, n, n_related_clusters;

		Coarsening_level_data[] coarsening_level_data;
		Coarsening_level_data[] coarsening_level_data_current_level, coarsening_level_data_next_level;
 
		coarsening_level_data = new Coarsening_level_data[MAX_N_COARSENING_LEVELS];
 
		for (i = 0; i < MAX_N_COARSENING_LEVELS; i++)
			coarsening_level_data[i] = new Coarsening_level_data();
	 
		coarsening_level_data_current_level = coarsening_level_data;

		coarsening_level_data_current_level[0].A_ir = A_ir;
		coarsening_level_data_current_level[0].A_jc = A_jc;
		coarsening_level_data_current_level[0].A_pr = A_pr;
		coarsening_level_data_current_level[0].node_weights = node_weights;
		coarsening_level_data_current_level[0].X = X;
		coarsening_level_data_current_level[0].n_nodes = n_nodes;
		coarsening_level_data_current_level[0].cluster_weights = new int[n_nodes]; 
 
		coarsening_level_data_current_level[0].n_nodes_per_cluster = new int[n_nodes];    
		
		for (i = 0; i < n_nodes; i++) coarsening_level_data_current_level[0].X[i] = i;


		run_local_moving_algorithm(coarsening_level_data_current_level[0],
				resolution);

		node_order = new int[n_nodes]; 
		node_order_index = new int[n_nodes];  
		n_links_per_cluster = new double[n_nodes];  
		related_clusters = new int[n_nodes - 1]; 

		iteration = 1;
		do {

			 n_coarsening_levels = 1;
			while ((coarsening_level_data_current_level[0].n_clusters < coarsening_level_data_current_level[0].n_nodes)
					&& (n_coarsening_levels < MAX_N_COARSENING_LEVELS)) {
 
				coarsening_level_data_next_level = Arrays.copyOfRange(
						coarsening_level_data, n_coarsening_levels,
						coarsening_level_data.length);

				n_coarsening_levels++;
				max_n_linked_node_pairs1 = coarsening_level_data_current_level[0].A_jc[coarsening_level_data_current_level[0].n_nodes];
				max_n_linked_node_pairs2 = (coarsening_level_data_current_level[0].n_clusters <= 46341) ? (coarsening_level_data_current_level[0].n_clusters * (coarsening_level_data_current_level[0].n_clusters - 1))
						: Integer.MAX_VALUE;
				max_n_linked_node_pairs = (max_n_linked_node_pairs1 < max_n_linked_node_pairs2) ? max_n_linked_node_pairs1
						: max_n_linked_node_pairs2;
				coarsening_level_data_next_level[0].A_ir = new int[max_n_linked_node_pairs]; 
																							 
				coarsening_level_data_next_level[0].A_jc = new int[coarsening_level_data_current_level[0].n_clusters + 1]; 
																															 																	 
				coarsening_level_data_next_level[0].A_pr = new double[max_n_linked_node_pairs]; 
																							 
				
				
				coarsening_level_data_next_level[0].node_weights = coarsening_level_data_current_level[0].cluster_weights;
				
				
				coarsening_level_data_next_level[0].X = new int[coarsening_level_data_current_level[0].n_clusters]; 
																												 
																													 
				coarsening_level_data_next_level[0].n_nodes = coarsening_level_data_current_level[0].n_clusters; 
																											 
				coarsening_level_data_next_level[0].cluster_weights = new int[coarsening_level_data_current_level[0].n_clusters];  
				coarsening_level_data_next_level[0].n_nodes_per_cluster = new int[coarsening_level_data_current_level[0].n_clusters];
 
				node_order_index[0] = 0;
				for (i = 1; i < coarsening_level_data_current_level[0].n_clusters; i++)
					node_order_index[i] = node_order_index[i - 1]
							+ coarsening_level_data_current_level[0].n_nodes_per_cluster[i - 1];
				for (i = 0; i < coarsening_level_data_current_level[0].n_nodes; i++) {
 
					node_order[node_order_index[coarsening_level_data_current_level[0].X[i]]] = i;
 
					node_order_index[coarsening_level_data_current_level[0].X[i]]++;
				}

				for (i = 0; i < coarsening_level_data_current_level[0].n_clusters; i++)
					n_links_per_cluster[i] = 0;

				j = 0;
				k = 0;
				coarsening_level_data_next_level[0].A_jc[0] = 0;
				for (i = 0; i < coarsening_level_data_current_level[0].n_clusters; i++) {
					n_related_clusters = 0;
					for (l = 0; l < coarsening_level_data_current_level[0].n_nodes_per_cluster[i]; l++) {
						for (m = coarsening_level_data_current_level[0].A_jc[node_order[j]]; m < coarsening_level_data_current_level[0].A_jc[node_order[j] + 1]; m++) {
							n = coarsening_level_data_current_level[0].X[coarsening_level_data_current_level[0].A_ir[m]];
							if (n != i) {
								if (n_links_per_cluster[n] == 0) {
									related_clusters[n_related_clusters] = n;
									n_related_clusters++;
								}
								n_links_per_cluster[n] += coarsening_level_data_current_level[0].A_pr[m];
							}
						}
						j++;
					}

					for (l = 0; l < n_related_clusters; l++) {
						m = related_clusters[l];
						coarsening_level_data_next_level[0].A_ir[k] = m;
						coarsening_level_data_next_level[0].A_pr[k] = n_links_per_cluster[m];
						n_links_per_cluster[m] = 0;
						k++;
					}
					coarsening_level_data_next_level[0].A_jc[i + 1] = k;

					coarsening_level_data_next_level[0].X[i] = i;
				}


				coarsening_level_data_current_level = coarsening_level_data_next_level;

				run_local_moving_algorithm(
						coarsening_level_data_current_level[0], resolution);
			}

			for (p = n_coarsening_levels - 3; p >= 0; p--) {
 
				coarsening_level_data_current_level = Arrays.copyOfRange(
						coarsening_level_data, p, coarsening_level_data.length);
				for (i = 0; i < coarsening_level_data_current_level[0].n_nodes; i++)
 
					coarsening_level_data_current_level[0].X[i] = coarsening_level_data_current_level[1].X[coarsening_level_data_current_level[0].X[i]];


				run_local_moving_algorithm(
						coarsening_level_data_current_level[0], resolution);
			}

			iteration++;
		} while (n_coarsening_levels >= 3);
	}

	public double calc_quality_function(int[] A_ir, int[] A_jc, double[] A_pr,
			int[] node_weights, int n_nodes, double resolution, int[] X) {
		int[] cluster_weights;
		int i, j, n_clusters;
		double V;
		V = 0;
		i = 0;
		n_clusters = 0;
		for (j = 0; j < n_nodes; j++) {
			while (i < A_jc[j + 1]) {
				if (X[A_ir[i]] == X[j])
					V += A_pr[i];
				i++;
			}
			if (X[j] + 1 > n_clusters)
				n_clusters = X[j] + 1;
		}

		cluster_weights = new int[n_clusters]; 
											 
		for (i = 0; i < n_clusters; i++)
			cluster_weights[i] = 0;
		for (i = 0; i < n_nodes; i++) {
			V += ((double) node_weights[i] * (double) node_weights[i])
					* resolution;
			cluster_weights[X[i]] += node_weights[i];
		}
		for (i = 0; i < n_clusters; i++)
			V -= ((double) cluster_weights[i])
					* ((double) cluster_weights[i]) * resolution;
		return V;
	}

	public double run_clustering_optimization(int[] A_ir, int[] A_jc,
			double[] A_pr, int[] node_weights, int n_nodes, double resolution,
			int[] X, boolean print_output) {
		run_multilevel_local_search_algorithm(A_ir, A_jc, A_pr, node_weights,
				n_nodes, resolution, X, print_output);
		double V = calc_quality_function(A_ir, A_jc, A_pr, node_weights, n_nodes,
				resolution, X);
		return V;
	}

	public void sort_clusters(int[] X, int n_nodes) {
		int update;
		int[] a, b, cluster_size;
		int i, j, n_clusters, temp;

		n_clusters = 0;
		for (i = 0; i < n_nodes; i++)
			if (X[i] + 1 > n_clusters)
				n_clusters = X[i] + 1;

		a = new int[n_clusters];
		b = new int[n_clusters];

		cluster_size = new int[n_clusters];

		for (i = 0; i < n_clusters; i++) {
			a[i] = i;
			cluster_size[i] = 0;
		}

		for (i = 0; i < n_nodes; i++)
			cluster_size[X[i]]++;

		i = n_clusters - 1;
		do {
			update = 0;
			for (j = 0; j < i; j++)
				if (cluster_size[j] < cluster_size[j + 1]) {
					temp = cluster_size[j];
					cluster_size[j] = cluster_size[j + 1];
					cluster_size[j + 1] = temp;
					temp = a[j];
					a[j] = a[j + 1];
					a[j + 1] = temp;
					update = 1;
				}
			i--;
		} while (update == 1);

		for (i = 0; i < n_clusters; i++)
			b[a[i]] = i;
		for (i = 0; i < n_nodes; i++)
			X[i] = b[X[i]];
	}

}
