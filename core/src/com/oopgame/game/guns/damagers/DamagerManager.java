
package com.oopgame.game.guns.damagers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.oopgame.game.Time;
import com.oopgame.game.vfx.VisualEffectsManager;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import helpers.GameInfo;

public class DamagerManager {
    // hetkel nimetasin DamagerManageriks, aga Damager võib edaspidi segadust tekitada äkki,
    // seega peaks mingile paremale nimele mõtlema

    private SpriteBatch batch;
    private World world;

    private Time time;

    private List<Damager> aliveDamagers = new ArrayList<Damager>();
    private Map<String, Deque<Damager>> damagerPools = new HashMap<String, Deque<Damager>>();
    private Deque<Damager> toDeactivate = new LinkedList<Damager>();

    // map, kus hoiame tekstuure
    private Map<String, Sprite> spriteMap = new HashMap<String, Sprite>();

    // map, kus hoiame soundfx-e
    private Map<String, Sound> soundMap = new HashMap<String, Sound>();

    // map, kus hoiame damageride bodyDef-e
    private Map<String, BodyDef> bodyDefMap = new HashMap<String, BodyDef>();

    // map, kus hoiame damageride shape-e
    private Map<String, Shape> shapeMap = new HashMap<String, Shape>();


    private VisualEffectsManager vfxManager;


    public DamagerManager(SpriteBatch batch, World world, Time time,
                          VisualEffectsManager vfxManager) {
        this.batch = batch;
        this.world = world;
        this.time = time;
        this.vfxManager = vfxManager;

        // laser
        Sprite sprite = new Sprite(new Texture(Gdx.files.internal("damagers/laser1.png")));
        sprite.setSize(sprite.getTexture().getWidth() * GameInfo.SCALING,
                sprite.getTexture().getHeight() * GameInfo.SCALING);

        spriteMap.put("laser", sprite);
        soundMap.put("laser", Gdx.audio.newSound(Gdx.files.internal("lask.wav")));

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDefMap.put("laser", bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(sprite.getWidth() * 0.5f, sprite.getHeight() * 0.5f);
        shapeMap.put("laser", shape);

        // minilaser
        sprite = new Sprite(new Texture(Gdx.files.internal("damagers/laser1.png")));
        sprite.setSize(sprite.getTexture().getWidth() * GameInfo.SCALING * 0.5f,
                sprite.getTexture().getHeight() * GameInfo.SCALING * 0.5f);

        spriteMap.put("miniLaser", sprite);
        soundMap.put("miniLaser", Gdx.audio.newSound(Gdx.files.internal("lask.wav")));

        bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDefMap.put("miniLaser", bodyDef);

        shape = new PolygonShape();
        shape.setAsBox(sprite.getWidth() * 0.5f, sprite.getHeight() * 0.5f);
        shapeMap.put("miniLaser", shape);

    }

    public void render() {
        for (Damager damager : aliveDamagers)
            damager.draw(batch);
    }

    public void update() {
        for (Damager damager = toDeactivate.poll(); damager != null; damager = toDeactivate.poll()) {
            aliveDamagers.remove(damager);

            String key = damager.getKeyType();

            /*if (damager instanceof Laser)
                key = "laser";
            else if (damager instanceof MiniLaser)
                key = "miniLaser";
            else
                throw new RuntimeException("sellist damageri tüüpi pole siin välja toodud!");*/

            Vector2 pos = damager.getBody().getPosition();

            vfxManager.addBloom(2, pos.x , pos.y,
                    0.2f, damager.getColor(), 0.5f);

            damager.deactivate();

            damagerPools.get(key).add(damager);
        }

        for (Damager damager : aliveDamagers)
            damager.update();
    }

    public void dispose() {
        for (Sprite sprite : spriteMap.values())
            sprite.getTexture().dispose();

        for (Sound sound : soundMap.values())
            sound.dispose();

        for (Shape shape : shapeMap.values())
            shape.dispose();
    }

    public void shootLaser(
            Integer damage, Integer faction,
            Vector2 source, Float speed, float angle) {
        // meetod mida kutsuda, et laserit lasta
        String key = "laser";

        if (!damagerPools.containsKey(key))
            damagerPools.put(key, new LinkedList<Damager>());

        Damager damager = damagerPools.get(key).poll();

        if (damager != null) {
            ((Laser) damager).reset(damage, faction, source, speed, angle);
        } else
            damager = new Laser(
                    this, world, spriteMap.get("laser"), time,
                    damage, faction, source, speed, angle,
                    bodyDefMap.get("laser"), shapeMap.get("laser"));

        aliveDamagers.add(damager);

        if (faction == 0)
            soundMap.get("laser").play(0.35f);
    }

    public void shootMiniLaser(
            Integer damage, Integer faction,
            Vector2 source, Float speed, float angle) {
        // meetod mida kutsuda, et minilaserit lasta
        String key = "miniLaser";

        if (!damagerPools.containsKey(key))
            damagerPools.put(key, new LinkedList<Damager>());

        Damager damager = damagerPools.get(key).poll();

        if (damager != null) {
            ((MiniLaser) damager).reset(damage, faction, source, speed, angle);
        } else
            damager = new MiniLaser(
                    this, world, spriteMap.get("miniLaser"), time,
                    damage, faction, source, speed, angle,
                    bodyDefMap.get("miniLaser"), shapeMap.get("miniLaser"));

        aliveDamagers.add(damager);

        if (faction == 0)
            soundMap.get("miniLaser").play(0.25f);
    }

    public void poolDamager(Damager damager) {
        // siin ei tohi laserite listist midagi eemaldada,
        // sest seda listi võibolla läbitakse selle meetodi kutsumise ajal
        toDeactivate.add(damager);
    }
}
