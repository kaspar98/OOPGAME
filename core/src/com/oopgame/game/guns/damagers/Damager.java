package com.oopgame.game.guns.damagers;

public interface Damager {
    int getDamage();

    int getFaction();

    void hit();

    void deactivate();
}
