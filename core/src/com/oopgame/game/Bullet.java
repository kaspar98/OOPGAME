package com.oopgame.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;

import helpers.GameInfo;

public class Bullet extends Sprite {
    private Body body;
    private Fixture fixture;

    private float damage;

    private boolean playerShot;

    private long timeExpire;

    private boolean alive;

    public Bullet(Vector2 kust, Vector2 suunaVektor, float damage,
                  Sprite appearance, World world, boolean playerShot) {
        super(appearance);
        this.damage = damage;
        this.playerShot = playerShot;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(kust);

        // loome bulletile keha
        body = world.createBody(bodyDef);
        body.setUserData(this);
        body.setBullet(true);

        PolygonShape box = new PolygonShape();
        box.setAsBox(getWidth() * 0.5f, getHeight() * 0.5f);

        fixture = body.createFixture(box, 0);
        fixture.setSensor(true);
        fixture.setUserData(this);

        box.dispose();

        body.setLinearVelocity(suunaVektor.cpy().setLength(GameInfo.FORCE_MULTIPLIER * 1000));

        setRotation(suunaVektor.angle());
        body.setTransform(body.getPosition(), suunaVektor.angleRad());

        timeExpire = TimeUtils.millis() + GameInfo.BULLET_DURATION;

        alive = true;
    }

    public void draw(Batch batch) {
        super.draw(batch);
    }

    public void update() {
        setCenter(body.getPosition().x, body.getPosition().y);

        long time = TimeUtils.millis();
        if (timeExpire < time) {
            kill();
        }
    }

    public void kill() {
        body.setLinearVelocity(0, 0);
        body.setTransform(-GameInfo.W_WIDTH, 0, 0);
        body.setActive(false);
    }

    public void revive(Vector2 pos, Vector2 suunaVektor, float damage, Sprite appearance, boolean playerShot) {
        this.set(appearance);

        this.damage = damage;
        this.playerShot = playerShot;
        timeExpire = TimeUtils.millis() + GameInfo.BULLET_DURATION;

        setRotation(suunaVektor.angle());

        body.setTransform(pos, suunaVektor.angleRad());

        body.setLinearVelocity(suunaVektor.cpy().setLength(GameInfo.FORCE_MULTIPLIER * 1000));

        body.setActive(true);

        alive = true;
    }

    public Body getBody() {
        return body;
    }

    public float getDamage() {
        return damage;
    }

    public boolean isPlayerShot() {
        return playerShot;
    }

    public void hasHit() {
        alive = false;
    }

    public boolean isAlive() {
        return alive;
    }
}
