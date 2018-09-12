package com.oopgame.game.ui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;

public class UIMarker extends Image {
    private UIManager uiManager;

    private Vector2 pos = new Vector2();
    private Vector2 center;

    private Vector3 camPos;
    private Vector2 enemyPos;

    private float radius;

    private Vector2 spareVector = new Vector2();

    public UIMarker(Vector2 compassCenter, float radius,
                    OrthographicCamera camera, Vector2 enemyPos,
                    Sprite appearance, UIManager uiManager) {
        super(appearance);

        this.center = compassCenter;
        this.camPos = camera.position;
        this.radius = radius;

        this.uiManager = uiManager;

        setOrigin(getWidth() * 0.5f, getHeight() * 0.5f);

        reconfigure(enemyPos);
    }

    public void reconfigure(Vector2 enemyPos) {
        this.enemyPos = enemyPos;
    }

    public void update() {
        float angle = spareVector.set(
                enemyPos.x - camPos.x,
                enemyPos.y - camPos.y).angle();

        setRotation(angle);
        pos.set(radius - 4, 0).setAngle(angle).add(center);

        setPosition(pos.x, pos.y,
                Align.center);
    }

    public void disable() {
        setRotation(0);
        setPosition(0, 0, Align.center);
        uiManager.removeMarker(this);
    }
}
