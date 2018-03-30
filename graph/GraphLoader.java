package com.eng.cber.na.graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.vaers.VAERS_Node;

/**
 * This is a graph loader specifically for the VAERS
 * file format that parses two comma-delimited text files 
 * and creates VAERS_Nodes related to report IDs for
 * those files.<br/><br/>
 *
 * Assumes data in two files linked by a report ID.  One
 * file contains all the vaccines; the other contains all the 
 * symptoms. For instance:<br/><br/>
 *
 * 
 * PT.txt (report 384790 has 2 symptoms):<br/>
 * <code>
 * 384790,"Dizziness"<br/>
 * 384790,"Vomiting"<br/>
 * 384256,"Dizziness"<br/>
 * 385283,"Oedema"<br/>
 * </code><br/>
 * 
 * VAX.txt (report 384256 has 3 vaccines):<br/>
 * <code>
 * 384790,"VARICELLA (VARIVAX)"<br/>
 * 384256,"MENINGOCOCCAL CONJUGATE (MENACTRA)"<br/>
 * 384256,"TDAP (ADACEL)"<br/>
 * 384256,"VARICELLA (VARIVAX)"<br/>
 * 385283,"HEP A (VAQTA)"<br/>
 * </code><br/>
 * 
 * Each entry will be treated as a String and will be put in
 * lower case. The entries should not contain any
 * extraneous (i.e. non-matched) double-quote characters (").
 * Any additional columns in the files will be ignored.
 * No headings should be included in either file.
 * 
 */
public class GraphLoader implements Serializable{

	private VAERS_Node.MedDRA level = VAERS_Node.MedDRA.PT;
	protected HashMap<Object, VAERS_Node> node_hash = new HashMap<Object, VAERS_Node>();
	protected Map<Object, Set<VAERS_Node>> report_hash = new HashMap<Object, Set<VAERS_Node>>();
	protected Map<Object, VAERS_Node> dual_node_hash = new HashMap<Object, VAERS_Node>();
	protected Map<Object, Set<VAERS_Node>> dual_report_hash = new HashMap<Object, Set<VAERS_Node>>();
	private Map<VAERS_Node.MedDRA, Set<String>> meddra_sets = new EnumMap<VAERS_Node.MedDRA, Set<String>>(VAERS_Node.MedDRA.class);
	private String vaxFilePath;
	private String ptFilePath;
	
	public GraphLoader() {			
		for (VAERS_Node.MedDRA meddra : VAERS_Node.MedDRA.values()) {
			meddra_sets.put(meddra, new TreeSet<String>());
		}
	}
		
	/**
	 * Returns nodeID <-> VAERS_Node.
	 */
	public Map<Object, VAERS_Node> getNodeHash() {
		if (NetworkAnalysisVisualization.getInstance().getDualState())
		{
			return dual_node_hash;
		}
		else
		{
			return node_hash;
		}
	}
	public void clean(){
		node_hash = null;
		report_hash = null;
		dual_node_hash = null;
		dual_report_hash = null;
	}
	public Map<Object, VAERS_Node> getOrigNodeHash() {
		return node_hash;
	}
	public Map<Object, Set<VAERS_Node>> getOrigReportHash() {
		return report_hash;
	}

	public int getOrigTotalReportCount() {
		return report_hash.size();
	}
	
	/**
	 * Returns reportID <-> VAERS_Node.
	 */
	public Map<Object, Set<VAERS_Node>> getReportHash() {
		if (NetworkAnalysisVisualization.getInstance().getDualState())
		{
			return dual_report_hash;
		}
		else
		{
			return report_hash;
		}
	}
	
	public VAERS_Node.MedDRA getLevel() {
		return level;
	}
	
	public Set<VAERS_Node> getNodesFromReport(Object r) {
		if (NetworkAnalysisVisualization.getInstance().getDualState())
		{
			return dual_report_hash.get(r);
		}
		else
		{
			return report_hash.get(r);
		}
	}
	
	public Map<VAERS_Node.MedDRA, Integer> getMeddraCounts() {
		Map<VAERS_Node.MedDRA, Integer> counts = new EnumMap<VAERS_Node.MedDRA, Integer>(VAERS_Node.MedDRA.class);
		for (VAERS_Node.MedDRA meddra : VAERS_Node.MedDRA.values()) {
			counts.put(meddra, new Integer(meddra_sets.get(meddra).size()));
		}
		return counts;
	}
	
