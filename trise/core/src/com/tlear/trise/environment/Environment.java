package com.tlear.trise.environment;

import java.util.LinkedList;

import com.tlear.trise.objects.Agent;
import com.tlear.trise.objects.StaticGoal;
import com.tlear.trise.objects.StaticObstacle;

public class Environment {
	public final int maxX;
	public final int maxY;

	public LinkedList<Agent> agents;
	public LinkedList<StaticObstacle> obstacles;
	public LinkedList<StaticGoal> goals;
	
	public Environment(int maxX, int maxY) {
		this.maxX = maxX;
		this.maxY = maxY;
	}
}
