package com.eng.cber.na.gl;

import java.awt.geom.Point2D;
import java.util.Collection;

import com.eng.cber.na.layout.Layout;

/**
 * An interface that allows OpenGL-rendered objects
 * to be selected on the basis of a coordinate pair on
 * the screen to which they have been rendered.  
 * Classes that implement this interface will know how
 * to return a vertex or an edge if a particular point is
 * provided, or if two points are provided that form a 
 * rectangle, the class will know how to return the 
 * collection of vertices that fall within that rectangle.
 *
 *
 * This is an OpenGL version of JUNG's interface
 * GraphElementAccessor.
 */
public interface GLGraphElementAccessor<V, E> {
    public V getVertex(Layout<V,E> layout, double x, double y);
    public Collection<V> getVertices(Layout<V,E> layout, Point2D p1, Point2D p2);
    public E getEdge(Layout<V,E> layout, double x, double y);
}
