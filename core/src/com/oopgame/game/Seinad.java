package com.oopgame.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import helpers.GameInfo;

public class Seinad {
    Array<Sein> seinad = new Array<Sein>();

    public Seinad(World world) {
        float seinaPaksusPool = 120;
        float seinaPikkusPool = GameInfo.W_WIDTH / 2f + seinaPaksusPool * 3;

        float[][] coords = new float[][]{
                {GameInfo.W_WIDTH / 2f, -seinaPaksusPool},
                {GameInfo.W_WIDTH / 2f, GameInfo.W_HEIGHT + seinaPaksusPool},
                {-seinaPaksusPool, GameInfo.W_HEIGHT / 2f},
                {GameInfo.W_WIDTH + seinaPaksusPool, GameInfo.W_HEIGHT / 2f}
        };

        // praegu lükkasin seinad nii paika, et 0,0 koordinaadis saaks ka liikuda,
        // aga lõpuks võiks selle koodi natuke lihtsamaks teha ja optimiseerida
        // lõpuks võiks nii äkki teha:

        /*float seinaPaksusPool = 120;
        float seinaPikkusPool = GameInfo.W_WIDTH / 2f + seinaPaksusPool;

        float[][] coords = new float[][]{
                {GameInfo.W_WIDTH / 2f, 0},
                {GameInfo.W_WIDTH / 2f, GameInfo.W_HEIGHT},
                {0, GameInfo.W_HEIGHT / 2f},
                {GameInfo.W_WIDTH, GameInfo.W_HEIGHT / 2f}
        };*/

        float[][] suurused = new float[][]{
                {GameInfo.W_WIDTH / 2f + seinaPaksusPool * 2, seinaPaksusPool},
                {seinaPaksusPool, GameInfo.W_HEIGHT / 2f + seinaPaksusPool * 2}
        };

        float lüke = 1.5f * GameInfo.FORCE_MULTIPLIER;

        Vector2[] lükked = new Vector2[]{
                new Vector2(0, lüke),
                new Vector2(0, -lüke),
                new Vector2(lüke, 0),
                new Vector2(-lüke, 0)
        };

        for (int i = 0; i < 4; i++) {
            seinad.add(new Sein(
                    world,
                    coords[i][0],
                    coords[i][1],
                    suurused[i / 2][0],
                    suurused[i / 2][1],
                    lükked[i]
            ));
        }
    }
}
