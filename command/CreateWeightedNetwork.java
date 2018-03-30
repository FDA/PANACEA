package com.eng.cber.na.command;

import java.awt.Cursor;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.NetworkAnalysisVisualization.WeightingScheme;
import com.eng.cber.na.command.util.BaseCommand;
import com.eng.cber.na.weighting.Weighting;

/****
 * The command pattern design to create a network using different
 * weighting schemes.  The available options are listed in the enum
 * NetworkAnalysisVisualization.WeightingScheme.
 * 
 * For all commands, the following fields and methods must be defined:
 * Private String title;
 * Private String shortDescription;
 */
public class CreateWeightedNetwork extends BaseCommand{
	public Integer weightingscheme = 0;
	private NetworkAnalysisVisualization.WeightingScheme ws ;

	public CreateWeightedNetwork (){
		title = "Create weighted networks";
		this.shortDescription = "<html><p>This command is used to create weighted networks for report networks. <br/>" + 
							"Input one of the following weighting schemes (0~6) <br/>"+ 
							"<li>0: LinSimilarity</li><br/>"+ 
							"<li>1: Singleton</li><br/>" + 
							"<li>2: Dyads</li><br/>" +
		  					"<li>3: Triplets</li><br/>" +
							"<li>4: Quadruplets</li><br/>" +
							"<li>5: Quintuplets</li><br/>" +
							"<li>6: Sextuplets</li><br/></p></html>";
							
	};
	
	public CreateWeightedNetwork (NetworkAnalysisVisualization.WeightingScheme ws){
		this.ws = ws;
	}
	
	@Override
	public void execute(String name) {
		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
		if (!nv.getGraph().isDual()){
			nv.logger.logp(java.util.logging.Level.WARNING, "CreateWeightedNetwork", "execute", 
					"Weighted network generation applied to report networks only");
			
			JOptionPane.showMessageDialog(null, "Weighted network generation applied to report networks only");
			return;
		}
		if(ws == null ){
			try{
				if(weightingscheme >= 0 & weightingscheme <= 6)
					ws = WeightingScheme.values()[weightingscheme];
				else
				{
					nv.logger.logp(java.util.logging.Level.WARNING, "CreateWeightedNetwork", "execute", 
							"weighting scheme " + weightingscheme + " does not exist!");
					JOptionPane.showMessageDialog(null, "weighting scheme " + weightingscheme + " does not exist!");
				}
			}
			catch(IllegalArgumentException ex)
			{
				nv.logger.logp(java.util.logging.Level.WARNING, "CreateWeightedNetwork", "execute", 
						"weighting scheme " + weightingscheme + " does not exist!");
				JOptionPane.showMessageDialog(null, "weighting scheme " + weightingscheme + " does not exist!");
				return;
			}
		}
		nv.setWeightingScheme(ws);
		nv.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		SwingUtilities.invokeLater(new Weighting(nv.getGraph(), ws, "SYM"));
	}

	@Override
	public Boolean recordable() {
		return true;
	}
}
