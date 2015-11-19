package com.tlear.trise.environment;

import java.util.LinkedList;
import java.util.Map;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.tlear.trise.objects.Agent;
import com.tlear.trise.objects.StaticGoal;
import com.tlear.trise.objects.StaticObstacle;

public class Environment {
	public final int maxX;
	public final int maxY;

	public LinkedList<Agent> agents;
	public LinkedList<StaticObstacle> obstacles;
	public LinkedList<StaticGoal> goals;
	
	public Environment() {
		agents = new LinkedList<Agent>();
		obstacles = new LinkedList<StaticObstacle>();
		goals = new LinkedList<StaticGoal>();
		
		maxX = 0;
		maxY = 0;
	}
	
	public Environment(int maxX, int maxY) {
		agents = new LinkedList<Agent>();
		obstacles = new LinkedList<StaticObstacle>();
		goals = new LinkedList<StaticGoal>();
		
		this.maxX = maxX;
		this.maxY = maxY;
	}
	
	public Environment(Environment that) {
		this.maxX = that.maxX;
		this.maxY = that.maxY;
		
		this.agents = new LinkedList<Agent>(that.agents);
		this.obstacles = new LinkedList<StaticObstacle>(that.obstacles);
		this.goals = new LinkedList<StaticGoal>(that.goals);
	}
	
	public int placeAgent(Agent a) {
		agents.add(a);
		return agents.size();
	}
	
	/**
	 * Generates the next keyframe
	 * @param timeMap
	 */
	public void getNextKeyframe(Map<Integer, Integer> timeMap) {
		agents.getFirst().process(this, timeMap);
	}
	
	public void update(Map<Integer, Integer> timeMap, int prevKeyframe, int time, int nextKeyframe) {
		
		/* prevKeyframe = k-1
		 * nextKeyframe = k
		 *
		 * PRE: k-1 < k
		 * 		k-1 > 0
		 * 		k <= lastKeyframe
		 * 		timeMap[k-1] <= t <= timeMap[k]
		 * 		timeMap.length > 0
		 */
		
		// Finds the last keyframe as the largest value in the keys of timeMap
		int lastKeyframe = timeMap.keySet().stream().max((x, y) -> x - y).orElse(-1);
		
		if (lastKeyframe < 0) {
			throw new RuntimeException("Invalid time map " + timeMap.toString());
		}
		if (prevKeyframe >= nextKeyframe) {
			throw new RuntimeException("Previous keyframe must be less than next keyframe");
		}
		if (prevKeyframe < 0) {
			throw new RuntimeException("Trying to interpolate from a negative keyframe: " + prevKeyframe);
		}
		if (nextKeyframe > lastKeyframe) {
			throw new RuntimeException("Trying to interpolate to a non-existent keyframe: " + nextKeyframe + ".  Most recent keyframe is " + lastKeyframe);
		}
		if (timeMap.get(prevKeyframe) > time || timeMap.get(nextKeyframe) < time) {
			throw new RuntimeException("Trying to interpolate for a time outside the keyframe bounds: " + time + ".  Bounds are " + timeMap.get(prevKeyframe) + " and " + timeMap.get(nextKeyframe));
		}
		
		/*
		 * Loop through agents
		 */
		
		for (Agent a : agents) {
			a.update(timeMap, prevKeyframe, time, nextKeyframe);
		}
	}
	
	public void draw(ShapeRenderer sr) {
		for (Agent a : agents) {
			a.draw(sr);
		}
	}
}
