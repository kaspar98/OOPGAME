package com.oopgame.game.vfx;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface VisualEffect {
    void restart();

    void update();

    void draw(SpriteBatch batch);

    int getLayer();

    String getKeyType();

    void deactivate();

    // selleks, et suuremad VisualEffectid saaksid mugavamalt alam-efekte hallata
    void setVisualEffectKeeper(VisualEffectKeeper vfxKeeper);
}
