package com.eng.cber.na.command;

import java.awt.Dimension;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import com.eng.cber.na.command.util.BaseCommand;

/****
 * The command pattern design to show a window with the list of all
 * available keyboard shortcuts.
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */

public class ViewShortcutsCommand extends BaseCommand{	
	@Override
	public void execute(String name) {
		
		String message = "<html>";
		
		message += "<p style=\"width:250px;\"><b>Selecting multiple nodes:</b><br/>"
				+ "Hold <em>SHIFT</em>&nbsp; key and click on individual nodes<br/>"
				+ "<em>OR</em><br/>"
				+ "Hold <em>SHIFT</em>&nbsp; key, press and hold on empty canvas, and drag to select all nodes in a rectangle.<br/><br/>"
				+ "<b>Adding Annotations:</b><br/>"
				+ "Hold <em>ALT</em>&nbsp;  key and click to place text annotations.<br/>"
				+ "Hold <em>ALT</em>&nbsp;  key, press and hold on empty canvas, and drag to create rectangular annotations.<br/>";
		
		
		
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("Open User Manual", "F1");
		map.put("", "");
		map.put("Create Full PANACEA Network", "CTRL+V");
		map.put("Create Business Objects Network", "CTRL+B");
		map.put("Import General Network", "CTRL+G");
		map.put("Open Project", "CTRL+O");
		map.put("Save Project", "CTRL+S");
		map.put("Close Project", "CTRL+W");
		map.put("Preferences", "CTRL+SHIFT+P");
		map.put(" ", "");
		map.put("Remove Last Annotation", "CTRL+R");
		map.put("Save Network Snapshot", "CTRL+SHIFT+S");
		map.put("Toggle Cluster Coloring", "CTRL+C");
		map.put("Display/Hide Lower Panel", "CTRL+H");
		map.put("  ", "");
		map.put("Select All Nodes", "CTRL+A");
		map.put("Expand Selection", "CTRL+X");
		map.put("Invert Selection", "CTRL+I");
		map.put("Select Nodes From List", "CTRL+L");
		map.put("   ", "");
		map.put("Create Subnetwork From Selection", "CTRL+U");
		map.put("Delete Selection", "Delete");
		map.put("    ", "");
		map.put("Retrieve Similar Cases (Synthesis)", "CTRL+Y");
		map.put("Retrieve Similar Cases (Selection)", "CTRL+N");
		map.put("Identify Clusters (K-Means)", "CTRL+ALT+K");
		map.put("Identify Clusters (Louvain)", "CTRL+ALT+L");
		map.put("Identify Clusters (VOS)", "CTRL+ALT+V");
		map.put("     ", "");
		map.put("Switch Network Type", "CTRL+T");
		map.put("View Properties", "CTRL+P");
		map.put("View Node Metric Plot", "CTRL+M");

		message += "<br/><b>Keyboard Shortcuts</b><br/><table>";
		
		for (Map.Entry<String, String> entry : map.entrySet()) {
			message += "<tr><td style=\"padding-top:0; padding-bottom:0;\">" + entry.getKey() + "</td><td style=\"padding-top:0; padding-bottom:0;\">" + entry.getValue() + "</td></tr>";
		}
		
		message += "</table></html>";
		
		JLabel label = new JLabel(message);
		
		JScrollPane scrollPane = new JScrollPane(label);
		scrollPane.setPreferredSize(new Dimension(350,400));
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.setViewportBorder(BorderFactory.createEmptyBorder(4, 4, 4, 0));
		
		JOptionPane.showMessageDialog(null, scrollPane, "PANACEA Controls", JOptionPane.INFORMATION_MESSAGE);
	}
	
	@Override
	public Boolean recordable() {
		return false;
	}
	
}
