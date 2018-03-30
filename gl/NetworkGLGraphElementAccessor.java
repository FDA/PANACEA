package com.eng.cber.na.gl;

import java.awt.BasicStroke;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.eng.cber.na.gl.shape.GLShape;
import com.eng.cber.na.graph.GeneralEdge;
import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.graph.GeneralNode;
import com.eng.cber.na.layout.Layout;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.Layer;

/**
 * An implemented version of the selection interface
 * that describes how to select OpenGL-rendered objects
 * on the basis of a coordinate pair on the screen.
 * 
 */
public class NetworkGLGraphElementAccessor<V, E> implements GLGraphElementAccessor<V,E> {
    
    protected GLVisualizationServer<V,E> vv;
       
    public NetworkGLGraphElementAccessor(GLVisualizationServer<V,E> vv) {
        this.vv = vv;
    }
    
    @Override
	public V getVertex(Layout<V, E> layout, double x, double y) {
    	
    	Point2D ip = vv.getGLRenderContext().getMultiLayerTransformer().inverseTransform(Layer.VIEW, new Point2D.Double(x,y));
        x = ip.getX();
        y = ip.getY();
    	
        V closest = null;
        Graph<V,E> g = layout.getGraph();
        for(V v : g.getVertices()) {	
            // get the vertex location
            Point2D p = layout.transform(v);
            if(p == null) continue;
            p = vv.getGLRenderContext().getMultiLayerTransformer().transform(Layer.LAYOUT, p);
            
            double ox = x - p.getX();
            double oy = y - p.getY();

            // Instead of scaling the size of the GLShape, scale the relative coordinates -- when zoomed out ONLY (i.e. when the
            // view transformer's scale is not 1).  When visualization is zoomed in (and layout transformer's scale is not 1),
            // the detection of clicking inside a node's radius is already done correctly.
            double scale = vv.getGLRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).getScale();
            if (scale < 1) {
            	ox *= scale;
            	oy *= scale;
            }
            
            GLShape shape = vv.getGLRenderContext().getVertexShapeTransformer().transform(v);
            
            if(shape.contains(ox, oy) && (((GeneralGraph)g).getNodeDisplay((GeneralNode)v))) {                    	
        		closest = v;
            }
        }
        return closest;
    }
    
    @Override
	public Collection<V> getVertices(Layout<V,E> layout, Point2D p1, Point2D p2) {
    	Set<V> pickedVertices = new HashSet<V>();
    	
    	double x_min = Math.min(p1.getX(), p2.getX());
    	double y_min = Math.min(p1.getY(), p2.getY());
    	double x_max = Math.max(p1.getX(), p2.getX());
    	double y_max = Math.max(p1.getY(), p2.getY());
    	
    	double x,y;
    	
    	Graph<V,E> g = layout.getGraph();
    	for(V v : g.getVertices()) {
    		Point2D p = layout.transform(v);
    		p = vv.getGLRenderContext().getMultiLayerTransformer().transform(p);
    		x = p.getX();
    		y = p.getY();
    		if(x >= x_min && x <= x_max && y >= y_min && y <= y_max && (((GeneralGraph)g).getNodeDisplay((GeneralNode)v))) {
    			pickedVertices.add(v);
    		}    		
    	}
    	return pickedVertices;
    }
    
    @Override
	public E getEdge(Layout<V, E> layout, double x, double y) {
 
        Point2D ip = vv.getGLRenderContext().getMultiLayerTransformer().inverseTransform(Layer.VIEW, new Point2D.Double(x,y));
        x = ip.getX();
        y = ip.getY();
        
        E edge = null;
               
    	Graph<V,E> g = layout.getGraph();
        int ctr = 0;
        for(E e : g.getEdges()) {
        	ctr = ctr + 1;
        	Pair<V> endpoints = g.getEndpoints(e);
        	V v1 = endpoints.getFirst();
        	V v2 = endpoints.getSecond();
        	
        	Point2D p1 = layout.transform(v1);
        	p1 = vv.getGLRenderContext().getMultiLayerTransformer().transform(Layer.LAYOUT, p1);
        	
        	Point2D p2 = layout.transform(v2);
        	p2 = vv.getGLRenderContext().getMultiLayerTransformer().transform(Layer.LAYOUT, p2);
        	
        	float lineWidth = ((BasicStroke)vv.getGLRenderContext().getEdgeStrokeTransformer().transform(e)).getLineWidth();
        	
        	if(edgeContainsPoint(p1, p2, lineWidth, x, y) && (((GeneralGraph)g).getEdgeDisplay((GeneralEdge)e))) {        	
        		edge = e;
        	}
        }
		return edge;
    }


	// checks to see if the point is contained by the rectangle formed by the edge
	private static boolean edgeContainsPoint(Point2D v1, Point2D v2, double lineWidth, double xp, double yp) {
		double x1 = v1.getX();
		double y1 = v1.getY();
		double x2 = v2.getX();
		double y2 = v2.getY();
		
		
		// If the vertices are literally on top of each other,
		// then we definitely did not click on an edge.
		// This fixes a bug that appears in large networks under
		// layouts that might place vertices on top of each other
		// (e.g, the PCA layout -- but not the Circle layout).
		if (x1 == x2 && y1 == y2) {
			return false;
		}
			
		if((y2-y1)*(yp-y1) < (x1-x2)*(xp-x1)) {
			return false;
		}
		
		if((y2-y1)*(yp-y2) > (x1-x2)*(xp-x2)) {
			return false;
		}
		
		double width = Math.max(lineWidth, 2);
		
		double dist = Math.sqrt((y2-y1)*(y2-y1)+(x1-x2)*(x1-x2));
		double wx = width * (y2-y1) / dist;
		double wy = width * (x1-x2) / dist;
		
		double xa = x1 + wx;
		double ya = y1 + wy;
		
		if((x2-x1)*(yp-ya) < (y2-y1)*(xp-xa)) {
			return false;
		}
		
		xa = x1 - wx;
		ya = y1 - wy;
		
		if((x2-x1)*(yp-ya) > (y2-y1)*(xp-xa)) {
			return false;
		}		
		
		return true;	
	}
   
}

