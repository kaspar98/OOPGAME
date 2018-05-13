package com.oopgame.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;

import helpers.GameInfo;

public class Planet extends Sprite {
    private float parallaxConstant;

    private float dx;
    private float dy;

    public Planet(String textureName) {
        super(new Texture(Gdx.files.internal(textureName)));

        float resizeXY = (
                parallaxConstant = getTexture().getWidth() * MathUtils.random(0.05f, 0.1f)
        ) * GameInfo.SCALING;

        parallaxConstant = 1 - 1f / (getTexture().getWidth() * 0.2f - parallaxConstant) * 2;

        setSize(resizeXY, resizeXY);

        dx = MathUtils.random(0, GameInfo.WIDTH * 0.9f) * GameInfo.SCALING;
        dy = MathUtils.random(0, GameInfo.HEIGHT * 0.9f) * GameInfo.SCALING;
    }

    public void update(float x, float y) {
        setCenter(
                x * parallaxConstant + dx,
                y * parallaxConstant + dy
        );
    }

    public void dispose() {
        getTexture().dispose();
    }

    public float getParallaxConstant() {
        return parallaxConstant;
    }
}
