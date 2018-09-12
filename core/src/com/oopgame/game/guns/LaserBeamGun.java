package com.oopgame.game.guns;

import com.badlogic.gdx.math.Vector2;
import com.oopgame.game.guns.damagers.DamagerManager;
import com.oopgame.game.guns.damagers.LaserBeam;

public class LaserBeamGun implements Gun {
    public static String keyType = "laserBeam";
    public static String name = "laser beam";

    private DamagerManager damagerManager;

    private static final int baseDamage = 1;
    private Integer damage = baseDamage;

    private Integer faction;

    private Vector2 source;

    private LaserBeam beam;

    private boolean selected;

    public LaserBeamGun(DamagerManager damagerManager,
                        Vector2 source, Integer faction) {
        this.damagerManager = damagerManager;
        this.source = source;
        this.faction = faction;
    }

    @Override
    public void update() {

    }

    @Override
    public boolean shoot(float angle, Vector2 force) {
        if (beam != null && beam.isActive())
            beam.setPos(angle);
        else
            beam = damagerManager.shootLaserBeam(source, angle, damage, faction);

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
        return -1;
    }

    @Override
    public int getMaxAmmo() {
        return -1;
    }

    @Override
    public void resetAmmo() {
    }
}
