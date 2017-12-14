package com.eng.cber.na;

/****
 * The command interface -- for the command pattern design.
 *
 */
public abstract interface Command {
	public abstract void execute(String name);
	public abstract Boolean recordable();
	public abstract String getTitle();
	public abstract void setTitle(String title);
	public abstract String getShortDescription();
	public abstract void setShortDescription(String shortDescription);
	public abstract void redo(String name);
}
