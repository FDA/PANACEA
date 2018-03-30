package com.eng.cber.na.command.util;

import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JFrame;

public class CommandCheckBox extends JCheckBox implements CommandHolder{
	
	private static final long serialVersionUID = 1L;
	
	protected BaseCommand menuCommand;
	protected JFrame frame;
	protected String name;
	protected String[] nameArray = null ;

	public CommandCheckBox(String name, JFrame frame, ActionListener al){
		super();
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
