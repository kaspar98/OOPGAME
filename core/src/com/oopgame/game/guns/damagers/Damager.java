package com.oopgame.game.guns.damagers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;

public interface Damager {
    int getDamage();

    int getFaction();

    String getKeyType();

    void hit();

    void update();

    void draw(SpriteBatch batch);

    void deactivate();

    Body getBody();

    Color getColor();
}
