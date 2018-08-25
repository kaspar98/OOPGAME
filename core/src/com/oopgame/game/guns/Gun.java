package com.oopgame.game.guns;

public interface Gun {
    // meetod tulistamiseks, siin iga relv toimiks omamoodi aka
    // iga relv vaatab oma intervalli, seab kuuli kiiruse, tüübi jne
    boolean shoot(float angle);

    // et näha kui palju ammot alles veel on,
    // negatiivsed arvud, eelistatavalt -1, on hetkel infinite
    int ammoLeft();

    //TODO: äkki mingi meetod selleks ka, et näidata kui palju aega järgmise tulistamiseni on
    //TODO: meetod ammo lisamiseks
    //TODO: meetod intervalli temporary vähendamiseks
}
