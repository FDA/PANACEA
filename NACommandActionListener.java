package com.eng.cber.na;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;

import com.eng.cber.na.command.util.BaseCommand;
import com.eng.cber.na.command.util.CommandHolder;


/**
 * Listener for the main GUI.  Uses the command pattern design.
 * 
 * Calls the execute method for the command that is linked to
 * the source of the ActionEvent.  Note that standard Swing
 * components have been replaced by Command* versions, so that
 * they can possess an associated BaseCommand type object.
 *
 */
public class NACommandActionListener implements ActionListener {
	public Macro macro;
	public void setMacro(Macro macro) {
		this.macro = macro;
	}

	public Macro getMacro() {
		return macro;
	}

	public NACommandActionListener(){
		macro =  new Macro();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		CommandHolder obj = (CommandHolder)e.getSource();
		BaseCommand command = obj.getCommand();
		Class cls = obj.getClass(); 
		String name = obj.getName();
		command.execute(name);

		Field[] fields = command.getClass().getFields();
		String parameter = "";
		
		for(int i = 0; i < fields.length; i++ ){
			parameter = parameter + fields[0].getName();
			try {
				if(fields[0].get(command) != null )
					parameter = parameter + ": " + fields[0].get(command).toString() + "\n";
					
			} catch (IllegalArgumentException e1) {
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
			}
		}

		NetworkAnalysisVisualization.NALog("INFO", parameter, "", name);  
		if (command.recordable() ){
			macro.add(name, command);
		}
	}
}
