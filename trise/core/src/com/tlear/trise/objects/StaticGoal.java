package com.tlear.trise.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class StaticGoal extends StaticObject {

	public StaticGoal(float x, float y, float width, float height) {
		super(x, y, width, height);
		// TODO Auto-generated constructor stub
	}

	public void draw(ShapeRenderer sr, SpriteBatch batch) {
		sr.begin(ShapeType.Line);
		sr.setColor(selected ? 1 : 0, 1, selected ? 0 : 1, 1);
		sr.circle(pos.x, pos.y, width + height);
		sr.end();
	}

}
