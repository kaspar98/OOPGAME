package com.oopgame.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import helpers.GameInfo;

public class UIManager {
    SpriteBatch batch;
    OrthographicCamera camera;
    Player player;

    Sprite compass;

    UIBar health;
    UIBar shield;

    public UIManager(SpriteBatch batch, OrthographicCamera camera) {
        this.batch = batch;
        this.camera = camera;

        compass = new Sprite(new Texture("ui1_compass1_t.png"));
        compass.setSize(
                compass.getWidth() * GameInfo.SCALING,
                compass.getHeight() * GameInfo.SCALING);
        compass.setCenter(camera.position.x, camera.position.y);

        health = new UIBar(new Texture("ui1_health1_t.png"),
                100, -115 * GameInfo.SCALING, 0, camera);

        shield = new UIBar(new Texture("ui1_shield1_t.png"),
                100, 115 * GameInfo.SCALING, 0, camera);
    }

    public void update() {
        compass.setCenter(
                camera.position.x,
                camera.position.y);

        health.update(100);
        shield.update(100);
    }

    public void render() {
        compass.draw(batch);
        health.draw(batch);
        shield.draw(batch);
    }

    public void dispose() {
        compass.getTexture().dispose();
        health.getTexture().dispose();
        shield.getTexture().dispose();
    }
}
