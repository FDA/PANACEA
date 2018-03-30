package com.eng.cber.na.event.mouse;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.eng.cber.na.command.util.BaseCommand;
import com.eng.cber.na.command.util.CommandMenuItem;
import com.eng.cber.na.event.PopupMenu;
import com.eng.cber.na.event.PopupMenuItem;

/**
 * An abstracted mouse listener which tracks how many vertices & 
 * what kind, and sets up action commands for the 
 * items that are added to it.
 *
 */
public abstract class AbstractPopupMouseEvent implements MouseListener {

	protected JPopupMenu popup;
	protected int modifiers;
	
	public static enum SelectionType {
		NONE,SINGLE_SYM,VERTEX,EDGE, REFERENCE;
	}
	
	protected AbstractPopupMouseEvent(int modifiers) {
		this.modifiers = modifiers;
		popup = new JPopupMenu();
		popup.setLightWeightPopupEnabled(false);
	}
		
	protected void addItem(String item, SelectionType requiresVertexSelection, String[] subItems, ActionListener actionListener, BaseCommand cmd) {
		JMenuItem menuItem = addItemSub(item, requiresVertexSelection, subItems, actionListener, cmd);
		popup.add(menuItem);
	}
	private JMenuItem addItemSub(String item, SelectionType requiresVertexSelection, String[] subItems, ActionListener actionListener, BaseCommand cmd) {
		JMenuItem menuItem;
		if(subItems == null) {
			menuItem = new PopupMenuItem(item, requiresVertexSelection, actionListener);
			((CommandMenuItem)menuItem).setCommand(cmd);
			
		}
		else {
			menuItem = new PopupMenu(item, requiresVertexSelection);
			for(String subItem : subItems) {
				CommandMenuItem menuSub = new CommandMenuItem(subItem, null, actionListener);
				menuSub.setCommand(cmd);
				menuItem.add(menuSub);
			}
		}
		return menuItem;
	}
	protected void addItem(String item, SelectionType requiresVertexSelection, String[] subItems, ActionListener actionListener, BaseCommand cmd, int ks) {
		JMenuItem menuItem = addItemSub(item, requiresVertexSelection, subItems, actionListener, cmd);
		menuItem.setMnemonic(ks);
		popup.add(menuItem);
	}
	
	protected void addSeparator() {
		popup.addSeparator();
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getModifiers() == modifiers) {
			Component comp = (Component)e.getSource();
			Point p = e.getPoint();
			alterPopupMenu(comp);
			popup.show(comp, (int)p.getX(), (int)p.getY());
		}
	}
	
	public abstract void alterPopupMenu(Component comp);
	
	@Override
	public void mouseClicked(MouseEvent e) { }
	
	@Override
	public void mousePressed(MouseEvent e) { }
	
	@Override
	public void mouseEntered(MouseEvent e) { }
	
	@Override
	public void mouseExited(MouseEvent e) { }
	

}