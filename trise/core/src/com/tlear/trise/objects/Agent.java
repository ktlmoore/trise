package com.tlear.trise.objects;

import java.util.LinkedList;

public class Agent extends DynamicObject {
	
	public LinkedList<Agent> stateKeyframes;

	public Agent(float x, float y, float width, float height) {
		super(x, y, width, height);
		// TODO Auto-generated constructor stub
		
		/*
		 * Add the initial state to the start of our list of keyframes
		 */
		stateKeyframes = new LinkedList<Agent>();
		stateKeyframes.add(this);
	}

}
