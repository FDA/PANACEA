package com.eng.cber.na.math;

import Jama.Matrix;

/** 
 * Computes the largest eigenvector-eigenvalue pair
 * in the input matrix using the Power method.
 * 
 * This is very efficient when only a few eigenvectors
 * are desired.
 *
 */
public class LargestEigenvalueDecomposition {
	Matrix dataMatrix;
	
	private double eigenvalue;
	private Matrix eigenvector;
	
	public LargestEigenvalueDecomposition(double[][] vals) {
		dataMatrix = new Matrix(vals);
		calculateEigen();
	}
	
	public LargestEigenvalueDecomposition(Matrix vals) {
		dataMatrix = vals;
		calculateEigen();
	}
	
	private void calculateEigen() {
		int maxNumPasses = 100;
		int passes = 0;
		double threshold = 0.000001;

		// Create an estimated eigenvector that initially
		// matches the first column of the matrix
		Matrix inprogressEigenvector = new Matrix(dataMatrix.getRowDimension(), 1);
		for (int r = 0; r < dataMatrix.getRowDimension(); r++) {
			double val = dataMatrix.get(r,0);
			inprogressEigenvector.set(r, 0, val);
		}
		
		double[] e = new double[dataMatrix.getRowDimension()];
		double[] eStar = new double[dataMatrix.getRowDimension()];
		double lambda = 0;
		double lastLambda = -1;
		
		// Initialize eigenvector guess
		for (int i = 0; i < dataMatrix.getRowDimension(); i++) {
			e[i] = 1;
		}
		
		while ((Math.abs(lambda - lastLambda) > threshold) && (passes < maxNumPasses)) {
			passes++;
			
			// Create the adjusted eigenvector
			for (int i = 0; i < dataMatrix.getRowDimension(); i++){
				eStar[i] = 0;
				for (int j = 0; j < dataMatrix.getRowDimension(); j++) {
					eStar[i] += dataMatrix.get(i, j) * e[j];
				}
			}
			
			// Sum the lambda
			double lambdaSum = 0;
			for (int i = 0; i < dataMatrix.getRowDimension(); i++) {
				lambdaSum += (eStar[i] * eStar[i]);
			}
			
			lastLambda = lambda;
			lambda = Math.sqrt(lambdaSum);
			
			for (int i = 0; i < dataMatrix.getRowDimension(); i ++) {
				e[i] = eStar[i]/lambda;
			}
		}
		
		eigenvalue = lambda;
		eigenvector = new Matrix(e, dataMatrix.getRowDimension());

	}
	
	public double getEigenvalue() {
		return eigenvalue;
	}
	
	public Matrix getEigenvector() {
		return eigenvector;
	}
}
