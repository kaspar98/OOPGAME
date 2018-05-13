package com.oopgame.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import helpers.GameInfo;

public class UIBar extends Sprite{
    float dx;
    float dy;
    float maxValue;

    OrthographicCamera camera;

    public UIBar(Texture texture, float maxValue, float dx, float dy, OrthographicCamera camera) {
        super(texture);

        this.maxValue = maxValue;
        this.dx = dx;
        this.dy = dy;
        this.camera = camera;

        setSize(
                getWidth() * GameInfo.SCALING,
                getHeight() * GameInfo.SCALING);
    }

    public void update(float value) {
        setCenter(camera.position.x + dx, camera.position.y + dy);
    }

    public void dispose() {
        getTexture().dispose();
    }
}
