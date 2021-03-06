package com.oopgame.game.guns;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.oopgame.game.guns.damagers.DamagerManager;

public class GunList {
    // TODO: relvaidee: nanite gun
    // laseb hästi palju väikseid tükikesi, näevad välja nagu dust particles,
    // liiguvad ise vastase pihta, salves on mingi 30 kuuli äkki korraga ja need refillivad siis,
    // kui lastud kuulid kaovad või vastasele pihta lähevad

    // relvade kohad:
    // 0 - LaserGun
    // 1 - MiniGun
    // 2 - LaserBeamGun
    // 3 - ?
    // 4 - ?
    private Gun[] guns = new Gun[2];
    private int selected = 0;

    Sound sound = Gdx.audio.newSound(Gdx.files.internal("lask.wav"));

    public GunList(DamagerManager damagerManager, Vector2 source, Integer faction) {
        guns[0] = new LaserGun(damagerManager, source, faction);
        guns[1] = new MiniGun(damagerManager, source, faction);
        /*guns[2] = new LaserBeamGun(damagerManager, source, faction);*/

        selectGun(0);
    }

    public void update() {
        for (Gun gun : guns)
            gun.update();
    }

    public boolean shoot(float angle, Vector2 force) {
        boolean state = guns[selected].shoot(angle, force);

        if (state)
            sound.play(0.35f);

        return state;
    }

    public void selectGun(int index) {
        if (index >= 0 && index < guns.length &&
                guns[index] != null && guns[index].getAmmoLeft() != 0) {
            guns[selected].setSelected(false);
            selected = index;
            guns[selected].setSelected(true);
        }
    }

    public void selectNext() {
        for (int next = selected + 1; next != selected; next++) {
            if (next >= guns.length)
                next = 0;

            if (guns[next].getAmmoLeft() != 0) {
                selectGun(next);
                break;
            }
        }
    }

    public void selectPrevious() {
        for (int previous = selected - 1; previous != selected; previous--) {
            if (previous < 0)
                previous = guns.length - 1;

            if (guns[previous].getAmmoLeft() != 0) {
                selectGun(previous);
                break;
            }
         }
    }

    public int getSelected() {
        return selected;
    }

    public Gun[] getGuns() {
        return guns;
    }

    public void resetAmmo() {
        for (Gun gun : guns)
            gun.resetAmmo();
    }

    public void dispose() {
        sound.dispose();
    }
}
