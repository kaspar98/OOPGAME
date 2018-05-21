package com.oopgame.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;

import helpers.GameInfo;

public class Background extends Sprite {
    private Vector3 camPos;

    private float dx = 0;
    private float dy = 0;

    private float cropX;
    private float cropY;

    private float parallaxConstantXY;

    public Background(String textureName, float cropX, float cropY, Vector3 camPos) {
        super(new Texture(Gdx.files.internal(textureName)));
        this.camPos = camPos;


        setSize(
                getWidth() * GameInfo.CAM_SCALING,
                getHeight() * GameInfo.CAM_SCALING
        );

        setOrigin(getWidth() * 0.5f, getHeight() * 0.5f);

        this.cropX = /*cropX*/getWidth() * 0.45f;
        this.cropY = /*cropY*/getHeight() * 0.45f;

        parallaxConstantXY =
                1 - (
                        getWidth() - 2 * (this.cropX > this.cropY ? this.cropX : this.cropY)
                ) / (GameInfo.W_WIDTH > GameInfo.W_HEIGHT ? GameInfo.W_WIDTH : GameInfo.W_HEIGHT);
    }

    public void update() {
        setCenter(
                dx + camPos.x * parallaxConstantXY,
                dy + camPos.y * parallaxConstantXY
        );
    }

    public void dispose() {
        getTexture().dispose();
    }
}
