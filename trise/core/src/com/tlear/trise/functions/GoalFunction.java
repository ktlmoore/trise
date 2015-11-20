package com.tlear.trise.functions;

import java.util.function.Function;

import com.tlear.trise.environment.Environment;
import com.tlear.trise.objects.StaticGoal;

public class GoalFunction implements Function<Environment, Float> {

	@Override
	public Float apply(Environment t) {
		float result = 0;
		for (StaticGoal g : t.goals) {
			if (t.agents.getFirst().pos.equals(g.pos)) {
				result = 1;
			}
		}
		return result;
	}

}
