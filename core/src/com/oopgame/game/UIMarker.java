package com.oopgame.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import helpers.GameInfo;

public class UIMarker extends Sprite {
    OrthographicCamera camera;

    Vector2 point;

    float radius;

    public UIMarker(Texture texture, OrthographicCamera camera, float radius, Vector2 point) {
        super(texture);

        this.camera = camera;
        this.radius = radius;
        this.point = point;

        setSize(getWidth() * GameInfo.CAM_SCALING,
                getHeight() * GameInfo.CAM_SCALING);

        setOrigin(getWidth(), getHeight() * 0.5f);
    }

    public void update() {
        float angle = new Vector2(
                point.x - camera.position.x,
                point.y - camera.position.y
        ).angle();
        setOriginBasedPosition(
                camera.position.x + MathUtils.cosDeg(angle) * radius,
                camera.position.y + MathUtils.sinDeg(angle) * radius);

        setRotation(angle);
    }

    public Vector2 getPoint() {
        return point;
    }

    public void dispose() {
        getTexture().dispose();
    }
}
