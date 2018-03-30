package com.eng.cber.na.sim.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.eng.cber.na.sim.NetworkSimulatorGUI;
import com.eng.cber.na.sim.ParameterFileLoader;
import com.eng.cber.na.sim.ParameterFileSaver;
import com.eng.cber.na.util.json.JSONException;

/**
 * Listener for the (few) items in the menu of the
 * simulator GUI.  Mostly handles saving and
 * loading of JSON parameter files.
 */
public class MenuActionListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		NetworkSimulatorGUI main = NetworkSimulatorGUI.getInstance();
		
		if (e.getActionCommand().equals("Save Current Parameters to File")) {
			File f;
			JFileChooser fc = new JFileChooser(main.getOutputPath());
			fc.setAcceptAllFileFilterUsed(true);
			fc.setFileFilter(new FileNameExtensionFilter("JSON Parameter Files (*.json)", "json"));
			
			if (fc.showSaveDialog(main) == JFileChooser.APPROVE_OPTION) {
				f = fc.getSelectedFile();
				
				if (!f.getAbsolutePath().toLowerCase().endsWith(".json") && !f.getAbsolutePath().toLowerCase().endsWith(".txt")) {
					f = new File(f+".json");
				}
			
				// Create new file or confirm overwrite
				if (f.exists()) {
					int confirmOverwrite = JOptionPane.showConfirmDialog(main,
																		 "The file \"" + f.getName() + "\" already exists.  Are you sure you want to overwrite it?",
																		 "Confirm File Overwrite",
																		 JOptionPane.YES_NO_CANCEL_OPTION);
					if (confirmOverwrite == JOptionPane.NO_OPTION || confirmOverwrite == JOptionPane.CANCEL_OPTION) {
						return;
					}
				}
				else {
					try {
						f.createNewFile();
					} catch (IOException ioe) {
						JOptionPane.showMessageDialog(main, "Cannot create file \"" + f.getAbsolutePath() + "\"", "ERROR", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				
				// Actually write the new file.
				if (f.canWrite()) {
					ParameterFileSaver fileSaver = new ParameterFileSaver(f.getAbsolutePath());
					fileSaver.saveParamsToFile();
				}
				else {
					JOptionPane.showMessageDialog(main,  "Cannot write to file \"" + f.getAbsolutePath() + "\"", "ERROR", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			else {
				return;
			}
			


		}
		else if (e.getActionCommand().equals("Load Parameters from File")) {
			File f;
			JFileChooser fc = new JFileChooser(main.getOutputPath());
			fc.setAcceptAllFileFilterUsed(true);
			fc.setFileFilter(new FileNameExtensionFilter("JSON Parameter Files (*.json)", "json"));
			
			if (fc.showOpenDialog(main) == JFileChooser.APPROVE_OPTION) {
				f = fc.getSelectedFile();
				
				if (f.canRead()) {
					try {
						ParameterFileLoader fileLoader = new ParameterFileLoader(f);
						main.setParameterListing(fileLoader);
						main.setMode(NetworkSimulatorGUI.Mode.STORED_MODE);
					}
					catch (FileNotFoundException fnfe) {
						JOptionPane.showMessageDialog(main,  "Cannot find file \"" + f.getAbsolutePath() + "\"", "ERROR", JOptionPane.ERROR_MESSAGE);
						return;
					}
					catch (IOException ioe) {
						JOptionPane.showMessageDialog(main,  "Error reading file \"" + f.getAbsolutePath() + "\"", "ERROR", JOptionPane.ERROR_MESSAGE);
						return;
					}
					catch (JSONException jse) {
						JOptionPane.showMessageDialog(main, "Error parsing JSON file.  Are you sure this file is the right format?\n\n" + jse.getLocalizedMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				else {
					JOptionPane.showMessageDialog(main,  "Cannot read file \"" + f.getAbsolutePath() + "\"", "ERROR", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}	
		}
		else if (e.getActionCommand().equals("Return to Live Mode")) {
			main.setMode(NetworkSimulatorGUI.Mode.LIVE_MODE);
		}
	}

	
}
