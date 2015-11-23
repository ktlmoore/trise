package com.tlear.trise.functions;

import java.util.function.BiFunction;
import java.util.function.Function;

import com.badlogic.gdx.math.Vector2;
import com.tlear.trise.environment.Environment;
import com.tlear.trise.graph.Node;
import com.tlear.trise.objects.StaticGoal;

public class GoalFunction implements BiFunction<Environment, Node<Vector2>, Boolean> {

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
