package com.eng.cber.na.model;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.eng.cber.na.graph.GeneralEdge;
import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.graph.GeneralNode;
import com.eng.cber.na.graph.Graph_Object;
import com.eng.cber.na.vaers.VAERS_Comparator.BetweennessComparator;
import com.eng.cber.na.vaers.VAERS_Comparator.ClosenessComparator;
import com.eng.cber.na.vaers.VAERS_Comparator.ComparatorType;
import com.eng.cber.na.vaers.VAERS_Comparator.DegreeComparator;
import com.eng.cber.na.vaers.VAERS_Comparator.Direction;
import com.eng.cber.na.vaers.VAERS_Comparator.NameComparator;
import com.eng.cber.na.vaers.VAERS_Comparator.ReportCountComparator;
import com.eng.cber.na.vaers.VAERS_Comparator.StrengthComparator;
import com.eng.cber.na.vaers.VAERS_Comparator.TypeComparator;
import com.eng.cber.na.vaers.VAERS_Node;

/**
 * A tree model for the adjacent vertex table.  The tree
 * model stores a set of report identifiers beneath each
 * node in the tree, and it can be updated to sort by 
 * a particular property.
 *
 */
@SuppressWarnings("serial")
public class AdjacentVertexTreeModel extends DefaultTreeModel {

	private GeneralGraph g;
	private Graph_Object curObj;
	
	public AdjacentVertexTreeModel(GeneralGraph g) {
		super(null);
		this.g = g;
	}
	
	public void updateCurrent(ComparatorType type, Direction d) {
		if(curObj != null) {
			update(curObj,type,d);
		}	
	}
	
	public void clear() {
		setRoot(null);
	}
	
	public void update(Graph_Object obj, ComparatorType type, Direction d) {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(obj);
		setRoot(root);
		Collection<GeneralNode> neighbors = obj instanceof GeneralNode ? (Collection<GeneralNode>) (Collection<?>)g.getNeighbors((GeneralNode)obj): (Collection<GeneralNode>) (Collection<?>) g.getIncidentVertices((GeneralEdge)obj);
		List<GeneralNode> nodeList = new LinkedList<GeneralNode>(neighbors);
		switch(type) {
		case TYPE:
			Collections.sort(nodeList,new TypeComparator(d));
			break;
		case NAME:
			Collections.sort(nodeList,new NameComparator(d));
			break;
		case REPORT_COUNT:
			Collections.sort(nodeList,new ReportCountComparator(d));
			break;
		case BETWEENNESS:
			Collections.sort(nodeList,new BetweennessComparator(g,d));
			break;
		case CLOSENESS:
			Collections.sort(nodeList,new ClosenessComparator(g,d));
			break;
		case DEGREE:
			Collections.sort(nodeList,new DegreeComparator(g,d));
			break;
		case STRENGTH:
			Collections.sort(nodeList,new StrengthComparator(g,d));
			break;
		}		
		for(GeneralNode neighbor : nodeList) {
			DefaultMutableTreeNode neighborTreeNode = new DefaultMutableTreeNode(neighbor);
			root.add(neighborTreeNode);
			if(neighbor instanceof VAERS_Node){
				Set<?> reports = ((VAERS_Node)neighbor).getReports();
				for(Object report : reports) {
					neighborTreeNode.add(new DefaultMutableTreeNode(report));
				}
			}
		}
		curObj = obj;
	}
}
