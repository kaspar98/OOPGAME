package com.oopgame.game.inputs.Devices_OLD;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.math.Vector2;

public class Pult {
    public static void movement(Vector2 vector) {
        Vector2 neo;

        for (Controller controller : Controllers.getControllers()) {
            neo = new Vector2(controller.getAxis(1), -controller.getAxis(0));

            if (neo.len() > 0.1f)
                vector.add(neo);
        }
    }

    public static boolean aiming(Vector2 vector) {
        Vector2 neo;

        for (Controller controller : Controllers.getControllers()) {
            neo = new Vector2(controller.getAxis(3), -controller.getAxis(2));

            if (neo.len() > 0.4f) {
                vector.set(neo);

                return true;
            }
        }
        return false;
    }

    public static boolean isShooting() {
        for (Controller controller : Controllers.getControllers())
            if (controller.getButton(5) || controller.getButton(4))
                return true;

        return false;
    }
}
