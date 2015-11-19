package com.tlear.trise.interactions;

import com.badlogic.gdx.math.Vector2;

public class MoveToAction implements Action {

	public final Vector2 origin;
	public final Vector2 destination;
	private final Vector2 motion;
	public final float length; 
	
	public MoveToAction(Vector2 from, Vector2 to) {
		origin = from.cpy();
		destination = to.cpy();
		motion = destination.cpy().sub(origin);
		length = motion.len();
	}
}
