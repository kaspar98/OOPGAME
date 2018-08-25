
package com.oopgame.game.guns.damagers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import helpers.GameInfo;

public class DamagerManager {
    // hakkab asendama BulletManageri

    // hetkel nimetasin DamagerManageriks, aga Damager võib edaspidi segadust tekitada äkki,
    // seega peaks mingile paremale nimele mõtlema

    private SpriteBatch batch;
    private World world;

    // map, kus hoiame tekstuure
    private Map<String, Sprite> spriteMap = new HashMap<String, Sprite>();

    // map, kus hoiame soundfx-e
    private Map<String, Sound> soundMap = new HashMap<String, Sound>();

    // Laseri väljad
    private List<Laser> lasers = new ArrayList<Laser>();
    private Deque<Laser> laserPool = new LinkedList<Laser>();

    // edasi...

    private Deque<Damager> toDeactivate = new LinkedList<Damager>();

    // TODO: damageride BodyDef siin ära teha ja alles hoida, siis delegeerida damageridele

    public DamagerManager(SpriteBatch batch, World world) {
        this.batch = batch;
        this.world = world;

        Sprite sprite = new Sprite(new Texture(Gdx.files.internal("damagers/laser1.png")));
        sprite.setSize(sprite.getTexture().getWidth() * GameInfo.SCALING,
                sprite.getTexture().getHeight() * GameInfo.SCALING);

        spriteMap.put("laser", sprite);
        soundMap.put("laser", Gdx.audio.newSound(Gdx.files.internal("lask.wav")));
    }

    public void render() {
        for (Laser laser : lasers)
            laser.draw(batch);
    }

    public void update() {
        for (Damager damager = toDeactivate.poll(); damager != null; damager = toDeactivate.poll())
            damager.deactivate();

        for (Laser laser : lasers)
            laser.update();
    }

    public void dispose() {
        for (Sprite sprite : spriteMap.values())
            sprite.getTexture().dispose();

        for (Sound sound : soundMap.values())
            sound.dispose();
    }

    public void shootLaser(
            Integer damage, Integer faction,
            Vector2 source, Float speed, float angle) {
        // meetod mida kutsuda, et laserit lasta
        Laser laser = laserPool.poll();

        if (laser != null) {
            laser.reset(damage, faction, source, speed, angle);
            lasers.add(laser);
        } else
            lasers.add(new Laser(
                    this, world, spriteMap.get("laser"),
                    damage, faction, source, speed, angle));

        if (faction == 0)
            soundMap.get("laser").play(0.35f);
    }

    public void poolLaser(Laser laser) {
        lasers.remove(laser);
        toDeactivate.add(laser);
        laserPool.add(laser);
    }
}
