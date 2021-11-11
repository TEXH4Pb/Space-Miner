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

import java.util.ArrayList;
import java.util.Iterator;

public class MyGdxGame extends ApplicationAdapter {
	static final float STEP_TIME = 1f / 60f;
	static final int VELOCITY_ITERATIONS = 6;
	static final int POSITION_ITERATIONS = 2;
	static final int ASTEROID_COUNT = 15;
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
	BoxListener contactListener;
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
		fontParameter.size = 24;
		font = fontGenerator.generateFont(fontParameter);

		contactListener = new BoxListener();
		world = new World(new Vector2(0,0), true);
		world.setContactListener(contactListener);
		entities = new ArrayList<>();

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
		if(player.getLifes()>0){
			for (Entity e: entities) {
				e.draw(batch);
			}
			player.draw(batch);
		}
		batch.end();

		if(debugMode)
			debugRenderer.render(world, camera.combined);

		//UI render
		batch.setProjectionMatrix(uiCam.combined);
		batch.begin();
		font.draw(batch, "SCORE: " + player.getScore(), 0 + screenWidth * 0.02f, screenHeight * 0.95f);
		font.draw(batch, "LIFES: " + player.getLifes(), screenWidth - 150, screenHeight * 0.95f);
		if(debugMode)
			font.draw(batch, "FPS " + Gdx.graphics.getFramesPerSecond(), 0 + screenWidth * 0.05f, screenHeight * 0.05f);
		if(player.getLifes() <= 0)
			font.draw(batch, "GAME OVER!", screenWidth * 0.5f - 70, screenHeight * 0.5f);
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
		systemKeysHandling();//mostly for debug purposes
		if(player.getLifes() <= 0)
			return;

		world.step(STEP_TIME, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
		player.update();
		checkBorders(player);

		int asteroidCount = 0;
		Iterator<Entity> iterator = entities.iterator();
		while (iterator.hasNext()) {
			Entity e = iterator.next();
			e.update();
			if(e.queuedForRemoval) {
				world.destroyBody(e.body);
				if (e instanceof Asteroid)
					asteroidCount++;
				iterator.remove();
				continue;
			}
			//border handling
			checkBorders(e);
		}
		for(; asteroidCount>0; asteroidCount--)
			entities.add(spawnNewAsteroid());
		if(!player.isStunned())
			controlsHandling();
	}

	private void checkBorders(Entity e) {
		Vector2 newPos;
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

	private Vector3 getMouseVector(){
		Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
		camera.unproject(mouse);
		return mouse;
	}

	void controlsHandling() {
		//rotating ship to mouse cursor
		Vector3 pos = new Vector3(player.getPosition().x, player.getPosition().y, 0);
		pos.sub(getMouseVector());
		Vector2 direction = new Vector2(0,0);
		direction.set(pos.x, pos.y);
		direction.rotate90(0);
		player.body.setTransform(player.getPosition(), direction.angleRad());

		if(Gdx.input.isKeyPressed(Input.Keys.W))
			player.accelerate();
		if(Gdx.input.justTouched() && player.canShoot())
			entities.add(player.shoot());
		if(Gdx.input.isKeyPressed(Input.Keys.A) && !Gdx.input.isKeyPressed(Input.Keys.D))
			player.strafeLeft();
		else if(!Gdx.input.isKeyPressed(Input.Keys.A) && Gdx.input.isKeyPressed(Input.Keys.D))
			player.strafeRight();
	}

	private void systemKeysHandling() {
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
		if (!start) {
			for (Entity e : entities) {
				world.destroyBody(e.body);
			}
			entities.clear();
			world.destroyBody(player.body);
		}
		player = new Ship(world, worldWidth / 2, worldHeight / 2);
		for (int i = 0; i < ASTEROID_COUNT; ++i) {
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