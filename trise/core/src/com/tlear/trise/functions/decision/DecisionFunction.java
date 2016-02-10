package com.tlear.trise.functions.decision;

import java.util.function.Function;

import com.badlogic.gdx.math.Vector2;
import com.tlear.trise.environment.Environment;
import com.tlear.trise.graph.TrackedGraph;
import com.tlear.trise.interactions.Action;
import com.tlear.trise.metrics.MutableMetrics;
import com.tlear.trise.utils.Tuple;

public interface DecisionFunction extends Function<Environment, Tuple<MutableMetrics, Tuple<Action, TrackedGraph<Vector2>>>> {
	
	

	@Override
	public Tuple<MutableMetrics, Tuple<Action, TrackedGraph<Vector2>>> apply(Environment t);
	
	public String getName();
}
 