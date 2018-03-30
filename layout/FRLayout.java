package com.eng.cber.na.layout;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.map.LazyMap;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.graph.GeneralNode;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * The FRLayout positions nodes in a force-directed layout
 * through the Fruchterman-Reingold algorithm.  This class
 * is based on FRLayout within JUNG.
 * 
 * This class was modified: (1) to alter the starting parameters
 * to produce a better visual layout, (2) to remove a final 
 * processing phase in which nodes that the layout suggested
 * should be placed off screen or in the border were randomly reassigned
 * new coordinates, (3) to start the layout on the basis of existing
 * node coordinates rather than with every node at (0,0), and 
 * (4) to introduce minor jitter when beginning the layout to ensure 
 * that nodes that are laid out on top of each other will eventually 
 * separate from each other in the visualization.
 * 
 */
public class FRLayout<V,E> extends AbstractLayout<V, E> implements Serializable {
	
    private transient Map<V, FRVertexData> frVertexData =
    	LazyMap.decorate(new HashMap<V,FRVertexData>(), new Factory<FRVertexData>() {
    		@Override
			public FRVertexData create() {
    			return new FRVertexData();
    		}});

	private double forceConstant;
    private double temperature;
    private int currentIteration;
    private int mMaxIterations = 700;
    private double attraction_multiplier = 0.90;
    private double attraction_constant;
    private double repulsion_multiplier = 0.25;
    private double repulsion_constant;
    private double max_dimension;
    private double EPSILON = 0.000001D;
    
    public FRLayout(Layout<V,E> layout) {
		super(layout);
		int i = 0;
		// Introduce minor jitter (otherwise nodes that are currently laid on top of each other will always be laid on top of each other)
		for (V v : graph.getVertices()) {
			i= i +1;
			Point2D sourcePt = layout.transform(v);
			setLocation(v, new Point2D.Double(sourcePt.getX() + Math.random(), sourcePt.getY() + Math.random()));
		}
	}
    
	public FRLayout(Graph<V, E> graph) {
		super(graph);		
		int i = 0;
		// Introduce minor jitter (otherwise nodes that are currently laid on top of each other will always be laid on top of each other)
		for (V v : graph.getVertices()) {
			i= i +1;
			setLocation(v, new Point2D.Double(Math.random()*800, Math.random()*600));
		}
	}

	@Override
	public void layout() {
		Dimension d = getSize();
		NetworkAnalysisVisualization nv =NetworkAnalysisVisualization.getInstance();
		int repaintInterval = nv.getRepaintInterval();
		
    	if(graph != null && d != null) {
    		currentIteration = 0;
    		temperature = d.getWidth() / 20;

    		forceConstant = Math.sqrt(d.getHeight() * d.getWidth() / graph.getVertexCount());

    		attraction_constant = attraction_multiplier * forceConstant;
    		repulsion_constant = repulsion_multiplier * forceConstant;
    	}
        max_dimension = Math.max(d.height, d.width);

		while(currentIteration <= mMaxIterations && temperature >= 1.0/max_dimension) {
            for(V v1 : getGraph().getVertices()) {
            	calcRepulsion(v1);
            }
            
            for(E e : getGraph().getEdges()) {
                calcAttraction(e);
            }
            
            for(V v : getGraph().getVertices()) {
            	calcPositions(v);
            }
	                
	        temperature *= (1.0 - currentIteration / (double) mMaxIterations);
	        currentIteration++;
	        
	        if(repaintInterval  > 0 )
	        	if (currentIteration % repaintInterval == 0) {
	        		NetworkAnalysisVisualization.getInstance().getNetworkGLVisualizationServer().repaint();
	        }
		}
		
    	relocateIsolates();
		super.centerVerticesInAvailableSpace();
	}
	
	protected void calcAttraction(E e) {
    	Pair<V> endpoints = getGraph().getEndpoints(e);
        V v1 = endpoints.getFirst();
        V v2 = endpoints.getSecond();
        
        Point2D p1 = transform(v1);
        Point2D p2 = transform(v2);
        if(p1 == null || p2 == null) return;
        double xDelta = p1.getX() - p2.getX();
        double yDelta = p1.getY() - p2.getY();

        double deltaLength = Math.max(EPSILON, Math.sqrt((xDelta * xDelta)
                + (yDelta * yDelta)));

        double force = (deltaLength * deltaLength) / attraction_constant;

        if (Double.isNaN(force)) { throw new IllegalArgumentException(
                "Unexpected mathematical result in FRLayout:calcPositions [force]"); }

        double dx = (xDelta / deltaLength) * force;
        double dy = (yDelta / deltaLength) * force;
    	
        FRVertexData fvd1 = frVertexData.get(v1);
    	fvd1.offset(-dx, -dy);
    	FRVertexData fvd2 = frVertexData.get(v2);
    	fvd2.offset(dx, dy);
    }
	
