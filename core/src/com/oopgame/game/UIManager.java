package com.oopgame.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import helpers.GameInfo;

public class UIManager {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Player player;

    private Sprite compass;

    private float dx = 117 * GameInfo.CAM_SCALING;

    private Texture back;

    private UIBar health;
    private Sprite health_back;
    private UIBar shield;
    private Sprite shield_back;

    public UIManager(SpriteBatch batch, OrthographicCamera camera, Player player) {
        this.batch = batch;
        this.camera = camera;
        this.player = player;

        compass = new Sprite(new Texture("ui1_compass1_t.png"));
        compass.setSize(
                compass.getWidth() * GameInfo.CAM_SCALING,
                compass.getHeight() * GameInfo.CAM_SCALING);

        health = new UIBar(new Texture("ui1_health1_t.png"),
                player.getMaxHealth(), -dx, 0, camera);

        shield = new UIBar(new Texture("ui1_shield1_t.png"),
                player.getMaxShield(), dx, 0, camera);

        back = new Texture(Gdx.files.internal("ui1_health1_back_t.png"));

        health_back = new Sprite(back);
        health_back.setSize(
                health_back.getWidth() * GameInfo.CAM_SCALING,
                health_back.getHeight() * GameInfo.CAM_SCALING);

        shield_back = new Sprite(back);
        shield_back.setSize(
                health_back.getWidth(),
                health_back.getHeight());
        shield_back.setFlip(true, false);
    }

    public void update() {
        compass.setCenter(
                camera.position.x,
                camera.position.y);

        health.update(player.getHealth());
        health_back.setPosition(health.getX(), health.getY());
        shield.update(player.getShield());
        shield_back.setPosition(shield.getX(), shield.getY());
    }

    public void render() {
        health_back.draw(batch);
        shield_back.draw(batch);
        health.draw(batch);
        shield.draw(batch);
        compass.draw(batch);
    }

    public void dispose() {
        compass.getTexture().dispose();
        health.getTexture().dispose();
        shield.getTexture().dispose();
        back.dispose();
    }
}
