package com.eng.cber.na.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.event.ListDataListener;

public class CommandListModel extends DefaultListModel {

	private List<ListDataListener> listeners = new LinkedList<ListDataListener>();
	private List<String> commandList;
	
	public CommandListModel(int size) {
		commandList = new ArrayList<String>(size);
	}
	
	public CommandListModel(Collection<String> commands) {
		if (commands == null)
			commandList = null; 
		else
			commandList = new ArrayList<String>(commands);
		
		for (int i = 0; i < commands.size(); i++ )
			super.add(i, commandList.get(i));
		
	}
	
	public String[] getCommands() {
		String[] ret = new String[commandList.size()];
		return commandList.toArray(ret);
	}
	
	public int getElementIndex(String command) {
		return commandList.indexOf(command);
	}
	
	@Override
	public Object getElementAt(int index) {
		if(index >=0 && index < commandList.size()) {
			return commandList.get(index);
		}
		return null;
	}

	@Override
	public int getSize() {
		if (commandList == null)
				return 0;
		else
			return commandList.size();
	}
	
	public boolean contains(String command) {
		return commandList.contains(command);
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		if(!listeners.contains(l)) {
			listeners.add(l);
		}
	}
	
	@Override
	public void removeListDataListener(ListDataListener l) {
		listeners.remove(l);
	}
	@Override
	public void add(int index, Object element) {
		commandList.add((String)element);
		super.add(index, element);
	}
	public void addAll(Object elements[]) {
	    Collection c = Arrays.asList(elements);
	    commandList.addAll(c);
	}
	@Override
	public boolean removeElement(Object element) {
	    super.removeElement(element);
	    boolean removed = commandList.remove(element);
	    return removed;
	}
	@Override
	public void removeAllElements() {
		super.removeAllElements();
	    commandList.clear();
	}
}
