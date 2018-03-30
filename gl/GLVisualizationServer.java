package com.eng.cber.na.gl;


import java.awt.geom.Point2D;
import java.beans.PropertyChangeListener;

import com.eng.cber.na.event.util.PropertyChangeSupport;
import com.eng.cber.na.gl.renderer.GLRenderer;
import com.eng.cber.na.graphevent.GraphMouse;
import com.eng.cber.na.layout.Layout;
import com.eng.cber.na.layout.VisualizationModel;

import edu.uci.ics.jung.visualization.picking.PickedState;

/**
 * This interface is a "visualization server" that tracks
 * what is renderable on screen -- the JUNG VisualizationModel 
 * that is being displayed, what is prerenderable, postrenderable,
 * an interface for getting the picked items that the user
 * has chosen, repainting, and so on.
 * 
 * This is an OpenGL-supporting version of JUNG's
 * interface VisualizationServer.
 */
public interface GLVisualizationServer<V, E> extends PropertyChangeListener, PropertyChangeSupport {
    public VisualizationModel<V, E> getModel();
    public void setModel(VisualizationModel<V, E> model);
    public void setGLRenderer(GLRenderer<V, E> r);
    public GLRenderer<V, E> getGLRenderer();
    public void setGraphLayout(Layout<V, E> layout);
    public Layout<V, E> getGraphLayout();
    public void setGraphMouse(GraphMouse graphMouse);
    public GraphMouse getGraphMouse();
    public void setVisible(boolean aFlag);
    public void addPreRenderRenderable(GLRenderable renderable);
    public void removePreRenderRenderable(GLRenderable renderable);
    public void addPostRenderRenderable(GLRenderable renderable);
    public void removePostRenderRenderable(GLRenderable renderable);
    public PickedState<V> getPickedVertexState();
    public PickedState<E> getPickedEdgeState();
    public void setPickedVertexState(PickedState<V> pickedVertexState);
    public void setPickedEdgeState(PickedState<E> pickedEdgeState);
    public GLGraphElementAccessor<V, E> getPickSupport();
    public void setPickSupport(GLGraphElementAccessor<V, E> pickSupport);
    public Point2D getCenter();
    public GLRenderContext<V, E> getGLRenderContext();
    public void setGLRenderContext(GLRenderContext<V, E> glRenderContext);
    public void repaint();
}
