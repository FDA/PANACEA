package com.eng.cber.na.graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JOptionPane;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.vaers.VAERS_Node;

/**
 * This is a graph loader specifically for the Business Objects
 * file format that parses a comma-delimited text file and 
 * creates VAERS_Nodes related to report IDs for that file.<br/><br/>
 * 
 * Assumes data in the following format: <br/><br/>
 * 
 * <code>
 * "Report Id","Symptom","Vaccine"<br/>
 * "384790","Dizziness","VARICELLA (VARIVAX)"<br/>
 * "384790","Vomiting","VARICELLA (VARIVAX)"<br/>
 * "384256","Dizziness","MENINGOCOCCAL CONJUGATE (MENACTRA)"<br/>
 * "384256","Dizziness","TDAP (ADACEL)"<br/>
 * "384256","Dizziness","VARICELLA (VARIVAX)"<br/>
 * "385283","Oedema","HEP A (VAQTA)"<br/>
 * </code><br/>
 * 
 * Each entry will be treated as a String and will be put in
 * lower case. The entries should not contain any
 * extraneous (i.e. non-matched) double-quote characters (").
 * Any additional columns in the file will be ignored.
 * There should be exactly one header line in the file,
 * although it's content will not be read.
 * 
 */
public class BusinessObjectsGraphLoader extends GraphLoader {
	private String boFilePath;
	
	public void parse(String filename) throws FileNotFoundException, InterruptedException, Exception {
		Scanner fScan = null;
		try{
			String[] tokens;
			// Match a comma only if it has an even number of quote marks following it
			// on the same line (i.e. not inside a quoted string).
			String delims = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
			
			VAERS_Node nodeFirst;
			VAERS_Node nodeSecond;
			File fFile = new File(filename);
			fScan = new Scanner(new FileReader(fFile));
			
			fScan.nextLine(); // skip first line (column headings)
			while ( fScan.hasNextLine() ) {
				if (Thread.interrupted()) {
					throw new InterruptedException("BusinessObjects graph loading interrupted.");
				}
				
				tokens = fScan.nextLine().split(delims);
				
				if (tokens.length < 3) {
					break;
				}
				String reportString = tokens[0].replaceAll("\"", ""); // delete the quotation marks
				
				String reportid = reportString;
				
				String node_symptom = tokens[1].replaceAll("\"", "").toLowerCase();
				String node_vax = tokens[2].replaceAll("\"", "").toLowerCase();
				
				// add or update nodes
				nodeFirst = addOrUpdateNodes(node_symptom, VAERS_Node.NodeType.SYM, reportid, node_hash);
				nodeSecond = addOrUpdateNodes(node_vax, VAERS_Node.NodeType.VAX, reportid, node_hash);
				
				// add or update reports (edges)
				addOrUpdateReportNodes(reportid, nodeFirst, report_hash);
				addOrUpdateReportNodes(reportid, nodeSecond, report_hash);
			}
		}
		catch (Exception ex) {
			JOptionPane.showMessageDialog(null, "Problem encountered reading BusinessObjects file.  File format may be incorrect or file does not exist	.", "Problem reading from file", JOptionPane.ERROR_MESSAGE);
			throw ex;
		}
		finally {
			if (fScan != null) {
				fScan.close();
			}
		}
	}
	
	private VAERS_Node addOrUpdateNodes(Object node_symptom, VAERS_Node.NodeType type, Object reportid, Map<Object, VAERS_Node> hash) {
		VAERS_Node node;
		if (hash.containsKey(node_symptom)) {
			node = hash.get(node_symptom);
			node.appendReport(reportid);
		}
		else {
			node = new VAERS_Node(node_symptom, type, reportid);
			hash.put(node_symptom, node);
		}
		
		return node;
	}
	
	private void addOrUpdateReportNodes(Object reportid, VAERS_Node node, Map<Object, Set<VAERS_Node>> hash) {		
		if (hash.containsKey(reportid)) {
			Set<VAERS_Node> report_nodes = hash.get(reportid);
			if (!report_nodes.contains(node)) {
				report_nodes.add(node);	
			}
		}
		else {
			Set<VAERS_Node> report_nodes = new HashSet<VAERS_Node>();
			report_nodes.add(node);
			hash.put(reportid, report_nodes);
		}
	}
	
	/** Static method to create and return a GraphLoader instance for the given file. **/
	public static GraphLoader populateBOData(String path) throws FileNotFoundException, Exception {
		BusinessObjectsGraphLoader gl = new BusinessObjectsGraphLoader();
		gl.parse(path);
		gl.setBOFilePath(path);
		
		if(NetworkAnalysisVisualization.startLogging)
			NetworkAnalysisVisualization.logger.logp(java.util.logging.Level.INFO,"","","Node size: " + gl.getNodeHash().size() + 
					"; Report Size: " + gl.getReportHash().size());
		return gl;
	}
	
	public void setBOFilePath(String boFilePath) {
		this.boFilePath = boFilePath;
	}

	public String getBOFilePath() {
		return boFilePath;
	}
}
