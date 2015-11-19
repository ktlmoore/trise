package com.tlear.trise.objects;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.tlear.trise.environment.Environment;
import com.tlear.trise.functions.DecisionFunction;
import com.tlear.trise.functions.GoalFunction;
import com.tlear.trise.functions.ResultFunction;
import com.tlear.trise.interactions.Action;
import com.tlear.trise.interactions.Actuator;
import com.tlear.trise.interactions.Sensor;
import com.tlear.trise.utils.Tuple;

public class Agent extends DynamicObject {
	
	private Map<Integer, Agent> beliefKeyframes;
	private Map<Integer, Agent> actualKeyframes;
	
	private int lastKeyframe;
	
	private Set<Sensor> sensors;
	private Set<Actuator> actuators;
	private ResultFunction result;
	private GoalFunction goal;
	private DecisionFunction decide;
	private Environment belief;
	
	private float speed;

	public Agent(float x, float y, float width, float height) {
		super(x, y, width, height);
		
		/*
		 * Add the initial state to the start of our list of keyframes
		 */
		beliefKeyframes = new LinkedHashMap<Integer, Agent>();
		actualKeyframes = new LinkedHashMap<Integer, Agent>();
		beliefKeyframes.put(0, this.copy());
		actualKeyframes.put(0, this.copy());
		
		lastKeyframe = 0;
		
		sensors = new HashSet<Sensor>();
		actuators = new HashSet<Actuator>();
		
		result = new ResultFunction();
		goal = new GoalFunction();
		decide = new DecisionFunction();
		
		belief = new Environment();
		
		speed = 5.0f;
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
	
	public Tuple<Environment, Map<Integer, Integer>> process(Environment env, Map<Integer, Integer> timeMap) {
		/*
		 * Determine the action to take
		 */
		Action act = decide.apply(belief);
		
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
		actualKeyframes.put(lastKeyframe + 1, actualResult.fst.agents.get(0));
		
		/*
		 * Update keyframing
		 */
		timeMap.put(lastKeyframe + 1, timeMap.get(lastKeyframe) + actualResult.snd);
		lastKeyframe++;
		
		/*
		 * Return the updated environment and time map
		 */
		return new Tuple<Environment, Map<Integer, Integer>>(env, timeMap);
	}
	
	public void update(Map<Integer, Integer> timeMap, int prevKeyframe, int time, int nextKeyframe) {
		/* prevKeyframe = k-1
		 * nextKeyframe = k
		 *
		 * PRE: k-1 < k
		 * 		k-1 > 0
		 * 		k <= lastKeyframe
		 * 		timeMap[k-1] <= t <= timeMap[k]
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
			throw new RuntimeException("Trying to interpolate for a time outside the keyframe bounds: " + time + ".  Bounds are " + timeMap.get(prevKeyframe) + " and " + timeMap.get(nextKeyframe));
		}
		
		/*
		 * Interpolation
		 */
		
		float t = time;
		float kprev = timeMap.get(prevKeyframe);
		float knext = timeMap.get(nextKeyframe);
		
		float lambda = (t - kprev) / (knext - kprev);
		
		Vector2 prevPos = actualKeyframes.get(prevKeyframe).pos.cpy();
		Vector2 nextPos = actualKeyframes.get(nextKeyframe).pos.cpy();
		
		pos.set(prevPos.scl(1 - lambda).add(nextPos.scl(lambda)));
	}
	
	public void draw(ShapeRenderer sr) {
		sr.begin(ShapeType.Line);
		sr.rect(pos.x, pos.y, width, height);
		sr.end();
	}
	
	public Agent copy() {
		return new Agent(this);
	}

}
