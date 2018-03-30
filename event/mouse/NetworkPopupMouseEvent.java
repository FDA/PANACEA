package com.eng.cber.na.event.mouse;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JTree;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.command.DeleteChildrenNetworkCommand;
import com.eng.cber.na.command.DeleteNetworkCommand;
import com.eng.cber.na.command.SwitchNetwork;
import com.eng.cber.na.command.ViewPropertiesCommand;

/**
 * A popup mouse event for the network tree panel that allows
 * users to select which network is currently showing.  Allows
 * users to view the properties of the selected network and
 * to delete the selected network.
 *
 */
public class NetworkPopupMouseEvent extends AbstractPopupMouseEvent  {

	public NetworkPopupMouseEvent(ActionListener actionListener, String pathETHER) {
		super(MouseEvent.BUTTON3_MASK);
		addItem("View Properties",SelectionType.NONE,null,actionListener,new ViewPropertiesCommand());
		addItem("Switch Network Type...", SelectionType.NONE,null,actionListener,new SwitchNetwork());
		addItem("Delete Network",SelectionType.NONE,null,actionListener,new DeleteNetworkCommand());
		addItem("Delete Children Networks",SelectionType.NONE,null,actionListener,new DeleteChildrenNetworkCommand());
	}

	@Override
	public void alterPopupMenu(Component comp) {
		boolean enabled;
		if(NetworkAnalysisVisualization.getInstance().getGraph() != null && NetworkAnalysisVisualization.getInstance().getGraph().getVertexCount()>0)
		{
			enabled = true;
		}
		else
		{
			enabled = false;
		}
		for (java.awt.Component c : popup.getComponents()) {
			c.setEnabled(enabled);
		}			
	}	
}
