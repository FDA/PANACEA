package com.eng.cber.na.layout;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.map.LazyMap;

import edu.uci.ics.jung.graph.Graph;

/**
 * The circle layout positions nodes in a circle
 * through basic trigonometry, ordering them
 * in a random fashion.  This class
 * is based on the CircleLayout within JUNG.
 * 
 */
public class CircleLayout<V, E> extends AbstractLayout<V, E> implements Serializable {

	private double radius;
	private List<V> vertex_ordered_list;
	
	transient Map<V, CircleVertexData> circleVertexDataMap =
			LazyMap.decorate(new HashMap<V,CircleVertexData>(), 
			new Factory<CircleVertexData>() {
				@Override
				public CircleVertexData create() {
					return new CircleVertexData();
				}});	
	
	public CircleLayout(Layout<V,E> layout) {
		super(layout);
	}
	
	public CircleLayout(Graph<V, E> graph) {
		super(graph);
	}

	/**
	 * Returns the radius of the circle.
	 */
	public double getRadius() {
		return radius;
	}

	/**
	 * Sets the radius of the circle.  Must be called before
	 * {@code initialize()} is called.
	 */
	public void setRadius(double radius) {
		this.radius = radius;
	}

	/**
	 * Sets the order of the vertices in the layout according to the ordering
	 * specified by {@code comparator}.
	 */
	public void setVertexOrder(Comparator<V> comparator)
	{
	    if (vertex_ordered_list == null)
	        vertex_ordered_list = new ArrayList<V>(getGraph().getVertices());
	    Collections.sort(vertex_ordered_list, comparator);
	}

    /**
     * Sets the order of the vertices in the layout according to the ordering
     * of {@code vertex_list}.
     */
	public void setVertexOrder(List<V> vertex_list)
	{
	    if (!vertex_list.containsAll(getGraph().getVertices())) 
	        throw new IllegalArgumentException("Supplied list must include " +
	        		"all vertices of the graph");
	    this.vertex_ordered_list = vertex_list;
	}
	
	@Override
	public void layout() 
	{
		setProgress(0);
		Dimension d = getSize();
		
		if (d != null) 
		{
		    if (vertex_ordered_list == null) 
		        setVertexOrder(new ArrayList<V>(getGraph().getVertices()));

			double height = d.getHeight();
			double width = d.getWidth();

			if (radius <= 0) {
				radius = 0.45 * (height < width ? height : width);
			}

			int i = 0;
			for (V v : vertex_ordered_list)	{
				double angle = (2 * Math.PI * i) / vertex_ordered_list.size();
				
				Point2D coord = transform(v);
				coord.setLocation(Math.cos(angle) * radius + width / 2, Math.sin(angle) * radius + height / 2);
				
				CircleVertexData data = getCircleData(v);
				data.setAngle(angle);
				setProgress((int)(((double)i / vertex_ordered_list.size())*100));
				i++;
			}
		}
		setProgress(100);
	}

	protected CircleVertexData getCircleData(V v) {
		return circleVertexDataMap.get(v);
	}

	protected static class CircleVertexData {
		private double angle;

		protected double getAngle() {
			return angle;
		}

		protected void setAngle(double angle) {
			this.angle = angle;
		}

		@Override
		public String toString() {
			return "CircleVertexData: angle=" + angle;
		}
	}

	@Override
	public LayoutType getType() {
		return LayoutType.CIRCLE;
	}
	private void readObject(ObjectInputStream stream){
		try {
			stream.defaultReadObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		circleVertexDataMap =
				LazyMap.decorate(new HashMap<V,CircleVertexData>(), 
				new Factory<CircleVertexData>() {
					@Override
					public CircleVertexData create() {
						return new CircleVertexData();
					}});
		}
}
