package com.eng.cber.na.command.util;


public interface CommandHolder {
	public void setCommand(BaseCommand cmd);
	public BaseCommand getCommand();
	public String getName();
}
