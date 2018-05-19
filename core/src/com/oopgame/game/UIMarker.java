package com.oopgame.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class UIMarker extends Sprite {
    private Vector3 camPos;

    private Vector2 enemyPos;

    private float radius;

    public UIMarker(Sprite appearance, OrthographicCamera camera, float radius, Vector2 enemyPos) {
        super(appearance);

        this.camPos = camera.position;
        this.radius = radius;
        this.enemyPos = enemyPos;
    }

    public void update() {
        float angle = new Vector2(enemyPos.x - camPos.x, enemyPos.y - camPos.y).angle();

        setOriginBasedPosition(
                camPos.x + MathUtils.cosDeg(angle) * radius,
                camPos.y + MathUtils.sinDeg(angle) * radius);

        setRotation(angle);
    }
}
