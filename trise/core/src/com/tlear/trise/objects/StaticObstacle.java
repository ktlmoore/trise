package com.tlear.trise.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class StaticObstacle extends StaticObject {

	public StaticObstacle(float x, float y, float width, float height) {
		super(x, y, width, height);
		// TODO Auto-generated constructor stub
	}
	
	public StaticObstacle(StaticObstacle that) {
		super(that.pos.x, that.pos.y, that.width, that.height);
	}
	
	public void draw(ShapeRenderer sr, SpriteBatch batch) {
		sr.begin(ShapeType.Filled);
		sr.setColor(0, 0, 0, 1);
		sr.rect(pos.x, pos.y, width, height);
		sr.end();
		sr.begin(ShapeType.Line);
		sr.setColor(1, 1, 1, 1);
		sr.rect(pos.x, pos.y, width, height);
		sr.end();
	}

}
