package com.eng.cber.na.command.util;

import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenuItem;

public class CommandRadioButtonMenu extends JMenuItem implements CommandHolder{
	
	private static final long serialVersionUID = 1L;
	
	protected BaseCommand menuCommand;
	protected JFrame frame;
	protected String name;

	public CommandRadioButtonMenu(String name, JFrame frame, ActionListener al){
		super(name);
		this.frame = frame;
		this.name = name;
		this.addActionListener(al);
	}
	@Override
	public void setCommand(BaseCommand cmd) {
		menuCommand = cmd;
	}

	@Override
	public BaseCommand getCommand() {
		return menuCommand;
	}
	@Override
	public String getName() {
		return name;
	}
}
