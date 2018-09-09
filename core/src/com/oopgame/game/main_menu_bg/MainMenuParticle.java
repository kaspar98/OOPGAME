package com.oopgame.game.main_menu_bg;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class MainMenuParticle extends Sprite {
    private Color[] colors = new Color[]{
            Color.WHITE,
            Color.RED,
            Color.CYAN,
            Color.GOLD,
            Color.MAGENTA,
            Color.SKY,
            new Color(0.5f, 0.6f, 1, 1),
            new Color(1, 0.7f, 1, 1)};

    private float maxRadius;

    private Vector2 center;
    private Vector2 heading = new Vector2();

    private float speedMultiplier;

    public MainMenuParticle(Sprite sprite, Vector2 center, float maxRadius) {
        super(sprite);
        this.center = center;
        this.maxRadius = maxRadius;

        setOrigin(0, getHeight() * 0.5f);

        float length = MathUtils.random(maxRadius);
        float angle = MathUtils.random(360f);

        heading.set(length, 0).setAngle(angle);
        setRotation(angle);
        updatePosSize();

        reset();
    }

    public void reconfigure() {
        reset();

        float length = MathUtils.random(1f);
        float angle = MathUtils.random(360f);

        heading.set(length, 0).setAngle(angle);
        setRotation(angle);
        updatePosSize();
    }

    private void reset() {
        setColor(colors[MathUtils.random(colors.length - 1)]);

        speedMultiplier = MathUtils.random(1.1f, 1.2f);
    }

    private void updatePosSize() {
        float length = heading.len();

        setSize(length * 0.25f * speedMultiplier,
                length * 0.025f * speedMultiplier);

        setOriginBasedPosition(
                center.x + heading.x,
                center.y + heading.y);
    }

    public void update() {
        float length = heading.len();

        updatePosSize();

        heading.setLength(length * speedMultiplier);
    }

    public float getDistance() {
        return heading.len();
    }
}
