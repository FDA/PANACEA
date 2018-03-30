package com.eng.cber.na.communitydetection;

import org.netlib.blas.Dgemm;
import org.netlib.lapack.Dgesvd;
import org.netlib.lapack.Dgetrf;
import org.netlib.lapack.Dgetri;
import org.netlib.util.intW;

public class MappingFunctions {

	public MappingFunctions() {

	}

	public void calc_inverse(double[] A, int n_nodes, double[] inverse) {

		double[] work;
		double a;

		int[] ipiv;
		intW info = new intW(0);

		int lwork;

		for (int j = 0; j < n_nodes; j++)
			inverse[j + n_nodes * j] = 1;

		for (int j = 0; j < n_nodes; j++)
			for (int k = 0; k < j; k++) {
				a = A[j + n_nodes * k];
				
				inverse[j + n_nodes * k] = 1 - a;
				inverse[k + n_nodes * j] = 1 - a;
				inverse[j + n_nodes * j] += a;
				inverse[k + n_nodes * k] += a;
			}

		ipiv = new int[n_nodes];
		lwork = 64 * n_nodes;

		work = new double[lwork];

		Dgetrf.dgetrf(n_nodes, n_nodes, inverse, 0, n_nodes, ipiv, 0, info);

		Dgetri.dgetri(n_nodes, inverse, 0, n_nodes, ipiv, 0, work, 0,
				work.length, info);

		a = 1.0 / (n_nodes * n_nodes);
		for (int j = 0; j < n_nodes; j++)
			for (int k = 0; k < n_nodes; k++)
				inverse[j + n_nodes * k] -= a;

	}

	public void calc_distances(double[] A, double[] X, int n_nodes,
			int n_dimensions, double[] B, double[] V) {
		double difference, distance, distance_reciprocal, squared_distance;
		int p;
		int j, k;

		for (j = 0; j < n_nodes; j++)
			B[j + n_nodes * j] = 0;
		V[0] = 0;
		for (j = 0; j < n_nodes; j++)
			for (k = 0; k < j; k++) {
				squared_distance = 0;
				for (p = 0; p < n_dimensions; p++) {
					difference = X[j + n_nodes * p] - X[k + n_nodes * p];
					squared_distance += difference * difference;
				}
				distance = Math.sqrt(squared_distance);
				distance_reciprocal = 1 / distance;
				B[j + n_nodes * k] = -distance_reciprocal;
				B[k + n_nodes * j] = -distance_reciprocal;
				B[j + n_nodes * j] += distance_reciprocal;
				B[k + n_nodes * k] += distance_reciprocal;
				V[0] -= A[j + n_nodes * k] * squared_distance - 2 * distance;

			}
	}

	public void multiply_matrices(double[] A, boolean transpose_A, double[] B,
			boolean transpose_B, int m, int n, int p, double[] C) {

		double one, zero;
		String TRANS_A1, TRANS_B1;
		int TRANS_A2, TRANS_B2;

		zero = 0;
		one = 1;

		TRANS_A1 = (transpose_A == true) ? "T" : "N";
		TRANS_B1 = (transpose_B == true) ? "T" : "N";

		TRANS_A2 = (transpose_A == true) ? n : m;
		TRANS_B2 = (transpose_B == true) ? p : n;

		Dgemm.dgemm(TRANS_A1, TRANS_B1, m, p, n, one, A, 0, TRANS_A2, B, 0,
				TRANS_B2, zero, C, 0, m);
	}

	public void run_mapping_optimization(double[] A, double[] inverse,
			double[] X, int n_nodes, int n_dimensions, int max_n_iter,
			double convergence, boolean print_output, double[] V) {

		double old_V;
		int i;
		double[] B = new double[n_nodes * n_nodes];

		double[] BX = new double[n_nodes * n_dimensions];

		i = 0;
		old_V = -Double.MAX_VALUE;

		calc_distances(A, X, n_nodes, n_dimensions, B, V);

		while ((i < max_n_iter)
				&& ((V[0] - old_V) / Math.abs(V[0]) >= convergence)) {
			i++;

			multiply_matrices(B, false, X, false, n_nodes, n_nodes,
					n_dimensions, BX);
			multiply_matrices(inverse, false, BX, false, n_nodes, n_nodes,
					n_dimensions, X);
			old_V = V[0];

			calc_distances(A, X, n_nodes, n_dimensions, B, V);

		}

	}

	public void translate_and_dilate_map(double[] X, int n_nodes,
			int n_dimensions) {
		double difference, mean_distance, mean_X, squared_distance;
		int p;
		int j, k;

		mean_distance = 0;
		for (j = 0; j < n_nodes; j++)
			for (k = 0; k < j; k++) {
				squared_distance = 0;
				for (p = 0; p < n_dimensions; p++) {
					difference = X[j + n_nodes * p] - X[k + n_nodes * p];
					squared_distance += difference * difference;
				}
				mean_distance += Math.sqrt(squared_distance);
			}
		mean_distance /= (n_nodes * (n_nodes - 1)) / 2;
		for (p = 0; p < n_dimensions; p++) {
			mean_X = 0;
			for (j = 0; j < n_nodes; j++) {
				X[j + n_nodes * p] /= mean_distance;
				mean_X += X[j + n_nodes * p];
			}
			mean_X /= n_nodes;
			for (j = 0; j < n_nodes; j++)
				X[j + n_nodes * p] -= mean_X;
		}
	}

	public void rotate_map(double[] X, int n_nodes, int n_dimensions) {
		double[] work;

		int max, min;
		int lwork;
		intW info = new intW(0);

		if (n_nodes >= n_dimensions) {
			min = n_dimensions;
			max = n_nodes;
		} else {
			min = n_nodes;
			max = n_dimensions;
		}

		lwork = 5 * max;

		work = new double[lwork];

		double[] S = new double[min];
		double[] U = new double[n_nodes * min];
		double[] VT = new double[n_dimensions * n_dimensions];
		double[] X_copy = new double[n_nodes * n_dimensions];

		for (int j = 0; j < n_nodes; j++)
			for (int p = 0; p < n_dimensions; p++)
				X_copy[j + n_nodes * p] = X[j + n_nodes * p];

		Dgesvd.dgesvd("S", "A", n_nodes, n_dimensions, X, 0, n_nodes, S, 0, U,
				0, n_nodes, VT, 0, n_dimensions, work, 0, work.length, info);

		multiply_matrices(X_copy, false, VT, true, n_nodes, n_dimensions,
				n_dimensions, X);
	}

	public void reflect_map(double[] X, int n_nodes, int n_dimensions) {
		double max_X, min_X, x;
		int p;
		int j, max_X_index, min_X_index;

		for (p = 0; p < n_dimensions; p++) {
			min_X = X[n_nodes * p];
			min_X_index = 0;
			max_X = min_X;
			max_X_index = 0;
			for (j = 1; j < n_nodes; j++) {
				x = X[j + n_nodes * p];
				if (x < min_X) {
					min_X = x;
					min_X_index = j;
				} else if (x > max_X) {
					max_X = x;
					max_X_index = j;
				}
			}
			if (min_X_index > max_X_index)
				for (j = 0; j < n_nodes; j++)
					X[j + n_nodes * p] *= -1;
		}
	}
}
