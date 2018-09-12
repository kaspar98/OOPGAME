
package com.oopgame.game.guns.damagers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
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
    private Map<String, List<Sprite>> graphicsMap = new HashMap<String, List<Sprite>>();

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
    }

    public void render() {
        for (Damager damager : aliveDamagers)
            damager.draw(batch);
    }

    public void update() {
        // deactivation of damagers
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

            if (damager.didHit())
                vfxManager.addBloom(2, pos.x, pos.y,
                        2, damager.getColor(), 10,
                        1, 0.5f, 0,
                        0, 0);

            /*if (damager.getFaction() == 0)
                vfxManager.addPortal(2,
                        pos.x*//*GameInfo.W_WIDTH * 0.5f*//*, pos.y*//*GameInfo.W_HEIGHT * 0.5f*//*,
                        10, 300, 0.5f, 0.6f);*/

            damager.deactivate();

            checkPool(key);

            damagerPools.get(key).add(damager);
        }

        for (Damager damager : aliveDamagers)
            damager.update();
    }

    public void dispose() {
        for (List<Sprite> sprites : graphicsMap.values())
            for (Sprite sprite : sprites)
                sprite.getTexture().dispose();

        for (Sound sound : soundMap.values())
            sound.dispose();

        for (Shape shape : shapeMap.values())
            shape.dispose();
    }

    public void shootLaser(
            Integer damage, Integer faction,
            Vector2 source, Float speed, float angle,
            Vector2 force) {

        String key = Laser.keyType;

        if (!graphicsMap.containsKey(key)) {
            Sprite sprite = new Sprite(new Texture(Gdx.files.internal("damagers/laser1.png")));
            sprite.setSize(sprite.getTexture().getWidth() * GameInfo.SCALING,
                    sprite.getTexture().getHeight() * GameInfo.SCALING);

            List<Sprite> sprites = new ArrayList<Sprite>();
            sprites.add(sprite);
            graphicsMap.put(key, sprites);
            /*soundMap.put(key, Gdx.audio.newSound(Gdx.files.internal("lask.wav")));*/

            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            bodyDefMap.put(key, bodyDef);

            PolygonShape shape = new PolygonShape();
            shape.setAsBox(sprite.getWidth() * 0.5f, sprite.getHeight() * 0.5f);
            shapeMap.put(key, shape);
        }

        checkPool(key);

        Damager damager = damagerPools.get(key).poll();

        if (damager != null) {
            ((Laser) damager).reconfigure(damage, faction, source, speed, angle, force);
        } else
            damager = new Laser(
                    this, world, graphicsMap.get(key), time,
                    damage, faction, source, speed, angle, force,
                    bodyDefMap.get(key), shapeMap.get(key));

        aliveDamagers.add(damager);

        /*if (faction == 0)
            soundMap.get(key).play(0.35f);*/
    }

    public void shootMiniLaser(
            Integer damage, Integer faction,
            Vector2 source, Float speed, float angle, Vector2 force) {

        String key = MiniLaser.keyType;

        if (!graphicsMap.containsKey(key)) {
            Sprite sprite = new Sprite(new Texture(Gdx.files.internal("damagers/laser1.png")));
            sprite.setSize(sprite.getTexture().getWidth() * GameInfo.SCALING * 0.5f,
                    sprite.getTexture().getHeight() * GameInfo.SCALING * 0.5f);

            List<Sprite> sprites = new ArrayList<Sprite>();
            sprites.add(sprite);
            graphicsMap.put(key, sprites);
            /*soundMap.put(key, Gdx.audio.newSound(Gdx.files.internal("lask.wav")));*/

            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            bodyDefMap.put(key, bodyDef);

            PolygonShape shape = new PolygonShape();
            shape.setAsBox(sprite.getWidth() * 0.5f, sprite.getHeight() * 0.5f);
            shapeMap.put(key, shape);
        }

        checkPool(key);

        Damager damager = damagerPools.get(key).poll();

        if (damager != null) {
            ((MiniLaser) damager).reconfigure(damage, faction, source, speed, angle, force);
        } else
            damager = new MiniLaser(
                    this, world, graphicsMap.get(key), time,
                    damage, faction, source, speed, angle, force,
                    bodyDefMap.get(key), shapeMap.get(key));

        aliveDamagers.add(damager);

        /*if (faction == 0)
            soundMap.get(key).play(0.25f);*/
    }

    public LaserBeam shootLaserBeam(
            Vector2 source, float angle,
            Integer damage, Integer faction) {

        String key = LaserBeam.keyType;

        if (!graphicsMap.containsKey(key)) {
            float hx = 0;
            float hy = 0;

            List<Sprite> sprites = new ArrayList<Sprite>();

            for (String part : new String[]{"start", "mid"}) {
                Sprite sprite = new Sprite(new Texture(
                        Gdx.files.internal("damagers/laserBeam1" + part + ".png")));

                sprite.setSize(
                        ("mid".equals(part) ?
                                GameInfo.CAM_SCALING * GameInfo.OUTER_RADIUS :
                                sprite.getWidth() * GameInfo.SCALING),
                        sprite.getHeight() * GameInfo.SCALING);

                sprite.setOrigin(0, sprite.getHeight() * 0.5f);

                hx += sprite.getWidth();
                hy = sprite.getHeight();

                sprites.add(sprite);
            }
            graphicsMap.put(key, sprites);

            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            bodyDefMap.put(key, bodyDef);

            PolygonShape shape = new PolygonShape();
            shape.setAsBox(hx * 0.5f, hy * 0.3f);
            shapeMap.put(key, shape);
        }

        checkPool(key);

        Damager damager = damagerPools.get(key).poll();
        LaserBeam laserBeam;

        if (damager != null) {
            laserBeam = (LaserBeam) damager;
            laserBeam.reconfigure(source, angle, damage, faction);
        } else
            laserBeam = new LaserBeam(source, angle, world, time, damage, faction,
                    bodyDefMap.get(key), shapeMap.get(key), graphicsMap.get(key),
                    this);

        aliveDamagers.add(laserBeam);

        return laserBeam;
    }

    private void checkPool(String key) {
        if (!damagerPools.containsKey(key))
            damagerPools.put(key, new LinkedList<Damager>());
    }

    public void poolDamager(Damager damager) {
        // siin ei tohi laserite listist midagi eemaldada,
        // sest seda listi võibolla läbitakse selle meetodi kutsumise ajal
        toDeactivate.add(damager);
    }

    public void impactEffect(Damager damager) {
        if (damager instanceof Laser || damager instanceof MiniLaser) {
            Vector2 pos = damager.getBody().getPosition();
            Color color = damager.getColor();
            float angle = damager.getAngle();

            for (int i = 0; i < MathUtils.random(3, 6); i++)
                vfxManager.addLaserEffect(2, pos.x, pos.y,
                        MathUtils.random(angle + 120, angle + 240),
                        MathUtils.random(0.5f, 0.75f),
                        color, 10, MathUtils.random(0.5f, 1),
                        1, 0.5f, 0,
                        0, 0.9f);
        }
    }
}
