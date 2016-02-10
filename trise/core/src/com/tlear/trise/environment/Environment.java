package com.tlear.trise.environment;

import java.util.LinkedList;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.tlear.trise.graph.TrackedGraph;
import com.tlear.trise.objects.Agent;
import com.tlear.trise.objects.StaticGoal;
import com.tlear.trise.objects.StaticObstacle;
import com.tlear.trise.utils.Triple;
import com.tlear.trise.utils.Tuple;

public class Environment {
	public final int maxX;
	public final int maxY;

	public LinkedList<Agent> agents;
	public LinkedList<StaticObstacle> obstacles;
	public LinkedList<StaticGoal> goals;

	public boolean dirty;

	@Override
	public String toString() {
		return "Environment [maxX=" + maxX + ", maxY=" + maxY + ", agents=" + agents + ", obstacles=" + obstacles + ", goals=" + goals + "]";
	}

	public Environment() {
		agents = new LinkedList<Agent>();
		obstacles = new LinkedList<StaticObstacle>();
		goals = new LinkedList<StaticGoal>();

		maxX = Gdx.graphics.getWidth() - 200;
		maxY = Gdx.graphics.getHeight();

		dirty = false;
	}

	public Environment(int maxX, int maxY) {
		agents = new LinkedList<Agent>();
		obstacles = new LinkedList<StaticObstacle>();
		goals = new LinkedList<StaticGoal>();

		this.maxX = maxX;
		this.maxY = maxY;

		dirty = false;
	}

	public Environment(Environment that) {
		this.maxX = that.maxX;
		this.maxY = that.maxY;

		this.agents = new LinkedList<Agent>(that.agents);
		this.obstacles = new LinkedList<StaticObstacle>(that.obstacles);
		this.goals = new LinkedList<StaticGoal>(that.goals);

		dirty = false;
	}

	public boolean placeAgent(Agent a) {
		/*
		 * We'll want to do some checking that we *can* place this agent
		 */
		agents.add(a);
		return true;
	}

	public boolean placeObstacle(StaticObstacle o) {
		obstacles.add(o);
		return true;
	}

	public boolean placeGoal(StaticGoal g) {
		goals.add(g);
		return true;
	}

	/**
	 * Generates the next keyframe
	 * 
	 * @param timeMap
	 */
	public Triple<Integer, Integer, TrackedGraph<Vector2>> getNextKeyframe(Map<Integer, Integer> timeMap) {

		// System.out.println("GETTING NEXT KEYFRAME");
		/*
		 * we'll probably want to check that we are actually at the latest
		 * keyframe
		 */

		Triple<Environment, Tuple<Integer, Integer>, TrackedGraph<Vector2>> nextKeyframe = agents.getFirst().process(this, timeMap);
		// System.out.println("NEXT KEYFRAME: " + nextKeyframe);
		Tuple<Integer, Integer> mapEntry = nextKeyframe.snd;

		return new Triple<Integer, Integer, TrackedGraph<Vector2>>(mapEntry.fst, mapEntry.snd, nextKeyframe.thd);
	}

	public void update(Map<Integer, Integer> timeMap, int prevKeyframe, int time, int nextKeyframe) {

		/*
		 * prevKeyframe = k-1 nextKeyframe = k
		 * 
		 * PRE: k-1 < k k-1 > 0 k <= lastKeyframe timeMap[k-1] <= t <=
		 * timeMap[k] timeMap.length > 0
		 */

		// System.out.println(time);

		int lastKeyframe = findLastKeyframe(timeMap);

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
			throw new RuntimeException("Trying to interpolate for a time outside the keyframe bounds: " + time + ".  Bounds are " + timeMap.get(prevKeyframe)
					+ " and " + timeMap.get(nextKeyframe));
		}

		/*
		 * Loop through agents
		 */

		for (Agent a : agents) {
			a.update(timeMap, prevKeyframe, time, nextKeyframe);
			// System.out.println(a);
		}
	}

	public void draw(ShapeRenderer sr, SpriteBatch batch) {
		for (StaticObstacle o : obstacles) {
			o.draw(sr, batch);
		}
		for (StaticGoal g : goals) {
			g.draw(sr, batch);
		}
		for (Agent a : agents) {
			a.draw(sr, batch);
		}
	}

	private int findLastKeyframe(Map<Integer, Integer> timeMap) {
		// Finds the last keyframe as the largest value in the keys of timeMap
		return timeMap.keySet().stream().max((x, y) -> x - y).orElse(-1);
	}

	public void dirty() {
		dirty = true;
	}

	public void clean() {
		dirty = false;
	}
}
