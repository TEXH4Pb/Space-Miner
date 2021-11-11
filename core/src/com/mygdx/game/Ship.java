package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class Ship extends Entity{
    private int lifes;
    private int score;
    private int immuneTimer;//ship is immune for a few seconds after getting damage
    private int cooldownTimer;//cooldown between laser shots
    private int stunTimer;//if the ship is hit by laser, it becomes stunned for a while
    private float alphaOffset;//summarize effects
    private static final float FORWARD_ACCELERATION = 0.5f;
    private static final float STRAFE_ACCELERATION = 0.3f;
    private static final int SHOT_COOLDOWN = 20;

    Ship(World world, float x, float y)
    {
        lifes = 3;
        score = 0;
        immuneTimer = 0;
        alphaOffset = 0;

        body = SHAPES.createBody("playerShip", world, SCALE, SCALE);
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
                alphaOffset += 0.5f;
                target.queuedForRemoval = true;
            }
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
        if (immuneTimer > 0){
            immuneTimer--;
            if (immuneTimer == 0) //just become zero
                alphaOffset -= 0.5f;
        }
        if(stunTimer > 0){
            stunTimer--;
            if(stunTimer == 0) {
                body.setFixedRotation(true);
                alphaOffset -= 0.2f;
            }
        }
        if(cooldownTimer > 0)
            cooldownTimer--;
    }

    //Ship's body origin is in the center, so I need to modify sprite render
    void draw(SpriteBatch batch) {
        if(body == null)
            return;
        Vector2 pos = body.getPosition();
        sprite.setOrigin(sprite.getWidth()/2, sprite.getHeight()/2);
        sprite.setPosition(pos.x - sprite.getOriginX(), pos.y - sprite.getOriginY());
        sprite.setRotation((float) Math.toDegrees(body.getAngle()));
        sprite.setAlpha(1 - alphaOffset);
        sprite.draw(batch);
    }

    public void accelerate() {
        Vector2 dir = new Vector2(FORWARD_ACCELERATION, FORWARD_ACCELERATION);
        dir.setAngleRad(body.getAngle());
        dir.rotateDeg(90);
        body.applyLinearImpulse(dir, body.getPosition(), true);
    }

    public boolean canShoot() {
        return cooldownTimer <= 0 && stunTimer <= 0;
    }

    public boolean isStunned(){
        return stunTimer > 0;
    }

    public Laser shoot() {
        cooldownTimer = SHOT_COOLDOWN;
        return new Laser(this);
    }

    public void strafeLeft() {
        Vector2 dir = new Vector2(STRAFE_ACCELERATION, STRAFE_ACCELERATION);
        dir.setAngleRad(body.getAngle());
        dir.rotateDeg(160);
        body.applyLinearImpulse(dir, body.getPosition(), true);
    }

    public void strafeRight() {
        Vector2 dir = new Vector2(STRAFE_ACCELERATION, STRAFE_ACCELERATION);
        dir.setAngleRad(body.getAngle());
        dir.rotateDeg(20);
        body.applyLinearImpulse(dir, body.getPosition(), true);
    }

    public void getBonus(int bonus){
        score += bonus;
    }

    public void stun(){
        if(stunTimer == 0) {
            body.setFixedRotation(false);
            alphaOffset += 0.2f;
        }
        stunTimer = 90;
    }
}
