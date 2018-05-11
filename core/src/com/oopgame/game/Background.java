package com.oopgame.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import helpers.GameInfo;

public class Background extends Sprite {
    float cropX;
    float cropY;

    float parallaxConstantX;
    float parallaxConstantY;

    public Background(String textureName, float cropXY) {
        this(textureName, cropXY, cropXY);
    }

    public Background(String textureName, float cropX, float cropY) {
        super(new Texture(Gdx.files.internal(textureName)));

        setSize(
                getWidth() * GameInfo.SCALING,
                getHeight() * GameInfo.SCALING
        );

        this.cropX = cropX;
        this.cropY = cropY;

        parallaxConstantX = 1 - (getWidth() - 2 * this.cropX) / GameInfo.W_WIDTH;
        parallaxConstantY = 1 - (getHeight() - 2 * this.cropY) / GameInfo.W_HEIGHT;
    }

    public void update(float x, float y) {
        setPosition(
                x * parallaxConstantX - cropX,
                y * parallaxConstantY - cropY
        );
    }

    public void dispose() {
        getTexture().dispose();
    }
}
