package com.eng.cber.na.gl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker.StateValue;

import org.apache.commons.collections15.Transformer;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.concurrent.ConcurrentJob.JobStateValue;
import com.eng.cber.na.event.util.DefaultPropertyChangeSupport;
import com.eng.cber.na.event.util.PropertyChangeSupport;
import com.eng.cber.na.gl.renderer.GLRenderer;
import com.eng.cber.na.gl.renderer.GLRenderer.AnnotationTextRenderer;
import com.eng.cber.na.gl.renderer.GLRenderer.VertexLabelRenderer;
import com.eng.cber.na.gl.renderer.NetworkGLAnnotationTextRenderer;
import com.eng.cber.na.gl.renderer.NetworkGLRenderer;
import com.eng.cber.na.gl.renderer.NetworkGLVertexLabelRenderer;
import com.eng.cber.na.gl.shape.GLShape;
import com.eng.cber.na.graph.GeneralEdge;
import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.graph.GeneralNode;
import com.eng.cber.na.graphevent.GraphMouse;
import com.eng.cber.na.layout.Layout;
import com.eng.cber.na.layout.VisualizationModel;
import com.eng.cber.na.subgraph.CreateSelectedSubgraph;
import com.eng.cber.na.transformer.EdgeDisplayTransformer;
import com.eng.cber.na.transformer.VertexDisplayTransformer;
import com.jogamp.common.nio.Buffers;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.decorators.PickableEdgePaintTransformer;
import edu.uci.ics.jung.visualization.decorators.PickableVertexPaintTransformer;
import edu.uci.ics.jung.visualization.picking.MultiPickedState;
import edu.uci.ics.jung.visualization.picking.PickedState;

/**
 * An implemented version of the background canvas on which all network objects
 * are drawn on the screen, which controls how those objects are drawn to screen.  
 * This class implements both the methods of the general visualization server, 
 * the event listener, and the canvas itself.  
 * 
 * This is an OpenGL version of the JUNG classes 
 * BasicVisualizationServer/VisualizationViewer.
 * 
 */
@SuppressWarnings("serial")
public class NetworkGLVisualizationServer<V, E> extends GLCanvas implements GLVisualizationServer<V, E>, GLEventListener {

    protected PropertyChangeSupport changeSupport = new DefaultPropertyChangeSupport(this);    
    protected List<GLRenderable> preRenderers = new LinkedList<GLRenderable>();
	protected List<GLRenderable> postRenderers = new LinkedList<GLRenderable>();	
    protected GLRenderContext<V,E> glRenderContext = new NetworkGLRenderContext<V,E>();    
	protected GLRenderer<V,E> renderer;
    protected NetworkGLScalingControl glScalingControl = new NetworkGLScalingControl(this);
    
	protected int flagSave = 0; //0: don't save; 1: jpg
    public int getFlagSave() {
		return flagSave;
	}

	public void setFlagSave(int flagSave) {
		this.flagSave = flagSave;
	}
	protected VisualizationModel<V,E> model;			
	protected GraphMouse graphMouse;
	protected String snapshotFile; 
	protected BufferedImage bufferedImage;

	public BufferedImage getBufferedImage() {
		return bufferedImage;
	}

	public String getSnapshotFile() {
		return snapshotFile;
	}

	public void setSnapshotFile(String snapshotFile) {
		this.snapshotFile = snapshotFile;
	}
	
