package com.oopgame.game.guns;

import com.badlogic.gdx.math.Vector2;
import com.oopgame.game.guns.damagers.DamagerManager;

public class GunList {
    // TODO: relvaidee: nanite gun
    // laseb hästi palju väikseid tükikesi, näevad välja nagu dust particles,
    // liiguvad ise vastase pihta, salves on mingi 30 kuuli äkki korraga ja need refillivad siis,
    // kui lastud kuulid kaovad või vastasele pihta lähevad

    // relvade kohad:
    // 0 - LaserGun
    // 1 - ?
    // 2 - ?
    // 3 - ?
    // 4 - ?
    private Gun[] guns = new Gun[1];
    private int selected = 0;

    public GunList(DamagerManager damagerManager, Vector2 source, Integer faction) {
        guns[0] = new LaserGun(damagerManager, source, faction);
    }

    public boolean shoot(float angle) {
        return guns[selected].shoot(angle);
    }

    public void selectGun(int number) {
        // TODO: see meetod tuleb veel ära lõpetada
        if (number > 0 && number < guns.length && guns[number].ammoLeft() != 0);
    }

    public void selectNext() {
        // TODO: see meetod tuleb veel ära lõpetada
        for (int next = selected + 1; next != selected; next++) {
            if (next >= guns.length)
                next = 0;

            if (guns[next].ammoLeft() != 0) {
                selected = next;
                break;
            }
        }
    }

    public void selectPrevious() {
        // TODO: see meetod tuleb veel ära lõpetada
        for (int previous = selected - 1; previous != selected; previous--) {
            if (previous < 0)
                previous = guns.length - 1;

            if (guns[previous].ammoLeft() != 0) {
                selected = previous;
                break;
            }
         }
    }

    public int getSelected() {
        return selected;
    }
}
