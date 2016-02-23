package com.tlear.trise.objects;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.tlear.trise.environment.Environment;
import com.tlear.trise.functions.BooleanIsGoalFunction;
import com.tlear.trise.functions.DistanceToGoalHeuristicFunction;
import com.tlear.trise.functions.GoalFunction;
import com.tlear.trise.functions.ResultFunction;
import com.tlear.trise.functions.decision.DecideByAStarSearch;
import com.tlear.trise.functions.decision.DecideByBFS;
import com.tlear.trise.functions.decision.DecideByRandomPRM;
import com.tlear.trise.functions.decision.DecideByUCS;
import com.tlear.trise.functions.decision.DecisionFunction;
import com.tlear.trise.graph.TrackedGraph;
import com.tlear.trise.interactions.Action;
import com.tlear.trise.interactions.Actuator;
import com.tlear.trise.interactions.Sensor;
import com.tlear.trise.interactions.SensorManager;
import com.tlear.trise.metrics.ImmutableMetrics;
import com.tlear.trise.metrics.MutableMetrics;
import com.tlear.trise.utils.Triple;
import com.tlear.trise.utils.Tuple;

public class Agent extends DynamicObject {

	public Map<Integer, Agent> beliefKeyframes;
	public Map<Integer, Agent> actualKeyframes;

	private int lastKeyframe;

	private SensorManager sensorManager;
	private Set<Actuator> actuators;
	private ResultFunction result;
	private GoalFunction goal;
	private DecisionFunction decide;
	private Environment belief;

	private MutableMetrics metrics;

	private float speed;

	/**
	 * Construct an agent at position (x, y) with the given width and height,
	 * and add it to the environment.
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param env
	 */
	public Agent(float x, float y, float width, float height, Environment env,
			Collection<Sensor> sensors) {
		super(x, y, width, height);

		/*
		 * Add the initial state to the start of our list of keyframes for this
		 * agent
		 */
		beliefKeyframes = new LinkedHashMap<Integer, Agent>();
		actualKeyframes = new LinkedHashMap<Integer, Agent>();

		/*
		 * The most recently used keyframe
		 */
		lastKeyframe = 0;

		/*
		 * The set of sensors that the agent can consult
		 */
		sensorManager = new SensorManager(sensors);

		/*
		 * The set of actuators that the agent can use to move
		 */
		actuators = new HashSet<Actuator>();

		/*
		 * The function we use to predict the result of our action
		 */
		result = new ResultFunction();

		/*
		 * The function to determine whether a given node is a goal
		 */
		goal = new BooleanIsGoalFunction();
		// goal = new MultipleGoalsGoalFunction();

		/*
		 * The function to make a decision on what to do next given a current
		 * location
		 */
		decide = new DecideByAStarSearch(goal,
				new DistanceToGoalHeuristicFunction(env));
		// decide = new DecideByUCS(goal);

		/*
		 * An environment describing what the agent believes of the environment
		 */
		belief = new Environment();

		/*
		 * How fast the agent moves.
		 */
		speed = 3.0f;

		/*
		 * A metrics object tracking information we want to know about
		 */
		metrics = new MutableMetrics();

		/*
		 * Now we have defined the agent, we add it to the first keyframe for
		 * both belief and actual
		 */
		beliefKeyframes.put(0, this.copy());
		actualKeyframes.put(0, this.copy());
	}

	/**
	 * Creates an agent that is a deep copy of another agent.
	 * 
	 * @param that
	 */
	public Agent(Agent that) {
		super(that.pos.x, that.pos.y, that.width, that.height);

		this.beliefKeyframes = new LinkedHashMap<Integer, Agent>(
				that.beliefKeyframes);
		this.actualKeyframes = new LinkedHashMap<Integer, Agent>(
				that.actualKeyframes);
		this.sensorManager = new SensorManager(that.sensorManager);
		this.actuators = new HashSet<Actuator>(that.actuators);
		this.result = that.result;
		this.goal = that.goal;
		this.decide = that.decide;
		this.belief = new Environment(that.belief);
		this.speed = that.speed;

		this.metrics = that.metrics;

		this.lastKeyframe = that.lastKeyframe;
	}

	/**
	 * Returns the speed of the agent.
	 * 
	 * @return speed
	 */
	public float getSpeed() {
		return speed;
	}

