package com.tlear.trise.objects;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class EnvObject {
	
	public Rectangle rect;
	
	public Vector2 pos;
	
	public final float width;
	public final float height;
	
	public EnvObject(float x, float y, float width, float height) {
		
		pos = new Vector2(x, y);
		
		this.width = width;
		this.height = height;
		
		rect = new Rectangle(x, y, width, height);
	}
}
