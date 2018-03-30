package com.eng.cber.na.event;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.gl.NetworkGLVisualizationServer;
import com.eng.cber.na.graph.GeneralEdge;
import com.eng.cber.na.graph.GeneralNode;
import com.eng.cber.na.model.AdjacentVertexTreeModel;
import com.eng.cber.na.renderer.HeadTableCellRenderer;
import com.eng.cber.na.vaers.VAERS_Comparator.ComparatorType;
import com.eng.cber.na.vaers.VAERS_Comparator.Direction;

/**
 * A mouse listener for the adjacent vertex table. The adjacent 
 * vertex table lists all of the nodes adjacent to the currently 
 * selected node, as well as their nodal properties. This mouse
 * listener allows the user to sort the table by those properties/
 * by a column in the adjacent vertex table.
 *
 */
public class AdjacentTableSortListener implements MouseListener {

	private int prevIndex;
	private Direction prevDirection;
	private JTree tree;
	
	public AdjacentTableSortListener(JTree tree) {
		this.tree = tree;
		prevIndex = 0;
		prevDirection = Direction.ASCENDING;
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getModifiers() == InputEvent.BUTTON1_MASK) {

		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getModifiers() == InputEvent.BUTTON1_MASK) {
			JTable table = (JTable)e.getSource();
			HeadTableCellRenderer cellRenderer;
			if(table.getSelectedColumn()< 0){
				cellRenderer= (HeadTableCellRenderer)table.getColumnModel().getColumn(0).getCellRenderer();
				cellRenderer.mousePressed(0);
				table.repaint();
				return;
			}
			else
				cellRenderer= (HeadTableCellRenderer)table.getColumnModel().getColumn(table.getSelectedColumn()).getCellRenderer();
			cellRenderer.mousePressed(table.getSelectedColumn());
			int newIndex = table.getSelectedColumn();
			NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
			NetworkGLVisualizationServer<GeneralNode, GeneralEdge> vv = nv.getNetworkGLVisualizationServer();
			if (vv.getPickedVertexState().getPicked().size() == 0 &
					vv.getPickedEdgeState().getPicked().size() == 0){
				cellRenderer = (HeadTableCellRenderer)table.getColumnModel().getColumn(table.getSelectedColumn()).getCellRenderer();
				cellRenderer.mouseReleased(newIndex);
				table.repaint();
				
				return;				
			}
				
			SwingUtilities.invokeLater(new NetworkTreeUpdater(newIndex));
			cellRenderer = (HeadTableCellRenderer)table.getColumnModel().getColumn(table.getSelectedColumn()).getCellRenderer();
			cellRenderer.mouseReleased(newIndex);
			table.repaint();
		}
	}
	
	class NetworkTreeUpdater implements Runnable {
		private int newIndex;
		public NetworkTreeUpdater(int newIndex) {
			this.newIndex = newIndex;
		}
		@Override
		public synchronized void run() {
			if(newIndex == prevIndex) {
				prevDirection = (prevDirection == Direction.ASCENDING) ? Direction.DESCENDING : Direction.ASCENDING;
			}
			else {
				prevDirection = Direction.ASCENDING;
			}
			prevIndex = newIndex;
			AdjacentVertexTreeModel ntm = (AdjacentVertexTreeModel)tree.getModel();
			
			switch(newIndex) {
			case 0:
				ntm.updateCurrent(ComparatorType.TYPE, prevDirection);
				break;
			case 1:
				ntm.updateCurrent(ComparatorType.NAME, prevDirection);
				break;
			case 2:
				ntm.updateCurrent(ComparatorType.BETWEENNESS, prevDirection);
				break;
			case 3:
				ntm.updateCurrent(ComparatorType.CLOSENESS, prevDirection);
				break;
			case 4:
				ntm.updateCurrent(ComparatorType.DEGREE, prevDirection);
				break;
			case 5:
				ntm.updateCurrent(ComparatorType.STRENGTH, prevDirection);
				break;
			case 6:
				ntm.updateCurrent(ComparatorType.REPORT_COUNT, prevDirection);
				break;
			}
			
			if(ntm.getRoot() != null) {
				tree.expandPath(new TreePath(ntm.getPathToRoot((DefaultMutableTreeNode)ntm.getRoot())));
			}
		}		
	}
	
	@Override
	public void mouseClicked(MouseEvent e) { }

	@Override
	public void mouseEntered(MouseEvent e) { }

	@Override
	public void mouseExited(MouseEvent e) {	}

}
