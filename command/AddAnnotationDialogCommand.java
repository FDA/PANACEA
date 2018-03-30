package com.eng.cber.na.command;

import javax.swing.JOptionPane;

import com.eng.cber.na.command.util.BaseCommand;

/****
 * The command pattern design to display a small message about 
 * how to use the ALT key and the mouse to add annotations 
 * to the visualization.
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */

public class AddAnnotationDialogCommand extends BaseCommand{
	public AddAnnotationDialogCommand(){
		title = "How to Add Annotations";
		shortDescription = "Opens a window which describes how to use the ALT key and the mouse to add annotations to the visualization.";
	}
	
	@Override
	public void execute(String name) {
		JOptionPane.showMessageDialog(null,
									  "<html><p style=\"width:400px;\">Annotations can be added to the visualization by using the <i>ALT</i> key.<br/><br/>" +
									  "<b>Text Annotations:</b><br/>" +
									  "Hold down the <i>ALT</i> key and click the <i>Left Mouse Button</i> somewhere on the canvas to create a text annotation.  A small window will appear to enter the text that should be displayed at that location.  Text annotations will appear in red.<br/><br/>" +
									  "<b>Rectangular Annoations:</b><br/>" +
									  "Hold down the <i>ALT</i> key and press and hold the <i>Left Mouse Button</i> on the canvas.  Move the mouse to drag a rectangle across part of the canvas.  Release the <i>Left Mouse Button</i> to finalize the annotation.  Rectangular annotations will appear in black.</p></html>",
									  "Adding Annotations",
									  JOptionPane.INFORMATION_MESSAGE);
	}

	@Override
	public Boolean recordable() {
		return false;
	}

	@Override
	public void redo(String name) {
		execute(name);		
	}
}