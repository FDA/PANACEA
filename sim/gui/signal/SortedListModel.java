package com.eng.cber.na.sim.gui.signal;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/** Used for the lists of symptoms when selecting
 * associated PTs in the By Symptom tab.
 */
public class SortedListModel<E extends Comparable<E>> implements ListModel {

	private List<E> cont = new LinkedList<E>();
	private List<ListDataListener> listeners = new LinkedList<ListDataListener>();
	
	public void add(E e) {
		cont.add(e);
		Collections.sort(cont);
		for(ListDataListener l : listeners) {
			l.contentsChanged(new ListDataEvent(this,ListDataEvent.INTERVAL_ADDED,cont.indexOf(e),cont.indexOf(e)));
		}
	}
	
	public void remove(E e) {
		int ind = cont.indexOf(e);
		cont.remove(e);
		for(ListDataListener l : listeners) {
			l.contentsChanged(new ListDataEvent(this,ListDataEvent.INTERVAL_REMOVED,ind,ind));
		}
	}
	
	public void clear() {
		int ind = cont.size() - 1;
		cont.clear();
		for(ListDataListener l : listeners) {
			l.contentsChanged(new ListDataEvent(this,ListDataEvent.INTERVAL_REMOVED,0,ind));
		}
	}
	
	public List<E> getList() {
		return new LinkedList<E>(cont);
	}
	
	@Override
	public int getSize() {
		return cont.size();
	}

	@Override
	public Object getElementAt(int index) {
		return cont.get(index);
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		listeners.add(l);
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		listeners.remove(l);
	}

}
