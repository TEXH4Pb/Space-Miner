package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;

public class MyGdxGame extends ApplicationAdapter {
	static final float STEP_TIME = 1f / 60f;
	static final int VELOCITY_ITERATIONS = 6;
	static final int POSITION_ITERATIONS = 2;
	static final int ASTEROID_COUNT = 20;
	static final float SCALE = 0.02f;

	SpriteBatch batch;
	Sprite space;
	private OrthographicCamera camera;
	int screenWidth, screenHeight;
	World world;
	Ship player;
	ArrayList<Entity> entities;
	Box2DDebugRenderer debugRenderer;

	@Override
	public void create () {
		Box2D.init();

		screenWidth = (int) (Gdx.graphics.getWidth() * SCALE);
		screenHeight = (int) (Gdx.graphics.getHeight() * SCALE);
		camera = new OrthographicCamera();
		camera.setToOrtho(false, screenWidth, screenHeight);

		batch = new SpriteBatch();
		space = new Sprite(new Texture("sprites/background/blue.png"));
		space.setSize(space.getWidth() * SCALE, space.getHeight() * SCALE);

		world = new World(new Vector2(0,0), true);
		player = new Ship(world, screenWidth/2, screenHeight/2);
		entities = new ArrayList<Entity>();
		entities.add(player);
		for(float i = 0, x, y; i < ASTEROID_COUNT; ++i)
		{
			do {
				x = screenWidth * (float)Math.random();
				y = screenHeight * (float)Math.random();
			}
			while (player.getPosition().dst(x,y)<5);
			entities.add(new Asteroid(world, x, y));
		}
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
		for(float x = 0; x< screenWidth; x+=space.getWidth()){
			for(float y = 0; y< screenHeight; y+=space.getHeight()){
				space.setPosition(x,y);
				space.draw(batch);
			}
		}
		//rendering all entities
		//player.draw(batch);
		for (Entity e: entities) {
			e.draw(batch);
		}
		batch.end();

		debugRenderer.render(world, camera.combined);
	}
	@Override
	public void resize(int width, int height) {
		screenWidth = (int) (width * SCALE);
		screenHeight = (int) (height * SCALE);
		camera.setToOrtho(false, screenWidth, screenHeight);
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

		Vector2 newPos;
		for (Entity e : entities) {
			if (e.getPosition().x > screenWidth) {
				newPos = e.getPosition();
				newPos.x = 0;
				e.body.setTransform(newPos, e.body.getAngle());
			}
			else if (e.getPosition().x < 0){
			newPos = e.getPosition();
			newPos.x = screenWidth;
			e.body.setTransform(newPos, e.body.getAngle());
			}

			if(e.getPosition().y > screenHeight) {
				newPos = e.getPosition();
				newPos.y = 0;
				e.body.setTransform(newPos, e.body.getAngle());
			}
			else if(e.getPosition().y < 0){
				newPos = e.getPosition();
				newPos.y = screenHeight;
				e.body.setTransform(newPos, e.body.getAngle());
			}
		}

		player.controlHandling(getMouseVector());
	}
	private Vector3 getMouseVector(){
		Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
		camera.unproject(mouse);
		return mouse;
	}
}