package com.eng.cber.na.layout;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import Jama.Matrix;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.graph.GeneralEdge;
import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.graph.GeneralNode;
import com.eng.cber.na.math.LargestEigenvalueDecomposition;

import edu.uci.ics.jung.graph.Graph;

/**
 * The PCALayout positions nodes according to the
 * dimensional decomposition that is produced through
 * identifying the principal components of the dataset.
 * This is a new layout introduced in PANACEA.
 * 
 * The principal components transformation is a linear
 * algebra transformation that attempts to find the 
 * "most informative" axes by which to view the data
 * and rearranges the data according to the most informative 
 * two of these axes.
 *
 */
public class PCALayout extends AbstractLayout<GeneralNode, GeneralEdge> implements Serializable {

	public PCALayout(Layout<GeneralNode, GeneralEdge> layout) {
		super(layout);
	}
	
	public PCALayout(Graph<GeneralNode, GeneralEdge> graph) {
		super(graph);
	}

	@Override
	public void layout() {

		NetworkAnalysisVisualization.NALog("PCA Layout started", true);

		GeneralGraph graph = (GeneralGraph)getGraph();
		if (graph != null && graph.getEdgeCount() > 0) {
			// Identify the size of the square area that the
			// PCA layout should fill
			Dimension d = NetworkAnalysisVisualization.getInstance().getNetworkGLVisualizationServer().getSize();
			double sizeOfSquare; 
			if (d.getWidth() < d.getHeight()){
				sizeOfSquare = (int)d.getWidth();
			}
			else {
				sizeOfSquare = (int)d.getHeight();
			}
	
			double[][] adjacencyMatrix = new double[graph.getVertexCount()][graph.getVertexCount()];
			for (int i = 0; i < graph.getVertexCount(); i++){
				for (int j = 0; j < graph.getVertexCount(); j++) {
					adjacencyMatrix[i][j] = 0.0;
				}
			}
			Integer currentLoc = 0;
			HashMap<GeneralNode, Integer> vertexToIndex = new HashMap<GeneralNode, Integer>();
			for (GeneralEdge edge : graph.getEdges()) {
				GeneralNode u = graph.getFrom(edge);
				GeneralNode v = graph.getTo(edge);
				
				Integer uIndex, vIndex;			
				if (vertexToIndex.containsKey(u)){
					uIndex = vertexToIndex.get(u);
				}
				else {
					vertexToIndex.put(u,currentLoc);
					uIndex = currentLoc;
					currentLoc++;
				}
				if (vertexToIndex.containsKey(v)){
					vIndex = vertexToIndex.get(v);
				}
				else {
					vertexToIndex.put(v,currentLoc);
					vIndex = currentLoc;
					currentLoc++;
				}
				
				// Because graph is undirected, both directions apply
				adjacencyMatrix[uIndex][vIndex] = edge.getWeight();
				adjacencyMatrix[vIndex][uIndex] = edge.getWeight();
			}
	
			LargestEigenvalueDecomposition firstEigenPair = new LargestEigenvalueDecomposition(adjacencyMatrix);
			Matrix firstEigenvector = firstEigenPair.getEigenvector();
			double firstEigenvalue = firstEigenPair.getEigenvalue();
			
			Matrix originalData = new Matrix(adjacencyMatrix);
			Matrix dataAccountedFor = firstEigenvector.times(firstEigenvector.transpose()).times(firstEigenvalue);
			Matrix remainingData = originalData.minus(dataAccountedFor);
			
			LargestEigenvalueDecomposition secondEigenPair = new LargestEigenvalueDecomposition(remainingData.getArray());
			Matrix secondEigenvector = secondEigenPair.getEigenvector();
			
			Iterator<Map.Entry<GeneralNode,Integer>> vertexIterator = vertexToIndex.entrySet().iterator();
			// If the eigenvalue decomposition doesn't produce a meaningful layout, do something different
			if (PCALayout.getRangeOfFirstColumn(firstEigenvector) == 0 || 
				PCALayout.getRangeOfFirstColumn(secondEigenvector) == 0	) {
				vertexIterator = vertexToIndex.entrySet().iterator();
				while (vertexIterator.hasNext()) {
					Map.Entry<GeneralNode, Integer> pair = vertexIterator.next();				
					GeneralNode n = pair.getKey();
					Point2D pt = transform(n);
									
					Double centerX = d.getWidth()/2;
					Double centerY = d.getHeight()/2;
					Double xCoord = centerX;
					Double yCoord = centerY; 
	
					pt.setLocation(xCoord, yCoord);
				}
			}
			else {
				// Find the two biggest eigenvalues. These are
				// the first and second eigenvalues.
					
				// Take first eigenvector as X coordinates (corresponds to first eigenvalue)
				// Take second eigenvector as Y coordinates
				Double minXVal = Double.MAX_VALUE;
				Double minYVal = Double.MAX_VALUE;
				Double maxXVal = Double.MIN_VALUE;
				Double maxYVal = Double.MIN_VALUE;
				while (vertexIterator.hasNext()) {
					Map.Entry<GeneralNode, Integer> pair = vertexIterator.next();
	
					GeneralNode vertex = pair.getKey();
					
					Double xCoordinate = firstEigenvector.get(pair.getValue(),0);
					Double yCoordinate = secondEigenvector.get(pair.getValue(),0);
					
					if (xCoordinate.isNaN()) {
						xCoordinate = 0.0;
					}
					if (yCoordinate.isNaN()) {
						yCoordinate = 0.0;
					}
									
					if (xCoordinate < minXVal)
						minXVal = xCoordinate;
					if (xCoordinate > maxXVal)
						maxXVal = xCoordinate;
					if (yCoordinate < minYVal)
						minYVal = yCoordinate;
					if (yCoordinate > maxYVal)
						maxYVal = yCoordinate;
	
					Point2D coord = transform(vertex);
					coord.setLocation(xCoordinate,yCoordinate);
				}
				
				// Scale results to fit the full square size available
				Double widthOfViz = maxXVal - minXVal;
				Double heightOfViz = maxYVal - minYVal;
				
				Double multiplyXValuesBy = (sizeOfSquare)/(widthOfViz);
				if (multiplyXValuesBy.isNaN() || multiplyXValuesBy.isInfinite())
					multiplyXValuesBy = 0.0;
				Double multiplyYValuesBy = (sizeOfSquare)/(heightOfViz);
				if (multiplyYValuesBy.isNaN() || multiplyYValuesBy.isInfinite())
					multiplyYValuesBy = 0.0;
				
				vertexIterator = vertexToIndex.entrySet().iterator();
				while (vertexIterator.hasNext()) {
					Map.Entry<GeneralNode, Integer> pair = vertexIterator.next();				
					GeneralNode n = pair.getKey();
					Point2D pt = transform(n);
									
					Double xCoord = (pt.getX()-minXVal)*multiplyXValuesBy;
					Double yCoord = (pt.getY()-minYVal)*multiplyYValuesBy; 
	
					pt.setLocation(xCoord, yCoord);
				}
				
				// Center results
				super.centerVerticesInAvailableSpace();

				// Flip results horizontally so that largest eigenvector centralities
				// tend to appear on left
				super.flipHorizontally();	
			}
			
		}
		else {
			for (GeneralNode n : graph.getVertices()) {
				Point2D pt = transform(n);
				pt.setLocation(100, 100);
			}
		}
		NetworkAnalysisVisualization.NALog("PCA Layout ended", true);
	}

	@Override
	public LayoutType getType() {	
		return LayoutType.PCA;
	}
	
	private static Double getRangeOfFirstColumn(Matrix m) {
		Double maxFirst = Double.MIN_VALUE;
		Double minFirst = Double.MAX_VALUE;
		for (int r = 0; r < m.getRowDimension(); r++) {
			Double v = m.get(r, 0);
			if (v > maxFirst) {
				maxFirst = v;
			}
			if (v < minFirst) {
				minFirst = v;
			}
		}
		return maxFirst - minFirst;
	}
	private void writeObject(ObjectOutputStream stream) throws IOException{
		stream.defaultWriteObject();
	}
}