    protected MouseListener requestFocusListener = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			requestFocusInWindow();
		}
    };
    protected ItemListener pickEventListener = new ItemListener() {
        @Override
		public void itemStateChanged(ItemEvent e) {
        	firePropertyChange("set_picked",null,null);
        }
    };
    
    public NetworkGLVisualizationServer(GLCapabilities glCapabilities) {
    	this(glCapabilities,null,new Dimension(600,600));
    }
    
	public NetworkGLVisualizationServer(GLCapabilities glCapabilities, VisualizationModel<V,E> model) {
		this(glCapabilities,model,new Dimension(600,600));
	}
	
	public NetworkGLVisualizationServer(GLCapabilities glCapabilities, VisualizationModel<V,E> model, Dimension preferredSize) {
		setPickSupport(new NetworkGLGraphElementAccessor<V,E>(this));
		setPickedVertexState(new MultiPickedState<V>());
		setPickedEdgeState(new MultiPickedState<E>());
		
        glRenderContext.setEdgeDrawPaintTransformer(new PickableEdgePaintTransformer<E>(getPickedEdgeState(), Color.black, Color.cyan));
        glRenderContext.setVertexFillPaintTransformer(new PickableVertexPaintTransformer<V>(getPickedVertexState(), Color.red, Color.yellow));

        if (model == null ){
        	glRenderContext.setVertexDisplayTransformer(new VertexDisplayTransformer(null));
        	glRenderContext.setEdgeDisplayTransformer(new EdgeDisplayTransformer(null));
        }
        
        glRenderContext.getMultiLayerTransformer().addPropertyChangeListener(this);
        
        setFocusable(true);
        addMouseListener(requestFocusListener);
                
        addGLEventListener(this);
        
	    setModel(model);
	    setPreferredSize(preferredSize);
	}


	public void removeModel() {
    	this.model = null;
    }
    
	
	@Override
	public Dimension getSize() {
		Dimension d = super.getSize();
		if(d.width <= 0 || d.height <= 0) {
			d = getPreferredSize();
		}
		return d;
	}
	
	@Override
    public VisualizationModel<V,E> getModel() {
        return model;
    }
	
    @Override
    public void setModel(VisualizationModel<V,E> model) {
    	if(model != null) {
	    	if(this.model != null) {
	    		this.model.removePropertyChangeListener(this);
	    	}
	        this.model = model;
	        this.model.addPropertyChangeListener(this);
    	}
    }
    
    @Override
	public void propertyChange(PropertyChangeEvent e) {
    	String property = e.getPropertyName();
    	Object value = e.getNewValue();
    	
    	if(property.equals("state") && value.equals(StateValue.DONE)) { // Layout done, reset the view transformers
    		glScalingControl.reset();
    	}
    	
    	if((property.equals("state") && (value.equals(StateValue.DONE) || value.equals(JobStateValue.DONE))) || 
    			(!property.equals("state") && !property.equals("progress"))) {
	    	repaint();
		    firePropertyChange(e);
    	}
	}

    @Override
	public void setGLRenderer(GLRenderer<V,E> renderer) {
	    this.renderer = renderer;
	    firePropertyChange("set_renderer",null,null);
	}
	
	@Override
	public GLRenderer<V,E> getGLRenderer() {
	    return renderer;
	}
	
	public NetworkGLScalingControl getNetworkScalingControl() {
		return glScalingControl;
	}

	@Override
    public void setGraphLayout(Layout<V,E> layout) {
    	Dimension viewSize = getPreferredSize();
    	if(this.isShowing()) {
    		viewSize = getSize();
    	}
	    model.setGraphLayout(layout, viewSize);
    }
    
    @Override
	public Layout<V,E> getGraphLayout() {
    	if (model == null )
    		return null;
    	else
    		return model.getGraphLayout();
    	
	}
	
	@Override
    public void setVisible(boolean aFlag) {
		super.setVisible(aFlag);
		if(aFlag == true) {
			Dimension d = this.getSize();
			if(d.width <= 0 || d.height <= 0) {
				d = this.getPreferredSize();
			}
			model.getGraphLayout().setSize(d);
		}
	}
   	
	@Override
    public void addPreRenderRenderable(GLRenderable renderable) {
        if(preRenderers == null) {
            preRenderers = new ArrayList<GLRenderable>();
        }
        preRenderers.add(renderable);
    }
        
    @Override
    public void removePreRenderRenderable(GLRenderable renderable) {
        if(preRenderers != null) {
            preRenderers.remove(renderable);
        }
    }
    
    @Override
    public void addPostRenderRenderable(GLRenderable renderable) {
        if(postRenderers == null) {
            postRenderers = new ArrayList<GLRenderable>();
        }
        postRenderers.add(renderable);
    }
        
    @Override
    public void removePostRenderRenderable(GLRenderable renderable) {
        if(postRenderers != null) {
            postRenderers.remove(renderable);
        }
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        changeSupport.addPropertyChangeListener(l);
    }
    
    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        changeSupport.removePropertyChangeListener(l);
    }
    
    @Override
    public PropertyChangeListener[] getPropertyChangeListeners() {
        return changeSupport.getPropertyChangeListeners();
    }

    @Override
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        changeSupport.firePropertyChange(propertyName, oldValue, newValue);
    } 
    
    @Override
    public void firePropertyChange(PropertyChangeEvent e) {
        changeSupport.firePropertyChange(e);
    }   
    
    @Override
    public PickedState<V> getPickedVertexState() {
        return glRenderContext.getPickedVertexState();
    }

    @Override
    public PickedState<E> getPickedEdgeState() {
        return glRenderContext.getPickedEdgeState();
    }
    
    @Override
    public void setPickedVertexState(PickedState<V> pickedVertexState) {
    	if(glRenderContext.getPickedVertexState() != null) {
    		glRenderContext.getPickedVertexState().removeItemListener(pickEventListener);
    	}
        glRenderContext.setPickedVertexState(pickedVertexState);
        pickedVertexState.addItemListener(pickEventListener);
    }
    
    @Override
    public void setPickedEdgeState(PickedState<E> pickedEdgeState) {
    	if(glRenderContext.getPickedEdgeState() != null) {
    		glRenderContext.getPickedEdgeState().removeItemListener(pickEventListener);
    	}
        glRenderContext.setPickedEdgeState(pickedEdgeState);
        pickedEdgeState.addItemListener(pickEventListener);
    }
    
    @Override
    public GLGraphElementAccessor<V,E> getPickSupport() {
        return glRenderContext.getPickSupport();
    }
    
    @Override
    public void setPickSupport(GLGraphElementAccessor<V,E> pickSupport) {
        glRenderContext.setPickSupport(pickSupport);
    }
    
    @Override
    public Point2D getCenter() {
        Dimension d = getSize();
        return new Point2D.Float(d.width/2, d.height/2);
    }    

    @Override
	public void setGraphMouse(GraphMouse graphMouse) {
	    this.graphMouse = graphMouse;
	    MouseListener[] ml = getMouseListeners();
	    for(int i=0; i<ml.length; i++) {
	        if(ml[i] instanceof GraphMouse) {
	            removeMouseListener(ml[i]);
	        }
	    }
	    MouseMotionListener[] mml = getMouseMotionListeners();
	    for(int i=0; i<mml.length; i++) {
	        if(mml[i] instanceof GraphMouse) {
	            removeMouseMotionListener(mml[i]);
	        }
	    }
	    MouseWheelListener[] mwl = getMouseWheelListeners();
	    for(int i=0; i<mwl.length; i++) {
	        if(mwl[i] instanceof GraphMouse) {
	            removeMouseWheelListener(mwl[i]);
	        }
	    }
	    addMouseListener(graphMouse);
	    addMouseMotionListener(graphMouse);
	    addMouseWheelListener(graphMouse);
	}
	
	@Override
	public GraphMouse getGraphMouse() {
	    return graphMouse;
	}

	@Override
	public GLRenderContext<V,E> getGLRenderContext() {
        return glRenderContext;
    }

	@Override
	public void setGLRenderContext(GLRenderContext<V,E> glRenderContext) {
        this.glRenderContext = glRenderContext;
    }

	@Override
	public void display(GLAutoDrawable glAutoDrawable) {

        if(NetworkAnalysisVisualization.startLogging)
        	NetworkAnalysisVisualization.logger.logp(java.util.logging.Level.INFO,"","","Start openGL display...");
		if(glRenderContext.getGLAutoDrawable() == null) {
	        glRenderContext.setGLAutoDrawable(glAutoDrawable);
        }
		
		GL2 gl2 = glAutoDrawable.getGL().getGL2();
		gl2.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
		gl2.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		
		for(GLRenderable renderable : preRenderers) {
			renderable.render(glAutoDrawable);
	        if(NetworkAnalysisVisualization.startLogging)
	        	NetworkAnalysisVisualization.logger.logp(java.util.logging.Level.INFO,"","","Pre Render");
		}
        
		// Only render the layout if there already is a layout 
		if (model != null && model.getGraphLayout() != null) {
			Layout<V,E> layout = model.getGraphLayout();
			
			// Reset the layout if the initial sizing of the layout
			// is incorrect.
			if (!this.getSize().equals(layout.getSize())) {
				if (this == NetworkAnalysisVisualization.getInstance().getNetworkGLVisualizationServer()){
					NetworkAnalysisVisualization.logger.logp(java.util.logging.Level.INFO,"","","Reset layout");
					NetworkAnalysisVisualization.getInstance().getVisualizationModel().resetCurrentNetworkLayout(new Dimension(this.getWidth(), this.getHeight()));
				}
			}
	        if(NetworkAnalysisVisualization.startLogging)
	        	NetworkAnalysisVisualization.logger.logp(java.util.logging.Level.INFO,"","","Render layout");
			
	        renderer.render(glRenderContext, layout);
			
	        if(NetworkAnalysisVisualization.startLogging)
	        	NetworkAnalysisVisualization.logger.logp(java.util.logging.Level.INFO,"","","Layout rendered");
		}
		
        for(GLRenderable renderable : postRenderers) {
			renderable.render(glAutoDrawable);
	        if(NetworkAnalysisVisualization.startLogging)
	        	NetworkAnalysisVisualization.logger.logp(java.util.logging.Level.INFO,"","","Post Render");
		}		
        
        if (flagSave > 0){
        	bufferedImage = getImage(glAutoDrawable);
        	
        	flagSave = 0;
        }
	}

	@Override
	public void dispose(GLAutoDrawable glautodrawable) { }

	@Override
	public void init(GLAutoDrawable glautodrawable) { 
		renderer = new NetworkGLRenderer<V,E>(glautodrawable);
		VertexLabelRenderer<V,E> vertexLabelRenderer = new NetworkGLVertexLabelRenderer<V,E>(glautodrawable);
		glRenderContext.setVertexLabelRenderer(vertexLabelRenderer);
		AnnotationTextRenderer<V,E> annotationTextRenderer = new NetworkGLAnnotationTextRenderer<V,E>(glautodrawable);
		glRenderContext.setAnnotationTextRenderer(annotationTextRenderer);
	}

	@Override
	public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width, int height) {
		GL2 gl2 = glAutoDrawable.getGL().getGL2();
		gl2.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl2.glLoadIdentity();
		
		GLU glu = new GLU();
		glu.gluOrtho2D(0.0f, width, 0.0f, height);
		
		gl2.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl2.glLoadIdentity();
		
		gl2.glViewport(0, 0, width, height);
	}
	
	public void selectAll(boolean flagSelected) {
		PickedState<GeneralNode> pickedNodes = (PickedState<GeneralNode>)getPickedVertexState();
		Graph<V,E> gg = model.getGraphLayout().getGraph();
		Collection<GeneralNode> expandedPicked = (Collection<GeneralNode>)gg.getVertices();
		
		for (GeneralNode n : expandedPicked){
			if (((GeneralGraph)gg).getNodeDisplay(n))
				pickedNodes.pick(n, flagSelected);
		}
		
		getPickedEdgeState().clear();
		
		firePropertyChange("select_all",null,null);
		NetworkAnalysisVisualization.getInstance().updateAfterNodeSelection();
		repaint();
		
		NetworkAnalysisVisualization.logger.logp(java.util.logging.Level.INFO,"","",flagSelected ? "Selected all visible nodes." : "Deselected all nodes.");
	}
	public void expandSelection() {
		PickedState<V> pickedNodes = getPickedVertexState();
		HashSet<V> expandedPicked = new HashSet<V>(pickedNodes.getPicked());
		String picked = new String();
		Graph<V, E> gg = model.getGraphLayout().getGraph();
		Collection<V> neighbors ;
		for (V n : pickedNodes.getPicked()){
			if (gg.containsVertex(n)){
				neighbors = gg.getNeighbors(n);
				expandedPicked.addAll(neighbors );
			}
		}
		
		for (V n : expandedPicked){
			if (((GeneralGraph)gg).getNodeDisplay((GeneralNode)n))
				pickedNodes.pick(n, true);
		}
		
		firePropertyChange("expand_selection",null,null);
		NetworkAnalysisVisualization.getInstance().updateAfterNodeSelection();
		repaint();
		
		NetworkAnalysisVisualization.logger.logp(java.util.logging.Level.INFO,"","","Expanded selection one step outward from: " + picked);
	}
	public void expandSelectionBasedOnWeigth(double threshold) {
		PickedState<V> pickedNodes = getPickedVertexState();
		HashSet<V> expandedPicked = new HashSet<V>(pickedNodes.getPicked());
		String picked = new String();
		Graph<V, E> gg = model.getGraphLayout().getGraph();
		for (V n : pickedNodes.getPicked()){
			if (gg.containsVertex(n)){
				Collection<E> connectedEdges = gg.getOutEdges(n);
				for (E edge: connectedEdges){
					if(((GeneralEdge)edge).getWeight()>threshold){
						expandedPicked.add(gg.getOpposite(n, edge));
					}
				}
			}
		}
		
		for (V n : expandedPicked){
			if (((GeneralGraph)gg).getNodeDisplay((GeneralNode)n))
				pickedNodes.pick(n, true);
		}
		
		firePropertyChange("expand_selection",null,null);
		NetworkAnalysisVisualization.getInstance().updateAfterNodeSelection();
		repaint();
		
		NetworkAnalysisVisualization.logger.logp(java.util.logging.Level.INFO,"","","Expanded selection one step outward from: " + picked);
	}
	public void deleteSelection() {
		PickedState<V> pickedStates = getPickedVertexState();
	
		// Invert selection using same technique as invertSelection() below
		HashSet<V> currentPicked = new HashSet<V>(pickedStates.getPicked());
		 
		for (V n : model.getGraphLayout().getGraph().getVertices()){
			pickedStates.pick(n, !currentPicked.contains(n));
		}
		
		firePropertyChange("delete_selection",null,null);
		SwingUtilities.invokeLater(new CreateSelectedSubgraph(true));
		NetworkAnalysisVisualization.getInstance().updateAfterNodeSelection();
		repaint();
		
		NetworkAnalysisVisualization.logger.logp(java.util.logging.Level.INFO,"","","Removed nodes: " + currentPicked);
	}
	
	public void invertSelection() {
		PickedState<V> pickedNodes = getPickedVertexState();
		HashSet<V> currentPicked = new HashSet<V>(pickedNodes.getPicked());

		Graph<V, E> gg = model.getGraphLayout().getGraph();
		for (V n : gg.getVertices()){
			if (((GeneralGraph)gg).getNodeDisplay((GeneralNode)n))
				pickedNodes.pick(n, !currentPicked.contains(n));
		}

		firePropertyChange("invert_selection",null,null);
		NetworkAnalysisVisualization.getInstance().updateAfterNodeSelection();
		repaint();
		
		NetworkAnalysisVisualization.logger.logp(java.util.logging.Level.INFO,"","","Inverted selection");
	}
	
	public void writeImage(String path, BufferedImage appendableImage) {
		BufferedImage image;
		
		if (appendableImage != null) {
			// Paint the visualization to the graphics object
			image = new BufferedImage(getWidth()+appendableImage.getWidth(),Math.max(getHeight(), appendableImage.getHeight()),BufferedImage.TYPE_INT_RGB);
			Graphics2D g = image.createGraphics();
			paintAll(g);
			
			// Paint the addendum to the graphics object
			g.drawImage(appendableImage, null, getWidth(), 0);
		}
		else {
			// Paint ONLY the visualization to the graphics object
			image = new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_INT_RGB);
			Graphics2D g = image.createGraphics();
			paintAll(g);
		}
		
		
		// Save the graphics object to disk
		if(!path.endsWith(".jpg")) {
			path += ".jpg";
		}
		try {
			ImageIO.write(image, "jpg", new File(path));
			
			NetworkAnalysisVisualization.logger.logp(java.util.logging.Level.INFO,"","","Saved network snapshot to " + path);
		}
		catch(IOException e) {
			System.err.println("Image creation failed.");
		}	
	}
	
	public void updateVertexShapeTransformer(Transformer<V,GLShape> transformer) {
		glRenderContext.setVertexShapeTransformer(transformer);
		firePropertyChange("update_vertex_shape",null,null);
		repaint();
	}
	
	private BufferedImage getImage(GLAutoDrawable drawable) {
		int width = drawable.getWidth();
		int height = drawable.getHeight();

		ByteBuffer pixelsRGB = Buffers.newDirectByteBuffer(width * height * 3);

		GL2 gl = drawable.getGL().getGL2();

		gl.glReadBuffer(GL.GL_BACK);
		gl.glPixelStorei(GL.GL_PACK_ALIGNMENT, 1);

		gl.glReadPixels(0, 0, width, height, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, pixelsRGB);

		int[] pixels = new int[width * height];

		int firstByte = width * height * 3;
		int sourceIndex;
		int targetIndex = 0;
		int rowBytesNumber = width * 3;

		for (int row = 0; row < height; row++) {
			firstByte -= rowBytesNumber;
			sourceIndex = firstByte;
			for (int col = 0; col < width; col++) {

				int iR = pixelsRGB.get(sourceIndex++);
				int iG = pixelsRGB.get(sourceIndex++);
				int iB = pixelsRGB.get(sourceIndex++);

				pixels[targetIndex++] = 0xFF000000
						| ((iR & 0x000000FF) << 16)
						| ((iG & 0x000000FF) << 8)
						| (iB & 0x000000FF);
			}

		}

		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		bufferedImage.setRGB(0, 0, width, height, pixels, 0, width);

		return bufferedImage;
	}
	
	public void SelectPTOfInterest(Collection<GeneralNode> selectedNodes, boolean flagSelect){
		@SuppressWarnings("unchecked")
		PickedState<GeneralNode> pickedState = (PickedState<GeneralNode>) getPickedVertexState();
		Iterator<GeneralNode> it = selectedNodes.iterator();
		while(it.hasNext()){
			pickedState.pick(it.next(), flagSelect);
		}
		NetworkAnalysisVisualization.getInstance().updateAfterNodeSelection();
		repaint();
	}	
}
