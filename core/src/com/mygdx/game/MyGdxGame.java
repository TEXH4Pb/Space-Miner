package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.codeandweb.physicseditor.PhysicsShapeCache;

public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture space;
	private OrthographicCamera camera;
	int screenWidth, screenHeight;
	PhysicsShapeCache shapes;

	@Override
	public void create () {
		batch = new SpriteBatch();
		space = new Texture("sprites/background/blue.png");
		camera = new OrthographicCamera();
		shapes = new PhysicsShapeCache("physics.xml");
		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();
		camera.setToOrtho(false, screenWidth, screenHeight);
	}

	@Override
	public void render () {
		ScreenUtils.clear(1, 0, 0, 1);
		batch.setProjectionMatrix(camera.combined);
		camera.update();
		batch.begin();
		//filling background with tiles
		for(int x = 0; x< screenWidth; x+=space.getWidth())
			for(int y = 0; y< screenHeight; y+=space.getHeight())
				batch.draw(space, x, y);
		batch.end();
	}
	@Override
	public void resize(int width, int height) {
		screenWidth = width;
		screenHeight = height;
		camera.setToOrtho(false, screenWidth, screenHeight);
	}

	@Override
	public void dispose () {
		batch.dispose();
		space.dispose();
	}
}