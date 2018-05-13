package com.oopgame.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import helpers.GameInfo;

public class UIBar extends Sprite {
    OrthographicCamera camera;

    float dx;
    float dy;
    float maxValue;
    float value = 0;
    float maxSize = 0;

    public UIBar(Texture texture, float maxValue, float dx, float dy, OrthographicCamera camera) {
        super(texture);

        this.maxValue = maxValue;
        this.dx = dx;
        this.dy = dy;
        this.camera = camera;

        setSize(
                getWidth() * GameInfo.SCALING,
                getHeight() * GameInfo.SCALING);

        maxSize = getTexture().getHeight();
    }

    public void update(float value) {
        if (value > maxValue)
            value = maxValue;
        else if (value < 1)
            value = 1;

        float height = value / maxValue * maxSize;

        setRegionY((int) (maxSize - height));
        setSize(getWidth(), height * GameInfo.SCALING);

        setCenter(
                camera.position.x + dx,
                camera.position.y + dy - (maxSize - height) * 0.5f * GameInfo.SCALING);
    }

    public void dispose() {
        getTexture().dispose();
    }
}
