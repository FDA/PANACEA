package com.eng.cber.na.command.util;

import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenuItem;

import com.eng.cber.na.Command;

public class CommandMenuItem extends JMenuItem implements CommandHolder, Command{
	
	private static final long serialVersionUID = 1L;
	
	protected BaseCommand menuCommand;
	protected JFrame frame;
	protected String name;
	protected String title, shortDescription;
	
	public CommandMenuItem(String name){
		this(name, null, null);
	}
	
	public CommandMenuItem(String name, JFrame frame, ActionListener al){
		super(name);
		title = "Command Menu";
		shortDescription = title;
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
	@Override
	public void execute(String name) {
		
	}
	@Override
	public Boolean recordable() {
		return null;
	}
	@Override
	public String getTitle() {
		return title;
	}
	@Override
	public void setTitle(String title) {
		this.title = title;
	}
	@Override
	public String getShortDescription() {
		return shortDescription;
	}
	@Override
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}
	@Override
	public void redo(String name) {
		execute(name);
	}
}
