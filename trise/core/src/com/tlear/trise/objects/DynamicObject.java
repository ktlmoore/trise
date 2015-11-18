package com.tlear.trise.objects;

import java.util.LinkedList;

import com.badlogic.gdx.math.Vector2;

public class DynamicObject extends EnvObject {

	public LinkedList<Vector2> posKeyframes;
	
	public DynamicObject(float x, float y, float width, float height) {
		super(x, y, width, height);
		
		/*
		 * We make the first keyframe be the initial position
		 */
		posKeyframes = new LinkedList<Vector2>();
		posKeyframes.add(new Vector2(pos));
	}
	
}
