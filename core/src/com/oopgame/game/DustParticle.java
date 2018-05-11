package com.oopgame.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import helpers.GameInfo;

public class DustParticle extends Sprite {
    public DustParticle(Texture texture, Vector2 asukoht) {
        super(texture);
        setSize(0, GameInfo.SCALING);

        setPosition(asukoht.x, asukoht.y);
        flip(true, false);
    }

    public void uuenda(Vector2 vel) {
        setPosition(
                getX() - vel.x / 120,
                getY() - vel.y / 120
        );

        setSize(vel.len() / 12, GameInfo.SCALING);
        setRotation(vel.angle());
    }

    public void setPosition(Vector2 asukoht) {
        super.setPosition(asukoht.x, asukoht.y);
    }
}
