package com.oopgame.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Sein {
    private Body body;
    Fixture fixture;
    private Vector2 force;

    public Sein(World world, float x, float y, float hx, float hy, Vector2 force) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(x, y);

        Body body = world.createBody(bodyDef);

        PolygonShape box = new PolygonShape();
        box.setAsBox(hx, hy);

        fixture = body.createFixture(box, 0);
        fixture.setSensor(true);
        fixture.setUserData(this);

        box.dispose();

        this.force = force;
    }

    public Vector2 getForce() {
        return force;
    }
}
