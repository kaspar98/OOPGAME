package com.oopgame.game.guns;

import com.badlogic.gdx.math.Vector2;

public interface Gun {
    void update();

    // meetod tulistamiseks, siin iga relv toimiks omamoodi aka
    // iga relv vaatab oma intervalli, seab kuuli kiiruse, tüübi jne
    boolean shoot(float angle, Vector2 force);

    String getName();

    String getKeyType();

    void setSelected(boolean value);

    boolean isSelected();

    // et näha kui palju ammot alles veel on,
    // negatiivsed arvud, eelistatavalt -1, on hetkel infinite
    int getAmmoLeft();

    int getMaxAmmo();

    void resetAmmo();
}
