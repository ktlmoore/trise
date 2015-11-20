package com.tlear.trise.objects;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.collision.Segment;

public class EnvObject {
	
	public Rectangle rect;
	
	public Vector2 pos;
	
	public float width;
	public float height;
	
	public EnvObject(float x, float y, float width, float height) {
		
		pos = new Vector2(x, y);
		
		this.width = width;
		this.height = height;
		
		rect = new Rectangle(x, y, width, height);
	}
	
	/**
	 * Tests whether a point is within or on the object and returns true if it is.
	 * @param p
	 * @return
	 */
	public boolean containsPoint(Vector2 p) {
		if (p.x < pos.x || p.x > pos.x + width) {
			return false;
		}
		if (p.y < pos.y || p.y > pos.y + height) {
			return false;
		}
		return true;
	}
	
	/**
	 * Tests whether a point is within the object and returns true if it is.
	 * @param p
	 * @return
	 */
	public boolean strictlyContainsPoint(Vector2 p) {
		if (p.x <= pos.x || p.x >= pos.x + width) {
			return false;
		}
		if (p.y <= pos.y || p.y >= pos.y + height) {
			return false;
		}
		return true;
	}
	
	public boolean intersectsLine(Vector2 a, Vector2 b) {
		// If either point is in the obstacle then there is an intersection
		if (strictlyContainsPoint(a) || strictlyContainsPoint(b)) {
			return true;
		}
		
		// Test the line segment against the four lines
		if (Intersector.intersectSegments(a, b, pos.cpy(), pos.cpy().add(width, 0), new Vector2())) {
			return true;
		}
		if (Intersector.intersectSegments(a, b, pos.cpy().add(width, 0), pos.cpy().add(width, height), new Vector2())) {
			return true;
		}
		if (Intersector.intersectSegments(a, b, pos.cpy().add(width, height), pos.cpy().add(0, height), new Vector2())) {
			return true;
		}
		if (Intersector.intersectSegments(a, b, pos.cpy().add(0, height), pos.cpy(), new Vector2())) {
			return true;
		}
	
		return false;
	}
}
