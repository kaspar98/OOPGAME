package com.oopgame.game.inputs.Devices_OLD;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

public class Klaviatuur {
    public static void movement(Vector2 vector) {
        if (Gdx.input.isKeyPressed(Input.Keys.W))
            vector.add(0, 1);

        if (Gdx.input.isKeyPressed(Input.Keys.A))
            vector.add(-1, 0);

        if (Gdx.input.isKeyPressed(Input.Keys.S))
            vector.add(0, -1);

        if (Gdx.input.isKeyPressed(Input.Keys.D))
            vector.add(1, 0);
    }
}