	/**
	 * @return all nodes that appear in this data, sorted
	 * by their ID
	 */
	public List<VAERS_Node> getNodes() {
		List<VAERS_Node> list;
		if (NetworkAnalysisVisualization.getInstance().getDualState())
		{
			list = new ArrayList<VAERS_Node>(dual_node_hash.values());
		}
		else
		{
			list = new ArrayList<VAERS_Node>(node_hash.values());
		}
		Collections.sort(list, new Comparator<VAERS_Node>() {
			@Override
			public int compare(VAERS_Node v1, VAERS_Node v2) {
				return v1.getID().compareTo(v2.getID());
			}			
		});
		return list;
	}
	
	/** This form is for parsing the vaccine file. **/
	public void parse(String filename, VAERS_Node.NodeType node_type) throws FileNotFoundException, NumberFormatException {
		String[] tokens;
		// Match a comma only if it has an even number of quote marks following it on the same line
		// (i.e. not inside a quoted string).  Should work fine for .csv files without any quotation marks, too.
		String delims = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
		
		File fFile = new File(filename);
		Scanner fScan = new Scanner(new FileReader(fFile));
		while ( fScan.hasNextLine() ) {
			String line = fScan.nextLine();
			
			// Add to enable blank lines
			if (line.trim().length() == 0) {
				continue;
			}
			
			tokens = line.split(delims);
			
			if (tokens.length < 2) {
				break;
			}
			
			String nodeid = stripString(tokens[1]).toLowerCase();
			String reportid = stripString(tokens[0]); // Remove quotation marks
			
			vaxParse(nodeid, reportid, node_type, node_hash, report_hash);
		}
		fScan.close();
	}
	
	private void vaxParse(Object nodeid, Object reportid, VAERS_Node.NodeType node_type, HashMap<Object, VAERS_Node> node_map, Map<Object, Set<VAERS_Node>> report_map)
	{
		// add or update nodes
		VAERS_Node node = null;
		Set<VAERS_Node> report_nodes = null;
		if (node_map.containsKey(nodeid)) {
			node = node_map.get(nodeid);
			node.appendReport(reportid);
		}
		else {
			node = new VAERS_Node(nodeid, node_type, reportid);
			node_map.put(nodeid, node);
		}
		
		// add or update edges
		if (report_map.containsKey(reportid)) {
			report_nodes = report_map.get(reportid);
			if (!report_nodes.contains(node)) {
				report_nodes.add(node);	
			}
		}
		else {
			report_nodes = new HashSet<VAERS_Node>();
			report_nodes.add(node);
			report_map.put(reportid, report_nodes);
		}
	}
	
	/** This form is for parsing the symptom file. **/
	public void parse(String filename,VAERS_Node.NodeType node_type, VAERS_Node.MedDRA level) throws FileNotFoundException, ArrayIndexOutOfBoundsException, NumberFormatException {
		this.level = level;
		
		// Match a comma only if it has an even number of quote marks following it
		// on the same line (i.e. not inside a quoted string).
		String delims = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
		File fFile = new File(filename);
		Scanner fScan = new Scanner(new FileReader(fFile));
		while ( fScan.hasNextLine() ) {
			String line = fScan.nextLine();
			
			// Add to enable blank lines
			if (line.trim().length() == 0) {
				continue;
			}
			
			String[] tokens = line.split(delims);
			
			if (tokens.length < 2) {
				break;
			}
			
			String reportid = stripString(tokens[0]); // Remove quotation marks
			String nodeid = stripString(tokens[level.ordinal() + 1]).toLowerCase();		
			
			symParse(nodeid, reportid, node_type, tokens, node_hash, report_hash, true);
		}
		fScan.close();
	}

