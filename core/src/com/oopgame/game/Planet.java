package com.oopgame.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import helpers.GameInfo;

public class Planet extends Sprite {
    private Vector3 camPos;

    private float parallaxConstant;

    private float dx;
    private float dy;

    public Planet(String textureName, Vector3 camPos) {
        super(new Texture(Gdx.files.internal(textureName)));

        this.camPos = camPos;

        float resizeXY = (
                parallaxConstant = getTexture().getWidth() * MathUtils.random(0.5f, 0.9f)
        ) * GameInfo.CAM_SCALING;

        parallaxConstant = 1 - 1f / (getTexture().getWidth() - parallaxConstant) * 2f;

        setSize(resizeXY, resizeXY);

        float rangeMax = GameInfo.W_WIDTH * GameInfo.CAM_SCALING * 0.5f;
        float rangeMin = rangeMax * 0.2f;

        dx = MathUtils.random(-rangeMin, rangeMax);
        dy = MathUtils.random(-rangeMin, rangeMax);
    }

    public void update() {
        setCenter(
                camPos.x * parallaxConstant + dx,
                camPos.y * parallaxConstant + dy
        );
    }

    public void dispose() {
        getTexture().dispose();
    }

    public float getParallaxConstant() {
        return parallaxConstant;
    }
}
