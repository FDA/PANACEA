package com.eng.cber.na.layout;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.map.LazyMap;

import com.eng.cber.na.gl.GLGraphElementAccessor;
import com.eng.cber.na.gl.RadiusGLGraphElementAccessor;

import edu.uci.ics.jung.graph.Graph;

/**
 * The ISOMLayout positions nodes in a self-organizing map layout
 * through the self-organizing map algorithm.  This class
 * is based on ISOMLayout within JUNG.
 * 
 * See:
 * -- JUNG's ISOMLayout class
 * -- Meyer, B. (1998). Self-organizing Graphs: A Neural Network Perspective 
 * of Graph Layout. Proceedings of the 6th International Symposium on Graph 
 * Drawing, Montreal, Canada.
 * 
 */
public class ISOMLayout<V, E> extends AbstractLayout<V, E> implements Serializable {

	transient Map<V, ISOMVertexData> isomVertexData =
			LazyMap.decorate(new HashMap<V, ISOMVertexData>(),
					new Factory<ISOMVertexData>() {
						@Override
						public ISOMVertexData create() {
							return new ISOMVertexData();
						}});

	private int maxEpoch = 2000;
	private int epoch = 1;

	private int radiusConstantTime = 100;
	private int radius = 5;
	private int minRadius = 1;

	private double initialAdaption = 90.0D / 100.0D;;
	private double adaption = 90.0D / 100.0D;
	private double minAdaption = 0;

	private double coolingFactor = 2;
	
    protected transient GLGraphElementAccessor<V,E> elementAccessor = new RadiusGLGraphElementAccessor<V,E>();

    private List<V> queue = new ArrayList<V>();
	
    public ISOMLayout(Layout<V, E> layout) {
		super(layout);
	}
    
	public ISOMLayout(Graph<V, E> graph) {
		super(graph);
	}

	@Override
	public void layout() {
		while (epoch < maxEpoch) {
			adjust();
			updateParameters();
			setProgress((int)(((double)epoch/maxEpoch)*100));
		}
	}
	
	private void adjust() {
		//Generate random position in graph space
		Point2D tempXYD = new Point2D.Double();

		// creates a new XY data location
        tempXYD.setLocation(10 + Math.random() * getSize().getWidth(),
                10 + Math.random() * getSize().getHeight());

		//Get closest vertex to random position
		V winner = elementAccessor.getVertex(this, tempXYD.getX(), tempXYD.getY());
		
		for(V v : getGraph().getVertices()) {
			ISOMVertexData ivd = isomVertexData.get(v);
			ivd.distance = 0;
			ivd.visited = false;
		}
		
		adjustVertex(winner, tempXYD);
	}


	private void adjustVertex(V v, Point2D tempXYD) {
		queue.clear();
		ISOMVertexData ivd = isomVertexData.get(v);
		ivd.distance = 0;
		ivd.visited = true;
		queue.add(v);
		V current;

		while (!queue.isEmpty()) {
			current = queue.remove(0);
			ISOMVertexData currData = isomVertexData.get(current);
			Point2D currXYData = transform(current);

			double dx = tempXYD.getX() - currXYData.getX();
			double dy = tempXYD.getY() - currXYData.getY();
			double factor = adaption / Math.pow(2, currData.distance);

			currXYData.setLocation(currXYData.getX()+(factor*dx), currXYData.getY()+(factor*dy));

			if (currData.distance < radius) {
			    Collection<V> s = getGraph().getNeighbors(current);
			    
			    for(V child : s) {
			    	ISOMVertexData childData = isomVertexData.get(child);
			    	if (childData != null && !childData.visited) {
			    		childData.visited = true;
			    		childData.distance = currData.distance + 1;
			    		queue.add(child);
			    	}
			    }
			    
			}
		}
	}

	private void updateParameters() {
		epoch++;
		double factor = Math.exp(-1 * coolingFactor * (1.0 * epoch / maxEpoch));
		adaption = Math.max(minAdaption, factor * initialAdaption);
		
		if ((radius > minRadius) && (epoch % radiusConstantTime == 0)) {
			radius--;
		}
	}
	
	protected static class ISOMVertexData {
		int distance;
		boolean visited;

		protected ISOMVertexData() {
		    distance = 0;
		    visited = false;
		}
	}

	@Override
	public LayoutType getType() {	
		return LayoutType.SELF_ORGANIZING_MAP;
	}

	private void readObject(ObjectInputStream stream){
		try {
			stream.defaultReadObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		isomVertexData =
				LazyMap.decorate(new HashMap<V, ISOMVertexData>(),
						new Factory<ISOMVertexData>() {
					@Override
					public ISOMVertexData create() {
						return new ISOMVertexData();
					}});

		elementAccessor = new RadiusGLGraphElementAccessor<V,E>();		
	}
	private void writeObject(ObjectOutputStream stream) throws IOException{
		stream.defaultWriteObject();
		System.out.println("Write ISOM Layout");
	}
}