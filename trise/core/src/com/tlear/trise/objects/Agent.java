package com.tlear.trise.objects;

import java.util.LinkedList;
import java.util.Set;

import com.tlear.trise.environment.Environment;

public class Agent extends DynamicObject {
	
	private LinkedList<Agent> beliefKeyframes;
	private LinkedList<Agent> actualKeyframes;
	
	private Set<Sensor> sensors;
	private Set<Actuator> actuators;
	private ResultFunction result;
	private GoalFunction goal;
	private DecisionFunction decide;
	private Environment belief;

	public Agent(float x, float y, float width, float height) {
		super(x, y, width, height);
		// TODO Auto-generated constructor stub
		
		/*
		 * Add the initial state to the start of our list of keyframes
		 */
		beliefKeyframes = new LinkedList<Agent>();
		actualKeyframes = new LinkedList<Agent>();
		beliefKeyframes.add(this);
		actualKeyframes.add(this);
	}

}
