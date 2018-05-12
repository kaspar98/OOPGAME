package com.oopgame.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;

public class BodiedSprite extends Sprite {
    Body body;
    Fixture fixture;

    public BodiedSprite(Texture texture) {
        super(texture);
    }

    public void bodyUpdate() {
        if (body != null) {
            // muudab sprite'i keskpunkti asukoht vastavalt keha asukohale
            body.setAngularVelocity(0);
            setCenter(body.getPosition().x, body.getPosition().y);
        }
    }

    public Body getBody() {
        return body;
    }
}
