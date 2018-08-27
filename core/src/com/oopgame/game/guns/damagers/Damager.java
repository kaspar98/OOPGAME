package com.oopgame.game.guns.damagers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface Damager {
    int getDamage();

    int getFaction();

    void hit();

    void update();

    void draw(SpriteBatch batch);

    void deactivate();
}
