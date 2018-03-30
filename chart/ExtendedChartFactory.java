package com.eng.cber.na.chart;


import java.awt.Shape;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.urls.StandardXYURLGenerator;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.xy.XYDataset;


/** 
 * Provides some useful chart creation methods that
 * were not available in the original ChartFactory
 * from JFreeChart.  Specifically, adds support for
 * plots with log-scaling on one or both axes.  The
 * create plot methods also allow for customized
 * shapes for each point and drawing/hiding vertex
 * labels.
 */
public abstract class ExtendedChartFactory extends ChartFactory {

    /**
     * Slightly edited version of ChartFactory's createScatterPlot
     * method.  Uses a custom renderer to add support for
     * customized shapes for each point and drawing/hiding labels. 
     *
     * ** BEGIN ORIGINAL JAVADOC **
     * Creates a scatter plot with default settings.  The chart object
     * returned by this method uses an {@link XYPlot} instance as the plot,
     * with a {@link NumberAxis} for the domain axis, a  {@link NumberAxis}
     * as the range axis, and an {@link XYLineAndShapeRenderer} as the
     * renderer.
     *
     * @param title  the chart title (<code>null</code> permitted).
     * @param xAxisLabel  a label for the X-axis (<code>null</code> permitted).
     * @param yAxisLabel  a label for the Y-axis (<code>null</code> permitted).
     * @param dataset  the dataset for the chart (<code>null</code> permitted).
     * @param seriesToShape  a map of datapoint index to shape it should be drawn as.
     * @param orientation  the plot orientation (horizontal or vertical)
     *                     (<code>null</code> NOT permitted).
     * @param labels  a flag specifying whether vertex labels should be shown.
     * @param legend  a flag specifying whether or not a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return A scatter plot.
     */
    public static JFreeChart createScatterPlot(String title, String xAxisLabel,
            String yAxisLabel, XYDataset dataset, Map<Integer,Shape> seriesToShape,
            PlotOrientation orientation, boolean labels,
            boolean legend, boolean tooltips, boolean urls) {

        if (orientation == null) {
            throw new IllegalArgumentException("Null 'orientation' argument.");
        }
        NumberAxis xAxis = new NumberAxis(xAxisLabel);
        xAxis.setAutoRangeIncludesZero(true);
        NumberAxis yAxis = new NumberAxis(yAxisLabel);
        yAxis.setAutoRangeIncludesZero(true);
        yAxis.setUpperMargin(0.1); // 10% extra space at top of plot, so labels are less likely to be cut off
        
        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, null);

        XYToolTipGenerator toolTipGenerator = null;
        if (tooltips) {
            toolTipGenerator = new StandardXYToolTipGenerator();
        }

        XYURLGenerator urlGenerator = null;
        if (urls) {
            urlGenerator = new StandardXYURLGenerator();
        }
        
        XYItemRenderer renderer = new XYLineAndShapeRenderer(false, true);
        // Ensure the correct shapes are used
        for (int i = 0; i < dataset.getSeriesCount(); i++){
        	renderer.setSeriesShape(i, seriesToShape.get(i));
        }
        
        renderer.setBaseToolTipGenerator(toolTipGenerator);
        renderer.setURLGenerator(urlGenerator);
        
        // PLT added to show label
        StandardXYItemLabelGenerator labelGenerator = new StandardXYItemLabelGenerator() {		
			public String generateLabel(XYDataset dataset, int series, int item) {
				if (dataset.getSeriesKey(series) instanceof String) {
					return (String)dataset.getSeriesKey(series);
				}
				return ""; 
			}
		};
		renderer.setBaseItemLabelGenerator(labelGenerator);
		renderer.setBaseItemLabelsVisible(labels); // Only show labels if requested
        
        plot.setRenderer(renderer);
        plot.setOrientation(orientation);
        
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
                plot, legend);
        return chart;

    }

    public static JFreeChart createLogXScatterPlot(String title, String xAxisLabel,
    		String yAxisLabel, XYDataset dataset, Map<Integer,Shape> seriesToShape,
    		PlotOrientation orientation, boolean labels,
    		boolean legend, boolean tooltips, boolean urls) {
    	
    	return createLogScatterPlot(title, xAxisLabel, true, yAxisLabel, false, dataset, seriesToShape, orientation, labels, legend, tooltips, urls); 
    }
    
    public static JFreeChart createLogYScatterPlot(String title, String xAxisLabel,
    		String yAxisLabel, XYDataset dataset, Map<Integer,Shape> seriesToShape,
    		PlotOrientation orientation, boolean labels,
    		boolean legend, boolean tooltips, boolean urls) {
    	
    	return createLogScatterPlot(title, xAxisLabel, false, yAxisLabel, true, dataset, seriesToShape, orientation, labels, legend, tooltips, urls); 
    }
 
    public static JFreeChart createLogLogScatterPlot(String title, String xAxisLabel,
    		String yAxisLabel, XYDataset dataset, Map<Integer,Shape> seriesToShape,
    		PlotOrientation orientation, boolean labels,
    		boolean legend, boolean tooltips, boolean urls) {
    	
    	return createLogScatterPlot(title, xAxisLabel, true, yAxisLabel, true, dataset, seriesToShape, orientation, labels, legend, tooltips, urls); 
    }
    
	protected static JFreeChart createLogScatterPlot(String title, String xAxisLabel,
			boolean xAxisLogScale, String yAxisLabel, boolean yAxisLogScale,
			XYDataset dataset, Map<Integer,Shape> seriesToShape,
			PlotOrientation orientation,  boolean labels,
			boolean legend, boolean tooltips, boolean urls) {
        if (orientation == null) {
            throw new IllegalArgumentException("Null 'orientation' argument.");
        }
        // Use a LogAxis if requested
        ValueAxis xAxis = xAxisLogScale ? new LogAxis(xAxisLabel) : new NumberAxis(xAxisLabel);
        // Help to ensure that there is space for the labels
        xAxis.setLowerMargin(0.20);
        xAxis.setUpperMargin(0.20);
        ValueAxis yAxis = yAxisLogScale ? new LogAxis(yAxisLabel) : new NumberAxis(yAxisLabel);
        yAxis.setUpperMargin(0.10);
       
        
        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, null);

        XYToolTipGenerator toolTipGenerator = null;
        if (tooltips) {
            toolTipGenerator = new StandardXYToolTipGenerator();
        }

        XYURLGenerator urlGenerator = null;
        if (urls) {
            urlGenerator = new StandardXYURLGenerator();
        }
        XYItemRenderer renderer = new XYLineAndShapeRenderer(false, true);

        // Ensure the correct shapes are used
        for (int i = 0; i < dataset.getSeriesCount(); i++){
        	renderer.setSeriesShape(i, seriesToShape.get(i));
        }
        
        renderer.setBaseToolTipGenerator(toolTipGenerator);
        renderer.setURLGenerator(urlGenerator);

        // PLT addition to show labels
		StandardXYItemLabelGenerator labelGenerator = new StandardXYItemLabelGenerator() {		
			public String generateLabel(XYDataset dataset, int series, int item) {
				if (dataset.getSeriesKey(series) instanceof String) {
					return (String)dataset.getSeriesKey(series);
				}
				return ""; 
			}
		};
		renderer.setBaseItemLabelGenerator(labelGenerator);
		renderer.setBaseItemLabelsVisible(labels); // Only show labels if requested
		
        plot.setRenderer(renderer);
        plot.setOrientation(orientation);

        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
                plot, legend);
        
        return chart;
	}
}
