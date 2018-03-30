package com.eng.cber.na.command;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.command.util.BaseCommand;
import com.eng.cber.na.graph.FDAGraph;
import com.eng.cber.na.sim.comparison.MatrixFileReader;
import com.eng.cber.na.sim.comparison.RealVsSimComparisonDialog;

/****
 * The command pattern design to run a comparison between simulated
 * networks (as output by the network simulator) and the current
 * network in PANACEA.
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */

public class CompareRealVsSimCommand extends BaseCommand{
	@Override
	public void execute(String name) {
		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
		
		if (nv.getGraph().getDual() > 0) {
			JOptionPane.showMessageDialog(nv, "This command is only available for Element Networks.");
			return;
		}
		if (!(nv.getGraph() instanceof FDAGraph)) {
			JOptionPane.showMessageDialog(nv, "Cannot do this with a general network.");
			return;
		}
		
		JFileChooser fileChooser = new JFileChooser(NetworkAnalysisVisualization.getSimulatorDir());
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Text File (*.txt)","txt");;
		
		fileChooser.setFileFilter(filter);
		
		if (JFileChooser.APPROVE_OPTION == fileChooser.showDialog(nv, "Open")) {
			String userChosenFilePath = fileChooser.getSelectedFile().getAbsolutePath().replace("\\", "/");
			if (userChosenFilePath.contains("/") && !userChosenFilePath.endsWith("/")) {
				String userChosenDirectory = userChosenFilePath.substring(0, userChosenFilePath.lastIndexOf("/")+1);
				String userChosenFileName = userChosenFilePath.substring(userChosenFilePath.lastIndexOf("/")+1, userChosenFilePath.length());
				
				Set<String> matrixFileNames = new HashSet<String>();
				
				if (userChosenFileName.matches(".+[0-9]+\\.txt$")) {
					String nameBeforeDigits = userChosenFileName.substring(0, userChosenFileName.length()-4);
					while (nameBeforeDigits.matches(".+[0-9]$"))
						nameBeforeDigits = nameBeforeDigits.substring(0, nameBeforeDigits.length()-1);
					
					final String startOfFileName = nameBeforeDigits;
					
					File directory = new File(userChosenDirectory);
					File[] matrixFiles = directory.listFiles(new FilenameFilter() {
						@Override
						public boolean accept(File dir, String name) {
							if (name.matches(startOfFileName + "[0-9]+\\.txt$"))
								return true;
							else
								return false;
						}
					});
					
					
					for (File matrixFile : matrixFiles) {
						matrixFileNames.add(matrixFile.getAbsolutePath());
					}
				}
				else {
					System.out.println("Warning: Unable to search for additional similar files because selected file name is not numbered.");
					matrixFileNames.add(userChosenFilePath);
				}
				
				MatrixFileReader fileReader = new MatrixFileReader();
				fileReader.readFiles(matrixFileNames);
				
				if (fileReader.hasMatrixInfo()) {
					RealVsSimComparisonDialog rvscd = new RealVsSimComparisonDialog(nv, fileReader);
				}
			}
			else {
				JOptionPane.showMessageDialog(nv, "File name doesn't appear correct.  Please make sure that you have selected one of the MATRIX output files from the network simulator.");
				return;
			}
		}
	}

	@Override
	public Boolean recordable() {
		return false;
	}
}
