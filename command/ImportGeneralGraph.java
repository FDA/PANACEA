package com.eng.cber.na.command;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.command.util.BaseCommand;
import com.eng.cber.na.graph.ReadJungGraph;
import com.eng.cber.na.graph.ReadJungGraph.FileType;

/****
 * The command pattern design to import a general graph (i.e. NOT
 * a VAERS data set).  A general graph will still show topology (nodes
 * and edges), but will not have node types (SYM and VAX) or reports.
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */

public class ImportGeneralGraph extends BaseCommand{
	
	public ImportGeneralGraph(){
	}
	@Override
	public void execute(String name) {
		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
		JPanel fileOption = new JPanel();
		fileOption.add(new JLabel("Select graph file type to be imported:"));
		JFrame f = new JFrame();
		Object[] options = new String[]{"Edge List(.txt or .csv)", "Adjacent Matrix (.txt)", "Pajek (.net)"};
		int selectedFileType = JOptionPane.showOptionDialog(f, fileOption, "", JOptionPane.YES_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
		if (selectedFileType != -1 ){
			//Load .txt or Pajek files
			JFileChooser fc = new JFileChooser(nv.getDataDir());
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Graph files (.txt, .csv, .net)","txt", "csv", "net"); 
			fc.setFileFilter(filter);
			String path = "";

			if(JFileChooser.APPROVE_OPTION == fc.showOpenDialog(nv)) {
				path = fc.getSelectedFile().getAbsolutePath();
			}

			if (path.length()>0){
				try {
					String tokens[] = path.substring(path.lastIndexOf("\\"), path.length()).split("\\.");
					nv.cleanNetwork();
					FileType fileType = FileType.valueOf((tokens[tokens.length-1]).toLowerCase());
					ReadJungGraph graphReader = new ReadJungGraph(path, fileType, selectedFileType);
					String graphName = "General Network - " + path.substring(path.lastIndexOf("\\")+1, path.length());
					nv.setGraphLoaderAndCalculateGeneral(graphReader, graphName, false, false);

				} 
				catch (ArrayIndexOutOfBoundsException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(nv, "Unexpected file format.", "File format unexpected", JOptionPane.ERROR_MESSAGE);
				}
				catch (Exception e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(nv, "Error in import.", "Import error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
	@Override
	public Boolean recordable() {
		return true;
	}
}
