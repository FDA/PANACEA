package com.eng.cber.na.layout;

import java.awt.Dimension;
import java.awt.geom.Point2D;

import org.apache.commons.collections15.Transformer;

import com.eng.cber.na.annotations.NetworkGLAnnotationManager;
import com.eng.cber.na.event.util.PropertyChangeSupport;
import com.eng.cber.na.graph.GeneralGraph;

import edu.uci.ics.jung.graph.Graph;

/**
 * The Layout interface defines a set of functions
 * that must be defined for every Layout -- such
 * as the ability to set a graph, set the size of
 * the canvas, the ability to flip a layout, and
 * so on.
 * 
 */
public interface Layout<V, E> extends Transformer<V, Point2D>, Runnable, PropertyChangeSupport  {
	/**LayoutType: defines different layouts; the order should match that defined in NetworkAnalysisVisualization.layoutTypes, 
	 * which is used for Layout Combo. */  
	static enum LayoutType {
		PCA,FORCE_DIRECTED,ISLAND_HEIGHT,CIRCLE,SELF_ORGANIZING_MAP,VOS_MAP, OTHER;
	}	
	
	void setGraph(Graph<V,E> graph);

	Graph<V,E> getGraph();
	
	void setSize(Dimension d);

	NetworkGLAnnotationManager getNetworkGLAnnotationManager();
	
	Dimension getSize();
	
	public void layout();
	
	LayoutType getType();

	void setLocation(V v, Point2D location);	
	public GeneralGraph getReducedGraph() ;

	void flipVertically();
	void rotateClockwise(double angleInDegrees);
	void rotateCounterClockwise(double angleInDegrees);
	
	void flipHorizontally();
	
}
