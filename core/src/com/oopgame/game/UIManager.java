package com.oopgame.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import helpers.GameInfo;

public class UIManager {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Player player;

    private Sprite compass;
    private Texture compass_marker_texture;
    private Array<UIMarker> compass_markers = new Array<UIMarker>();

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

        compass = new Sprite(new Texture("ui1_compass2_t.png"));
        compass.setSize(
                compass.getWidth() * GameInfo.CAM_SCALING,
                compass.getHeight() * GameInfo.CAM_SCALING);

        compass_marker_texture = new Texture(Gdx.files.internal("ui1_compass_marker2_t.png"));

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

        addMarker(new Vector2(0, 0));
    }

    public void update() {
        compass.setCenter(
                camera.position.x,
                camera.position.y);

        for (UIMarker marker : compass_markers)
            marker.update();

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
        for (UIMarker marker : compass_markers)
            marker.draw(batch);
    }

    public void dispose() {
        compass.getTexture().dispose();
        health.getTexture().dispose();
        shield.getTexture().dispose();
        back.dispose();

        for (UIMarker marker : compass_markers)
            marker.dispose();
    }

    public UIMarker addMarker(Vector2 point) {
        UIMarker marker = new UIMarker(
                compass_marker_texture,
                camera, compass.getWidth() * 0.5f,
                point
        );

        compass_markers.add(marker);

        return marker;
    }

    public void removeMarker(Vector2 keskkoht) {
        for (UIMarker u : compass_markers) {
            if (u.getPoint().equals(keskkoht)) {
                compass_markers.removeValue(u, false);
                break;
            }
        }
    }
}
