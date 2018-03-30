package com.eng.cber.na.sim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.SwingWorker;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.vaers.VAERS_Node;

/**
 * Produces a set of the names of all
 * vaccines found in the reports in the current graph by
 * getting them from PANACEA instead of reading from a file.
 * 
 */
public class VaccineReaderPanacea extends SwingWorker<Object, Object> {

	private Set<String> vaccines = new TreeSet<String>();
	
	public VaccineReaderPanacea() {
		
	}
	
	public Set<String> getVaccines() {
		return vaccines;
	}
	
	@Override
	protected Object doInBackground() throws Exception {
		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
		
		setProgress(0);

		// Get the map of all reports to set of nodes in that report.
		Map<Object, Set<VAERS_Node>> reportIDToNodesMap = nv.getUnderlyingData().getOrigReportHash();
		// Get the set of reports in the current graph only.
		Set<Object> reportSet;
		if (nv.getGraph().isDual()) {
			reportSet = nv.getGraph().getNodeObjects();
		}
		else {
			reportSet = nv.getGraph().getReports();
		}
		// Get the sorted list of reports.
		List<Object> reportList = new ArrayList<Object>(reportSet);
		Collections.sort(reportList, new Comparator<Object>() {
			@Override
			public int compare(Object o1, Object o2) {
				if (o1 instanceof String && o2 instanceof String)
					return ((String) o1).compareTo((String) o2);
				else
					return ((Integer) o1).compareTo((Integer) o2);
			}
		});
		
		int i=1;
		// Loop over all the reports in the current graph
		for (Object report : reportSet) {
			
			Set<VAERS_Node> nodeSet = reportIDToNodesMap.get(report);
			// Loop over all the nodes mentioned in the report
			for (VAERS_Node node : nodeSet) {
				
				// Check if the node is a symptom or vaccine.
				if (node.getNodeType() == VAERS_Node.NodeType.VAX) {
					vaccines.add(node.getID());
				}
			}
			
			setProgress((int) ((double) i / reportSet.size() * 100));
			i++;
		}
		
		setProgress(100);
		
		return null;
	}

	@Override
	public void done() {
		firePropertyChange("done",null,null);
	}	
	
}
