package com.oopgame.game.guns.damagers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;

public interface Damager {
    void update();

    void draw(SpriteBatch batch);

    void deactivate();

    void hit();

    int getDamage();

    int getFaction();

    String getKeyType();

    Body getBody();

    Color getColor();

    float getAngle();

    boolean didHit();
}
