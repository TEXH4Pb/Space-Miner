package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class Laser extends Entity{
    private static final int ENERGY_MAX = 60;
    private Ship owner;
    private int energy;
    public boolean firstContact;

    Laser(Ship shooter){
        energy = ENERGY_MAX;
        owner = shooter;
        firstContact = true;

        body = SHAPES.createBody("laser", owner.body.getWorld(), SCALE, SCALE);
        body.setUserData(this);
        Vector2 dir = new Vector2(4,4);
        body.setTransform(owner.getPosition(), owner.body.getAngle());
        dir.setAngleRad(body.getAngle());
        dir.rotate90(0);
        body.applyLinearImpulse(dir, body.getPosition(), true);

        sprite = new Sprite(new Texture("sprites/effects/laserRed.png"));
        sprite.setSize(sprite.getWidth() * SCALE, sprite.getHeight() * SCALE);
    }

    @Override
    public void collideWith(Entity target) {
        if (firstContact) {
            firstContact = false;
            return;
        }

        queuedForRemoval = true;
        if(target instanceof Asteroid){
            owner.getBonus(((Asteroid) target).bonus);
            target.queuedForRemoval = true;
        }else if(target instanceof Ship){
            ((Ship) target).stun();
        }
        else
            target.queuedForRemoval = true;
    }

    @Override
    public void update() {
        if(energy <= 0)
            queuedForRemoval = true;
        else
            energy--;
    }
}
