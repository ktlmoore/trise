package com.tlear.trise.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

public class StaticGoal extends StaticObject {

	public StaticGoal(float x, float y, float width, float height) {
		super(x, y, width, height);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean containsPoint(Vector2 p) {
		return ((pos.x - p.x) * (pos.x - p.x) + (pos.y - p.y) * (pos.y - p.y)) < width * width;
	}

	public void draw(ShapeRenderer sr, SpriteBatch batch) {
		sr.begin(ShapeType.Filled);
		sr.setColor(1, 1, 0, 1);
		sr.circle(pos.x, pos.y, width);
		sr.end();

		sr.begin(ShapeType.Line);
		sr.setColor(selected ? 1 : 0, 1, selected ? 0 : 1, 1);
		sr.circle(pos.x, pos.y, width + height);
		sr.end();
	}

}
