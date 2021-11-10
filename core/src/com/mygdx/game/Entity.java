package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.codeandweb.physicseditor.PhysicsShapeCache;

public abstract class Entity {
    protected static final PhysicsShapeCache SHAPES = new PhysicsShapeCache("physics.xml");
    static final float SCALE = 0.02f;

    Sprite sprite = null;
    Body body = null;
    public boolean queuedForRemoval = false;

    public abstract void collideWith(Entity target);
    public abstract void update();

    Vector2 getPosition()
    {
        return body.getPosition();
    }
    void draw(SpriteBatch batch) {
        if(body == null)
            return;
        Vector2 pos = body.getPosition();
        sprite.setOrigin(0, 0);
        sprite.setPosition(pos.x, pos.y);
        sprite.setRotation((float) Math.toDegrees(body.getAngle()));
        sprite.draw(batch);
    }
}
