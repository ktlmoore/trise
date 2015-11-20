package com.tlear.trise.functions;

import java.util.function.Function;

import com.badlogic.gdx.math.Vector2;
import com.tlear.trise.environment.Environment;
import com.tlear.trise.interactions.Action;
import com.tlear.trise.interactions.MoveToAction;

public class DecisionFunction implements Function<Environment, Action> {
	
	private int i = 0;

	@Override
	public Action apply(Environment t) {
		/*
		 * TODO FIXME
		 */
//		System.out.println("Applying Decision Function: " + t);
		
		
		Vector2 p = t.agents.getFirst().pos.cpy();
		System.out.println(t);
		Vector2 q = new Vector2();
		
		switch (i % 4) {
		case 0:
			q = p.cpy().add(new Vector2(100, 0));
			break;
		case 1:
			q = p.cpy().add(new Vector2(0, 100));
			break;
		case 2:
			q = p.cpy().add(new Vector2(0, -100));
			break;
		case 3:
			q = p.cpy().add(new Vector2(-100, 0));
			break;
		default:
			q = p.cpy().add(new Vector2(100, 100));
		}
		
		
		if (q.x < 5) {
			q.set(5, q.y);
		} else if (q.x > t.maxX-5) {
			q.set(t.maxX-5, q.y);
		}
		
		if (q.y < 5) {
			q.set(q.x, 5);
		} else if (q.y > t.maxY-5) {
			q.set(q.x, t.maxY-5);
		}
		i++;
		
		return new MoveToAction(p, q);
	}
	
}
