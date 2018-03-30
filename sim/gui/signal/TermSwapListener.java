package com.eng.cber.na.sim.gui.signal;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JList;

/**
 * An action listener that knows how to swap a set
 * of terms between two independent lists.
 *
 */
public class TermSwapListener implements FocusListener, ActionListener {

	private JList[] lists = new JList[2];
	
	public TermSwapListener(JList possib, JList select) {
		lists[0] = possib;
		lists[1] = select;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		int last;
		Object[] terms = null;
		
		for(last = 0; last < 2; last++) {
			terms = lists[last].getSelectedValues();
			if(terms.length > 0)
				break;
			if(last == 1)
				return;
		}
		
		SortedListModel<String> lm = (SortedListModel<String>)lists[last].getModel();
		for(Object obj : terms) {
			lm.remove((String)obj);
		}
		
		lm = (SortedListModel<String>)lists[(last + 1) % 2].getModel();
		for(Object obj : terms) {
			lm.add((String)obj);
		}
		
		// Clear selection to ensure we are not re-using selections that are too large
		lists[last].clearSelection();
	}

	@Override
	public void focusGained(FocusEvent e) {
		((JList)e.getSource() == lists[0] ? lists[1] : lists[0]).clearSelection();
	}
	
	@Override
	public void focusLost(FocusEvent e) { }
	
}
