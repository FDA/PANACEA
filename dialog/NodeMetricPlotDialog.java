package com.eng.cber.na.dialog;

import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Ellipse2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.chart.ExtendedChartFactory;
import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.graph.GeneralNode;
import com.eng.cber.na.vaers.VAERS_Node;

/**
 * A modal (interrupting) dialog that displays a 2D plot
 * of centrality metrics (e.g. closeness, betweenness)
 * for the vertices in the current graph.  Allows user
 * to select a metric for each axis. 
 * 
 * Uses JFreeChart which has automatic support for
 * zooming to a rectangular selection, saving chart
 * to .png file, and other useful options.
 */
public class NodeMetricPlotDialog extends JDialog {

	private static String defaultXAxisType = "Closeness";
	private static String defaultYAxisType = "Betweenness";
	private static boolean defaultLogScaleForX = false;
	private static boolean defaultLogScaleForY = false;
	private static boolean defaultShowLabels = true;
	
	private static String[] axisTypes = {"Betweenness", "Closeness", "Degree", "Strength"};
	
	private final GeneralGraph graph;
	private final Collection<GeneralNode> nodes;
	
	private ChartPanel chartPanel;
	
	private JComboBox horAxisComboBox, vertAxisComboBox;
	private JCheckBox horAxisLogScale, vertAxisLogScale, showLabels;
	private JButton helpButton;
	
	public NodeMetricPlotDialog(NetworkAnalysisVisualization nv) {	
		super(nv,"Node Centrality Metrics",true);

		graph = nv.getGraph();
		nodes = graph.getVertices();

		JPanel controlPanel = createControlPanel();
		JFreeChart initialPlot = createNewPlot(graph, nodes, defaultXAxisType, defaultYAxisType, defaultLogScaleForX, defaultLogScaleForY, defaultShowLabels);
		chartPanel = new ChartPanel(initialPlot);

		setLayout(new BoxLayout(getContentPane(),BoxLayout.PAGE_AXIS));
		add(chartPanel);
		add(controlPanel);
		
		pack();
		setLocationRelativeTo(nv);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setVisible(true);
		
	}
	
	/**
	 * Initializes the control panel that will be placed
	 * below the plot.  Has JComboBoxes to choose the 
	 * centrality metric and JCheckBoxes to set log
	 * scaling and a help button.
	 * @return A JPanel with the controls arranged horizontally.
	 */
	private JPanel createControlPanel() {
		
		PlotOptionListener plotOptionListener = new PlotOptionListener();
		
		horAxisComboBox = new JComboBox(axisTypes);
		horAxisComboBox.setSelectedItem(defaultXAxisType);
		horAxisComboBox.addActionListener(plotOptionListener);
		horAxisLogScale = new JCheckBox("Log Scale?", defaultLogScaleForX);
		horAxisLogScale.addActionListener(plotOptionListener);
		
		JPanel xAxisBoxPanel = new JPanel();
		xAxisBoxPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		xAxisBoxPanel.add(new JLabel("X-Axis Metric:  "));
		xAxisBoxPanel.add(horAxisComboBox);
		xAxisBoxPanel.add(horAxisLogScale);
		
		vertAxisComboBox = new JComboBox(axisTypes);
		vertAxisComboBox.setSelectedItem(defaultYAxisType);
		vertAxisComboBox.addActionListener(plotOptionListener);
		vertAxisLogScale = new JCheckBox("Log Scale?", defaultLogScaleForY);
		vertAxisLogScale.addActionListener(plotOptionListener);

		JPanel yAxisBoxPanel = new JPanel();
		yAxisBoxPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		yAxisBoxPanel.add(new JLabel("Y-Axis Metric: "));
		yAxisBoxPanel.add(vertAxisComboBox);
		yAxisBoxPanel.add(vertAxisLogScale);
		
		showLabels = new JCheckBox("Show Labels?", defaultShowLabels);
		showLabels.addItemListener(plotOptionListener);
		
		helpButton = new JButton("Help");
		helpButton.addActionListener(new PlotHelpButtonListener());
		
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		controlPanel.add(xAxisBoxPanel);
		controlPanel.add(yAxisBoxPanel);
		controlPanel.add(showLabels);
		controlPanel.add(helpButton);
		
		return controlPanel;
	}
	
	public void updatePlot() {
		// Get the current settings of all the options in the control panel
		String xAxisType = (String) horAxisComboBox.getSelectedItem();
		String yAxisType = (String) vertAxisComboBox.getSelectedItem();
		boolean horLogScale = horAxisLogScale.isSelected();
		boolean vertLogScale = vertAxisLogScale.isSelected();
		boolean labels = showLabels.isSelected();
		
		JFreeChart newPlot = createNewPlot(graph, nodes, xAxisType, yAxisType, horLogScale, vertLogScale, labels);
		
		chartPanel.setChart(newPlot);
	}
	
