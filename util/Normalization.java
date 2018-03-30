package com.eng.cber.na.util;
public class Normalization {

	public Normalization() {

	}

	public void normalize_full1(double[] A, int n_nodes) {
		double s[];
		double a, m;
		int j, k;

		s = new double[n_nodes];

		m = 0;
		for (j = 0; j < n_nodes; j++) {
			s[j] = 0;
			for (k = 0; k < n_nodes; k++){
				s[j] += A[j + n_nodes * k];
				}
			m += s[j];
		}
		m /= 2;
		for (j = 0; j < n_nodes; j++)
			for (k = 0; k <= j; k++) {
				a = A[j + n_nodes * k] / (s[j] * (s[k] / (2 * m)));
				A[j + n_nodes * k] = a;
				A[k + n_nodes * j] = a;
			}
	}

	public void normalize_sparse1(int[] A_ir, int[] A_jc, double[] A_pr,
			int n_nodes) {
		double[] s;
		double m;
		int j, k;

		s = new double[n_nodes];
		m = 0;
		for (j = 0; j < n_nodes; j++) {
			s[j] = 0;
			for (k = A_jc[j]; k < A_jc[j + 1]; k++)
				s[j] += A_pr[k];
			m += s[j];
		}
		m /= 2;
		for (j = 0; j < n_nodes; j++)
			for (k = A_jc[j]; k < A_jc[j + 1]; k++)
				// (A_pr[k]) *= 1 / (s[j] * ((s[A_ir[k]]) / (2 * m)));
				A_pr[k] *= 1 / (s[j] * (s[A_ir[k]] / (2 * m)));

	}

	public void normalize_full2(double[] A, int n_nodes) {
		double[] p, s;
		double a;
		int j, k;

		p = new double[n_nodes];
		s = new double[n_nodes];
		for (j = 0; j < n_nodes; j++) {
			s[j] = 0;
			for (k = 0; k < n_nodes; k++)
				s[j] += (A[j + n_nodes * k]);
		}
		for (j = 0; j < n_nodes; j++) {
			p[j] = 0;
			for (k = 0; k < n_nodes; k++)
				p[j] += A[j + n_nodes * k] / s[k];
			p[j] /= n_nodes;
		}
		for (j = 0; j < n_nodes; j++)
			for (k = 0; k <= j; k++) {
				a = A[j + n_nodes * k]
						* ((1 / (s[j] * p[k]) + 1 / (s[k] * p[j])) / 2);
				A[j + n_nodes * k] = a;
				A[k + n_nodes * j] = a;
			}
	}

	public void normalize_sparse2(int[] A_ir, int[] A_jc, double[] A_pr,
			int n_nodes) {
		double[] p, s;
		int j, k;

		p = new double[n_nodes];
		s = new double[n_nodes];
		for (j = 0; j < n_nodes; j++) {
			s[j] = 0;
			for (k = A_jc[j]; k < A_jc[j + 1]; k++)
				s[j] += A_pr[k];
		}
		for (j = 0; j < n_nodes; j++) {
			p[j] = 0;
			for (k = A_jc[j]; k < A_jc[j + 1]; k++)
				p[j] += A_pr[k] / s[A_ir[k]];
			p[j] /= n_nodes;
		}
		for (j = 0; j < n_nodes; j++)
			for (k = A_jc[j]; k < A_jc[j + 1]; k++)
				A_pr[k] *= (1 / (s[j] * p[A_ir[k]]) + 1 / (s[A_ir[k]] * p[j])) / 2;
	}
}