	protected void calcRepulsion(V v1) {
        FRVertexData fvd1 = frVertexData.get(v1);
        if(fvd1 == null)
            return;

        fvd1.setLocation(0, 0);
        
        for(V v2 : getGraph().getVertices()) {
            if (v1 != v2) {
                Point2D p1 = transform(v1);
                Point2D p2 = transform(v2);
                if(p1 == null || p2 == null) continue;
                double xDelta = p1.getX() - p2.getX();
                double yDelta = p1.getY() - p2.getY();

                double deltaLength = Math.max(EPSILON, Math.sqrt((xDelta * xDelta) + (yDelta * yDelta)));

                double force = (repulsion_constant * repulsion_constant) / deltaLength;

                if (Double.isNaN(force)) { 
                	throw new RuntimeException("Unexpected mathematical result in FRLayout:calcPositions [repulsion]"); 
                }

                fvd1.offset((xDelta / deltaLength) * force, (yDelta / deltaLength) * force);
            }
        }
    }
	
	protected void calcPositions(V v) {
        FRVertexData fvd = frVertexData.get(v);
        if(fvd == null) return;
        Point2D xyd = transform(v);
        double deltaLength = Math.max(EPSILON, fvd.norm());

        double newXDisp = fvd.getX() / deltaLength
                * Math.min(deltaLength, temperature);

        if (Double.isNaN(newXDisp)) {
        	throw new IllegalArgumentException(
                "Unexpected mathematical result in FRLayout:calcPositions [xdisp]"); }

        double newYDisp = fvd.getY() / deltaLength
                * Math.min(deltaLength, temperature);
        
        xyd.setLocation(xyd.getX()+newXDisp, xyd.getY()+newYDisp);
        
    }
	protected void relocateIsolates() {
		GeneralGraph graph = (GeneralGraph)getGraph();
		Map<Integer, Double[]> dimMap = new HashMap<Integer, Double[]>();
		
		int componentID = 0;
        for(int i=0; i< graph.getComponentCount();i++ ){
        	dimMap.put(i, new Double[]{100000.0, -100000.0, 100000.0, -100000.0});
        }
        for(V v1:getGraph().getVertices()){

        	componentID = graph.getComponentID((GeneralNode)v1) - 1;
        	FRVertexData fvd = frVertexData.get(v1);
        	if(fvd == null) 
        		return;
        	Point2D xyd = transform(v1);
        	dimMap.get(componentID)[0]= Math.min(dimMap.get(componentID)[0],  xyd.getX());
        	dimMap.get(componentID)[1]= Math.max(dimMap.get(componentID)[1],  xyd.getX());
        	dimMap.get(componentID)[2]= Math.min(dimMap.get(componentID)[2],  xyd.getY());
        	dimMap.get(componentID)[3] = Math.max(dimMap.get(componentID)[3],  xyd.getY());
        }
        int mainComponentID = graph.getMainComponent()-1;
        
        for(V v1:getGraph().getVertices()){
        	if(graph.getNeighborCount((GeneralNode)v1) == 0){
            	Point2D xyd = transform(v1);
                xyd.setLocation(dimMap.get(mainComponentID )[1]+Math.random()*50,dimMap.get(mainComponentID)[2] - Math.random()*50 );
        	}
        }
	}
	
	@SuppressWarnings("serial")
    protected static class FRVertexData extends Point2D.Double {
        protected void offset(double x, double y) {
            this.x += x;
            this.y += y;
        }

        protected double norm() {
            return Math.sqrt(x*x + y*y);
        }
    }

	@Override
	public LayoutType getType() {
		return LayoutType.FORCE_DIRECTED;
	}
	private void readObject(ObjectInputStream stream){
		try {
			stream.defaultReadObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			HashMap<V, FRVertexData> saveMap = (HashMap<V, FRVertexData> ) stream.readObject();
			frVertexData =
					LazyMap.decorate(new HashMap<V,FRVertexData>(), new Factory<FRVertexData>() {
						@Override
						public FRVertexData create() {
							return new FRVertexData();
						}});
			
			for(Entry<V, FRVertexData> e:saveMap.entrySet()){
				frVertexData.put(e.getKey(), e.getValue());
			}
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeObject(ObjectOutputStream stream) throws IOException{
		stream.defaultWriteObject();
		HashMap<V, FRVertexData> saveMap = new HashMap<V, FRVertexData>(frVertexData);
		stream.writeObject(saveMap);
	}
}
