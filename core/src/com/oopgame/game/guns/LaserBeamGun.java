package com.oopgame.game.guns;

import com.badlogic.gdx.math.Vector2;
import com.oopgame.game.guns.damagers.DamagerManager;
import com.oopgame.game.guns.damagers.LaserBeam;

public class LaserBeamGun implements Gun {
    public static String keyType = "laserBeam";

    private DamagerManager damagerManager;

    private static final int baseDamage = 1;
    private Integer damage = baseDamage;

    private Integer faction;

    private Vector2 source;

    private LaserBeam beam;

    public LaserBeamGun(DamagerManager damagerManager,
                        Vector2 source, Integer faction) {
        this.damagerManager = damagerManager;
        this.source = source;
        this.faction = faction;
    }

    @Override
    public boolean shoot(float angle) {
        if (beam != null && beam.isActive())
            beam.setPos(angle);
        else
            beam = damagerManager.shootLaserBeam(source, angle, damage, faction);

        return false;
    }

    @Override
    public int ammoLeft() {
        return -1;
    }
}