	private void symParse(Object nodeid, Object reportid, VAERS_Node.NodeType node_type, String[] tokens, Map<Object, VAERS_Node> node_map, Map<Object, Set<VAERS_Node>> report_map, boolean meddra)
	{
		Set<VAERS_Node> report_nodes = null;
		VAERS_Node node = null;
		// add or update nodes
		if (node_map.containsKey(nodeid)) {
			// update node
			node = node_map.get(nodeid);
			node.appendReport(reportid);
			
		}
		else {
			// add the new node
			node = new VAERS_Node(nodeid, node_type, reportid);
			
			node_map.put(nodeid, node);
		}
		
		// add or update edges
		if (report_map.containsKey(reportid)) {
			report_nodes = report_map.get(reportid);
			if (!report_nodes.contains(node)) {
				report_nodes.add(node);	
			}
		}
		else {
			report_nodes = new HashSet<VAERS_Node>();
			report_nodes.add(node);
			report_map.put(reportid, report_nodes);
		}
	}

	public void generateDual(){
		VAERS_Node report = null;
		
		for(Map.Entry<Object,VAERS_Node> entry : node_hash.entrySet()) {
			VAERS_Node node = entry.getValue();
			Set<?> report_ids = node.getReports(); 
			
			for(Object obj: report_ids){
				if (dual_node_hash.containsKey(obj)){ //A VAERS_Node for the report exists already
					report = dual_node_hash.get(obj);
					report.appendReport(node.getID());
					
				}
				else{// Generate a new VAERS_Node for the report
					report = new VAERS_Node(obj, VAERS_Node.NodeType.REPORT, node.getID());
					dual_node_hash.put(obj, report);
				}
				
			}
		}
		
		
		for(Map.Entry<Object,VAERS_Node> entry : node_hash.entrySet()) {
			VAERS_Node node = entry.getValue();
			Set<?> report_ids = node.getReports();
			
			Set<VAERS_Node> node_reports = new HashSet<VAERS_Node>();

			for(Object obj: report_ids){
				report = dual_node_hash.get(obj);
				node_reports.add(report);
			}
			dual_report_hash.put(node.getID(), node_reports);
		}
	}
	
	public void populateDegree() {
		Set<VAERS_Node> tmpSet = new HashSet<VAERS_Node>();
		for(Map.Entry<Object,VAERS_Node> entry : node_hash.entrySet()) {
			VAERS_Node node = entry.getValue();
				
			
			Set<?> report_ids = node.getReports();
			for(Object report_id : report_ids) {
				Set<VAERS_Node> nodes = report_hash.get(report_id);
				tmpSet.addAll(nodes);
				node.setDegree(report_id, tmpSet.size() - 1);
			}
			tmpSet.clear();
		}
	}

	public static String stripString(String str) {
		if(str==null )
			return "";
		String trimmed = str.trim();
		if (trimmed.equals("") || trimmed.equals("\""))
			return "";
		
		if(trimmed.charAt(0) == '\"') {
			trimmed = trimmed.substring(1,trimmed.length());
		}
		if(trimmed.charAt(trimmed.length() - 1) == '\"') {
			trimmed = trimmed.substring(0,trimmed.length() - 1);
		}
		return trimmed.trim();
	}
	
	/** Static method to create and return a GraphLoader instance for the given files. **/
	public static GraphLoader populateVAERSData(String VAX_PATH, String SYM_PATH, VAERS_Node.MedDRA level) throws FileNotFoundException, ArrayIndexOutOfBoundsException, NumberFormatException {

		GraphLoader gl = new GraphLoader();
		gl.setPtFilePath(SYM_PATH);
		gl.setVaxFilePath(VAX_PATH);
		gl.parse(SYM_PATH, VAERS_Node.NodeType.SYM, level);
		gl.parse(VAX_PATH, VAERS_Node.NodeType.VAX);
		
		gl.populateDegree();
		
		NetworkAnalysisVisualization.logger.logp(java.util.logging.Level.INFO,"","","Node size: " + gl.getNodeHash().size() + 
				"; Report Size: " + gl.getReportHash().size());

		if (NetworkAnalysisVisualization.getInstance().getDualState())
			gl.generateDual();
		return gl;
	}

	public void setVaxFilePath(String vaxFilePath) {
		this.vaxFilePath = vaxFilePath;
		
	}

	public void setPtFilePath(String ptFilePath) {
		this.ptFilePath = ptFilePath;
	}

	public String getVaxFilePath() {
		return vaxFilePath;
	}
	
	public String getPtFilePath() {
		return ptFilePath;
	}
}

