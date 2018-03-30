package com.eng.cber.na.layout;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingWorker;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.map.LazyMap;

import com.eng.cber.na.annotations.NetworkGLAnnotation;
import com.eng.cber.na.annotations.NetworkGLAnnotationManager;
import com.eng.cber.na.gl.shape.GLRectangle;
import com.eng.cber.na.graph.GeneralGraph;

import edu.uci.ics.jung.graph.Graph;

/**
 * Abstract class defining a layout algorithm.  In the
 * abstract, layouts are threaded and map a vertex
 * to a point in space.  Additionally, a number of 
 * general helper functions are defined in this file
 * for activities like flipping a layout horizontally
 * or vertically and ensuring that a layout is centered
 * in the available space and fills that space as much
 * as possible.
 * 
 */
public abstract class AbstractLayout<V, E> extends SwingWorker<Object, Object> implements Layout<V, E>, Serializable{

	public static EnumMap<LayoutType,String> LayoutTypeToString = new EnumMap<LayoutType,String>(LayoutType.class);
	static {
		LayoutTypeToString.put(LayoutType.PCA, "Principal Components");
		LayoutTypeToString.put(LayoutType.FORCE_DIRECTED, "Force Directed");
		LayoutTypeToString.put(LayoutType.ISLAND_HEIGHT, "Island Height");
		LayoutTypeToString.put(LayoutType.CIRCLE, "Circle");
		LayoutTypeToString.put(LayoutType.SELF_ORGANIZING_MAP, "Self-Organizing Map");
		LayoutTypeToString.put(LayoutType.VOS_MAP, "VOS Mapping");
	}
	
	protected Graph<V,E> graph;
	protected transient Map<V,Point2D> locations = LazyMap.decorate(new HashMap<V, Point2D>(),
			new Transformer<V,Point2D>() {
				@Override
				public Point2D transform(V arg0) {
					return new Point2D.Double();
				}});
	protected Dimension size;
	protected NetworkGLAnnotationManager annotationManager = new NetworkGLAnnotationManager();
	protected Graph<V,E>  origGraph;
	protected GeneralGraph reducedGraph;
	
	@Override
	public GeneralGraph getReducedGraph() {
		return reducedGraph;
	}

	public void setReducedGraph(GeneralGraph reducedGraph) {
		this.reducedGraph = reducedGraph;
	}

	//Build an abstract layout initializing node points to where they currently are in the visualization.
	public AbstractLayout(Layout<V,E> layout) {
		this(layout.getGraph());
		for (V v : graph.getVertices()) {
			Point2D sourcePt = layout.transform(v);
			setLocation(v, new Point2D.Double(sourcePt.getX(), sourcePt.getY()));
		}
	}
	
	public AbstractLayout(Graph<V,E> graph) {
		this.graph = graph;
		this.origGraph = graph;
		reducedGraph = (GeneralGraph)graph;
	}
	
	public AbstractLayout(Graph<V,E> graph, Dimension size) {
		this.graph = graph;
		this.size = size;
	}
	
	@Override
	public Point2D transform(V v) {
		return locations.get(v);
	}

	@Override
	public void setGraph(Graph<V, E> graph) {
		this.graph = graph;
	}

	@Override
	public Graph<V, E> getGraph() {
		return graph;
	}

	@Override
	public void setSize(Dimension size) {
		this.size = size;
	}

	@Override
	public Dimension getSize() {
		return size;
	}

	@Override
	public NetworkGLAnnotationManager getNetworkGLAnnotationManager() {
			return annotationManager;
	}

		@Override
	public void setLocation(V v, Point2D location) {
		locations.put(v, location);
	}

	@Override
	protected Object doInBackground() throws Exception {
		layout();
		return null;
	}
	
