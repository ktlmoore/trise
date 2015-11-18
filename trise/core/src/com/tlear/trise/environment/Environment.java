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
}
