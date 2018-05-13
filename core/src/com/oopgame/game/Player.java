package com.oopgame.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import helpers.GameInfo;

public class Player extends Sprite {
    Body body;
    Fixture fixture;

    public Player(float x, float y, World world) {
        super(new Texture(Gdx.files.internal("player_laev.png")));

        setSize(
                getTexture().getWidth() * GameInfo.SCALING,
                getTexture().getHeight() * GameInfo.SCALING
        );

        setOrigin(getWidth() / 2f, getHeight() / 2f);

        // positsiooni sisendi arvutus (keskpunkt) -> (nurgapunkt)
        setCenter(x, y);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        // loome Playerile keha
        body = world.createBody(bodyDef);
        body.setUserData(this);

        PolygonShape box = new PolygonShape();
        box.setAsBox(getWidth() * 0.15f, getHeight() * 0.45f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = box;
        fixtureDef.density = 0.9f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.1f;

        fixture = body.createFixture(fixtureDef);

        box.dispose();
    }

    // testimiseks väga lambine inputi jälgimine
    public void inputs(TouchPad touchpad) {
        // iseenesest me enam seda ei vaja, aga jätsin igaksjuhuks alles praegu, kui peaks tahtma
        // kusaltki meelde tuletada, kuidas me tegime input key polli
        /*if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
            body.applyForceToCenter(
                    10 * GameInfo.FORCE_MULTIPLIER,
                    0.0f,
                    true
            );
        }*/

        // touchpadi inputist saadud info põhjalt paneme playeri vastava vektori suunas liikuma
        Vector2 touchpadVector = new Vector2(
                touchpad.getTouchpad().getKnobPercentX(),
                touchpad.getTouchpad().getKnobPercentY()
        );
        body.applyForceToCenter(
                touchpadVector.x * GameInfo.FORCE_MULTIPLIER,
                touchpadVector.y * GameInfo.FORCE_MULTIPLIER,
                true
        );

        if (touchpadVector.len() > 0) {
            setRotation(touchpadVector.angle() - 90);
            body.setTransform(
                    body.getPosition(),
                    (touchpadVector.angle() - 90) * MathUtils.degRad
            );
        }

        System.out.println(body.getLinearVelocity().len());
    }

    public void update() {
        // muudab sprite'i keskpunkti asukoht vastavalt keha asukohale
        body.setAngularVelocity(0);
        setCenter(body.getPosition().x, body.getPosition().y);
    }

    public void dispose() {
        // võtab spraidiga seotud assetid mälust maha
        getTexture().dispose();
    }

    public void updateCam(OrthographicCamera camera) {
        camera.position.set(
                body.getPosition().x + body.getLinearVelocity().x / 24f,
                body.getPosition().y + body.getLinearVelocity().y / 24f,
                0
        );
    }
}
