package com.oopgame.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import helpers.GameInfo;

public class Explosion extends Sprite {
    private float x, y;
    private float angle;

    private Array<Sprite> frames;

    private ExplosionManager explosionManager;

    private int frame = 0;

    private long timeFrameLength = 50;
    private long timeNextFrame;

    private float scaler = 1f;

    public Explosion(float x, float y, Array<Sprite> frames, ExplosionManager explosionManager) {
        this(x, y, 1, frames, explosionManager);
    }

    public Explosion(float x, float y, float scaler, Array<Sprite> frames, ExplosionManager explosionManager) {
        super(frames.get(0));

        this.y = y;
        this.x = x;
        this.angle = MathUtils.random(0, 360);
        this.frames = frames;
        this.explosionManager = explosionManager;

        this.scaler = scaler;

        setOriginBasedPosition(x, y);
        setRotation(angle);

        timeNextFrame = TimeUtils.millis() + timeFrameLength;
    }

    public void update() {
        long time = TimeUtils.millis();

        if (timeNextFrame < time) {
            if (frame >= frames.size - 1)
                explosionManager.removeExplosion(this);
            else {
                set(frames.get(++frame));

                if (scaler != 1f) {
                    setSize(
                            getTexture().getWidth() * GameInfo.SCALING * 3f * scaler,
                            getTexture().getHeight() * GameInfo.SCALING * 3f * scaler);

                    setOrigin(
                            getWidth() * 0.5f,
                            getHeight() * 0.5f);
                }

                setOriginBasedPosition(x, y);
                setRotation(angle);
                timeNextFrame = TimeUtils.millis() + timeFrameLength;
            }
        }
    }

    public void revive(float x, float y) {
        this.x = x;
        this.y = y;

        frame = 0;

        timeNextFrame = TimeUtils.millis();
    }
}
