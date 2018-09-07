package com.oopgame.game.vfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.List;

import helpers.GameInfo;

public class Bloom extends Sprite implements VisualEffect {
    public static String keyType = "bloom";

    private int layer;

    private int lastFrame = 10;
    private int frame = 0;

    private float alphaStart = 0;
    private float alphaMid = 1;
    private float alphaEnd = 0;
    private float holdStart = 0 * lastFrame;
    private float holdEnd = 0 * lastFrame;

    private VisualEffectKeeper vfxKeeper;


    public Bloom(int layer, float x, float y, float scale,
                 Color color, int lastFrame,
                 float alphaStart, float alphaMid, float alphaEnd,
                 float midpointMultiplier, float holdMultiplier,
                 List<Sprite> frames, VisualEffectsManager vfxManager
    ) {
        super(frames.get(0));

        reconfigure(layer, x, y, scale,
                color, lastFrame,
                alphaStart, alphaMid, alphaEnd,
                midpointMultiplier, holdMultiplier);

        this.vfxKeeper = vfxManager;
    }

    public void reconfigure(int layer, float x, float y, float scale,
                            Color color, int lastFrame,
                            float alphaStart, float alphaMid, float alphaEnd,
                            float midpointMultiplier, float holdMultiplier) {
        this.layer = layer;
        setCenter(x, y);
        setScale(scale * GameInfo.SCALING);
        setColor(color);

        this.lastFrame = lastFrame;
        float midpoint = lastFrame * midpointMultiplier;
        this.holdStart = midpoint - midpoint * holdMultiplier;
        this.holdEnd = midpoint + (lastFrame - midpoint) * holdMultiplier;

        this.alphaStart = alphaStart;
        this.alphaMid = alphaMid;
        this.alphaEnd = alphaEnd;

        setAlpha(alphaStart);
    }

    @Override
    public void restart() {
        frame = 0;
    }

    @Override
    public void update() {
        // TODO: optimiseerida arvutamine!!! Hetkel on nulliga jagamine võimalik tekkima siin
        if (frame < holdStart)
            setAlpha(alphaStart + (alphaMid - alphaStart) * frame / holdStart);
        else if (frame >= holdStart && frame < holdEnd)
            setAlpha(alphaMid);
        else if (frame >= holdEnd)
            setAlpha(alphaMid + (alphaEnd - alphaMid) * (frame - holdEnd) / (lastFrame - holdEnd));
        else
            setAlpha(0);

        if (frame++ == lastFrame) {
            vfxKeeper.removeEffect(this);
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

    @Override
    public void setVisualEffectKeeper(VisualEffectKeeper vfxKeeper) {
        this.vfxKeeper = vfxKeeper;
    }
}
