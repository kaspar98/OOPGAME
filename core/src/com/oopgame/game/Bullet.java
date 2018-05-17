package com.oopgame.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import helpers.GameInfo;

public class Bullet extends Sprite {
    private Body body;
    private Fixture fixture;

    private float damage;
    private BulletManager bm;

    private boolean playerShot;

    public Bullet(float xKust, float yKust,
                  float xKuhu, float yKuhu,
                  float damage,
                  Texture texture, World world, BulletManager bm, boolean playerShot) {
        super(texture);
        this.damage = damage;
        this.bm = bm;
        this.playerShot = playerShot;
        setSize(
                getTexture().getWidth() * GameInfo.SCALING * 1.5f,
                getTexture().getHeight() * GameInfo.SCALING * 2f
        );
        setOrigin(getWidth() / 2f, getHeight() / 2f);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(xKust, yKust);

        // loome bulletile keha
        body = world.createBody(bodyDef);
        body.setUserData(this);
        body.setBullet(true);
        PolygonShape box = new PolygonShape();
        box.setAsBox(getWidth(), getHeight());
        fixture = body.createFixture(box, 0);
        fixture.setSensor(true);
        fixture.setUserData(this);

        box.dispose();

        Vector2 vektor = new Vector2(
                (xKuhu - xKust) * GameInfo.FORCE_MULTIPLIER,
                (yKuhu - yKust) * GameInfo.FORCE_MULTIPLIER);

        body.applyForceToCenter(vektor, true);

        setRotation(vektor.angle() + 90);
        body.setTransform(
                body.getPosition(),
                (getRotation()
                ) * MathUtils.degRad);
    }

    public void draw(Batch batch) {
        super.draw(batch);
    }

    public void update() {
        setCenter(body.getPosition().x, body.getPosition().y);
    }

    public void dispose() {
        getTexture().dispose();
    }

    public void die() {
        bm.removeBullet(this);
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

    // tagastab distantsi etteantud punktist
    public float getDistance(float x, float y) {
        return new Vector2(x - body.getPosition().x, y - body.getPosition().y).len();
    }
}
