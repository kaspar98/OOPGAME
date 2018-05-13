package com.oopgame.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import helpers.GameInfo;

public class Background extends Sprite {
    private float dx = 0;
    private float dy = 0;

    private float cropX;
    private float cropY;

    private float parallaxConstantXY;

    public Background(String textureName, float cropXY) {
        this(textureName, cropXY, cropXY);
    }

    public Background(String textureName, float cropX, float cropY) {
        super(new Texture(Gdx.files.internal(textureName)));

        setSize(
                getWidth() * GameInfo.CAM_SCALING,
                getHeight() * GameInfo.CAM_SCALING
        );

        setOrigin(getWidth() / 2f, getHeight() / 2f);

        this.cropX = cropX;
        this.cropY = cropY;

        parallaxConstantXY =
                1 - (
                        getWidth() - 2 * (
                                this.cropX > this.cropY ? this.cropX : this.cropY
                        )
                ) / (GameInfo.W_WIDTH > GameInfo.W_HEIGHT ? GameInfo.W_WIDTH : GameInfo.W_HEIGHT);
    }

    public void update(float x, float y) {
        setPosition(
                dx + x * parallaxConstantXY - cropX,
                dy + y * parallaxConstantXY - cropY
        );
    }

    public void dispose() {
        getTexture().dispose();
    }
}
