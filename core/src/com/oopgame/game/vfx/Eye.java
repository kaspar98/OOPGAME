package com.oopgame.game.vfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.List;

import helpers.GameInfo;

public class Eye implements VisualEffect {
    public static String keyType = "eye2";

    private int layer;

    private int lastFrame = 10;
    private int frame = 0;

    private float alphaStart = 0;
    private float alphaMid = 1;
    private float alphaEnd = 0;
    private float holdStart = 0.5f * lastFrame;
    private float holdEnd = 0.5f * lastFrame;

    private List<Sprite> sprites = new ArrayList<Sprite>();

    private static Color[] defaultColors = new Color[]{
            new Color(0.47f, 0.24f, 0.24f, 1),
            new Color(0, 0, 0, 1),
            new Color(1, 0, 0, 1),
            new Color(1, 0, 0, 1)
    };

    private VisualEffectKeeper vfxKeeper;

    public Eye(int layer, float x, float y, float angle, float scale,
               int lastFrame, float alphaStart, float alphaMid, float alphaEnd,
               float midpointMultiplier, float holdMultiplier,
               List<Sprite> graphics, VisualEffectsManager vfxManager) {
        this(layer, x, y, angle, scale,
                lastFrame, alphaStart, alphaMid, alphaEnd,
                midpointMultiplier, holdMultiplier,
                defaultColors[0], defaultColors[1], defaultColors[2], defaultColors[3],
                graphics, vfxManager);
    }

    public Eye(int layer, float x, float y, float angle, float scale,
               int lastFrame, float alphaStart, float alphaMid, float alphaEnd,
               float midpointMultiplier, float holdMultiplier,
               Color color0, Color color1, Color color2, Color color3,
               List<Sprite> graphics, VisualEffectsManager vfxManager) {

        for (int i = 0; i < graphics.size(); i++)
            sprites.add(new Sprite(graphics.get(i)));

        reconfigure(layer, x, y, angle, scale,
                lastFrame, alphaStart, alphaMid, alphaEnd,
                midpointMultiplier, holdMultiplier,
                color0, color1, color2, color3);

        this.vfxKeeper = vfxManager;
    }

    public void reconfigure(int layer, float x, float y, float angle, float scale,
                            int lastFrame, float alphaStart, float alphaMid, float alphaEnd,
                            float midpointMultiplier, float holdMultiplier) {
        reconfigure(layer, x, y, angle, scale,
                lastFrame, alphaStart, alphaMid, alphaEnd,
                midpointMultiplier, holdMultiplier,
                defaultColors[0], defaultColors[1], defaultColors[2], defaultColors[3]);
    }

    public void reconfigure(int layer, float x, float y, float angle, float scale,
                            int lastFrame, float alphaStart, float alphaMid, float alphaEnd,
                            float midpointMultiplier, float holdMultiplier,
                            Color color0, Color color1, Color color2, Color color3) {
        this.layer = layer;

        Color[] colors = new Color[]{color0, color1, color2, color3};

        for (int i = 0; i < sprites.size(); i++) {
            Sprite sprite = sprites.get(i);

            sprite.setScale(scale * GameInfo.SCALING);
            sprite.setRotation(angle);
            sprite.setOriginBasedPosition(x, y);

            setSpriteColor(i, colors[i]);

            sprite.setAlpha(alphaStart);
        }

        this.lastFrame = lastFrame;
        float midpoint = lastFrame * midpointMultiplier;
        this.holdStart = midpoint - midpoint * holdMultiplier;
        this.holdEnd = midpoint + (lastFrame - midpoint) * holdMultiplier;

        this.alphaStart = alphaStart;
        this.alphaMid = alphaMid;
        this.alphaEnd = alphaEnd;
    }

    public void setSpriteColor(int visualLayer, Color color) {
        sprites.get(visualLayer).setColor(color);
    }

    @Override
    public void restart() {
        frame = 0;
    }

    @Override
    public void update() {
        float alpha = 0;

        if (frame < holdStart)
            alpha = alphaStart + (alphaMid - alphaStart) * frame / holdStart;
        else if (frame >= holdStart && frame < holdEnd)
            alpha = alphaMid;
        else if (frame >= holdEnd)
            alpha = alphaMid + (alphaEnd - alphaMid) * (frame - holdEnd) / (lastFrame - holdEnd);

        for (Sprite sprite : sprites)
            sprite.setAlpha(alpha);

        if (frame++ >= lastFrame)
            vfxKeeper.removeEffect(this);
    }

    @Override
    public void draw(SpriteBatch batch) {
        for (Sprite sprite : sprites)
            sprite.draw(batch);
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
        for (Sprite sprite : sprites)
            sprite.setAlpha(0);
    }

    @Override
    public void setVisualEffectKeeper(VisualEffectKeeper vfxKeeper) {
        this.vfxKeeper = vfxKeeper;
    }

    public void setOriginBasedPosition(float x, float y) {
        for (Sprite sprite : sprites)
            sprite.setOriginBasedPosition(x, y);
    }

    public void setScale(float scale) {
        for (Sprite sprite : sprites)
            sprite.setScale(scale);
    }

    public void setOrigin(float x, float y) {
        // võibolla oleks mõtekam teha nii, et ta uuendab origini varasemate originide suhtes
        // - see teeks klassi veidi universaalsemaks
        for (Sprite sprite : sprites)
            sprite.setOrigin(x, y);
    }

    public void setAngle(float angle) {
        for (Sprite sprite : sprites)
            sprite.setRotation(angle);
    }

    public float getWidth() {
        return sprites.get(0).getWidth();
    }

    public float getHeight() {
        return sprites.get(0).getHeight();
    }
}
