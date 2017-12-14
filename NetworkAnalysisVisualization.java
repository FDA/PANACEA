package com.eng.cber.na;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.imageio.ImageIO;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker.StateValue;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.collections15.Transformer;

import com.eng.cber.na.command.AboutCommand;
import com.eng.cber.na.command.AddAnnotationDialogCommand;
import com.eng.cber.na.command.ClearAllAnnotations;
import com.eng.cber.na.command.CloseProjectCommand;
import com.eng.cber.na.command.CommunityDetectionCommand;
import com.eng.cber.na.command.CompareRealVsSimCommand;
import com.eng.cber.na.command.CreateCoNetwork;
import com.eng.cber.na.command.CreateSelectedSubGraph;
import com.eng.cber.na.command.CreateWeightedNetwork;
import com.eng.cber.na.command.DeleteSelectionCommand;
import com.eng.cber.na.command.DisplayLabelCommand;
import com.eng.cber.na.command.ExitCommand;
import com.eng.cber.na.command.ExpandSelectionCommand;
import com.eng.cber.na.command.ExportCSVGraph;
import com.eng.cber.na.command.ExportPajekGraph;
import com.eng.cber.na.command.FlipHorizontalCommand;
import com.eng.cber.na.command.FlipVerticalCommand;
import com.eng.cber.na.command.ImportBOGraph;
import com.eng.cber.na.command.ImportBOGraphCommand;
import com.eng.cber.na.command.ImportFullVAERS;
import com.eng.cber.na.command.ImportGeneralGraph;
import com.eng.cber.na.command.InvertSelectionCommand;
import com.eng.cber.na.command.OpenProjectCommand;
import com.eng.cber.na.command.PullReportsExcludingTerms;
import com.eng.cber.na.command.PullReportsWithAllTerms;
import com.eng.cber.na.command.PullReportsWithSelectedTerms;
import com.eng.cber.na.command.RemoveEdge;
import com.eng.cber.na.command.RemoveIsolates;
import com.eng.cber.na.command.RemoveLastAnnotation;
import com.eng.cber.na.command.RemoveNode;
import com.eng.cber.na.command.RemovePendants;
import com.eng.cber.na.command.ResetGraphCommand;
import com.eng.cber.na.command.ResetLayout;
import com.eng.cber.na.command.ResetLayoutCommand;
import com.eng.cber.na.command.RetrieveSimilarReportsCommand;
import com.eng.cber.na.command.RetrieveSyntheticSimilarCases;
import com.eng.cber.na.command.RotateClockwiseCommand;
import com.eng.cber.na.command.RotateCounterClockwiseCommand;
import com.eng.cber.na.command.RunSimulatorCommand;
import com.eng.cber.na.command.SaveLogCommand;
import com.eng.cber.na.command.SaveNetworkSnapshotCommand;
import com.eng.cber.na.command.SaveProjectCommand;
import com.eng.cber.na.command.SelectAllNodesCommand;
import com.eng.cber.na.command.SelectNodesFromListCommand;
import com.eng.cber.na.command.SetNodeSizeCommand;
import com.eng.cber.na.command.SetSimilarityThresholdCommand;
import com.eng.cber.na.command.SwitchNetwork;
import com.eng.cber.na.command.ToggleDisplayCommand;
import com.eng.cber.na.command.ToggleShowClusterColoring;
import com.eng.cber.na.command.ViewDocumentCommand;
import com.eng.cber.na.command.ViewNodeMetricPlotCommand;
import com.eng.cber.na.command.ViewPropertiesCommand;
import com.eng.cber.na.command.ViewSMQSimilarityPlotCommand;
import com.eng.cber.na.command.ViewShortcutsCommand;
import com.eng.cber.na.command.ZoomInCommand;
import com.eng.cber.na.command.ZoomOutCommand;
import com.eng.cber.na.command.configCommand;
import com.eng.cber.na.command.util.CommandButton;
import com.eng.cber.na.command.util.CommandCheckBox;
import com.eng.cber.na.command.util.CommandComboBox;
import com.eng.cber.na.command.util.CommandMenuItem;
import com.eng.cber.na.command.util.CommandRadioButtonMenu;
import com.eng.cber.na.event.AdjacentTableSortListener;
import com.eng.cber.na.event.GraphTreeSelectionListener;
import com.eng.cber.na.event.PopupMenu;
import com.eng.cber.na.event.PopupMenuItem;
import com.eng.cber.na.event.mouse.AbstractPopupMouseEvent.SelectionType;
import com.eng.cber.na.event.mouse.NetworkPopupMouseEvent;
import com.eng.cber.na.gl.NetworkGLVisualizationServer;
import com.eng.cber.na.gl.shape.GLShape;
import com.eng.cber.na.graph.AbstractCreateGraph;
import com.eng.cber.na.graph.BasicGraphDataCalculator;
import com.eng.cber.na.graph.BuildGraphFromAllReports;
import com.eng.cber.na.graph.BuildGraphFromReportSet;
import com.eng.cber.na.graph.FDAGraph;
import com.eng.cber.na.graph.GeneralEdge;
import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.graph.GeneralNode;
import com.eng.cber.na.graph.GraphLoader;
import com.eng.cber.na.graph.Graph_Object;
import com.eng.cber.na.graph.ReadJungGraph;
import com.eng.cber.na.graphevent.DefaultDisplayGraphMouseEvent;
import com.eng.cber.na.graphevent.DefaultPopupGraphMouseEvent;
import com.eng.cber.na.graphevent.DefaultSelectDragGraphMouseEvent;
import com.eng.cber.na.graphevent.DefaultZoomGraphMouseEvent;
import com.eng.cber.na.graphevent.GraphMouseEventHandler;
import com.eng.cber.na.layout.AbstractLayout;
import com.eng.cber.na.layout.CreateLayout;
import com.eng.cber.na.layout.Layout;
import com.eng.cber.na.layout.Layout.LayoutType;
import com.eng.cber.na.layout.NetworkVisualizationModelContainer;
import com.eng.cber.na.layout.PCALayout;
import com.eng.cber.na.model.AdjacentVertexTreeModel;
import com.eng.cber.na.model.GraphTreeModel;
import com.eng.cber.na.model.HeadAdjacentVertexTableModel;
import com.eng.cber.na.renderer.AdjacentVertexTreeCellRenderer;
import com.eng.cber.na.renderer.GraphTreeCellRenderer;
import com.eng.cber.na.renderer.HeadTableCellRenderer;
import com.eng.cber.na.renderer.ListCellToolTipRenderer;
import com.eng.cber.na.transformer.EdgeDrawPaintTransformer;
import com.eng.cber.na.transformer.EdgeStrokeTransformer;
import com.eng.cber.na.transformer.VertexDisplayTransformer;
import com.eng.cber.na.transformer.VertexFillPaintTransformer;
import com.eng.cber.na.transformer.VertexLabelBoldedTransformer;
import com.eng.cber.na.transformer.VertexLabelPaintTransformer;
import com.eng.cber.na.transformer.VertexLabelTransformer;
import com.eng.cber.na.transformer.VertexShapeTransformer;
import com.eng.cber.na.util.PreferenceFileIO;
import com.eng.cber.na.vaers.VAERS_Comparator.ComparatorType;
import com.eng.cber.na.vaers.VAERS_Comparator.Direction;
import com.eng.cber.na.vaers.VAERS_Edge;
import com.eng.cber.na.vaers.VAERS_Edge.EdgeType;
import com.eng.cber.na.vaers.VAERS_Node;
import com.eng.cber.na.vaers.VAERS_Node.NodeType;
import com.jogamp.newt.event.KeyEvent;

import edu.uci.ics.jung.visualization.picking.PickedState;

/****
 * The main class -- for the network analysis GUI.
 * 
 */
public class NetworkAnalysisVisualization extends JFrame implements PropertyChangeListener{
	public static Logger logger;
	
	public static boolean startLogging = false;
	public static boolean isStartLogging() {
		return startLogging;
	}
	public static void setStartLogging(boolean startLogging) {
		NetworkAnalysisVisualization.startLogging = startLogging;
	}
	
	private static String appDir = System.getProperty("user.dir").replace("\\", "/");
	private static String outputDir = appDir + "/output";
	private static String logDir = appDir + "/log";
	private static String prjDir = appDir + "/prj";
	private static String dataDir = appDir + "/data";
	private static String simulatorDir = dataDir + "/Simulator";
	
	public static String getAppDir() {
		return appDir;
	}
	public static void setAppDir(String appDir) {
		NetworkAnalysisVisualization.appDir = appDir;
	}
	
	public static String getOutputDir() {
		return outputDir;
	}
	public static void setOutputDir(String outputDir) {
		NetworkAnalysisVisualization.outputDir = outputDir;
	}
	
	public static String getLogDir() {
		return logDir;
	}
	public static void setLogDir(String logDir) {
		NetworkAnalysisVisualization.logDir = logDir;
	}
	
	public static String getPrjDir() {
		return prjDir;
	}
	public static void setPrjDir(String prjDir) {
		NetworkAnalysisVisualization.prjDir = prjDir;
	}

	public static String getDataDir() {
		return dataDir;
	}
	public static void setDataDir(String dataDir) {
		NetworkAnalysisVisualization.dataDir = dataDir;
	}
	
	public static String getSimulatorDir() {
		return simulatorDir;
	}
	public static void setSimulatorDir(String simulatorDir) {
		NetworkAnalysisVisualization.simulatorDir = simulatorDir;
	}
	
	private static HashMap<String,String> tooltips = new HashMap<String,String>();
	private static final int[] colWidths = {35,200,100,80,80,80,100};
	
	private static final String[] vertexSizeTypes = {"Degree", "Closeness","Betweenness","Report Count","Strength" };
	
	/**layoutTypes: select a layout from the Combo Box; the order of the layoutTypes should match that defined in Layout.LayoutType. */  
	private static final String[] layoutTypes = AbstractLayout.LayoutTypeToString.values().toArray(new String[0]);
	
	public static enum WeightingScheme {
		LinSimilarity, Singleton, Dyads, Triplets, Quadruplets, Quintuplets, Sextuplets}
	
	private static String logFile = logDir + "/PANACEA_Activities.log";
	private static long tStart, tNow;
	
	private static NetworkAnalysisVisualization instance;
	private static String boPath = null;
	private static int inputDualID = 2; 
	
	/** Used for general type networks **/
	private ReadJungGraph graphReader;
	/** Used for PANACEA type networks **/
	private GraphLoader graphLoader;
	
	private NetworkGLVisualizationServer<GeneralNode,GeneralEdge> vv;
	private GeneralGraph graph;
	/** The network type for the current graph.**/
	private int dualID;
	private boolean isDual;
	private GeneralGraph reducedGraph;
	
	private JTree graphTree;
	private GraphTreeModel graphTreeModel;
	
	private JTree neighborTree;
	private AdjacentVertexTreeModel treeModel;
	
	private String selNodeSizeType = "Degree"; 
	private String selLayoutType = "Principal Components";
	
	private WeightingScheme currentWeightingScheme;
	
	private NACommandActionListener commandActionListener = new NACommandActionListener();
	
	// Default values for Preferences.
	// Used if the preferences file cannot be found or read.
	private int maxEdgeSizeToDisplay = 100000;	
	private int lineToDrawAtOnceSize = 10000;
	private int edgeDarkness = 90;
	private int repaintInterval = 2;
	
	
	// Swing items needed outside their creation
	private JLabel networkTypeLabel, hiddenEdgeLabel, statusLabel;
	private JTable head;
	private JSplitPane splitPane;
	private JPanel bottomPanel;
	private JPanel info_p;
	private JPanel layoutLabelPanel;
	private CommandCheckBox labelChk;
	private CommandComboBox layoutCombo, sizeCombo;
	private JTextField vertCount;
	private JScrollPane gtsp;
	private CommandButton zoomOut, zoomIn; 
	private CommandButton flipVertical, flipHorizontal;
	private CommandButton rotateClockwise, rotateCounterClockwise;
	private CommandButton resetLayoutButton; 
	private JMenuBar menuBar;
	private JMenu select, view, reduce, network, documentRetrieval;
	private JMenu exportToOtherFormat, extractByTypeSubmenu, pullDocumentsSubmenu, weightingNetwork;
	private PopupMenu exportNodeProp;
	private CommandMenuItem viewSimilarityPlot;
	private CommandMenuItem compareRealVsSim;
	private CommandMenuItem saveLog;
	private CommandMenuItem saveProject, closeProject, saveSnapshot;
	private Map<String,JTextField> itemAttributeMap;
	
