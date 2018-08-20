package com.oopgame.game.inputs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import helpers.GameInfo;

public class Hiir {
    public static boolean aiming(Vector2 vector, Vector3 camera, Vector2 body) {
        vector.set(
                camera.x + (Gdx.input.getX() - 0.5f * GameInfo.WIDTH) * GameInfo.CAM_SCALING,
                camera.y + (0.5f * GameInfo.HEIGHT - Gdx.input.getY()) * GameInfo.CAM_SCALING);

        vector.sub(body);

        return Gdx.input.isTouched();
    }
}
