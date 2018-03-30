package com.eng.cber.na.event;

import javax.swing.JMenu;

import com.eng.cber.na.event.mouse.AbstractPopupMouseEvent.SelectionType;

/**
 * This class extends the JMenu class and requires users
 * to indicate whether the JMenu instance requires a vertex
 * to be selected.  This is useful, for instance, in
 * signaling when the JMenu instance should be active.
 *
 */
@SuppressWarnings("serial")
public class PopupMenu extends JMenu{
	SelectionType requiresVertexSelection;
	
	public PopupMenu(String item, SelectionType requiresVertexSelection) {
		super(item);
		this.requiresVertexSelection = requiresVertexSelection;
	}
	
	public SelectionType requiresVertexSelection() {
		return requiresVertexSelection;
	}
}