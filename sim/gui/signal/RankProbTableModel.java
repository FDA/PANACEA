package com.eng.cber.na.sim.gui.signal;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

/**
 * This model stores that state of the Rank/Probability table
 * for explicitly defining PTs associated with a signal vaccine.
 * 
 */

public class RankProbTableModel extends AbstractTableModel {

	private String[] colNames = {"Rel. Rank", "Probability"};
	private List<Tuple<Double,Double>> tups = new LinkedList<Tuple<Double,Double>>();
	private Comparator<Tuple<Double,Double>> comparator = new TupleComparatorFactory.FirstComparator<Double, Double>(1);
	
	public enum RankProbSortOrder {
		RANK_FORWARD,RANK_BACKWARD,PROB_FORWARD,PROB_BACKWARD
	}
	
	private static Map<RankProbSortOrder,Comparator<Tuple<Double,Double>>> sortMap = new HashMap<RankProbSortOrder,Comparator<Tuple<Double,Double>>>() {
		{
			put(RankProbSortOrder.RANK_FORWARD,new TupleComparatorFactory.FirstComparator<Double,Double>(1));
			put(RankProbSortOrder.RANK_BACKWARD,new TupleComparatorFactory.FirstComparator<Double,Double>(-1));
			put(RankProbSortOrder.PROB_FORWARD,new TupleComparatorFactory.SecondComparator<Double,Double>(1));
			put(RankProbSortOrder.PROB_BACKWARD,new TupleComparatorFactory.SecondComparator<Double,Double>(-1));
		}
	};
	
	public void addPair(double rank, double prob) {
		if(rank <= 0 || rank > 1 || prob <= 0 || prob > 1) {
			JOptionPane.showMessageDialog(null, "Please ensure that both rank and probability are in the interval (0,1]", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		tups.add(new Tuple<Double,Double>(rank,prob));
		sortAndUpdate();
	}
	
	public void changeOrder(RankProbSortOrder order) {
		comparator = sortMap.get(order);
		sortAndUpdate();
	}
	
	private void sortAndUpdate() {
		Collections.sort(tups, comparator);
		fireTableDataChanged();
	}
	
	public void removePair(int index) {
		tups.remove(index);
		fireTableDataChanged();
	}
	
	public void clear() {
		tups.clear();
	}
	
	public List<Double> getSignalRanks() {
		List<Double> ret = new LinkedList<Double>();
		for(Tuple<Double,Double> tup : tups) {
			ret.add(tup.getFirst());
		}
		return ret;
	}
	
	public List<Double> getSignalProbs() {
		List<Double> ret = new LinkedList<Double>();
		for(Tuple<Double,Double> tup : tups) {
			ret.add(tup.getSecond());
		}
		return ret;
	}
	
	@Override
	public String getColumnName(int col) {
		return colNames[col];
	}
	
	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public int getRowCount() {
		return tups.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		return col == 0 ? tups.get(row).getFirst() : tups.get(row).getSecond();
	}

	public class Tuple<F extends Comparable<F>,S extends Comparable<S>>  {
		private F f;
		private S s;
		public Tuple(F f, S s) {
			this.f = f;
			this.s = s;
		}
		public F getFirst() {
			return f;
		}
		public S getSecond() {
			return s;
		}
	}
	
	static class TupleComparatorFactory {
		static class FirstComparator<M extends Comparable<M>,N extends Comparable<N>> implements Comparator<Tuple<M,N>> {
			private int d;
			public FirstComparator(int d) {
				this.d = d;
			}
			@Override
			public int compare(Tuple<M,N> t1, Tuple<M,N> t2) {
				return d*t1.getFirst().compareTo(t2.getFirst());
			}						
		}
		static class SecondComparator<M extends Comparable<M>,N extends Comparable<N>> implements Comparator<Tuple<M,N>> {
			private int d;
			public SecondComparator(int d) {
				this.d = d;
			}
			@Override
			public int compare(Tuple<M, N> t1, Tuple<M, N> t2) {
				return d*t1.getSecond().compareTo(t2.getSecond());
			}						
		}
	}
	
	
	
}
