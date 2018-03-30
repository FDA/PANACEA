package com.eng.cber.na.command;

import javax.swing.JOptionPane;

import com.eng.cber.na.command.util.BaseCommand;

/****
 * The command pattern design to display a small message about the
 * software, including version number.
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */

public class AboutCommand extends BaseCommand{
	public static String version = "1.0";
	
	public AboutCommand(){
		title = "About PANACEA";
		shortDescription = "Pattern-based and Advanced Network Analyzer for Clinical Evaluation and Assessment (PANACEA) v" + version + "\n\n" +
						   "Food and Drug Administration\nCenter for Biologics Evaluation and Research\nOffice of Biostatistics and Epidemiology";
	}
	
	@Override
	public void execute(String name) {
		JOptionPane.showMessageDialog(null, shortDescription, "About", JOptionPane.INFORMATION_MESSAGE);
	}

	@Override
	public Boolean recordable() {
		return true;
	}
	
	public Class<? extends AboutCommand> getCommand(){
		return this.getClass();
	}

	@Override
	public void redo(String name) {
		execute(name);		
	}
}