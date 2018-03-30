package com.eng.cber.na.gl.renderer;

import com.eng.cber.na.annotations.NetworkGLAnnotation;
import com.eng.cber.na.gl.GLRenderContext;
import com.eng.cber.na.layout.Layout;


/**
 * An interface that requires the classes that implement it to render themselves
 * within a context.
 * 
 * This is an OpenGL version of JUNG's interface Renderer.
 *
 */
public interface GLRenderer<V,E> {
	
	public void render(GLRenderContext<V,E> glRenderContext, Layout<V,E> layout);
	
	interface VertexRenderer<V,E> {
		public void renderVertices(GLRenderContext<V,E> glRenderContext, 
								 Layout<V,E> layout);
	}
	
	interface EdgeRenderer<V,E> {
		public void renderEdges(GLRenderContext<V,E> glRenderContext, 
							   Layout<V,E> layout);
	}
	
	interface VertexLabelRenderer<V,E> {
		public void renderVertexLabel(GLRenderContext<V,E> glRenderContext, Layout<V,E> layout, V v);
	}
	
	interface EdgeLabelRenderer<V,E> {
		public void renderEdgeLabel(GLRenderContext<V,E> glRenderContext, Layout<V,E> layout, E e);
	}


	interface AnnotationTextRenderer<V,E> {
		public void renderTextAnnotations(GLRenderContext<V,E> glRenderContext, Layout<V,E> layout, NetworkGLAnnotation currentTextAnnotation);
	}
	interface AnnotationShapeRenderer<V,E> {
		public void renderShapeAnnotations(GLRenderContext<V,E> glRenderContext, Layout<V,E> layout);
	}
	
}
