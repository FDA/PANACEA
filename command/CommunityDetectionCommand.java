package com.eng.cber.na.command;

import javax.swing.JOptionPane;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.command.util.BaseCommand;
import com.eng.cber.na.communitydetection.KMeansCluster;
import com.eng.cber.na.communitydetection.LouvainClusterer;
import com.eng.cber.na.communitydetection.VOSClusterer;
import com.eng.cber.na.concurrent.NetworkExecutorService;
import com.eng.cber.na.dialog.ProgressDialog;
import com.eng.cber.na.graph.GeneralGraph;

/****
 * The command pattern design to start the community detection
 * (clustering) process by showing the correct dialog window.
 * Three algorithms are supported: K-Means, Louvain, and
 * VOS (Visualization of Similarity).
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */

public class CommunityDetectionCommand extends BaseCommand{
	
	private static final long serialVersionUID = 1L;
	
	private String method;
	
	public CommunityDetectionCommand (String method){
		this.method = method;
	}
	
	@Override
	public void execute(String name) {
		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();

		GeneralGraph gg = nv.getGraph();
		if (method.equals("K-Means")) {
			KMeansCluster kMeans = new KMeansCluster(gg);
			kMeans.addPropertyChangeListener(new ProgressDialog(nv, "K-Means Clustering", "Performing K-Means Clustering...", false, null));
			NetworkExecutorService.submit(kMeans);
		}
		else if (method.equals("Louvain")) {
			LouvainClusterer louvain = new LouvainClusterer(gg);
			louvain.addPropertyChangeListener(new ProgressDialog(nv, "Louvain Clustering", "Performing Louvain Clustering...", false, null));
			NetworkExecutorService.submit(louvain);
		}
		else if (method.equals("VOS")) {
			VOSClusterer vos = new VOSClusterer(gg);
			vos.addPropertyChangeListener(new ProgressDialog(nv, "VOS Clustering", "Performing VOS Clustering...", false, null));
			NetworkExecutorService.submit(vos);
		}
		else {
			NetworkAnalysisVisualization.logger.logp(java.util.logging.Level.INFO,"","","Community detection method " + method + " not defined.");
			JOptionPane.showMessageDialog(null, "Community detection method " + method + " not defined.", "Issue", JOptionPane.ERROR_MESSAGE);
		}
	}
}
