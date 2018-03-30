package com.eng.cber.na.sim.comparison;

import java.awt.Color;
import java.awt.GradientPaint;
import java.util.List;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;

import com.eng.cber.na.graph.FDAGraph;
import com.eng.cber.na.graph.GeneralNode;

public class RealVsSimCharts {
	private FDAGraph realGraph;
	private List<List<Integer>> degreesForSimGraphs;
	private int maxDegree = Integer.MIN_VALUE;
	private int numBins;
	
	public RealVsSimCharts(FDAGraph realGraph, List<List<Integer>> degreesForSimGraphs) {
		this.realGraph = realGraph;
		this.degreesForSimGraphs = degreesForSimGraphs;
		determineMaxDegree();
		numBins = Math.max(5, maxDegree/15);
	}

	/** Determine the largest value that will go on the x-axis **/
	public void determineMaxDegree() {
		for (List<Integer> currList : degreesForSimGraphs) {
			int listMax = getMaxValue(currList);
			if (listMax > maxDegree)
				maxDegree = listMax;
		}
		if (realGraph.getMaxDegree() > maxDegree)
			maxDegree = realGraph.getMaxDegree();
	}
	
	public int getMaxDegree() {
		return maxDegree;
	}
	
	public int getNumBins() {
		return numBins;
	}
	
	public void setNumBins(int numBins) {
		this.numBins = numBins;
	}
	
	public JFreeChart createDegreeDistributionChart(boolean useLogDomain, boolean useNaturalLogarithm) {
		HistogramDataset dataset = new HistogramDataset();
		
		// Set up the dataset for the REAL data
		double[] realData = new double[realGraph.getVertexCount()];
		int iter = 0;
		for (GeneralNode node : realGraph.getVertices()) {
			realData[iter] = useLogDomain ? useNaturalLogarithm ? Math.log(realGraph.getDegree(node)) : Math.log10(realGraph.getDegree(node)) : realGraph.getDegree(node);
			iter++;
		}
		
		// Set up the dataset for the SIMULATED data
		int totalNumNodes = 0;
		for (int i = 0; i < degreesForSimGraphs.size(); i++) {
			totalNumNodes += degreesForSimGraphs.get(i).size();
		}
		double[] simData = new double[totalNumNodes];
		iter = 0;
		for (List<Integer> degreeList : degreesForSimGraphs) {
			for (Integer degree : degreeList) {
				simData[iter] = useLogDomain ? useNaturalLogarithm ? Math.log(degree) : Math.log10(degree) : degree;
				iter++;
			}
		}
		
		dataset.setType(HistogramType.RELATIVE_FREQUENCY);
		dataset.addSeries("Real", realData, numBins, 0, useLogDomain ? useNaturalLogarithm ? Math.log(maxDegree) : Math.log10(maxDegree) : maxDegree);
		dataset.addSeries("All " + degreesForSimGraphs.size() + " Simulation" + (degreesForSimGraphs.size() > 1 ? "s" : ""), simData, numBins, 0, useLogDomain ? useNaturalLogarithm ? Math.log(maxDegree) : Math.log10(maxDegree) : maxDegree);
		
		NumberAxis dAxis = new NumberAxis(useLogDomain ? useNaturalLogarithm ? "log( Degree )" : "log10( Degree )" : "Degree");
		NumberAxis rAxis = new NumberAxis("Relative Frequency");
		
		StandardXYBarPainter painter = new StandardXYBarPainter();
		XYBarRenderer.setDefaultBarPainter(painter);
		XYBarRenderer renderer = new XYBarRenderer();
		renderer.setGradientPaintTransformer(null);
		renderer.setShadowVisible(false);
		
		float hashWidth = 3.0f;
		GradientPaint upGradient = new GradientPaint(hashWidth, hashWidth, Color.RED, hashWidth*2, hashWidth*2, new Color(0,0,0,0), true);
		GradientPaint downGradient = new GradientPaint(hashWidth, hashWidth*2, Color.BLUE, hashWidth*2, hashWidth, new Color(0,0,0,0), true);
		
		renderer.setSeriesPaint(0, upGradient);
		renderer.setSeriesPaint(1, downGradient);
		
		XYPlot plot = new XYPlot(dataset, dAxis, rAxis, renderer);
		plot.setOrientation(PlotOrientation.VERTICAL);
		
		JFreeChart chart = new JFreeChart(plot);
		chart.setTitle("Degree Distribution");
		plot.setForegroundAlpha(0.75f);
		
		return chart;
	}
	
	private static int getMaxValue(List<Integer> inputList) {
		Integer max = Integer.MIN_VALUE;
		for (Integer val : inputList) {
			if (val > max)
				max = val;
		}
		return max;
	}
}