	/**
	 * Given the current environment and map of times, computes a decision,
	 * implements it as the decided course of action, and updates the belief
	 * state with what is expected to happen.
	 * 
	 * @param env
	 * @param timeMap
	 * @return
	 */
	public Triple<Environment, Tuple<Integer, Integer>, TrackedGraph<Vector2>> process(
			Environment env, Map<Integer, Integer> timeMap) {

		/*
		 * Belief is updated to be what the sensors make of the environment at
		 * that point
		 */
		belief = sensorManager.apply(env);

		/*
		 * If something's happened to the belief state, we fix it.
		 */
		if (belief.dirty) {
			// decide = new DecideByAStarSearch(goal, new
			// DistanceToGoalHeuristicFunction(env));
			belief.clean();
		}

		/*
		 * Determine the action to take
		 */
		Tuple<MutableMetrics, Tuple<Action, TrackedGraph<Vector2>>> decision = decide
				.apply(belief);
		metrics = decision.fst;
		Action act = decision.snd.fst;
		TrackedGraph<Vector2> g = decision.snd.snd;

		/*
		 * Estimate what will happen when we take that action
		 */
		Tuple<Environment, Integer> beliefResult = result.apply(act, belief);

		/*
		 * Update belief states based on what we believe will happen
		 */
		belief = beliefResult.fst;
		beliefKeyframes.put(lastKeyframe + 1, beliefResult.fst.agents.get(0));

		/*
		 * Find out what will actually happen
		 */
		Tuple<Environment, Integer> actualResult = result.apply(act, env);

		/*
		 * Update actual states based on what will actually happen
		 */
		env = actualResult.fst;
		actualKeyframes.put(lastKeyframe + 1, actualResult.fst.agents.get(0)
				.copy());

		// System.out.println("ACTUAL KEYFRAMES " + actualKeyframes);

		/*
		 * Update keyframing
		 */
		Tuple<Integer, Integer> mapEntry = new Tuple<Integer, Integer>(
				lastKeyframe + 1, timeMap.get(lastKeyframe) + actualResult.snd);
		lastKeyframe++;

		// System.out.println(actualKeyframes);

		/*
		 * Return the updated environment and time map
		 */
		return new Triple<Environment, Tuple<Integer, Integer>, TrackedGraph<Vector2>>(
				env, mapEntry, g);
	}

	@Override
	public String toString() {
		return String.format("Agent [pos = %s, decisionFn = %s, s13nFn = %s",
				pos.toString(), decide.getName(), decide.getS13nName());
	}

	public void update(Map<Integer, Integer> timeMap, int prevKeyframe,
			int time, int nextKeyframe) {
		/*
		 * prevKeyframe = k-1 nextKeyframe = k
		 * 
		 * PRE: k-1 < k k-1 > 0 k <= lastKeyframe timeMap[k-1] <= t <=
		 * timeMap[k]
		 */

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
		 * Interpolation
		 */

		// System.out.println("INTERPOLATING");
		// System.out.println("ACTUAL KEYFRAMES BEFORE INTERPOLATION: " +
		// actualKeyframes);

		float t = time;
		float kprev = timeMap.get(prevKeyframe);
		float knext = timeMap.get(nextKeyframe);

		float lambda = (t - kprev) / (knext - kprev);

		Vector2 prevPos = actualKeyframes.get(prevKeyframe).pos.cpy();
		Vector2 nextPos = actualKeyframes.get(nextKeyframe).pos.cpy();

		// System.out.println("Prev pos: " + prevPos);
		// System.out.println("Next pos: " + nextPos);

		pos.set(prevPos.scl(1 - lambda).add(nextPos.scl(lambda)));

		// System.out.println("ACTUAL KEYFRAMES AFTER INTERPOLATION: " +
		// actualKeyframes);
	}

	public void draw(ShapeRenderer sr, SpriteBatch batch) {
		// theta+=5;
		// batch.begin();
		// batch.draw(tex, pos.x - (width + 30)/2, pos.y - (height+50)/2,
		// ((width + 30) / 2), ((height + 50) / 2), width + 30, height + 50, 1,
		// 1, theta);
		// batch.end();
		sr.begin(ShapeType.Filled);
		sr.setColor(1, 1, 1, 0);
		sr.rect(pos.x - (width) / 2, pos.y - (height) / 2, width, height);
		sr.end();
		sr.begin(ShapeType.Line);
		sr.setColor(selected ? 1 : 0, selected ? 1 : 0, 0, 1);
		sr.rect(pos.x - (width) / 2, pos.y - (height) / 2, width, height);
		sr.end();
	}

	public Agent copy() {
		return new Agent(this);
	}

	public ImmutableMetrics getMetrics() {
		return new ImmutableMetrics(metrics);
	}

	public DecisionFunction getDecisionFunction() {
		return decide;
	}

	public void nextDecisionFunction() {
		System.out.println("Cycling decision function...");
		if (decide instanceof DecideByRandomPRM) {
			decide = new DecideByBFS(goal);
			return;
		}
		if (decide instanceof DecideByBFS) {
			decide = new DecideByUCS(goal);
			return;
		}
		if (decide instanceof DecideByUCS) {
			decide = new DecideByAStarSearch(goal,
					new DistanceToGoalHeuristicFunction(belief));
			return;
		}
		if (decide instanceof DecideByAStarSearch) {
			decide = new DecideByRandomPRM();
			return;
		}
	}

	/*
	 * Cycles skeletonisation function to the next one in the list.
	 */
	public void nextSkeletonisationFunction() {
		System.out.println("Cycling s13n function...");
		if (decide instanceof DecideByAStarSearch) {
			((DecideByAStarSearch) decide).nextS13nFunction();
		}
	}

	/*
	 * Set the heuristic function to the one provided by the user.
	 */
	public void setHeuristic(List<String> functionAsList) {

	}

}
