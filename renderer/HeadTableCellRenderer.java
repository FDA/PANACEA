package com.eng.cber.na.renderer;

import java.awt.Component;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

/**
 * A class that knows how to render header cells in a table
 * on the basis of whether an item is selected, whether the 
 * table is expanded, and so on.  It also has tooltips
 * and knows which column to sort by.
 *
 */
public class HeadTableCellRenderer implements TableCellRenderer {
	
	private boolean state;
	private int columnIndex;
	private HashMap<String, String> tooltips;
	
	/**
	 * Constructor for no tooltips.
	 */
	public HeadTableCellRenderer() {
		state = true;
		tooltips = new HashMap<String, String>();
	}
	/**
	 * Constructor with tooltips.
	 * @param tooltips
	 */
	public HeadTableCellRenderer(HashMap<String, String> tooltips) {
		state = true;
		this.tooltips = tooltips;
	}
	
	public void mousePressed(int columnIndex) {
		state = false;
		this.columnIndex = columnIndex;
	}
	
	public void mouseReleased(int columnIndex) {
		state = true;
		this.columnIndex = columnIndex;
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		String colLabel = (String)value;
		
		JLabel lb = new JLabel(colLabel);
		if (!table.isEnabled())
			lb.setForeground(UIManager.getColor("Label.disabledForeground"));
		JPanel lb_p = new JPanel();
		lb_p.setLayout(new BoxLayout(lb_p,BoxLayout.LINE_AXIS));
		lb_p.setBackground(UIManager.getColor("Label.background"));
		if(!state && columnIndex == column) {
			lb_p.setBorder(BorderFactory.createLoweredBevelBorder());
		}
		else {
			lb_p.setBorder(BorderFactory.createEmptyBorder());
		}
		lb_p.add(lb);

		lb_p.setToolTipText(tooltips.get(colLabel.trim()));
		return lb_p;
	}

}
