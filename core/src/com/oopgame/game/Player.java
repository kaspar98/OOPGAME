package com.oopgame.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import helpers.GameInfo;

public class Player extends BodiedSprite {
    public Player(float x, float y, World world) {
        super(new Texture(Gdx.files.internal("player_laev.png")));

        setSize(
                getTexture().getWidth() * GameInfo.SCALING,
                getTexture().getHeight() * GameInfo.SCALING
        );

        setOrigin(getWidth() / 2f, getHeight() / 2f);

        // positsiooni sisendi arvutus (keskpunkt) -> (nurgapunkt)
        setPosition(
                x - getWidth() / 2f,
                y - getHeight() / 2f
        );

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
        /*float touchpadX = touchpad.getTouchpad().getKnobPercentX();
        float touchpadY = touchpad.getTouchpad().getKnobPercentY();
        *//*if (touchpadX!=0 && touchpadY!=0)
            body.setTransform(body.getPosition().x +touchpadX*3, body.getPosition().y+touchpadY*3,0);*//*
        body.applyForceToCenter(
                touchpadX * GameInfo.FORCE_MULTIPLIER,
                touchpadY * GameInfo.FORCE_MULTIPLIER,
                true
        );*/
        // touchpadi inputist saadud info põhjal keerame playeri vaatama sinna kuhu ta parasjagu kiirendab
        // (kuna tekstuuri nina ei ole seal kus body nina asub lahutame 90 kraadi)
        // (kuna arctan annab vahemikus -90 kuni 90 kraadi peame tegutsema kahes osas)
        /*if (touchpadX < 0) {
            sprite.setRotation((float) Math.toDegrees(Math.atan(touchpadY / touchpadX))- 90 + 180);
        }
        if (touchpadX > 0) {
            sprite.setRotation((float) Math.toDegrees(Math.atan(touchpadY / touchpadX)) - 90);
        }*/
        if (touchpadVector.len() > 0) {
            setRotation(touchpadVector.angle() - 90);
            body.setTransform(
                    body.getPosition(),
                    (touchpadVector.angle() - 90) * MathUtils.degRad
            );
        }

        System.out.println(body.getLinearVelocity().len());
    }

    public void dispose() {
        // võtab spraidiga seotud assetid mälust maha
        dispose();
    }
}
