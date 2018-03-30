package com.eng.cber.na.sim.rstruct;

import java.util.List;

/**
 * The Matrix interface defines a matrix used in the simulation.
 * 
 */
public interface Matrix extends WriteableObject {
	public int getVaxCount();
	public int getPtCount();
	public int addNewPT(int val);
	public int addNewVX(int val);
	public void increment(int m, int n);
	public void increment(int m, int n, int val);
	public List<Integer> getVXIndicies();
	public List<Integer> getPTIndicies();
	public List<Integer> getDiagList();
	public List<Integer> getDiagList(List<Integer> indicies);
	public void setDiag(int val);
	public void clear();
	public List<Integer> getRow(int m);
}
