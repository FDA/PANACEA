package com.eng.cber.na.sim.rstruct;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * The weight (degree) matrix for the simulation, describing
 * the number of connections for each PT and vaccine in the
 * simulation.  There is only one matrix per simulation, and
 * PTs and vaccines are intermixed along the axes.  Use the
 * ptIndex and vxIndex Lists to access only one type.<br/><br/>
 * 
 * The diagonal elements keep a running total of the weight for
 * each PT or Vax, which is used to determine the probability
 * of it being chosen to go into a report.  The diagonals are
 * zeroed out at the end of the simulation.<br/><br/>
 * 
 * The diagonal elements are the total number of connections
 * that particular Vax/PT has with all other terms.  The non-
 * diagonal elements are the number of times that those two
 * terms have appeared in a report together.<br/><br/>
 * 
 * This class is also a WriteableObject and is called to
 * create the MATRIX output file.
 */
public class SimulationMatrix implements Matrix {

	private List<List<Integer>> matrix;
	private List<Integer> ptIndex, vxIndex;
	
	public SimulationMatrix(int ptCount, int vxCount) {
		ptIndex = new LinkedList<Integer>();
		vxIndex = new LinkedList<Integer>();
		matrix = new ArrayList<List<Integer>>(ptCount + vxCount);
		for(int i = 0; i < ptCount + vxCount; i++) {
			List<Integer> row = new ArrayList<Integer>(ptCount + vxCount);
			for(int j = 0; j < ptCount + vxCount; j++) {
				if (i == j) {
					row.add(vxCount + ptCount - 1);
				}
				else {
					row.add(1);
				}
			}
			if(i < vxCount) {
				vxIndex.add(i);
			}
			else {
				ptIndex.add(i);
			}
			matrix.add(row);
		}
	}

	@Override
	public int addNewPT(int val) {
		int newIndex = addNew(val);
		ptIndex.add(newIndex);
		return newIndex;
	}
	
	@Override
	public int addNewVX(int val) {
		int newIndex = addNew(val);
		vxIndex.add(newIndex);
		return newIndex;
	}

	public int addNew(int val) {
		for(List<Integer> row : matrix) {
			row.add(val);
		}
		List<Integer> lst = new ArrayList<Integer>(matrix.size() + 1);
		for(int i = 0; i < matrix.size() + 1; i++) {
			lst.add(val);
		}
		matrix.add(lst);
		return matrix.size() - 1;
	}
	
	@Override
	public int getVaxCount() {
		return vxIndex.size();
	}

	@Override
	public int getPtCount() {
		return ptIndex.size();
	}

	@Override
	public void increment(int m, int n) {
		List<Integer> row = matrix.get(m);
		row.set(n, row.get(n) + 1);
	}

	@Override
	public void increment(int m, int n, int val) {
		List<Integer> row = matrix.get(m);
		row.set(n, row.get(n) + val);
	}
	
	@Override
	public List<Integer> getVXIndicies() {
		return vxIndex;
	}

	@Override
	public List<Integer> getPTIndicies() {
		return ptIndex;
	}

	@Override
	public List<Integer> getDiagList(List<Integer> indicies) {
		List<Integer> ret = new ArrayList<Integer>(indicies.size());
		for(Integer i : indicies) {
			ret.add(matrix.get(i).get(i));
		}
		return ret;
	}

	@Override
	public List<Integer> getDiagList() {
		List<Integer> ret = new ArrayList<Integer>(matrix.size());
		for(int i = 0; i < matrix.size(); i++) {
			ret.add(matrix.get(i).get(i));
		}
		return ret;
	}
	
	@Override
	public void write(Writer writer) throws IOException {
		List<String> names = new ArrayList<String>(matrix.size());
		
		writer.write(",");
		for(int i = 0; i < matrix.size(); i++) {
			String str = ptIndex.contains(i) ? "pt" + (ptIndex.indexOf(i) + 1) : "vx" + (vxIndex.indexOf(i) + 1);
			names.add(str);
			writer.write(str);
			if(i == matrix.size() - 1)
				break;
			writer.write(",");
		}	
		writer.write("\n");	
		
		for(ListIterator<List<Integer>> lit = matrix.listIterator(); lit.hasNext(); ) {
			List<Integer> lst = lit.next();
			writer.write(names.get(lit.previousIndex()) + ",");
			for(ListIterator<Integer> it = lst.listIterator(); it.hasNext(); ) {
				writer.write(it.next().toString());
				if(it.nextIndex() == lst.size())// - 1)
					break;
				writer.write(",");
			}
			writer.write("\n");
		}
	}
	
	@Override
	public void setDiag(int val) {
		for(int i = 0; i < matrix.size(); i++) {
			matrix.get(i).set(i, val);
		}
	}

	@Override
	public void clear() {
		for(List<Integer> lst : matrix) {
			lst.clear();
		}
		ptIndex.clear();
		vxIndex.clear();
	}

	@Override
	public List<Integer> getRow(int m) {
		return matrix.get(m);
	}
	
}
