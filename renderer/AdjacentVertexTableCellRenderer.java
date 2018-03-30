package com.eng.cber.na.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.eng.cber.na.renderer.NodeIcon.SYMIcon;
import com.eng.cber.na.renderer.NodeIcon.VAXIcon;
import com.eng.cber.na.vaers.VAERS_Node.NodeType;

/**
 * The AdjacentVertexTableCellRenderer knows how to render cells
 * in a table on the basis of some of their components, like
 * whether they are currently selected, expanded, and so on.
 *
 */
public class AdjacentVertexTableCellRenderer implements TableCellRenderer {
	
	private boolean selected;
	
	public AdjacentVertexTableCellRenderer(boolean selected) {
		this.selected = selected;
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if(column == 0) {
			JPanel ret = new JPanel();
			ret.setLayout(new BoxLayout(ret,BoxLayout.LINE_AXIS));
			ret.setBackground(selected ? Color.LIGHT_GRAY : Color.WHITE);
			
			
			if (value.getClass().isEnum()){
				NodeType type = (NodeType)value;
				ret.add(type == NodeType.VAX ? new VAXIcon(selected) : new SYMIcon(selected));
			}
			else{
				ret.add(new VAXIcon(selected) );
			}
			return ret;
		}
		else {
			String str;
			
			if ((column == 2 || column == 3 || column == 4) && value instanceof Double) {
				str = String.format(" %1.4G", (Double)value);
			}
			else if (column == 5 && value instanceof Double) {
				str = String.format("%1.6G", (Double)value);
			}
			else {
				str = " " + value;
			}
			
			JPanel ret = new JPanel();
			ret.setLayout(new BoxLayout(ret,BoxLayout.LINE_AXIS));
			ret.setBackground(selected ? Color.LIGHT_GRAY : Color.WHITE);
			ret.add(new JLabel(str));
			return ret;
		}
	}

}
