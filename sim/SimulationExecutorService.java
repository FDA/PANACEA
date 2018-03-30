package com.eng.cber.na.sim;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/** ExecutorService wrapper **/
public class SimulationExecutorService {

	/** There shall be as max of cores - 1 worker threads. **/
	private static ExecutorService multiExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1); 
	
	/** There shall be as max of cores - 1 worker threads. **/
	private static ExecutorService singleExecutor = Executors.newFixedThreadPool(1); 

	/** Submit to the executor service **/
	public static Future<?> submitSingle(Runnable task) {
		if (isShutdown())
			restartService();
		Future<?> resultOfSubmission = singleExecutor.submit(task);
		return resultOfSubmission;
	}
	
	/** Submit to the executor service **/
	public static Future<?> submitMulti(Runnable task) {
		if (isShutdown())
			restartService();
		return multiExecutor.submit(task);
	}
	
	/** Shut the executor service down **/
	public static void shutdown() {
		singleExecutor.shutdown();
		multiExecutor.shutdown();
	}
	
	private static boolean isShutdown() {
		return singleExecutor.isShutdown() || multiExecutor.isShutdown();
	}
	
	private static void restartService() {
		singleExecutor = Executors.newFixedThreadPool(1);
		multiExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1); 
	}
}
