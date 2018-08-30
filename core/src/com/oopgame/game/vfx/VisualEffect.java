package com.oopgame.game.vfx;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface VisualEffect {
    void start(int layer, float x, float y);

    void update();

    void draw(SpriteBatch batch);

    int getLayer();

    String getKeyType();

    void deactivate();
}
