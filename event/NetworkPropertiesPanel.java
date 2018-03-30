package com.eng.cber.na.event;

import javax.swing.JPanel;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.graph.FDAGraph;
import com.eng.cber.na.graph.GeneralGraph;

/**
 * A JPanel that provides a set of network properties to the user,
 * including the path of the data that was used to create the
 * network, the process by which the network was created,
 * and some network-level metrics.
 *
 */
@SuppressWarnings("serial")
public class NetworkPropertiesPanel extends JPanel {
	private NetworkAnalysisVisualization nv;
	
	public NetworkPropertiesPanel(NetworkAnalysisVisualization nv) {
		this.nv = nv;
		initComponents();
	}
	
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();

        jLabel1.setText(getIntroText());

        jTextPane1.setEditable(false);
        jTextPane1.setText(getDescriptiveText(nv.getGraph()));
        jTextPane1.setCaretPosition(0);

        jScrollPane1.setViewportView(jTextPane1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }

    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextPane jTextPane1;
    
    
	private String getIntroText() {
		return "The currently visible network has the following properties:";
	}
    
	private String getDescriptiveText(GeneralGraph graph) {
		String retStr = "";
		
		
		retStr = "NETWORK CHARACTERISTICS:\n" + getNetworkDesc(graph) +
				"\n\n" +
				"NETWORK LINEAGE:\n" + getNetworkSteps(graph) +
				"\n\n" +
				"CLUSTER METRICS: \n " + getClusterDesc(graph) + 
				"\n\n";

		if (nv.getUnderlyingData() != null )
			if(nv.getUnderlyingData().getPtFilePath() != null ){
				retStr = retStr + 
						"DATA PATHS:\nPT Data: " + 
						nv.getUnderlyingData().getPtFilePath() + 
						"\nVAX Data: " + nv.getUnderlyingData().getVaxFilePath();
			}

		if (nv.getGraphReader() != null )
			retStr = retStr + "DATA PATHS: " + nv.getGraphReader().fileName;
			
		return retStr;
	}
	
	private String getClusterDesc(GeneralGraph graph){
		String desc = "";
		double[] conductance  = graph.getConductance();
		if (conductance!= null){
			desc = desc + "Conductance: [";
			for (int i = 0; i < conductance.length; i++){
				desc = desc + String.format("%.2f", conductance[i]) + " ";
			}
			desc = desc + "]\n";
		}
		double[] internalDensity = graph.getInternalDensity();
		if (internalDensity != null){
			desc = desc + "Internal Density: [";
			for (int i = 0; i < internalDensity.length; i++){
				desc = desc + String.format("%.2f", internalDensity[i]) + " ";
			}
			desc = desc + "]\n";
		}
		double[] expansion = graph.getExpansion();
		if (expansion != null){
			desc = desc + "Expansion: [";
			for (int i = 0; i < expansion.length; i++){
				desc = desc + String.format("%.2f", expansion[i]) + " ";
			}
			desc = desc + "]\n";
		}
		double[] cutRatio = graph.getCutRatio();
		if (cutRatio != null){
			desc = desc + "Cut Ratio: [";
			for (int i = 0; i < cutRatio.length; i++){
				desc = desc + String.format("%.2f", cutRatio[i]) + " ";
			}
			desc = desc + "]\n";
		}
		
		double[] nCutRatio = graph.getNormalizedCut();
		if (nCutRatio != null){
			desc = desc + "Normalized Cut Ratio: [";
			for (int i = 0; i < nCutRatio.length; i++){
				desc = desc + String.format("%.2f", nCutRatio[i]) + " ";
			}
			desc = desc + "]\n";
		}
		
		return desc;
	}
	
	private String getNetworkSteps(GeneralGraph graph) {
		String netStrA = graph.getName() + " ";
		String netStrB = graph.getLineage();
		netStrB = netStrB.replaceAll(", ", "\n");
		
		return netStrA + "\n" + netStrB;
	}
	
	private String getNetworkDesc(GeneralGraph graph) {
		String closeBetweenString;
		if (!graph.areBetweenCloseCalculated()) {
			closeBetweenString = "Closeness: Not Calculated.\n" +
								 "Betweenness: Not Calculated.\n";
		}
		else {
			closeBetweenString = "Closeness: ["+ String.format("%1.2f", graph.getMinCloseness()) +", " + String.format("%1.2f", graph.getMaxCloseness()) + "]\n" +
								 "Betweenness: ["+ String.format("%1.2f", graph.getMinBetweenness()) +", " + String.format("%1.2f", graph.getMaxBetweenness()) + "]\n";
		}
		String notesStr;
		if (!(graph instanceof FDAGraph)){
			notesStr = "Nodes: " + graph.getDisplayedNodeCount() + " displayed out of " + graph.getVertexCount() + " total\n" +
					"Edges: " + graph.getDisplayedEdgeCount() + " displayed out of " + graph.getEdgeCount() + " total\n" +
					"Density: " + String.format("%1.4f", graph.getDensity()) + "\n" +
					"Components: " + graph.getComponentCount() + "\n" +
					"\n" +
					"Edge Weight: ["+ String.format("%1.2f", graph.getMinWeight()) +", " + String.format("%1.2f", graph.getMaxWeight()) + "]\n" +
					"Degree: ["+ String.valueOf(graph.getMinDegree()) +", " + String.valueOf(graph.getMaxDegree()) + "]\n" +
					closeBetweenString +
					"Strength: ["+ String.format("%1.1f", graph.getMinStrength()) +", " + String.format("%1.1f", graph.getMaxStrength()) + "]";
			
		}
		else{
			String strReport = "Reports: ";
			if (graph.isDual())
				strReport = "Elements: ";

			notesStr = strReport + graph.getReportCount() + " participating out of " + graph.getAllReportsFromNodes().size() + " total\n" +
					"Nodes: " + graph.getDisplayedNodeCount() + " displayed out of " + graph.getVertexCount() + " total\n" +
					"Edges: " + graph.getDisplayedEdgeCount() + " displayed out of " + graph.getEdgeCount() + " total\n" +
					"Density: " + String.format("%1.4f", graph.getDensity()) + "\n" +
					"Components: " + graph.getComponentCount() + "\n" +
					"\n" +
					"Edge Weight: ["+ String.format("%1.2f", graph.getMinWeight()) +", " + String.format("%1.2f", graph.getMaxWeight()) + "]\n" +
					"Degree: ["+ String.format("%1d", graph.getMinDegree()) +", " + String.format("%1d", graph.getMaxDegree()) + "]\n" +
					closeBetweenString +
					"Strength: ["+ String.format("%1.1f", graph.getMinStrength()) +", " + String.format("%1.1f", graph.getMaxStrength()) + "]";
			
		}
		
		return notesStr;
	}
}

