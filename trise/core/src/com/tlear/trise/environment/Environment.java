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

	/**
	 * String representation of an environment.
	 */
	@Override
	public String toString() {
		return "Environment [maxX=" + maxX + ", maxY=" + maxY + ", agents="
				+ agents + ", obstacles=" + obstacles + ", goals=" + goals
				+ "]";
	}

	/**
	 * Creates a blank environment.
	 */
	public Environment() {
		agents = new LinkedList<Agent>();
		obstacles = new LinkedList<StaticObstacle>();
		goals = new LinkedList<StaticGoal>();

		maxX = Gdx.graphics.getWidth() - 200;
		maxY = Gdx.graphics.getHeight();

		dirty = false;
	}

	/**
	 * Creates an empty environment with a given size.
	 * 
	 * @param maxX
	 * @param maxY
	 */
	public Environment(int maxX, int maxY) {
		agents = new LinkedList<Agent>();
		obstacles = new LinkedList<StaticObstacle>();
		goals = new LinkedList<StaticGoal>();

		this.maxX = maxX;
		this.maxY = maxY;

		dirty = false;
	}

	/**
	 * Creates this environment as a deep copy of another one.
	 * 
	 * @param that
	 */
	public Environment(Environment that) {
		this.maxX = that.maxX;
		this.maxY = that.maxY;

		this.agents = new LinkedList<Agent>(that.agents);
		this.obstacles = new LinkedList<StaticObstacle>(that.obstacles);
		this.goals = new LinkedList<StaticGoal>(that.goals);

		dirty = false;
	}

	/**
	 * Puts an agent in the environment.
	 * 
	 * @param a
	 * @return
	 */
	public boolean placeAgent(Agent a) {
		/*
		 * We'll want to do some checking that we *can* place this agent
		 */
		agents.add(a);
		return true;
	}

	/**
	 * Puts an obstacle in the environment.
	 * 
	 * @param o
	 * @return
	 */
	public boolean placeObstacle(StaticObstacle o) {
		obstacles.add(o);
		return true;
	}

	/**
	 * Puts a goal in the environment.
	 * 
	 * @param g
	 * @return
	 */
	public boolean placeGoal(StaticGoal g) {
		goals.add(g);
		return true;
	}

	/**
	 * Works out the next keyframe position and time given the one we're
	 * currently at.
	 * 
	 * @param timeMap
	 */
	public Triple<Integer, Integer, TrackedGraph<Vector2>> getNextKeyframe(
			Map<Integer, Integer> timeMap) {

		/*
		 * PRECONDITION: We are actually at a keyframe.
		 */

		/*
		 * Calls "process" on the agent to get the details of the next key
		 * frame.
		 */
		Triple<Environment, Tuple<Integer, Integer>, TrackedGraph<Vector2>> nextKeyframe = agents
				.getFirst().process(this, timeMap);
		Tuple<Integer, Integer> mapEntry = nextKeyframe.snd;

		/*
		 * We return... er... something. Dammit, Tom, this is why we comment
		 * things!
		 */
		return new Triple<Integer, Integer, TrackedGraph<Vector2>>(
				mapEntry.fst, mapEntry.snd, nextKeyframe.thd);
	}

	/**
	 * Updates the environment with everything being where it should be given
	 * the current time and the keyframes we are between.
	 * 
	 * @param timeMap
	 * @param prevKeyframe
	 * @param time
	 * @param nextKeyframe
	 */
	public void update(Map<Integer, Integer> timeMap, int prevKeyframe,
			int time, int nextKeyframe) {

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
			throw new RuntimeException(
					"Previous keyframe must be less than next keyframe");
		}
		if (prevKeyframe < 0) {
			throw new RuntimeException(
					"Trying to interpolate from a negative keyframe: "
							+ prevKeyframe);
		}
		if (nextKeyframe > lastKeyframe) {
			throw new RuntimeException(
					"Trying to interpolate to a non-existent keyframe: "
							+ nextKeyframe + ".  Most recent keyframe is "
							+ lastKeyframe);
		}
		if (timeMap.get(prevKeyframe) > time
				|| timeMap.get(nextKeyframe) < time) {
			throw new RuntimeException(
					"Trying to interpolate for a time outside the keyframe bounds: "
							+ time + ".  Bounds are "
							+ timeMap.get(prevKeyframe) + " and "
							+ timeMap.get(nextKeyframe));
		}

		/*
		 * Loop through agents and update each of them
		 */
		for (Agent a : agents) {
			a.update(timeMap, prevKeyframe, time, nextKeyframe);
			// System.out.println(a);
		}
	}

	/**
	 * Draws everything in the environment.
	 * 
	 * @param sr
	 * @param batch
	 */
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

	/**
	 * Finds the very last keyframe that we have any data for.
	 * 
	 * @param timeMap
	 * @return
	 */
	private int findLastKeyframe(Map<Integer, Integer> timeMap) {
		// Finds the last keyframe as the largest value in the keys of timeMap
		return timeMap.keySet().stream().max((x, y) -> x - y).orElse(-1);
	}

	/**
	 * Dirties the environment.
	 */
	public void dirty() {
		dirty = true;
	}

	/**
	 * Undirties the environment.
	 */
	public void clean() {
		dirty = false;
	}
}