	public JFreeChart createNewPlot(final GeneralGraph graph, final Collection<GeneralNode> nodes, String xAxisType, String yAxisType, boolean horAxisLog, boolean vertAxisLog, boolean showLabels) {
		// For every node, create an XYSeries object and add it to an XYSeriesCollection.
		XYSeriesCollection dotsToPlot = new XYSeriesCollection();
		Map<Integer, Shape> seriesToShape = new HashMap<Integer, Shape>();
		for (GeneralNode n : nodes) {
			XYSeries dot = new XYSeries(n.getID());
			
			// Get the selected metrics for this node
			Double xCoord, yCoord;
			if (xAxisType.equals("Betweenness")) {
				xCoord = graph.getBetweenness(n);
			}
			else if (xAxisType.equals("Closeness")) {
				xCoord = graph.getCloseness(n);
			}
			else if (xAxisType.equals("Degree")) {
				xCoord = Double.valueOf(graph.getDegree(n));
			}
			else {
				xCoord = graph.getStrength(n);
			}
			
			if (yAxisType.equals("Betweenness")) {
				yCoord = graph.getBetweenness(n);
			}
			else if (yAxisType.equals("Closeness")) {
				yCoord = graph.getCloseness(n);
			}
			else if (yAxisType.equals("Degree")) {
				yCoord = Double.valueOf(graph.getDegree(n));
			}
			else {
				yCoord = graph.getStrength(n);
			}
			
			dot.add(xCoord, yCoord);
			
			// Keep track of what shape the point should be
			if (n instanceof VAERS_Node) {
				if (((VAERS_Node)n).getNodeType() == VAERS_Node.NodeType.VAX) {
	    			Shape square = new Rectangle(-2,-2,4,4);
	    			seriesToShape.put(dotsToPlot.getSeriesCount(), square);
	    		}
	    		else {
	    			Shape circle = new Ellipse2D.Double(-2,-2,4,4);
	    			seriesToShape.put(dotsToPlot.getSeriesCount(), circle);
	    		}  
			}
			else {
				Shape square = new Rectangle(-2,-2,4,4);
				seriesToShape.put(dotsToPlot.getSeriesCount(), square);
			}
			
			// Add the series to the set
			dotsToPlot.addSeries(dot);
		}
		
		// Create a scatter plot from the XYSeriesCollection
		// with appropriate settings for the axes.
		JFreeChart plot;
		if (horAxisLog && vertAxisLog) {
			plot = ExtendedChartFactory.createLogLogScatterPlot("Centrality Metrics", xAxisType+" (log scale)", yAxisType+" (log scale)", dotsToPlot, seriesToShape, PlotOrientation.VERTICAL, showLabels, false, false, false);
		} 
		else if (horAxisLog && !vertAxisLog) {
			plot = ExtendedChartFactory.createLogXScatterPlot("Centrality Metrics", xAxisType+" (log scale)", yAxisType, dotsToPlot, seriesToShape, PlotOrientation.VERTICAL, showLabels, false, false, false);
		}
		else if (!horAxisLog && vertAxisLog) {
			plot = ExtendedChartFactory.createLogYScatterPlot("Centrality Metrics", xAxisType, yAxisType+" (log scale)", dotsToPlot, seriesToShape, PlotOrientation.VERTICAL, showLabels, false, false, false);
		}
		else {
			plot = ExtendedChartFactory.createScatterPlot("Centrality Metrics", xAxisType, yAxisType, dotsToPlot, seriesToShape, PlotOrientation.VERTICAL, showLabels, false, false, false);
		}

		return plot;
	}

	
	/**
	 * A listener class for the JComboBoxes and
	 * JCheckboxes in the control panel.  Updates
	 * the plot when any of the axis options are
	 * changed.  Adds/removes labels when the label
	 * checkbox is changed.
	 */
	public class PlotOptionListener implements ActionListener, ItemListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			updatePlot();
		}

		@Override
		public void itemStateChanged(ItemEvent e) {
			// This is a change in the "Show Labels" checkbox.
			
			((XYPlot)chartPanel.getChart().getPlot()).getRenderer().setBaseItemLabelsVisible(showLabels.isSelected());
		}
		
	}
	
	public class PlotHelpButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(((JButton)e.getSource()).getParent().getParent(), 
										  "<html><p style=\"width:500px;\">" +
										  "Betweenness: A measure of how often this node is part of the shortest path between two other nodes.<br/><br/>" +
										  "Closeness: A measure related to the number of steps required to reach any given node in the network.<br/><br/>" +
										  "Degree: A measure of the the number of nodes that are directly connected to this node.<br/><br/>" +
										  "Strength: A measure of the number of shared terms in the connections between this node and its neighbors." +
										  "</p></html>",
										  "Metric Information",
										  JOptionPane.INFORMATION_MESSAGE);
		}
	}
}
