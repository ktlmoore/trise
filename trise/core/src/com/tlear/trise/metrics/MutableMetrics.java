package com.tlear.trise.metrics;



public class MutableMetrics {
	
	private int nodesExplored;
	private int nodesInFrontier;
	private long timeToSkeletonise;
	private long timeToSearch;
	private long timeToReachGoal;
	private int timesReset;
	
	public boolean isSet(Number value) {
		return value.doubleValue() >= 0;
	}
	
	public MutableMetrics() {
		nodesExplored = -1;
		nodesInFrontier = -1;
		timeToSearch = -1;
		timeToReachGoal = -1;
		timeToSkeletonise = -1;
		timesReset = 0;
	}
	
	public void reset() {
		nodesExplored = -1;
		nodesInFrontier = -1;
		timeToSearch = -1;
		timeToReachGoal = -1;
		timeToSkeletonise = -1;
		timesReset++;
	}

	public void setNodesExplored(int n) {
		nodesExplored = n;
	}
	
	public void setNodesInFrontier(int n) {
		nodesInFrontier = n;
	}
	
	public void setTimeToSearch(long n) {
		timeToSearch = n;
	}
	
	public void setTimeToReachGoal(long n) {
		timeToReachGoal = n;
	}
	
	public void setTimeToSkeletonise(long n) {
		timeToSkeletonise = n;
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
