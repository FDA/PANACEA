package com.eng.cber.na.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.eng.cber.na.graph.GeneralEdge;
import com.eng.cber.na.graph.GeneralNode;
import com.eng.cber.na.graph.GraphLoader;


public class TxtFileGraphReader {
	public String fileName;
	String delims = ",";
	public ArrayList<GeneralEdge> edgeList; 
	public ArrayList<GeneralNode> nodeList; 
	
	public void ReadGraphFromFile(String fileName){
		ReadGraphFromFile(fileName, 0);
	}
	
	/**
	 * @param subType 0=Edgelist file; 1=Adjacency Matrix file
	 */
	public void ReadGraphFromFile(String filename, int subType) {
		if (subType == 0)
			ReadGraphFromEdgelistFile(filename);
		else if (subType == 1)
			ReadGraphFromMatrixFile(filename);
	}
	
	/**
	 * Reads an edgelist file and puts the resulting graph information
	 * into the "nodeList" and "edgeList" fields.  This method should
	 * properly ignore header information (by looking for lines with
	 * exactly three entries, the last of which is a number).  It will
	 * attempt to use multiple separator characters until it finds a
	 * usable result.  The characters in order are: Tab, comma,
	 * semicolon, and any whitespace.
	 */
	public void ReadGraphFromEdgelistFile(String fileName) {
		BufferedReader br = null;
	    GeneralNode node1, node2;
	    double edgeWeight = 0.0;
	    
        edgeList = new ArrayList<GeneralEdge>();
		Map<String, GeneralNode> nodeMap = new HashMap<String, GeneralNode>();
		HashSet<String> constructedNodeSet = new HashSet<String>();
		
        try {			
			// Read through file using different separator characters until
			// one is found that produces usable information.
			List<String> separators = Arrays.asList("[\\t]+", ",", ";", "[\\s]+");
			int sepCount = -1;
			int linesMatchingFormat = 0;
			while (linesMatchingFormat == 0) {
				sepCount++;
				br = new BufferedReader(new FileReader(fileName));
				ArrayList<Integer> tokensPerLine = new ArrayList<Integer>();
				String separator = separators.get(sepCount);
				String sLine = null;
				while ((sLine = br.readLine()) != null) {
					String fullLine = sLine.trim();
					String[] tokens = fullLine.split(separator);
					tokensPerLine.add(tokens.length);
					if (tokens.length == 3) {
						try {
							Double.parseDouble(GraphLoader.stripString(tokens[2]));
							linesMatchingFormat++;
						} catch (NumberFormatException e) {
							
						}
					}
				}
				br.close();
				sLine = null;
			}
			
			// Read file using the proper separator
			br = new BufferedReader(new FileReader(fileName));
			String nodeName1, nodeName2;
			String sLine = null;
	        while ((sLine = br.readLine())!= null )
	        {
	            String fullLine = sLine.trim();
	            String[] tokens = fullLine.split(separators.get(sepCount));

	            //Should have three values in each line: node1, node2, weight
            	//Ignores lines that don't match this format.
            	
            	if (tokens.length != 3)
            		continue;
            	try {
            		Double.parseDouble(tokens[2]);
            	} catch (NumberFormatException e) {
            		continue;
            	}
	            
	            nodeName1 = GraphLoader.stripString(tokens[0]);
	            nodeName2 = GraphLoader.stripString(tokens[1]);
	            
	            if (constructedNodeSet.contains(nodeName1) == false){
		            constructedNodeSet.add(nodeName1);
		            node1 = new GeneralNode(nodeName1);
	            	nodeMap.put(nodeName1, node1);
	            }
	            else{
	            	node1 = nodeMap.get(nodeName1);
	            }
	            	
	            
	            if (constructedNodeSet.contains(nodeName2) == false){
	            	constructedNodeSet.add(nodeName2);
	            	node2 = new GeneralNode(nodeName2);
	            	nodeMap.put(nodeName2, node2);
	            }
	            else{
	            	node2 = nodeMap.get(nodeName2);
	            }
	            if (tokens.length< 3){
	            	edgeWeight = 1;
	            }
	            else{
	            	edgeWeight = Double.parseDouble(tokens[2]);
	            }

	            if (node1 != node2)
	            {
	                edgeList.add(new GeneralEdge(edgeWeight, node1, node2));
	                edgeList.add(new GeneralEdge(edgeWeight, node2, node1));
	            }
		        nodeList = new ArrayList<GeneralNode>(nodeMap.values());
	        }
        } catch (IOException ioe) {
			ioe.printStackTrace();
		}
        finally {
           	if(br != null) {
           		try {
           			br.close();
           		} catch (IOException e) {
           			e.printStackTrace();
           		}
           	}
        }
	}
	
