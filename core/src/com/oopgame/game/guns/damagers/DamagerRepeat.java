package com.oopgame.game.guns.damagers;

import com.oopgame.game.Hittable;

public interface DamagerRepeat extends Damager {
    void addHittable(Hittable hittable);

    void removeHittable(Hittable hittable);
}
