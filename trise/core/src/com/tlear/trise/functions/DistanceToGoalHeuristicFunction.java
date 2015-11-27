package com.tlear.trise.functions;

import com.badlogic.gdx.math.Vector2;
import com.tlear.trise.environment.Environment;
import com.tlear.trise.graph.Node;

public class DistanceToGoalHeuristicFunction implements HeuristicFunction {

	private Vector2 goal;
	private int maxX, maxY;
	private int maxLen;
	
	public DistanceToGoalHeuristicFunction(Environment env) {
		/*
		 * Assert that there is at least one goal
		 */
		if (env.goals.size() <= 0) {
			throw new RuntimeException("Must be at least one goal to make a valid distance to goal heuristic");
		}
		this.goal = env.goals.getFirst().pos.cpy();
		this.maxX = env.maxX;
		this.maxY = env.maxY;
		maxLen = (int) Math.sqrt(maxX*maxX + maxY*maxY);
	}

	@Override
	public Float apply(Node<Vector2> t) {
		Vector2 d = goal.cpy().sub(t.getValue().cpy());
		return d.len();
	}
}
