package com.eng.cber.na.removal;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.text.MessageFormat;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.urls.StandardXYURLGenerator;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;

/**
 * An implementation of a JPanel that contains
 * the histograms for edge weights.  This JPanel
 * is not surrounded by the other additional 
 * supporting data (like text) -- instead it is just a
 * JPanel that knows how to display a histogram
 * data and to highlight min/max values in
 * the histogram.
 *
 */
@SuppressWarnings("serial")
public class EdgeWeightHistogramPanel extends javax.swing.JPanel {
	
	HistogramDataset edgeWeightDataset;
	EdgeWeightHistogramData edgeData;

	int minHighlightedValue = 1;
	int maxHighlightedValue = 1;

	static JFreeChart chart;
	
	public EdgeWeightHistogramPanel(EdgeWeightHistogramData ed) {
		this.edgeData = ed;
		ChartPanel chartPanel = getEdgeWeightHistogramChartPanel(edgeData);
        this.setLayout(new java.awt.BorderLayout());
        this.add(chartPanel, BorderLayout.CENTER);
        this.validate();
        
        maxHighlightedValue = edgeData.getMaxWeight();
	}
	
	public void changeDataset(EdgeWeightHistogramData ed) {
		edgeData = ed;
        XYPlot p = (XYPlot)chart.getPlot();
        p.setDataset(getHistogramDataset(edgeData.getRawWeights()));

        maxHighlightedValue = edgeData.getMaxWeight();
	}
	
	public void setMinHighlight(int i) {
    	minHighlightedValue = i; 
    	chart.fireChartChanged();
	}
	
	public void setMaxHighlight(int i) {
    	maxHighlightedValue = i; 
    	chart.fireChartChanged();
	}
	
	private ChartPanel getEdgeWeightHistogramChartPanel(EdgeWeightHistogramData edgeData) {
		// Get data
	    edgeWeightDataset = getHistogramDataset(edgeData.getRawWeights());
	    
	    // Get chart from data
	    String title = "Distribution of Edge Weights";
	    String xaxis = "Edge Weight";
	    String yaxis = "Frequency";
		PlotOrientation orientation = PlotOrientation.VERTICAL; 
		boolean showLegend = false; 
		boolean toolTips = true;
		boolean urls = false; 
	    JFreeChart edgeWeightHistChart = createHistogram(title,
				xaxis,
				yaxis,
				edgeWeightDataset,
				orientation,
				showLegend,
				toolTips,
				urls);
	    
	    // Create and return panel containing chart
        ChartPanel chartPanel = new ChartPanel(edgeWeightHistChart);
        return chartPanel;
	}
	
	private HistogramDataset getHistogramDataset(double[] weights) {
		HistogramDataset edgeWeightDataset = new HistogramDataset();
	    edgeWeightDataset.setType(HistogramType.FREQUENCY);
	    
	    if (weights.length == 0 || edgeData.getMaxWeight() <= 0) {
	    	return new HistogramDataset();
	    }

	    int numBins = edgeData.getMaxWeight() - edgeData.getMinWeight() + 1;
    	edgeWeightDataset.addSeries("edge weight", weights, numBins);
    	
	    return edgeWeightDataset;
	}

	// Initial version copied from ChartFactory.createHistogram source
	private JFreeChart createHistogram(String title,
										String xAxisLabel,
										String yAxisLabel,
										IntervalXYDataset dataset,
										PlotOrientation orientation,
										boolean legend,
										boolean tooltips,
										boolean urls) {

		XYBarRenderer.setDefaultShadowsVisible(false);
		XYBarRenderer.setDefaultBarPainter(new org.jfree.chart.renderer.xy.StandardXYBarPainter());
		
		if (orientation == null) {
			throw new IllegalArgumentException("Null 'orientation' argument.");
		}
		final NumberAxis xAxis = new NumberAxis(xAxisLabel);
		xAxis.setAutoRangeIncludesZero(true);

		ValueAxis yAxis = new NumberAxis(yAxisLabel);
		
		XYBarRenderer renderer = new XYBarRenderer() {
			@Override
			public java.awt.Paint getItemPaint(int series, int item) {
				java.awt.Paint origPaint = super.getItemPaint(series, item);	
				int revisedIndex = item + edgeData.getMinWeight();
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
			renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator() { 
				public String generateLabelString(XYDataset dataset, int series, int item) {
					String result = null;    
			        Object[] items = createItemArray((XYDataset) dataset, series, item);
        
			        // Find the floor of x if x is less than halfway, and 
			        // the ceiling of x if x is more than halfway
			        String d = (String)items[1];
			        d = d.replaceAll("," , "");
			        Double i = Double.parseDouble(d); 
			        Double midPoint = edgeData.getMinWeight() + ((edgeData.getMaxWeight()-edgeData.getMinWeight())/2.0);
			        if (i < midPoint) {
			        	items[1] = Math.floor(i);
			        }
			        else {
			        	items[1] = Math.ceil(i);
			        }			        
			        result = MessageFormat.format(getFormatString(), items);
			        return result;
				}
			});
		}
		if (urls) {
			renderer.setURLGenerator(new StandardXYURLGenerator());
		}
		renderer.setDrawBarOutline(true);
		renderer.setBaseOutlineStroke(new BasicStroke(0.5f));
		renderer.setBaseOutlinePaint(Color.red.darker());

		XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
		plot.setOrientation(orientation);
		
		chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
		plot, legend);
		
		return chart;
	}
	
}
