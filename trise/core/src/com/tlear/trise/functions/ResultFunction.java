package com.tlear.trise.functions;

import java.util.function.BiFunction;

import com.badlogic.gdx.math.Vector2;
import com.tlear.trise.environment.Environment;
import com.tlear.trise.interactions.Action;
import com.tlear.trise.interactions.MoveToAction;
import com.tlear.trise.utils.Tuple;

public class ResultFunction implements BiFunction<Action, Environment, Tuple<Environment, Integer>> {

	@Override
	public Tuple<Environment, Integer> apply(Action t, Environment u) {
		if (t instanceof MoveToAction) {
			return resolve((MoveToAction) t, u);
		}
		return null;
	}

	public Tuple<Environment, Integer> resolve(MoveToAction act, Environment env) {
		
//		System.out.println("RESOLVING ACTION");
		Environment newEnv = new Environment(env);
		newEnv.agents.get(0).pos = new Vector2(act.destination);
		
//		System.out.println("ACTION LENGTH: " + act.length);
		int t = (int) (act.length / env.agents.get(0).getSpeed());
		
		return new Tuple<Environment, Integer>(newEnv, Math.max(t, 1));
	}
	
}
