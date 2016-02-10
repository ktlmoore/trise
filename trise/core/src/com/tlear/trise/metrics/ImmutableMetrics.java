package com.tlear.trise.metrics;


public class ImmutableMetrics {
	
	private int nodesExplored;
	private int nodesInFrontier;
	private long timeToSearch;
	private long timeToReachGoal;
	private long timeToSkeletonise;
	private int timesReset;
	
	
	public boolean isSet(Number value) {
		return value.doubleValue() >= 0;
	}
	
	public ImmutableMetrics(MutableMetrics metrics) {
		nodesExplored = metrics.getNodesExplored();
		nodesInFrontier = metrics.getNodesInFrontier();
		timeToSearch = metrics.getTimeToSearch();
		timeToReachGoal = metrics.getTimeToReachGoal();
		timesReset = metrics.getTimesReset();
		timeToSkeletonise = metrics.getTimeToSkeletonise();
	}
	
	public int getNodesExplored() {
		return nodesExplored;
	}
	
	public int getNodesInFrontier() {
		return nodesInFrontier;
	}
	
	public long getTimeToSearch() {
		return timeToSearch;
	}
	
	public long getTimeToReachGoal() {
		return timeToReachGoal;
	}
	
	public long getTimeToSkeletonise() {
		return timeToSkeletonise;
	}
	
	public int getTimesReset() {
		return timesReset;
	}

}