	@Override
	public void flipVertically() {
		for (V n : graph.getVertices() ) {
			Point2D loc = transform(n);
			Point2D newLoc = new Point2D.Double(loc.getX(), size.getHeight()-loc.getY());
			setLocation(n, newLoc);
		}
		ArrayList<NetworkGLAnnotation> annotations = getNetworkGLAnnotationManager().getAnnotations();
		for (NetworkGLAnnotation ann : annotations) {
			Point2D loc = ann.getLocation();
			Point2D newloc = new Point2D.Double(loc.getX(), size.getHeight()-loc.getY());
			ann.setLocation(newloc);
		}
		
		firePropertyChange("flip_vertical",null,null);
	}
	
	@Override
	public void flipHorizontally() {
		for (V n : graph.getVertices() ) {
			Point2D loc = transform(n);
			Point2D newLoc = new Point2D.Double(size.getWidth()-loc.getX(), loc.getY());
			setLocation(n, newLoc);
		}
		ArrayList<NetworkGLAnnotation> annotations = getNetworkGLAnnotationManager().getAnnotations();
		for (NetworkGLAnnotation ann : annotations) {
		Point2D loc = ann.getLocation();
		Point2D newloc = new Point2D.Double(size.getWidth()-loc.getX(), loc.getY());
		ann.setLocation(newloc);
		}
		
		firePropertyChange("flip_horizontal",null,null);
	}
	
	@Override
	public PropertyChangeListener[] getPropertyChangeListeners() {
		return getPropertyChangeSupport().getPropertyChangeListeners();
	}
	
	@Override
	public void firePropertyChange(PropertyChangeEvent e) {
		getPropertyChangeSupport().firePropertyChange(e);
	}
	
	@Override
	public void layout(){
		
	};

	protected void centerVerticesInAvailableSpace() {		
		// Find min and max locations
		Double minXVal = Double.MAX_VALUE;
		Double minYVal = Double.MAX_VALUE;
		Double maxXVal = -Double.MAX_VALUE;
		Double maxYVal = -Double.MAX_VALUE;
		for (V vertex : graph.getVertices()) {
			Point2D vertexLoc = transform(vertex);
			
			Double xCoordinate = vertexLoc.getX();
			Double yCoordinate = vertexLoc.getY();
			
			if (xCoordinate < minXVal)
				minXVal = xCoordinate;
			if (xCoordinate > maxXVal)
				maxXVal = xCoordinate;
			if (yCoordinate < minYVal)
				minYVal = yCoordinate;
			if (yCoordinate > maxYVal)
				maxYVal = yCoordinate;
		}
		
		
		Double padding = 50.0;
		Double widthOfViz = maxXVal - minXVal;
		Double heightOfViz = maxYVal - minYVal;

		
		// Resize but keep aspect ratio
		double scalingFactorX = ((int)getSize().getWidth() - padding)/widthOfViz;
		double scalingFactorY = ((int)getSize().getHeight() - padding)/heightOfViz;
		Double scalingFactor = Math.min(scalingFactorX, scalingFactorY);
		if (scalingFactor.isInfinite()) {
			scalingFactor = 1.;
		}
		
		Double centerX = getSize().getWidth()/2;
		Double centerY = getSize().getHeight()/2;
		Double beginOfCenteredSquareX = centerX - widthOfViz*scalingFactor/2;
		Double beginOfCenteredSquareY = centerY - heightOfViz * scalingFactor/2;

		// Ensure we don't get values of 0
		minXVal = (maxXVal-minXVal > 0) ? minXVal : 0;
		minYVal = (maxYVal-minYVal > 0) ? minYVal : 0;
		for (V n : graph.getVertices()) {
			Point2D pt = transform(n);
			

			Double xCoord = (pt.getX()-minXVal)*scalingFactor + beginOfCenteredSquareX;
			Double yCoord = (pt.getY()-minYVal)*scalingFactor + beginOfCenteredSquareY;
			pt.setLocation(xCoord, yCoord);
		}
		
		//Now shift the annotations using the same transformation.
		ArrayList<NetworkGLAnnotation> annotations = getNetworkGLAnnotationManager().getAnnotations();
		for (NetworkGLAnnotation ann : annotations) {
			Point2D pt = ann.getLocation();
			 
			Double xCoord = (pt.getX()-minXVal)*scalingFactor + beginOfCenteredSquareX;
			Double yCoord = (pt.getY()-minYVal)*scalingFactor + beginOfCenteredSquareY;
			pt.setLocation(xCoord, yCoord);
			
			// Scale the size of rectangular annotations
			if (ann.getAnnotation() instanceof GLRectangle) {
				((GLRectangle) ann.getAnnotation()).scale(scalingFactor);
			}
		}
	}
	@Override
	public void rotateClockwise(double angleInDegrees) {
		double angleInRadians = angleInDegrees * Math.PI /180.;
		for (V n : graph.getVertices() ) {
			Point2D loc = transform(n);
			Point2D newLoc = new Point2D.Double(loc.getX()*Math.cos(angleInRadians) - loc.getY()*Math.sin(angleInRadians), loc.getX()*Math.sin(angleInRadians) + loc.getY()*Math.cos(angleInRadians));
			setLocation(n, newLoc);
		}
		ArrayList<NetworkGLAnnotation> annotations = getNetworkGLAnnotationManager().getAnnotations();
		for (NetworkGLAnnotation ann : annotations) {
			Point2D loc = ann.getLocation();
			Point2D newloc = new Point2D.Double(loc.getX()*Math.cos(angleInRadians) - loc.getY()*Math.sin(angleInRadians), loc.getX()*Math.sin(angleInRadians) + loc.getY()*Math.cos(angleInRadians));
			ann.setLocation(newloc);
			if (ann.getAnnotation() instanceof GLRectangle) {
				((GLRectangle) ann.getAnnotation()).rotateClockwise(angleInRadians);
			}
		}
		firePropertyChange("rotate_clockwise",null,null);
		centerVerticesInAvailableSpace();
	}

