package org.treeops.utils;

public class StopWatch {
	private long start;
	private final long totalStart;

	public StopWatch() {
		totalStart = System.currentTimeMillis();
		reset();
	}

	private void reset() {
		start = System.currentTimeMillis();
	}

	public double elapsedTime() {
		long now = System.currentTimeMillis();
		double res = (now - start) / 1000.0;
		reset();
		return res;
	}

	public double sinseStart() {
		long now = System.currentTimeMillis();
		return (now - totalStart) / 1000.0;
	}
}
