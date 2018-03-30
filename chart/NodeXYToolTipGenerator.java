package com.eng.cber.na.chart;

import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeriesCollection;

/****
 * Extension of JFreeChart's XYToolTipGenerator that
 * produces a node/value pair label.
 *
 */
public class NodeXYToolTipGenerator implements XYToolTipGenerator {
	
	@Override
	public String generateToolTip(XYDataset dataset, int series, int item) {
		NodeXYSeries s = (NodeXYSeries) ((XYSeriesCollection) dataset).getSeries(series);
		return "Node: " + s.getNode((Double)s.getX(item)).getID() + ", Value: " + s.getY(item);
	}
}
