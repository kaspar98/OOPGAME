package com.oopgame.game.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class VisualEffectsManager implements VisualEffectKeeper {
    private SpriteBatch batch;
    private World world;

    private BodyDef bodyDef = new BodyDef();
    private PolygonShape polygonShape = new PolygonShape();

    private HashMap<String, List<Sprite>> graphics = new HashMap<String, List<Sprite>>();

    private Map<Integer, List<VisualEffect>> aliveEffects = new HashMap<Integer, List<VisualEffect>>();
    private Map<String, Deque<VisualEffect>> effectsPool = new HashMap<String, Deque<VisualEffect>>();
    private Deque<VisualEffect> toDeactivate = new LinkedList<VisualEffect>();

    private int frame = 0;

    public VisualEffectsManager(SpriteBatch batch, World world) {
        this.batch = batch;
        this.world = world;

        // basic physics bodyDef, kui mingi efekt seda vajama peaks
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        polygonShape.setAsBox(1, 1);
    }

    public void update() {
        frame++;

        for (VisualEffect vfx = toDeactivate.poll(); vfx != null; vfx = toDeactivate.poll()) {
            checkLayer(vfx.getLayer());

            aliveEffects.get(vfx.getLayer()).remove(vfx);

            String key = vfx.getKeyType();

            vfx.deactivate();

            checkPool(key);

            effectsPool.get(key).add(vfx);
        }

        for (List<VisualEffect> list : aliveEffects.values())
            for (VisualEffect vfx : list)
                vfx.update();
    }

    public void render(int layer) {
        checkLayer(layer);

        for (VisualEffect vfx : aliveEffects.get(layer))
            vfx.draw(batch);
    }

    public void addExplosion(int layer, float x, float y, float scale, Color color) {
        addBloom(layer, x, y, scale * 10,
                color, 10,
                1, 0.6f, 0,
                0, 0);
    }

    public void addBloom(int layer, float x, float y, float scale,
                         Color color, int lastFrame,
                         float alphaStart, float alphaMid, float alphaEnd,
                         float midpointMultiplier, float holdMultiplier) {
        checkPool(Bloom.keyType);

        VisualEffect vfx = effectsPool.get(Bloom.keyType).poll();

        if (vfx != null) {
            ((Bloom) vfx).reconfigure(layer, x, y, scale,
                    color, lastFrame,
                    alphaStart, alphaMid, alphaEnd,
                    midpointMultiplier, holdMultiplier);

            vfx.restart();
        } else
            vfx = createBloom(layer, x, y, scale,
                    color, lastFrame,
                    alphaStart, alphaMid, alphaEnd,
                    midpointMultiplier, holdMultiplier);

        addEffect(layer, vfx);
    }

    public Bloom createBloom(int layer, float x, float y, float scale,
                             Color color, int lastFrame,
                             float alphaStart, float alphaMid, float alphaEnd,
                             float midpointMultiplier, float holdMultiplier) {
        if (!graphics.containsKey(Bloom.keyType)) {
            graphics.put(Bloom.keyType, new ArrayList<Sprite>());

            graphics.get(Bloom.keyType).add(new Sprite(new Texture(
                    Gdx.files.internal("vfx/bloom1.png"))));
        }

        return new Bloom(layer, x, y, scale,
                color, lastFrame,
                alphaStart, alphaMid, alphaEnd,
                midpointMultiplier, holdMultiplier,
                graphics.get(Bloom.keyType), this);
    }

    public void addEye(int layer, float x, float y, float angle, float scale,
                       int lastFrame, float alphaStart, float alphaMid, float alphaEnd,
                       float midpointMultiplier, float holdMultiplier) {
        checkPool(Eye.keyType);

        VisualEffect vfx = effectsPool.get(Eye.keyType).poll();

        if (vfx != null) {
            ((Eye) vfx).reconfigure(layer, x, y, angle, scale,
                    lastFrame, alphaStart, alphaMid, alphaEnd,
                    midpointMultiplier, holdMultiplier);
            vfx.restart();
        } else
            vfx = createEye(layer, x, y, angle, scale,
                    lastFrame, alphaStart, alphaMid, alphaEnd,
                    midpointMultiplier, holdMultiplier);

        addEffect(layer, vfx);
    }

    public Eye createEye(int layer, float x, float y, float angle, float scale,
                         int lastFrame, float alphaStart, float alphaMid, float alphaEnd,
                         float midpointMultiplier, float holdMultiplier) {
        if (!graphics.containsKey(Eye.keyType)) {
            List<Sprite> sprites = new ArrayList<Sprite>();

            for (int i = 0; i < 4; i++)
                sprites.add(new Sprite(new Texture(
                        Gdx.files.internal("vfx/eye2_" + i + ".png"))));

            graphics.put(Eye.keyType, sprites);
        }

        return new Eye(layer, x, y, angle, scale,
                lastFrame, alphaStart, alphaMid, alphaEnd,
                midpointMultiplier, holdMultiplier,
                graphics.get(Eye.keyType), this);
    }

    public void addLaserEffect(int layer, float x, float y, float angle, float scale,
                               Color color, int lastFrame, float movement,
                               float alphaStart, float alphaMid, float alphaEnd,
                               float midpointMultiplier, float holdMultiplier) {
        checkPool(LaserEffect.keyType);

        VisualEffect vfx = effectsPool.get(LaserEffect.keyType).poll();

        if (vfx != null) {
            ((LaserEffect) vfx).reconfigure(layer, x, y, angle, scale,
                    color, lastFrame, movement,
                    alphaStart, alphaMid, alphaEnd,
                    midpointMultiplier, holdMultiplier);
            vfx.restart();
        } else
            vfx = createLaserEffect(layer, x, y, angle, scale,
                    color, lastFrame, movement,
                    alphaStart, alphaMid, alphaEnd,
                    midpointMultiplier, holdMultiplier);

        addEffect(layer, vfx);
    }

    public LaserEffect createLaserEffect(int layer, float x, float y, float angle, float scale,
                                         Color color, int lastFrame, float movement,
                                         float alphaStart, float alphaMid, float alphaEnd,
                                         float midpointMultiplier, float holdMultiplier) {
        if (!graphics.containsKey(LaserEffect.keyType)) {
            graphics.put(LaserEffect.keyType, new ArrayList<Sprite>());

            graphics.get(LaserEffect.keyType).add(new Sprite(new Texture(
                    Gdx.files.internal("vfx/laserEffect1.png"))));
        }

        return new LaserEffect(layer, x, y, angle, scale,
                color, lastFrame, movement,
                alphaStart, alphaMid, alphaEnd,
                midpointMultiplier, holdMultiplier,
                graphics.get(LaserEffect.keyType), this);
    }

    public void addPortal(int layer, float x, float y, float scale, int lastFrame,
                          float midpointMultiplier, float holdMultiplier) {
        checkPool(Portal.keyType);

        VisualEffect vfx = effectsPool.get(Portal.keyType).poll();

        if (vfx != null) {
            ((Portal) vfx).reconfigure(layer, x, y, scale, lastFrame,
                    midpointMultiplier, holdMultiplier);
            vfx.restart();
        } else
            vfx = createPortal(layer, x, y, scale, lastFrame,
                    midpointMultiplier, holdMultiplier);

        addEffect(layer, vfx);
    }

    public Portal createPortal(int layer, float x, float y, float scale, int lastFrame,
                               float midpointMultipleir, float holdMultiplier) {
        return new Portal(layer, x, y, scale,
                lastFrame, midpointMultipleir, holdMultiplier,
                this);
    }

    private void checkPool(String keyType) {
        if (!effectsPool.containsKey(keyType))
            effectsPool.put(keyType, new LinkedList<VisualEffect>());
    }

    private void checkLayer(int layer) {
        if (!aliveEffects.containsKey(layer))
            aliveEffects.put(layer, new ArrayList<VisualEffect>());
    }

    private void addEffect(int layer, VisualEffect vfx) {
        checkLayer(layer);

        aliveEffects.get(layer).add(vfx);
    }

    public void removeEffect(VisualEffect vfx) {
        toDeactivate.add(vfx);
    }

    public VisualEffect getFromPool(String keyType) {
        checkPool(keyType);

        return effectsPool.get(keyType).poll();
    }

    public void dispose() {
        for (List<Sprite> list : graphics.values())
            for (Sprite sprite : list)
                sprite.getTexture().dispose();
    }
}
