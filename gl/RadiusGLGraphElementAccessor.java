package com.eng.cber.na.gl;

import java.awt.geom.Point2D;
import java.util.Collection;

import com.eng.cber.na.layout.Layout;

/**
 * An implemented version of the selection interface
 * that describes how to select OpenGL-rendered vertices
 * (only) on the basis of which is closest to a point
 * on screen.
 * 
 */
public class RadiusGLGraphElementAccessor<V, E> implements GLGraphElementAccessor<V, E>  {

	@Override
	public V getVertex(Layout<V, E> layout, double x, double y) {
		double minDistance = Double.MAX_VALUE;
        V closest = null;
        for(V v : layout.getGraph().getVertices()) {
            Point2D p = layout.transform(v);
            if (Double.compare(p.getY(), Double.NaN) == 0){
            	System.out.println("NAN found in getVertex() ");
            	p.setLocation(p.getX(), 0);
            }
            double dx = p.getX() - x;
            double dy = p.getY() - y;
            double dist_sq = dx * dx + dy * dy;
            if (dist_sq < minDistance) {
                minDistance = dist_sq;
                closest = v;
            }
        }
		return closest;
	}

	@Override
	public Collection<V> getVertices(Layout<V, E> layout, Point2D p1, Point2D p2) {
		return null;
	}

	@Override
	public E getEdge(Layout<V, E> layout, double x, double y) {
		return null;
	}

}
