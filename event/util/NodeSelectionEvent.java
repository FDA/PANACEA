package com.eng.cber.na.event.util;

import com.eng.cber.na.component.SearchableList;

/**
 * An event specifically for node selection from a SearchableList.
 *
 */
public class NodeSelectionEvent {
	
	private SearchableList list;
	
	public NodeSelectionEvent(SearchableList list) {
		this.list = list;
	}

	public SearchableList getList() {
		return list;
	}
}
