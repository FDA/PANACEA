package com.eng.cber.na.layout;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.eng.cber.na.graph.FDAGraph;
import com.eng.cber.na.graph.GeneralEdge;
import com.eng.cber.na.graph.GeneralNode;

import edu.uci.ics.jung.graph.Graph;

/** 
* The IslandLayout is just like an ISOMLayout (self-organizing map layout), but the y-axis
* is set to be the height of the island to which each node belongs.  This is a new
* layout introduced in PANACEA.
* 
* The ISOMLayout is used as the basis rather force directed because the 
* ISOMLayout tends to spread the network through the horizontal visual space
* better than the force directed layout, making the resulting visualization
* easier to read.  Both the ISOMLayout and the force directed layout approximate
* a layout in which the peripherally connected nodes appear at the outskirts of the
* visualization and the highly connected nodes appear near the nodes they are highly
* connected to.
*/
public class IslandLayout extends ISOMLayout<GeneralNode, GeneralEdge> implements Serializable {

	public IslandLayout(Layout<GeneralNode, GeneralEdge> layout) {
		super(layout);
	}
	
	public IslandLayout(Graph<GeneralNode, GeneralEdge> g) {
		super(g);
	}	
	
	@Override
	public void layout() {
		super.layout();
		setYToIslandHeight();
	}
		
	private void setYToIslandHeight() {
		FDAGraph g = (FDAGraph)this.getGraph();
	
		Double maxYVal = Double.MIN_VALUE;
		Double minYVal = Double.MAX_VALUE;
		Double padding = 100.0;
		//find range for heights
		for (GeneralNode n : this.getGraph().getVertices() ) {
			Double yCoordinate = new Double(g.getIslandHeight(n));
			if (yCoordinate != null) {				
				if (yCoordinate < minYVal)
					minYVal = yCoordinate;
				if (yCoordinate > maxYVal)
					maxYVal = yCoordinate;
			}
		}
		
		//Now scale and center appropriately, setting y values and repainting
		Double multiplyYValuesBy = 1.;
		if (maxYVal != minYVal) {
			multiplyYValuesBy = (this.getSize().getHeight() - padding)/(maxYVal - minYVal);
		}
		
		if(Double.compare(Double.POSITIVE_INFINITY, multiplyYValuesBy) == 0)
			multiplyYValuesBy = 1.0;
		
		for (GeneralNode n : this.getGraph().getVertices() ) {
			Point2D pt = this.transform(n);		
			
			// Spread the points through the entire vertical space
			Double yCoord = padding;
			Double h = new Double(g.getIslandHeight(n));
			if (h != null) { // if n is not a member of any island
				yCoord = (h - minYVal) * multiplyYValuesBy + padding/2;
				// Flip the points so that "big" islands are at the top of the screen
				yCoord = this.getSize().getHeight() - yCoord;		
			} 
			// Update the values
			pt.setLocation(pt.getX(), yCoord);
			this.setLocation(n, pt);		
		}

	}

	@Override
	public LayoutType getType() {	
		return LayoutType.ISLAND_HEIGHT;
	}
	private void writeObject(ObjectOutputStream stream) throws IOException{
		stream.defaultWriteObject();
		System.out.println("Write ISO Layout");
	}
	
}
