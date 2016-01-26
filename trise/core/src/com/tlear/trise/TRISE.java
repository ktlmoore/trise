package com.tlear.trise;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.tlear.trise.objects.EnvObject;

public class TRISE extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	ShapeRenderer sr;
	Simulation sim;

	public boolean modeEdit = false;
	public boolean modeSim = false;
	public boolean showGraph = false;

	public boolean modeNewObject = false;

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

	private void reset() {
		sim = new Simulation(this);
		d = 0;
		frames = 1;
	}

	private void checkForInput() {
		if (Gdx.input.isKeyJustPressed(Keys.E)) {
			modeEdit = !modeEdit;
			modeSim = false;
		}
		if (Gdx.input.isKeyJustPressed(Keys.S)) {
			modeSim = !modeSim;
		}
		if (Gdx.input.isKeyJustPressed(Keys.V)) {
			showGraph = !showGraph;
		}
		if (Gdx.input.isKeyJustPressed(Keys.R)) {
			reset();
		}
		if (modeEdit) {
			// Deal with mouse presses
			if (Gdx.input.justTouched()) {

				// First get the touch position
				Vector2 touchPosition = new Vector2(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());

				// Then work out what it was that we just touched
				EnvObject obj = sim.getObjectContainingPoint(touchPosition);
				if (obj != null) {
					System.out.println(obj.toString());
					sim.selectEnvObject(obj);
				}
			}

			// For adding new objects
			if (Gdx.input.isKeyJustPressed(Keys.N)) {
				modeNewObject = !modeNewObject;
			}

			if (modeNewObject) {
				// Add an obstacle
				if (Gdx.input.isKeyJustPressed(Keys.O)) {
					sim.addObject(false, true, false);
				}

				// Add a goal
				if (Gdx.input.isKeyJustPressed(Keys.G)) {
					sim.addObject(false, false, true);
				}
			}

			// For dealing with objects when they've been selected
			if (sim.selectedObject != null) {

				if (Gdx.input.isTouched()) {
					Vector2 touchPosition = new Vector2(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());

					if (sim.selectedObject.containsPoint(touchPosition)) {

						sim.selectedObject.pos.x = touchPosition.x - sim.selectedObject.width / 2;
						sim.selectedObject.pos.y = touchPosition.y - sim.selectedObject.height / 2;

						sim.dirtyEnvironment();
					}
				}

				if (Gdx.input.isKeyJustPressed(Keys.DEL) || Gdx.input.isKeyJustPressed(Keys.BACKSPACE)) {
					sim.deleteObject(sim.selectedObject);
				}
			}
		}
	}
}
