package com.eng.cber.na.vaers;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.collections15.Predicate;

import com.eng.cber.na.graph.BasicGraphDataCalculator;
import com.eng.cber.na.graph.FDAGraph;
import com.eng.cber.na.graph.GeneralEdge;
import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.graph.GeneralNode;

/**
 * The VAERS_EdgePredicateFilter is a filter that transforms
 * a source network according to a Predicate object that 
 * allows particular edges and disallows other edges. 
 * 
 * This is a PANACEA (OpenGL-reimplementation-friendly) version of 
 * the JUNG class EdgePredicateFilter.  It is necessary because 
 * the original JUNG implementation returns Graph<V,E> rather than
 * FDAGraph; the lack of specificity leads to problems in using
 * the new functions that are designed specifically to work with
 * the FDAGraph structure (such as the triangular weights 
 * transformations, which require each edge to have an associated
 * set of reports).
 *
 */
public class VAERS_EdgePredicateFilter extends VAERS_AbstractTransformer {
	Predicate<GeneralEdge> edge_pred;
	
	public VAERS_EdgePredicateFilter(Predicate<GeneralEdge> edge_pred) {
		this.edge_pred = edge_pred;
		
	}
	
	@Override
	public GeneralGraph transform(GeneralGraph g){
		GeneralGraph filtered = super.getFiltered(g);
		transformTarget(g,filtered);
        return filtered;
	}
	
	public void transformTarget(GeneralGraph g, GeneralGraph target) {
		for (GeneralNode v : g.getVertices()) {
            target.addVertex(v);
		}
    	int i = 0;

        for (GeneralEdge e : g.getEdges()) {
            if (edge_pred.evaluate(e)){
                target.addEdge(e, g.getIncidentVertices(e));
            }
            else{
            	i = i + 1;
            	Collection<GeneralNode> nodes = g.getIncidentVertices(e);
            	Iterator<GeneralNode> it2 = nodes.iterator();
            	boolean found = false;
            	while(it2.hasNext()){
            		GeneralNode node = it2.next();
            		System.out.println(node.getID());
            		if (node.getID().startsWith("Fake"))
            			found = true;
            	}
            	if (found == false)
            		System.out.println("No fake id found");
           }

        }
        BasicGraphDataCalculator graphCalc = new BasicGraphDataCalculator(target);
        graphCalc.setAllDisplaysToTrue();
        if (target instanceof FDAGraph) {
	        Set<Object> reportSet = ((FDAGraph)target).getReports();
	        Set<Object> reportSetOrig = g.getReports();
	        reportSetOrig.removeAll(reportSet);
        }
	}
}
