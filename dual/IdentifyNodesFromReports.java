package com.eng.cber.na.dual;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.eng.cber.na.graph.GraphLoader;
import com.eng.cber.na.vaers.VAERS_Node;

/**
 * @author G. Zhang gzhang@drc.com
 *
 */
public class IdentifyNodesFromReports {
	GraphLoader gl; 
	public IdentifyNodesFromReports(GraphLoader gl){
		this.gl = gl;
	}
	public Set<Object> GetNodeList(Set<?> reports){
		Set<Object> nodeSet = new HashSet<Object>();
		
		HashSet<VAERS_Node> nodeHash = new HashSet<VAERS_Node>();
		
		Map<Object, Set<VAERS_Node>> reportHash = gl.getOrigReportHash();
		for (Entry<Object, Set<VAERS_Node>> entry:reportHash.entrySet()){
			if(reports.contains(entry.getKey())){
				nodeHash.addAll(entry.getValue());
			}
		}
		Iterator<VAERS_Node> it = nodeHash.iterator();
		
		while(it.hasNext()){
			Object obj = it.next().getObject();
			nodeSet.add(obj);
		}
		return nodeSet;
	}
}
