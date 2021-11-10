package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class Asteroid extends Entity{
    private static final String COLOR_GREY = "Grey";
    private static final String COLOR_BROWN = "Brown";
    private static final String SIZE_BIG = "Big";
    private static final String SIZE_MED = "Med";
    private static final String SIZE_SMALL = "Small";
    private static final String SIZE_TINY = "Tiny";
    private static final String SPRITE_DIR = "sprites/meteors/";
    private static final String METEOR_STR = "meteor";
    private static final int BONUS_BIG = 1;//It's not a big bonus, but a bonus for a big asteroid.
    private static final int BONUS_MED = 3;//And tiny asteroid are actually harder to destroy while dealing the same damage.
    private static final int BONUS_SMALL = 5;//So, the smaller the size, the smaller the bonus.
    private static final int BONUS_TINY = 10;
    public final int bonus;

    Asteroid(World world, float x, float y){
        int randPick = (int)(Math.random()/0.125f);
        String color, size;
        switch (randPick) {
            case 0:
                color = COLOR_BROWN;
                size = SIZE_BIG;
                bonus = BONUS_BIG;
                break;
            case 1:
                color = COLOR_GREY;
                size = SIZE_BIG;
                bonus = BONUS_BIG;
                randPick = 0;
                break;
            case 2:
                color = COLOR_BROWN;
                size = SIZE_MED;
                bonus = BONUS_MED;
                break;
            case 3:
                color = COLOR_GREY;
                size = SIZE_MED;
                bonus = BONUS_MED;
                break;
            case 4:
                color = COLOR_BROWN;
                size = SIZE_SMALL;
                bonus = BONUS_SMALL;
                break;
            case 5:
                color = COLOR_GREY;
                size = SIZE_SMALL;
                bonus = BONUS_SMALL;
                break;
            case 6:
                color = COLOR_BROWN;
                size = SIZE_TINY;
                bonus = BONUS_TINY;
                break;
            default:
                color = COLOR_GREY;
                size = SIZE_TINY;
                bonus = BONUS_TINY;
                break;
        }
        if(randPick == 0)
            randPick = (int) (Math.random()/0.25 + 1);
        else
            randPick = (int) (Math.random()/0.5 + 1);

        body = SHAPES.createBody(METEOR_STR + size + randPick, world, SCALE, SCALE);
        body.setTransform(x, y, (float)Math.random()*6);
        Vector2 velocity = new Vector2();
        velocity.x = (float)Math.random() - 0.5f;
        velocity.y = (float)Math.random() - 0.5f;
        velocity.setLength((float)Math.random() * 2 + 1);
        body.setLinearVelocity(velocity);
        body.setUserData(this);

        sprite = new Sprite(new Texture(SPRITE_DIR + METEOR_STR + color + size + randPick + ".png"));
        sprite.setSize(sprite.getWidth() * SCALE, sprite.getHeight() * SCALE);
    }

    @Override
    public void collideWith(Entity target) {
        //TODO: Ship collision
        //TODO: Laser collision
    }
}
