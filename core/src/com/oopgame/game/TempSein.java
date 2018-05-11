package com.oopgame.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import helpers.GameInfo;

public class TempSein {
    Array<Body> bodies = new Array<Body>();

    public TempSein(World world) {
        float seinaPaksusPool = 120;
        float seinaPikkusPool = GameInfo.W_WIDTH / 2f + seinaPaksusPool * 3;

        float[][] coords = new float[][]{
                {GameInfo.W_WIDTH / 2f, -seinaPaksusPool * 2},
                {GameInfo.W_WIDTH / 2f, GameInfo.W_HEIGHT + seinaPaksusPool * 2},
                {-seinaPaksusPool * 2, GameInfo.W_HEIGHT / 2f},
                {GameInfo.W_WIDTH + seinaPaksusPool * 2, GameInfo.W_HEIGHT / 2f}
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

        for (int i = 0; i < 4; i++) {
            BodyDef bodyDef = new BodyDef();

            bodyDef.position.set(coords[i][0], coords[i][1]);

            Body body = world.createBody(bodyDef);

            PolygonShape box = new PolygonShape();
            if (i / 2 == 0)
                box.setAsBox(seinaPikkusPool, seinaPaksusPool);
            else
                box.setAsBox(seinaPaksusPool, seinaPikkusPool);

            body.createFixture(box, 0).setSensor(true);
            box.dispose();

            bodies.add(body);
        }
    }
}
