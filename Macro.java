package com.eng.cber.na;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JOptionPane;

import com.eng.cber.na.command.util.BaseCommand;
import com.eng.cber.na.util.CheckNameConflict;

public class Macro implements Serializable {
	protected LinkedHashMap<String, BaseCommand> history = new LinkedHashMap<String, BaseCommand>();
	protected BaseCommand latestCommand = null;
	int curCommandIndex = -1; 
	protected BaseCommand curCommand = null;
	public BaseCommand getCurCommand() {
		List<String> historyNameSet = new ArrayList(history.keySet());
		curCommand = history.get(historyNameSet.get(curCommandIndex));
		return curCommand;
	}
	public void setCurCommand(BaseCommand curCommand) {
		this.curCommand = curCommand;
	}
	public BaseCommand getLatestCommand() {
		return latestCommand;
	}
	public  int getCurCommandIndex() {
		return curCommandIndex;
	}
	public  void setCurCommandIndex(int curCommandIndex) {
		this.curCommandIndex = curCommandIndex;
	}
	public void setLatestCommand(BaseCommand latestCommand) {
		this.latestCommand = latestCommand;
	}
	protected String latestCommandName = null;
	public String getLatestCommandName() {
		return latestCommandName;
	}
	public void setLatestCommandName(String latestCommandName) {
		this.latestCommandName = latestCommandName;
	}
	public LinkedHashMap<String, BaseCommand> getHistory() {
		return history;
	}
	public void setHistory(LinkedHashMap<String, BaseCommand> history) {
		this.history = history;
	}
	
	public void add(String name, BaseCommand c){
		/** Check if the command in the map already. If yes, add an indicator (_1, _2, _3...) to the command name*/
		
		String newName = name;
		List<String> historyNameSet = new ArrayList(history.keySet());
		newName = new CheckNameConflict(historyNameSet, name).check();
		curCommand = c;
		latestCommandName = newName;
		latestCommand = c;
		history.put(newName, c);
		curCommandIndex = history.size()-1;
	}
	public void remove(String name){
		List<String> historyNameSet = new ArrayList(history.keySet());

		if(historyNameSet.contains(name))
		{
			history.remove(name);
		}
	}
	
	public void run(){
		Set<Entry<String, BaseCommand>> entrySet = history.entrySet();
		for(Entry<String, BaseCommand>entry:entrySet){
			((BaseCommand) entry.getValue()).execute(entry.getKey());
		}
	}
	
	public void Undo(String name){
		if (this.getCurCommandIndex()<0){
			JOptionPane.showMessageDialog(null, "No command in stack!");
			return;
		}
		curCommandIndex = curCommandIndex - 1;
	}
	public void Redo(String name){
		curCommandIndex = curCommandIndex + 1;
		if (curCommandIndex >history.size()-1){
			JOptionPane.showMessageDialog(null, "Latest command applied!");
			curCommandIndex = curCommandIndex - 1;
			return;
		}
		
	}
	public void printHistory(){
		int i = 0;
		System.out.println("Command History: ");
		for(String name:history.keySet()){
			i = i + 1;
			String[] names = name.split("_");
			System.out.println("Command " + i + ": " + names[0]);
		}
	}
	public BaseCommand getCommand(String strCommand){
		return history.get(strCommand);
	}
}
