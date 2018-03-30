package com.eng.cber.na.concurrent;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.SwingWorker;

/**
 * The NetworkExecutorService is a network-specific version
 * of the ExecutorService that provides utilities for submitting
 * workers to be run on the available threads and shutting
 * the service down. It is used throughout the program 
 * wherever parallel processing between cores would be helpful.
 *
 */
public class NetworkExecutorService {
	
	/** There shall be a max of cores - 1 worker threads. **/
	private static final ExecutorService executor = Executors.newFixedThreadPool(Math.max(1,Runtime.getRuntime().availableProcessors() - 1));
	/** An alternate executor for tasks if you know the main one will be busy. **/
	private static final ExecutorService altExecutor = Executors.newFixedThreadPool(1);
	
	/** Submit to the executor service **/
	public static List<Future<?>> submitBundle(WorkerBundle bundle) {
		List<? extends SwingWorker<Object,Object>> workers = bundle.getWorkers();
		if(workers == null || workers.size() == 0) {
			return null;
		}
		return submitWorkers(bundle.getWorkers());
	}
	
	private static List<Future<?>> submitWorkers(List<? extends SwingWorker<Object,Object>> workers) {
		List<Future<?>> ret = new LinkedList<Future<?>>();
		for(SwingWorker<Object,Object> worker : workers) {
			ret.add(executor.submit(worker));
		}
		return ret;
	}
	
	/** Submit to the executor service **/
	public static Future<?> submit(Runnable task) {
		return executor.submit(task);
	}
	
	/** Submit to the alternate executor service **/
	public static Future<?> submitAlternate(Runnable task) {
		return altExecutor.submit(task);
	}
	
	/** Shut executor service down **/
	public static void shutdown() {
		executor.shutdown();
		altExecutor.shutdown();
	}	
}
