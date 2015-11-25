package com.tlear.trise.functions;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.math.Vector2;
import com.tlear.trise.environment.Environment;
import com.tlear.trise.graph.Node;
import com.tlear.trise.objects.StaticGoal;

public class MultipleGoalsGoalFunction implements GoalFunction {

	private Map<StaticGoal, Boolean> visitedGoals;
	private boolean initialised;
	
	public MultipleGoalsGoalFunction() {
		super();
		initialised = false;
	}
	
	@Override
	public Boolean apply(Environment env, Node<Vector2> n) {
		if (!initialised) {
			visitedGoals = new HashMap<StaticGoal, Boolean>();
			for (StaticGoal g : env.goals) {
				visitedGoals.put(g,  false);
			}
			initialised = true;
		}
	
		boolean result = false;
		for (StaticGoal g : env.goals) {
			if (n.getValue().equals(g.pos) && !visitedGoals.get(g)) {
				visitedGoals.replace(g, true);
				result = true;
				boolean allTrue = true;
				for (StaticGoal h : env.goals) {
					if (!visitedGoals.get(h)) {
						allTrue = false;
					}
				}
				if (allTrue) {
					initialised = false;
				}
			}
		}
		return result;
	}

}
