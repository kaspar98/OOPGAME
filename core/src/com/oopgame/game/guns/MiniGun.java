package com.oopgame.game.guns;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.oopgame.game.guns.damagers.DamagerManager;

public class MiniGun implements Gun {
    public static String keyType = "minigun";
    public static String name = "minigun";


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
    private long nextAmmo = 0;

    private Integer faction;

    private Vector2 source;

    private boolean selected;

    private static int maxAmmo = 100;
    private int ammo = maxAmmo;

    public MiniGun(DamagerManager damagerManager,
                    Vector2 source, Integer faction) {
        this.damagerManager = damagerManager;
        this.source = source;
        this.faction = faction;
    }

    @Override
    public void update() {
        long time = TimeUtils.millis();

        if (ammo < maxAmmo && nextAmmo < time) {
            ammo++;
            int dynInterval = MathUtils.round(interval * (maxAmmo / (float) ammo));
            nextAmmo = time + (dynInterval > interval * 10 ? interval * 10 : dynInterval);
        }
    }

    @Override
    public boolean shoot(float angle, Vector2 force) {
        // TODO: äkki annab aja pollimist veel optimiseerida
        long time = TimeUtils.millis();

        // saaks delegeerida relva objektidele edasi ja tagastada booleani vastavalt sellele,
        // kas tulistamine toimus vmitte

        if (nextShot < time && ammo > 0) {
            ammo--;

            nextShot = time + interval;
            nextAmmo = time + interval * 10;

            damagerManager.shootMiniLaser(damage, faction, source, speed, angle, force);

            return true;
        }

        return false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getKeyType() {
        return keyType;
    }

    @Override
    public void setSelected(boolean value) {
        selected = value;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public int getAmmoLeft() {
        return ammo;
    }

    @Override
    public int getMaxAmmo() {
        return maxAmmo;
    }

    @Override
    public void resetAmmo() {
        ammo = maxAmmo;
    }
}