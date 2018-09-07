package com.oopgame.game.vfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

import helpers.GameInfo;

public class LaserEffect extends Sprite implements VisualEffect {
    public static String keyType = "laserEffect";

    private int layer;

    private int lastFrame = 60;
    private int frame = 0;

    private float alphaStart = 0;
    private float alphaMid = 1;
    private float alphaEnd = 0;
    private float holdStart = 0.5f * lastFrame;
    private float holdEnd = 0.5f * lastFrame;

    private Vector2 pos;
    private Vector2 frameMovement = new Vector2();

    private VisualEffectKeeper vfxKeeper;

    public LaserEffect(int layer, float x, float y, float angle, float scale,
                       Color color, int lastFrame, float movement,
                       float alphaStart, float alphaMid, float alphaEnd,
                       float midpointMultiplier, float holdMultiplier,
                       List<Sprite> graphics, VisualEffectsManager vfxManager) {
        super(graphics.get(0));

        reconfigure(layer, x, y, angle, scale,
                color, lastFrame, movement,
                alphaStart, alphaMid, alphaEnd,
                midpointMultiplier, holdMultiplier);

        this.vfxKeeper = vfxManager;
    }

    public void reconfigure(int layer, float x, float y, float angle, float scale,
                            Color color, int lastFrame, float movement,
                            float alphaStart, float alphaMid, float alphaEnd,
                            float midpointMultiplier, float holdMultiplier) {
        this.layer = layer;

        setRotation(angle);
        setCenter(x, y);
        setScale(GameInfo.SCALING * scale);
        setColor(color);

        this.pos = new Vector2(x, y);

        if (movement != 0)
            frameMovement.set(1, 0).setLength(movement).setAngle(angle);

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
        if (lastFrame != -1) {
            if (frame < holdStart)
                setAlpha(alphaStart + (alphaMid - alphaStart) * frame / holdStart);
            else if (frame >= holdStart && frame < holdEnd)
                setAlpha(alphaMid);
            else if (frame >= holdEnd)
                setAlpha(alphaMid + (alphaEnd - alphaMid) * (frame - holdEnd) / (lastFrame - holdEnd));
            else
                setAlpha(0);

            pos.add(frameMovement);
            setCenter(pos.x, pos.y);

            if (frame++ == lastFrame)
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