	/**
	 * Reads an adjacency matrix file and puts the resulting graph
	 * information into the "nodeList" and "edgeList" fields.  The file
	 * must have the correct format.  Entries should be separated by
	 * commas or semicolons.  The first line should list all the nodes
	 * with a BLANK ENTRY in the very first position.  The remaining
	 * lines should include the name of a node in the first entry and
	 * the edge weights it makes with other nodes in the remaining
	 * entries, to fill out a full adjacency matrix.  The matrix can be
	 * triangular or mirrored.<br/><br/>
	 * This method should properly ignore header information (by looking
	 * for multiple lines with the same number of entries, where most
	 * entries are numbers).
	 */
	public void ReadGraphFromMatrixFile(String fileName){ 
	    BufferedReader br = null;
	    GeneralNode node1;
	    
        edgeList = new ArrayList<GeneralEdge>();
		Map<String, GeneralNode> nodeMap = new HashMap<String, GeneralNode>();
		HashSet<String> constructedNodeSet = new HashSet<String>();
		
        try {			
			// Read through file using different separator characters until
			// one is found that produces usable information.
        	int dataStartLine = 0;
			List<String> separators = Arrays.asList(",", ";");
			int sepCount = -1;
			boolean foundData = false;
			while (!foundData) {
				sepCount++;
				dataStartLine = 0;
				ArrayList<Integer> tokensPerMatchingLine = new ArrayList<Integer>();
				br = new BufferedReader(new FileReader(fileName));
				String separator = separators.get(sepCount);
				String sLine = null;
				boolean dataHasStarted = false;
				int prevLineTokens = 0;
				int prevPrevLineTokens = 0;
				int currentLineNum = 0;
				while ((sLine = br.readLine()) != null) {
					currentLineNum++;
					String fullLine = sLine.trim();
					String[] tokens = fullLine.split(separator);
					if (tokens.length == prevLineTokens && tokens.length > 1) {
						try {
							// Make sure everything on the line except the first entry is a number.
							for (int j = 1; j < tokens.length; j++) {
								Double.parseDouble(tokens[j]);
							}
							tokensPerMatchingLine.add(tokens.length);
							if (tokens.length == prevPrevLineTokens && !dataHasStarted) {
								dataStartLine = currentLineNum - 2; // the header line is needed and should be above the previous line
								dataHasStarted = true;
							}
						} catch (NumberFormatException nfe) {
							
						}
					}
					prevPrevLineTokens = prevLineTokens;
					prevLineTokens = tokens.length;
				}
				boolean allLinesSameLength = false;
				if (tokensPerMatchingLine.size() > 0) {
					for (int k = 0; k < tokensPerMatchingLine.size(); k++) {
						if (!tokensPerMatchingLine.get(0).equals(tokensPerMatchingLine.get(k))) {
							allLinesSameLength = false;
							break;
						}
						allLinesSameLength = true;
					}
				}
				if (allLinesSameLength)
					foundData = true;
				br.close();
				sLine = null;
			}

			// Read file using the proper separator
        	br = new BufferedReader(new FileReader(fileName));
			String nodeName1;
			String[] nodes = null; 
        	double[] adjList = null; 
			String sLine = null;
			int currentLineNum = 0;
	        while ((sLine = br.readLine())!= null )
	        {
	        	currentLineNum++;
	        	if (currentLineNum < dataStartLine)
	        		continue;
	            String fullLine = sLine.trim();
	            String[] tokens = fullLine.split(separators.get(sepCount));
	            if (tokens.length == 0)
	            	continue;
	            
	            //adjacent matrix: the first row and the first column contain the node id's.
	            //Read the node id's from the first row
            	if (currentLineNum == dataStartLine){
            		nodes = new String[tokens.length - 1];
            		for (int j = 1; j < tokens.length; j++){
            			nodeName1 = tokens[j];
            			nodes[j-1] = nodeName1;
            			constructedNodeSet.add(nodeName1);
            			node1 = new GeneralNode(nodeName1);
            			nodeMap.put(nodeName1, node1);
            		}
		        	continue;
            	}
            	else{
            		nodeName1 =  tokens[0];
            		node1 = nodeMap.get(nodeName1);
	            	adjList = new double[tokens.length - 1];
            		for (int j = 1; j <= adjList.length; j++){
            			adjList[j-1] = Double.parseDouble(tokens[j]);
            			if (adjList[j-1] > 0){
    		                edgeList.add(new GeneralEdge(adjList[j-1], node1, nodeMap.get(nodes[j-1])));
            			}
            		}
            		
            	}
	            	
	        }
	        
	        nodeList = new ArrayList<GeneralNode>(nodeMap.values());
	        
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        finally{
        	if(br!= null)
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	
        }

        return ;
	}
	
}

