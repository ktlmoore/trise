package com.tlear.trise.functions;

import com.badlogic.gdx.math.Vector2;
import com.tlear.trise.environment.Environment;
import com.tlear.trise.graph.Node;
import com.tlear.trise.objects.StaticGoal;

public class BooleanIsGoalFunction implements GoalFunction {

	@Override
	public Boolean apply(Environment env, Node<Vector2> n) {
		boolean result = false;
		for (StaticGoal g : env.goals) {
			if (n.getValue().equals(g.pos)) {
				result = true;
			}
		}
		return result;
	}

}
