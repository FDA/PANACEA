package com.eng.cber.na.removal;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.urls.StandardXYURLGenerator;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;

import com.eng.cber.na.chart.NodeXYSeries;
import com.eng.cber.na.chart.NodeXYToolTipGenerator;
import com.eng.cber.na.graph.GeneralNode;
import com.eng.cber.na.removal.NodeChartDataSet.Pair;

/**
 * An implementation of a JPanel that contains
 * the line charts for nodal metrics.  This JPanel
 * is not surrounded by the other additional 
 * supporting data (like text) -- instead it is just a
 * JPanel that knows how to display the line 
 * chart and to highlight min/max values in
 * that chart.
 *
 */
@SuppressWarnings("serial")
public class NodeChartPanel extends javax.swing.JPanel {
	
	XYSeriesCollection nodeDataset;
	NodeChartData nodeData;

	double minHighlightedValue = 1.;
	double maxHighlightedValue = 1.;

	static JFreeChart chart;
	
	public NodeChartPanel(NodeChartData nd) {
		this.nodeData = nd;
		ChartPanel chartPanel = getNodeChartPanel(nodeData);
        this.setLayout(new java.awt.BorderLayout());
        this.add(chartPanel, BorderLayout.CENTER);
        this.validate();
        
        maxHighlightedValue = nodeData.getNumItems();
	}
	
	public void changeDataset(NodeChartData nd) {
		nodeData = nd;
        XYPlot p = (XYPlot)chart.getPlot();
        p.setDataset(getChartDataset(nodeData.getSortedMetrics()));
	}
	
	public void setMinHighlight(int i) {
    	minHighlightedValue = i; 
    	chart.fireChartChanged();
	}
	
	public void setMaxHighlight(int i) {
    	maxHighlightedValue = i; 
    	chart.fireChartChanged();
	}
	
	private ChartPanel getNodeChartPanel(NodeChartData nodeData) {
		// Get data
	    nodeDataset = getChartDataset(nodeData.getSortedMetrics());
	    
	    // Get chart from data
	    String title = "Distribution of Nodal Metrics";
	    String xaxis = "Rank of Node";
	    String yaxis = "Value";
		PlotOrientation orientation = PlotOrientation.VERTICAL; 
		boolean showLegend = false; 
		boolean toolTips = true;
		boolean urls = false; 
	    JFreeChart edgeWeightHistChart = createChart(title, xaxis, yaxis,
				nodeDataset, orientation, showLegend, toolTips, urls);
	    
	    // Create and return panel containing chart
        ChartPanel chartPanel = new ChartPanel(edgeWeightHistChart);
        return chartPanel;
	}
	
	// input metrics is assumed already sorted high to low
	private XYSeriesCollection getChartDataset(List<Pair<GeneralNode,Double>> metrics) {
		NodeXYSeries s = new NodeXYSeries();
		
	    if (metrics.size() == 0) {
	    	return new XYSeriesCollection();
	    }

	    int rank = 1;
	    for (Pair<GeneralNode,Double> m : metrics) {
	    	s.add(rank, m.value, m.key);
	    	rank++;
	    }

    	XYSeriesCollection nodeDataset = new XYSeriesCollection(s);
	    return nodeDataset;
	}

	
	// Initial version copied from ChartFactory.createHistogram source
	private JFreeChart createChart(String title,
										String xAxisLabel,
										String yAxisLabel,
										IntervalXYDataset dataset,
										PlotOrientation orientation,
										boolean legend,
										boolean tooltips,
										boolean urls) {	
		if (orientation == null) {
			throw new IllegalArgumentException("Null 'orientation' argument.");
		}
		final NumberAxis xAxis = new NumberAxis(xAxisLabel);
		xAxis.setAutoRangeIncludesZero(false);

		ValueAxis yAxis = new NumberAxis(yAxisLabel);
		
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer() {
			@Override
			public java.awt.Paint getItemPaint(int series, int item) {
				java.awt.Paint origPaint = super.getItemPaint(series, item);	
				int revisedIndex = item + 1;
				if (minHighlightedValue > maxHighlightedValue) {
					if (revisedIndex <= maxHighlightedValue || revisedIndex >= minHighlightedValue) {
						return java.awt.Color.YELLOW;
					}
					else {
						return origPaint;
					}
				}
				if (revisedIndex >= minHighlightedValue && revisedIndex <= maxHighlightedValue) {
					return java.awt.Color.YELLOW;
				}
				else {
					return origPaint;
				}
			}
		};
		
		if (tooltips) {
			renderer.setBaseToolTipGenerator(new NodeXYToolTipGenerator() );
		}
		if (urls) {
			renderer.setURLGenerator(new StandardXYURLGenerator());
		}
		renderer.setUseOutlinePaint(true);
		renderer.setBaseOutlineStroke(new BasicStroke(0.25f));
		renderer.setBaseOutlinePaint(Color.red.darker());
		renderer.setSeriesShape(0,ShapeUtilities.createDiamond(3f));
		renderer.setSeriesStroke(0, new BasicStroke(1f));

		XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
		plot.setOrientation(orientation);
		
		chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
		plot, legend);
		
		return chart;
	}
}