	@Override
	public void rotateCounterClockwise(double angleInDegrees) {
		double angleInRadians = -angleInDegrees * Math.PI /180.;
		for (V n : graph.getVertices() ) {
			Point2D loc = transform(n);
			Point2D newLoc = new Point2D.Double(loc.getX()*Math.cos(angleInRadians) - loc.getY()*Math.sin(angleInRadians), loc.getX()*Math.sin(angleInRadians) + loc.getY()*Math.cos(angleInRadians));
			setLocation(n, newLoc);
		}
		ArrayList<NetworkGLAnnotation> annotations = getNetworkGLAnnotationManager().getAnnotations();
		for (NetworkGLAnnotation ann : annotations) {
			Point2D loc = ann.getLocation();
			Point2D newloc = new Point2D.Double(loc.getX()*Math.cos(angleInRadians) - loc.getY()*Math.sin(angleInRadians), loc.getX()*Math.sin(angleInRadians) + loc.getY()*Math.cos(angleInRadians));
			ann.setLocation(newloc);
			if (ann.getAnnotation() instanceof GLRectangle) {
				((GLRectangle) ann.getAnnotation()).rotateCounterClockwise(angleInRadians);
			}
		}
		firePropertyChange("rotate_counterclockwise",null,null);
		centerVerticesInAvailableSpace();
	}

	private void readObject(ObjectInputStream stream){
		try {
			stream.defaultReadObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map<V, Point2D> readLoc;
		try {
			readLoc = (Map<V, Point2D>) stream.readObject();
			locations = LazyMap.decorate( readLoc,
					new Transformer<V,Point2D>() {
						@Override
						public Point2D transform(V arg0) {
							return new Point2D.Double();
						}});
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void writeObject(ObjectOutputStream stream) throws IOException{
		stream.defaultWriteObject();
		HashMap<V, Point2D> saveMap = new HashMap<V, Point2D>(locations);
		stream.writeObject(saveMap);
	}
	
}