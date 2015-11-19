package com.tlear.trise.functions;

import java.util.function.Function;

import com.badlogic.gdx.math.Vector2;
import com.tlear.trise.environment.Environment;
import com.tlear.trise.interactions.Action;
import com.tlear.trise.interactions.MoveToAction;

public class DecisionFunction implements Function<Environment, Action> {

	@Override
	public Action apply(Environment t) {
		/*
		 * TODO FIXME
		 */
		return new MoveToAction(t.agents.get(0).pos, new Vector2(100, 100));
	}
	
}
