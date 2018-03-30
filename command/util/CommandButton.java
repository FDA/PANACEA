package com.eng.cber.na.command.util;

import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

public class CommandButton extends JButton implements CommandHolder{
	
	private static final long serialVersionUID = 1L;
	
	protected BaseCommand buttonCommand;
	protected JFrame frame;
	protected String name;

	public CommandButton(String name, JFrame frame, ActionListener al){
		super(name);
		this.frame = frame;
		this.name = name;
		this.addActionListener(al);
	}
	@Override
	public void setCommand(BaseCommand cmd) {
		buttonCommand = cmd;
	}
	@Override
	public BaseCommand getCommand() {
		return buttonCommand;
	}
	@Override
	public String getName() {
		return name;
	}
}
