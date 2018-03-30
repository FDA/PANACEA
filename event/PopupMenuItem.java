package com.eng.cber.na.event;

import java.awt.event.ActionListener;

import com.eng.cber.na.command.util.CommandMenuItem;
import com.eng.cber.na.event.mouse.AbstractPopupMouseEvent.SelectionType;

/**
 * This class extends the JMenuItem class and requires users
 * to indicate whether the JMenuItem instance requires a vertex
 * to be selected.  This is useful, for instance, in
 * signaling when the JMenuItem instance should be active.
 *
 */
@SuppressWarnings("serial")
public class PopupMenuItem extends CommandMenuItem {
	SelectionType requiresVertexSelection;
	
	public PopupMenuItem(String item, SelectionType requiresVertexSelection, ActionListener actionListener) {
		super(item, null, actionListener);
		this.requiresVertexSelection = requiresVertexSelection;
	}
	
	public SelectionType requiresVertexSelection() {
		return requiresVertexSelection;
	}
}
