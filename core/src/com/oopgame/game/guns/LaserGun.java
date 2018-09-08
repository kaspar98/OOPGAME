package com.oopgame.game.guns;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.oopgame.game.guns.damagers.DamagerManager;

public class LaserGun implements Gun {
    public static String keyType = "laserGun";
    public static String name = "lasergun";


    private DamagerManager damagerManager;

    // miinimum intervall tulistamiste vahel
    private static final int baseInterval = 200;
    private Integer interval = baseInterval;

    // kuuli damage
    private static final int baseDamage = 10;
    private Integer damage = baseDamage;

    // kuuli kiirus
    private static final float baseSpeed = 20;
    private Float speed = baseSpeed;

    // jätab meelde kunasest alatest järgmine lask lubatud
    private long nextShot = 0;
    private long nextAmmo = 0;

    private Integer faction;

    private Vector2 source;

    private boolean selected;

    private static int maxAmmo = 50;
    private int ammo = maxAmmo;

    public LaserGun(DamagerManager damagerManager,
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
            nextAmmo = time + interval * 2;
        }
    }

    @Override
    public boolean shoot(float angle) {
        // TODO: äkki annab aja pollimist veel optimiseerida
        long time = TimeUtils.millis();

        // saaks delegeerida relva objektidele edasi ja tagastada booleani vastavalt sellele,
        // kas tulistamine toimus vmitte

        if (nextShot < time && ammo > 0) {
            ammo--;

            nextShot = time + interval;
            nextAmmo = time + interval * 10;

            damagerManager.shootLaser(damage, faction, source, speed, angle);

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
    public int ammoLeft() {
        return ammo;
    }

    @Override
    public int maxAmmo() {
        return maxAmmo;
    }

    @Override
    public void resetAmmo() {
        ammo = maxAmmo;
    }
}
