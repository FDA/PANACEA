package com.eng.cber.na.concurrent;

import java.util.HashMap;

/**
 * The IncrementableHashMap is a hashmap that allows
 * the user to increment a key by a given value while
 * avoiding the lengthy "put" syntax to do so.
 *
 */
@SuppressWarnings("serial")
public class IncrementableHashMap<K> extends HashMap<K, Double> {

	public IncrementableHashMap() {
		super();
	}
	
	public synchronized void increment(K key, Double increment) {
		put(key, get(key) + increment);
	}
}
