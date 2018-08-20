package com.oopgame.game.inputs;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;

public class Ekraan {
    public static void movement(Touchpad touchpad, Vector2 vector) {
        vector.add(touchpad.getKnobPercentX(), touchpad.getKnobPercentY());
    }

    public static void aiming(Touchpad touchpad, Vector2 vector) {
        vector.set(
                touchpad.getKnobPercentX(),
                touchpad.getKnobPercentY());
    }
}
