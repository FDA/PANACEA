package com.eng.cber.na.command.util;

import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JFrame;

public class CommandComboBox extends JComboBox implements CommandHolder{
	
	private static final long serialVersionUID = 1L;
	
	protected BaseCommand menuCommand;
	protected JFrame frame;
	protected String name;
	protected String[] nameArray = null ;

	public CommandComboBox(String[] nameArray, String name, JFrame frame, ActionListener al){
		super(nameArray);
		this.frame = frame;
		this.nameArray = nameArray;
		this.name = name;
		this.setSelectedIndex(0);
		this.addActionListener(al);
	}
	@Override
	public void setCommand(BaseCommand cmd) {
		menuCommand = cmd;
	}
	public void setIndex(int index){
		this.setSelectedIndex(index);
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