	private Boolean flagShowBottomPanel = true;
	private boolean flagShowClusterColoring = true;

	public Boolean getFlagShowBottomPanel() {
		return flagShowBottomPanel;
	}

	public GraphTreeModel getGraphTreeModel() {
		return graphTreeModel;
	}

	public void setGraphTreeModel(GraphTreeModel graphTreeModel) {
		this.graphTreeModel = graphTreeModel;
	}


	public void setGraphTree(JTree graphTree) {
		this.graphTree = graphTree;
	}

	public void setSelLayoutType(String selLayoutType) {
		this.selLayoutType = selLayoutType;
		this.setLayoutType(Layout.LayoutType.values()[(Arrays.asList(layoutTypes).indexOf(selLayoutType))]);
	}

	public int getRepaintInterval() {
		return repaintInterval;
	}

	public void setRepaintInterval(int repaintInterval) {
		this.repaintInterval = repaintInterval;
	}

	public int getLineToDrawAtOnceSize() {
		return lineToDrawAtOnceSize;
	}

	public void setLineToDrawAtOnceSize(int lineToDrawAtOnceSize) {
		this.lineToDrawAtOnceSize = lineToDrawAtOnceSize;
	}

	public int getMaxEdgeSizeToDisplay() {
		return maxEdgeSizeToDisplay;
	}

	public void setMaxEdgeSizeToDisplay(int maxEdgeSizeToDisplay) {
		this.maxEdgeSizeToDisplay = maxEdgeSizeToDisplay;
	}

	public int getEdgeDarkness() {
		return edgeDarkness;
	}

	public void setEdgeDarkness(int edgeDarkness) {
		this.edgeDarkness = edgeDarkness;
	}

	public JLabel getStatusLabel() {
		return statusLabel;
	}

	public void setStatusLabel(JLabel statusLabel) {
		this.statusLabel = statusLabel;
	}

	public JScrollPane getGtsp() {
		return gtsp;
	}

	public  static HashMap<String, String> getTooltips() {
		return tooltips;
	}
	
	public String getSelLayoutType() {
		return selLayoutType;
	}
	public int getSelLayoutTypeIndex() {
		return Arrays.asList(layoutTypes).indexOf(selLayoutType);
	}

	public void setLayoutTypeByIndex(int index) {
		layoutCombo.setSelectedIndex(index);
	}

	public String getSelNodeSizeType() {
		return selNodeSizeType;
	}

	public JPanel getInfo_p() {
		return info_p;
	}

	public NACommandActionListener getCommandActionListener() {
		return commandActionListener;
	}

