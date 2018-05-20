package com.oopgame.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import helpers.GameInfo;

public class Gibs extends Sprite {
    private Body body;

    public Gibs(Sprite sprite, World world) {
        super(sprite);

        setOrigin(getWidth() * 0.5f, getHeight() * 0.5f);

        setCenter(-GameInfo.W_WIDTH, 0);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(-GameInfo.W_WIDTH, 0);

        body = world.createBody(bodyDef);

        PolygonShape box = new PolygonShape();
        box.setAsBox(getWidth() * 0.5f, getHeight() * 0.5f);

        Fixture fixture = body.createFixture(box, 0);
        fixture.setSensor(true);

        box.dispose();

        body.setActive(false);
    }

    private Vector2 suvalineVektor(Vector2 vektor) {
        return vektor.cpy().setAngle(vektor.angle() + MathUtils.random(-60f, 60f));
    }

    private float suvalinePööre() {
        return MathUtils.random(0f, 2f);
    }

    public void start(float x, float y, Vector2 vektor) {
        body.setLinearVelocity(suvalineVektor(vektor));
        body.setAngularVelocity(suvalinePööre());

        body.setTransform(x, y, 0);

        body.setActive(true);
    }

    public void stop() {
        body.setLinearVelocity(0, 0);
        body.setAngularVelocity(0);

        body.setTransform(-GameInfo.W_WIDTH, 0, 0);

        body.setActive(false);
    }

    public void update() {
        setCenter(body.getPosition().x, body.getPosition().y);
        setRotation(body.getAngle() * MathUtils.radDeg);
    }
}
