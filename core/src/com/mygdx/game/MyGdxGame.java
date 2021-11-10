package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;

import javax.swing.*;
import java.util.ArrayList;

public class MyGdxGame extends ApplicationAdapter {
	static final float STEP_TIME = 1f / 60f;
	static final int VELOCITY_ITERATIONS = 6;
	static final int POSITION_ITERATIONS = 2;
	static final int ASTEROID_COUNT = 20;
	static final float SCALE = 0.02f;

	SpriteBatch batch;
	Sprite space;
	FreeTypeFontGenerator fontGenerator;
	FreeTypeFontGenerator.FreeTypeFontParameter fontParameter;
	BitmapFont font;
	OrthographicCamera camera, uiCam;
	int screenWidth, screenHeight;
	float worldWidth, worldHeight;
	World world;
	Ship player;
	ArrayList<Entity> entities;
	Box2DDebugRenderer debugRenderer;
	boolean debugMode = false;

	@Override
	public void create () {
		Box2D.init();

		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();
		worldWidth = (int) (Gdx.graphics.getWidth() * SCALE);
		worldHeight = (int) (Gdx.graphics.getHeight() * SCALE);
		camera = new OrthographicCamera();
		camera.setToOrtho(false, worldWidth, worldHeight);
		uiCam = new OrthographicCamera();
		uiCam.setToOrtho(false, screenWidth, screenHeight);

		batch = new SpriteBatch();
		space = new Sprite(new Texture("sprites/background/blue.png"));
		space.setSize(space.getWidth() * SCALE, space.getHeight() * SCALE);

		fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/kenvector_future.ttf"));
		fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		fontParameter.size = 20;
		font = fontGenerator.generateFont(fontParameter);

		world = new World(new Vector2(0,0), true);
		entities = new ArrayList<Entity>();

		restartGame(true);

		debugRenderer = new Box2DDebugRenderer();
	}

	@Override
	public void render () {
		ScreenUtils.clear(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);

		updateGame();

		batch.begin();
		//filling background with tiles
		for(float x = 0; x< worldWidth; x+=space.getWidth()){
			for(float y = 0; y< worldHeight; y+=space.getHeight()){
				space.setPosition(x,y);
				space.draw(batch);
			}
		}
		//rendering all entities
		for (Entity e: entities) {
			e.draw(batch);
		}
		batch.end();

		if(debugMode)
			debugRenderer.render(world, camera.combined);

		//UI render
		batch.setProjectionMatrix(uiCam.combined);
		batch.begin();
		font.draw(batch, "LIFES: " + player.getLifes(), 0 + screenWidth * 0.02f, screenHeight * 0.95f);
		font.draw(batch, "SCORE: " + player.getScore(), 0 + screenWidth - 200, screenHeight * 0.95f);
		if(debugMode)
			font.draw(batch, "FPS " + Gdx.graphics.getFramesPerSecond(), 0 + screenWidth * 0.05f, screenHeight * 0.05f);
		batch.end();
	}
	@Override
	public void resize(int width, int height) {
		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();
		worldWidth = width * SCALE;
		worldHeight = height * SCALE;
		camera.setToOrtho(false, worldWidth, worldHeight);
		uiCam.setToOrtho(false, screenWidth, screenHeight);
	}
	@Override
	public void dispose () {
		batch.dispose();
		world.dispose();
		debugRenderer.dispose();
	}
	//all non-render logic
	private void updateGame()
	{
		world.step(STEP_TIME, VELOCITY_ITERATIONS, POSITION_ITERATIONS);

		//border handling
		Vector2 newPos;
		for (Entity e : entities) {
			if (e.getPosition().x > worldWidth) {
				newPos = e.getPosition();
				newPos.x = 0;
				e.body.setTransform(newPos, e.body.getAngle());
			} else if (e.getPosition().x < 0) {
				newPos = e.getPosition();
				newPos.x = worldWidth;
				e.body.setTransform(newPos, e.body.getAngle());
			}

			if (e.getPosition().y > worldHeight) {
				newPos = e.getPosition();
				newPos.y = 0;
				e.body.setTransform(newPos, e.body.getAngle());
			} else if (e.getPosition().y < 0) {
				newPos = e.getPosition();
				newPos.y = worldHeight;
				e.body.setTransform(newPos, e.body.getAngle());
			}
		}

		systemKeysHandling();//mostly for debug purposes
		player.controlHandling(getMouseVector());
	}
	private Vector3 getMouseVector(){
		Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
		camera.unproject(mouse);
		return mouse;
	}

	private void systemKeysHandling()
	{
		if(Gdx.input.isKeyJustPressed(Input.Keys.F5))
			restartGame(false);
		if(Gdx.input.isKeyJustPressed(Input.Keys.F6))
			entities.add(spawnNewAsteroid());
		if(Gdx.input.isKeyJustPressed(Input.Keys.F7))
			debugMode = !debugMode;
		if(Gdx.input.isKeyJustPressed(Input.Keys.F11)) {
			if(!Gdx.graphics.isFullscreen()) {
				Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
			} else {
				Gdx.graphics.setWindowedMode(1024, 768);
			}
		}
	}

	private void restartGame(boolean start) {
		if(!start) {
		for(Entity e : entities) {
			world.destroyBody(e.body);
		}
		entities.clear();
		Gdx.graphics.setWindowedMode(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		JOptionPane.showMessageDialog(null, "Game over! Your score is: " + player.getScore());
		Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
		}
		player = new Ship(world, worldWidth /2, worldHeight /2);
		entities.add(player);
		for(int i = 0; i < ASTEROID_COUNT; ++i)
		{
			entities.add(spawnNewAsteroid());
		}
	}
	private Asteroid spawnNewAsteroid(){
		float x, y;
		do {
			x = worldWidth * (float)Math.random();
			y = worldHeight * (float)Math.random();
		}
		while (player.getPosition().dst(x,y)<5);
		return new Asteroid(world, x, y);
	}
}