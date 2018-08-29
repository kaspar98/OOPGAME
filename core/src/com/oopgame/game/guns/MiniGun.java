package com.oopgame.game.guns;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.oopgame.game.guns.damagers.DamagerManager;

public class MiniGun implements Gun {
    private DamagerManager damagerManager;

    // miinimum intervall tulistamiste vahel
    private static final int baseInterval = 80;
    private Integer interval = baseInterval;

    // kuuli damage
    private static final int baseDamage = 4;
    private Integer damage = baseDamage;

    // kuuli kiirus
    private static final float baseSpeed = 40;
    private Float speed = baseSpeed;

    // jätab meelde kunasest alatest järgmine lask lubatud
    private long nextShot = 0;

    private Integer faction;

    private Vector2 source;

    public MiniGun(DamagerManager damagerManager,
                    Vector2 source, Integer faction) {
        this.damagerManager = damagerManager;
        this.source = source;
        this.faction = faction;
    }

    @Override
    public boolean shoot(float angle) {
        // TODO: äkki annab aja pollimist veel optimiseerida
        long time = TimeUtils.millis();

        // saaks delegeerida relva objektidele edasi ja tagastada booleani vastavalt sellele,
        // kas tulistamine toimus vmitte

        if (nextShot < time) {
            nextShot = time + interval;

            damagerManager.shootMiniLaser(damage, faction, source, speed, angle);

            return true;
        }

        return false;
    }

    @Override
    public int ammoLeft() {
        return -1;
    }
}