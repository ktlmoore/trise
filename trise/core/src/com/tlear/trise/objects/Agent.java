package com.tlear.trise.objects;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.tlear.trise.environment.Environment;
import com.tlear.trise.functions.BooleanIsGoalFunction;
import com.tlear.trise.functions.DistanceToGoalHeuristicFunction;
import com.tlear.trise.functions.GoalFunction;
import com.tlear.trise.functions.ResultFunction;
import com.tlear.trise.functions.decision.DecideByAStarSearch;
import com.tlear.trise.functions.decision.DecisionFunction;
import com.tlear.trise.graph.TrackedGraph;
import com.tlear.trise.interactions.Action;
import com.tlear.trise.interactions.Actuator;
import com.tlear.trise.interactions.Sensor;
import com.tlear.trise.utils.Triple;
import com.tlear.trise.utils.Tuple;

public class Agent extends DynamicObject {

	public Map<Integer, Agent> beliefKeyframes;
	public Map<Integer, Agent> actualKeyframes;

	private int lastKeyframe;

	private Set<Sensor> sensors;
	private Set<Actuator> actuators;
	private ResultFunction result;
	private GoalFunction goal;
	private DecisionFunction decide;
	private Environment belief;

	private int theta = 0;

	private float speed;

	private Texture img;
	private TextureRegion tex;

	public Agent(float x, float y, float width, float height, Environment env) {
		super(x, y, width, height);

		img = new Texture("benhead.png");
		tex = new TextureRegion(img);

		/*
		 * Add the initial state to the start of our list of keyframes
		 */
		beliefKeyframes = new LinkedHashMap<Integer, Agent>();
		actualKeyframes = new LinkedHashMap<Integer, Agent>();

		lastKeyframe = 0;

		sensors = new HashSet<Sensor>();
		actuators = new HashSet<Actuator>();

		result = new ResultFunction();
		goal = new BooleanIsGoalFunction();
		decide = new DecideByAStarSearch(goal, new DistanceToGoalHeuristicFunction(env));
		// decide = new DecideByUCS(goal);

		belief = new Environment();

		speed = 3.0f;

		beliefKeyframes.put(0, this.copy());
		actualKeyframes.put(0, this.copy());
	}

	public Agent(Agent that) {
		super(that.pos.x, that.pos.y, that.width, that.height);

		this.beliefKeyframes = new LinkedHashMap<Integer, Agent>(that.beliefKeyframes);
		this.actualKeyframes = new LinkedHashMap<Integer, Agent>(that.actualKeyframes);
		this.sensors = new HashSet<Sensor>(that.sensors);
		this.actuators = new HashSet<Actuator>(that.actuators);
		this.result = that.result;
		this.goal = that.goal;
		this.decide = that.decide;
		this.belief = new Environment(that.belief);
		this.speed = that.speed;

		this.lastKeyframe = that.lastKeyframe;
	}

	public float getSpeed() {
		return speed;
	}

	public Triple<Environment, Tuple<Integer, Integer>, TrackedGraph<Vector2>> process(Environment env, Map<Integer, Integer> timeMap) {

		belief = env;
		/*
		 * Determine the action to take
		 */
		Tuple<Action, TrackedGraph<Vector2>> decision = decide.apply(belief);
		Action act = decision.fst;
		TrackedGraph<Vector2> g = decision.snd;

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
		actualKeyframes.put(lastKeyframe + 1, actualResult.fst.agents.get(0).copy());

		// System.out.println("ACTUAL KEYFRAMES " + actualKeyframes);

		/*
		 * Update keyframing
		 */
		Tuple<Integer, Integer> mapEntry = new Tuple<Integer, Integer>(lastKeyframe + 1, timeMap.get(lastKeyframe) + actualResult.snd);
		lastKeyframe++;

		// System.out.println(actualKeyframes);

		/*
		 * Return the updated environment and time map
		 */
		return new Triple<Environment, Tuple<Integer, Integer>, TrackedGraph<Vector2>>(env, mapEntry, g);
	}

	@Override
	public String toString() {
		return "Agent [pos = " + pos.toString() + "]";
	}

	public void update(Map<Integer, Integer> timeMap, int prevKeyframe, int time, int nextKeyframe) {
		/*
		 * prevKeyframe = k-1 nextKeyframe = k
		 * 
		 * PRE: k-1 < k k-1 > 0 k <= lastKeyframe timeMap[k-1] <= t <=
		 * timeMap[k]
		 */

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

}