	private NetworkAnalysisVisualization() {
		super("Pattern-based and Advanced Network Analyzer for Clinical Evaluation and Assessment (PANACEA)");
		ToolTipManager.sharedInstance().setDismissDelay((int)(1.5*ToolTipManager.sharedInstance().getDismissDelay()));
		ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		 
		populateToolTips();
		
		graph = new FDAGraph(0);
		graph.setName("Start up");
		GLProfile glProfile = GLProfile.getDefault();
		GLCapabilities glCapabilities = new GLCapabilities(glProfile);
		if(startLogging)
			logger.logp(java.util.logging.Level.INFO,"","","Hardware Acceleration " + (glCapabilities.getHardwareAccelerated() ? "Enabled" : "Disabled"));
				
		vv = new NetworkGLVisualizationServer<GeneralNode,GeneralEdge>(glCapabilities);
		vv.addPropertyChangeListener(this);
				
		GraphMouseEventHandler mouseEventHandler = new GraphMouseEventHandler();
		mouseEventHandler.addGraphMouseEvent(new DefaultSelectDragGraphMouseEvent());
		mouseEventHandler.addGraphMouseEvent(new DefaultDisplayGraphMouseEvent());
		mouseEventHandler.addGraphMouseEvent(new DefaultZoomGraphMouseEvent());
		mouseEventHandler.addGraphMouseEvent(new DefaultPopupGraphMouseEvent(commandActionListener));
		vv.setGraphMouse(mouseEventHandler);
		
		vv.getGLRenderContext().setVertexLabelTransformer(new VertexLabelTransformer.NameTransformer());
		vv.getGLRenderContext().setVertexLabelPaintTransformer(new VertexLabelPaintTransformer(vv.getPickedVertexState(),vv.getPickedEdgeState()));
		vv.getGLRenderContext().setVertexLabelBoldedTransformer(new VertexLabelBoldedTransformer(vv.getPickedVertexState()));
		vv.getGLRenderContext().setEdgeDrawPaintTransformer(new EdgeDrawPaintTransformer(vv.getPickedVertexState(),vv.getPickedEdgeState()));
		vv.getGLRenderContext().setVertexShapeTransformer(new VertexShapeTransformer.DegreeTransformer(graph));
		vv.getGLRenderContext().setEdgeStrokeTransformer(new EdgeStrokeTransformer(graph));
		vv.getGLRenderContext().setVertexFillPaintTransformer(new VertexFillPaintTransformer(vv.getPickedVertexState(),vv.getPickedEdgeState()));
		vv.getGLRenderContext().setVertexDisplayTransformer(new VertexDisplayTransformer(graph));
		
		setLayout(new BorderLayout());
		menuBar = getMenu();
		JPanel menuAndControlBarPanel = new JPanel();
		menuAndControlBarPanel.setLayout(new BorderLayout());
		menuAndControlBarPanel.add(menuBar, BorderLayout.NORTH);
		menuAndControlBarPanel.add(getControlBar(), BorderLayout.SOUTH);
		add(menuAndControlBarPanel,BorderLayout.NORTH);
		JPanel vizPanel = new JPanel();
		vizPanel.setLayout(new BorderLayout());
		vizPanel.setMinimumSize(new Dimension(768,300));
		vizPanel.add(getMainPanel(),BorderLayout.CENTER);
		bottomPanel = getBottomPanel();
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, vizPanel, bottomPanel);
		splitPane.setResizeWeight(0.9);
		add(splitPane, BorderLayout.CENTER);
		pack();
		setExtendedState(Frame.MAXIMIZED_BOTH); // open in full screen/maximized
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
	}
	
	public static NetworkAnalysisVisualization getInstance() {
		if(instance == null) {
			instance = new NetworkAnalysisVisualization();
		}
		return instance;
	}

	public WeightingScheme getWeightingScheme() {
		return currentWeightingScheme;
	}
	
	public void setWeightingScheme(WeightingScheme ws) {
		currentWeightingScheme = ws;
		Component[] components = weightingNetwork.getMenuComponents();
		for (int i = 0; i < components.length; i++) {
			JMenuItem currentMenu = (JMenuItem) components[i];
			if(WeightingScheme.valueOf(currentMenu.getText()) == ws){
				currentMenu.setSelected(true);
				return;
			}
		}
	}
	
	public GraphLoader getUnderlyingData() {
		return graphLoader;
	}
	public GraphLoader getGraphLoader() {
		return graphLoader;
	}
	public void setGraphLoader(GraphLoader gl) {
		graphLoader = gl;
	}
	
	public ReadJungGraph getGraphReader() {
		return graphReader;
	}
	
	/** 
	 * Sets the 'graphReader' field and then sets the 'graph' field to the graph that was
	 * contained in the graphReader.  Can also be told whether to calculate islands and
	 * betweenness/closeness, which it passes on.<br/><br/>
	 * 
	 * This method calls loadGraphAndCalculate() to finish processing the Graph object.<br/><br/>
	 * 
	 * This method should be used when loading a new general type data source.
	 */
	public void setGraphLoaderAndCalculateGeneral(ReadJungGraph graphReader, String name, boolean calcIslands, boolean calcBetweenClose) {
		this.graphReader = graphReader; 
		graphReader.LoadGraph();
		if (graphReader.graph.getVertexCount() == 0) {
			tNow = System.currentTimeMillis();
			enableControls(false);
			if(startLogging)
				logger.logp(java.util.logging.Level.INFO,"","","PANACEA started");
			setVisible(true);
		}
		else {
			try {
				graph = graphReader.graph;
				LoadGraphAndCalculate(graph, name, calcIslands, calcBetweenClose);
			} catch (IllegalArgumentException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	/** 
	 * Sets the 'graphLoader' field and then builds a new Graph object to put in the
	 * 'graph' field.  Can also be told whether to calculate islands and
	 * betweenness/closeness, which it passes on.<br/><br/>
	 * 
	 * This method calls loadGraphAndCalculate() to finish processing the Graph object.<br/><br/>
	 * 
	 * This method should be used when loading a new PANACEA type data source.
	 */
	public void setGraphLoaderAndCalculate(GraphLoader graphLoader, String name, boolean calcIslands, boolean calcBetweenClose) {
		this.graphLoader = graphLoader;
		if (graphLoader.getOrigNodeHash().size() == 0) {
			tNow = System.currentTimeMillis();
			enableControls(false);
			if (startLogging)
				logger.logp(java.util.logging.Level.INFO,  "", "", "PANACEA started.");

			setVisible(true);
		}
		else {
			AbstractCreateGraph buildGraph;
			if (getDualState())
				buildGraph= new BuildGraphFromReportSet(-1, true);
			else
				buildGraph= new BuildGraphFromAllReports();

			try {
				graph = buildGraph.getFinalNetwork();
				LoadGraphAndCalculate(graph, name, calcIslands, calcBetweenClose);
			} catch (IllegalArgumentException e1) {
				e1.printStackTrace();
			} catch (InstantiationException e1) {
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
			}
		}
		
		NetworkAnalysisVisualization.getInstance().setCursor(Cursor.getDefaultCursor());
	}
	
	/**
	 * Performs additional pre-processing on new Graph objects, generally loaded
	 * from a new data source.  It finds components in the graph, and the min and
	 * max for degree, strength, report count, and edge weight.  Also starts the
	 * islands and betweenness/closeness calculations if requested.<br/><br/>
	 * 
	 * This method calls setGraphAndReload() to switch the visualization and the
	 * network tree to the new data source that was loaded.
	 */
	private void LoadGraphAndCalculate(GeneralGraph graph, String name, boolean calcIslands, boolean calcBetweenClose) {	
		try {
			graph.setName(name);
			if(graph instanceof FDAGraph) {
				graph.setMedDRALevel(graphLoader.getLevel());
				graph.setMedDRACounts((Map<Object, Integer>)(Map<?, Integer>) graphLoader.getMeddraCounts());
				graph.setTotalReportSize(graphLoader.getOrigTotalReportCount());
			}
			
			NALog("INFO", "Graph " + name + " Built");  

			// Calculate the first round of data for the graph
			// e.g. Degree, Strength, Edge Weight
			if (graph.getEdgeCount()>0) {
				BasicGraphDataCalculator graphCalc = new BasicGraphDataCalculator(graph);
				
				NetworkAnalysisVisualization.NALog("Start to find Components for: " + name);
				
				graphCalc.identifyComponents();
				
				NetworkAnalysisVisualization.NALog("Found Components for: " + name);
				graphCalc.identifyMinAndMaxForVertices();
				NetworkAnalysisVisualization.NALog("Min and max  for nodes in " + name + " identified.");  
				graphCalc.identifyMinAndMaxForEdges();
				NetworkAnalysisVisualization.NALog("Min and max  for edges in " + name + " identified.");  
				graphCalc.setAllDisplaysToTrue();

				if (calcIslands) {
					graph.calculateIslands();
				}
				
				if (calcBetweenClose) {
					graph.calculateBetweenClose();
				}
				
				setGraphAndReload(graph);
				setComboString();
			}
			enableControls(true);
			setMenusDual(this.dualID);
			setCursor(Cursor.getDefaultCursor());
			getStatusLabel().setText("Network created!");
			getHead().updateUI();
			setVisible(true);
			
			if (graph.getEdgeCount() > getMaxEdgeSizeToDisplay()) {
				JOptionPane.showMessageDialog(this,
											  "<html><p style=\"width:400px;\">This graph has more than " +
											  getMaxEdgeSizeToDisplay() +
											  " edges, which is currently the maximum set to display.  Please be aware that some edges may be hidden unless you zoom in or select them." +
											  "<br/><br/>You may edit the maximum number of displayable edges in the Preferences menu.  " +
											  "Fewer edges will improve the performance and responsiveness of the viewer, while additional edges will degrade it.</p></html>",
											  "Edges Hidden", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
		} 
	}
	

	
	public List<GeneralNode> getNodes() {
		ArrayList<GeneralNode> retList = new ArrayList<GeneralNode>(graphLoader.getNodes());
		return retList;
	}
	
	public JTree getAdjacentTree() {
		return neighborTree;
	}
	
	public String getLogFilePath() {
		return logFile;
	}

	public GeneralGraph getGraph() {
		return graph;
	}
		
	public NetworkGLVisualizationServer<GeneralNode,GeneralEdge> getNetworkGLVisualizationServer() {
		return vv;
	}
	
	public JTree getNetworkTree() {
		return graphTree;
	}

	public NetworkVisualizationModelContainer getVisualizationModel() {
		return (NetworkVisualizationModelContainer)vv.getModel();
	}
	
	/**
	 * Sets the info to be displayed in the Information Panel, including tooltips,
	 * and sets the neighbors to display in the Adjacent Nodes Panel.
	 * @param obj The node or edge that should have its information displayed
	 */
	public void setDisplay(Graph_Object obj) {
		String contents = "<html> ";
		if (obj.getObjectType() == Graph_Object.ObjectType.NODE)
		{
			GeneralNode node = (GeneralNode)obj;
			if (node instanceof VAERS_Node){
				itemAttributeMap.get("type").setText(((VAERS_Node)node).getNodeType().toString());
				itemAttributeMap.get("count").setText(((VAERS_Node)node).getReports().size() + "");
				Set<Object> reports = (Set<Object>)((VAERS_Node)node).getReports();
				for(Object report: reports)
				{
					boolean isSpecialNode = false;
					if (graph.isDual())
					{
						if(graphLoader.getOrigNodeHash().get(report)!=null )
							if ((graphLoader.getOrigNodeHash().get(report).getNodeType()) != NodeType.values()[graph.getDual()-1])
								isSpecialNode = true;
					}
					if (isSpecialNode)
						contents = contents + "<font color=RED>" + report.toString() + "</font><br/>";
					else
						contents = contents + report.toString() + "<br/>";
				}
				contents = contents + " </html>";
				
				itemAttributeMap.get("name").setToolTipText(contents);
			}
			else{
				itemAttributeMap.get("type").setText("General");
				itemAttributeMap.get("count").setText("N/A");
			}
				
			itemAttributeMap.get("item").setText("Node");
			itemAttributeMap.get("name").setText(node.getID());
			
			itemAttributeMap.get("betweenness").setText(!graph.areBetweenCloseCalculated() ? (graph.isCalculatingBetweenClose() ? "Calculating..." : "Not Calculated") : String.format("%1.4f", graph.getBetweenness(node)));
			itemAttributeMap.get("closeness").setText(!graph.areBetweenCloseCalculated() ? (graph.isCalculatingBetweenClose() ? "Calculating..." : "Not Calculated") : String.format("%1.4f",graph.getCloseness(node)));
			itemAttributeMap.get("degree").setText(String.valueOf(graph.getDegree(node)));
			itemAttributeMap.get("strength").setText(String.format("%1.4f", graph.getStrength(node) ));
			itemAttributeMap.get("weight").setText("N/A"); 		
			}
		else {
			GeneralEdge edge = (GeneralEdge)obj;
			itemAttributeMap.get("item").setText("Edge");
			itemAttributeMap.get("name").setText(edge.getID().toString());
			if (edge instanceof VAERS_Edge){
				@SuppressWarnings("unchecked")
				Set<Object> reports = (Set<Object>)((VAERS_Edge)edge).getReports();
				for(Object report: reports)
				{
					boolean isSpecialNode = false;
					if(graph.isDual())
						if(graphLoader.getOrigNodeHash().get(report)!=null )
							if ((graphLoader.getOrigNodeHash().get(report).getNodeType()) != NodeType.values()[graph.getDual()-1])
								isSpecialNode = true;
					
					if(isSpecialNode)
						contents = contents + "<font color=RED>" + report.toString() + "</font><br/>";
					else
						contents = contents + report.toString() + "<br/>";
				}
				contents = contents + " </html>";
	
				itemAttributeMap.get("name").setToolTipText(contents);

				itemAttributeMap.get("type").setText(((VAERS_Edge)edge).getEdgeType().toString());
				itemAttributeMap.get("count").setText(((VAERS_Edge)edge).getReports().size() + "");
			}
			else {
				itemAttributeMap.get("type").setText("General");
				itemAttributeMap.get("count").setText("N/A");
			}
			itemAttributeMap.get("betweenness").setText("N/A");

			itemAttributeMap.get("closeness").setText("N/A");
			itemAttributeMap.get("degree").setText("N/A");
			itemAttributeMap.get("strength").setText("N/A");
			itemAttributeMap.get("weight").setText(String.format("%1.4f", edge.getWeight()));
		}
		treeModel.update(obj,ComparatorType.TYPE,Direction.ASCENDING);
		neighborTree.expandPath(new TreePath(treeModel.getPathToRoot((DefaultMutableTreeNode)treeModel.getRoot())));
	}
	
	/** Clear node info in the Information Panel and clear the adjacent nodes from the Adjacent Nodes Panel **/
	public void clearDisplay() {
		// Clear Information Box
		itemAttributeMap.get("item").setText("");
		itemAttributeMap.get("name").setText("");
		itemAttributeMap.get("type").setText("");
		itemAttributeMap.get("count").setText("");
		itemAttributeMap.get("betweenness").setText("");
		itemAttributeMap.get("closeness").setText("");
		itemAttributeMap.get("degree").setText("");
		itemAttributeMap.get("name").setToolTipText("Name of the item.");
		itemAttributeMap.get("strength").setText("");
		itemAttributeMap.get("weight").setText("");

		// Clear Adjacent Nodes
		treeModel.clear();
	}
	
	
	public void setSelectedVertexCount(int n) {
		vertCount.setText("" + n);
	}
	
	private void populateToolTips() {
		tooltips.put("Name", "Name of the item.");
		tooltips.put("Degree", "Measure of the proportion of nodes to which the node connects directly.");
		tooltips.put("Closeness", "Measure of how few edges are needed to reach all other nodes.");
		tooltips.put("Betweenness", "Measure of how often the node falls \"between\" other pairs of nodes.");
		tooltips.put("Document/Element Count", "Number of documents/elements that contain the element/document.");
		tooltips.put("Strength", "Sum of edge weights of the node.");
		tooltips.put("Principal Components", "<html><p>Indicates structurally similar nodes.<br/>&nbsp;&nbsp;The X axis tends to indicate recursive connectedness.<br/>&nbsp;&nbsp;The Y axis tends to indicate the next-remaining major structural schism.</p></html>");
		tooltips.put("Circle","<html><p>Indicates density of graph.</p></html>");
		tooltips.put("Island Height","<html><p>Indicates commonly reported nodes.<br/>&nbsp;&nbsp;The X axis tends to group co-reported nodes.<br/>&nbsp;&nbsp;The Y axis indicates the heaviest edge connecting each node to the network (island height).</p></html>");
		tooltips.put("Force Directed","<html><p>Indicates dense clusters of nodes.<br/>&nbsp;&nbsp;Nodes are drawn to nodes they have connections to and pushed from nodes they are not connected to.<br/>&nbsp;&nbsp;On large networks, the layout may settle before optimality is achieved, and additional clicks may trigger improvement attempts.</p></html>");
		tooltips.put("Self-Organizing Map", "<html><p>Indicates structurally similar nodes.<br/>&nbsp;&nbsp;Similar nodes appear near each other. The axes have no meaning. The layout is non-deterministically estimated by neural networks.<br/>&nbsp;&nbsp;Be wary if the visualization is unstable when laid out repeatedly; the data is probably too noisy for this layout to be meaningful.</p></html>");
		tooltips.put("VOS Map", "<html><p>Indicates structurally similar nodes.<br/>&nbsp;&nbsp;Similar nodes appear near each other. <br/>&nbsp;&nbsp.</p></html>");
		tooltips.put("Item", "Whether the item is a node or edge.");
		tooltips.put("Type", "Whether the item is a vaccine/product, a symptom, or an edge connecting a pair of vaccines/symptoms.");
		tooltips.put("Weight", "Measure of the intensity of the connection.");
	}
	
	/** Used for entirely new graphs from files -- 
	 *  would be a new root on the Network View control panel **/
	private void setGraphAndReload(GeneralGraph graph) {
		NetworkVisualizationModelContainer vm;
		vm = new NetworkVisualizationModelContainer(new PCALayout(graph), vv.getSize(), this.getDualID());

		// Clear Network View control panel and put the
		// new network as the root
		if(graph == null){
			treeModel = null;
			neighborTree = null;
		}
		else{
			NALog("Graph Layout - " + graph.getName() + " generated.");
			
			treeModel = new AdjacentVertexTreeModel(graph);
			neighborTree.setModel(treeModel);
			neighborTree.setCellRenderer(new AdjacentVertexTreeCellRenderer(graph,colWidths));
			
			graphTreeModel.setNetworkRoot(vm);
			graphTree.setSelectionRow(0);
			graphTree.setEditable(true);
		}
		NetworkAnalysisVisualization.logger.logp(java.util.logging.Level.INFO,"","","Graph loaded - " + graph.getName() + ", " + graph.getLineage());
	}
	
	/** Loads a particular NetworkVisualizationModelContainer into
	 * the display by setting its active Graph into the 'graph' field
	 * and passing the model to the visualization server.<br/><br/>
	 * 
	 * This method should be used when switching to a model container
	 * (network tree node) that already exists. **/
	public void setVisualizationModelAndReload(NetworkVisualizationModelContainer vm) {
		if(vm.getGraphLayout() == null) {
			return;
		}
		graph = (GeneralGraph) vm.getGraphLayout().getGraph();
		vv.setModel(vm);
		reducedGraph = (GeneralGraph) vm.getGraphLayout().getReducedGraph();
		
		vv.getGLRenderContext().setEdgeStrokeTransformer(new EdgeStrokeTransformer(reducedGraph));
		vv.getGLRenderContext().setVertexShapeTransformer(new VertexShapeTransformer.DegreeTransformer(reducedGraph));
		vv.getGLRenderContext().setVertexDisplayTransformer(new VertexDisplayTransformer(reducedGraph));
		vv.getGLRenderContext().setEdgeStrokeTransformer(new EdgeStrokeTransformer(reducedGraph));
		vv.getGLRenderContext().setVertexFillPaintTransformer(new VertexFillPaintTransformer(vv.getPickedVertexState(),vv.getPickedEdgeState()));

		treeModel = new AdjacentVertexTreeModel(graph);
		neighborTree.setModel(treeModel);
		neighborTree.setCellRenderer(new AdjacentVertexTreeCellRenderer(graph,colWidths));

		setComboString();
		updateGraphLayout();
		
		if (graph.getName().contains("Cluster ID"))
		{
			// If it is a cluster of a network, get its parent and cluster information
			Object[] treePath = graphTree.getSelectionPath().getPath();
			GeneralGraph parent = (GeneralGraph) ((NetworkVisualizationModelContainer)((DefaultMutableTreeNode)treePath[treePath.length-2]).getUserObject()).getGraphLayout().getGraph();
			updateClusterInfo(parent);
		}
		else
			updateClusterInfo(graph);
		updateGraphNodeSize();
		vv.getNetworkScalingControl().rescale();
		
		// Update the properties in the bottom boxes
		updateAfterNodeSelection();
		
		setReportCountLabel();
		getHead().updateUI();
		vm.resetCurrentNetworkLayout(vv.getSize());
	}
	
	private void updateClusterInfo(GeneralGraph gg) {
		if (gg.getClusters() == null ) 
			return;
		List<Map<GeneralNode, Integer>> cluster_list = new ArrayList<Map<GeneralNode, Integer>>(gg.getClusters());
		

		Map<GeneralNode, Integer> current_cluster; 
		
		for (int i= 0; i < cluster_list.size(); i++){
			current_cluster = cluster_list.get(i);
			Iterator<GeneralNode> it_cluster = current_cluster.keySet().iterator();
			GeneralNode node;
			while (it_cluster.hasNext()){
				node = it_cluster.next();
				node.setCluster(node.getCluster());
			}
		}
	}

	/**
	* Call this after (de)selecting a new set of nodes
	* to ensure that the display is properly cleared
	* and reset, that the Selected Vertex Count
	* is correct.
	**/
	public void updateAfterNodeSelection() {
		clearDisplay();
		
		PickedState<GeneralNode> pickedNodes = vv.getPickedVertexState();
		HashSet<GeneralNode> picked = new HashSet<GeneralNode>(pickedNodes.getPicked());
		if (picked.size() == 1) {
			GeneralNode n = (GeneralNode) picked.iterator().next();
			if (graph.containsVertex(n)) {
				setDisplay(n);
			}
		}
		setSelectedVertexCount(picked.size());
		
		updateMenuItems();
	}
	
	/**
	 * Enables or disables items in the main menu bar based on the current selection.
	 * For example, some menu options require a single node to be selected, or other
	 * specific criteria.
	 */
	private void updateMenuItems() {
		ArrayDeque<Container> containers = new ArrayDeque<Container>();
		containers.push(menuBar);
		while (!containers.isEmpty()) {
			Container container = containers.pop();
			Component[] currComponents = (container instanceof JMenu) ? ((JMenu)container).getMenuComponents() : container.getComponents();
			for (Component c : currComponents) {
				if (c instanceof Container)
					containers.push((Container)c);
				
				if ((c instanceof PopupMenuItem) && ((PopupMenuItem)c).requiresVertexSelection() == SelectionType.VERTEX) {
					c.setEnabled(vv.getPickedVertexState().getPicked().size() > 0);
				}
				if ((c instanceof PopupMenu) && ((PopupMenu)c).requiresVertexSelection() == SelectionType.VERTEX) {
					c.setEnabled(vv.getPickedVertexState().getPicked().size() > 0);
				}
				if ((c instanceof PopupMenuItem) && ((PopupMenuItem)c).requiresVertexSelection() == SelectionType.REFERENCE) {
					Set<GeneralNode> nodes = vv.getPickedVertexState().getPicked();
					c.setEnabled(false);
					if (!nodes.isEmpty()){
						GeneralNode node = (GeneralNode) nodes.toArray()[0];
						if( node instanceof VAERS_Node )
							c.setEnabled(((VAERS_Node)node).getNodeType() == NodeType.REFERENCE);
					}
				}
				if ((c instanceof PopupMenu) && ((PopupMenu)c).requiresVertexSelection() == SelectionType.REFERENCE) {
					Set<GeneralNode> nodes = vv.getPickedVertexState().getPicked();
					c.setEnabled(false);
					if (!nodes.isEmpty()){
						GeneralNode node = (GeneralNode) nodes.toArray()[0];
						if( node instanceof VAERS_Node )
								c.setEnabled(((VAERS_Node)node).getNodeType() == NodeType.REFERENCE);
					}
				}
				if ((c instanceof PopupMenuItem) && ((PopupMenuItem)c).requiresVertexSelection() == SelectionType.SINGLE_SYM) {
					Set<GeneralNode> nodes = vv.getPickedVertexState().getPicked();
					c.setEnabled(false);
					if (!nodes.isEmpty()) {
						if (nodes.toArray()[0] instanceof VAERS_Node)
							c.setEnabled(nodes.size() == 1 && ((VAERS_Node)nodes.toArray()[0]).getNodeType() == NodeType.SYM);
						else
							c.setEnabled(false);
					}
				}
				if ((c instanceof PopupMenu) && ((PopupMenu)c).requiresVertexSelection() == SelectionType.SINGLE_SYM) {
					Set<GeneralNode> nodes = vv.getPickedVertexState().getPicked();
					c.setEnabled(false);
					if (!nodes.isEmpty()) {
						if (nodes.toArray()[0] instanceof VAERS_Node)
							c.setEnabled(nodes.size() == 1 && ((VAERS_Node)nodes.toArray()[0]).getNodeType() == NodeType.SYM);
						else
							c.setEnabled(false);
					}
				}
				if ((c instanceof PopupMenuItem) && ((PopupMenuItem)c).requiresVertexSelection() == SelectionType.EDGE) {
					c.setEnabled(vv.getPickedEdgeState().getPicked().size() > 0);
				}
				if ((c instanceof PopupMenu) && ((PopupMenu)c).requiresVertexSelection() == SelectionType.EDGE) {
					c.setEnabled(vv.getPickedEdgeState().getPicked().size() > 0);
				}
			}
		}
	}
	
	public void updateGraphLayout() {
		if(layoutCombo.getSelectedItem()== null ){
			layoutCombo.setSelectedIndex(0);
			layoutCombo.updateUI();
			layoutCombo.revalidate();
			selLayoutType = layoutTypes[0].toString();
		}
		else
			selLayoutType = ((String)layoutCombo.getSelectedItem());
		
		if (selLayoutType.equals(AbstractLayout.LayoutTypeToString.get(LayoutType.ISLAND_HEIGHT))) {
			if (!graph.confirmIslands()) {
				selLayoutType = AbstractLayout.LayoutTypeToString.get(LayoutType.PCA);
				setLayoutTypeByIndex(Arrays.asList(layoutTypes).indexOf(AbstractLayout.LayoutTypeToString.get(LayoutType.PCA)));
			}
		}
		
		if((NetworkVisualizationModelContainer)vv.getModel() == null )
			return;
		Layout<GeneralNode,GeneralEdge> layout = ((NetworkVisualizationModelContainer)vv.getModel()).getGraphLayout();
		
		layoutCombo.setToolTipText(tooltips.get(selLayoutType));

		if(((GeneralGraph)layout.getGraph()).getName().equals(graph.getName()) & layoutTypes[layout.getType().ordinal()].equals(selLayoutType)) {
				return;
		}
		
		CreateLayout cl = new CreateLayout(selLayoutType, graph, layout);
		layout = cl.create();
		((NetworkVisualizationModelContainer)vv.getModel()).setNetworkLayout(layout, vv.getSize(), graph.getDual());
		
	}
	
	public void resetGraphLayout() {
		selLayoutType = ((String)layoutCombo.getSelectedItem());

		Layout<GeneralNode,GeneralEdge> layout  = null;
		
		if(vv.getModel() != null )
			layout = ((NetworkVisualizationModelContainer)vv.getModel()).getGraphLayout();
		else
			return;
		
		CreateLayout cl = new CreateLayout(selLayoutType, graph, layout);
		layout = cl.create();
		((NetworkVisualizationModelContainer)vv.getModel()).getCurrentModel().setGraphLayout(layout, vv.getSize());
	}
	
	public void updateGraphLabels() {
		vv.repaint();
	}
	
	public void updateGraphNodeSize() {
		selNodeSizeType = (String)sizeCombo.getSelectedItem();
		Transformer<GeneralNode,GLShape> transformer;
		if(selNodeSizeType.equals("Degree")) {
			transformer = new VertexShapeTransformer.DegreeTransformer(graph);
		}
		else if(selNodeSizeType.equals("Betweenness")) {
			if (!graph.confirmBetweenClose()) {
				transformer = new VertexShapeTransformer.DegreeTransformer(graph);
				sizeCombo.setSelectedIndex(Arrays.asList(vertexSizeTypes).indexOf("Degree"));
			}
			else {
				transformer = new VertexShapeTransformer.BetweennessTransformer(graph);
			}
		}
		else if(selNodeSizeType.equals("Closeness")) {
			if (!graph.confirmBetweenClose()) {
				transformer = new VertexShapeTransformer.DegreeTransformer(graph);
				sizeCombo.setSelectedIndex(Arrays.asList(vertexSizeTypes).indexOf("Degree"));
			}
			else {
				transformer = new VertexShapeTransformer.ClosenessTransformer(graph);
			}
		}
		else if(selNodeSizeType.equals("Report Count")) {
			transformer = new VertexShapeTransformer.ReportCountTransformer(graph);
		}
		else {// if(selNodeSizeType.equals("Strength")) {
			transformer = new VertexShapeTransformer.StrengthTransformer(graph);
		}
		vv.updateVertexShapeTransformer(transformer);
		sizeCombo.setToolTipText(tooltips.get(selNodeSizeType));
	}
	
	private JPanel getMainPanel() {
		JPanel centralPane = new JPanel();
		centralPane.setLayout(new BorderLayout());
		centralPane.add(vv, BorderLayout.CENTER);
		return centralPane;
	}
	
	private JPanel getBottomPanel() {
	    		
		JPanel nap = new JPanel();
		nap.setLayout(new BorderLayout());

		nap.add(getBottomPanelInner(), BorderLayout.CENTER);
		nap.setPreferredSize(new Dimension(768,195));
		nap.setMinimumSize(new Dimension(768,185));

		nap.setBorder(BorderFactory.createEtchedBorder());
		
		return nap;
	}
	
	
	private JPanel getBottomTopBarAtLeft() {
		/** Zoom controls **/
		JPanel zoomPanel = new JPanel();
		zoomPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 5));
		zoomPanel.add(new JLabel("Zoom: "));
		zoomOut = new CommandButton("-", this, commandActionListener);
		zoomOut.setCommand(new ZoomOutCommand());
		zoomPanel.add(zoomOut);
		zoomIn = new CommandButton("+", this, commandActionListener);
		zoomIn.setCommand(new ZoomInCommand());
		zoomPanel.add(zoomIn);
		/** End Zoom **/
		
		/** Flip controls **/
		JPanel flipPanel = new JPanel();
		flipPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 5));
		flipPanel.add(new JLabel("Flip: "));
		flipVertical = new CommandButton("V", this, commandActionListener);
		flipVertical.setCommand(new FlipVerticalCommand());
		flipPanel.add(flipVertical);
		flipHorizontal = new CommandButton("H", this, commandActionListener);
		flipHorizontal.setCommand(new FlipHorizontalCommand());
		flipPanel.add(flipHorizontal);
		/** End flip **/

		/** Rotation controls **/
		JPanel rotatePanel = new JPanel();
		rotatePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 5));
		rotatePanel.add(new JLabel("Rotate: "));
		rotateClockwise = new CommandButton("CW", this, commandActionListener);
		rotateClockwise.setCommand(new RotateClockwiseCommand());
		rotatePanel.add(rotateClockwise);
		rotateCounterClockwise = new CommandButton("CCW", this, commandActionListener);
		rotateCounterClockwise.setCommand(new RotateCounterClockwiseCommand());
		rotatePanel.add(rotateCounterClockwise);
		/** End Rotation **/
		
		JPanel vertexShape = new JPanel();
		vertexShape.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 5));
		vertexShape.add(new JLabel("Node Size: "));
		
		sizeCombo = new CommandComboBox(vertexSizeTypes, "Set Node Size", this, commandActionListener);
		sizeCombo.setCommand(new SetNodeSizeCommand());
		ListCellToolTipRenderer vertexSizeRenderer = new ListCellToolTipRenderer(tooltips);
		sizeCombo.setRenderer(vertexSizeRenderer);
		sizeCombo.setToolTipText(tooltips.get(sizeCombo.getItemAt(0)));
		vertexShape.add(sizeCombo);
		
		JPanel vertexLabel = new JPanel();
		vertexLabel.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 5));
		vertexLabel.add(new JLabel("Show Labels:"));
		labelChk = new CommandCheckBox("Label", this, commandActionListener);
		labelChk.setCommand(new DisplayLabelCommand());
		labelChk.setSelected(false);
		vertexLabel.add(labelChk);
		
		layoutLabelPanel = new JPanel();
		layoutLabelPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 5));
		layoutLabelPanel.add(new JLabel("Layout: "));
		
		layoutCombo = new CommandComboBox(layoutTypes, "Set Layout", this, commandActionListener);
		layoutCombo.setCommand(new ResetLayoutCommand());
		ListCellToolTipRenderer layoutRenderer = new ListCellToolTipRenderer(tooltips);
		layoutCombo.setRenderer(layoutRenderer);
		layoutCombo.setToolTipText(tooltips.get(layoutCombo.getItemAt(2)));
		
		layoutLabelPanel.add(layoutCombo);
		
		resetLayoutButton = new CommandButton("Reset Layout", this, commandActionListener);
		resetLayoutButton.setCommand(new ResetLayout());
		JPanel resetLayout = new JPanel();
		resetLayout.setLayout(new FlowLayout());
		resetLayout.add(resetLayoutButton);
		
		
		JPanel topMenu = new JPanel();
		topMenu.setLayout(new BoxLayout(topMenu,BoxLayout.LINE_AXIS));
		Dimension barGap = new Dimension(18,1);
		topMenu.add(Box.createRigidArea(new Dimension(5,1)));
		topMenu.add(zoomPanel);
		topMenu.add(Box.createRigidArea(barGap));
		topMenu.add(flipPanel);
		topMenu.add(Box.createRigidArea(barGap));
		topMenu.add(rotatePanel);
		topMenu.add(Box.createRigidArea(barGap));
		topMenu.add(vertexShape);
		topMenu.add(Box.createRigidArea(barGap));
		topMenu.add(vertexLabel);
		topMenu.add(Box.createRigidArea(barGap));
		topMenu.add(layoutLabelPanel); 
		topMenu.add(Box.createRigidArea(barGap));
		topMenu.add(resetLayout);

		return topMenu;
	}
	
	private JPanel getBottomTopBarAtRight() {
		vertCount = new JTextField(3);
		vertCount.setEditable(false);
		
		JPanel title = new JPanel();
		title.setLayout(new BoxLayout(title,BoxLayout.LINE_AXIS));
		title.add(new JLabel("Selected Node Count: "));
		title.add(vertCount);
		
		return title;
	}

	private JPanel getBottomTopBarAtRightStatus() {
		statusLabel = new JLabel("Status:            ");
		JPanel statusPanel = new JPanel();
		statusPanel.setLayout(new BorderLayout());
		statusPanel.add(statusLabel, BorderLayout.CENTER);
				
		return statusPanel;
	}
	
	private JPanel getControlBar() {
		JPanel topMenu = getBottomTopBarAtLeft();		
		JPanel title = getBottomTopBarAtRight();
		JPanel statusPanel = getBottomTopBarAtRightStatus(); 		
		JPanel ttitle = new JPanel();
		ttitle.setLayout(new FlowLayout());
		ttitle.add(title);
		ttitle.add(statusPanel);
		
		JPanel ttopMenu = new JPanel();
		ttopMenu.setLayout(new BorderLayout());
		ttopMenu.add(topMenu,BorderLayout.WEST);
		ttopMenu.add(ttitle,BorderLayout.EAST);
		
		return ttopMenu;
	}
	
	private JPanel getBottomPanelInner() {		
		JPanel overallBottom = new JPanel();
		overallBottom.setLayout(new BorderLayout());
		overallBottom.add(getBottomBottom(),BorderLayout.CENTER);
		
		return overallBottom;
	}
	
	private JPanel getBottomBottom() {

		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BorderLayout());
		infoPanel.add(getBottomLeftInfoPanel(),BorderLayout.EAST);

		JPanel adjacentPanel = getBottomCenterAdjacencyPanel();
		JPanel networkViewPanel = getBottomRightPanelControlPanel();

		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new BorderLayout());
		controlPanel.add(adjacentPanel,BorderLayout.CENTER);
		controlPanel.add(infoPanel,BorderLayout.WEST);

		JPanel jsp = new JPanel();
		jsp.setLayout(new BorderLayout());
		jsp.setBorder(BorderFactory.createEtchedBorder());
		jsp.add(networkViewPanel,BorderLayout.CENTER);
		jsp.add(controlPanel,BorderLayout.EAST);
		
		
		JPanel left = new JPanel();
		left.setLayout(new BorderLayout());
		left.add(jsp,BorderLayout.CENTER);
		return left;
	}
	
	private JPanel getBottomCenterAdjacencyPanel() {
		
		treeModel = new AdjacentVertexTreeModel((FDAGraph)graph);
		neighborTree = new JTree(treeModel);
		neighborTree.setRootVisible(false);
		neighborTree.setBorder(BorderFactory.createEtchedBorder());
		
		neighborTree.setCellRenderer(new AdjacentVertexTreeCellRenderer(graph,colWidths));
		
		TableCellRenderer tableCellRenderer = new HeadTableCellRenderer(tooltips);
		DefaultTableColumnModel columnModel = new DefaultTableColumnModel();
		TableColumn tableColumn;
		for(int i = 0; i < 7; i++) {
			tableColumn = (i == 0) ? new TableColumn(i,colWidths[i]+3) : new TableColumn(i,colWidths[i]); 
			tableColumn.setCellRenderer(tableCellRenderer);
			columnModel.addColumn(tableColumn);
		}
		
		head = new JTable(new HeadAdjacentVertexTableModel(),columnModel);
		head.addMouseListener(new AdjacentTableSortListener(neighborTree));
		
		JPanel heading = new JPanel();
		heading.setLayout(new BorderLayout());
		heading.add(head,BorderLayout.WEST);
		
		JPanel tree_p = new JPanel();
		tree_p.setLayout(new BorderLayout());
		tree_p.setBorder(BorderFactory.createEtchedBorder());
		JScrollPane tree_jsp = new JScrollPane(neighborTree);
		tree_p.add(heading,BorderLayout.NORTH);
		tree_p.add(tree_jsp,BorderLayout.CENTER);
		
		
		JPanel adj_p = new JPanel();
		adj_p.setLayout(new BorderLayout());
		adj_p.setBorder(BorderFactory.createRaisedBevelBorder());
		adj_p.add(new JLabel(" Adjacent Nodes"),BorderLayout.NORTH);
		adj_p.add(tree_p,BorderLayout.CENTER);
		
		return adj_p;
	}
	
	public JTable getHead() {
		return head;
	}

	private JPanel getBottomLeftInfoPanel() {
		itemAttributeMap = new HashMap<String,JTextField>();
		itemAttributeMap.put("name", new JTextField(20));
		itemAttributeMap.put("item", new JTextField(10));
		itemAttributeMap.put("type", new JTextField(10));
		itemAttributeMap.put("count", new JTextField(10));
		itemAttributeMap.put("betweenness", new JTextField(10));
		itemAttributeMap.put("closeness", new JTextField(10));
		itemAttributeMap.put("degree", new JTextField(10));
		itemAttributeMap.put("strength", new JTextField(10));
		itemAttributeMap.put("weight", new JTextField(10)); 
		
		info_p = new JPanel();
		info_p.setLayout(new GridLayout(9,1)); 
		info_p.setBorder(BorderFactory.createEtchedBorder());
		info_p.add(getDisplayLine(itemAttributeMap.get("name")," Name: "));
		info_p.add(getDisplayLine(itemAttributeMap.get("item")," Item: "));
		info_p.add(getDisplayLine(itemAttributeMap.get("type")," Type: "));
		info_p.add(getDisplayLine(itemAttributeMap.get("count")," Document Count: "));
		info_p.add(getDisplayLine(itemAttributeMap.get("weight")," Weight: "));
		info_p.add(getDisplayLine(itemAttributeMap.get("betweenness")," Betweenness: "));
		
		info_p.add(getDisplayLine(itemAttributeMap.get("closeness")," Closeness: "));
		info_p.add(getDisplayLine(itemAttributeMap.get("degree")," Degree: "));
		info_p.add(getDisplayLine(itemAttributeMap.get("strength")," Strength: "));

		JPanel text_p = new JPanel();
		text_p.setLayout(new BorderLayout());
		text_p.setBorder(BorderFactory.createRaisedBevelBorder());
		text_p.add(new JLabel(" Information"),BorderLayout.NORTH);
		text_p.add(info_p,BorderLayout.CENTER);
		
		return text_p;
	}
	
	private Map<String,JTextField> getItemAttributeMap() {
		return itemAttributeMap;
	}
	
	private JPanel getDisplayLine(JTextField field, String label) {
		field.setHorizontalAlignment(JTextField.RIGHT);
		field.setEditable(false);
		
		
		JLabel lab = new JLabel(label);
		JPanel ret = new JPanel();
		ret.setLayout(new BorderLayout());
		ret.add(lab,BorderLayout.WEST);
		ret.add(field,BorderLayout.EAST);
		
		String bareLabel = label.trim().replace(":","");
		if(bareLabel.contains("Count")){
			field.setToolTipText(tooltips.get("Document/Element Count"));
		}
		else
			field.setToolTipText(tooltips.get(bareLabel));
		
		lab.setToolTipText(tooltips.get(bareLabel));
		
		return ret;
	}
	
	private JPanel getBottomRightPanelControlPanel() {
		graphTreeModel = new GraphTreeModel((NetworkVisualizationModelContainer)vv.getModel());
		graphTree = new JTree(graphTreeModel);
		graphTree.addMouseListener(new NetworkPopupMouseEvent(commandActionListener, appDir));
		graphTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		graphTree.setSelectionRow(0);
		
		graphTree.addTreeSelectionListener(new GraphTreeSelectionListener((NetworkVisualizationModelContainer)vv.getModel()));
		graphTree.setCellRenderer(new GraphTreeCellRenderer());
		ToolTipManager.sharedInstance().registerComponent(graphTree);
		
		gtsp = new JScrollPane(graphTree);
		
		JPanel networkTopPanel = new JPanel();
		
		networkTopPanel.setLayout(new BorderLayout());
		
		JLabel networkViewLabel = new JLabel("Network View");
		networkTopPanel.add(networkViewLabel, BorderLayout.WEST);
		hiddenEdgeLabel = new JLabel("");
		networkTopPanel.add(hiddenEdgeLabel, BorderLayout.EAST);
		
		JPanel subNetTree_p = new JPanel();
		subNetTree_p.setLayout(new BorderLayout());
		subNetTree_p.add(networkTopPanel,BorderLayout.NORTH);
		subNetTree_p.add(gtsp,BorderLayout.CENTER);
		
		networkTypeLabel = new JLabel("");
		
		JPanel belowNetworkPanel = new JPanel();
		belowNetworkPanel.setLayout(new BorderLayout());
		belowNetworkPanel.add(networkTypeLabel, BorderLayout.EAST);
		
		JPanel bottom = new JPanel();
		bottom.setLayout(new BorderLayout());
		bottom.setBorder(BorderFactory.createRaisedBevelBorder());
		bottom.add(subNetTree_p,BorderLayout.CENTER);
		bottom.add(belowNetworkPanel, BorderLayout.SOUTH);
		return bottom;
	}
	
	public String GetNetworkViewTypeLabel() {
		return networkTypeLabel.getText();
	}
	
	public void SetNetworkViewTypeLabel(String newLabel) {
		networkTypeLabel.setText(newLabel);
		if (newLabel.toLowerCase().contains("element")) {
			networkTypeLabel.setToolTipText("In an Element Network, nodes are symptoms or vaccines and edges represent reports that contained both terms.");
		}
		else if (newLabel.toLowerCase().contains("vax")) {
			networkTypeLabel.setToolTipText("In a Report Network: VAX, nodes are individual reports and edges represent vaccines that appeared in both reports.");
		}
		else if (newLabel.toLowerCase().contains("sym")) {
			networkTypeLabel.setToolTipText("In a Report Network: SYM, nodes are individual reports and edges represent symptoms that appeared in both reports.");
		}
		else {
			networkTypeLabel.setToolTipText("");
		}
	}
		
	private JMenuBar getMenu() {
		JMenuBar mb = new JMenuBar();
		
		/** File menu **/
		JMenu file = new JMenu("File");
		file.setMnemonic(KeyEvent.VK_F);
		
		JMenu newProject= new JMenu("New project");
		newProject.setMnemonic(KeyEvent.VK_N);
		CommandMenuItem importGraph = new CommandMenuItem("Create Full PANACEA Network...", this, commandActionListener);
		importGraph.setCommand(new ImportFullVAERS());
		importGraph.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
		importGraph.setMnemonic(KeyEvent.VK_F);
		newProject.add(importGraph);
		CommandMenuItem importBOGraph = new CommandMenuItem ("Create Business Objects Network...", this, commandActionListener);
		importBOGraph.setCommand(new ImportBOGraph());
		importBOGraph.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.CTRL_MASK));
		importBOGraph.setMnemonic(KeyEvent.VK_B);
		newProject.add(importBOGraph);
		CommandMenuItem importGraphGeneral = new CommandMenuItem("Import General Network...", this, commandActionListener);
		importGraphGeneral.setCommand(new ImportGeneralGraph());
		importGraphGeneral.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.CTRL_MASK));
		importGraphGeneral.setMnemonic(KeyEvent.VK_G);
		newProject.add(importGraphGeneral);
		file.add(newProject);
		
		CommandMenuItem openProject= new CommandMenuItem("Open Project...", this, commandActionListener);
		openProject.setCommand(new OpenProjectCommand());
		openProject.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		openProject.setMnemonic(KeyEvent.VK_O);
		file.add(openProject);
		saveProject= new CommandMenuItem("Save Project...", this, commandActionListener);
		saveProject.setCommand(new SaveProjectCommand());
		saveProject.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		saveProject.setMnemonic(KeyEvent.VK_S);
		file.add(saveProject);
		
		closeProject= new CommandMenuItem("Close Project", this, commandActionListener);
		closeProject.setCommand(new CloseProjectCommand());
		closeProject.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
		closeProject.setMnemonic(KeyEvent.VK_C);
		file.add(closeProject);
		
		file.add(new JSeparator());
		
		exportToOtherFormat = new JMenu("Export");
		exportToOtherFormat.setMnemonic(KeyEvent.VK_X);
		CommandMenuItem exportCSVGraph = new CommandMenuItem("Export in Edgelist Format (.csv)...", this, commandActionListener);
		exportCSVGraph.setCommand(new ExportCSVGraph());
		exportCSVGraph.setMnemonic(KeyEvent.VK_E);
		exportToOtherFormat.add(exportCSVGraph );
		CommandMenuItem exportPajekGraph = new CommandMenuItem("Export in Pajek Format (.net)...", this, commandActionListener);
		exportPajekGraph.setCommand(new ExportPajekGraph());
		exportPajekGraph.setMnemonic(KeyEvent.VK_P);
		exportToOtherFormat.add(exportPajekGraph);
		file.add(exportToOtherFormat);
		
		saveLog = new CommandMenuItem("Save log...", this, commandActionListener);
		saveLog.setCommand(new SaveLogCommand());
		saveLog.setMnemonic(KeyEvent.VK_L);
		file.add(saveLog);
		
		file.add(new JSeparator());
		
		CommandMenuItem config = new CommandMenuItem("Preferences...", this, commandActionListener);
		config.setCommand(new configCommand());
		config.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
		config.setMnemonic(KeyEvent.VK_P);
		file.add(config);
		
		CommandMenuItem exit = new CommandMenuItem("Exit", this, commandActionListener);
		exit.setCommand(new ExitCommand());
		exit.setMnemonic(KeyEvent.VK_E);
		file.add(exit);
		
		mb.add(file);
		
		/** View Menu **/
		view = new JMenu("View");
		view.setMnemonic(KeyEvent.VK_V);
		
		CommandMenuItem toggleDisplayControl = new CommandMenuItem("Display/Hide Lower Pane", this, commandActionListener);
		toggleDisplayControl.setCommand(new ToggleDisplayCommand());
		toggleDisplayControl.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.CTRL_MASK));
		toggleDisplayControl.setMnemonic(KeyEvent.VK_D);
		view.add(toggleDisplayControl);
		
		view.add(new JSeparator());
		
		CommandMenuItem toggleCColor = new CommandMenuItem("Toggle Cluster Coloring", this, commandActionListener);
		toggleCColor.setCommand(new ToggleShowClusterColoring());
		toggleCColor.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		toggleCColor.setMnemonic(KeyEvent.VK_T);
		flagShowClusterColoring = true;
		view.add(toggleCColor);
		
		CommandMenuItem resetGraphInfo = new CommandMenuItem("Reset Graph Information", this, commandActionListener);
		resetGraphInfo.setCommand(new ResetGraphCommand());
		resetGraphInfo.setMnemonic(KeyEvent.VK_R);
		view.add(resetGraphInfo);
		
		view.add(new JSeparator());

		CommandMenuItem addAnnotation = new CommandMenuItem("Add Annotation", this, commandActionListener);
		addAnnotation.setCommand(new AddAnnotationDialogCommand());
		addAnnotation.setMnemonic(KeyEvent.VK_A);
		view.add(addAnnotation);
		
		CommandMenuItem removeLastAnnotation = new CommandMenuItem ("Remove Last Annotation", this, commandActionListener);
		removeLastAnnotation.setCommand(new RemoveLastAnnotation());
		removeLastAnnotation.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
		removeLastAnnotation.setMnemonic(KeyEvent.VK_L);
		view.add(removeLastAnnotation);
		
		CommandMenuItem clearAllAnnotations = new CommandMenuItem("Clear All Annotations", this, commandActionListener);
		clearAllAnnotations.setCommand(new ClearAllAnnotations());
		clearAllAnnotations.setMnemonic(KeyEvent.VK_C);
		view.add(clearAllAnnotations);

		mb.add(view);
		
		/** Select Menu **/
		select = new JMenu("Select");
		select.setMnemonic(KeyEvent.VK_S);
		
		CommandMenuItem selectAll = new CommandMenuItem("Select All Nodes", this, commandActionListener);
		selectAll.setCommand(new SelectAllNodesCommand());
		selectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		selectAll.setMnemonic(KeyEvent.VK_A);
		select.add(selectAll);
		
		PopupMenuItem expandSel = new PopupMenuItem("Expand Selection", SelectionType.VERTEX, commandActionListener);
		expandSel.setCommand(new ExpandSelectionCommand());
		expandSel.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		expandSel.setMnemonic(KeyEvent.VK_X);
		select.add(expandSel);
		
		PopupMenuItem invertSel = new PopupMenuItem("Invert Selection", SelectionType.VERTEX, commandActionListener);
		invertSel.setCommand(new InvertSelectionCommand());
		invertSel.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
		invertSel.setMnemonic(KeyEvent.VK_I);
		select.add(invertSel);
		
		select.add(new JSeparator());
		
		CommandMenuItem selNodesFromList = new CommandMenuItem("Select Nodes From List...", this, commandActionListener);
		selNodesFromList.setCommand(new SelectNodesFromListCommand());
		selNodesFromList.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
		selNodesFromList.setMnemonic(KeyEvent.VK_L);
		select.add(selNodesFromList);
		
		mb.add(select);
		
		/** Reduce Menu **/
		reduce = new JMenu("Reduce");
		reduce.setMnemonic(KeyEvent.VK_R);
		
		PopupMenuItem createFromSel = new PopupMenuItem("Create Subnetwork From Selection", SelectionType.VERTEX, commandActionListener);
		createFromSel.setCommand(new CreateSelectedSubGraph());
		createFromSel.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, ActionEvent.CTRL_MASK));
		createFromSel.setMnemonic(KeyEvent.VK_U);
		reduce.add(createFromSel);
		
		PopupMenuItem deleteSel = new PopupMenuItem("Delete Selection", SelectionType.VERTEX, commandActionListener);
		deleteSel.setCommand(new DeleteSelectionCommand());
		deleteSel.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		deleteSel.setMnemonic(KeyEvent.VK_D);
		reduce.add(deleteSel);
		
		reduce.add(new JSeparator());
		
		CommandMenuItem remEdges = new CommandMenuItem("Remove Edges...", this, commandActionListener);
		remEdges.setCommand(new RemoveEdge());
		remEdges.setMnemonic(KeyEvent.VK_E);
		reduce.add(remEdges);
		CommandMenuItem remNodes = new CommandMenuItem("Remove Nodes...", this, commandActionListener);
		remNodes.setCommand(new RemoveNode());
		remNodes.setMnemonic(KeyEvent.VK_N);
		reduce.add(remNodes);
		
		reduce.add(new JSeparator());
		
		CommandMenuItem remIsolates = new CommandMenuItem("Remove isolates", this, commandActionListener);
		remIsolates.setCommand(new RemoveIsolates());
		remIsolates.setMnemonic(KeyEvent.VK_I);
		reduce.add(remIsolates);
		
		CommandMenuItem remPendants = new CommandMenuItem("Remove pendants", this, commandActionListener);
		remPendants.setCommand(new RemovePendants());
		remPendants.setMnemonic(KeyEvent.VK_P);
		reduce.add(remPendants);
		
		reduce.add(new JSeparator());
		
		extractByTypeSubmenu = new JMenu("Extract by Type");
		extractByTypeSubmenu.setMnemonic(KeyEvent.VK_X);
		CommandMenuItem remSym = new CommandMenuItem("Symptoms (SYM-SYM; co-reporting network)", this, commandActionListener);
		remSym.setCommand(new CreateCoNetwork(EdgeType.SYM2SYM));
		remSym.setMnemonic(KeyEvent.VK_S);
		extractByTypeSubmenu.add(remSym);
		CommandMenuItem remVax = new CommandMenuItem("Vaccines (VAX-VAX; co-administration network)", this, commandActionListener);
		remVax.setCommand(new CreateCoNetwork(EdgeType.VAX2VAX));
		remVax.setMnemonic(KeyEvent.VK_V);
		extractByTypeSubmenu.add(remVax);
		CommandMenuItem keepVAXPT = new CommandMenuItem("Cross-edges (SYM-VAX; reported effect network)", this, commandActionListener);
		keepVAXPT.setCommand(new CreateCoNetwork(EdgeType.VAX2SYM));
		keepVAXPT.setMnemonic(KeyEvent.VK_C);
		extractByTypeSubmenu.add(keepVAXPT);
		reduce.add(extractByTypeSubmenu);
		
		mb.add(reduce);
		
		/** Document Retrieval Menu **/
		documentRetrieval = new JMenu("Document Retrieval");
		documentRetrieval.setMnemonic(KeyEvent.VK_D);
		
		CommandMenuItem retrieveSyntheticDocuments = new CommandMenuItem("Retrieve Similar Documents (Synthesis)...", this, commandActionListener);
		retrieveSyntheticDocuments.setCommand(new RetrieveSyntheticSimilarCases());
		retrieveSyntheticDocuments.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
		retrieveSyntheticDocuments.setMnemonic(KeyEvent.VK_Y);
		documentRetrieval.add(retrieveSyntheticDocuments);
		
		PopupMenuItem retrieveSimilarDocuments = new PopupMenuItem("Retrieve Similar Documents (Selection)", SelectionType.VERTEX, commandActionListener);
		retrieveSimilarDocuments.setCommand(new RetrieveSimilarReportsCommand());
		retrieveSimilarDocuments.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		retrieveSimilarDocuments.setMnemonic(KeyEvent.VK_N);
		documentRetrieval.add(retrieveSimilarDocuments);
		
		documentRetrieval.add(new JSeparator());
		
		PopupMenu setSimilarity = new PopupMenu("Set Minimum Similarity", SelectionType.REFERENCE);
		setSimilarity.setMnemonic(KeyEvent.VK_S);
		PopupMenuItem byValue = new PopupMenuItem("By Value...", SelectionType.REFERENCE, commandActionListener);
		byValue.setCommand(new SetSimilarityThresholdCommand());
		byValue.setMnemonic(KeyEvent.VK_V);
		setSimilarity.add(byValue);
		PopupMenuItem byTerms = new PopupMenuItem("By Common Terms...", SelectionType.REFERENCE, commandActionListener);
		byTerms.setCommand(new SetSimilarityThresholdCommand());
		byTerms.setMnemonic(KeyEvent.VK_C);
		setSimilarity.add(byTerms);
		documentRetrieval.add(setSimilarity);
		
		viewSimilarityPlot = new CommandMenuItem("View Similarity Plot", this, commandActionListener);
		viewSimilarityPlot.setCommand(new ViewSMQSimilarityPlotCommand());
		viewSimilarityPlot.setMnemonic(KeyEvent.VK_V);
		documentRetrieval.add(viewSimilarityPlot);
		
		mb.add(documentRetrieval);	
		
		/** Network Menu **/
		network = new JMenu("Network");
		network.setMnemonic(KeyEvent.VK_N);
		
		CommandMenuItem switchNetwork = new CommandMenuItem("Switch Network Type...", this, commandActionListener);
		switchNetwork.setCommand(new SwitchNetwork());
		switchNetwork.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));
		switchNetwork.setMnemonic(KeyEvent.VK_T);
		network.add(switchNetwork);
		
		network.addSeparator();
		
		JMenu identifyClustersSubMenu = new JMenu("Identify Clusters");
		identifyClustersSubMenu.setMnemonic(KeyEvent.VK_C);
		
		CommandMenuItem kMeansMenuItem = new CommandMenuItem("K-Means...", this, commandActionListener);
		kMeansMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, ActionEvent.CTRL_MASK | ActionEvent.ALT_MASK));
		kMeansMenuItem.setCommand(new CommunityDetectionCommand("K-Means"));
		identifyClustersSubMenu.add(kMeansMenuItem);
		
		CommandMenuItem louvainMenuItem = new CommandMenuItem("Louvain...", this, commandActionListener);
		louvainMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK | ActionEvent.ALT_MASK));
		louvainMenuItem.setCommand(new CommunityDetectionCommand("Louvain"));
		identifyClustersSubMenu.add(louvainMenuItem);

		CommandMenuItem vOSMenuItem = new CommandMenuItem("VOS...", this, commandActionListener);
		vOSMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK | ActionEvent.ALT_MASK));
		vOSMenuItem.setCommand(new CommunityDetectionCommand("VOS"));
		identifyClustersSubMenu.add(vOSMenuItem);
		
		network.add(identifyClustersSubMenu);
		
		weightingNetwork = new JMenu("Weight Schemes");
		weightingNetwork.setMnemonic(KeyEvent.VK_W);
		ButtonGroup bg = new ButtonGroup();
		WeightingScheme[] weighting = WeightingScheme.values();
		
		currentWeightingScheme = weighting[1];
		
		for (int i = 0; i <= 6; i++){
			CommandRadioButtonMenu similarityMenuItem = new CommandRadioButtonMenu(weighting[i].name(), this, commandActionListener);
			similarityMenuItem.setCommand(new CreateWeightedNetwork(weighting[i]));
			similarityMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_0+i, ActionEvent.CTRL_MASK));
			weightingNetwork.add(similarityMenuItem);
			if (i==1) {
				similarityMenuItem.setSelected(true);
			}
			bg.add(similarityMenuItem);
		}
		network.add(weightingNetwork);		
		
		network.addSeparator();
		
		CommandMenuItem properties = new CommandMenuItem("View Properties", this, commandActionListener);
		properties.setCommand(new ViewPropertiesCommand());
		properties.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
		properties.setMnemonic(KeyEvent.VK_P);
		network.add(properties);
		
		CommandMenuItem nodeMetricPlot = new CommandMenuItem("View Node Metric Plot", this, commandActionListener);
		nodeMetricPlot.setCommand(new ViewNodeMetricPlotCommand());
		nodeMetricPlot.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.CTRL_MASK));
		nodeMetricPlot.setMnemonic(KeyEvent.VK_M);
		network.add(nodeMetricPlot);
		
		saveSnapshot = new CommandMenuItem("Save Network Snapshot...", this, commandActionListener);
		saveSnapshot.setCommand(new SaveNetworkSnapshotCommand("JPEG"));
		saveSnapshot.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
		saveSnapshot.setMnemonic(KeyEvent.VK_S);
		network.add(saveSnapshot);
		
		/** Network - Export Node Properties Menu **/
		// NOTE: The String labels for these menu items must exactly match
		// the ones used in SaveNetworkSnapshotCommand.
		exportNodeProp = new PopupMenu("Export Node Properties", SelectionType.NONE);
		exportNodeProp.setMnemonic(KeyEvent.VK_N);
		PopupMenuItem nameNodes = new PopupMenuItem("Names of Nodes...", SelectionType.NONE, commandActionListener);
		nameNodes.setCommand(new SaveNetworkSnapshotCommand("CSV"));
		nameNodes.setMnemonic(KeyEvent.VK_N);
		exportNodeProp.add(nameNodes);
		PopupMenuItem attrNodes = new PopupMenuItem("Attributes of Nodes...", SelectionType.NONE, commandActionListener);
		attrNodes.setCommand(new SaveNetworkSnapshotCommand("CSV"));
		attrNodes.setMnemonic(KeyEvent.VK_A);
		exportNodeProp.add(attrNodes);
		PopupMenuItem idsNodes = new PopupMenuItem("Report IDs of Nodes...", SelectionType.NONE, commandActionListener);
		idsNodes.setCommand(new SaveNetworkSnapshotCommand("CSV"));
		idsNodes.setMnemonic(KeyEvent.VK_R);
		exportNodeProp.add(idsNodes);
		network.add(exportNodeProp);
		
		/** Network - Export Edge Properties Menu **/
		// NOTE: The String labels for these menu items must exactly match
		// the ones used in SaveNetworkSnapshotCommand.
		PopupMenu exportEdgeProp = new PopupMenu("Export Edge Properties", SelectionType.EDGE);
		exportEdgeProp.setEnabled(false);
		exportEdgeProp.setMnemonic(KeyEvent.VK_E);
		PopupMenuItem nameEdges = new PopupMenuItem("Names of Edges...", SelectionType.EDGE, commandActionListener);
		nameEdges.setCommand(new SaveNetworkSnapshotCommand("CSV"));
		nameEdges.setMnemonic(KeyEvent.VK_N);
		exportEdgeProp.add(nameEdges);
		PopupMenuItem attrEdges = new PopupMenuItem("Attributes of Edges...", SelectionType.EDGE, commandActionListener);
		attrEdges.setCommand(new SaveNetworkSnapshotCommand("CSV"));
		attrEdges.setMnemonic(KeyEvent.VK_A);
		exportEdgeProp.add(attrEdges);
		PopupMenuItem idsEdges = new PopupMenuItem("Report IDs of Edges...", SelectionType.EDGE, commandActionListener);
		idsEdges.setCommand(new SaveNetworkSnapshotCommand("CSV"));
		idsEdges.setMnemonic(KeyEvent.VK_R);
		exportEdgeProp.add(idsEdges);
		network.add(exportEdgeProp);
		
		network.addSeparator();
		
		pullDocumentsSubmenu = new JMenu("Pull Documents From Original Data");
		pullDocumentsSubmenu.setMnemonic(KeyEvent.VK_U);
		PopupMenuItem pullWithAny = new PopupMenuItem("Pull Documents With Any Selected Terms", SelectionType.VERTEX, commandActionListener);
		pullWithAny.setCommand(new PullReportsWithSelectedTerms());
		pullWithAny.setMnemonic(KeyEvent.VK_N);
		pullDocumentsSubmenu.add(pullWithAny);
		PopupMenuItem pullWithAll = new PopupMenuItem("Pull Documents With All Selected Terms", SelectionType.VERTEX, commandActionListener);
		pullWithAll.setCommand(new PullReportsWithAllTerms());
		pullWithAll.setMnemonic(KeyEvent.VK_L);
		pullDocumentsSubmenu.add(pullWithAll);
		PopupMenuItem pullExcluding = new PopupMenuItem("Pull Documents Excluding Selected Terms", SelectionType.VERTEX, commandActionListener);
		pullExcluding.setCommand(new PullReportsExcludingTerms());
		pullExcluding.setMnemonic(KeyEvent.VK_X);
		pullDocumentsSubmenu.add(pullExcluding);
		network.add(pullDocumentsSubmenu);
		
		network.addSeparator();
		
		CommandMenuItem runSimulator = new CommandMenuItem("Simulate Similar Network...", this, commandActionListener);
		runSimulator.setCommand(new RunSimulatorCommand());
		runSimulator.setMnemonic(KeyEvent.VK_I);
		network.add(runSimulator);
		
		compareRealVsSim = new CommandMenuItem("Compare Network To Simulation...", this, commandActionListener);
		compareRealVsSim.setCommand(new CompareRealVsSimCommand());
		compareRealVsSim.setMnemonic(KeyEvent.VK_O);
		network.add(compareRealVsSim);
		
		mb.add(network);
		
		/** Help menu **/
		JMenu help = new JMenu("Help");
		help.setMnemonic(KeyEvent.VK_H);
		
		CommandMenuItem documentation = new CommandMenuItem("Documentation", this, commandActionListener);
		documentation.setCommand(new ViewDocumentCommand());
		documentation.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		documentation.setMnemonic(KeyEvent.VK_D);
		help.add(documentation);
		
		CommandMenuItem about = new CommandMenuItem("About", this, commandActionListener);
		about.setCommand(new AboutCommand());
		about.setMnemonic(KeyEvent.VK_A);
		help.add(about);
		
		CommandMenuItem shortcuts = new CommandMenuItem("View Quick-Start Controls", this, commandActionListener);
		shortcuts.setCommand(new ViewShortcutsCommand());
		shortcuts.setMnemonic(KeyEvent.VK_C);
		help.add(shortcuts);
		
		mb.add(help);
		
		return mb;
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if(e.getSource() instanceof Layout) {
			if(e.getPropertyName().equals("state") && e.getNewValue().equals(StateValue.DONE) && !isVisible()) {
				setVisible(true);
				this.setCursor(Cursor.getDefaultCursor());
				tNow = System.currentTimeMillis();
				System.out.println("PANACEA started: " + (tNow-tStart)/1000.0);
			}
		}
		else if (e.getPropertyName().equals("set_picked"))
			updateMenuItems();
	}

	public boolean getDualState() {
		if (isDual)
			return true;
		else
			return false;
	}

	public int getDualID() {
		return dualID;
	}
	
	public void setDualID(int dualID) {
		this.dualID = dualID;
		if(dualID >0)
			this.setDualNetwork(true);
		else
			this.setDualNetwork(false);
	}
	
	public void setDualNetwork(boolean nType) {
		isDual = nType;
		setMenusDual(this.dualID);
	}
	
	public void cleanNetwork() {
		vv.getPickedEdgeState().clear();
		vv.getPickedVertexState().clear();
		updateAfterNodeSelection();
		if (graphLoader != null )
			graphLoader.clean();
		setGraphLoader(null);
		this.setDualID(0);
		this.setDualNetwork(false);
		((DefaultMutableTreeNode)graphTree.getModel().getRoot()).removeAllChildren();
		enableControls(false);
		vv.removeModel();
		vv.repaint();
		graphTreeModel.setNetworkRoot(null);
		graphTreeModel.reload();
		hiddenEdgeLabel.setText("");
		graph = null;
		SetNetworkViewTypeLabel("");
	}
	
	public void showBottomPanel(Boolean show) {
		flagShowBottomPanel = show;
		bottomPanel.setEnabled(show);
		bottomPanel.setVisible(show);
		splitPane.resetToPreferredSizes();
	}
	
	public static void main(String[] args) {
		
		tStart = System.currentTimeMillis();
		logger = Logger.getLogger("UserActivities");
		if (args.length > 0 ) {
			/** Call PANACEA with a business object file as a parameter*/
			boPath = args[0];
			inputDualID = 0;
			if (args.length>1){
				try{
					inputDualID = Integer.valueOf(args[1]);
				}
				catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(null, "<html> The second input argument is not an integer: <br>"
							+ "&nbsp; 0: element network; <br>"
							+ "&nbsp; 1: document network (VAX); <br>"
							+ "&nbsp; 2: document network (SYM) <br> </html>", "Issue", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
		}
		
		try {
			new File(appDir).mkdirs();
			new File(logDir).mkdirs();
			new File(dataDir).mkdirs();
			new File(outputDir).mkdirs();
			new File(prjDir).mkdirs();
			
			FileHandler fh = new FileHandler(logFile);
			logger.addHandler(fh);
			fh.setFormatter(new SimpleFormatter());
		
			logger.logp(java.util.logging.Level.INFO,"","","============ Start Pattern-based and Advanced Network Analyzer for Clinical Evaluation and Assessment (PANACEA) ============");
			logger.logp(java.util.logging.Level.INFO,"","","Base directory is " + appDir);
		}
		catch (SecurityException e) {
			JOptionPane.showMessageDialog(null, "Could not start logging.", "Issue", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
        }
		catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Could not start logging.", "Issue", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					NetworkAnalysisVisualization instance = NetworkAnalysisVisualization.getInstance();
					instance.enableControls(false);
					
					File preferenceFile = new File(getAppDir() + "/PANACEA_Preferences.json");
					if (preferenceFile.exists()) {
						PreferenceFileIO prefReader = new PreferenceFileIO();
						if (prefReader.readPreferenceFile(preferenceFile)) {
							instance.setMaxEdgeSizeToDisplay(prefReader.getMaxEdgesToDraw());
							instance.setRepaintInterval(prefReader.getFrameIntervalToAnimate());
							instance.setEdgeDarkness(prefReader.getEdgeDarkness());
							NetworkAnalysisVisualization.setStartLogging(prefReader.getDebugging());
						}
					}
					
					if (boPath != null ) {
						/** Call PANACEA with a parameter (business object). */
						instance.setDualID(inputDualID);
						
						ImportBOGraphCommand importBOGraph = new ImportBOGraphCommand(boPath);
						importBOGraph.execute(null);
					}
					else {
						inputDualID = 0;
						instance.setDualID(inputDualID);
						
						GraphLoader graphLoader =new GraphLoader();
						instance.setGraphLoaderAndCalculate(graphLoader, "", false, false);
						instance.showBottomPanel(true);
					}
					
					File imageFile = new File(getAppDir() + "/PANACEA_Logo.png");
					if (imageFile.exists()){
						Image logo = ImageIO.read(imageFile);
						if (logo != null )
							instance.setIconImage(logo);
					}
					
					if(startLogging)
						logger.logp(java.util.logging.Level.INFO, "NetworkAnalylsisVisualization", "Main", System.getProperty("java.runtime.version"));
					
				} 
				catch (ClassNotFoundException e) {
					e.printStackTrace();
					instance.dispatchEvent(new WindowEvent(instance,WindowEvent.WINDOW_CLOSING));
				} 
				catch (InstantiationException e) {
					e.printStackTrace();
					instance.dispatchEvent(new WindowEvent(instance,WindowEvent.WINDOW_CLOSING));
				} 
				catch (IllegalAccessException e) {
					e.printStackTrace();
					instance.dispatchEvent(new WindowEvent(instance,WindowEvent.WINDOW_CLOSING));
				} 
				catch (UnsupportedLookAndFeelException e) {
					e.printStackTrace();
					instance.dispatchEvent(new WindowEvent(instance,WindowEvent.WINDOW_CLOSING));
				}
				catch(ArrayIndexOutOfBoundsException ex) {
					System.err.println("File in unexpected format." + ex);
					JOptionPane.showMessageDialog(instance, "PANACEA file in unexpected format.", "Unexpected format", JOptionPane.ERROR_MESSAGE);
					ex.printStackTrace();
					instance.dispatchEvent(new WindowEvent(instance,WindowEvent.WINDOW_CLOSING));
				}
				catch(Exception ex) {
					System.err.println("Exception. Possibly could not find file." + ex);
					JOptionPane.showMessageDialog(instance, "Exception.  Possibly could not find PANACEA file.", "PANACEA Exception", JOptionPane.ERROR_MESSAGE);
					ex.printStackTrace();
					instance.dispatchEvent(new WindowEvent(instance,WindowEvent.WINDOW_CLOSING));
				}
			}
		});
		tNow = System.currentTimeMillis();
		
        if(NetworkAnalysisVisualization.startLogging)
			logger.logp(java.util.logging.Level.INFO,"","","Start PANACEA...");
	}
	
	public void setComboString() {
		if (graph instanceof FDAGraph) {
			String[] strTypes = new String[vertexSizeTypes.length];
			DefaultComboBoxModel<String> model;
			 System.arraycopy(vertexSizeTypes, 0, strTypes, 0, vertexSizeTypes.length );			
			if (graph.isDual())
				strTypes[vertexSizeTypes.length -2] = "Element Count";
			else
				strTypes[vertexSizeTypes.length -2] = "Document Count";
			model = new DefaultComboBoxModel<String>( strTypes);
			sizeCombo.setModel(model);
		}
		else {
			String[] strTypes2 = new String[vertexSizeTypes.length-1];
			DefaultComboBoxModel<String> model2;
			System.arraycopy(vertexSizeTypes, 0, strTypes2, 0, 3);
			System.arraycopy(vertexSizeTypes, 4, strTypes2, 3, 1);
			model2 = new DefaultComboBoxModel<String>( strTypes2);
			sizeCombo.setModel(model2);
		}
	}
	
	public String getVertexSizeString() {
		return (String) (sizeCombo.getSelectedItem());
	}

	public void setVertexSize(String strVertexSize) {
		sizeCombo.setSelectedIndex(Arrays.asList(vertexSizeTypes).indexOf(strVertexSize));
	}	

	public void setLayoutType(Object layout) {
		layoutCombo.setSelectedIndex(((Layout.LayoutType)layout).ordinal());
	}	
	
	/** Determine the display in the node/edge information panel
	 * documentCountLabelExist  = 0: no display
	 * documentCountLabelExist  = 1: Document Count
	 * documentCountLabelExist  = 2: Element Count  
	 */
	public void setReportCountLabel() {
		
		int documentCountLabelExist = 0;
		
		if (!graph.isFDAType())
			documentCountLabelExist  = 0;
		else
			if(graph.getDual() == 0 )
				documentCountLabelExist  = 1;
			else
				documentCountLabelExist  = 2;
		
		synchronized(getInfo_p()) {
			if (getInfo_p().getComponentCount() == 9)
				getInfo_p().remove(3);
			
			
			
			switch (documentCountLabelExist ) {
			case 0:
				break;
			case 1:
				getInfo_p().add(getDisplayLine(getItemAttributeMap().get("count")," Document Count: "), 3);
				break;
			case 2: 
				getInfo_p().add(getDisplayLine(getItemAttributeMap().get("count")," Element Count: "), 3);
				break;
			}
			getInfo_p().revalidate();
		}
	}
	
	public void toggleLabel(boolean flag) {
		labelChk.setSelected(flag);
	}
	
	public boolean shouldShowLabels() {
		return labelChk.isSelected();
	}
	
	public boolean shouldShowClusterColoring() {
		return flagShowClusterColoring;
	}
	
	public void setShowClusterColoring(boolean flagShowClusterColoring) {
		this.flagShowClusterColoring = flagShowClusterColoring;
	}
	
	private void setMenusDual(int dualID) {
		documentRetrieval.setEnabled(view.isEnabled() && dualID == 2);
		pullDocumentsSubmenu.setEnabled(view.isEnabled() && dualID == 0);
		extractByTypeSubmenu.setEnabled(view.isEnabled() && dualID == 0);
		compareRealVsSim.setEnabled(view.isEnabled() && dualID == 0);
		
		boolean refDocumentExists = false;
		DefaultMutableTreeNode selectedNode= (DefaultMutableTreeNode)graphTree.getLastSelectedPathComponent();
		if (selectedNode != null && selectedNode.getUserObject() != null) {
			refDocumentExists = ((NetworkVisualizationModelContainer)selectedNode.getUserObject()).getCurrentModel().getModel().findNodeByID("ReferenceDocument") != null;
		}
		viewSimilarityPlot.setEnabled(refDocumentExists);
	}
	
	public void enableControls(boolean enable) {
		// Lower Pane
		setChildrenEnabled(bottomPanel, enable);
		bottomPanel.setEnabled(enable);
				
		// Control Bar
		this.sizeCombo.setEnabled(enable);
		this.layoutCombo.setEnabled(enable);
		this.labelChk.setEnabled(enable);
		this.zoomIn.setEnabled(enable);
		this.zoomOut.setEnabled(enable);
		this.flipHorizontal.setEnabled(enable);
		this.flipVertical.setEnabled(enable);
		this.rotateClockwise.setEnabled(enable);
		this.rotateCounterClockwise.setEnabled(enable);
		this.resetLayoutButton.setEnabled(enable);

		// Top Menu Bar
		this.saveProject.setEnabled(enable);
		this.closeProject.setEnabled(enable);
		this.view.setEnabled(enable);
		this.select.setEnabled(enable);
		this.reduce.setEnabled(enable);
		this.network.setEnabled(enable);
		this.exportToOtherFormat.setEnabled(enable);
		this.exportNodeProp.setEnabled(enable);
		
		// Items that depend on current network type might be different.
		this.documentRetrieval.setEnabled(enable);
		pullDocumentsSubmenu.setEnabled(enable);
		extractByTypeSubmenu.setEnabled(enable);
		compareRealVsSim.setEnabled(enable);
		setMenusDual(this.dualID);
	}
	
	public void setChildrenEnabled(Container c, boolean enable) {
		Component[] components = c.getComponents();
		for (Component comp: components) {
			if (comp instanceof java.awt.Container)
				setChildrenEnabled((java.awt.Container) comp, enable);
			comp.setEnabled(enable);
		}
	}
	
	public void setNumHiddenEdges(int numHidden) {
		hiddenEdgeLabel.setText(numHidden == 0 ? "" : String.valueOf(numHidden) + " Hidden Edges");
	}
	
	public void writePreferences() {
		PreferenceFileIO prefWriter = new PreferenceFileIO();
		prefWriter.writePreferenceFile(appDir + "/PANACEA_Preferences.json");
	}

	public static void NALog(String logLevel, String msg, String cls, String method) {
		if(startLogging)
			logger.logp(java.util.logging.Level.parse(logLevel), cls, method, msg);
	}
	public static void NALog(String logLevel, String msg) {
		if(startLogging)
			logger.logp(java.util.logging.Level.parse(logLevel.toUpperCase()), "", "", msg);
	}
	public static void NALog( String msg) {
		if(startLogging)
			logger.logp(java.util.logging.Level.INFO, "", "", msg);
	}
	public static void NALog( String msg, boolean enforced) {
		if(startLogging || enforced)
			logger.logp(java.util.logging.Level.INFO, "", "", msg);
	}
}