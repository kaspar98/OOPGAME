package com.oopgame.game.vfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.List;

public class Bloom extends Sprite implements VisualEffect {
    public static String keyType = "bloom";

    private int layer;

    private static int lastFrame = 10;
    private int frame = 0;

    private float maxAlpha = 1;

    private VisualEffectsManager vfxManager;


    /*public Bloom(int layer, float x, float y,
                 List<Sprite> frames, float scale,
                 VisualEffectsManager vfxManager) {
        this(layer, x, y, frames, scale, Color.WHITE, vfxManager);
    }*/

    public Bloom(int layer, float x, float y,
                 List<Sprite> frames, float scale, Color color,
                 VisualEffectsManager vfxManager, float maxAlpha) {
        super(frames.get(0));

        this.layer = layer;
        this.maxAlpha = maxAlpha;
        setAlpha(maxAlpha);
        setCenter(x, y);
        setScale(scale);

        if (color != Color.WHITE)
            setColor(color);

        this.vfxManager = vfxManager;
    }

    @Override
    public void start(int layer, float x, float y) {
        start(layer, x, y, 1, Color.WHITE, 1);
    }

    public void start(int layer, float x, float y, float scale, Color color, float maxAlpha) {
        this.layer = layer;
        this.maxAlpha = maxAlpha;
        setCenter(x, y);
        setScale(scale);
        setColor(color);

        frame = 0;
        setAlpha(maxAlpha);
    }

    @Override
    public void update() {
        setAlpha(maxAlpha * (lastFrame - ++frame) / (float) lastFrame);

        if (frame == lastFrame) {
            vfxManager.removeEffect(this);

            deactivate();
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        super.draw(batch);
    }

    @Override
    public int getLayer() {
        return layer;
    }

    @Override
    public String getKeyType() {
        return keyType;
    }

    @Override
    public void deactivate() {
        setAlpha(0);
    }
}
