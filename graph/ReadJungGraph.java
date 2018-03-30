package com.eng.cber.na.graph;

import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.collections15.Factory;

import com.eng.cber.na.util.TxtFileGraphReader;

import edu.uci.ics.jung.io.PajekNetReader;

/**
 * @author Guangfan (Geoffrey) Zhang
 *
 */
public class ReadJungGraph {
	public String fileName = "";
	public static enum FileType {
		txt, net, csv};
	public GeneralGraph graph;
	public int subType;

	Factory<GeneralEdge> edgeFactory = new Factory<GeneralEdge> () {
		
		public GeneralEdge create(){
			return new GeneralEdge(1.0);
		}
	};
	Factory<GeneralNode> nodeFactory = new Factory<GeneralNode> () {
		int n = 0;
		public GeneralNode create(){
			return new GeneralNode(n++);
		}
	};
	
	private FileType fileType;
	
	public ReadJungGraph(String fileName){
		this.fileName = fileName;
		
	}

	public ReadJungGraph(String fileName, FileType fileType){
		this.fileName = fileName;
		this.fileType = fileType;
	}
	
	/**
	 * @param subType 0=edgelist file (.txt or .csv);  1=matrix file (.txt);  2=Pajek file (.net)
	 */
	public ReadJungGraph(String fileName, FileType fileType, int subType){
		this.fileName = fileName;
		this.fileType = fileType;
		this.subType = subType;
	}

	public void LoadGraph(){
		if (fileType.ordinal() == 0 || fileType.ordinal() == 2){
			ReadFiletoGraph();
		}
		else if (fileType.ordinal() == 1){
			ReadPajekFiletoGraph();
		}else{
			System.out.println("File format not defined when loading graph");
		}
		return;
		
	}
	public void ReadFiletoGraph(){
		TxtFileGraphReader txtReader = new TxtFileGraphReader();
		txtReader.ReadGraphFromFile(fileName, subType);
		
		graph = new GeneralGraph();
		
		Iterator<GeneralNode> itr = txtReader.nodeList.iterator();
		while (itr.hasNext()){
			GeneralNode node = itr.next();
			graph.addVertex( node);
		}
		GeneralEdge edge1;
		for (int jj = 0; jj < txtReader.edgeList.size(); jj++ ){
			
			edge1 = txtReader.edgeList.get(jj);
			graph.addEdge(edge1, edge1.node1, edge1.node2);
	    }

		
	}
	
	public void ReadPajekFiletoGraph(){
		graph = new GeneralGraph();
		PajekNetReader<GeneralGraph, GeneralNode, GeneralEdge> pnw;
        pnw = new PajekNetReader<GeneralGraph, GeneralNode, GeneralEdge>(nodeFactory, edgeFactory);
		
		try {
			long tStart= System.currentTimeMillis();
			
			pnw.load(fileName, graph);
			System.out.println("Loading Pajek .net file: " + (System.currentTimeMillis()-tStart)/1000.0);
			for(GeneralEdge edge:graph.getEdges()){
				Number nu;
				nu = (pnw.getEdgeWeightTransformer().transform(edge));
				if (nu != null )
					edge.setWeight(nu.doubleValue());
				
				edge.node1 = graph.getFrom(edge);
				edge.node2 = graph.getTo(edge);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}		
}
