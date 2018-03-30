package com.eng.cber.na.sim;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;

/** Class with useful static methods for the network formation simulator. **/
public class Util {
	
	public static void setChildrenEnabled(Container component, boolean enabled) {
		Stack<Container> stack = new Stack<Container>();
		stack.push(component);
		while(!stack.isEmpty()) {
			Container curComp = stack.pop();
			curComp.setEnabled(enabled);
			for(Component c : curComp.getComponents()) {
				if(c instanceof Container) {
					stack.push((Container)c);
				}
			}
		}
	}
	
	public static List<Integer> makeIntList(int from, int to) {
		List<Integer> ret = new ArrayList<Integer>(to - from + 1);
		for(int i = from; i < to + 1; i++) {
			ret.add(i);
		}
		return ret;
	}
	
	/** Sample without replacement **/
	public static <T> List<T> sample(UniformRealDistribution rng, List<T> vals_in, List<Double> probs_in, int numSamples) {
		if(vals_in.size() != probs_in.size()) {
			System.err.println("values and probabilities arrays need to be the same length");
			System.exit(1);
		}
		
		List<T> ret = new ArrayList<T>(numSamples);
		double totalWeight = 1;
		
		List<Double> probs = new ArrayList<Double>(probs_in);
		List<T> vals = new ArrayList<T>(vals_in);
		
		for(int i = 0; i < numSamples; i++) {
			
			double rnd = totalWeight*rng.sample();
			int j = 0;
			double cur = probs.get(j);
			while(cur < rnd && j + 1 < probs.size()) {
				cur += probs.get(++j);
			}
			ret.add(vals.get(j));
			
			totalWeight -= probs.get(j);
			probs.remove(j);
			vals.remove(j);
		}
		return ret;
	}
	
	public static <T> List<T> sample(UniformRealDistribution uniformRNG, List<T> vals_in, int numSamples) {
		List<Double> probs = new ArrayList<Double>(vals_in.size());
		for(int i = 0; i < vals_in.size(); i++) {
			probs.add(1.0/probs.size());
		}
		return Util.sample(uniformRNG, vals_in, probs, numSamples);
	}
	
	public static List<Integer> sampleBinomial(long randomSeed, int trials, double prob, int numSamples) {
		List<Integer> ret = new ArrayList<Integer>(numSamples);
		BinomialDistribution binomRNG = new BinomialDistribution(trials, prob);
		binomRNG.reseedRandomGenerator(randomSeed);
		for(int i = 0; i < numSamples; i++) {
			ret.add(binomRNG.sample());
		}
		return ret;
	}
	
	public static List<Double> normalizeIntList(List<Integer> lst) {
		List<Double> ret = new ArrayList<Double>(lst.size());
		Integer sum = 0;
		for(Integer i : lst) {
			sum += i;
		}
		for(Integer i : lst) {
			ret.add(i.doubleValue()/sum);
		}
		return ret;
	}
	
	public static <T> void printList(List<T> lst, String name) {
		System.out.print(name + ": ");
		for(T t : lst) {
			System.out.print(t + " ");
		}
		System.out.println();
	}
	
	public static <K,V extends Comparable<V>> Map<K,Double> rank(Map<K,V> in, int groups) {
		List<Pair<K,V>> lst = new ArrayList<Pair<K,V>>();
		for(K k : in.keySet()) {
			lst.add(new Pair<K,V>(k,in.get(k)));
		}
		Collections.sort(lst);

		Map<K,Double> rankMap = new TreeMap<K,Double>();
		int i = 1;
		for(Pair<K,V> p : lst) {
			rankMap.put(p.key, (double)i);
			i++;
		}

		Map<V,Integer> comCount = new TreeMap<V,Integer>();
		Map<V,Double> comSum = new TreeMap<V,Double>();
	
		i = 0;
		Double s = 0.0;

		if (lst.size() > 0) {
			V last = lst.get(0).value;
			for(Pair<K,V> p : lst) {
				if(p.value != last) {
					comCount.put(last, i);
					comSum.put(last, s);
					last = p.value;
					i = 0;
					s = 0.0;
				}
				i++;
				s += rankMap.get(p.key);
			}
	
			comCount.put(last, i);
			comSum.put(last, s);
		}		

		Map<K,Double> ret = new TreeMap<K,Double>();
		for(K k : rankMap.keySet()) {
			Integer c = comCount.get(in.get(k));
			Double ss = comSum.get(in.get(k));
			ret.put(k,Math.floor(ss/c*groups/in.size()));
		}
		

		return ret;
	}
	
	public static <T extends Comparable<T>> List<Integer> order(List<T> lst) {
		List<Pair<Integer,T>> plst = new ArrayList<Pair<Integer,T>>(lst.size());
		for(int i = 0; i < lst.size(); i++) {
			plst.add(new Pair<Integer,T>(i,lst.get(i)));
		}
		Collections.sort(plst);
		List<Integer> ret = new ArrayList<Integer>(lst.size());
		for(Pair<Integer,T> p : plst) {
			ret.add(p.key);
		}
		return ret;
	}
	
	static class Pair<K,V extends Comparable<V>> implements Comparable<Pair<K,V>> {
		public V value;
		public K key;
		public Pair(K key,V value) {
			this.value = value;
			this.key = key;
		}
		@Override
		public int compareTo(Pair<K,V> o) {
			return value.compareTo(o.value);
		}
	}
	
	public static List<Double> genPercentiles(int len) {
		List<Double> ret = new ArrayList<Double>(len);
		for(int i = 1; i <= len; i++) {
			ret.add((double)i/len);
		}
		return ret;
	}
	
	public static <T> List<T> subList(List<T> lst, List<Integer> indicies) {
		List<T> ret = new ArrayList<T>(indicies.size());
		for(Integer i : indicies) {
			ret.add(lst.get(i));
		}
		return ret;
	}
	
}

