package com.tlear.trise.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.tlear.trise.TRISE;

public class EditorScreen implements Screen {
	
	final TRISE trise;
	
	private Stage stage;
	private Table table;
	private Skin skin;
	private VerticalGroup leftSidebar, rightSidebar;
	
	public EditorScreen(final TRISE trise) {
		this.trise = trise;
		
		// Sets up for UI
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		
		table = new Table();
		table.setFillParent(true);
		stage.addActor(table);
		
		table.setDebug(true);
		
		skin = new Skin(Gdx.files.internal("uiskin.json"));
		
		// Set up Editor
		rightSidebar = new VerticalGroup();
		leftSidebar = new VerticalGroup();
		
		Label editorTitle = new Label("Edit", skin);
		rightSidebar.addActor(editorTitle);
		TextButton newObstacleBtn = new TextButton("Add obstacle", skin);
		TextButton newAgentBtn = new TextButton("Add agent", skin);
		rightSidebar.addActor(newObstacleBtn);
		rightSidebar.addActor(newAgentBtn);
		
		Label propertiesTitle = new Label("Properties", skin);
		leftSidebar.addActor(propertiesTitle);
		
		table.setSkin(skin);
		table.add(leftSidebar).size(100);
		table.add("main view goes here").expandX().expandY();
		table.add(rightSidebar).size(100);
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(float delta) {
		
		stage.act(Gdx.graphics.getDeltaTime());
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		trise.batch.begin();
		stage.draw();
		trise.batch.end();

	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		stage.dispose();

	}

}
