package com.tlear.trise.functions;

import java.util.function.Function;

import com.badlogic.gdx.math.Vector2;
import com.tlear.trise.environment.Environment;
import com.tlear.trise.functions.skeletonisation.ProbabilisticRoadMap;
import com.tlear.trise.graph.Graph;
import com.tlear.trise.graph.TrackedGraph;
import com.tlear.trise.interactions.Action;
import com.tlear.trise.interactions.MoveToAction;
import com.tlear.trise.utils.Tuple;

public interface DecisionFunction extends Function<Environment, Tuple<Action, TrackedGraph<Vector2>>> {
	
	

	@Override
	public Tuple<Action, TrackedGraph<Vector2>> apply(Environment t);
}
