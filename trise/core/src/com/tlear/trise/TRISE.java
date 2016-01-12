package com.tlear.trise;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.tlear.trise.screens.EditorScreen;

public class TRISE extends Game {
	public SpriteBatch batch;
	Texture img;
	public ShapeRenderer sr;
	Simulation sim;
	public BitmapFont font;
	
	int d;
	int frames;
	
	@Override
	public void create () {
		d = 0;
		frames = 1;
//		sim = new Simulation();
		batch = new SpriteBatch();
//		img = new Texture("badlogic.jpg");
		sr = new ShapeRenderer();
		font = new BitmapFont();
		
		this.setScreen(new EditorScreen(this));
	}
	
	@Override
	public void dispose() {
		batch.dispose();
		sr.dispose();
		font.dispose();
	}

	@Override
	public void render () {
		
		super.render();
//		Gdx.gl.glClearColor(1, 0.4f, 0.7f, 1);
//		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//		
//		d++;
//		
//		if (d % frames == 0) {
//			sim.update();
//			d = 0;
//		}
//		
//		sim.draw(sr, batch);
//		
	}
}
