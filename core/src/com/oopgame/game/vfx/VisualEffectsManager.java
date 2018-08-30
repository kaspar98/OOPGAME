package com.oopgame.game.vfx;

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

public class VisualEffectsManager {
    private SpriteBatch batch;
    private World world;

    private BodyDef bodyDef = new BodyDef();
    private PolygonShape polygonShape = new PolygonShape();

    private HashMap<String, List<Sprite>> frames = new HashMap<String, List<Sprite>>();

    private Map<Integer, List<VisualEffect>> aliveEffects = new HashMap<Integer, List<VisualEffect>>();
    private Map<String, Deque<VisualEffect>> effectsPool = new HashMap<String, Deque<VisualEffect>>();
    private Deque<VisualEffect> toDeactivate = new LinkedList<VisualEffect>();

    public VisualEffectsManager(SpriteBatch batch, World world) {
        this.batch = batch;
        this.world = world;

        // basic physics bodyDef, kui mingi efekt seda vajama peaks
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        polygonShape.setAsBox(1, 1);
    }

    public void update() {
        for (VisualEffect vfx = toDeactivate.poll(); vfx != null; vfx = toDeactivate.poll()) {
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

    public void addExplosion(int layer, float x, float y, float scale) {
        addExplosion(layer, x, y, scale, Color.WHITE);
    }

    public void addExplosion(int layer, float x, float y, float scale, Color color) {
        addBloom(layer, x, y, scale, color, 0.3f);

    }

    public void addBloom(int layer, float x, float y, float scale, float maxAlpha) {
        addBloom(layer, x, y, scale, Color.WHITE, maxAlpha);
    }

    public void addBloom(int layer, float x, float y, float scale, Color color, float maxAlpha) {
        if (!frames.containsKey(Bloom.keyType)) {
            frames.put(Bloom.keyType, new ArrayList<Sprite>());

            frames.get(Bloom.keyType).add(new Sprite(new Texture("vfx/bloom1.png")));
        }

        checkPool(Bloom.keyType);

        VisualEffect vfx = effectsPool.get(Bloom.keyType).poll();

        if (vfx != null) {
            ((Bloom) vfx).start(layer, x, y, scale, color, maxAlpha);
        } else
            vfx = new Bloom(layer, x, y,
                    frames.get(Bloom.keyType), scale, color,
                    this, maxAlpha);

        checkLayer(layer);

        aliveEffects.get(layer).add(vfx);
    }

    private void checkPool(String keyType) {
        if (!effectsPool.containsKey(keyType))
            effectsPool.put(keyType, new LinkedList<VisualEffect>());
    }

    private void checkLayer(int layer) {
        if (!aliveEffects.containsKey(layer))
            aliveEffects.put(layer, new ArrayList<VisualEffect>());
    }

    public void removeEffect(VisualEffect vfx) {
        toDeactivate.add(vfx);
    }

    public void dispose() {
        for (List<Sprite> list : frames.values())
            for (Sprite sprite : list)
                sprite.getTexture().dispose();
    }
}
