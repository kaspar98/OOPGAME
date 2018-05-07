package com.oopgame.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class TempSein {
    Body body;

    public TempSein(World world, float x, float y) {
        BodyDef wallBodyDef = new BodyDef();

        wallBodyDef.position.set(new Vector2(x, y));

        body = world.createBody(wallBodyDef);

        PolygonShape box = new PolygonShape();
        box.setAsBox(512, 100);
        body.createFixture(box, 0);
        box.dispose();
    }
}
