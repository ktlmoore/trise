package com.tlear.trise;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class TRISE extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	ShapeRenderer sr;
	Simulation sim;

	public boolean modeEdit = false;
	public boolean modeSim = false;
	public boolean showGraph = false;

	int d;
	int frames;

	@Override
	public void create() {
		d = 0;
		frames = 1;

		sim = new Simulation(this);

		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		sr = new ShapeRenderer();
	}

	@Override
	public void render() {
		update();
		checkForInput();

		Gdx.gl.glClearColor(0.5f, 0.4f, 0.7f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		sim.draw(sr, batch);

	}

	private void update() {
		d++;
		if (modeSim && d % frames == 0) {
			sim.update();
			d = 0;
		}
	}

	private void checkForInput() {
		if (Gdx.input.isKeyJustPressed(Keys.E)) {
			modeEdit = !modeEdit;
		}
		if (Gdx.input.isKeyJustPressed(Keys.S)) {
			modeSim = !modeSim;
		}
		if (Gdx.input.isKeyJustPressed(Keys.G)) {
			showGraph = !showGraph;
		}
	}
}
