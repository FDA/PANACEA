package com.eng.cber.na.dialog;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.chart.ExtendedChartFactory;
import com.eng.cber.na.graph.GeneralEdge;
import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.graph.GeneralNode;
import com.eng.cber.na.vaers.VAERS_Edge;
import com.eng.cber.na.vaers.VAERS_Node;

/**
 * A JDialog extension class that display the similarity plot for
 * the current network.  There must be a ReferenceDocument node in 
 * the network.
 * 
 * The plot has an X-axis of similarity value normalized between
 * zero and one and a Y-axis of number of shared PTs with the
 * ReferenceDocument.
 * 
 * There is also a static method to create the JFreeChart plot object,
 * so that the plot can be retrieved without showing the dialog.
 */
public class SMQSimilarityPlotDialog extends JDialog {

	JFreeChart plot;
	
	public SMQSimilarityPlotDialog(NetworkAnalysisVisualization nv) {
		super(nv, "Similarity Plot", false);
		
		GeneralGraph graph = nv.getGraph();
		
		init();
		plot = getSMQSimilarityPlot(graph);
		if (plot == null) {
			JOptionPane.showMessageDialog(nv, "<html><p style=\"width:300px;\">Couldn't find the ReferenceDocument.  Are you sure this is a similarity graph?</p></html>", "ERROR", JOptionPane.ERROR_MESSAGE);
			dispose();
			return;
		}
		ChartPanel plotPanel = new ChartPanel(plot);
		add(plotPanel);
		
		pack();
		setLocationRelativeTo(nv);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setVisible(true);
		
	}
	
	
	private void init() {
		
	}
	
	
	/**
	 * Creates a plot showing the reports in the current graph and their
	 * similarity to the ReferenceDocument node.
	 * 
	 * The plot has an X-axis of similarity value normalized between
	 * zero and one and a Y-axis of number of shared PTs with the
	 * ReferenceDocument.
	 */
	public static JFreeChart getSMQSimilarityPlot(GeneralGraph graph) {	
		Collection<GeneralNode> nodes = graph.getVertices();
		GeneralNode refReportNode = graph.findNodeByID("ReferenceDocument");
		
		if (refReportNode == null) {
			return null;
		}
		
		boolean flagMissingEdge = false;
		
		// For every node, create an XYSeries object and add it to an XYSeriesCollection.
		XYSeriesCollection dotsToPlot = new XYSeriesCollection();
		Map<Integer, Shape> seriesToShape = new HashMap<Integer, Shape>();
		for (GeneralNode n : nodes) {
			if (n.equals(refReportNode))
				continue;
			if (!graph.getNodeDisplay(n))
				continue;
			
			GeneralEdge edgeWithRef = graph.findEdge(n, refReportNode);
			if (edgeWithRef == null) {
				flagMissingEdge = true;
				continue;
			}
			
			XYSeries dot = new XYSeries(n.getID());
			double xCoord = edgeWithRef.getWeight();
			double yCoord = ((VAERS_Edge)edgeWithRef).getReports().size();
			
			
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
		
		if (flagMissingEdge) {
			JOptionPane.showMessageDialog(NetworkAnalysisVisualization.getInstance(), "At least one node has no edge connecting to ReferenceDocument.", "WARNING", JOptionPane.WARNING_MESSAGE);
		}
		
		// Create a scatter plot from the XYSeriesCollection
		// with appropriate settings for the axes.
		JFreeChart plot;
		plot = ExtendedChartFactory.createScatterPlot("Similarity of Reports in: " + graph.getName(), "Similarity to Reference Report", "Number of Shared Terms", dotsToPlot, seriesToShape, PlotOrientation.VERTICAL, true, false, false, false);
		
		plot.getXYPlot().getRangeAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		
		return plot;
	}
	
	
}
