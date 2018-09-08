package com.oopgame.game.guns;

public interface Gun {
    void update();

    // meetod tulistamiseks, siin iga relv toimiks omamoodi aka
    // iga relv vaatab oma intervalli, seab kuuli kiiruse, tüübi jne
    boolean shoot(float angle);

    String getName();

    String getKeyType();

    void setSelected(boolean value);

    boolean isSelected();

    // et näha kui palju ammot alles veel on,
    // negatiivsed arvud, eelistatavalt -1, on hetkel infinite
    int ammoLeft();

    int maxAmmo();
}
