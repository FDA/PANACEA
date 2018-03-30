package com.eng.cber.na.command;

import java.util.Collection;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.command.util.BaseCommand;
import com.eng.cber.na.graph.GeneralEdge;
import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.graph.GeneralNode;
import com.eng.cber.na.vaers.VAERS_Edge;

/****
 * The command pattern design to set the minimum similarity to
 * the ReferenceDocument that is required for nodes to be
 * visible.  The minimum can be set as a similarity value
 * (normalized between 0 and 1) or as the number of elements that 
 * are shared by the report and the ReferenceDocument.
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */
public class SetSimilarityThresholdCommand extends BaseCommand implements ChangeListener{
	public boolean basedOnValue = true;
	private JSlider slider;
	private JOptionPane optionPane;
	private double threshold = 0.0;
	private JLabel simValueLabel ;
	private double maxWeight = 1;
	
	public SetSimilarityThresholdCommand(){
		this(true);
	}
	public SetSimilarityThresholdCommand(boolean basedOnValue){
		this.basedOnValue = basedOnValue;
		title = "Set Similarity Threshold to a Reference Case";
		shortDescription = "Set Similarity Threshold to a Reference Case in a report-network (SYM).";
	}
	
	@Override
	public void execute(String name) {
		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
		if (!nv.getDualState()){
			JOptionPane.showMessageDialog(null,"Not a report network (SYM or VAX).", "Network Type Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		optionPane = new JOptionPane();
		threshold = nv.getGraph().getSimilarityThreshold();
		
		if(name.compareTo("By Common Terms...") == 0 ){
			if(basedOnValue == true){
				new ResetGraphCommand().execute("");
				threshold = 0;
				basedOnValue = false;
			}
		}
		else{
			if(basedOnValue == false){
				new ResetGraphCommand().execute("");
				threshold = 0;
				basedOnValue = true;
			}
		}
			
		
		GeneralGraph graph = nv.getGraph();
		GeneralNode refNode = graph.findNodeByID("ReferenceDocument");
		
		Collection<GeneralEdge> edges = graph.getInEdges(refNode);
		if (edges == null )
			return;
		
		maxWeight = 0;
		
		if(basedOnValue){
			for(GeneralEdge edge:edges){
				if (maxWeight < edge.getWeight())
					maxWeight = edge.getWeight();
			}
			slider = new JSlider(0, 100, (int)((threshold/maxWeight)*100));
			simValueLabel = new JLabel("Similarity Value of: " + String.format("%1.3f", threshold/maxWeight));
		}
		else
		{
			for(GeneralEdge edge:edges){
				int reportSize = ((VAERS_Edge)edge).getReports().size();
				maxWeight = (reportSize > maxWeight)?reportSize:maxWeight;
			}			
			slider = new JSlider(0, (int)maxWeight, (int)(threshold));
			simValueLabel = new JLabel((int)threshold + "/" + (int)maxWeight + " Common PT's");
		}
		
		slider.addChangeListener(this);
		Object[] messages = {slider, "Threshold: ", simValueLabel};
		optionPane.setMessage(messages);
		optionPane.setMessageType(JOptionPane.QUESTION_MESSAGE);
		optionPane.setOptionType(JOptionPane.CLOSED_OPTION);
		JDialog dialog = optionPane.createDialog(nv, "Select Minimum Similarity Threshold");
		dialog.setVisible(true);
	}

	@Override
	public Boolean recordable() {
		return true;
	}

	@Override
	public void redo(String name) {
		execute(name);
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		try {
			NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
			if(basedOnValue)
			{
				threshold = ((double)slider.getValue())/100 * maxWeight;
				optionPane.setInputValue(threshold);
				simValueLabel.setText("Similarity Value of: " + String.format("%1.3f", threshold));
			}
			else{
				threshold = slider.getValue();
				optionPane.setInputValue(slider.getValue());
				simValueLabel.setText(slider.getValue() + "/" + (int)maxWeight + " Common PTs");	
			}
				
			GeneralGraph graph = nv.getGraph();
			GeneralNode refNode = graph.findNodeByID("ReferenceDocument");
			Collection<GeneralEdge> edges = graph.getInEdges(refNode);
			if (edges == null )
				return;
			
			graph.setSimilarityThreshold(threshold);
			boolean flagDisplay = false;
			for(GeneralEdge edge:edges){
				GeneralNode node2 = graph.getOpposite(refNode, edge);
				if(basedOnValue){
					if (edge.getWeight() >= threshold){
						flagDisplay= true;
					}
					else
						flagDisplay= false;
				}
				else{
					if (edge instanceof VAERS_Edge ){
						if(((VAERS_Edge)edge).getReports().size() >= threshold)
							flagDisplay = true;
						else 
							flagDisplay = false;
					}
				}
					
				if (flagDisplay){
					graph.getNodeDisplayTransformer().set(node2, true);
					graph.getEdgeDisplayTransformer().set(edge, true);
					
					// Display edges connected to node2 
					Collection<GeneralEdge> edges2 = graph.getInEdges(node2);
					for(GeneralEdge edge2:edges2){
						GeneralNode node3 = graph.getOpposite(node2, edge2);
						if(graph.getNodeDisplay(node3))
							graph.getEdgeDisplayTransformer().set(edge2, true);
						else
							graph.getEdgeDisplayTransformer().set(edge2, false);
					}
				}
				else{
					// Further hide edges connected to node2 
					Collection<GeneralEdge> edges2 = graph.getInEdges(node2);
					for(GeneralEdge edge2:edges2)
						graph.getEdgeDisplayTransformer().set(edge2, false);
					
					graph.getEdgeDisplayTransformer().set(edge, false);
					graph.getNodeDisplayTransformer().set(node2, false);
					
				}
			}
			
			nv.updateGraphLabels();
		} catch (IllegalArgumentException x) {
			JOptionPane.showMessageDialog(null, x.getMessage(),
					"Input Error", JOptionPane.ERROR_MESSAGE);
		}
		
	}
}
