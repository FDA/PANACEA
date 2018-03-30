package com.eng.cber.na.renderer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import com.eng.cber.na.renderer.NodeIcon.SETIcon;
import com.eng.cber.na.vaers.VAERS_Node;
import com.eng.cber.na.vaers.VAERS_NodeSet;

/***
 * This class maps a set of nodes (an average line) to a color
 * and it knows how to render the list given an index, 
 * selection state, and so on.
 *
 */
public class AvgListCellRenderer implements ListCellRenderer {

	private Map<VAERS_NodeSet, Color> nodeListPaint;
	
	public AvgListCellRenderer(Map<VAERS_NodeSet, Color> nodeListPaint) {
		this.nodeListPaint = nodeListPaint;
	}
	
	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		VAERS_NodeSet nodeList = (VAERS_NodeSet)value;
		JPanel ret = new JPanel();
		ret.setLayout(new BorderLayout());
		ret.setBackground(isSelected ? Color.LIGHT_GRAY : Color.WHITE);
		StringBuffer buf = new StringBuffer();
		buf.append("  ");
		for(VAERS_Node n : nodeList) {
			buf.append(n.getID());
			if(nodeList.headSet(n).size() != nodeList.size() - 1) {
				buf.append(", ");
			}
		}
		ret.add(new SETIcon(isSelected, nodeListPaint.get(nodeList)), BorderLayout.WEST);
		ret.add(new JLabel(buf.toString()), BorderLayout.CENTER);
		return ret;
	}

}
