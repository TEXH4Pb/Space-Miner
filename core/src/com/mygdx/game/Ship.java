package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;

public class Ship extends Entity{
    private int lifes;
    private int score;
    private int immuneTimer;
    private static final float FORWARD_VELOCITY = 0.7f;
    private static final float STRAFE_VELOCITY = 0.3f;

    Ship(World world, float x, float y)
    {
        lifes = 3;
        score = 0;
        immuneTimer = 0;

        body = SHAPES.createBody("playerShip", world, SCALE, SCALE);
        body.setFixedRotation(true);
        body.setUserData(this);
        body.setTransform(x, y, 0);

        sprite = new Sprite(new Texture("sprites/playerShip3_orange.png"));
        sprite.setSize(sprite.getWidth() * SCALE, sprite.getHeight() * SCALE);
    }

    @Override
    public void collideWith(Entity target) {
        if(target instanceof Asteroid) {
            if(immuneTimer == 0) {
                lifes--;
                immuneTimer = 180;
                sprite.setAlpha(0.4f);
                target.queuedForRemoval = true;
            }
        }
    }

    public void controlHandling(Vector3 mousePos) {
        //rotating ship to mouse cursor
        Vector3 pos = new Vector3(body.getPosition().x, body.getPosition().y, 0);
        pos.sub(mousePos);
        Vector2 direction = new Vector2(0,0);
        direction.set(pos.x, pos.y);
        direction.rotate90(0);
        body.setTransform(body.getPosition(), direction.angleRad());
        direction.rotate90(0);

        if(Gdx.input.isKeyPressed(Input.Keys.W)) {  //fly towards cursor
            direction.setLength(FORWARD_VELOCITY);
            body.applyLinearImpulse(direction, body.getPosition(), true);
        }
        if(Gdx.input.justTouched()) {
            //TODO: Laser shot
        }
        if(Gdx.input.isKeyPressed(Input.Keys.A) && !Gdx.input.isKeyPressed(Input.Keys.D)) { //strafe to the left
            direction.setLength(STRAFE_VELOCITY);
            direction.rotateDeg(90);
            body.applyLinearImpulse(direction, body.getPosition(), true);
        }
        else if(!Gdx.input.isKeyPressed(Input.Keys.A) && Gdx.input.isKeyPressed(Input.Keys.D)) {//strafe to the right
            direction.setLength(STRAFE_VELOCITY);
            direction.rotateDeg(-90);
            body.applyLinearImpulse(direction, body.getPosition(), true);
        }
    }

    public int getLifes() {
        return lifes;
    }

    public int getScore() {
        return score;
    }

    public boolean isImmune(){
        return immuneTimer > 0;
    }

    @Override
    public void update(){
        if (immuneTimer == 0)
            return;
        immuneTimer--;
        if (immuneTimer == 0) //just become zero
            sprite.setAlpha(1);
    }

    //Ship's body origin is in the center, so I need to modify sprite render
    void draw(SpriteBatch batch) {
        if(body == null)
            return;
        Vector2 pos = body.getPosition();
        sprite.setOrigin(sprite.getWidth()/2, sprite.getHeight()/2);
        sprite.setPosition(pos.x - sprite.getOriginX(), pos.y - sprite.getOriginY());
        sprite.setRotation((float) Math.toDegrees(body.getAngle()));
        sprite.draw(batch);
    }
}
