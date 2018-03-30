package com.eng.cber.na.sim.rstruct;

import java.util.List;

/**
 * PiecewiseFunction implements a Function used in the simulation.
 * 
 */
public class PiecewiseFunction implements Function {

	private List<Double> probs;
	private int interval = 200;

	public PiecewiseFunction(List<Double> probs, int interval) {
		this(probs);
		this.interval = interval;
	}
	
	public PiecewiseFunction(List<Double> probs) {
		this.probs = probs;
	}
	
	public void setInterval(int interval) {
		this.interval = interval;
	}
	
	@Override
	public double eval(double x) {
		return probs.get((int)Math.floor(x/interval));
	}

}
