package com.eng.cber.na.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

import com.eng.cber.na.graph.GeneralEdge;
import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.graph.GeneralNode;
import com.eng.cber.na.model.AdjacentVertexTableModel;

/**
 * The AdjacentVertexTreeCellRenderer knows how to render cells
 * in a tree on the basis of some of their components, like
 * whether they are currently selected, expanded, and so on.
 *
 */
public class AdjacentVertexTreeCellRenderer implements TreeCellRenderer {

	private GeneralGraph g;
	private int[] columnWidths;
	
	public AdjacentVertexTreeCellRenderer(GeneralGraph g, int[] columnWidths) {
		this.g = g;
		this.columnWidths = columnWidths;
	}
	
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		Object obj = ((DefaultMutableTreeNode) value).getUserObject();
		if(obj instanceof GeneralEdge) {
			return new JPanel();
		}
		if(obj instanceof GeneralNode) {
			GeneralNode node = (GeneralNode)obj;
			TableModel tableModel = new AdjacentVertexTableModel(g,node);
			TableCellRenderer tblCellRenderer = new AdjacentVertexTableCellRenderer(selected);
			
			DefaultTableColumnModel columnModel = new DefaultTableColumnModel();
			for(int i = 0; i < 7; i++)
				columnModel.addColumn(new TableColumn(i,columnWidths[i],tblCellRenderer,null));
			
			JTable table = new JTable(tableModel,columnModel);
			
			JPanel tablep = new JPanel();
			tablep.setLayout(new BoxLayout(tablep,BoxLayout.LINE_AXIS));
			tablep.add(table);
			return tablep;
		}
		else {
			JPanel lbp = new JPanel();
			lbp.setLayout(new BoxLayout(lbp,BoxLayout.LINE_AXIS));
			lbp.add(new JLabel(obj.toString()));
			lbp.setBackground(selected ? Color.LIGHT_GRAY : Color.WHITE);
			return lbp;
		}
	}

}
