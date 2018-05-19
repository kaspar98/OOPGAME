package com.oopgame.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import helpers.GameInfo;

public class UIBar extends Sprite {
    private OrthographicCamera camera;

    private float dx;
    private float dy;
    private float maxValue;
    private float maxSize;

    public UIBar(Texture texture, float maxValue, float dx, OrthographicCamera camera) {
        super(texture);

        this.maxValue = maxValue;
        this.dx = dx;
        this.dy = getHeight() * 0.5f * GameInfo.CAM_SCALING;
        this.camera = camera;

        setSize(
                getWidth() * GameInfo.CAM_SCALING,
                getHeight() * GameInfo.CAM_SCALING);

        setCenter(
                camera.position.x + dx,
                camera.position.y + dy);

        maxSize = getTexture().getHeight();
    }

    public void update(float value) {
        if (value > maxValue)
            value = maxValue;
        else if (value < 1)
            value = 1;

        float height = value / maxValue * maxSize;

        setRegionY((int) (maxSize - height));
        setSize(getWidth(), height * GameInfo.CAM_SCALING);

        setOrigin(getWidth() * 0.5f, 0);

        setOriginBasedPosition(
                camera.position.x + dx,
                camera.position.y - dy);
    }

    public void dispose() {
        getTexture().dispose();
    }
}
