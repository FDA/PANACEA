package com.eng.cber.na.event;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JList;

import com.eng.cber.na.component.SearchableList;
import com.eng.cber.na.vaers.VAERS_Node;

/**
 * An action listener that knows how to swap a set
 * of terms between two independent lists.
 *
 */
public class TermSwapListener implements FocusListener, ActionListener {

	private SearchableList[] lists = new SearchableList[2];
	
	public TermSwapListener(SearchableList list1, SearchableList list2) {
		lists[0] = list1;
		lists[1] = list2;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		int last;
		Collection<VAERS_Node> terms = null;
		
		for(last = 0; last < 2; last++) {
			terms = new ArrayList<VAERS_Node>(lists[last].getSelectedSourceNodes());
			if(terms.size() > 0)
				break;
			if(last == 1)
				return;
		}
		
		for(Object obj : terms) {
			lists[last].removeNode((VAERS_Node)obj);
		}
		lists[last].updateList();
		
		for(Object obj : terms) {
			lists[(last + 1) % 2].addNode((VAERS_Node)obj);
		}
		lists[(last + 1) % 2].updateList();
		
	}

	@Override
	public void focusGained(FocusEvent e) {
		((JList)e.getSource() == lists[0].getList() ? lists[1] : lists[0]).clearSelection();
	}
	
	@Override
	public void focusLost(FocusEvent e) { }
	
}
