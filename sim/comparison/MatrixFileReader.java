package com.eng.cber.na.sim.comparison;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JOptionPane;

public class MatrixFileReader {
	
	private List<Integer> nodesInSims = new ArrayList<Integer>();
	private List<Integer> ptsInSims = new ArrayList<Integer>();
	private List<Integer> vxsInSims = new ArrayList<Integer>();
	private List<List<Integer>> degreesInSims = new ArrayList<List<Integer>>();
	
	private double avgNodesInSims;
	private double avgPTsInSims;
	private double avgVXsInSims;
	
	boolean hasMatrixInfo = false;
	
	public void readFiles(Set<String> matrixFileNames) {
		List<Integer> degreeList = new ArrayList<Integer>();
		
		for (String matrixFileName : matrixFileNames) {
			int numNodes = 0;
			int numPTs = 0;
			int numVXs = 0;
			degreeList = new ArrayList<Integer>();
			Scanner fScan = null;
			File matrixFile = new File(matrixFileName);
			try {
				fScan = new Scanner(new FileReader(matrixFile));
				
				int i = 0;
				while (fScan.hasNextLine()) {
					String fullLine = fScan.nextLine();
					String[] tokens = fullLine.split(",");
					
					if (i == 0) {
						for (String token : tokens) {
							if (token.length() > 0)
								numNodes++;
							if (token.contains("pt"))
								numPTs++;
							if (token.contains("vx"))
								numVXs++;
						}
					}
					else {
						int degree = 0;
						for (int j = 1; j < tokens.length; j++) {
							int weight = Integer.parseInt(tokens[j]);
							if (weight > 0)
								degree++;
						}
						degreeList.add(degree);
					}
					i++;
				}
			} catch (FileNotFoundException fnfe) {
				System.out.println("Could not find file " + matrixFileName);
				continue;
			} catch (NumberFormatException nfe) {
				System.out.println("Error reading file " + matrixFileName);
				continue;
			}
			
			degreesInSims.add(degreeList);
			nodesInSims.add(numNodes);
			ptsInSims.add(numPTs);
			vxsInSims.add(numVXs);
			
			
			if (fScan != null)
				fScan.close();
		}
		
		if (isListListEmpty(degreesInSims)) {
			JOptionPane.showMessageDialog(null, "Could not read the given file or any similar files.\nPlease make sure that you have selected one of the MATRIX output files from the network simulator.", "No Matrix File Read", JOptionPane.ERROR_MESSAGE);
			hasMatrixInfo = false;
			return;
		}
		
		avgNodesInSims = getAverageOfList(nodesInSims);
		avgPTsInSims = getAverageOfList(ptsInSims);
		avgVXsInSims = getAverageOfList(vxsInSims);
		hasMatrixInfo = true;
	}
	
	private static double getAverageOfList(List<Integer> inputList) {
		if (!inputList.isEmpty()) {
			Integer sum = 0;
			for (Integer entry : inputList) {
				sum += entry;
			}
			return ((double) sum) / inputList.size();
		}
		else {
			return 0;
		}
	}
	
	private static boolean isListListEmpty(List<List<Integer>> inputList) {
		if (inputList.isEmpty())
			return true;
		else {
			boolean empty = true;;
			for (List<?> innerList : inputList) {
				if (!innerList.isEmpty())
					empty = false;
			}
			return empty;
		}
	}
	
	public boolean hasMatrixInfo() {
		return hasMatrixInfo;
	}
	
	public List<List<Integer>> getDegreesInSims() {
		return degreesInSims;
	}
	
	public double getAvgNodesInSims() {
		return avgNodesInSims;
	}
	
	public double getAvgPTsInSims() {
		return avgPTsInSims;
	}
	
	public double getAvgVXsInSims() {
		return avgVXsInSims;
	}
}
