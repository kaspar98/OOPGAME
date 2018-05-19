package com.oopgame.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class Explosion extends Sprite {
    private float x, y;
    private float angle;

    private Array<Sprite> frames;

    private ExplosionManager explosionManager;

    private int frame = 0;

    private long frameLenght = 50;
    private long frameChange;

    public Explosion(float x, float y, Array<Sprite> frames, ExplosionManager explosionManager) {
        super(frames.get(0));

        this.y = y;
        this.x = x;
        this.angle = MathUtils.random(0, 360);
        this.frames = frames;
        this.explosionManager = explosionManager;

        setOriginBasedPosition(x, y);
        setRotation(angle);

        frameChange = TimeUtils.millis();
    }

    public void update() {
        long time = TimeUtils.millis();

        if (frameChange + frameLenght < time) {
            if (frame >= frames.size - 1)
                explosionManager.removeExplosion(this);
            else {
                set(frames.get(++frame));
                setOriginBasedPosition(x, y);
                setRotation(angle);
                frameChange = TimeUtils.millis();
            }
        }
    }

    public void revive(float x, float y) {
        this.x = x;
        this.y = y;

        frame = 0;

        frameChange = TimeUtils.millis();
    }
}
