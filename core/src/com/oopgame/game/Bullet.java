package com.oopgame.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

import helpers.GameInfo;

public class Bullet extends Sprite {
    private Body body;
    private float damage;

    public Bullet (float xKust, float yKust, float xKuhu, float yKuhu, float damage, Texture texture, World world) {
        super(texture);
        this.damage = damage;
        setSize(
                getTexture().getWidth() * GameInfo.SCALING,
                getTexture().getHeight() * GameInfo.SCALING
        );
        setOrigin(getWidth() / 2f, getHeight() / 2f);
        setCenter(xKust, yKust);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(xKust, yKust);

        // loome bulletile keha
        body = world.createBody(bodyDef);
        body.setUserData(this);

        body.applyForceToCenter(new Vector2((xKuhu-xKust)*GameInfo.FORCE_MULTIPLIER, (yKuhu-yKust)*GameInfo.FORCE_MULTIPLIER), true);

        setRotation(new Vector2(xKuhu-xKust, yKuhu-yKust).angle()+90);
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
}
