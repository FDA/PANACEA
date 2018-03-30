package com.eng.cber.na.renderer;

import java.awt.Component;
import java.util.HashMap;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JList;

/**
 * A class that knows how to render tooltips on a list's cell 
 * on the basis of whether an item is selected, whether the 
 * cell has focus, and so on.
 *
 */
@SuppressWarnings("serial")
public class ListCellToolTipRenderer extends DefaultListCellRenderer {
	private HashMap<String, String> tooltips;
	
	public ListCellToolTipRenderer(HashMap<String, String> tooltips) {
		this.tooltips = tooltips;
	}
	
	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		JComponent comp = (JComponent)super.getListCellRendererComponent(list,
				value, index, isSelected, cellHasFocus);
		
		if (index > -1 && null != value && null != tooltips) {
			String strSelection = (String)list.getSelectedValue();
			if (tooltips.containsKey(strSelection)){
				list.setToolTipText(tooltips.get(strSelection.trim()));
			}
		}
		
		return comp;
	}
	
}
