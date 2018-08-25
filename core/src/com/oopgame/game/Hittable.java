package com.oopgame.game;

import com.oopgame.game.guns.damagers.Damager;

public interface Hittable {
    // kasutame selleks, et toimetada objektidega, mida saavad damagerid tabada
    // saame hiljem näiteks damageride enda peal ka rakendada, kui kuul tabab kuuli ja
    // me soovime, et kuulid üksteist hävitaksid - näiteks kui
    // mingeid seisvaid või väga aeglaseid pomme lisame

    boolean isHit(Damager damager);
}
